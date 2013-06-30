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
import org.f3.api.tree.TypeTree.Cardinality;
import org.f3.tools.code.FunctionType;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.*;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.ClassType;
import com.sun.tools.mjavac.code.Type.TypeVar;
import com.sun.tools.mjavac.code.Type.WildcardType;
import com.sun.tools.mjavac.code.Type.CapturedType;
import static com.sun.tools.mjavac.code.TypeTags.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.Options;
import com.sun.tools.mjavac.code.BoundKind;
import javax.tools.JavaFileObject;

/**
 * Shared support for the pre-translation passes.  Not a pass itself.
 *
 * @author Maurizio Cimadamore
 * @author Robert Field
 */
public class F3PreTranslationSupport {

    private final F3TreeMaker f3make;
    private final F3Defs defs;
    private final Name.Table names;
    private final F3Check chk;
    private final F3Types types;
    private final F3Symtab syms;
    private final F3OptimizationStatistics optStat;

    private final boolean debugNames;
    private int tmpCount = 0;

    protected static final Context.Key<F3PreTranslationSupport> preTranslation =
            new Context.Key<F3PreTranslationSupport>();

    public static F3PreTranslationSupport instance(Context context) {
        F3PreTranslationSupport instance = context.get(preTranslation);
        if (instance == null) {
            instance = new F3PreTranslationSupport(context);
        }
        return instance;
    }

    private F3PreTranslationSupport(Context context) {
        context.put(preTranslation, this);

        f3make = F3TreeMaker.instance(context);
        defs = F3Defs.instance(context);
        names = Name.Table.instance(context);
        chk = F3Check.instance(context);
        types = F3Types.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        optStat = F3OptimizationStatistics.instance(context);

        String opt = Options.instance(context).get("debugNames");
        debugNames = opt != null && !opt.startsWith("n");
    }

    // Just adds a counter. prefix is expected to include "$"
    public Name syntheticName(String prefix) {
        return names.fromString(prefix + tmpCount++);
    }

    public F3Expression defaultValue(Type type) {
        F3Expression res;
        if (types.isSequence(type)) {
            res = f3make.EmptySequence();
        } else {
            switch (type.tag) {
                case FLOAT:
                    res = f3make.Literal(0F);
                    break;
                case DOUBLE:
                    res = f3make.Literal(0.0);
                    break;
                case CHAR:
                    res = f3make.Literal((char) 0);
                    break;
                case BYTE:
                    res = f3make.Literal((byte) 0);
                    break;
                case SHORT:
                    res = f3make.Literal((short) 0);
                    break;
                case INT:
                    res = f3make.Literal((int) 0);
                    break;
                case LONG:
                    res = f3make.Literal(0L);
                    break;
                case BOOLEAN:
                    res = f3make.Literal(false);
                    break;
                default:
                    res = f3make.Literal(BOT, null);
            }
        }
        res.type = type;
        return res;
    }

    public Scope getEnclosingScope(Symbol s) {
        if (s.owner.kind == Kinds.TYP) {
            return ((ClassSymbol)s.owner).members();
        }
        else if (s.owner.kind == Kinds.PCK) {
            return ((PackageSymbol)s.owner).members();
        }
        else
            return null;
    }

    public JavaFileObject sourceFile(Symbol owner) {
        for (Symbol currOwner = owner; currOwner != null; currOwner = currOwner.owner) {
            if (currOwner instanceof ClassSymbol) {
                JavaFileObject src = ((ClassSymbol)currOwner).sourcefile;
                if (src != null) {
                    return src;
                }
            }
        }
        return null;
    }

    public F3ClassSymbol makeClassSymbol(Name name, Symbol owner) {
        F3ClassSymbol classSym = new F3ClassSymbol(Flags.SYNTHETIC, name, owner);
        classSym.flatname = chk.localClassName(classSym);
        chk.compiled.put(classSym.flatname, classSym);

        // we may be able to get away without any scope stuff
        //  s.enter(sym);

        // Fill out class fields.
        classSym.completer = null;
        if (classSym.owner instanceof MethodSymbol &&
            (classSym.owner.flags() & F3Flags.BOUND) != 0L) {
            classSym.flags_field |= F3Flags.F3_BOUND_FUNCTION_CLASS;
        }
        classSym.sourcefile = sourceFile(owner);
        classSym.members_field = new Scope(classSym);

        ClassType ct = (ClassType) classSym.type;
        // We are seeing a local or inner class.
        // Set outer_field of this class to closest enclosing class
        // which contains this class in a non-static context
        // (its "enclosing instance class"), provided such a class exists.
	//System.err.println("make class symbol: "+ name+": "+owner +":"+owner.type);
        Symbol owner1 = (owner instanceof MethodSymbol) ? owner.owner : owner.enclClass();

        if (owner1.kind == Kinds.TYP) {
            ct.setEnclosingType(owner1.type);
	    //System.err.println("owner1="+owner1.type);
        }

        ct.supertype_field = syms.f3_BaseType;

        return classSym;
    }

