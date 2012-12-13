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

import com.sun.tools.mjavac.comp.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.jvm.*;
import com.sun.tools.mjavac.tree.*;

import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.code.Symbol.*;

import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.TypeTags.*;
import javax.lang.model.element.ElementVisitor;

import org.f3.tools.code.*;
import org.f3.tools.tree.*;
import org.f3.tools.util.MsgSym;

/** Helper class for name resolution, used mostly by the attribution phase.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3Resolve {
    protected static final Context.Key<F3Resolve> f3ResolveKey =
        new Context.Key<F3Resolve>();

    Name.Table names;
    Log log;
    F3Symtab syms;
    F3Check chk;
    Infer infer;
    F3ClassReader reader;
    JCDiagnostic.Factory diags;
    F3Attr attr;
    F3TreeInfo treeinfo;
    F3Types types;
    F3Defs defs;
    public final boolean boxingEnabled; // = source.allowBoxing();
    public final boolean varargsEnabled; // = source.allowVarargs();
    private final boolean debugResolve;

    public static F3Resolve instance(Context context) {
        F3Resolve instance = context.get(f3ResolveKey);
        if (instance == null)
            instance = new F3Resolve(context);
        return instance;
    }

    protected F3Resolve(Context context) {
        context.put(f3ResolveKey, this);
        syms = (F3Symtab)F3Symtab.instance(context);

        varNotFound = new
            ResolveError(ABSENT_VAR, syms.errSymbol, "variable not found");
        wrongMethod = new
            ResolveError(WRONG_MTH, syms.errSymbol, "method not found");
        wrongMethods = new
            ResolveError(WRONG_MTHS, syms.errSymbol, "wrong methods");
        methodNotFound = new
            ResolveError(ABSENT_MTH, syms.errSymbol, "method not found");
        typeNotFound = new
            ResolveError(ABSENT_TYP, syms.errSymbol, "type not found");

        names = Name.Table.instance(context);
        log = Log.instance(context);
        chk = (F3Check)F3Check.instance(context);
        infer = Infer.instance(context);
        reader = F3ClassReader.instance(context);
        treeinfo = F3TreeInfo.instance(context);
        types = F3Types.instance(context);
        Source source = Source.instance(context);
        boxingEnabled = source.allowBoxing();
        varargsEnabled = source.allowVarargs();
        diags = JCDiagnostic.Factory.instance(context);
        Options options = Options.instance(context);
        debugResolve = options.get("debugresolve") != null;
        attr = F3Attr.instance(context);
        defs = F3Defs.instance(context);
    }

    /** error symbols, which are returned when resolution fails
     */
    final ResolveError varNotFound;
    final ResolveError wrongMethod;
    final ResolveError wrongMethods;
    final ResolveError methodNotFound;
    final ResolveError typeNotFound;

/* ************************************************************************
 * Identifier resolution
 *************************************************************************/

    /** An environment is "static" if its static level is greater than
     *  the one of its outer environment
     */
// F3 change
    public
// F3 change
    static boolean isStatic(F3Env<F3AttrContext> env) {
        return env.info.staticLevel > env.outer.info.staticLevel;
    }

    /** An environment is an "initializer" if it is a constructor or
     *  an instance initializer.
     */
    static boolean isInitializer(F3Env<F3AttrContext> env) {
        Symbol owner = env.info.scope.owner;
        return owner.isConstructor() ||
            owner.owner.kind == TYP &&
            (owner.kind == VAR ||
             owner.kind == MTH && (owner.flags() & BLOCK) != 0) &&
            (owner.flags() & STATIC) == 0;
    }

    /** Is class accessible in given environment?
     *  @param env    The current environment.
     *  @param c      The class whose accessibility is checked.
     */
    public boolean isAccessible(F3Env<F3AttrContext> env, TypeSymbol c) {
        // because the SCRIPT_PRIVATE bit is too high for the switch, test it later
        switch ((short)(c.flags() & Flags.AccessFlags)) {
        case PRIVATE:
            return
                env.enclClass.sym.outermostClass() ==
                c.owner.outermostClass();
        case 0:
            if ((c.flags() & F3Flags.SCRIPT_PRIVATE) != 0) {
                // script-private
                //System.err.println("isAccessible " + c + " = " + (env.enclClass.sym.outermostClass() ==
                //        c.outermostClass()) + ", enclClass " + env.enclClass.getName());
                //System.err.println(" encl outer: " + env.enclClass.sym.outermostClass() + ", c outer: " + c.outermostClass());
                return env.enclClass.sym.outermostClass() == c.outermostClass();
            };
            // 'package' access
            return
                env.toplevel.packge == c.owner // fast special case
                ||
                env.toplevel.packge == c.packge()
                ||
                // Hack: this case is added since synthesized default constructors
                // of anonymous classes should be allowed to access
                // classes which would be inaccessible otherwise.
                env.enclFunction != null &&
                (env.enclFunction.mods.flags & ANONCONSTR) != 0;
        default: // error recovery
        case PUBLIC:
            return true;
        case PROTECTED:
            return
                env.toplevel.packge == c.owner // fast special case
                ||
                env.toplevel.packge == c.packge()
                ||
                isInnerSubClass(env.enclClass.sym, c.owner);
        }
    }

    /**
     * Looks up the variable marked as default on the given Type.
     * 
     * Will return null if the TypeSymbol is not a F3 Class or there
     * is no default.
     * 
     * @param c Type to lookup the default on.
     * @return The default variable name or null if there is none.
     */
    private Name lookupDefault(TypeSymbol c) {
        return c instanceof F3ClassSymbol ? ((F3ClassSymbol) c).getDefaultVar() : null;
    }

    /** Is given class a subclass of given base class, or an inner class
     *  of a subclass?
     *  Return null if no such class exists.
     *  @param c     The class which is the subclass or is contained in it.
     *  @param base  The base class
     */
    private boolean isInnerSubClass(ClassSymbol c, Symbol base) {
        while (c != null && !c.isSubClass(base, types)) {
            c = c.owner.enclClass();
        }
        return c != null;
    }

    boolean isAccessible(F3Env<F3AttrContext> env, Type t) {
        return (t.tag == ARRAY)
            ? isAccessible(env, types.elemtype(t))
            : isAccessible(env, t.tsym);
    }

    /** Is symbol accessible as a member of given type in given environment?
     *  @param env    The current environment.
     *  @param site   The type of which the tested symbol is regarded
     *                as a member.
     *  @param sym    The symbol.
     */
    public boolean isAccessible(F3Env<F3AttrContext> env, Type site, Symbol sym) {
        if (sym.name == names.init && sym.owner != site.tsym) return false;
        if ((sym.flags() & (F3Flags.PUBLIC_READ | F3Flags.PUBLIC_INIT)) != 0) {
            // assignment access handled elsewhere -- treat like
            return isAccessible(env, site);
        }

        // if the READABLE flag isn't set, then access for read is the same as for write
        return isAccessibleForWrite(env, site, sym);
    }

    /** Is symbol accessible for write as a member of given type in given environment?
     *  @param env    The current environment.
     *  @param site   The type of which the tested symbol is regarded
     *                as a member.
     *  @param sym    The symbol.
     */
    public boolean isAccessibleForWrite(F3Env<F3AttrContext> env, Type site, Symbol sym) {
        if (sym.name == names.init && sym.owner != site.tsym) return false;
        // because the SCRIPT_PRIVATE bit is too high for the switch, test it later
        switch ((short)(sym.flags() & Flags.AccessFlags)) {
        case PRIVATE:
            return
                (env.enclClass.sym == sym.owner // fast special case
                 ||
                 env.enclClass.sym.outermostClass() ==
                 sym.owner.outermostClass())
                &&
                isInheritedIn(sym, site.tsym, types);
        case 0:
            if ((sym.flags() & F3Flags.SCRIPT_PRIVATE) != 0) {
                // script-private
                //TODO: don't know what is right
                if (env.enclClass.sym == sym.owner) {
                    return true;  // fast special case -- in this class
                }
                Symbol enclOuter = env.enclClass.sym.outermostClass();
                Symbol ownerOuter = sym.owner.outermostClass();
                return enclOuter == ownerOuter;
            };
            // 'package' access
            PackageSymbol pkg = env.toplevel.packge;
            boolean samePkg = 
                    (pkg == sym.owner.owner // fast special case
                    ||
                    pkg == sym.packge());
            boolean typeAccessible = isAccessible(env, site);
            // TODO: javac logic is to also 'and'-in inheritedIn.
            // Based possibly on bugs in what is passed in,
            // this doesn't work when accessing static variables in an outer class.
            // When called from findVar this works because the site in the actual
            // owner of the sym, but when coming from an ident and checkAssignable
            // this isn't true.
            //boolean inheritedIn = isInheritedIn(sym, site.tsym, types);
            return samePkg && typeAccessible;
        case PROTECTED:
            return
                (env.toplevel.packge == sym.owner.owner // fast special case
                 ||
                 env.toplevel.packge == sym.packge()
                 ||
                 isProtectedAccessible(sym, env.enclClass.sym, site)
                 ||
                 // OK to select instance method or field from 'super' or type name
                 // (but type names should be disallowed elsewhere!)
                 env.info.selectSuper && (sym.flags() & STATIC) == 0 && sym.kind != TYP)
                &&
                isAccessible(env, site)
                &&
                // `sym' is accessible only if not overridden by
                // another symbol which is a member of `site'
                // (because, if it is overridden, `sym' is not strictly
                // speaking a member of `site'.)
                (sym.kind != MTH || sym.isConstructor() ||
                 types.implementation((MethodSymbol)sym, site.tsym, true) == sym);
        default: // this case includes erroneous combinations as well
            return isAccessible(env, site);
        }
    }
    //where
        /** Is given protected symbol accessible if it is selected from given site
         *  and the selection takes place in given class?
         *  @param sym     The symbol with protected access
         *  @param c       The class where the access takes place
         *  @site          The type of the qualifier
         */
        private
        boolean isProtectedAccessible(Symbol sym, ClassSymbol c, Type site) {
            while (c != null &&
                   !(types.isSubtype(c.type, types.erasure(sym.owner.type)) &&
                     (c.flags() & INTERFACE) == 0 &&
                     // In JLS 2e 6.6.2.1, the subclass restriction applies
                     // only to instance fields and methods -- types are excluded
                     // regardless of whether they are declared 'static' or not.
                     ((sym.flags() & STATIC) != 0 || sym.kind == TYP || site.tsym.isSubClass(c, types))))
                c = c.owner.enclClass();
            return c != null;
        }

    /** Try to instantiate the type of a method so that it fits
     *  given type arguments and argument types. If succesful, return
     *  the method's instantiated type, else return null.
     *  The instantiation will take into account an additional leading
     *  formal parameter if the method is an instance method seen as a member
     *  of un underdetermined site In this case, we treat site as an additional
     *  parameter and the parameters of the class containing the method as
     *  additional type variables that get instantiated.
     *
     *  @param env         The current environment
     *  @param m           The method symbol.
     *  @param mt          The expected type.
     *  @param argtypes    The invocation's given value arguments.
     *  @param typeargtypes    The invocation's given type arguments.
     *  @param allowBoxing Allow boxing conversions of arguments.
     *  @param useVarargs Box trailing arguments into an array for varargs.
     */
    Type rawInstantiate(F3Env<F3AttrContext> env,
                        Symbol m,
                        Type mt,
                        List<Type> argtypes,
                        List<Type> typeargtypes,
                        boolean allowBoxing,
                        boolean useVarargs,
                        Warner warn)
        throws Infer.NoInstanceException {
        if (useVarargs && (m.flags() & VARARGS) == 0) return null;
        m.complete();

        // tvars is the list of formal type variables for which type arguments
        // need to inferred.
        List<Type> tvars = env.info.tvars;
        if (typeargtypes == null) typeargtypes = List.nil();
        if (mt == null) {
            return null;
        }
        if (mt.tag != FORALL && typeargtypes.nonEmpty()) {
            // This is not a polymorphic method, but typeargs are supplied
            // which is fine, see JLS3 15.12.2.1
        } else if (mt.tag == FORALL && typeargtypes.nonEmpty()) {
            ForAll pmt = (ForAll) mt;
            if (typeargtypes.length() != pmt.tvars.length())
                return null;
            // Check type arguments are within bounds
            List<Type> formals = pmt.tvars;
            List<Type> actuals = typeargtypes;
            while (formals.nonEmpty() && actuals.nonEmpty()) {
                List<Type> bounds = types.subst(types.getBounds((TypeVar)formals.head),
                                                pmt.tvars, typeargtypes);
                for (; bounds.nonEmpty(); bounds = bounds.tail)
                    if (!types.isSubtypeUnchecked(actuals.head, bounds.head, warn))
                        return null;
                formals = formals.tail;
                actuals = actuals.tail;
            }
            mt = types.subst(pmt.qtype, pmt.tvars, typeargtypes);
        } else if (mt.tag == FORALL) {
            ForAll pmt = (ForAll) mt;
            List<Type> tvars1 = types.newInstances(pmt.tvars);
            tvars = tvars.appendList(tvars1);
            mt = types.subst(pmt.qtype, pmt.tvars, tvars1);
        }
        // find out whether we need to go the slow route via infer
        boolean instNeeded = tvars.tail != null/*inlined: tvars.nonEmpty()*/;
        for (List<Type> l = argtypes;
             l.tail != null/*inlined: l.nonEmpty()*/ && !instNeeded;
             l = l.tail) {
            if (l.head.tag == FORALL) instNeeded = true;
        }
        if (instNeeded) {
	    Type r = 
		infer.instantiateMethod(tvars,
					(MethodType)mt,
					argtypes,
					allowBoxing,
					useVarargs,
					warn);
	    //System.err.println("infer " + mt + " = "+ r);
	    return r;
        } return
            argumentsAcceptable(argtypes, mt.getParameterTypes(),
                                allowBoxing, useVarargs, warn)
            ? mt
            : null;
    }

    /** Same but returns null instead throwing a NoInstanceException
     */
    Type instantiate(F3Env<F3AttrContext> env,
                     Type site,
                     Symbol m,
                     List<Type> argtypes,
                     List<Type> typeargtypes,
                     boolean allowBoxing,
                     boolean useVarargs,
                     Warner warn) {
        try {
            Type r = rawInstantiate(env, m, types.memberType(site, m), argtypes, typeargtypes,
				    allowBoxing, useVarargs, warn);
	    //System.err.println("instantiated "+m+" to "+ r);
	    return r;
        } catch (Infer.NoInstanceException ex) {
            return null;
        }
    }

    /** Check if a parameter list accepts a list of args.
     */
    boolean argumentsAcceptable(List<Type> argtypes,
                                List<Type> formals,
                                boolean allowBoxing,
                                boolean useVarargs,
                                Warner warn) {
        Type varargsFormal = useVarargs ? formals.last() : null;
        while (argtypes.nonEmpty() && formals.head != varargsFormal) {
            boolean works = allowBoxing
                ? types.isConvertible(argtypes.head, formals.head, warn)
                : types.isSubtypeUnchecked(argtypes.head, formals.head, warn);
            if (!works) {
		//System.err.println("unacceptable: "+argtypes.head +": "+ formals.head);
		return false;
	    }
            argtypes = argtypes.tail;
            formals = formals.tail;
        }
        if (formals.head != varargsFormal) {
	    //System.err.println("not enough args: "+ formals + " " + argtypes);
	    return false; // not enough args
	}
        if (!useVarargs)
            return argtypes.isEmpty();
        Type elt = types.elemtype(varargsFormal);
        while (argtypes.nonEmpty()) {
            if (!types.isConvertible(argtypes.head, elt, warn)) {
                System.err.println("can't convert "+argtypes.head + " to " +elt);
                return false;
            }
            argtypes = argtypes.tail;
        }
        return true;
    }

