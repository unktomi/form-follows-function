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

package org.f3.api.tree;

/**
 * A simple visitor for F3 tree nodes.
 *
 * @author Tom Ball
 */
public class SimpleF3TreeVisitor <R,P> implements F3TreeVisitor<R,P> {

    protected final R DEFAULT_VALUE;

    protected SimpleF3TreeVisitor() {
        DEFAULT_VALUE = null;
    }

    protected SimpleF3TreeVisitor(R defaultValue) {
        DEFAULT_VALUE = defaultValue;
    }

    protected R defaultAction(Tree node, P p) {
        return DEFAULT_VALUE;
    }

    public final R visit(Tree node, P p) {
        return (node == null) ? null : node.accept(this, p);
    }

    public final R visit(Iterable<? extends Tree> nodes, P p) {
        R r = null;
        if (nodes != null)
            for (Tree node : nodes)
                r = visit(node, p);
        return r;
    }

    public R visitCompilationUnit(UnitTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitImport(ImportTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitVariable(VariableTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitEmptyStatement(EmptyStatementTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitWhileLoop(WhileLoopTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTry(TryTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitCatch(CatchTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitConditionalExpression(ConditionalExpressionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitBreak(BreakTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitContinue(ContinueTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitReturn(ReturnTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitThrow(ThrowTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitMethodInvocation(FunctionInvocationTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitParenthesized(ParenthesizedTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitAssignment(AssignmentTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitCompoundAssignment(CompoundAssignmentTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitUnary(UnaryTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitBinary(BinaryTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeCast(TypeCastTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitInstanceOf(InstanceOfTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitMemberSelect(MemberSelectTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitIdentifier(IdentifierTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitLiteral(LiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitModifiers(ModifiersTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitErroneous(ErroneousTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitBlockExpression(BlockExpressionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitClassDeclaration(ClassDeclarationTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitForExpression(ForExpressionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitIndexof(IndexofTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitForExpressionInClause(ForExpressionInClauseTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitInitDefinition(InitDefinitionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitPostInitDefinition(InitDefinitionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitInstantiate(InstantiateTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitObjectLiteralPart(ObjectLiteralPartTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTrigger(TriggerTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitOnReplace(OnReplaceTree node, P p) {
        return defaultAction(node, p);
    }
    
    public R visitFunctionDefinition(FunctionDefinitionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitFunctionValue(FunctionValueTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceDelete(SequenceDeleteTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceEmpty(SequenceEmptyTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceExplicit(SequenceExplicitTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceIndexed(SequenceIndexedTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceSlice(SequenceSliceTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceInsert(SequenceInsertTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitSequenceRange(SequenceRangeTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitVariableInvalidate(VariableInvalidateTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitStringExpression(StringExpressionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeAny(TypeAnyTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeClass(TypeClassTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeFunctional(TypeFunctionalTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeArray(TypeArrayTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTypeUnknown(TypeUnknownTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitTimeLiteral(TimeLiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitLengthLiteral(LengthLiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitAngleLiteral(AngleLiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitColorLiteral(ColorLiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitInterpolateValue(InterpolateValueTree node, P p) {
        return defaultAction(node, p);
    }
    
    public R visitKeyFrameLiteral(KeyFrameLiteralTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitMissingExpression(ExpressionTree node, P p) {
        return defaultAction(node, p);
    }

    public R visitOverrideClassVar(OverrideClassVarTree node, P p) {
        return defaultAction(node, p);
    }
}