    public MethodSymbol makeDummyMethodSymbol(Symbol owner) {
        return makeDummyMethodSymbol(owner, names.empty);
    }

    public MethodSymbol makeDummyMethodSymbol(Symbol owner, Name name) {
        return new MethodSymbol(Flags.BLOCK, name, null, owner.enclClass());
    }

    F3Type makeTypeTree(Type type) {
        Type elemType = types.elementTypeOrType(type);
        F3Expression typeExpr;
	typeExpr = f3make.Type(elemType).setType(elemType);
        F3TreeInfo.setSymbol(typeExpr, elemType.tsym);
	Symbol tsym = type.tsym;
	if (type instanceof CapturedType) {
	    throw new RuntimeException("can't handle captured type: "+ type);
	}
	if (false) {
	    System.err.println("elemType="+elemType);
	    System.err.println("typeExpr="+typeExpr);
	    System.err.println("type="+type.getClass()+ ": "+type);
	    System.err.println("tsym="+tsym);
	}
	if (type instanceof FunctionType) {
	    F3Type f3type = (F3Type)f3make.Type(type);
	    //System.err.println("returning "+f3type);
	    //Thread.currentThread().dumpStack();
	    f3type.setType(type);
	    return f3type;
	}
	if (!(tsym instanceof ClassSymbol) || (type instanceof TypeVar)) {
	    if (type instanceof TypeVar) {
		TypeVar tv = (TypeVar)type;
		//		System.err.println("tv.lower="+tv.lower);
		if (tv.lower instanceof WildcardType) { // hack
		    F3Type ft = f3make.TypeExists();
		}
	    } 
	    return (F3Type)f3make.TypeVar(typeExpr, types.isSequence(type) ? Cardinality.ANY : Cardinality.SINGLETON, (TypeSymbol)tsym).setType(type);
	} else {
	    return (F3Type)f3make.TypeClass(typeExpr, types.isSequence(type) ? Cardinality.ANY : Cardinality.SINGLETON, (ClassSymbol)tsym).setType(type);
	}
    }

    F3Var BoundLocalVar(DiagnosticPosition diagPos, Type type, Name name, F3Expression boundExpr, Symbol owner) {
        return Var(diagPos, F3Flags.IS_DEF, type, name, F3BindStatus.UNIDIBIND, boundExpr, owner);
    }

    F3Var LocalVar(DiagnosticPosition diagPos, Type type, Name name, F3Expression expr, Symbol owner) {
        return Var(diagPos,0L, type, name, F3BindStatus.UNBOUND, expr, owner);
    }

    private static final String idChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String suffixGen() {
        final int dig = idChars.length();
        int i = ++tmpCount;
        StringBuffer sb = new StringBuffer();
        while (i > 0) {
            int md = i % dig;
            char ch = idChars.charAt(md);
            sb.append(ch);
            i -= md;
            i = i / dig;
        }
        return sb.toString();
    }

    F3Var SynthVar(DiagnosticPosition diagPos, F3VarSymbol vsymParent, String id, F3Expression initExpr, F3BindStatus bindStatus, Type type, boolean inScriptLevel, Symbol owner) {
        optStat.recordSynthVar(id);
        String ns = "_$" + suffixGen();
        if (debugNames) {
            ns = (vsymParent==null? "" : vsymParent.toString() + "$") + id + ns;
        }
        Name name = names.fromString(ns);
	//System.err.println("synth var: "+ vsymParent + ": "+ id+": "+name);
        long flags = F3Flags.SCRIPT_PRIVATE | Flags.SYNTHETIC | (inScriptLevel ? Flags.STATIC | F3Flags.SCRIPT_LEVEL_SYNTH_STATIC : 0L);
        F3Var var = Var(diagPos, flags, type/*types.normalize(type)*/, name, bindStatus, initExpr, owner);
        owner.members().enter(var.sym);
        return var;
    }

