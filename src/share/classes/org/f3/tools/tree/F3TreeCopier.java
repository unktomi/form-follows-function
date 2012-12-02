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

package org.f3.tools.tree;

import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import java.util.Map;

/**
 * Creates a copy of a tree, using a given TreeMaker.
 * Names, literal values, etc are shared with the original.
 *
 * @author tball
 */
public class F3TreeCopier implements F3Visitor {
    protected F3TreeMaker maker;
    protected F3Tree result;

    public Map<JCTree, Integer> endPositions;

    /** Creates a new instance of TreeCopier */
    public F3TreeCopier(F3TreeMaker maker) {
        this.maker = maker;
    }

    @SuppressWarnings("unchecked")
    public <T extends F3Tree> T copy(T tree) {
        if (tree == null)
            return null;
        tree.accept(this);

        // Update the end positions map, we keep the prior object
        // in there in case others have references to them.
        //
        Integer endpos = endPositions.get(tree);
        endPositions.put(result, endpos);

        return (T)result;
    }

    public <T extends F3Tree> List<T> copy(List<T> trees) {
        if (trees == null)
            return null;
        ListBuffer<T> lb = new ListBuffer<T>();
        for (T tree: trees)
            lb.append(copy(tree));
        return lb.toList();
    }

    public void visitScript(F3Script tree) {
        F3Expression pid = copy(tree.pid);
        List<F3Tree> defs = copy(tree.defs);
        result = maker.at(tree.pos).Script(pid, defs);
    }

    public void visitImport(F3Import tree) {
        F3Expression qualid = copy(tree.qualid);
        result = maker.at(tree.pos).Import(qualid);
    }

    public void visitSkip(F3Skip tree) {
        result = maker.at(tree.pos).Skip();
    }

    public void visitWhileLoop(F3WhileLoop tree) {
        F3Expression cond = copy(tree.cond);
        F3Expression body = copy(tree.body);
        result = maker.at(tree.pos).WhileLoop(cond, body);
    }

    public void visitTry(F3Try tree) {
        F3Block body = copy(tree.body);
        List<F3Catch> catchers = copy(tree.catchers);
        F3Block finalizer = copy(tree.finalizer);
        result = maker.at(tree.pos).Try(body, catchers, finalizer);
    }

    public void visitCatch(F3Catch tree) {
        F3Var param = copy(tree.param);
        F3Block body = copy(tree.body);
        result = maker.at(tree.pos).Catch(param, body);
    }

    public void visitIfExpression(F3IfExpression tree) {
        F3Expression cond = copy(tree.cond);
        F3Expression truepart = copy(tree.truepart);
        F3Expression falsepart = copy(tree.falsepart);
        result = maker.at(tree.pos).Conditional(cond, truepart, falsepart);
    }

    public void visitBreak(F3Break tree) {
        result = maker.at(tree.pos).Break(tree.label);
    }

    public void visitContinue(F3Continue tree) {
        result = maker.at(tree.pos).Continue(tree.label);
    }

    public void visitReturn(F3Return tree) {
        F3Expression expr = copy(tree.expr);
        result = maker.at(tree.pos).Return(expr);
    }

