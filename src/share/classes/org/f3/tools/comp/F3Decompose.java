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

import org.f3.api.F3BindStatus;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.FunctionType;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3TranslationSupport.NotYetImplementedException;
import org.f3.tools.tree.*;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.MethodType;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.jvm.ClassReader;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 * Decompose bind expressions into easily translated expressions
 *
 * @author Robert Field
 */
public class F3Decompose implements F3Visitor {
    protected static final Context.Key<F3Decompose> decomposeKey =
            new Context.Key<F3Decompose>();

    private F3Tree result;
    private F3BindStatus bindStatus = F3BindStatus.UNBOUND;
    private ListBuffer<F3Tree> lbVar;
    private Set<String> synthNames;
    private Symbol varOwner = null;
    private F3VarSymbol currentVarSymbol;
    private Symbol currentClass = null;
    private boolean inScriptLevel = true;
    private F3VarInit varInitContext = null;
    private boolean allowDebinding = false;

    // Map of shreded (Ident) selectors in bound select expressions.
    // Used in shred optimization. We use two maps - one for instance level
    // expressions and one for script level expressions.
    private Map<Symbol, F3Expression> shrededSelectors;
    private Map<Symbol, F3Expression> scriptShrededSelectors;

    protected final F3TreeMaker f3make;
    protected final F3PreTranslationSupport preTrans;
    protected final F3Defs defs;
    protected final Name.Table names;
    protected final F3Resolve rs;
    protected final F3Symtab syms;
    protected final F3Types types;
    protected final ClassReader reader;
    protected final F3OptimizationStatistics optStat;

    public static F3Decompose instance(Context context) {
        F3Decompose instance = context.get(decomposeKey);
        if (instance == null)
            instance = new F3Decompose(context);
        return instance;
    }

    F3Decompose(Context context) {
        context.put(decomposeKey, this);
        f3make = F3TreeMaker.instance(context);
        preTrans = F3PreTranslationSupport.instance(context);
        names = Name.Table.instance(context);
        types = F3Types.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        rs = F3Resolve.instance(context);
        defs = F3Defs.instance(context);
        reader = ClassReader.instance(context);
        optStat = F3OptimizationStatistics.instance(context);
    }

    /**
     * External access: overwrite the top-level tree with the translated tree
     */
    public void decompose(F3Env<F3AttrContext> attrEnv) {
        bindStatus = F3BindStatus.UNBOUND;
        lbVar = null;
        synthNames = new HashSet<String>();
        attrEnv.toplevel = inlineShreddedVarInits(decompose(attrEnv.toplevel));
        synthNames = null;
        lbVar = null;
    }

    void TODO(String msg) {
        throw new NotYetImplementedException("Not yet implemented: " + msg);
    }

    @SuppressWarnings("unchecked")
    private <T extends F3Tree> T decompose(T tree) {
        if (tree == null)
            return null;
        boolean ib = bindStatus != F3BindStatus.UNBOUND;
        if (ib) optStat.recordDecomposeEnter(tree.getClass());
        tree.accept(this);
        if (ib) optStat.recordDecomposeExit();
        result.type = tree.type;
        return (T)result;
    }

    private <T extends F3Tree> List<T> decompose(List<T> trees) {
        if (trees == null)
            return null;
        ListBuffer<T> lb = new ListBuffer<T>();
        for (T tree: trees)
            lb.append(decompose(tree));
        return lb.toList();
    }

    private boolean requiresShred(F3Expression tree) {
        if (tree==null) {
            return false;
        }
        switch (tree.getF3Tag()) {
            case APPLY:
            case OBJECT_LITERAL:
            case SEQUENCE_EXPLICIT:
            case SEQUENCE_RANGE:
            case FOR_EXPRESSION:
                return true;
            case CONDEXPR:
                return types.isSequence(tree.type);
        }
        return false;
    }

    private F3Expression decomposeComponent(F3Expression tree) {
        if (requiresShred(tree))
            return shred(tree);
        else
            return decompose(tree);
    }

    private List<F3Expression> decomposeComponents(List<F3Expression> trees) {
        if (trees == null)
            return null;
        ListBuffer<F3Expression> lb =  ListBuffer.lb();
        for (F3Expression tree: trees)
            lb.append(decomposeComponent(tree));
        return lb.toList();
    }
    
    private <T extends F3Tree> T inlineShreddedVarInits(T tree) {
        new F3TreeScanner() {
            private void unwindVarInit(F3VarInit vi, ListBuffer<F3Expression> elb) {
                for (F3VarInit cvi : vi.getShreddedVarInits()) {
                    unwindVarInit(cvi, elb);
                }
                elb.append(vi);
            }

            private List<F3Expression> process(F3Expression expr) {
                scan(expr);
                if (expr instanceof F3VarInit) {
                    ListBuffer<F3Expression> elb = ListBuffer.lb();
                    unwindVarInit((F3VarInit) expr, elb);
                    return elb.toList();
                } else {
                    return List.of(expr);
                }
            }

            @Override
            public void visitBlockExpression(F3Block tree) {
                ListBuffer<F3Expression> elb = ListBuffer.lb();
                for (F3Expression expr : tree.stats) {
                    elb.appendList(process(expr));
                }
                if (tree.value != null) {
                    List<F3Expression> val = process(tree.value);
                    for (int i = 0; i < (val.length() - 1); ++i) {
                        elb.append(val.get(i));
                    }
                }
                tree.stats = elb.toList();
            }
        }.scan(tree);
        return tree;
    }

    private F3Var makeVar(DiagnosticPosition diagPos, String label, F3Expression initExpr, F3BindStatus bindStatus, Type type) {
        F3Var var = preTrans.SynthVar(diagPos, currentVarSymbol, label, initExpr, bindStatus, type, inScriptLevel, varOwner);
        lbVar.append(var);
        return var;
    }

    private F3Var makeVar(DiagnosticPosition diagPos, Name vName, F3Expression pose, F3BindStatus bindStatus, Type type) {
        optStat.recordSynthVar("synth");
        long flags = F3Flags.SCRIPT_PRIVATE | Flags.SYNTHETIC | (inScriptLevel ? Flags.STATIC | F3Flags.SCRIPT_LEVEL_SYNTH_STATIC : 0L);
        F3Var var = preTrans.Var(diagPos, flags, /*types.normalize(type)*/type, vName, bindStatus, pose, varOwner);
        varOwner.members().enter(var.sym);
        lbVar.append(var);
        return var;
    }

