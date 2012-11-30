/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.tools.tree.*;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.F3Expression;

import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.TypeTags.*;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Symbol.OperatorSymbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.*;

import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;



import java.util.Map;
import java.util.HashMap;

/**
 * Normalize tree before translation. This includes adding explicit type conversions
 * (e.g. for autoboxing, sequence conversions, etc.) and rewriting unary/assignop operations
 * as binary operations.
 * 
 * @author Maurizio Cimadamore
 */
public class F3Lower implements F3Visitor {
    protected static final Context.Key<F3Lower> convertTypesKey =
            new Context.Key<F3Lower>();

    private F3PreTranslationSupport preTrans;
    private F3Types types;
    private F3Resolve rs;
    private F3Symtab syms;
    private F3TreeMaker m;
    private F3Defs defs;
    private Type pt;
    private LowerMode mode;
    private Map<F3ForExpressionInClause, F3ForExpressionInClause> forClauseMap; //TODO this should be refactord into a common translation support
    private F3Env<F3AttrContext> env;
    private F3Tree enclFunc;
    private F3Tree result;
    private Name.Table names;
    private Symbol currentClass;
    private int varCount;

    enum LowerMode {
        EXPRESSION,
        STATEMENT,
        DECLARATION;
    }

    public static F3Lower instance(Context context) {
        F3Lower instance = context.get(convertTypesKey);
        if (instance == null)
            instance = new F3Lower(context);
        return instance;
    }

    F3Lower(Context context) {
        context.put(convertTypesKey, this);
        preTrans = F3PreTranslationSupport.instance(context);
        types = F3Types.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        m = F3TreeMaker.instance(context);
        rs = F3Resolve.instance(context);
        names = Name.Table.instance(context);
        defs = F3Defs.instance(context);
        forClauseMap = new HashMap<F3ForExpressionInClause, F3ForExpressionInClause>();
    }

    public F3Tree lower(F3Env<F3AttrContext> attrEnv) {
        this.env = attrEnv;
        attrEnv.toplevel = lowerDecl(attrEnv.toplevel);
        //System.out.println(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    <T extends F3Tree> T lower(T tree, Type pt, LowerMode mode) {
	if (pt == null) {
	    throw new NullPointerException("pt is null: "+ tree);
	}
        Type prevPt = this.pt;
        LowerMode prevMode = this.mode;
        try {
            this.pt = pt;
            this.mode = mode;
            if (tree != null) {
                tree.accept(this);
                return (T)(mode == LowerMode.EXPRESSION ?
                    convertTree((F3Expression)result, this.pt) :
                    result);
            }
            else
                return null;
        }
        finally {
            this.pt = prevPt;
            this.mode = prevMode;
        }
    }

    
    public <T extends F3Tree> T lowerExpr(T tree) {
        return lower(tree, Type.noType, LowerMode.EXPRESSION);
    }

    public <T extends F3Tree> T lowerExpr(T tree, Type pt) {
        return lower(tree, pt, LowerMode.EXPRESSION);
    }

    public <T extends F3Tree> T lowerStmt(T tree) {
        return lower(tree, Type.noType, LowerMode.STATEMENT);
    }

    public <T extends F3Tree> T lowerDecl(T tree) {
        return lower(tree, Type.noType, LowerMode.DECLARATION);
    }


    <T extends F3Tree> List<T> lower(List<T> trees, LowerMode mode) {
        ListBuffer<T> buf = ListBuffer.lb();
        for (T tree : trees) {
            buf.append(lower(tree, Type.noType, mode));
        }
        return buf.toList();
    }

    public <T extends F3Expression> List<T> lowerExprs(List<? extends T> trees, List<Type> pts) {
        ListBuffer<T> buf = ListBuffer.lb();
        for (T tree : trees) {
            buf.append(lowerExpr(tree, pts.head));
            pts = pts.tail;
        }
        return buf.toList();
    }

    public <T extends F3Tree> List<T> lowerExprs(List<T> trees) {
        return lower(trees, LowerMode.EXPRESSION);
    }
    
    public <T extends F3Tree> List<T> lowerDecls(List<T> trees) {
        return lower(trees, LowerMode.DECLARATION);
    }

    public <T extends F3Tree> List<T> lowerStats(List<T> trees) {
        return lower(trees, LowerMode.STATEMENT);
    }

    F3Expression convertTree(F3Expression tree, Type type) {
        if (type == Type.noType) return tree;
	if (type.tag == TypeTags.TYPEVAR) {
	    return tree;
	}
        return tree = needSequenceConversion(tree, type) ?
            toSequence(tree, type) :
            preTrans.makeCastIfNeeded(tree, type);
    }

    private boolean needSequenceConversion(F3Expression tree, Type type) {
        return (types.isSequence(type) &&
            ((!types.isSequence(tree.type) &&
            tree.type != syms.unreachableType &&
            !types.isArray(tree.type)) ||
            isNull(tree)));
    }

    private boolean isNull(F3Tree tree) {
        return (tree.getF3Tag() == F3Tag.LITERAL &&
                ((F3Literal)tree).value == null);
    }

    private F3Expression toSequence(F3Expression tree, Type type) {
        F3Expression seqExpr = null;
        if (isNull(tree)) {
            seqExpr = m.at(tree.pos).EmptySequence().setType(type);
        }
        else if (types.isSameType(tree.type, syms.objectType) &&
                types.isSubtypeUnchecked(syms.f3_SequenceTypeErasure, type)) { //synthetic call
            MethodSymbol msym = (MethodSymbol)rs.findIdentInType(env, syms.f3_SequencesType, defs.Sequences_convertObjectToSequence.methodName, Kinds.MTH);
            F3Expression sequencesType = m.at(tree.pos).Type(syms.f3_SequencesType).setType(syms.f3_SequencesType);
            F3TreeInfo.setSymbol(sequencesType, syms.f3_SequencesType.tsym);
            F3Ident convertMeth = m.at(tree.pos).Ident(defs.Sequences_convertObjectToSequence.methodName);
            convertMeth.sym = msym;
            convertMeth.type = msym.type;
            seqExpr = m.at(tree.pos).Apply(List.<F3Expression>nil(), convertMeth, List.of(tree)).setType(msym.type.getReturnType());

        }
        else {
            seqExpr = m.at(tree.pos).ExplicitSequence(List.of(preTrans.makeCastIfNeeded(tree, types.elementType(type))));
            seqExpr.type = type;
        }
        return seqExpr;
    }

    private Name tempName(String label) {
        return names.fromString("$" + label + "$" + varCount++);
    }

    private F3Var makeVar(DiagnosticPosition diagPos, String name, F3Expression init, Type type) {
        return makeVar(diagPos, 0L, name, F3BindStatus.UNBOUND, init, type);
    }
    
    private F3Var makeVar(DiagnosticPosition diagPos, long flags, String name, F3BindStatus bindStatus, F3Expression init, Type type) {
        F3VarSymbol vsym = new F3VarSymbol(types, names, flags, tempName(name), types.normalize(type), preTrans.makeDummyMethodSymbol(currentClass));
        return makeVar(diagPos, vsym, bindStatus, init);
    }

    private F3Var makeVar(DiagnosticPosition diagPos, F3VarSymbol vSym, F3BindStatus bindStatus, F3Expression init) {
        F3Modifiers mod = m.at(diagPos).Modifiers(vSym.flags());
        F3Type f3Type = preTrans.makeTypeTree(vSym.type);
        F3Var v = m.at(diagPos).Var(vSym.name, f3Type, mod, init, bindStatus, null, null);
        v.sym = vSym;
        v.type = vSym.type;
        return v;
    }

