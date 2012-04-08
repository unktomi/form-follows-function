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

import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.*;
import org.f3.tools.comp.F3AbstractTranslation.ExpressionResult;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Name;

/**
 * Translate an inversion of bind expressions.
 * 
 * @author Jim Laskey
 * @author Robert Field
 */
public class F3TranslateInvBind extends F3AbstractTranslation implements F3Visitor {

    protected static final Context.Key<F3TranslateInvBind> f3BoundInvTranslation =
        new Context.Key<F3TranslateInvBind>();


    // Symbol for the var whose bound expression we are translating.
    private F3VarSymbol targetSymbol;

    // The outermost bound expression
    private F3Expression boundExpression;

    public static F3TranslateInvBind instance(Context context) {
        F3TranslateInvBind instance = context.get(f3BoundInvTranslation);
        if (instance == null) {
            F3ToJava toJava = F3ToJava.instance(context);
            instance = new F3TranslateInvBind(context, toJava);
        }
        return instance;
    }

    public F3TranslateInvBind(Context context, F3ToJava toJava) {
        super(context, toJava);

        context.put(f3BoundInvTranslation, this);
    }

    ExpressionResult translate(F3Expression expr, Type type, Symbol symbol) {
        this.targetSymbol = (F3VarSymbol) symbol;
        this.boundExpression = expr;
        
        return translateToExpressionResult(expr, type);
    }

    private class BiBoundSequenceSelectTranslator extends BiBoundSelectTranslator {

        BiBoundSequenceSelectTranslator(F3Select tree) {
            super(tree);
        }

        @Override
        protected BoundSequenceResult doit() {
            addInterClassBindee((F3VarSymbol) selectorSym, refSym);
            return new BoundSequenceResult(
                    List.<JCStatement>nil(),
                    null,
                    bindees(),
                    invalidators(),
                    interClass(),
                    makeGetElementBody(),
                    makeSizeBody(),
                    targetSymbol.type);
        }

        private JCExpression selector() {
            return refSym.isStatic() ?
                Getter(selectorSym) :
                concreteSelector();
        }

        private JCExpression concreteSelector() {
            return translateToCheck(getToCheck());
        }

        // ---- Stolen from BoundSequenceTranslator ----
        //TODO: unify

        private Name activeFlagBit = defs.varFlagSEQUENCE_LIVE;
        F3VarSymbol flagSymbol = (F3VarSymbol)targetSymbol;

        JCExpression isSequenceActive() {
            return FlagTest(flagSymbol, activeFlagBit, activeFlagBit);
        }

        JCExpression isSequenceDormant() {
            return FlagTest(flagSymbol, activeFlagBit, null);
        }

        JCStatement setSequenceActive() {
            return FlagChangeStmt(flagSymbol, null, BITOR(id(defs.varFlagSEQUENCE_LIVE), id(defs.varFlagINIT_INITIALIZED_DEFAULT)));
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
            if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeSizeName(sym));
            else
                return Call(defs.Sequences_size, Getter(rcvr, sym));
        }

        JCExpression CallGetElement(Symbol sym, JCExpression pos) {
            return CallGetElement(getReceiverForCallHack(sym), sym, pos);
        }