    private F3Var shredVar(String label, F3Expression pose, Type type) {
        return shredVar(label, pose, type, F3BindStatus.UNIDIBIND);
    }
    
    private F3Var shredVar(String label, F3Expression pose, Type type, F3BindStatus bindStatus) {
        optStat.recordShreds();
        Name tmpName = tempName(label);
        // If this shred var initialized with a call to a bound function?
	//System.err.println("shred var: "+ tmpName+": "+type);
        F3Var ptrVar = makeTempBoundResultName(tmpName, pose);
        if (ptrVar != null) {
            return makeVar(pose.pos(), tmpName, id(ptrVar), bindStatus, type);
        } else {
            return makeVar(pose.pos(), label, pose, bindStatus, type);
        }
    }

    private F3Ident id(F3Var v) {
        F3Ident id = f3make.at(v.pos).Ident(v.getName());
        id.sym = v.sym;
        id.type = v.type;
        return id;
    }

    /**
     * If we are in a bound expression, break this expression out into a separate synthetic bound variable.
     */
    private F3Expression shred(F3Expression tree, Type contextType) {
        if (tree == null) {
            return null;
        }
        if (bindStatus.isBound()) {
            F3VarInit prevVarInitContext = varInitContext;
            F3VarInit ourVarInit = null;
            F3BindStatus prevBindStatus = bindStatus;
            if (false & allowDebinding && preTrans.isImmutable(tree)) {
                bindStatus = F3BindStatus.UNBOUND;
                if (prevVarInitContext != null) {
                    ourVarInit = f3make.VarInit(null);
                    varInitContext = ourVarInit;
                }
            }
            F3Expression pose = decompose(tree);
            Type varType = tree.type;
            if (tree.type == syms.botType && contextType != null) {
                // If the tree type is bottom, try to use contextType
                varType = contextType;
            }
            F3Var v = shredVar("", pose, varType, bindStatus);
            if (ourVarInit != null) {
                ourVarInit.resetVar(v);
                prevVarInitContext.addShreddedVarInit(ourVarInit);
            }
            varInitContext = prevVarInitContext;
            F3Expression shred = id(v);
            bindStatus = prevBindStatus;
            return shred;
        } else {
            return decompose(tree);
        }
    }

    private F3Expression shred(F3Expression tree) {
        return shred(tree, null);
    }

    private F3Expression shredUnlessIdent(F3Expression tree) {
        if (tree instanceof F3Ident) {
            return decompose(tree);
        }
        return shred(tree);
    }

    private List<F3Expression> shred(List<F3Expression> trees, List<Type> paramTypes) {
        if (trees == null)
            return null;
        ListBuffer<F3Expression> lb = new ListBuffer<F3Expression>();
        Type paramType = paramTypes != null? paramTypes.head : null;
        for (F3Expression tree: trees) {
            if (false/*disable-VSGC-4079*/ && tree != null && preTrans.isImmutable(tree)) {
                lb.append(tree);
            } else {
                lb.append(shred(tree, paramType));
            }
            if (paramTypes != null) {
                paramTypes = paramTypes.tail;
		if (paramTypes != null) {
		    paramType = paramTypes.head;
		}
            }
        }
        return lb.toList();
    }

    private Name tempName(String label) {
        String name = currentVarSymbol != null ? currentVarSymbol.toString() : "";
        name += "$" + label + "$";
        if (synthNames.contains(name)) {
            for (int i = 0; true; i++) {
                String numbered = name + i;
                if (!synthNames.contains(numbered)) {
                    name = numbered;
                    break;
                }
            }
        }

        // name += defs.internalNameMarker;
        synthNames.add(name);

        return names.fromString(name);
    }

    private Name tempBoundResultName(Name name) {
        return names.fromString(F3Defs.boundFunctionResult + name);
    }

    //TODO: clean-up this whole mess
    private boolean isBoundFunctionResult(F3Expression initExpr) {
        if (initExpr instanceof F3FunctionInvocation) {
            Symbol meth = F3TreeInfo.symbol(((F3FunctionInvocation)initExpr).meth);
            return meth != null && (meth.flags() & F3Flags.BOUND) != 0L;
        } else {
            return false;
        }
    }

    private F3Var makeTempBoundResultName(Name varName, F3Expression initExpr) {
        F3Var ptrVar = null;
        if (isBoundFunctionResult(initExpr)) {
                Name tmpBoundResName = tempBoundResultName(varName);
                /*
                 * Introduce a Pointer synthetic variable which will be used to cache
                 * bound function's return value. The name of the sythetic Pointer
                 * variable is derived from the given varName.
                 */
                ptrVar = makeVar(initExpr.pos(), tmpBoundResName, initExpr, F3BindStatus.UNIDIBIND, syms.f3_PointerType);
                ptrVar.sym.flags_field |= Flags.SYNTHETIC | F3Flags.VARUSE_BIND_ACCESS;
        }
        return ptrVar;
    }

    private <T extends F3Tree> List<T> decomposeContainer(List<T> trees) {
        if (trees == null)
            return null;
        ListBuffer<T> lb = new ListBuffer<T>();
        for (T tree: trees)
            lb.append(decompose(tree));
        return lb.toList();
    }

    public void visitScript(F3Script tree) {
        bindStatus = F3BindStatus.UNBOUND;
        tree.defs = decomposeContainer(tree.defs);
        result = tree;
    }

    public void visitImport(F3Import tree) {
        result = tree;
    }

    public void visitSkip(F3Skip tree) {
        result = tree;
    }

    public void visitWhileLoop(F3WhileLoop tree) {
        F3Expression cond = decompose(tree.cond);
        F3Expression body = decompose(tree.body);
        result = f3make.at(tree.pos).WhileLoop(cond, body);
    }

    public void visitTry(F3Try tree) {
        F3Block body = decompose(tree.body);
        List<F3Catch> catchers = decompose(tree.catchers);
        F3Block finalizer = decompose(tree.finalizer);
        result = f3make.at(tree.pos).Try(body, catchers, finalizer);
    }

