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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Lint.LintCategory;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type.ClassType;
import com.sun.tools.mjavac.code.Type.ErrorType;
import com.sun.tools.mjavac.code.Type.ForAll;
import com.sun.tools.mjavac.code.Type.TypeVar;
import com.sun.tools.mjavac.code.Type.MethodType;

import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.TypeTags.*;
import static com.sun.tools.mjavac.code.TypeTags.WILDCARD;

import com.sun.tools.mjavac.comp.Infer;
import com.sun.tools.mjavac.jvm.ByteCodes;
import com.sun.tools.mjavac.jvm.ClassReader;
import com.sun.tools.mjavac.jvm.Target;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;

import org.f3.api.F3BindStatus;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.FunctionType;
import org.f3.tools.comp.F3Attr.Sequenceness;
import org.f3.tools.tree.*;
import org.f3.tools.tree.F3TreeScanner;
import org.f3.tools.util.MsgSym;

import static org.f3.tools.code.F3Flags.*;

/** Type checking helper class for the attribution phase.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3Check {
    protected static final Context.Key<F3Check> f3CheckKey =
	new Context.Key<F3Check>();

    private final F3Defs defs;
    private final Name.Table names;
    private final Log log;
    private final JCDiagnostic.Factory diags;
    private final Messages messages;
    private final Options options;
    private final F3Symtab syms;
    private final Infer infer;
    private final Target target;
    private final Source source;
    private final F3Types types;
    private final F3Attr attr;
    private final F3TreeInfo treeinfo;
    private final F3Resolve rs;

    // The set of lint options currently in effect. It is initialized
    // from the context, and then is set/reset as needed by Attr as it 
    // visits all the various parts of the trees during attribution.
    private Lint lint;

    enum WriteKind {
        ASSIGN,
        INIT_NON_BIND,
        INIT_BIND,
        VAR_QUERY
    }

    public static F3Check instance(Context context) {
        F3Check instance = context.get(f3CheckKey);
        if (instance == null)
            instance = new F3Check(context);
        return instance;
    }

    public static void preRegister(final Context context) {
        context.put(f3CheckKey, new Context.Factory<F3Check>() {
	       public F3Check make() {
		   return new F3Check(context);
	       }
        });
    }

    protected F3Check(Context context) {
        context.put(f3CheckKey, this);

        defs = F3Defs.instance(context);
        names = Name.Table.instance(context);
        log = Log.instance(context);
        diags = JCDiagnostic.Factory.instance(context);
        messages = Messages.instance(context);
        syms = (F3Symtab) Symtab.instance(context);
        infer = Infer.instance(context);
        types = F3Types.instance(context);
        attr = F3Attr.instance(context);
        options = Options.instance(context);
        target = Target.instance(context);
            source = Source.instance(context);
        lint = Lint.instance(context);
            treeinfo = (F3TreeInfo)F3TreeInfo.instance(context);

        allowGenerics = source.allowGenerics();
        //allowAnnotations = source.allowAnnotations();
        complexInference = options.get("-complexinference") != null;

        boolean verboseDeprecated = lint.isEnabled(LintCategory.DEPRECATION);
        boolean verboseUnchecked = lint.isEnabled(LintCategory.UNCHECKED);

        deprecationHandler = new MandatoryWarningHandler(log,verboseDeprecated, MsgSym.MESSAGEPREFIX_DEPRECATED);
        warnOnUsePackageHandler = new MandatoryWarningHandler(log, true, MsgSym.MESSAGEPREFIX_WARNONUSE);
        uncheckedHandler = new MandatoryWarningHandler(log, verboseUnchecked, MsgSym.MESSAGEPREFIX_UNCHECKED);
        rs = F3Resolve.instance(context);
    }


    /** Switch: generics enabled?
     */
    boolean allowGenerics;

    /** Switch: annotations enabled?
     */
    //boolean allowAnnotations;

    /** Switch: -complexinference option set?
     */
    boolean complexInference;

    /** A table mapping flat names of all compiled classes in this run to their
     *  symbols; maintained from outside.
     */
    public Map<Name,ClassSymbol> compiled = new HashMap<Name, ClassSymbol>();

    /** A handler for messages about deprecated usage.
     */
    private MandatoryWarningHandler deprecationHandler;


    /** A handler for messages about -XDwarnOnUse package usage.
     */
    private MandatoryWarningHandler warnOnUsePackageHandler;


    /** A handler for messages about unchecked or unsafe usage.
     */
    private MandatoryWarningHandler uncheckedHandler;


/* *************************************************************************
 * Errors and Warnings
 **************************************************************************/
    Lint setLint(Lint newLint) {
	Lint prev = lint;
	lint = newLint;
	return prev;
    }

    /** Warn about deprecated symbol.
     *  @param pos        Position to be used for error reporting.
     *  @param sym        The deprecated symbol.
     */ 
    void warnDeprecated(DiagnosticPosition pos, Symbol sym) {
	if (!lint.isSuppressed(LintCategory.DEPRECATION))
	    deprecationHandler.report(pos, MsgSym.MESSAGE_HAS_BEEN_DEPRECATED, sym, sym.location());
    }

    /** Warn about a -XDwarnOnUse package symbol.
     *  @param pos        Position to be used for error reporting.
     *  @param sym        The deprecated symbol.
     */
     void warnWarnOnUsePackage(DiagnosticPosition pos, Symbol sym) {
         warnOnUsePackageHandler.report(pos, MsgSym.MESSAGE_F3_WARN_ON_USE_PACKAGE, sym);
     }

    /** Warn about unchecked operation.
     *  @param pos        Position to be used for error reporting.
     *  @param msg        A string describing the problem.
     */
    public void warnUnchecked(DiagnosticPosition pos, String msg, Object... args) {
	if (!lint.isSuppressed(LintCategory.UNCHECKED))
	    uncheckedHandler.report(pos, msg, args);
    }

    /**
     * Report any deferred diagnostics.
     */
    public void reportDeferredDiagnostics() {
	deprecationHandler.reportDeferredDiagnostic();
    warnOnUsePackageHandler.reportDeferredDiagnostic();
	uncheckedHandler.reportDeferredDiagnostic();
    }


    /** Report a failure to complete a class.
     *  @param pos        Position to be used for error reporting.
     *  @param ex         The failure to report.
     */
    public Type completionError(DiagnosticPosition pos, CompletionFailure ex) {
	log.error(pos, MsgSym.MESSAGE_CANNOT_ACCESS, ex.sym, ex.errmsg);
	if (ex instanceof ClassReader.BadClassFile) throw new Abort();
	else return syms.errType;
    }

    /** Report a type error.
     *  @param pos        Position to be used for error reporting.
     *  @param problem    A string describing the error.
     *  @param found      The type that was found.
     *  @param req        The type that was required.
     */
    Type typeError(DiagnosticPosition pos, Object problem, Type found, Type req) {
        String foundAsF3Type = types.toF3String(found);
        String requiredAsF3Type = types.toF3String(req);
	log.error(pos, MsgSym.MESSAGE_PROB_FOUND_REQ, problem, foundAsF3Type, requiredAsF3Type);
	return syms.errType;
    }

    Type typeError(DiagnosticPosition pos, Object problem, Object found, Object req) {
        Object requiredAsF3Type = req;
        if (req instanceof Type) {
            requiredAsF3Type = types.toF3String((Type) requiredAsF3Type);
        }
        Object foundAsF3Type = found;
        if (foundAsF3Type instanceof Type) {
            foundAsF3Type = types.toF3String((Type) foundAsF3Type);
        }
        log.error(pos, MsgSym.MESSAGE_PROB_FOUND_REQ, problem, foundAsF3Type, requiredAsF3Type);
        return syms.errType;
    }

    Type typeError(DiagnosticPosition pos, String problem, Type found, Type req, Object explanation) {
        String foundAsF3Type = types.toF3String(found);
        String requiredAsF3Type = types.toF3String(req);
	log.error(pos, MsgSym.MESSAGE_PROB_FOUND_REQ_1, problem, foundAsF3Type, requiredAsF3Type, explanation);
	return syms.errType;
    }

    /** Report an error that wrong type tag was found.
     *  @param pos        Position to be used for error reporting.
     *  @param required   An internationalized string describing the type tag
     *                    required.
     *  @param found      The type that was found.
     */
    Type typeTagError(DiagnosticPosition pos, Object required, Object found) {
        Object requiredAsF3Type = required;
        if (required instanceof Type) {
            requiredAsF3Type = types.toF3String((Type) requiredAsF3Type);
        }
        Object foundAsF3Type = found;
        if (foundAsF3Type instanceof Type) {
            foundAsF3Type = types.toF3String((Type) foundAsF3Type);
        }
	log.error(pos, MsgSym.MESSAGE_TYPE_FOUND_REQ, foundAsF3Type, requiredAsF3Type);
	return syms.errType;
    }

    /** Report an error that symbol cannot be referenced before super
     *  has been called.
     *  @param pos        Position to be used for error reporting.
     *  @param sym        The referenced symbol.
     */
    void earlyRefError(DiagnosticPosition pos, Symbol sym) {
	log.error(pos, MsgSym.MESSAGE_CANNOT_REF_BEFORE_CTOR_CALLED, sym);
    }

    /** Report duplicate declaration error.
     */
    void duplicateError(DiagnosticPosition pos, Symbol sym) {
	if (sym.type == null || !sym.type.isErroneous()) {
	    log.error(pos, MsgSym.MESSAGE_ALREADY_DEFINED, sym, types.location(sym));
	}
    }

    /** Report array/varargs duplicate declaration 
     */
    void varargsDuplicateError(DiagnosticPosition pos, Symbol sym1, Symbol sym2) {
	if (!sym1.type.isErroneous() && !sym2.type.isErroneous()) {
	    log.error(pos, MsgSym.MESSAGE_ARRAY_AND_VARARGS, sym1, sym2, sym2.location());
	}
    }

/* ************************************************************************
 * duplicate declaration checking
 *************************************************************************/

    /** Check that variable does not hide variable with same name in
     *	immediately enclosing local scope.
     *	@param pos	     Position for error reporting.
     *	@param v	     The symbol.
     *	@param s	     The scope.
     */
    void checkTransparentVar(DiagnosticPosition pos, F3VarSymbol v, Scope s) {
	if (s.next != null) {
	    for (Scope.Entry e = s.next.lookup(v.name);
		 e.scope != null && e.sym.owner == v.owner;
		 e = e.next()) {
		if (e.sym.kind == VAR &&
		    (e.sym.owner.kind & (VAR | MTH)) != 0 &&
		    v.name != names.error) {
		    duplicateError(pos, e.sym);
		    return;
		}
	    }
	}
    }

    /** Check that a class or interface does not hide a class or
     *	interface with same name in immediately enclosing local scope.
     *	@param pos	     Position for error reporting.
     *	@param c	     The symbol.
     *	@param s	     The scope.
     */
    void checkTransparentClass(DiagnosticPosition pos, ClassSymbol c, Scope s) {
	if (s.next != null) {
	    for (Scope.Entry e = s.next.lookup(c.name);
		 e.scope != null && e.sym.owner == c.owner;
		 e = e.next()) {
		if (e.sym.kind == TYP &&
		    (e.sym.owner.kind & (VAR | MTH)) != 0 &&
		    c.name != names.error) {
		    duplicateError(pos, e.sym);
		    return;
		}
	    }
	}
    }

    /** Check that class does not have the same name as one of
     *	its enclosing classes, or as a class defined in its enclosing scope.
     *	return true if class is unique in its enclosing scope.
     *	@param pos	     Position for error reporting.
     *	@param name	     The class name.
     *	@param s	     The enclosing scope.
     */
    boolean checkUniqueClassName(DiagnosticPosition pos, Name name, Scope s) {
	for (Scope.Entry e = s.lookup(name); e.scope == s; e = e.next()) {
	    if (e.sym.kind == TYP && e.sym.name != names.error) {
		duplicateError(pos, e.sym);
		return false;
	    }
	}
	for (Symbol sym = s.owner; sym != null; sym = sym.owner) {
	    if (sym.kind == TYP && sym.name == name && sym.name != names.error) {
		duplicateError(pos, sym);
		return true;
	    }
	}
	return true;
    }

/* *************************************************************************
 * Class name generation
 **************************************************************************/

    /** Return name of local class.
     *  This is of the form    <enclClass> $ n <classname>
     *  where
     *    enclClass is the flat name of the enclosing class,
     *    classname is the simple name of the local class
     */
    Name localClassName(ClassSymbol c) {
	for (int i=1; ; i++) {
	    Name flatname = names.
		fromString("" + c.owner.enclClass().flatname +
                           target.syntheticNameChar() + i +
                           c.name);
	    if (compiled.get(flatname) == null) return flatname;
	}
    }

