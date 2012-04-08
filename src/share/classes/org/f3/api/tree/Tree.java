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
 * Common interface for all nodes in an abstract syntax tree for the 
 * F3 language.
 *
 * <p><b>WARNING:</b> This interface and its sub-interfaces are
 * subject to change as the Java&trade; programming language evolves.
 * These interfaces are implemented by Sun's Java compiler (javac)
 * and should not be implemented either directly or indirectly by
 * other applications.
 * 
 * @author Tom Ball
 *
 * Merged by
 * @author Robert Field
 * 
 * with the Java AST, by
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 *
 * @since 1.6
 */
public interface Tree {

    /**
     * Enumerates all kinds of trees.
     */
    public enum F3Kind {

        /**
         * Used for instances of {@link AssignmentTree}.
         */
        ASSIGNMENT(AssignmentTree.class),

        /**
         * Used for instances of {@link BreakTree}.
         */
        BREAK(BreakTree.class),

        /**
         * Used for instances of {@link CatchTree}.
         */
        CATCH(CatchTree.class),

        /**
         * Used for instances of {@link UnitTree}.
         */
        COMPILATION_UNIT(UnitTree.class),

        /**
         * Used for instances of {@link ConditionalExpressionTree}.
         */
        CONDITIONAL_EXPRESSION(ConditionalExpressionTree.class),

        /**
         * Used for instances of {@link ContinueTree}.
         */
        CONTINUE(ContinueTree.class),

        /**
         * Used for instances of {@link MemberSelectTree}.
         */
        MEMBER_SELECT(MemberSelectTree.class),

        /**
         * Used for instances of {@link IdentifierTree}.
         */
        IDENTIFIER(IdentifierTree.class),

        /**
         * Used for instances of {@link ImportTree}.
         */
        IMPORT(ImportTree.class),

        /**
         * Used for instances of {@link InstanceOfTree}.
         */
        INSTANCE_OF(InstanceOfTree.class),

        /**
         * Used for instances of {@link FunctionInvocationTree}.
         */
        METHOD_INVOCATION(FunctionInvocationTree.class),

        /**
         * Used for instances of {@link ModifiersTree}.
         */
        MODIFIERS(ModifiersTree.class),

        /**
         * Used for instances of {@link ParenthesizedTree}.
         */
        PARENTHESIZED(ParenthesizedTree.class),

        /**
         * Used for instances of {@link ReturnTree}.
         */
        RETURN(ReturnTree.class),

        /**
         * Used for instances of {@link EmptyStatementTree}.
         */
        EMPTY_STATEMENT(EmptyStatementTree.class),

        /**
         * Used for instances of {@link ThrowTree}.
         */
        THROW(ThrowTree.class),

        /**
         * Used for instances of {@link TryTree}.
         */
        TRY(TryTree.class),

        /**
         * Used for instances of {@link TypeCastTree}.
         */
        TYPE_CAST(TypeCastTree.class),

        /**
         * Used for instances of {@link VariableTree}.
         */
        VARIABLE(VariableTree.class),

        /**
         * Used for instances of {@link VariableInvalidateTree}.
         */
        VARIABLE_INVALIDATE(VariableInvalidateTree.class),

