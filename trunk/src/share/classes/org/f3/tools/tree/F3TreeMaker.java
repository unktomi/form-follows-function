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
import java.util.Map;
import java.util.HashMap;
import org.f3.api.F3BindStatus;
import org.f3.api.tree.TimeLiteralTree.Duration;
import org.f3.api.tree.TypeTree.Cardinality;
import org.f3.api.tree.Tree.F3Kind;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Type.ClassType;
import com.sun.tools.mjavac.code.Type.TypeVar;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.code.FunctionType;
import org.f3.tools.comp.F3Defs;
import static org.f3.api.F3BindStatus.*;
import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.TypeTags.*;
import f3.lang.AngleUnit;
import f3.lang.LengthUnit;

/* F3 version of tree maker
 */
public class F3TreeMaker implements F3TreeFactory {

    /** The context key for the tree factory. */
    protected static final Context.Key<F3TreeMaker> f3TreeMakerKey =
        new Context.Key<F3TreeMaker>();

    /** Get the F3TreeMaker instance. */
    public static F3TreeMaker instance(Context context) {
        F3TreeMaker instance = context.get(f3TreeMakerKey);
        if (instance == null)
            instance = new F3TreeMaker(context);
        return instance;
    }

    /** The position at which subsequent trees will be created.
     */
    public int pos = Position.NOPOS;

    /** The toplevel tree to which created trees belong.
     */
    public F3Script toplevel;

    /** The current name table. */
    protected Name.Table names;

    /** The current type table. */
    protected F3Types types;

    /** The current symbol table. */
    protected F3Symtab syms;

    /** The current defs table. */
    protected F3Defs defs;

    /** Create a tree maker with null toplevel and NOPOS as initial position.
     */
    protected F3TreeMaker(Context context) {
        context.put(f3TreeMakerKey, this);
        this.pos = Position.NOPOS;
        this.toplevel = null;
        this.names = Name.Table.instance(context);
        this.syms = (F3Symtab)F3Symtab.instance(context);
        this.types = F3Types.instance(context);
        this.defs = F3Defs.instance(context);
    }

    /** Create a tree maker with a given toplevel and FIRSTPOS as initial position.
     */
    protected F3TreeMaker(F3Script toplevel, Name.Table names, F3Types types, F3Symtab syms) {
        this.pos = Position.FIRSTPOS;
        this.toplevel = toplevel;
        this.names = names;
        this.types = types;
        this.syms = syms;
    }

    /** Create a new tree maker for a given toplevel.
     */
    public F3TreeMaker forToplevel(F3Script toplevel) {
        return new F3TreeMaker(toplevel, names, types, syms);
    }

    /** Reassign current position.
     */
    public F3TreeMaker at(int pos) {
        this.pos = pos;
        return this;
    }

    /** Reassign current position.
     */
    public F3TreeMaker at(DiagnosticPosition pos) {
        this.pos = (pos == null ? Position.NOPOS : pos.getStartPosition());
        return this;
    }

    public F3Import Import(F3Expression qualid) {
        F3Import tree = new F3Import(qualid);
        tree.pos = pos;
        return tree;
    }

    public F3Skip Skip() {
        F3Skip tree = new F3Skip();
        tree.pos = pos;
        return tree;
    }

    public F3WhileLoop WhileLoop(F3Expression cond, F3Expression body) {
        F3WhileLoop tree = new F3WhileLoop(cond, body);
        tree.pos = pos;
        return tree;
    }

    public F3Try Try(F3Block body, List<F3Catch> catchers, F3Block finalizer) {
        F3Try tree = new F3Try(body, catchers, finalizer);
        tree.pos = pos;
        return tree;
    }

    public F3Catch ErroneousCatch(List<? extends F3Tree> errs) {
        F3Catch tree = new F3ErroneousCatch(errs);
        tree.pos = pos;
        return tree;
    }
    
    public F3Catch Catch(F3Var param, F3Block body) {
        F3Catch tree = new F3Catch(param, body);
        tree.pos = pos;
        return tree;
    }

    public F3IfExpression Conditional(F3Expression cond,
                                   F3Expression thenpart,
                                   F3Expression elsepart)
    {
        F3IfExpression tree = new F3IfExpression(cond, thenpart, elsepart);
        tree.pos = pos;
        return tree;
    }

    public F3Break Break(Name label) {
        F3Break tree = new F3Break(label, null);
        tree.pos = pos;
        return tree;
    }

    public F3Continue Continue(Name label) {
        F3Continue tree = new F3Continue(label, null);
        tree.pos = pos;
        return tree;
    }

    public F3Return Return(F3Expression expr) {
        F3Return tree = new F3Return(expr);
        tree.pos = pos;
        return tree;
    }

    public F3Throw Throw(F3Expression expr) {
        F3Throw tree = new F3Throw(expr);
        tree.pos = pos;
        return tree;
    }
    public F3Throw ErroneousThrow() {
        F3Throw tree = new F3ErroneousThrow();
        tree.pos = pos;
        return tree;
    }
    public F3FunctionInvocation Apply(List<F3Expression> typeargs,
				      F3Expression fn,
				      List<F3Expression> args)
    {
        F3FunctionInvocation tree = new F3FunctionInvocation(
                typeargs != null? typeargs : List.<F3Expression>nil(),
                fn,
                args != null? args : List.<F3Expression>nil());
        tree.pos = pos;
        return tree;
    }

    public F3Parens Parens(List<F3Expression> expr) {
        F3Parens tree = new F3Parens(expr);
        tree.pos = pos;
        return tree;
    }

    public F3Type.TheType TheType(F3Type expr) {
        F3Type.TheType tree = new F3Type.TheType(expr);
        tree.pos = pos;
        return tree;
    }

    public F3Type RawSequenceType() {
        F3Type.RawSequenceType tree = new F3Type.RawSequenceType();
        tree.pos = pos;
        return tree;
    }

    public F3Expression Tuple(List<F3Expression> expr) {
	System.err.println("tuple: "+ expr);
	if (expr.size() == 1) {
	    return expr.head;
	}
        F3Ident id = Ident(names.fromString("org"));
        F3Select sel = 
            Select(id, names.fromString("f3"), false);
        sel = Select(sel, names.fromString("runtime"), false);
        sel = Select(sel, names.fromString("Pair"), false);
        sel = Select(sel, names.fromString("both"), false);
	F3Expression arg1 = expr.head;
	expr = expr.tail;
	F3Expression arg2 = expr.head;
	expr = expr.tail;
	F3FunctionInvocation result = Apply(null, sel, List.of(arg1, arg2));
	result.immutable = true;
	Name mul = names.fromString("and");
	while (expr.nonEmpty()) {
	    F3Expression arg = expr.head;	    
	    result = Apply(null, Select(result, mul, false), List.of(arg));
	    result.immutable = true;
	    expr = expr.tail;
	}
	System.err.println("tuple: "+ expr + " => "+ result);
        return result;
    }

