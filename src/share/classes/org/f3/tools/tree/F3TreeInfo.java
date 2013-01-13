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

import org.f3.api.tree.Tree;
import java.util.Map;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.tree.JCTree;

import org.f3.tools.code.F3Flags;

/** Utility class containing inspector methods for trees.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3TreeInfo {

    /** The names of all operators.
     */
    protected Name[] opname = new Name[F3Tag.MOD.ordinal() - F3Tag.NEG.ordinal() + 1];
    protected Name[] opname2 = new Name[F3Tag.MOD.ordinal() - F3Tag.NEG.ordinal() + 1];

    protected static final Context.Key<F3TreeInfo> f3TreeInfoKey =
        new Context.Key<F3TreeInfo>();

    public static F3TreeInfo instance(Context context) {
        F3TreeInfo instance = context.get(f3TreeInfoKey);
        if (instance == null)
            instance = new F3TreeInfo(context);
        return instance;
    }

     protected F3TreeInfo(Context context) {
	Name.Table names = Name.Table.instance(context);
        int base = F3Tag.NEG.ordinal();
        opname = new Name[F3Tag.F3_OP_LAST.ordinal() - base + 1];
	opname[F3Tag.NEG    .ordinal() - base] = names.hyphen;
	opname[F3Tag.NOT    .ordinal() - base] = names.fromString("not");
	opname[F3Tag.PREINC .ordinal() - base] = names.fromString("++");
	opname[F3Tag.PREDEC .ordinal() - base] = names.fromString("--");
	opname[F3Tag.POSTINC.ordinal() - base] = names.fromString("++");
	opname[F3Tag.POSTDEC.ordinal() - base] = names.fromString("--");
	opname[F3Tag.NULLCHK.ordinal() - base] = names.fromString("<*nullchk*>");
	opname[F3Tag.OR     .ordinal() - base] = names.fromString("or");
	opname[F3Tag.AND    .ordinal() - base] = names.fromString("and");
	opname[F3Tag.EQ     .ordinal() - base] = names.fromString("==");
	opname[F3Tag.NE     .ordinal() - base] = names.fromString("<>");
	opname[F3Tag.LT     .ordinal() - base] = names.fromString("<");
	opname[F3Tag.GT     .ordinal() - base] = names.fromString(">");
	opname[F3Tag.LE     .ordinal() - base] = names.fromString("<=");
	opname[F3Tag.GE     .ordinal() - base] = names.fromString(">=");
	opname[F3Tag.PLUS   .ordinal() - base] = names.fromString("+");
	opname[F3Tag.MINUS  .ordinal() - base] = names.hyphen;
	opname[F3Tag.MUL    .ordinal() - base] = names.asterisk;
	opname[F3Tag.DIV    .ordinal() - base] = names.slash;
	opname[F3Tag.MOD    .ordinal() - base] = names.fromString("%");
	opname[F3Tag.XOR    .ordinal() - base] = names.fromString("xor");
	opname[F3Tag.SIZEOF .ordinal() - base] = names.fromString("sizeof");
	opname[F3Tag.INDEXOF .ordinal() - base] = names.fromString("indexof");
	opname[F3Tag.REVERSE .ordinal() - base] = names.fromString("reverse");
	opname[F3Tag.TUPLE .ordinal() - base] = names.fromString(",");

        opname2 = new Name[F3Tag.F3_OP_LAST.ordinal() - base + 1];
	opname2[F3Tag.NEG    .ordinal() - base] = names.hyphen;
	opname2[F3Tag.NOT    .ordinal() - base] = names.fromString("$bang");
	opname2[F3Tag.PREINC .ordinal() - base] = names.fromString("$plus$plus");
	opname2[F3Tag.PREDEC .ordinal() - base] = names.fromString("$minus$minus");
	opname2[F3Tag.POSTINC.ordinal() - base] = names.fromString("$plus$plus");
	opname2[F3Tag.POSTDEC.ordinal() - base] = names.fromString("$minus$minus");
	opname2[F3Tag.NULLCHK.ordinal() - base] = names.fromString("<*nullchk*>");
	opname2[F3Tag.OR     .ordinal() - base] = names.fromString("$bar$bar");
	opname2[F3Tag.AND    .ordinal() - base] = names.fromString("$amp$amp");
	opname2[F3Tag.EQ     .ordinal() - base] = names.fromString("$eq$eq");
	opname2[F3Tag.NE     .ordinal() - base] = names.fromString("$not$eq");
	opname2[F3Tag.LT     .ordinal() - base] = names.fromString("$less");
	opname2[F3Tag.GT     .ordinal() - base] = names.fromString("$greater");
	opname2[F3Tag.LE     .ordinal() - base] = names.fromString("$less$eq");
	opname2[F3Tag.GE     .ordinal() - base] = names.fromString("$greater$eq");
	opname2[F3Tag.PLUS   .ordinal() - base] = names.fromString("$plus");
	opname2[F3Tag.MINUS  .ordinal() - base] = names.fromString("$minus");
	opname2[F3Tag.MUL    .ordinal() - base] = names.fromString("$times");
	opname2[F3Tag.DIV    .ordinal() - base] = names.fromString("$div");
	opname2[F3Tag.MOD    .ordinal() - base] = names.fromString("%");
	opname2[F3Tag.XOR    .ordinal() - base] = names.fromString("xor");
	opname2[F3Tag.SIZEOF .ordinal() - base] = names.fromString("sizeof");
	opname2[F3Tag.INDEXOF .ordinal() - base] = names.fromString("indexof");
	opname2[F3Tag.REVERSE .ordinal() - base] = names.fromString("reverse");
	opname2[F3Tag.TUPLE .ordinal() - base] = names.fromString("$comma");
    }    

    /** Return name of operator with given tree tag.
     */
    public Name operatorName(F3Tag tag) {
        return opname[tag.ordinal() - F3Tag.NEG.ordinal()];
    }

    /** Return scala-like encoding of name of operator with given tree tag.
     */
    public Name operatorName2(F3Tag tag) {
        return opname2[tag.ordinal() - F3Tag.NEG.ordinal()];
    }

    /** A DiagnosticPosition with the preferred position set to the 
     *  end position of given tree, if it is a block with
     *  defined endpos.
     */
    public static DiagnosticPosition diagEndPos(final F3Tree tree) {
        final int endPos = F3TreeInfo.endPos(tree);
        return new DiagnosticPosition() {
            public F3Tree getTree() { return tree; }
            public int getStartPosition() { return F3TreeInfo.getStartPos(tree); }
            public int getPreferredPosition() { return endPos; }
            public int getEndPosition(Map<JCTree, Integer> endPosTable) { 
                return F3TreeInfo.getEndPos(tree, endPosTable);
            }
        };
    }

    public static DiagnosticPosition diagnosticPositionFor(final Symbol sym, final F3Tree tree) {
        F3Tree decl = declarationFor(sym, tree);
        return ((decl != null) ? decl : tree).pos();
    }

    /** Find the declaration for a symbol, where
     *  that symbol is defined somewhere in the given tree. */
    public static F3Tree declarationFor(final Symbol sym, final F3Tree tree) {
        class DeclScanner extends F3TreeScanner {

            F3Tree result = null;

            @Override
            public void scan(F3Tree tree) {
                if ( tree != null && result == null ) {
                    tree.accept(this);
                }
            }

            @Override
            public void visitClassDeclaration(F3ClassDeclaration that) {
                if (that.sym == sym) {
                    result = that;
                }
                else  {
                    super.visitClassDeclaration(that);
                }
            }

            @Override
            public void visitScript(F3Script that) {
                if ( that.packge == sym ) {
                    result = that;
                }
                else {
                    super.visitScript(that);
                }
            }

            @Override
            public void visitFunctionDefinition(F3FunctionDefinition that) {
                if ( that.sym == sym ) {
                    result = that;
                }
                else {
                    super.visitFunctionDefinition(that);
                }
            }

            @Override
            public void visitVar(F3Var that) {
                if ( that.sym == sym ) {
                    result = that;
                }
                else {
                    super.visitVar(that);
                }
            }
        }
        DeclScanner s = new DeclScanner();
        tree.accept(s);
        return s.result;
    }

    public static List<F3Tree> pathFor(final F3Tree node, final F3Script unit) {
	class Result extends Error {
	    static final long serialVersionUID = -5942088234594905625L;
	    List<F3Tree> path;
	    Result(List<F3Tree> path) {
		this.path = path;
	    }
	}
	class PathFinder extends F3TreeScanner {
	    List<F3Tree> path = List.nil();
            @Override
	    public void scan(F3Tree tree) {
		if (tree != null) {
		    path = path.prepend(tree);
		    if (tree == node)
			throw new Result(path);
		    super.scan(tree);
		    path = path.tail;
		}
	    }
	}
	try {
	    new PathFinder().scan(unit);
	} catch (Result result) {
	    return result.path;
	}
        return List.nil();
    }

    /** Return first (smallest) flag in `flags':
     *  pre: flags != 0
     */
    public static long firstFlag(long flags) {
        for (int i = 0; i < 63; ++i) {
            long flag = 1L << i;
            if ((flag & flags) != 0) {
                return flag;
            }
        }
        throw new AssertionError();
    }

    /** Return flags as a string, separated by " ".
     */
    public static String flagNames(long flags) {
        return flagNames(flags, false);
    }

    /** Return flags as a string, separated by " ".
     */
    public static String flagNames(long flags, boolean pretty) {
        StringBuffer fsb = new StringBuffer(Flags.toString(flags));
        if ((flags & F3Flags.PACKAGE_ACCESS) != 0) {
            fsb.append("package ");
        }
        if (!pretty && (flags & F3Flags.SCRIPT_PRIVATE) != 0) {
            fsb.append("script only (default) ");
        }
        if ((flags & F3Flags.PUBLIC_READ) != 0) {
            fsb.append("public-read ");
        }
        if ((flags & F3Flags.PUBLIC_INIT) != 0) {
            fsb.append("public-init ");
        }
        if ((flags & F3Flags.DEFAULT) != 0) {
            fsb.append("default ");
        }
        if ((flags & F3Flags.BOUND) != 0) {
            fsb.append("bound ");
        }
        if ((flags & F3Flags.MIXIN) != 0) {
            fsb.append("mixin ");
        }
        if ((flags & F3Flags.OVERRIDE) != 0) {
            fsb.append("override ");
        }
        return fsb.toString().trim();
    }

    /** Operator precedences values.
     */
    public static final int
        notExpression = -1,   // not an expression
        noPrec = 0,           // no enclosing expression
        assignPrec = 1,
	assignopPrec = 2,
	orPrec = 3,
	andPrec = 4,
	eqPrec = 5,
	ordPrec = 6,
	addPrec = 7,
	mulPrec = 8,
	prefixPrec = 9,
	postfixPrec = 10,
	precCount = 11;


    /** Map operators to their precedence levels.
     */
    public static int opPrec(F3Tag op) {
	switch(op) {
	case ASSIGN: // Java distinguished, F3 doesn't -- Java-style
            return assignPrec;
	case PLUS_ASG:
	case MINUS_ASG:
	case MUL_ASG:
	case DIV_ASG:
            return assignopPrec;
        case OR: 
        case XOR: 
            return orPrec;
        case AND: 
            return andPrec;
        case EQ:
        case NE: 
        case TUPLE: 
            return eqPrec;
        case LT:
        case GT:
        case LE:
        case GE: 
            return ordPrec;
        case PLUS:
        case MINUS: 
            return addPrec;
        case MUL:
        case DIV:
        case MOD: 
            return mulPrec;
	case TYPETEST: 
            return ordPrec;
	case NEG:
	case NOT:
	case AMP:
	case PREINC:
	case PREDEC:
	case REVERSE:
	case INDEXOF:
	case SIZEOF:
            return prefixPrec;
	case POSTINC:
	case POSTDEC:
	case NULLCHK: 
            return postfixPrec;
            default: throw new AssertionError("Unexpected operator precidence request: " + op);
	}
    }

    static Tree.F3Kind tagToKind(F3Tag tag) {
        switch (tag) {
        // Postfix expressions
        case POSTINC:           // _ ++
            return Tree.F3Kind.POSTFIX_INCREMENT;
        case POSTDEC:           // _ --
            return Tree.F3Kind.POSTFIX_DECREMENT;

        // Unary operators
        case PREINC:            // ++ _
            return Tree.F3Kind.PREFIX_INCREMENT;
        case PREDEC:            // -- _
            return Tree.F3Kind.PREFIX_DECREMENT;
        case NEG:               // -
            return Tree.F3Kind.UNARY_MINUS;
        case NOT:               // !
            return Tree.F3Kind.LOGICAL_COMPLEMENT;

        // Binary operators

        // Multiplicative operators
        case MUL:               // *
            return Tree.F3Kind.MULTIPLY;
        case DIV:               // /
            return Tree.F3Kind.DIVIDE;
        case MOD:               // %
            return Tree.F3Kind.REMAINDER;

        // Additive operators
        case PLUS:              // +
            return Tree.F3Kind.PLUS;
        case MINUS:             // -
            return Tree.F3Kind.MINUS;

         // Relational operators
        case LT:                // <
            return Tree.F3Kind.LESS_THAN;
        case GT:                // >
            return Tree.F3Kind.GREATER_THAN;
        case LE:                // <=
            return Tree.F3Kind.LESS_THAN_EQUAL;
        case GE:                // >=
            return Tree.F3Kind.GREATER_THAN_EQUAL;

        // Equality operators
        case EQ:                // ==
            return Tree.F3Kind.EQUAL_TO;
        case NE:                // !=
            return Tree.F3Kind.NOT_EQUAL_TO;

         // Conditional operators
        case AND:               // &&
            return Tree.F3Kind.CONDITIONAL_AND;
        case OR:                // ||
            return Tree.F3Kind.CONDITIONAL_OR;

        // Assignment operators
        case MUL_ASG:           // *=
            return Tree.F3Kind.MULTIPLY_ASSIGNMENT;
        case DIV_ASG:           // /=
            return Tree.F3Kind.DIVIDE_ASSIGNMENT;
        case PLUS_ASG:          // +=
            return Tree.F3Kind.PLUS_ASSIGNMENT;
        case MINUS_ASG:         // -=
            return Tree.F3Kind.MINUS_ASSIGNMENT;

        // Null check (implementation detail), for example, __.getClass()
        case NULLCHK:
            return Tree.F3Kind.OTHER;

        // F3 tags which are used in javac trees
        case TUPLE:
        // F3 tags which are used in javac trees
        case SIZEOF:
            return Tree.F3Kind.OTHER;
        case REVERSE:
            return Tree.F3Kind.OTHER;

        default:
            return null;
        }
    }
    
    public static void setSymbol(F3Tree tree, Symbol sym) {
	tree = skipParens(tree);
	switch (tree.getF3Tag()) {
	case IDENT:
	    ((F3Ident) tree).sym = sym; break;
	case SELECT:
	    ((F3Select) tree).sym = sym; break;
	}
    }

    public static BoundKind boundKind(F3Tree tree) {
	if (tree instanceof F3Type) {
	    return ((F3Type)tree).boundKind;
	}
	return null;
    }

    /** If this tree is an identifier or a field, return its symbol,
     *  otherwise return null.
     */
    public static Symbol symbol(F3Tree tree) {
	tree = skipParens(tree);
	switch (tree.getF3Tag()) {
	case IDENT:
	    return ((F3Ident) tree).sym;
	case SELECT:
	    return ((F3Select) tree).sym;
        case SEQUENCE_INDEXED:
            return symbol(((F3SequenceIndexed) tree).getSequence());
        case SEQUENCE_SLICE:
            return symbol(((F3SequenceSlice) tree).getSequence());
        case VAR_REF:
            return ((F3VarRef)tree).getVarSymbol();
	default:
	    return null;
	}
    }

    /** Skip parens and return the enclosed expression
     */
    public static F3Tree skipParens(F3Tree tree) {

        if (tree == null) return tree;
        if (tree.getF3Tag() == F3Tag.PARENS)
            return skipParens(((F3Parens)tree).getExpression());
        else
            return tree;
    }

    /** If this tree is a qualified identifier, its return fully qualified name,
     *  otherwise return null.
     */
    public static Name fullName(F3Tree tree) {

        // Protect against a missing tree
        //
        if  (tree == null) return null;

        tree = skipParens(tree);
        switch (tree.getF3Tag()) {
        case IDENT:
            return ((F3Ident) tree).getName();
        case SELECT:
            Name sname = fullName(((F3Select) tree).selected);
            return sname == null ? null : sname.append('.', name(tree));
        default:
            return null;
        }
    }

    /** If this tree is an identifier or a field or a parameterized type,
     *  return its name, otherwise return null.
     */
    public static Name name(F3Tree tree) {
        switch (tree.getF3Tag()) {
        case IDENT:
            return ((F3Ident) tree).getName();
        case SELECT:
            return ((F3Select) tree).name;
        default:
            return null;
        }
    }

    public static List<F3Expression> typeArgs(F3Tree tree) {
	List<F3Expression> result = null;
        switch (tree.getF3Tag()) {
        case IDENT:
            result = ((F3Ident) tree).typeArgs;
	    break;
        case SELECT:
            result = ((F3Select) tree).typeArgs;
	    break;
        default:
        }
	return result == null ? List.<F3Expression>nil() : result;
    }


    public static Symbol symbolFor(F3Tree node) {
        if (node == null)
        {
            return null;
        }
        node = skipParens(node);

        switch (node.getF3Tag()) {
        case VAR_DEF:
            return ((F3Var) node).sym;
        case VAR_SCRIPT_INIT:
            return ((F3VarInit) node).getSymbol();
        case CLASS_DEF:
            return ((F3ClassDeclaration) node).sym;
        case FUNCTION_DEF:
            return ((F3FunctionDefinition) node).sym;
        case FUNCTIONEXPRESSION:
            return symbolFor(((F3FunctionValue) node).definition);
        case OBJECT_LITERAL_PART:
            return ((F3ObjectLiteralPart) node).sym;
        case TYPECLASS:
            return symbolFor(((F3TypeClass) node).getTypeExpression());
        case IDENT:
            return ((F3Ident) node).sym;
        case INDEXOF:
            F3ForExpressionInClause clause = ((F3Indexof) node).clause;
            return clause == null ? null : clause.var.sym;
        case SELECT:
            return ((F3Select) node).sym;
        case APPLY:
            return symbolFor(((F3FunctionInvocation) node).meth);
        case TOPLEVEL:
            return ((F3Script) node).packge;
        case ON_REPLACE:
            return symbolFor(((F3OnReplace) node).getOldValue());
        case OVERRIDE_ATTRIBUTE_DEF:
            return symbolFor(((F3OverrideClassVar) node).getId());
        case INIT_DEF:
            return ((F3InitDefinition) node).sym;
        case POSTINIT_DEF:
            return ((F3PostInitDefinition) node).sym;
        default:
            return null;
        }
    }

    /** Get the start position for a tree node.  The start position is
     * defined to be the position of the first character of the first
     * token of the node's source text.
     * @param tree  The tree node
     */
    public static int getStartPos(F3Tree tree) {
        if (tree == null) {
            return Position.NOPOS;
        }

        switch (tree.getF3Tag()) {

            case APPLY:
                return getStartPos(((F3FunctionInvocation) tree).meth);

            case ASSIGN:
                return getStartPos(((F3Assign) tree).lhs);

            case PLUS_ASG:
            case MINUS_ASG:
            case MUL_ASG:
            case DIV_ASG:
                return getStartPos(((F3AssignOp) tree).lhs);

            case OR:
            case AND:
            case EQ:
            case NE:
            case LT:
            case GT:
            case LE:
            case GE:
            case PLUS:
            case MINUS:
            case DIV:
            case MOD:
                return getStartPos(((F3Binary) tree).lhs);
            case SELECT:
                return getStartPos(((F3Select) tree).selected);

            case TYPETEST:
                return getStartPos(((F3InstanceOf) tree).expr);

            case POSTINC:
            case POSTDEC:
                return getStartPos(((F3Unary) tree).arg);

            case ERRONEOUS:

                // Erroneous nodes are created with the correct start
                // position in the source as their pos position, so we do
                // not need to interrogate the list.
                //
                return tree.pos;

            default:

                return tree.pos;
        }
    }

    /** The end position of given tree, if it is a block with
     *  defined endpos.
     */
    public static int endPos(F3Tree tree) {
        if (tree.getF3Tag() == F3Tag.BLOCK_EXPRESSION && ((F3Block) tree).endpos != Position.NOPOS)
            return ((F3Block) tree).endpos;
        else if (tree.getF3Tag() == F3Tag.TRY) {
            F3Try t = (F3Try) tree;
            return endPos((t.finalizer != null)
                          ? t.finalizer
                          : t.catchers.last().body);
        } else
            return tree.pos;
    }

    /** The end position of given tree, given  a table of end positions generated by the parser
     */
    public static int getEndPos(F3Tree tree, Map<JCTree, Integer> endPositions) {
        if (tree == null)
            return Position.NOPOS;

        if (endPositions == null) {
            // fall back on limited info in the tree
            return tree instanceof F3Block ?
                ((F3Block)tree).endpos : F3TreeInfo.endPos(tree);
        }

        Integer mapPos = endPositions.get(tree);
        if (mapPos != null)
            return mapPos;

        switch(tree.getF3Tag()) {
          case INIT_DEF:
            return getEndPos((F3Tree) ((F3InitDefinition) tree).getBody(), endPositions);
          case POSTINIT_DEF:
            return getEndPos((F3Tree) ((F3PostInitDefinition) tree).getBody(), endPositions);
          case OVERRIDE_ATTRIBUTE_DEF: {
            F3OverrideClassVar t = (F3OverrideClassVar)tree;
            if (t.getOnReplace() != null)
                return getEndPos(t.getOnReplace(), endPositions);
            return getEndPos(t.getInitializer(), endPositions);
          }
          case ON_REPLACE:
            return getEndPos(((F3OnReplace) tree).getBody(), endPositions);
          case OBJECT_LITERAL_PART:
            return getEndPos(((F3ObjectLiteralPart) tree).getExpression(), endPositions);
          case STRING_EXPRESSION:
            return tree.pos + ((F3StringExpression) tree).translationKey.length();
          case FOR_EXPRESSION:
            return getEndPos(((F3ForExpression) tree).getBodyExpression(), endPositions);
          case FOR_EXPRESSION_IN_CLAUSE:
            return getEndPos(((F3ForExpressionInClause) tree).getWhereExpression(), endPositions);
          case TYPECLASS:
            return getEndPos(((F3TypeClass) tree).getClassName(), endPositions);
          case TIME_LITERAL:
            return tree.pos + tree.toString().length();
        }
        return F3TreeInfo.getStartPos(tree);
    }
}