    F3Var Var(DiagnosticPosition diagPos, long flags, Type type, Name name, F3BindStatus bindStatus, F3Expression expr, Symbol owner) {
        F3VarSymbol vsym = new F3VarSymbol(
                types,
                names,
                flags,
                name, type, owner);
        F3Var var = f3make.at(diagPos).Var(
                name,
                makeTypeTree(vsym.type),
                f3make.at(diagPos).Modifiers(flags),
                expr,
                bindStatus,
                null, null);
        var.type = vsym.type;
        var.sym = vsym;
        return var;
    }
    
    F3Expression makeCastIfNeeded(F3Expression tree, Type type) {
	Type treetype = tree.type;
	if (F3TranslationSupport.ERASE_BACK_END) {
	    type = types.erasure(type);
	    treetype = types.erasure(treetype);
	}
        if (type == Type.noType ||
                type == null ||
                type.isErroneous() ||
                type == syms.voidType ||
                treetype == syms.voidType ||
                treetype == syms.unreachableType ||
                type == syms.unreachableType)
            return tree;
        else {
	    int tcons0 = types.isTypeConsType(treetype);
	    int tcons1 = types.isTypeConsType(type);
	    boolean forceCast = false;
	    if (tcons0 <= 0 & tcons1 > 0) {
		if (false) {
		    System.err.println("tree="+tree);
		    System.err.println("treetype="+treetype+ ": "+ tcons0);
		    System.err.println("type="+type + ": "+tcons1);
		}
		// need to cast TypeConsN of (X, Y, ..) to the real type: X of (Y, ...)
		type = treetype;
		forceCast = true;
	    }
            tree = makeNumericBoxConversionIfNeeded(tree, type);
	    F3Expression target = tree;
	    if (types.isSameType(treetype, type) && !types.isSameType(treetype, type, false)) {
		target = makeCast(tree, syms.objectType);
	    }
            F3Expression newTree =  forceCast || !types.isSameType(treetype, type) &&
                   (!types.isSubtypeUnchecked(treetype, type) ||
                   (treetype.isPrimitive() && type.isPrimitive() ||
                   (types.isSameType(treetype, syms.f3_EmptySequenceType) &&
                   types.isSequence(type)))) ?
                makeCast(target, type) :
                tree;
	    if (target != tree) {
		System.err.println("cast: "+ newTree);
	    }
	    if (newTree != tree) {
		//System.err.println("make cast: "+ newTree);
		//Thread.currentThread().dumpStack();
		return newTree;
	    }
	    return tree;
        }
    }

    /**
     * It is necessary to add an extra cast if either source type or target type
     * is a boxed Java type - this is required because we might want to go from
     * java.Lang.Long to int and vice-versa
     */
    private boolean needNumericBoxConversion(F3Expression tree, Type type) {
	if (tree.type == null) {
	    System.err.println("tree.type is null="+tree.getClass()+": "+ tree);
	}
        boolean sourceIsPrimitive = tree.type.isPrimitive();
        boolean targetIsPrimitive = type.isPrimitive();
        Type unboxedSource = types.unboxedType(tree.type);
        Type unboxedTarget = types.unboxedType(type);
        return (sourceIsPrimitive && !targetIsPrimitive && unboxedTarget != Type.noType && !types.isSameType(unboxedTarget, tree.type)) ||
                (targetIsPrimitive && !sourceIsPrimitive && unboxedSource != Type.noType && !types.isSameType(unboxedSource, type)) ||
                (!sourceIsPrimitive && !targetIsPrimitive && unboxedTarget != Type.noType && unboxedSource!= Type.noType && !types.isSameType(type, tree.type));
    }

    private F3Expression makeNumericBoxConversionIfNeeded(F3Expression tree, Type type) {
        if (needNumericBoxConversion(tree, type)) {
           //either tree.type or type is primitive!
           if (tree.type.isPrimitive() && !type.isPrimitive()) {
               return makeCast(tree, types.unboxedType(type));
           }
           else if (type.isPrimitive() && !tree.type.isPrimitive()) {
               return makeCast(tree, types.unboxedType(tree.type));
           }
           else { //both are boxed types
               return makeCast(makeCast(tree, types.unboxedType(tree.type)), types.unboxedType(type));
           }
        }
        else {
            return tree;
        }
    }