    public F3Assign Assign(F3Expression lhs, F3Expression rhs) {
        F3Assign tree = new F3Assign(lhs, rhs);
        tree.pos = pos;
        return tree;
    }

    public F3AssignOp Assignop(F3Tag opcode, F3Expression lhs, F3Expression rhs) {
        F3AssignOp tree = new F3AssignOp(opcode, lhs, rhs, null);
        tree.pos = pos;
        return tree;
    }

    public F3Binary Binary(F3Tag opcode, F3Expression lhs, F3Expression rhs) {
        F3Binary tree = new F3Binary(opcode, lhs, rhs, null);
        tree.pos = pos;
        return tree;
    }

    public F3TypeCast TypeCast(F3Tree clazz, F3Expression expr) {
        F3TypeCast tree = new F3TypeCast(clazz, expr);
        tree.pos = pos;
        return tree;
    }

    public F3InstanceOf TypeTest(F3Expression expr, F3Tree clazz) {
        F3InstanceOf tree = new F3InstanceOf(expr, clazz);
        tree.pos = pos;
        return tree;
    }

    public F3Select Select(F3Expression selected, Name selector, boolean nullCheck) {
        F3Select tree = new F3Select(selected, selector, null, nullCheck);
        tree.pos = pos;
        return tree;
    }

    public F3IdentSequenceProxy IdentSequenceProxy(Name name, Symbol sym, F3VarSymbol boundSizeSym) {
        F3IdentSequenceProxy tree = new F3IdentSequenceProxy(name, sym, boundSizeSym);
        tree.pos = pos;
        return tree;
    }

    public F3Ident Ident(Name name) {
        F3Ident tree = new F3Ident(name, null);
        tree.pos = pos;
        return tree;
    }

    public F3ErroneousIdent ErroneousIdent() {
        F3ErroneousIdent tree = new F3ErroneousIdent(List.<F3Tree>nil());
        tree.pos = pos;
        return tree;
    }


    public F3Literal Literal(int tag, Object value) {
        F3Literal tree = new F3Literal(tag, value);
        tree.pos = pos;
        return tree;
    }

    public F3Modifiers Modifiers(long flags) {
        F3Modifiers tree = new F3Modifiers(flags);
        boolean noFlags = (flags & Flags.StandardFlags) == 0;
        tree.pos = (noFlags) ? Position.NOPOS : pos;
        return tree;
    }

    public F3Erroneous Erroneous() {
        return Erroneous(List.<F3Tree>nil());
    }

    public F3Erroneous Erroneous(List<? extends F3Tree> errs) {
        F3Erroneous tree = new F3Erroneous(errs);
        tree.pos = pos;
        return tree;
    }

/* ***************************************************************************
 * Derived building blocks.
 ****************************************************************************/

    /** Create an identifier from a symbol.
     */
    public F3Ident Ident(Symbol sym) {
        F3Ident id = new F3Ident(
                (sym.name != names.empty)
                                ? sym.name
                                : sym.flatName(), sym);
        id.setPos(pos);
        id.setType(sym.type);
        id.sym = sym;
        return id;
    }

    /** Create a selection node from a qualifier tree and a symbol.
     *  @param base   The qualifier tree.
     */
    public F3Expression Select(F3Expression base, Symbol sym, boolean nullCheck) {
        return new F3Select(base, sym.name, sym, nullCheck).setPos(pos).setType(sym.type);
    }

    /** Create an identifier that refers to the variable declared in given variable
     *  declaration.
     */
    public F3Ident Ident(F3Var param) {
        return Ident(param.sym);
    }

    /** Create a list of identifiers referring to the variables declared
     *  in given list of variable declarations.
     */
    public List<F3Expression> Idents(List<F3Var> params) {
        ListBuffer<F3Expression> ids = new ListBuffer<F3Expression>();
        for (List<F3Var> l = params; l.nonEmpty(); l = l.tail)
            ids.append(Ident(l.head));
        return ids.toList();
    }

    /** Create a tree representing the script class of a given enclosing class.
     */
    public F3ClassSymbol ScriptSymbol(Symbol sym) {
        F3ClassSymbol owner = (F3ClassSymbol)sym;
        
        if (owner.scriptSymbol == null) {
            Name scriptName = owner.name.append(defs.scriptClassSuffixName);
            owner.scriptSymbol = new F3ClassSymbol(Flags.STATIC | Flags.PUBLIC, scriptName, owner);
            owner.scriptSymbol.type = new ClassType(Type.noType, List.<Type>nil(), owner.scriptSymbol);
        }
        
        return owner.scriptSymbol;
    }
    public F3Ident Script(Symbol sym) {
        return Ident(ScriptSymbol(sym));
    }

    /** Create a tree representing `this', given its type.
     */
    public F3VarSymbol ThisSymbol(Type t) {
        F3ClassSymbol owner = (F3ClassSymbol)t.tsym;

        if (owner.thisSymbol == null) {
            long flags = FINAL | HASINIT | F3Flags.VARUSE_SPECIAL;
            owner.thisSymbol = new F3VarSymbol(types, names, flags, names._this, t, owner);
        }
        
        return owner.thisSymbol;
    }
    public F3Ident This(Type t) {
        return Ident(ThisSymbol(t));
    }

    /** Create a tree representing `super', given its type and owner.
     */
    public F3VarSymbol SuperSymbol(Type t, TypeSymbol sym) {
        F3ClassSymbol owner = (F3ClassSymbol)sym;

        if (owner.superSymbol == null) {
            long flags = FINAL | HASINIT | F3Flags.VARUSE_SPECIAL;
            owner.superSymbol = new F3VarSymbol(types, names, flags, names._super, t, owner);
        }
        
        return owner.superSymbol;
    }
    public F3Ident Super(Type t, TypeSymbol owner) {
        return Ident(SuperSymbol(t, owner));
    }

    /** Create a tree representing the script instance of the enclosing class.
     */
    public F3VarSymbol ScriptAccessSymbol(Symbol sym) {
        F3ClassSymbol owner = (F3ClassSymbol)sym;

        if (owner.scriptAccessSymbol == null) {
            Name scriptLevelAccessName = defs.scriptLevelAccessField(names, sym);
            F3ClassSymbol script = ScriptSymbol(sym);
            long flags = FINAL | STATIC | PUBLIC | HASINIT | F3Flags.VARUSE_SPECIAL;
            owner.scriptAccessSymbol = new F3VarSymbol(types, names, flags, scriptLevelAccessName, script.type, owner);
        }
        
        return owner.scriptAccessSymbol;
    }
    public F3Ident ScriptAccess(Symbol sym) {
        return Ident(ScriptAccessSymbol(sym));
    }
    
    /**
     * Create a method invocation from a method tree and a list of
     * argument trees.
     */
    public F3FunctionInvocation App(F3Expression meth, List<F3Expression> args) {
        return Apply(null, meth, args).setType(meth.type.getReturnType());
    }