/* *************************************************************************
 * Type Checking
 **************************************************************************/

    /** Check that a given type is assignable to a given proto-type.
     *  If it is, return the type, otherwise return errType.
     *  @param pos        Position to be used for error reporting.
     *  @param found      The type that was found.
     *  @param req        The type that was required.
     */
    Type checkType(DiagnosticPosition pos, Type foundRaw, Type reqRaw, Sequenceness pSequenceness) {
        return checkType(pos, foundRaw, reqRaw, pSequenceness, true);
    }

    Type asMethod(Type t) {
	if (t instanceof FunctionType) {
	    return ((FunctionType)t).asMethodOrForAll();
	}
	return t;
    }

    Type checkType(DiagnosticPosition pos, Type found, Type req, Sequenceness pSequenceness, boolean giveWarnings) {
        Type realFound = found;
	if (req instanceof FunctionType) {
	    return found;
	}
        if (req.tag == ERROR)
            return req;
	if (found == syms.unreachableType)
	    return found;
        if (found.tag == FORALL) {
	    if (true) {
		//		return found;
	    }
            if (false && (req == syms.f3_UnspecifiedType || req == Type.noType))
                // Is this the right thing to do?  FIXME
                return types.erasure(found);
            else {
		req = types.boxedTypeOrType(req);
		return instantiatePoly(pos, (ForAll)found, req, convertWarner(pos, found, req));
	    }
        }
        if (req.tag == NONE || req == syms.f3_UnspecifiedType)
            return found;
        if (types.isSequence(req)) {    
            //pSequenceness = Sequenceness.REQUIRED;
        }
        if (types.isSequence(found)) {
	    if (false) {
		if (pSequenceness == Sequenceness.DISALLOWED && ! types.isSameType(req, syms.objectType)) {
		    log.error(pos, MsgSym.MESSAGE_F3_BAD_SEQUENCE, types.toF3String(req));
		    return syms.errType;
		}
	    }
        }
        Type reqUnboxed, foundUnboxed;
        if (req.tag == CLASS) {
            reqUnboxed = types.unboxedType(req);
            if (reqUnboxed.tag == NONE)
                reqUnboxed = req;
        }
        else
            reqUnboxed = req;
        if (found.tag == CLASS) {
            foundUnboxed = types.unboxedType(found);
            if (foundUnboxed.tag == NONE)
                foundUnboxed = found;
        }
        else
            foundUnboxed = found;

        if (types.isAssignable(foundUnboxed, reqUnboxed, convertWarner(pos, found, req))) {
            Type foundElem = types.elementTypeOrType(found);
            Type reqElem = types.elementTypeOrType(req);
            if (foundElem.tag == VOID && reqElem.tag != VOID) {
                return typeError(pos, JCDiagnostic.fragment(MsgSym.MESSAGE_INCOMPATIBLE_TYPES), found, req);
            }
            if (reqElem.tag <= LONG && foundElem.tag >= FLOAT && foundElem.tag <= DOUBLE && giveWarnings) {
                // FUTURE/FIXME: return typeError(pos, JCDiagnostic.fragment(MsgSym.MESSAGE_INCOMPATIBLE_TYPES), found, req);
                String foundAsF3Type = types.toF3String(foundUnboxed);
                String requiredAsF3Type = types.toF3String(reqUnboxed);
                log.warning(pos,
                        MsgSym.MESSAGE_PROB_FOUND_REQ,
                        JCDiagnostic.fragment(MsgSym.MESSAGE_POSSIBLE_LOSS_OF_PRECISION),
                        foundAsF3Type,
                        requiredAsF3Type);
            }
            return realFound;
       }

        // use the F3ClassSymbol's supertypes to see if req is in the supertypes of found.
        for (Type baseType : types.supertypesClosure(found, true)) {
            if (types.isAssignable(baseType, req, convertWarner(pos, found, req)))
                return realFound;
        }

        Type foundElem = types.elementTypeOrType(found);
        Type reqElem = types.elementTypeOrType(req);

	if (pSequenceness == Sequenceness.PERMITTED) {
	    if (types.isSameType(foundElem, reqElem)) {
		return realFound;
	    }
	} else {
	    if (types.isSameType(found, req)) {
		return realFound;
	    }
	}

        if (foundElem.tag <= DOUBLE && reqElem.tag <= DOUBLE) {
            if (foundElem.tag == VOID && reqElem.tag != VOID) {
                return typeError(pos, JCDiagnostic.fragment(MsgSym.MESSAGE_INCOMPATIBLE_TYPES), found, req);
            }
            if (giveWarnings) {
                String foundAsF3Type = types.toF3String(found);
                String requiredAsF3Type = types.toF3String(req);
                log.warning(pos.getStartPosition(), MsgSym.MESSAGE_PROB_FOUND_REQ, JCDiagnostic.fragment(MsgSym.MESSAGE_POSSIBLE_LOSS_OF_PRECISION),
                        foundAsF3Type, requiredAsF3Type);
            }
            return realFound;
        }
        if (found.isSuperBound()) {
            log.error(pos, MsgSym.MESSAGE_ASSIGNMENT_FROM_SUPER_BOUND, found);
            return syms.errType;
        }
        if (req.isExtendsBound()) {
            log.error(pos, MsgSym.MESSAGE_ASSIGNMENT_TO_EXTENDS_BOUND, req);
            return syms.errType;
        }
        return typeError(pos, JCDiagnostic.fragment(MsgSym.MESSAGE_INCOMPATIBLE_TYPES), found, req);
    }

    /** Instantiate polymorphic type to some prototype, unless
     *  prototype is `anyPoly' in which case polymorphic type
     *  is returned unchanged.
     */
    Type instantiatePoly(DiagnosticPosition pos, ForAll t, Type pt, Warner warn) {
	if (pt == Infer.anyPoly && complexInference) {
	    return t;
	} else if (pt == Infer.anyPoly || pt.tag == NONE) {
	    Type newpt = t.qtype.tag <= VOID ? t.qtype : syms.objectType;
	    return instantiatePoly(pos, t, newpt, warn);
	} else if (pt.tag == ERROR) {
	    return pt;
	} else {
	    try {
		return infer.instantiateExpr(t, pt, warn);
	    } catch (Infer.NoInstanceException ex) {
		if (ex.isAmbiguous) {
		    JCDiagnostic d = ex.getDiagnostic();
		    log.error(pos,
                  d!=null ? MsgSym.MESSAGE_UNDETERMINDED_TYPE_1 : MsgSym.MESSAGE_UNDETERMINDED_TYPE,
                  t, d);
		    return syms.errType;
		} else {
		    JCDiagnostic d = ex.getDiagnostic();
		    return typeError(pos,
                 JCDiagnostic.fragment(d!=null ? MsgSym.MESSAGE_INCOMPATIBLE_TYPES_1 : MsgSym.MESSAGE_INCOMPATIBLE_TYPES, d),
                 t, pt);
		}
	    }
	}
    }

    void checkInstanceOf(DiagnosticPosition pos, Type s, Type t) {
        if (!types.isCastableNoConversion(s, t, Warner.noWarnings)) {
                typeError(pos,
                    JCDiagnostic.fragment(MsgSym.MESSAGE_INCONVERTIBLE_TYPES),
                    s, t);
            }
    }

    /** Check that a given type can be cast to a given target type.
     *  Return the result of the cast.
     *  @param pos        Position to be used for error reporting.
     *  @param found      The type that is being cast.
     *  @param req        The target type of the cast.
     */
    Type checkCastable(DiagnosticPosition pos, Type found, Type req) {
        if (/*found.tag == FORALL && */found instanceof ForAll) {
            instantiatePoly(pos, (ForAll) found, req, castWarner(pos, found, req));
            return req;
        } else if (types.isCastable(found, req, castWarner(pos, found, req))) {
            return req;
        } else {
            return typeError(pos,
                    JCDiagnostic.fragment(MsgSym.MESSAGE_INCONVERTIBLE_TYPES),
                    found, req);
        }
    }
