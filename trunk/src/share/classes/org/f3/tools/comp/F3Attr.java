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
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import javax.lang.model.element.ElementKind;
import javax.tools.JavaFileObject;
import org.f3.api.tree.ForExpressionInClauseTree;
import org.f3.api.tree.Tree.F3Kind;
import org.f3.api.tree.TypeTree.Cardinality;
import com.sun.tools.mjavac.code.*;
import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Flags.ANNOTATION;
import static com.sun.tools.mjavac.code.Flags.BLOCK;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.Kinds.ERRONEOUS;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.code.Types.SimpleVisitor;
import static com.sun.tools.mjavac.code.TypeTags.*;
import static com.sun.tools.mjavac.code.TypeTags.WILDCARD;
import com.sun.tools.mjavac.comp.*;
import com.sun.tools.mjavac.jvm.ByteCodes;
import com.sun.tools.mjavac.jvm.Target;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import org.f3.tools.code.*;
import org.f3.tools.tree.*;
import org.f3.tools.util.MsgSym;
import static org.f3.tools.code.F3Flags.SCRIPT_LEVEL_SYNTH_STATIC;
import org.f3.tools.comp.F3Check.WriteKind;

/** This is the main context-dependent analysis phase in GJC. It
 *  encompasses name resolution, type checking and constant folding as
 *  subtasks. Some subtasks involve auxiliary classes.
 *  @see Check
 *  @see Resolve
 *  @see ConstFold
 *  @see Infer
 *
 * This class is interleaved with {@link F3MemberEnter}, which is used
 * to enter declarations into a local scope.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3Attr implements F3Visitor {
    protected static final Context.Key<F3Attr> f3AttrKey =
        new Context.Key<F3Attr>();

    /*
     * modules imported by context
     */
    private final F3Defs defs;
    private final Name.Table names;
    private final Log log;
    F3ClassReader reader;
    private final F3Resolve rs;
    private final F3Infer infer;
    private final F3Symtab syms;
    private final F3Check chk;
    private final Messages messages;
    private final F3MemberEnter memberEnter;
    private final JCDiagnostic.Factory diags;
    private final F3TreeMaker f3make;
    private final ConstFold cfolder;
    private final F3Enter enter;
    private final Target target;
    private final F3Types types;
    private final Annotate annotate;
    
    /*
     * other instance information
     */
    private final Source source;
    
    Map<F3VarSymbol, F3Var> varSymToTree =
            new HashMap<F3VarSymbol, F3Var>();
    Map<MethodSymbol, F3FunctionDefinition> methodSymToTree =
            new HashMap<MethodSymbol, F3FunctionDefinition>();
    Map<MethodSymbol, F3Env<F3AttrContext>> methodSymToEnv =
            new HashMap<MethodSymbol, F3Env<F3AttrContext>>();

    public static F3Attr instance(Context context) {
        F3Attr instance = context.get(f3AttrKey);
        if (instance == null)
            instance = new F3Attr(context);
        return instance;
    }

    protected F3Attr(Context context) {
        context.put(f3AttrKey, this);

        defs = F3Defs.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        names = Name.Table.instance(context);
        log = Log.instance(context);
        diags = JCDiagnostic.Factory.instance(context);
        messages = Messages.instance(context);
        rs = F3Resolve.instance(context);
        infer = F3Infer.instance(context);
        chk = F3Check.instance(context);
        memberEnter = F3MemberEnter.instance(context);
        f3make = (F3TreeMaker)F3TreeMaker.instance(context);
        enter = F3Enter.instance(context);
        cfolder = ConstFold.instance(context);
        target = Target.instance(context);
        types = F3Types.instance(context);
        annotate = Annotate.instance(context);
        reader = F3ClassReader.instance(context);
        Options options = Options.instance(context);

        source = Source.instance(context);
        allowGenerics = source.allowGenerics();
        allowVarargs = source.allowVarargs();
        allowBoxing = source.allowBoxing();
        allowCovariantReturns = source.allowCovariantReturns();
        allowAnonOuterThis = source.allowAnonOuterThis();
        relax = (options.get("-retrofit") != null ||
                 options.get("-relax") != null);

        String pkgs = options.get("warnOnUse");
        if (pkgs != null) {
            warnOnUsePackages = pkgs.split(",");
        }

    }
    /** Switch: relax some constraints for retrofit mode.
     */
    private boolean relax;

    /** Switch: support generics?
     */
    private boolean allowGenerics;

    /** Switch: allow variable-arity methods.
     */
    private boolean allowVarargs;

    /** Switch: support boxing and unboxing?
     */
    private boolean allowBoxing;

    /** Switch: support covariant result types?
     */
    private boolean allowCovariantReturns;

    /** Switch: allow references to surrounding object from anonymous
     * objects during constructor call?
     */
    private boolean allowAnonOuterThis;

    /**
     * Packages for which we have to issue warnings.
     */
    private String[] warnOnUsePackages;

    enum Sequenceness {
        DISALLOWED,
        PERMITTED,
        REQUIRED
    }

    /** Check kind and type of given tree against protokind and prototype.
     *  If check succeeds, store type in tree and return it.
     *  If check fails, store errType in tree and return it.
     *  No checks are performed if the prototype is a method type.
     *  Its not necessary in this case since we know that kind and type
     *  are correct.  WRONG - see VSGC-2199.
     *
     *  @param tree     The tree whose kind and type is checked
     *  @param owntype  The computed type of the tree
     *  @param ownkind  The computed kind of the tree
     *  @param pkind    The expected kind (or: protokind) of the tree
     *  @param pt       The expected type (or: prototype) of the tree
     */
    Type check(F3Tree tree, Type owntype, int ownkind, int pkind, Type pt, Sequenceness pSequenceness) {
        return check(tree, owntype, ownkind, pkind, pt, pSequenceness, true);
    }
    Type check(F3Tree tree, Type owntype, int ownkind, int pkind, Type pt, Sequenceness pSequenceness, boolean giveWarnings) {

        if (owntype != null && owntype != syms.f3_UnspecifiedType && owntype.tag != ERROR && pt.tag != METHOD && pt.tag != FORALL && !(pt instanceof FunctionType)) {
            if ((pkind & VAL) != 0 && ownkind == MTH) {
                ownkind = VAL;
                if (owntype instanceof MethodType) {
                    owntype = chk.checkFunctionType(tree.pos(), (MethodType)owntype);
                }
            }
            if ((ownkind & ~pkind) == 0) {
                owntype = chk.checkType(tree.pos(), owntype, pt, pSequenceness, giveWarnings);
            } else {
		Thread.currentThread().dumpStack();
                log.error(tree.pos(), MsgSym.MESSAGE_UNEXPECTED_TYPE,
                          Resolve.kindNames(pkind),
                          Resolve.kindName(ownkind));
                owntype = syms.errType;
            }
        }
        tree.type = owntype;
        return owntype;
    }

    /** Is this symbol a type?
     */
    static boolean isType(Symbol sym) {
        return sym != null && sym.kind == TYP;
    }

    /** The current `this' symbol.
     *  @param env    The current environment.
     */
    Symbol thisSym(DiagnosticPosition pos, F3Env<F3AttrContext> env) {
	if (env.thisVar != null) {
	    return env.thisVar.sym;
	}
        return rs.resolveSelf(pos, env, env.getEnclosingClassSymbol(), names._this);
    }