        JCExpression CallGetElement(JCExpression rcvr, Symbol sym, JCExpression pos) {
            if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeGetElementName(sym), pos);
            else
                return Call(Getter(rcvr, sym), defs.get_SequenceMethodName, pos);
        }

        /**
         * size$ method
         */
        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CallSize(concreteSelector(), refSym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            SetStmt(targetSymbol,
                                m().NewClass(null, null,
                                    makeType(types.erasure(syms.f3_SequenceProxyType)),
                                    List.<JCExpression>of(
                                        TypeInfo(diagPos, refSym.type),
                                        selector(),
                                        Offset(getReceiver(selectorSym), refSym)),
                                    null)
                            ),
                            setSequenceActive(),
                            CallStmt(defs.F3Base_addDependent,
                                        selector(),
                                        Offset(selector(), refSym),
                                        getReceiverOrThis(selectorSym),
                                        DepNum(getReceiver(selectorSym), selectorSym, refSym)
                            ),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return (id(vSize))
                );
        }

        /**
         * elem$ method
         */
        JCStatement makeGetElementBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return (CallGetElement(concreteSelector(), refSym, posArg()))
                );
        }
    }

    private class BiBoundSequenceIdentTranslator extends BoundIdentTranslator {

        private final F3VarSymbol refSym;

        BiBoundSequenceIdentTranslator(F3Ident tree) {
            super(tree);
            this.refSym = (F3VarSymbol) tree.sym;
        }

        @Override
        protected BoundSequenceResult doit() {
            super.doit();
            return new BoundSequenceResult(
                    List.<JCStatement>nil(),
                    null,
                    bindees(),
                    invalidators(),
                    interClass(),
                    makeGetElementBody(),
                    makeSizeBody(),
                    targetSymbol.type);
        }

        // ---- Stolen from BoundSequenceTranslator ----
        //TODO: unify

        private Name activeFlagBit = defs.varFlagSEQUENCE_LIVE;
        F3VarSymbol flagSymbol = (F3VarSymbol)targetSymbol;

        JCExpression isSequenceActive() {
            return FlagTest(flagSymbol, activeFlagBit, activeFlagBit);
        }

        JCExpression isSequenceDormant() {
            return FlagTest(flagSymbol, activeFlagBit, null);
        }

        JCStatement setSequenceActive() {
            return FlagChangeStmt(flagSymbol, null, BITOR(id(defs.varFlagSEQUENCE_LIVE), id(defs.varFlagINIT_INITIALIZED_DEFAULT)));
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
            if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeSizeName(sym));
            else
                return Call(defs.Sequences_size, Getter(rcvr, sym));
        }

        JCExpression CallGetElement(Symbol sym, JCExpression pos) {
            return CallGetElement(getReceiverForCallHack(sym), sym, pos);
        }

        JCExpression CallGetElement(JCExpression rcvr, Symbol sym, JCExpression pos) {
            if (((F3VarSymbol) sym).useAccessors())
                return Call(rcvr, attributeGetElementName(sym), pos);
            else
                return Call(Getter(rcvr, sym), defs.get_SequenceMethodName, pos);
        }

        /**
         * size$ method
         */
        JCStatement makeSizeBody() {
            JCVariableDecl vSize = TmpVar("size", syms.intType, CallSize(refSym));

            return
                Block(
                    vSize,
                    If (isSequenceDormant(),
                        Block(
                            SetStmt(targetSymbol,
                                m().NewClass(null, null,
                                    makeType(types.erasure(syms.f3_SequenceProxyType)),
                                    List.<JCExpression>of(
                                        TypeInfo(diagPos, refSym.type),
                                        getReceiverOrThis(refSym),
                                        Offset(refSym)),
                                    null)
                            ),
                            setSequenceActive(),
                            CallSeqInvalidateUndefined(targetSymbol),
                            CallSeqTriggerInitial(targetSymbol, id(vSize))
                        )
                    ),
                    Return (id(vSize))
                );
        }

        /**
         * elem$ method
         */
        JCStatement makeGetElementBody() {
            return
                Block(
                    If (isSequenceDormant(),
                        Stmt(CallSize(targetSymbol))
                    ),
                    Return (CallGetElement(refSym, posArg()))
                );
        }
    }

    private class BiBoundSelectTranslator extends BoundSelectTranslator {

        final Symbol selectorSym;

        BiBoundSelectTranslator(F3Select tree) {
            super(tree, targetSymbol);
            F3Expression selectorExpr = tree.getExpression();
            assert selectorExpr instanceof F3Ident : "should be another var in the same instance.";
            F3Ident selector = (F3Ident) selectorExpr;
            selectorSym = selector.sym;
        }

        @Override
        protected ExpressionResult doit() {
            /*
            type tmp0 = inv expression(varNewValue$);
            seltype tmp1 = get$select();
            if (tmp1 != null) tmp1.set$varSym(tmp0);
            varNewValue$
             */
            JCExpression receiver;
            if (!refSym.isStatic() &&
                    selectorSym.kind == Kinds.TYP &&
                    currentClass().sym.isSubClass(selectorSym, types)) {
                receiver = id(names._super);
            } else if (!(selectorSym instanceof VarSymbol)) {
                receiver = id(selectorSym);
            } else {
                JCVariableDecl selector =
                        TmpVar(syms.f3_ObjectType,
                        Getter(selectorSym));
                addSetterPreface(selector);
                receiver = id(selector);
            }

            //note: we have to use the set$(int, F3Base) version because
            //the set$xxx version is not always accessible from the
            //selector expression (if selector is XXX$Script class)
            addSetterPreface(
                    If(NEnull(receiver),
                        Block(
                            CallStmt(receiver, defs.set_F3ObjectMethodName,
                                Offset(receiver, refSym),
                                id(defs.varNewValue_ArgName)
                            )
                        )
                    )
            );

            return super.doit();
        }
    }

    private class BiBoundIdentTranslator extends BoundIdentTranslator {

        BiBoundIdentTranslator(F3Ident tree) {
            super(tree);
        }

        @Override
        protected ExpressionResult doit() {
            /*
            type tmp0 = inv expression(varNewValue$);
            set$varSym(tmp0);
            varNewValue$
             */
            addSetterPreface(SetterStmt(sym, id(defs.varNewValue_ArgName)));

            return super.doit();
        }
    }


    /***********************************************************************
     *
     * Utilities
     *
     */

    protected String getSyntheticPrefix() {
        return "ibf3$";
    }