/* ***************************************************************************
 *  Symbol lookup
 *  the following naming conventions for arguments are used
 *
 *       env      is the environment where the symbol was mentioned
 *       site     is the type of which the symbol is a member
 *       name     is the symbol's name
 *                if no arguments are given
 *       argtypes are the value arguments, if we search for a method
 *
 *  If no symbol was found, a ResolveError detailing the problem is returned.
 ****************************************************************************/
    /** Find field. Synthetic fields are always skipped.
     *  @param env     The current environment.
     *  @param site    The original type from where the selection takes place.
     *  @param name    The name of the field.
     *  @param c       The class to search for the field. This is always
     *                 a superclass or implemented interface of site's class.
     */
    public Symbol findField(F3Env<F3AttrContext> env,
                     Type site,
                     Name name,
                     TypeSymbol c) {
        Symbol bestSoFar = varNotFound;
        Symbol sym;
        if (name == null) {
            name = lookupDefault(c);
        }
        Scope.Entry e = name == null ? null : c.members().lookup(name);
        while (e != null && e.scope != null) {
            if ((e.sym.kind & (VAR|MTH)) != 0 && (e.sym.flags_field & SYNTHETIC) == 0) {
                sym = isAccessible(env, site, e.sym)
                    ? e.sym : new AccessError(env, site, e.sym);
                if (bestSoFar.kind < AMBIGUOUS && sym.kind < AMBIGUOUS &&
                    sym.owner != bestSoFar.owner)
                    bestSoFar = new AmbiguityError(bestSoFar, sym);
                else if (sym.kind < bestSoFar.kind)
                    bestSoFar = sym;
            }
            e = e.next();
        }
        if (bestSoFar != varNotFound)
            return bestSoFar;
        Type st = types.supertype(c.type);
        if (st != null && st.tag == CLASS) {
            sym = findField(env, site, name, st.tsym);
            if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

         // We failed to find the field in the single Java class supertype of the 
         // F3 class.
         // Now try to find the field in all of the F3 supertypes.
         if (bestSoFar.kind > AMBIGUOUS && c instanceof F3ClassSymbol) {
             List<Type> supertypes = types.supertypes(c.type);
             for (Type tp : supertypes) {
                 if (tp != null && tp.tag == CLASS) {
                     sym = findField(env, site, name, tp.tsym);
                     if (sym.kind < bestSoFar.kind) bestSoFar = sym;
                     if (bestSoFar.kind < AMBIGUOUS) {
                         break;
                     }
                 }
             }
         }

        for (List<Type> l = types.interfaces(c.type);
             bestSoFar.kind != AMBIGUOUS && l.nonEmpty();
             l = l.tail) {
            sym = findField(env, site, name, l.head.tsym);
            if (bestSoFar.kind < AMBIGUOUS && sym.kind < AMBIGUOUS &&
                sym.owner != bestSoFar.owner && !mixableIn(bestSoFar, sym, site))
                bestSoFar = new AmbiguityError(bestSoFar, sym);
            else if (sym.kind < bestSoFar.kind)
                bestSoFar = sym;
        }
        return bestSoFar;
    }
    //where
    boolean mixableIn(Symbol s1, Symbol s2, Type site) {
        if (!types.isMixin(s1.owner) &&
                !types.isMixin(s2.owner))
            return false;
        List<Type> supertypes = types.supertypesClosure(site);
        int i1 = indexInSupertypeList(supertypes, s1.owner.type);
        int i2 = indexInSupertypeList(supertypes, s2.owner.type);
        return i1 <= i2 && i1 != -1 && i2 != -1;
    }

    int indexInSupertypeList(List<Type> ts, Type t) {
        int count = 0;
        for (Type t2 : ts) {
            if (types.isSameType(t, t2))
                return count;
            count++;
        }
        return -1;
    }

    /** Resolve a field identifier, throw a fatal error if not found.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the method invocation.
     *  @param site      The type of the qualifying expression, in which
     *                   identifier is searched.
     *  @param name      The identifier's name.
     */
    public F3VarSymbol resolveInternalField(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                                          Type site, Name name) {
        Symbol sym = findField(env, site, name, site.tsym);
        if (sym.kind == VAR) return (F3VarSymbol)sym;
        else throw new FatalError(
                 JCDiagnostic.fragment(MsgSym.MESSAGE_FATAL_ERR_CANNOT_LOCATE_FIELD,
                                name));
    }

    /** Find unqualified variable or field with given name.
     *  Synthetic fields always skipped.
     *  @param env     The current environment.
     *  @param name    The name of the variable or field.
     */
    Symbol findVar(F3Env<F3AttrContext> env, Name name, int kind, Type expected, 
		   boolean boxingEnabled, boolean varargsEnabled) 
    {
        Symbol bestSoFar = expected.tag == METHOD ? methodNotFound : varNotFound;
        Symbol sym;
        F3Env<F3AttrContext> env1 = env;
        boolean staticOnly = false;
        boolean innerAccess = false;
        Type mtype = expected;
        if (mtype instanceof FunctionType)
            mtype = ((FunctionType)mtype).asMethodOrForAll();
        boolean checkArgs = (mtype instanceof MethodType) || (mtype instanceof ForAll);
	//System.err.println("checkArgs: "+ checkArgs+": "+mtype.getClass()+": "+mtype);
        while (env1 != null) {
            Scope sc = env1.info.scope;
            Type envClass;
            if (env1.tree instanceof F3ClassDeclaration) {
                F3ClassDeclaration cdecl = (F3ClassDeclaration) env1.tree;
                if (cdecl.runMethod != null &&
                        name != names._this && name != names._super) {
                    envClass = null;
                    sc = cdecl.runBodyScope;
                    innerAccess = true;
                }
                envClass = cdecl.sym.type;
            }
            else
                envClass = null;
            if (envClass != null) {
                //first try resolution without boxing

                sym = findMember(env1, envClass, name,
                        expected,
                        boxingEnabled, varargsEnabled, false);

                if (sym.exists()) {
                    if (staticOnly) {
                        // Note: can't call isStatic with null owner
                        if (sym.owner != null) {
                            if (!sym.isStatic()) {
                                return new StaticError(sym);
                            }
                        }
                    }
                    return sym;
                }
            }
            if (sc != null) {
                for (Scope.Entry e = sc.lookup(name); e.scope != null; e = e.next()) {
                    if ((e.sym.flags_field & SYNTHETIC) != 0)
                        continue;
                    if ((e.sym.kind & (MTH|VAR)) != 0) {
                        if (checkArgs) {
                            return checkArgs(e.sym, mtype);
                        }
                        return !e.sym.isStatic() && staticOnly ?
                            new StaticError(e.sym) :
                            e.sym;
                    }
                }
            }
            if (env1.tree instanceof F3FunctionDefinition)
                innerAccess = true;
            if (env1.outer != null && isStatic(env1)) staticOnly = true;
            env1 = env1.outer;
        }
	/*
        Scope.Entry e = env.toplevel.namedImportScope.lookup(name);
        for (; e.scope != null; e = e.next()) {
            sym = e.sym;
            Type origin = e.getOrigin().owner.type;
            if ((sym.kind & (MTH|VAR)) != 0) {
                if (e.sym.owner.type != origin)
                    sym = sym.clone(e.getOrigin().owner);
                if (sym.kind == VAR)
                    return isAccessible(env, origin, sym)
                    ? sym : new AccessError(env, origin, sym);
                else //method 
		    {
			sym = selectBest(env, origin, mtype,
					 e.sym, bestSoFar,
					 boxingEnabled,
					 varargsEnabled,
					 false);
			System.err.println(sym +" for "+e.sym+" for "+name);
		    }
            }
        }
	*/
        Symbol origin = null;
        Scope.Entry e = env.toplevel.starImportScope.lookup(name);
        for (; e.scope != null; e = e.next()) {
            sym = e.sym;
            if ((sym.kind & (MTH|VAR)) == 0)
                continue;
            // invariant: sym.kind == VAR
            if (bestSoFar.kind < AMBIGUOUS && sym.owner != bestSoFar.owner)
                return new AmbiguityError(bestSoFar, sym);
            else if (bestSoFar.kind >= VAR) {
                origin = e.getOrigin().owner;
		//System.err.println("sym: "+ (sym.kind == VAR) + " "+ (sym.kind == MTH));
		//System.err.println("sym: "+ sym.getClass());
		//System.err.println("sym: "+ sym);
                if (sym.kind == VAR || !checkArgs)
                    bestSoFar = isAccessible(env, origin.type, sym)
                    ? sym : new AccessError(env, origin.type, sym);
                else { //method
                    bestSoFar = selectBest(env, origin.type, mtype,
                                           e.sym, bestSoFar,
                                           boxingEnabled,
                                           varargsEnabled,
                                           false);
		    //System.err.println("select best: "+ e.sym + ": "+bestSoFar);
		}
            }
	    //System.err.println("findVar: "+name+": "+sym+": "+bestSoFar);
        }
        if (name == names.fromString("__DIR__") || name == names.fromString("__FILE__") 
			|| name == names.fromString("__PROFILE__")) {
            Type type = syms.stringType;
            return new F3VarSymbol(types, names,Flags.PUBLIC, name, type, env.enclClass.sym);
        }
        if (bestSoFar.kind == VAR && bestSoFar.owner.type != origin.type)
            return bestSoFar.clone(origin);
        else
            return bestSoFar;
    }
    //where

    private Symbol checkArgs(Symbol sym, Type mtype) {
        Type mt = sym.type;
        if (mt instanceof FunctionType) {
            mt = ((FunctionType)mt).asMethodOrForAll();
        }
        // Better to use selectBest, but that requires some
        // changes.  FIXME
        if (!((mt instanceof MethodType) || (mt instanceof ForAll)) ||
                !argumentsAcceptable(mtype.getParameterTypes(), mt.getParameterTypes(),
                true, false, Warner.noWarnings)) {
	    //System.err.println("check args failed: "+ sym + ": "+mtype);
            return wrongMethod.setWrongSym(sym);
        }
        return sym;

    }

    Warner noteWarner = new Warner();

    /** Select the best method for a call site among two choices.
     *  @param env              The current environment.
     *  @param site             The original type from where the
     *                          selection takes place.
     *  @param argtypes         The invocation's value arguments,
     *  @param typeargtypes     The invocation's type arguments,
     *  @param sym              Proposed new best match.
     *  @param bestSoFar        Previously found best match.
     *  @param allowBoxing Allow boxing conversions of arguments.
     *  @param useVarargs Box trailing arguments into an array for varargs.
     */
    Symbol selectBest(F3Env<F3AttrContext> env,
                      Type site,
                      Type expected,
                      Symbol sym,
                      Symbol bestSoFar,
                      boolean allowBoxing,
                      boolean useVarargs,
                      boolean operator) {
        if (sym.kind == ERR) return bestSoFar;
        if (!isInheritedIn(sym, site.tsym, types)) return bestSoFar;
        assert sym.kind < AMBIGUOUS;
        List<Type> argtypes = expected.getParameterTypes();
        List<Type> typeargtypes = expected.getTypeArguments();
	//System.err.println("expected="+expected);
	//System.err.println("clazz="+expected.getClass());
        try {
	    Type memberType = types.memberType(site, sym);
	    //System.err.println("memberType: "+types.memberType(site, sym));
	    //System.err.println("clazz="+memberType.getClass());
	    //if (types.isSameType(memberType, expected)) {
	    //return sym;
	    //}
	    Type tx;
            if ((tx =rawInstantiate(env, sym, memberType, argtypes, typeargtypes,
				    allowBoxing, useVarargs, Warner.noWarnings)) == null) {
                // inapplicable
		System.err.println("raw instantiate failed: "+ sym);
		System.err.println("argtypes: "+argtypes);
		System.err.println("typeargtypes: "+typeargtypes);
		//Thread.currentThread().dumpStack();
                switch (bestSoFar.kind) {
                case ABSENT_MTH: return wrongMethod.setWrongSym(sym);
                case WRONG_MTH: return wrongMethods;
                default: return bestSoFar;
                }
            }
	    //System.err.println("instantiated: "+ sym.type);
	    //System.err.println("memberType: "+ memberType);
	    //System.err.println("tx: "+ tx);
	    if (allowBoxing) { // hack
		if (memberType instanceof ForAll) {
		    sym = sym.clone(sym.owner);
		    sym.type = reader.translateType(tx);
		    //System.err.println("instantiated: "+ sym.type);
		    //System.err.println("memberType: "+ memberType);
		    //System.err.println("tx: "+ tx);
		}
	    }
        } catch (Infer.NoInstanceException ex) {
	    System.err.println("raw instantiate exception: "+ sym);
	    System.err.println("argtypes: "+argtypes);
	    System.err.println("typeargtypes: "+typeargtypes);
            switch (bestSoFar.kind) {
            case ABSENT_MTH:
                return wrongMethod.setWrongSym(sym, ex.getDiagnostic());
            case WRONG_MTH:
                return wrongMethods;
            default:
                return bestSoFar;
            }
        }
        if (!isAccessible(env, site, sym)) {
            return (bestSoFar.kind == ABSENT_MTH)
                ? new AccessError(env, site, sym)
                : bestSoFar;
        }
        return (bestSoFar.kind > AMBIGUOUS)
            ? sym
            : mostSpecific(sym, bestSoFar, env, site,
                           allowBoxing && operator, useVarargs);
    }

    /* Return the most specific of the two methods for a call,
     *  given that both are accessible and applicable.
     *  @param m1               A new candidate for most specific.
     *  @param m2               The previous most specific candidate.
     *  @param env              The current environment.
     *  @param site             The original type from where the selection
     *                          takes place.
     *  @param allowBoxing Allow boxing conversions of arguments.
     *  @param useVarargs Box trailing arguments into an array for varargs.
     */
    Symbol mostSpecific(Symbol m1,
                        Symbol m2,
                        F3Env<F3AttrContext> env,
                        Type site,
                        boolean allowBoxing,
                        boolean useVarargs) {
        switch (m2.kind) {
        case MTH:
            if (m1 == m2) return m1;
            Type mt1 = types.memberType(site, m1);
            noteWarner.unchecked = false;
            boolean m1SignatureMoreSpecific =
                (instantiate(env, site, m2, types.lowerBoundArgtypes(mt1), null,
                             allowBoxing, false, noteWarner) != null ||
                 useVarargs && instantiate(env, site, m2, types.lowerBoundArgtypes(mt1), null,
                                           allowBoxing, true, noteWarner) != null) &&
                !noteWarner.unchecked;
            Type mt2 = types.memberType(site, m2);
            noteWarner.unchecked = false;
            boolean m2SignatureMoreSpecific =
                (instantiate(env, site, m1, types.lowerBoundArgtypes(mt2), null,
                             allowBoxing, false, noteWarner) != null ||
                 useVarargs && instantiate(env, site, m1, types.lowerBoundArgtypes(mt2), null,
                                           allowBoxing, true, noteWarner) != null) &&
                !noteWarner.unchecked;
            if (m1SignatureMoreSpecific && m2SignatureMoreSpecific) {
                if (!types.overrideEquivalent(mt1, mt2))
                    return new AmbiguityError(m1, m2);
                // same signature; select (a) the non-bridge method, or
                // (b) the one that overrides the other, or (c) the concrete
                // one, or (d) merge both abstract signatures
                if ((m1.flags() & BRIDGE) != (m2.flags() & BRIDGE)) {
                    return ((m1.flags() & BRIDGE) != 0) ? m2 : m1;
                }
                // if one overrides or hides the other, use it
                TypeSymbol m1Owner = (TypeSymbol)m1.owner;
                TypeSymbol m2Owner = (TypeSymbol)m2.owner;
                if (types.asSuper(m1Owner.type, m2Owner) != null &&
                    ((m1.owner.flags_field & INTERFACE) == 0 ||
                     (m2.owner.flags_field & INTERFACE) != 0) &&
                    m1.overrides(m2, m1Owner, types, false))
                    return m1;
                if (types.asSuper(m2Owner.type, m1Owner) != null &&
                    ((m2.owner.flags_field & INTERFACE) == 0 ||
                     (m1.owner.flags_field & INTERFACE) != 0) &&
                    m2.overrides(m1, m2Owner, types, false))
                    return m2;
                boolean m1Abstract = (m1.flags() & ABSTRACT) != 0;
                boolean m2Abstract = (m2.flags() & ABSTRACT) != 0;
                if (m1Abstract && !m2Abstract) return m2;
                if (m2Abstract && !m1Abstract) return m1;
                // both abstract or both concrete
                if (!m1Abstract && !m2Abstract)
                    return !mixableIn(m2, m1, site) ? new AmbiguityError(m1, m2) : m2;
                // check for same erasure
                if (!types.isSameType(m1.erasure(types), m2.erasure(types)))
                    return new AmbiguityError(m1, m2);
                // both abstract, neither overridden; merge throws clause and result type
                Symbol result;
                Type result2 = mt2.getReturnType();
                if (mt2.tag == FORALL)
                    result2 = types.subst(result2, ((ForAll)mt2).tvars, ((ForAll)mt1).tvars);
                if (types.isSubtype(mt1.getReturnType(), result2)) {
                    result = m1;
                } else if (types.isSubtype(result2, mt1.getReturnType())) {
                    result = m2;
                } else {
                    // Theoretically, this can't happen, but it is possible
                    // due to error recovery or mixing incompatible class files
                    return new AmbiguityError(m1, m2);
                }
                result = result.clone(result.owner);
                result.type = (Type)result.type.clone();
                result.type.setThrown(chk.intersect(mt1.getThrownTypes(),
                                                    mt2.getThrownTypes()));
                return result;
            }
            if (m1SignatureMoreSpecific) return m1;
            if (m2SignatureMoreSpecific) return m2;
            return new AmbiguityError(m1, m2);
        case AMBIGUOUS:
            AmbiguityError e = (AmbiguityError)m2;
            Symbol err1 = mostSpecific(m1, e.sym1, env, site, allowBoxing, useVarargs);
            Symbol err2 = mostSpecific(m1, e.sym2, env, site, allowBoxing, useVarargs);
            if (err1 == err2) return err1;
            if (err1 == e.sym1 && err2 == e.sym2) return m2;
            if (err1 instanceof AmbiguityError &&
                err2 instanceof AmbiguityError &&
                ((AmbiguityError)err1).sym1 == ((AmbiguityError)err2).sym1)
                return new AmbiguityError(m1, m2);
            else
                return new AmbiguityError(err1, err2);
        default:
            throw new AssertionError();
        }
    }

    /** Find best qualified method matching given name, type and value
     *  arguments.
     *  @param env       The current environment.
     *  @param site      The original type from where the selection
     *                   takes place.
     *  @param name      The method's name.
     *  @param argtypes  The method's value arguments.
     *  @param typeargtypes The method's type arguments
     *  @param allowBoxing Allow boxing conversions of arguments.
     *  @param useVarargs Box trailing arguments into an array for varargs.
     */

    Symbol findMethod(F3Env<F3AttrContext> env,
                      Type site,
                      Name name,
                      List<Type> argtypes,
                      List<Type> typeargtypes,
                      boolean allowBoxing,
                      boolean useVarargs,
                      boolean operator) {
        return findMember(env,
                          site,
                          name,
                          newMethTemplate(argtypes, typeargtypes),
                          site.tsym.type,
                          methodNotFound,
                          allowBoxing,
                          useVarargs,
                          operator);
    }

    Symbol findMember(F3Env<F3AttrContext> env,
                      Type site,
                      Name name,
                      Type expected,
                      boolean allowBoxing,
                      boolean useVarargs,
                      boolean operator) {
        return findMember(env,
                          site,
                          name,
                          expected,
                          site.tsym.type,
                          methodNotFound,
                          allowBoxing,
                          useVarargs,
                          operator);
    }
    // where
    private Symbol findMember(F3Env<F3AttrContext> env,
                              Type site,
                              Name name,
                              Type expected,
                              Type intype,
                              Symbol bestSoFar,
                              boolean allowBoxing,
                              boolean useVarargs,
                              boolean operator) {
        Symbol best = findMemberWithoutAccessChecks(env,
                          site,
                          name,
                          expected,
                          intype,
                          bestSoFar,
                          allowBoxing,
                          useVarargs,
                          operator);
        if (!(best instanceof ResolveError) && !isAccessible(env, site, best)) {
            // it is not accessible, return an error instead
            best = new AccessError(env, site, best);
        }
        return best;
    }
    // where
    private Symbol findMemberWithoutAccessChecks(F3Env<F3AttrContext> env,
                              Type site,
                              Name name,
                              Type expected,
                              Type intype,
                              Symbol bestSoFar,
                              boolean allowBoxing,
                              boolean useVarargs,
                              boolean operator) {
        Type mtype = expected;
        if (mtype instanceof FunctionType)
            mtype = ((FunctionType)mtype).asMethodOrForAll();
        boolean checkArgs = mtype instanceof MethodType || mtype instanceof ForAll;
        for (Type ct = intype; ct.tag == CLASS; ct = types.supertype(ct)) {
            ClassSymbol c = (ClassSymbol)ct.tsym;
	    if (c.members() == null) {
		System.err.println("site="+site);
		System.err.println("members null: "+ c);
		continue;
	    }
	    for (Scope.Entry e = c.members().lookup(name);
                 e.scope != null;
                 e = e.next()) {
                if ((e.sym.kind & (VAR|MTH)) == 0 ||
                        (e.sym.flags_field & SYNTHETIC) != 0)
                    continue;
                e.sym.complete();
                if (! checkArgs) {
                    // No argument list to disambiguate.
                    if (bestSoFar.kind == ABSENT_VAR || bestSoFar.kind == ABSENT_MTH)
                        bestSoFar = e.sym;
                    else if (e.sym != bestSoFar && !mixableIn(bestSoFar, e.sym, site))
                        bestSoFar = new AmbiguityError(bestSoFar, e.sym);
                }
                else if (e.sym.kind == MTH) {                    
		    //System.err.println("type="+e.sym.type);
		    //System.err.println("mtype="+mtype);
                    bestSoFar = selectBest(env, site, mtype,
                                           e.sym, bestSoFar,
                                           allowBoxing,
                                           useVarargs,
                                           operator);
                }
                else if ((e.sym.kind & (VAR|MTH)) != 0 && bestSoFar == methodNotFound) {
                    // FIXME duplicates logic in findVar.
                    Type mt = e.sym.type;
                    if (mt instanceof FunctionType)
                        mt = ((FunctionType)mt).asMethodOrForAll();
                    if (!( (mt instanceof MethodType) || (mt instanceof ForAll)) ||
                            ! argumentsAcceptable(mtype.getParameterTypes(), mt.getParameterTypes(),
						  true, false, Warner.noWarnings)) {
			System.err.println("arguments unacceptable : "+mtype + ": "+ mt);
                        return wrongMethod.setWrongSym(e.sym);
		    }
                    return e.sym;
                }
            }
            if (! checkArgs &&
                bestSoFar.kind != ABSENT_VAR && bestSoFar.kind != ABSENT_MTH) {
                return bestSoFar;
            }
            Symbol concrete = methodNotFound;
            if ((bestSoFar.flags() & ABSTRACT) == 0)
                concrete = bestSoFar;
            for (List<Type> l = types.interfaces(c.type);
                 l.nonEmpty();
                 l = l.tail) {
                bestSoFar = findMemberWithoutAccessChecks(env, site, name, expected,
                                       l.head, bestSoFar,
                                       allowBoxing, useVarargs, operator);
            }
            if (concrete != bestSoFar &&
                concrete.kind < ERR  && bestSoFar.kind < ERR &&
                types.isSubSignature(concrete.type, bestSoFar.type))
                bestSoFar = concrete;
            if (name == names.init)
                return bestSoFar;
        }

        // We failed to find the field in the single Java class supertype of the 
        // F3 class.
        // Now try to find the field in all of the F3 supertypes.
        if (bestSoFar.kind > AMBIGUOUS && intype.tsym instanceof F3ClassSymbol) {
            List<Type> supertypes = types.supertypes(intype);
            for (Type tp : supertypes) {
                bestSoFar = findMemberWithoutAccessChecks(env, site, name, expected, tp,
                        bestSoFar, allowBoxing, useVarargs, operator);                
                if (bestSoFar.kind < AMBIGUOUS) {
                    break;
                }
            }
        }

        return bestSoFar;
    }

    private boolean isExactMatch(Type mtype, Symbol bestSoFar) {
        if (bestSoFar.kind == MTH && (bestSoFar.type instanceof MethodType) &&
                mtype.tag == TypeTags.METHOD ) {
            List<Type> actuals = ((MethodType)mtype).getParameterTypes();
            List<Type> formals = ((MethodType)bestSoFar.type).getParameterTypes();
            if (actuals != null && formals != null) {
                if (actuals.size() == formals.size()) {
                    for (Type actual : actuals) {
                        if (! types.isSameType(actual, formals.head)) {
                            return false;
                        }

                        formals = formals.tail;
                    }
                    return true;
                }
            }
        }
        
        return false;
    }

    Type newMethTemplate(List<Type> argtypes, List<Type> typeargtypes) {
        MethodType mt = new MethodType(argtypes, syms.voidType, null, syms.methodClass);
        return (typeargtypes == null) ? mt : (Type)new ForAll(typeargtypes, mt);
    }
         
    /** Load toplevel or member class with given fully qualified name and
     *  verify that it is accessible.
     *  @param env       The current environment.
     *  @param name      The fully qualified name of the class to be loaded.
     */
    Symbol loadClass(F3Env<F3AttrContext> env, Name name) {
        try {
            ClassSymbol c = reader.loadClass(name);
            return isAccessible(env, c) ? c : new AccessError(c);
        } catch (ClassReader.BadClassFile err) {
            throw err;
        } catch (CompletionFailure ex) {
            return typeNotFound;
        }
    }

    /** Find qualified member type.
     *  @param env       The current environment.
     *  @param site      The original type from where the selection takes
     *                   place.
     *  @param name      The type's name.
     *  @param c         The class to search for the member type. This is
     *                   always a superclass or implemented interface of
     *                   site's class.
     */
// F3 change
    public
// F3 change
    Symbol findMemberType(F3Env<F3AttrContext> env,
                          Type site,
                          Name name,
                          TypeSymbol c) {
        Symbol bestSoFar = typeNotFound;
        Symbol sym;
        Scope.Entry e = c.members().lookup(name);
        while (e.scope != null) {
            if (e.sym.kind == TYP) {
                return isAccessible(env, site, e.sym)
                    ? e.sym
                    : new AccessError(env, site, e.sym);
            }
            e = e.next();
        }
        Type st = types.supertype(c.type);
        if (st != null && st.tag == CLASS) {
            sym = findMemberType(env, site, name, st.tsym);
            if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        // We failed to find the field in the single Java class supertype of the 
        // F3 class.
        // Now try to find the filed in all of the F3 supertypes.
//        if (bestSoFar.kind > AMBIGUOUS && c instanceof F3ClassSymbol) {
//            List<Type> supertypes = ((F3ClassSymbol)c).getSuperTypes();
//            for (Type tp : supertypes) {
//                if (tp != null && tp.tag == CLASS) {
//                    sym = findField(env, site, name, tp.tsym);
//                    if (sym.kind < bestSoFar.kind) bestSoFar = sym;
//                    if (bestSoFar.kind < AMBIGUOUS) {
//                        break;
//                    }
//                }
//            }
//        }

        for (List<Type> l = types.interfaces(c.type);
             bestSoFar.kind != AMBIGUOUS && l.nonEmpty();
             l = l.tail) {
            sym = findMemberType(env, site, name, l.head.tsym);
            if (bestSoFar.kind < AMBIGUOUS && sym.kind < AMBIGUOUS &&
                sym.owner != bestSoFar.owner)
                bestSoFar = new AmbiguityError(bestSoFar, sym);
            else if (sym.kind < bestSoFar.kind)
                bestSoFar = sym;
        }
        return bestSoFar;
    }

    /** Find a global type in given scope and load corresponding class.
     *  @param env       The current environment.
     *  @param scope     The scope in which to look for the type.
     *  @param name      The type's name.
     */
    Symbol findGlobalType(F3Env<F3AttrContext> env, Scope scope, Name name) {
        Symbol bestSoFar = typeNotFound;
        for (Scope.Entry e = scope.lookup(name); e.scope != null; e = e.next()) {
            Symbol sym = loadClass(env, e.sym.flatName());
            if (bestSoFar.kind == TYP && sym.kind == TYP &&
                bestSoFar != sym)
                return new AmbiguityError(bestSoFar, sym);
            else if (sym.kind < bestSoFar.kind)
                bestSoFar = sym;
        }
        return bestSoFar;
    }

    Type findBuiltinType (Name typeName) {
        if (typeName == syms.booleanTypeName)
            return syms.f3_BooleanType;
        if (typeName == syms.charTypeName)
            return syms.f3_CharacterType;
        if (typeName == syms.byteTypeName)
            return syms.f3_ByteType;
        if (typeName == syms.shortTypeName)
            return syms.f3_ShortType;
        if (typeName == syms.integerTypeName)
            return syms.f3_IntegerType;
        if (typeName == syms.longTypeName)
            return syms.f3_LongType;
        if (typeName == syms.floatTypeName)
            return syms.f3_FloatType;
        if (typeName == syms.doubleTypeName)
            return syms.f3_DoubleType;
        if (typeName == syms.numberTypeName)
            return syms.f3_NumberType;
        if (typeName == syms.stringTypeName)
            return syms.f3_StringType;
        if (typeName == syms.voidTypeName)
            return syms.f3_VoidType;
        return null;
    }

   /** Find an unqualified type symbol.
     *  @param env       The current environment.
     *  @param name      The type's name.
     */
    Symbol findType(F3Env<F3AttrContext> env, Name name) {
        Symbol bestSoFar = typeNotFound;
        Symbol sym;
        boolean staticOnly = false;
        for (F3Env<F3AttrContext> env1 = env; env1.outer != null; env1 = env1.outer) {
            if (isStatic(env1)) staticOnly = true; else staticOnly = false;
            for (Scope.Entry e = env1.info.scope.lookup(name);
                 e.scope != null;
                 e = e.next()) {
                if (e.sym.kind == TYP) {
                    if (staticOnly &&
                        e.sym.type.tag == TYPEVAR &&
                        e.sym.owner.kind == TYP) return new StaticError(e.sym);
                    return e.sym;
                }
            }

            sym = findMemberType(env1, env1.enclClass.sym.type, name,
                                 env1.enclClass.sym);
            if (staticOnly && sym.kind == TYP &&
                sym.type.tag == CLASS &&
                sym.type.getEnclosingType().tag == CLASS &&
                env1.enclClass.sym.type.isParameterized() &&
                sym.type.getEnclosingType().isParameterized())
                return new StaticError(sym);
            else if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;

            F3ClassDeclaration encl = env1.baseClause ? (F3ClassDeclaration)env1.tree : env1.enclClass;
            if ((encl.sym.flags() & STATIC) != 0)
                staticOnly = true;
        }

        if (env.tree.getF3Tag() != F3Tag.IMPORT) {
            sym = findGlobalType(env, env.toplevel.namedImportScope, name);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        sym = findGlobalType(env, env.toplevel.packge.members(), name);
        if (sym.exists()) return sym;
        else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        
        Type type = findBuiltinType(name);
        if (type != null)
            return type.tsym;

        if (env.tree.getF3Tag() != F3Tag.IMPORT) {
            sym = findGlobalType(env, env.toplevel.starImportScope, name);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        return bestSoFar;
    }

    /** Find an unqualified identifier which matches a specified kind set.
     *  @param env       The current environment.
     *  @param name      The indentifier's name.
     *  @param kind      Indicates the possible symbol kinds
     *                   (a subset of VAL, TYP, PCK).
     */
    Symbol findIdent(F3Env<F3AttrContext> env, Name name, int kind, Type expected) {
        Symbol bestSoFar = expected.tag == METHOD ? methodNotFound : typeNotFound;
        Symbol sym;
         if ((kind & (VAR|MTH)) != 0) {
            sym = findVar(env, name, kind, expected, false, env.info.varArgs = false);
            if (sym.kind >= WRONG_MTHS)
                sym = findVar(env, name, kind, expected, true, env.info.varArgs = false);
            if (sym.kind >= WRONG_MTHS)
                sym = findVar(env, name, kind, expected, true, env.info.varArgs = true);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        if ((kind & TYP) != 0) {
            sym = findType(env, name);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        if ((kind & PCK) != 0) return reader.enterPackage(name);
        else return bestSoFar;
    }

    /** Find an identifier in a package which matches a specified kind set.
     *  @param env       The current environment.
     *  @param name      The identifier's name.
     *  @param kind      Indicates the possible symbol kinds
     *                   (a nonempty subset of TYP, PCK).
     */
    Symbol findIdentInPackage(F3Env<F3AttrContext> env, TypeSymbol pck,
                              Name name, int kind) {
        Name fullname = TypeSymbol.formFullName(name, pck);
        Symbol bestSoFar = typeNotFound;
        PackageSymbol pack = null;
        if ((kind & PCK) != 0) {
            pack = reader.enterPackage(fullname);
            if (pack.exists()) return pack;
        }
        if ((kind & TYP) != 0) {
            Symbol sym = loadClass(env, fullname);
            if (sym.exists()) {
                // don't allow programs to use flatnames
                if (name == sym.name) return sym;
            }
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }
        return (pack != null) ? pack : bestSoFar;
    }

    /** Find an identifier among the members of a given type `site'.
     *  @param env       The current environment.
     *  @param site      The type containing the symbol to be found.
     *  @param name      The identifier's name.
     *  @param kind      Indicates the possible symbol kinds
     *                   (a subset of VAL, TYP, MTH).
     */
    Symbol findIdentInType(F3Env<F3AttrContext> env, Type site,
                           Name name, int kind) {
        Symbol bestSoFar = typeNotFound;
        Symbol sym;
        if ((kind & (VAR|MTH)) != 0) {
            sym = findField(env, site, name, site.tsym);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }

        if ((kind & TYP) != 0) {
            sym = findMemberType(env, site, name, site.tsym);
            if (sym.exists()) return sym;
            else if (sym.kind < bestSoFar.kind) bestSoFar = sym;
        }
        return bestSoFar;
    }

/* ***************************************************************************
 *  Access checking
 *  The following methods convert ResolveErrors to ErrorSymbols, issuing
 *  an error message in the process
 ****************************************************************************/

    /** If `sym' is a bad symbol: report error and return errSymbol
     *  else pass through unchanged,
     *  additional arguments duplicate what has been used in trying to find the
     *  symbol (--> flyweight pattern). This improves performance since we
     *  expect misses to happen frequently.
     *
     *  @param sym       The symbol that was found, or a ResolveError.
     *  @param pos       The position to use for error reporting.
     *  @param site      The original type from where the selection took place.
     *  @param name      The symbol's name.
     *  @param argtypes  The invocation's value arguments,
     *                   if we looked for a method.
     *  @param typeargtypes  The invocation's type arguments,
     *                   if we looked for a method.
     */
    Symbol access(Symbol sym,
                  DiagnosticPosition pos,
                  Type site,
                  Name name,
                  boolean qualified,
                  List<Type> argtypes,
                  List<Type> typeargtypes) {
        if (sym.kind >= AMBIGUOUS) {
//          printscopes(site.tsym.members());//DEBUG
            if (!site.isErroneous() &&
                !Type.isErroneous(argtypes) &&
                (typeargtypes==null || !Type.isErroneous(typeargtypes))) {
                ((ResolveError)sym).report(log, pos, site, name, argtypes, typeargtypes);
            }
            do {
                sym = ((ResolveError)sym).sym;
            } while (sym.kind >= AMBIGUOUS);
            if (sym == syms.errSymbol // preserve the symbol name through errors
                || ((sym.kind & ERRONEOUS) == 0 // make sure an error symbol is returned
                    && (sym.kind & TYP) != 0))
                sym = new ErrorType(name, qualified?site.tsym:syms.noSymbol).tsym;
        }
        return sym;
    }

    Symbol access(Symbol sym,
                  DiagnosticPosition pos,
                  Type site,
                  Name name,
                  boolean qualified,
                  Type expected) {
        return access(sym, pos, site, name, qualified, expected.getParameterTypes(), expected.getTypeArguments());
    }
    /** Same as above, but without type arguments and arguments.
     */
// F3 change
    public
// F3 change
    Symbol access(Symbol sym,
                  DiagnosticPosition pos,
                  Type site,
                  Name name,
                  boolean qualified) {
        if (sym.kind >= AMBIGUOUS)
            return access(sym, pos, site, name, qualified, List.<Type>nil(), null);
        else
            return sym;
    }

    /** Check that sym is not an abstract method.
     */
    void checkNonAbstract(DiagnosticPosition pos, Symbol sym) {
        if ((sym.flags() & ABSTRACT) != 0)
            log.error(pos, MsgSym.MESSAGE_ABSTRACT_CANNOT_BE_ACCESSED_DIRECTLY,
                      kindName(sym), sym, sym.location());
    }

/* ***************************************************************************
 *  Debugging
 ****************************************************************************/

    /** print all scopes starting with scope s and proceeding outwards.
     *  used for debugging.
     */
    public void printscopes(Scope s) {
        while (s != null) {
            if (s.owner != null)
                System.err.print(s.owner + ": ");
            for (Scope.Entry e = s.elems; e != null; e = e.sibling) {
                if ((e.sym.flags() & ABSTRACT) != 0)
                    System.err.print("abstract ");
                System.err.print(e.sym + " ");
            }
            System.err.println();
            s = s.next;
        }
    }

    void printscopes(F3Env<F3AttrContext> env) {
        while (env.outer != null) {
            System.err.println("------------------------------");
            printscopes(env.info.scope);
            env = env.outer;
        }
    }

    public void printscopes(Type t) {
        while (t.tag == CLASS) {
            printscopes(t.tsym.members());
            t = types.supertype(t);
        }
    }

/* ***************************************************************************
 *  Name resolution
 *  Naming conventions are as for symbol lookup
 *  Unlike the find... methods these methods will report access errors
 ****************************************************************************/

    /** Resolve an unqualified (non-method) identifier.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the identifier use.
     *  @param name      The identifier's name.
     *  @param kind      The set of admissible symbol kinds for the identifier.
     *  @param pt        The expected type.
     */
    Symbol resolveIdent(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                        Name name, int kind, Type pt) {
        Symbol sym = findIdent(env, name, kind, pt);
        if (sym.kind >= AMBIGUOUS) {
            return access(sym, pos, env.enclClass.sym.type, name, false, pt);
        } else
            return sym;
    }

    /** Resolve a qualified method identifier
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the method invocation.
     *  @param site      The type of the qualifying expression, in which
     *                   identifier is searched.
     *  @param name      The identifier's name.
     *  @param argtypes  The types of the invocation's value arguments.
     *  @param typeargtypes  The types of the invocation's type arguments.
     */
    Symbol resolveQualifiedMethod(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                                  Type site, Name name, Type expected) {
        Symbol sym = findMember(env, site, name, expected, false,
                                env.info.varArgs=false, false);
        if (varargsEnabled && sym.kind >= WRONG_MTHS) {
            sym = findMember(env, site, name, expected, true,
                             false, false);
            if (sym.kind >= WRONG_MTHS)
                sym = findMember(env, site, name, expected, true,
                                 env.info.varArgs=true, false);
        }
        if (sym.kind >= AMBIGUOUS) {
            sym = access(sym, pos, site, name, true, expected);
        }
        return sym;
    }

    /** Resolve a qualified method identifier, throw a fatal error if not
     *  found.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the method invocation.
     *  @param site      The type of the qualifying expression, in which
     *                   identifier is searched.
     *  @param name      The identifier's name.
     *  @param argtypes  The types of the invocation's value arguments.
     *  @param typeargtypes  The types of the invocation's type arguments.
     */
    public MethodSymbol resolveInternalMethod(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                                        Type site, Name name,
                                        List<Type> argtypes,
                                        List<Type> typeargtypes) {
        Symbol sym = resolveQualifiedMethod(
            pos, env, site, name, newMethTemplate(argtypes, typeargtypes));
        if (sym.kind == MTH) return (MethodSymbol)sym;
        else throw new FatalError(
                 JCDiagnostic.fragment(MsgSym.MESSAGE_FATAL_ERR_CANNOT_LOCATE_METH,
                                name));
    }

    /** Resolve constructor.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the constructor invocation.
     *  @param site      The type of class for which a constructor is searched.
     *  @param argtypes  The types of the constructor invocation's value
     *                   arguments.
     *  @param typeargtypes  The types of the constructor invocation's type
     *                   arguments.
     */
    public // F3 change
    Symbol resolveConstructor(DiagnosticPosition pos,
                              F3Env<F3AttrContext> env,
                              Type site,
                              List<Type> argtypes,
                              List<Type> typeargtypes) {
        Symbol sym = resolveConstructor(pos, env, site, argtypes, typeargtypes, false, env.info.varArgs=false);
        if (varargsEnabled && sym.kind >= WRONG_MTHS) {
            sym = resolveConstructor(pos, env, site, argtypes, typeargtypes, true, false);
            if (sym.kind >= WRONG_MTHS)
                sym = resolveConstructor(pos, env, site, argtypes, typeargtypes, true, env.info.varArgs=true);
        }
        if (sym.kind >= AMBIGUOUS) {
            sym = access(sym, pos, site, names.init, true, argtypes, typeargtypes);
        }
        return sym;
    }

    /** Resolve constructor.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the constructor invocation.
     *  @param site      The type of class for which a constructor is searched.
     *  @param argtypes  The types of the constructor invocation's value
     *                   arguments.
     *  @param typeargtypes  The types of the constructor invocation's type
     *                   arguments.
     *  @param allowBoxing Allow boxing and varargs conversions.
     *  @param useVarargs Box trailing arguments into an array for varargs.
     */
    public // F3 change
    Symbol resolveConstructor(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                              Type site, List<Type> argtypes,
                              List<Type> typeargtypes,
                              boolean allowBoxing,
                              boolean useVarargs) {
        Symbol sym = findMethod(env, site,
                                names.init, argtypes,
                                typeargtypes, allowBoxing,
                                useVarargs, false);
        if ((sym.flags() & DEPRECATED) != 0 &&
            (env.info.scope.owner.flags() & DEPRECATED) == 0 &&
            env.info.scope.owner.outermostClass() != sym.outermostClass())
            chk.warnDeprecated(pos, sym);
        return sym;
    }

    /** Resolve a constructor, throw a fatal error if not found.
     *  @param pos       The position to use for error reporting.
     *  @param env       The environment current at the method invocation.
     *  @param site      The type to be constructed.
     *  @param argtypes  The types of the invocation's value arguments.
     *  @param typeargtypes  The types of the invocation's type arguments.
     */
    public MethodSymbol resolveInternalConstructor(DiagnosticPosition pos, F3Env<F3AttrContext> env,
                                        Type site,
                                        List<Type> argtypes,
                                        List<Type> typeargtypes) {
        Symbol sym = resolveConstructor(
            pos, env, site, argtypes, typeargtypes);
        if (sym.kind == MTH) return (MethodSymbol)sym;
        else throw new FatalError(
                 JCDiagnostic.fragment(MsgSym.MESSAGE_FATAL_ERR_CANNOT_LOCATE_CTOR, site));
    }

    /** Resolve operator.
     *  @param pos       The position to use for error reporting.
     *  @param optag     The tag of the operation tree.
     *  @param env       The environment current at the operation.
     *  @param argtypes  The types of the operands.
     */
    Symbol resolveOperator(DiagnosticPosition pos, F3Tag optag,
                          F3Env<F3AttrContext> env, List<Type> argtypes) {
	Symbol sym;
	Name name = treeinfo.operatorName(optag);
	if (false) {
	    sym = resolveOperator2(pos, optag, env, argtypes);
	    if (sym.kind != MTH) {
		sym = resolveOperator1(pos, optag, env, argtypes);
	    }
	} else {
            sym = findMethod(env, syms.predefClass.type, name, argtypes,
                             null, false, false, true);
	    if (boxingEnabled && sym.kind != MTH) {
		sym = findMethod(env, syms.predefClass.type, name, argtypes,
				 null, true, false, true);
	    }
	    Symbol defSym = sym;
	    sym = resolveOperator2(pos, optag, env, argtypes);
	    if (sym.kind != MTH) {
		sym = resolveOperator1(pos, optag, env, argtypes);
	    }
	    if (sym.kind != MTH) {
		sym = defSym;
	    }
	}
        return access(sym, pos, env.enclClass.sym.type, name,
                      false, argtypes, null);
    }

    Symbol resolveOperator1(DiagnosticPosition pos, F3Tag optag,
			    F3Env<F3AttrContext> env, List<Type> argtypes) {
        Name name = treeinfo.operatorName(optag);
        Symbol sym = findMethod(env, argtypes.head, name, argtypes.tail,
                                null, true, false, true);
	System.err.println("resolveOperator1: "+name+": "+argtypes+": "+sym);
        if (boxingEnabled && sym.kind >= WRONG_MTHS) {
	    /*
            sym = findMethod(env, env.enclClass.sym.type, name, argtypes,
                             null, true, false, true);
	    */
	    sym = findVar(env, name, MTH, 
			  newMethTemplate(argtypes, List.<Type>nil()),
			  true, false);
	    System.err.println("resolveOperator1.default: "+name+": "+argtypes+": "+sym);
	}
	
	return sym;
    }

    Symbol resolveOperator2(DiagnosticPosition pos, F3Tag optag,
                           F3Env<F3AttrContext> env, List<Type> argtypes) {
        Name name = treeinfo.operatorName2(optag);
        Symbol sym = findMethod(env, argtypes.head, name, argtypes.tail,
                                null, true, false, true);
	System.err.println("resolveOperator2: "+name+": "+argtypes+": "+sym);
        if (boxingEnabled && sym.kind != MTH) {
            //sym = findMethod(env, env.enclClass.sym.type, name, argtypes,
	    //null, true, false, true);
	    sym = findVar(env, name, MTH, 
			  newMethTemplate(argtypes, List.<Type>nil()),
			  true, false);
	    System.err.println("resolveOperator2.default: "+name+": "+argtypes+": "+sym);
	}
	return sym;
    }

    /** Resolve operator.
     *  @param pos       The position to use for error reporting.
     *  @param optag     The tag of the operation tree.
     *  @param env       The environment current at the operation.
     *  @param arg       The type of the operand.
     */
    Symbol resolveUnaryOperator(DiagnosticPosition pos, F3Tag optag, F3Env<F3AttrContext> env, Type arg) {
        // check for Duration unary minus
        if (true || types.isSameType(arg, syms.f3_DurationType)) {
            Symbol res = null;
            switch (optag) {
            case NEG:
                res = resolveMethod(pos,  env,
                                    defs.negate_DurationMethodName,
                                    arg, List.<Type>nil());
                if (res == null || res.kind != MTH) {
                    res = resolveMethod(pos,  env,
                                        names.fromString("-"),
                                        arg, List.<Type>nil());
                }
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            }
        }
        // check for Length unary minus
        if (types.isSameType(arg, syms.f3_LengthType)) {
            Symbol res = null;
            switch (optag) {
            case NEG:
                res = resolveMethod(pos,  env,
                                    defs.negate_LengthMethodName,
                                    arg, List.<Type>nil());
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            }
        }
        // check for Angle unary minus
        if (types.isSameType(arg, syms.f3_AngleType)) {
            Symbol res = null;
            switch (optag) {
            case NEG:
                res = resolveMethod(pos,  env,
                                    defs.negate_AngleMethodName,
                                    arg, List.<Type>nil());
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            }
        }
        // check for Color unary minus
        if (types.isSameType(arg, syms.f3_ColorType)) {
            Symbol res = null;
            switch (optag) {
            case NEG:
                res = resolveMethod(pos,  env,
                                    defs.negate_ColorMethodName,
                                    arg, List.<Type>nil());
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            }
        }
        return resolveOperator(pos, optag, env, List.of(arg));
    }

    /** Resolve binary operator.
     *  @param pos       The position to use for error reporting.
     *  @param optag     The tag of the operation tree.
     *  @param env       The environment current at the operation.
     *  @param left      The types of the left operand.
     *  @param right     The types of the right operand.
     */
    Symbol resolveBinaryOperator(DiagnosticPosition pos,
                                 F3Tag optag,
                                 F3Env<F3AttrContext> env,
                                 Type left,
                                 Type right) {
	if (left instanceof MethodType) {
	    left = syms.makeFunctionType(left.asMethodType());
	}
	if (right instanceof MethodType) {
	    right = syms.makeFunctionType(right.asMethodType());
	}
        // Duration operator overloading
        if (true ||(types.isSameType(left, syms.f3_DurationType) ||
                    types.isSameType(right, syms.f3_DurationType))) {
            Type dur = left;
            Symbol res = null;
            switch (optag) {
            case PLUS:
                res = resolveMethod(pos,  env,
				    defs.add_DurationMethodName,
				    dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("+"),
                                         dur,
                                         List.of(right));
                }
                break;
            case MINUS:
                res =  resolveMethod(pos,  env,
                                     defs.sub_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("-"),
                                         dur,
                                         List.of(right));
                }
                break;
            case MUL:
                /*
                if (!types.isSameType(left, syms.f3_DurationType)) {
                    left = right;
                    right = dur;
                    dur = left;
                }
                */
		//System.err.println("resolving * in "+dur);
		
                res =  resolveMethod(pos,  env,
                                     defs.mul_DurationMethodName,
                                     dur,
                                     List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("*"),
                                         dur,
                                         List.of(right));
                }
                break;
            case DIV:
                res =  resolveMethod(pos,  env,
                                     defs.div_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("/"),
                                         dur,
                                         List.of(right));
                }
                break;

            //fix me...inline or move to static helper?
            case LT:
                res =  resolveMethod(pos,  env,
                                     defs.lt_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("<"),
                                         dur,
                                         List.of(right));
                }
                break;
            case LE:
                res =  resolveMethod(pos,  env,
                                     defs.le_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString("<="),
                                         dur,
                                         List.of(right));
                }
                break;
            case GT:
                res =  resolveMethod(pos,  env,
                                     defs.gt_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString(">"),
                                         dur,
                                         List.of(right));
                }
                break;
            case GE:
                res =  resolveMethod(pos,  env,
                                     defs.ge_DurationMethodName,
                                     dur, List.of(right));
                if (res == null || res.kind != MTH) {
                    res =  resolveMethod(pos,  env,
                                         names.fromString(">="),
                                         dur,
                                         List.of(right));
                }
                break;
            }
            //System.out.println("resolve bin: "+left+" "+ right+": "+ res);
            if (res != null && res.kind == MTH) {
                return res;
            } // else fall through
        }
        // Length operator overloading
        if (types.isSameType(left, syms.f3_LengthType) ||
            types.isSameType(right, syms.f3_LengthType)) {
            Type dur = left;
            Symbol res = null;
            switch (optag) {
            case PLUS:
                res = resolveMethod(pos,  env,
                                     defs.add_LengthMethodName,
                                     dur, List.of(right));
                break;
            case MINUS:
                res =  resolveMethod(pos,  env,
                                     defs.sub_LengthMethodName,
                                     dur, List.of(right));
                break;
            case MUL:
                if (!types.isSameType(left, syms.f3_LengthType)) {
                    left = right;
                    right = dur;
                    dur = left;
                }
                res =  resolveMethod(pos,  env,
                                     defs.mul_LengthMethodName,
                                     dur,
                                     List.of(right));
                break;
            case DIV:
                res =  resolveMethod(pos,  env,
                                     defs.div_LengthMethodName,
                                     dur, List.of(right));
                break;

            //fix me...inline or move to static helper?
            case LT:
                res =  resolveMethod(pos,  env,
                                     defs.lt_LengthMethodName,
                                     dur, List.of(right));
                break;
            case LE:
                res =  resolveMethod(pos,  env,
                                     defs.le_LengthMethodName,
                                     dur, List.of(right));
                break;
            case GT:
                res =  resolveMethod(pos,  env,
                                     defs.gt_LengthMethodName,
                                     dur, List.of(right));
                break;
            case GE:
                res =  resolveMethod(pos,  env,
                                     defs.ge_LengthMethodName,
                                     dur, List.of(right));
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            } // else fall through
        }
        // Angle operator overloading
        if (types.isSameType(left, syms.f3_AngleType) ||
            types.isSameType(right, syms.f3_AngleType)) {
            Type dur = left;
            Symbol res = null;
            switch (optag) {
            case PLUS:
                res = resolveMethod(pos,  env,
                                     defs.add_AngleMethodName,
                                     dur, List.of(right));
                break;
            case MINUS:
                res =  resolveMethod(pos,  env,
                                     defs.sub_AngleMethodName,
                                     dur, List.of(right));
                break;
            case MUL:
                if (!types.isSameType(left, syms.f3_AngleType)) {
                    left = right;
                    right = dur;
                    dur = left;
                }
                res =  resolveMethod(pos,  env,
                                     defs.mul_AngleMethodName,
                                     dur,
                                     List.of(right));
                break;
            case DIV:
                res =  resolveMethod(pos,  env,
                                     defs.div_AngleMethodName,
                                     dur, List.of(right));
                break;

            //fix me...inline or move to static helper?
            case LT:
                res =  resolveMethod(pos,  env,
                                     defs.lt_AngleMethodName,
                                     dur, List.of(right));
                break;
            case LE:
                res =  resolveMethod(pos,  env,
                                     defs.le_AngleMethodName,
                                     dur, List.of(right));
                break;
            case GT:
                res =  resolveMethod(pos,  env,
                                     defs.gt_AngleMethodName,
                                     dur, List.of(right));
                break;
            case GE:
                res =  resolveMethod(pos,  env,
                                     defs.ge_AngleMethodName,
                                     dur, List.of(right));
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            } // else fall through
        }
        // Color operator overloading
        if (types.isSameType(left, syms.f3_ColorType) ||
            types.isSameType(right, syms.f3_ColorType)) {
            Type dur = left;
            Symbol res = null;
            switch (optag) {
            case PLUS:
                res = resolveMethod(pos,  env,
                                     defs.add_ColorMethodName,
                                     dur, List.of(right));
                break;
            case MINUS:
                res =  resolveMethod(pos,  env,
                                     defs.sub_ColorMethodName,
                                     dur, List.of(right));
                break;
            case MUL:
                if (!types.isSameType(left, syms.f3_ColorType)) {
                    left = right;
                    right = dur;
                    dur = left;
                }
                res =  resolveMethod(pos,  env,
                                     defs.mul_ColorMethodName,
                                     dur,
                                     List.of(right));
                break;
            case DIV:
                res =  resolveMethod(pos,  env,
                                     defs.div_ColorMethodName,
                                     dur, List.of(right));
                break;

            //fix me...inline or move to static helper?
            case LT:
                res =  resolveMethod(pos,  env,
                                     defs.lt_ColorMethodName,
                                     dur, List.of(right));
                break;
            case LE:
                res =  resolveMethod(pos,  env,
                                     defs.le_ColorMethodName,
                                     dur, List.of(right));
                break;
            case GT:
                res =  resolveMethod(pos,  env,
                                     defs.gt_ColorMethodName,
                                     dur, List.of(right));
                break;
            case GE:
                res =  resolveMethod(pos,  env,
                                     defs.ge_ColorMethodName,
                                     dur, List.of(right));
                break;
            }
            if (res != null && res.kind == MTH) {
                return res;
            } // else fall through
        }
        return resolveOperator(pos, optag, env, List.of(left, right));
    }

    Symbol resolveMethod(DiagnosticPosition pos,
                         F3Env<F3AttrContext> env, 
                         Name name,
                         Type type,
                         List<Type> argtypes) {
        Symbol sym = findMethod(env, type, name, argtypes,
                                null, true, false, false);
        if (sym.kind == MTH) { // skip access if method wasn't found
            return access(sym, pos, env.enclClass.sym.type, name,
                          false, argtypes, null);
        }
        return sym;
    }

    /**
     * Resolve `c.name' where name == this or name == super.
     * @param pos           The position to use for error reporting.
     * @param env           The environment current at the expression.
     * @param c             The qualifier.
     * @param name          The identifier's name.
     */
    public // F3 change
    Symbol resolveSelf(DiagnosticPosition pos,
                       F3Env<F3AttrContext> env,
                       TypeSymbol c,
                       Name name) {
        F3Env<F3AttrContext> env1 = env;
        boolean staticOnly = false;
        while (env1.outer != null) {
            if (isStatic(env1)) staticOnly = true;
            if (env1.enclClass.sym == c) {
                Symbol sym = env1.info.scope.lookup(name).sym;
                if (sym != null) {
                    if (staticOnly) sym = new StaticError(sym);
                    return access(sym, pos, env.enclClass.sym.type,
                                  name, true);
                }
            }
            if ((env1.enclClass.sym.flags() & STATIC) != 0) staticOnly = true;
            env1 = env1.outer;
        }
        log.error(pos, MsgSym.MESSAGE_NOT_ENCL_CLASS, c);
        return syms.errSymbol;
    }

    /**
     * Resolve `c.this' for an enclosing class c that contains the
     * named member.
     * @param pos           The position to use for error reporting.
     * @param env           The environment current at the expression.
     * @param member        The member that must be contained in the result.
     */
    Symbol resolveSelfContaining(DiagnosticPosition pos,
                                 F3Env<F3AttrContext> env,
                                 Symbol member) {
        Name name = names._this;
        F3Env<F3AttrContext> env1 = env;
        boolean staticOnly = false;
        while (env1.outer != null) {
            if (isStatic(env1)) staticOnly = true;
            if (env1.enclClass.sym.isSubClass(member.owner, types) &&
                isAccessible(env, env1.enclClass.sym.type, member)) {
                Symbol sym = env1.info.scope.lookup(name).sym;
                if (sym != null) {
                    if (staticOnly) sym = new StaticError(sym);
                    return access(sym, pos, env.enclClass.sym.type,
                                  name, true);
                }
            }
            if ((env1.enclClass.sym.flags() & STATIC) != 0)
                staticOnly = true;
            env1 = env1.outer;
        }
        log.error(pos, MsgSym.MESSAGE_ENCL_CLASS_REQUIRED, member);
        return syms.errSymbol;
    }

    /**
     * Resolve an appropriate implicit this instance for t's container.
     * JLS2 8.8.5.1 and 15.9.2
     */
    public // F3 change
    Type resolveImplicitThis(DiagnosticPosition pos, F3Env<F3AttrContext> env, Type t) {
        Type thisType = (((t.tsym.owner.kind & (MTH|VAR)) != 0)
                         ? resolveSelf(pos, env, t.getEnclosingType().tsym, names._this)
                         : resolveSelfContaining(pos, env, t.tsym)).type;
        if (env.info.isSelfCall && thisType.tsym == env.enclClass.sym)
            log.error(pos, MsgSym.MESSAGE_CANNOT_REF_BEFORE_CTOR_CALLED, "this");
        return thisType;
    }