    /**
     * Create a no-arg method invocation from a method tree
     */
    public F3FunctionInvocation App(F3Expression meth) {
        return Apply(null, meth, List.<F3Expression>nil()).setType(meth.type.getReturnType());
    }

    /** Create a tree representing given type.
     */
    public F3Expression Type(Type t) {
	Type ityp = t;
        if (t == null) {
            return null;
        }
	return Type(t, new HashMap<Type, F3Expression>());
    }

    public F3Expression Type(Type t, Map<Type, F3Expression> visited) {
	Type ityp = t;
        if (t == null) {
            return null;
        }
	F3Expression e = visited.get(t);
	if (e != null) return e;
	if (t instanceof TypeVar) {
	    TypeVar tv = (TypeVar)t;
	    if (tv.bound != null) {
		Type bound = tv.bound;
		BoundKind bk = BoundKind.EXTENDS;
		if (bound instanceof WildcardType) {
		    WildcardType wc = (WildcardType)bound;
		    bk = wc.kind;
		    bound = wc.type;
		}
		try {
		    F3Expression ident = Ident(t.tsym.name);
		    visited.put(t, ident);
		    F3Expression exp = 
			TypeVar(ident, Cardinality.SINGLETON, bk, Type(bound, visited));
		    exp.setType(t);
		    return exp;
		} catch (StackOverflowError err) {
		    System.err.println("***TYPE is circular: "+ System.identityHashCode(bound)+": "+t);
		    System.err.println("bound="+System.identityHashCode(bound) + ": "+bound);
		}
	    } else {
		F3Expression exp = 
		    TypeVar(Ident(t.tsym.name), Cardinality.SINGLETON, BoundKind.EXTENDS, null);
		exp.setType(t);
		return exp;
	    }
	} 
	//t = types.erasure(types.normalize(t));
	t = types.normalize(t);
	if (t instanceof CapturedType) {
	    throw new RuntimeException("can't handle captured type:"+t);
	}
	if (t instanceof Type.MethodType) {
	    t = syms.makeFunctionType((Type.MethodType)t);
	    //System.err.println(ityp+ "=>"+t);
	}
	Cardinality tcard = Cardinality.SINGLETON;
	if (types.isSequence(t)) {
	    tcard = Cardinality.ANY;
	    t = types.elementType(t);
	}
	if (t instanceof FunctionType) {
	    FunctionType funType = (FunctionType)t;
	    ListBuffer<F3Type> ts = ListBuffer.<F3Type>lb();
	    for (Type paramType: funType.getParameterTypes()) {
		Cardinality card = Cardinality.SINGLETON;
		if (types.isSequence(paramType)) {
		    paramType = types.elementType(paramType);
		    card = Cardinality.ANY;
		}
		F3Expression expr = Type(paramType, visited);
		F3Type typ = null;
		if (expr instanceof F3Type) {
		    typ = (F3Type)expr;
		} else {
		    typ = TypeClass(expr, card);
		}
		ts.append(typ);
	    }
	    F3Type resTyp = null;
	    Cardinality card = Cardinality.SINGLETON;
	    Type res = funType.getReturnType();
	    if (types.isSequence(res)) {
		res = types.elementType(res);
		card = Cardinality.ANY;
	    }
	    F3Expression resExpr = Type(res, visited);
	    if (resExpr instanceof F3Type) {
		resTyp = (F3Type)resExpr;
	    } else {
		resTyp = TypeClass(resExpr, card);
	    }
	    ListBuffer<F3Expression> typeArgs = ListBuffer.lb();
	    List<Type> targs = funType.typeArgs;
	    if (targs != null) {
		for (Type targ: targs) {
		    typeArgs.append(Type(targ, visited));
		}
	    }
	    F3Type rt = TypeFunctional(ts.toList(),
				       typeArgs.toList(),
				       resTyp, tcard);
	    rt.setType(ityp);
	    return rt;
	}
        F3Expression tp;
        switch (t.tag) {
            case FLOAT: 
                tp = Ident(syms.floatTypeName);
                break;
            case DOUBLE:
                tp = Ident(syms.doubleTypeName);
                break;
            case CHAR:
                tp = Ident(syms.charTypeName);
                break;
            case BYTE:
                tp = Ident(syms.byteTypeName);
                break;
            case SHORT:
                tp = Ident(syms.shortTypeName);
                break;
            case INT:
                tp = Ident(syms.integerTypeName);
                break;
            case LONG:
                tp = Ident(syms.longTypeName);
                break;
            case BOOLEAN:
                tp = Ident(syms.booleanTypeName);
                break;
            case VOID:
                tp = Ident(syms.voidTypeName);
		break;
	    case WILDCARD:
		{
		    WildcardType w = (WildcardType)t;
		    if (w.kind != BoundKind.UNBOUND) {
			tp = Type(w.type, visited);
		    } else {
			tp = TypeExists();
		    }
		}
		break;
            case TYPEVAR:
		{
		    tp = Ident(((TypeVar)t).tsym.name);
		}
                break;
            case ARRAY:
                F3Expression elem = Type(types.elemtype(t), visited);
                elem = elem instanceof F3Type ? elem : TypeClass(elem, Cardinality.SINGLETON);
                tp = TypeArray((F3Type)elem);
                break;
            case CLASS:
		List<Type> targs = ityp.getTypeArguments();
                Type outer = t.getEnclosingType();
                tp = outer.tag == CLASS && t.tsym.owner.kind == TYP
		    ? Select(Type(outer, visited), t.tsym, false)
                        : QualIdent(t.tsym);
		if (targs.size() > 0) {
		    ListBuffer<F3Expression> typeArgs = ListBuffer.<F3Expression>lb();
		    for (Type targ: targs) {
			typeArgs.append(Type(targ, visited));
		    }
		    if (tp instanceof F3Ident) {
			((F3Ident) tp).typeArgs = typeArgs.toList();
		    } else if (tp instanceof F3Select) {
			((F3Select) tp).typeArgs = typeArgs.toList();
		    }
		}
                break;
            default:
                throw new AssertionError("unexpected type: " + t.getClass()+": "+t);
        }
        return tp.setType(ityp);
    }

    /** Create a list of trees representing given list of types.
     */
    public List<F3Expression> Types(List<Type> ts) {
        ListBuffer<F3Expression> typeList = new ListBuffer<F3Expression>();
        for (List<Type> l = ts; l.nonEmpty(); l = l.tail)
            typeList.append(Type(l.head));
        return typeList.toList();
    }

    public F3Literal LiteralInteger(String text, int radix) {
        long longVal = Convert.string2long(text, radix);
        // For decimal, allow Integer negative numbers
        if ((radix==10)? (longVal <= Integer.MAX_VALUE && longVal >= Integer.MIN_VALUE) : ((longVal & ~0xFFFFFFFFL) == 0L))
            return Literal(TypeTags.INT, Integer.valueOf((int) longVal));
        else
            return Literal(TypeTags.LONG, Long.valueOf(longVal));
    }