/* ************************************************************************
 * Visitor methods
 *************************************************************************/

    /** Visitor argument: the current environment.
     */
    private F3Env<F3AttrContext> env;

    /** Visitor argument: the currently expected proto-kind.
     */
    int pkind;

    /** Visitor argument: the currently expected proto-type.
     */
    Type pt;

    /** Visitor argument: is a sequence permitted
     */
    private Sequenceness pSequenceness;
    
    /** Visitor result: the computed type.
     */
    private Type result;

    /** Visitor method: attribute a tree, catching any completion failure
     *  exceptions. Return the tree's type.
     *
     *  @param tree    The tree to be visited.
     *  @param env     The environment visitor argument.
     *  @param pkind   The protokind visitor argument.
     *  @param pt      The prototype visitor argument.
     */
    Type attribTree(F3Tree tree, F3Env<F3AttrContext> env, int pkind, Type pt) {
        return attribTree(tree, env, pkind, pt, this.pSequenceness);
    }

    Type attribTree(F3Tree tree, F3Env<F3AttrContext> env, int pkind, Type pt, Sequenceness pSequenceness) {
        F3Env<F3AttrContext> prevEnv = this.env;
        int prevPkind = this.pkind;
        Type prevPt = this.pt;
        Sequenceness prevSequenceness = this.pSequenceness;
	boolean prevInSuperType = inSuperType;
        try {
            this.env = env;
            this.pkind = pkind;
            this.pt = pt;
            this.pSequenceness = pSequenceness;
	    //System.err.println("attrib tree: "+ tree+ ", pt="+pt);
            if (tree != null )tree.accept(this);
            if (tree == breakTree)
                throw new BreakAttr(env);
            if (pSequenceness == Sequenceness.REQUIRED && result.tag != ERROR
                    && ! types.isSequence(result)) {
                result = chk.typeTagError(tree, types.sequenceType(syms.unknownType), result);
            }
	    if (result == null) {
		System.err.println("result is null: "+tree);
		result = syms.unknownType;
	    }
	    Type localResult = result;
	    if (localResult.tsym instanceof F3Resolve.TypeAliasSymbol) {
		localResult = localResult.tsym.type;
	    }
	    if (localResult.tag != ERROR) { // hack!!
		List<F3Expression> typeArgs = null;
		if (tree instanceof F3Ident) {
		    typeArgs = ((F3Ident)tree).typeArgs;
		} else if (tree instanceof F3Select) {
		    typeArgs = ((F3Select)tree).typeArgs;
		} else {
		    //System.err.println("unhandled case: "+ tree.getClass() + " "+tree);
		}
		if (typeArgs != null) {
		    boolean typeCons = types.isTypeCons(localResult);
		    List<Type> typeArgTypes = attribTypeArgs(typeArgs, env, true);
		    if (false && typeCons) {
			List<Type> targs = localResult.getTypeArguments();
			if (targs.head != null && 
			    (targs.head instanceof TypeVar) &&
			    "This".equals((((TypeVar)targs.head).tsym.name.toString()))) {
			    F3Env<F3AttrContext> e = env;
			    while (e != null && e.enclClass.getName().toString().length() == 0) {
				e = e.outer;
			    }
			    if (e != null) {
				Type thisType = attribType(f3make.Ident(e.enclClass.getName()), env);
				if (!inSuperType) { // hack
				    //return thisType;
				}
				if (targs.size() == typeArgTypes.size()+1) {
				    typeArgTypes = typeArgTypes.prepend(thisType);
				}
			    }
			}
		    }
		    //System.err.println("localResult="+localResult.getClass()+": "+localResult);
		    if (localResult instanceof MethodType) {
			/*
			localResult = newForAll(typeArgTypes, (MethodType)localResult);
			localResult = types.subst(localResult.asMethodType(),
						  localResult.getTypeArguments(), 
						  typeArgTypes); 
			*/
		    }
		    if (localResult instanceof FunctionType) {
			if (false) {
			    FunctionType ft = (FunctionType)localResult;
			    if (ft.typeArgs != null && ft.typeArgs.size() > 0) {
				localResult = ft.asMethodOrForAll();
				localResult = types.subst(localResult, localResult.getTypeArguments(), typeArgTypes);
				localResult = syms.asFunctionType(localResult);
			    } else { 
				localResult = syms.makeFunctionType(typeArgTypes, 
								    (MethodType)localResult.asMethodType());
			    }
			}
		    } else if (localResult instanceof ClassType) {
			if (false && typeArgTypes.size() > 0) {
			    System.err.println("adding type args to: "+ localResult+": "+typeArgTypes);
			    localResult = newClassType(localResult.getEnclosingType(),
						       typeArgTypes,
						       localResult.tsym);
			}
		    } else {
			Type bound = types.upperBound(localResult);
			//System.err.println("localResult="+localResult);
			//System.err.println("bound="+bound.getClass());
			//System.err.println("bound="+types.toF3String(bound));
			if (bound instanceof ConstI) {
			    ConstI consti = (ConstI)bound;
			    //System.err.println("targs="+typeArgTypes);
			    //System.err.println("i="+consti.i);
			    if (consti.i < typeArgTypes.size()) {
				localResult = typeArgTypes.get(consti.i);
			    }
			    //System.err.println("result="+localResult);
			    //Thread.currentThread().dumpStack();
			} else if (bound instanceof TypeVar) {
			    TypeVar tv = (TypeVar)bound;
			    TypeCons tc = new TypeCons(tv.tsym.name, 
						       tv.tsym, 
						       tv.bound,
						       typeArgTypes);
			    tc.bound = tv.bound;
			    tc.ctor = tv;
			    localResult = tc;
			    //System.err.println("tree="+tree);
			    //System.err.println("tv="+tv);
			    //System.err.println("tc="+tc.getClass()+": "+types.toF3String(tc));
			    localResult = types.applySimpleGenericType(tc, tc.getTypeArguments());
			    //System.err.println("bound="+tv.bound);
			} 

		    }
		    //if (types.isId(localResult)) {
		    //localResult = types.idElementType(localResult);
		    //}
		    tree.type = localResult;
		} else {
		    // we need to erase unspecified type arguments?
		    if ((tree instanceof F3Ident) || 
			(tree instanceof F3Select)) {
			if (F3TreeInfo.symbol(tree) != null && F3TreeInfo.symbol(tree).kind == TYP) {
			    if (localResult instanceof ClassType) {
				if (!types.isF3Function(localResult) && !types.isSequence(localResult) &&
				    !types.isMonadType(localResult)) {
				    localResult = types.erasure(localResult);
				} else {
				    if (types.isSequence(localResult)) {
					String ident = tree.toString();
					if (ident.equals("Sequence") ||
					    ident.equals("org.f3.runtime.sequence.Sequence")) {
					    localResult = types.erasure(localResult);
					}
				    }

				}
			    } else {
				//System.err.println("not erasing: "+ localResult);
			    }
			}
		    } else {
			//System.err.println("unhandled case: "+ tree.getClass()+": "+tree);
		    }
		}
	    }
	    return localResult;
        } catch (CompletionFailure ex) {
            tree.type = syms.errType;
            return chk.completionError(tree.pos(), ex);
        } finally {
	    this.inSuperType = prevInSuperType;
            this.env = prevEnv;
            this.pkind = prevPkind;
            this.pt = prevPt;
            this.pSequenceness = prevSequenceness;
        }
    }

    /** Derived visitor method: attribute an expression tree.
     */
    Type attribExpr(F3Tree tree, F3Env<F3AttrContext> env, Type pt, Sequenceness pSequenceness) {
        return attribTree(tree, env, VAL, pt.tag != ERROR ? pt : Type.noType, pt.tag != ERROR ? pSequenceness : Sequenceness.PERMITTED);
    }

    /** Derived visitor method: attribute an expression tree.
     *  allow a sequence if no proto-type is specified, the proto-type is a seqeunce,
     *  or the proto-type is an error.
     */
    Type attribExpr(F3Tree tree, F3Env<F3AttrContext> env, Type pt) {
        return attribTree(tree, env, VAL, pt.tag != ERROR ? pt : Type.noType,
                (pt.tag == ERROR || pt == Type.noType || types.isSequence(pt))?
                        Sequenceness.PERMITTED :
                        Sequenceness.DISALLOWED);
    }

    /** Derived visitor method: attribute an expression tree with
     *  no constraints on the computed type.
     */
    Type attribExpr(F3Tree tree, F3Env<F3AttrContext> env) {
        return attribTree(tree, env, VAL, Type.noType, Sequenceness.PERMITTED);
    }

    /** Derived visitor method: attribute a type tree.
     */
    Type attribType(F3Tree tree, F3Env<F3AttrContext> env) {
        Type localResult = attribTree(tree, env, TYP, Type.noType, Sequenceness.PERMITTED);
        return localResult;
    }

    boolean inSuperType;
    Type attribSuperType(F3Tree tree, F3Env<F3AttrContext> env) {
	boolean prevInSuperType = inSuperType;
	try {
	    inSuperType = true;
	    Type localResult = attribTree(tree, env, TYP, Type.noType, Sequenceness.PERMITTED);
	    if (localResult instanceof ClassType) {
		localResult = applyTypeArgs((ClassType)localResult);;
	    }
	    return localResult;
	} finally {
	    inSuperType = prevInSuperType;
	}
    }

    /** Derived visitor method: attribute a statement or definition tree.
     */
    public Type attribDecl(F3Tree tree, F3Env<F3AttrContext> env) {
        return attribTree(tree, env, NIL, Type.noType, Sequenceness.DISALLOWED);
    }
    private boolean isObjLiteral(Symbol sym) {
	return sym.name.toString().contains(defs.objectLiteralClassInfix);
    }

    public Type attribVar(F3Var tree, F3Env<F3AttrContext> env) {
        memberEnter.memberEnter(tree, env);
        Type t = attribExpr(tree, env);
	if (tree instanceof F3Var.This) {
	    Type t1 = t;
	    tree.sym.type = types.erasure(tree.sym.type);
	    Type expectedType = env.getEnclosingClassType();
	    if (isObjLiteral(expectedType.tsym)) {
		expectedType = types.supertype(expectedType);
	    }
	    t = checkId(tree, env.getEnclosingClassType(), tree.sym, env, t.getTypeArguments(), VAR, 
			expectedType,
			pSequenceness, false);
	    tree.baseType = env.getEnclosingClassType();
	    //System.err.println("this: "+env.getEnclosingClassType());
	    //System.err.println("this': "+t1);;
	    //System.err.println("check id: "+ t);
	    t = tree.sym.type = tree.type = t1;
	}
	return t;
    }

    /** Attribute a list of expressions, returning a list of types.
     */
    List<Type> attribExprs(List<F3Expression> trees, F3Env<F3AttrContext> env, Type pt) {
        ListBuffer<Type> ts = new ListBuffer<Type>();
        for (List<F3Expression> l = trees; l.nonEmpty(); l = l.tail)
            ts.append(attribExpr(l.head, env, pt));
        return ts.toList();
    }

    /** Attribute the arguments in a method call, returning a list of types.
     */
    List<Type> attribArgs(List<F3Expression> trees, F3Env<F3AttrContext> env) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        for (List<F3Expression> l = trees; l.nonEmpty(); l = l.tail)
            argtypes.append(chk.checkNonVoid(
                l.head.pos(), types.upperBound(attribTree(l.head, env, VAL, F3Infer.anyPoly))));
        return argtypes.toList();
    }

    /** Does tree represent a static reference to an identifier?
     *  It is assumed that tree is either a SELECT or an IDENT.
     *  We have to weed out selects from non-type names here.
     *  @param tree    The candidate tree.
     */
    boolean isStaticReference(F3Tree tree) {
        if (tree.getF3Tag() == F3Tag.SELECT) {
            Symbol lsym = F3TreeInfo.symbol(((F3Select) tree).selected);
            if (lsym == null || lsym.kind != TYP) {
                return false;
            }
        }
        return true;
    }

    /** Attribute a list of statements, returning nothing.
     */
    <T extends F3Tree> void attribDecls(List<T> trees, F3Env<F3AttrContext> env) {
        for (List<T> l = trees; l.nonEmpty(); l = l.tail)
            attribDecl(l.head, env);
    }

    List<Type> attribTypeArgs(List<F3Expression> trees, F3Env<F3AttrContext> env) {
	return attribTypeArgs(trees, env, !inSuperType);
    }

    List<Type> attribTypeArgs(List<F3Expression> trees, F3Env<F3AttrContext> env, boolean extend) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();

        for (List<F3Expression> l = trees; l.nonEmpty(); l = l.tail) {
	    if (l.head instanceof F3TypeExists) { // hack
		argtypes.append(new WildcardType(syms.objectType, BoundKind.UNBOUND, syms.boundClass));
	    } else {
		Type t = attribType(l.head, env);
		t = types.boxedTypeOrType(t);
		l.head.type = t;
		BoundKind bk = F3TreeInfo.boundKind(l.head);
		if (l.head instanceof F3Type) {
		    F3Expression upperBound = ((F3Type)l.head).upperBound;
		    Type upper = upperBound == null ? null : attribType(upperBound, env);
		    if (upper != null) {
			System.err.println("upper="+upper+"t="+t);
		    }
		    if (upper == t) {
			bk = BoundKind.UNBOUND;
		    }
		}
		if (!isWildcard(t) && !inSuperType && bk != null && bk != BoundKind.UNBOUND) {
		    System.err.println("making wildcard from: "+ t.getClass()+": "+types.toF3String(t));
		    t = new WildcardType(t, bk, syms.boundClass);
		} 
		argtypes.append(t);
	    }
	}
	//System.err.println("attrib type args: "+trees + " => " +argtypes.toList());
        return argtypes.toList();
    }

    List<Type> attribTypeParams(List<F3Expression> trees, F3Env<F3AttrContext> env) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        for (List<F3Expression> l = trees; l.nonEmpty(); l = l.tail) {
	    if (l.head instanceof F3TypeExists) { // hack
		argtypes.append(new WildcardType(syms.objectType, BoundKind.UNBOUND, syms.boundClass));
	    } else {
		Type t = types.boxedTypeOrType(attribType(l.head, env));
		l.head.type = t;
		/*
		BoundKind bk = F3TreeInfo.boundKind(l.head);
		if (bk != null && bk != BoundKind.UNBOUND) {
		    //System.err.println("making wildcard from: "+ l.head);
		    t = new WildcardType(t, bk, syms.boundClass);
		    System.err.println("making wildcard from: "+ l.head+": "+types.toF3String(t));
		} 
		*/
		argtypes.append(t);
	    }
	}
        return argtypes.toList();
    }


    /** Attribute a type argument list, returning a list of types.
     */
    List<Type> attribTypes(List<F3Expression> trees, F3Env<F3AttrContext> env) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        for (List<F3Expression> l = trees; l.nonEmpty(); l = l.tail) {
            //argtypes.append(chk.checkRefType(l.head.pos(), attribType(l.head, env)));
            argtypes.append(attribType(l.head, env));
	}
        return argtypes.toList();
    }

    /** Attribute type reference in an `extends' or `implements' clause.
     *
     *  @param tree              The tree making up the type reference.
     *  @param env               The environment current at the reference.
     *  @param classExpected     true if only a class is expected here.
     *  @param interfaceExpected true if only an interface is expected here.
     */
    Type attribBase(F3Tree tree,
                    F3Env<F3AttrContext> env,
                    boolean classExpected,
                    boolean interfaceExpected,
                    boolean checkExtensible) {
        Type t = attribSuperType(tree, env);
        return checkBase(t, tree, env, classExpected, interfaceExpected, checkExtensible);
    }

    Type checkBase(Type t,
                   F3Tree tree,
                   F3Env<F3AttrContext> env,
                   boolean classExpected,
                   boolean interfaceExpected,
                   boolean checkExtensible) {
        if (t.tag == TYPEVAR && !classExpected && !interfaceExpected) {
            // check that type variable is already visible
            if (t.getUpperBound() == null) {
                log.error(tree.pos(), MsgSym.MESSAGE_ILLEGAL_FORWARD_REF);
                return syms.errType;
            }
        } else {
            t = chk.checkClassType(tree.pos(), t, checkExtensible|!allowGenerics);
        }
        if (interfaceExpected && (t.tsym.flags() & INTERFACE) == 0) {
            log.error(tree.pos(), MsgSym.MESSAGE_INTF_EXPECTED_HERE);
            // return errType is necessary since otherwise there might
            // be undetected cycles which cause attribution to loop
            return syms.errType;
        } else if (checkExtensible &&
                   classExpected &&
                   (t.tsym.flags() & INTERFACE) != 0) {
            log.error(tree.pos(), MsgSym.MESSAGE_NO_INTF_EXPECTED_HERE);
            return syms.errType;
        }
        if (checkExtensible &&
            ((t.tsym.flags() & FINAL) != 0)) {
            log.error(tree.pos(),
                      MsgSym.MESSAGE_CANNOT_INHERIT_FROM_FINAL, t.tsym);
        }
        chk.checkNonCyclic(tree.pos(), t);
        return t;
    }

    private F3Env<F3AttrContext> newLocalEnv(F3Tree tree) {
        F3Env<F3AttrContext> localEnv =
                env.dup(tree, env.info.dup(env.info.scope.dupUnshared()));
        localEnv.outer = env;
        localEnv.info.scope.owner = new MethodSymbol(BLOCK, names.empty, null, env.getEnclosingClassSymbol());
        return localEnv;
    }

    //@Override
    public void visitTypeCast(F3TypeCast tree) {
        Type clazztype = attribType(tree.clazz, env);  
        Type exprtype = attribExpr(tree.expr, env);
	if (exprtype instanceof MethodType) {
	    exprtype = syms.makeFunctionType((MethodType)exprtype);
	}
        Type owntype = chk.checkCastable(tree.expr.pos(), exprtype, clazztype);
        if (exprtype.constValue() != null)
            owntype = cfolder.coerce(exprtype, owntype);
        result = check(tree, capture(owntype), VAL, pkind, pt, Sequenceness.DISALLOWED);
	tree.type = result;
    }

    //@Override
    public void visitInstanceOf(F3InstanceOf tree) {
        Type exprtype = attribExpr(tree.expr, env);
        Type type = attribType(tree.clazz, env);
	type = types.erasure(type);
        //FIXME - check that the target type is not a generic type - this hack
        //disables instanceof where target type is a sequence, currently
        //not supported by translation
        result = chk.checkReifiableReferenceType(
                tree.clazz.pos(),
                types.boxedTypeOrType(type));
        if (!result.isErroneous()) {
            chk.checkInstanceOf(tree.expr.pos(), exprtype, type);
            result = check(tree, syms.booleanType, VAL, pkind, pt, Sequenceness.DISALLOWED);
        }
    }

    private void checkTypeCycle(F3Tree tree, Symbol sym) {
        if (sym.type == null) {
            F3Var var = varSymToTree.get(sym);
	    //System.err.println("tree="+tree);
	    //System.err.println("sym="+sym);
            if (var != null) {
		JavaFileObject prevSource = log.currentSource();
		try {
		    //we need to switch log source as the var def could be
		    //in another source w.r.t. the current one
		    log.useSource(sym.outermostClass().sourcefile);
		    log.note(var, MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_VAR_DECL, sym.name);
		}
		finally {
		    log.useSource(prevSource);
		}
	    }
            log.error(tree.pos(), MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_VAR_REF, sym.name);
            sym.type = syms.errType;
        }
        else if (sym.type instanceof MethodType &&
                sym.type.getReturnType() == syms.unknownType) {
            F3FunctionDefinition fun = methodSymToTree.get(sym);
            if (fun != null) {
		JavaFileObject prevSource = log.currentSource();
		try {
		    //we need to switch log source as the func def could be
		    //in another source w.r.t. the current one
		    log.useSource(sym.outermostClass().sourcefile);
		    log.note(fun, MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_FUN_DECL, sym.name);
		}
		finally {
		    log.useSource(prevSource);
		}
	    }
            log.error(tree.pos(), MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_VAR_REF, sym.name);
            if (pt instanceof MethodType)
                ((MethodType)pt).restype = syms.errType;
            sym.type = syms.errType;
        }
    }

    //@Override
    public void visitIdent(F3Ident tree) {
        Symbol sym;
        boolean varArgs = false;

        // Find symbol
        if (tree.sym != null && tree.sym.kind != VAR) {
            sym = tree.sym;
        } else {
	    Type req = pt;
	    //System.err.println("resolve ident: "+tree+": "+ req.getClass()+": "+ req);
	    //if ((req instanceof MethodType) && ((MethodType)req).getReturnType() == syms.unknownType) {
	    //Thread.currentThread().dumpStack();
	    //}
	    //System.err.println("resolve ident in "+env.getEnclosingClassType());
	    sym = rs.resolveIdent(tree.pos(), env, tree.getName(), pkind, req);
	    //if (sym != null) {
		//System.err.println("resolve ident: "+tree+" "+sym.getClass() + ": "+ sym);
	    //}
        }
        tree.sym = sym;
        sym.complete();
        checkTypeCycle(tree, sym);

        // (1) Also find the environment current for the class where
       //     sym is defined (`symEnv').
        // Only for pre-tiger versions (1.4 and earlier):
        // (2) Also determine whether we access symbol out of an anonymous
        //     class in a this or super call.  This is illegal for instance
        //     members since such classes don't carry a this$n link.
        //     (`noOuterThisPath').
        F3Env<F3AttrContext> symEnv = env;
        boolean noOuterThisPath = false;
        if (env.getEnclosingClassSymbol().owner.kind != PCK && // we are in an inner class
            (sym.kind & (VAR | MTH | TYP)) != 0 &&
            sym.owner.kind == TYP &&
            tree.getName() != names._this && tree.getName() != names._super) {

            // Find environment in which identifier is defined.
            while (symEnv.outer != null &&
                   !sym.isMemberOf(symEnv.getEnclosingClassSymbol(), types)) {
                if ((symEnv.getEnclosingClassSymbol().flags() & NOOUTERTHIS) != 0)
                    noOuterThisPath = !allowAnonOuterThis;
                symEnv = symEnv.outer;
            }
        }

        // In a constructor body,
        // if symbol is a field or instance method, check that it is
        // not accessed before the supertype constructor is called.
        if ((symEnv.info.isSelfCall || noOuterThisPath) &&
            (sym.kind & (VAR | MTH)) != 0 &&
            sym.owner.kind == TYP &&
            (sym.flags() & STATIC) == 0) {
            chk.earlyRefError(tree.pos(), sym.kind == VAR ? sym : thisSym(tree.pos(), env));
        }

    	F3Env<F3AttrContext> env1 = env;
        if (sym.kind != ERR && sym.owner != null && sym.owner != env1.getEnclosingClassSymbol()) {
            // If the found symbol is inaccessible, then it is
            // accessed through an enclosing instance.  Locate this
            // enclosing instance:
            while (env1.outer != null && !rs.isAccessible(env, env1.getEnclosingClassType(), sym))
                env1 = env1.outer;
        }

        // If symbol is a variable, ...
        if (sym.kind == VAR) {
            F3VarSymbol v = (F3VarSymbol)sym;

            if (env.info.inInvalidate &&
		sym == env.info.enclVar) {
                log.error(tree.pos(), MsgSym.MESSAGE_CANNOT_REF_INVALIDATE_VAR, sym);
            }

            // If we are expecting a variable (as opposed to a value), check
            // that the variable is assignable in the current environment.
            if (pkind == VAR)
                chk.checkAssignable(tree.pos(), v, null, env1.getEnclosingClassType(), env, WriteKind.ASSIGN);
        }
	List<Type> actuals = List.nil();
	if (tree.typeArgs != null && tree.typeArgs.nonEmpty()) {
	    //System.err.println("attrib type args: "+tree);
	    actuals = attribTypeArgs(tree.typeArgs, env);
	    //System.err.println("actuals="+actuals);
	    if (true) {
		List<Type> baseTypeArgs = sym.type.getTypeArguments();
		for (List<Type> x = baseTypeArgs, y = actuals; x != null && y != null && x.head != null && y.head != null; x = x.tail, y = y.tail) {
		    Type z = x.head;
		    //System.err.println("z="+types.toF3String(z));
		    if (z instanceof TypeVarDefn) {
			TypeVarDefn def = (TypeVarDefn)z;
			Type q = y.head;
			if (y.head instanceof TypeVar) {
			    continue;
			}
			if (y.head instanceof WildcardType) {
			    y.head = ((WildcardType)y.head).type;
			}
			y.head = new WildcardType(y.head, def.variance, syms.boundClass); 
			//System.err.println(types.toF3String(q) + " -> " + types.toF3String(y.head));
		    }
		}
		
	    } 
	}
	//System.err.println("checkId: "+ sym);
	//System.err.println("pt="+pt);
	//System.err.println("actuals="+actuals);
        result = checkId(tree, env1.getEnclosingClassType(), sym, env, actuals, pkind, pt, pSequenceness, varArgs);
	if (actuals.size() > 0) {
	    //System.err.println("sym.type="+sym.type);
	    //System.err.println("actuals="+actuals);
	    //System.err.println("result="+result.getClass()+": "+types.toF3String(result));
	}
	//System.err.println("result="+result.getClass()+": "+result);
	if (result == null) {
	    System.out.println("result of "+tree.pos+" "+sym + " = "+result.getClass()+": "+result);
	    System.err.println("result="+result);
	}
	tree.type = result;
	if (false) {
	    System.out.println("result of "+tree+ " = "+result.getClass()+": "+result);
	    System.err.println("result="+result);
	}
	//if (pkind == TYP) {
	    //System.err.println("visitIdent: "+ tree+": "+result+ " inSuper="+inSuperType);
	//}
	if (pkind == TYP && !inSuperType) {

	    if (result instanceof TypeVarDefn) {
		Type result0 = result;
		tree.type = result = types.expandTypeVar(result);
	    }
	}
    }

    //@Override
    public void visitSelect(F3Select tree) {
        // Determine the expected kind of the qualifier expression.
        int skind = 0;
        if (tree.name == names._this || tree.name == names._super ||
            tree.name == names._class)
        {
            skind = TYP;
        } else {
            if ((pkind & PCK) != 0) skind = skind | PCK;
            if ((pkind & TYP) != 0) skind = skind | TYP | PCK;
            if ((pkind & (VAL | MTH)) != 0) skind = skind | VAL | TYP;
        }
        // Attribute the qualifier expression, and determine its symbol (if any).
        Type site = attribTree(tree.selected, env, skind,
                F3Infer.anyPoly, Sequenceness.PERMITTED);
        boolean wasPrimitive = site.isPrimitive();
        site = types.boxedTypeOrType(site);
        
        if ((pkind & (PCK | TYP)) == 0) {
	    site = capture(site); // Capture field access
	}

        // don't allow T.class T[].class, etc
        if (skind == TYP) {
            Type elt = site;
            while (elt.tag == ARRAY)
                elt = ((ArrayType)elt).elemtype;
            if (elt.tag == TYPEVAR) {
                log.error(tree.pos(), MsgSym.MESSAGE_TYPE_VAR_CANNOT_BE_DEREF);
                result = syms.errType;
                return;
            }
        }

        // If qualifier symbol is a type or `super', assert `selectSuper'
        // for the selection. This is relevant for determining whether
        // protected symbols are accessible.
        Symbol sitesym = F3TreeInfo.symbol(tree.selected);
	if (isType(sitesym)) {
	    site = types.boxedTypeOrType(types.normalize(site, false));
	    //site = types.boxedTypeOrType(sitesym.type);
	}
        boolean selectSuperPrev = env.info.selectSuper;
        env.info.selectSuper =
            sitesym != null &&
            sitesym.name == names._super;
        
        // If selected expression is polymorphic, strip
        // type parameters and remember in env.info.tvars, so that
        // they can be added later (in Attr.checkId and Infer.instantiateMethod).
        if (tree.selected.type.tag == FORALL) {
            ForAll pstype = (ForAll)tree.selected.type;
            env.info.tvars = pstype.tvars;
            site = tree.selected.type = pstype.qtype;
        }

        // Determine the symbol represented by the selection.
        env.info.varArgs = false;
        if (sitesym instanceof ClassSymbol &&
                (types.isSameType(sitesym.type, syms.objectType) ||
                env.getEnclosingClassSymbol().isSubClass(sitesym, types)))
                    env.info.selectSuper = true;
        Symbol sym = selectSym(tree, site, env, pt, pkind);

        sym.complete();
        if (sym.exists() && !isType(sym) && (pkind & (PCK | TYP)) != 0) {
            site = capture(site);
            sym = selectSym(tree, site, env, pt, pkind);
        }

        boolean varArgs = env.info.varArgs;
        tree.sym = sym;

        if (wasPrimitive && sym.isStatic() && tree.selected instanceof F3Ident)
            tree.selected.type = site;

        checkTypeCycle(tree, sym);

        if (site.tag == TYPEVAR && !isType(sym) && sym.kind != ERR)
            site = capture(site.getUpperBound());

        // If that symbol is a variable, ...
        if (sym.kind == VAR) {
            F3VarSymbol v = (F3VarSymbol)sym;
            
            // If we are expecting a variable (as opposed to a value), check
            // that the variable is assignable in the current environment.
            if (pkind == VAR)
                chk.checkAssignable(tree.pos(), v, tree.selected, site, env, WriteKind.ASSIGN);
        }

        // Disallow selecting a type from an expression
        if (isType(sym) && (sitesym==null || (sitesym.kind&(TYP|PCK)) == 0)) {
            tree.type = check(tree.selected, pt,
                              sitesym == null ? VAL : sitesym.kind, TYP|PCK, pt, pSequenceness);
        }

        if (isType(sitesym)) {
            if (sym.name == names._this) {
                // If `C' is the currently compiled class, check that
                // C.this' does not appear in a call to a super(...)
                if (env.info.isSelfCall &&
                    site.tsym == env.getEnclosingClassSymbol()) {
                    chk.earlyRefError(tree.pos(), sym);
                }
            }
            else if (!sym.isStatic()) {

		// @TODO
		// We can create a lambda for it, e.g
		// String.toUpperCase
		// translates to
		// function(_:String):String {_.toUpperCase()}
		
		
		if (false) {
		    
		    for (F3Env<F3AttrContext> env1 = env; ; env1 = env1.outer) {
			if (env1 == null) {
			    rs.access(rs.new StaticError(sym),
				      tree.pos(), site, sym.name, true);
			    break;
			}
			
			if (env1.tree instanceof F3FunctionDefinition &&
			    ((F3FunctionDefinition)env1.tree).isStatic()) {
			    rs.access(rs.new StaticError(sym),
				      tree.pos(), site, sym.name, true);
			    break;
			}
			
			if (env1.tree instanceof F3ClassDeclaration &&
                            types.isSubtype(((F3ClassDeclaration) env1.tree).sym.type, site)) {
			    break;
			}
		    }
		}
	    }
        }

        // If we are selecting an instance member via a `super', ...
        if (env.info.selectSuper && (sym.flags() & STATIC) == 0) {

            // Check that super-qualified symbols are not abstract (JLS)
            rs.checkNonAbstract(tree.pos(), sym);

            if (env.getEnclosingClassSymbol() instanceof F3ClassSymbol) {
                // Check that the selectet type is a direct supertype of the enclosing class
                chk.checkSuper(tree.pos(), (F3ClassSymbol)env.getEnclosingClassSymbol(), site);
            }

            if (site.isRaw()) {
                // Determine argument types for site.
                Type site1 = types.asSuper(env.getEnclosingClassType(), site.tsym);
                if (site1 != null) site = site1;
            }
        }

        env.info.selectSuper = selectSuperPrev;
	List<Type> actuals = List.nil();
	if (tree.typeArgs != null) {
	    actuals = attribTypeArgs(tree.typeArgs, env);
	}
        result = checkId(tree, site, sym, env, actuals, pkind, pt, pSequenceness, varArgs);

	if (isType(sitesym)) {
	    if (!sym.isStatic()) {
		tree.staticRefOfNonStatic = true;
		MethodType mt = null;
		if (result instanceof MethodType) {
		    mt = (MethodType)result;
		} else if (result instanceof FunctionType) {
		    mt = ((FunctionType)result).asMethodType();
		}
		if (mt != null) {
		    ListBuffer<Type> typarams = new ListBuffer<Type>();
		    Type rtype = mt.restype;
		    typarams.append(types.boxedTypeOrType(rtype));
		    typarams.append(types.boxedTypeOrType(tree.selected.type));
		    for (List<Type> l = mt.argtypes; l.nonEmpty(); l = l.tail) {
			typarams.append(types.boxedTypeOrType(l.head));
		    }
		    result = syms.makeFunctionType(typarams.toList());
		}
	    }
	}
	tree.type = result;
	if (tree.sym.kind == MTH && result instanceof FunctionType) {
	    MethodSymbol res = new MethodSymbol(sym.flags_field, sym.name, ((FunctionType)result).mtype, sym.owner);
	    MethodSymbol origSym = (MethodSymbol)sym;
	    if (origSym.params != null) {
		res.params = List.<VarSymbol>nil();
		List<Type> pts = res.type.asMethodType().argtypes;
		for (VarSymbol vsym: origSym.params) {
		    res.params = res.params.append(new F3VarSymbol(types, names, 
								   vsym.flags(), vsym.name, 
								   pts.head, 
								   res));
		    pts = pts.tail;
		}
	    }
	    //System.err.println("created method symbol: "+res);
	    //System.err.println("created method symbol params: "+res.params);
	    tree.sym = res;
	}

	env.info.tvars = List.nil();
    }
    //where
        /** Determine symbol referenced by a Select expression,
         *
         *  @param tree   The select tree.
         *  @param site   The type of the selected expression,
         *  @param env    The current environment.
         *  @param pt     The current prototype.
         *  @param pkind  The expected kind(s) of the Select expression.
         */
    @SuppressWarnings("fallthrough")
        private Symbol selectSym(F3Select tree,
                                 Type site,
                                 F3Env<F3AttrContext> env,
                                 Type pt,
                                 int pkind) {
            DiagnosticPosition pos = tree.pos();
            Name name = tree.name;
            Symbol sym;

	    if (site instanceof MethodType) {
		site = syms.makeFunctionType(site.asMethodType());
	    }

            switch (site.tag) {
            case PACKAGE:
                return rs.access(
                    rs.findIdentInPackage(env, site.tsym, name, pkind),
                    pos, site, name, true);
            case ARRAY:
            case CLASS:
                if (pt.tag == METHOD || pt.tag == FORALL) {
		    //System.err.println("site="+site);
                    return rs.resolveQualifiedMethod(pos, env, site, name, pt);
                } else if (name == names._this || name == names._super) {
                    return rs.resolveSelf(pos, env, site.tsym, name);
                } else if (name == names._class) {
                    // In this case, we have already made sure in
                    // visitSelect that qualifier expression is a type.
                    Type t = syms.classType;
                    List<Type> typeargs = allowGenerics
                        //? List.of(types.erasure(site))
                        ? List.of(site)
                        : List.<Type>nil();
                    t = newClassType(t.getEnclosingType(), typeargs, t.tsym);
                    return new F3VarSymbol(
                        types, names,STATIC | PUBLIC | FINAL | F3Flags.VARUSE_SPECIAL, names._class, t, site.tsym);
                } else {
                    // We are seeing a plain identifier as selector.
                    sym = rs.findIdentInType(env, site, name, pkind);
                    if ((pkind & ERRONEOUS) == 0)
                        sym = rs.access(sym, pos, site, name, true);
                    return sym;
                }
            case WILDCARD:
		System.err.println(tree);
		System.err.println(site.getClass());
		System.err.println(types.toF3String(site));
                //throw new AssertionError(tree);
            case TYPEVAR:
                // Normally, site.getUpperBound() shouldn't be null.
                // It should only happen during memberEnter/attribBase
                // when determining the super type which *must* be
                // done before attributing the type variables.  In
                // other words, we are seeing this illegal program:
                // class B<T> extends A<T.foo> {}
                sym = (site.getUpperBound() != null)
                    ? selectSym(tree, capture(site.getUpperBound()), env, pt, pkind)
                    : null;
		//System.err.println("sym="+sym);
                if (sym == null || isType(sym)) {
                    log.error(pos, MsgSym.MESSAGE_TYPE_VAR_CANNOT_BE_DEREF);
                    return syms.errSymbol;
                } else {
                    return sym;
                }
            case ERROR:
                // preserve identifier names through errors
                return new ErrorType(name, site.tsym).tsym;
            default:
                // The qualifier expression is of a primitive type -- only
                // .class is allowed for these.
                if (name == names._class) {
                    // In this case, we have already made sure in Select that
                    // qualifier expression is a type.
                    Type t = syms.classType;
                    Type arg = types.boxedTypeOrType(site);
                    t = newClassType(t.getEnclosingType(), List.of(arg), t.tsym);
                    return new F3VarSymbol(
                        types, names,STATIC | PUBLIC | FINAL | F3Flags.VARUSE_SPECIAL, names._class, t, site.tsym);
                } else {
                    log.error(pos, MsgSym.MESSAGE_CANNOT_DEREF, site);
                    return syms.errSymbol;
                }
            }
        }


    //@Override
    public void visitParens(F3Parens tree) {
        Type owntype = attribTree(tree.getExpression(), env, pkind, pt);
        result = check(tree, owntype, pkind, pkind, pt, pSequenceness);
        Symbol sym = F3TreeInfo.symbol(tree);
        if (sym != null && (sym.kind&(TYP|PCK)) != 0)
            log.error(tree.pos(), MsgSym.MESSAGE_ILLEGAL_START_OF_TYPE);
    }

    //@Override
    public void visitAssign(F3Assign tree) {
        Type owntype = null;
        F3Env<F3AttrContext> dupEnv = env.dup(tree);
        dupEnv.outer = env;
        owntype = attribTree(tree.lhs, dupEnv, VAR, Type.noType);
        boolean hasLhsType;
        if (owntype == null || owntype == syms.f3_UnspecifiedType) {
            owntype = attribExpr(tree.rhs, env, Type.noType);
            hasLhsType = false;
        }
        else {
            hasLhsType = true;
        }

        Symbol lhsSym = F3TreeInfo.symbol(tree.lhs);
        if (lhsSym == null) {
            log.error(tree, MsgSym.MESSAGE_F3_INVALID_ASSIGNMENT);
            return;
        }
	if (tree.lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED) {
	    F3SequenceIndexed ind = (F3SequenceIndexed)tree.lhs;
	    if (ind.get != null) {
		Name set = names.fromString("set");
		F3Expression setTree = f3make.at(tree.pos).Select(ind.getSequence(), set, true);
		List<F3Expression> args = List.of(ind.getIndex());
		args = args.append(tree.lhs);
		F3Expression invokeTree = f3make.at(tree.pos).Apply(List.<F3Expression>nil(),
								    setTree,
								    args);
		tree.set = invokeTree;
		result = attribExpr(invokeTree, dupEnv);
		return;
	    }
	}
        if (hasLhsType) {
            attribExpr(tree.rhs, dupEnv, owntype);
	    if (tree.lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED) {
		if (types.isSequence(tree.rhs.type)) {
		    owntype = types.sequenceType(types.elementTypeOrType(owntype));
		} 
	    }
        }
        else {
            if (tree.lhs.getF3Tag() == F3Tag.SELECT) {
                F3Select fa = (F3Select)tree.lhs;
                fa.type = owntype;
            }
            else if (tree.lhs.getF3Tag() == F3Tag.IDENT) {
                F3Ident id = (F3Ident)tree.lhs;
                id.type = owntype;
            }
	    
            attribTree(tree.lhs, dupEnv, VAR, owntype);
            lhsSym.type = owntype;
        }
        result = check(tree, capture(owntype), VAL, pkind, pt, pSequenceness);
	
        if (tree.rhs != null && tree.lhs.getF3Tag() == F3Tag.IDENT) {
            F3Var lhsVar = varSymToTree.get(lhsSym);
            if (lhsVar != null && (lhsVar.getF3Type() instanceof F3TypeUnknown)) {
                if (lhsVar.type == null ||
		    lhsVar.type == syms.f3_AnyType ||
		    lhsVar.type == syms.f3_UnspecifiedType) {
                    if (tree.rhs.type != syms.botType &&
			tree.rhs.type != null &&
			lhsVar.type != tree.rhs.type) {
                        tree.type = tree.lhs.type = lhsVar.type = lhsSym.type = types.normalize(tree.rhs.type);
                        lhsVar.setF3Type(f3make.at(tree.pos()).TypeClass(f3make.Type(lhsSym.type), lhsVar.getF3Type().getCardinality()));
			//attribExpr(lhsVar);
		    }
		}
	    }
	}
    }

    public void finishVar(F3Var tree, F3Env<F3AttrContext> env) {
        F3VarSymbol v = tree.sym;        
	v.owner.complete();

        // The info.lint field in the envs stored in enter.typeEnvs is deliberately uninitialized,
        // because the annotations were not available at the time the env was created. Therefore,
        // we look up the environment chain for the first enclosing environment for which the
        // lint value is set. Typically, this is the parent env, but might be further if there
        // are any envs created as a result of TypeParameter nodes.
        F3Env<F3AttrContext> lintEnv = env;
        while (lintEnv.info.lint == null)
            lintEnv = lintEnv.next;
        Lint lint = lintEnv.info.lint.augment(v.attributes_field, v.flags());
        Lint prevLint = chk.setLint(lint);
        JavaFileObject prev = log.useSource(env.toplevel.sourcefile);

        try {
            Type declType = attribType(tree.getF3Type(), env);
	    if (declType instanceof WildcardType) {
		System.err.println(tree.getF3Type().getClass());
		if (tree.getF3Type() instanceof F3TypeClass) {
		    F3TypeClass c = (F3TypeClass)tree.getF3Type();
		    System.err.println("lower="+c.getClassName());
		    System.err.println("upper="+c.upperBound);
		}
		System.err.println("var "+tree+": "+declType.getClass()+": "+declType+ ": "+types.toF3String(declType));
	    }
            declType = chk.checkNonVoid(tree.getF3Type(), declType);
            if (declType != syms.f3_UnspecifiedType) {
                result = tree.type = v.type = declType;
            }
            // Check that the variable's declared type is well-formed.
	    //chk.validate(tree.vartype);

            Type initType;
            if (tree.getInitializer() == null && (tree.getModifiers().flags & F3Flags.IS_DEF) != 0) {
                log.error(tree.pos(), MsgSym.MESSAGE_F3_DEF_MUST_HAVE_INIT, v);
            }            
            if (tree.isBound() && (tree.getModifiers().flags & F3Flags.IS_DEF) != 0) {
                log.error(tree.pos(), MsgSym.MESSAGE_F3_DEF_MUST_HAVE_INIT, v);
            }            
            if (tree.getInitializer() != null) {
                if (tree.getInitializer().getF3Kind() == F3Kind.INSTANTIATE_OBJECT_LITERAL &&
                    (tree.getModifiers().flags & F3Flags.IS_DEF) != 0)                        
                    v.flags_field |= F3Flags.OBJ_LIT_INIT;
                // Attribute initializer in a new environment.
                // Check that initializer conforms to variable's declared type.
                Scope initScope = new Scope(new MethodSymbol(BLOCK, v.name, null, env.getEnclosingClassSymbol()));
                initScope.next = env.info.scope;
                F3Env<F3AttrContext> initEnv =
                    env.dup(tree, env.info.dup(initScope));
                initEnv.outer = env;
                initEnv.info.lint = lint;
                if ((tree.getModifiers().flags & STATIC) != 0)
                    initEnv.info.staticLevel++;

                // In order to catch self-references, we set the variable's
                // declaration position to maximal possible value, effectively
                // marking the variable as undefined.
                initEnv.info.enclVar = v;
		initEnv.enclVar = tree;

                initType = attribExpr(tree.getInitializer(), initEnv, declType);

		if (initEnv.implicitArgs.size() > 0) {
		    v.flags_field |= F3Flags.IMPLICIT_PARAMETER;
		    System.err.println("IMPLICIT VAR: "+v);
		}

		//System.err.println("initType "+ v + " = "+initType.getClass()+" "+initType);
                /*
                 * We introduce a synthetic variable for bound function result.
                 * See F3BoundContextAnalysis. If the type of that var is
                 * Void, then return statement will catch it and produce appropriate
                 * error message ("Bound function can not be void").
                 */
                if (tree.name != defs.boundFunctionResultName) {
                    initType = chk.checkNonVoid(tree.pos(), initType);
                }
                chk.checkType(tree.pos(), initType, declType,
			      types.isSequence(declType) ? Sequenceness.REQUIRED : Sequenceness.PERMITTED /* DISALLOWED */, false);
                chk.checkBidiBind(tree.getInitializer(),tree.getBindStatus(), initEnv, v.type);
            }
            else if (tree.type != null)
                initType = tree.type;
            else
                initType = syms.objectType;  // nothing to go on, so we assume Object
            if (declType == syms.f3_UnspecifiedType && v.type == null) {
                result = tree.type = v.type = initType;//types.normalize(initType);
            }
	    if ((tree.getModifiers().flags & F3Flags.IS_DEF) != 0) {
		if (false && result instanceof TypeVar) {
		    result = tree.type = v.type = new WildcardType(result, BoundKind.EXTENDS, syms.boundClass);
		    //System.err.println("result="+result);
		}
	    }
            //chk.validateAnnotations(tree.mods.annotations, v);
            chk.checkBoundArrayVar(tree);
        }
        finally {
            chk.setLint(prevLint);
            log.useSource(prev);
	    varSymToTree.remove(v);
        }
    }

    //@Override
    public void visitVarInit(F3VarInit tree) {
        result = tree.type = attribExpr(tree.getVar(), env);
    }

    //@Override
    public void visitVarRef(F3VarRef tree) {
        throw new AssertionError("Shouldn't be here!");
    }
            
    //@Override
    public void visitVar(F3Var tree) {
        long flags = tree.getModifiers().flags;
        Symbol sym = tree.sym;
        if (sym == null) {
            log.error(tree.pos(), MsgSym.MESSAGE_F3_VAR_NOT_SUPPORTED_HERE, (flags & F3Flags.IS_DEF) == 0 ? "var" : "const", tree.getName());
            return;
        }

        sym.complete();
        
        boolean isClassVar = env.info.scope.owner.kind == TYP;

	//System.err.println("flags "+sym.name+": "+F3TreeInfo.flagNames(flags));

        if (isClassVar && (flags & STATIC) == 0L) {
	    //System.err.println("isClassVar: "+sym.name);
            // Check that instance variables don't override
            chk.checkVarOverride(tree, (F3VarSymbol)sym);
        }

        //variable decl in bind context with no initializer are not allowed
        if ((tree.sym.flags_field & Flags.PARAMETER) == 0 &&
	    env.tree.getF3Tag() != F3Tag.FOR_EXPRESSION &&
	    tree.getInitializer() == null &&
	    tree.getBindStatus() != F3BindStatus.UNBOUND) {
            log.error(tree.pos(), MsgSym.MESSAGE_TRIGGER_VAR_IN_BIND_MUST_HAVE_INIT, tree.sym);
        }

        for (F3OnReplace.Kind triggerKind : F3OnReplace.Kind.values()) {
            F3OnReplace trigger = tree.getTrigger(triggerKind);
            boolean inInvalidate = triggerKind == F3OnReplace.Kind.ONINVALIDATE;
            if (trigger != null) {
                if (triggerKind == F3OnReplace.Kind.ONREPLACE) {
                    F3Var oldValue = trigger.getOldValue();
                    if (oldValue != null && oldValue.type == null) {
                            oldValue.type =  tree.type;
                    }

                    F3Var newElements = trigger.getNewElements();
                    if (newElements != null && newElements.type == null)
                        newElements.type = tree.type;
                } else if (triggerKind == F3OnReplace.Kind.ONINVALIDATE) {
//                    if ((sym.flags_field & F3Flags.VARUSE_BOUND_INIT) == 0) {
//                        log.error(trigger.pos(), MsgSym.MESSAGE_ON_INVALIDATE_UNBOUND_NOT_ALLOWED, sym);
//                    }
                }

                if (isClassVar) {
                        // let the owner of the environment be a freshly
                        // created BLOCK-method.
                        F3Env<F3AttrContext> localEnv = newLocalEnv(tree);
                        localEnv.info.inInvalidate = inInvalidate;
                        if (inInvalidate) {
                            localEnv.info.enclVar = sym;
                        }
                        if ((flags & STATIC) != 0) {
                            localEnv.info.staticLevel++;
                        }
                        attribDecl(trigger, localEnv);
                } else {
                        // Create a new local environment with a local scope.
                        F3Env<F3AttrContext> localEnv = env.dup(tree, env.info.dup(env.info.scope.dup()));
                        localEnv.info.inInvalidate = inInvalidate;
                        if (inInvalidate) {
                            localEnv.info.enclVar = sym;
                        }
                        attribDecl(trigger, localEnv);
                        localEnv.info.scope.leave();
                }

            }
        }
        warnOnStaticUse(tree.pos(), tree.getModifiers(), sym);        
	Type argtype = tree.type;
	F3Var pvar = tree;
	if ((tree.sym.flags_field & Flags.PARAMETER) != 0) {
	    boolean isCovariant = false;
	    if (argtype != null) {
		isCovariant = types.expandTypeVar(argtype).isExtendsBound();
	    }
	    if (argtype != null && isCovariant) {
		Type j = types.unexpandWildcard(argtype);
		if (j instanceof TypeVarDefn) {
		    System.err.println("owner=>"+j.tsym.owner);
		    System.err.println("sym=>"+tree.sym.owner.owner);
		    System.err.println("env=>"+env.outer);
		}
		log.warning(pvar.getF3Type().pos(),
			 "covariant.in.contravariant.pos", pvar.getF3Type());
	    }
	} else {
	    if (argtype != null && types.expandTypeVar(argtype).isSuperBound()) {
		log.warning(pvar.getF3Type().pos(),
			 "contravariant.in.covariant.pos", pvar.getF3Type());
	    }
	}
	if (argtype != null) {
	    if ((tree.sym.flags_field & (F3Flags.IS_DEF | F3Flags.PUBLIC_INIT | Flags.FINAL)) == 0) {
		Type expanded = types.expandTypeVar(argtype);
		if (expanded.isSuperBound()) {
		    log.warning(pvar.getF3Type().pos(),
			      "contravariant.in.invariant.pos", pvar.getF3Type());
		} else if (expanded.isExtendsBound()) {
		    log.warning(pvar.getF3Type().pos(),
				"covariant.in.invariant.pos", pvar.getF3Type());
		}
	    }
	}
        result = /*tree.isBound()? syms.voidType : */ tree.type;
    }

    private void warnOnStaticUse(DiagnosticPosition pos, F3Modifiers mods, Symbol sym) {
        // temporary warning for the use of 'static'
        if ((mods.flags & (STATIC | SCRIPT_LEVEL_SYNTH_STATIC)) == STATIC) {
            log.warning(pos, MsgSym.MESSAGE_F3_STATIC_DEPRECATED, sym);
        }
    }

    /**
     * OK, this is a not really "finish" as in the completer, at least not now.
     * But it does finish the attribution of the override by attributing the
     * default initialization.
     *
     * @param tree
     * @param env
     */
    public void finishOverrideAttribute(F3OverrideClassVar tree, F3Env<F3AttrContext> env) {
        F3VarSymbol v = tree.sym;
        Type declType = tree.getId().type;
        result = tree.type = declType;

        // Need to check that the override did not specify a different type
        // w.r.t. the one that comes from overridden variable
        Type f3Type = attribType(tree.getF3Type(), env);
        if (f3Type != syms.f3_UnspecifiedType &&
                !types.isSameType(declType, f3Type)) {
            chk.typeError(tree.getF3Type().pos(),
                    messages.getLocalizedString(MsgSym.MESSAGEPREFIX_COMPILER_MISC + 
                    MsgSym.MESSAGE_F3_TYPED_OVERRIDE),
                    f3Type,
                    declType);
        }
        else if (f3Type == syms.f3_UnspecifiedType) {
            tree.setF3Type(f3make.at(tree.pos).TypeClass(f3make.at(tree.pos).Type(declType),
                    types.isSequence(declType) ?
                        Cardinality.ANY :
                        Cardinality.SINGLETON));
        }

        chk.checkBoundArrayVar(tree);

        if (types.isSameType(env.getEnclosingClassType(), v.owner.type)) {
	    if (false) {
		log.error(tree.getId().pos(), MsgSym.MESSAGE_F3_CANNOT_OVERRIDE_OWN,tree.getId().getName());
	    }
        }

        // The info.lint field in the envs stored in enter.typeEnvs is deliberately uninitialized,
        // because the annotations were not available at the time the env was created. Therefore,
        // we look up the environment chain for the first enclosing environment for which the
        // lint value is set. Typically, this is the parent env, but might be further if there
        // are any envs created as a result of TypeParameter nodes.
        F3Env<F3AttrContext> lintEnv = env;
        while (lintEnv.info.lint == null) {
            lintEnv = lintEnv.next;
        }
        Lint lint = lintEnv.info.lint.augment(v.attributes_field, v.flags());
        Lint prevLint = chk.setLint(lint);
        JavaFileObject prev = log.useSource(env.toplevel.sourcefile);

        if ((v.flags() & F3Flags.IS_DEF) != 0L) {
            log.error(tree.getId().pos(), MsgSym.MESSAGE_F3_CANNOT_OVERRIDE_DEF,tree.getId().getName());
        } else if (!rs.isAccessibleForWrite(env, env.getEnclosingClassType(), v)) {
            log.error(tree.getId().pos(), MsgSym.MESSAGE_F3_CANNOT_OVERRIDE,tree.getId().getName());
        }

        //TODO: (below)
        /***
         * inBindContext is not implemented correctly here since things are not walked in tree order in finish*.
         *
            if (this.inBindContext) {
                v.flags_field |= F3Flags.VARUSE_BOUND_INIT;
            }
         * */
        try {
            F3Expression init = tree.getInitializer();
            if (init != null) {
                // Attribute initializer in a new environment/
                // Check that initializer conforms to variable's declared type.
                Scope initScope = new Scope(new MethodSymbol(BLOCK, v.name, null, env.getEnclosingClassSymbol()));
                initScope.next = env.info.scope;
                F3Env<F3AttrContext> initEnv =
                    env.dup(tree, env.info.dup(initScope));
                initEnv.outer = env;

                // In order to catch self-references, we set the variable's
                // declaration position to maximal possible value, effectively
                // marking the variable as undefined.
                v.pos = Position.MAXPOS;

                chk.checkNonVoid(init, attribExpr(init, initEnv, declType));
                chk.checkBidiBind(tree.getInitializer(), tree.getBindStatus(), initEnv, v.type);
            }
        } finally {
            chk.setLint(prevLint);
            log.useSource(prev);
        }
    }

    //@Override
    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        //TODO: handle static triggers
        F3Ident id = tree.getId();

        // let the owner of the environment be a freshly
        // created BLOCK-method.
        F3Env<F3AttrContext> localEnv = newLocalEnv(tree);
        //find overridden var
        Type type = null;
        Symbol idSym = rs.findIdentInType(localEnv, localEnv.enclClass.type, tree.getName(), VAR);
        if (idSym != null) {
            idSym.complete();
            id.sym = idSym;
            id.type = type = tree.type = idSym.type;
	    Type clazztype = localEnv.enclClass.type;
	    Type memberType = type;
	    try {
		memberType = types.memberType(clazztype, idSym);
	    } catch (NullPointerException exc) {
		System.err.println(idSym);
		exc.printStackTrace();
	    }
	    Symbol memberSym = idSym;
            id.type = type = tree.type = memberType;
	    F3VarSymbol idSym1 = new F3VarSymbol(types, names, idSym.flags_field, idSym.name, memberType, localEnv.getEnclosingClassSymbol());
	    idSym1.kind = idSym.kind;
	    id.sym = idSym1;
	    idSym = idSym1;
            if (idSym.kind < ERRONEOUS && idSym.kind == VAR) {
                tree.sym = (F3VarSymbol)idSym;
            }
            else {
                //we couldn't find the overridden var
                log.error(id.pos(), MsgSym.MESSAGE_F3_DECLARED_OVERRIDE_DOES_NOT, rs.kindName(VAR), tree.getName());
            }
        }

        for (F3OnReplace.Kind triggerKind : F3OnReplace.Kind.values()) {
            F3OnReplace trigger = tree.getTrigger(triggerKind);
            if (trigger != null) {
                if (triggerKind == F3OnReplace.Kind.ONINVALIDATE) {
                    localEnv.info.inInvalidate = true;
                    localEnv.info.enclVar = idSym;
                }

                F3Var oldValue = trigger.getOldValue();
                if (oldValue != null && oldValue.type == null) {
                    oldValue.type = type;
                }
                F3Var newElements = trigger.getNewElements();
                if (newElements != null && newElements.type == null) {
                    newElements.type = type;
                }
                attribDecl(trigger, localEnv);
            }
        }

        if (idSym.kind < ERRONEOUS) {
            // Must reference an attribute
            if (idSym.kind != VAR) {
                log.error(id.pos(), MsgSym.MESSAGE_F3_MUST_BE_AN_ATTRIBUTE,id.getName());
            } else if (localEnv.outer.tree.getF3Tag() != F3Tag.CLASS_DEF) {
                log.error(tree.pos(), MsgSym.MESSAGE_F3_CANNOT_OVERRIDE_CLASS_VAR_FROM_FUNCTION, idSym.name, idSym.owner);
            } else {
                F3VarSymbol v = (F3VarSymbol) idSym;
                if (v.isOverridenIn(env.getEnclosingClassSymbol())) {
                    log.error(tree.getId().pos(), MsgSym.MESSAGE_F3_DUPLICATE_VAR_OVERRIDE, idSym.name, idSym.owner);
                }
                else {
                    v.addOverridingClass(env.getEnclosingClassSymbol());
                    tree.sym = v;
                    if (tree.isBound()) {
                        // If it is overridden with a bound, it is a bound init
                        v.flags_field |= F3Flags.VARUSE_BOUND_INIT;
                    }
                    finishOverrideAttribute(tree, env);
                }
            }
        }
    }

    //@Override
    public void visitOnReplace(F3OnReplace tree) {
        Scope localScope = new Scope(new MethodSymbol(BLOCK, defs.lambda_MethodName, null, env.getEnclosingClassSymbol()));
        F3Env<F3AttrContext> localEnv = env.dup(tree, env.info.dup(localScope));        
        localEnv.outer = env;
        F3Var lastIndex = tree.getLastIndex();
        if (lastIndex != null) {
            lastIndex.mods.flags |= Flags.FINAL;
            attribVar(lastIndex, localEnv);
            lastIndex.sym.type = syms.intType;
        }
        F3Var newElements = tree.getNewElements();
        if (newElements != null) {
            newElements.mods.flags |= Flags.FINAL;
            attribVar(newElements, localEnv);
        }

        F3Var firstIndex = tree.getFirstIndex();
        if (firstIndex != null) {
            firstIndex.mods.flags |= Flags.FINAL;
            attribVar(firstIndex, localEnv);
            firstIndex.sym.type = syms.intType;
        }

        F3Var oldValue = tree.getOldValue();
	if (oldValue != null) {
            oldValue.mods.flags |= Flags.FINAL;
            attribVar(oldValue, localEnv);
        }        
        attribExpr(tree.getBody(), localEnv);
    }

    F3Expression accessThe(Symbol sym, Type expectedType, F3Env<F3AttrContext> env) {
	if (sym == null) return null;
	F3Expression exp;
	if ((sym.flags() & Flags.STATIC) != 0) {
	    exp = f3make.QualIdent(sym);
	} else {
	    exp = f3make.Ident(sym);
	}
	exp.type = sym.type;
	if (!types.isSubtype(sym.type, expectedType)) {
	    System.err.println("sym.type="+sym.type);
	    System.err.println("expectedType="+expectedType);
	}
	if (!types.isSubtype(sym.type, expectedType) && (sym instanceof MethodSymbol)) {
	    MethodSymbol msym = (MethodSymbol)sym;
	    if (sym instanceof F3Resolve.InstanceMethodSymbol) {
		msym = ((F3Resolve.InstanceMethodSymbol)sym).generic;
	    }
	    List<Type> targs = List.<Type>nil();
	    ForAll result = infer.instantiateExpr1(newForAll(msym.type.getTypeArguments(), msym.type.getReturnType()), expectedType, Warner.noWarnings);
	    System.err.println("infer.result: "+result);
	    targs = result.getTypeArguments();
	    List<F3Expression> texps = List.<F3Expression>nil();
	    for (Type t: targs) {
		texps = texps.append(f3make.Type(t));
	    }
	    exp = f3make.Apply(texps, exp, List.<F3Expression>nil());
	    System.err.println("accessThe.texps => "+texps);
	    System.err.println("accessThe => "+exp);
	    Type expType = attribExpr(exp, env);//
	    System.err.println("exp="+exp);
	    System.err.println("expType="+expType);
	}
	return exp;
    }

    private ArrayList<F3ForExpressionInClause> forClauses = null;    

    //@Override
    public void visitForExpression(F3ForExpression tree) {
        F3Env<F3AttrContext> forExprEnv =
            env.dup(tree, env.info.dup(env.info.scope.dup()));
        forExprEnv.outer = env;

        if (forClauses == null)
            forClauses = new ArrayList<F3ForExpressionInClause>();
        int forClausesOldSize = forClauses.size();
        Type clause1Type = null;
	Type functorType = null;
	Type monadType = null;
	Type comonadType = null;
	boolean isIter = tree.isIterable;
	boolean isSeq = false;
	int size = tree.getInClauses().size();
	int idx = 0;
        Type[] clauseTypes = new Type[tree.getInClauses().size()];
        F3Expression[] typeClasses = new F3Expression[tree.getInClauses().size()];
	boolean isComonad = false;
        for (ForExpressionInClauseTree cl : tree.getInClauses()) {
	    boolean last = idx + 1 == size;

            // Don't try to examine erroneous in clauses. We don't wish to
            // place the entire for expression into error nodes, just because
            // one or more in clauses was in error, so we jsut skip any
            // erroneous ones.
            //
            if  (cl instanceof F3ErroneousForExpressionInClause) continue;

            F3ForExpressionInClause clause = (F3ForExpressionInClause)cl;
            forClauses.add(clause);
            
            F3Var var = clause.getVar();

	    if (clause.intoVar != null) {
		isComonad = true;
	    }

	    if (var == null) {
		var = clause.intoVar;
	    }

            // Don't try to examine erroneous loop controls, such as
            // when a variable was missing. Again, this is because the IDE may
            // try to attribute a node that is mostly correct, but contains
            // one or more components that are in error.
            //
            if  (var == null || var instanceof F3ErroneousVar) continue;

            Type declType = attribType(var.getF3Type(), forExprEnv);
            attribVar(var, forExprEnv);
            F3Expression expr = (F3Expression)clause.getSequenceExpression();
            Type exprType = types.upperBound(attribExpr(expr, forExprEnv));
	    if (exprType instanceof ForAll) {
		exprType = syms.makeFunctionType((ForAll)exprType);
	    } else if (exprType instanceof MethodType) { // hack
		exprType = syms.makeFunctionType((MethodType)exprType);
	    }
            chk.checkNonVoid(((F3Tree)clause).pos(), exprType);
	    //System.err.println("exprType="+exprType);
            Type elemtype;
	    if (clause.intoVar != null) {
		if (!types.isComonad(exprType)) {
		    chk.typeError(clause, MsgSym.MESSAGE_INCOMPATIBLE_TYPES, exprType, syms.f3_ComonadType);
		    elemtype = exprType;
		} else {
		    elemtype = comonadType = exprType;
		    //elemtype = types.comonadElementType(exprType);
		}
		Type comonadTypeClass = types.comonadTypeClass(exprType);
		Symbol theOne = findThe(env, clause.getSequenceExpression(), comonadTypeClass);
		typeClasses[idx] = accessThe(theOne, comonadTypeClass, env);
	    } else      // if exprtype is T[], T is the element type of the for-each
		if (types.isSequence(exprType)) {
		    elemtype = types.elementType(exprType);
		    //elemtype = exprType.getTypeArguments().head;
		    isSeq = true;
		    if (last) {
			functorType = exprType;
		    } else {
			monadType = exprType;
		    }
		}
            // if exprtype implements Iterable<T>, T is the element type of the for-each
            else if (types.asSuper(exprType, syms.iterableType.tsym) != null) {
                if (clause.isBound()) {
                    log.error(clause.getSequenceExpression().pos(), MsgSym.MESSAGE_F3_FOR_OVER_ITERABLE_DISALLOWED_IN_BIND);
                }
                Type base = types.asSuper(exprType, syms.iterableType.tsym);
                List<Type> iterableParams = base.allparams();
                if (iterableParams.isEmpty()) {
                    elemtype = syms.objectType;
                } else {
                    elemtype = types.upperBound(iterableParams.last());
                }
		isIter = true;
            }
            //FIXME: if exprtype is nativearray of T, T is the element type of the for-each (see VSGC-2784)
            else if (types.isArray(exprType)) {
                elemtype = types.elemtype(exprType);
		//isIter = true;
            }
            else {
		Symbol theOne = null;
		if (!last) {
		    Type monadTypeClass = types.monadTypeClass(exprType);
		    theOne = findThe(env, clause.getSequenceExpression(), monadTypeClass);
		    typeClasses[idx] = accessThe(theOne, monadTypeClass, env);
		} else {
		    Type functorTypeClass = types.functorTypeClass(exprType);
		    theOne = findThe(env, clause.getSequenceExpression(), functorTypeClass);
		    typeClasses[idx] = accessThe(theOne, functorTypeClass, env);
		}
		if (theOne == null && ( (last && !types.isFunctor(exprType)) || (!last && !types.isMonad(exprType)) )) {
		    log.warning(expr, MsgSym.MESSAGE_F3_ITERATING_NON_SEQUENCE);
		    elemtype = exprType;
		    isIter = true;
		} else {
		    if (!last || monadType != null) {
			if (monadType == null) {
			    monadType = exprType;
			} else {
			    monadType = types.lub(monadType, exprType);
			    //monadType = exprType;
			}
			elemtype = types.monadElementType(exprType);
			//System.err.println("monadType="+exprType);
			//System.err.println("exprType="+exprType);
			//System.err.println("theMonad="+theOne);
			//System.err.println("monadType="+monadType);
			if (theOne != null) {
			    Type tcons = types.asSuper(exprType, syms.f3_TypeCons[1].tsym);
			    if (tcons != null) {
				//System.err.println("tcons="+tcons);
				List<Type> targs = tcons.getTypeArguments();
				if (targs.nonEmpty()) {
				    monadType = targs.get(0);
				    Type altElemType = targs.get(1);
				    if (elemtype == null) {
					elemtype = altElemType;
				    }
				    //System.err.println("ELEM TYPE="+altElemType);
				}
			    }
			}
		    } else {
			//System.err.println("exprType="+exprType);
			//System.err.println("theFunctor="+theOne);
			functorType = exprType;
			elemtype = types.functorElementType(exprType);
			//System.err.println("functorType="+exprType);
			if (theOne != null) {
			    Type superType = types.asSuper(exprType, syms.f3_TypeCons[1].tsym);
			    if (superType != null) {
				List<Type> targs = superType.getTypeArguments();
				functorType = targs.get(0);
				Type altElemType = targs.get(1);
				if (elemtype == null) {
				    elemtype = altElemType;
				}
			    }
			    //System.err.println("ELEM TYPE="+altElemType);
			}
		    }
		}
            }
	    if (elemtype == null) {
		System.err.println("elem type is null "+exprType.getClass());
		System.err.println("isFunctor: "+ types.isFunctor(exprType));
		System.err.println("Functor element: "+ types.functorElementType(exprType));
		//System.err.println("functor="+types.getFunctor(exprType));
		elemtype = syms.objectType;
	    }
            if (elemtype == syms.errType) {
                log.error(clause.getSequenceExpression().pos(), MsgSym.MESSAGE_FOREACH_NOT_APPLICABLE_TO_TYPE);
            } else if (elemtype == syms.botType || elemtype == syms.unreachableType) {
                elemtype = syms.objectType;
            } else {
                // if it is a primitive type, unbox it
                Type unboxed = types.unboxedType(elemtype);
                if (unboxed != Type.noType) {
                    elemtype = unboxed;
                }
                chk.checkType(clause.getSequenceExpression().pos(), elemtype, declType, Sequenceness.DISALLOWED);
            }
            if (declType == syms.f3_UnspecifiedType) {
                var.type = elemtype;
                var.sym.type = elemtype;
            }
            //clauseTypes[clauseTypes.length-1-idx] = elemtype;
            clauseTypes[idx] = elemtype;
	    if (clause1Type == null) {
		clause1Type = elemtype;
	    } else {
		clause1Type = types.lub(clause1Type, elemtype);
	    }
	    //System.err.println("clause1Type="+clause1Type);
            if (clause.getWhereExpression() != null) {
                attribExpr(clause.getWhereExpression(), env, syms.booleanType);
            }
	    idx++;
        }
        forExprEnv.tree = tree; // before, we were not in loop!
	Type bt = pt;
	bt = types.capture(bt);
	if (types.isSequence(bt)) {
	    bt = types.elementType(bt);
	} else {
	    bt = syms.unknownType;
	}
	boolean rawForLoop = isIter || isSeq;
	if (rawForLoop) {
	    attribTree(tree.getBodyExpression(), forExprEnv, VAL, bt.tag != ERROR ? bt : Type.noType, rawForLoop ? Sequenceness.PERMITTED : Sequenceness.DISALLOWED);

	    if (rawForLoop) {
		if (!tree.getBodyExpression().type.isErroneous() &&
		    tree.getBodyExpression().type.tag != VOID &&
		    (pt.tag == NONE ||
		     pt == syms.f3_UnspecifiedType)) {
		    Type blockElemType = types.elementTypeOrType(tree.getBodyExpression().type);
		    Type typeToCheck = types.sequenceType(types.normalize(blockElemType));
		    attribTree(tree.getBodyExpression(), forExprEnv, VAL, typeToCheck, 
			       (rawForLoop) ? Sequenceness.PERMITTED : Sequenceness.DISALLOWED);
		}
	    }
	} else {
	    attribTree(tree.getBodyExpression(), forExprEnv, VAL, Type.noType);
	}
        Type bodyType = tree.getBodyExpression().type;
        if (bodyType == syms.unreachableType)
            log.error(tree.getBodyExpression(), MsgSym.MESSAGE_UNREACHABLE_STMT);
        Type owntype = (tree.isIterable || bodyType == null || bodyType == syms.voidType)?
            syms.voidType :
            types.isSequence(bodyType) ?
            bodyType :
            types.sequenceType(bodyType);


	boolean domap = !isIter;
	if (isSeq) {
	    domap = tree.isBound();
	}
        if (domap) {
            F3Expression map = null;
	    if (false && ((types.isSequence(owntype) && tree.isBound()))) {
		map = tree.getMap(f3make, names, clause1Type ,
				  types.isSequence(bodyType) ? types.elementType(bodyType) : bodyType);
	    } else {
		if (isComonad || comonadType != null) {
		    Type comonadElementType = clause1Type;
		    map = tree.getComonadMap(f3make, 
					     names, 
					     types, 
					     clauseTypes,
					     typeClasses,
					     tree.isBound());
		    //System.err.println(map);
		} else {
		    Type monadElementType = clause1Type;
		    Type typeCons = monadType == null ? functorType: monadType;
		    if (tree.isBound() || !isIter) {
			map = tree.getMonadMap(f3make, names, types, clauseTypes, typeClasses,
					       typeCons,
					       bodyType,
					       tree.isBound());
			//System.err.println(map);
		    }
		}
	    }
            if (map != null) {
                owntype = attribExpr(map, env);
            }
        }
        while (forClauses.size() > forClausesOldSize)
            forClauses.remove(forClauses.size()-1);
        forExprEnv.info.scope.leave();
	tree.type = owntype;
        result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
    }

    //@Override
    public void visitForExpressionInClause(F3ForExpressionInClause that) {
        // Do not assert that we cannot reach here as this unit can
        // be visited by virtue of visiting F3Erronous which
        // will attempt to visit each Erroneous node that it has
        // encapsualted.
        //
    }

    public void visitIndexof(F3Indexof tree) {
        for (int n = forClauses == null ? 0 : forClauses.size(); ; ) {
            if (--n < 0) {
                 log.error(tree.pos(), MsgSym.MESSAGE_F3_INDEXOF_NOT_FOUND,tree.fname.getName());
                 break;
            }
            F3ForExpressionInClause clause = forClauses.get(n);

            // Don't try to examine erroneous in clauses. We don't wish to
            // place the entire for expression into error nodes, just because
            // one or more in clauses was in error, so we jsut skip any
            // erroneous ones.
            //
            if  (clause == null || clause instanceof F3ErroneousForExpressionInClause) continue;

            F3Var v = clause.getVar();

            // Don't try to deal with Erroneous or missing variables
            //
            if (v == null || v instanceof F3ErroneousVar) continue;
            
            if (clause.getVar().getName() == tree.fname.getName()) {
                tree.clause = clause;
                tree.fname.sym = clause.getVar().sym;
                clause.setIndexUsed(true);
                break;
            }
        }
        result = check(tree, syms.f3_IntegerType, VAL,
                pkind, pt, pSequenceness);
    }

    //@Override
    public void visitSkip(F3Skip tree) {
        result = syms.voidType;
        tree.type = result;
    }

    //@Override
    public void visitBlockExpression(F3Block tree) {
        // Create a new local environment with a local scope.
        F3Env<F3AttrContext> localEnv;
        if (env.info.scope.owner.kind == TYP) {
            // Block is a static or instance initializer;
            // let the owner of the environment be a freshly
            // created BLOCK-method.
            localEnv = newLocalEnv(tree);
            if ((tree.flags & STATIC) != 0) {
                localEnv.info.staticLevel++;
            }
        } else {
            Scope localScope = new Scope(env.info.scope.owner);
            localScope.next = env.info.scope;
            localEnv = env.dup(tree, env.info.dup(localScope));
            localEnv.outer = env;
            if (env.tree instanceof F3FunctionDefinition) {
                if (env.enclClass.runMethod == env.tree)
                    env.enclClass.runBodyScope = localEnv.info.scope;
                localEnv.info.scope.owner = env.info.scope.owner;
            }
            else {
                localEnv.info.scope.owner = new MethodSymbol(BLOCK, names.empty, null, env.getEnclosingClassSymbol());
                }
            }
        memberEnter.memberEnter(tree.getStmts(), localEnv);
        if (tree.getValue() != null) {
            memberEnter.memberEnter(tree.getValue(), localEnv);
        }
        boolean canReturn = true;
        boolean unreachableReported = false;
        tree.type = syms.f3_UnspecifiedType;
        for (List<F3Expression> l = tree.stats; l.nonEmpty(); l = l.tail) {
            if (! canReturn && ! unreachableReported) {
                unreachableReported = true;
                log.error(l.head.pos(), MsgSym.MESSAGE_UNREACHABLE_STMT);
            }
            Type stype = attribExpr(l.head, localEnv);
            if (stype == syms.unreachableType)
                canReturn = false;
        }
        Type owntype = null;
        if (tree.value != null) {
            if (!canReturn && !unreachableReported) {
                log.error(tree.value.pos(), MsgSym.MESSAGE_UNREACHABLE_STMT);
            }
            Type valueType = attribExpr(tree.value, localEnv, pt, pSequenceness);
            
            if (valueType == syms.voidType &&
                    !tree.isVoidValueAllowed) {
                //void value not allowed in a block that has non-void return
                log.warning(tree.value.pos(), MsgSym.MESSAGE_F3_VOID_BLOCK_VALUE_NOT_ALLOWED);
                owntype = tree.type;
            }
            else {
                owntype = valueType != syms.unreachableType ?
                    unionType(tree.pos(), tree.type, valueType) :
                    syms.unreachableType;
            }
        }        
        if (owntype == null) {
            owntype = syms.voidType;
        }
        if (!canReturn) {
            owntype = syms.unreachableType;
        }
        owntype = owntype.baseType();
        result = check(tree, owntype, VAL, pkind, pt, pSequenceness, false);
        if (env.info.scope.owner.kind != TYP)
            localEnv.info.scope.leave();
    }

    /**
     * @param tree
     */
    //@Override
    public void visitWhileLoop(F3WhileLoop tree) {
        attribExpr(tree.cond, env, syms.booleanType);
        attribExpr(tree.body, env.dup(tree));
        result = syms.voidType;
        tree.type = result;
    }

    //@Override
    public void visitInstanciate(F3Instanciate tree) {
        Type owntype = syms.errType;

        // The local environment of a class creation is
        // a new environment nested in the current one.
        F3Env<F3AttrContext> localEnv = newLocalEnv(tree);


        // The anonymous inner class definition of the new expression,
        // if one is defined by it.
        F3ClassDeclaration cdef = tree.getClassBody();

        // If enclosing class is given, attribute it, and
        // complete class name to be fully qualified
        F3Expression clazz = tree.getIdentifier(); // Class field following new

	List<F3Expression> typeArgs = null;
	if (clazz instanceof F3Ident) {
	    typeArgs = ((F3Ident)clazz).typeArgs;
	} else if (clazz instanceof F3Select) {
	    typeArgs = ((F3Select)clazz).typeArgs;
	} else {
	    //System.err.println("unhandled case: "+ clazz);
	}
	Type clazztype;
	List<Type> typeArgTypes = null;
	List<F3Expression> args = tree.getArgs();
	List<Type> argTypes = null;
	if (tree.genericInstance) { // hack
	    clazztype = attribSuperType(clazz, env);
	    argTypes = attribTypes(args, env);
	    args = List.<F3Expression>nil();
	} else {
	    // Attribute clazz expression
	    clazztype = attribSuperType(clazz, env);
	    if (false && typeArgs != null) {
		if (tree.typeArgTypes == null) {
		    typeArgTypes = attribTypeParams(typeArgs, env);
		    tree.typeArgTypes = typeArgTypes;
		} else {
		    typeArgTypes = tree.typeArgTypes;
		}
	    }
	    //System.err.println("clazztype="+clazztype);
	    // MAYBE FUTURE, e.g. if we support the syntax 'new ARRAY_TYPE (COUNT)':
	    if (tree.getF3Kind() == F3Kind.INSTANTIATE_NEW &&
                clazztype.tag == ARRAY) {
		if (tree.getArgs().size() != 1)
		    ;//log.error(tree.pos(), MsgSym.MESSAGE_F3_NEW_ARRAY_MUST_HAVE_SINGLE_ARG);
		else
		    attribExpr(tree.getArgs().head, env, syms.f3_IntegerType);
		result = check(tree, clazztype, VAL, pkind, pt, pSequenceness);
		tree.type = result;
		localEnv.info.scope.leave();
		return;
	    }
	}
	//System.err.println("clazztype="+clazztype);
        /*
        If so, add to MsgSym.java this definition:
        and in f3compiler.properties map that to:
        Allocating a native array requires a single length parameter.
        */
        List<F3Var> vars = tree.getLocalvars();
        memberEnter.memberEnter(vars, localEnv);

        // Store symbol + type back into the attributed tree.
        clazztype = chk.checkClassType(
            clazz.pos(), clazztype, true);
        chk.validate(clazz);
        if (!clazztype.tsym.isInterface() &&
                   clazztype.getEnclosingType().tag == CLASS) {
            // Check for the existence of an apropos outer instance
            rs.resolveImplicitThis(tree.pos(), env, clazztype);
        }
	if (false) {
	    List<Type> baseTypeArgs = clazz.type.tsym.type.getTypeArguments();
	    System.err.println("base type args: "+baseTypeArgs);
	    for (List<Type> x = baseTypeArgs, y = clazz.type.getTypeArguments(); x != null && y != null && x.head != null && y.head != null; x = x.tail, y = y.tail) {
		Type z = x.head;
		System.err.println("z="+z);
		if (z instanceof TypeVarDefn) {
		    TypeVarDefn def = (TypeVarDefn)z;
		    y.head = new WildcardType(y.head, def.variance, syms.boundClass); 
		    typeArgTypes = clazz.type.getTypeArguments();
		}
	    }
	}
	/*
	if (typeArgTypes != null && typeArgTypes.size() > 0) {
	    clazztype = clazz.type = new ClassType(clazz.type.getEnclosingType(), typeArgTypes, clazz.type.tsym);
	}
	*/
        // Attribute constructor arguments.
        List<Type> argtypes = attribArgs(args, localEnv);

        // If we have made no mistakes in the class type...
        if (clazztype.tag == CLASS) {
            // Check that class is not abstract or mixin
            long flags = clazztype.tsym.flags();
            if ((cdef == null &&
			  (flags & (ABSTRACT | INTERFACE | F3Flags.MIXIN)) != 0)) {
		/*
                if ((flags & (F3Flags.MIXIN)) != 0) {
                    // VSGC-2815 - new expressions should report an error when trying to instantiate a mixin class.
                    log.error(tree.pos(), MsgSym.MESSAGE_F3_MIXIN_CANNOT_BE_INSTANTIATED,
                              clazztype.tsym);
                } else {
                    log.error(tree.pos(), MsgSym.MESSAGE_ABSTRACT_CANNOT_BE_INSTANTIATED,
                              clazztype.tsym);
                }
		*/
            } else if (cdef != null && clazztype.tsym.isInterface()) {
                // Check that no constructor arguments are given to
                // anonymous classes implementing an interface
                if (!argtypes.isEmpty())
                    log.error(tree.getArgs().head.pos(), MsgSym.MESSAGE_ANON_CLASS_IMPL_INTF_NO_ARGS);


                // Error recovery: pretend no arguments were supplied.
                argtypes = List.nil();
            } else if (types.isF3Class(clazztype.tsym) && tree.getArgs().nonEmpty()) {
		//try to map args to members
		List<F3VarSymbol> initvars = List.<F3VarSymbol>nil();
		for (Type clazz1: types.supertypesClosure(clazztype, true)) {
		    for (Scope.Entry ent = clazz1.tsym.members().elems; ent != null && ent.scope != null; ent = ent.sibling) {
			if (ent.sym instanceof F3VarSymbol) {
			    F3VarSymbol varSym = (F3VarSymbol)ent.sym;
			    if ((varSym.flags() & F3Flags.PUBLIC_INIT) != 0) {
				System.err.println(varSym.name);
				initvars = initvars.prepend(varSym);
			    }
			}
		    }
		}
		System.err.println("vars="+initvars);
		if (initvars.size() == tree.getArgs().size()) {
		    List<F3ObjectLiteralPart> initparts = List.<F3ObjectLiteralPart>nil();
		    List<F3Expression> vargs = tree.getArgs();
		    for (List<F3VarSymbol> vl = initvars; vl.nonEmpty(); vl = vl.tail, vargs = vargs.tail) {
			F3VarSymbol v = vl.head;
			initparts = initparts.append(f3make.at(vargs.head.pos()).ObjectLiteralPart(v.name, vargs.head, F3BindStatus.UNBOUND));
		    }
		    tree.parts = initparts;
		} else {
		    log.error(tree.getArgs().head.pos(), MsgSym.MESSAGE_NEW_F3_CLASS_NO_ARGS);
		}
            }

            // Resolve the called constructor under the assumption
            // that we are referring to a superclass instance of the
            // current instance (JLS ???).
            else {
                localEnv.info.selectSuper = cdef != null &&
                        cdef.getMembers().nonEmpty();
                localEnv.info.varArgs = false;

                if (! types.isF3Class(clazztype.tsym)) {
		    try {
			tree.constructor = rs.resolveConstructor(
								 tree.pos(), localEnv, clazztype, argtypes, null);
		    } catch (NullPointerException exc) {
			System.err.println("tree is "+tree);
			exc.printStackTrace();
		    }
		}
                /**
                List<Type> emptyTypeargtypes = List.<Type>nil();
                tree.constructor = rs.resolveConstructor(
                    tree.pos(), localEnv, clazztype, argtypes, emptyTypeargtypes);
                Type ctorType = checkMethod(clazztype,
                                            tree.constructor,
                                            localEnv,
                                            tree.getArguments(),
                                            argtypes,
                                            emptyTypeargtypes,
                                            localEnv.info.varArgs);
                if (localEnv.info.varArgs)
                    assert ctorType.isErroneous();
                 * ***/

            }

            if (((ClassSymbol) clazztype.tsym).fullname == defs.cJavaLangThreadName) {
                log.warning(tree.pos(), MsgSym.MESSAGE_F3_EXPLICIT_THREAD);
            }
	    
	    //System.err.println("cdef="+cdef);

            if (cdef != null) {
                // We are seeing an anonymous class instance creation.
                // In this case, the class instance creation
                // expression
                //
                //    E.new <typeargs1>C<typargs2>(args) { ... }
                //
                // is represented internally as
                //
                //    E . new <typeargs1>C<typargs2>(args) ( class <empty-name> { ... } )  .
                //
                // This expression is then *transformed* as follows:
                //
                // (1) add a STATIC flag to the class definition
                //     if the current environment is static
                // (2) add an extends or implements clause
                // (3) add a constructor.
                //
                // For instance, if C is a class, and ET is the type of E,
                // the expression
                //
                //    E.new <typeargs1>C<typargs2>(args) { ... }
                //
                // is translated to (where X is a fresh name and typarams is the
                // parameter list of the super constructor):
                //
                //   new <typeargs1>X(<*nullchk*>E, args) where
                //     X extends C<typargs2> {
                //       <typarams> X(ET e, args) {
                //         e.<typeargs1>super(args)
                //       }
                //       ...
                //     }
                if (cdef.sym == null) {
                    enter.classEnter(cdef, env);
		}
		ClassSymbol c = cdef.sym;
		ClassType ct = (ClassType)c.type;
		attribDecl(cdef, localEnv);
                clazztype = cdef.sym.type;
                Symbol sym = rs.resolveConstructor(
                    tree.pos(), localEnv, clazztype, argtypes,
                    List.<Type>nil(), true, false);

                tree.constructor = sym;
            }

	    //         if (tree.constructor != null && tree.constructor.kind == MTH)
	    owntype = clazz.type;  // this give declared type, where clazztype would give anon type
        }
        for (List<F3Var> l = vars; l.nonEmpty(); l = l.tail)
            attribExpr(l.head, localEnv);
	tree.type = owntype;
	Type clazztype0 = clazztype;
	//System.err.println("owntype="+owntype);
	clazz.type = clazztype = tree.type = owntype;

	Symbol ownerSym = clazztype.tsym;

        Scope partsScope = new Scope(clazztype.tsym);

	java.util.Set<Name> seen = new java.util.HashSet<Name>();

        for (F3ObjectLiteralPart localPt : tree.getParts()) {
            
            // Protect against erroneous nodes
            //
            if (localPt == null) continue;



            F3ObjectLiteralPart part = (F3ObjectLiteralPart)localPt;

	    //System.err.println("part="+part);

            Symbol memberSym = rs.findIdentInType(env, clazz.type, part.name, VAR);
            memberSym = rs.access(memberSym, localPt.pos(), clazz.type, part.name, true);
            memberSym.complete();

	    //System.err.println("memberSym="+memberSym.getClass()+": "+memberSym);

            Scope.Entry oldEntry = partsScope.lookup(memberSym.name);
            if (oldEntry.sym != null) {
                log.error(localPt.pos(), MsgSym.MESSAGE_F3_ALREADY_DEFINED_OBJECT_LITERAL, memberSym);
            }
            partsScope.enter(memberSym);

            Type memberType = types.memberType(clazz.type, memberSym);
	    //System.err.println("clazz.type="+clazz.type);
	    //System.err.println("memberSym="+memberSym);
	    //System.err.println("memberType="+memberType);

            Scope initScope = new Scope(new MethodSymbol(BLOCK, memberSym.name, null, env.getEnclosingClassSymbol()));
            initScope.next = env.info.scope;
            F3Env<F3AttrContext> initEnv =
                env.dup(localPt, env.info.dup(initScope));
            initEnv.outer = localEnv;

            // Protect against erroneous tress called for attribution from the IDE
            //
            if (part.getExpression() != null) {
                attribExpr(part.getExpression(), initEnv, memberType);
                if (types.isArray(part.getExpression().type) &&
                    part.isBound()) {
                    log.warning(part.pos(), MsgSym.MESSAGE_F3_UNSUPPORTED_TYPE_IN_BIND);
                }
            }
	    Type x = memberType;
	    if (x instanceof FunctionType) {
		x = ((FunctionType)x).asMethodOrForAll();
	    }
	    if (memberSym instanceof MethodSymbol) {
		if (cdef == null) {
		    long innerClassFlags = Flags.SYNTHETIC | Flags.FINAL; // to enable, change to Flags.FINAL
		    cdef = f3make.at(tree.pos()).ClassDeclaration(f3make.Modifiers(innerClassFlags),
								  f3make.objectLiteralClassName(ownerSym.name),
								  List.<F3Expression>of(f3make.Type(ownerSym.type)),
								  List.<F3Tree>nil());
		    
		    if (cdef.sym == null) {
			enter.classEnter(cdef, env);
		    }
		    cdef.sym.complete();
		    attribSupertypes(cdef, cdef.sym);
		    cdef.type = cdef.sym.type;
		    System.err.println("cdef.type="+cdef.type);
		    types.addF3Class(cdef.sym, cdef);
		}
		MethodSymbol methSym = new MethodSymbol(F3Flags.OVERRIDE, 
							memberSym.name, 
							part.getExpression().type, 
							cdef.sym);
		System.err.println("method type: "+ methSym.type);
		System.err.println("member method type: "+ memberSym.type);
		System.err.println("member method type': "+ memberType);
		Type expType = part.getExpression().type;
		System.err.println("expType="+expType.getClass()+": "+expType);
		expType = reader.translateType(types.normalize(expType, false));
		System.err.println("expType="+expType.getClass()+": "+expType);
		if (expType instanceof FunctionType) {
		    expType = ((FunctionType)expType).asMethodOrForAll();
		}
		System.err.println("expType: "+ expType);
		Symbol inherited = rs.resolveQualifiedMethod(part.pos(), env, 
							     ownerSym.type, 
							     memberSym.name,
							     expType);
		System.err.println("inherited: "+inherited);
		if (inherited instanceof MethodSymbol) {
		    seen.add(memberSym.name);
		    part.type = memberType;
		    part.expr.type = memberType;
		    part.sym = memberSym;
		    part.isMethodDef = true;
		    System.err.println("inherited="+inherited.getClass()+": "+inherited);
		    if (tree.def == null) {
			tree.def = cdef;
		    }
		    cdef.sym.members_field.enter(memberSym);
		    continue;
		}
	    }
            if (memberSym instanceof F3VarSymbol) {
                F3VarSymbol v = (F3VarSymbol) memberSym;
                if (v.isStatic()) {
                    log.error(localPt.pos(), MsgSym.MESSAGE_F3_CANNOT_INIT_STATIC_OBJECT_LITERAL, memberSym);
                }
                WriteKind kind = part.isExplicitlyBound() ? WriteKind.INIT_BIND : WriteKind.INIT_NON_BIND;                
                chk.checkAssignable(part.pos(), v, part, clazz.type, localEnv, kind);
                chk.checkBidiBind(part.getExpression(), part.getBindStatus(), localEnv, v.type);
            }
	    seen.add(memberSym.name);
            part.type = memberType;
	    memberSym = new F3VarSymbol(types, names, memberSym.flags(), memberSym.name, memberType, ownerSym);
            part.sym = memberSym;
        }
	{
	    List<F3ObjectLiteralPart> newParts = List.<F3ObjectLiteralPart>nil();
	    for (Type clazz1: types.supertypesClosure(clazz.type, true)) {
		clazz1.tsym.complete();
		for (Scope.Entry ent = clazz1.tsym.members().elems; ent != null && ent.scope != null; ent = ent.sibling) {
		    if (ent.sym instanceof F3VarSymbol) {
			F3VarSymbol varSym = (F3VarSymbol)ent.sym;
			if ((varSym.flags() & STATIC) == 0) {
			    varSym.complete();			
			}
			if (!seen.contains(varSym.name)) {
			    seen.add(varSym.name);
			    if ((varSym.flags() & F3Flags.IMPLICIT_PARAMETER) != 0) {
				Type memberType = types.memberType(clazz.type, varSym);
				System.err.println("varSym="+varSym+ ":"+memberType);
				Symbol toAssign = findThe(localEnv, tree, memberType, false);
				if (toAssign != null && toAssign.kind < AMBIGUOUS) {
				    //F3Expression exp = f3make.QualIdent(toAssign);
				    F3Expression exp = accessThe(toAssign, varSym.type, localEnv);
				    attribExpr(exp, localEnv, memberType);
				    F3ObjectLiteralPart newPart = f3make.ObjectLiteralPart(varSym.name,
											   exp,
											   F3BindStatus.UNBOUND);
				    newPart.sym = varSym;
				    newPart.type = memberType;
				    newParts = newParts.append(newPart);
				} else {
				    log.error(tree.pos(), "the.not.found", varSym.name, clazz, types.toF3String(memberType));
				}
			    }
			}
		    }
		}
	    }
	    tree.parts = tree.parts.appendList(newParts);
	}
	long flags = clazztype.tsym.flags();
	if ((cdef == null &&
	     (flags & (ABSTRACT | INTERFACE | F3Flags.MIXIN)) != 0)) {
	    if ((flags & (F3Flags.MIXIN)) != 0) {
		// VSGC-2815 - new expressions should report an error when trying to instantiate a mixin class.
		log.error(tree.pos(), MsgSym.MESSAGE_F3_MIXIN_CANNOT_BE_INSTANTIATED,
			  clazztype.tsym);
	    } else {
		log.error(tree.pos(), MsgSym.MESSAGE_ABSTRACT_CANNOT_BE_INSTANTIATED,
			  clazztype.tsym);
	    }
	}
        result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
        localEnv.info.scope.leave();
	//	chk.checkAllDefined(tree.pos(), (ClassSymbol)clazz.type.tsym);
    }
    /** Make an attributed null check tree.
     */
    public F3Expression makeNullCheck(F3Expression arg) {
        // optimization: X.this is never null; skip null check
        Name name = F3TreeInfo.name(arg);
        if (name == names._this || name == names._super) return arg;

        F3Tag optag = F3Tag.NULLCHK;
        F3Unary tree = f3make.at(arg.pos).Unary(optag, arg);
        tree.operator = syms.nullcheck;
        tree.type = arg.type;
        return tree;
    }

    //@Override
    public void visitFunctionValue(F3FunctionValue tree) {
        F3FunctionDefinition def = new F3FunctionDefinition(f3make.Modifiers((tree.mods.flags&F3Flags.BOUND)|Flags.SYNTHETIC), defs.lambda_MethodName, tree);
        def.pos = tree.pos;
        tree.definition = def;
	def.typeArgs = tree.typeArgs;
	def.infer = tree.infer;
        MethodSymbol m = new MethodSymbol(SYNTHETIC, def.name, null, env.getEnclosingClassSymbol());
        // m.flags_field = chk.checkFlags(def.pos(), def.mods.flags, m, def);
        def.sym = m;
	if (tree.infer) {
	    result = tree.type = syms.botType;
	    return;
	}
	finishFunctionDefinition(def, env);
	if (def.typeArgTypes != null) {
	    List<Type> typeArgTypes = def.typeArgTypes;
	    for (List l = typeArgTypes; l != null; l = l.tail) {
		if (l.head instanceof TypeVar) {
		    TypeVar tv = (TypeVar)l.head;
		    tv.lower = 
			new WildcardType(Type.noType,
					 BoundKind.UNBOUND,
					 syms.boundClass,
					 tv);
		}
	    }
	}
	FunctionType ftype;
	if (def.type == null) {
	    System.err.println("type is null: "+ tree);
	}
	if (def.type instanceof MethodType) {
	    ftype = syms.makeFunctionType(def.type.asMethodType());
	} else if (def.type instanceof FunctionType) {
	    ftype = (FunctionType)def.type;
	} else {
	    ForAll fa = (ForAll)def.type;
	    ftype = syms.makeFunctionType(fa.asMethodType());
	    ftype.typeArgs = fa.getTypeArguments();
	}
	Type req = pt;
	if (req.getTypeArguments().size() > 0) {
	    req = newForAll(req.getTypeArguments(), req);
	}
	result = check(tree, ftype, VAL, pkind, req, pSequenceness);
	if (def.type instanceof ForAll) {
	    result = capture(def.type);
	}
    }

    public static class ConstI extends TypeVar {
	public int i;
	public ConstI(int i, TypeVar var) {
	    super(var.tsym.name, var.tsym, var.lower);
	    bound = var.bound;
	    this.i = i;
	}
        //public Type withTypeVar(Type t) {
	    //System.err.println("with type var: "+ this + ": "+t.getClass()+ ": "+t);
	//return this;
	//}
    }

    public static class TypeVarDefn extends TypeVar {
	final public TypeVar base;
	final public BoundKind variance;
	public String toString() {
	    return "type var definition "+ base + " "+variance;
	}
	public TypeVarDefn(TypeVar base, BoundKind variance) {
	    super(base.tsym, base.bound, base.lower);
	    this.base = base;
	    this.variance = variance;
	    base.tsym.type = this;
	    if (bound == null) {
		throw new NullPointerException("bound is null: "+ base.tsym.name);
	    }
	}
    }

    public static class TypeCons extends TypeVar {
	public Type ctor;
	public List<Type> args;
	public List<Type> getTypeArguments() {
	    return args;
	}
	public Type upperBound() {
	    return this;
	}
	public TypeCons(Name name, Symbol sym, Type bound, List args) {
	    super(name, sym, bound);
	    this.args = args;
	}
        public Type withTypeVar(Type t) {
	    //System.err.println("with type var: "+ this + ": "+t.getClass()+ ": "+t);
	    return super.withTypeVar(t);
	}
	
	public String toString() {
	    return super.toString() +" ctor="+ctor+" args="+args;
	}
    }

    Type makeTypeVar(F3Expression exp, Symbol sym) {
	Type result = makeTypeVar0(exp, sym);
	if (result == null) {
	    return syms.botType;
	}
	if (result instanceof TypeVar) {
	    TypeVar tv = (TypeVar)result;
	    if (tv.bound == null) {
		throw new NullPointerException("bound is null: "+ tv.tsym.name);
	    }
	}
	//System.err.println("make type var: "+ exp.getClass() +" "+exp + " => "+ types.toF3String(result));
	return result;
    }

    Type makeTypeVar0(F3Expression exp, Symbol sym) {
	long flags = 0;
	//System.err.println("make type var: "+ exp.getClass() + exp);
	if (exp instanceof F3TypeCons) {
	    // class Foo of (class F of X, X) =>
	    // class Foo of (F extends TypeCons1(F, X), X)
	    F3TypeCons cons = (F3TypeCons)exp;
	    exp = cons.getClassName();
	    F3Ident ident = (F3Ident)exp;
	    TypeCons tv = new TypeCons(ident.getName(), sym, syms.botType, null);
	    TypeSymbol tsym = new TypeSymbol(flags, ident.getName(), tv, sym);
	    tv.tsym = tsym;
	    env.info.scope.enterIfAbsent(tsym);
	    tv.args = makeTypeVars(cons.getArgs(), tsym);
	    tv.bound = types.makeTypeCons(tv, tv.args);
	    //tv.bound = syms.objectType;
	    //System.err.println("typeCons: "+tv);
	    return tv;
	} else if (exp instanceof F3Ident) {
	    F3Ident ident = (F3Ident)exp;
	    TypeVar tv = new TypeVar(ident.getName(), sym, syms.botType);
	    tv.tsym = new TypeSymbol(flags, ident.getName(), tv, sym);
	    tv.bound = syms.objectType;
	    //System.err.println("created type var: "+ System.identityHashCode(tv) + ": " +tv);
	    //Thread.currentThread().dumpStack();
	    env.info.scope.enterIfAbsent(((TypeVar)tv).tsym);
	    return tv;
	} else if (exp instanceof F3TypeExists) {
	    return new WildcardType(syms.objectType, BoundKind.UNBOUND, syms.boundClass);
	} else if (exp instanceof F3TypeClass) {
	    F3TypeClass clazz = (F3TypeClass)exp;
	    TypeVar tv = (TypeVar)makeTypeVar0(clazz.getClassName(), sym);
	    if (tv == null) {
		return null;
	    }
	    if (clazz.boundKind != BoundKind.UNBOUND) {
		tv = new TypeVarDefn(tv, clazz.boundKind);
		tv.tsym.type = tv;
	    }
	    //System.err.println("typeclass "+clazz +" => "+ tv);
	    return tv;
	} else if (exp instanceof F3TypeVar) {
	    F3TypeVar t = (F3TypeVar)exp;
	    F3Ident ident = (F3Ident)t.getClassName();
	    BoundKind bk = t.getBoundKind();
	    Type lower = syms.botType;
	    boolean checkBound = false;
	    TypeVar tv = new TypeVar(ident.getName(), sym, lower);
	    tv.tsym = new TypeSymbol(flags, ident.getName(), tv, sym);
	    env.info.scope.enterIfAbsent(((TypeVar)tv).tsym);
	    //System.err.println("attrib bound: "+ t.getBound());
	    Type bound = attribType(t.getBound(), env);
	    if (bound instanceof WildcardType) {
		//System.err.println("EXP="+exp);
		//System.err.println("EXP bound="+t.getBound().getClass()+": "+t.getBound());
		WildcardType wc = (WildcardType)bound;
		///System.err.println("wc.type="+wc.type);
		//System.err.println("wc.bound="+wc.bound);
		//System.err.println("wc.kind="+wc.kind);
		//System.err.println("upper="+((F3Type)t.getBound()).upperBound);
		bound = wc.type;
		bk = wc.kind;
		//System.err.println("bk="+bk);
		if (wc.kind == BoundKind.SUPER) {
		    if (bound instanceof TypeVarDefn) {
			TypeVarDefn def = (TypeVarDefn)bound;
			bound = new TypeVarDefn(def, wc.kind);
		    }
		    //System.err.println("bound="+bound);
		    tv.lower = bound;
		    bound = syms.objectType;
		    bk = BoundKind.UNBOUND;
		    checkBound = true;
		}
	    } else {
		//System.err.println("bound="+bound);
		tv.lower = bk == BoundKind.SUPER ? bound: syms.botType;
		//System.err.println("lower="+lower);
	    }
	    if (checkBound) {
		if (((F3Type)t.getBound()).upperBound != null) {
		    //System.err.println("upper Bound: "+((F3Type)t.getBound()).upperBound);
		    bound = attribType(((F3Type)t.getBound()).upperBound, env);
		    bk = BoundKind.UNBOUND;
		}
	    }
	    if (bk != BoundKind.UNBOUND) {
		if (bound instanceof WildcardType) {
		    bound = ((WildcardType)bound).type;
		}
		bound = new WildcardType(bound, bk, syms.boundClass);
	    }
	    /*
	    tv = new TypeVar(tv.tsym, bound, tv.lower);
	    tv.tsym = new TypeSymbol(0, ident.getName(), tv, sym);
	    */
	    if (bound == null) {
		bound = syms.objectType;
	    }
	    tv.bound = bound == null ? syms.objectType: bound;
	    //System.err.println("created type var: "+ types.toF3String(tv));
	    //Thread.currentThread().dumpStack();
	    return tv;
	} else {
	    System.err.println("exp="+exp.getClass());
	}
	return null;
    }

    List<Type> makeTypeVars(List<F3Expression> types, Symbol sym, F3Env<F3AttrContext> env) {
	F3Env<F3AttrContext> prevEnv = this.env;
        int prevPkind = this.pkind;
        Type prevPt = this.pt;
	boolean prevInSuperType = inSuperType;
        Sequenceness prevSequenceness = this.pSequenceness;
	try {
	    this.env = env;
	    return makeTypeVars(types, sym);
	} finally {
	    this.inSuperType = prevInSuperType;
	    this.env = prevEnv;
            this.pkind = prevPkind;
            this.pt = prevPt;
            this.pSequenceness = prevSequenceness;
	}
    }

    List<Type> makeTypeVars(List<F3Expression> types, Symbol sym) {
	ListBuffer<Type> argbuf = new ListBuffer<Type>();
	for (F3Expression exp: types) {
	    Type tv = makeTypeVar(exp, sym);
	    if (tv != null) {
		argbuf.append(tv);
	    }
	}
	return argbuf.toList();
    }

    //@Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        
        // Tree may come in paritally complete or in error from IDE and so we protect
        // against it. Do nothing if the tree isn't properly attributed.
        //
        if  (tree.sym != null) {
            MethodSymbol m = tree.sym;
	    /*
	    if (tree.typeArgTypes == null) {
		List<Type> typeArgTypes = tree.typeArgs == null ? null : makeTypeVars(tree.typeArgs, m);
		tree.typeArgTypes = typeArgTypes;
	    }
	    */
            m.complete();
            warnOnStaticUse(tree.pos(), tree.getModifiers(), m);
        }

    }

    /** Search super-clases for a parameter type in a matching method.
     * The idea is that when a formal parameter isn't specified in a class
     * function, see if there is a method with the same name in a superclass,
     * and use that method's parameter type.  If there are multiple methods
     * in super-classes that all have the same name and argument count,
     * the parameter types have to be the same in all of them.
     * @param csym Class to search.
     * @param name Name of matching methods.
     * @param paramCount Number of parameters of matching methods.
     * @param paramNum The parameter number we're concerned about,
     *    or -1 if we're searching for the return type.
     * @return The found type.  Null is we found no match.
     *   Notype if we found an ambiguity.
     */
    private Type searchSupersForParamType (ClassSymbol c, Name name, int paramCount, int paramNum) {
	return searchSupersForParamType((ClassType)c.type, c, name, paramCount, paramNum);
    }

    private Type searchSupersForParamType (ClassType ctype, ClassSymbol c, Name name, int paramCount, int paramNum) {
        Type found = null;

        for (Scope.Entry e = c.members().lookup(name);
                 e.scope != null;
                 e = e.next()) {
            if ((e.sym.kind & MTH) == 0 ||
                        (e.sym.flags_field & (STATIC|SYNTHETIC)) != 0)
                continue;
	    if (e.sym.type == null) {
		continue;
	    }
            Type mt = types.memberType(ctype, e.sym);
            if (mt == null)
                continue;
            List<Type> formals = mt.getParameterTypes();
            if (formals.size() != paramCount)
                continue;
            Type t = paramNum >= 0 ? formals.get(paramNum) : mt.getReturnType();
            if (t == Type.noType)
                return t;
            if (found == null) {
                found = t;
            } else if (t != null && found != t)
                return Type.noType;
        }

        Type st = types.supertype(c.type);
        if (st.tag == CLASS) {
            Type t = searchSupersForParamType(ctype,
					      (ClassSymbol)st.tsym, 
					      name, paramCount, paramNum);
            if (t == Type.noType)
                return t;
            if (found == null) {
                found = t;
            } else if (t != null && found != t)
                return Type.noType;
        }
	for (List<Type> l = types.interfaces(c.type);
		     l.nonEmpty();
		     l = l.tail) {
            Type t = searchSupersForParamType(ctype,
					      (ClassSymbol)l.head.tsym, name, paramCount, paramNum);
            if (t == Type.noType)
                return t;
            if (found == null) {
                found = t;
            } else if (t != null && found != t)
                return Type.noType;
        }
        return found;
    }

    public void finishFunctionDefinition(F3FunctionDefinition tree, F3Env<F3AttrContext> env) {
	finishFunctionDefinition(tree, env, true);
    }

    public void finishFunctionDefinition(F3FunctionDefinition tree, F3Env<F3AttrContext> env, boolean newEnv) {
        MethodSymbol m = tree.sym;
	//System.err.println("Finishing: "+System.identityHashCode(m)+"@"+m.getClass()+": "+m);
	m.owner.complete();
        F3FunctionValue opVal = tree.operation;
        F3Env<F3AttrContext> localEnv = methodSymToEnv.get(m);
	if (localEnv == null) {
	    localEnv = memberEnter.methodEnv(tree, env);	
	    //System.err.println("creating new env: "+ tree);
	    methodSymToEnv.put(m, localEnv);
	}
	if (tree.typeArgs != null) {
	    if (tree.typeArgTypes == null) {
		tree.typeArgTypes = makeTypeVars(tree.typeArgs, m, localEnv);
	    }
	    localEnv.info.tvars = tree.typeArgTypes;
	    for (Type t: tree.typeArgTypes) {
		localEnv.info.scope.enterIfAbsent(((TypeVar)t).tsym);
	    }
	}
        Type returnType;
        // Create a new environment with local scope
        // for attributing the method.

        F3Env<F3AttrContext> lintEnv = env;
        while (lintEnv.info.lint == null)
            lintEnv = lintEnv.next;

        JavaFileObject prev = log.useSource(env.toplevel.sourcefile);
        Lint lint = lintEnv.info.lint.augment(m.attributes_field, m.flags());
        Lint prevLint = chk.setLint(lint);

        try {
            localEnv.info.lint = lint;

            ClassSymbol owner = env.getEnclosingClassSymbol();


            if ((owner.flags() & ANNOTATION) != 0 &&
                tree.operation.funParams.nonEmpty())
                log.error(tree.operation.funParams.head.pos(),
                          MsgSym.MESSAGE_INTF_ANNOTATION_MEMBERS_CANNOT_HAVE_PARAMS);

            // Attribute all value parameters.
            ListBuffer<Type> argbuf = new ListBuffer<Type>();
            List<Type> pparam = null;
            MethodType mtype = null;
            if (pt.tag == TypeTags.METHOD || pt instanceof FunctionType) {
                mtype = pt.asMethodType();
                pparam = mtype.getParameterTypes();
            }
            int paramNum = 0;
            List<F3Var> params = tree.getParams();
	    if (params.head instanceof F3Var.This) {
		F3Var pvar = params.head;
		Type refinedThis = attribVar(pvar, localEnv);
		localEnv.thisVar = pvar;
		params = tree.operation.funParams = params.tail;
		System.err.println("refined this: "+ refinedThis+ ": "+refinedThis.tsym.type);
		if (refinedThis.tsym instanceof ClassSymbol) {
		    ClassSymbol base = (ClassSymbol)refinedThis.tsym;
		    owner = new ClassSymbol(base.flags(), base.name, refinedThis, base.owner);
		    m.owner = owner;
		    localEnv.thisVarSym = owner;
		}
		pvar.sym.refinedThis = refinedThis.tsym.type;
	    }
            int paramCount = params.size();
	    boolean inferring = false;
            for (List<F3Var> l = params; l.nonEmpty(); l = l.tail) {

                F3Var pvar = l.head;

                // Don't try to deal with parameters that are Erroneous
                // or missing, which can happen when the IDE is trying to
                // make sense of a partially completed function definition
                //
                if  (       pvar == null                                    // Not even present
                        ||  pvar.getF3Type() == null                       // There, but can't do anythign about typing it
                        ||  pvar.getF3Type() instanceof F3ErroneousType   // There, but flagged as an erroneous type for some  syntactic reason
                    ) 
                        continue;   // Hence, we can't do anythign with this parameter definitionm skip it
		Type type;
		if (pvar.type == null) {
		    if (pparam != null && pparam.nonEmpty()) {
			type = pparam.head;
			pparam = pparam.tail;
		    }
		    else {
			type = syms.objectType;
			if (pvar.getF3Type() instanceof F3TypeUnknown) {
			    Type t = searchSupersForParamType (owner, m.name, paramCount, paramNum);
			    if (t == Type.noType)
				log.warning(pvar.pos(), MsgSym.MESSAGE_F3_AMBIGUOUS_PARAM_TYPE_FROM_SUPER);
			    else if (t != null)
				type = t;
			}
		    }
		    pvar.type = type;
		    boolean infer = false;
		    if (pvar.getInitializer() instanceof F3FunctionValue) {
			F3FunctionValue val = (F3FunctionValue)pvar.getInitializer();
			if (val.infer) {
			    infer = true;
			}
		    }
		    if (infer) {
			inferring = true;
			type = syms.botType;
		    } else {
			type = chk.checkNonVoid(pvar, attribVar(pvar, localEnv));
		    }
		} else {
		    type = pvar.type;
		    memberEnter.memberEnter(pvar, localEnv);
		}
                argbuf.append(type);
                paramNum++;
            }
	    if (tree.implicitArgTrees.nonEmpty()) {
		for (F3Var var: tree.implicitArgTrees) {
		    attribVar(var, localEnv);
		    var.sym.flags_field |= F3Flags.IMPLICIT_PARAMETER | Flags.PARAMETER;
		}
	    }
            returnType = syms.unknownType;
            if (opVal.getF3ReturnType().getF3Tag() != F3Tag.TYPEUNKNOWN)
                returnType = attribType(tree.getF3ReturnType(), localEnv);
            else if (m.type != null) {
                Type mrtype = m.type.getReturnType();
                if (mrtype != null && mrtype.tag != TypeTags.NONE)
                    returnType = mrtype;
            } else {
                // If we made use of the parameter types to select a matching
                // method, we could presumably get a non-ambiguous return type.
                // But this is pretty close, in practice.
		if (m.type != null) {
		    Type t = searchSupersForParamType (owner, m.name, paramCount, -1);
		    if (t == Type.noType)
			log.warning(tree.pos(), MsgSym.MESSAGE_F3_AMBIGUOUS_RETURN_TYPE_FROM_SUPER);
		    else if (t != null)
			returnType = t;
		}
            }
	    if (opVal.getF3ReturnType().getF3Tag() == F3Tag.TYPEUNKNOWN) {
		returnType = syms.f3_UnspecifiedType;
		//System.err.println("unknown return type: "+ returnType);
	    }
	    if (!(returnType instanceof WildcardType)) {
		//returnType = new WildcardType(returnType, BoundKind.EXTENDS, syms.boundClass);
		//System.err.println(returnType);
	    }
            if (returnType == syms.f3_java_lang_VoidType)
                returnType = syms.voidType;
	    if (returnType == null) {
		System.err.println("return type was null: "+ tree);
		returnType = syms.unknownType;
	    }
	    List<Type> argList = argbuf.toList();
	    if (inferring) {
		List<Type> l = argList;
		while (l.nonEmpty()) {
		    if (l.head != null && l.head != syms.botType) {
			l.head = types.boxedTypeOrType(l.head);
		    }
		    l = l.tail;
		}
	    }
	    returnType = types.expandTypeVar(returnType);
	    mtype = new MethodType(argList,
				   returnType, // may be unknownType
				   List.<Type>nil(),
				   syms.methodClass);
	    if (tree.typeArgTypes != null) {
		ListBuffer<Type> lb = ListBuffer.lb();
		if (tree.typeArgTypes != null) {
		    lb.appendList(tree.typeArgTypes);
		}
		//lb.appendList(env.info.tvars);
		m.type = newForAll(lb.toList(), mtype);
	    } else {
		m.type = mtype;
	    }

            if (m.owner instanceof ClassSymbol) {
                // Fix primitive/number types so overridden Java methods will have the correct types.
                fixOverride(tree, m, true);
                if (returnType == syms.unknownType) {
                    returnType = m.getReturnType();
                }
            }
	    {
	        List<VarSymbol> paramSyms = List.<VarSymbol>nil();
		List<Type> paramTypes = List.<Type>nil();
		for (F3Var var : tree.getParams()) {
		    // Skip erroneous parameters, which happens if the IDE is calling with a
		    // a paritally defined function.
		    //
		    if (var == null || var.type == null) continue;
		    paramSyms = paramSyms.append(var.sym);
		    paramTypes = paramTypes.append(var.type);
		}
		for (F3Var var: tree.implicitArgTrees) {
		    F3VarSymbol sym = var.sym;
		    paramSyms = paramSyms.append(sym);
		    paramTypes = paramTypes.append(sym.type);
		}
		//	System.err.println("finishing: "+tree);
		//System.err.println("symbol="+m);
		//System.err.println("type="+m.type);
		//System.err.println("implicit args="+tree.implicitArgs);
		for (F3VarSymbol sym: tree.implicitArgs) {
		    paramSyms = paramSyms.append(sym);
		    paramTypes = paramTypes.append(sym.type);
		}
		//	System.err.println("m="+System.identityHashCode(m));
		//System.err.println("m.type="+m.type);
		m.params = paramSyms;
	    }

            if (tree.getBodyExpression() == null) {
                // Empty bodies are only allowed for
                // abstract, native, or interface methods, or for methods
                // in a retrofit signature class.
		if (false) {
		    tree.mods.flags |= ABSTRACT;
		    owner.flags_field |= ABSTRACT;
		    m.flags_field |= ABSTRACT;
		}
                if ((owner.flags() & INTERFACE) == 0 &&
                    (tree.mods.flags & (ABSTRACT | NATIVE)) == 0 &&
                    !relax)
                    log.error(tree.pos(), MsgSym.MESSAGE_MISSING_METH_BODY_OR_DECL_ABSTRACT);
                else if (returnType == syms.unknownType)
                    // no body, can't infer, assume Any
                    // FIXME Should this be Void or an error?
                    returnType = syms.f3_AnyType;
            } else if ((owner.flags() & INTERFACE) != 0) {
                log.error(tree.getBodyExpression().pos(), MsgSym.MESSAGE_INTF_METH_CANNOT_HAVE_BODY);
            } else if ((tree.mods.flags & ABSTRACT) != 0) {
                log.error(tree.pos(), MsgSym.MESSAGE_ABSTRACT_METH_CANNOT_HAVE_BODY);
            } else if ((tree.mods.flags & NATIVE) != 0) {
                log.error(tree.pos(), MsgSym.MESSAGE_NATIVE_METH_CANNOT_HAVE_BODY);
            } else {
                F3Block body = opVal.getBodyExpression();
                // Attribute method bodyExpression
                Type typeToCheck = returnType;
                if(tree.name == defs.internalRunFunctionName) {
                    typeToCheck = Type.noType;
                }
                else if (returnType == syms.voidType) {
                    typeToCheck = Type.noType;
                }
		//System.err.println("attrib body in "+localEnv.getEnclosingClassType());
                Type bodyType = attribExpr(body, localEnv, typeToCheck); // Special handling for the run function. Its body is empty at this point.
                //System.err.println("body="+body);
                //System.err.println("bodyType="+bodyType);
                //System.err.println("returnType="+returnType);
                //System.err.println("returnType="+(returnType == syms.f3_VoidType));
                if (body.value == null) {
                    if (returnType == syms.unknownType)
                        returnType = syms.f3_VoidType; //TODO: this is wrong if there is a return statement
                } else {
                    if (returnType == syms.unknownType) {
                        //returnType = bodyType == syms.unreachableType ? syms.f3_VoidType : types.normalize(bodyType);
			returnType = bodyType == syms.unreachableType ? syms.unreachableType : types.normalize(bodyType);
                        //System.err.println("return type ..."+returnType);
                    } else if (returnType != syms.f3_VoidType && tree.getName() != defs.internalRunFunctionName) {
                        Type r = chk.checkType(tree.pos(), bodyType, returnType, Sequenceness.PERMITTED, false);
                        //System.err.println("return type = "+r);
                    } else {
                        //System.err.println("unhandled case");
                    }
                }
                if (tree.isBound() && returnType == syms.f3_VoidType) {
                    //log.error(tree.pos(), MsgSym.MESSAGE_F3_BOUND_FUNCTION_MUST_NOT_BE_VOID);
                }                
                if (body.value instanceof F3Return) {
                    if (returnType == syms.voidType && 
			(bodyType != syms.voidType && 
			 bodyType != types.boxedTypeOrType(syms.voidType) &&
			 bodyType != syms.unreachableType)) {
			System.err.println("bodyType="+bodyType);
                        log.error(body.value.pos(),
                                MsgSym.MESSAGE_CANNOT_RET_VAL_FROM_METH_DECL_VOID);
                    }
                    /*
                     * We are going to rewrite blocks value as an expression instead
                     * of original return statement. So, we better save the original
                     * return statement so that we can present it to external tree
                     * walkers, if needed.
                     */
                    body.returnStatement = (F3Return) body.value;
                    body.value = ((F3Return) body.value).expr;
                }
            }
	    if (localEnv.info.scope.next != null) { // hack!!
		localEnv.info.scope.leave();
	    }

            mtype.restype = returnType;
	    
	    //if (tree.getBodyExpression() != null) {
		//System.err.println(tree);
		//System.err.println("mtype="+mtype);
		//System.err.println("bodyType="+tree.getBodyExpression().type);
	    // }
            result = tree.type = mtype;
            
            // If we override any other methods, check that we do so properly.
            // JLS ???
            if (m.owner instanceof ClassSymbol) {
                chk.checkOverride(tree, m);
            } else {
                if ((m.flags() & F3Flags.OVERRIDE) != 0) {
                    log.error(tree.pos(), MsgSym.MESSAGE_F3_DECLARED_OVERRIDE_DOES_NOT, rs.kindName(m), types.toF3String(m.type));
                }
            }
        } catch (Exception exc) {
	    exc.printStackTrace();
	} finally {
            chk.setLint(prevLint);
            log.useSource(prev);
        }
	{
	    List<VarSymbol> paramSyms = List.<VarSymbol>nil();
	    List<Type> paramTypes = List.<Type>nil();
	    for (F3Var var : tree.getParams()) {
		// Skip erroneous parameters, which happens if the IDE is calling with a
		// a paritally defined function.
		//
		if (var == null || var.type == null) continue;
		paramSyms = paramSyms.append(var.sym);
		paramTypes = paramTypes.append(var.type);
	    }
	    for (F3Var var: tree.implicitArgTrees) {
		F3VarSymbol sym = var.sym;
		paramSyms = paramSyms.append(sym);
		paramTypes = paramTypes.append(sym.type);
	    }
	    //	System.err.println("finishing: "+tree);
	    //System.err.println("symbol="+m);
	    //System.err.println("type="+m.type);
	    //System.err.println("implicit args="+tree.implicitArgs);
	    for (F3VarSymbol sym: tree.implicitArgs) {
		paramSyms = paramSyms.append(sym);
		paramTypes = paramTypes.append(sym.type);
	    }
	    //	System.err.println("m="+System.identityHashCode(m));
	    //System.err.println("m.type="+m.type);
	    m.params = paramSyms;
	}

	{
	    Type argtype = m.type.getReturnType();
	    if (argtype != null && types.expandTypeVar(argtype).isSuperBound()) {
		log.error(tree.getF3ReturnType().pos(),
			  "contravariant.in.covariant.pos", tree.getF3ReturnType());
	    }
	}
	
        // mark the method varargs, if necessary
        // if (isVarArgs) m.flags_field |= Flags.VARARGS;
        // Set the inferred types in the MethodType.argtypes and in correct symbols in MethodSymbol
	//System.err.println("m.params="+m.params);
        if (m.type != null) {
	    //	    m.type.asMethodType().argtypes = paramTypes;
        }
	fixOverride(tree, m, true);
	//System.err.println("m="+m+": "+m.type+", owner="+m.owner+": "+m.owner.type);
	methodSymToEnv.remove(m);
	methodSymToTree.remove(m);
    }

    //@Override
    public void visitTry(F3Try tree) {
        boolean canReturn = false;
        // Attribute body
        Type stype = attribExpr(tree.body, env.dup(tree, env.info.dup()));
        if (stype != syms.unreachableType)
            canReturn = true;

        // Attribute catch clauses
        for (List<F3Catch> l = tree.catchers; l.nonEmpty(); l = l.tail) {
            F3Catch c = l.head;

            if (c == null) continue;    // Don't try to handle erroneous catch blocks

            F3Env<F3AttrContext> catchEnv = newLocalEnv(c);
            memberEnter.memberEnter(c.param, catchEnv);

            if (c.param == null) continue;    // Don't try to handle erroneous catch blocks

            if (c.param.type == null)
                c.param.sym.type = c.param.type = syms.throwableType;
            Type ctype = attribDecl((F3Var) c.param, catchEnv);
            if (c.param.type.tsym.kind == Kinds.VAR) {
                c.param.sym.setData(ElementKind.EXCEPTION_PARAMETER);
            }
//uses vartype
//            chk.checkType(c.param.vartype.pos(),
//                          chk.checkClassType(c.param.vartype.pos(), ctype),
//                          syms.throwableType);
            ctype = attribExpr(c.body, catchEnv);
            if (ctype != syms.unreachableType)
                canReturn = true;
        }

        // Attribute finalizer
        if (tree.finalizer != null) attribExpr(tree.finalizer, env);
        result = canReturn ? syms.voidType : syms.unreachableType;
        tree.type = result;
    }

    //@Override
    public void visitIfExpression(F3IfExpression tree) {
        attribExpr(tree.cond, env, syms.booleanType);
        attribTree(tree.truepart, env, VAL, pt, pSequenceness);
        Type falsepartType = tree.falsepart != null ?
            attribTree(tree.falsepart, env, VAL, pt, pSequenceness) :
            syms.voidType;
	if (tree.cond.type == null || 
	    tree.truepart.type == null) {
	} else {
	    result = check(tree,
			   condType(tree.pos(), tree.cond.type,
				    tree.truepart.type, falsepartType),
			   VAL, pkind, pt, pSequenceness);
	    if (!tree.isThen) {
		// can only do this if no returns are contained in the if and else parts
		//tree.type = result = syms.voidType;
	    }
	}
    }
    //where
        /** Compute the type of a conditional expression, after
         *  checking that it exists. See Spec 15.25.
         *
         *  @param pos      The source position to be used for
         *                  error diagnostics.
         *  @param condtype The type of the expression's condition.
         *  @param type1 The type of the expression's then-part.
         *  @param type2 The type of the expression's else-part.
         */
        private Type condType(DiagnosticPosition pos,
                              Type condtype,
                              Type thentype,
                              Type elsetype) {
            Type ctype = unionType(pos, thentype, elsetype);

            // If condition and both arms are numeric constants,
            // evaluate at compile-time.
            return ((condtype.constValue() != null) &&
                    (thentype.constValue() != null) &&
                    (elsetype.constValue() != null))
                ? cfolder.coerce(condtype.isTrue()?thentype:elsetype, ctype)
                : ctype;
        }
        /** Compute the type of a conditional expression, after
         *  checking that it exists.  Does not take into
         *  account the special case where condition and both arms
         *  are constants.
         *
         *  @param pos      The source position to be used for error
         *                  diagnostics.
         *  @param condtype The type of the expression's condition.
         *  @param type1 The type of the expression's then-part.
         *  @param type2 The type of the expression's else-part.
         */
        private Type unionType(DiagnosticPosition pos,
                               Type type1, Type type2) {
            if (type1 == syms.unreachableType || type1 == syms.f3_UnspecifiedType)
                return type2;
            if (type2 == syms.unreachableType || type2 == syms.f3_UnspecifiedType)
                return type1;
            if (type1 == type2)
                return type1;

            // Ensure that we don't NPE if either of the inputs were from
            // Erroneous nodes such as missing blocks on conditionals and so on.
            //
            if (type1 == null ) {
                if  (type2 == null) {
                    return syms.voidType;
                } else {
                    return type2;
                }
            } else  if (type2 == null) {
                return type1;
            }
    
            if (type1.tag == VOID || type2.tag == VOID)
                return syms.voidType;

            boolean isSequence1 = types.isSequence(type1);
            boolean isSequence2 = types.isSequence(type2);
            if (isSequence1 || isSequence2) {
                if (isSequence1)
                    type1 = types.elementType(type1);
                if (isSequence2)
                    type2 = types.elementType(type2);
                Type union = unionType(pos, type1, type2);
                return union.tag == ERROR ? union : types.sequenceType(union);
            }
            // If same type, that is the result
            if (types.isSameType(type1, type2))
                return type1.baseType();

            Type thenUnboxed = (!allowBoxing || type1.isPrimitive())
                ? type1 : types.unboxedType(type1);
            Type elseUnboxed = (!allowBoxing || type2.isPrimitive())
                ? type2 : types.unboxedType(type2);

            // Otherwise, if both arms can be converted to a numeric
            // type, return the least numeric type that fits both arms
            // (i.e. return larger of the two, or return int if one
            // arm is short, the other is char).
            if (thenUnboxed.isPrimitive() && elseUnboxed.isPrimitive()) {
                // If one arm has an integer subrange type (i.e., byte,
                // short, or char), and the other is an integer constant
                // that fits into the subrange, return the subrange type.
                if (thenUnboxed.tag < INT && elseUnboxed.tag == INT &&
                    types.isAssignable(elseUnboxed, thenUnboxed))
                    return thenUnboxed.baseType();
                if (elseUnboxed.tag < INT && thenUnboxed.tag == INT &&
                    types.isAssignable(thenUnboxed, elseUnboxed))
                    return elseUnboxed.baseType();

                for (int i = BYTE; i < VOID; i++) {
                    Type candidate = syms.typeOfTag[i];
                    if (types.isSubtype(thenUnboxed, candidate) &&
                        types.isSubtype(elseUnboxed, candidate))
                        return candidate;
                }
            }

            // Those were all the cases that could result in a primitive
            if (allowBoxing) {
                type1 = types.boxedTypeOrType(type1);
                type2 = types.boxedTypeOrType(type2);
            }

            if (types.isSubtype(type1, type2))
                return type2.baseType();
            if (types.isSubtype(type2, type1))
                return type1.baseType();

            if (!allowBoxing) {
                log.error(pos, MsgSym.MESSAGE_NEITHER_CONDITIONAL_SUBTYPE,
                          type1, type2);
                return type1.baseType();
            }

            // both are known to be reference types.  The result is
            // lub(type1,type2). This cannot fail, as it will
            // always be possible to infer "Object" if nothing better.
            return types.makeUnionType(type1, type2);
        }

    //@Override
    public void visitBreak(F3Break tree) {
        tree.target = findJumpTarget(tree.pos(), tree.getF3Tag(), tree.label, env);
        result = tree.type = syms.unreachableType;
    }

    //@Override
    public void visitContinue(F3Continue tree) {
        tree.target = findJumpTarget(tree.pos(), tree.getF3Tag(), tree.label, env);
        result = tree.type = syms.unreachableType;
    }
    //where
        /** Return the target of a break or continue statement, if it exists,
         *  report an error if not.
         *  Note: The target of a labelled break or continue is the
         *  (non-labelled) statement tree referred to by the label,
         *  not the tree representing the labelled statement itself.
         *
         *  @param pos     The position to be used for error diagnostics
         *  @param tag     The tag of the jump statement. This is either
         *                 Tree.BREAK or Tree.CONTINUE.
         *  @param label   The label of the jump statement, or null if no
         *                 label is given.
         *  @param env     The environment current at the jump statement.
         */
        private F3Tree findJumpTarget(DiagnosticPosition pos,
                                    F3Tag tag,
                                    Name label,
                                    F3Env<F3AttrContext> env) {
            // Search environments outwards from the point of jump.
            F3Env<F3AttrContext> env1 = env;
            LOOP:
            while (env1 != null) {
                switch (env1.tree.getF3Tag()) {
                case WHILELOOP:
                case FOR_EXPRESSION:
                    if (label == null) return env1.tree;
                    break;
                default:
                }
                env1 = env1.next;
            }
            if (label != null)
                log.error(pos, MsgSym.MESSAGE_UNDEF_LABEL, label);
            else if (tag == F3Tag.CONTINUE)
                log.error(pos, MsgSym.MESSAGE_CONT_OUTSIDE_LOOP);
            else
                log.error(pos, MsgSym.MESSAGE_BREAK_OUTSIDE_SWITCH_LOOP);
            return null;
        }

    //@Override
    public void visitReturn(F3Return tree) {
        if (env.enclFunction == null) {
            log.error(tree.pos(), MsgSym.MESSAGE_RETURN_OUTSIDE_METH);

        } else {
            // Attribute return expression, if it exists, and check that
            // it conforms to result type of enclosing method.
            Symbol m = env.enclFunction.sym;
            tree.returnType = m.type.getReturnType();
            F3Block enclBlock = env.enclFunction.operation.bodyExpression;
            if (tree.returnType == null)
                log.error(tree.pos(), MsgSym.MESSAGE_F3_CANNOT_INFER_RETURN_TYPE);
            else if (false && tree.returnType.tag == VOID) {
                if (tree.expr != null) {
		    System.err.println("tree.type="+tree.type);
		    if (false) {
			log.error(tree.pos(),
				  MsgSym.MESSAGE_CANNOT_RET_VAL_FROM_METH_DECL_VOID);
		    }
                }
            } else if (tree.expr == null) {
                if (enclBlock.type == syms.f3_UnspecifiedType)
                    enclBlock.type = syms.f3_VoidType;
                else if (enclBlock.type != syms.f3_VoidType)
                    log.error(tree.pos(), MsgSym.MESSAGE_MISSING_RET_VAL);
            } else {
                Type exprType = attribExpr(tree.expr, env);
                if (enclBlock.type.tag == VOID) {
		    System.err.println("tree.type1="+exprType);
                    //log.error(tree.pos(), MsgSym.MESSAGE_CANNOT_RET_VAL_FROM_METH_DECL_VOID);
                }
                enclBlock.type = unionType(tree.pos(), enclBlock.type, exprType);
                enclBlock.isVoidValueAllowed = false;
            }
        }    
        result = tree.type = syms.unreachableType;
    }

    //@Override
    public void visitThrow(F3Throw tree) {
        if  (tree.expr != null && !(tree.expr instanceof F3Erroneous)) {
            attribExpr(tree.expr, env, syms.throwableType);
        }
        result = tree.type = syms.unreachableType;
    }

    private void searchParameterTypes (F3Expression meth, Type[] paramTypes) {
        // FUTURE: Search for matching overloaded methods/functions that
        // would be a match for meth, and number of arguments==paramTypes.length.
        // If all the candidates have the same type for parameter # i,
        // set paramTypes[i] to that type.
        // Otherwise, leave paramTypes[i]==null.
    }
    
    Symbol findThe(F3Env<F3AttrContext> env,
		   F3Expression tree,
		   Type expectedType) {
	return findThe(env, tree, expectedType, false);
    }

    Symbol findThe(F3Env<F3AttrContext> env,
		   F3Expression tree,
		   Type expectedType,
		   boolean instantiate) {
	if (expectedType == syms.botType) {
	    chk.typeTagError(tree, "inhabited type", expectedType);  
	    return null;
	}
	Symbol sym = findTheUnchecked(env, expectedType);
	if (sym.kind >= AMBIGUOUS) {
	    if (sym.kind == AMBIGUOUS || instantiate) {
		if (sym == rs.methodNotFound) {
		    sym = rs.varNotFound;
		}
		return rs.access(sym, tree, env.getEnclosingClassType(), syms.the, false, expectedType);
	    }
	    System.err.println("find the "+expectedType+" => "+ sym);
	    if (false && !(expectedType instanceof MethodType)) {
		System.err.println("trying as method: "+tree+": sym="+sym);
		MethodType mt;
		sym = findThe(env, tree, 
			      mt = new MethodType(List.<Type>nil(), expectedType, List.<Type>nil(), syms.methodClass));
		if (sym instanceof MethodSymbol) {
		    sym.complete();
		    MethodSymbol msym = (MethodSymbol)sym;
		    if (sym instanceof F3Resolve.InstanceMethodSymbol) {
			msym = ((F3Resolve.InstanceMethodSymbol)sym).generic;
		    }
		    System.err.println("found: "+sym.type.getReturnType());
		    System.err.println("expected: "+ expectedType);
		    try {
			ForAll result = infer.instantiateExpr1(newForAll(msym.type.getTypeArguments(), msym.type.getReturnType()), expectedType, Warner.noWarnings);
			if (!types.isAssignable(expectedType, result.qtype)) {
			    System.err.println("not assignable");
			    return null;
			}
			for (VarSymbol varSym: ((MethodSymbol)sym).params) {
			    if ((varSym.flags() & F3Flags.IMPLICIT_PARAMETER) != 0) {
				Symbol dep = findTheUnchecked(env, varSym.type);
				System.err.println("checking implicit parameter: "+ varSym +" => "+ dep);
				if (dep == null || dep.kind >= AMBIGUOUS) {
				    return null;
				}
			    }			    
			}
			sym = new F3Resolve.InstanceMethodSymbol(msym, 
								 new MethodType(List.<Type>nil(),
										expectedType,
										result.getTypeArguments(),
										syms.methodClass),
								 sym.owner);
			System.err.println("resolved to: "+ sym);
		    } catch (F3Infer.NoInstanceException exc) {
			//exc.printStackTrace();
			System.err.println(exc.getDiagnostic());
			return null;
		    }
		    return sym;
		}
	    }
	    return null;
	}
	return sym;
    }

    Symbol findTheUnchecked(F3Env<F3AttrContext> env,
			    Type expectedType) {
	Symbol sym = rs.findVar(env, syms.the, (MTH|VAR), expectedType, true, false);
	//System.err.println("searching for the "+expectedType+ " => "+sym);
	if (sym.kind >= AMBIGUOUS) {
	    if (sym.kind != AMBIGUOUS) {
		if (true) {
		    List<Type> toSearch = List.nil();
		    toSearch = toSearch.append(expectedType);
		    toSearch = toSearch.appendList(expectedType.getTypeArguments());
		    //System.err.println("searching: "+ toSearch);
		    for (Type st: toSearch) {
			//System.err.println("st="+st.getClass()+": "+st);
			if (st instanceof ClassType) {
			    sym = rs.findMember(env,
						types.erasure(st),
						syms.the,
						expectedType,
						true, false,
						false);
			    //System.err.println("searched "+st+": "+sym);
			    if (sym.kind < AMBIGUOUS) {
				return sym;
			    }
			}
		    }
		}
		if ((expectedType instanceof MethodType) ||
		    (expectedType instanceof ForAll) ||
		    (expectedType instanceof FunctionType)) {
		    MethodType mt = expectedType.asMethodType();
		    Symbol bestSoFar = null;
		    MethodType searchType = 
			new MethodType(mt.argtypes.tail, mt.restype,
				       List.<Type>nil(), syms.methodClass);
		    for (Type argType : mt.argtypes) {
			sym = rs.findMember(env,
					    argType,
					    syms.the,
					    searchType,
					    true, false,
					    false);
			//System.err.println("searching' for the "+searchType+ " => "+sym);
			break;
		    }
		}
	    }
	}
	return sym;
    }

    //@Override
    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        // The local environment of a method application is
        // a new environment nested in the current one.
        F3Env<F3AttrContext> localEnv = env.dup(tree, env.info.dup());
        localEnv.outer = env;
        // The types of the actual method type arguments.
        List<Type> typeargtypes;

        Name methName = F3TreeInfo.name(tree.meth);

        int argcount = tree.args.size();

        Type[] paramTypes = new Type[argcount];
        searchParameterTypes(tree.meth, paramTypes);

        ListBuffer<Type> argtypebuffer = new ListBuffer<Type>();
        ListBuffer<Type> typeargbuffer = new ListBuffer<Type>();
        int i = 0;
        for (List<F3Expression> l = tree.args; l.nonEmpty(); l = l.tail, i++) {
            Type argtype = paramTypes[i];
            if (argtype != null)
                argtype = attribExpr(l.head, env, argtype);
            else
                argtype = chk.checkNonVoid(l.head.pos(),
					   //types.upperBound(attribTree(l.head, env, VAL, F3Infer.anyPoly, Sequenceness.PERMITTED)));
					   (attribTree(l.head, env, VAL, F3Infer.anyPoly, Sequenceness.PERMITTED)
					   ));
	    //System.err.println("argType "+ l.head+": "+argtype);
	    if (argtype instanceof MethodType) {
		argtype = syms.makeFunctionType((MethodType)argtype);
	    }
	    //System.err.println("argtype:" + types.toF3String(argtype));
            argtypebuffer.append(argtype);
        }
        List<Type> argtypes = argtypebuffer.toList();
	if (tree.typeargs.isEmpty()) {
	    tree.typeargs = F3TreeInfo.typeArgs(tree.meth); 
	}
        typeargtypes = attribTypeArgs(tree.typeargs, localEnv);
	typeargbuffer.appendList(typeargtypes);
	typeargtypes = typeargbuffer.toList();
	// ... and attribute the method using as a prototype a methodtype
	// whose formal argument types is exactly the list of actual
	// arguments (this will also set the method symbol).	
	Type mtype = null;
	if (tree.partial) {
	    Type mpt = attribExpr(tree.meth, localEnv); 
	    if (mpt instanceof FunctionType) {
		mpt = ((FunctionType)mpt).asMethodOrForAll();
	    }
	    mtype = mpt;
	}
	if (mtype == null) {
	    if (pt == null) {
		System.err.println("pt is null: "+tree);
		pt = syms.unknownType;
	    }
	    Type restype = pt;
	    if (restype == syms.unknownType) {
		restype = syms.voidType;
	    }

	    if (false && argtypes.size() > 0) {
		Type x = argtypes.head;
		List<Type> xs = argtypes.tail;
		Symbol sym = rs.findMethod(env, x, methName, xs, typeargtypes, true, false, false);
		if (sym.kind < AMBIGUOUS) {
		    //System.err.println("found: "+sym);
		    int pos = tree.meth.pos;
		    tree.meth = f3make.Select(tree.args.head, methName, false);
		    tree.meth.pos = pos;
		    F3TreeInfo.setSymbol(tree.meth, sym);
		    tree.meth.type = sym.type;
		    tree.args = tree.args.tail;
		    argtypes = xs;
		}
	    }

	    Type mpt = new MethodType(argtypes, pt, List.<Type>nil(), syms.methodClass);

	    if (typeargtypes.nonEmpty()) {
		mpt = newForAll(typeargtypes, mpt);
	    }
	    //System.err.println("mpt="+types.toF3String(mpt));
	    localEnv.info.varArgs = false;
	    mtype = attribExpr(tree.meth, localEnv, mpt);
	    if (false) {
		Symbol sym = F3TreeInfo.symbol(tree.meth);
		System.err.println("meth="+tree.meth);
		System.err.println("mtype="+mtype);
		System.err.println("sym="+sym);
		System.err.println("sym.owner="+sym.owner.type);
		if (tree.meth instanceof F3Select) {
		    F3Select select = (F3Select)tree.meth;
		    Type receiver = select.selected.type;
		    System.err.println("receiver="+receiver);
		    System.err.println("tv1="+sym.owner.type.tsym.type.getTypeArguments());
		    System.err.println("tv2="+receiver.getTypeArguments());
		    mtype = types.subst2(mtype, 
					 sym.owner.type.getTypeArguments(),
					 receiver.getTypeArguments(), 
					 true);
		    System.err.println("mtype"+mtype);
		}
		if (sym != null) {
		    sym.complete();
		}
	    }
	    //System.err.println("mtype="+types.toF3String(mtype));
	    //System.err.println("attrib " +tree.meth.getClass()+": "+ tree.meth + " => "+mtype);
	    if (true) {
		if (!(mtype instanceof ErrorType) &&
		    !(mtype instanceof FunctionType) &&
		    !(mtype instanceof MethodType) &&
		    !(mtype instanceof ForAll)) {
		    Name invoke = names.fromString("invoke");
		    F3Expression invokeTree = f3make.at(tree.meth.pos).Select(tree.meth, invoke, true);
		    System.err.println("invokeTree="+invokeTree);
		    mtype = attribExpr(invokeTree, localEnv, mpt);
		    System.err.println("mtype="+mtype);
		    if ((mtype instanceof FunctionType) ||
			(mtype instanceof MethodType) ||
			(mtype instanceof ForAll)) {
			tree.meth = invokeTree;
		    }
		}
	    }
	    //System.err.println("mtype "+tree.meth+"="+mtype.getClass()+" "+mtype);
	    if (mtype instanceof FunctionType) {
		mtype = ((FunctionType)mtype).asMethodOrForAll();
	    }

	    //System.err.println("mtype "+tree.meth+" = "+mtype);
	}

	if (!(mtype instanceof ErrorType)) {
	    if (!(mtype instanceof FunctionType) && !(mtype instanceof MethodType))  {
		mtype = reader.translateType(mtype);
	    }
	}

	if (tree.explicits != null) {
	    for (F3Expression exp: tree.explicits) {
		attribExpr(exp, env);
	    }
	}

	if (localEnv.info.varArgs)
	    assert mtype.isErroneous() || tree.varargsElement != null;

	// Compute the result type.
	Type restype = mtype.getReturnType();
	if (restype == syms.unknownType) {
	    log.error(tree.meth.pos(), MsgSym.MESSAGE_F3_FUNC_TYPE_INFER_CYCLE, methName);
	    restype = syms.objectType;
	}
	// as a special case, array.clone() has a result that is
	// the same as static type of the array being cloned
	if (tree.meth.getF3Tag() == F3Tag.SELECT &&
	    allowCovariantReturns &&
	    methName == names.clone &&
	    types.isArray(((F3Select) tree.meth).selected.type))
	    restype = ((F3Select) tree.meth).selected.type;
	
	// as a special case, x.getClass() has type Class<? extends |X|>
	if (allowGenerics &&
	    methName == names.getClass && tree.args.isEmpty()) {
	    Type qualifier = (tree.meth.getF3Tag() == F3Tag.SELECT)
		? ((F3Select) tree.meth).selected.type
		: env.getEnclosingClassType();
	    qualifier = types.boxedTypeOrType(qualifier);
	    restype = new
		ClassType(restype.getEnclosingType(),
			  List.<Type>of(new WildcardType(types.erasure(qualifier),
							 BoundKind.EXTENDS,
							 syms.boundClass)),
			  restype.tsym);
	}
	List<F3Expression> implicitExprs = List.<F3Expression>nil();
	tree.meth.type = mtype;
	//System.err.println("mtype="+mtype);
	if (mtype instanceof ErrorType) {
	    tree.type = mtype;
	    result = mtype;
	} else if (mtype instanceof MethodType || mtype instanceof FunctionType || mtype instanceof ForAll) {
	    // If the "method" has a symbol, we've already checked for
	    // formal/actual consistency.  So doing it again would be
	    // wasteful - plus varargs hasn't been properly implemented.
	    boolean partial = tree.partial;

	    if (tree.meth.getF3Tag() != F3Tag.SELECT &&
		tree.meth.getF3Tag() != F3Tag.IDENT &&
		! rs.argumentsAcceptable(argtypes, mtype.getParameterTypes(),
					 true, false, Warner.noWarnings)) {
		if (!partial || argtypes.size() >= mtype.getParameterTypes().size()) {
		    System.err.println("argtypes: "+argtypes);
		    System.err.println("paramtypes: "+ mtype.getParameterTypes());
		    log.error(tree,
			      MsgSym.MESSAGE_F3_CANNOT_APPLY_FUNCTION,
			      mtype.getParameterTypes(), argtypes);
		} else {
		    //System.err.println("args acceptable");
		}
	    } else {
		//partial = false;
	    }
	    // Check that value of resulting type is admissible in the
	    // current context.  Also, capture the return type
	    if (partial) {
		//System.err.println("skipped check");
		ListBuffer<Type> typarams = new ListBuffer<Type>();
		Type rtype = restype;
		List<Type> pts = mtype.getParameterTypes();
		for (int j = 0; j < argtypes.size(); j++) {
		    pts = pts.tail;
		}
		typarams.append(types.boxedTypeOrType(rtype));
		if (pts != null) {
		    for (Type t: pts) {
			typarams.append(types.boxedTypeOrType(t));
		    }
		}
		restype = syms.makeFunctionType(typarams.toList());
	    } else {
	    }
	    //System.err.println("result "+ tree + " = " + result);
	}
	else {
	    log.error(tree,
		      MsgSym.MESSAGE_F3_NOT_A_FUNC,
		      mtype, typeargtypes, Type.toString(argtypes));
	    tree.type = pt;
	    result = pt;       
	}
        Symbol msym = F3TreeInfo.symbol(tree.meth);
	List<Symbol> resolvedImplicits = List.<Symbol>nil();
	if (!(mtype instanceof ErrorType) && msym instanceof MethodSymbol) {
	    try {
		MethodSymbol mmsym = (MethodSymbol)msym;
		if (mtype.getParameterTypes() == null) {
		    System.err.println("bad type: "+ msym.name);
		}
		List<Type> args = List.<Type>nil();
		List<Type> formalArgs = List.<Type>nil();
		boolean sawImplicit = false;
		args = args.appendList(mtype.getParameterTypes());
		MethodSymbol genSym = mmsym;
		//System.err.println("mmsym="+mmsym.getClass() +": "+mmsym);
		if (msym instanceof F3Resolve.InstanceMethodSymbol) {
		    genSym = ((F3Resolve.InstanceMethodSymbol)mmsym).generic;
		}
		//System.err.println("args="+types.toF3String(args));
		if (genSym.params != null) {
		    loop1: for (VarSymbol varSym: genSym.params) {
			try {
			    formalArgs = formalArgs.append(types.normalize(varSym.type, false));
			} catch (StackOverflowError exc) {
			    System.err.println("can't normalize: "+types.toF3String(varSym.type));
			    formalArgs = formalArgs.append(varSym.type);
			}
			if ((varSym.flags() & F3Flags.IMPLICIT_PARAMETER) != 0) {
			    if (tree.explicits != null) {
				for (F3Expression exp: tree.explicits) {
				    if (types.isSubtypeUnchecked(varSym.type, exp.type)) {
					args = args.append(exp.type);
					sawImplicit = true;
					continue loop1;
				    }
				}
			    } 
			    args = args.append(syms.botType);
			    sawImplicit = true;
			}
		    }
		}
		Type mtype1 = rs.newMethTemplate(formalArgs, genSym.type.getTypeArguments());
		//System.err.println("mtype1="+mtype1);
		if (sawImplicit) {
		    System.err.println("saw implicit: "+ tree+ ": "+msym);
		    Type inst = null;
		    List<Type> ts = args;
		    while (ts.nonEmpty()) {
			ts.head = types.normalize(ts.head, false);
			ts = ts.tail;
		    }
		    try {
			MethodSymbol isym = new MethodSymbol(0L, genSym.name, mtype1, genSym.owner);
			System.err.println("args="+args);
			System.err.println("typeargtypes="+typeargtypes);
			inst = rs.rawInstantiate/*Debug*/(env, 
						 isym,
						 mtype1,
						 args, 
						 typeargtypes, true, false, 
						 noteWarner);
		    } catch (F3Infer.NoInstanceException exc) {
			//exc.printStackTrace();
			System.err.println(exc.getDiagnostic());
		    }
		    System.err.println("inst="+inst);
		    if (inst == null) {
			//System.err.println("couldn't instantiate: "+ mtype1 + " with "+ args);
			log.error(tree.pos(), 
				  MsgSym.MESSAGE_INTERNAL_ERROR_CANNOT_INSTANTIATE,
				  types.toF3String(genSym, genSym.params),
				  types.toF3String(env.getEnclosingClassType()),
				  types.toF3String(args));
		    }
		    if (inst != null) {
			MethodType minst = inst.asMethodType();
			minst.restype = restype;
			tree.meth.type = minst;
			//restype = minst.getReturnType();
			//tree.type = restype;
			//System.err.println("minst="+minst);
			List<Type> ptr = minst.argtypes;
			List<Type> ptr0 = args;
			for (VarSymbol varSym: ((MethodSymbol)msym).params) {
			    if ((varSym.flags() & F3Flags.IMPLICIT_PARAMETER) != 0) {
				Type expectedType = reader.translateType(ptr.head);
				if (expectedType instanceof FunctionType) {
				    expectedType = ((FunctionType)expectedType).asMethodOrForAll();
				}
				F3Expression implicitExpr = null;
				System.err.println("explicits="+tree.explicits);
				if (tree.explicits != null) {
				    for (F3Expression exp: tree.explicits) {
					if (types.isSubtypeUnchecked(expectedType, exp.type)) {
					    if (implicitExpr != null) {
						if (types.isSameType(implicitExpr.type, exp.type)) {
						    // ambiguous
						    System.err.println("ambiguous: "+ implicitExpr.type);
						    System.err.println("ambiguous: "+ exp.type);
						}
					    }
					    implicitExpr = exp;
					    break;
					}
				    }
				}
				if (implicitExpr == null) {
				    System.err.println("FIND the "+ expectedType);
				    Symbol sym = findThe(env, tree, expectedType);
				    if (sym == null || sym.kind >= AMBIGUOUS) {
					System.err.println("find the: "+expectedType+": "+ sym);
				    } else {
					System.err.println("find the "+expectedType+": found: "+sym);
					resolvedImplicits = resolvedImplicits.append(sym);
					//F3Expression exp = f3make.QualIdent(sym);
					F3Expression exp = accessThe(sym, expectedType, env);
					if (!types.isSubtype(sym.type, expectedType)) {
					    exp = f3make.Apply(List.<F3Expression>nil(), exp, List.<F3Expression>nil());
					}
					Type t = attribExpr(exp, env);
					System.err.println("implicit expr="+exp);
					System.err.println("type="+t);
					exp.type = t;
					implicitExpr = exp;
				    }
				}
				if (implicitExpr != null) {
				    implicitExprs = implicitExprs.append(implicitExpr);
				}
			    } else {
				if (ptr0 != null) { 
				    ptr.head = ptr0.head; // hack!!! see above "inst" has bugs
				}
			    }
			    ptr = ptr.tail;
			    if (ptr0 != null) ptr0 = ptr0.tail;
			}
		    }
		}
	    } catch (Throwable t) {
		t.printStackTrace();
	    }
	}
        // We can add more methods here that we want to warn about.
        // If it becomes too hairy, it should be moved into a separate method,
        // and perhaps be table-driven.  FIXME.
        if (msym != null && msym.owner instanceof ClassSymbol &&
	    ((ClassSymbol) msym.owner).fullname == defs.cJavaLangThreadName &&
	    msym.name == defs.start_ThreadMethodName) {
            log.warning(tree.pos(), MsgSym.MESSAGE_F3_EXPLICIT_THREAD);
        }
        if (msym!=null && msym.owner!=null && msym.owner.type!=null &&
                msym.owner.type.tsym == syms.f3_PointerType.tsym &&
                methName == defs.make_PointerMethodName &&
                argcount == 1) {
            msym.flags_field |= F3Flags.FUNC_POINTER_MAKE;
            for (List<F3Expression> l = tree.args; l.nonEmpty(); l = l.tail, i++) {
                F3Expression arg = l.head;
                Symbol asym = F3TreeInfo.symbol(arg);
                if (asym == null || !(asym.type instanceof ErrorType)) {
                    if (asym == null ||
                            !(asym instanceof F3VarSymbol) ||
                            (arg.getF3Tag() != F3Tag.IDENT && arg.getF3Tag() != F3Tag.SELECT) ||
                            asym.owner == null ||
                            (asym.owner.kind != TYP && !asym.isLocal())) {
                        log.error(tree.pos(), MsgSym.MESSAGE_F3_APPLIED_TO_INSTANCE_VAR, methName);
                    }
                }
            }
        }
	tree.resolvedImplicits = resolvedImplicits;
	//	System.err.println("implicit exprs="+implicitExprs);
	for (F3Expression exp: implicitExprs) {
	    tree.args = tree.args.append(exp);
	}
	if (msym != null) {
	    //System.err.println("msym="+msym);
	    //System.err.println("msym.type="+msym.type);
	    //System.err.println("msym.owner="+msym.owner);
	    //System.err.println("msym.owner.type="+msym.owner.type);
	    if (msym.isStatic()) { // handle specialization of this's type variables
		restype = tree.meth.type.getReturnType();
	    } else {
		Type self = null;
		F3Tree dt = tree.meth;
		if (tree.meth instanceof F3Select) {
		    F3Select sel = (F3Select)tree.meth;
		    self = sel.selected.type;
		    dt = sel.selected;
		} else {
		    //self = env.getEnclosingClassType();
		    //System.err.println("self="+self);
		}
		Type self1 = reader.translateType(self);
		if (self != null && self != env.getEnclosingClassType() && !(self1 instanceof FunctionType) &&
		    self.getTypeArguments().nonEmpty()) {
		    //System.err.println("self="+self.getClass() +": "+self);
		    Symbol found = rs.resolveMethod(dt.pos(), env, msym.name, self, msym.type.getParameterTypes());
		    if (found != null) {
			found.complete();
		    }
		    Symbol generic = found;
		    if (found instanceof F3Resolve.InstanceMethodSymbol) {
			generic = ((F3Resolve.InstanceMethodSymbol)found).generic;
		    }
		    //System.err.println("found="+types.toF3String(found.type));
		    //System.err.println("found="+found.owner.type);
		    if (found != null && found.owner != null && found.owner.type != null && msym !=null && msym.owner != null && msym.owner.type != null && !types.isSameType(found.owner.type, msym.owner.type.tsym.type) && msym.owner.type.tsym != null) {
			Type hack = types.subst2(msym.owner.type,
						 msym.owner.type.tsym.type.getTypeArguments(),
						 msym.owner.type.tsym.type.getTypeArguments(),
						 true);
			List<Type> formals = List.of(hack).appendList(generic.type.getParameterTypes());
			List<Type> targs = msym.owner.type.tsym.type.getTypeArguments().appendList(generic.type.getTypeArguments());
			for (List<Type> tl = targs; tl != null; tl = tl.tail) { // hack
			    tl.head = types.unexpandWildcard(tl.head);
			}
			Type rtype = found.type.getReturnType();
			rtype = types.subst2(rtype,
					     targs,
					     targs,
					     true);
			//System.err.println("rtype="+rtype);
			Type mt1 = //rs.newMethTemplate(formals, targs);
			    new ForAll(targs,
				       new MethodType(formals, rtype, List.<Type>nil(), 
						      syms.methodClass));
			
			//System.err.println("mt1="+mt1);
			try {
			    // omg, this is so fucked...
			    List<Type> parms = List.of(self).appendList(mtype.getParameterTypes());
			    //System.err.println("mt1="+types.toF3String(mt1));
			    //System.err.println("parms="+parms);
			    Type instanced = 
				rs.rawInstantiate(env, found, mt1, parms, List.<Type>nil(), true, false, Warner.noWarnings);
			    //System.err.println("found="+found+ ": instance="+instanced);
			    /*
			    Type proto = 
				syms.makeFunctionType(List.of(hack).prepend(rtype)).asMethodOrForAll();
			    proto = types.subst2(proto,
						 msym.owner.type.tsym.type.getTypeArguments(),
						 msym.owner.type.tsym.type.getTypeArguments());
			    proto = new ForAll(msym.owner.type.tsym.type.getTypeArguments(), proto);
			    System.err.println("fun1="+proto);
			    Type instanced2 = rs.rawInstantiate(env, found, proto, List.of(self), List.<Type>nil(), true, false, Warner.noWarnings);
			    System.err.println("found="+found+ ": instanced2="+instanced2);
			    restype = instanced2.getReturnType();
			    */
			    restype = instanced.getReturnType();
			    tree.type = restype;
			    Type mt = mtype;
			    if (mt instanceof ForAll) {
				mt = ((ForAll)mt).qtype;
			    }
			    MethodType mmt = (MethodType)mt;
			    if (mmt.restype != restype) {
				msym = new F3Resolve.InstanceMethodSymbol((MethodSymbol)msym, mtype, msym.owner);
				mmt.restype = restype;
				F3TreeInfo.setSymbol(tree.meth, msym);
			    }
			    //System.err.println("msym="+msym+": "+msym.type);
			    //System.err.println("msym=>"+msym.type);
			} catch (F3Infer.NoInstanceException ex) {
			    //ex.printStackTrace();
			    System.err.println(ex.getDiagnostic());
			    if (true) {
				Type t = self;
				if (ex.isAmbiguous) {
				    JCDiagnostic d = ex.getDiagnostic();
				    log.error(dt.pos(),
					      d!=null ? MsgSym.MESSAGE_UNDETERMINDED_TYPE_1 : MsgSym.MESSAGE_UNDETERMINDED_TYPE,
					      types.toF3String(generic.type), d);
				} else {
				    JCDiagnostic d = ex.getDiagnostic();
				    chk.typeError(dt.pos(),
						  //JCDiagnostic.fragment(d!=null ? MsgSym.MESSAGE_INCOMPATIBLE_TYPES_1 : MsgSym.MESSAGE_INCOMPATIBLE_TYPES, d),
						  "Incompatible types",
						  types.toF3String(t), types.toF3String(msym.owner.type));
				}
			    }
			}
		    } else {
		    }
		}
	    }
            List<F3Expression> params = tree.getArguments();
	    List<Type> pts = mtype.getParameterTypes();
	    //System.err.println("msym.type="+msym.type);
            boolean inferredArg = false;
            for (List<F3Expression> l = params; l.nonEmpty(); l = l.tail) {
		if (pts.head == null) {
		    continue;
		}
                List ptt = pts;
		Type t = reader.translateType(types.normalize(pts.head));
		pts = pts.tail;
		if (t instanceof FunctionType) {
		    t = ((FunctionType)t).asMethodOrForAll();
		}
		//System.err.println("t="+t);
		F3Expression pvar = l.head;
		if (pvar instanceof F3FunctionValue) {
		    F3FunctionValue val = (F3FunctionValue)pvar;
		    if (val.infer) {
			val.infer = false;
			List<Type> inferred = t.getParameterTypes();
			//System.err.println("inferred="+inferred);
			for (F3Var var: val.funParams) {
			    if (inferred != null) {
				Type ip = inferred.head;
				//System.err.println("inferred type="+ip);
				//var.init = f3make.Type(ip);
				var.type = ip;
				//System.err.println("inferred type exp="+var.init);
				//attribVar(var, env);
				inferred = inferred.tail;
			    }
			}
			val.type = t;
			finishFunctionDefinition(val.definition, env);
                        val.type = val.definition.type;
                        val.definition.sym.type = val.type; // hack
                        inferredArg = true;
                        ptt.head = val.type;
		    }
		}
	    }
	}
        if (msym!=null && msym.owner!=null && msym.owner.type!=null &&
                (msym.owner.type.tsym == syms.f3_AutoImportRuntimeType.tsym ||
                 msym.owner.type.tsym == syms.f3_RuntimeType.tsym) &&
                (methName == defs.isInitialized_MethodName ||
                methName == defs.isReadOnly_MethodName)) {
            msym.flags_field |= F3Flags.FUNC_IS_BUILTINS_SYNTH;
            for (List<F3Expression> l = tree.args; l.nonEmpty(); l = l.tail, i++) {
                F3Expression arg = l.head;
                Symbol asym = F3TreeInfo.symbol(arg);
                if (asym == null || !(asym.type instanceof ErrorType)) {
                    if (asym == null ||
                            !(asym instanceof F3VarSymbol) ||
                            (arg.getF3Tag() != F3Tag.IDENT && arg.getF3Tag() != F3Tag.SELECT) ||
                            (asym.flags() & F3Flags.IS_DEF) != 0 ||
                            asym.owner == null ||
                            asym.owner.kind != TYP) {
                        log.error(tree.pos(), MsgSym.MESSAGE_F3_APPLIED_TO_INSTANCE_VAR, methName);
                    } else {
                        // check that we have write access
                        // unless it is a public-init or public-read var, that was already handled 
                        // the regular access check
                        if ((asym.flags() & (F3Flags.PUBLIC_INIT | F3Flags.PUBLIC_READ)) != 0) {
                            Type site;
                            F3Tree base;
                            switch (arg.getF3Tag()) {
                                case IDENT:
                                    base = null;
                                    site = env.getEnclosingClassType();
                                    break;
                                case SELECT:
                                    base = ((F3Select)arg).selected;
                                    site = base.type;
                                    break;
                                default:
                                    throw new AssertionError(); // see above, should not occur
                            }
                            chk.checkAssignable(tree.pos(), (F3VarSymbol) asym, base, site, env, WriteKind.VAR_QUERY);
                        }
                    }
                }
            }
        }

	result = check(tree, capture(restype), VAL, pkind, pt, pSequenceness);
	tree.type = result;
	//System.err.println("tree="+tree);
	//System.err.println("result="+result);
        chk.validate(tree.typeargs);
	// hack...
    }

    //@Override
    public void visitAssignop(F3AssignOp tree) {
        // Attribute arguments.
        Type owntype = attribTree(tree.lhs, env, VAR, Type.noType);
        Type operand = attribExpr(tree.rhs, env);

        // Fix types of numeric arguments with non -specified type.
        Symbol lhsSym = F3TreeInfo.symbol(tree.lhs);
        if (lhsSym != null &&
                (lhsSym.type == null || lhsSym.type == Type.noType || lhsSym.type == syms.f3_AnyType)) {
            F3Var lhsVarTree = varSymToTree.get(lhsSym);
            owntype = setBinaryTypes(tree.getF3Tag(), tree.lhs, lhsVarTree, lhsSym.type, lhsSym);
        }

        Symbol rhsSym = F3TreeInfo.symbol(tree.rhs);
        if (rhsSym != null  &&
                (rhsSym.type == null || rhsSym.type == Type.noType || rhsSym.type == syms.f3_AnyType)) {
            F3Var rhsVarTree = varSymToTree.get(rhsSym);
            operand = setBinaryTypes(tree.getF3Tag(), tree.rhs, rhsVarTree, rhsSym.type, rhsSym);
        }

        // Find operator.        
        Symbol operator = tree.operator = attribBinop(
            tree.pos(), tree.getNormalOperatorF3Tag(),
            owntype, operand, env);

        if (operator.kind == MTH) {
            if (operator instanceof OperatorSymbol) {
                chk.checkOperator(tree.pos(),
                                  (OperatorSymbol)operator,
                                  tree.getF3Tag(),
                                  owntype,
                                  operand);
            }
            if (types.isSameType(operator.type.getReturnType(), syms.stringType)) {
                // String assignment; make sure the lhs is a string
                chk.checkType(tree.lhs.pos(),
                              owntype,
                              syms.stringType, Sequenceness.DISALLOWED);
            } else {
                chk.checkDivZero(tree.rhs.pos(), operator, operand);
                chk.checkCastable(tree.rhs.pos(),
                                  operator.type.getReturnType(),
                                  owntype);
            }
        }
        result = check(tree, operator.type.getReturnType(), VAL, pkind, owntype, pSequenceness);

        if (lhsSym != null && tree.rhs != null) {
            F3Var lhsVar = varSymToTree.get(lhsSym);
            if (lhsVar != null && (lhsVar.getF3Type() instanceof F3TypeUnknown)) {
                if ((lhsVar.type == null || lhsVar.type == syms.f3_AnyType)) {
                    if (tree.rhs.type != null && lhsVar.type != tree.rhs.type) {
                        lhsVar.type = lhsSym.type = tree.rhs.type;
                        F3Expression jcExpr = f3make.at(tree.pos()).Ident(lhsSym);
                        lhsVar.setF3Type(f3make.at(tree.pos()).TypeClass(jcExpr, lhsVar.getF3Type().getCardinality()));
                    }
                }
            }
        }
    }

    //@Override
    public void visitUnary(F3Unary tree) {
        switch (tree.getF3Tag()) {
            case SIZEOF: {
                attribExpr(tree.arg, env);
                result = check(tree, syms.f3_IntegerType, VAL, pkind, pt, pSequenceness);
                return;
            }
            case AMP: {
		F3Tag tag = F3TreeInfo.skipParens(tree.arg).getF3Tag();
		if (tag == F3Tag.IDENT || tag == F3Tag.SELECT) {
		    Type argtype = chk.checkNonVoid(tree.arg.pos(), attribExpr(tree.arg, env));
		    Symbol varSym = F3TreeInfo.symbol(tree.arg);
		    boolean readOnly = true;
		    Type siteType = null;
		    if (tag == F3Tag.SELECT) {
			siteType = ((F3Select)tree.arg).selected.type;
		    }
		    if (varSym != null) {
			long flags = varSym.flags();
			readOnly = 
			    ((flags & (F3Flags.IS_DEF | 
				      F3Flags.PUBLIC_READ | 
				      Flags.PARAMETER | 
				       F3Flags.PUBLIC_INIT)) != 0);
		    }
		    // result type is argument type, unless this is a singleton, then convert to a sequence

		    if (siteType == null) {
			siteType = new WildcardType(syms.f3_ObjectType, BoundKind.EXTENDS, syms.boundClass);
		    }
		    Type owntype = types.pointerType(siteType, argtype, readOnly);
		    result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
		} else {
		    log.error(tree.pos(), MsgSym.MESSAGE_UNEXPECTED_TYPE,
			      Resolve.kindNames(VAR), Resolve.kindName(VAL));
		}
                return;
            }
            case DEREF: {
                Type argtype = chk.checkNonVoid(tree.arg.pos(), attribExpr(tree.arg, env));
		Type argtype1 = check(tree, types.erasure(argtype), VAL, pkind, 
				      types.erasure(syms.f3_ReadOnlyPointerType), 
				      pSequenceness);
                Type owntype = types.pointerElementType(argtype);
                result = check(tree, owntype, VAL, pkind, pt, pSequenceness); // fix me!!!
		return;
            }
            case REVERSE: {
                Type argtype = chk.checkNonVoid(tree.arg.pos(), attribExpr(tree.arg, env));
                // result type is argument type, unless this is a singleton, then convert to a sequence
                Type owntype = (argtype.tag == ERROR || types.isSequence(argtype))? argtype : types.sequenceType(argtype);
                result = check(tree, owntype, VAL, pkind, pt, Sequenceness.REQUIRED);
                return;
            }
        }
        boolean isIncDec = tree.getF3Tag().isIncDec();

        Type argtype;
        if (isIncDec) {
            // Attribute arguments.
            argtype = attribTree(tree.arg, env, VAR, Type.noType);
        } else {
            argtype = chk.checkNonVoid(tree.arg.pos(), attribExpr(tree.arg, env));
        }

        //TODO: redundant now, but if we want to deferentiate error for increment/decremenet
        // from assignment, this code may be useful
        /***
        if (isIncDec) {
            Symbol argSym = F3TreeInfo.symbol(tree.arg);
            if (argSym == null) {
                log.error(tree, MsgSym.MESSAGE_F3_INVALID_ASSIGNMENT);
                return;
            }
            if ((argSym.flags() & F3Flags.IS_DEF) != 0L) {
                log.error(tree, MsgSym.MESSAGE_F3_CANNOT_ASSIGN_TO_DEF, argSym);
                return;
            }
            if ((argSym.flags() & Flags.PARAMETER) != 0L) {
                log.error(tree, MsgSym.MESSAGE_F3_CANNOT_ASSIGN_TO_PARAMETER, argSym);
                return;
            }
        }
        ***/

        Symbol sym =  rs.resolveUnaryOperator(tree.pos(),
                tree.getF3Tag(),
                env,
                types.unboxedTypeOrType(argtype));
	tree.methodName = sym.name;
        Type owntype = syms.errType;
        if (sym instanceof OperatorSymbol) {
            // Find operator.
            Symbol operator = tree.operator = sym;
            if (operator.kind == MTH) {
                owntype = isIncDec
                    ? tree.arg.type
                    : operator.type.getReturnType();
            }
        } else {
	    tree.operator = sym;
	    //System.err.println("tree.name="+tree.methodName);
            owntype = sym.type.getReturnType();
        }
        result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
    }

    private Type setBinaryTypes(F3Tag opcode, F3Expression tree, F3Var var, Type type, Symbol treeSym) {
        Type newType = type;
        F3Expression jcExpression = null;
        // boolean type
        if (opcode == F3Tag.OR ||
            opcode == F3Tag.AND) {
            newType = syms.f3_BooleanType;
            jcExpression = f3make.at(tree.pos()).Ident(syms.f3_BooleanType.tsym);
        }
        // Integer type
        else if (opcode == F3Tag.MOD) {
            newType = syms.f3_IntegerType;
            jcExpression = f3make.at(tree.pos()).Ident(syms.f3_IntegerType.tsym);
        }
        // Number type
        else if (opcode == F3Tag.LT ||
                 opcode == F3Tag.GT ||
                 opcode == F3Tag.LE ||
                 opcode == F3Tag.GE ||
                 opcode == F3Tag.PLUS ||
                 opcode == F3Tag.MINUS ||
                 opcode == F3Tag.MUL ||
                 opcode == F3Tag.DIV ||
                 opcode == F3Tag.PLUS_ASG ||
                 opcode == F3Tag.MINUS_ASG ||
                 opcode == F3Tag.MUL_ASG ||
                 opcode == F3Tag.DIV_ASG) {
            newType = syms.f3_DoubleType;
            jcExpression = f3make.at(tree.pos()).Ident(syms.f3_DoubleType.tsym);
        }
        else
            return newType;

        // tree is not null here
        tree.setType(newType);
        treeSym.type = newType;

        if (var != null) {
            var.setType(newType);
            F3Type f3Type = f3make.at(tree.pos()).TypeClass(jcExpression, Cardinality.SINGLETON);
            f3Type.type = newType;
            var.setF3Type(f3Type);
            var.sym.type = newType;
        }

        return newType;
    }

    public Symbol attribBinop(DiagnosticPosition pos, F3Tag tag, Type left, Type right, F3Env<F3AttrContext> env) {
        boolean isEq = tag == F3Tag.EQ || tag == F3Tag.NE;
        //comparson operators == and != should work in the sequence vs. non-sequence
        //case - resolution of comparison operators works over non-sequence types
        Type leftUnboxed = (isEq && types.isSequence(left)) ?
            types.elementType(left) :
            types.unboxedTypeOrType(left);
        Type rightUnboxed = (isEq && types.isSequence(right)) ?
            types.elementType(right) :
            types.unboxedTypeOrType(right);
        return rs.resolveBinaryOperator(pos, tag, env, leftUnboxed, rightUnboxed);
    }

    //@Override
    public void visitBinary(F3Binary tree) {
        // Attribute arguments.
        Type left = chk.checkNonVoid(tree.lhs.pos(), attribExpr(tree.lhs, env));
        Type right = chk.checkNonVoid(tree.rhs.pos(), attribExpr(tree.rhs, env));
	//System.err.println("tree="+tree+ " left="+left+", right="+right);

        if (left == syms.f3_UnspecifiedType) {
            left = setEffectiveExpressionType(tree.lhs, newTypeFromType(getEffectiveExpressionType(right)));
        }
        else if (right == syms.f3_UnspecifiedType) {
            right = setEffectiveExpressionType(tree.rhs, newTypeFromType(getEffectiveExpressionType(left)));
        }

        // Fix types of numeric arguments with non -specified type.
        boolean lhsSet = false;

        // If an operand is untyped AND it's a var or attribute, constrain the
        // operand based on the operator.  Rather a special-case kludge.
        Symbol lhsSym = F3TreeInfo.symbol(tree.lhs);
        if (lhsSym != null &&
                (lhsSym.type == null || lhsSym.type == Type.noType || lhsSym.type == syms.f3_AnyType)) {
            F3Var lhsVarTree = varSymToTree.get(lhsSym);
            left = setBinaryTypes(tree.getF3Tag(), tree.lhs, lhsVarTree, lhsSym.type, lhsSym);
            lhsSet = true;
        }
        Symbol rhsSym = F3TreeInfo.symbol(tree.rhs);
        if (rhsSym != null  &&
                (rhsSym.type == null || rhsSym.type == Type.noType || rhsSym.type == syms.f3_AnyType) || (lhsSet && lhsSym == rhsSym)) {
            F3Var rhsVarTree = varSymToTree.get(rhsSym);
            right = setBinaryTypes(tree.getF3Tag(), tree.rhs, rhsVarTree, rhsSym.type, rhsSym);
        }
        Symbol sym = attribBinop(tree.pos(), tree.getF3Tag(), left, right, env);
        Type owntype = syms.errType;
	tree.methodName = sym.name;
	tree.infix = sym.type.getParameterTypes().size() == 1;
        if (sym instanceof OperatorSymbol) {
            // Find operator.
            Symbol operator = tree.operator = sym;

            if (operator.kind == MTH) {
                owntype = operator.type.getReturnType();
                int opc = chk.checkOperator(tree.lhs.pos(),
                                            (OperatorSymbol)operator,
                                            tree.getF3Tag(),
                                            left,
                                            right);

                // If both arguments are constants, fold them.
                if (left.constValue() != null && right.constValue() != null) {
                    Type ctype = cfolder.fold2(opc, left, right);
                    if (ctype != null) {
                        owntype = cfolder.coerce(ctype, owntype);

                        // Remove constant types from arguments to
                        // conserve space. The parser will fold concatenations
                        // of string literals; the code here also
                        // gets rid of intermediate results when some of the
                        // operands are constant identifiers.
                        if (tree.lhs.type.tsym == syms.stringType.tsym) {
                            tree.lhs.type = syms.stringType;
                        }
                        if (tree.rhs.type.tsym == syms.stringType.tsym) {
                            tree.rhs.type = syms.stringType;
                        }
                    }
                }

                // Check that operands a, b of a binary reference comparison
                // ==, != are castable to each other (either a castable to b
                // or b castable to a)
                if ((opc == ByteCodes.if_acmpeq || opc == ByteCodes.if_acmpne)) {
                    if (!types.isCastable(left, right, Warner.noWarnings) &&
                            !types.isCastable(right, left, Warner.noWarnings)) {
                        log.error(tree.pos(), MsgSym.MESSAGE_INCOMPARABLE_TYPES,
                            types.toF3String(left),
                            types.toF3String(right));
                    }
                }
                chk.checkDivZero(tree.rhs.pos(), operator, right);
            }
        } else {
	    tree.operator = sym;
            owntype = sym.type.getReturnType();
        }
        result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
        if (tree.getF3Tag() == F3Tag.PLUS && owntype == syms.stringType) {
            log.error(tree.pos(), MsgSym.MESSAGE_F3_STRING_CONCATENATION, expressionToString(tree));
        }
	/*
	System.err.println("attr tree.methodName="+tree.methodName);
	System.err.println("attr tree.sym="+sym.getClass()+": "+sym);
	System.err.println("attr tree.op="+tree.operator);
	System.err.println("attr tree.infix="+tree.infix);
	*/
    }
    //where
    private String expressionToString(F3Expression expr) {
        if (expr.type == syms.stringType) {
            if (expr.getF3Tag() == F3Tag.LITERAL) {
                return (String) (((F3Literal) expr).getValue());
            } else if (expr.getF3Tag() == F3Tag.PLUS) {
                F3Binary plus = (F3Binary) expr;
                return expressionToString(plus.lhs) + expressionToString(plus.rhs);
            }
        }
        return "{" + expr.toString() + "}";
    }

    boolean isPrimitiveOrBoxed(Type pt, int tag) {
        return pt.tag == tag ||
                (pt.tsym instanceof ClassSymbol && 
                 ((ClassSymbol) pt.tsym).fullname == syms.boxedName[tag]);
    }

    //@Override
    public void visitLiteral(F3Literal tree) {
        Type expected = types.elementTypeOrType(pt);
        if (tree.value instanceof Double) {
            double dvalue = ((Double) tree.value).doubleValue();
            double dabs = Math.abs(dvalue);
            boolean fitsInFloat = Double.isInfinite(dvalue) || dvalue == 0.0 ||
                    (dabs <= Float.MAX_VALUE && dabs >= Float.MIN_VALUE);
            if (isPrimitiveOrBoxed(expected, DOUBLE) || (expected.tag == UNKNOWN && !fitsInFloat)) {
                tree.typetag = TypeTags.DOUBLE;
            }
            else {
                if (isPrimitiveOrBoxed(expected, FLOAT) && !fitsInFloat) {
                    log.error(tree, MsgSym.MESSAGE_F3_LITERAL_OUT_OF_RANGE, "Number", tree.value.toString());
                }
                tree.typetag = TypeTags.FLOAT;
                tree.value = Float.valueOf((float) dvalue);
            }
        }
        else if ((tree.value instanceof Integer || tree.value instanceof Long) &&
                tree.typetag != TypeTags.BOOLEAN) {
            long lvalue = ((Number) tree.value).longValue();
            if (isPrimitiveOrBoxed(expected, BYTE)) {
                if (lvalue != (byte) lvalue) {
                    log.error(tree, MsgSym.MESSAGE_F3_LITERAL_OUT_OF_RANGE, "Byte", tree.value.toString());
                }
                tree.typetag = TypeTags.BYTE;
                tree.value = Byte.valueOf((byte) lvalue);
            }
            else if (isPrimitiveOrBoxed(expected, SHORT)) {
                if (lvalue != (short) lvalue) {
                    log.error(tree, MsgSym.MESSAGE_F3_LITERAL_OUT_OF_RANGE, "Short", tree.value.toString());
                }
                tree.typetag = TypeTags.SHORT;
                tree.value = Short.valueOf((short) lvalue);
            }
            else if (isPrimitiveOrBoxed(expected, CHAR)  && lvalue == (char) lvalue) {
                tree.typetag = TypeTags.CHAR;
            }
            else if (isPrimitiveOrBoxed(expected, FLOAT)) {
                tree.typetag = TypeTags.FLOAT;
                tree.value = Float.valueOf(lvalue);
            }
            else if (isPrimitiveOrBoxed(expected, DOUBLE)) {
                tree.typetag = TypeTags.DOUBLE;
                tree.value = Double.valueOf(lvalue);
            }
            else if (isPrimitiveOrBoxed(expected, INT) || tree.typetag == TypeTags.INT) {
                if (tree.typetag == TypeTags.LONG) {
                    log.error(tree, MsgSym.MESSAGE_F3_LITERAL_OUT_OF_RANGE, "Integer", tree.value.toString());
                }
                tree.typetag = TypeTags.INT;
                if (! (tree.value instanceof Integer))
                    tree.value = Integer.valueOf((int) lvalue);
            }
            else {
                tree.typetag = TypeTags.LONG;
                if (! (tree.value instanceof Long))
                    tree.value = Long.valueOf(lvalue);
            }
        }
	if (tree.typetag == TypeTags.BOT && pkind == TYP) {
	    tree.type = result = syms.botType;
	} else {
	    result = check(
			   tree, litType(tree.typetag, pt), VAL, pkind, pt, pSequenceness);
	}
    }
    //where
    /** Return the type of a literal with given type tag.
     */
    private Type litType(int tag, Type pt) {
        switch (tag) {
            case TypeTags.CLASS: return syms.stringType;
            case TypeTags.BOT: return types.isSequence(pt) &&
                                      !types.isNullable(types.elementType(pt)) ?
                pt :
                syms.botType;
            default: return syms.typeOfTag[tag];
        }
    }
    
    //@Override
    public void visitErroneous(F3Erroneous tree) {
      //  if (tree.getErrorTrees() != null)
       //     for (F3Tree err : tree.getErrorTrees())
         //       attribTree(err, env, ERR, pt);
        result = tree.type = syms.errType;
    }

    /** Main method: attribute class definition associated with given class symbol.
     *  reporting completion failures at the given position.
     *  @param pos The source position at which completion errors are to be
     *             reported.
     *  @param c   The class symbol whose definition will be attributed.
     */
    public void attribClass(DiagnosticPosition pos, F3ClassDeclaration tree, ClassSymbol c) {
        try {
            annotate.flush();
            attribClass(tree, c);
	    //VarianceAnalysis ana = new VarianceAnalysis();
	    //ana.scan(tree);
	    //System.err.println(ana.result);
        } catch (CompletionFailure ex) {
            chk.completionError(pos, ex);
        }
    }

    /** Attribute class definition associated with given class symbol.
     *  @param c   The class symbol whose definition will be attributed.
     */
    void attribClass(F3ClassDeclaration tree, ClassSymbol c) throws CompletionFailure {
        if (c.type.tag == ERROR) return;

        // Check for cycles in the inheritance graph, which can arise from
        // ill-formed class files.
        chk.checkNonCyclic(null, c.type);

        if (tree != null) {
            attribSupertypes(tree, c);
        }

        // The previous operations might have attributed the current class
        // if there was a cycle. So we test first whether the class is still
        // UNATTRIBUTED.
        if ((c.flags_field & UNATTRIBUTED) != 0) {
            c.flags_field &= ~UNATTRIBUTED;

            // Get environment current at the point of class definition.
            F3Env<F3AttrContext> localEnv = enter.typeEnvs.get(c);

            // The info.lint field in the envs stored in enter.typeEnvs is deliberately uninitialized,
            // because the annotations were not available at the time the env was created. Therefore,
            // we look up the environment chain for the first enclosing environment for which the
            // lint value is set. Typically, this is the parent env, but might be further if there
            // are any envs created as a result of TypeParameter nodes.
            F3Env<F3AttrContext> lintEnv = localEnv;
            while (lintEnv.info.lint == null)
                lintEnv = lintEnv.next;

            // Having found the enclosing lint value, we can initialize the lint value for this class
            localEnv.info.lint = lintEnv.info.lint.augment(c.attributes_field, c.flags());

            Lint prevLint = chk.setLint(localEnv.info.lint);
            JavaFileObject prev = log.useSource(c.sourcefile);

            try {
                attribClassBody(localEnv, c);

            } finally {
                log.useSource(prev);
                chk.setLint(prevLint);
            }
        }
	//tree.mods.flags |= c.flags();
    }

    /** Clones a type without copiyng constant values
     * @param t the type that needs to be cloned.
     * @return  the cloned type with no cloned constants.
     */
    public Type newTypeFromType(Type t) {
        if (t == null) return null;
        switch (t.tag) {
            case BYTE:
                return syms.byteType;
            case CHAR:
                return syms.charType;
            case SHORT:
                return syms.shortType;
            case INT:
                return syms.intType;
            case LONG:
                return syms.longType;
            case FLOAT:
                return syms.floatType;
            case DOUBLE:
                return syms.doubleType;
            case BOOLEAN:
                return syms.booleanType;
            case VOID:
                return syms.voidType;
            default:
                return t;
        }
    }

    /**
     * Gets the effective type of a type. If MethodType - the return type,
     * otherwise the passed in type.
     */
    private Type getEffectiveExpressionType(Type type) {
        if (type.tag == TypeTags.METHOD) {
            return type.getReturnType();
        }

        return type;
    }

    /**
     * Sets the effective type of an expression. If MethodType - the return type,
     * otherwise the whole type of the expression is set.
     */
    private Type setEffectiveExpressionType(F3Expression expression, Type type) {
        if (expression.type.tag == TypeTags.METHOD) {
            ((MethodType)expression.type).restype = type;
        }
        else {
            expression.type = type;
        }

        return expression.type;
    }