/* ***************************************************************************
 *  Methods related to kinds
 ****************************************************************************/

    /** A localized string describing a given kind.
     */
    public // F3 change
    JCDiagnostic kindName(int kind) {
        switch (kind) {
        case PCK: return JCDiagnostic.fragment(MsgSym.KINDNAME_PACKAGE);
        case TYP: return JCDiagnostic.fragment(MsgSym.KINDNAME_CLASS);
        case VAR: return JCDiagnostic.fragment(MsgSym.KINDNAME_VARIABLE);
        case VAL: return JCDiagnostic.fragment(MsgSym.KINDNAME_VALUE);
        case MTH: return diags.fragment(MsgSym.MESSAGE_F3_KINDNAME_FUNCTION);
        default : return JCDiagnostic.fragment(MsgSym.KINDNAME,
                                               Integer.toString(kind)); //debug
        }
    }

    JCDiagnostic kindName(Symbol sym) {
        switch (sym.getKind()) {
        case PACKAGE:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_PACKAGE);

        case ENUM:
        case ANNOTATION_TYPE:
        case INTERFACE:
        case CLASS:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_CLASS);

        case TYPE_PARAMETER:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_TYPE_VARIABLE);

        case ENUM_CONSTANT:
        case FIELD:
        case PARAMETER:
        case LOCAL_VARIABLE:
        case EXCEPTION_PARAMETER:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_VARIABLE);

        case METHOD:
        case CONSTRUCTOR:
        case STATIC_INIT:
        case INSTANCE_INIT:
            return diags.fragment(MsgSym.MESSAGE_F3_KINDNAME_FUNCTION);

        default:
            if (sym.kind == VAL)
                // I don't think this can happen but it can't harm
                // playing it safe --ahe
                return JCDiagnostic.fragment(MsgSym.KINDNAME_VALUE);
            else
                return JCDiagnostic.fragment(MsgSym.KINDNAME, sym.getKind()); // debug
        }
    }

    /** A localized string describing a given set of kinds.
     */
    public // F3 change
    static JCDiagnostic kindNames(int kind) {
        StringBuffer key = new StringBuffer();
        key.append(MsgSym.KINDNAME);
        if ((kind & VAL) != 0)
            key.append(((kind & VAL) == VAR) ? MsgSym.KINDNAME_KEY_VARIABLE : MsgSym.KINDNAME_KEY_VALUE);
        if ((kind & MTH) != 0) key.append(MsgSym.KINDNAME_KEY_METHOD);
        if ((kind & TYP) != 0) key.append(MsgSym.KINDNAME_KEY_CLASS);
        if ((kind & PCK) != 0) key.append(MsgSym.KINDNAME_KEY_PACKAGE);
        return JCDiagnostic.fragment(key.toString(), kind);
    }

    /** A localized string describing the kind -- either class or interface --
     *  of a given type.
     */
    static JCDiagnostic typeKindName(Type t) {
        if (t.tag == TYPEVAR ||
            t.tag == CLASS && (t.tsym.flags() & COMPOUND) != 0)
            return JCDiagnostic.fragment(MsgSym.KINDNAME_TYPE_VARIABLE_BOUND);
        else if (t.tag == PACKAGE)
            return JCDiagnostic.fragment(MsgSym.KINDNAME_PACKAGE);
        else if ((t.tsym.flags_field & ANNOTATION) != 0)
            return JCDiagnostic.fragment(MsgSym.KINDNAME_ANNOTATION);
        else if ((t.tsym.flags_field & INTERFACE) != 0)
            return JCDiagnostic.fragment(MsgSym.KINDNAME_INTERFACE);
        else
            return JCDiagnostic.fragment(MsgSym.KINDNAME_CLASS);
    }

    /** A localized string describing the kind of a missing symbol, given an
     *  error kind.
     */
    JCDiagnostic absentKindName(int kind) {
        switch (kind) {
        case ABSENT_VAR:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_VARIABLE);
        case WRONG_MTHS: case WRONG_MTH: case ABSENT_MTH:
            return diags.fragment(MsgSym.MESSAGE_F3_KINDNAME_FUNCTION);
        case ABSENT_TYP:
            return JCDiagnostic.fragment(MsgSym.KINDNAME_CLASS);
        default:
            return JCDiagnostic.fragment(MsgSym.KINDNAME, kind);
        }
    }