        /**
         * Used for instances of {@link WhileLoopTree}.
         */
        WHILE_LOOP(WhileLoopTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * increment operator {@code ++}.
         */
        POSTFIX_INCREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * decrement operator {@code --}.
         */
        POSTFIX_DECREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * increment operator {@code ++}.
         */
        PREFIX_INCREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * decrement operator {@code --}.
         */
        PREFIX_DECREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing unary minus
         * operator {@code -}.
         */
        UNARY_MINUS(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * complement operator {@code !}.
         */
        LOGICAL_COMPLEMENT(UnaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * multiplication {@code *}.
         */
        MULTIPLY(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * division {@code /}.
         */
        DIVIDE(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * remainder {@code %}.
         */
        REMAINDER(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * addition or string concatenation {@code +}.
         */
        PLUS(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * subtraction {@code -}.
         */
        MINUS(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than {@code <}.
         */
        LESS_THAN(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than {@code >}.
         */
        GREATER_THAN(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than-equal {@code <=}.
         */
        LESS_THAN_EQUAL(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than-equal {@code >=}.
         */
        GREATER_THAN_EQUAL(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * equal-to {@code ==}.
         */
        EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * not-equal-to {@code !=}.
         */
        NOT_EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-and {@code &&}.
         */
        CONDITIONAL_AND(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-or {@code ||}.
         */
        CONDITIONAL_OR(BinaryTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * multiplication assignment {@code *=}.
         */
        MULTIPLY_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * division assignment {@code /=}.
         */
        DIVIDE_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * addition or string concatenation assignment {@code +=}.
         */
        PLUS_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * subtraction assignment {@code -=}.
         */
        MINUS_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * an integral literal expression of type {@code int}.
         */
        INT_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a long integral literal expression of type {@code long}.
         */
        LONG_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a floating-point literal expression of type {@code float}.
         */
        FLOAT_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a floating-point literal expression of type {@code double}.
         */
        DOUBLE_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a boolean literal expression of type {@code boolean}.
         */
        BOOLEAN_LITERAL(LiteralTree.class),


        /**
         * Used for instances of {@link LiteralTree} representing
         * a string literal expression of type {@link String}.
         */
        STRING_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * the use of {@code null}.
         */
        NULL_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link ErroneousTree}.
         */
        ERRONEOUS(ErroneousTree.class),

         /**
         * Used for instances of {@link BlockExpressionTree}.
         */
        BLOCK_EXPRESSION(BlockExpressionTree.class),

        /**
         * Used for instances of {@link ClassDeclarationTree}.
         */
        CLASS_DECLARATION(ClassDeclarationTree.class),

        /**
         * Used for instances of {@link ForExpressionTree}.
         * In for (...) ...
         */
        FOR_EXPRESSION_FOR(ForExpressionTree.class),

        /**
         * Used for instances of {@link ForExpressionTree}.
         * In  seq[ x | cond ]
         */
        FOR_EXPRESSION_PREDICATE(ForExpressionTree.class),

        /**
         * Used for instances of {@link ForExpressionInClauseTree}.
         */
        FOR_EXPRESSION_IN_CLAUSE(ForExpressionInClauseTree.class),

        /**
         * Used for instances of {@link InitDefinitionTree}.
         */
        INIT_DEFINITION(InitDefinitionTree.class),
        
        /**
         * Used for instances of {@link InterpolateValueTree}.
         */
        INTERPOLATE_VALUE(InterpolateValueTree.class), 
        
        /**
         * Used for instances of {@link KeyFrameLiteralTree}.
         */
        KEYFRAME_LITERAL(KeyFrameLiteralTree.class),

        /**
         * Used for instances of {@link InitDefinitionTree}.
         */
        POSTINIT_DEFINITION(InitDefinitionTree.class),

        /**
         * Used for instances of {@link InstantiateTree}.
         * In object literal
         */
        INSTANTIATE_OBJECT_LITERAL(InstantiateTree.class),

        /**
         * Used for instances of {@link InstantiateTree}.
         * In a new class
         */
        INSTANTIATE_NEW(InstantiateTree.class),

        /**
         * Used for instances of {@link ObjectLiteralPartTree}.
         */
        OBJECT_LITERAL_PART(ObjectLiteralPartTree.class),

        /**
         * Used for instances of {@link TriggerTree}.
         */
        TRIGGER_WRAPPER(TriggerTree.class),


        /**
         * Used for instances of {@link OnReplaceTree}.
         */
        ON_REPLACE(OnReplaceTree.class),

       
        /**
         * Used for instances of {@link FunctionDefinitionTree}.
         */
        FUNCTION_DEFINITION(FunctionDefinitionTree.class),

        /**
         * Used for instances of {@link FunctionValueTree}.
         */
        FUNCTION_VALUE(FunctionValueTree.class),

        /**
         * Used for instances of {@link SequenceDeleteTree}.
         */
        SEQUENCE_DELETE(SequenceDeleteTree.class),

        /**
         * Used for instances of {@link SequenceEmptyTree}.
         */
        SEQUENCE_EMPTY(SequenceEmptyTree.class),

        /**
         * Used for instances of {@link SequenceExplicitTree}.
         */
        SEQUENCE_EXPLICIT(SequenceExplicitTree.class),

        /**
         * Used for instances of {@link SequenceIndexedTree}.
         */
        SEQUENCE_INDEXED(SequenceIndexedTree.class),

         /**
         * Used for instances of {@link SequenceSliceTree}.
         */
        SEQUENCE_SLICE(SequenceSliceTree.class),

        /**
         * Used for instances of {@link SequenceInsertTree}.
         */
        SEQUENCE_INSERT(SequenceInsertTree.class),

        /**
         * Used for instances of {@link SequenceRangeTree}.
         */
        SEQUENCE_RANGE(SequenceRangeTree.class),

        /**
         * Used for instances of {@link StringExpressionTree}.
         */
        STRING_EXPRESSION(StringExpressionTree.class),

        /**
         * Used for instances of {@link TimeLiteralTree}.
         */
        TIME_LITERAL(TimeLiteralTree.class),

        /**
         * Used for instances of {@link LengthLiteralTree}.
         */
        LENGTH_LITERAL(LengthLiteralTree.class),

        /**
         * Used for instances of {@link AngleLiteralTree}.
         */
        ANGLE_LITERAL(AngleLiteralTree.class),

        /**
         * Used for instances of {@link ColorLiteralTree}.
         */
        COLOR_LITERAL(ColorLiteralTree.class),

        /**
         * Used for instances of {@link TypeAnyTree}.
         */
        TYPE_ANY(TypeAnyTree.class),

        /**
         * Used for instances of {@link TypeClassTree}.
         */
        TYPE_CLASS(TypeClassTree.class),

        /**
         * Used for instances of {@link TypeFunctionalTree}.
         */
        TYPE_FUNCTIONAL(TypeFunctionalTree.class),

        /**
         * Used for instances of {@link TypeArrayTree}.
         */
        TYPE_ARRAY(TypeArrayTree.class),

        /**
         * Used for sizeof unary operator.
         */
        SIZEOF(UnaryTree.class),
        
        /**
         * Used for reverse unary operator.
         */
        REVERSE(UnaryTree.class),
        
        /**
         * Used for indexof operator.
         */
        INDEXOF(IndexofTree.class),

        /**
         * Used for instances of {@link TypeUnknownTree}.
         */
        TYPE_UNKNOWN(TypeUnknownTree.class),

        /**
         * Used for expressions which are missing.
         */
        MISSING_EXPRESSION(ExpressionTree.class),

        /**
         * ???
         */
        OTHER(null);
        

        F3Kind(Class<? extends Tree> intf) {
            associatedInterface = intf;
        }

        public Class<? extends Tree> asInterface() {
            return associatedInterface;
        }

        private final Class<? extends Tree> associatedInterface;
    }

    /**
     * Gets the F3 kind of this tree.
     *
     * @return the kind of this tree.
     */
    F3Kind getF3Kind();

    /**
     * Was this tree expected, but missing, and filled-in by the parser
     */
    boolean isMissing();

    /**
     * Accept method used to implement the visitor pattern.  The
     * visitor pattern is used to implement operations on trees.
     *
     * @param <R> result type of this operation.
     * @param <D> type of additonal data.
     */
    <R,D> R accept(F3TreeVisitor<R,D> visitor, D data);
}