    public F3Literal Literal(Object value) {
        F3Literal result = null;
        if (value instanceof String) {
            result = Literal(CLASS, value).
                setType(syms.stringType.constType(value));
        } else if (value instanceof Integer) {
            result = Literal(INT, value).
                setType(syms.intType.constType(value));
        } else if (value instanceof Long) {
            result = Literal(LONG, value).
                setType(syms.longType.constType(value));
        } else if (value instanceof Byte) {
            result = Literal(BYTE, value).
                setType(syms.byteType.constType(value));
        } else if (value instanceof Character) {
            result = Literal(CHAR, value).
                setType(syms.charType.constType(value));
        } else if (value instanceof Double) {
            result = Literal(DOUBLE, value).
                setType(syms.doubleType.constType(value));
        } else if (value instanceof Float) {
            result = Literal(FLOAT, value).
                setType(syms.floatType.constType(value));
        } else if (value instanceof Short) {
            result = Literal(SHORT, value).
                setType(syms.shortType.constType(value));
        } else if (value instanceof Boolean) {
            result = Literal(BOOLEAN, ((Boolean) value).booleanValue() ? 1 : 0).
                setType(syms.booleanType.constType(value));
        } else {
            throw new AssertionError(value);
        }
        return result;
    }

    /** Make an attributed type cast expression.
     */
    public F3TypeCast TypeCast(Type type, F3Expression expr) {
        return (F3TypeCast)TypeCast(Type(type), expr).setType(type);
    }

/* ***************************************************************************
 * Helper methods.
 ****************************************************************************/

    /** Can given symbol be referred to in unqualified form?
     */
    boolean isUnqualifiable(Symbol sym) {
        if (sym.name == names.empty ||
            sym.owner == null ||
            sym.owner.kind == MTH || sym.owner.kind == VAR) {
            return true;
        } else if (sym.kind == TYP && toplevel != null) {
            Scope.Entry e;
            e = toplevel.namedImportScope.lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
            e = toplevel.packge.members().lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
            e = toplevel.starImportScope.lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
        }
        return false;
    }

    /** The name of synthetic parameter number `i'.
     */
    public Name paramName(int i)   { return names.fromString("x" + i); }

    public F3ClassDeclaration ClassDeclaration(F3Modifiers mods,
            Name name,
            List<F3Expression> supertypes,
            List<F3Tree> declarations) {
        F3ClassDeclaration tree = new F3ClassDeclaration(mods,
                name,
                supertypes,
                declarations,
                null);
        tree.pos = pos;

        return tree;
    }

    public F3Block Block(long flags, List<F3Expression> stats, F3Expression value) {
        F3Block tree = new F3Block(flags, stats, value);
        tree.pos = pos;
        return tree;
    }

    public F3Block ErroneousBlock() {
        F3ErroneousBlock tree = new F3ErroneousBlock(List.<F3Tree>nil());
        tree.pos = pos;
        return tree;
    }
    public F3Block ErroneousBlock(List<? extends F3Tree> errs) {
        F3ErroneousBlock tree = new F3ErroneousBlock(errs);
        tree.pos = pos;
        return tree;
    }

    public F3FunctionValue InferredExpr(List<F3Var> params,
					F3Expression exp) {
	F3FunctionValue fv = 
	    FunctionValue(Modifiers(0L),
			  List.<F3Expression>nil(), 
			  TypeUnknown(), 
			  params, 
			  Block(0L, List.<F3Expression>nil(), 
				exp));
	fv.infer = true;
	return fv;
    }
        
    public F3FunctionDefinition FunctionDefinition(
            F3Modifiers modifiers,
            Name name,
            F3Type restype,
            List<F3Var> params,
            F3Block bodyExpression) {
        // hack until backend is fixed
        boolean needsTransform = (modifiers.flags & F3Flags.BOUND) != 0;
        if (params.size() == 0) {
            needsTransform = false;
        } else {
            if (params.head.name.toString().endsWith("$0")) {
                // already done apparently
                needsTransform = false;
            }
        }
        if (needsTransform) {
            // rename parameters and create local variables bound to them
            ListBuffer<F3Var> newParams = ListBuffer.lb();
            ListBuffer<F3Expression> stmts = ListBuffer.lb();
            for (F3Var f3Var : params) {
                F3Var paramVar = Var(
                                         names.fromString(f3Var.name+"$0"),
                                         f3Var.getF3Type(),
                                         Modifiers(f3Var.mods.flags),
                                         f3Var.getInitializer(),
                                         f3Var.getBindStatus(), null, null);
                newParams.append(paramVar);
                F3Var localVar = Var(
                                         f3Var.name,
                                         f3Var.getF3Type(),
                                         Modifiers(f3Var.mods.flags & ~Flags.PARAMETER),
                                         Ident(paramVar.name),
                                         F3BindStatus.UNIDIBIND, null, null);
                localVar.type = f3Var.type;
                localVar.sym = f3Var.sym;
                stmts.append(localVar);
            }
            stmts.appendList(bodyExpression.stats);
            bodyExpression.stats = stmts.toList();
            params = newParams.toList();
        }
        F3FunctionDefinition tree = new F3FunctionDefinition(
                modifiers,
                name,
                restype,
                params,
                bodyExpression);
        tree.operation.definition = tree;
        tree.pos = pos;
        return tree;
    }

    public F3FunctionValue FunctionValue(
            F3Modifiers mods,
	    List<F3Expression> typeArgs,
            F3Type restype,
	    List<F3Var> params,
            F3Block bodyExpression) {
        F3FunctionValue tree = new F3FunctionValue(
                mods,
                restype,
                params,
                bodyExpression);
	tree.typeArgs = typeArgs;
        tree.pos = pos;
        return tree;
    }

    public F3FunctionValue FunctionValue(
            F3Modifiers mods,
            F3Type restype,
	    List<F3Var> params,
            F3Block bodyExpression) {
	return FunctionValue(mods, null, restype, params, bodyExpression);
    }

    public F3InitDefinition InitDefinition(
            F3Block body) {
        F3InitDefinition tree = new F3InitDefinition(
                body);
        tree.pos = pos;
        return tree;
    }