    public F3Expression makeCast(F3Expression tree, Type type) {
	//System.err.println("casting: " +tree);
	//System.err.println("tree.type: " +tree.type);
	//System.err.println("to type: "+ type);
	//Thread.currentThread().dumpStack();
	//type = types.erasure(type);
        F3Expression typeTree = makeTypeTree(type);
        F3Expression expr = f3make.at(tree.pos).TypeCast(typeTree, tree);
        expr.type = type;
        return expr;
    }

    void liftTypes(final F3ClassDeclaration cdecl, final Type newEncl, final Symbol newOwner) {
        class NestedClassTypeLifter extends F3TreeScanner {

            @Override
            public void visitClassDeclaration(F3ClassDeclaration that) {
                super.visitClassDeclaration(that);
                if (that.sym != newEncl.tsym &&
                        (that.type.getEnclosingType() == Type.noType ||
                        that.type.getEnclosingType().tsym == newEncl.getEnclosingType().tsym)) {
                    Scope oldScope = getEnclosingScope(that.sym);
                    if (oldScope != null)
                        oldScope.remove(that.sym);
                    ((ClassType)that.type).setEnclosingType(newEncl);
                    that.sym.owner = newOwner;
                    newEncl.tsym.members().enter(that.sym);
                }
            }
        }
        new NestedClassTypeLifter().scan(cdecl);
    }

    Symbol makeSyntheticBuiltinsMethod(Name name) {
        return new MethodSymbol(
                Flags.PUBLIC | Flags.STATIC | F3Flags.FUNC_IS_BUILTINS_SYNTH,
                name,
                new Type.MethodType(
                    List.of(syms.f3_ObjectType, syms.intType),
                    syms.booleanType,
                    List.<Type>nil(),
                    syms.methodClass),
                syms.f3_AutoImportRuntimeType.tsym);
    }

    Symbol makeSyntheticPointerMake() {
        return new MethodSymbol(
                Flags.PUBLIC | Flags.STATIC | F3Flags.FUNC_POINTER_MAKE,
                defs.Pointer_make.methodName,
                new Type.MethodType(
                    List.of(syms.f3_ObjectType, syms.intType, types.erasure(syms.classType)),
                    syms.f3_PointerTypeErasure,
                    List.<Type>nil(),
                    syms.methodClass),
                syms.f3_PointerType.tsym);
    }

    Name makeUniqueVarNameIn(Name name, Symbol owner) {
        while (owner.members().lookup(name).sym != null) {
            name = name.append('$', name);
        }
        return name;
    }

    boolean isNullable(F3Expression expr) {
        if (!types.isNullable(expr.type)) {
            return false;
        }
        while (true) {
            switch (expr.getF3Tag()) {
                case OBJECT_LITERAL:
                    return false;
                case PARENS:
                    //expr = ((F3Parens)expr).getExpressionList();
                    break;
                case BLOCK_EXPRESSION:
                    expr = ((F3Block)expr).getValue();
                    break;
                case CONDEXPR:
                {
                    F3IfExpression ife = (F3IfExpression)expr;
                    return isNullable(ife.getTrueExpression()) || isNullable(ife.getFalseExpression());
                }
                default:
                    return true;
            }
        }
    }

    //TODO: unify with hasSideEffects in TranslationSupport
    boolean hasSideEffectsInBind(F3Expression expr) {
        class SideEffectScanner extends F3TreeScanner {

            boolean hse = false;

            private void markSideEffects() {
                hse = true;
            }

            @Override
            public void visitAssign(F3Assign tree) {
                // In case we add back assignment
                markSideEffects();
            }

            @Override
            public void visitInstanciate(F3Instanciate tree) {
                markSideEffects();
            }

            @Override
            public void visitFunctionInvocation(F3FunctionInvocation tree) {
                markSideEffects();
            }
        }
        SideEffectScanner scanner = new SideEffectScanner();
        scanner.scan(expr);
        return scanner.hse;
    }