    public void visitAssign(F3Assign tree) {
        if (tree.lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED &&
                types.isSequence(((F3SequenceIndexed)tree.lhs).getSequence().type) &&
                (types.isSequence(tree.rhs.type) || types.isSameType(tree.rhs.type, syms.objectType))) {
            result = lowerSequenceIndexedAssign(tree.pos(), (F3SequenceIndexed)tree.lhs, tree.rhs);
        }
        else {
            F3Expression lhs = lowerExpr(tree.lhs);
            F3Expression rhs = lowerExpr(tree.rhs, tree.lhs.type);
            result = m.at(tree.pos).Assign(lhs, rhs).setType(tree.type);
        }
    }

    F3Expression lowerSequenceIndexedAssign(DiagnosticPosition pos, F3SequenceIndexed indexed, F3Expression val) {
        Type resType = indexed.getSequence().type;
        F3Var indexVar = makeVar(pos, defs.posNamePrefix(), indexed.getIndex(), indexed.getIndex().type);
        F3Ident indexRef = m.at(pos).Ident(indexVar);
        F3Expression lhs = m.SequenceSlice(indexed.getSequence(), indexRef, indexRef, F3SequenceSlice.END_INCLUSIVE);
        lhs.setType(resType);
        F3Expression assign = m.Assign(lhs, val).setType(resType);
        return lowerExpr(m.Block(0L, List.<F3Expression>of(indexVar), assign).setType(resType));
    }

    public void visitAssignop(F3AssignOp tree) {
        result = visitNumericAssignop(tree, 
                                      !(tree.operator instanceof OperatorSymbol));
    }
    //where
    private F3Expression visitNumericAssignop(F3AssignOp tree, boolean isSpecialLiteralOperation) {

        F3Tag opTag = tree.getNormalOperatorF3Tag();
        ListBuffer<F3Expression> stats = ListBuffer.lb();

        //if the assignop operand is an indexed expression of the kind a.x[i]
        //then we need to cache the index value (not to recompute it twice).

        F3Expression lhs = tree.lhs;
        F3Ident index = null;
        
        if (tree.lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED) {
            F3SequenceIndexed indexed = (F3SequenceIndexed)tree.lhs;
            F3Var varDef = makeVar(tree.pos(), defs.indexNamePrefix(), indexed.getIndex(), indexed.getIndex().type);
            index = m.at(tree.pos).Ident(varDef.sym);
            index.sym = varDef.sym;
            index.type = varDef.type;
            stats.append(varDef);
            lhs = indexed.getSequence();
        }

        //if the assignop operand is a select of the kind a.x
        //then we need to cache the selected part (a), so that
        //it won't be recomputed twice.
        //
        // var $expr$ = a;

        F3Ident selector = null;

        if (lhs.getF3Tag() == F3Tag.SELECT) {
            F3Expression selected = ((F3Select)lhs).selected;
            // But, if this select is ClassName.foo, then we don't want
            // to create "var $expr = a;"
            Symbol sym = F3TreeInfo.symbolFor(selected);
            if (sym == null || sym.kind != Kinds.TYP) {
                F3Var varDef = makeVar(tree.pos(), defs.exprNamePrefix(), selected, selected.type);
                selector = m.at(tree.pos).Ident(varDef.sym);
                selector.sym = varDef.sym;
                selector.type = varDef.type;
                stats.append(varDef);
            }
        }

        F3Expression varRef = lhs;

        //create a reference to the cached var. The translated expression
        //depends on whether the operand is a select or not:
        //
        //(SELECT)  $expr$.x;
        //(IDENT)   x;

        if (selector != null) {
            F3VarSymbol vsym = (F3VarSymbol)F3TreeInfo.symbol(lhs);
            varRef = m.at(tree.pos).Select(selector, vsym, false);
            ((F3Select)varRef).sym = vsym;
        }

        if (index != null) {
            varRef = m.at(tree.pos).SequenceIndexed(varRef, index).setType(tree.lhs.type);
        }

        //Generate the binary expression this assignop translates to
        F3Expression op = null;

        if (isSpecialLiteralOperation) {
            //special literal assignop (duration, length, angle, or color)
            //
            //(SELECT) $expr$.x = $expr$.x.[add/sub/mul/div](lhs);
            //(IDENT)  x = x.[add/sub/mul/div](lhs);
            F3Select meth = (F3Select)m.at(tree.pos).Select(varRef, tree.operator.name, false);
            meth.setType(tree.operator.type);
            meth.sym = tree.operator;
            op = m.at(tree.pos).Apply(List.<F3Expression>nil(), meth, List.of(tree.rhs));
            op.setType(tree.type);
        } else {
            //numeric assignop
            //
            //(SELECT) $expr$.x = $expr$.x [+|-|*|/] lhs;
            //(IDENT)  x = $expr$.x [+|-|*|/] lhs;
            op = m.at(tree.pos).Binary(opTag, varRef, tree.rhs);
            ((F3Binary)op).operator = tree.operator;
            op.setType(tree.operator.type.asMethodType().getReturnType());
        }
        F3Expression assignOpStat = (F3Expression)m.at(tree.pos).Assign(varRef, op).setType(op.type);

        F3Expression res = stats.nonEmpty() ?
            m.at(tree.pos).Block(0L, stats.toList(), assignOpStat).setType(op.type) :
            assignOpStat;
        return lowerExpr(res, Type.noType);
    }

    public void visitBinary(F3Binary tree) {
        boolean isSpecialLiteralBinaryExpr = tree.operator == null;
        boolean isEqualExpr = (tree.getF3Tag() == F3Tag.EQ ||
                tree.getF3Tag() == F3Tag.NE);
        boolean isSequenceOp = types.isSequence(tree.lhs.type) ||
                types.isSequence(tree.rhs.type);
        boolean isBoxedOp = (tree.lhs.type.isPrimitive() && !tree.rhs.type.isPrimitive()) ||
                (tree.rhs.type.isPrimitive() && !tree.lhs.type.isPrimitive());
        Type lhsType = tree.lhs.type;
        Type rhsType = tree.rhs.type;
        if (!isSpecialLiteralBinaryExpr) {
            lhsType = isSequenceOp && isEqualExpr ?
                types.sequenceType(tree.operator.type.getParameterTypes().head) :
                tree.operator.type.getParameterTypes().head;
            rhsType = isSequenceOp && isEqualExpr ?
                types.sequenceType(tree.operator.type.getParameterTypes().tail.head) :
                tree.operator.type.getParameterTypes().tail.head;
        }
        F3Expression lhs = isEqualExpr && isBoxedOp && !isSequenceOp ?
            lowerExpr(tree.lhs) :
            lowerExpr(tree.lhs, lhsType);
        F3Expression rhs = isEqualExpr && isBoxedOp && !isSequenceOp ?
            lowerExpr(tree.rhs) :
            lowerExpr(tree.rhs, rhsType);
        F3Binary res = m.at(tree.pos).Binary(tree.getF3Tag(), lhs, rhs);
        res.operator = tree.operator;
	res.methodName = tree.methodName;
	res.infix = tree.infix;
        result = res.setType(tree.type);
    }