    public F3PostInitDefinition PostInitDefinition(F3Block body) {
        F3PostInitDefinition tree = new F3PostInitDefinition(body);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceEmpty EmptySequence() {
        F3SequenceEmpty tree = new F3SequenceEmpty();
        tree.pos = pos;
        return tree;
    }

    public F3SequenceRange RangeSequence(F3Expression lower, F3Expression upper, F3Expression stepOrNull, boolean exclusive) {
        F3SequenceRange tree = new F3SequenceRange(lower, upper,  stepOrNull,  exclusive);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceExplicit ExplicitSequence(List<F3Expression> items) {
        F3SequenceExplicit tree = new F3SequenceExplicit(items);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceIndexed SequenceIndexed(F3Expression sequence, F3Expression index) {
        F3SequenceIndexed tree = new F3SequenceIndexed(sequence, index);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceSlice SequenceSlice(F3Expression sequence, F3Expression firstIndex, F3Expression lastIndex, int endKind) {
        F3SequenceSlice tree = new F3SequenceSlice(sequence, firstIndex,
                lastIndex, endKind);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceInsert SequenceInsert(F3Expression sequence, F3Expression element, F3Expression position, boolean after) {
        F3SequenceInsert tree = new F3SequenceInsert(sequence, element, position, after);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceDelete SequenceDelete(F3Expression sequence) {
        F3SequenceDelete tree = new F3SequenceDelete(sequence, null);
        tree.pos = pos;
        return tree;
    }

    public F3SequenceDelete SequenceDelete(F3Expression sequence, F3Expression element) {
        F3SequenceDelete tree = new F3SequenceDelete(sequence, element);
        tree.pos = pos;
        return tree;
    }

    public F3StringExpression StringExpression(List<F3Expression> parts,
                                        String translationKey) {
        F3StringExpression tree = new F3StringExpression(parts, translationKey);
        tree.pos = pos;
        return tree;
    }

    public F3Instanciate Instanciate(F3Kind kind, F3Expression clazz, F3ClassDeclaration def, List<F3Expression> args, List<F3ObjectLiteralPart> parts, List<F3Var> localVars) {
        F3Instanciate tree = new F3Instanciate(kind, clazz, def, args, parts, localVars, null);
        tree.pos = pos;
        return tree;
    }

    public F3Instanciate ObjectLiteral(F3Expression ident,
            List<F3Tree> defs) {
        return Instanciate(F3Kind.INSTANTIATE_OBJECT_LITERAL,
                ident,
                List.<F3Expression>nil(),
                defs);
    }

    public F3Instanciate WithObjectLiteral(F3Expression ident,
            List<F3Tree> defs) {
        return Instanciate(F3Kind.WITH_OBJECT_LITERAL,
                ident,
                List.<F3Expression>nil(),
                defs);
    }

    public F3Instanciate InstanciateNew(F3Expression ident,
					List<F3Expression> args) {
        return Instanciate(F3Kind.INSTANTIATE_NEW,
                ident,
                args != null? args : List.<F3Expression>nil(),
                List.<F3Tree>nil());
    }

   public F3Instanciate Instanciate(F3Kind kind, F3Expression ident,
           List<F3Expression> args,
           List<F3Tree> defs) {

       // Don't try and process object literals that have erroneous elements
       //
       if  (ident instanceof F3Erroneous) return null;

       ListBuffer<F3ObjectLiteralPart> partsBuffer = ListBuffer.lb();
       ListBuffer<F3Tree> defsBuffer = ListBuffer.lb();
       ListBuffer<F3Expression> varsBuffer = ListBuffer.lb();
       boolean boundParts = false;
       if (defs != null) {
           for (F3Tree def : defs) {
               if (def instanceof F3ObjectLiteralPart) {
                   F3ObjectLiteralPart olp = (F3ObjectLiteralPart) def;
                   partsBuffer.append(olp);
                   boundParts |= olp.isExplicitlyBound();
               } else if (def instanceof F3Var /* && ((F3Var)def).isLocal()*/) {
                   // for now, at least, assume any var declaration inside an object literal is local
                   varsBuffer.append((F3Var) def);
               } else {
                   defsBuffer.append(def);
               }
           }
       }
       F3ClassDeclaration klass = null;
       if (defsBuffer.size() > 0 || boundParts) {
           F3Expression id = ident;
           while (id instanceof F3Select) id = ((F3Select)id).getExpression();
           Name cname = objectLiteralClassName(((F3Ident)id).getName());
           long innerClassFlags = Flags.SYNTHETIC | Flags.FINAL; // to enable, change to Flags.FINAL
	   
           klass = this.ClassDeclaration(this.Modifiers(innerClassFlags), cname, List.<F3Expression>of(ident), defsBuffer.toList());
	   //klass.typeArgs = F3TreeInfo.typeArgs(ident);
	   //System.err.println("set type args "+F3TreeInfo.typeArgs(ident)+" on "+cname);
       }

       F3Instanciate tree = new F3Instanciate(kind, ident,
               klass,
               args==null? List.<F3Expression>nil() : args,
               partsBuffer.toList(),
               List.convert(F3Var.class, varsBuffer.toList()),
               null);
       tree.pos = pos;
       return tree;
   }

    public F3ObjectLiteralPart ObjectLiteralPart(
            Name attrName,
            F3Expression expr,
            F3BindStatus bindStatus) {
        F3ObjectLiteralPart tree =
                new F3ObjectLiteralPart(attrName, expr, bindStatus, null);
        tree.pos = pos;
        return tree;
    }

    public F3Type  TypeAny(Cardinality cardinality) {
        F3Type tree = new F3TypeAny(cardinality);
        tree.pos = pos;
        return tree;
    }
    
    public F3Type  ErroneousType() {
        F3Type tree = new F3ErroneousType(List.<F3Tree>nil());
        tree.pos = pos;
        return tree;
    }

    public F3Type  ErroneousType(List<? extends F3Tree> errs) {
        F3Type tree = new F3ErroneousType(errs);
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeUnknown() {
        F3Type tree = new F3TypeUnknown();
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeClass(F3Expression className,Cardinality cardinality) {
        return TypeClass(className, cardinality, null);
    }

    public F3Type  TypeClass(F3Expression className,Cardinality cardinality, ClassSymbol sym) {
        F3Type tree = new F3TypeClass(className, cardinality, sym);
        tree.pos = pos;
        return tree;
    }

    public F3Expression TypeApply(F3Expression expr, List<F3Expression> typeArgs) {
	// hack fix me!!!
	if (expr instanceof F3Ident) {
	    ((F3Ident)expr).typeArgs = typeArgs;
	} else if (expr instanceof F3Select) {
	    ((F3Select)expr).typeArgs = typeArgs;
	} else {
	    System.err.println("unhandled case: type apply: " +expr);
	}
	return expr;
    }

    public F3Expression PartialApply(F3Expression expr, List<F3Expression> args) {
        F3FunctionInvocation tree = new F3FunctionInvocation(
                List.<F3Expression>nil(),
                expr,
                args != null? args : List.<F3Expression>nil());
	tree.partial = true;
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeVar(F3Expression className,Cardinality cardinality) {
        return TypeVar(className, cardinality, null);
    }

    public F3Type TypeVar(F3Expression className, Cardinality card, BoundKind boundKind, F3Expression bound) {
        F3Type tree = new F3TypeVar(className, card, boundKind, bound);
        tree.pos = pos;
	//System.err.println("typevar="+tree);
	//System.err.println("bk="+boundKind);
	//System.err.println("bound="+bound);
	if (bound instanceof F3Type) {
	    ((F3Type)bound).boundKind = boundKind;
	}
        return tree;
    }

    public F3Type TypeVar(F3Expression className,Cardinality cardinality, TypeSymbol sym) {
        F3Type tree = new F3TypeVar(className, cardinality, sym);
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeCons(F3Expression className,Cardinality cardinality, List<F3Expression> args) {
        return TypeCons(className, cardinality, args, null);
    }

    public F3Type TypeApply(F3Expression className, Cardinality cardinality, List<F3Expression> args) {
        F3Type tree = new F3Type.TypeApply(className, cardinality, args);
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeExists() {
	return new F3TypeExists();
    }

    public F3Type TypeThis(Cardinality cardinality, List<F3Expression> args) {
        F3Type tree = new F3TypeThis(Identifier("This"), cardinality, args, null);
	tree.pos = pos;
	return tree;
    }

    public F3Type TypeCons(F3Expression className,Cardinality cardinality, List<F3Expression> args, TypeSymbol sym) {
        F3Type tree = new F3TypeCons(className, cardinality, args,sym);
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeAlias(Name name,
			    List<F3Expression> typeArgs,
			    F3Type type) 
    {
	F3Type tree = new F3TypeAlias(name, typeArgs, type);
	tree.pos = pos;
	return tree;
    }

    public F3Type TypeFunctional(List<F3Type> params,
				 F3Type restype,
				 Cardinality cardinality) {
	return TypeFunctional(params, List.<F3Expression>nil(), restype, cardinality);
    }

    public F3Type TypeFunctional(List<F3Type> params,
				 List<F3Expression> typeargs,
				 F3Type restype,

            Cardinality cardinality) {
        F3TypeFunctional tree = new F3TypeFunctional(params,
						     restype,
						     cardinality);
	tree.typeArgs = typeargs;
        tree.pos = pos;
        return tree;
    }

    public F3Type TypeArray(F3Type elementType) {
        F3Type tree = new F3TypeArray(elementType);
        tree.pos = pos;
        return tree;
    }

    public F3OverrideClassVar TriggerWrapper(F3Ident expr, F3OnReplace onReplace, F3OnReplace onInvalidate) {
        F3OverrideClassVar tree = new F3OverrideClassVar(null, null, null, expr, null, null, onReplace, onInvalidate, null);
        tree.pos = pos;
        return tree;
    }
    
   public F3OnReplace ErroneousOnReplace(List<? extends F3Tree> errs) {
        F3OnReplace tree = new F3ErroneousOnReplace(errs, F3OnReplace.Kind.ONREPLACE);
        tree.pos = pos;
        return tree;
    }

   public F3OnReplace ErroneousOnInvalidate(List<? extends F3Tree> errs) {
        F3OnReplace tree = new F3ErroneousOnReplace(errs, F3OnReplace.Kind.ONINVALIDATE);
        tree.pos = pos;
        return tree;
    }

    public F3OnReplace OnReplace(F3Var oldValue, F3Block body) {
        F3OnReplace tree = new F3OnReplace(oldValue, body, F3OnReplace.Kind.ONREPLACE);
        tree.pos = pos;
        return tree;
    }

     public F3OnReplace OnReplace(F3Var oldValue, F3Var firstIndex,
             F3Var lastIndex, F3Var newElements, F3Block body) {
         return OnReplace(oldValue, firstIndex, lastIndex,
                 F3SequenceSlice.END_INCLUSIVE, newElements, body);
    }

     public F3OnReplace OnReplace(F3Var oldValue, F3Var firstIndex,
             F3Var lastIndex, int endKind, F3Var newElements, F3Block body) {
         F3OnReplace tree = OnReplace(oldValue, firstIndex, lastIndex,
                 endKind, newElements, null, body);
        tree.pos = pos;
        return tree;
     }

     public F3OnReplace OnReplace(F3Var oldValue, F3Var firstIndex,
             F3Var lastIndex, int endKind, F3Var newElements, F3Var saveVar, F3Block body) {
         F3OnReplace tree = new F3OnReplace(oldValue, firstIndex, lastIndex,
                 endKind, newElements, saveVar, body, F3OnReplace.Kind.ONREPLACE);
        tree.pos = pos;
        return tree;
     }

     public F3OnReplace OnInvalidate(F3Block body) {
        F3OnReplace tree = new F3OnReplace(null, body, F3OnReplace.Kind.ONINVALIDATE);
        tree.pos = pos;
        return tree;
    }

     public F3VarInit VarInit(F3Var var) {
         F3VarInit tree = new F3VarInit(var);
         tree.pos = (var==null) ? Position.NOPOS : var.pos;
         return tree;
     }

     public F3VarRef VarRef(F3Expression expr, F3VarRef.RefKind kind) {
         F3VarRef tree = new F3VarRef(expr, kind);
         tree.pos = pos;
         return tree;
     }

    public F3Var Var(Name name,
            F3Type type,
            F3Modifiers mods,
            F3Expression initializer,
            F3BindStatus bindStatus,
            F3OnReplace onReplace,
            F3OnReplace onInvalidate) {
	if (initializer instanceof F3Select) {
	    if (bindStatus == UNIDIBIND) {
		//bindStatus = BIDIBIND;
	    }
	}
	F3Var tree = new F3Var(name, type,
			       mods, initializer, bindStatus, onReplace, onInvalidate, null);
        tree.pos = pos;
        return tree;
    }
    public F3OverrideClassVar OverrideClassVar(Name name, F3Type type, F3Modifiers mods, F3Ident expr,
            F3Expression initializer,
            F3BindStatus bindStatus,
            F3OnReplace onReplace,
            F3OnReplace onInvalidate) {
        F3OverrideClassVar tree = new F3OverrideClassVar(name, type, mods, expr, initializer,
                bindStatus, onReplace, onInvalidate, null);
        tree.pos = pos;
        return tree;
    }

    public F3Var Param(Name name,
            F3Type type) {
        F3Var tree = new F3Var(name, type,
                Modifiers(Flags.PARAMETER), null, F3BindStatus.UNBOUND, null, null, null);
        tree.pos = pos;
        return tree;
    }

    public F3ForExpression ForExpression(
            List<F3ForExpressionInClause> inClauses,
            F3Expression bodyExpr) {
        F3ForExpression tree = new F3ForExpression(F3Kind.FOR_EXPRESSION_FOR, inClauses, bodyExpr);
        tree.pos = pos;
        return tree;
    }

    public F3ForExpression Predicate(
            List<F3ForExpressionInClause> inClauses,
            F3Expression bodyExpr) {
        F3ForExpression tree = new F3ForExpression(F3Kind.FOR_EXPRESSION_PREDICATE, inClauses, bodyExpr);
        tree.pos = pos;
        return tree;
    }

    public F3ForExpressionInClause InClause(
            F3Var var,
            F3Expression seqExpr,
            F3Expression whereExpr) {
	return InClause(var, seqExpr, whereExpr, null);
    }

    public F3ForExpressionInClause InClause(
            F3Var var,
            F3Expression seqExpr,
            F3Expression whereExpr,
	    F3Var fromVar) {
        F3ForExpressionInClause tree = new F3ForExpressionInClause(var, seqExpr, whereExpr, fromVar);
        tree.pos = pos;
        return tree;
    }
    
    public F3ErroneousForExpressionInClause ErroneousInClause(List<? extends F3Tree> errs) {

        F3ErroneousForExpressionInClause tree = new F3ErroneousForExpressionInClause(errs);
        tree.pos = pos;
        return tree;
    }


    public F3Expression BottomType() {
	return TypeClass(Literal(TypeTags.BOT, null), 
			 Cardinality.SINGLETON, 
			 (ClassSymbol)syms.botType.tsym);
    }

    public F3Expression TupleType(F3Expression first, F3Expression second) {
	// hack...
	System.err.println(first.getClass());
	System.err.println(second.getClass());
	if (first instanceof F3Type) {
	    ((F3Type)first).boundKind = BoundKind.EXTENDS;
	}
	if (second instanceof F3Type) {
	    ((F3Type)second).boundKind = BoundKind.EXTENDS;
	}
        F3Ident id = Ident(names.fromString("org"));
        F3Select sel = 
            Select(id, names.fromString("f3"), false);
        sel = Select(sel, names.fromString("runtime"), false);
        sel = Select(sel, names.fromString("Pair"), false);
	return Ident(sel, List.of(first, second));
    }

    public F3Expression Ident(F3Expression name, List<F3Expression> typeVars) {
	if (typeVars.head != null) {
	    if (name instanceof F3Ident) {
		((F3Ident)name).typeArgs = typeVars;
	    } else if (name instanceof F3Select) {
		((F3Select)name).typeArgs = typeVars;
	    } else {
		System.err.println("unhandled Ident case: "+name);
	    }
	}
	return name;
    }

    public F3Expression Identifier(Name name) {
        String str = name.toString();
        if (str.indexOf('.') < 0 && str.indexOf('<') < 0) {
            return Ident(name);
        }
        return Identifier(str);
    }

    public F3Expression Identifier(String str) {
        assert str.indexOf('<') < 0 : "attempt to parse a type with 'Identifier'.  Use TypeTree";
        F3Expression tree = null;
        int inx;
        int lastInx = 0;
        do {
            inx = str.indexOf('.', lastInx);
            int endInx;
            if (inx < 0) {
                endInx = str.length();
            } else {
                endInx = inx;
            }
            String part = str.substring(lastInx, endInx);
            Name partName = names.fromString(part);
            tree = tree == null?
                Ident(partName) :
                Select(tree, partName, false);
            lastInx = endInx + 1;
        } while (inx >= 0);
        return tree;
    }

    public F3InterpolateValue InterpolateValue(F3Expression attr, F3Expression v, F3Expression interp) {
        F3InterpolateValue tree = new F3InterpolateValue(attr, v, interp);
        tree.pos = pos;
        return tree;
    }

    public F3Invalidate Invalidate(F3Expression var) {
        F3Invalidate tree = new F3Invalidate(var);
        tree.pos = pos;
        return tree;
    }
     
    public F3Indexof Indexof (F3Ident name) {
        F3Indexof tree = new F3Indexof(name);
        tree.pos = pos;
        return tree;
    }

    public F3TimeLiteral TimeLiteral(String str) {
        int i = 0;
        char[] buf = str.toCharArray();

        // Locate the duration specifier.
        //
        while (i < buf.length && (Character.isDigit(buf[i]) || buf[i] == '.' || buf[i] == 'e' || buf[i] == 'E'))
            i++;

        assert i > 0;               // lexer should only pass valid time strings
        assert buf.length - i > 0;  // lexer should only pass valid time strings

        String dur = str.substring(i);
        Duration duration =
                dur.equals("ms") ? Duration.MILLIS :
                dur.equals("s") ? Duration.SECONDS :
                dur.equals("m") ? Duration.MINUTES :
                dur.equals("h") ? Duration.HOURS : null;
        assert duration != null;
        Object timeVal;
        Double value;
        try {

            // Extract the literal value up to but excluding the duration
            // specifier.
            //
            String s = str.substring(0, i);

            // Even though the number of hours/mounts/etc may be specified
            // as an integer, we still need to use a double value always because
            // durations such as 999999m will overflow an integer.
            //
            value = Double.valueOf(s) * duration.getMultiplier();

            // Now use an integer if we will not overflow the maximum vlaue
            // for an integer and the value is an integer number.
            //
            if  (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE && value == value.intValue()) {
                timeVal = new Integer(value.intValue());
            }
            else
            {
                // Has to stay as a double or it would overflow, or it was
                // not an integer vlaue, such as 5.5m
                //
                timeVal = value;
            }
        }
        catch (NumberFormatException ex) {
            // error already reported in scanner
            timeVal = Double.NaN;
        }
        F3Literal literal = Literal(timeVal);
        F3TimeLiteral tree = new F3TimeLiteral(literal, duration);
        tree.pos = pos;
        return tree;
    }

    public F3TimeLiteral TimeLiteral(F3Literal literal, Duration duration) {
        F3TimeLiteral tree = new F3TimeLiteral(literal, duration);
        tree.pos = pos;
        return tree;
    }

    public F3ErroneousTimeLiteral ErroneousTimeLiteral() {
        F3ErroneousTimeLiteral tree = new F3ErroneousTimeLiteral(List.<F3Tree>nil());
        tree.pos = pos;

        return tree;
    }

    public F3LengthLiteral LengthLiteral(String str) {
        int i = 0;
        char[] buf = str.toCharArray();

        // Locate the length specifier. (also swallows the 'e' in "em")
        //
        while (i < buf.length && (Character.isDigit(buf[i]) || buf[i] == '.' || buf[i] == 'e' || buf[i] == 'E'))
            i++;
        
        if (str.substring(i).equals("m")) {
            // ate the 'e' in "em", backup one.
            i--;
        }

        assert i > 0;               // lexer should only pass valid length strings
        assert buf.length - i > 0;  // lexer should only pass valid length strings

        String u = str.substring(i);
        LengthUnit units =
                u.equals("in") ? LengthUnit.INCH :
                u.equals("cm") ? LengthUnit.CENTIMETER :
                u.equals("mm") ? LengthUnit.MILLIMETER :
                u.equals("pt") ? LengthUnit.POINT :
                u.equals("pc") ? LengthUnit.PICA :
                u.equals("em") ? LengthUnit.EM :
                u.equals("px") ? LengthUnit.PIXEL :
                u.equals("dp") ? LengthUnit.DENSITY_INDEPENDENT_PIXEL :
                u.equals("sp") ? LengthUnit.SCALE_INDEPENDENT_PIXEL :
                u.equals("%") ? LengthUnit.PERCENTAGE : null;
        assert units != null : "unknown unit: '" + u + "'";
        Object lengthVal;
        Double value;
        try {

            // Extract the literal value up to but excluding the length
            // specifier.
            //
            String s = str.substring(0, i);
            value = Double.valueOf(s);

            // Now use an integer if we will not overflow the maximum vlaue
            // for an integer and the value is an integer number.
            //
            if  (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE && value == value.intValue()) {
                lengthVal = new Integer(value.intValue());
            }
            else
            {
                // Has to stay as a double or it would overflow, or it was
                // not an integer vlaue, such as 5.5mm
                //
                lengthVal = value;
            }
        }
        catch (NumberFormatException ex) {
            // error already reported in scanner
            lengthVal = Double.NaN;
        }
        F3Literal literal = Literal(lengthVal);
        F3LengthLiteral tree = new F3LengthLiteral(literal, units);
        tree.pos = pos;
        return tree;
    }

    public F3LengthLiteral LengthLiteral(F3Literal literal, LengthUnit units) {
        F3LengthLiteral tree = new F3LengthLiteral(literal, units);
        tree.pos = pos;
        return tree;
    }

    public F3ErroneousLengthLiteral ErroneousLengthLiteral() {
        F3ErroneousLengthLiteral tree = new F3ErroneousLengthLiteral(List.<F3Tree>nil());
        tree.pos = pos;

        return tree;
    }

    public F3AngleLiteral AngleLiteral(String str) {
        int i = 0;
        char[] buf = str.toCharArray();

        // Locate the angle specifier.
        //
        while (i < buf.length && (Character.isDigit(buf[i]) || buf[i] == '.' || buf[i] == 'e' || buf[i] == 'E'))
            i++;

        assert i > 0;               // lexer should only pass valid angle strings
        assert buf.length - i > 0;  // lexer should only pass valid angle strings

        String u = str.substring(i);
        AngleUnit units =
                u.equals("deg") ? AngleUnit.DEGREE :
                u.equals("rad") ? AngleUnit.RADIAN :
                u.equals("turn") ? AngleUnit.TURN : null;
        assert units != null;
        Object angleVal;
        Double value;
        try {

            // Extract the literal value up to but excluding the angle
            // specifier.
            //
            String s = str.substring(0, i);
            value = Double.valueOf(s);

            // Now use an integer if we will not overflow the maximum vlaue
            // for an integer and the value is an integer number.
            //
            if  (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE && value == value.intValue()) {
                angleVal = new Integer(value.intValue());
            }
            else
            {
                // Has to stay as a double or it would overflow, or it was
                // not an integer vlaue, such as 5.5mm
                //
                angleVal = value;
            }
        }
        catch (NumberFormatException ex) {
            // error already reported in scanner
            angleVal = Double.NaN;
        }
        F3Literal literal = Literal(angleVal);
        F3AngleLiteral tree = new F3AngleLiteral(literal, units);
        tree.pos = pos;
        return tree;
    }

    public F3AngleLiteral AngleLiteral(F3Literal literal, AngleUnit units) {
        F3AngleLiteral tree = new F3AngleLiteral(literal, units);
        tree.pos = pos;
        return tree;
    }

    public F3ErroneousAngleLiteral ErroneousAngleLiteral() {
        F3ErroneousAngleLiteral tree = new F3ErroneousAngleLiteral(List.<F3Tree>nil());
        tree.pos = pos;

        return tree;
    }

    public F3ColorLiteral ColorLiteral(String str) {
        // valid strings: #rgb, #rrggbb, #rgb|a, or #rrggbb|aa
        String color;
        String alpha = null;
        switch (str.length()) {
            case 4:
                color = str.substring(1);
                break;
            case 7:
                color = str.substring(1);
                break;
            case 6:
                color = str.substring(1, 4);
                alpha = str.substring(5);
                break;
            case 10:
                color = str.substring(1, 7);
                alpha = str.substring(8);
                break;
            default:
                throw new IllegalStateException("malformed color literal string: " + str);
        }
        int colorVal = Integer.parseInt(color, 16);
        int alphaVal = alpha == null ? 0xFF : Integer.parseInt(alpha, 16);
        switch (str.length()) {
            case 6:
                alphaVal |= alphaVal << 4;
            case 4:
                int r = (colorVal >> 8) & 0xF; r |= r << 4;
                int g = (colorVal >> 4) & 0xF; g |= g << 4;
                int b = colorVal & 0xF; b |= b << 4;
                colorVal = r << 16 | g << 8 | b;
        }
        F3Literal literal = Literal(colorVal | alphaVal << 24);
        F3ColorLiteral tree = new F3ColorLiteral(literal);
        tree.pos = pos;
        return tree;
    }

    public F3ColorLiteral ColorLiteral(F3Literal literal) {
        F3ColorLiteral tree = new F3ColorLiteral(literal);
        tree.pos = pos;
        return tree;
    }

    public F3ErroneousColorLiteral ErroneousColorLiteral() {
        F3ErroneousColorLiteral tree = new F3ErroneousColorLiteral(List.<F3Tree>nil());
        tree.pos = pos;

        return tree;
    }

    public F3KeyFrameLiteral KeyFrameLiteral(F3Expression start, List<F3Expression> values, F3Expression trigger) {
        F3KeyFrameLiteral tree = new F3KeyFrameLiteral(start, values, trigger);
        tree.pos = pos;
        return tree;
    }

    public F3Unary Unary(F3Tag opcode, F3Expression arg) {
        F3Unary tree = new F3Unary(opcode, arg);
        tree.pos = pos;
        return tree;
    }

    private int syntheticClassNumber = 0;

    Name syntheticClassName(Name superclass, String infix) {
        return names.fromString(superclass.toString() + infix + ++syntheticClassNumber);
    }

    Name objectLiteralClassName(Name superclass) {
        return syntheticClassName(superclass, F3Defs.objectLiteralClassInfix);
    }

    /**
     * Clone of javac's F3TreeMaker.Script, minus the assertion check of defs types.
     */
    public F3Script Script(F3Expression pid,
                                      List<F3Tree> defs) {
        F3Script tree = new F3Script(pid, defs,
                                     null, null, null, null);
        tree.pos = pos;
        return tree;
    }

    public F3Expression QualIdent(Symbol sym) {
	if (sym.kind ==Kinds.PCK && sym.owner == syms.rootPackage)
	    return Ident(sym);
        return isUnqualifiable(sym)
            ? Ident(sym)
            : Select(QualIdent(sym.owner), sym, false);
    }
}
