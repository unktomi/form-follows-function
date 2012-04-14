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

/** The visitor interface for F3 trees.
 */
public interface F3Visitor {
    public void visitScript(F3Script tree);
    public void visitImport(F3Import tree);
    public void visitSkip(F3Skip tree);
    public void visitWhileLoop(F3WhileLoop tree);
    public void visitTry(F3Try tree);
    public void visitCatch(F3Catch tree);
    public void visitIfExpression(F3IfExpression tree);
    public void visitBreak(F3Break tree);
    public void visitContinue(F3Continue tree);
    public void visitReturn(F3Return tree);
    public void visitThrow(F3Throw tree);
    public void visitFunctionInvocation(F3FunctionInvocation tree);
    public void visitParens(F3Parens tree);
    public void visitAssign(F3Assign tree);
    public void visitAssignop(F3AssignOp tree);
    public void visitUnary(F3Unary tree);
    public void visitBinary(F3Binary tree);
    public void visitTypeCast(F3TypeCast tree);
    public void visitInstanceOf(F3InstanceOf tree);
    public void visitSelect(F3Select tree);
    public void visitIdent(F3Ident tree);
    public void visitLiteral(F3Literal tree);
    public void visitModifiers(F3Modifiers tree);
    public void visitErroneous(F3Erroneous tree);
    public void visitClassDeclaration(F3ClassDeclaration tree);
    public void visitFunctionDefinition(F3FunctionDefinition tree);
    public void visitInitDefinition(F3InitDefinition tree);
    public void visitPostInitDefinition(F3PostInitDefinition tree);
    public void visitStringExpression(F3StringExpression tree);
    public void visitInstanciate(F3Instanciate tree);
    public void visitObjectLiteralPart(F3ObjectLiteralPart tree);
    public void visitTypeAny(F3TypeAny tree);
    public void visitTypeClass(F3TypeClass tree);
    public void visitTypeVar(F3TypeVar tree);
    public void visitTypeFunctional(F3TypeFunctional tree);
    public void visitTypeArray(F3TypeArray tree);
    public void visitTypeUnknown(F3TypeUnknown tree);
    public void visitVar(F3Var tree);
    public void visitVarInit(F3VarInit tree);
    public void visitVarRef(F3VarRef tree);
    public void visitOnReplace(F3OnReplace tree);
    public void visitBlockExpression(F3Block tree);
    public void visitFunctionValue(F3FunctionValue tree);
    public void visitSequenceEmpty(F3SequenceEmpty tree);
    public void visitSequenceRange(F3SequenceRange tree);
    public void visitSequenceExplicit(F3SequenceExplicit tree);
    public void visitSequenceIndexed(F3SequenceIndexed tree);
    public void visitSequenceSlice(F3SequenceSlice tree);
    public void visitSequenceInsert(F3SequenceInsert tree);
    public void visitSequenceDelete(F3SequenceDelete tree);
    public void visitInvalidate(F3Invalidate tree);
    public void visitForExpression(F3ForExpression tree);
    public void visitForExpressionInClause(F3ForExpressionInClause tree);
    public void visitIndexof(F3Indexof tree);
    public void visitTimeLiteral(F3TimeLiteral tree);
    public void visitLengthLiteral(F3LengthLiteral tree);
    public void visitAngleLiteral(F3AngleLiteral tree);
    public void visitColorLiteral(F3ColorLiteral tree);
    public void visitOverrideClassVar(F3OverrideClassVar tree);
    public void visitInterpolateValue(F3InterpolateValue tree);
    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree);
}