// Begin F3 trees
    //@Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
        // Local classes have not been entered yet, so we need to do it now:
        if ((env.info.scope.owner.kind & (VAR | MTH)) != 0) {
            enter.classEnter(tree, env);
	}

        ClassSymbol c = tree.sym;
        if (c == null) {
            // exit in case something drastic went wrong during enter.
            result = null;
        } else {

            // make sure class has been completed:

            c.complete();

            attribSupertypes(tree, c);

            attribClass(tree.pos(), tree, c);
	    
	    result = tree.type = c.type;

            types.addF3Class(c, tree);
        }
        result = syms.voidType;
    }

    private void attribSupertypes(F3ClassDeclaration tree, ClassSymbol c) {
        F3ClassSymbol f3ClassSymbol = null;
        if (c instanceof F3ClassSymbol) {
            f3ClassSymbol = (F3ClassSymbol)c;
        }

        Symbol javaSupertypeSymbol = null;
        for (F3Expression superClass : tree.getSupertypes()) {
            Type supType = superClass.type == null ? attribSuperType(superClass, env)
                                                   : superClass.type;
	    supType.tsym.complete();
	    //System.err.println("tree="+superClass);
	    //System.err.println("supType "+c+"="+supType);
	    //System.err.println("supType.sym="+supType.tsym.type);
	    if (supType instanceof FunctionType) {
		supType = types.normalize(supType, false);
	    }
            // java.lang.Enum may not be subclassed by a non-enum
            if (supType.tsym == syms.enumSym &&
                ((c.flags_field & (Flags.ENUM|Flags.COMPOUND)) == 0))
                log.error(superClass.pos(), MsgSym.MESSAGE_ENUM_NO_SUBCLASSING);

            // Enums may not be extended by source-level classes
            if (supType.tsym != null &&
                ((supType.tsym.flags_field & Flags.ENUM) != 0) &&
                ((c.flags_field & Flags.ENUM) == 0) &&
                !target.compilerBootstrap(c)) {
                log.error(superClass.pos(), MsgSym.MESSAGE_ENUM_TYPES_NOT_EXTENSIBLE);
            }

            if (!(supType instanceof TypeVar) &&
		!supType.isInterface() &&
		!types.isF3Class(supType.tsym) &&
		!types.isMixin(supType.tsym) &&
		!supType.isPrimitive() &&
		f3ClassSymbol.type instanceof ClassType) {
                if (javaSupertypeSymbol == null) {
                    javaSupertypeSymbol = supType.tsym;
                    // Verify there is a non-parametric constructor.
                    boolean hasNonParamCtor = true; // If there is no non-param constr we will create one later.
                    if (javaSupertypeSymbol != null && javaSupertypeSymbol.members() != null) for (Scope.Entry e1 = javaSupertypeSymbol.members().elems;
			 e1 != null;
			 e1 = e1.sibling) {
                            Symbol s1 = e1.sym;
                            if (s1 != null &&
                                    s1.name == names.init &&
                                    s1.kind == Kinds.MTH) {
                                MethodType mtype = ((MethodSymbol)s1).type.asMethodType();
                                if (mtype != null && mtype.getParameterTypes().isEmpty()) {
                                    hasNonParamCtor = true;
                                    break;
                                }
                                else {
                                    hasNonParamCtor = false;
                                }
                            }
                    }

                    if (hasNonParamCtor) {
                        ((ClassType)f3ClassSymbol.type).supertype_field = supType;
                    }
                    else {
                        log.error(superClass.pos(), MsgSym.MESSAGE_F3_BASE_JAVA_CLASS_NON_PAPAR_CTOR, supType.tsym.name);

                    }
                }
                else {
                    // We are already extending one Java class. No more than one is allowed. Report an error.
                    log.error(superClass.pos(), MsgSym.MESSAGE_F3_ONLY_ONE_BASE_JAVA_CLASS_ALLOWED, supType.tsym.name);
                }
            }
        }
    }

    //@Override
    public void visitInitDefinition(F3InitDefinition that) {
        Symbol symOwner = env.info.scope.owner;
        try {
            MethodType mt = new MethodType(List.<Type>nil(), syms.voidType, List.<Type>nil(), (TypeSymbol)symOwner);
            that.sym = new MethodSymbol(0L, defs.init_MethodSymbolName, mt, symOwner);
            env.info.scope.owner = that.sym;
            F3Env<F3AttrContext> localEnv = env.dup(that);
            localEnv.outer = env;
            attribExpr(that.getBody(), localEnv);
        }
        finally {
            env.info.scope.owner = symOwner;
        }
    }

    public void visitPostInitDefinition(F3PostInitDefinition that) {
        Symbol symOwner = env.info.scope.owner;
        try {
            MethodType mt = new MethodType(List.<Type>nil(), syms.voidType, List.<Type>nil(), (TypeSymbol)symOwner);
            that.sym = new MethodSymbol(0L, defs.postinit_MethodSymbolName, mt, symOwner);
            env.info.scope.owner = that.sym;
            F3Env<F3AttrContext> localEnv = env.dup(that);
            localEnv.outer = env;
            attribExpr(that.getBody(), localEnv);
        }
        finally {
            env.info.scope.owner = symOwner;
        }
    }

    //@Override
    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        boolean isSeq = types.isSequence(pt);
        Type owntype = pt.tag == NONE || pt.tag == UNKNOWN ? syms.f3_EmptySequenceType :
                isSeq ? pt : types.sequenceType(pt);
	//System.err.println("empty seq type: "+ owntype);
        result = check(tree, owntype, VAL, pkind, Type.noType, pSequenceness);
    }

    //@Override
    public void visitSequenceRange(F3SequenceRange tree) {
        Type lowerType =  attribExpr(tree.getLower(), env);
        Type upperType = attribExpr(tree.getUpper(), env);
        Type stepType = tree.getStepOrNull() == null? syms.f3_IntegerType : attribExpr(tree.getStepOrNull(), env);
        boolean allInt = true;
        if (lowerType != syms.f3_IntegerType) {
            allInt = false;
            if (lowerType != syms.f3_FloatType && lowerType != syms.f3_DoubleType) {
                log.error(tree.getLower().pos(), MsgSym.MESSAGE_F3_RANGE_START_INT_OR_NUMBER);
            }
        }
        if (upperType != syms.f3_IntegerType) {
            allInt = false;
            if (upperType != syms.f3_FloatType && upperType != syms.f3_DoubleType) {
                log.error(tree.getLower().pos(), MsgSym.MESSAGE_F3_RANGE_END_INT_OR_NUMBER);
            }
        }
        if (stepType != syms.f3_IntegerType) {
            allInt = false;
            if (stepType != syms.f3_FloatType && stepType != syms.f3_DoubleType) {
                log.error(tree.getStepOrNull().pos(), MsgSym.MESSAGE_F3_RANGE_STEP_INT_OR_NUMBER);
            }
        }
	if (tree.getLower().getF3Tag() == F3Tag.LITERAL && tree.getUpper().getF3Tag() == F3Tag.LITERAL
	    && (tree.getStepOrNull() == null || tree.getStepOrNull().getF3Tag() == F3Tag.LITERAL)) {
            chk.warnEmptyRangeLiteral(tree.pos(), (F3Literal)tree.getLower(), (F3Literal)tree.getUpper(), (F3Literal)tree.getStepOrNull(), tree.isExclusive());
	}
        Type owntype = types.sequenceType(allInt? syms.f3_IntegerType : syms.f3_FloatType);
        result = tree.type = check(tree, owntype, VAL, pkind, pt, pSequenceness);
    }

    //@Override
    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        Type elemType = null;
        boolean errorFound = false;
        for (F3Expression expr : tree.getItems()) {
            Type itemType = attribTree(expr, env, VAL,
                    pt, Sequenceness.PERMITTED);
            if (types.isSequence(itemType) || types.isArray(itemType)) {
                itemType = types.isSequence(itemType) ? types.elementType(itemType) : types.elemtype(itemType);
            }
            itemType = chk.checkNonVoid(expr, itemType);
            if (elemType == null || itemType.tag == NONE || itemType.tag == ERROR) {                
                elemType = itemType;
            }
            else
                elemType = unionType(tree, itemType, elemType);
            errorFound |= elemType.isErroneous() || itemType.isErroneous();
        }
        
        if (!errorFound && 
                (pt.tag == NONE ||
                 pt == syms.f3_UnspecifiedType)) {
            Type typeToCheck = types.sequenceType(types.normalize(elemType));
            for (F3Expression expr : tree.getItems()) {
                attribTree(expr, env, VAL, typeToCheck, Sequenceness.PERMITTED);
            }
        }
        Type owntype = elemType.tag == ERROR ? elemType : types.sequenceType(elemType);
	result = check(tree, owntype, VAL, pkind, pt, pSequenceness);
        if (owntype == result && pt.tag != NONE && pt != syms.f3_UnspecifiedType && pt != syms.objectType) {
             result = tree.type = pt;
        }
    }

    //@Override
    public void visitSequenceSlice(F3SequenceSlice tree) {
        F3Expression seq = tree.getSequence();
         // Attribute as a tree so we can check that target is assignable
         // when pkind is VAR
         //
	Type seqType = attribTree(seq, env, pkind, Type.noType, Sequenceness.PERMITTED);
	if (types.isSequence(seqType)) {
	    attribExpr(tree.getFirstIndex(), env, syms.f3_IntegerType);
	    if (tree.getLastIndex() != null) {
		attribExpr(tree.getLastIndex(), env, syms.f3_IntegerType);
	    }
	    result = check(tree, seqType, VAR, pkind, pt, pSequenceness);
	} else {
	    Name get = names.fromString("slice");
	    F3Expression getTree = f3make.at(tree.pos).Select(seq, get, true);
	    List<F3Expression> args = List.of(tree.getFirstIndex());
	    if (tree.getLastIndex() != null) {
		args = args.append(tree.getLastIndex());
	    }
	    F3Expression invokeTree = f3make.at(tree.pos).Apply(List.<F3Expression>nil(),
								getTree,
								args);
	    tree.slice = invokeTree;
	    result = attribExpr(invokeTree, env);
	}
    }

    //@Override
    public void visitSequenceIndexed(F3SequenceIndexed tree) {

        F3Expression seq = tree.getSequence();

        // Attribute as a tree so we can check that target is assignable
        // when pkind is VAR
        //
        Type seqType = attribTree(seq, env, pkind, Type.noType, Sequenceness.PERMITTED);
	if (types.isSequence(seqType) || types.isArray(seqType)) {
	    attribExpr(tree.getIndex(), env, syms.f3_IntegerType);
	    chk.checkSequenceOrArrayType(seq.pos(), seqType);
	    Type owntype = seqType.tag == ARRAY ?
		types.elemtype(seqType) :
		types.elementType(seqType);
	    result = check(tree, owntype, VAR, pkind, pt, pSequenceness);
	} else {
	    Name get = names.fromString("get");
	    F3Expression getTree = f3make.at(tree.pos).Select(seq, get, true);
	    F3Expression invokeTree = f3make.at(tree.pos).Apply(List.<F3Expression>nil(),
								getTree,
								List.of(tree.getIndex()));
	    tree.get = invokeTree;
	    result = attribExpr(invokeTree, env);
	}
    }

    //@Override
    public void visitSequenceInsert(F3SequenceInsert tree) {
        F3Expression seq = tree.getSequence();
        Type seqType = attribTree(seq, env, VAR, Type.noType, Sequenceness.REQUIRED);
        attribExpr(tree.getElement(), env, seqType);
        if (tree.getPosition() != null) {
            attribExpr(tree.getPosition(), env, syms.f3_IntegerType);
        }
        result = syms.voidType;
        tree.type = result;
    }

    //@Override
    public void visitSequenceDelete(F3SequenceDelete tree) {
        F3Expression seq = tree.getSequence();
        if (tree.getElement() == null) {
            Sequenceness sequenceness = (seq instanceof F3SequenceIndexed) ?
                Sequenceness.DISALLOWED :
                Sequenceness.REQUIRED;
            int pkind = (seq instanceof F3SequenceIndexed) || (seq instanceof F3SequenceSlice) ?
                VAL : VAR;
            attribTree(seq, env, pkind, Type.noType, sequenceness);
        } else {
            Type seqType = attribTree(seq, env, VAR, Type.noType, Sequenceness.REQUIRED);
            attribExpr(tree.getElement(), env,
                    chk.checkSequenceElementType(seq.pos(), seqType));
        }
        result = syms.voidType;
        tree.type = result;
    }

    //@Override
    public void visitInvalidate(F3Invalidate tree) {
        //the target expr should be a variable
        attribTree(tree.getVariable(), env, VAR, Type.noType);
//        Symbol varSym = F3TreeInfo.symbol(tree.getVariable());
//        if (varSym != null &&
//                varSym.kind == VAR) {
//            //non-static/local vars can be overridden with a bound init - other cases
//            //are handled at runtime (exception)
//            if ((varSym.flags_field & F3Flags.VARUSE_BOUND_DEFINITION) == 0 &&
//                    (varSym.isStatic() || varSym.owner.kind != TYP))
//                log.error(tree.getVariable().pos(), MsgSym.MESSAGE_CANNOT_INVALIDATE_UNBOUND_VAR, varSym);
//        }
        result = tree.type = syms.voidType;
    }

    //@Override
    public void visitStringExpression(F3StringExpression tree) {
        List<F3Expression> parts = tree.getParts();
        attribExpr(parts.head, env, syms.f3_StringType);
        parts = parts.tail;
        while (parts.nonEmpty()) {
            // First the format specifier:
            attribExpr(parts.head, env, syms.f3_StringType);
            parts = parts.tail;
            // Next the enclosed expression:
            chk.checkNonVoid(parts.head.pos(), attribExpr(parts.head, env, Type.noType));
            parts = parts.tail;
            // Next the following string literal part:
            attribExpr(parts.head, env, syms.f3_StringType);
            parts = parts.tail;
        }
        result = check(tree, syms.f3_StringType, VAL, pkind, pt, pSequenceness);
    }

    //@Override
    public void visitObjectLiteralPart(F3ObjectLiteralPart that) {
        
        // Note that this method can be reached legitimately if visitErroneous is
        // called and the error nodes contain an objectLiteralPart. Hence this
        // just sets the result to errType.
        //
        result = syms.errType;
    }

    //@Override
    public void visitTypeAny(F3TypeAny tree) {
        //assert false : "MUST IMPLEMENT";
	if (tree instanceof F3Type.RawSequenceType) {
	    tree.type = result = syms.f3_SequenceTypeErasure;
	} else if (tree instanceof F3Type.TypeApply) {
	    F3Type.TypeApply app = (F3Type.TypeApply)tree;
            Type t = attribTree(app.className,
				env,
				TYP,
				Type.noType);
	    List<Type> targs = makeTypeVars(app.args, env.info.scope.owner, env);
	    TypeCons cons = (TypeCons)t;
	    //System.err.println("t="+t.getClass() + ": "+t);
	    TypeCons ap = new TypeCons(cons.tsym.name,
				       cons.tsym,
				       cons.bound,
				       targs);
	    if (cons.bound == null) {
		System.err.println("bound is null: "+ cons);
	    }
	    ap.bound = cons.bound;
	    ap.ctor = cons;
	    tree.type = result = ap;
	} else if (tree instanceof F3TypeAlias) {
	    F3TypeAlias ta = (F3TypeAlias)tree;
            F3Env<F3AttrContext> localEnv = env;
	    List<Type> targs = null;
	    if (ta.typeArgs != null) {
		localEnv = newLocalEnv(tree);
		targs = makeTypeVars(ta.typeArgs, env.info.scope.owner, localEnv);
	    }
            Type t = attribTree(ta.type,
				localEnv,
				TYP,
				Type.noType);
	    //System.err.println("t="+t.getClass()+ ": "+ t);
	    //System.err.println("targs="+targs);
	    if (ta.typeArgs != null) {
		if (t instanceof FunctionType) {
		    ((FunctionType)t).typeArgs = targs;
		    t = ((FunctionType)t).asMethodOrForAll();
		} else {
		    int i = targs.indexOf(t);
		    if (i >= 0) {
			if (false && targs.size() == 1) {
			    t = types.idType(t);
			} else {
			    t = new ConstI(i, (TypeVar)t);
			}
		    } else {
			t = new ForAll(targs, t);
		    }
		}
	    }
	    //System.err.println("t="+t.getClass());
	    //System.err.println("t'="+types.toF3String(t));
	    ta.tsym.type = t;
	    result = t;
	    tree.type = result;
	    return;
	} else if (tree instanceof F3Type.TheType) {
	    F3Type.TheType theType = (F3Type.TheType)tree;
	    Type t = attribTree(theType.theType, env, TYP, Type.noType);
	    System.err.println("\"the\" type is "+t);
	    ///System.err.println("env="+env);
	    //	    System.err.println("searching for the "+t+ " in env1="+env);
	    Symbol sym = findThe(env, tree, t);		
	    if (env.enclVar != null && 
		sym == env.enclVar.sym) {
		sym = null;
	    }
	    //if (sym != null) System.err.println("found: "+sym.getClass());
	    F3FunctionDefinition fun = env.enclFunction;
	    if (sym == null) {
		if (fun != null && !fun.sym.name.equals(syms.runMethodName)) {
		    F3VarSymbol idSym1 = 
			new F3VarSymbol(types, 
					names, 
					F3Flags.IMPLICIT_PARAMETER | Flags.PARAMETER,
					names.fromString("the$"+fun.implicitArgs.size()),
					t, 
					env.enclFunction.sym);
		    //		    System.err.println("not found: creating :"+t);
		    theType.resolvedSymbol = idSym1;
		    env.info.scope.enter(idSym1);
		    fun.implicitArgs = fun.implicitArgs.append(idSym1);
		} else if (env.enclVar != null) {
		    System.err.println("unresolved implicit value: "+ tree);
		    F3VarSymbol idSym1 = 
			new F3VarSymbol(types, 
					names, 
					F3Flags.IMPLICIT_PARAMETER | Flags.PARAMETER,
					names.fromString("the$"+env.implicitArgs.size()),
					t, 
					env.enclVar.sym);
		    //System.err.println("not found: creating :"+t);
		    //theType.resolvedSymbol = idSym1;
		    env.info.scope.enter(idSym1);
		    env.implicitArgs = env.implicitArgs.append(idSym1);
		}
	    } else {
		System.err.println("found: "+sym + ": "+sym.type);
		theType.resolvedSymbol = sym;
		theType.accessExpr = accessThe(sym, t, env);

	    }
	    result = t;
	    tree.type = result;
	} else {
	    System.err.println("unhandled case any: "+ tree);
	}
    }

    //@Override
    public void visitTypeClass(F3TypeClass tree) {
        F3Expression classNameExpr = ((F3TypeClass) tree).getClassName();
        Type type = attribType(classNameExpr, env);
        Cardinality cardinality = tree.getCardinality();
        if (cardinality != Cardinality.SINGLETON &&
                type == syms.voidType) {
            log.error(tree, MsgSym.MESSAGE_F3_VOID_SEQUENCE_NOT_ALLOWED);
            cardinality = Cardinality.SINGLETON;
        }
        type = sequenceType(type, cardinality);
	BoundKind bk = tree.boundKind;
	Type bound = null;
	if (tree.upperBound != null) {
	    bound = attribType(tree.upperBound, env);
	    type = types.unexpandWildcard(type);
	    if (type instanceof TypeVarDefn) {
		TypeVar tv = ((TypeVarDefn)type).base;
		type = tv;
	    }
	    if (types.isSameType(type, bound)) {
		tree.type = type;
		result = type;
		return;
	    }
	    if (type instanceof TypeVar) {
		TypeVar tv = (TypeVar)type;
		type =new WildcardType(new TypeVar(tv.tsym, syms.botType, bound),
				       BoundKind.SUPER, syms.boundClass);
		tree.type = type;
		result = type;
		return;
	    }
	}
	if (bk != null && bk != BoundKind.UNBOUND) {
	    if (type instanceof WildcardType) {
		type = ((WildcardType)type).type;
	    }
	    type = new WildcardType(types.boxedTypeOrType(type),
				    bk,
				    syms.boundClass);
	}
	//System.err.println("result="+type);
        tree.type = type;
        result = type;
    }

    //@Override
    public void visitTypeVar(F3TypeVar tree) {
	if (tree instanceof F3TypeThis) {
	    F3TypeThis t = (F3TypeThis)tree;
	    Type ct = env.getEnclosingClassType();
	    Type tc0 = types.asSuper(ct, syms.f3_TypeCons[0].tsym);
	    if (tc0 != null) {
		result = tc0.getTypeArguments().get(0);
	    } else {
		result = ct;
	    }
	    tree.type = result;
	    return;
	}
        F3Expression classNameExpr = ((F3TypeVar) tree).getClassName();
        Type type = attribType(classNameExpr, env);

        Cardinality cardinality = tree.getCardinality();
        if (cardinality != Cardinality.SINGLETON &&
                type == syms.voidType) {
            log.error(tree, MsgSym.MESSAGE_F3_VOID_SEQUENCE_NOT_ALLOWED);
            cardinality = Cardinality.SINGLETON;
        }
        type = sequenceType(type, cardinality);
	System.err.println("visit type var: "+ tree + ": "+type);
        tree.type = type;
        result = type;
    }

    boolean isWildcard(Type t) {
	if (t instanceof WildcardType) {
	    return true;
	}
	/*
	if (t instanceof TypeVar) {
	    TypeVar tv = (TypeVar)t;
	    if (isWildcard(tv.lower)) {
		return true;
	    }
	}
	*/
	/*
	if (types.isTypeCons(t)) {
	    return true;
	}
	*/
	return false;
    }
    //@Override
    public void visitTypeFunctional(F3TypeFunctional tree) {

        List<Type> typeArgTypes = null;
	if (tree.typeArgs != null) {
	    if (tree.typeArgTypes == null) {
		tree.typeArgTypes = typeArgTypes = makeTypeVars(tree.typeArgs, env.info.scope.owner);
	    } else {
		typeArgTypes = tree.typeArgTypes;
	    }
	    for (List l = typeArgTypes; l != null; l = l.tail) {
		if (l.head instanceof TypeVar) {
		    TypeVar tv = (TypeVar)l.head;
		    tv.lower = 
			new WildcardType(Type.noType,
					 BoundKind.UNBOUND,
					 syms.boundClass,
					 tv);
		    
		    }
	    }
	}
        Type restype = attribType(tree.restype, env);
	//System.err.println("restype: "+ tree);
	if (restype == null) {
	    System.err.println("restype is null: "+ tree);
	    restype = syms.unknownType;
	}
        if (restype == syms.unknownType)
            restype = syms.voidType;
        Type rtype = isWildcard(restype) ? restype : restype == syms.voidType ? syms.f3_java_lang_VoidType
                : new WildcardType(types.boxedTypeOrType(restype), BoundKind.EXTENDS, syms.boundClass);
        ListBuffer<Type> typarams = new ListBuffer<Type>();
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        typarams.append(rtype);
        int nargs = 0;
        for (F3Type param : (List<F3Type>)tree.params) {
            Type argtype = attribType(param, env);
            if (argtype == syms.f3_UnspecifiedType)
                argtype = syms.objectType;
            argtypes.append(argtype);
            Type ptype = types.boxedTypeOrType(argtype);
            //Type ptype = argtype;
	    if (!isWildcard(ptype)) {
		ptype = new WildcardType(ptype, BoundKind.SUPER, syms.boundClass);
	    }
            typarams.append(ptype);
            nargs++;
        }
        MethodType mtype = new MethodType(argtypes.toList(), restype, 
					  List.<Type>nil(), syms.methodClass);
        if (nargs > F3Symtab.MAX_FIXED_PARAM_LENGTH) {
            log.error(tree, MsgSym.MESSAGE_TOO_MANY_PARAMETERS);
            tree.type = result = syms.objectType;
            return;
        }
        FunctionType ftype = syms.makeFunctionType(typarams.toList(), mtype);
	if (typeArgTypes != null) {
	    ftype.typeArgs = typeArgTypes;
	}
        Type type = sequenceType(ftype, tree.getCardinality());
        tree.type = type;
        result = type;
    }

    //@Override
    public void visitTypeArray(F3TypeArray tree) {
        //TODO: Do the right thing here
        Type etype = attribType(tree.getElementType(), env);
        Type type = new ArrayType(etype, syms.arrayClass);
        tree.type = type;
        result = type;
    }

    //@Override
    public void visitTypeUnknown(F3TypeUnknown tree) {
        result = tree.type = syms.f3_UnspecifiedType;
    }

    Type sequenceType(Type elemType, Cardinality cardinality) {
        return cardinality == cardinality.ANY
	    ? types.sequenceType(elemType)
	    : elemType;
    }

        /** Determine type of identifier or select expression and check that
         *  (1) the referenced symbol is not deprecated
         *  (2) the symbol's type is safe (@see checkSafe)
         *  (3) if symbol is a variable, check that its type and kind are
         *      compatible with the prototype and protokind.
         *  (4) if symbol is an instance field of a raw type,
         *      which is being assigned to, issue an unchecked warning if its
         *      type changes under erasure.
         *  (5) if symbol is an instance method of a raw type, issue an
         *      unchecked warning if its argument types change under erasure.
         *  If checks succeed:
         *    If symbol is a constant, return its constant type
         *    else if symbol is a method, return its result type
         *    otherwise return its type.
         *  Otherwise return errType.
         *
         *  @param tree       The syntax tree representing the identifier
         *  @param site       If this is a select, the type of the selected
         *                    expression, otherwise the type of the current class.
         *  @param sym        The symbol representing the identifier.
         *  @param env        The current environment.
         *  @param pkind      The set of expected kinds.
         *  @param pt         The expected type.
         */
        Type checkId(F3Tree tree,
                     Type site,
                     Symbol sym,
                     F3Env<F3AttrContext> env,
		     List<Type> typeargtypes,
                     int pkind,
                     Type pt,
                     Sequenceness pSequenceness,
                     boolean useVarargs) {
	    //System.err.println("checkId: "+ sym);
            if (pt.isErroneous()) return syms.errType;
	    //System.err.println("checkId: "+tree);
	    //System.err.println("pkind="+pkind);
	    //System.err.println("pt="+pt);
            Type owntype; // The computed type of this identifier occurrence.
	    int kind = sym.kind;
	    if (sym.type instanceof MethodType ||
		sym.type instanceof ForAll) {
		kind = MTH;
	    }
            switch (kind) {
            case TYP:
                // For types, the computed type equals the symbol's type,
                // except for two situations:
                owntype = sym.type;
		if (typeargtypes.nonEmpty()) {
		    if (owntype.getEnclosingType() != null) {
			owntype = newClassType(owntype.getEnclosingType(),
					       typeargtypes, owntype.tsym);
		    } else {
			//System.err.println("owntype.enclosing=null: "+sym);
		    }
		}
		//System.err.println("typ: "+ sym+": "+owntype);
                if (owntype.tag == CLASS) {
                    Type ownOuter = owntype.getEnclosingType();

                    // (a) If the symbol's type is parameterized, erase it
                    // because no type parameters were given.
                    // We recover generic outer type later in visitTypeApply.
                    if (owntype.tsym.type.getTypeArguments().nonEmpty()) {
                        //owntype = types.erasure(owntype); // hack!!!
                    }

                    // (b) If the symbol's type is an inner class, then
                    // we have to interpret its outer type as a superclass
                    // of the site type. Example:
                    //
                    // class Tree<A> { class Visitor { ... } }
                    // class PointTree extends Tree<Point> { ... }
                    // ...PointTree.Visitor...
                    //
                    // Then the type of the last expression above is
                    // Tree<Point>.Visitor.
                    else if (ownOuter.tag == CLASS && site != ownOuter) {
                        Type normOuter = site;
                        if (normOuter.tag == CLASS)
                            normOuter = types.asEnclosingSuper(site, ownOuter.tsym);
                        if (normOuter == null) // perhaps from an import
                            normOuter = ownOuter; //types.erasure(ownOuter); hack!!
                        if (normOuter != ownOuter)
                            owntype = newClassType(
						    normOuter, owntype.getTypeArguments(), owntype.tsym);
                    }
                }
                break;
            case VAR:
		//System.err.println("VAR");
                F3VarSymbol v = (F3VarSymbol)sym;
                // Test (4): if symbol is an instance field of a raw type,
                // which is being assigned to, issue an unchecked warning if
                // its type changes under erasure.
                if (allowGenerics &&
                    pkind == VAR &&
                    v.isMember() &&
                    (v.flags() & STATIC) == 0 &&
                    (site.tag == CLASS || site.tag == TYPEVAR)) {
                    Type s = types.asOuterSuper(site, v.owner);
                    if (s != null &&
                        s.isRaw() &&
                        !types.isSameType(v.type, v.erasure(types))) {
                        chk.warnUnchecked(tree.pos(),
                                          MsgSym.MESSAGE_UNCHECKED_ASSIGN_TO_VAR,
                                          v, s);
                    }
                }
                // The computed type of a variable is the type of the
                // variable symbol, taken as a member of the site type.
                owntype = (sym.owner.kind == TYP &&
                           sym.name != names._this && sym.name != names._super)
                    ? types.memberType(site, sym)
                    : sym.type;
		//System.err.println("var: "+ sym+": "+owntype);
		if (owntype instanceof FunctionType) { // hack
		    owntype = ((FunctionType)owntype).asMethodOrForAll();
		    //System.err.println("owntype is now: "+ owntype);
		}
                if (((pt instanceof MethodType) || (pt instanceof ForAll))) {
		    try {
			owntype = types.memberType(site, sym);
		    } catch (NullPointerException exc) {
			System.err.println("owntype: "+owntype);
			System.err.println("site: "+ site);
			System.err.println("sym="+sym.getClass()+": "+sym);
			System.err.println("pt="+pt);
		    }
                    F3FunctionInvocation app = (F3FunctionInvocation)env.tree;
		    Type siteType = site;
		    if (sym instanceof F3Resolve.InstanceMethodSymbol) {
			sym = ((F3Resolve.InstanceMethodSymbol)sym).generic;
		    }
		    if (siteType instanceof MethodType) {
			siteType = syms.makeFunctionType((MethodType)siteType);
		    }
                    Type inst = checkMethod(siteType, sym, env, app.args,
					    pt.getParameterTypes(), typeargtypes,
					    env.info.varArgs);
		    if (inst != null) { // hack
			owntype = inst;
		    } else {
			//owntype = pt;
			System.err.println("inst failed: using : "+owntype +": "+pt.getParameterTypes() +": "+app.args);
		    }
		    if (owntype instanceof MethodType) {
			try {
			    owntype = syms.makeFunctionType((MethodType)owntype);
			} catch (Exception exc) { // hack
			    // could happen if too many args
			}
		    }
		} else {
		    if (owntype instanceof ForAll) {
			if (typeargtypes.nonEmpty()) {
			    owntype = types.subst(owntype,
						  owntype.getTypeArguments(),
						  typeargtypes);
			}
		    } else if (env.info.tvars.nonEmpty()) {
			/*
			  Type owntype1 = newForAll(env.info.tvars, owntype);
			  for (List<Type> l = env.info.tvars; l.nonEmpty(); l = l.tail) {
			  if (!owntype.contains(l.head)) {
			  //log.error(tree.pos(), MsgSym.MESSAGE_UNDETERMINDED_TYPE, owntype1);
			  //owntype1 = syms.errType;
			  }
			  }
			*/
		    }
		}

                // If the variable is a constant, record constant value in
                // computed type.
                //if (v.getConstValue() != null && isStaticReference(tree))
                //    owntype = owntype.constType(v.getConstValue());

                if (pkind == VAL) {
                    //owntype = capture(owntype); // capture "names as expressions"
                }
                break;
            case MTH: {
		//xSystem.err.println("MTH");
                owntype = types.memberType(site,
					   sym);
		//System.err.println("meth: "+ sym+": "+owntype);
                // This is probably wrong now that we have function expressions.
                // Instead, we should checkMethod in visitFunctionInvocation.
                // In that case we should also handle FunctionType. FIXME.
                if ((pt instanceof MethodType) || (pt instanceof ForAll)) {
		    Type siteType = site;
		    if (env.tree instanceof F3FunctionInvocation) {
			F3FunctionInvocation app = (F3FunctionInvocation)env.tree;

			if (siteType instanceof MethodType) {
			    siteType = syms.makeFunctionType((MethodType)siteType);
			}
			Type inst = checkMethod(siteType, sym, env, app.args,
						pt.getParameterTypes(), typeargtypes,
						env.info.varArgs);
			if (inst != null) { // hack
			    owntype = inst;
			} else {
			    //owntype = pt;
			    System.err.println("inst failed: using : "+owntype +": "+pt.getParameterTypes() +": "+app.args);
			    //Thread.currentThread().dumpStack();
			}
		    } else {
			if (typeargtypes.nonEmpty()) {
			    owntype = types.subst(owntype.asMethodType(), 
						  owntype.getTypeArguments(),
						  typeargtypes);
			    System.err.println("subst="+owntype);
			}
		    }
		    if (false && owntype instanceof MethodType) {
			try {
			    owntype = syms.makeFunctionType((MethodType)owntype);
			} catch (Exception exc) { // hack
			    // could happen if too many args
			}
		    }
                } else {
		    if (typeargtypes.nonEmpty()) {
			owntype = types.subst(((ForAll)owntype).qtype,
					      owntype.getTypeArguments(),
					      typeargtypes);

			/*
			owntype = checkMethod(site, sym, env, owntype.getTypeArguments(),
					      pt.getParameterTypes(), typeargtypes,
					      env.info.varArgs);
			*/
		    }
		}
		//System.err.println("owntype="+owntype);
                break;
            }
            case PCK: case ERR:
                owntype = sym.type;
                break;
            default:
                throw new AssertionError("unexpected kind: " + sym.kind +
                                         " in tree " + tree);
            }

            // Test (1): emit a `deprecation' warning if symbol is deprecated.
            // (for constructors, the error was given when the constructor was
            // resolved)
            if (sym.name != names.init &&
                (sym.flags() & DEPRECATED) != 0 &&
                (env.info.scope.owner.flags() & DEPRECATED) == 0 &&
                sym.outermostClass() != env.info.scope.owner.outermostClass())
                chk.warnDeprecated(tree.pos(), sym);

            if (warnOnUsePackages != null && ElementKind.PACKAGE.equals(sym.getKind())) {
                for (String pkg : warnOnUsePackages) {
                    if (sym.toString().startsWith(pkg)) {
                        chk.warnWarnOnUsePackage(tree.pos(), sym);
                    }
                }
            }

            if ((sym.flags() & PROPRIETARY) != 0)
                log.strictWarning(tree.pos(), MsgSym.MESSAGE_SUN_PROPRIETARY, sym);

            // Test (3): if symbol is a variable, check that its type and
            // kind are compatible with the prototype and protokind.
	    //System.err.println("check: "+ owntype+": pt="+pt);
            return check(tree, owntype, sym.kind, pkind, pt, pSequenceness);
        }

        public boolean isClassOrFuncDef(F3Env<F3AttrContext> env, boolean discardRun) {
            return isFunctionDef(env, discardRun) ||
                   env.tree.getF3Tag() == F3Tag.FUNCTIONEXPRESSION ||                   
                   env.tree.getF3Tag() == F3Tag.CLASS_DEF ||
                   env.tree.getF3Tag() == F3Tag.ON_REPLACE ||
                   env.tree.getF3Tag() == F3Tag.KEYFRAME_LITERAL ||
                   env.tree.getF3Tag() == F3Tag.INIT_DEF ||
                   env.tree.getF3Tag() == F3Tag.POSTINIT_DEF;
        }
        //where
        private boolean isFunctionDef(F3Env<F3AttrContext> env, boolean discardRun) {
            return env.tree.getF3Tag() == F3Tag.FUNCTION_DEF && (!discardRun ||
                    !(((F3FunctionDefinition)env.tree).name.equals(syms.runMethodName)));
        }
        
    Warner noteWarner = new Warner();

    /**
     * Check that method arguments conform to its instantation.
     **/
    public Type checkMethod(Type site,
                            Symbol sym,
                            F3Env<F3AttrContext> env,
                            final List<F3Expression> argtrees,
                            List<Type> argtypes,
                            List<Type> typeargtypes,
                            boolean useVarargs) {
	if (sym instanceof F3Resolve.InstanceMethodSymbol) {
	    sym = ((F3Resolve.InstanceMethodSymbol)sym).generic;
	}
	if (argtypes != null) {
	    List<Type> list = argtypes;
	    while (list.nonEmpty()) {
		Type arg = list.head;
		if (arg instanceof MethodType) {
		    arg = syms.makeFunctionType((MethodType)arg);
		} else {
		    //arg = types.boxedTypeOrType(arg);
		}
		list.head = arg;
		list = list.tail;
	    }
	}
	if (typeargtypes != null) {
	    List<Type> list = typeargtypes;
	    while (list.nonEmpty()) {
		Type arg = list.head;
		if (arg instanceof MethodType) {
		    arg = syms.makeFunctionType((MethodType)arg);
		} else {
		    //arg = types.boxedTypeOrType(arg);
		}
		list.head = arg;
		list = list.tail;
	    }
	}
        // Test (5): if symbol is an instance method of a raw type, issue
        // an unchecked warning if its argument types change under erasure.
        if (allowGenerics &&
            (sym.flags() & STATIC) == 0 &&
            (site.tag == CLASS || site.tag == TYPEVAR)) {
            Type s = types.asOuterSuper(site, sym.owner);
            if (s != null && s.isRaw() &&
                !types.isSameTypes(sym.type.getParameterTypes(),
                                   sym.erasure(types).getParameterTypes())) {
                chk.warnUnchecked(env.tree.pos(),
                                  MsgSym.MESSAGE_UNCHECKED_CALL_MBR_OF_RAW_TYPE,
                                  sym, s);
            }
        }

        // Compute the identifier's instantiated type.
        // For methods, we need to compute the instance type by
        // Resolve.instantiate from the symbol's type as well as
        // any type arguments and value arguments.
        noteWarner.warned = false;
        Type owntype = rs.instantiate(env,
				      site,
				      sym,
				      argtypes,
				      typeargtypes,
				      true,
				      useVarargs,
				      noteWarner);
        boolean warned = noteWarner.warned;

        // If this fails, something went wrong; we should not have
        // found the identifier in the first place.
        if (owntype == null) {
            if (!pt.isErroneous()) { 
		if (argtypes != null) {
		    List<Type> list = argtypes;
		    while (list.nonEmpty()) {
			Type arg = list.head;
			if (arg instanceof MethodType) {
			    arg = syms.makeFunctionType((MethodType)arg);
			} else {
			    arg = types.boxedTypeOrType(arg);
			}
			list.head = arg;
			list = list.tail;
		    }
		}
		owntype = rs.instantiate(env,
					 site,
					 sym,
					 argtypes,
					 typeargtypes,
					 true,
					 useVarargs,
					 noteWarner);
		
		if (true) {
		    return sym.type;
		}
		if (owntype == null) { 
		    log.error(env.tree.pos(),
			      MsgSym.MESSAGE_INTERNAL_ERROR_CANNOT_INSTANTIATE,
			      sym, site,
			      Type.toString(pt.getParameterTypes()));
		}
		return owntype; // hack
	    } else {
		owntype = syms.errType;
	    }
        } else {
            // System.out.println("call   : " + env.tree);
            // System.out.println("method : " + owntype);
            // System.out.println("actuals: " + argtypes);
            List<Type> formals = owntype.getParameterTypes();
	    if (formals == null) {
		System.err.println("bad type: "+ owntype);
	    }
            Type last = useVarargs ? formals.last() : null;
            if (sym.name==names.init &&
                sym.owner == syms.enumSym)
                formals = formals.tail.tail;
	    List<F3Expression> args = argtrees;
	    while (formals != null && formals.head != last) {
		F3Tree arg = args.head;
		Warner warn = chk.convertWarner(arg.pos(), arg.type, formals.head);
		assertConvertible(arg, arg.type, formals.head, warn);
		warned |= warn.warned;
		args = args.tail;
		formals = formals.tail;
	    }
            if (useVarargs) {
                Type varArg = types.elemtype(last);
                while (args.tail != null) {
                    F3Tree arg = args.head;
                    Warner warn = chk.convertWarner(arg.pos(), arg.type, varArg);
                    assertConvertible(arg, arg.type, varArg, warn);
                    warned |= warn.warned;
                    args = args.tail;
                }
            } else if ((sym.flags() & VARARGS) != 0 && allowVarargs) {
                // non-varargs call to varargs method
                Type varParam = owntype.getParameterTypes().last();
                Type lastArg = argtypes.last();
                if (types.isSubtypeUnchecked(lastArg, types.elemtype(varParam)) &&
                    !types.isSameType(types.erasure(varParam), types.erasure(lastArg)))
                    log.warning(argtrees.last().pos(), MsgSym.MESSAGE_INEXACT_NON_VARARGS_CALL,
                                types.elemtype(varParam),
                                varParam);
            }

            if (warned && sym.type.tag == FORALL) {
                String typeargs = "";
                if (typeargtypes != null && typeargtypes.nonEmpty()) {
                    typeargs = " of (" + Type.toString(typeargtypes) + ")";
                }
                chk.warnUnchecked(env.tree.pos(),
                                  MsgSym.MESSAGE_UNCHECKED_METH_INVOCATION_APPLIED,
                                  sym,
                                  sym.location(),
                                  typeargs,
                                  Type.toString(argtypes));
		if (owntype.getReturnType() == null) {
		    System.err.println("null ret : "+ owntype);
		}
                owntype = new MethodType(owntype.getParameterTypes(),
                                         //types.erasure(owntype.getReturnType()),
					 owntype.getReturnType(),
                                         owntype.getThrownTypes(),
                                         syms.methodClass);

            }
            if (useVarargs) {
                F3Tree tree = env.tree;
                Type argtype = owntype.getParameterTypes().last();
                if (!types.isReifiable(argtype))
                    chk.warnUnchecked(env.tree.pos(),
                                      MsgSym.MESSAGE_UNCHECKED_GENERIC_ARRAY_CREATION,
                                      argtype);
                Type elemtype = types.elemtype(argtype);
                switch (tree.getF3Tag()) {
                case APPLY:
                    ((F3FunctionInvocation) tree).varargsElement = elemtype;
                    break;
                default:
                    throw new AssertionError(""+tree);
                }
            }
        }
        return owntype;
    }

    private void assertConvertible(F3Tree tree, Type actual, Type formal, Warner warn) {

	if (actual == null || formal == null) { // hack!!
	    return; 
	}
        if (types.isConvertible(actual, formal, warn))
            return;

        if (formal.isCompound()
            && types.isSubtype(actual, types.supertype(formal))
            && types.isSubtypeUnchecked(actual, types.interfaces(formal), warn))
            return;

        if (false) {
            // TODO: make assertConvertible work
            chk.typeError(tree.pos(), JCDiagnostic.fragment(MsgSym.MESSAGE_INCOMPATIBLE_TYPES), actual, formal);
            throw new AssertionError("Tree: " + tree
                                     + " actual:" + actual
                                     + " formal: " + formal);
        }
    }

    //@Override
    public void visitImport(F3Import tree) {
        // nothing to do
    }

    /** Finish the attribution of a class. */
    public void attribClassBody(F3Env<F3AttrContext> env, ClassSymbol c) {
        F3ClassDeclaration tree = (F3ClassDeclaration)env.tree;
        assert c == tree.sym;

        // Validate annotations
        //chk.validateAnnotations(tree.mods.annotations, c);

        // Validate type parameters, supertype and interfaces.
        //attribBounds(tree.typeArgs);
        //chk.validateTypeParams(tree.getEmptyTypeParameters());
        chk.validate(tree.getSupertypes());

        // Check that class does not import the same parameterized interface
        // with two different argument lists.
        chk.checkClassBounds(tree.pos(), c.type);

        tree.type = c.type;

        // Check that a generic class doesn't extend Throwable
        if (!c.type.allparams().isEmpty() && types.isSubtype(c.type, syms.throwableType))
            log.error(tree.getExtending().head.pos(), MsgSym.MESSAGE_GENERIC_THROWABLE);

        for (List<F3Tree> l = tree.getMembers(); l.nonEmpty(); l = l.tail) {
            // Attribute declaration
            attribDecl(l.head, env);
            // Check that declarations in inner classes are not static (JLS 8.1.2)
            // Make an exception for static constants.
            // F3 allows that.
//            if (c.owner.kind != PCK &&
//                ((c.flags() & STATIC) == 0 || c.name == names.empty) &&
//                (F3TreeInfo.flags(l.head) & (STATIC | INTERFACE)) != 0) {
//                Symbol sym = null;
//                if (l.head.getF3Tag() == F3Tag.VARDEF) sym = ((JCVariableDecl) l.head).sym;
//                if (sym == null ||
//                    sym.kind != VAR ||
//                    ((F3VarSymbol) sym).getConstValue() == null)
//                    log.error(l.head.pos(), "icls.cant.have.static.decl");
//            }
        }

        // If this is a non-abstract class, check that it has no abstract
        // methods or unimplemented methods of an implemented interface.
        if ((c.flags() & (ABSTRACT | INTERFACE | F3Flags.MIXIN)) == 0) {
            if (!relax)
                chk.checkAllDefined(tree.pos(), c);
        }
            
        // Make sure there is only one real base class.  Others may be mixins.
        chk.checkOneBaseClass(tree);

        // If the class is a mixin 
        if ((c.flags() & (F3Flags.MIXIN)) != 0) {
            // Check that the mixin is only a pure mixin class.
            chk.checkPureMixinClass(tree.pos(), c);
            // Check that only it only extends mixins and interfaces.
            chk.checkOnlyMixinsAndInterfaces(tree);
        } else {
            // Check to make sure that mixins don't cause any conflicts.
            chk.checkMixinConflicts(tree);
        }

         
        // Check that all extended classes and interfaces
        // are compatible (i.e. no two define methods with same arguments
        // yet different return types).  (JLS 8.4.6.3)
        chk.checkCompatibleSupertypes(tree.pos(), c.type);

        // Check that all methods which implement some
        // method conform to the method they implement.
        chk.checkImplementations(tree);

        if (tree.isScriptClass) {
            chk.checkForwardReferences(tree);
        }

        Scope enclScope = F3Enter.enterScope(env);
        for (List<F3Tree> l = tree.getMembers(); l.nonEmpty(); l = l.tail) {
            if (l.head instanceof F3FunctionDefinition)
                if (false) chk.checkUnique(l.head.pos(), ((F3FunctionDefinition) l.head).sym, enclScope); // Fix me !!!!!
        }

        // Check for proper use of serialVersionUID
        if (env.info.lint.isEnabled(Lint.LintCategory.SERIAL) &&
            isSerializable(c) &&
            (c.flags() & Flags.ENUM) == 0 &&
            (c.flags() & ABSTRACT | F3Flags.MIXIN) == 0) {
            checkSerialVersionUID(tree, c);
        }
    }
        // where
        /** check if a class is a subtype of Serializable, if that is available. */
        private boolean isSerializable(ClassSymbol c) {
            try {
                syms.serializableType.complete();
            }
            catch (CompletionFailure e) {
                return false;
            }
            return types.isSubtype(c.type, syms.serializableType);
        }

        /** Check that an appropriate serialVersionUID member is defined. */
        private void checkSerialVersionUID(F3ClassDeclaration tree, ClassSymbol c) {

            // check for presence of serialVersionUID
            Scope.Entry e = c.members().lookup(names.serialVersionUID);
            while (e.scope != null && e.sym.kind != VAR) e = e.next();
            if (e.scope == null) {
                log.warning(tree.pos(), MsgSym.MESSAGE_MISSING_SVUID, c);
                return;
            }

            // check that it is static final
            F3VarSymbol svuid = (F3VarSymbol)e.sym;
            if ((svuid.flags() & (STATIC | FINAL)) !=
                (STATIC | FINAL))
                log.warning(F3TreeInfo.diagnosticPositionFor(svuid, tree), MsgSym.MESSAGE_IMPROPER_SVUID, c);

            // check that it is long
            else if (svuid.type.tag != TypeTags.LONG)
                log.warning(F3TreeInfo.diagnosticPositionFor(svuid, tree), MsgSym.MESSAGE_LONG_SVUID, c);

            // check constant
            else if (svuid.getConstValue() == null)
                log.warning(F3TreeInfo.diagnosticPositionFor(svuid, tree), MsgSym.MESSAGE_CONSTANT_SVUID, c);
        }

    private Type capture(Type type) {
	type = types.normalize(type);
        Type ctype = types.capture(type);
        if (type instanceof FunctionType)
            ctype = new FunctionType((FunctionType) type);
        return ctype;
    }

    public void clearCaches() {
        varSymToTree = null;
        methodSymToTree = null;
    }

    private void fixOverride(F3FunctionDefinition tree, MethodSymbol m, boolean fixFlags) {
        ClassSymbol origin = (ClassSymbol) m.owner;
        if ((origin.flags() & ENUM) != 0 && names.finalize.equals(m.name)) {
            if (m.overrides(syms.enumFinalFinalize, origin, types, false)) {
                log.error(tree.pos(), MsgSym.MESSAGE_ENUM_NO_FINALIZE);
                return;
            }
        }
	List<Type> supers = types.supertypesClosure(origin.type, false);

        for (Type t : supers) {

            if (t.tag == CLASS) {
                TypeSymbol c = t.tsym;
                Scope.Entry e = c.members().lookup(m.name);
                while (e.scope != null) {
                    e.sym.complete();
		    boolean overrides = types.overrides(m, e.sym, origin, false);
                    if (overrides) {
                        //if other has an uninferred return type we should report an error
                        if (e.sym.type.asMethodType().restype == syms.f3_UnspecifiedType) {
                            log.note(tree, MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_FUN_DECL, e.sym.name);
                            log.error(tree.pos(), MsgSym.MESSAGE_F3_TYPE_INFER_CYCLE_VAR_REF, e.sym.name);
                            //set a dummy return type for other so that f3c is happy
                            ((MethodType)e.sym.type).restype = syms.errType;
                            break;
                        }
                        else if (fixOverride(tree, m, (MethodSymbol) e.sym, origin, fixFlags)) {
                            break;
                        }
                    }
                    e = e.next();
                }
            }
        }
    }

    private boolean fixOverride(F3FunctionDefinition tree,
				MethodSymbol m,
				MethodSymbol other,
				ClassSymbol origin,
				boolean fixFlags) {
	Type mt = types.memberType(origin.type, m);
	Type ot = types.memberType(origin.type, other);
	if (m.type.getReturnType() == syms.f3_UnspecifiedType) {
            MethodType methodType = m.type.asMethodType();
	    methodType.restype = ot.getReturnType();
	}
	// Error if overriding result type is different
	// (or, in the case of generics mode, not a subtype) of
	// overridden result type. We have to rename any type parameters
	// before comparing types.
	List<Type> mtvars = mt.getTypeArguments();
	List<Type> otvars = ot.getTypeArguments();
	List<F3Var> vars = tree.operation.funParams;
	List<Type> newArgTypes = List.<Type>nil();
        List<Type> x = mt.getParameterTypes();
        List<Type> y = ot.getParameterTypes();
        while (x != null) {
	    if (x.head != null && y.head != null) {
		Type x1 = types.subst2(y.head, 
				       otvars,
				       mtvars,
				       true);

		if (x1 != x.head) {
		    int tc2 = types.isTypeConsType(y.head);
		    if (tc2 >= 0 || !x1.toString().equals(x.head.toString())) { // hack - but cheaper than propagating a copy ?
			//System.err.println("x1="+x1+"..."+x.head+ " / "+y.head);
			vars.head.baseType = x1;
		    }
		}
	    }
	    if (x.head != null && types.boxedTypeOrType(x.head) == y.head) {
                x.head = y.head;
		vars.head.type = x.head;
	    }
	    if (vars.head != null) {
		newArgTypes = newArgTypes.append(vars.head.type);
	    }
            x = x.tail;
            y = y.tail;
            vars = vars.tail;
	}
	Type mtres = mt.getReturnType();
	Type otres = types.subst(ot.getReturnType(), otvars, mtvars);
	{
	    int tc2 = types.isTypeConsType(ot.getReturnType());
	    if (tc2 >= 0) {
		otres = types.subst2(ot.getReturnType(),
				     otvars,
				     mtvars,
				     true);
		
		mt.asMethodType().restype = otres;
	    }
	}
        return fixOverride1(tree, m, other, mt, ot, mtres, otres, fixFlags);
    }

    boolean fixOverride1(F3FunctionDefinition tree,
                         MethodSymbol m, MethodSymbol other,
                         Type mt, Type ot,
                         Type mtres, Type otres, boolean fixFlags) {
        ClassSymbol origin = (ClassSymbol) m.owner;
	boolean resultTypesOK = mtres != syms.f3_UnspecifiedType &&
	    types.returnTypeSubstitutable(mt, ot, otres, noteWarner);
	if (!resultTypesOK) {
	    if (!source.allowCovariantReturns() &&
		m.owner != origin &&
		m.owner.isSubClass(other.owner, types)) {
		// allow limited interoperability with covariant returns
	    }
            else {
                Type setReturnType = null;
                if (mtres == syms.f3_DoubleType && otres == syms.floatType) {
                    setReturnType = syms.floatType;
                }
                else if ((mtres == syms.f3_IntegerType || mtres == syms.f3_DoubleType) && otres == syms.byteType) {
                    setReturnType = syms.byteType;
                }
                else if ((mtres == syms.f3_IntegerType || mtres == syms.f3_DoubleType) && otres == syms.charType) {
                    setReturnType = syms.charType;
                }
                else if ((mtres == syms.f3_IntegerType || mtres == syms.f3_DoubleType) && otres == syms.shortType) {
                    setReturnType = syms.shortType;
                }
                else if ((mtres == syms.f3_IntegerType || mtres == syms.f3_DoubleType) && otres == syms.longType) {
                    setReturnType = syms.longType;
                }
                else if (mtres == syms.f3_UnspecifiedType) {
                    setReturnType = otres;
                }
		if (types.boxedTypeOrType(mtres) == otres) {
		    setReturnType = otres;
		}
                if (setReturnType != null) {
                    F3Type oldType = tree.operation.getF3ReturnType();
                    tree.operation.rettype = f3make.TypeClass(f3make.Type(setReturnType), oldType.getCardinality());
		    attribTree(tree.operation.rettype, env, TYP, setReturnType);
                    if (mt instanceof MethodType) {
                        ((MethodType)mt).restype = setReturnType;
                    }

                    if (tree.type != null && tree.type instanceof MethodType) {
                        ((MethodType)tree.type).restype = setReturnType;
                    }
                }
            }
	}
        // now fix up the access modifiers
        if (fixFlags) {
            long origFlags = m.flags();
            long flags = origFlags;
            if ((flags & F3Flags.F3ExplicitAccessFlags) == 0) {
                flags |= other.flags() & (F3Flags.F3ExplicitAccessFlags | F3Flags.F3AccessFlags);
            }
            if (flags != origFlags) {
                m.flags_field = flags;
                F3Modifiers mods = tree.getModifiers();
                mods.flags = flags;
            }
        }
        return true;
    }

    public void visitTimeLiteral(F3TimeLiteral tree) {
        result = check(tree, syms.f3_DurationType, VAL, pkind, pt, pSequenceness);
    }

    public void visitLengthLiteral(F3LengthLiteral tree) {
        result = check(tree, syms.f3_LengthType, VAL, pkind, pt, pSequenceness);
    }

    public void visitAngleLiteral(F3AngleLiteral tree) {
        result = check(tree, syms.f3_AngleType, VAL, pkind, pt, pSequenceness);
    }

    public void visitColorLiteral(F3ColorLiteral tree) {
        result = check(tree, syms.f3_ColorType, VAL, pkind, pt, pSequenceness);
    }

    public void visitInterpolateValue(F3InterpolateValue tree) {
        F3Env<F3AttrContext> dupEnv = env.dup(tree);
        dupEnv.outer = env;
        F3Tag tag = F3TreeInfo.skipParens(tree.attribute).getF3Tag();
        Type instType;
        if (tag == F3Tag.IDENT || tag == F3Tag.SELECT) {
            instType = attribTree(tree.attribute, dupEnv, VAR, Type.noType);
            tree.sym = F3TreeInfo.symbol(tree.attribute);
            if (instType == null || instType == syms.f3_UnspecifiedType)
                instType = Type.noType;

            if (tag == F3Tag.SELECT) {
                F3Select select = (F3Select) tree.attribute;
                if (chk.checkBidiSelect(select, env, pt))
                    log.warning(select.getExpression().pos(),
                        MsgSym.MESSAGE_SELECT_TARGET_NOT_REEVALUATED_FOR_ANIM,
                        select.getExpression(), select.name);
            }
        }
        else {
            instType = Type.noType;
            log.error(tree.pos(), MsgSym.MESSAGE_UNEXPECTED_TYPE,
                    Resolve.kindNames(VAR), Resolve.kindName(VAL));
        }
        
        attribExpr(tree.value, dupEnv, instType);
        if (tree.interpolation != null)
            attribExpr(tree.interpolation, dupEnv);

        //TODO: this is evil
        //wrap it in a function
        /* 
         * VSGC-3133 -- previously, we filled "tree.value" with anon
         * FunctionValue. But, if this F3InterpolateValue tree is
         * attributed twice, on the second visit, tree.value would
         * be a function value and so would fail with type check.
         * Now, we use tree.funcValue. Decomposition will copy the
         * "tree.funcValue" to "tree.value".
         */
        tree.funcValue = f3make.at(tree.pos()).FunctionValue(f3make.Modifiers(0),
                f3make.at(tree.pos()).TypeUnknown(),
                List.<F3Var>nil(),
                f3make.at(tree.pos()).Block(0L, List.<F3Expression>nil(), tree.value));
        attribExpr(tree.funcValue, env);
        result = check(tree, syms.f3_KeyValueType, VAL, pkind, pt, pSequenceness);
    }

    /*
    private void checkInterpolationValue(F3InterpolateValue tree, F3Expression var) {
        final Type targetType;
        if (tree.getAttribute() != null) {
            F3Expression t = tree.getAttribute();
            F3Env<F3AttrContext> localEnv = newLocalEnv(tree);
            Name attribute = names.fromString(t.toString());
            Symbol memberSym = rs.findIdentInType(env, var.type, attribute, VAR);
            memberSym = rs.access(memberSym, t.pos(), var.type, attribute, true);
            memberSym.complete();
            t.type = memberSym.type;
            t.sym = memberSym;
            targetType = t.type;
        } else
            targetType = var.type;
        Type valueType = attribExpr(tree.getValue(), env, F3Infer.anyPoly);

        Type interpolateType = syms.errType;
        if (types.isAssignable(valueType, syms.f3_ColorType)) {
            interpolateType = syms.f3_ColorInterpolatorType;
        } else if (types.isAssignable(valueType, syms.f3_DoubleType) ||
                   types.isAssignable(valueType, syms.f3_IntegerType)) {
            interpolateType = syms.f3_NumberInterpolatorType;
        } else {
            log.error(tree.pos(), "unexpected.type", Resolve.kindNames(pkind), Resolve.kindName(pkind));
            interpolateType = syms.errType;
        }
        tree.type = interpolateType;
        result = tree.type;
    }
    */

    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        F3Env<F3AttrContext> localEnv = env.dup(tree);
        localEnv.outer = env;
        attribExpr(tree.start, localEnv);
        for (F3Expression e:tree.values) {
            Type keyValueType = attribExpr(e, localEnv);
            if (keyValueType.tag != ERROR && !types.isSameType(keyValueType, syms.f3_KeyValueType)) {
                log.error(e, MsgSym.MESSAGE_F3_KEYVALUE_REQUIRED);
            }
        }
        result = check(tree, syms.f3_KeyFrameType, VAL, pkind, pt, pSequenceness);
    }

    private F3Tree breakTree = null;

    public F3Env<F3AttrContext> attribExprToTree(F3Tree expr, F3Env<F3AttrContext> env, F3Tree tree) {
        breakTree = tree;
        JavaFileObject prev = log.useSource(null);
        try {
            attribExpr(expr, env);
        } catch (BreakAttr b) {
            return b.env;
        } finally {
            breakTree = null;
            log.useSource(prev);
        }
        return env;
    }

    public F3Env<F3AttrContext> attribStatToTree(F3Tree stmt, F3Env<F3AttrContext> env, F3Tree tree) {
        breakTree = tree;
        JavaFileObject prev = log.useSource(null);
        try {
            attribDecl(stmt, env);
        } catch (BreakAttr b) {
            return b.env;
        } finally {
            breakTree = null;
            log.useSource(prev);
        }
        return env;
    }

    private static class BreakAttr extends RuntimeException {
        static final long serialVersionUID = -6924771130405446405L;
        private F3Env<F3AttrContext> env;
        private BreakAttr(F3Env<F3AttrContext> env) {
            this.env = env;
        }
    }

    public void visitScript(F3Script tree) {

        // Do not assert that we cannot reach here as this unit can
        // be visited by virtue of visiting F3Erronous which
        // will attempt to visit each Erroneous node that it has
        // encapsualted.
        //
    }

    public void visitCatch(F3Catch tree) {
        // Do not assert that we cannot reach here as this unit can
        // be visited by virtue of visiting F3Erronous which
        // will attempt to visit each Erroneous node that it has
        // encapsualted.
        //
    }

    public void visitModifiers(F3Modifiers tree) {
        // Do not assert that we cannot reach here as this unit can
        // be visited by virtue of visiting F3Erronous which
        // will attempt to visit each Erroneous node that it has
        // encapsualted.
        //
    }

    public ForAll newForAll(List<Type> targs, Type t) {
	if (t instanceof FunctionType) {
	    t = ((FunctionType)t).asMethodOrForAll();
	}
	if (t instanceof ClassType) {
	    //throw new IllegalArgumentException(t.getClass()+ ": "+types.toF3String(t));
	}
	return new ForAll(targs, t);
    }

    ClassType newClassType(Type enclosing, List<Type> targs, TypeSymbol tsym) {
	if (enclosing == null) {
	    throw new NullPointerException("enclosing");
	}
	//System.err.println("new class type: "+ tsym + ": "+tsym.type+": "+targs);
	ClassType ct = new ClassType(enclosing, targs, tsym);
	return ct;
    }

    ClassType applyTypeArgs(ClassType ct) {
	if (ct.tsym.type == ct) return ct;
	List<Type> targs = ct.getTypeArguments();
	Type enclosing = ct.getEnclosingType();
	TypeSymbol tsym = ct.tsym;
	if (targs != null && targs.size() == tsym.type.getTypeArguments().size()) {
	    List<Type> newTargs = List.<Type>nil();
	    List<Type> baseTargs = tsym.type.getTypeArguments();
	    for (; targs.head != null; targs = targs.tail, baseTargs = baseTargs.tail) {
		Type targ = targs.head;
		Type baseTarg = baseTargs.head;
		if (baseTarg instanceof TypeVarDefn) {
		    TypeVarDefn defn = (TypeVarDefn)baseTarg;
		    if (targ instanceof TypeVar) {
			targ = new TypeVarDefn((TypeVar)targ, defn.variance);
		    }
		}
		newTargs = newTargs.append(targ);
	    }
	    //System.err.println("newTargs="+newTargs);
	    targs = newTargs;
	}
	return new ClassType(enclosing, targs, tsym);
    }

    abstract class TypeVariance extends SimpleVisitor<Void, BoundKind> {
    }


    class VarianceAnalysis extends F3TreeScanner {
	
	Map<TypeVar, BoundKind> result = new HashMap();

	void applyVariance(BoundKind bk, TypeVar t, long flags, boolean isContra) 
	{
	    if ((flags & (F3Flags.PUBLIC_INIT|F3Flags.IS_DEF|Flags.FINAL|Flags.PARAMETER)) == 0) {
		bk = BoundKind.UNBOUND;
	    } else {
		BoundKind bk1 = isContra ? BoundKind.SUPER : BoundKind.EXTENDS;
		if (bk != null && bk == bk1) {
		    bk = BoundKind.UNBOUND;
		} else {
		    bk = bk1;
		}
	    }
	    result.put(t, bk);
	}

	public void visitVar(F3Var tree) {
	    scan(tree.getF3Type());
	    scan(tree.mods);
	    scan(tree.getInitializer());
	    scan(tree.getOnReplace());
	    scan(tree.getOnInvalidate());
	    if (tree.type instanceof TypeVar) {
		TypeVar t = (TypeVar)tree.type;
		applyVariance(result.get(t), t, tree.sym.flags(), false);
	    }
	}
	
	public void visitOverrideClassVar(F3OverrideClassVar tree) {
	    scan(tree.getId());
	    scan(tree.getInitializer());
	    scan(tree.getOnReplace());
	    scan(tree.getOnInvalidate());
	    if (tree.type instanceof TypeVar) {
		TypeVar t = (TypeVar)tree.type;
		applyVariance(result.get(t), t, tree.sym.flags(), false);
	    }
	}
 
	public void visitFunctionValue(F3FunctionValue tree) {
	    for (F3Var param : tree.getParams()) {
		scan(param);
		if (param.type instanceof TypeVar) {
		    TypeVar t = (TypeVar)param.type;
		    applyVariance(result.get(t), t, Flags.PARAMETER, false);
		}
	    }
	    scan(tree.getBodyExpression());
	}
   }
}
