/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.f3.tools.comp;

import org.f3.api.tree.SequenceSliceTree;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.*;
import org.f3.tools.comp.F3AbstractTranslation.ExpressionResult;
import org.f3.tools.comp.F3Defs.RuntimeMethod;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;

/**
 * Translate bind expressions into code in bind defining methods
 *
 * @author Robert Field
 */
public class F3TranslateBind extends F3AbstractTranslation implements F3Visitor {

    protected static final Context.Key<F3TranslateBind> f3BoundTranslation =
        new Context.Key<F3TranslateBind>();
    private F3ToJava toJava;

    // Symbol for the var whose bound expression we are translating.
    private F3VarSymbol targetSymbol;

    // The outermost bound expression
    private F3Expression boundExpression;

    private DependencyGraphWriter depGraphWriter;

    public static F3TranslateBind instance(Context context) {
        F3TranslateBind instance = context.get(f3BoundTranslation);
        if (instance == null) {
            F3ToJava toJava = F3ToJava.instance(context);
            instance = new F3TranslateBind(context, toJava);
        }
        return instance;
    }

    public F3TranslateBind(Context context, F3ToJava toJava) {
        super(context, toJava);
        this.toJava = toJava;

        context.put(f3BoundTranslation, this);
        this.depGraphWriter = DependencyGraphWriter.instance(context);
    }

    static JCExpression TODO(String msg, F3Expression tree) {
        return TODO(msg + " -- " + tree.getClass());
    }

    /**
     * Entry-point into F3TranslateBind.
     *
     * @param expr Bound expression to translate.  Directly held by a var (or bound function?). Not a sub-expression/
     * @param targetSymbol Symbol for the var whose bound expression we are translating.
     * @param isBidiBind Is this a bi-directional bind?
     * @return
     */
    ExpressionResult translateBoundExpression(F3Expression expr, F3VarSymbol targetSymbol) {
        // Bind translation is re-entrant -- save and restore state
        F3VarSymbol prevTargetSymbol = this.targetSymbol;
        F3Expression prevBoundExpression = this.boundExpression;

        this.targetSymbol = targetSymbol;
        this.boundExpression = expr;

        // Special case: If the targetSymbol is a bound function result, then
        // make the expected type to be Pointer or else make it null.
        ExpressionResult res = translateToExpressionResult(
                expr,
                isBoundFunctionResult(targetSymbol) ?
                    syms.f3_PointerTypeErasure :
                    targetSymbol.type);

        this.targetSymbol = prevTargetSymbol;
        this.boundExpression = prevBoundExpression;
        return res;
    }

/****************************************************************************
 *                     Bound Non-Sequence Translators
 ****************************************************************************/

    /**
     * Translator for bound object literals
     */
    class BoundInstanciateTranslator extends InstanciateTranslator {

        // Call function only if conditions met
        private JCExpression condition = null;

        // If we are wrapping with a test, collect the preface
        private ListBuffer<JCStatement> wrappingPreface = ListBuffer.lb();

        BoundInstanciateTranslator(F3Instanciate tree) {
            super(tree);
        }

        @Override
        protected void initInstanceVariables(Name instName) {
            // True if any of the initializers are sequences, we can't pre-test arguments
            boolean hasSequenceInitializer = false;

            for (F3ObjectLiteralPart olpart : tree.getParts()) {
                if (types.isSequence(olpart.sym.type)) {
                    hasSequenceInitializer = true;
                }
            }
            
            if (!hasSequenceInitializer) {
                condition = bindNeedsDefault(targetSymbol);
            }
            
            super.initInstanceVariables(instName);
        }

        @Override
        protected JCExpression translateInstanceVariableInit(F3Expression init, F3VarSymbol vsym) {
            if (init instanceof F3Ident) {
                Symbol isym = ((F3Ident) init).sym;
                addBindee((F3VarSymbol) isym);

                if (condition != null) {
                    JCVariableDecl oldVar = TmpVar("old", vsym.type, Get(isym));
                    JCVariableDecl newVar = TmpVar("new", vsym.type, Getter(isym));
                    wrappingPreface.append(oldVar);
                    wrappingPreface.append(newVar);

                    // concatenate with OR --  oldArg1 != newArg1 || oldArg2 != newArg2
                    condition = OR(condition, NE(id(oldVar), id(newVar)));

                    return id(newVar);
                } else {
                    return super.translateInstanceVariableInit(init, vsym);
                }
            } else {
                return super.translateInstanceVariableInit(init, vsym);
            }
        }

        /**
         * If we can, wrap the instance creation in a test to be sure an initializer really changed
         *
         * T res;
         * if (DefaultsNotApplied || oldArg1 != newArg1 || oldArg2 != newArg2) {
         *   T objlit = ... instance creation stuff
         *   res = objectLit;
         * } else {
         *   res = prevValue;
         * }
         */
        @Override
        protected ExpressionResult doit() {
            ExpressionResult eres = super.doit();
            if (condition != null) {
                // if no initializers have changed, don't create a new instance, just return previous value\
                JCVariableDecl resVar = MutableTmpVar("res", targetSymbol.type, null);
                JCStatement setRes =
                    If(condition,
                        Block(
                            eres.statements().append(Stmt(m().Assign(id(resVar), eres.expr())))),
                        Stmt(m().Assign(id(resVar), Get(targetSymbol))));
                return new ExpressionResult(
                        diagPos,
                        wrappingPreface.toList().append(resVar).append(setRes),
                        id(resVar),
                        eres.bindees(),
                        eres.invalidators(),
                        eres.interClass(),
                        eres.setterPreface(),
                        eres.resultType());
            } else {
                return eres;
            }
        }
    }

    /**
     * Translate a bound function call
     */
    private class BoundFunctionCallTranslator extends FunctionCallTranslator {

        /*
         * True if the (bind) call is made conditionally, false if function call
         * is executed always (no condition check). This conditional evaluation
         * is an optimization for calls in bind expressions - we would like to
         * avoid calling the function whenever possible.
         */
        private boolean conditionallyReevaluate = false;

        // Call function only if conditions met
        private JCExpression condition = null;

        // True if any arguments are sequences, we can't pre-test arguments
        private boolean hasSequenceArg = false;

        BoundFunctionCallTranslator(F3FunctionInvocation tree) {
            super(tree);
            // Determine if any arguments are sequences, if so, we can't pre-test arguments
            for (F3Expression arg : args) {
                if (types.isSequence(arg.type)) {
                    hasSequenceArg = true;
                }
            }

            // If the function has a sequence arg or if this is a Function.invoke or
            // if this is a Java call, we avoid conditional reevaluation. (i.e., force
            // re-evaluation always)
            boolean isJavaCall = (msym != null) && !types.isF3Class(msym.owner);
            conditionallyReevaluate = ! (hasSequenceArg  || useInvoke || isJavaCall);

            // If the receiver changes, then we have to call the function again
            // If selector is local var, then it is going to be final, and thus won't change (and doesn't have a getter)
            if (conditionallyReevaluate && !knownNonNull && selectorSym instanceof VarSymbol && selectorSym.owner.kind == Kinds.TYP) {
                JCVariableDecl oldVar = TmpVar("old", selectorSym.type, Get(selectorSym));
                JCVariableDecl newVar = TmpVar("new", selectorSym.type, Getter(selectorSym));
                addPreface(oldVar);
                addPreface(newVar);
                // oldRcvr != newRcvr
                condition = NE(id(oldVar), id(newVar));
            }
        }

        @Override
        List<JCExpression> determineArgsImpl() {
            ListBuffer<JCExpression> targs = ListBuffer.lb();
            // if this is a super.foo(x) call, "super" will be translated to referenced class,
            // so we add a receiver arg to make a direct call to the implementing method  MyClass.foo(receiver$, x)
            if (superToStatic) {
                targs.append(id(defs.receiverName));
            }

            if (callBound) {
                for (F3Expression arg : args) {
                    if (arg.getF3Tag() == F3Tag.IDENT) {
                        F3Ident ident = (F3Ident)arg;
                        targs.append(getReceiverOrThis(ident.sym));
                        targs.append(Offset(ident.sym));
                    } else if (false/*disable-VSGC-4079*/ && preTrans.isImmutable(arg)) {
                        // pass F3Constant wrapper for argument expression
                        targs.append(Call(defs.F3Constant_make, translateExpr(arg, arg.type)));
                        // pass F3Constant.VOFF$value as offset value
                        targs.append(Select(makeType(syms.f3_ConstantType), defs.varOFF$valueName));
                    } else {
                        TODO("non-Ident and non-immutable in bound call");
                    }
                }
                return targs.toList();
            } else {
                return super.determineArgsImpl();
            }
        }

        @Override
        JCExpression translateArg(F3Expression arg, Type formal) {
            if (conditionallyReevaluate && arg instanceof F3Ident /*disable-VSGC-4079: && !preTrans.isImmutable(arg)*/) {
                // if no args have changed, don't call function, just return previous value
                Symbol sym = ((F3Ident) arg).sym;
                addBindee((F3VarSymbol) sym);   //TODO: isn't this redundant?

                JCVariableDecl oldVar = TmpVar("old", formal, Get(sym));
                JCVariableDecl newVar = TmpVar("new", formal, Getter(sym));
                addPreface(oldVar);
                addPreface(newVar);

                // oldArg != newArg
                JCExpression compare = NE(id(oldVar), id(newVar));
                // concatenate with OR --  oldArg1 != newArg1 || oldArg2 != newArg2
                condition = condition != null? OR(condition, compare) : compare;

                return id(newVar);
            } else {
                return super.translateArg(arg, formal);
            }
        }

        @Override
        JCExpression fullExpression(JCExpression mungedToCheckTranslated) {
            if (callBound) {
                // call to a bound function in bind context
                JCExpression tMeth = Select(mungedToCheckTranslated, methodName());
                return m().Apply(translateExprs(typeargs), tMeth, determineArgs());
            } else {
                JCExpression full = super.fullExpression(mungedToCheckTranslated);
                if (condition != null) {
                    // Always call function if the default has not been applied yet
                    full = TypeCast(targetType, Type.noType,
                              If (OR(condition, bindNeedsDefault(targetSymbol)),
                                  full,
                                  Get(targetSymbol)));
                }
                return full;
            }
        }
    }

    /**
     * Translate if-expression
     *
     * bind if (cond) foo else bar
     *
     * becomes preface statements:
     *
     *   T res;
     *   cond.preface;
     *   if (cond) {
     *     foo.preface;
     *     res = foo;
     *   } else {
     *     bar.preface;
     *     res = bar;
     *   }
     *
     * result value:
     *
     *   res
     *
     */
    private class BoundIfExpressionTranslator extends ExpressionTranslator {

        private final F3IfExpression tree;
        private final JCVariableDecl resVar;
        private final Type type;

        BoundIfExpressionTranslator(F3IfExpression tree) {
            super(tree.pos());
            this.tree = tree;
            this.type = (targetType != null)? targetType : tree.type;
            this.resVar = TmpVar("res", type, null);
        }

        JCStatement side(F3Expression expr) {
            ExpressionResult res = translateToExpressionResult(expr, type);
            addBindees(res.bindees());
            addInterClassBindees(res.interClass());
            return Block(res.statements().append(Stmt(m().Assign(id(resVar), res.expr()))));
        }

        protected ExpressionResult doit() {
            JCExpression cond = translateExpr(tree.getCondition(), syms.booleanType);
            addPreface(resVar);
            addPreface(If (cond,
                    side(tree.getTrueExpression()),
                    side(tree.getFalseExpression())));
            return toResult( id(resVar), type );
        }
    }

/****************************************************************************
 *                     Bound Sequence Translators
 ****************************************************************************/

    /**
     * Abstract super class of bound sequence Translators.
     *
     * Provides the framework of abstract methods that must be implemented:
     *       makeSizeBody(), makeGetElementBody(), setupInvalidators()
     *
     * And provides common utilities
     */
    private abstract class BoundSequenceTranslator extends ExpressionTranslator {


        abstract JCStatement makeSizeBody();
        abstract JCStatement makeGetElementBody();
        abstract void setupInvalidators();

        BoundSequenceTranslator(DiagnosticPosition diagPos) {
            super(diagPos);
        }