    public void visitThrow(F3Throw tree) {
        F3Expression expr = copy(tree.expr);
        result = maker.at(tree.pos).Throw(expr);
    }

    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        List<F3Expression> typeargs = copy(tree.typeargs);
        F3Expression fn = copy(tree.meth);
        List<F3Expression> args = copy(tree.args);
        result = maker.at(tree.pos).Apply(typeargs, fn, args);
    }

    public void visitParens(F3Parens tree) {
        F3Expression expr = copy(tree.getExpression());
        result = maker.at(tree.pos).Parens(List.of(expr));
    }

    public void visitAssign(F3Assign tree) {
        F3Expression lhs = copy(tree.lhs);
        F3Expression rhs = copy(tree.rhs);
        result = maker.at(tree.pos).Assign(lhs, rhs);
    }

    public void visitAssignop(F3AssignOp tree) {
        F3Expression lhs = copy(tree.lhs);
        F3Expression rhs = copy(tree.rhs);
        F3Tag tag = tree.getF3Tag();
        result = maker.at(tree.pos).Assignop(tag, lhs, rhs);
    }

    public void visitUnary(F3Unary tree) {
        F3Expression arg = copy(tree.arg);
        F3Tag tag = tree.getF3Tag();
        result = maker.at(tree.pos).Unary(tag, arg);
    }

    public void visitBinary(F3Binary tree) {
        F3Expression lhs = copy(tree.lhs);
        F3Expression rhs = copy(tree.rhs);
        F3Tag tag = tree.getF3Tag();
        result = maker.at(tree.pos).Binary(tag, lhs, rhs);
    }

    public void visitTypeCast(F3TypeCast tree) {
        F3Tree clazz = copy(tree.clazz);
        F3Expression expr = copy(tree.expr);
        result = maker.at(tree.pos).TypeCast(clazz, expr);
    }

    public void visitInstanceOf(F3InstanceOf tree) {
        F3Expression expr = copy(tree.expr);
        F3Tree clazz = copy(tree.clazz);
        result = maker.at(tree.pos).TypeTest(expr, clazz);
    }

    public void visitSelect(F3Select tree) {
        F3Expression selected = copy(tree.selected);
        result = maker.at(tree.pos).Select(selected, tree.name, tree.nullCheck);
    }

    public void visitIdent(F3Ident tree) {
        result = maker.at(tree.pos).Ident(tree.getName());
    }

    public void visitLiteral(F3Literal tree) {
        result = maker.at(tree.pos).Literal(tree.typetag, tree.value);
    }

    public void visitModifiers(F3Modifiers tree) {
        result = maker.at(tree.pos).Modifiers(tree.flags);
    }

    public void visitErroneous(F3Erroneous tree) {
        List<? extends F3Tree> errs = copy(tree.getErrorTrees());
        result = maker.at(tree.pos).Erroneous(errs);
    }

    public void visitClassDeclaration(F3ClassDeclaration tree) {
        F3Modifiers mods = copy(tree.mods);
        Name name = tree.getName();
        List<F3Expression> supertypes = copy(tree.getSupertypes());
        List<F3Tree> defs = copy(tree.getMembers());
        result = maker.at(tree.pos).ClassDeclaration(mods, name, supertypes, defs);
    }

    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        F3Modifiers mods = copy(tree.mods);
        Name name = tree.getName();
        F3Type restype = copy(tree.getF3ReturnType());
        List<F3Var> params = copy(tree.getParams());
        F3Block bodyExpression = copy(tree.getBodyExpression());
        result = maker.at(tree.pos).FunctionDefinition(mods, name, restype, params, bodyExpression);
    }

    public void visitInitDefinition(F3InitDefinition tree) {
        F3Block body = tree.body;
        result = maker.at(tree.pos).InitDefinition(body);
    }

    public void visitPostInitDefinition(F3PostInitDefinition tree) {
        F3Block body = tree.body;
        result = maker.at(tree.pos).PostInitDefinition(body);
    }

    public void visitStringExpression(F3StringExpression tree) {
        List<F3Expression> parts = copy(tree.parts);
        result = maker.at(tree.pos).StringExpression(parts, tree.translationKey);
    }

    public void visitInstanciate(F3Instanciate tree) {
        F3Expression clazz = copy(tree.getIdentifier());
        F3ClassDeclaration def = copy(tree.getClassBody());
        List<F3Expression> args = copy(tree.getArgs());
        List<F3ObjectLiteralPart> parts = copy(tree.getParts());
        List<F3Var> localVars = copy(tree.getLocalvars());
        result = maker.at(tree.pos).Instanciate(tree.getF3Kind(), clazz, def, args, parts, localVars);
    }

    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        F3Expression expr = copy(tree.getExpression());
        F3ObjectLiteralPart res = maker.at(tree.pos).ObjectLiteralPart(tree.name, expr, tree.getExplicitBindStatus());
        res.markBound(tree.getBindStatus());
        result = res;
    }

    public void visitTypeAny(F3TypeAny tree) {
        result = maker.at(tree.pos).TypeAny(tree.getCardinality());
    }

    public void visitTypeClass(F3TypeClass tree) {
        F3Expression clazz = copy(tree.getClassName());
        result = maker.at(tree.pos).TypeClass(clazz, tree.getCardinality());
    }

    public void visitTypeVar(F3TypeVar tree) {
        F3Expression clazz = copy(tree.getClassName());
        result = maker.at(tree.pos).TypeVar(clazz, tree.getCardinality());
    }

    public void visitTypeFunctional(F3TypeFunctional tree) {
        List<F3Type> params = copy(tree.getParams());
        F3Type restype = copy(tree.restype);
        result = maker.at(tree.pos).TypeFunctional(params, restype, tree.getCardinality());
    }

    //@Override
    public void visitTypeArray(F3TypeArray tree) {
        F3Type elementType = copy(tree.getElementType());
        result = maker.at(tree.pos).TypeArray(elementType);
    }

    public void visitTypeUnknown(F3TypeUnknown tree) {
        result = maker.at(tree.pos).TypeUnknown();
    }

    //@Override
    public void visitVarInit(F3VarInit tree) {
    }

    //@Override
    public void visitVarRef(F3VarRef tree) {
        result = maker.at(tree.pos).VarRef(tree.getExpression(), tree.getVarRefKind());
    }

    public void visitVar(F3Var tree) {
        Name name = tree.name;
        F3Type type = copy(tree.getF3Type());
        F3Modifiers mods = copy(tree.getModifiers());
        F3Expression init = copy(tree.getInitializer());
        F3OnReplace onReplace = copy(tree.getOnReplace());
        F3OnReplace onInvalidate = copy(tree.getOnInvalidate());
        result = maker.at(tree.pos).Var(name, type, mods, 
                                        init, tree.getBindStatus(), onReplace, onInvalidate);
    }

    public void visitOnReplace(F3OnReplace tree) {
        F3Var oldValue = copy(tree.getOldValue());
        F3Var firstIndex = copy(tree.getFirstIndex());
        F3Var lastIndex = copy(tree.getLastIndex());
        F3Var newElements = copy(tree.getNewElements());
        F3Block body = copy(tree.getBody());
        result = maker.at(tree.pos).OnReplace(oldValue, firstIndex, lastIndex, tree.getEndKind(), newElements, body);
    }

    public void visitBlockExpression(F3Block tree) {
        List<F3Expression> stats = copy(tree.stats);
        F3Expression value = copy(tree.value);
        result = maker.at(tree.pos).Block(tree.flags, stats, value);

    }

    public void visitFunctionValue(F3FunctionValue tree) {
        F3Type restype = copy(tree.rettype);
        List<F3Var> params = copy(tree.getParams());
        F3Block bodyExpression = copy(tree.bodyExpression);
        result = maker.at(tree.pos).FunctionValue(maker.at(tree.pos).Modifiers(0), 
                                                  restype, params, bodyExpression);
    }

    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        result = maker.at(tree.pos).EmptySequence();
    }

    public void visitSequenceRange(F3SequenceRange tree) {
        F3Expression lower = copy(tree.getLower());
        F3Expression upper = copy(tree.getUpper());
        F3Expression stepOrNull = copy(tree.getStepOrNull());
        result = maker.at(tree.pos).RangeSequence(lower, upper, stepOrNull, tree.isExclusive());
    }

    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        List<F3Expression> items = copy(tree.getItems());
        result = maker.at(tree.pos).ExplicitSequence(items);
    }

    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        F3Expression sequence = copy(tree.getSequence());
        F3Expression index = copy(tree.getIndex());
        result = maker.at(tree.pos).SequenceIndexed(sequence, index);
    }

    public void visitSequenceSlice(F3SequenceSlice tree) {
        F3Expression sequence = copy(tree.getSequence());
        F3Expression firstIndex = copy(tree.getFirstIndex());
        F3Expression lastIndex = copy(tree.getLastIndex());
        result = maker.at(tree.pos).SequenceSlice(sequence, firstIndex, lastIndex, tree.getEndKind());
    }

    public void visitSequenceInsert(F3SequenceInsert tree) {
        F3Expression sequence = copy(tree.getSequence());
        F3Expression element = copy(tree.getElement());
        F3Expression position = copy(tree.getPosition());
        result = maker.at(tree.pos).SequenceInsert(sequence, element, position, tree.shouldInsertAfter());
    }

    public void visitSequenceDelete(F3SequenceDelete tree) {
        F3Expression sequence = copy(tree.getSequence());
        F3Expression element = copy(tree.getElement());
        result = maker.at(tree.pos).SequenceDelete(sequence, element);
    }

    public void visitInvalidate(F3Invalidate tree) {
       F3Expression variable = copy(tree.getVariable());
       result = maker.at(tree.pos).Invalidate(variable);
    }

    public void visitForExpression(F3ForExpression tree) {
        List<F3ForExpressionInClause> inClauses = copy(tree.inClauses);
        F3Expression bodyExpr = copy(tree.bodyExpr);
        result = maker.at(tree.pos).ForExpression(inClauses, bodyExpr);
    }

    public void visitForExpressionInClause(F3ForExpressionInClause tree) {
        tree.seqExpr = copy(tree.seqExpr);
        tree.setWhereExpr(copy(tree.getWhereExpression()));
        result = tree;
    }

    public void visitIndexof(F3Indexof tree) {
        result = maker.at(tree.pos).Indexof(tree.fname);
    }

    public void visitTimeLiteral(F3TimeLiteral tree) {
        F3Literal literal = copy(tree.value);
        result = maker.at(tree.pos).TimeLiteral(literal, tree.duration);
    }

    public void visitLengthLiteral(F3LengthLiteral tree) {
        F3Literal literal = copy(tree.value);
        result = maker.at(tree.pos).LengthLiteral(literal, tree.units);
    }

    public void visitAngleLiteral(F3AngleLiteral tree) {
        F3Literal literal = copy(tree.value);
        result = maker.at(tree.pos).AngleLiteral(literal, tree.units);
    }

    public void visitColorLiteral(F3ColorLiteral tree) {
        F3Literal literal = copy(tree.value);
        result = maker.at(tree.pos).ColorLiteral(literal);
    }

    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        Name name = tree.getName();
        F3Modifiers mods = copy(tree.getModifiers());
        F3Ident expr = copy(tree.getId());
        F3Type f3type = copy(tree.getF3Type());
        F3Expression initializer = copy(tree.getInitializer());
        F3OnReplace onReplace = copy(tree.getOnReplace());
        F3OnReplace onInvalidate = copy(tree.getOnInvalidate());
        result = maker.at(tree.pos).OverrideClassVar(name, f3type, mods, expr, initializer, tree.getBindStatus(), onReplace, onInvalidate);
    }

    public void visitInterpolateValue(F3InterpolateValue tree) {
        F3Expression attr = copy(tree.attribute);
        F3Expression value = copy(tree.value);
        F3Expression interpolation = copy(tree.interpolation);
        result = maker.at(tree.pos).InterpolateValue(attr, value, interpolation);
    }

    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        F3Expression start = copy(tree.start);
        List<F3Expression> values = copy(tree.values);
        F3Expression trigger = copy(tree.trigger);
        result = maker.at(tree.pos).KeyFrameLiteral(start, values, trigger);
    }

}