//where
        /** Is type a type variable, or a (possibly multi-dimensional) array of
	 *  type variables?
	 */
	boolean isTypeVar(Type t) {
	    return t.tag == TYPEVAR || t.tag == ARRAY && isTypeVar(types.elemtype(t));
	}

    /** Check that a type is within some bounds.
     *
     *  Used in TypeApply to verify that, e.g., X in V<X> is a valid
     *  type argument.
     *  @param pos           Position to be used for error reporting.
     *  @param a             The type that should be bounded by bs.
     *  @param bs            The bound.
     */
    private void checkExtends(DiagnosticPosition pos, Type a, TypeVar bs) {
	if (a.isUnbound()) {
	    return;
	} else if (a.tag != WILDCARD) {
	    a = types.upperBound(a);
	    for (List<Type> l = types.getBounds(bs); l.nonEmpty(); l = l.tail) {
		if (!types.isSubtype(a, l.head)) {
		    log.error(pos, MsgSym.MESSAGE_NOT_WITHIN_BOUNDS_EXPLAIN, a, l.head);
		    return;
		}
	    }
	} else if (a.isExtendsBound()) {
	    if (!types.isCastable(bs.getUpperBound(), types.upperBound(a), Warner.noWarnings))
		log.error(pos, MsgSym.MESSAGE_NOT_WITHIN_BOUNDS_EXPLAIN, a, bs.getUpperBound());
	} else if (a.isSuperBound()) {
	    if (types.notSoftSubtype(types.lowerBound(a), bs.getUpperBound()))
		log.error(pos, MsgSym.MESSAGE_NOT_WITHIN_BOUNDS, a);
	}
    }

    /** Check that type is different from 'void'.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     */
    Type checkNonVoid(DiagnosticPosition pos, Type t) {
	if (t.tag == VOID && t != syms.unreachableType) {
	    /*
	    log.error(pos, MsgSym.MESSAGE_VOID_NOT_ALLOWED_HERE);
	    return syms.errType;
	    */
	    return types.boxedTypeOrType(t);
	} else {
	    return t;
	}
    }

    /** Check that type is a class or interface type.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     */
    Type checkClassType(DiagnosticPosition pos, Type t) {
	if (t.tag != CLASS && t.tag != ERROR)
            return typeTagError(pos,
                                JCDiagnostic.fragment(MsgSym.MESSAGE_TYPE_REQ_CLASS),
                                (t.tag == TYPEVAR)
                                ? JCDiagnostic.fragment(MsgSym.MESSAGE_TYPE_PARAMETER, t)
                                : t); 
	else
	    return t;
    }

    /** Is given blank final variable assignable, i.e. in a scope where it
     *  may be assigned to even though it is final?
     *  @param v      The blank final variable.
     *  @param env    The current environment.
     */
    boolean isAssignableAsBlankFinal(F3VarSymbol v, F3Env<F3AttrContext> env) {
        Symbol owner = env.info.scope.owner;
           // owner refers to the innermost variable, method or
           // initializer block declaration at this point.
        return
            v.owner == owner
            ||
            ((owner.name == names.init ||    // i.e. we are in a constructor
              owner.kind == VAR ||           // i.e. we are in a variable initializer
              (owner.flags() & BLOCK) != 0)  // i.e. we are in an initializer block
             &&
             v.owner == owner.owner
             &&
             ((v.flags() & STATIC) != 0) == F3Resolve.isStatic(env));
    }

    /** Check that variable can be assigned to.
     *  @param pos    The current source code position.
     *  @param v      The assigned varaible
     *  @param base   If the variable is referred to in a Select, the part
     *                to the left of the `.', null otherwise.
     *  @param env    The current environment.
     */
    void checkAssignable(DiagnosticPosition pos, F3VarSymbol v, F3Tree base, Type site, F3Env<F3AttrContext> env, WriteKind writeKind) {
        //TODO: for attributes they are always final -- this should really be checked in F3ClassReader
        //TODO: rebutal, actual we should just use a different final
        if ((v.flags() & FINAL) != 0 && !types.isF3Class(v.owner) &&
            ((v.flags() & HASINIT) != 0
             ||
             !((base == null ||
               (base.getF3Tag() == F3Tag.IDENT && F3TreeInfo.name(base) == names._this)) &&
               isAssignableAsBlankFinal(v, env)))) {
            log.error(pos, MsgSym.MESSAGE_CANNOT_ASSIGN_VAL_TO_FINAL_VAR, v);
        } else if ((v.flags() & F3Flags.IS_DEF) != 0L) {
            log.error(pos, MsgSym.MESSAGE_F3_CANNOT_ASSIGN_TO_DEF, v);
        } else if ((v.flags() & Flags.PARAMETER) != 0L) {
            log.error(pos, MsgSym.MESSAGE_F3_CANNOT_ASSIGN_TO_PARAMETER, v);
        } else {
            // now check access permissions for write/init
            switch (writeKind) {
                case INIT_NON_BIND:
                    if ((v.flags() & F3Flags.PUBLIC_INIT) != 0L) {
                        // it is an initialization, and init is explicitly allowed
                        return;
                    }
                    break;
            }
            if (!rs.isAccessibleForWrite(env, site, v)) {
                String msg;
                switch (writeKind) {
                    case INIT_BIND:
                        msg = MsgSym.MESSAGE_F3_REPORT_BIND_ACCESS;
                        break;
                    case INIT_NON_BIND:
                        msg = MsgSym.MESSAGE_F3_REPORT_INIT_ACCESS;
                        break;
                    case VAR_QUERY:
                        msg = MsgSym.MESSAGE_F3_REPORT_VAR_QUERY_ACCESS;
                        break;
                    case ASSIGN:
                    default:
                        msg = MsgSym.MESSAGE_F3_REPORT_WRITE_ACCESS;
                        break;
                }
                log.error(pos, msg, v,
                        F3Check.protectionString(v.flags()),
                        v.location());
            }
        }
    }

    /** True if we should warn in 'a.b.' that 'a' is non-constant.
     * We don't re-evaluate the select target
     * in bidirectional binds. So, we may issue warning.
     */
    boolean checkBidiSelect(F3Select select, F3Env<F3AttrContext> env, Type pt) {
        F3Tree base = select.getExpression();

        // We don't re-evaluate the select target
        // in bidirectional binds. So, we may issue warning.

        // Do not warn for this.foo and super.foo
        Name baseName = F3TreeInfo.name(base);
        if (baseName == names._this ||
                baseName == names._super) {
            return false;
        }

        // Do not warn for static variable select,
        // because the target is a class and so that
        // can not change. Also, ClassName.foo is used
        // to access super class variable - we do not
        // warn that case either.
        Symbol sym = F3TreeInfo.symbolFor(base);
        if (sym instanceof F3ClassSymbol) {
            return false;
        }

        // If the target of member select is a "def"
        // variable and not initialized with bind, then
        // we know the target can not change.
        if (base instanceof F3Ident) {
            long flags = sym.flags();
            boolean isDef = (flags & F3Flags.IS_DEF) != 0L;
            boolean isBindInit = (flags & F3Flags.VARUSE_BOUND_INIT) != 0L;
            boolean targetFinal = isDef && !isBindInit;
            return !targetFinal;
        }
        return true;
    }

    void checkBoundArrayVar(F3AbstractVar tree) {
        if (tree.getInitializer() == null ||
                tree.getInitializer().type == null) {
            return;
        }
        else if (types.isArray(tree.getInitializer().type)) {
            if (tree.isBound()) {
                log.warning(tree.pos(), MsgSym.MESSAGE_F3_UNSUPPORTED_TYPE_IN_BIND);
            }
            if (tree.getOnInvalidate() != null || tree.getOnReplace() != null) {
                DiagnosticPosition pos = tree.getOnReplace() != null ?
                    tree.getOnReplace().pos() :
                    tree.getOnInvalidate().pos();
                log.warning(pos, MsgSym.MESSAGE_F3_UNSUPPORTED_TYPE_IN_TRIGGER);
            }
        }
    }

    void checkBidiBind(F3Expression init, F3BindStatus bindStatus, F3Env<F3AttrContext> env, Type pt) {        
        if (bindStatus.isBidiBind()) {
            Symbol initSym = null;
            F3Tree base = null;
            Type site = null;
            switch (init.getF3Tag()) {
                case IDENT: {
                    initSym = ((F3Ident) init).sym;
                    base = null;
                    site = env.enclClass.sym.type;
                    break;
                }
                case SELECT: {
                    F3Select select = (F3Select) init;
                    initSym = select.sym;
                    base = select.getExpression();

                    if (checkBidiSelect(select, env, pt))
                        log.warning(select.getExpression().pos(),
                            MsgSym.MESSAGE_SELECT_TARGET_NOT_REEVALUATED_FOR_BIDI_BIND,
                            select.getExpression(), select.name);

                    site = select.type;
                    break;
                }
            }
            if (initSym instanceof VarSymbol) {
                if (pt != null && bindStatus.isBidiBind() && !types.isSameType(pt, initSym.type)) {
                    log.error(init.pos(), 
                              MsgSym.MESSAGE_F3_WRONG_TYPE_FOR_BIDI_BIND,
                              types.toF3String(initSym.type),
                              types.toF3String(pt));
                }
                checkAssignable(init.pos(), (F3VarSymbol) initSym, base, site, env, WriteKind.INIT_BIND);
            } else {
                log.error(init.pos(), MsgSym.MESSAGE_F3_EXPR_UNSUPPORTED_FOR_BIDI_BIND);
            }
        }
    }

    /**
     * Return element type for a sequence type, and report error otherwise.
     */
    public Type checkSequenceElementType (DiagnosticPosition pos, Type t) {
        if (types.isSequence(t))
            return types.elementType(t);
        if (t.tag != ERROR) {
            return typeTagError(pos, types.sequenceType(syms.unknownType), t);
        }
        return syms.errType;
    }

    public Type checkSequenceOrArrayType (DiagnosticPosition pos, Type t) {
        if (!types.isSequence(t) && t.tag != ARRAY && !t.isErroneous())
            return typeTagError(pos,
                        messages.getLocalizedString(MsgSym.MESSAGEPREFIX_COMPILER_MISC +
                        MsgSym.MESSAGE_F3_SEQ_OR_ARRAY),
                        t);
        else
            return t;
    }

    /**
     * Check that a method call of the kind t.memberName() is legal.
     * t must be a direct supertype of the enclosing class type csym.
     *
     * @param pos the position in which the error should be reported
     * @param csym the enclosing class
     * @param t the qualifier type
     */
    public void checkSuper(DiagnosticPosition pos, F3ClassSymbol csym, Type t) {
        if (types.isSameType(csym.type, t))
            return;

        boolean isOk = false;
        List<Type> supertypes = types.supertypes(csym.type);
        if (supertypes.isEmpty()) {
            isOk = types.isSameType(syms.objectType, t);
        }
        else {
            while(supertypes.nonEmpty() && !isOk) {
                if (types.isSameType(t, supertypes.head))
                    isOk = true;
                supertypes = supertypes.tail;
            }
        }

        if (!isOk) {
            log.error(pos, MsgSym.MESSAGE_F3_INVALID_SELECT_FOR_SUPER,
                    types.toF3String(t),
                    types.toF3String(csym.type));
        }
    }

    /** Check that type is a class or interface type.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     *  @param noBounds    True if type bounds are illegal here.
     */
    Type checkClassType(DiagnosticPosition pos, Type t, boolean noBounds) {
	t = checkClassType(pos, t);
	if (noBounds && t.isParameterized()) {
	    List<Type> args = t.getTypeArguments();
	    if (false) while (args.nonEmpty()) {
		System.err.println("args="+types.toF3String(args));
		if (args.head.tag == WILDCARD)
		    return typeTagError(pos,
					Log.getLocalizedString(MsgSym.MESSAGE_TYPE_REQ_EXACT),
					args.head);
		args = args.tail;
	    }
	}
	return t;
    }

    /** Check that type is a reifiable class, interface or array type.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     */
    Type checkReifiableReferenceType(DiagnosticPosition pos, Type t) {
	if (t.tag != CLASS && t.tag != ARRAY && t.tag != ERROR) {
	    return typeTagError(pos,
				JCDiagnostic.fragment(MsgSym.MESSAGE_TYPE_REQ_CLASS_ARRAY),
				t);
	} else if (!types.isReifiable(t)) {
	    log.error(pos, MsgSym.MESSAGE_ILLEGAL_GENERIC_TYPE_FOR_INSTOF);
	    return syms.errType;
	} else {
	    return t;
	}
    }

    /** Check that type is a reference type, i.e. a class, interface or array type
     *  or a type variable.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     */
    Type checkRefType(DiagnosticPosition pos, Type t) {
	switch (t.tag) {
	case CLASS:
	case ARRAY:
	case TYPEVAR:
	case WILDCARD:
	case ERROR:
	    return t;
	default:
	    return typeTagError(pos,
				JCDiagnostic.fragment(MsgSym.MESSAGE_TYPE_REQ_REF),
				t);
	}
    }

    /** Check that type is a null or reference type.
     *  @param pos           Position to be used for error reporting.
     *  @param t             The type to be checked.
     */
    Type checkNullOrRefType(DiagnosticPosition pos, Type t) {
	switch (t.tag) {
	case CLASS:
	case ARRAY:
	case TYPEVAR:
	case WILDCARD:
	case BOT:
	case ERROR:
	    return t;
	default:
	    return typeTagError(pos,
				JCDiagnostic.fragment(MsgSym.MESSAGE_TYPE_REQ_REF),
				t);
	}
    }

    /** Check that flag set does not contain elements of two conflicting sets. 
     *  Log error if it does.
     *  Return true if it doesn't.
     *  @param pos           Position to be used for error reporting.
     *  @param flags         The set of flags to be checked.
     *  @param set1          Conflicting flags set #1.
     *  @param set2          Conflicting flags set #2.
     */
    boolean checkDisjoint(DiagnosticPosition pos, long flags, long set1, long set2) {
        if ((flags & set1) != 0 && (flags & set2) != 0) {
            log.error(pos,
		      MsgSym.MESSAGE_ILLEGAL_COMBINATION_OF_MODIFIERS,
		      F3TreeInfo.flagNames(F3TreeInfo.firstFlag(flags & set1)),
		      F3TreeInfo.flagNames(F3TreeInfo.firstFlag(flags & set2)));
            return false;
        } else
            return true;
    }

    /** Check that flag set does not contain elements of two conflicting sets. 
     *  Log warning if it does.
     *  Return true if it doesn't.
     *  @param pos           Position to be used for error reporting.
     *  @param flags         The set of flags to be checked.
     *  @param set1          Conflicting flags set #1.
     *  @param set2          Conflicting flags set #2.
     */
    boolean checkDisjointWarn(DiagnosticPosition pos, long flags, long set1, long set2) {
        if ((flags & set1) != 0 && (flags & set2) != 0) {
            log.warning(pos,
		      MsgSym.MESSAGE_F3_REDUNDANT_ACCESS_MODIFIERS,
		      F3TreeInfo.flagNames(F3TreeInfo.firstFlag(flags & set1)),
		      F3TreeInfo.flagNames(F3TreeInfo.firstFlag(flags & set2)));
            return false;
        } else
            return true;
    }

    /** Check that given modifiers are legal for given symbol and
     *  return modifiers together with any implicit modifiers for that symbol.
     *  Warning: we can't use flags() here since this method
     *  is called during class enter, when flags() would cause a premature
     *  completion.
     *  @param pos           Position to be used for error reporting.
     *  @param flags         The set of modifiers given in a definition.
     *  @param sym           The defined symbol.
     */
    long checkFlags(DiagnosticPosition pos, long flags, Symbol sym, F3Tree tree) {
        long mask;
        String msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON;
        String thing;
        boolean isScriptLevel = (flags & STATIC) != 0;
        switch (sym.kind) {
            case VAR:
                F3VarSymbol vsym = (F3VarSymbol)sym;
                boolean isDef = ((flags & IS_DEF) != 0);
                thing = isDef? "def" : "var";
                if (!vsym.isMember()) {
                    mask = F3LocalVarFlags;
                    msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON_LOCAL;
                } else if (isDef) {
                    mask = F3MemberDefFlags;
                    msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON;
                } else if (isScriptLevel) {
                    mask = F3ScriptVarFlags;
                    msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON_SCRIPT;
                } else {
                    mask = F3InstanceVarFlags;
                    msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON_INSTANCE;
                }
                break;
            case MTH:
                if (isScriptLevel) {
                    mask = F3ScriptFunctionFlags;
                    msg = MsgSym.MESSAGE_F3_MOD_NOT_ALLOWED_ON_SCRIPT;
                } else {
                    mask = F3FunctionFlags;
                }
                thing = "function";
                break;
            case TYP:
                // flags aren't currently different:  if (sym.isLocal()) ...
                mask = F3ClassFlags;
                thing = "class";
                break;
            default:
                throw new AssertionError();
        }
        long illegal = flags & F3UserFlags & ~mask;
        /***
        System.err.println(sym);
        System.err.printf("%022o mask -- %s\n", mask, F3TreeInfo.flagNames(mask));
        System.err.printf("%022o ~mask -- %s\n", ~mask, F3TreeInfo.flagNames(~mask));
        System.err.printf("%022o F3UserFlags -- %s\n", F3UserFlags, F3TreeInfo.flagNames(F3UserFlags));
        System.err.printf("%022o flags -- %s\n", flags, F3TreeInfo.flagNames(flags));
        System.err.printf("%022o illegal -- %s\n", illegal, F3TreeInfo.flagNames(illegal));
        ***/
        if (illegal != 0) {
	    Thread.currentThread().dumpStack();
            log.error(pos, msg, F3TreeInfo.flagNames(illegal), thing);
        }
        else if ((sym.kind == TYP ||
		 checkDisjoint(pos, flags,
			       ABSTRACT | MIXIN,
			       PRIVATE | STATIC))
		 &&
		 checkDisjoint(pos, flags,
			       ABSTRACT | INTERFACE,
			       FINAL | NATIVE | SYNCHRONIZED)
		 &&
                 checkDisjoint(pos, flags,
                               PUBLIC,
                               PRIVATE | PROTECTED | PACKAGE_ACCESS | SCRIPT_PRIVATE)
		 &&
                 checkDisjoint(pos, flags,
                               PRIVATE,
                               PUBLIC | PROTECTED | PACKAGE_ACCESS | SCRIPT_PRIVATE)
		 &&
                 checkDisjoint(pos, flags,
                               SCRIPT_PRIVATE,
                               PRIVATE | PROTECTED | PUBLIC | PACKAGE_ACCESS)
		 &&
                 checkDisjoint(pos, flags,
                               PACKAGE_ACCESS,
                               PRIVATE | PROTECTED | PUBLIC | SCRIPT_PRIVATE)
		 &&
		 (sym.kind == TYP ||
		  checkDisjoint(pos, flags,
				ABSTRACT | NATIVE,
				STRICTFP))
		 &&
		 checkDisjointWarn(pos, flags,
			       PUBLIC,
			       PUBLIC_INIT | PUBLIC_READ)
		 &&
		 checkDisjointWarn(pos, flags,
			       PUBLIC_INIT,
			       PUBLIC_READ)
                                
                                ) {
	    // skip
        }
        return flags & ~illegal;
    }