/* ***************************************************************************
 * Visitor methods -- implemented (alphabetical order)
 ****************************************************************************/

    private boolean isTargettedToSequence() {
        return types.isSequence(targetSymbol.type);
    }
    
    public void visitIdent(F3Ident tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            result = new BiBoundSequenceIdentTranslator(tree).doit();
        } else {
            result = new BiBoundIdentTranslator(tree).doit();
        }
    }

    public void visitSelect(F3Select tree) {
        if (tree == boundExpression && isTargettedToSequence()) {
            result = new BiBoundSequenceSelectTranslator(tree).doit();
        } else {
            result = new BiBoundSelectTranslator(tree).doit();
        }
    }


    /***********************************************************************
     *
     * Moot visitors  (alphabetical order)
     *
     */

    private void disallowedInInverseBind() {
        badVisitor("should not be processed as part of a binding with inverse");
    }

    @Override
    public void visitBinary(F3Binary tree) {
        disallowedInInverseBind();
    }

    public void visitBlockExpression(F3Block tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitForExpression(F3ForExpression tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitForExpressionInClause(F3ForExpressionInClause tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitFunctionValue(F3FunctionValue tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitIfExpression(F3IfExpression tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitIndexof(F3Indexof tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitInstanceOf(F3InstanceOf tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitInstanciate(F3Instanciate tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitInterpolateValue(F3InterpolateValue tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitLiteral(F3Literal tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitParens(F3Parens tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitSequenceRange(F3SequenceRange tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitSequenceSlice(F3SequenceSlice tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitStringExpression(F3StringExpression tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitTimeLiteral(F3TimeLiteral tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitLengthLiteral(F3LengthLiteral tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitAngleLiteral(F3AngleLiteral tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitColorLiteral(F3ColorLiteral tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitTypeCast(F3TypeCast tree) {
        disallowedInInverseBind();
    }

    @Override
    public void visitUnary(F3Unary tree) {
        disallowedInInverseBind();
    }

}