    public void visitForExpressionInClause(F3ForExpressionInClause that) {
        F3Expression whereExpr = lowerExpr(that.getWhereExpression());
        Type typeToCheck = that.seqExpr.type;
        if  (that.seqExpr.type.tag == TypeTags.BOT ||
                types.isSameType(that.seqExpr.type, syms.f3_EmptySequenceType)) {
            typeToCheck = types.sequenceType(that.var.type);
        }
        else if (that.isBound() &&
                types.isArray(that.seqExpr.type)) {
            // Bound-for is implemented only over sequences, convert the nativearray to a sequence
            typeToCheck = types.sequenceType(types.elemtype(that.seqExpr.type));
        }
        else if (!types.isSequence(that.seqExpr.type) &&
                !types.isArray(that.seqExpr.type) &&
                types.asSuper(that.seqExpr.type, syms.iterableType.tsym) == null) {
            typeToCheck = types.sequenceType(that.seqExpr.type);
        }
        F3Expression seqExpr = lowerExpr(that.seqExpr, typeToCheck);
        F3ForExpressionInClause res = m.at(that.pos).InClause(that.getVar(), seqExpr, whereExpr);
        res.setIndexUsed(that.getIndexUsed());
        res.indexVarSym = that.indexVarSym;
        forClauseMap.put(that, res);
        result = res.setType(that.type);
    }

    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        F3Tree prevFunc = enclFunc;
        try {
            enclFunc = tree;
            F3Block body  = (F3Block)lowerExpr(tree.getBodyExpression(), tree.type != null ? tree.type.getReturnType() : Type.noType);
            F3FunctionDefinition res = m.at(tree.pos).FunctionDefinition(tree.mods, tree.name, tree.getF3ReturnType(), tree.getParams(), body);
            res.operation.definition = res;
            res.sym = tree.sym;
            result = res.setType(tree.type);
        }
        finally {
            enclFunc = prevFunc;
        }
    }

    public void visitFunctionInvocation(F3FunctionInvocation tree) {
	if (tree.partial) {
	    result = toFunctionValue(tree, false);
	    return;
	} 
        F3Expression meth = lowerFunctionName(tree.meth);
        List<Type> paramTypes = tree.meth.type.getParameterTypes();

        Symbol sym = F3TreeInfo.symbolFor(tree.meth);
        
        List<F3Expression> args = List.nil();
        boolean pointer_Make = types.isSyntheticPointerFunction(sym);
        boolean builtins_Func = types.isSyntheticBuiltinsFunction(sym);
        if (pointer_Make || builtins_Func) {
                F3Expression varExpr = lowerExpr(tree.args.head);
                ListBuffer<F3Expression> syntheticArgs = ListBuffer.lb();
                syntheticArgs.append(m.at(tree.pos).VarRef(varExpr, F3VarRef.RefKind.INST).setType(syms.f3_ObjectType));
                
                if (varExpr.getF3Tag() == F3Tag.IDENT && ((F3Ident)varExpr).getName().equals(names._this)) {
                    syntheticArgs.append(m.at(tree.pos).LiteralInteger("-1", 10).setType(syms.intType));
                } else {
                    syntheticArgs.append(m.at(tree.pos).VarRef(varExpr, F3VarRef.RefKind.VARNUM).setType(syms.intType));
                }
                
                Symbol msym = builtins_Func ?
                    preTrans.makeSyntheticBuiltinsMethod(sym.name) :
                    preTrans.makeSyntheticPointerMake();
                F3TreeInfo.setSymbol(meth, msym);
                meth.type = msym.type;
                args = syntheticArgs.toList();
        }
        else if (sym instanceof MethodSymbol &&
                ((MethodSymbol)sym).isVarArgs()) {
            List<F3Expression> actuals = tree.args;
            while (paramTypes.tail.nonEmpty()) {
                args = args.append(lowerExpr(actuals.head, paramTypes.head));
                actuals = actuals.tail;
                paramTypes = paramTypes.tail;
            }
            Type varargType = paramTypes.head;
            if (actuals.size() == 1 && 
                    (types.isSequence(actuals.head.type) ||
                    types.isArray(actuals.head.type))) {
                args = args.append(lowerExpr(actuals.head, varargType));
            }
            else if (actuals.size() > 0) {
                while (actuals.nonEmpty()) {
                    args = args.append(lowerExpr(actuals.head, types.elemtype(varargType)));
                    actuals = actuals.tail;
                }
            }
        }
        else {
	    try {
		//System.err.println("tree.args="+tree.args);
		//System.err.println("meth.paramTypes="+tree.meth.type.getParameterTypes());
		//System.err.println("type.paramTypes="+tree.type.getParameterTypes());
		args = lowerExprs(tree.args, paramTypes);
	    } catch (NullPointerException exc) {
		throw new RuntimeException(tree + " => type: "+tree.meth.type, exc);
	    }
        }
         
        result = m.Apply(tree.typeargs, meth, args);
        result.type = tree.type;
    }
    //where
    private F3Expression lowerFunctionName(F3Expression meth) {
        Symbol msym = F3TreeInfo.symbolFor(meth);
        if (meth.getF3Tag() == F3Tag.IDENT) {
            return m.at(meth.pos()).Ident(msym).setType(meth.type);
        } else if (meth.getF3Tag() == F3Tag.SELECT) {
            return lowerSelect((F3Select)meth);
        } else {
            return lowerExpr(meth);
        }
    }

    public void visitFunctionValue(F3FunctionValue tree) {
        F3Tree prevFunc = enclFunc;
        try {
            enclFunc = tree;
            tree.bodyExpression = (F3Block)lowerExpr(tree.bodyExpression, tree.type.getReturnType());
            result = tree;
        }
        finally {
            enclFunc = prevFunc;
        }
    }

    public void visitIfExpression(F3IfExpression tree) {
        if (tree.type.tag != TypeTags.VOID &&
                (tree.truepart.type == syms.unreachableType ||
                (tree.falsepart != null && tree.falsepart.type == syms.unreachableType))) {
            result = lowerUnreachableIfExpression(tree);
        }
        else {
            boolean thenPartSeq = types.isSequence(tree.truepart.type);
            boolean elsePartSeq = tree.falsepart != null ?
                types.isSequence(tree.falsepart.type) :
                thenPartSeq;
            boolean nonSeqExpected = thenPartSeq != elsePartSeq &&
                    !types.isSequence(pt);
            F3Expression cond = lowerExpr(tree.cond, syms.booleanType);
            F3Expression truePart = lowerExpr(tree.truepart,
                    !nonSeqExpected || thenPartSeq ? tree.type : types.elementTypeOrType(tree.type));
            F3Expression falsePart = lowerExpr(tree.falsepart,
                    !nonSeqExpected || elsePartSeq ? tree.type : types.elementTypeOrType(tree.type));
            result = m.at(tree.pos).Conditional(cond, truePart, falsePart);
            result.setType(nonSeqExpected ? syms.objectType : tree.type);
        }
    }

    public F3Tree lowerUnreachableIfExpression(F3IfExpression tree) {
        boolean inverted = tree.truepart.type == syms.unreachableType;
        Type treeType = tree.type.tag == TypeTags.BOT ?
            types.isSequence(tree.type) ?
                types.sequenceType(syms.objectType) : syms.objectType :
            tree.type;
        F3Expression truePart = lowerExpr(tree.truepart, treeType);
        F3Expression falsePart = lowerExpr(tree.falsepart, treeType);
        F3Var varDef = makeVar(tree.pos(), defs.resNamePrefix(), null, treeType);
        F3Ident varRef = m.at(tree.pos).Ident(varDef.sym);
        varRef.sym = varDef.sym;
        varRef.type = varDef.type;

        F3Expression assign = m.at(tree.pos).Assign(varRef, inverted ? falsePart : truePart).setType(syms.voidType); //we need void here!

        F3Expression ifExpr = m.at(tree.pos).Conditional(tree.cond,
                inverted ? truePart : assign, inverted ? assign : falsePart).setType(syms.voidType); //we need void here!

        return m.at(tree.pos()).Block(0L, List.of(varDef, ifExpr), varRef).setType(varRef.type);
    }

    public void visitIndexof(F3Indexof that) {
        F3Indexof res = m.at(that.pos).Indexof(that.fname);
        res.clause = that.clause;
        result = res.setType(that.type);
    }

    public void visitInstanceOf(F3InstanceOf tree) {
        F3Expression expr = lowerExpr(tree.getExpression());
        result = m.at(tree.pos).TypeTest(expr, tree.clazz).setType(tree.type);
    }

    public void visitInterpolateValue(F3InterpolateValue that) {
        F3Expression pointerType = m.at(that.pos).Type(syms.f3_PointerType).setType(syms.f3_PointerType);
        Symbol pointerMakeSym = rs.resolveQualifiedMethod(that.pos(),
                env, syms.f3_PointerType,
                defs.Pointer_make.methodName,
                rs.newMethTemplate(List.of(syms.objectType),
                List.<Type>nil()));
        pointerMakeSym.flags_field |= F3Flags.FUNC_POINTER_MAKE;
        F3Select pointerMake = (F3Select)m.at(that.pos).Select(pointerType, pointerMakeSym, false);
        pointerMake.sym = pointerMakeSym;
        F3Expression pointerCall = m.at(that.pos).Apply(List.<F3Expression>nil(),
                pointerMake,
                List.of(that.attribute)).setType(pointerMakeSym.type.getReturnType());
        ListBuffer<F3Tree> parts = ListBuffer.lb();
        parts.append(makeObjectLiteralPart(that.pos(), syms.f3_KeyValueType, defs.value_InterpolateMethodName, that.funcValue));
        parts.append(makeObjectLiteralPart(that.pos(), syms.f3_KeyValueType, defs.target_InterpolateMethodName, pointerCall));
        if (that.interpolation != null) {
            parts.append(makeObjectLiteralPart(that.pos(), syms.f3_KeyValueType, defs.interpolate_InterpolateMethodName, that.interpolation));
        }
        F3Expression res = m.at(that.pos).ObjectLiteral(m.at(that.pos).Type(syms.f3_KeyValueType), parts.toList()).setType(syms.f3_KeyValueType);
        result = lowerExpr(res);
    }
    //where
    private F3ObjectLiteralPart makeObjectLiteralPart(DiagnosticPosition pos, Type site, Name varName, F3Expression value) {
        F3ObjectLiteralPart part = m.at(pos).ObjectLiteralPart(varName, value, F3BindStatus.UNBOUND);
        part.setType(value.type);
        part.sym = rs.findIdentInType(env, site, varName, Kinds.VAR);
        return part;
    }

    public void visitKeyFrameLiteral(F3KeyFrameLiteral that) {
        ListBuffer<F3Tree> parts = ListBuffer.lb();
        F3Expression keyValues = m.at(that.pos).ExplicitSequence(that.values).setType(types.sequenceType(syms.f3_KeyValueType));
        parts.append(makeObjectLiteralPart(that.pos(), syms.f3_KeyFrameType, defs.time_KeyFrameMethodName, that.start));
        parts.append(makeObjectLiteralPart(that.pos(), syms.f3_KeyFrameType, defs.values_KeyFrameMethodName, keyValues));
        F3Expression res = m.at(that.pos).ObjectLiteral(m.at(that.pos).Type(syms.f3_KeyValueType), parts.toList()).setType(syms.f3_KeyFrameType);
        result = lowerExpr(res);
    }

    public void visitLiteral(F3Literal tree) {
        result = tree;
    }

    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        F3Expression expr = lowerExpr(tree.getExpression(), tree.type); //tree.sym.type);
        F3ObjectLiteralPart res = m.at(tree.pos).ObjectLiteralPart(tree.name, expr, tree.getExplicitBindStatus());
        res.markBound(tree.getBindStatus());
        res.sym = tree.sym;
        result = res.setType(tree.type);
    }

    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        F3Expression init = lowerExpr(tree.getInitializer(), tree.getId().sym.type);
        F3OnReplace onReplace = lowerDecl(tree.getOnReplace());
        F3OnReplace onInvalidate = lowerDecl(tree.getOnInvalidate());
        F3OverrideClassVar res = m.at(tree.pos).OverrideClassVar(tree.name, tree.getF3Type(), tree.mods, tree.getId(), init, tree.getBindStatus(), onReplace, onInvalidate);
        res.sym = tree.sym;
        result = res.setType(tree.type);
    }

    public void visitReturn(F3Return tree) {
        Type typeToCheck = enclFunc.type != null ?
            enclFunc.type.getReturnType() :
            syms.objectType; //nedded because run function has null type
        F3Expression retExpr = lowerExpr(tree.getExpression(), typeToCheck);
        result = m.at(tree.pos).Return(retExpr).setType(tree.type);
    }

    public void visitSequenceDelete(F3SequenceDelete that) {
        F3Expression seq = lowerExpr(that.getSequence());
        F3Expression el = that.getElement();
        if (that.getElement() != null) {
            Type typeToCheck = types.isArrayOrSequenceType(that.getElement().type) ?
                    that.getSequence().type :
                    types.elementType(that.getSequence().type);
            el = lowerExpr(that.getElement(), typeToCheck);
        }
        result = m.at(that.pos).SequenceDelete(seq, el).setType(that.type);
    }

    public void visitSequenceEmpty(F3SequenceEmpty that) {
        result = that;
    }

    public void visitSequenceExplicit(F3SequenceExplicit that) {
        ListBuffer<F3Expression> buf = ListBuffer.lb();
        for (F3Expression item : that.getItems()) {
            Type typeToCheck = types.isSameType(item.type, syms.objectType) ||
                    types.isArrayOrSequenceType(item.type) ?
                that.type :
                types.elementType(that.type);
            flatten(lowerExpr(item, typeToCheck), buf);
        }
        result = buf.length() == 1 && types.isSequence(buf.toList().head.type) ?
            buf.toList().head :
            m.at(that.pos).ExplicitSequence(buf.toList()).setType(that.type);
    }
    //where
    private void flatten(F3Expression item, ListBuffer<F3Expression> items) {
        if (item.getF3Tag() == F3Tag.SEQUENCE_EXPLICIT) {
            F3SequenceExplicit nestedSeq = (F3SequenceExplicit)item;
            for (F3Expression nestedItem : nestedSeq.getItems()) {
                flatten(nestedItem, items);
            }
        }
        else {
            items.append(item);
        }
    }
    public void visitSequenceIndexed(F3SequenceIndexed that) {
        F3Expression index = lowerExpr(that.getIndex(), syms.intType);
        F3Expression seq = lowerExpr(that.getSequence());
        result = m.at(that.pos).SequenceIndexed(seq, index).setType(that.type);
    }

    public void visitSequenceInsert(F3SequenceInsert that) {
        F3Expression seq = lowerExpr(that.getSequence());
        Type typeToCheck = types.isArrayOrSequenceType(that.getElement().type) ||
                types.isSameType(syms.objectType, that.getElement().type) ?
                that.getSequence().type :
                types.elementType(that.getSequence().type);
        F3Expression el = lowerExpr(that.getElement(), typeToCheck);
        F3Expression pos = lowerExpr(that.getPosition(), syms.intType);
        result = m.at(that.pos).SequenceInsert(seq, el, pos, that.shouldInsertAfter()).setType(that.type);
    }

    public void visitSequenceRange(F3SequenceRange that) {
        F3Expression lower = lowerExpr(that.getLower(), types.elementType(that.type));
        F3Expression upper = lowerExpr(that.getUpper(), types.elementType(that.type));
        F3Expression step = lowerExpr(that.getStepOrNull(), types.elementType(that.type));
        F3SequenceRange res = m.at(that.pos).RangeSequence(lower, upper, step, that.isExclusive());
        result = res.setType(that.type);
    }

    public void visitSequenceSlice(F3SequenceSlice that) {
        F3Expression seq = lowerExpr(that.getSequence());
        F3Expression start = lowerExpr(that.getFirstIndex(), syms.intType);
        F3Expression end = lowerExpr(that.getLastIndex(), syms.intType);
        result = m.at(that.pos).SequenceSlice(seq, start, end, that.getEndKind()).setType(that.type);
    }

    public void visitStringExpression(F3StringExpression tree) {
        List<F3Expression> parts = lowerExprs(tree.parts);
        result = m.at(tree.pos).StringExpression(parts, tree.translationKey).setType(tree.type);
    }

    public void visitUnary(F3Unary tree) {
        if (tree.getF3Tag().isIncDec()) {
            result = lowerNumericUnary(tree);
        } else {
            F3Expression arg = tree.getF3Tag() == F3Tag.REVERSE ?
                lowerExpr(tree.getExpression(), tree.type) :
                tree.getOperator() != null ?
                    lowerExpr(tree.getExpression(), tree.getOperator().type.getParameterTypes().head) :
                    lowerExpr(tree.getExpression());
            F3Unary res = m.at(tree.pos).Unary(tree.getF3Tag(), arg);
            res.operator = tree.operator;
            res.type = tree.type;
            result = res;
        }
    }

    private F3Expression lowerNumericUnary(F3Unary tree) {
        boolean postFix = isPostfix(tree.getF3Tag());
        F3Tag opTag = unaryToBinaryOpTag(tree.getF3Tag());
        Type opType = types.unboxedTypeOrType(tree.getExpression().type);
        if (types.isSameType(opType, syms.charType)) {
            opType = syms.intType;
        }
        ListBuffer<F3Expression> stats = ListBuffer.lb();

        //if the unary operand is an indexed expression of the kind a.x[i]
        //then we need to cache the index value (not to recumpute it twice).

        F3Expression expr = tree.getExpression();
        F3Ident index = null;

        if (tree.getExpression().getF3Tag() == F3Tag.SEQUENCE_INDEXED) {
            F3SequenceIndexed indexed = (F3SequenceIndexed)tree.getExpression();
            F3Var varDef = makeVar(tree.pos(), defs.indexNamePrefix(), indexed.getIndex(), indexed.getIndex().type);
            index = m.at(tree.pos).Ident(varDef.sym);
            index.sym = varDef.sym;
            index.type = varDef.type;
            stats.append(varDef);
            expr = indexed.getSequence();
        }

        //if the unary operand is a select of the kind a.x
        //then we need to cache the selected part (a), so that
        //it won't be recomputed twice.
        //
        // var $expr$ = a;
        F3Ident selector = null;

        if (expr.getF3Tag() == F3Tag.SELECT) {
            F3Expression selected = ((F3Select)expr).selected;
            Symbol sym = F3TreeInfo.symbolFor(selected);
            // But, if this select is ClassName.foo, then we don't want
            // to create "var $expr = a;"
            if (sym == null || sym.kind != Kinds.TYP) {
                F3Var varDef = makeVar(tree.pos(), defs.exprNamePrefix(), selected, selected.type);
                selector = m.at(tree.pos).Ident(varDef.sym);
                selector.sym = varDef.sym;
                selector.type = varDef.type;
                stats.append(varDef);
            }
        }

        F3Expression varRef = expr;

        if (selector != null) {
            F3VarSymbol vsym = (F3VarSymbol)F3TreeInfo.symbol(expr);
            varRef = m.at(tree.pos).Select(selector, vsym, false);
            ((F3Select)varRef).sym = vsym;
        }

        if (index != null) {
            varRef = m.at(tree.pos).SequenceIndexed(varRef, index).setType(tree.getExpression().type);
        }

        //cache the old value of the unary operand. The translated expression
        //depends on whether the operand is a select or not:
        //
        //(SELECT) var $oldVal$ = $expr$.x;
        //(IDENT)  var $oldVal$ = x;
        F3Expression oldVal = varRef;
        boolean needOldValue = postFix && (
                pt != Type.noType ||
                mode == LowerMode.EXPRESSION);
        if (needOldValue) {
            F3Var oldValDef = makeVar(tree.pos(), defs.oldValueNamePrefix(), varRef, varRef.type);
            stats.append(oldValDef);

            oldVal = m.at(tree.pos).Ident(oldValDef.sym);
            ((F3Ident)oldVal).sym = oldValDef.sym;
        }

            //Generate the binary expression this unary translates to
            //
            //(SELECT) $expr$.x = $oldVal [+/-] 1;
            //(IDENT)  x = $oldVal [+/-] 1;

        F3Binary binary = (F3Binary)m.at(tree.pos).Binary(opTag, oldVal, m.at(tree.pos).Literal(opType.tag, 1).setType(opType));
        binary.operator = rs.resolveBinaryOperator(tree.pos(), opTag, env, opType, opType);
	binary.methodName = binary.operator.name;
        binary.setType(binary.operator.type.asMethodType().getReturnType());
        F3Expression incDecStat = (F3Expression)m.at(tree.pos).Assign(varRef, binary).setType(opType);

        //If this is a postfix unary expression, the old value is returned
        F3Expression blockValue = incDecStat;
        if (needOldValue) {
            stats.append(incDecStat);
            blockValue = oldVal;
        }

        F3Expression res = stats.nonEmpty() ?
            m.at(tree.pos).Block(0L, stats.toList(), blockValue).setType(opType) :
            blockValue;
        return lowerExpr(res, Type.noType);
    }
    //where
    private F3Tag unaryToBinaryOpTag(F3Tag tag) {
        switch (tag) {
            case POSTINC:
            case PREINC: return F3Tag.PLUS;
            case POSTDEC:
            case PREDEC: return F3Tag.MINUS;
            default: throw new AssertionError("Unexpecetd unary operator tag: " + tag);
        }
    }
    //where
    private boolean isPostfix(F3Tag tag) {
        switch (tag) {
            case POSTINC:
            case POSTDEC: return true;
            case PREINC:
            case PREDEC: return false;
            default: throw new AssertionError("Unexpected unary operator tag: " + tag);
        }
    }

    public void visitVar(F3Var tree) {
        F3Expression init = lowerExpr(tree.getInitializer(), tree.type);
        F3OnReplace onReplace = lowerDecl(tree.getOnReplace());
        F3OnReplace onInvalidate = lowerDecl(tree.getOnInvalidate());
        F3Var res = m.at(tree.pos).Var(tree.name, tree.getF3Type(), tree.mods, init, tree.getBindStatus(), onReplace, onInvalidate);
        res.sym = tree.sym;
        result = res.setType(tree.type);
        F3VarInit vsi = tree.getVarInit();
        if (vsi != null) {
            // update the var in the var-init
            vsi.resetVar(res);
        }
    }

    public void visitVarInit(F3VarInit tree) {
        result = tree;
    }

    public void visitVarRef(F3VarRef tree) {
        result = tree;
    }

    public void visitBlockExpression(F3Block tree) {
        List<F3Expression> stats = tree.stats;
        F3Expression value = tree.value;
        if (value != null) {
            if (F3TreeInfo.skipParens(value).getF3Tag() == F3Tag.VAR_DEF) {
                F3Var varDef = (F3Var)F3TreeInfo.skipParens(value);
                F3Ident varRef = m.at(tree.value.pos).Ident(varDef.sym);
                varRef.sym = varDef.sym;
                varRef.type = varDef.type;
                value = varRef;
                stats = stats.append(varDef);
            }
            else if (value.type == syms.voidType &&
                    !tree.isVoidValueAllowed) {
                 stats = stats.append(value);
                 value = makeDefaultValue(tree.type);
            }
        }
        List<F3Expression> loweredStats = lowerBlockStatements(stats);
        F3Expression loweredValue = value != null ?
                lowerExpr(value, pt) :
            null;

        if (value != null && pt == syms.voidType) {
            List<F3Expression> mergedLoweredValue =
                    mergeLoweredBlockStatements(
                        new ListBuffer<F3Expression>(),
                        loweredValue,
                        value).toList();
            while (mergedLoweredValue.tail.nonEmpty()) {
                loweredStats = loweredStats.append(mergedLoweredValue.head);
                mergedLoweredValue = mergedLoweredValue.tail;
            }
            loweredValue = mergedLoweredValue.head;
        }

        F3Block res = m.at(tree.pos).Block(tree.flags, loweredStats, loweredValue);
        res.endpos = tree.endpos;
        result = res;
        result.type = value != null ?
            loweredValue != null ? loweredValue.type : syms.voidType :
            tree.type;
    }

    private List<F3Expression> lowerBlockStatements(List<F3Expression> stats) {
        ListBuffer<F3Expression> loweredStats = ListBuffer.lb();
        for (F3Expression stat : stats) {
            mergeLoweredBlockStatements(loweredStats, lowerStmt(stat), stat);
        }
        return loweredStats.toList();
    }

    private ListBuffer<F3Expression> mergeLoweredBlockStatements(ListBuffer<F3Expression> loweredStats, F3Expression loweredStat, F3Expression stat) {
        loweredStats.append(loweredStat);
        return loweredStats;
    }
    //where
    private F3Expression makeDefaultValue(Type t) {
        switch (t.tag) {
            case TypeTags.BYTE: return m.Literal(TypeTags.BYTE, 0).setType(syms.byteType);
            case TypeTags.SHORT: return m.Literal(TypeTags.SHORT, 0).setType(syms.shortType);
            case TypeTags.INT: return m.Literal(TypeTags.INT, 0).setType(syms.intType);
            case TypeTags.FLOAT: return m.Literal(TypeTags.FLOAT, 0).setType(syms.floatType);
            case TypeTags.DOUBLE: return m.Literal(TypeTags.DOUBLE, 0).setType(syms.doubleType);
            case TypeTags.BOOLEAN: return m.Literal(TypeTags.BOOLEAN, 0).setType(syms.booleanType);
            case TypeTags.CLASS: {
                if (types.isSequence(t)) {
                    return m.EmptySequence().setType(syms.f3_EmptySequenceType);
                } else if (types.isSameType(t, syms.f3_StringType)) {
                    return m.Literal("").setType(syms.f3_StringType);
                } else if (types.isSameType(t, syms.f3_DurationType)) {
                    Name zeroName = names.fromString("ZERO");
                    F3Select res = (F3Select)m.Select(
                            preTrans.makeTypeTree(syms.f3_DurationType),
                            zeroName, false).setType(syms.f3_DurationType);
                    res.sym = rs.findIdentInType(env, syms.f3_DurationType, zeroName, Kinds.VAR);
                    return res;
                } else if (types.isSameType(t, syms.f3_LengthType)) {
                    Name zeroName = names.fromString("ZERO");
                    F3Select res = (F3Select)m.Select(
                            preTrans.makeTypeTree(syms.f3_LengthType),
                            zeroName, false).setType(syms.f3_LengthType);
                    res.sym = rs.findIdentInType(env, syms.f3_LengthType, zeroName, Kinds.VAR);
                    return res;
                } else if (types.isSameType(t, syms.f3_AngleType)) {
                    Name zeroName = names.fromString("ZERO");
                    F3Select res = (F3Select)m.Select(
                            preTrans.makeTypeTree(syms.f3_AngleType),
                            zeroName, false).setType(syms.f3_AngleType);
                    res.sym = rs.findIdentInType(env, syms.f3_AngleType, zeroName, Kinds.VAR);
                    return res;
                } else if (types.isSameType(t, syms.f3_ColorType)) {
                    Name blackName = names.fromString("BLACK");
                    F3Select res = (F3Select)m.Select(
                            preTrans.makeTypeTree(syms.f3_ColorType),
                            blackName, false).setType(syms.f3_ColorType);
                    res.sym = rs.findIdentInType(env, syms.f3_ColorType, blackName, Kinds.VAR);
                    return res;
                }
            }
            default: return m.Literal(TypeTags.BOT, null).setType(syms.botType);
        }
    }

    public void visitBreak(F3Break tree) {
        result = tree;
    }

    public void visitCatch(F3Catch tree) {
        F3Block body = lowerExpr(tree.body);
        result = m.at(tree.pos).Catch(tree.param, body).setType(tree.type);
    }

    public void visitClassDeclaration(F3ClassDeclaration tree) {
        Symbol prevClass = currentClass;
        try {
            currentClass = tree.sym;
            List<F3Tree> cdefs = lowerDecls(tree.getMembers());
            tree.setMembers(cdefs);
            result = tree;
        }
        finally {
            currentClass = prevClass;
        }
    }

    public void visitContinue(F3Continue tree) {
        result = tree;
    }

    public void visitErroneous(F3Erroneous tree) {
        result = tree;
    }

    public void visitForExpression(F3ForExpression tree) {
        if (tree.getMap() != null) {
            result = lowerExpr(tree.getMap());
        } else {
            result = lowerForExpression(tree);
            patchForLoop(result, tree.getForExpressionInClauses());
            for (F3ForExpressionInClause clause : tree.getForExpressionInClauses()) {
                forClauseMap.remove(clause);
            }
        }
    }

    public F3Expression lowerForExpression(F3ForExpression tree) {
        F3ForExpressionInClause clause = lowerDecl(tree.getForExpressionInClauses().head);
        F3Expression body = tree.getBodyExpression();
        if (tree.getForExpressionInClauses().size() > 1) {
            // for (INCLAUSE(1), INCLAUSE(2), ... INCLAUSE(n)) BODY
            // (n>1) is lowered to:
            // for (INCLAUSE(1) Lower(for (INCLAUSE(2) (... for (INCLAUSE(n)) ... )) BODY
            F3ForExpression nestedFor = (F3ForExpression)m.ForExpression(tree.getForExpressionInClauses().tail, tree.bodyExpr).setType(tree.type);
            body = lowerForExpression(nestedFor);
        }
        else {
            //single clause for expression - standard lowering
	    if (tree.getBodyExpression().type == null) {
		System.err.println("null body: "+ tree);
	    }
            Type typeToCheck = types.isSameType(tree.getBodyExpression().type, syms.objectType) ||
                    types.isSequence(tree.getBodyExpression().type) ?
                tree.type :
                types.elementType(tree.type);
            body = lowerExpr(tree.bodyExpr, typeToCheck);
        }
        // Standard form is that the body is a block-expression
        if(!(body instanceof F3Block)) {
            body = m.Block(0L, List.<F3Expression>nil(), body).setType(body.type);
        }
        F3ForExpression res = m.at(tree.pos).ForExpression(List.of(clause), body);
        return (F3ForExpression)res.setType(tree.type);
    }

    private void patchForLoop(F3Tree forExpr, final List<F3ForExpressionInClause> clausesToPatch) {
        class ForLoopPatcher extends F3TreeScanner {

            Name targetLabel;
            boolean inWhile = false;
            int synthNameCount = 0;

            private Name newLabelName() {
                return names.fromString(F3Defs.synthForLabelPrefix + forClauseMap.size() + "$" + synthNameCount++);
            }

            @Override
            public void visitWhileLoop(F3WhileLoop tree) {
                boolean prevInWhile = inWhile;
                try {
                    inWhile = true;
                    super.visitWhileLoop(tree);
                }
                finally {
                    inWhile = prevInWhile;
                }
            }

            @Override
            public void visitBreak(F3Break tree) {
                tree.label = (tree.label == null && !inWhile) ?
                    targetLabel :
                    tree.label;
            }

            @Override
            public void visitContinue(F3Continue tree) {
                tree.label = (tree.label == null && !inWhile) ?
                    targetLabel :
                    tree.label;
            }

            @Override
            public void visitIndexof(F3Indexof tree) {
                tree.clause = clausesToPatch.contains(tree.clause) ?
                    forClauseMap.get(tree.clause) :
                    tree.clause;
            }

            @Override
            public void visitForExpressionInClause(F3ForExpressionInClause tree) {
                tree.label = tree.label == null ?
                    newLabelName() :
                    tree.label;
                if (targetLabel == null) {
                    targetLabel = tree.label;
                }
                super.visitForExpressionInClause(tree);
            }
        }
        new ForLoopPatcher().scan(forExpr);
    }

    public void visitIdent(F3Ident tree) {
        if (tree.sym.kind == Kinds.MTH) {
            result = toFunctionValue(tree, false);
        }
        else {
            result = tree;
        }
    }

    F3Expression toFunctionValue(F3Expression tree, boolean isSelect) {
        boolean needsReceiverVar = isSelect;
	boolean staticOfNonStatic = false;
        if (isSelect) {
             F3Select qualId = (F3Select)tree;
             Symbol selectedSym = F3TreeInfo.symbolFor(qualId.selected);
	     staticOfNonStatic = qualId.staticRefOfNonStatic;
             if (selectedSym != null && selectedSym.kind == Kinds.TYP) {
                 needsReceiverVar = false;
             }
        }
	Symbol sym = F3TreeInfo.symbolFor(tree);
        MethodSymbol msym = sym instanceof MethodSymbol ? (MethodSymbol)sym : null;
        Type mtype = tree.type; // hack!!
	Type callType = mtype;
	if (tree instanceof F3FunctionInvocation) {
	    F3FunctionInvocation ftree = (F3FunctionInvocation)tree;
	    callType = ftree.meth.type;
	}
	if (mtype instanceof ForAll) { // hack fix me !!
	    mtype = syms.makeFunctionType(mtype.getTypeArguments(),
					  mtype.asMethodType());
	} else if (mtype instanceof MethodType) {
	    mtype = syms.makeFunctionType((Type.MethodType)mtype);
	}
	mtype = mtype.asMethodType();
        ListBuffer<F3Var> params = ListBuffer.lb();
        ListBuffer<F3Expression> args = ListBuffer.lb();
        MethodSymbol lambdaSym = new MethodSymbol(Flags.SYNTHETIC, defs.lambda_MethodName, mtype, currentClass);
        int count = 0;
        for (Type t : mtype.getParameterTypes()) {
            Name paramName = tempName("x"+count);
            F3VarSymbol paramSym = new F3VarSymbol(types, names, Flags.PARAMETER, paramName, t, lambdaSym);
            F3Var param = m.at(tree.pos).Param(paramName, preTrans.makeTypeTree(t));
            param.sym = paramSym;
            param.type = t;
            params.append(param);
            F3Ident arg = m.at(tree.pos).Ident(param);
            arg.type = param.type;
            arg.sym = param.sym;
            args.append(arg);
            count++;
        }
        Type returnType = mtype.getReturnType();
        F3Var receiverVar = null;
        F3Expression meth;
	if (tree instanceof F3FunctionInvocation) {
	    F3FunctionInvocation ftree = (F3FunctionInvocation)tree;
	    List extraArgs = args.toList();
	    args.clear();
	    for (F3Expression expr : ftree.args) {
		args.append(lowerExpr(expr));
	    }
	    args.appendList(extraArgs);
	    meth = ftree.meth;
	} else {
	    meth = tree.setType(mtype);
	}
	if (staticOfNonStatic) {
	    // we want to change 
	    // String.toUpperCase($x$0) 
	    // to
	    // $x$0.toUpperCase();
	    F3Ident p = (F3Ident)args.next();
	    meth = m.at(tree.pos).Select(p, msym, false);
	}
        if (needsReceiverVar) {
            F3Select qualId= (F3Select)tree;
            receiverVar = makeVar(tree.pos(), "rec", qualId.selected, qualId.selected.type);
            F3Ident receiverVarRef = (F3Ident)m.at(tree.pos).Ident(receiverVar.sym).setType(receiverVar.type);
            meth = m.at(tree.pos).Select(receiverVarRef, msym, false).setType(mtype);
        }
        F3Expression call = m.at(tree.pos).Apply(List.<F3Expression>nil(), meth, args.toList()).setType(returnType);
        F3Block body = (F3Block)m.at(tree.pos).Block(0, List.<F3Expression>nil(), call).setType(returnType);
        F3FunctionValue funcValue = m.at(tree.pos).FunctionValue(m.at(tree.pos).Modifiers(0L), preTrans.makeTypeTree(returnType),
                params.toList(), body);
	//	System.err.println("funcvalue="+funcValue);
	//	System.err.println("lower: "+ tree);
	//	System.err.println("lower mtype="+mtype.getClass()+" "+mtype);
	funcValue.type = mtype;
        funcValue.definition = new F3FunctionDefinition(
                m.at(tree.pos).Modifiers(lambdaSym.flags_field),
		lambdaSym.name,
                funcValue);
        funcValue.definition.pos = tree.pos;
        funcValue.definition.sym = lambdaSym;
        funcValue.definition.type = lambdaSym.type;
        if (needsReceiverVar) {
            F3Binary eqNull = (F3Binary)m.at(tree.pos).Binary(
                    F3Tag.EQ,
                    m.at(tree.pos).Ident(receiverVar.sym).setType(receiverVar.type),
                    m.at(tree.pos).Literal(TypeTags.BOT, null).setType(syms.botType));
            eqNull.operator = rs.resolveBinaryOperator(tree.pos(), F3Tag.EQ, env, syms.objectType, syms.objectType);
            eqNull.setType(syms.booleanType);
            F3Expression blockValue = m.at(tree.pos()).Conditional(
                    eqNull,
                    m.at(tree.pos).Literal(TypeTags.BOT, null).setType(syms.botType),
                    funcValue).setType(funcValue.type);
            return m.at(tree.pos).Block(0,
                    List.<F3Expression>of(receiverVar),
                    blockValue).setType(funcValue.type);
        }
        else {
            return funcValue;
        }
    }

    public void visitImport(F3Import tree) {
        result = tree;
    }

    public void visitInitDefinition(F3InitDefinition tree) {
        F3Block body = lowerExpr(tree.body);
        F3InitDefinition res = m.at(tree.pos).InitDefinition(body);
        res.sym = tree.sym;
        result = res.setType(tree.type);
    }

    /*
     * Determine if the expression uses any names that could clash with names in the class
     */
    private boolean hasNameConflicts(final TypeSymbol csym, final F3Expression expr) {
        class NameClashScanner extends F3TreeScanner {

            boolean clashFound = false;

            //TODO: utterly naive -- add visibility testing
            void checkForClash(Name name) {
                Scope.Entry e = csym.members().lookup(name);
                if (e.scope != null) {
                    clashFound = true;
                }
            }

            @Override
            public void visitIdent(F3Ident tree) {
                checkForClash(tree.getName());
            }
        }
        NameClashScanner ncs = new NameClashScanner();
        ncs.scan(expr);
        boolean clashFound = ncs.clashFound;
        // if (clashFound) System.err.println("Name clash found: " + csym + ", expr: " + expr);
        return clashFound;
    }

    public void visitInstanciate(F3Instanciate tree) {
        ListBuffer<F3Expression> locals = ListBuffer.lb();
        if (tree.getLocalvars().nonEmpty()) {
            //ObjLit {
            //  local var 1;
            //  local var 2;
            //  ...
            //  local var n;
            //}
            //
            //is equivalent to:
            //
            //{
            //
            //  local var 1;
            //  local var 2;
            //  ...
            //  local var n;
            //
            //  ObjLit {
            //    ...
            //  }
            //}
            for (F3Var var : tree.getLocalvars()) {
                locals.append(lowerDecl(var));
            }
        }

        ListBuffer<F3Tree> newOverrides = ListBuffer.<F3Tree>lb();
        ListBuffer<F3ObjectLiteralPart> unboundParts = ListBuffer.<F3ObjectLiteralPart>lb();

        // Determine if there is a mutable non-explicitly bound initializer in a bound object literal,
        // since this could cause the instance to be re-created so binds then need to be external so
        // that they can be re-used (thus, won't, for example, create new objects)
        boolean holdBindsOutsideSubclass = false;
        if (tree.isBound()) {
            for (F3ObjectLiteralPart part : tree.getParts()) {
                if (!part.isExplicitlyBound() && !preTrans.isImmutable(part.getExpression())) {
                    // A bound object literal with non-explicitly bound initializer
                    // requires continuity of binds
                    holdBindsOutsideSubclass = true;
                    break;
                }
            }
        }


        for (F3ObjectLiteralPart part : tree.getParts()) {
            if (part.isExplicitlyBound()) {
                m.at(part.pos());  // create at part position

                // id for the override
                F3Ident id = m.Ident(part.name);
                id.sym = part.sym;
		Type partType = part.type;
                id.type = partType; //part.sym.type;

		//System.err.println("part="+part);
		//System.err.println("part.type="+part.type);
		//System.err.println("part.sym="+part.sym);
		//System.err.println("part.sym.type="+part.sym.type);

                F3Expression partExpr = part.getExpression();

                F3Expression initExpr;

                // Determine if bound object literal initializer should be scoped to object literal level.
                if (true || (holdBindsOutsideSubclass && preTrans.hasSideEffectsInBind(partExpr)) || hasNameConflicts(tree.type.tsym, partExpr)) {
                    // Shread the expression outside the class, so that the context is correct
                    // The variable should be marked as script private as it shouldn't
                    // be accessible from outside.

                    F3Var shred = makeVar(
                            part.pos(),
                            Flags.SYNTHETIC | F3Flags.SCRIPT_PRIVATE,
                            part.name + "$ol",
                            part.getBindStatus(),
                            lowerExpr(partExpr, partType), //part.sym.type),
                            partType //part.sym.type
					  );
                    F3Ident sid = m.Ident(shred.name);
                    sid.sym = shred.sym;
                    sid.type = partType;//part.sym.type;
                    locals.append(shred);
                    initExpr = sid;
                } else {
                    initExpr = partExpr; // lowered with class
                }

                // Turn the part into an override var
                F3OverrideClassVar ocv =
                        m.OverrideClassVar(
                        part.name,
                        preTrans.makeTypeTree(partType),
                        m.Modifiers(part.sym.flags_field),
                        id,
                        initExpr,
                        part.getBindStatus(),
                        null,
                        null);
                ocv.sym = (F3VarSymbol) part.sym;
                ocv.type = partType;//part.sym.type;
                newOverrides.append(ocv);
            } else {
                unboundParts.append(lowerExpr(part));
            }
        }

        // Lower the class.  If there are new overrides, fold them into the class first
        F3ClassDeclaration cdecl = tree.getClassBody();
        F3ClassDeclaration lowCdecl;
        if (newOverrides.nonEmpty()) {
            cdecl.setMembers(cdecl.getMembers().appendList(newOverrides));
            lowCdecl = lowerDecl(cdecl);
            preTrans.liftTypes(cdecl, cdecl.type, preTrans.makeDummyMethodSymbol(cdecl.sym));
        } else {
            lowCdecl = lowerDecl(cdecl);
        }

        // Construct the new instanciate
        F3Instanciate res = m.at(tree.pos).Instanciate(tree.getF3Kind(),
                tree.getIdentifier(),
                lowCdecl,
                lowerExprs(tree.getArgs()),
                unboundParts.toList(),
                List.<F3Var>nil());
        res.sym = tree.sym;
        res.constructor = tree.constructor;
        res.varDefinedByThis = tree.varDefinedByThis;
        res.type = tree.type;

        // If there are locals wrap the whole thing in a block-expression
        if (locals.nonEmpty()) {
            result = m.Block(0L, locals.toList(), res).setType(tree.type);
        } else {
            result = res;
        }
    }

    public void visitInvalidate(F3Invalidate tree) {
        F3Expression expr = lowerExpr(tree.getVariable());
        result = m.at(tree.pos).Invalidate(expr).setType(tree.type);
    }

    public void visitModifiers(F3Modifiers tree) {
        result = tree;
    }

    public void visitOnReplace(F3OnReplace tree) {
        F3Block body = lowerExpr(tree.getBody());
        F3OnReplace res = tree.getTriggerKind() == F3OnReplace.Kind.ONREPLACE ?
                m.at(tree.pos).OnReplace(tree.getOldValue(), tree.getFirstIndex(), tree.getLastIndex(), tree.getNewElements(), body) :
                m.at(tree.pos).OnInvalidate(body);
        result = res.setType(tree.type);
    }

    public void visitParens(F3Parens tree) {
        F3Expression expr = lowerExpr(tree.expr);
        result = m.at(tree.pos).Parens(expr).setType(tree.type);
    }

    public void visitPostInitDefinition(F3PostInitDefinition tree) {
        F3Block body = lowerExpr(tree.body);
        F3PostInitDefinition res = m.at(tree.pos).PostInitDefinition(body);
        res.sym = tree.sym;
        result = res.setType(tree.type);
    }

    public void visitScript(F3Script tree) {
        varCount = 0;
        tree.defs = lowerDecls(tree.defs);
        result = tree;
    }

    public void visitSelect(F3Select tree) {
        result = (tree.sym.kind == Kinds.MTH) ?
            toFunctionValue(tree, true) :
            lowerSelect(tree);
    }

    private F3Expression lowerSelect(F3Select tree) {
        F3Expression res = null;
        if (tree.sym.isStatic() &&
                F3TreeInfo.symbolFor(tree.selected) != null &&
                F3TreeInfo.symbolFor(tree.selected).kind == Kinds.TYP) {
            res = m.at(tree.pos()).Ident(tree.sym);
        }
        else {
            F3Expression selected = lowerExpr(tree.selected);
            res = (F3Select)m.Select(selected, tree.sym, tree.nullCheck);
        }
        return res.setType(tree.type);
    }

    public void visitSkip(F3Skip tree) {
        result = tree;
    }

    public void visitThrow(F3Throw tree) {
        F3Expression expr = lowerExpr(tree.getExpression());
        result = m.at(tree.pos).Throw(expr).setType(tree.type);
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

    public void visitTry(F3Try tree) {
        F3Block body = lowerExpr(tree.getBlock());
        List<F3Catch> catches = lowerDecls(tree.catchers);
        F3Block finallyBlock = lowerExpr(tree.getFinallyBlock());
        result = m.at(tree.pos).Try(body, catches, finallyBlock).setType(tree.type);
    }

    public void visitTypeAny(F3TypeAny tree) {
        result = tree;
    }

    public void visitTypeArray(F3TypeArray tree) {
        result = tree;
    }

    public void visitTypeCast(F3TypeCast tree) {
	Type clazztype = types.erasure(tree.clazz.type);
	//System.err.println("tree="+tree);
	//System.err.println("clazztype="+clazztype);
        F3Expression expr = lowerExpr(tree.getExpression(), clazztype);
        result = m.at(tree.pos).TypeCast(tree.clazz, expr).setType(tree.type);
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

    public void visitTypeUnknown(F3TypeUnknown tree) {
        result = tree;
    }

    public void visitWhileLoop(F3WhileLoop tree) {
        F3Expression cond = lowerExpr(tree.getCondition(), syms.booleanType);
        F3Expression body = lowerExpr(tree.getBody());
        // Standard form is that the body is a block-expression
        if(!(body instanceof F3Block)) {
            body = m.Block(0L, List.<F3Expression>nil(), body);
        }
        body.setType(syms.voidType);
        result = m.at(tree.pos).WhileLoop(cond, body).setType(syms.voidType);
    }
}
