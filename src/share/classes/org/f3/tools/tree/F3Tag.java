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

    /* Tree tag values, identifying kinds of trees */
public enum F3Tag {
    
    /** Toplevel nodes, of type TopLevel, representing entire source files.
     */
    TOPLEVEL,

    /** Import clauses, of type Import.
     */
    IMPORT,

    /** The no-op statement ";", of type Skip
     */
    SKIP,

    /** While-loops, of type WhileLoop.
     */
    WHILELOOP,

    /** Try statements, of type Try.
     */
    TRY,

    /** Catch clauses in try statements, of type Catch.
     */
    CATCH,

    /** Conditional expressions, of type Conditional.
     */
    CONDEXPR,

    /** Break statements, of type Break.
     */
    BREAK,

    /** Continue statements, of type Continue.
     */
    CONTINUE,

    /** Return statements, of type Return.
     */
    RETURN,

    /** Throw statements, of type Throw.
     */
    THROW,

    /** Method invocation expressions, of type Apply.
     */
    APPLY,

    /** Parenthesized subexpressions, of type Parens.
     */
    PARENS,

    /** Assignment expressions, of type Assign.
     */
    ASSIGN,

    /** Type cast expressions, of type TypeCast.
     */
    TYPECAST,

    /** Type test expressions, of type TypeTest.
     */
    TYPETEST,

    /** Selections, of type Select.
     */
    SELECT,

    /** Simple identifiers, of type Ident.
     */
    IDENT,

    /** Literals, of type Literal.
     */
    LITERAL,

    /** metadata: Modifiers
     */
    MODIFIERS,

    /** Error trees, of type Erroneous.
     */
    ERRONEOUS,

    /** Unary operators, of type Unary.
     */
    NEG,
    NOT,
    PREINC,
    PREDEC,
    POSTINC,
    POSTDEC,

    /** unary operator for null reference checks, only used internally.
     */
    NULLCHK,

    /** Binary operators, of type Binary.
     */
    OR,
    AND,
    EQ,
    NE,
    LT,
    GT,
    LE,
    GE,
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD,

    /** Assignment operators, of type Assignop.
     */
    PLUS_ASG,
    MINUS_ASG,
    MUL_ASG,
    DIV_ASG,

    /** class declaration
     */
    CLASS_DEF,

    /** Operation definition
     */
    FUNCTION_DEF,

    /** init definition
     */
    INIT_DEF,

    /** postinit definition
     */
    POSTINIT_DEF,

    /** any var declaration including formal params
     */
    VAR_DEF,

    /** variable reference of the kind (inst, varNum)
     */
    VAR_REF,

    /** the run function initialization of a script-level var
     */
    VAR_SCRIPT_INIT,

    /** var override
     */
    OVERRIDE_ATTRIBUTE_DEF,

    /** on change triggers
     */
    ON_REPLACE,

    /** on change triggers
     */
    ON_REPLACE_ELEMENT,

    /** In object literal  "Identifier ':' [ 'bind' 'lazy'?] expression"
     */
    OBJECT_LITERAL_PART,
    
    /** pure object literal 
     */
    OBJECT_LITERAL,
    
    /** String expression "Hello { world() %s }"
     */
    STRING_EXPRESSION,
    
    /** for expression 
     */
    FOR_EXPRESSION,

    /** for expression (x in seq where cond) clause
     */
    FOR_EXPRESSION_IN_CLAUSE,

    /** block expression { ... }
     */
    BLOCK_EXPRESSION,

    /** explicit sequence [78, 6, 14, 21]
     */
    SEQUENCE_EXPLICIT,

    /** range sequence [1..100]
     */
    SEQUENCE_RANGE,

    /** empty sequence []
     */
    SEQUENCE_EMPTY,

    /** index into a sequence
     */
    SEQUENCE_INDEXED,

    /** slice index into a sequence
     */
    SEQUENCE_SLICE,

    /** insert statement
     */
    INSERT,

    /** invalidate statement
     */
    INVALIDATE,

    /** delete statement
     */
    DELETE,

    /** function expression
     */
    FUNCTIONEXPRESSION,

    /** class type
     */
    TYPECLASS,

    /** functional type
     */
    TYPEFUNC,

    /** array type
     */
    TYPEARRAY,

    /** any type
     */
    TYPEANY,

    /** type unspecified
     */
    TYPEUNKNOWN,

    /** xor operator
     */
    XOR,
    F3_OP_FIRST,
    
    /** sizeof operator
     */
    SIZEOF,

    /** The 'indexof name' operator.
     */
    INDEXOF,

    /** reverse unary operator
     */
    REVERSE,

    /** time literal
     */
    TIME_LITERAL,

    /** length literal
     */
    LENGTH_LITERAL,

    /** angle literal
     */
    ANGLE_LITERAL,

    /** color literal
     */
    COLOR_LITERAL,
    
    /** value clause in an interpolation
     */
    INTERPOLATION_VALUE,
    
    /** keyframe literal
     */
    KEYFRAME_LITERAL,

    F3_OP_LAST;
    
    public boolean isIncDec() {
        return (PREINC.ordinal() <= ordinal() && ordinal() <= POSTDEC.ordinal());
    }
    
    public int asOperatorTag() {
        switch (this) {
            case PLUS_ASG:
                return JCTree.PLUS_ASG;
            case MINUS_ASG:
                return JCTree.MINUS_ASG;
            case MUL_ASG:
                return JCTree.MUL_ASG;
            case DIV_ASG:
                return JCTree.DIV_ASG;
            case OR:
                return JCTree.OR;
            case AND:
                return JCTree.AND;
            case EQ:
                return JCTree.EQ;
            case NE:
                return JCTree.NE;
            case LT:
                return JCTree.LT;
            case GT:
                return JCTree.GT;
            case LE:
                return JCTree.LE;
            case GE:
                return JCTree.GE;
            case PLUS:
                return JCTree.PLUS;
            case MINUS:
                return JCTree.MINUS;
            case MUL:
                return JCTree.MUL;
            case DIV:
                return JCTree.DIV;
            case MOD:
                return JCTree.MOD;
            case PREINC:
                return JCTree.PREINC;
            case PREDEC:
                return JCTree.PREDEC;
            case POSTINC:
                return JCTree.POSTINC;
            case POSTDEC:
                return JCTree.POSTDEC;
            case NEG:
                return JCTree.NEG;
            case NOT:
                return JCTree.NOT;
            default:
                throw new RuntimeException("Unexpected operator" + this);
        }
    }
}