/* ***************************************************************************
 *  ResolveError classes, indicating error situations when accessing symbols
 ****************************************************************************/

    public void logAccessError(F3Env<F3AttrContext> env, JCTree tree, Type type) {
        AccessError error = new AccessError(env, type.getEnclosingType(), type.tsym);
        error.report(log, tree.pos(), type.getEnclosingType(), null, null, null);
    }

    /** Root class for resolve errors.
     *  Instances of this class indicate "Symbol not found".
     *  Instances of subclass indicate other errors.
     */
    private class ResolveError extends Symbol {

        ResolveError(int kind, Symbol sym, String debugName) {
            super(kind, 0, null, null, null);
            this.debugName = debugName;
            this.sym = sym;
        }

        /** The name of the kind of error, for debugging only.
         */
        final String debugName;

        /** The symbol that was determined by resolution, or errSymbol if none
         *  was found.
         */
        final Symbol sym;

        /** The symbol that was a close mismatch, or null if none was found.
         *  wrongSym is currently set if a simgle method with the correct name, but
         *  the wrong parameters was found.
         */
        Symbol wrongSym;

        /** An auxiliary explanation set in case of instantiation errors.
         */
        JCDiagnostic explanation;


        public <R, P> R accept(ElementVisitor<R, P> v, P p) {
            throw new AssertionError();
        }

        /** Print the (debug only) name of the kind of error.
         */
        @Override
        public String toString() {
            return debugName + " wrongSym=" + wrongSym + " explanation=" + explanation;
        }

        /** Update wrongSym and explanation and return this.
         */
        ResolveError setWrongSym(Symbol sym, JCDiagnostic explanation) {
            this.wrongSym = sym;
            this.explanation = explanation;
            return this;
        }

        /** Update wrongSym and return this.
         */
        ResolveError setWrongSym(Symbol sym) {
            this.wrongSym = sym;
            this.explanation = null;
            return this;
        }

        @Override
        public boolean exists() {
            switch (kind) {
            case HIDDEN:
            case ABSENT_VAR:
            case ABSENT_MTH:
            case ABSENT_TYP:
                return false;
            default:
                return true;
            }
        }

        /** Report error.
         *  @param log       The error log to be used for error reporting.
         *  @param pos       The position to be used for error reporting.
         *  @param site      The original type from where the selection took place.
         *  @param name      The name of the symbol to be resolved.
         *  @param argtypes  The invocation's value arguments,
         *                   if we looked for a method.
         *  @param typeargtypes  The invocation's type arguments,
         *                   if we looked for a method.
         */
        void report(Log log, DiagnosticPosition pos, Type site, Name name,
                    List<Type> argtypes, List<Type> typeargtypes) {
            if (name == null) {
                log.error(pos, MsgSym.MESSAGE_F3_NO_DEFAULT_DECLARED, site);
            } else if (name != name.table.error) {
                JCDiagnostic kindname = absentKindName(kind);
                String idname = name.toString();
                String args = "";
                String typeargs = "";
                if (kind >= WRONG_MTHS && kind <= ABSENT_MTH) {
                    if (isOperator(name)) {
                        log.error(pos, MsgSym.MESSAGE_OPERATOR_CANNOT_BE_APPLIED,
                                  name, types.toF3String(argtypes));
                        return;
                    }
                    if (name == name.table.init) {
                        kindname = JCDiagnostic.fragment(MsgSym.KINDNAME_CONSTRUCTOR);
                        idname = site.tsym.name.toString();
                    }
                    args = "(" + types.toF3String(argtypes) + ")";
                    if (typeargtypes != null && typeargtypes.nonEmpty())
                        typeargs = "of (" + types.toF3String(typeargtypes) + ")";
                }
                if (kind == WRONG_MTH) {
                    String wrongSymStr;
                    if (wrongSym instanceof MethodSymbol)
                        wrongSymStr =
                                types.toF3String((MethodSymbol) wrongSym.asMemberOf(site, types),
                                    ((MethodSymbol) wrongSym).params);
                    else
                        wrongSymStr = wrongSym.toString();
		    System.err.println("wrongSym="+wrongSym.getClass() + wrongSymStr);
		    Thread.currentThread().dumpStack();
                    log.error(pos,
                              MsgSym.MESSAGE_CANNOT_APPLY_SYMBOL + (explanation != null ? ".1" : ""),
                              wrongSymStr,
                              types.location(wrongSym, site),
                              typeargs,
                              types.toF3String(argtypes),
                              explanation);
                } else if (site.tsym.name.len != 0) {
                    if (site.tsym.kind == PCK && !site.tsym.exists())
                        log.error(pos, MsgSym.MESSAGE_DOES_NOT_EXIST, site.tsym);
                    else
                        log.error(pos, MsgSym.MESSAGE_CANNOT_RESOLVE_LOCATION,
                                  kindname, idname, args, typeargs,
                                  typeKindName(site), types.toF3String(site));
                } else {
                    log.error(pos, MsgSym.MESSAGE_CANNOT_RESOLVE, kindname, idname, args, typeargs);
                }
            }
        }
//where
            /** A name designates an operator if it consists
             *  of a non-empty sequence of operator symbols +-~!/*%&|^<>=
             */
            boolean isOperator(Name name) {
                int i = 0;
                while (i < name.len &&
                       "+-~!*/%&|^<>=".indexOf(name.byteAt(i)) >= 0) i++;
                return i > 0 && i == name.len;
            }
    }

    /** Resolve error class indicating that a symbol is not accessible.
     */
    class AccessError extends ResolveError {

        AccessError(Symbol sym) {
            this(null, null, sym);
        }

        AccessError(F3Env<F3AttrContext> env, Type site, Symbol sym) {
            super(HIDDEN, sym, "access error");
            this.env = env;
            this.site = site;
            if (debugResolve)
                log.error(MsgSym.MESSAGE_PROC_MESSAGER, sym + " @ " + site + " is inaccessible.");
        }

        private F3Env<F3AttrContext> env;
        private Type site;

        /** Report error.
         *  @param log       The error log to be used for error reporting.
         *  @param pos       The position to be used for error reporting.
         *  @param site      The original type from where the selection took place.
         *  @param name      The name of the symbol to be resolved.
         *  @param argtypes  The invocation's value arguments,
         *                   if we looked for a method.
         *  @param typeargtypes  The invocation's type arguments,
         *                   if we looked for a method.
         */
        @Override
        void report(Log log, DiagnosticPosition pos, Type site, Name name,
                    List<Type> argtypes, List<Type> typeargtypes) {
            if (sym.owner.type.tag != ERROR) {
                if (sym.name == sym.name.table.init && sym.owner != site.tsym)
                    new ResolveError(ABSENT_MTH, sym.owner, "absent method " + sym).report(
                        log, pos, site, name, argtypes, typeargtypes);
                if ((sym.flags() & PUBLIC) != 0
                    || (env != null && this.site != null
                        && !isAccessible(env, this.site)))
                    log.error(pos, MsgSym.MESSAGE_NOT_DEF_ACCESS_CLASS_INTF_CANNOT_ACCESS,
                        sym, sym.location());
                else if ((sym.flags() & F3Flags.F3AccessFlags) == 0L) // 'package' access
                    log.error(pos, MsgSym.MESSAGE_NOT_DEF_PUBLIC_CANNOT_ACCESS,
                              sym, sym.location());
                else
                    log.error(pos, MsgSym.MESSAGE_REPORT_ACCESS, sym,
                              F3Check.protectionString(sym.flags()),
                              sym.location());
            }
        }
    }

    /** Resolve error class indicating that an instance member was accessed
     *  from a static context.
     */
    class StaticError extends ResolveError {
        StaticError(Symbol sym) {
            super(STATICERR, sym, "static error");
        }

        /** Report error.
         *  @param log       The error log to be used for error reporting.
         *  @param pos       The position to be used for error reporting.
         *  @param site      The original type from where the selection took place.
         *  @param name      The name of the symbol to be resolved.
         *  @param argtypes  The invocation's value arguments,
         *                   if we looked for a method.
         *  @param typeargtypes  The invocation's type arguments,
         *                   if we looked for a method.
         */
        @Override
        void report(Log log,
                    DiagnosticPosition pos,
                    Type site,
                    Name name,
                    List<Type> argtypes,
                    List<Type> typeargtypes) {
            String symstr = ((sym.kind == TYP && sym.type.tag == CLASS)
                ? types.erasure(sym.type)
                : sym).toString();
            log.error(pos, MsgSym.MESSAGE_NON_STATIC_CANNOT_BE_REF,
                      kindName(sym), symstr);
        }
    }

    /** Resolve error class indicating an ambiguous reference.
     */
    class AmbiguityError extends ResolveError {
        Symbol sym1;
        Symbol sym2;

        AmbiguityError(Symbol sym1, Symbol sym2) {
            super(AMBIGUOUS, sym1, "ambiguity error");
            this.sym1 = sym1;
            this.sym2 = sym2;
        }

        /** Report error.
         *  @param log       The error log to be used for error reporting.
         *  @param pos       The position to be used for error reporting.
         *  @param site      The original type from where the selection took place.
         *  @param name      The name of the symbol to be resolved.
         *  @param argtypes  The invocation's value arguments,
         *                   if we looked for a method.
         *  @param typeargtypes  The invocation's type arguments,
         *                   if we looked for a method.
         */
        @Override
        void report(Log log, DiagnosticPosition pos, Type site, Name name,
                    List<Type> argtypes, List<Type> typeargtypes) {
            AmbiguityError pair = this;
            while (true) {
                if (pair.sym1.kind == AMBIGUOUS)
                    pair = (AmbiguityError)pair.sym1;
                else if (pair.sym2.kind == AMBIGUOUS)
                    pair = (AmbiguityError)pair.sym2;
                else break;
            }
            Name sname = pair.sym1.name;
            if (sname == sname.table.init) sname = pair.sym1.owner.name;
            log.error(pos, MsgSym.MESSAGE_REF_AMBIGUOUS, sname,
                      kindName(pair.sym1),
                      pair.sym1,
                      types.location(pair.sym1, site),
                      kindName(pair.sym2),
                      pair.sym2,
                      types.location(pair.sym2, site));
        }
    }

    /**
     *  @param sym    The symbol.
     *  @param clazz  The type symbol of which the tested symbol is regarded
     *                as a member.
     *
     * From the javac code from which this was cloned --
     *
     * Is this symbol inherited into a given class?
     *  PRE: If symbol's owner is a interface,
     *       it is already assumed that the interface is a superinterface
     *       of given class.
     *  @param clazz  The class for which we want to establish membership.
     *                This must be a subclass of the member's owner.
     */
    public boolean isInheritedIn(Symbol sym, Symbol clazz, F3Types types) {
        // because the SCRIPT_PRIVATE bit is too high for the switch, test it later
        switch ((short)(sym.flags_field & Flags.AccessFlags)) {
        default: // error recovery
        case PUBLIC:
            return true;
        case PRIVATE:
            return sym.owner == clazz;
        case PROTECTED:
            // we model interfaces as extending Object
            return (clazz.flags() & INTERFACE) == 0;
        case 0:
            if ((sym.flags() & F3Flags.SCRIPT_PRIVATE) != 0) {
                // script-private
                //TODO: this isn't right
                //return sym.owner == clazz;
            };
            // 'package' access
            boolean foundInherited = false;
            for (Type supType : types.supertypesClosure(clazz.type, true)) {
                if (supType.tsym == sym.owner) {
                    foundInherited = true;
                    break;
                }
                else if (supType.isErroneous()) {
                    return true; // Error recovery
                }
                else if (supType.tsym != null && (supType.tsym.flags() & COMPOUND) != 0) {
                    continue;
                }
            }
            return foundInherited && (clazz.flags() & INTERFACE) == 0;
        }
    }
}