    boolean isImmutable(List<F3Expression> trees) {
        for (F3Expression item : trees) {
            if (!isImmutable(item)) {
                return false;
            }
        }
        return true;
    }

    boolean isImmutable(F3Expression tree) {
//        boolean im = isImmutableReal(tree);
//        System.err.println((im? "IMM: " : "MUT: ") + tree);
//        return im;
//    }
//    boolean isImmutableReal(F3Expression tree) {
        //TODO: add for-loop, sequence indexed, string expression
        switch (tree.getF3Tag()) {
            case IDENT: {
                F3Ident id = (F3Ident) tree;
                return isImmutable(id.sym, id.getName());
            }
            case SELECT: {
                F3Select sel = (F3Select) tree;
                return (sel.sym.isStatic() || isImmutable(sel.getExpression())) && isImmutable(sel.sym, sel.getIdentifier());
            }
            case LITERAL:
            case TIME_LITERAL:
            case SEQUENCE_EMPTY:
                return true;
            case PARENS:
                return true;//isImmutable(((F3Parens)tree).getExpression());
            case BLOCK_EXPRESSION: {
                F3Block be = (F3Block) tree;
                for (F3Expression stmt : be.getStmts()) {
                    //TODO: OPT probably many false positive cases
                    if (stmt instanceof F3Var) {
                        F3Var var = (F3Var) stmt;
                        if (!isImmutable(var.getInitializer())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return isImmutable(be.getValue());
            }
            case FOR_EXPRESSION: {
                F3ForExpression fe = (F3ForExpression) tree;
                for (F3ForExpressionInClause clause : fe.getForExpressionInClauses()) {
                    if (!isImmutable(clause.getSequenceExpression())) {
                        return false;
                    }
                    if (clause.getWhereExpression() != null && !isImmutable(clause.getWhereExpression())) {
                        return false;
                    }
                }
                return isImmutable(fe.getBodyExpression());
            }
            case APPLY: {
                F3FunctionInvocation finv = (F3FunctionInvocation) tree;
		if (finv.immutable) return true;
                F3Expression meth = finv.meth;
                Symbol refSym = F3TreeInfo.symbol(meth);
                return
                        isImmutable(meth) &&                       // method being called won't change
                        isImmutable(finv.getArguments()) &&        // arguments won't change
                        !(meth.type instanceof FunctionType) &&    // not a function value call -- over-cautious
                        (refSym instanceof MethodSymbol) &&        // call to a method, protects the next check -- over-cautious
                        (refSym.flags() & F3Flags.BOUND) == 0; // and isn't a call to a bound function
            }
            case CONDEXPR: {
                F3IfExpression ife = (F3IfExpression) tree;
                return isImmutable(ife.getCondition()) && isImmutable(ife.getTrueExpression()) && isImmutable(ife.getFalseExpression());
            }
            case SEQUENCE_RANGE: {
                F3SequenceRange rng = (F3SequenceRange) tree;
                return isImmutable(rng.getLower()) && isImmutable(rng.getUpper()) && (rng.getStepOrNull()==null || isImmutable(rng.getStepOrNull()));
            }
            case SEQUENCE_EXPLICIT: {
                F3SequenceExplicit se = (F3SequenceExplicit) tree;
                for (F3Expression item : se.getItems()) {
                    if (!isImmutable(item)) {
                        return false;
                    }
                }
                return true;
            }
            case TYPECAST: {
                F3TypeCast tc = (F3TypeCast) tree;
                return isImmutable(tc.getExpression());
            }
            default:
                if (tree instanceof F3Unary) {
                    if (tree.getF3Tag().isIncDec()) {
                        return false;
                    } else {
                        return isImmutable(((F3Unary) tree).getExpression());
                    }
                } else if (tree instanceof F3Binary) {
                    F3Binary b = (F3Binary) tree;
                    return isImmutable(b.lhs) && isImmutable(b.rhs);
                } else {
                    return false;
                }
        }
    }

    private boolean isImmutable(Symbol sym, Name name) {
        if (sym.kind != Kinds.VAR) {
            return true;
        }
        F3VarSymbol vsym = (F3VarSymbol) sym;
        Symbol owner = sym.owner;
        return
                    name == names._this ||
                    name == names._super ||
                    (owner instanceof F3ClassSymbol && name == f3make.ScriptAccessSymbol(owner).name) ||
                    !vsym.canChange();
     }
}

