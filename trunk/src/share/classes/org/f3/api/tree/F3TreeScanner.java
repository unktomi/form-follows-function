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
 * A TreeVisitor that visits all the child tree nodes.
 * To visit nodes of a particular type, just override the
 * corresponding visitXYZ method.
 * Inside your method, call super.visitXYZ to visit descendant
 * nodes.
 *
 * <p>The default implementation of the visitXYZ methods will determine
 * a result as follows:
 * <ul>
 * <li>If the node being visited has no children, the result will be null.
 * <li>If the node being visited has one child, the result will be the
 * result of calling {@code scan} on that child. The child may be a simple node
 * or itself a list of nodes.
 * <li> If the node being visited has more than one child, the result will
 * be determined by calling {@code scan} each child in turn, and then combining the
 * result of each scan after the first with the cumulative result
 * so far, as determined by the {@link #reduce} method. Each child may be either
 * a simple node of a list of nodes. The default behavior of the {@code reduce}
 * method is such that the result of the visitXYZ method will be the result of
 * the last child scanned.
 * </ul>
 *
 * <p>Here is an example to count the number of identifier nodes in a tree:
 * <pre>
 *   class CountIdentifiers extends TreeScanner<Integer,Void> {
 *      {@literal @}Override
 *      public Integer visitIdentifier(IdentifierTree node, Void p) {
 *          return 1;
 *      }
 *      {@literal @}Override
 *      public Integer reduce(Integer r1, Integer r2) {
 *          return (r1 == null ? 0 : r1) + (r2 == null ? 0 : r2);
 *      }
 *   }
 * </pre>
 *
 * Java version by:
 * @see com.sun.source.util.TreeScanner
 * 
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * 
 * As merged and modified by:
 * 
 * @author Tom Ball
 * @author Robert Field
 */

public class F3TreeScanner<R,P> implements F3TreeVisitor<R,P> {

    /** Scan a single node.
     */
    public R scan(Tree node, P p) {
        return (node == null) ? null : node.accept(this, p);
    }

    private R scanAndReduce(Tree node, P p, R r) {
        return reduce(scan(node, p), r);
    }

    /** Scan a list of nodes.
     */
    public R scan(Iterable<? extends Tree> nodes, P p) {
        R r = null;
        if (nodes != null) {
            boolean first = true;
            for (Tree node : nodes) {
                r = (first ? scan(node, p) : scanAndReduce(node, p, r));
                first = false;
            }
        }
        return r;
    }

    /**
     * Reduces two results into a combined result.
     * The default implementation is to return the first parameter.
     * The general contract of the method is that it may take any action whatsoever.
     */
    public R reduce(R r1, R r2) {
        return r1;
    }

    private R scanAndReduce(Iterable<? extends Tree> nodes, P p, R r) {
        return reduce(scan(nodes, p), r);
    }

/* ***************************************************************************
 * Visitor methods
 ****************************************************************************/


    public R visitCompilationUnit(UnitTree node, P p) {
        R r = scan(node.getPackageName(), p);
        r = scanAndReduce(node.getImports(), p, r);
        r = scanAndReduce(node.getTypeDecls(), p, r);
        return r;
    }

    public R visitImport(ImportTree node, P p) {
        return scan(node.getQualifiedIdentifier(), p);
    }

    public R visitEmptyStatement(EmptyStatementTree node, P p) {
        return null;
    }

    public R visitWhileLoop(WhileLoopTree node, P p) {
        R r = scan(node.getCondition(), p);
        r = scanAndReduce(node.getBody(), p, r);
        return r;
    }

    public R visitTry(TryTree node, P p) {
        R r = scan(node.getBlock(), p);
        r = scanAndReduce(node.getCatches(), p, r);
        r = scanAndReduce(node.getFinallyBlock(), p, r);
        return r;
    }

    public R visitCatch(CatchTree node, P p) {
        R r = scan(node.getParameter(), p);
        r = scanAndReduce(node.getBlock(), p, r);
        return r;
    }

    public R visitConditionalExpression(ConditionalExpressionTree node, P p) {
        R r = scan(node.getCondition(), p);
        r = scanAndReduce(node.getTrueExpression(), p, r);
        r = scanAndReduce(node.getFalseExpression(), p, r);
        return r;
    }

    public R visitBreak(BreakTree node, P p) {
        return null;
    }

    public R visitContinue(ContinueTree node, P p) {
        return null;
    }

    public R visitReturn(ReturnTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitThrow(ThrowTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitMethodInvocation(FunctionInvocationTree node, P p) {
        R r = scan(node.getTypeArguments(), p);
        r = scanAndReduce(node.getMethodSelect(), p, r);
        r = scanAndReduce(node.getArguments(), p, r);
        return r;
    }

    public R visitParenthesized(ParenthesizedTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitAssignment(AssignmentTree node, P p) {
        R r = scan(node.getVariable(), p);
        r = scanAndReduce(node.getExpression(), p, r);
        return r;
    }

    public R visitCompoundAssignment(CompoundAssignmentTree node, P p) {
        R r = scan(node.getVariable(), p);
        r = scanAndReduce(node.getExpression(), p, r);
        return r;
    }

    public R visitUnary(UnaryTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitBinary(BinaryTree node, P p) {
        R r = scan(node.getLeftOperand(), p);
        r = scanAndReduce(node.getRightOperand(), p, r);
        return r;
    }

    public R visitTypeCast(TypeCastTree node, P p) {
        R r = scan(node.getType(), p);
        r = scanAndReduce(node.getExpression(), p, r);
        return r;
    }

    public R visitInstanceOf(InstanceOfTree node, P p) {
        R r = scan(node.getExpression(), p);
        r = scanAndReduce(node.getType(), p, r);
        return r;
    }

    public R visitMemberSelect(MemberSelectTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitIdentifier(IdentifierTree node, P p) {
        return null;
    }

    public R visitLiteral(LiteralTree node, P p) {
        return null;
    }

    public R visitModifiers(ModifiersTree node, P p) {
        return null;
    }

    public R visitErroneous(ErroneousTree node, P p) {
        return null;
    }
    
    public R visitBlockExpression(BlockExpressionTree node, P p) {
        return scan(node.getStatements(), p);
    }

    public R visitClassDeclaration(ClassDeclarationTree node, P p) {
        R r = scan(node.getModifiers(), p);
        r = scanAndReduce(node.getImplements(), p, r);
        r = scanAndReduce(node.getMixins(), p, r);
        r = scanAndReduce(node.getExtends(), p, r);
        return scanAndReduce(node.getClassMembers(), p, r);
    }

    public R visitForExpression(ForExpressionTree node, P p) {
        R r = scan(node.getInClauses(), p);
        return scanAndReduce(node.getBodyExpression(), p, r);
    }

    public R visitForExpressionInClause(ForExpressionInClauseTree node, P p) {
        R r = scan(node.getVariable(), p);
        r = scanAndReduce(node.getSequenceExpression(), p, r);
        return scanAndReduce(node.getWhereExpression(), p, r);
    }
    
    public R visitIndexof(IndexofTree node, P p) {
        return scan(node.getForVarIdentifier(), p);
    }

    public R visitInitDefinition(InitDefinitionTree node, P p) {
        return scan(node.getBody(), p);
    }

    public R visitPostInitDefinition(InitDefinitionTree node, P p) {
        return scan(node.getBody(), p);
    }

    public R visitInstantiate(InstantiateTree node, P p) {
        R r = scan(node.getArguments(), p);
        r = scanAndReduce(node.getClassBody(), p, r);
        r = scanAndReduce(node.getIdentifier(), p, r);
        r = scanAndReduce(node.getLocalVariables(), p, r);
        return scanAndReduce(node.getLiteralParts(), p, r);
    }

    public R visitInterpolateValue(InterpolateValueTree node, P p) {
        R r = scan(node.getAttribute(), p);
        if (node.getInterpolation() != null) {
            r = scanAndReduce(node.getInterpolation(), p, r);
        }
        return scanAndReduce(node.getValue(), p, r);
    }

    public R visitKeyFrameLiteral(KeyFrameLiteralTree node, P p) {
        R r = scan(node.getStartDuration(), p);
        r = scanAndReduce(node.getInterpolationValues(), p, r);
        return scanAndReduce(node.getTrigger(), p, r);
    }
    public R visitObjectLiteralPart(ObjectLiteralPartTree node, P p) {
        return scan(node.getExpression(), p);
    }

    public R visitOnReplace(OnReplaceTree node, P p) {
        R r = scan(node.getOldValue(), p);
        r = scanAndReduce(node.getFirstIndex(), p, r);
        r = scanAndReduce(node.getLastIndex(), p, r);
        r = scanAndReduce(node.getNewElements(), p, r);
        return scanAndReduce(node.getBody(), p, r);
    }

    public R visitTrigger(TriggerTree node, P p) {
        R r = scan(node.getExpressionTree(), p);
        return scanAndReduce(node.getOnReplaceTree(), p, r);
    }

    
    public R visitFunctionDefinition(FunctionDefinitionTree node, P p) {
        R r = scan(node.getModifiers(), p);
        return scanAndReduce(node.getFunctionValue(), p, r);
    }

    public R visitFunctionValue(FunctionValueTree node, P p) {
        R r = scan(node.getType(), p);
        r = scanAndReduce(node.getParameters(), p, r);
        return scanAndReduce(node.getBodyExpression(), p, r);
    }

    public R visitSequenceDelete(SequenceDeleteTree node, P p) {
        R r = scan(node.getSequence(), p);
        return scanAndReduce(node.getElement(), p, r);
    }

    public R visitSequenceEmpty(SequenceEmptyTree node, P p) {
        return null;
    }

    public R visitSequenceExplicit(SequenceExplicitTree node, P p) {
        return scan(node.getItemList(), p);
    }

    public R visitSequenceIndexed(SequenceIndexedTree node, P p) {
        R r = scan(node.getSequence(), p);
        return scanAndReduce(node.getIndex(), p, r);
    }

    public R visitSequenceSlice(SequenceSliceTree node, P p) {
        R r = scan(node.getSequence(), p);
        r = scanAndReduce(node.getFirstIndex(), p, r);
        return scanAndReduce(node.getLastIndex(), p, r);
    }

    public R visitSequenceInsert(SequenceInsertTree node, P p) {
        R r = scan(node.getSequence(), p);
        return scanAndReduce(node.getElement(), p, r);
    }

    public R visitSequenceRange(SequenceRangeTree node, P p) {
        R r = scan(node.getLower(), p);
        r = scanAndReduce(node.getUpper(), p, r);
        return scanAndReduce(node.getStepOrNull(), p, r);
    }

    public R visitVariableInvalidate(VariableInvalidateTree node, P p) {
        return scan(node.getVariable(), p);
    }

    public R visitStringExpression(StringExpressionTree node, P p) {
        return scan(node.getPartList(), p);
    }

    public R visitTimeLiteral(TimeLiteralTree node, P p) {
        return null;
    }

    public R visitLengthLiteral(LengthLiteralTree node, P p) {
        return null;
    }

    public R visitAngleLiteral(AngleLiteralTree node, P p) {
        return null;
    }

    public R visitColorLiteral(ColorLiteralTree node, P p) {
        return null;
    }

    public R visitTypeAny(TypeAnyTree node, P p) {
        return null;
    }

    public R visitTypeClass(TypeClassTree node, P p) {
        return scan(node.getClassName(), p);
    }

    public R visitTypeFunctional(TypeFunctionalTree node, P p) {
        R r = scan(node.getReturnType(), p);
        return scanAndReduce(node.getParameters(), p, r);
    }

    public R visitTypeArray(TypeArrayTree node, P p) {
        return scan(node.getElementType(), p);
    }

    public R visitTypeUnknown(TypeUnknownTree node, P p) {
        return null;
    }

    public R visitVariable(VariableTree node, P p) {
        R r = scan(node.getModifiers(), p);
        r = scanAndReduce(node.getInitializer(), p, r);
        r = scanAndReduce(node.getF3Type(), p, r);
        r = scanAndReduce(node.getOnReplaceTree(), p, r);
        return scanAndReduce(node.getOnInvalidateTree(), p, r);
    }

    public R visitOverrideClassVar(OverrideClassVarTree node, P p) {
        R r = scan(node.getId(), p);
        r = scanAndReduce(node.getModifiers(), p, r);
        r = scanAndReduce(node.getInitializer(), p, r);
        r = scanAndReduce(node.getF3Type(), p, r);
        r = scanAndReduce(node.getOnReplaceTree(), p, r);
        return scanAndReduce(node.getOnInvalidateTree(), p, r);
    }

    public R visitMissingExpression(ExpressionTree node, P p) {
        return null;
    }

}