/* *************************************************************************
 * Type Validation
 **************************************************************************/

    /** Validate a type expression. That is,
     *  check that all type arguments of a parametric type are within
     *  their bounds. This must be done in a second phase after type attributon
     *  since a class might have a subclass as type parameter bound. E.g:
     *
     *  class B<A extends C> { ... }
     *  class C extends B<C> { ... }
     *
     *  and we can't make sure that the bound is already attributed because
     *  of possible cycles.
     */
    private Validator validator = new Validator();

    /** Visitor method: Validate a type expression, if it is not null, catching
     *  and reporting any completion failures.
     */
    void validate(F3Tree tree) {
	try {
	    if (tree != null) tree.accept(validator);
	} catch (CompletionFailure ex) {
	    completionError(tree.pos(), ex);
	}
    }

    /** Visitor method: Validate a list of type expressions.
     */
    void validate(List<? extends F3Tree> trees) {
	for (List<? extends F3Tree> l = trees; l.nonEmpty(); l = l.tail)
	    validate(l.head);
    }

    /** A visitor class for type validation.
     */
    class Validator extends F3TreeScanner {

        @Override
        public void visitSelect(F3Select tree) {
	    if (tree.type.tag == CLASS) {
                visitSelectInternal(tree);

                // Check that this type is either fully parameterized, or
                // not parameterized at all.
                if (tree.selected.type.isParameterized() && tree.type.tsym.type.getTypeArguments().nonEmpty())
                    log.error(tree.pos(), MsgSym.MESSAGE_IMPROPERLY_FORMED_TYPE_PARAM_MISSING);
            }
	}
        public void visitSelectInternal(F3Select tree) {
            if (tree.type.getEnclosingType().tag != CLASS &&
                tree.selected.type.isParameterized()) {
                // The enclosing type is not a class, so we are
                // looking at a static member type.  However, the
                // qualifying expression is parameterized.
                log.error(tree.pos(), MsgSym.MESSAGE_CANNOT_SELECT_STATIC_CLASS_FROM_PARAM_TYPE);
            } else {
                // otherwise validate the rest of the expression
                validate(tree.selected);
            }
        }

	/** Default visitor method: do nothing.
	 */
	@Override
	public void visitTree(F3Tree tree) {
	}
    }

/* *************************************************************************
 * Exception checking
 **************************************************************************/

    /* The following methods treat classes as sets that contain
     * the class itself and all their subclasses
     */

    /** Is given type a subtype of some of the types in given list?
     */
    boolean subset(Type t, List<Type> ts) {
	for (List<Type> l = ts; l.nonEmpty(); l = l.tail)
	    if (types.isSubtype(t, l.head)) return true;
	return false;
    }

    /** Is given type a subtype or supertype of
     *  some of the types in given list?
     */
    boolean intersects(Type t, List<Type> ts) {
	for (List<Type> l = ts; l.nonEmpty(); l = l.tail)
	    if (types.isSubtype(t, l.head) || types.isSubtype(l.head, t)) return true;
	return false;
    }

    /** Add type set to given type list, unless it is a subclass of some class
     *  in the list.
     */
    List<Type> incl(Type t, List<Type> ts) {
	return subset(t, ts) ? ts : excl(t, ts).prepend(t);
    }

    /** Remove type set from type set list.
     */
    List<Type> excl(Type t, List<Type> ts) {
	if (ts.isEmpty()) {
	    return ts;
	} else {
	    List<Type> ts1 = excl(t, ts.tail);
	    if (types.isSubtype(ts.head, t)) return ts1;
	    else if (ts1 == ts.tail) return ts;
	    else return ts1.prepend(ts.head);
	}
    }

    /** Form the union of two type set lists.
     */
    List<Type> union(List<Type> ts1, List<Type> ts2) {
	List<Type> ts = ts1;
	for (List<Type> l = ts2; l.nonEmpty(); l = l.tail)
	    ts = incl(l.head, ts);
	return ts;
    }

    /** Form the difference of two type lists.
     */
    List<Type> diff(List<Type> ts1, List<Type> ts2) {
	List<Type> ts = ts1;
	for (List<Type> l = ts2; l.nonEmpty(); l = l.tail)
	    ts = excl(l.head, ts);
	return ts;
    }

    /** Form the intersection of two type lists.
     */
    public List<Type> intersect(List<Type> ts1, List<Type> ts2) {
	List<Type> ts = List.nil();
	for (List<Type> l = ts1; l.nonEmpty(); l = l.tail)
	    if (subset(l.head, ts2)) ts = incl(l.head, ts);
	for (List<Type> l = ts2; l.nonEmpty(); l = l.tail)
	    if (subset(l.head, ts1)) ts = incl(l.head, ts);
	return ts;
    }

    /** Is exc an exception symbol that need not be declared?
     */
    boolean isUnchecked(ClassSymbol exc) {
	return
	    exc.kind == ERR ||
	    exc.isSubClass(syms.errorType.tsym, types) ||
	    exc.isSubClass(syms.runtimeExceptionType.tsym, types);
    }

    /** Is exc an exception type that need not be declared?
     */
    boolean isUnchecked(Type exc) {
	return
	    (exc.tag == TYPEVAR) ? isUnchecked(types.supertype(exc)) :
	    (exc.tag == CLASS) ? isUnchecked((ClassSymbol)exc.tsym) :
	    exc.tag == BOT;
    }

    /** Same, but handling completion failures.
     */
    boolean isUnchecked(DiagnosticPosition pos, Type exc) {
	try {
	    return isUnchecked(exc);
	} catch (CompletionFailure ex) {
	    completionError(pos, ex);
	    return true;
	}
    }

    /** Is exc handled by given exception list?
     */
    boolean isHandled(Type exc, List<Type> handled) {
	return isUnchecked(exc) || subset(exc, handled);
    }

    /** Return all exceptions in thrown list that are not in handled list.
     *  @param thrown     The list of thrown exceptions.
     *  @param handled    The list of handled exceptions.
     */
    List<Type> unHandled(List<Type> thrown, List<Type> handled) {
	List<Type> unhandled = List.nil();
	for (List<Type> l = thrown; l.nonEmpty(); l = l.tail)
	    if (!isHandled(l.head, handled)) unhandled = unhandled.prepend(l.head);
	return unhandled;
    }