    public void visitCatch(F3Catch tree) {
        F3Var param = decompose(tree.param);
        F3Block body = decompose(tree.body);
        result = f3make.at(tree.pos).Catch(param, body);
    }

    public void visitIfExpression(F3IfExpression tree) {
        F3Expression cond = decomposeComponent(tree.cond);
        F3Expression truepart = decomposeComponent(tree.truepart);
        F3Expression falsepart = decomposeComponent(tree.falsepart);
        F3IfExpression res = f3make.at(tree.pos).Conditional(cond, truepart, falsepart);
        if (bindStatus.isBound() && types.isSequence(tree.type)) {
            res.boundCondVar = synthVar(defs.condNamePrefix(), cond, cond.type, false);
            res.boundThenVar = synthVar(defs.thenNamePrefix(), truepart, truepart.type, false);
            res.boundElseVar = synthVar(defs.elseNamePrefix(), falsepart, falsepart.type, false);
            // Add a size field to hold the previous size on condition switch
            F3Var v = makeSizeVar(tree.pos(), F3Defs.UNDEFINED_MARKER_INT);
            res.boundSizeVar = v;
        }
        result = res;
    }

    public void visitBreak(F3Break tree) {
        if (tree.nonLocalBreak) {
            // A non-local break gets turned into an exception
            F3Ident nonLocalExceptionClass = f3make.Ident(names.fromString(F3Defs.cNonLocalBreakException));
            nonLocalExceptionClass.sym = syms.f3_NonLocalBreakExceptionType.tsym;
            nonLocalExceptionClass.type = syms.f3_NonLocalBreakExceptionType;
            F3Instanciate expInst = f3make.InstanciateNew(nonLocalExceptionClass, List.<F3Expression>nil());
            expInst.sym = (ClassSymbol)syms.f3_NonLocalBreakExceptionType.tsym;
            expInst.type = syms.f3_NonLocalBreakExceptionType;
            result = f3make.Throw(expInst).setType(syms.unreachableType);
        } else {
            result = tree;
        }
    }

    public void visitContinue(F3Continue tree) {
        if (tree.nonLocalContinue) {
            // A non-local continue gets turned into an exception
            F3Ident nonLocalExceptionClass = f3make.Ident(names.fromString(F3Defs.cNonLocalContinueException));
            nonLocalExceptionClass.sym = syms.f3_NonLocalContinueExceptionType.tsym;
            nonLocalExceptionClass.type = syms.f3_NonLocalContinueExceptionType;
            F3Instanciate expInst = f3make.InstanciateNew(nonLocalExceptionClass, List.<F3Expression>nil());
            expInst.sym = (ClassSymbol)syms.f3_NonLocalContinueExceptionType.tsym;
            expInst.type = syms.f3_NonLocalContinueExceptionType;
            result = f3make.Throw(expInst).setType(syms.unreachableType);
        } else {
            result = tree;
        }
    }

    public void visitReturn(F3Return tree) {
        tree.expr = decompose(tree.expr);
        if (tree.nonLocalReturn) {
            // A non-local return gets turned into an exception
            F3Ident nonLocalExceptionClass = f3make.Ident(names.fromString(F3Defs.cNonLocalReturnException));
            nonLocalExceptionClass.sym = syms.f3_NonLocalReturnExceptionType.tsym;
            nonLocalExceptionClass.type = syms.f3_NonLocalReturnExceptionType;
            List<F3Expression> valueArg = tree.expr==null? List.<F3Expression>nil() : List.of(tree.expr);
            F3Instanciate expInst = f3make.InstanciateNew(
                    nonLocalExceptionClass,
                    valueArg);
            expInst.sym = (ClassSymbol)syms.f3_NonLocalReturnExceptionType.tsym;
            expInst.type = syms.f3_NonLocalReturnExceptionType;
            result = f3make.Throw(expInst);
        } else {
            result = tree;
        }
    }