        BoundSequenceResult doit() {
            setupInvalidators();
            return new BoundSequenceResult(bindees(), invalidators(), interClass(), makeGetElementBody(), makeSizeBody());
        }

        protected JCExpression getReceiverForCallHack(Symbol sym) {
            if (sym.isStatic()) {
                return makeType(sym.owner.type, false);
            }
            return getReceiver(sym);
        }

        JCExpression CallSize(Symbol sym) {
            return CallSize(getReceiverForCallHack(sym), sym);
        }

        JCExpression CallSize(JCExpression rcvr, Symbol sym) {
            if (types.isArray(sym.type)) {
                JCVariableDecl vArr = TmpVar("arr", sym.type, Getter(rcvr,sym));
                return 
                    BlockExpression(
                        vArr,
                        If (NEnull(id(vArr)),
                            Select(id(vArr), names.length),
                            Int(0)
                        )
                    );
            }
            else if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeSizeName(sym));
            else
                return Call(defs.Sequences_size, Getter(rcvr, sym));
        }

        JCExpression CallGetElement(Symbol sym, JCExpression pos) {
            return CallGetElement(getReceiverForCallHack(sym), sym, pos);
        }

        JCExpression CallGetElement(JCExpression rcvr, Symbol sym, JCExpression pos) {
            if (types.isArray(sym.type)) {
                JCVariableDecl vArr = TmpVar("arr", sym.type, Getter(rcvr,sym));
                JCVariableDecl vPos = TmpVar("pos", syms.intType, pos);
                return
                    BlockExpression(
                        vArr,
                        vPos,
                        If (OR(OR(
                                EQnull(id(vArr)),
                                LT(id(vPos), Int(0))),
                                LE(Select(id(vArr), names.length), id(vPos))),
                            DefaultValue(types.elemtype(sym.type)),
                            m().Indexed(id(vArr), id(vPos))
                        )
                    );
            }
            else if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeGetElementName(sym), pos);
            else
                return Call(Getter(rcvr, sym), defs.get_SequenceMethodName, pos);
        }

        private Name activeFlagBit = defs.varFlagSEQUENCE_LIVE;
        private F3VarSymbol flagSymbol = (F3VarSymbol)targetSymbol;

        JCExpression isSequenceActive() {
            return FlagTest(flagSymbol, activeFlagBit, activeFlagBit);
        }

        JCExpression isSequenceDormant() {
            return FlagTest(flagSymbol, activeFlagBit, null);
        }

        JCStatement setSequenceActive() {
            return FlagChangeStmt(flagSymbol, null, BITOR(id(defs.varFlagSEQUENCE_LIVE), id(defs.varFlagINIT_INITIALIZED_DEFAULT)));
        }

        JCStatement Assign(JCExpression vid, JCExpression value) {
            return Stmt(m().Assign(vid, value));
        }
        JCStatement Assign(JCVariableDecl var, JCExpression value) {
            return Assign(id(var), value);
        }

        @Override
        void addInvalidator(F3VarSymbol sym, JCStatement invStmt) {
            super.addInvalidator(sym, invStmt);
            if (depGraphWriter != null) {
                depGraphWriter.writeDependency(targetSymbol, sym);
            }
        }
    }

    /**
     * Bound identifier Translator for identifiers referencing sequences
     *
     * Just forward the requests for size and elements
     */
    class BoundIdentSequenceTranslator extends BoundSequenceTranslator {
        // Symbol of the referenced
        private final F3VarSymbol sym;

        // ExpressionResult for etracting bindee info
        private final ExpressionResult exprResult;

        BoundIdentSequenceTranslator(F3Ident tree, ExpressionResult exprResult) {
            super(tree.pos());
            this.sym = (F3VarSymbol) tree.sym;
            this.exprResult = exprResult;
        }

        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CallSize(sym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return(id(vSize))
                );
        }

        JCStatement makeGetElementBody() {
            return Block(
                         If (isSequenceDormant(),
                             Stmt(CallSize(targetSymbol))
                             ),
                         Return(CallGetElement(sym, posArg()))
                         );
        }

        /**
         * Simple bindee info from normal translation will do it
         */
        void setupInvalidators() {
            mergeResults(exprResult);
         }
    }

    /**
     * Bound identifier Translator for identifiers referencing sequences but pretending to be non-sequences
     *
     * Use the referenced as a sequence
     */
    class BoundIdentSequenceFromNonTranslator extends BoundSequenceTranslator {

        // Symbol of the referenced
        private final F3VarSymbol sym;

        // Size holder
        private final F3VarSymbol sizeSym;

        BoundIdentSequenceFromNonTranslator(F3IdentSequenceProxy tree) {
            super(tree.pos());
            this.sym = (F3VarSymbol) tree.sym;
            this.sizeSym = tree.boundSizeSym();
        }

        JCExpression makeSizeValue() {
            return Call(Getter(sym), defs.size_SequenceMethodName);
        }

        /**
         * Body of the sequence size method.
         *
         * Get the stored size.
         * If the sequence is uninitialized (size is invalid)
         *   Set the size var, from the proxied result. (thus initializing the sequence).
         *   Send initial update nodification.
         * Return the size
         */
        JCStatement makeSizeBody() {
            JCVariableDecl sizeVar = MutableTmpVar("size", syms.intType, Get(sizeSym));

            return
                Block(
                    sizeVar,
                    If(EQ(id(sizeVar), Undefined()),
                        Block(
                            Stmt(m().Assign(id(sizeVar), makeSizeValue())),
                            SetStmt(sizeSym, id(sizeVar)),
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(sizeVar))
                        )
                    ),
                    Return(id(sizeVar))
                );
        }

        /**
         * Body of the sequence get element method.
         *
         * Make sure the sequence is initialized, by calling the size method.
         * Redirect to the proxied sequence to get the element.
         */
        JCStatement makeGetElementBody() {
            return
                Block(
                    If(EQ(Get(sizeSym), Undefined()),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return (Call(Getter(sym), defs.get_SequenceMethodName, posArg()))
                );
        }

        /**
         * Body of a invalidate$ method for the proxied sequences
         *
         * Do nothing if the sequence is uninitialized.
         * If this is invalidation phase,
         *     send a blanket invalidation of the sequence.
         * If this is trigger phase,
         *     send an invalidation of the whole sequence
         *     update the sequence size,
         */
        private JCStatement makeInvalidateFuncValue() {
            JCVariableDecl oldSizeVar = TmpVar("oldSize", syms.intType, Get(sizeSym));
            JCVariableDecl newSizeVar = TmpVar("newSize", syms.intType, makeSizeValue());

            return
                Block(
                    oldSizeVar,
                    If(NE(id(oldSizeVar), Undefined()),
                        PhaseCheckedBlock(sym,
                            If(IsInvalidatePhase(),
                                Block(
                                    CallSeqInvalidateUndefined(targetSymbol)
                                ),
                            /*Else (Trigger phase)*/
                                Block(
                                    newSizeVar,
                                    SetStmt(sizeSym, id(newSizeVar)),
                                    CallSeqTrigger(targetSymbol, Int(0), id(oldSizeVar), id(newSizeVar))
                                )
                            )
                        )
                    )
                );
        }

        /**
         * Set-up proxy's invalidator.
         */
        void setupInvalidators() {
            addInvalidator(sym, makeInvalidateFuncValue());
        }
    }

    /**
     * Bound block Translator block of sequence type
     *
     * Assumptions:
     *   Block vars have been moved out to class and replaced with VarInits.
     *   Block value has been made into a synthetic value, and a VarInit for has
     *   been added to block vars
     *
     * Core is that VarInits are run when size is first queried.
     */
    class BoundBlockSequenceTranslator extends BoundSequenceTranslator {

        // Symbol of the referenced
        private final F3VarSymbol vsym;

        // The VarInits aka the non-value part of the block
        private final List<F3Expression> varInits;

        BoundBlockSequenceTranslator(F3Block tree) {
            super(tree.pos());
            F3Ident id = (F3Ident) (tree.value);
            this.vsym = (F3VarSymbol) id.sym;
            this.varInits = tree.stats;
        }

        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CallSize(vsym));
            ListBuffer<JCStatement> tVarInits = ListBuffer.lb();
            for (F3Expression init : varInits) {
                tVarInits.append(translateToStatement(init, syms.voidType));
            }
            tVarInits.append(vSize);
            tVarInits.append(setSequenceActive());
            tVarInits.append(CallSeqInvalidateUndefined(targetSymbol));
            tVarInits.append(CallSeqTriggerInitial(targetSymbol, id(vSize)));
            tVarInits.append(Return(id(vSize)));

            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            tVarInits.toList()
                        )
                    ),
                    Return(CallSize(vsym))
                );
        }

        JCStatement makeGetElementBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return(CallGetElement(vsym, posArg()))
                );
        }

        /**
         * Updates through value
         */
        void setupInvalidators() {
            addBindee(vsym);
        }
    }

    /**
     * Bound type-cast Translator for type-cast from-and-to sequences
     *
     * Just forward the requests for size and elements, the latter type-converted
     * to its the desired element type.
     */
    class BoundTypeCastSequenceTranslator extends BoundSequenceTranslator {

        final F3VarSymbol exprSym;
        final Type elemType;

        BoundTypeCastSequenceTranslator(F3TypeCast tree) {
            super(tree.pos());
            assert types.isSequence(tree.type);
            assert tree.getExpression() instanceof F3Ident; // Decompose should shred
            this.exprSym = (F3VarSymbol)((F3Ident)tree.getExpression()).sym;
            assert types.isSequence(tree.getExpression().type) || types.isArray(tree.getExpression().type);
            this.elemType = types.elementType(tree.type);
        }

        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar(syms.intType, CallSize(exprSym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return(id(vSize))
                );
       }

        JCStatement makeGetElementBody() {
            //we could have a sequence of Object converted into sequence of Integers
            //in this case the target type of the cast should be the boxed sequence
            //element type (this doesn't get handled in lower)
            Type targetType = elemType.isPrimitive() &&
                    !types.elementType(exprSym.type).isPrimitive() ?
                types.boxedTypeOrType(elemType) :
                elemType;
            return Return(m().TypeCast(makeType(targetType), CallGetElement(exprSym, posArg())));
        }

        /**
         * Simple bindee info from normal translation will do it
         */
        void setupInvalidators() {
            addBindee(exprSym);
        }
    }

    /**
     * Bound type-cast Translator for type-cast from nativearray to sequence
     */
    class BoundTypeCastArrayToSequenceTranslator extends BoundTypeCastSequenceTranslator {

        private final F3VarSymbol sizeSym;

        BoundTypeCastArrayToSequenceTranslator(F3TypeCast tree) {
            super(tree);
            this.sizeSym = tree.boundArraySizeSym;
        }

        @Override
        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar(syms.intType, CallSize(exprSym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            SetStmt(sizeSym, id(vSize)),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return(id(vSize))
                );
       }

        /**
         * Body of a invalidate$ method for the nativearray
         */
        private JCStatement makeInvalidateArray() {
            JCVariableDecl oldSizeVar = TmpVar("oldSize", syms.intType, Get(sizeSym));
            JCVariableDecl newSizeVar = TmpVar("newSize", syms.intType, CallSize(exprSym));

            return
                PhaseCheckedBlock(exprSym,
                    If (isSequenceActive(),
                        Block(
                            If(IsInvalidatePhase(),
                                Block(
                                    CallSeqInvalidateUndefined(targetSymbol)
                                ),
                            /*Else (Trigger phase)*/
                                Block(
                                    oldSizeVar,
                                    newSizeVar,
                                    SetStmt(sizeSym, id(newSizeVar)),
                                    CallSeqTrigger(targetSymbol, Int(0), id(oldSizeVar), id(newSizeVar))
                                )
                            )
                        )
                    )
                );
        }

        /**
         * Set-up array's invalidator.
         */
        @Override
        void setupInvalidators() {
            addInvalidator(exprSym, makeInvalidateArray());
        }
    }

    /**
     * Bound empty sequence Translator
     *
     * Size is always zero, element is always an error (so return default value)
     */
    class BoundEmptySequenceTranslator extends BoundSequenceTranslator {
        private final Type elemType;
        BoundEmptySequenceTranslator(F3SequenceEmpty tree) {
            super(tree.pos());
            this.elemType = types.elementType(tree.type);
        }

        JCStatement makeSizeBody() {
            return Return(Int(0));
        }

        JCStatement makeGetElementBody() {
            return Return(DefaultValue(elemType));
        }

        void setupInvalidators() {
            // nada
        }
    }

    /**
     * Bound member-select reference to a sequence Translator
     *
     *
     */
    private class BoundSelectSequenceTranslator extends BoundSequenceTranslator {

        private final SelectTranslator strans;
        private final Symbol refSym;
        private final F3VarSymbol selectorSym;
        private final F3VarSymbol sizeSym;


        BoundSelectSequenceTranslator(F3Select tree) {
            super(tree.pos());
            this.strans = new SelectTranslator(tree);
            this.refSym = strans.refSym;
            this.sizeSym = tree.boundSize.sym;
            F3Expression selectorExpr = tree.getExpression();
            assert canChange();
            assert (selectorExpr instanceof F3Ident);
            F3Ident selector = (F3Ident) selectorExpr;
            this.selectorSym = (F3VarSymbol) selector.sym;
        }

        /*** forward to SelectTranslator ***/
        private F3Expression getToCheck() { return strans.getToCheck(); }
        private JCExpression translateToCheck(F3Expression expr) { return strans.translateToCheck(expr); }
        private boolean canChange() { return strans.canChange(); }
        private JCExpression wrapInNullCheckExpression(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
             return strans.wrapInNullCheckExpression(full, tToCheck, theResultType, theFullType);
        }
        private JCStatement wrapInNullCheckStatement(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
             return strans.wrapInNullCheckStatement(full, tToCheck, theResultType, theFullType);
        }

        private JCExpression selector() {
            return Get(selectorSym);
        }

        /**
         * Size accessor
         * (
         *     if ( default-not-set ) {
         *         set-default-flag;
         *         clear selector's trigger flag
         *         invalidate the selector
         *     }
         *     // redirect to the size of the referenced sequence, updating the selector
         *     return get$selector()==null? 0 : get$selector().size$ref();
         * }
         *
         */
        JCStatement makeSizeBody() {
            assert selectorSym.useAccessors() : "Would need redesign to implement without accessors";

            JCExpression tToCheck = translateToCheck(getToCheck());
            JCStatement callSize = buildBody(tToCheck, CallSize(tToCheck, refSym), syms.intType);

            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            FlagChangeStmt(selectorSym, defs.varFlagINIT_STATE_MASK, defs.varFlagVALID_DEFAULT_APPLIED),
                            CallBeInvalidate(selectorSym),
                            CallBeTrigger(selectorSym)
                        )
                    ),
                    callSize
                );
        }

        /**
         * Get sequence element
         * {
         *     size$self(); // Access size to make sure we are initialized
         *     return get$selector()==null? <default> : get$selector().get$ref(pos);
         * }
         */
        JCStatement makeGetElementBody() {
            JCExpression tToCheck = translateToCheck(getToCheck());

            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    buildBody(tToCheck, CallGetElement(tToCheck, refSym, posArg()), types.elementType(refSym.type))
                );
        }

        protected JCStatement buildBody(JCExpression tToCheck, JCExpression full, Type theResultType) {
            return wrapInNullCheckStatement(full, tToCheck, theResultType, theResultType);
        }

        /**
         *  $selector === null? 0 : $selector.size$ref();
         */
        private JCExpression getSize() {
            if (refSym.isStatic()) {
                return CallSize(makeType(refSym.owner), refSym);
            } else {
                return
                    If (EQnull(selector()),
                        Int(0),
                        CallSize(selector(), refSym));
            }
        }

        /**
         * Invalidator for the selector
         *
         * invalidator$selector(int phase) {
         *     if (valid-for-this-phase) {
         *         clear-valid-for-this-phase;
         *         if ( is-invalidate-phase) {
         *             // remove dependent, do it now, so updates from referenced don't interject
         *             removeDependent($selector, VOFF$ref);
         *             // invalidate with undefined newSize, since we can't compute without enter trigger phase
         *             invalidate$self(0, undefined, undefined, phase);
         *         } else {
         *             int oldSize = $selector === null? 0 : $selector.size$ref();
         *             get$selector();  // update selector
         *             int newSize = $selector === null? 0 : $selector.size$ref();
         *             addDependent($selector, VOFF$ref);
         *             invalidate$self(0, oldSize, newSize, phase);
         *         }
         *     }
         * }
         */
        private JCStatement makeInvalidateSelector() {
            JCVariableDecl oldSize = TmpVar(syms.intType, Get(sizeSym));
            JCVariableDecl newSize = TmpVar(syms.intType, getSize());

            return
                PhaseCheckedBlock(selectorSym,
                        If (IsInvalidatePhase(),
                            Block(
                                If (NEnull(selector()),
                                    CallStmt(defs.F3Base_removeDependent,
                                       selector(),
                                       Offset(selector(), refSym),
                                       getReceiverOrThis(selectorSym)
                                    )
                                ),
                                CallSeqInvalidateUndefined(targetSymbol)
                            ),
                        /*Else (Trigger phase)*/
                            Block(
                                oldSize,
                                Stmt(Getter(selectorSym)),
                                newSize,
                                If (NEnull(selector()),
                                    CallStmt(defs.F3Base_addDependent,
                                        selector(),
                                        Offset(selector(), refSym),
                                        getReceiverOrThis(selectorSym),
                                        DepNum(getReceiver(selectorSym), selectorSym, refSym)
                                    )
                                ),
                                CallSeqTrigger(targetSymbol,
                                    Int(0),
                                    id(oldSize),
                                    id(newSize)
                                )
                            )
                        )
                    );
        }

        /**
         * Addition to the invalidate for this bound select sequence
         *
         * invalidate$self(int start, int end, int newLen, int phase) {
         *     if ( is-trigger-phase ) {
         *         $size = $size + newLen - (end - start);
         *     }
         *     ....
         * }
         */
        private JCStatement makeInvalidateSelf() {
            return
                Block(
//                    setSequenceActive(),
                    If (IsTriggerPhase(),
                        SetStmt(sizeSym,
                            PLUS(
                                Get(sizeSym),
                                MINUS(
                                    newLengthArg(),
                                    MINUS(
                                        endPosArg(),
                                        startPosArg()
                                    )
                                )
                            )
                        )
                    )
                );
        }

        void setupInvalidators() {
            addInvalidator(selectorSym, makeInvalidateSelector());
            addInvalidator(targetSymbol, makeInvalidateSelf());
            addInterClassBindee(selectorSym, refSym);
        }
    }

    /**
     * Bound reverse operator sequence Translator
     */
    private class BoundReverseSequenceTranslator extends BoundSequenceTranslator {

        private final F3VarSymbol argSym;

        BoundReverseSequenceTranslator(F3Unary tree) {
            super(tree.pos());
            F3Ident arg = (F3Ident) tree.arg;
            this.argSym = (F3VarSymbol) arg.sym;
        }

        /**
         * Size accessor -- pass through
         */
        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CallSize(argSym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return(id(vSize))
                );
        }

        /**
         * Get sequence element
         * Element is underlying element at size - 1 - index
         */
        JCStatement makeGetElementBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return ( CallGetElement(argSym, MINUS( MINUS(CallSize(argSym), Int(1)), posArg()) ) )
                );
        }

        /**
         * Pass-through the invalidation reversing the start and end
         *
         * Compute the old size:
         *    oldSize = newSize - (newLen - (end - start));
         *
         * reversedStart = oldSize - 1 - (end - 1) = oldSize - end
         * reversedEnd = oldSize - 1 + 1 - start = oldSize - start
         */
        private JCStatement makeInvalidateArg() {
            JCVariableDecl oldSize = TmpVar(syms.intType,
                        MINUS(
                            CallSize(argSym),
                            MINUS(
                                newLengthArg(),
                                MINUS(
                                    endPosArg(),
                                    startPosArg()
                                )
                            )
                        ));

            return
                If (IsInvalidatePhase(),
                    Block(
                        CallSeqInvalidateUndefined(targetSymbol)
                    ),
                /*Else (Trigger phase)*/
                    Block(
                        If(IsUnchangedTrigger(),
                            Block(
                                CallSeqTriggerUnchanged(targetSymbol) // Pass it on
                            ),
                        /*else (real trigger)*/
                            Block(
                                oldSize,
                                CallSeqTrigger(targetSymbol,
                                    MINUS(id(oldSize), endPosArg()),
                                    MINUS(id(oldSize), startPosArg()),
                                    newLengthArg()
                                )
                           )
                        )
                    )
                );
        }

        void setupInvalidators() {
                addInvalidator(argSym, makeInvalidateArg());
        }
    }

    /**
     * Bound explicit sequence Translator ["hi", [2..k], x]
     *
     *
     */
    private abstract class AbstractBoundExplicitSequenceTranslator extends BoundSequenceTranslator {
        private final List<F3VarSymbol> itemSyms;
        private final List<F3VarSymbol> itemLengthSyms;
        final F3VarSymbol ignoreInvalidationsSym;
        final Type elemType;
        final int length;

        boolean DEBUG = false;

        AbstractBoundExplicitSequenceTranslator(F3SequenceExplicit tree) {
            super(tree.pos());
            this.itemSyms = tree.boundItemsSyms;
            this.itemLengthSyms = tree.boundItemLengthSyms;
            this.length = itemSyms.length();
            this.elemType = types.elementType(tree.type);
            this.ignoreInvalidationsSym = tree.boundIgnoreInvalidationsSym;
        }

        abstract JCStatement makeItemInvalidatePhase(int index);
        abstract JCStatement makeItemTriggerPhase(int index, boolean isSequence);

        boolean isSequence(int index) {
            return types.isSequence(type(index));
        }

        boolean isFixedLength(int index) {
            return itemLengthSym(index) == null;
        }

        F3VarSymbol itemLengthSym(int index) {
            return itemLengthSyms.get(index);
        }

        F3VarSymbol itemSym(int index) {
            return itemSyms.get(index);
        }

        Type type(int index) {
            return itemSym(index).type;
        }

        JCExpression IsInvalid(F3VarSymbol sym) {
            return FlagTest(sym, defs.varFlagINVALID_STATE_BIT, defs.varFlagINVALID_STATE_BIT);
        }

        JCExpression IsInvalid(int index) {
            return IsInvalid(itemSym(index));
        }

        JCStatement UpdateValue(int index) {
            if (isFixedLength(index)) {
                return SetStmt(itemSym(index), Getter(itemSym(index)));
            } else {
                return SetStmt(itemLengthSym(index),
                           isSequence(index)?
                                CallSize(itemSym(index)) :
                                If (EQnull(Set(itemSym(index), Getter(itemSym(index)))), Int(0), Int(1))
                      );
            }
        }

        JCStatement Update(int index) {
            return
                Block(
                    UpdateValue(index),
                    FlagChangeStmt(itemSym(index), defs.varFlagSTATE_MASK, defs.varFlagVALID_DEFAULT_APPLIED)
                );
        }

        JCStatement UpdateAll() {
            ListBuffer<JCStatement> upds = ListBuffer.lb();
            for (int i = 0; i < length; ++i) {
                upds.append(Update(i));
            }
            return 
                    upds.length()==1?
                        upds.first() :
                        Block(upds);
        }

        JCExpression TransientLength(int index) {
            if (isFixedLength(index)) {
                return Int(1);
            } else {
                return isSequence(index)?
                                CallSize(itemSym(index)) :
                                If (IsInvalid(index),
                                    If (EQnull(Getter(itemSym(index))), Int(0), Int(1)),
                                    Get(itemLengthSym(index))
                                );
            }
        }

        JCExpression CummulativeTransientLength(int index) {
            JCExpression sum = index==0? Int(0) : TransientLength(0);
            for (int i = 1; i < index; ++i) {
                sum = PLUS(sum, TransientLength(i));
            }
            return sum;
        }

        JCExpression CachedLength(int index) {
            F3VarSymbol lenSym = itemLengthSym(index);
            return lenSym==null? Int(1) : Get(lenSym);
        }

        JCExpression CummulativeCachedSize(int index) {
            JCExpression sum = index==0? Int(0) : CachedLength(0);
            for (int i = 1; i < index; ++i) {
                sum = PLUS(sum, CachedLength(i));
            }
            return sum;
        }

        JCExpression CachedGetElement(int index, JCExpression pos) {
            if (isSequence(index)) {
                return Call(attributeGetElementName(itemSym(index)), pos);
            } else {
                return Get(itemSym(index));
            }
        }

        JCExpression TransientGetElement(int index, JCExpression pos) {
            if (isSequence(index)) {
                return Call(attributeGetElementName(itemSym(index)), pos);
            } else {
                return 
                    If (IsInvalid(index),
                        Getter(itemSym(index)),
                        Get(itemSym(index))
                    );
            }
        }

        /**
         * Invalidate addition for an element in the sequence
         */
        private JCStatement makeItemInvalidateInner(int index) {
            return
                    If (AND(isSequenceActive(), NOT(Get(ignoreInvalidationsSym))),
                        Block(
                            If (IsInvalidatePhase(),
                                makeItemInvalidatePhase(index),
                            /*Else (Trigger phase)*/
                                makeItemTriggerPhase(index, isSequence(index))
                            )
                        ));
        }

        /**
         * Invalidate addition for an element in the sequence
         */
        private JCStatement makeItemInvalidate(int index) {
            if (isSequence(index)) {
                return
                    makeItemInvalidateInner(index);
            } else {
                return
                    PhaseCheckedBlock(itemSym(index),
                        makeItemInvalidateInner(index)
                    );
            }
        }

        /**
         * For each item, and for size, set-up the invalidate method
         */
        void setupInvalidators() {
            for (int index = 0; index < length; ++index) {
                addInvalidator(itemSym(index), makeItemInvalidate(index));
            }
         }
    }

    /**
     * Bound explicit sequence Translator ["hi", [2..k], x]
     *
     *
     */
    private class BoundExplicitSequenceTranslator extends AbstractBoundExplicitSequenceTranslator {
        private final F3VarSymbol sizeSym;
        private final F3VarSymbol lowestSym;
        private final F3VarSymbol highestSym;
        private final F3VarSymbol pendingSym;
        private final F3VarSymbol deltaSym;
        private final F3VarSymbol changeStartSym;
        private final F3VarSymbol changeEndSym;

        BoundExplicitSequenceTranslator(F3SequenceExplicit tree) {
            super(tree);
            this.sizeSym = tree.boundSizeSym;
            this.lowestSym = tree.boundLowestInvalidPartSym;
            this.highestSym = tree.boundHighestInvalidPartSym;
            this.pendingSym = tree.boundPendingTriggersSym;
            this.deltaSym = tree.boundDeltaSym;
            this.changeStartSym = tree.boundChangeStartPosSym;
            this.changeEndSym = tree.boundChangeEndPosSym;
        }

        private boolean isFixedLength() {
            return sizeSym == null;
        }

        private JCExpression CachedSize() {
            if (isFixedLength()) {
                return Int(length);
            } else {
                return Get(sizeSym);
            }
        }

        private JCStatement SetSizeStmt(JCExpression value) {
            if (isFixedLength()) {
                return null;
            } else {
                return SetStmt(sizeSym, value);
            }
        }

        private JCExpression GetChangeStart() {
            if (isFixedLength()) {
                return Get(lowestSym);
            } else {
                return Get(changeStartSym);
            }
        }

        private JCStatement SetChangeStartStmt(JCExpression value) {
            if (isFixedLength()) {
                return null;
            } else {
                return SetStmt(changeStartSym, value);
            }
        }

        private JCStatement SetChangeEndStmt(JCExpression value) {
            if (isFixedLength()) {
                return null;
            } else {
                return SetStmt(changeEndSym, value);
            }
        }

        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CummulativeCachedSize(length));

            return
                Block(
                    If(isSequenceDormant(),
                        Block(
                            UpdateAll(),
                            vSize,
                            setSequenceActive(),
                            SetSizeStmt(id(vSize)),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize)),
                            Return(id(vSize))
                        )
                    ),
                    DEBUG? Debug("size pending=", Get(pendingSym)) : null,
                    DEBUG? Debug("   size=", CachedSize()) : null,
                    Return(
                        isFixedLength()?
                            Int(length) :
                            If (EQ(Get(pendingSym), Int(0)),
                                CachedSize(),
                                CummulativeTransientLength(length)
                            )
                    )
                );
        }

        /**
         * if (pos < 0) {
         *    return null; // default
         * }
         * int start = 0;
         * int next = 0;
         * next += size$s1();
         * if (pos < next) {
         *    return get$s1(pos – start);
         * }
         * start = next;
         * next += size$s2();
         * if (pos < next) {
         *    return get$s2(pos – start);
         * }
         * start = next;
         * next += size$s3();
         * if (pos < next) {
         *    return get$s3(pos – start);
         * )
         * return null; // default
         */
        JCStatement makeGetElementBody() {
            JCVariableDecl vStart = MutableTmpVar("start", syms.intType, Int(0));
            JCVariableDecl vNext = MutableTmpVar("next", syms.intType, Int(0));
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            ListBuffer<JCStatement> pendStmts = ListBuffer.lb();
            ListBuffer<JCStatement> normalStmts = ListBuffer.lb();

            stmts.appendList(Stmts(
                    DEBUG? Debug("GetElement pending=", Get(pendingSym)) : null,
                    DEBUG? Debug("   size=", CachedSize()) : null,
                    If(LT(posArg(), Int(0)),
                        Return(DefaultValue(elemType))
                    ),
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    vStart,
                    vNext
                ));
            //TODO: switch-statement if isFixedLength
            for (int index = 0; index < length; ++index) {
                pendStmts.appendList(Stmts(
                        Assign(vNext, PLUS(id(vNext), TransientLength(index))),
                        If(LT(posArg(), id(vNext)),
                            Return(TransientGetElement(index, MINUS(posArg(), id(vStart))))
                        ),
                        Assign(vStart, id(vNext))
                    ));
            }
            for (int index = 0; index < length; ++index) {
                normalStmts.appendList(Stmts(
                        Assign(vNext, PLUS(id(vNext), CachedLength(index))),
                        If(LT(posArg(), id(vNext)),
                            Return(CachedGetElement(index, MINUS(posArg(), id(vStart))))
                        ),
                        Assign(vStart, id(vNext))
                    ));
            }
            stmts.appendList(Stmts(
                    If (EQ(Get(pendingSym), Int(0)),
                        Block(normalStmts),
                        Block(pendStmts)
                    ),
                    Return(DefaultValue(elemType)
            )));
            return Block(stmts);
        }

        JCStatement makeItemInvalidatePhase(int index) {
            return
                Block(
                    If (LT(Get(highestSym), Int(0)),
                        Block(
                            Assert(EQ(Get(highestSym), Undefined())),
                            Assert(EQ(Get(pendingSym), Int(0))),
                            DEBUG? Debug("inv 1st #"+index+" pending=", Get(pendingSym)) : null,
                            SetStmt(highestSym, Int(index)),
                            SetStmt(lowestSym, Int(index)),
                            SetStmt(pendingSym, Int(1)),
                            isFixedLength()? null :
                                SetStmt(deltaSym, Int(0)),
                            SetChangeStartStmt(CummulativeCachedSize(index)),
                            SetChangeEndStmt(PLUS(GetChangeStart(), CachedLength(index))),
                            CallSeqInvalidateUndefined(targetSymbol)
                        ),
                    /*Else (already have invalid parts)*/
                        Block(
                            DEBUG? Debug("inv #"+index+" pending=", Get(pendingSym)) : null,
                            SetStmt(pendingSym, PLUS(Get(pendingSym), Int(1))),
                            If (LT(Int(index), Get(lowestSym)),
                                Block(
                                    SetChangeStartStmt(CummulativeCachedSize(index)),
                                    SetStmt(lowestSym, Int(index))
                                )
                            ),
                            If (GT(Int(index), Get(highestSym)),
                                Block(
                                    SetChangeEndStmt(CummulativeCachedSize(index+1)),
                                    SetStmt(highestSym, Int(index))
                                )
                            )
                        )
                    )
                );
        }

        JCStatement makeItemTriggerPhase(int index, boolean isSequence) {
            JCVariableDecl vEnd = TmpVar("hi", syms.intType, PLUS(Get(highestSym), Int(1)));

            JCStatement triggerChanged =
                    isFixedLength()?
                        Block(
                            vEnd,
                            SetStmt(highestSym, Undefined()),
                            DEBUG? Debug("bulk #"+index+" changeStart=", Get(lowestSym)) : null,
                            CallSeqTrigger(targetSymbol,
                                Get(lowestSym),
                                id(vEnd),
                                MINUS(id(vEnd), Get(lowestSym))
                            )
                        ) :
                        Block(
                            SetStmt(highestSym, Undefined()),
                            DEBUG? Debug("bulk #"+index+" changeStart=", GetChangeStart()) : null,
                            CallSeqTrigger(targetSymbol,
                                GetChangeStart(),
                                Get(changeEndSym),
                                PLUS(Get(deltaSym), MINUS(Get(changeEndSym), GetChangeStart()))
                            )
                        );
            JCStatement fire;
            if (isSequence) {
                fire =
                    Block(
                        If (
                            AND(
                                EQ(Get(highestSym), Int(index)),
                                EQ(Get(lowestSym), Int(index))
                            ),
                            Block(
                                    SetStmt(highestSym, Undefined()),
                                    If (IsUnchangedTrigger(),
                                        Block(
                                            DEBUG? Debug("uncng #"+index, Int(index)) : null,
                                            CallSeqTriggerUnchanged(targetSymbol) // Pass it on
                                        ),
                                    /*else (real trigger)*/
                                        Block(
                                            DEBUG? Debug("one #"+index+" changeStart=", GetChangeStart()) : null,
                                            DEBUG? Debug("   endPos=", endPosArg()) : null,
                                            CallSeqTrigger(targetSymbol,
                                                PLUS(GetChangeStart(), startPosArg()),
                                                If (EQ(endPosArg(), Undefined()),
                                                    Undefined(),
                                                    PLUS(GetChangeStart(), endPosArg())
                                                ),
                                                newLengthArg()
                                            )
                                        )
                                    )
                            ),
                            triggerChanged
                        )
                    );
            } else {
                fire = triggerChanged;
            }

            JCVariableDecl vOldLength = TmpVar("oldLen", syms.intType, CachedLength(index));
            JCVariableDecl vNewLength = TmpVar("newLen", syms.intType, CachedLength(index));

            return
                Block(
                    Assert(AND(GE(Int(index), Get(lowestSym)), LE(Int(index), Get(highestSym)))),
                    Assert(GT(Get(pendingSym), Int(0))),
                    SetStmt(pendingSym, MINUS(Get(pendingSym), Int(1))),
                    isFixedLength(index)? null :
                        vOldLength,
                    SetStmt(ignoreInvalidationsSym, True()),
                    Update(index),
                    SetStmt(ignoreInvalidationsSym, False()),
                    isFixedLength(index)? null :
                        vNewLength,
                    isFixedLength(index)? null : 
                        SetSizeStmt(PLUS(CachedSize(), MINUS(id(vNewLength), id(vOldLength)))),
                    isFixedLength(index)? null :
                        SetStmt(deltaSym, PLUS(Get(deltaSym), MINUS(id(vNewLength), id(vOldLength)))),
                    DEBUG? Debug("trig #"+index+"  pending = ", Get(pendingSym)) : null,
                    If (EQ(Get(pendingSym), Int(0)),
                        fire
                    )
                );
        }
    }

    /**
     * Bound explicit sequence Translator [x]
     *
     *
     */
    private class BoundExplicitSingletonSequenceTranslator extends AbstractBoundExplicitSequenceTranslator {

        BoundExplicitSingletonSequenceTranslator(F3SequenceExplicit tree) {
            super(tree);
        }

        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, TransientLength(0));

            return
                Block(
                    If(isSequenceDormant(),
                        Block(
                            UpdateAll(),
                            vSize,
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize)),
                            Return(id(vSize))
                        )
                    ),
                    Return(TransientLength(0))
                );
        }

        JCStatement makeGetElementBody() {
            return
                Block(
                    If(NE(posArg(), Int(0)),
                        Return(DefaultValue(elemType))
                    ),
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return(TransientGetElement(0, null))
                );
        }

        JCStatement makeItemInvalidatePhase(int index) {
            return
                CallSeqInvalidateUndefined(targetSymbol);
        }

        JCStatement makeItemTriggerPhase(int index, boolean isSequence) {
            JCVariableDecl vOldLength = TmpVar("oldLen", syms.intType, CachedLength(index));

            return
                Block(
                    vOldLength,
                    SetStmt(ignoreInvalidationsSym, True()),
                    Update(index),
                    SetStmt(ignoreInvalidationsSym, False()),
                    CallSeqTrigger(targetSymbol,
                        Int(0),
                        id(vOldLength),
                        CachedLength(index)
                    )
                );
        }
    }

    /**
     * Bound range sequence Translator [10..100 step 10]
     */
    private class BoundRangeSequenceTranslator extends BoundSequenceTranslator {
        private final F3Var varLower;
        private final F3Var varUpper;
        private final F3Var varStep;
        private final F3Var varSize;
        private final Type elemType;
        private final boolean exclusive;

        BoundRangeSequenceTranslator(F3SequenceRange tree) {
            super(tree.pos());
            this.varLower = (F3Var)tree.getLower();
            this.varUpper = (F3Var)tree.getUpper();
            this.varStep = (F3Var)tree.getStepOrNull();
            this.varSize = tree.boundSizeVar;
            if (varLower.type == syms.f3_IntegerType) {
                this.elemType = syms.f3_IntegerType;
            } else {
                this.elemType = syms.f3_NumberType;
            }
            this.exclusive = tree.isExclusive();
        }

        private JCExpression zero() {
            return m().Literal(elemType.tag, 0);
        }

        private JCExpression one() {
            return m().Literal(elemType.tag, 1);
        }

        private JCExpression lower() {
            return Get(varLower.getSymbol());
        }
        private JCExpression upper() {
            return Get(varUpper.getSymbol());
        }
        private JCExpression step() {
            return varStep == null?
                  one()
                : Get(varStep.getSymbol());
        }
        private JCExpression size() {
            return Get(varSize.getSymbol());
        }

        private JCStatement setLower(JCExpression value) {
            return SetStmt(varLower.getSymbol(), value);
        }
        private JCStatement setUpper(JCExpression value) {
            return SetStmt(varUpper.getSymbol(), value);
        }
        private JCStatement setStep(JCExpression value) {
            return SetStmt(varStep.getSymbol(), value);
        }
        private JCStatement setSize(JCExpression value) {
            return SetStmt(varSize.getSymbol(), value);
        }

        private JCExpression CallGetter(F3Var var) {
            return Getter(var.getSymbol());
        }
        private JCExpression CallLower() {
            return CallGetter(varLower);
        }
        private JCExpression CallUpper() {
            return CallGetter(varUpper);
        }
        private JCExpression CallStep() {
            return varStep == null?
                  one()
                : CallGetter(varStep);
        }

        private JCExpression DIVstep(JCExpression v1) {
            return varStep == null?
                  v1
                : DIV(v1, step());
        }
        private JCExpression MULstep(JCExpression v1) {
            return varStep == null?
                  v1
                : MUL(v1, step());
        }
        private JCExpression exclusive() {
            return Boolean(exclusive);
        }

        private JCExpression calculateSize(JCExpression vl, JCExpression vu, JCExpression vs) {
            RuntimeMethod rm =
                    (elemType == syms.f3_NumberType)?
                          defs.Sequences_calculateFloatRangeSize
                        : defs.Sequences_calculateIntRangeSize;
            return Call(rm, vl, vu, vs, exclusive());
        }

        private JCExpression isInvalid(F3Var var) {
            if (var == null) {
                return False();
            } else {
                return FlagTest(var.getSymbol(), defs.varFlagINVALID_STATE_BIT, defs.varFlagINVALID_STATE_BIT);
            }
        }

        private JCStatement setValid(F3Var var) {
            if (var == null) {
                return null;
            } else {
                return FlagChangeStmt(var.getSymbol(), defs.varFlagSTATE_MASK, defs.varFlagSTATE_VALID);
            }
        }

        /**
         * int size$range() 
         */
        JCStatement makeSizeBody() {
            JCVariableDecl vNewLower = TmpVar("newLower", elemType, CallLower());
            JCVariableDecl vNewUpper = TmpVar("newUpper", elemType, CallUpper());
            JCVariableDecl vNewStep = TmpVar("newStep", elemType, CallStep());
            JCVariableDecl vNewSize = TmpVar("newSize", syms.intType,
                                        calculateSize(id(vNewLower), id(vNewUpper), id(vNewStep)));

            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            vNewLower,
                            vNewUpper,
                            vNewStep,
                            vNewSize,
                            setLower(id(vNewLower)),
                            setUpper(id(vNewUpper)),
                            (varStep == null)? null :
                                setStep(id(vNewStep)),
                            setSize(id(vNewSize)),
                            setValid(varLower),
                            setValid(varUpper),
                            setValid(varStep),
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vNewSize))
                        ),
                    /*else (it is live) */
                        If (OR(OR(isInvalid(varLower), isInvalid(varUpper)), isInvalid(varStep)),
                            Block(
                                // We are being asked for the size during an invalidation
                                // Calculate without changing state
                                Return (calculateSize(CallLower(), CallUpper(), CallStep()))
                            )
                        )
                    ),
                    Return (size())
                );
        }

        /**
         * float get$range(int pos) {
         *    return (pos >= 0 && pos < getSize())?
         *              pos * step + lower
         *            : 0.0f;
         * }
         */
        JCStatement makeGetElementBody() {
            JCVariableDecl vNewLower = TmpVar("newLower", elemType, CallLower());
            JCVariableDecl vNewUpper = TmpVar("newUpper", elemType, CallUpper());
            JCVariableDecl vNewStep = TmpVar("newStep", elemType, CallStep());
            JCVariableDecl vNewSize = TmpVar("newSize", syms.intType,
                                        calculateSize(id(vNewLower), id(vNewUpper), id(vNewStep)));

            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            // Force initialization
                            Stmt(CallSize(targetSymbol))
                        ),
                    /*else (it is live) */
                        If (OR(OR(isInvalid(varLower), isInvalid(varUpper)), isInvalid(varStep)),
                            Block(
                                // We are being asked for an element during an invalidation
                                // Calculate without changing state
                                vNewLower,
                                vNewUpper,
                                vNewStep,
                                vNewSize,
                                Return (
                                    If (AND(
                                            GE(posArg(), Int(0)),
                                            LT(posArg(), id(vNewSize))
                                        ),
                                        PLUS(MUL(posArg(), id(vNewStep)), id(vNewLower)),
                                        zero()
                                    )
                                )
                            )
                        )
                    ),
                    Return(
                        If (AND(
                                GE(posArg(), Int(0)),
                                LT(posArg(), size())
                            ),
                            PLUS(MULstep(posArg()), lower()),
                            zero()
                        )
                    )
                );
        }

        /**
         * float newLower = getLower();
         * if (step != 0 && lower != newLower) {
         *    int newSize = Sequences.calculateFloatRangeSize(newLower, upper, step, false);
         *    int loss = 0;
         *    int gain = 0;
         *    float delta = newLower - lower;
         *    if (size == 0 || ((delta % step) != 0)) {
         *      // invalidate everything - new or start point different
         * 	loss = size;
         * 	gain = newSize;
         *    } else if (newLower > lower) {
         *      // shrink -- chop off the front
         * 	loss = (int) delta / step;
         * 	if (loss > size)
         * 	    loss = size
         *    } else {
         *      // grow -- add to the beginning
         * 	gain = (int) -delta / step;
         *    }
         *    if (phase == TRIGGER_PHASE) {
         * 	lower = newLower;
         * 	size = newSize;
         *    }
         *    invalidate$range(0, loss, gain, phase);
         * }
         */
        private JCStatement makeInvalidateLower() {
            JCVariableDecl vNewLower = TmpVar("newLower", elemType, CallLower());
            JCVariableDecl vNewSize = TmpVar("newSize", syms.intType,
                                        calculateSize(id(vNewLower), upper(), step()));
            JCVariableDecl vLoss = MutableTmpVar("loss", syms.intType, Int(0));
            JCVariableDecl vGain = MutableTmpVar("gain", syms.intType, Int(0));
            JCVariableDecl vDelta = TmpVar("delta", elemType, MINUS(id(vNewLower), lower()));
            JCVariableDecl vUnits = TmpVar("units", elemType, DIVstep(id(vDelta)));

            return
                PhaseCheckedBlock(varLower.sym,
                    If (isSequenceActive(),
                        Block(
                            If (IsInvalidatePhase(),
                                Block(
                                    CallSeqInvalidateUndefined(targetSymbol)
                                ),
                            /*Else (Trigger phase)*/
                                Block(
                                    vNewLower,
                                    setValid(varLower),
                                    //Debug("Trig Lower ", id(vNewLower)),
                                    If (AND(NE(step(), zero()), NE(lower(), id(vNewLower))),
                                        Block(
                                            vNewSize,
                                            vLoss,
                                            vGain,
                                            vDelta,
                                            If (OR(EQ(size(), Int(0)), NE(MOD(id(vDelta), step()), zero())),
                                                Block( // was empty, or re-aligned on step
                                                    Assign(vLoss, size()),
                                                    Assign(vGain, id(vNewSize))
                                                ),
                                            /* else (not a redo) */
                                                Block(
                                                    vUnits,
                                                    If (GT(id(vUnits), zero()),
                                                        Block(
                                                            Assign(vLoss, m().TypeCast(syms.intType, id(vUnits))),
                                                            If (GT(id(vLoss), size()),
                                                                Assign(vLoss, size())
                                                            )
                                                        ),
                                                    /* else */
                                                        Block(
                                                            Assign(vGain, m().TypeCast(syms.intType, NEG(id(vUnits))))
                                                        )
                                                    )
                                                )
                                            ),
                                            setLower(id(vNewLower)),
                                            setSize(id(vNewSize)),
                                            CallSeqTrigger(targetSymbol, Int(0), id(vLoss), id(vGain))
                                        ),
                                    /*else (no change in lower, send no-change trigger)*/
                                        Block(
                                            CallSeqTriggerUnchanged(targetSymbol)
                                        )
                                    )
                                )
                            )
                        )));
        }

        /**
         * float newUpper = getUpper();
         * if (step != 0 && upper != newUpper) {
         *    int newSize = Sequences.calculateFloatRangeSize(lower, newUpper, step, false);
         *    int oldSize = size();
         *    if (phase == TRIGGER_PHASE) {
         *       upper = newUpper;
         *       size = newSize;
         *    }
         *    if (newSize >= oldSize)
         *       // grow
         *       invalidate$range(oldSize, oldSize, newSize-oldSize, phase);
         *    else
         *       // shrink
         *       invalidate$range(newSize, oldSize, 0, phase);
         * }
         */
        private JCStatement makeInvalidateUpper() {
            JCVariableDecl vNewUpper = TmpVar("newUpper", elemType, CallUpper());
            JCVariableDecl vOldSize = TmpVar("oldSize", syms.intType, size());
            JCVariableDecl vNewSize = TmpVar("newSize", syms.intType,
                                        calculateSize(lower(), id(vNewUpper), step()));

            return
                PhaseCheckedBlock(varUpper.sym,
                    If (isSequenceActive(),
                        Block(
                            If (IsInvalidatePhase(),
                                Block(
                                    CallSeqInvalidateUndefined(targetSymbol)
                                ),
                            /*Else (Trigger phase)*/
                                Block(
                                    vNewUpper,
                                    setValid(varUpper),
                                    //Debug("Trig Upper ", id(vNewUpper)),
                                    If (AND(NE(step(), zero()), NE(upper(), id(vNewUpper))),
                                        Block(
                                            vNewSize,
                                            vOldSize,
                                            setUpper(id(vNewUpper)),
                                            setSize(id(vNewSize)),
                                            If (GE(id(vNewSize), id(vOldSize)),
                                                CallSeqTrigger(targetSymbol, id(vOldSize), id(vOldSize), MINUS(id(vNewSize), id(vOldSize))),
                                            /*else*/
                                                CallSeqTrigger(targetSymbol, id(vNewSize), id(vOldSize), Int(0))
                                            )
                                        ),
                                    /*else (no change in upper, send no-change trigger)*/
                                        Block(
                                            CallSeqTriggerUnchanged(targetSymbol)
                                        )
                                    )
                                )
                            ))));
        }

        /**
         * float newStep = getStep();
         * if (step != newStep) {
         *    int newSize = Sequences.calculateFloatRangeSize(lower, upper, newStep, false);
         *    int oldSize = size();
         *    if (phase == TRIGGER_PHASE) {
         *       step = newStep;
         *       size = newSize;
         *    }
         *    // Invalidate everything
         *    invalidate$range(0, oldSize, newSize, phase);
         * }
         */
        private JCStatement makeInvalidateStep() {
            JCVariableDecl vNewStep = TmpVar("newStep", elemType, CallStep());
            JCVariableDecl vOldSize = TmpVar("oldSize", syms.intType, size());
            JCVariableDecl vNewSize = TmpVar("newSize", syms.intType,
                                        calculateSize(lower(), upper(), id(vNewStep)));

            return
                PhaseCheckedBlock(varStep.sym,
                    If (isSequenceActive(),
                        Block(
                            If (IsInvalidatePhase(),
                                Block(
                                    CallSeqInvalidateUndefined(targetSymbol)
                                ),
                            /*Else (Trigger phase)*/
                                Block(
                                    vNewStep,
                                    setValid(varStep),
                                    //Debug("Trig Step ", id(vNewStep)),
                                    If (NE(step(), id(vNewStep)),
                                        Block(
                                            vNewSize,
                                            vOldSize,
                                            setStep(id(vNewStep)),
                                            setSize(id(vNewSize)),
                                            CallSeqTrigger(targetSymbol, Int(0), id(vOldSize), id(vNewSize))
                                        ),
                                    /*else (no change in step, send no-change trigger)*/
                                        Block(
                                            CallSeqTriggerUnchanged(targetSymbol)
                                        )
                                    )
                          )))));
        }

        /**
         * Set invalidators for the synthetic support variables
         */
        void setupInvalidators() {
            addInvalidator(varLower.sym, makeInvalidateLower());
            addInvalidator(varUpper.sym, makeInvalidateUpper());
            if (varStep != null) {
                addInvalidator(varStep.sym, makeInvalidateStep());
            }
        }
    }

    /**
     * Bound slice sequence Translator seq[3..5]
     */
    private class BoundSliceSequenceTranslator extends BoundSequenceTranslator {
        private final F3VarSymbol seqSym;
        private final F3VarSymbol lowerSym;
        private final F3VarSymbol upperSym;
        private final boolean isExclusive;
        private final Type elemType;

        BoundSliceSequenceTranslator(F3SequenceSlice tree) {
            super(tree.pos());
            this.seqSym = (F3VarSymbol) (((F3Ident) tree.getSequence()).sym);
            this.lowerSym = (F3VarSymbol) (((F3Ident) tree.getFirstIndex()).sym);
            this.upperSym = tree.getLastIndex() == null?
                null :
                (F3VarSymbol) (((F3Ident) tree.getLastIndex()).sym);
            this.isExclusive = tree.getEndKind() == SequenceSliceTree.END_EXCLUSIVE;
            this.elemType = types.elementType(tree.type);
        }

        private JCExpression lower() {
            return Get(lowerSym);
        }
        private JCExpression upper() {
            return Get(upperSym);
        }

        private JCStatement setLower(JCExpression value) {
            return SetStmt(lowerSym, value);
        }

        private JCExpression CallSeqSize() {
            return CallSize(seqSym);
        }
        private JCExpression CallSeqGetElement(JCExpression pos) {
            return CallGetElement(seqSym, pos);
        }

        private JCExpression CallLower() {
            return Getter(lowerSym);
        }
        private JCExpression CallUpper() {
            return Getter(upperSym);
        }

        /**
         * if (lower < 0) lower = 0;
         * if (lower > underlyingSize) lower = underlyingSize;
         * if (upper < lower) upper = lower;
         * if (upper > underlyingSize) upper = underlyingSize;
         * int size = upper - lower;
         * if (sequence-inactive) {
         *   set-active;
         *   invalidate
         * }
         * return size
         */
        JCStatement makeSizeBody() {
            JCVariableDecl vSeqSize = TmpVar("seqSize", syms.intType, CallSeqSize());
            JCVariableDecl vLower = MutableTmpVar("lower", syms.intType, CallLower());
            // standardize on exclusive upper
            JCVariableDecl vUpper = MutableTmpVar("upper", syms.intType,
                    upperSym==null?
                        isExclusive?
                            MINUS(id(vSeqSize), Int(1)) :
                            id(vSeqSize) :
                        isExclusive?
                            CallUpper() :
                            PLUS(CallUpper(), Int(1)));
            JCVariableDecl vSize = TmpVar("size", syms.intType, MINUS(id(vUpper), id(vLower)));

            return
                Block(
                    vSeqSize,
                    vLower,
                    vUpper,
                    If (GT(id(vLower), id(vSeqSize)),
                        Assign(vLower, id(vSeqSize))
                    ),
                    If (LT(id(vUpper), id(vLower)),
                        Assign(vUpper, id(vLower))
                    ),
                    If (GT(id(vUpper), id(vSeqSize)),
                        Assign(vUpper, id(vSeqSize))
                    ),
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return (id(vSize))
                );
        }

        /**
         * if (sequence-is-dormant) {
         *   call size -- to initialize it
         * }
         * if (lower < 0) lower = 0;
         * if (pos < 0 || pos >= (upper - lower)) return default-value;
         * return seq[pos + lower];
         */
        JCStatement makeGetElementBody() {
            JCVariableDecl vLower = TmpVar("lower", syms.intType, CallLower());
            // standardize on exclusive upper
            JCVariableDecl vUpper = MutableTmpVar("upper", syms.intType,
                    upperSym==null?
                        isExclusive?
                            MINUS(CallSeqSize(), Int(1)) :
                            CallSeqSize() :
                        isExclusive?
                            CallUpper() :
                            PLUS(CallUpper(), Int(1)));

            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            Stmt(CallSize(targetSymbol))
                        )
                    ),
                    vLower,
                    vUpper,
                    If (LT(id(vUpper), id(vLower)),
                        Assign(vUpper, id(vLower))
                    ),
                    If (OR(LT(posArg(), Int(0)), GE(posArg(), MINUS(id(vUpper), id(vLower)))),
                        Return (DefaultValue(elemType))
                    ),
                    Return (CallSeqGetElement(PLUS(posArg(), id(vLower))))
                );
        }

        /**
         * Invalidator for lower
         */
        private JCStatement makeInvalidateLower() {
            JCVariableDecl vOldLower = MutableTmpVar("oldLower", syms.intType, lower());
            JCVariableDecl vNewLower = MutableTmpVar("newLower", syms.intType, CallLower());
            JCVariableDecl vSeqSize = TmpVar("seqSize", syms.intType, CallSeqSize());
            JCVariableDecl vUpper = MutableTmpVar("upper", syms.intType,
                    upperSym==null?
                        isExclusive?
                            MINUS(id(vSeqSize), Int(1)) :
                            id(vSeqSize) :
                        isExclusive?
                            CallUpper() :
                            PLUS(CallUpper(), Int(1)));

            return
                    PhaseCheckedBlock(lowerSym,
                        If (isSequenceActive(),
                            Block(
                                If (IsInvalidatePhase(),
                                    Block(
                                        CallSeqInvalidateUndefined(targetSymbol)
                                    ),
                                /*Else (Trigger phase)*/
                                    Block(
                                        vOldLower,
                                        vNewLower,
                                        vSeqSize,
                                        vUpper,
                                        If (LT(id(vNewLower), Int(0)),
                                            Assign(vNewLower, Int(0))
                                        ),
                                        setLower(id(vNewLower)),
                                        If (GT(id(vUpper), id(vSeqSize)),
                                            Assign(vUpper, id(vSeqSize))
                                        ),
                                        If (GT(id(vNewLower), id(vUpper)),
                                            Assign(vNewLower, id(vUpper))
                                        ),
                                        If (GT(id(vOldLower), id(vUpper)),
                                            Assign(vOldLower, id(vUpper))
                                        ),
                                        If (GT(id(vNewLower), id(vOldLower)),
                                            Block(
                                                // lose elements from the front
                                                CallSeqTrigger(targetSymbol, Int(0), MINUS(id(vNewLower), id(vOldLower)), Int(0))
                                            ),
                                        /*else*/ If (LT(id(vNewLower), id(vOldLower)),
                                            Block(
                                                // Gain elements in the front
                                                CallSeqTrigger(targetSymbol, Int(0), Int(0), MINUS(id(vOldLower), id(vNewLower)))
                                            ),
                                        /*else (no change, send no-change trigger)*/
                                            Block(
                                                CallSeqTriggerUnchanged(targetSymbol)
                                            )
                                        ))
                                   )
                                )
                            )
                        )
                    );
        }


        /**
         * Invalidator for upper
         *
         * Adjust uppers (old and new) between lower and the underlying sequence size.
         * If upper is greater, elements added at end:
         *   invalidate(oldSize, oldSize, delta)
         * If upper is lesser, elements removed from end
         *   invalidate(newSize, oldSize, 0)
         */
        private JCStatement makeInvalidateUpper() {
            JCVariableDecl vOldUpper = MutableTmpVar("oldUpper", syms.intType,
                    isExclusive?
                            upper() :
                            PLUS(upper(), Int(1)));
            JCVariableDecl vNewUpper = MutableTmpVar("newUpper", syms.intType,
                    isExclusive?
                            CallUpper() :
                            PLUS(CallUpper(), Int(1)));
            JCVariableDecl vSeqSize = TmpVar("seqSize", syms.intType, CallSeqSize());
            JCVariableDecl vLower = MutableTmpVar("lower", syms.intType, lower());
            JCVariableDecl vOldSize = TmpVar("oldSize", syms.intType, MINUS(id(vOldUpper), id(vLower)));

            return
                    PhaseCheckedBlock(upperSym,
                        If (isSequenceActive(),
                            Block(
                                If (IsInvalidatePhase(),
                                    Block(
                                        CallSeqInvalidateUndefined(targetSymbol)
                                    ),
                                /*Else (Trigger phase)*/
                                    Block(
                                        vOldUpper,
                                        vNewUpper,
                                        vSeqSize,
                                        vLower,
                                        If (GT(id(vLower), id(vSeqSize)),
                                            Assign(vLower, id(vSeqSize))
                                        ),
                                        If (GT(id(vOldUpper), id(vSeqSize)),
                                            Assign(vOldUpper, id(vSeqSize))
                                        ),
                                        If (GT(id(vNewUpper), id(vSeqSize)),
                                            Assign(vNewUpper, id(vSeqSize))
                                        ),
                                        If (LT(id(vOldUpper), id(vLower)),
                                            Assign(vOldUpper, id(vLower))
                                        ),
                                        If (LT(id(vNewUpper), id(vLower)),
                                            Assign(vNewUpper, id(vLower))
                                        ),
                                        vOldSize,
                                        If (GT(id(vNewUpper), id(vOldUpper)),
                                            Block(
                                                // Gain elements at the end
                                                CallSeqTrigger(targetSymbol, id(vOldSize), id(vOldSize), MINUS(id(vNewUpper), id(vOldUpper)))
                                            ),
                                        /*else*/ If (LT(id(vNewUpper), id(vOldUpper)),
                                            Block(
                                                // Lose elements in the end
                                                CallSeqTrigger(targetSymbol, MINUS(id(vNewUpper), id(vLower)), id(vOldSize), Int(0))
                                            ),
                                        /*else (no change, send no-change trigger)*/
                                            Block(
                                                CallSeqTriggerUnchanged(targetSymbol)
                                            )
                                        ))
                                   )
                                )
                            )
                        )
                    );
        }

        /**
         * Invalidator for the underlying sequence
         *
         * Adjust uppers (old and new) between lower and the underlying sequence size.
         * If upper is greater, elements added at end:
         *   invalidate(oldSize, oldSize, delta)
         * If upper is lesser, elements removed from end
         *   invalidate(newSize, oldSize, 0)
         */
        private JCStatement makeInvalidateUnderlying() {
            // startPosArg(), endPosArg(), newLengthArg()
            JCVariableDecl vDelta = TmpVar("delta", syms.intType, MINUS(newLengthArg(), MINUS(endPosArg(), startPosArg())));
            JCVariableDecl vSeqSize = TmpVar("seqSize", syms.intType, CallSeqSize());
            JCVariableDecl vLower = MutableTmpVar("lower", syms.intType, lower());
            JCVariableDecl vUpper = MutableTmpVar("upper", syms.intType,
                    upperSym==null?
                        isExclusive?
                            MINUS(id(vSeqSize), Int(1)) :
                            id(vSeqSize) :
                        isExclusive?
                            upper() :
                            PLUS(upper(), Int(1)));
            JCVariableDecl vOldSeqSize = TmpVar("oldSeqSize", syms.intType, MINUS(id(vSeqSize), id(vDelta)));
            JCVariableDecl vOldUpper = MutableTmpVar("adjOldUpper", syms.intType, id(vUpper));
            JCVariableDecl vNewUpper = MutableTmpVar("adjNewUpper", syms.intType, id(vUpper));
            JCVariableDecl vOldLower = MutableTmpVar("adjOldLower", syms.intType, id(vLower));
            JCVariableDecl vNewLower = MutableTmpVar("adjNewLower", syms.intType, id(vLower));
            JCVariableDecl vBegin = MutableTmpVar("begin", syms.intType, id(vLower));
            JCVariableDecl vEnd = MutableTmpVar("end", syms.intType, id(vUpper));
            JCVariableDecl vOldBegin = MutableTmpVar("beginOld", syms.intType, id(vBegin));
            JCVariableDecl vOldEnd = MutableTmpVar("endOld", syms.intType, id(vEnd));
            JCVariableDecl vNewBegin = MutableTmpVar("beginNew", syms.intType, id(vBegin));
            JCVariableDecl vNewEnd = MutableTmpVar("endNew", syms.intType, id(vEnd));

            return
                If (isSequenceActive(),
                    Block(
                        If (IsInvalidatePhase(),
                            Block(
                                CallSeqInvalidateUndefined(targetSymbol)
                            ),
                        /*Else (Trigger phase)*/
                            Block(
                                If (IsUnchangedTrigger(),
                                    Block(
                                        CallSeqTriggerUnchanged(targetSymbol) // Pass it on
                                    ),
                                /*else (real trigger)*/
                                    Block(
                                        vDelta,
                                        vSeqSize,
                                        vLower,
                                        vUpper,
                                        vOldSeqSize,
                                        vOldLower,
                                        vOldUpper,
                                        If(LT(id(vOldSeqSize), id(vOldUpper)),
                                            Assign(vOldUpper, id(vOldSeqSize))
                                        ),
                                        If(LT(id(vOldUpper), id(vOldLower)),
                                            Assign(vOldLower, id(vOldUpper))
                                        ),

                                        // Change is before or in slice
                                        If (LT(startPosArg(), id(vUpper)),
                                            Block(
                                                // If change is entirely before slice
                                                If (LE(endPosArg(), id(vLower)),
                                                    Block(
                                                        If (NE(id(vDelta), Int(0)),
                                                            Block(
                                                                vNewUpper,
                                                                vNewLower,
                                                                If(LT(id(vSeqSize), id(vNewUpper)),
                                                                    Assign(vNewUpper, id(vSeqSize))
                                                                ),
                                                                If(LT(id(vNewUpper), id(vNewLower)),
                                                                    Assign(vNewLower, id(vNewUpper))
                                                                ),
                                                                CallSeqTrigger(targetSymbol,
                                                                    Int(0),
                                                                    MINUS(id(vOldUpper), id(vOldLower)),
                                                                    MINUS(id(vNewUpper), id(vNewLower))
                                                                )
                                                            )
                                                        )
                                                    ),
                                                /*else -- change is within slice */
                                                    Block(
                                                        vBegin,
                                                        vEnd,
                                                        // Starts at the greater of the change start and the slice lower
                                                        If(GT(startPosArg(), id(vBegin)),
                                                            Assign(vBegin, startPosArg())
                                                        ),
                                                        // If the change does not shift elements, limit to end position
                                                        If(AND(EQ(id(vDelta), Int(0)), LT(endPosArg(), id(vEnd))),
                                                            Assign(vEnd, endPosArg())
                                                        ),
                                                        // Now constrain to be within old/new underlying sequence length
                                                        vOldEnd,
                                                        vNewEnd,
                                                        If(LT(id(vOldSeqSize), id(vOldEnd)),
                                                            Assign(vOldEnd, id(vOldSeqSize))
                                                        ),
                                                        If(LT(id(vSeqSize), id(vNewEnd)),
                                                            Assign(vNewEnd, id(vSeqSize))
                                                        ),
                                                        vOldBegin,
                                                        vNewBegin,
                                                        If(LT(id(vOldEnd), id(vOldBegin)),
                                                            Assign(vOldBegin, id(vOldEnd))
                                                        ),
                                                        If(LT(id(vNewEnd), id(vNewBegin)),
                                                            Assign(vNewBegin, id(vNewEnd))
                                                        ),
                                                        // Invalidate
                                                        CallSeqTrigger(targetSymbol,
                                                            MINUS(id(vOldBegin), id(vOldLower)),
                                                            MINUS(id(vOldEnd),   id(vOldLower)),
                                                            MINUS(id(vNewEnd),   id(vNewBegin))
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                   )
                                )
                            )
                        )));
        }

        /**
         * Set invalidators for the synthetic support variables
         */
        void setupInvalidators() {
            addInvalidator(lowerSym, makeInvalidateLower());
            if (upperSym!=null)
                addInvalidator(upperSym, makeInvalidateUpper());
            addInvalidator(seqSym, makeInvalidateUnderlying());
        }
    }

    private class BoundForExpressionTranslator extends BoundSequenceTranslator {
        F3ForExpression forExpr;
        F3ForExpressionInClause clause;

        BoundForExpressionTranslator(F3ForExpression tree) {
            super(tree.pos());
            this.forExpr = tree;
            this.clause = tree.inClauses.head; // KLUDGE - FIXME
        }

        JCStatement makeSizeBody() {
            F3VarSymbol helperSym = clause.boundHelper.sym;
            JCExpression createHelper;
            // Translate
            //   var y = bind for (x in xs) body(x, indexof x)
            // to (roughly, using a hybrid of Java with object-literals):
            //   y$helper = new BoundForHelper() {
            //      F3ForPart makeForPart(int $index$) {
            //        // The following body of makeForPart
            //        // is the body of the for
            //        class anon implements F3ForPart {
            //          var $indexof$x: Integer = $index$;
            //          var x;
            //          def result = bind value
            //      };
            //   }

            Type inductionType = types.boxedTypeOrType(clause.inductionVarSym.type);
            Type bodyType = forExpr.bodyExpr.type;

            // Translate the part created in the F3 AST
            JCExpression makePart = toJava.translateToExpression(forExpr.bodyExpr, bodyType);
            BlockExprJCBlockExpression jcb = (BlockExprJCBlockExpression) makePart;

            // Add access methods
            JCClassDecl tcdecl = (JCClassDecl) jcb.stats.head;
            ClassSymbol csym = tcdecl.sym;

            tcdecl.defs = tcdecl.defs
                    .append(makeSetInductionVarMethod(csym, inductionType))
                    .append(makeGetIndexMethod(csym))
                    .append(makeAdjustIndexMethod(csym));
            jcb.stats = jcb.stats
                    .append(CallStmt(makeType(((F3Block)forExpr.bodyExpr).value.type), defs.count_F3ObjectFieldName))
                    .append(Stmt(m().Assign(Select(Get(helperSym), defs.partResultVarNum_BoundForHelper), Offset(clause.boundResultVarSym)))
            );

            Type helperType = clause.boundHelper.type;
            JCVariableDecl indexParam = Var(syms.intType, names.fromString(defs.dollarIndexNamePrefix()), null);
            Type partType = types.applySimpleGenericType(syms.f3_ForPartInterfaceType, inductionType);
            JCMethodDecl makeDecl = Method(Flags.PUBLIC,
                                        partType,
                                        names.fromString(F3Defs.makeForPart_AttributeMethodPrefix),
                                        List.<JCVariableDecl>of(indexParam),
                                        currentClass().sym,
                                        Return(makePart)
                                    );
            JCClassDecl helperClass = m().AnonymousClassDef(m().Modifiers(0), List.<JCTree>of(makeDecl));
            Symbol seqSym = ((F3Ident)(clause.getSequenceExpression())).sym;
            createHelper = m().NewClass(null, null, // FIXME
                    makeType(helperType),
                    List.<JCExpression>of(
                        getReceiverOrThis(targetSymbol),
                        Offset(targetSymbol),
                        Offset(seqSym),
                        Boolean(clause.getIndexUsed())
                    ),
                    helperClass);
            return
                Block(
                    If(EQnull(Get(helperSym)),
                        Block(
                            setSequenceActive(),
                            Stmt(Set(clause.boundHelper.sym, createHelper))
                        )
                    ),
                    Return(Call(Get(clause.boundHelper.sym), defs.size_SequenceMethodName))
                );
        }

        private JCMethodDecl Method(long flags, Type returnType, Name methodName, List<JCVariableDecl> params, Symbol owner, JCStatement stmt) {
            ListBuffer<Type> paramTypes = ListBuffer.lb();
            for (JCVariableDecl param : params) {
                paramTypes.append(param.getType().type);
            }
            return Method(flags, List.<Type>nil(), returnType, methodName, paramTypes.toList(), params, owner, Stmts(stmt));
        }

        // Make a set induction variable method
        private JCTree makeSetInductionVarMethod(ClassSymbol owner, Type inductionType) {
              return
                    Method(
                        Flags.PUBLIC,
                        syms.voidType,
                        defs.setInductionVar_BoundForPartMethodName,
                        List.of(Param(inductionType, defs.value_ArgName)),
                        owner,
                        SetterStmt(null, clause.inductionVarSym, id(defs.value_ArgName))
                    );
        }

        // Make an adjust index variable method
        private JCTree makeAdjustIndexMethod(ClassSymbol owner) {
              return
                    Method(
                        Flags.PUBLIC,
                        syms.voidType,
                        defs.adjustIndex_BoundForPartMethodName,
                        List.of(Param(syms.intType, defs.value_ArgName)),
                        owner,
                        SetterStmt(clause.indexVarSym,
                            PLUS(
                                Get(null, clause.indexVarSym),
                                id(defs.value_ArgName)))
                    );
        }

        // Make a get index variable method
        private JCTree makeGetIndexMethod(ClassSymbol owner) {
              return
                    Method(
                        Flags.PUBLIC,
                        syms.intType,
                        defs.getIndex_BoundForPartMethodName,
                        List.<JCVariableDecl>nil(),
                        owner,
                        Return(Get(null, clause.indexVarSym))
                    );
        }

        /**
         * Create the elem$ body
         * For primitives, handle null (out-of-range)
         * by returning default value.
         */
        JCStatement makeGetElementBody() {
            JCStatement initAssure =
                    If(EQnull(Get(clause.boundHelper.sym)),
                        Stmt(CallSize(targetSymbol))
                    );
            Type elemType = types.elementType(forExpr.type);
            JCExpression elemFromHelper =
                    Call(Get(clause.boundHelper.sym), defs.get_SequenceMethodName, posArg());
            JCVariableDecl vValue = TmpVar("val", types.boxedTypeOrType(elemType), elemFromHelper);

            if (elemType.isPrimitive() || isValueType(elemType)) {
                return
                    Block(
                        initAssure,
                        vValue,
                        Return(
                            If(EQnull(id(vValue)),
                                DefaultValue(elemType),
                                id(vValue)
                            )
                        )
                    );
            } else {
                return
                    Block(
                        initAssure,
                        Return(elemFromHelper)
                    );
            }
        }

        void setupInvalidators() {
            if (clause.seqExpr instanceof F3Ident) {
                Symbol bindee = ((F3Ident) clause.seqExpr).sym;
                JCStatement inv =
                    If(NEnull(Get(clause.boundHelper.sym)),
                        CallStmt(Get(clause.boundHelper.sym),
                             defs.replaceParts_BoundForMethodName,
                             startPosArg(), endPosArg(), newLengthArg(), phaseArg()));
                addInvalidator((F3VarSymbol) bindee, inv);
            }
        }
    }



    /**
     * Bound if-expression over sequences.
     *
     * Assumptions:
     *   No eager compution and no invalate calls until sequence is active.
     *   Sequence is made active by a call to size.
     *   Once the sequence is active,
     *     the cond field is kept up-to-date (by condition invalidator);
     *     the size field is kept up-to-date (by the condition and arm invalidators
     */
    private class BoundIfSequenceTranslator extends BoundSequenceTranslator {

        private final F3VarSymbol condSym;
        private final F3VarSymbol thenSym;
        private final F3VarSymbol elseSym;
        private final F3VarSymbol sizeSym;

        BoundIfSequenceTranslator(F3IfExpression tree) {
            super(tree.pos());
            this.condSym = tree.boundCondVar.sym;
            this.thenSym = tree.boundThenVar.sym;
            this.elseSym = tree.boundElseVar.sym;
            this.sizeSym = tree.boundSizeVar.sym;
        }

        JCExpression CallGetCond() {
            return Getter(condSym);
        }

        private JCStatement MarkValid(F3VarSymbol sym) {
            if (sym.hasFlags()) {
                return FlagChangeStmt(sym, defs.varFlagSTATE_MASK, defs.varFlagSTATE_VALID);
            } else {
                // Block() skips null statements.
                return null;
            }
        }

        private JCStatement MarkInvalid(F3VarSymbol sym) {
            if (sym.hasFlags()) {
                return FlagChangeStmt(sym, defs.varFlagSTATE_MASK, defs.varFlagINVALID_STATE_BIT);
            } else {
                // Block() skips null statements.
                return null;
            }
        }

        private JCExpression IsInvalid(F3VarSymbol sym) {
            if (sym.hasFlags()) {
                return FlagTest(sym, defs.varFlagINVALID_STATE_BIT, defs.varFlagINVALID_STATE_BIT);
            } else {
                return False();
            }
        }

        private JCExpression IsValid(F3VarSymbol sym) {
            return NOT(IsInvalid(sym));
        }

        /**
         * Body of the sequence size method.
         *
         * If the sequence is dormant
         *   Set it active.
         *   Eagerly calculate the if-condition.
         *   Use that to determine (and set) the size from appropriate branch arm.
         *   Send initial update nodification.
         * Else (already active)
         *   If the size has been marked invalid,
         *     determine (and set) the size
         * In either case, return the (possibly updated) size field
         */
        JCStatement makeSizeBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Block(
                            SetStmt(condSym, CallGetCond()),
                            SetStmt(sizeSym,
                                If (Get(condSym),
                                    CallSize(thenSym),
                                    CallSize(elseSym)
                                )
                            ),
                            MarkValid(condSym),
                            MarkValid(thenSym),
                            MarkValid(elseSym),
                            MarkValid(sizeSym),
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, Get(sizeSym))
                        ),
                    /*Else (already active)*/
                        Block(
                            If (IsInvalid(sizeSym),
                                If (OR(OR(
                                        IsInvalid(condSym),
                                        IsInvalid(thenSym)),
                                        IsInvalid(elseSym)),
                                    // Accessing in between invalidation and triggering, compute, but don't smash size
                                    Block(
                                        Return (
                                            If (CallGetCond(),
                                                CallSize(thenSym),
                                                CallSize(elseSym)
                                            )
                                        )
                                    ),
                                /*else (nothing actually invalid (note that for next time))*/
                                    Block(
                                        MarkValid(sizeSym)
                                    )
                                )
                            )
                        )
                    ),
                    Return(Get(sizeSym))
                );
        }

        /**
         * Body of the sequence get element method.
         *
         * Make sure the sequence is initialized, by calling the size method.
         * Redirect to the arm sequence to get the element.
         */
        JCStatement makeGetElementBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))  // Assure initialized
                    ),
                    If (IsInvalid(sizeSym),
                        If (CallGetCond(),
                            Return(CallGetElement(thenSym, posArg())),
                            Return(CallGetElement(elseSym, posArg()))
                        ),
                        If (Get(condSym),
                            Return(CallGetElement(thenSym, posArg())),
                            Return(CallGetElement(elseSym, posArg()))
                        )
                    )
                );
        }

        /**
         * Body of a invalidate$ method for the synthetic condition boolean.
         */
        private JCStatement makeInvalidateCond() {
            JCVariableDecl oldCondVar = TmpVar("oldCond", syms.booleanType, Get(condSym));
            JCVariableDecl newCondVar = TmpVar("newCond", syms.booleanType, CallGetCond());
            JCVariableDecl oldSizeVar = TmpVar("oldSize", syms.intType, Get(sizeSym));

            return
                If(isSequenceActive(),
                    If(IsInvalidatePhase(),
                        Block(
                            // Mark mid invalidation
                            MarkInvalid(condSym),
                            MarkInvalid(sizeSym),

                            // Whole sequence potentially invalid
                            If (AND(
                                    IsValid(thenSym),
                                    IsValid(elseSym)),
                                CallSeqInvalidateUndefined(targetSymbol)
                            )
                        ),
                    /*Else (Trigger phase)*/
                        Block(
                            If (IsInvalid(condSym),
                                Block(
                                    oldCondVar,
                                    newCondVar,
                                    If (NE(id(newCondVar), id(oldCondVar)),
                                        Block(
                                            oldSizeVar,
                                            SetStmt(sizeSym,
                                                If (id(newCondVar),
                                                    CallSize(thenSym),
                                                    CallSize(elseSym)
                                                )
                                            ),
                                            SetStmt(condSym, id(newCondVar)),
                                            MarkValid(condSym),
                                            MarkValid(thenSym),
                                            MarkValid(elseSym),
                                            MarkValid(sizeSym),
                                            CallSeqTrigger(targetSymbol, Int(0), id(oldSizeVar), Get(sizeSym))
                                        ),
                                    /*else (condition did not actually change, no or empty change trigger)*/
                                        Block(
                                            MarkValid(condSym),
                                            If (AND(
                                                    IsValid(thenSym),
                                                    IsValid(elseSym)),
                                                CallSeqTriggerUnchanged(targetSymbol)
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                );
        }

        /**
         * Body of a invalidate$ method for the synthetic bound sequences
         * that is a branch arm.
         *
         * Do nothing if the sequence is dormant.
         * If this is invalidation phase, send a blanket invalidation of the sequence.
         * If this is trigger phase and we are the active arm, update the sequence size,
         * and just pass the invalidation through.
         */
        private JCStatement makeInvalidateArm(F3VarSymbol armSym, boolean take) {
            return
                If(AND(isSequenceActive(), EQ(Get(condSym), Boolean(take))),
                    If(IsInvalidatePhase(),
                        Block(
                            // Mark mid invalidation
                            MarkInvalid(armSym),
                            MarkInvalid(sizeSym),

                            // Whole sequence potentially invalid
                            If (IsValid(condSym),
                                CallSeqInvalidateUndefined(targetSymbol)
                            )
                        ),
                    /*Else (Trigger phase)*/
                        Block(
                            If (AND(
                                    IsInvalid(armSym),
                                    OR(
                                        IsValid(condSym),
                                        EQ(Get(condSym), CallGetCond())
                                    )),
                                Block(
                                    SetStmt(sizeSym, CallSize(armSym)), // update the size
                                    MarkValid(thenSym),
                                    MarkValid(elseSym),
                                    MarkValid(condSym),
                                    MarkValid(sizeSym),
                                    CallSeqTrigger(targetSymbol, startPosArg(), endPosArg(), newLengthArg())
                                )
                            )
                        )
                    )
                );
        }

        /**
         * Set-up the condition and branch arm invalidators.
         */
        void setupInvalidators() {
            addInvalidator(condSym, makeInvalidateCond());
            addInvalidator(thenSym, makeInvalidateArm(thenSym, true));
            addInvalidator(elseSym, makeInvalidateArm(elseSym, false));
        }
    }

    /***********************************************************************
     *
     * Visitors  (alphabetical order)
     *
     * Override those that need special bind handling
     */

    private boolean isTargettedToSequence() {
        return types.isSequence(targetSymbol.type);
    }

    public void visitBlockExpression(F3Block tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We are translating to a bound sequence
            result = new BoundBlockSequenceTranslator(tree).doit();
        } else {
            result = new BoundBlockExpressionTranslator(tree).doit();
        }
    }

    @Override
    public void visitForExpression(F3ForExpression tree) {
        System.out.println("translate for expr: "+ tree);
        F3ClassDeclaration prevClass = F3TranslateBind.this.currentClass();
        try {
            F3TranslateBind.this.setCurrentClass((F3ClassDeclaration)((F3Block)tree.bodyExpr).stats.head);
            result = new BoundForExpressionTranslator(tree).doit();
        }
        finally {
            F3TranslateBind.this.setCurrentClass(prevClass);
        }
    }

    @Override
    public void visitFunctionInvocation(final F3FunctionInvocation tree) {
        result = new BoundFunctionCallTranslator(tree).doit();
    }

    @Override
    public void visitFunctionValue(final F3FunctionValue tree) {
        result = toJava().translateToExpressionResult(tree, targetSymbol.type);
    }

    public void visitIdent(F3Ident tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We are translating to a bound sequence
            if (tree instanceof F3IdentSequenceProxy) {
                result = new BoundIdentSequenceFromNonTranslator((F3IdentSequenceProxy) tree).doit();
            } else {
                final ExpressionResult exprResult = new BoundIdentTranslator(tree).doit();
                result = new BoundIdentSequenceTranslator(tree, exprResult).doit();
            }
        } else {
            final ExpressionResult exprResult = new BoundIdentTranslator(tree).doit();
            result = exprResult;
        }
    }

    @Override
    public void visitIfExpression(F3IfExpression tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We are translating to a bound sequence
            result = new BoundIfSequenceTranslator(tree).doit();
        } else {
            result = new BoundIfExpressionTranslator(tree).doit();
        }
    }

    @Override
    public void visitInstanciate(F3Instanciate tree) {
        result = new BoundInstanciateTranslator(tree).doit();
    }

    @Override
    public void visitParens(F3Parens tree) {
        result = translateBoundExpression(tree.getExpression(), targetSymbol);
    }

    public void visitSelect(F3Select tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We want to translate to a bound sequence
            result = new BoundSelectSequenceTranslator(tree).doit();
        } else {
            result = new BoundSelectTranslator(tree, targetSymbol).doit();
        }
    }

    @Override
    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We want to translate to a bound sequence
            result = new BoundEmptySequenceTranslator(tree).doit();
        } else {
            super.visitSequenceEmpty(tree);
        }
    }

    @Override
    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        if (tree.boundPendingTriggersSym == null) {
            result = new BoundExplicitSingletonSequenceTranslator(tree).doit();
        } else {
            result = new BoundExplicitSequenceTranslator(tree).doit();
        }
    }

    @Override
    public void visitSequenceRange(F3SequenceRange tree) {
        result = new BoundRangeSequenceTranslator(tree).doit();
    }

    @Override
    public void visitSequenceSlice(F3SequenceSlice tree) {
        result = new BoundSliceSequenceTranslator(tree).doit();
    }

    @Override
    public void visitTypeCast(final F3TypeCast tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We want to translate to a bound sequence
            if (tree.boundArraySizeSym != null) {
                result = new BoundTypeCastArrayToSequenceTranslator(tree).doit();
            } else {
                result = new BoundTypeCastSequenceTranslator(tree).doit();
            }
        } else {
            super.visitTypeCast(tree);
        }
    }

    @Override
    public void visitUnary(F3Unary tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            // We want to translate to a bound sequence
            assert tree.getF3Tag() == F3Tag.REVERSE : "should be reverse operator";
            result = new BoundReverseSequenceTranslator(tree).doit();
        } else {
            super.visitUnary(tree);
        }
    }


    /***********************************************************************
     *
     * Utilities
     *
     */

    protected String getSyntheticPrefix() {
        return "bf3$";
    }

}