/* *************************************************************************
 * Overriding/Implementation checking
 **************************************************************************/

    /** The level of access protection given by a flag set,
     *  where PRIVATE is highest and PUBLIC is lowest.
     */
    static int protection(long flags) {
        // because the SCRIPT_PRIVATE bit is too high for the switch, test it later
        switch ((short)(flags & Flags.AccessFlags)) {
        case PRIVATE: return 3;
        case PROTECTED: return 1;
        default:
        case PUBLIC: return 0;
        // 'package' vs script-private
        case 0: return ((flags & SCRIPT_PRIVATE)==0)? 2 : 3;
        }
    }

    /** A string describing the access permission given by a flag set.
     *  This always returns a space-separated list of Java Keywords.
     */
    public static String protectionString(long flags) {
	long flags1 = flags & (F3Flags.F3AccessFlags | F3Flags.F3ExplicitAccessFlags);
	return F3TreeInfo.flagNames(flags1);
    }

    /** A customized "cannot override" error message.
     *  @param m      The overriding method.
     *  @param other  The overridden method.
     *  @return       An internationalized string.
     */
    static Object cannotOverride(MethodSymbol m, MethodSymbol other) {
	String key;
	if ((other.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_CANNOT_OVERRIDE;
	else if ((m.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_CANNOT_IMPLEMENT;
	else
	    key = MsgSym.MESSAGE_CLASHES_WITH;
	return JCDiagnostic.fragment(key, m, m.location(), other, other.location());
    }

    /** A customized "override" warning message.
     *  @param m      The overriding method.
     *  @param other  The overridden method.
     *  @return       An internationalized string.
     */
    static Object uncheckedOverrides(MethodSymbol m, MethodSymbol other) {
	String key;
	if ((other.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_UNCHECKED_OVERRIDE;
	else if ((m.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_UNCHECKED_IMPLEMENT;
	else 
	    key = MsgSym.MESSAGE_UNCHECKED_CLASH_WITH;
	return JCDiagnostic.fragment(key, m, m.location(), other, other.location());
    }

    /** A customized "override" warning message.
     *  @param m      The overriding method.
     *  @param other  The overridden method.
     *  @return       An internationalized string.
     */
    static Object varargsOverrides(MethodSymbol m, MethodSymbol other) {
	String key;
	if ((other.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_VARARGS_OVERRIDE;
	else  if ((m.owner.flags() & INTERFACE) == 0) 
	    key = MsgSym.MESSAGE_VARARGS_IMPLEMENT;
	else
	    key = MsgSym.MESSAGE_VARARGS_CLASH_WITH;
	return JCDiagnostic.fragment(key, m, m.location(), other, other.location());
    }

    /** Check that this method conforms with overridden method 'other'.
     *  where `origin' is the class where checking started.
     *  Complications:
     *  (1) Do not check overriding of synthetic methods
     *      (reason: they might be final).
     *      todo: check whether this is still necessary.
     *  (2) Admit the case where an interface proxy throws fewer exceptions
     *      than the method it implements. Augment the proxy methods with the
     *      undeclared exceptions in this case.
     *  (3) When generics are enabled, admit the case where an interface proxy
     *	    has a result type
     *      extended by the result type of the method it implements.
     *      Change the proxies result type to the smaller type in this case.
     *
     *  @param tree         The tree from which positions
     *			    are extracted for errors.
     *  @param m            The overriding method.
     *  @param other        The overridden method.
     *  @param origin       The class of which the overriding method
     *			    is a member.
     */
    private void checkOverride(F3Tree tree,
			       MethodSymbol m,
			       MethodSymbol other,
			       ClassSymbol origin) {
	//System.err.println(tree);
	//System.err.println(m);
	//System.err.println(other);
	//System.err.println(origin);
	// Don't check overriding of synthetic methods or by bridge methods.
	m.complete();
	if ((m.flags() & (SYNTHETIC|BRIDGE)) != 0 || (other.flags() & SYNTHETIC) != 0) {
	    return;
	}

	// Error if static method overrides instance method (JLS 8.4.6.2).
	if ((m.flags() & STATIC) != 0 &&
		   (other.flags() & STATIC) == 0) {
	    log.error(F3TreeInfo.diagnosticPositionFor(m, tree), MsgSym.MESSAGE_OVERRIDE_STATIC,
		      cannotOverride(m, other));
	    return;
	}

	// Error if instance method overrides static or final
	// method (JLS 8.4.6.1).
	if ((other.flags() & FINAL) != 0 ||
		 (m.flags() & STATIC) == 0 &&
		 (other.flags() & STATIC) != 0) {
	    log.error(F3TreeInfo.diagnosticPositionFor(m, tree), MsgSym.MESSAGE_OVERRIDE_METH,
		      cannotOverride(m, other),
		      F3TreeInfo.flagNames(other.flags() & (FINAL | STATIC)));
            return;
        }

        // Error if bound function overrides non-bound.
        if ((other.flags() & BOUND) == 0 && (m.flags() & BOUND) != 0) {
            log.error(F3TreeInfo.diagnosticPositionFor(m, tree), MsgSym.MESSAGE_F3_BOUND_OVERRIDE_METH,
                    cannotOverride(m, other));
            return;
        }

        // Error if non-bound function overrides bound.
        if ((other.flags() & BOUND) != 0 && (m.flags() & BOUND) == 0) {
            log.error(F3TreeInfo.diagnosticPositionFor(m, tree), MsgSym.MESSAGE_F3_NON_BOUND_OVERRIDE_METH,
                    cannotOverride(m, other));
            return;
        }

        if ((m.owner.flags() & ANNOTATION) != 0) {
                // handled in validateAnnotationMethod
            return;
        }

	// Error if overriding method has weaker access (JLS 8.4.6.3).
/*---------------  Taken out. F3 doesn't have the JLS 8.4.6.3 rule...
	if ((origin.flags() & INTERFACE) == 0 &&
		 protection(m.flags()) > protection(other.flags())) {
	    log.error(F3TreeInfo.diagnosticPositionFor(m, tree), "override.weaker.access",
		      cannotOverride(m, other),
		      protectionString(other.flags()));
	    return;

	}
----------------- */
	//System.err.println("m="+m);
	//System.err.println("other="+other);
	//System.err.println("origin.type="+origin.type);

	Type mt = types.memberType(origin.type, m);
	Type ot = types.memberType(origin.type, other);
	//System.err.println("mt="+mt);
	//System.err.println("ot="+ot);
	// Error if overriding result type is different
	// (or, in the case of generics mode, not a subtype) of
	// overridden result type. We have to rename any type parameters
	// before comparing types.
	List<Type> mtvars = mt.getTypeArguments();
	List<Type> otvars = ot.getTypeArguments();
	//System.err.println("mtvars="+mtvars);
	//System.err.println("otvars="+otvars);
	Type mtres = mt.getReturnType();
	Type otres = types.subst(ot.getReturnType(), otvars, mtvars);
	//System.err.println("mtres="+mtres);
	//System.err.println("otres="+otres);

	overrideWarner.warned = false;
	boolean resultTypesOK = 
	    types.returnTypeSubstitutable(mt,
					  ot,
					  otres, 
					  overrideWarner);

	if (!resultTypesOK) { 
	    // we accept boxed types
	    if (types.isSameType(types.boxedTypeOrType(otres), types.boxedTypeOrType(mt.getReturnType()))) {
		resultTypesOK = true;
	    }
	}
	//System.err.println("returnTypesOK="+resultTypesOK);
	if (!resultTypesOK) {
	    if (!source.allowCovariantReturns() &&
		m.owner != origin &&
		m.owner.isSubClass(other.owner, types)) {
		// allow limited interoperability with covariant returns
	    } else {
		typeError(F3TreeInfo.diagnosticPositionFor(m, tree),
			  JCDiagnostic.fragment(MsgSym.MESSAGE_OVERRIDE_INCOMPATIBLE_RET,
					 cannotOverride(m, other)),
			  mtres, otres);
		return;
	    }
	} else if (overrideWarner.warned) {
	    warnUnchecked(F3TreeInfo.diagnosticPositionFor(m, tree),
			  MsgSym.MESSAGE_PROB_FOUND_REQ,
			  JCDiagnostic.fragment(MsgSym.MESSAGE_OVERRIDE_UNCHECKED_RET,
					      uncheckedOverrides(m, other)),
			  mtres, otres);
	}
	
	// Error if overriding method throws an exception not reported
	// by overridden method.
	List<Type> otthrown = types.subst(ot.getThrownTypes(), otvars, mtvars);
	List<Type> unhandled = unHandled(mt.getThrownTypes(), otthrown);
	if (unhandled.nonEmpty()) {
	    log.error(F3TreeInfo.diagnosticPositionFor(m, tree),
		      MsgSym.MESSAGE_OVERRIDE_METH_DOES_NOT_THROW,
		      cannotOverride(m, other),
		      unhandled.head);
	    return;
	}

	// Optional warning if varargs don't agree 
	if ((((m.flags() ^ other.flags()) & Flags.VARARGS) != 0)
	    && lint.isEnabled(Lint.LintCategory.OVERRIDES)) {
	    log.warning(F3TreeInfo.diagnosticPositionFor(m, tree),
			((m.flags() & Flags.VARARGS) != 0)
			? MsgSym.MESSAGE_OVERRIDE_VARARGS_MISSING
			: MsgSym.MESSAGE_OVERRIDE_VARARGS_EXTRA,
			varargsOverrides(m, other));
	} 

	// Warn if instance method overrides bridge method (compiler spec ??)
	if ((other.flags() & BRIDGE) != 0) {
	    log.warning(F3TreeInfo.diagnosticPositionFor(m, tree), MsgSym.MESSAGE_OVERRIDE_BRIDGE,
			uncheckedOverrides(m, other));
	}

	// Warn if a deprecated method overridden by a non-deprecated one.
	if ((other.flags() & DEPRECATED) != 0 
	    && (m.flags() & DEPRECATED) == 0 
	    && m.outermostClass() != other.outermostClass()
	    && !isDeprecatedOverrideIgnorable(other, origin)) {
	    warnDeprecated(F3TreeInfo.diagnosticPositionFor(m, tree), other);
	}
    }
    // where
	private boolean isDeprecatedOverrideIgnorable(MethodSymbol m, ClassSymbol origin) {
	    // If the method, m, is defined in an interface, then ignore the issue if the method
	    // is only inherited via a supertype and also implemented in the supertype,
	    // because in that case, we will rediscover the issue when examining the method
	    // in the supertype.
	    // If the method, m, is not defined in an interface, then the only time we need to
	    // address the issue is when the method is the supertype implemementation: any other
	    // case, we will have dealt with when examining the supertype classes
	    ClassSymbol mc = m.enclClass();
	    Type st = types.supertype(origin.type);
	    if (st.tag != CLASS)
		return true;
	    MethodSymbol stimpl = types.implementation(m, (ClassSymbol)st.tsym, false);

	    if (mc != null && ((mc.flags() & INTERFACE) != 0)) {
		List<Type> intfs = types.interfaces(origin.type);
		return (intfs.contains(mc.type) ? false : (stimpl != null));
	    }
	    else
		return (stimpl != m);
	}


    // used to check if there were any unchecked conversions
    private Warner overrideWarner = new Warner();

    /** Check that a class does not inherit two concrete methods
     *  with the same signature.
     *  @param pos          Position to be used for error reporting.
     *  @param site         The class type to be checked.
     */
    private void checkCompatibleConcretes(DiagnosticPosition pos, Type site) {
	Type sup = types.supertype(site);
	if (sup.tag != CLASS) return;
	for (Type t1 = sup;
	     t1.tsym.type.isParameterized();
	     t1 = types.supertype(t1)) {
	    for (Scope.Entry e1 = t1.tsym.members().elems;
		 e1 != null;
		 e1 = e1.sibling) {
		Symbol s1 = e1.sym;
		if (s1.kind != MTH ||
		    (s1.flags() & (STATIC|SYNTHETIC|BRIDGE)) != 0 ||
		    !s1.isInheritedIn(site.tsym, types) ||
		    types.implementation((MethodSymbol)s1, site.tsym,
						      true) != s1)
		    continue;
		Type st1 = types.memberType(t1, s1);
		int s1ArgsLength = st1.getParameterTypes().length();
		if (st1 == s1.type) continue;

		for (Type t2 = sup;
		     t2.tag == CLASS;
		     t2 = types.supertype(t2)) {
		    for (Scope.Entry e2 = t1.tsym.members().lookup(s1.name);
			 e2.scope != null;
			 e2 = e2.next()) {
			Symbol s2 = e2.sym;
			if (s2.type == null) {
			    System.err.println("s2.type is null: "+s2+", s1="+s1);
			    continue;
			}
			if (s2 == s1 ||
			    s2.kind != MTH ||
			    (s2.flags() & (STATIC|SYNTHETIC|BRIDGE)) != 0 ||
			    s2.type.getParameterTypes().length() != s1ArgsLength ||
			    !s2.isInheritedIn(site.tsym, types) ||
			    types.implementation((MethodSymbol)s2, site.tsym,
							      true) != s2)
			    continue;
			Type st2 = types.memberType(t2, s2);
			if (types.overrideEquivalent(st1, st2))
			    log.error(pos, MsgSym.MESSAGE_CONCRETE_INHERITANCE_CONFLICT,
				      s1, t1, s2, t2, sup);
		    }
		}
	    }
	}
    }

    /** Check that classes (or interfaces) do not each define an abstract
     *  method with same name and arguments but incompatible return types.
     *  @param pos          Position to be used for error reporting.
     *  @param t1           The first argument type.
     *  @param t2           The second argument type.
     */
    private boolean checkCompatibleAbstracts(DiagnosticPosition pos,
            Type t1,
            Type t2,
            Type site) {
        Symbol sym = firstIncompatibility(t1, t2, site);
        if (sym != null) {
            if (sym.kind == VAR) {
                log.error(pos, MsgSym.MESSAGE_F3_TYPES_INCOMPATIBLE_VARS,
                    t1, t2, sym.name);
            }
            else {
                log.error(pos, MsgSym.MESSAGE_TYPES_INCOMPATIBLE_DIFF_RET,
                        t1, t2, sym.name +
                        "(" + types.memberType(t2, sym).getParameterTypes() + ")");
            }
            return false;
        }
        return true;
    }

    /** Return the first method which is defined with same args
     *  but different return types in two given interfaces, or null if none
     *  exists.
     *  @param t1     The first type.
     *  @param t2     The second type.
     *  @param site   The most derived type.
     *  @returns symbol from t2 that conflicts with one in t1.
     */
    private Symbol firstIncompatibility(Type t1, Type t2, Type site) {
	Map<TypeSymbol,Type> interfaces1 = new HashMap<TypeSymbol,Type>();
	closure(t1, interfaces1);
	Map<TypeSymbol,Type> interfaces2;
	if (t1 == t2)
	    interfaces2 = interfaces1;
	else
	    closure(t2, interfaces1, interfaces2 = new HashMap<TypeSymbol,Type>());

	for (Type t3 : interfaces1.values()) {
	    for (Type t4 : interfaces2.values()) {
		Symbol s = firstDirectIncompatibility(t3, t4, site);
		if (s != null) return s;
	    }
	}
	return null;
    }

    /** Compute all the supertypes of t, indexed by type symbol. */
    private void closure(Type t, Map<TypeSymbol,Type> typeMap) {
	if (t.tag != CLASS) return;
	if (typeMap.put(t.tsym, t) == null) {
	    closure(types.supertype(t), typeMap);
	    for (Type i : types.interfaces(t))
		closure(i, typeMap);
	}
    }

    /** Compute all the supertypes of t, indexed by type symbol (except thise in typesSkip). */
    private void closure(Type t, Map<TypeSymbol,Type> typesSkip, Map<TypeSymbol,Type> typeMap) {
	if (t.tag != CLASS) return;
	if (typesSkip.get(t.tsym) != null) return;
	if (typeMap.put(t.tsym, t) == null) {
	    closure(types.supertype(t), typesSkip, typeMap);
	    for (Type i : types.interfaces(t))
		closure(i, typesSkip, typeMap);
	}
    }

    /** Return the first method in t2 that conflicts with a method from t1. */
    private Symbol firstDirectIncompatibility(Type t1, Type t2, Type site) {
        Symbol s = firstDirectMethodIncompatibility(t1, t2, site);
        if (s != null) {
            return s;
        }
        else {
            return firstDirectVarIncompatibility(t1, t2, site);
        }
    }

    private Symbol firstDirectMethodIncompatibility(Type t1, Type t2, Type site) {
	for (Scope.Entry e1 = t1.tsym.members().elems; e1 != null; e1 = e1.sibling) {
	    Symbol s1 = e1.sym;
            s1.complete();
	    Type st1 = null;
	    if (s1.kind != MTH || s1.name == defs.internalRunFunctionName ||
                    !s1.isInheritedIn(site.tsym, types)) continue;
	    for (Scope.Entry e2 = t2.tsym.members().lookup(s1.name); e2.scope != null; e2 = e2.next()) {
		Symbol s2 = e2.sym;
                s2.complete();
		if (s1 == s2) continue;
		if (s2.kind != MTH || !s2.isInheritedIn(site.tsym, types)) continue;
		if (st1 == null) st1 = types.memberType(t1, s1);
		Type st2 = types.memberType(t2, s2);
		if (types.overrideEquivalent(st1, st2)) {
		    List<Type> tvars1 = st1.getTypeArguments();
		    List<Type> tvars2 = st2.getTypeArguments();
		    Type rt1 = st1.getReturnType();
		    Type rt2 = types.subst(st2.getReturnType(), tvars2, tvars1);
		    boolean compat =
			types.isSameType(rt1, rt2) ||
                        rt1.tag >= CLASS && rt2.tag >= CLASS &&
                        (types.covariantReturnType(rt1, rt2, Warner.noWarnings) ||
                         types.covariantReturnType(rt2, rt1, Warner.noWarnings));
		    if (!compat) return s2;
		}
	    }
	}
	return null;
    }

    private Symbol firstDirectVarIncompatibility(Type t1, Type t2, Type site) {
        for (Scope.Entry e1 = t1.tsym.members().elems; e1 != null; e1 = e1.sibling) {
            Symbol s1 = e1.sym;
            s1.complete();
            Type st1 = null;
            if (s1.kind != VAR ||
                    !s1.isInheritedIn(site.tsym, types)) continue;
            for (Scope.Entry e2 = t2.tsym.members().lookup(s1.name); e2.scope != null; e2 = e2.next()) {
                Symbol s2 = e2.sym;
                s2.complete();
                if (s1 == s2) continue;
                if (s2.kind != VAR || !s2.isInheritedIn(site.tsym, types)) continue;
                if (!types.isSameType(s1.type, s2.type)) {
                    return s2;
                }
            }
        }
        return null;
    }

    /** Check that a given method conforms with any method it overrides.
     *  @param tree         The tree from which positions are extracted
     *			    for errors.
     *  @param m            The overriding method.
     */
    void checkOverride(F3Tree tree, MethodSymbol m) {
        ClassSymbol origin = (ClassSymbol) m.owner;
	origin.complete();
	//System.err.println("check override: "+tree+": "+types.toString(m.type));
        boolean doesOverride = false;
        if ((origin.flags() & ENUM) != 0 && names.finalize.equals(m.name)) {
            if (m.overrides(syms.enumFinalFinalize, origin, types, false)) {
                log.error(tree.pos(), MsgSym.MESSAGE_ENUM_NO_FINALIZE);
                return;
            }
        }
        for (Type t : types.supertypesClosure(origin.type)) {
	    //System.err.println("st="+t);
	    //System.err.println("origin="+origin.type);
            if (t.tag == CLASS) {
                TypeSymbol c = t.tsym;
                Scope.Entry e = c.members().lookup(m.name);
		//System.err.println("lookup "+ m.name+"="+e +" in "+c.members());
                while (e.scope != null) {
                    e.sym.complete();
		    //System.err.println("other="+types.memberType(origin.type, e.sym));
                    if (types.overrides(m, e.sym, origin, false)) { // hack
                        checkOverride(tree, m, (MethodSymbol)e.sym, origin);
                        doesOverride = !e.sym.type.getReturnType().isErroneous();
                    }  else {
			//System.err.println("doesn't override: "+e.sym);
		    }
                    e = e.next();
                }
            }
        }
        boolean declaredOverride = (m.flags() & OVERRIDE) != 0;
        if (doesOverride) {
            if (!declaredOverride && (m.flags() & (Flags.SYNTHETIC|Flags.STATIC)) == 0) {
                log.warning(tree.pos(), MsgSym.MESSAGE_F3_SHOULD_BE_DECLARED_OVERRIDE, types.toF3String(m.type));
            }
        } else {
            if (declaredOverride) {
                log.error(tree.pos(), MsgSym.MESSAGE_F3_DECLARED_OVERRIDE_DOES_NOT, m.name/*rs.kindName(m)*/, types.toF3String(m.type));
            }
        }
    }
    
    
    /** Check to make sure that any mixins don't create var conflicts.
     */
    void checkMixinConflicts(F3ClassDeclaration tree) {
        for (F3Expression mixin : tree.getMixing()) {
            if (mixin instanceof F3Ident) {
                Symbol symbol = ((F3Ident)mixin).sym;
                if ((symbol.flags_field & F3Flags.MIXIN) != 0) {
                    ClassSymbol mixinSym = (ClassSymbol)symbol;
                    Scope s = mixinSym.members();
                    for (Scope.Entry e = s.elems; e != null; e = e.sibling) {
                        if (e.sym.kind == VAR) {
                            checkVarOverride(mixin.pos(), (F3VarSymbol)e.sym, tree.sym, false);
                        }
                    }
               }
            }
        }
    }

    /** Check that var/def does not override (unless it is hidden by being script private)
     */
    void checkVarOverride(DiagnosticPosition diagPos, F3VarSymbol vsym) {
        checkVarOverride(diagPos, vsym, (ClassSymbol)vsym.owner, true);
    }
    void checkVarOverride(DiagnosticPosition diagPos, F3VarSymbol vsym, ClassSymbol origin, boolean overrides) {
        for (Type t : types.supertypesClosure(origin.type)) {
            if (t.tag == CLASS) {
                TypeSymbol c = t.tsym;

                for (Scope.Entry e = c.members().lookup(vsym.name); e.scope != null; e = e.next()) {
                    Symbol eSym = e.sym;
                    eSym.complete();
                    if (!(eSym.owner instanceof ClassSymbol)) continue;
                    
                    long flags = eSym.flags_field;
                    boolean isNotScriptPrivate = (flags & F3Flags.SCRIPT_PRIVATE) == 0L;
                    boolean isPublicRead = (flags & (F3Flags.PUBLIC_READ|F3Flags.PUBLIC_INIT)) != 0L;
                    boolean isScriptScope = origin.outermostClass() == ((ClassSymbol) eSym.owner).outermostClass();
                    
                    if (isNotScriptPrivate || isPublicRead || isScriptScope) {
                        // We have a name clash, the variable name is the name of a member
                        // which is visible outside the script or which is in the same script
                        if (!types.isF3Class(eSym.owner)) {
                            log.error(diagPos, (vsym.flags_field & F3Flags.IS_DEF) == 0L?
                                   MsgSym.MESSAGE_F3_VAR_OVERRIDES_JAVA_MEMBER :
                                   MsgSym.MESSAGE_F3_DEF_OVERRIDES_JAVA_MEMBER,
                                eSym,
                                eSym.owner);
                        } else if (overrides) {
                            log.error(diagPos, (vsym.flags_field & F3Flags.IS_DEF) == 0L?
                                   MsgSym.MESSAGE_F3_VAR_OVERRIDES_MEMBER :
                                   MsgSym.MESSAGE_F3_DEF_OVERRIDES_MEMBER,
                                eSym,
                                eSym.owner);
                        }
                        return;
                    }
                }
            }
        }
    }

    /** Check that all abstract members of given class have definitions.
     *  @param pos          Position to be used for error reporting.
     *  @param c            The class.
     */
    void checkAllDefined(DiagnosticPosition pos, ClassSymbol c) {
	try {
	    MethodSymbol undef = firstUndef(c, c);
	    if (undef != null) {
                if ((c.flags() & ENUM) != 0 &&
                    types.supertype(c.type).tsym == syms.enumSym &&
                    (c.flags() & FINAL) == 0) {
                    // add the ABSTRACT flag to an enum
                    c.flags_field |= ABSTRACT;
                } else {
                    MethodSymbol undef1 =
                        new MethodSymbol(undef.flags(), undef.name,
                                         types.memberType(c.type, undef), undef.owner);
                    log.error(pos, MsgSym.MESSAGE_DOES_NOT_OVERRIDE_ABSTRACT,
                              c, types.toF3String(undef1, List.<VarSymbol>nil()), undef1.location());
                }
            }
	} catch (CompletionFailure ex) {
	    completionError(pos, ex);
	}
    }
//where
        /** Return first abstract member of class `c' that is not defined
	 *  in `impl', null if there is none.
	 */
	private MethodSymbol firstUndef(ClassSymbol impl, ClassSymbol c) {
	    MethodSymbol undef = null;
	    // Do not bother to search in classes that are not abstract,
	    // since they cannot have abstract members.
	    if (c == impl || (c.flags() & (ABSTRACT | INTERFACE | MIXIN)) != 0) {
		Scope s = c.members();
		for (Scope.Entry e = s.elems;
		     undef == null && e != null;
		     e = e.sibling) {
		    if (e.sym.kind == MTH &&
			(e.sym.flags() & (ABSTRACT|IPROXY)) == ABSTRACT) {
			MethodSymbol absmeth = (MethodSymbol)e.sym;
			MethodSymbol implmeth = types.implementation(absmeth, impl, true);
			if (implmeth == null || implmeth == absmeth) {
                            undef = absmeth;
                        }
		    }
		}
		if (undef == null) {
		    Type st = types.supertype(c.type);
		    if (st.tag == CLASS)
			undef = firstUndef(impl, (ClassSymbol)st.tsym);
		}
		for (List<Type> l = types.interfaces(c.type);
		     undef == null && l.nonEmpty();
		     l = l.tail) {
		    if (l.head.tsym instanceof ClassSymbol) {
			undef = firstUndef(impl, (ClassSymbol)l.head.tsym);
		    }
		}
	    }
            return undef;
	}

    /** Check for cyclic references. Issue an error if the
     *  symbol of the type referred to has a LOCKED flag set.
     *
     *  @param pos      Position to be used for error reporting.
     *  @param t        The type referred to.
     */
    void checkNonCyclic(DiagnosticPosition pos, Type t) {
	checkNonCyclicInternal(pos, t, false);
    }


    void checkNonCyclic(DiagnosticPosition pos, TypeVar t) {
        checkNonCyclic1(pos, t, new HashSet<TypeVar>());
    }

    private void checkNonCyclic1(DiagnosticPosition pos, Type t, Set<TypeVar> seen) {
        final TypeVar tv;
        if (seen.contains(t)) {
            tv = (TypeVar)t;
            tv.bound = new ErrorType();
            log.error(pos, MsgSym.MESSAGE_CYCLIC_INHERITANCE, t);
        } else if (t.tag == TYPEVAR) {
            tv = (TypeVar)t;
            seen.add(tv);
            for (Type b : types.getBounds(tv))
                checkNonCyclic1(pos, b, seen);
        }
    }

    /** Check for cyclic references. Issue an error if the
     *  symbol of the type referred to has a LOCKED flag set.
     *
     *  @param pos      Position to be used for error reporting.
     *  @param t        The type referred to.
     *  @returns        True if the check completed on all attributed classes
     */
    private boolean checkNonCyclicInternal(DiagnosticPosition pos, Type t, boolean ownerCycle) {
	boolean complete = true; // was the check complete?
	//- System.err.println("checkNonCyclicInternal("+t+");");//DEBUG
	Symbol c = t.tsym;
	if ((c.flags_field & ACYCLIC) != 0) return true;

	if ((c.flags_field & LOCKED) != 0) {
	    noteCyclic(pos, (ClassSymbol)c, ownerCycle);
	} else if (!c.type.isErroneous()) {
	    try {
		c.flags_field |= LOCKED;
		if (c.type.tag == CLASS) {
		    ClassType clazz = (ClassType)c.type;
		    if (clazz.interfaces_field != null)
			for (List<Type> l=clazz.interfaces_field; l.nonEmpty(); l=l.tail)
			    complete &= checkNonCyclicInternal(pos, l.head, ownerCycle);
		    if (clazz.supertype_field != null) {
			Type st = clazz.supertype_field;
			if (st != null && st.tag == CLASS)
			    complete &= checkNonCyclicInternal(pos, st, ownerCycle);
		    }
		    if (c.owner.kind == TYP)
			complete &= checkNonCyclicInternal(pos, c.owner.type, true);
		}
	    } finally {
		c.flags_field &= ~LOCKED;
	    }
	}
	if (complete)
	    complete = ((c.flags_field & UNATTRIBUTED) == 0) && c.completer == null;
	if (complete) c.flags_field |= ACYCLIC;
	return complete;
    }

    /** Note that we found an inheritance cycle. */
    private void noteCyclic(DiagnosticPosition pos, ClassSymbol c, boolean ownerCycle) {
	if (!ownerCycle)
        log.error(pos, MsgSym.MESSAGE_CYCLIC_INHERITANCE, c);
    else
        log.error(pos, MsgSym.MESSAGE_CANNOT_INHERIT_FROM_SCRIPT_CLASS, c);
	for (List<Type> l=types.interfaces(c.type); l.nonEmpty(); l=l.tail)
	    l.head = new ErrorType((ClassSymbol)l.head.tsym);
	Type st = types.supertype(c.type);
	if (st.tag == CLASS)
	    ((ClassType)c.type).supertype_field = new ErrorType((ClassSymbol)st.tsym);
	c.type = new ErrorType(c);
	c.flags_field |= ACYCLIC;
    }

    /** Check that all methods which implement some
     *  method conform to the method they implement.
     *  @param tree         The class definition whose members are checked.
     */
    void checkImplementations(F3ClassDeclaration tree) {
	checkImplementations(tree, tree.sym);
    }
//where
        /** Check that all methods which implement some
	 *  method in `ic' conform to the method they implement.
	 */
	void checkImplementations(F3ClassDeclaration tree, ClassSymbol ic) {
	    ClassSymbol origin = tree.sym;
	    for (List<Type> l = types.closure(ic.type); l.nonEmpty(); l = l.tail) {
		if (!(l.head.tsym instanceof ClassSymbol)) {
		    continue;
		}
		ClassSymbol lc = (ClassSymbol)l.head.tsym;
		if ((allowGenerics || origin != lc) && (lc.flags() & (ABSTRACT|MIXIN)) != 0) {
		    for (Scope.Entry e=lc.members().elems; e != null; e=e.sibling) {
			if (e.sym.kind == MTH &&
			    (e.sym.flags() & (STATIC|ABSTRACT)) == ABSTRACT) {
			    MethodSymbol absmeth = (MethodSymbol)e.sym;
			    MethodSymbol implmeth = types.implementation(absmeth, origin, false);
			    if (implmeth != null && implmeth != absmeth &&
				(implmeth.owner.flags() & INTERFACE) ==
				(origin.flags() & INTERFACE)) {
				// don't check if implmeth is in a class, yet
				// origin is an interface. This case arises only
				// if implmeth is declared in Object. The reason is
				// that interfaces really don't inherit from
				// Object it's just that the compiler represents
				// things that way.
				checkOverride(tree, implmeth, absmeth, origin);
			    }
			}
		    }
		}
	    }
	}
    

    /** Check that only one extend class is a java or f3 base class.
     *  @param tree         The class definition whose extends are checked.
     **/
     void checkOneBaseClass(F3ClassDeclaration tree) {
        // Get the list of non-mixin extends.
        List<F3Expression> extending = tree.getExtending();
        
        // If there is more than one then we have too many.
        if (extending.size() > 1) {
            // Get the first extra for error position.
            F3Expression extra = extending.get(1);
            log.error(extra.pos(),
                MsgSym.MESSAGE_F3_ONLY_ONE_BASE_CLASS_ALLOWED);
        }
    }
    
    /** Check that a mixin class is pure of other modifiers.
     *  @param pos          Position to be used for error reporting.
     *  @param c            The class whose modifiers are checked.
     **/
    void checkPureMixinClass(DiagnosticPosition pos, ClassSymbol c) {
        if ((c.flags() & ABSTRACT) != 0) {
            log.error(pos, MsgSym.MESSAGE_F3_PURE_MIXIN);
        }
    }

    /** Check that a mixin class has only mixin extends.
     *  @param tree         The class definition whose extends are checked.
     **/
    void checkOnlyMixinsAndInterfaces(F3ClassDeclaration tree) {
        // Get the list of non-mixin extends.
        List<F3Expression> extending = tree.getExtending();
        
        // Any is too many.
        if (extending.size() > 0) {
            // Get the first extra for error position.
            F3Expression extra = extending.get(0);
	    if (false)
            log.error(extra.pos(),
                MsgSym.MESSAGE_F3_ONLY_MIXINS_AND_INTERFACES);
        }
    }

    /** Check that all abstract methods implemented by a class are
     *  mutually compatible.
     *  @param pos          Position to be used for error reporting.
     *  @param c            The class whose interfaces are checked.
     */
    void checkCompatibleSupertypes(DiagnosticPosition pos, Type c) {
        List<Type> supertypes = types.interfaces(c);
        Type supertype = types.supertype(c);
        if (supertype.tag == CLASS &&
                (supertype.tsym.flags() & (ABSTRACT|MIXIN)) != 0) {
            supertypes = supertypes.prepend(supertype);
        }
        for (List<Type> l = supertypes; l.nonEmpty(); l = l.tail) {
	    /*
            if (allowGenerics && !l.head.getTypeArguments().isEmpty() &&
                    !checkCompatibleAbstracts(pos, l.head, l.head, c)) {
                return;
            }
	    */
            for (List<Type> m = supertypes; m != l; m = m.tail) {
                if (!checkCompatibleAbstracts(pos, l.head, m.head, c)) {
                    return;
                }
            }
        }
        checkCompatibleConcretes(pos, c);
    }

    /** Check that class c does not implement directly or indirectly
     *  the same parameterized interface with two different argument lists.
     *  @param pos          Position to be used for error reporting.
     *  @param type         The type whose interfaces are checked.
     */
    void checkClassBounds(DiagnosticPosition pos, Type type) {
	checkClassBounds(pos, new HashMap<TypeSymbol,Type>(), type);
    }
//where
        /** Enter all interfaces of type `type' into the hash table `seensofar'
	 *  with their class symbol as key and their type as value. Make
	 *  sure no class is entered with two different types.
	 */
	void checkClassBounds(DiagnosticPosition pos,
			      Map<TypeSymbol,Type> seensofar,
			      Type type) {
	    if (type.isErroneous()) return;
	    List<Type> l = types.interfaces(type);
	    //System.err.println("check class bounds: "+type);
	    //System.err.println("interfaces: "+l);
	    for ( ; l.nonEmpty(); l = l.tail) {
		Type it = l.head;
		if (types.isTypeConsType(it) >= 0) {
		    it = types.erasure(it);
		}
		Type oldit = seensofar.put(it.tsym, it);
		if (oldit != null) {
		    List<Type> oldparams = oldit.allparams();
		    List<Type> newparams = it.allparams();
		    if (false && !types.containsTypeEquivalent(oldparams, newparams))
			{
			    log.error(pos, MsgSym.MESSAGE_CANNOT_INHERIT_DIFF_ARG,
				      it.tsym, Type.toString(oldparams),
				      Type.toString(newparams));
			}
		}
		checkClassBounds(pos, seensofar, it);
	    }
	    Type st = types.supertype(type);
	    if (st != null) checkClassBounds(pos, seensofar, st);
	}

    /** Enter interface or mixin into into set.
     *  If the class is a duplicate, issue a "repeated interface/mixin" error.
     */
    void checkNotRepeated(DiagnosticPosition pos, Type it, Set<Type> its) {
        // if class is already in the set.
        if (its.contains(it)) {
            if (false) {
                // If class is a mixin.
                if ((it.tsym.flags_field & F3Flags.MIXIN) != 0)
                    log.error(pos, MsgSym.MESSAGE_F3_REPEATED_MIXIN);
                else
                    log.error(pos, MsgSym.MESSAGE_REPEATED_INTERFACE);
            }
        } else {
        	  its.add(it);
        }
    }
	
/* *************************************************************************
 * Miscellaneous
 **************************************************************************/
    /**
     * Return the opcode of the operator but emit an error if it is an
     * error.
     * @param pos        position for error reporting.
     * @param operator   an operator
     * @param tag        a tree tag
     * @param left       type of left hand side
     * @param right      type of right hand side
     */
    int checkOperator(DiagnosticPosition pos,
                       OperatorSymbol operator,
                       F3Tag tag,
                       Type left,
                       Type right) {
        if (operator.opcode == ByteCodes.error) {
            log.error(pos,
                      MsgSym.MESSAGE_OPERATOR_CANNOT_BE_APPLIED,
                      treeinfo.operatorName(tag),
                      left + "," + right);
        }
        return operator.opcode;
    }


    /**
     *  Check for division by integer constant zero
     *	@param pos	     Position for error reporting.
     *	@param operator      The operator for the expression
     *	@param operand       The right hand operand for the expression
     */
    void checkDivZero(DiagnosticPosition pos, Symbol operator, Type operand) {
	if (operand.constValue() != null
	    && lint.isEnabled(Lint.LintCategory.DIVZERO)
	    && operand.tag <= LONG
	    && ((Number) (operand.constValue())).longValue() == 0) {
	    int opc = ((OperatorSymbol)operator).opcode;
	    if (opc == ByteCodes.idiv || opc == ByteCodes.imod 
		|| opc == ByteCodes.ldiv || opc == ByteCodes.lmod) {
		log.warning(pos, MsgSym.MESSAGE_DIV_ZERO);
	    }
	}
    }

    /** Check that symbol is unique in given scope.
     *	@param pos	     Position for error reporting.
     *	@param sym	     The symbol.
     *	@param s	     The scope.
     */
    boolean checkUnique(DiagnosticPosition pos, Symbol sym, F3Env<F3AttrContext> env) {
        boolean shouldContinue = true;
        do {
            shouldContinue = !attr.isClassOrFuncDef(env, false);
            if (!checkUnique(pos, sym, F3Enter.enterScope(env)))
                return false;
            env = env.outer;
        } while (env != null && shouldContinue);
        return true;
    }
    
    boolean checkUnique(DiagnosticPosition pos, Symbol sym, Scope s) {
        if (sym.type != null && sym.type.isErroneous()) {
            return true;
        }
        if (sym.owner.name == names.any) {
            return false;
        }
        for (Scope.Entry e = s.lookup(sym.name); e.scope == s; e = e.next()) {
            sym.complete();
            if (sym != e.sym &&
                    sym.kind == e.sym.kind &&
                    sym.name != names.error &&
                    (sym.kind != MTH || types.overrideEquivalent(sym.type, e.sym.type))) {
                if ((sym.flags() & VARARGS) != (e.sym.flags() & VARARGS)) {
                    varargsDuplicateError(pos, sym, e.sym);
                } else {
                    duplicateError(pos, e.sym);
                }
                return false;
            }
        }
        return true;
    }

    /** Check that single-type import is not already imported or top-level defined,
     *	but make an exception for two single-type imports which denote the same type.
     *	@param pos	     Position for error reporting.
     *	@param sym	     The symbol.
     *	@param s	     The scope
     */
    boolean checkUniqueImport(DiagnosticPosition pos, Symbol sym, Scope s) {
	return checkUniqueImport(pos, sym, s, false);
    }

    /** Check that static single-type import is not already imported or top-level defined,
     *	but make an exception for two single-type imports which denote the same type.
     *	@param pos	     Position for error reporting.
     *	@param sym	     The symbol.
     *	@param s	     The scope
     *  @param staticImport  Whether or not this was a static import
     */
    boolean checkUniqueStaticImport(DiagnosticPosition pos, Symbol sym, Scope s) {
	return checkUniqueImport(pos, sym, s, true);
    }

    /** Check that single-type import is not already imported or top-level defined,
     *	but make an exception for two single-type imports which denote the same type.
     *	@param pos	     Position for error reporting.
     *	@param sym	     The symbol.
     *	@param s	     The scope.
     *  @param staticImport  Whether or not this was a static import
     */
    private boolean checkUniqueImport(DiagnosticPosition pos, Symbol sym, Scope s, boolean staticImport) {
	for (Scope.Entry e = s.lookup(sym.name); e.scope != null; e = e.next()) {
	    // is encountered class entered via a class declaration?
	    boolean isClassDecl = e.scope == s;
	    if ((isClassDecl || sym != e.sym) &&
		sym.kind == e.sym.kind &&
		sym.name != names.error) {
		if (!e.sym.type.isErroneous()) {
		    String what = e.sym.toString();
		    if (!isClassDecl) {
			if (staticImport)
			    log.error(pos, MsgSym.MESSAGE_ALREADY_DEFINED_STATIC_SINGLE_IMPORT, what);
			else
			    log.error(pos, MsgSym.MESSAGE_ALREADY_DEFINED_SINGLE_IMPORT, what);
		    }
		    else if (sym != e.sym)
			log.error(pos, MsgSym.MESSAGE_ALREADY_DEFINED_THIS_UNIT, what);
		}
		return false;
	    }
	}
	return true;
    }

    /** Check that a qualified name is in canonical form (for import decls).
     */
    public void checkCanonical(F3Tree tree) {
	if (!isCanonical(tree))
	    log.error(tree.pos(), MsgSym.MESSAGE_IMPORT_REQUIRES_CANONICAL,
		      F3TreeInfo.symbol(tree));
    }
        // where
	private boolean isCanonical(F3Tree tree) {
	    while (tree.getF3Tag() == F3Tag.SELECT) {
		F3Select s = (F3Select) tree;
		if (s.sym.owner != F3TreeInfo.symbol(s.selected))
		    return false;
		tree = s.selected;
	    }
	    return true;
	}

    private class ConversionWarner extends Warner {
        final String key;
	final Type found;
        final Type expected;
	public ConversionWarner(DiagnosticPosition pos, String key, Type found, Type expected) {
            super(pos);
            this.key = key;
	    this.found = found;
	    this.expected = expected;
	}

	@Override
	public void warnUnchecked() {
            boolean localWarned = this.warned;
            super.warnUnchecked();
            if (localWarned) return; // suppress redundant diagnostics
	    Object problem = JCDiagnostic.fragment(key);
	    F3Check.this.warnUnchecked(pos(), MsgSym.MESSAGE_PROB_FOUND_REQ, problem, found, expected);
	}
    }

    public Warner castWarner(DiagnosticPosition pos, Type found, Type expected) {
	return new ConversionWarner(pos, MsgSym.MESSAGE_UNCHECKED_CAST_TO_TYPE, found, expected);
    }

    public Warner convertWarner(DiagnosticPosition pos, Type found, Type expected) {
	return new ConversionWarner(pos, MsgSym.MESSAGE_UNCHECKED_ASSIGN, found, expected);
    }
	
    public void warnEmptyRangeLiteral(DiagnosticPosition pos, F3Literal lower, F3Literal upper, F3Literal step, boolean isExclusive) {
    double lowerValue = ((Number)lower.getValue()).doubleValue();
    double upperValue = ((Number)upper.getValue()).doubleValue();
    double stepValue = step != null? ((Number)step.getValue()).doubleValue() : 1;
    if ((stepValue > 0 && lowerValue > upperValue)
            || (stepValue < 0 && lowerValue < upperValue)
            || (isExclusive && lowerValue == upperValue)) {
        log.warning(pos, MsgSym.MESSAGE_F3_RANGE_LITERAL_EMPTY);
            }
    }
        
    public Type checkFunctionType(DiagnosticPosition pos, MethodType m) {
        if (m.argtypes.length() > F3Symtab.MAX_FIXED_PARAM_LENGTH) {
            log.error(pos, MsgSym.MESSAGE_TOO_MANY_PARAMETERS);
            return syms.errType;
        } else {
            return syms.makeFunctionType(m);
        }
    }

    public void checkForwardReferences(F3Tree tree) {
        final boolean onlyWarnings = options.get("fwdRefError") != null &&
			options.get("fwdRefError").contains("false");

        new ForwardReferenceChecker(names, types, defs, getForwardRefKinds()) {
            @Override
            protected void reportForwardReference(DiagnosticPosition pos, boolean selfReference, Symbol s, boolean potential) {
                JCDiagnostic description =
                        diags.fragment(selfReference ?
                            MsgSym.MESSAGE_F3_SELF_REFERENCE :
                            MsgSym.MESSAGE_F3_FORWARD_REFERENCE);
                if (potential) {
                    log.warning(pos,
                            MsgSym.MESSAGE_MAYBE_FORWARD_REF,
                            description,
                            rs.kindName(VAR),
                            s);
                }
                else if (onlyWarnings) {
                    log.warning(pos,
                            MsgSym.MESSAGE_ILLEGAL_FORWARD_REF,
                            description,
                            rs.kindName(VAR),
                            s);
                }
                else {
                    log.error(pos,
                            MsgSym.MESSAGE_ILLEGAL_FORWARD_REF,
                            description,
                            rs.kindName(VAR),
                            s);
                }
            }
        }.scan(tree);
    }

    public abstract static class ForwardReferenceChecker extends F3TreeScanner {

	protected ForwardReferenceChecker(Name.Table names, F3Types types, F3Defs defs, Collection<ScopeKind> kinds) {
	    this.names = names;
	    this.types = types;
	    this.defs = defs;
	    this.optionalKinds = kinds;
	}

	Name.Table names;
	Types types;
	F3Defs defs;
	List<VarScope> scopes = List.nil();
	ClassSymbol enclClass = null;
	Collection<ScopeKind> optionalKinds;

	public enum ScopeKind {
	    CLASS(false, false),
	    FUNCTION_DEF(false, false),
	    VAR_DEF(false, true),
	    BLOCK_EXPR(false, true),
	    ON_REPLACE(true, false),
	    ON_INVALIDATE(true, false),
	    FUNCTION_VALUE(true, false),
	    INTERPOLATE_VALUE(true, false),
	    KEYFRAME_LIT(true, false),
	    BOUND_CTX(true, false),
	    ASSIGN_CTX(true, false),
	    OBJ_LIT(true, false),
            OBJ_LIT_FUNC(true, false);

            boolean optional;
            boolean defaultTransparent;

            ScopeKind(boolean optional, boolean defaultTransparent) {
                this.optional = optional;
                this.defaultTransparent = defaultTransparent;
            }

            public boolean isOptional() {
                return optional;
            }

            public boolean isDefaultTransparent() {
                return defaultTransparent;
            }
	}

	protected class VarScope {
            VarScope(ScopeKind kind) {
                this.kind = kind;
            }
            VarScope(ScopeKind kind, VarScope prevScope) {
                this(kind);
                this.prevScope = prevScope;
            }
            ScopeKind kind;
            boolean isStatic = false;
            VarScope prevScope = null;
            Set<F3VarSymbol> uninited_vars = new LinkedHashSet<F3VarSymbol>();
            F3VarSymbol currentVar = null;
            int overrideVarIdx = Integer.MAX_VALUE;
	}

	@Override
	public void visitClassDeclaration(F3ClassDeclaration tree) {
	    ClassSymbol prevClass = enclClass;
	    try {
		enclClass = tree.sym;
		beginScope(isObjLiteral(tree.sym) ?
                    ScopeKind.OBJ_LIT :
                    ScopeKind.CLASS
                );
		addVars(tree.getMembers());
                currentScope().isStatic = tree.sym.isStatic();
		super.visitClassDeclaration(tree);
		endScope();
	    }
	    finally {
		enclClass = prevClass;
	    }
	}

	@Override
	public void visitFunctionValue(F3FunctionValue tree) {
            boolean isLambda = tree.definition.sym.name.equals(defs.lambda_MethodName);
            beginScope(isLambda ?
                ScopeKind.FUNCTION_VALUE :
                isObjLiteral(tree.definition.sym.owner) ?
                    ScopeKind.OBJ_LIT_FUNC :
                    ScopeKind.FUNCTION_DEF);
            if (!isLambda) {
                currentScope().isStatic = tree.definition.sym.isStatic();
            }
	    super.visitFunctionValue(tree);
	    endScope();
	}

	@Override
	public void visitOnReplace(F3OnReplace tree) {
	    beginScope(tree.getTriggerKind() == F3OnReplace.Kind.ONREPLACE ?
                ScopeKind.ON_REPLACE :
                ScopeKind.ON_INVALIDATE);
	    super.visitOnReplace(tree);
	    endScope();
	}

	@Override
	public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
	    beginScope(ScopeKind.KEYFRAME_LIT);
	    super.visitKeyFrameLiteral(tree);
	    endScope();
	}

	@Override
	public void visitInterpolateValue(F3InterpolateValue tree) {
	    beginScope(ScopeKind.INTERPOLATE_VALUE);
	    super.visitInterpolateValue(tree);
	    endScope();
	}

	@Override
	public void visitInitDefinition(F3InitDefinition tree) {
	    beginScope(ScopeKind.FUNCTION_DEF);
	    super.visitInitDefinition(tree);
	    endScope();
	}

	@Override
	public void visitPostInitDefinition(F3PostInitDefinition tree) {
	    beginScope(ScopeKind.FUNCTION_DEF);
	    super.visitPostInitDefinition(tree);
	    endScope();
	}

	@Override
	public void visitBlockExpression(F3Block tree) {
            beginScope(ScopeKind.BLOCK_EXPR);
	    addVars(tree.stats);
	    addVar(tree.value);
	    super.visitBlockExpression(tree);
            endScope();
	}

        @Override
	public void visitVar(F3Var tree) {
	    if (tree.getSymbol() != null) {
                beginScope(ScopeKind.VAR_DEF);
                currentScope().isStatic = tree.getSymbol().isStatic();
                currentScope().currentVar = tree.getSymbol();
                super.scan(tree.getInitializer());
                removeVar(tree.getSymbol());
                super.scan(tree.getOnReplace());
                super.scan(tree.getOnInvalidate());
                endScope();
	    }
	}

	@Override
	public void visitAssign(F3Assign tree) {
	    Symbol s = F3TreeInfo.symbolFor(tree.lhs);
	    F3VarSymbol vsym = (s != null && s.kind == VAR) ?
		(F3VarSymbol)s : null;
	    if (vsym != null) {
                beginScope(ScopeKind.ASSIGN_CTX);
                scan(tree.lhs);
                endScope();
                scan(tree.rhs);
	    }
            else {
                super.visitAssign(tree);
            }
	}

	@Override
	public void visitOverrideClassVar(F3OverrideClassVar tree) {
	    if (tree.getSymbol() != null) {
                beginScope(ScopeKind.VAR_DEF);
		currentScope().overrideVarIdx =
                        tree.getSymbol().getAbsoluteIndex(enclClass.type);
                currentScope().currentVar = tree.getSymbol();
                currentScope().isStatic = tree.getSymbol().isStatic();
                scan(tree.getInitializer());
                currentScope().overrideVarIdx++;
                super.scan(tree.getOnReplace());
                super.scan(tree.getOnInvalidate());
		endScope();
	    }
	}

	@Override
	public void visitIdent(F3Ident tree) {
	    checkForwardReference(tree, tree.sym);
	}

	@Override
	public void visitSelect(F3Select tree) {
	    Symbol selectedSym = F3TreeInfo.symbolFor(tree.selected);
	    boolean shouldCheck = false;
	    if (selectedSym != null) {
		if (selectedSym.kind == TYP) {
		    shouldCheck = true;
		}
		if (selectedSym.kind == VAR) {
		    if (selectedSym.name == names._this ||
			    selectedSym.name == names._super) {
			shouldCheck = true;
		    }
		}
		if (shouldCheck) {
		    checkForwardReference(tree, tree.sym);
		}
	    }

	    super.visitSelect(tree);
	}

	@Override
	public void visitVarInit(F3VarInit tree) {
	    removeVar(tree.getSymbol());
	}

	private void checkForwardReference(F3Expression tree, Symbol s) {
            checkForwardReference(currentScope(), tree, s, false);
        }

	private void checkForwardReference(VarScope scope, F3Expression tree, Symbol s, boolean optional) {
	    if ((!tree.isBound() || optionalKinds.contains(ScopeKind.BOUND_CTX)) &&
                    s != null && s instanceof F3VarSymbol) {
		F3VarSymbol vsym = (F3VarSymbol)s;
		if (scope.currentVar == s) {
                    reportForwardReference(tree, true, vsym, tree.isBound() || optional);
                }
                else if (vsym.name != names._this &&
			vsym.name != names._super &&
			(isForwardReferenceInSameClass(scope, vsym) ||
                        isForwardReferenceInSubclass(scope, vsym))) {
		    reportForwardReference(tree, false, vsym, tree.isBound() || optional);
		}
                else if (isTransparent(scope.kind) && scope.prevScope != null) {
                    checkForwardReference(scope.prevScope, tree, s, optional || scope.kind.isOptional());
                }
	    }
	}
        //where
        private boolean isForwardReferenceInSameClass(VarScope scope, F3VarSymbol vsym) {
            return scope.uninited_vars.contains(vsym) &&
                    vsym.isStatic() == currentScope().isStatic;
        }
        //where
        private boolean isForwardReferenceInSubclass(VarScope scope, F3VarSymbol vsym) {
            int refIdx = vsym.getAbsoluteIndex(enclClass.type);
            return scope.overrideVarIdx <= refIdx &&
                    vsym.isMember() &&
                    enclClass.isSubClass(vsym.owner, types);
        }

	private void beginScope(ScopeKind kind) {
	    VarScope prevScope = currentScope();
	    VarScope newScope = isTransparent(kind) ?
		new VarScope(kind, prevScope) :
		new VarScope(kind);
            newScope.isStatic = prevScope != null ?
                prevScope.isStatic :
                false;
            scopes = scopes.prepend(newScope);
	}

	private void endScope() {
	    scopes = scopes.tail;
	}

	protected VarScope currentScope() {
	    return scopes.head;
	}

        private void removeVar(F3VarSymbol vsym) {
            VarScope scope = currentScope();
            while (scope != null) {
                scope.uninited_vars.remove(vsym);
                scope = scope.prevScope;
            }
        }

	private void addVars(List<? extends F3Tree> trees) {
	    for (F3Tree t : trees) {
		addVar(t);
	    }
	}

	private void addVar(F3Tree tree) {
	    if (tree != null) {
		F3VarSymbol sym = null;
		switch (tree.getF3Tag()) {
		    case VAR_DEF: sym = ((F3Var)tree).getSymbol(); break;
		    case VAR_SCRIPT_INIT: sym = ((F3VarInit)tree).getSymbol(); break;
		}
		if (sym != null) {
		    currentScope().uninited_vars.add(sym);
		}
	    }
	}

        private boolean isObjLiteral(Symbol sym) {
            return sym.name.toString().contains(defs.objectLiteralClassInfix);
        }

        private boolean isTransparent(ScopeKind kind) {
            return kind.isDefaultTransparent() ||
                   (kind.isOptional() && optionalKinds.contains(kind));
        }

	protected abstract void reportForwardReference(DiagnosticPosition pos, boolean selfReference, Symbol s, boolean potential);
    }

    private List<ForwardReferenceChecker.ScopeKind> getForwardRefKinds() {
	String s = options.get("fwdRefOpt");
	List<ForwardReferenceChecker.ScopeKind> kinds = List.nil();
	if (s == null) {
	    return kinds;
	}
	if (s.contains("objlit")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.OBJ_LIT);
            kinds = kinds.append(ForwardReferenceChecker.ScopeKind.OBJ_LIT_FUNC);
	}
	if (s.contains("bind")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.BOUND_CTX);
	}
	if (s.contains("interpolate")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.INTERPOLATE_VALUE);
	}
	if (s.contains("keyframe")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.KEYFRAME_LIT);
	}
	if (s.contains("lambda")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.FUNCTION_VALUE);
	}        
        if (s.contains("assign")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.ASSIGN_CTX);
	}
	if (s.contains("onreplace") || s.contains("trigger")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.ON_REPLACE);
	}
	if (s.contains("oninvalidate") || s.contains("trigger")) {
	    kinds = kinds.append(ForwardReferenceChecker.ScopeKind.ON_INVALIDATE);
	}
	return kinds;
    }
}