    public void visitThrow(F3Throw tree) {
        result = tree;
    }

    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        F3Expression fn = decompose(tree.meth);
        Symbol msym = F3TreeInfo.symbol(tree.meth);
        /*
         * Do *not* shred select expression if it is passed to intrinsic function
         * Pointer.make(Object). Shred only the "selected" portion of it. If
         * we shred the whole select expr, then a temporary shred variable will
         * be used to create Pointer. That temporary is a bound variable and so
         * Pointer.set() on that would throw assign-to-bind-variable exception.
         */
        List<F3Expression> args;
        if (types.isSyntheticPointerFunction(msym)) {
            F3VarRef varRef = (F3VarRef)tree.args.head;
            if (varRef.getReceiver() != null) {
                varRef.setReceiver(shred(varRef.getReceiver()));
            }
            args = tree.args;
        } else {
            List<Type> paramTypes = tree.meth.type.getParameterTypes();
	    //System.err.println("paramTypes="+paramTypes);
            Symbol sym = F3TreeInfo.symbolFor(tree.meth);
            if (sym instanceof MethodSymbol &&
                ((MethodSymbol)sym).isVarArgs()) {
                Type varargType = paramTypes.reverse().head;
                paramTypes = paramTypes.reverse().tail.reverse(); //remove last formal
                while (paramTypes.size() < tree.args.size()) {
                    paramTypes = paramTypes.append(types.elemtype(varargType));
                }
            }
            args = shred(tree.args, paramTypes);
        }
        F3Expression res = f3make.at(tree.pos).Apply(tree.typeargs, fn, args);
        res.type = tree.type;
        if (bindStatus.isBound() && types.isSequence(tree.type) && !isBoundFunctionResult(tree)) {
            F3Var v = shredVar(defs.functionResultNamePrefix(), res, tree.type);
            F3Var sz = makeSizeVar(v.pos(), F3Defs.UNDEFINED_MARKER_INT);
            res = f3make.IdentSequenceProxy(v.name, v.sym, sz.sym);
        }
        result = res;
    }

    public void visitParens(F3Parens tree) {
        F3Expression expr = decomposeComponent(tree.expr);
        result = f3make.at(tree.pos).Parens(expr);
    }

    public void visitAssign(F3Assign tree) {
        F3Expression lhs = decompose(tree.lhs);
        F3Expression rhs = decompose(tree.rhs);
        result = f3make.at(tree.pos).Assign(lhs, rhs);
    }

    public void visitAssignop(F3AssignOp tree) {
        F3Expression lhs = decompose(tree.lhs);
        F3Expression rhs = decompose(tree.rhs);
        F3Tag tag = tree.getF3Tag();
        F3AssignOp res = f3make.at(tree.pos).Assignop(tag, lhs, rhs);
        res.operator = tree.operator;
        result = res;
    }

    public void visitUnary(F3Unary tree) {
        F3Tag tag = tree.getF3Tag();
        F3Expression arg = tag == F3Tag.REVERSE ||
                            tag == F3Tag.SIZEOF ?
            shredUnlessIdent(tree.arg) :
            decomposeComponent(tree.arg);
        F3Unary res = f3make.at(tree.pos).Unary(tag, arg);
        res.operator = tree.operator;
        result = res;
    }

    public void visitBinary(F3Binary tree) {
        F3Tag tag = tree.getF3Tag();
        boolean cutOff = tag==F3Tag.AND || tag==F3Tag.OR;
        F3Expression lhs = decomposeComponent(tree.lhs);
        F3Expression rhs = cutOff?
            shredUnlessIdent(tree.rhs) :  // If cut-off operation, preface code must be evaluated separately
            decomposeComponent(tree.rhs);
        F3Binary res = f3make.at(tree.pos).Binary(tag, lhs, rhs);
	res.methodName = tree.methodName;
	res.infix = tree.infix;
        res.operator = tree.operator;
        result = res;
    }

    public void visitTypeCast(F3TypeCast tree) {
        boolean isBoundSequence = bindStatus.isBound() && types.isSequence(tree.type);
        boolean isCastingArray = types.isArray(tree.expr.type);
        F3Tree clazz = decompose(tree.clazz);
        F3Expression expr = isBoundSequence?
            isCastingArray?
                shred(tree.expr) : // can't smash invalidation logic of user var
                shredUnlessIdent(tree.expr) :
            decomposeComponent(tree.expr);
        F3TypeCast res = f3make.at(tree.pos).TypeCast(clazz, expr);
	//System.err.println("tree="+tree);
	//System.err.println("res="+res);
        if (isBoundSequence && isCastingArray) {
            // Add a size field to hold the previous size of nativearray
            F3Var v = makeSizeVar(tree.pos(), 0);
            res.boundArraySizeSym = v.sym;
        }
        result = res;
    }

    public void visitInstanceOf(F3InstanceOf tree) {
        F3Expression expr = decomposeComponent(tree.expr);
        F3Tree clazz = decompose(tree.clazz);
        result = f3make.at(tree.pos).TypeTest(expr, clazz);
    }

    public void visitSelect(F3Select tree) {
        DiagnosticPosition diagPos = tree.pos();
        Symbol sym = tree.sym;
        Symbol selectSym = F3TreeInfo.symbolFor(tree.selected);
        if (selectSym != null
                && ((selectSym.kind == Kinds.TYP && sym.kind != Kinds.MTH)
                || selectSym.name == names._this)) {
            // Select is just via "this" -- make it a simple Ident
            //TODO: move this to lower
            F3Ident res = f3make.at(diagPos).Ident(sym.name);
            res.sym = sym;
            result = res;
        } else {
            F3Expression selected;
            if ((selectSym != null && (selectSym.kind == Kinds.TYP || selectSym.name == names._super || selectSym.name == names._class))) {
                // Referenced is static, or qualified super access
                // then selected is a class reference
                selected = decompose(tree.selected);
            } else {
                F3BindStatus oldBindStatus = bindStatus;
                if (bindStatus == F3BindStatus.BIDIBIND) bindStatus = F3BindStatus.UNIDIBIND;

                /**
                 * Avoding shreding as an optimization: if the select expression's selected part
                 * is a F3Ident and that identifier is an instance var of current class, then we
                 * don't have to shred it.
                 *
                 * Example:
                 *
                 * class Person {
                 *     var name : String;
                 *     var age: Integer;
                 * }
                 *
                 * class Test {
                 *     var p : Person;
                 *     var name = bind p.name; // instance var "p" in bind-select
                 *     var age = bind p.age;   // same instance var "p" in bind-select
                 * }
                 *
                 * In this case we can avoid shreding and generating two synthetic variables for
                 * bind select expressions p.name, p.age.
                 *
                 * Special cases:
                 * 
                 *     (1) sequences are always shreded
                 *     (2) non-variable access (eg. select expression selects method)
                 *
                 * TODO: for some reason this optimization does not work if the same selected part is
                 * used by a unidirectional and bidirectional bind expressions. For now, filtering out
                 * bidirectional cases. We need to revisit that mystery. Also. I've to oldBindStatus
                 * because bindStatus has been set to UNIDIBIND in the previous statement.
                 */
                if (oldBindStatus == F3BindStatus.UNIDIBIND &&
                    tree.selected instanceof F3Ident &&
                    !types.isSequence(tree.type) &&
                    sym instanceof VarSymbol) {
                    if (selectSym.owner == currentClass && !(selectSym.isStatic() ^ inScriptLevel)) {
                        selected = tree.selected;
                    } else {
                        Map<Symbol, F3Expression> shredMap = inScriptLevel? scriptShrededSelectors : shrededSelectors;
                        if (shredMap.containsKey(selectSym)) {
                            selected = shredMap.get(selectSym);
                        } else {
                            selected = shred(tree.selected);
                            shredMap.put(selectSym, selected);
                        }
                    }
                } else {
		    boolean doit = true;
		    selected = tree.selected;
		    if (selected instanceof F3Ident) {
			if (((F3Ident)selected).getName().toString().length() == 0) {
			    doit = false;
			}
		    }
		    if (doit) {
			selected = shred(selected);
		    }
                }
                bindStatus = oldBindStatus;
            }
            F3Select res = f3make.at(diagPos).Select(selected, sym.name, tree.nullCheck);
            res.sym = sym;
            if (bindStatus.isBound() && types.isSequence(tree.type)) {
                // Add a size field to hold the previous size on selector switch
                F3Var v = makeSizeVar(diagPos, 0);
                res.boundSize = v;
            }
            result = res;
        }
    }

    public void visitIdent(F3Ident tree) {
	F3Ident res = f3make.at(tree.pos).Ident(tree.getName());
	res.type = tree.type;
	res.sym = tree.sym;
	result = res;
    }

    public void visitLiteral(F3Literal tree) {
        result = tree;
    }

    public void visitModifiers(F3Modifiers tree) {
        result = tree;
    }

    public void visitErroneous(F3Erroneous tree) {
        result = tree;
    }

    public void visitClassDeclaration(F3ClassDeclaration tree) {
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;
        Symbol prevVarOwner = varOwner;
        Symbol prevClass = currentClass;
        Map<Symbol, F3Expression> prevShredExprs = shrededSelectors;
        Map<Symbol, F3Expression> prevScriptShredExprs = scriptShrededSelectors;
        shrededSelectors = new HashMap<Symbol, F3Expression>();
        scriptShrededSelectors = new HashMap<Symbol, F3Expression>();
        currentClass = varOwner = tree.sym;
        ListBuffer<F3Tree> prevLbVar = lbVar;
        lbVar = ListBuffer.<F3Tree>lb();
        for (F3Tree mem : tree.getMembers()) {
            lbVar.append(decompose(mem));
        }
        tree.setMembers(lbVar.toList());
        lbVar = prevLbVar;
        varOwner = prevVarOwner;
        currentClass = prevClass;
        shrededSelectors = prevShredExprs;
        scriptShrededSelectors = prevScriptShredExprs;
        result = tree;
        bindStatus = prevBindStatus;
    }

    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        boolean wasInScriptLevel = inScriptLevel;
        // Bound functions are handled by local variable bind facility.
        // The return value is transformed already in F3LocalToClass.
        // So, we are not changing bind state "inBind".
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;
        inScriptLevel = tree.isStatic();
        Symbol prevVarOwner = varOwner;
        varOwner = null;
        F3Modifiers mods = tree.mods;
        Name name = tree.getName();
        F3Type restype = tree.getF3ReturnType();
        List<F3Var> params = decompose(tree.getParams());
        F3Block bodyExpression = decompose(tree.getBodyExpression());
        F3FunctionDefinition res = f3make.at(tree.pos).FunctionDefinition(mods, name, restype, params, bodyExpression);
        res.sym = tree.sym;
        result = res;
        bindStatus = prevBindStatus;
        inScriptLevel = wasInScriptLevel;
        varOwner = prevVarOwner;
    }

    public void visitInitDefinition(F3InitDefinition tree) {
        boolean wasInScriptLevel = inScriptLevel;
        inScriptLevel = tree.sym.isStatic();
        F3Block body = decompose(tree.body);
        F3InitDefinition res = f3make.at(tree.pos).InitDefinition(body);
        res.sym = tree.sym;
        result = res;
        inScriptLevel = wasInScriptLevel;
    }

    public void visitPostInitDefinition(F3PostInitDefinition tree) {
        boolean wasInScriptLevel = inScriptLevel;
        inScriptLevel = tree.sym.isStatic();
        F3Block body = decompose(tree.body);
        F3PostInitDefinition res = f3make.at(tree.pos).PostInitDefinition(body);
        res.sym = tree.sym;
        result = res;
        inScriptLevel = wasInScriptLevel;
    }

    public void visitStringExpression(F3StringExpression tree) {
        List<F3Expression> parts = decomposeComponents(tree.parts);
        result = f3make.at(tree.pos).StringExpression(parts, tree.translationKey);
    }

   public void visitInstanciate(F3Instanciate tree) {
       F3Expression klassExpr = tree.getIdentifier();
       List<F3ObjectLiteralPart> dparts = decompose(tree.getParts());
       F3ClassDeclaration dcdel = decompose(tree.getClassBody());
       List<F3Expression> dargs = decomposeComponents(tree.getArgs());

       F3Instanciate res = f3make.at(tree.pos).Instanciate(tree.getF3Kind(), klassExpr, dcdel, dargs, dparts, tree.getLocalvars());
       res.sym = tree.sym;
       res.constructor = tree.constructor;
       res.varDefinedByThis = tree.varDefinedByThis;

       long anonTestFlags = Flags.SYNTHETIC | Flags.FINAL;
       if (dcdel != null && (dcdel.sym.flags_field & anonTestFlags) == anonTestFlags) {
           ListBuffer<F3VarSymbol> objInitSyms = ListBuffer.lb();
           for (F3ObjectLiteralPart olp : dparts) {
              objInitSyms.append((F3VarSymbol)olp.sym);
           }

           if (objInitSyms.size() > 1) {
              dcdel.setObjInitSyms(objInitSyms.toList());
           }
       }

       result = res;
   }

    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        F3VarSymbol prevVarSymbol = currentVarSymbol;
        currentVarSymbol = (F3VarSymbol)tree.sym;
        if (tree.isExplicitlyBound())
            throw new AssertionError("bound parts should have been converted to overrides");
        F3Expression expr = shred(tree.getExpression(), tree.type);//		  tree.sym.type);
        F3ObjectLiteralPart res = f3make.at(tree.pos).ObjectLiteralPart(tree.name, expr, tree.getExplicitBindStatus());
        res.markBound(bindStatus);
        res.sym = tree.sym;
        currentVarSymbol = prevVarSymbol;
        result = res;
    }

    public void visitTypeAny(F3TypeAny tree) {
        result = tree;
    }

    public void visitTypeClass(F3TypeClass tree) {
        result = tree;
    }

    public void visitTypeVar(F3TypeVar tree) {
        result = tree;
    }

    public void visitTypeFunctional(F3TypeFunctional tree) {
        result = tree;
    }

    public void visitTypeArray(F3TypeArray tree) {
        result = tree;
    }

    public void visitTypeUnknown(F3TypeUnknown tree) {
        result = tree;
    }

    public void visitVarInit(F3VarInit tree) {
        // Handled in visitVar
        result = tree;
    }

    public void visitVarRef(F3VarRef tree) {
        result = tree;
    }

    public void visitVar(F3Var tree) {
        boolean wasInScriptLevel = inScriptLevel;
        inScriptLevel = tree.isStatic();
        F3VarSymbol prevVarSymbol = currentVarSymbol;
        currentVarSymbol = tree.sym;

        F3BindStatus prevBindStatus = bindStatus;
        // for on-replace, decompose as unbound
        bindStatus = F3BindStatus.UNBOUND;
        F3OnReplace onReplace = decompose(tree.getOnReplace());
        F3OnReplace onInvalidate = decompose(tree.getOnInvalidate());
        // bound if was bind context or is bound variable
        bindStatus = tree.isBound()?
                            tree.getBindStatus() :
                            prevBindStatus;

        F3VarInit vsi = tree.getVarInit();
        F3VarInit prevVarInitContext = varInitContext;
        boolean prevAllowDebinding = allowDebinding;
        varInitContext = vsi;
        allowDebinding = !tree.sym.hasForwardReference(); // No debinding for forward referenced var

        F3Expression initExpr = decompose(tree.getInitializer());
        // Is this a bound var and initialized with a Pointer result
        // from a bound function call? If so, we need to create Pointer
        // synthetic var here.
        F3Var ptrVar = bindStatus.isBound()? makeTempBoundResultName(tree.name, initExpr) : null;

        F3Var res = f3make.at(tree.pos).Var(
                    tree.name,
                    tree.getF3Type(),
                    tree.getModifiers(),
                    (ptrVar != null)? id(ptrVar) : initExpr,
                    tree.getBindStatus(),
                    onReplace,
                    onInvalidate);
        res.sym = tree.sym;
        res.type = tree.type;
        if (vsi != null) {
            // update the var in the var-init
            vsi.resetVar(res);
        }

        allowDebinding = prevAllowDebinding;
        varInitContext = prevVarInitContext;
        bindStatus = prevBindStatus;
        inScriptLevel = wasInScriptLevel;
        currentVarSymbol = prevVarSymbol;
        result = res;
    }

    public void visitOnReplace(F3OnReplace tree) {
        F3Var oldValue = tree.getOldValue();
        F3Var firstIndex = tree.getFirstIndex();
        F3Var lastIndex = tree.getLastIndex();
        F3Var newElements = tree.getNewElements();
        F3Var saveVar = null;
        if (oldValue != null && types.isSequence(oldValue.type)) {
            F3VarSymbol sym = oldValue.sym;
            // FIXME OPTIMIZATION:
            // if sym.isUsedInSizeof() && ! sym.isUsedOutsideSizeof())
            // then we can save just the old size in the save-var.
            // We also have to translate 'sizeof oldVar' to the saved size.
            if (sym.isUsedInSizeof() || sym.isUsedOutsideSizeof())
                saveVar = makeSaveVar(tree.pos(), oldValue.type);
        }
        F3Block body = decompose(tree.getBody());
        result = f3make.at(tree.pos).OnReplace(oldValue, firstIndex, lastIndex, tree.getEndKind(), newElements, saveVar, body);
    }

    /**
     * Block-expressions
     *
     * For bound sequence block-expressions, get the initialization right by
     *   The block vars have already been made into VarInits.
     *   Making block value into a synthetic var, and add a VarInit for it
     *   to the block vars
     */
    public void visitBlockExpression(F3Block tree) {
        List<F3Expression> stats;
        F3Expression value;
        if (bindStatus.isBound() && types.isSequence(tree.type)) {
            for (F3Expression stat : tree.stats) {
                if (!(stat instanceof F3VarInit)) {
                    throw new AssertionError("the statements in a bound block should already be just VarInit");
                }
            }
            F3Var v = shredVar(defs.valueNamePrefix(), decompose(tree.value), tree.type);
            F3VarInit vi = f3make.at(tree.value.pos()).VarInit(v);
            vi.type = tree.type;
            stats = tree.stats.append(vi);
            F3Ident val = id(v);
            val.sym = v.sym;
            val.type = tree.type;
            value = val;
        } else {
            stats = decomposeContainer(tree.stats);
            value = decompose(tree.value);
        }
        F3Block res = f3make.at(tree.pos()).Block(tree.flags, stats, value);
        res.endpos = tree.endpos;
        result = res;
    }

    public void visitFunctionValue(F3FunctionValue tree) {
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;
        Symbol prevVarOwner = varOwner;
        varOwner = null;
        boolean wasInScriptLevel = inScriptLevel;
        inScriptLevel = false;
        if (false) {
            tree.bodyExpression = decompose(tree.bodyExpression);
            result = tree;
        } else {
            F3Modifiers mods = tree.mods;
            F3Type restype = tree.getF3ReturnType();
            List<F3Var> params = decompose(tree.getParams());
            F3Block bodyExpression = decompose(tree.getBodyExpression());
            F3FunctionValue res = f3make.at(tree.pos).FunctionValue(mods, restype, params, bodyExpression);
            res.definition = tree.definition;
	    res.typeArgs = tree.typeArgs;
	    res.type = tree.type;
            result = res;
        }
        bindStatus = prevBindStatus;
        inScriptLevel = wasInScriptLevel;
        varOwner = prevVarOwner;
    }

    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        result = tree;
    }

    private F3Var synthVar(String label, F3Expression tree, Type type) {
        return synthVar(label, tree, type, true);
    }

    private F3Var synthVar(String label, F3Expression tree, Type type, boolean decompose) {
        if (tree == null) {
            return null;
        }
        F3Expression expr = decompose ? decompose(tree) : tree;

        f3make.at(tree.pos()); // set position

        if (!types.isSameType(tree.type, type)) {
            // cast to desired type
            F3Ident tp = (F3Ident) f3make.Type(type);
            tp.sym = type.tsym;
            expr = f3make.TypeCast(tp, expr);
        }

        F3Var v = shredVar(label, expr, type);
        v.sym.flags_field |= F3Flags.VARMARK_BARE_SYNTH;
        return v;
    }

    private F3Var makeSizeVar(DiagnosticPosition diagPos, int initial) {
        return makeIntVar(diagPos, defs.sizeNamePrefix(), initial);
    }

    private F3Var makeIntVar(DiagnosticPosition diagPos, String label, int initial) {
        F3Expression initialSize = f3make.at(diagPos).Literal(initial);
        initialSize.type = syms.intType;
        F3Var v = makeVar(diagPos, label, initialSize, F3BindStatus.UNBOUND, syms.intType);
        return v;
    }

    private F3Var makeSaveVar(DiagnosticPosition diagPos, Type type) {
        F3Var v = makeVar(diagPos, defs.saveNamePrefix(), null, F3BindStatus.UNBOUND, type);
        v.sym.flags_field |= F3Flags.VARMARK_BARE_SYNTH;
        return v;
    }

    /**
     * Add synthetic variables, and attach them to the reconstituted range.
     *    def range = bind [rb .. re step st]
     * adds:
     *
     * def lower = bind rb; // marked BARE_SYNTH
     * def upper = bind re; // marked BARE_SYNTH
     * def step = bind st; // marked BARE_SYNTH
     * def size = bind -99;
     */
    public void visitSequenceRange(F3SequenceRange tree) {
        F3Expression lower;
        F3Expression upper;
        F3Expression stepOrNull;
        if (bindStatus.isBound()) {
            Type elemType = types.elementType(tree.type);
            lower = synthVar(defs.lowerNamePrefix(), tree.getLower(), elemType);
            upper = synthVar(defs.upperNamePrefix(), tree.getUpper(), elemType);
            stepOrNull = synthVar(defs.stepNamePrefix(), tree.getStepOrNull(), elemType);
        } else {
            lower = decomposeComponent(tree.getLower());
            upper = decomposeComponent(tree.getUpper());
            stepOrNull = decomposeComponent(tree.getStepOrNull());
        }
        F3SequenceRange res = f3make.at(tree.pos).RangeSequence(lower, upper, stepOrNull, tree.isExclusive());
        res.type = tree.type;
        if (bindStatus.isBound()) {
            // now add a size var
            res.boundSizeVar = makeSizeVar(tree.pos(), F3Defs.UNDEFINED_MARKER_INT);
        }
        result = res;
    }

    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        DiagnosticPosition diagPos = tree.pos();
        F3SequenceExplicit res;
        if (bindStatus.isBound()) {
            // bound should not use items - non-null for pretty-printing
            res = f3make.at(diagPos).ExplicitSequence(List.<F3Expression>nil());
            boolean hasNullable = false;
            int n = 0;
            ListBuffer<F3VarSymbol> vb = ListBuffer.lb();
            ListBuffer<F3VarSymbol> vblen = ListBuffer.lb();
            for (F3Expression item : tree.getItems()) {
                vb.append(synthVar(defs.itemNamePrefix()+n, item, item.type).sym);
                F3VarSymbol lenSym = null;
                if (preTrans.isNullable(item)) {
                    lenSym = makeIntVar(item.pos(), defs.lengthNamePrefix()+n, 0).sym;
                    hasNullable = true;
                }
                vblen.append(lenSym);
                ++n;
            }
            res.boundItemsSyms = vb.toList();
            res.boundItemLengthSyms = vblen.toList();

            // now add synth vars
            if (tree.getItems().length() > 1  || types.isArrayOrSequenceType(res.boundItemsSyms.get(0).type)) {
                res.boundLowestInvalidPartSym = makeIntVar(diagPos, defs.lowNamePrefix(), F3Defs.UNDEFINED_MARKER_INT).sym;
                res.boundHighestInvalidPartSym = makeIntVar(diagPos, defs.highNamePrefix(), F3Defs.UNDEFINED_MARKER_INT).sym;
                res.boundPendingTriggersSym = makeIntVar(diagPos, defs.pendingNamePrefix(), 0).sym;
                if (hasNullable) {
                    res.boundSizeSym = makeSizeVar(diagPos, F3Defs.UNDEFINED_MARKER_INT).sym;
                    res.boundDeltaSym = makeIntVar(diagPos, defs.deltaNamePrefix(), 0).sym;
                    res.boundChangeStartPosSym = makeIntVar(diagPos, defs.cngStartNamePrefix(), 0).sym;
                    res.boundChangeEndPosSym = makeIntVar(diagPos, defs.cngEndNamePrefix(), 0).sym;
                }
            }
            F3Expression falseLit = f3make.Literal(TypeTags.BOOLEAN, 0);
            falseLit.type = syms.booleanType;
            res.boundIgnoreInvalidationsSym = makeVar(diagPos, defs.ignoreNamePrefix(), falseLit, F3BindStatus.UNBOUND, syms.booleanType).sym;
        } else {
            List<F3Expression> items = decomposeComponents(tree.getItems());
            res = f3make.at(diagPos).ExplicitSequence(items);
        }
        res.type = tree.type;
        result = res;
    }

    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        F3Expression sequence = null;
        if (bindStatus.isBound()) {
            sequence = shredUnlessIdent(tree.getSequence());
        } else {
            sequence = decomposeComponent(tree.getSequence());
        }
        F3Expression index = decomposeComponent(tree.getIndex());
        result = f3make.at(tree.pos).SequenceIndexed(sequence, index);
    }

    public void visitSequenceSlice(F3SequenceSlice tree) {
        F3Expression sequence = shred(tree.getSequence());
        F3Expression firstIndex = shred(tree.getFirstIndex());
        F3Expression lastIndex = shred(tree.getLastIndex());
        result = f3make.at(tree.pos).SequenceSlice(sequence, firstIndex, lastIndex, tree.getEndKind());
    }

    public void visitSequenceInsert(F3SequenceInsert tree) {
        F3Expression sequence = decompose(tree.getSequence());
        F3Expression element = decompose(tree.getElement());
        F3Expression position = decompose(tree.getPosition());
        result = f3make.at(tree.pos).SequenceInsert(sequence, element, position, tree.shouldInsertAfter());
    }

    public void visitSequenceDelete(F3SequenceDelete tree) {
        F3Expression sequence = decompose(tree.getSequence());
        F3Expression element = decompose(tree.getElement());
        result = f3make.at(tree.pos).SequenceDelete(sequence, element);
    }

    public void visitInvalidate(F3Invalidate tree) {
        F3Expression variable = decompose(tree.getVariable());
        result = f3make.at(tree.pos).Invalidate(variable);
    }

    public void visitForExpression(F3ForExpression tree) {
        if (bindStatus.isBound()) {
            F3Expression map = tree.getMap();
            if (map != null) {
                result = decompose(map);
            } else {
                F3ForExpressionInClause clause = tree.inClauses.head;
                clause.seqExpr = shred(clause.seqExpr, null);
                // clause.whereExpr = decompose(clause.whereExpr);
                
                // Create the BoundForHelper variable:
                Type inductionType = types.boxedTypeOrType(clause.inductionVarSym.type);
                F3Block body = (F3Block) tree.getBodyExpression();
                Type helperType = types.applySimpleGenericType(
                                                               types.isSequence(body.type)?
                                                               syms.f3_BoundForOverSequenceType :
                                                               (preTrans.isNullable(body) || clause.hasWhereExpression())?
                                                               syms.f3_BoundForOverNullableSingletonType :
                                                               syms.f3_BoundForOverSingletonType,
                                                               types.boxedElementType(tree.type),
                                                               inductionType);
                F3Expression init = f3make.Literal(TypeTags.BOT, null);
                init.type = helperType;
                Name helperName = preTrans.makeUniqueVarNameIn(names.fromString(defs.helperDollarNamePrefix()+currentVarSymbol.name), varOwner);
                F3Var helper = makeVar(tree, helperName, init, F3BindStatus.UNBOUND, helperType);

                clause.boundHelper = helper;
                
                // Fix up the class
                F3ClassDeclaration cdecl = (F3ClassDeclaration) decompose(body.stats.head);
                body.stats.head = cdecl;
                
                // Patch the type of the doit function
                patchDoitFunction(cdecl);

                // Patch the type of the anon{}.doit() call
                body.value.type = cdecl.type;  //TODO: probably need to go deeper
                
                // Add F3ForPart as implemented interface -- F3ForPart<T>
                Type intfc = types.applySimpleGenericType(types.erasure(syms.f3_ForPartInterfaceType), inductionType);
                cdecl.setDifferentiatedExtendingImplementingMixing(
                                                                   List.<F3Expression>nil(),
                                                                   List.<F3Expression>of(f3make.Type(intfc)),  // implement interface
                                                                   List.<F3Expression>nil());
                
                result = f3make.at(tree.pos).ForExpression(List.of(clause), body);
            }
        } else {
            F3Expression map = tree.getMap();
            if (map != null) {
                result = decompose(map);
	    } else {
		List<F3ForExpressionInClause> inClauses = decompose(tree.inClauses);
		F3Expression bodyExpr = decompose(tree.bodyExpr);
		result = f3make.at(tree.pos).ForExpression(inClauses, bodyExpr);
	    }
        }
    }

    private void patchDoitFunction(F3ClassDeclaration cdecl) {
        Type ctype = cdecl.type;
        for (F3Tree mem : cdecl.getMembers()) {
            if (mem.getF3Tag() == F3Tag.FUNCTION_DEF) {
                F3FunctionDefinition func = (F3FunctionDefinition) mem;
                if ((func.sym.flags() & F3Flags.FUNC_SYNTH_LOCAL_DOIT) != 0L) {
                    // Change the value to be "this"
                    F3Block body = func.getBodyExpression();
                    body.value = f3make.This(ctype);
                    body.type = ctype;

                    // Adjust function to return class type
                    final MethodType funcType = new MethodType(
                            List.<Type>nil(), // arg types
                            ctype, // return type
                            List.<Type>nil(), // Throws type
                            syms.methodClass);   // TypeSymbol
                    func.sym.type = funcType;
                    func.type = funcType;
               }
            }
        }

    }

    public void visitForExpressionInClause(F3ForExpressionInClause tree) {
        tree.seqExpr = decompose(tree.seqExpr);
        tree.setWhereExpr(decompose(tree.getWhereExpression()));
        result = tree;
    }

    public void visitIndexof(F3Indexof tree) {
        result = tree.clause.indexVarSym == null ? tree : f3make.Ident(tree.clause.indexVarSym);
    }

    public void visitTimeLiteral(F3TimeLiteral tree) {
        result = tree;
    }

    public void visitLengthLiteral(F3LengthLiteral tree) {
        result = tree;
    }

    public void visitAngleLiteral(F3AngleLiteral tree) {
        result = tree;
    }

    public void visitColorLiteral(F3ColorLiteral tree) {
        result = tree;
    }

    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        boolean wasInScriptLevel = inScriptLevel;
        inScriptLevel = tree.isStatic();
        F3BindStatus prevBindStatus = bindStatus;
        F3VarSymbol prevVarSymbol = currentVarSymbol;
        currentVarSymbol = tree.sym;
        // on-replace is always unbound
        bindStatus = F3BindStatus.UNBOUND;
        F3OnReplace onReplace = decompose(tree.getOnReplace());
        F3OnReplace onInvalidate = decompose(tree.getOnInvalidate());
        // bound if was bind context or is bound variable
        bindStatus = tree.isBound()?
                            tree.getBindStatus() :
                            prevBindStatus;
        F3Expression initializer = shredUnlessIdent(tree.getInitializer());
        F3OverrideClassVar res = f3make.at(tree.pos).OverrideClassVar(tree.getName(),
                tree.getF3Type(),
                tree.getModifiers(),
                tree.getId(),
                initializer,
                tree.getBindStatus(),
                onReplace,
                onInvalidate);
        res.sym = tree.sym;
        bindStatus = prevBindStatus;
        currentVarSymbol = prevVarSymbol;
        inScriptLevel = wasInScriptLevel;
        result = res;
    }

    public void visitInterpolateValue(F3InterpolateValue tree) {
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;
        F3Expression attr = decompose(tree.attribute);
        F3Expression funcValue = decompose(tree.funcValue);
        F3Expression interpolation = decompose(tree.interpolation);
        // Note: funcValue takes the place of value
        F3InterpolateValue res = f3make.at(tree.pos).InterpolateValue(attr, funcValue, interpolation);
        res.sym = tree.sym;
        result = res;
        bindStatus = prevBindStatus;
    }

    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        F3Expression start = decompose(tree.start);
        List<F3Expression> values = decomposeComponents(tree.values);
        F3Expression trigger = decompose(tree.trigger);
        result = f3make.at(tree.pos).KeyFrameLiteral(start, values, trigger);
    }
}
