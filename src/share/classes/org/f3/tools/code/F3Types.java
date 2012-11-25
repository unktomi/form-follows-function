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

package org.f3.tools.code;

import org.f3.tools.comp.F3Defs;
import static org.f3.tools.code.F3TypeRepresentation.*;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.code.Type.*;
import java.util.HashMap;
import org.f3.tools.tree.*;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.jvm.ClassWriter;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.TypeTags.*;
import java.util.Set;
import java.util.HashSet;
import org.f3.tools.comp.F3Attr.TypeCons; // hack
/**
 *
 * @author bothner
 */
public class F3Types extends Types {
    F3Symtab syms;
    ClassWriter writer;

    private HashMap<ClassSymbol, F3ClassDeclaration> f3Classes;

    public static void preRegister(final Context context) {
        if (context.get(typesKey) == null)
            context.put(typesKey, new Context.Factory<Types>() {
                public Types make() {
                    return new F3Types(context);
                }
            });
    }

    public static void preRegister(final Context context, F3Types types) {
        context.put(typesKey, types);
    }

    public static F3Types instance(Context context) {
        F3Types instance = (F3Types) context.get(typesKey);
        if (instance == null)
            instance = new F3Types(context);
        return instance;
    }

    protected F3Types(Context context) {
        super(context);
        syms = (F3Symtab) F3Symtab.instance(context);
        writer = ClassWriter.instance(context);
    }

    public boolean isNullable(Type type) {
            return !type.isPrimitive() &&
                    type != syms.f3_StringType &&
                    type != syms.f3_DurationType &&
                    type != syms.f3_LengthType &&
                    type != syms.f3_AngleType &&
                    type != syms.f3_ColorType;
    }

    public boolean isFunctor(Type type) {
	return getFunctor(type) != null;
    }

    public boolean isMonad(Type type) {
	return getMonad(type) != null;
    }

    public boolean isComonad(Type type) {
	return getComonad(type) != null;
    }

    public Type makeTypeCons(Type thisType, List<Type> args) {
	List<Type> list = List.of(thisType);
	list.appendList(args);
        return applySimpleGenericType(syms.f3_TypeCons[args.size()], list);
    }

    public Type makeMonadType(Type monadType, Type bodyType) {
        return applySimpleGenericType(syms.f3_MonadType, erasure(monadType), bodyType);
    }

    public Type makeFunctorType(Type functorType, Type bodyType) {
        return applySimpleGenericType(syms.f3_FunctorType, erasure(functorType), bodyType);
    }

    public Type makeComonadType(Type comonadType, Type bodyType) {
        Type result =
	    applySimpleGenericType(syms.f3_ComonadType, erasure(comonadType), bodyType);
	//System.err.println("make comonad type: "+ comonadType+ ", "+bodyType +"="+result);
	return result;
    }

    public Type getFunctor(Type type) {
	if (isFunctorType(type)) {
	    return type;
	}
	for (Type st: supertypes(type)) {
	    Type t = getFunctor(st);
	    if (t != null) {
		return t;
	    }
	}
	return null;
    }

    public Type getMonad(Type type) {
	if (isMonadType(type)) {
	    return type;
	}
	for (Type st: supertypesClosure(type)) {
	    if (st != type) {
		Type t = getMonad(st);
		if (t != null) {
		    return t;
		}
	    }
	}
	return null;
    }

    public Type getComonad(Type type) {
	if (isComonadType(type)) {
	    return type;
	}
	for (Type st: supertypesClosure(type)) {
	    if (isComonadType(st)) {
		return st;
	    }
	}
	return null;
    }

    public Type getTypeConsThis(Type type)  {
	if (type == null) return null;
	if (isSequence(type)) {
	    return type;
	}
	Type t = getTypeCons(type);
	if (t == null) return null;
	List<Type> targs = t.getTypeArguments();
	if (targs.size() > 0) {
	    Type result = targs.get(0);
	    targs = targs.tail;
	    if (targs.size() > 0) {
		result = applySimpleGenericType(result, targs.toArray(new Type[targs.size()]));
	    }
	    //System.err.println("result="+result);
	    return result;
	}
	return null;
    }

    public Type getTypeCons(Type type) {
	if (type == null) {
	    return null;
	}
	if (isTypeConsType(type) != 0) {
	    return type;
	}
	//System.err.println("getTypeCons: "+ type);
	List<Type> supers = supertypesClosure(type);
	if (supers != null) for (Type st: supers) {
		if (isTypeConsType(st) != 0) {
		    return st;
		}
	    }
	return null;
    }

    public boolean isTypeCons(Type t) {
	return getTypeCons(t) != null;
    }

    public int isTypeConsType(Type type) {
        if (!(type != Type.noType && type != null
	      && type.tag != TypeTags.ERROR
	      && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL)) {
	    return 0;
	}
	Type t = erasure(type);
	for (int i = 0; i < syms.f3_TypeCons.length; i++) {
	    if (t == syms.f3_TypeConsErasure[i]) {
		return 1 + i;
	    }
	}
	if (t == syms.f3_TypeConsTypeErasure) {
	    return 1;
	}
	return 0;
    }

    public boolean isSequence(Type type) {
        return type != Type.noType && type != null
            && type.tag != TypeTags.ERROR
            && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL
            && erasure(type) == syms.f3_SequenceTypeErasure;
    }

    public boolean isSyntheticBuiltinsFunction(Symbol sym) {
        return  sym != null && sym.kind == Kinds.MTH &&
                (sym.flags_field & F3Flags.FUNC_IS_BUILTINS_SYNTH) != 0;
    }

    public boolean isSyntheticPointerFunction(Symbol sym) {
        return  sym != null && sym.kind == Kinds.MTH &&
                (sym.flags_field & F3Flags.FUNC_POINTER_MAKE) != 0;
    }

    public boolean isArrayOrSequenceType(Type type) {
        return isArray(type) || isSequence(type);
    }

    public Type arrayOrSequenceElementType(Type type) {
        return isArray(type) ?
            elemtype(type) :
            elementType(type);
    }

    public Type functorElementType(Type type) {
	Type functor = getFunctor(type);
	if (functor != null) {
	    List<Type> list = functor.getTypeArguments();
	    if (list.size() > 1) {
		Type elemType = list.get(1);
		while (elemType instanceof CapturedType)
		    elemType = ((CapturedType) elemType).wildcard;
		while (elemType instanceof WildcardType)
		    elemType = ((WildcardType) elemType).type;
		if (elemType == null)
		    return syms.f3_AnyType;
		return elemType;
	    }
	}
	return null;
    }

    public Type monadElementType(Type type) {
	Type monad = getMonad(type);
	if (monad != null) {
	    List<Type> list = monad.getTypeArguments();
	    if (list.size() > 1) {
		Type elemType = list.get(1);
		while (elemType instanceof CapturedType)
		    elemType = ((CapturedType) elemType).wildcard;
		while (elemType instanceof WildcardType)
		    elemType = ((WildcardType) elemType).type;
		if (elemType == null)
		    return syms.f3_AnyType;
		return elemType;
	    }
	}
	return null;
    }

    public Type comonadElementType(Type type) {
	Type comonad = getComonad(type);
	if (comonad != null) {
	    List<Type> list = comonad.getTypeArguments();
	    Type elemType = list.get(1);
	    while (elemType instanceof CapturedType)
		elemType = ((CapturedType) elemType).wildcard;
	    while (elemType instanceof WildcardType)
		elemType = ((WildcardType) elemType).type;
	    if (elemType == null)
		return syms.f3_AnyType;
	    return elemType;
	}
	return null;
    }

    public Type getMonadType(Type type) {
	Type monad = getMonad(type);
	if (monad != null) {
	    List<Type> list = monad.getTypeArguments();
	    return list.head;
	}
	return null;
    }

    public boolean isFunctorType(Type type) {
        if (type != Type.noType && type != null
            && type.tag != TypeTags.ERROR
            && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL
            && erasure(type) == syms.f3_FunctorTypeErasure) {
	    //System.err.println("is a monad: "+ type);
	    return true;
	}
	//System.err.println("not a monad: "+ type);
	return false;
    }

    public boolean isMonadType(Type type) {
        if (type != Type.noType && type != null
            && type.tag != TypeTags.ERROR
            && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL
            && erasure(type) == syms.f3_MonadTypeErasure) {
	    //System.err.println("is a monad: "+ type);
	    return true;
	}
	//System.err.println("not a monad: "+ type);
	return false;
    }

    public boolean isComonadType(Type type) {
        if (type != Type.noType && type != null
            && type.tag != TypeTags.ERROR
            && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL
            && erasure(type) == syms.f3_ComonadTypeErasure) {
	    //System.err.println("is a comonad: "+ type);
	    return true;
	}
	//System.err.println("not a comonad: "+ type);
	return false;
    }

    public Type monadType(Type t) {
	Type monad = getMonad(t);
	return monad;
    }

    public Type sequenceType(Type elemType) {
        return sequenceType(elemType, true);
    }

    public Type sequenceType(Type elemType, boolean withExtends) {
        elemType = boxedTypeOrType(elemType);
        if (withExtends)
            elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
        return applySimpleGenericType(syms.f3_SequenceType, elemType);
    }

    public Type applySimpleGenericType(Type base, Type... parameter) {
        List<Type> actuals = List.from(parameter);
	return applySimpleGenericType(base, actuals);
    }

    public Type applySimpleGenericType(Type base, List<Type> actuals) {
        Type clazzOuter = base.getEnclosingType();
        return new ClassType(clazzOuter, actuals, base.tsym);
    }

    public F3TypeRepresentation typeRep(Type type) {
        TypeSymbol tsym = type.tsym;

        if (tsym == syms.booleanType.tsym) return TYPE_REPRESENTATION_BOOLEAN;
        if (tsym == syms.charType.tsym) return TYPE_REPRESENTATION_CHAR;
        if (tsym == syms.byteType.tsym) return TYPE_REPRESENTATION_BYTE;
        if (tsym == syms.shortType.tsym) return TYPE_REPRESENTATION_SHORT;
        if (tsym == syms.intType.tsym) return TYPE_REPRESENTATION_INT;
        if (tsym == syms.longType.tsym) return TYPE_REPRESENTATION_LONG;
        if (tsym == syms.floatType.tsym) return TYPE_REPRESENTATION_FLOAT;
        if (tsym == syms.doubleType.tsym) return TYPE_REPRESENTATION_DOUBLE;
        if (isSequence(type)) {
            return TYPE_REPRESENTATION_SEQUENCE;
        } else {
            return TYPE_REPRESENTATION_OBJECT;
        }
    }

    public Type arraySequenceType(Type elemType) {
        if (elemType.isPrimitive()) {
            String tname = typeRep(elemType).prefix();
            return syms.enterClass(F3Defs.sequence_PackageString + "." + tname + "ArraySequence");
        }
        Type seqtype = syms.enterClass("org.f3.runtime.sequence.ObjectArraySequence");
        return applySimpleGenericType(seqtype, elemType);
    }

    public Type boxedElementType(Type seqType) {
        Type elemType = seqType.getTypeArguments().head;
        while (elemType instanceof CapturedType)
            elemType = ((CapturedType) elemType).wildcard;
        while (elemType instanceof WildcardType)
            elemType = ((WildcardType) elemType).type;
        if (elemType == null)
            return syms.f3_AnyType;
        return elemType;
    }

    public Type elementType(Type seqType) {
        Type elemType = boxedElementType(seqType);
        Type unboxed = unboxedType(elemType);
        if (unboxed.tag != TypeTags.NONE)
            elemType = unboxed;
        return elemType;
    }

    public Type unboxedTypeOrType(Type t) {
	Type ubt = unboxedType(t);
	return ubt==Type.noType? t : ubt;
    }

    public Type boxedTypeOrType(Type t) {
        return (t.isPrimitive() || t == syms.voidType)?
                      boxedClass(t).type
                    : t;
    }

    public Type elementTypeOrType(Type t) {
        return isSequence(t) ? elementType(t) : t;
    }

    public Type lub(Type t, Type s) {
	if (t.isPrimitive() && s.isPrimitive()) {
	    if (isConvertible(t, s)) {
		return s;
	    }
	    if (isConvertible(s, t)) {
		return t;
	    }
	}
	return super.lub(t, s);
    }

    public Type makeUnionType(Type s, Type t) {
        Type lub = lub(s.baseType(), t.baseType());
        if (lub.isCompound()) {
            //members of the compound type could not be ordered properly
            //due to the fact that F3 allows MI through mixins
            //the compound supertype should always be a F3 class
            //while the superinterfaces should be mixins
            Type clazz = null;
            ListBuffer<Type> interfaces = new ListBuffer<Type>();
            ListBuffer<Type> mixins = new ListBuffer<Type>();
            for (Type st : interfaces(lub).prepend(supertype(lub))) {
                if (isMixin(st.tsym))
                    mixins.append(st);
                else if (st.isInterface())
                    interfaces.append(st);
                else
                    clazz = st;
            }
            List<Type> supertypes = interfaces.toList().prependList(mixins.toList());
            if (clazz != null)
                supertypes = supertypes.prepend(clazz);
            lub = makeCompoundType(supertypes);
        }
        return lub;
    }
    
    @Override
    public boolean isSubtype(Type t, Type s, boolean capture) {
	try {
	    if (isSameType(t, s)) {
		return true;
	    }
	} catch (AssertionError err) {
	    // hack;
	    err.printStackTrace();
	}

	if (s == syms.f3_AnyType) {
	    return true;
	}
	if (t.tag == METHOD) { // fix me !!!!
	    t = syms.makeFunctionType((MethodType)t);
	} else {
	    if (t instanceof WildcardType) {
		t = upperBound(t);
	    }
	}
	try {
	    boolean b = super.isSubtype(t, s, capture);
	    if (!b && s.tag == CLASS && s.isCompound()) {
		for (Type s2 : interfaces(s).prepend(supertype(s))) {
		    if (!isSubtype(t, s2, capture))
			return false;
		}
		return true;
        }
	    else
		return b;
	} catch (AssertionError exc) {
	    System.err.println("bad subtype: "+ t + " " + s);
	    System.err.println("bad subtype: "+ t.getClass() + " " + s.getClass());
	    Thread.currentThread().dumpStack();
	    return false;
	} catch (StackOverflowError exc) {
	    System.err.println("circular: "+ t + " " + s);
	    System.err.println("circular: "+ t.getClass() + " " + s.getClass());
	    Thread.currentThread().dumpStack();
	    return false;
	}
    }

    @Override
    public Type asSuper(Type t, Symbol sym) {
         return asSuper.visit(t, sym);
    }
    // where
    private SimpleVisitor<Type,Symbol> asSuper = new SimpleVisitor<Type,Symbol>() {

        public Type visitType(Type t, Symbol sym) {
            return null;
        }

        @Override
        public Type visitClassType(ClassType t, Symbol sym) {
            if (t.tsym == sym)
                return t;

            for (Type st : supertypes(t)) {
                if (st.tag == CLASS || st.tag == TYPEVAR || st.tag == ERROR) {
                    Type x = asSuper(st, sym);
                    if (x != null)
                        return x;
                }
             }
             return null;
        }

        @Override
        public Type visitArrayType(ArrayType t, Symbol sym) {
            return isSubtype(t, sym.type) ? sym.type : null;
        }

        @Override
        public Type visitTypeVar(TypeVar t, Symbol sym) {
            if (t.tsym == sym)
                return t;
            else
                return asSuper(t.bound, sym);
        }

        @Override
        public Type visitErrorType(ErrorType t, Symbol sym) {
            return t;
        }
    };

    @Override
    public boolean isConvertible (Type t, Type s, Warner warn) {
	if (isSameType(t, s)) {
	    return true;
	}
        if (super.isConvertible(t, s, warn))
            return true;
        if (isSequence(t) && isArray(s))
            return isConvertible(elementType(t), elemtype(s), warn);
        if (isArray(t) && isSequence(s))
            return isConvertible(elemtype(t), elementType(s), warn);
        if (isSequence(t) && isSequence(s))
            return isConvertible(elementType(t), elementType(s), warn);
        //sequence promotion conversion
        if (isSequence(s) && !isSequence(t)) {
            return isConvertible(sequenceType(t), s, warn);
        }
        // Allow all numeric conversion, for now (some should warn)
        if (isNumeric(t) && isNumeric(s)) {
            return true;
        }
        if (t == syms.intType && s == syms.charType)
            return true;

	return false;
    }

    boolean isSameTypeCons(Type a, Type b) {
	if (true) {
	    Type t = getTypeConsThis(a);
	    if (t != null && t != a) {
		//System.err.println("testing: "+ t+", "+ b);
		if (isSameType(erasure(t), erasure(b))) {
		    if (t.getTypeArguments().size() == b.getTypeArguments().size()) {
			if (isSameType(t, b, false)) {
			    //System.err.println("matched "+a + ", "+b);
			    return true;
			}
		    } else {
			return true;
		    }
		}
	    }
	    t = getTypeConsThis(b);
	    if (t != null && t != b) {
		//System.err.println("testing: "+ a+", "+ t);
		if (isSameType(erasure(a), erasure(t))) {
		    if (a.getTypeArguments().size() == t.getTypeArguments().size()) {
			if (isSameType(a, t, false)) {
			    //System.err.println("matched "+a + ", "+b);
			    return true;
			}
		    } else {
			return true;
		    }
		}
	    }
	}
        return false;
    }

    @Override
    public boolean isCastable(Type t, Type s, Warner warn) {
        //if source is a sequence and target is neither a sequence nor Object return false
        if (isSequence(t) &&
                !(isSequence(s) || s.tag == TypeTags.ARRAY) &&
                s != syms.objectType &&
                s != syms.botType) {
            return false;
        }

        //cannot cast from null to a value type (non-null by default) and vice-versa
        if ((s == syms.botType && t.isPrimitive()) ||
                (t == syms.botType && s.isPrimitive())) {
            return false;
        }

        Type target = isSequence(s) ? elementType(s) : s.tag == TypeTags.ARRAY ? ((ArrayType) s).elemtype : s;
        Type source = isSequence(t) ? elementType(t) : t.tag == TypeTags.ARRAY ? ((ArrayType) t).elemtype : t;
        if (!source.isPrimitive())
            target = boxedTypeOrType(target);
        if (!target.isPrimitive())
            source = boxedTypeOrType(source);

        if (source == syms.botType ||
            target == syms.botType)
            return true;

        return isCastableNoConversion(source, target, warn);
    }

    public boolean isCastableNoConversion(Type source, Type target, Warner warn) {
        if (isSequence(source) != isSequence(target) &&
                !isSameType(source, syms.objectType) &&
                !isSameType(target, syms.objectType))
            return false;

        if (source.isPrimitive() &&
                !target.isPrimitive() &&
                isSubtype(boxedClass(source).type, target))
            return true;

        if (target.isPrimitive() &&
                !source.isPrimitive() &&
                isSubtype(boxedClass(target).type, source))
            return true;

        boolean isSourceFinal = (source.tsym.flags() & FINAL) != 0;
        boolean isTargetFinal = (target.tsym.flags() & FINAL) != 0;
        if (isMixin(source.tsym) && isMixin(target.tsym))
            return true;
        else if (isMixin(source.tsym) &&
            !isTargetFinal ||
            (target.isInterface() && !isSequence(target)))
            return true;
        else if (isMixin(target.tsym) &&
            !isSourceFinal ||
            (target.isInterface() && !isSequence(target)))
            return true;
        else //conversion between two primitives/Java classes
            return super.isCastable(source, target, warn);
    }
    
    public boolean isMixin(Symbol sym) {
        if (! (sym instanceof F3ClassSymbol))
            return false;
        sym.complete();
        return (sym.flags_field & F3Flags.MIXIN) != 0;
    }

    public boolean isF3Class(Symbol sym) {
        if (! (sym instanceof F3ClassSymbol))
            return false;
        sym.complete();
        return (sym.flags_field & F3Flags.F3_CLASS) != 0;
    }

    public boolean isF3Function(Type t) {
        return (t instanceof FunctionType);
    }
    
    public void addF3Class(ClassSymbol csym, F3ClassDeclaration cdecl) {
        if (f3Classes == null) {
            f3Classes = new HashMap<ClassSymbol, F3ClassDeclaration>();
        }
        csym.flags_field |= F3Flags.F3_CLASS;
        f3Classes.put(csym, cdecl);
    }
    
    public F3ClassDeclaration getF3Class (ClassSymbol csym) {
       return f3Classes.get(csym);
    }
    
    /** The implementation of this (abstract) symbol in class origin;
     *  null if none exists. Synthetic methods are not considered
     *  as possible implementations.
     *  Based on the Javac implementation method in MethodSymbol,
     *  but modified to handle multiple inheritance.
     */
    public MethodSymbol implementation(MethodSymbol msym, TypeSymbol origin, boolean checkResult) {
        msym.complete();
        if (origin instanceof F3ClassSymbol) {
            F3ClassSymbol c = (F3ClassSymbol) origin;
            for (Scope.Entry e = c.members().lookup(msym.name);
                     e.scope != null;
                     e = e.next()) {
                if (e.sym.kind == MTH) {
                        MethodSymbol m = (MethodSymbol) e.sym;
                        m.complete();
                        if ((true || m.overrides(msym, origin, this, checkResult)) && // hack
                            (m.flags() & SYNTHETIC) == 0)
                            return m;
                }
            }
            List<Type> supers = supertypes(c.type);
            for (List<Type> l = supers; l.nonEmpty(); l = l.tail) {
                MethodSymbol m = implementation(msym, l.head.tsym, checkResult);
                if (m != null)
                    return m;
            }
            return null;
        }
        else
            return msym.implementation(origin, this, checkResult);
    }
    /*

    boolean hasSameBounds0(ForAll t, ForAll s) {
        List<Type> l1 = t.tvars;
        List<Type> l2 = s.tvars;
        while (l1.nonEmpty() && l2.nonEmpty() &&
               isSameType(l1.head.getUpperBound(),
                          subst(l2.head.getUpperBound(),
                                s.tvars,
                                t.tvars))) {
            l1 = l1.tail;
            l2 = l2.tail;
        }
        return l1.isEmpty() && l2.isEmpty();
    }

    public boolean hasSameBounds(ForAll t, ForAll s) {
	boolean result = hasSameBounds0(t, s);
	System.err.println("has same bounds: "+ t.getClass()+ " and " +s.getClass()+": "+result);
	System.err.println("has same bounds: "+ t+ " and " +s+": "+result);
	if (result) {
	    Type q = t.qtype;
	    Type r = subst(s.qtype, s.tvars, t.tvars);
	    System.err.println("q="+q);
	    System.err.println("r="+r);
	    return hasSameArgs(q, r);
	}
	return result;
    }

    public boolean hasSameArgs(Type t, Type s) {
	if ((t instanceof ForAll) && (s instanceof ForAll)) {
	    if (hasSameBounds((ForAll)s, (ForAll)t)) {
	    }
	}
	boolean result = super.hasSameArgs(t, s);
	//System.err.println("has same args: "+ t.getClass()+ " and " +s.getClass()+": "+result);
	//System.err.println("has same args: "+ t+ " and " +s+": "+result);
	return result;
    }

    public boolean containsTypeEquivalent(List<Type> ts, List<Type> ss) {
	for (int i = 0; i < ts.size(); i++) {
	    Type x = ts.get(i);
	    Type y = ss.get(i);
	    System.err.println("x="+x+", y="+y);
	    System.err.println("isSameType: "+ isSameType(x, y));
	    System.err.println("x contains y: "+ containsType(x, y));
	    System.err.println("y contains x: "+ containsType(y, x));
	    if (!isSameType(x, y) && !containsType(x, y)) {
		return false;
	    }
	}
	return true;
	//boolean result = super.containsTypeEquivalent(ts, ss);
	//return result;
    }
    */

    /*
    public boolean hasSameArgs(Type t, Type s) {
	boolean result = super.hasSameArgs(t, s);
	System.err.println("has same args: "+ t.getClass()+ " and " +s.getClass()+": "+result);
	System.err.println("has same args: "+ t+ " and " +s+": "+result);
	System.err.println("t.args="+t.getParameterTypes());
	System.err.println("s.args="+s.getParameterTypes());
	for (Type x : t.getParameterTypes()) {
	    System.err.println("t.arg="+x.getClass()+": "+ System.identityHashCode(x) + ": "+ x);
	}
	for (Type x : s.getParameterTypes()) {
	    System.err.println("s.arg="+x.getClass()+": "+ System.identityHashCode(x) + ": "+ x);
	}
	return result;
    }
    */

    /** A replacement for MethodSymbol.overrides. */
    public boolean overrides(Symbol sym, Symbol _other, TypeSymbol origin, boolean checkResult) {
        if (sym.isConstructor() || _other.kind != MTH) return false;
        if (sym == _other) return true;
        MethodSymbol other = (MethodSymbol)_other;

        // assert types.asSuper(origin.type, other.owner) != null;
        Type mt = this.memberType(origin.type, sym);
        Type ot = this.memberType(origin.type, other);
	//System.err.println("mt="+mt);
	//System.err.println("ot="+ot);
        return
            this.isSubSignature(mt, ot) &&
            (!checkResult || this.resultSubtype(mt, ot, Warner.noWarnings));
    }

    /**
     * Returns a list of all supertypes of t, without duplicates, where supertypes
     * are listed according to the order in which they appear in t's extends clause.
     * This method is used in order to implicitly resolve mixin conflicts.
     *
     * @param t the type for which the supertypes list is to be retrieved
     * @return list of ordered supertypes
     */
    public List<Type> supertypesClosure(Type t) {
        return supertypesClosure(t, false, false);
    }

    public List<Type> supertypesClosure(Type t, boolean includeThis) {
        return supertypesClosure(t, includeThis, false);
    }

    public List<Type> supertypesClosure(Type t, boolean includeThis, boolean ascending) {
        List<Type> closure = supertypesClosure(t, ListBuffer.<Type>lb(), ascending);
        return includeThis ? closure :
            ascending ? 
                closure.reverse().tail.reverse() :
                closure.tail;
    }
    //where
    private List<Type> supertypesClosure(Type t, ListBuffer<Type> seenTypes, boolean ascending) {
        if (t == null || t.tag == NONE || seenTypes.contains(t)) {
            return List.nil();
        }
        else {
            seenTypes.append(t);
            List<Type> closure = supertypesClosure(supertype(t), seenTypes, ascending);
            for (Type i : interfaces(t)) {
                closure = closure.appendList(supertypesClosure(i, seenTypes, ascending));
            }
            closure = ascending ?
                closure.append(t) :
                closure.prepend(t);
            return closure;
        }
    }

    public List<Type> supertypes(Type t) {
        Type sup = supertype(t);
        return (sup == null || sup.tag == NONE) ?
            interfaces(t) :
            interfaces(t).prepend(sup);
    }

    public void clearCaches() {
        f3Classes = null;
    }

    public boolean isSameType(Type a, Type b) {
	if (a instanceof MethodType) { // hack not sure why unknownType is found here, but it crashes javac
	    if (b instanceof MethodType) {
		MethodType m1 = (MethodType)a;
		MethodType m2 = (MethodType)b;
		if (hasSameArgs(m1, m2)) {
		    if (m1.getReturnType() == syms.unknownType ||
			m2.getReturnType() == syms.unknownType) {
			return true;
		    }
		} else {
		    return false;
		}
	    }
	}
	return isSameType(a, b, true);
    }

    public boolean isSameType(Type a, Type b, boolean checkTypeCons) {
	if (a == b) {
	    return true;
	}
	if (false && checkTypeCons && isSameTypeCons(a, b)) {
	    return true;
	}
	if (a.tag == TYPEVAR && b.tag == TYPEVAR) { // hack: fix me (I have duplicate type vars somewhere)
	    a = new ForAll(List.of(a), a);
	    b = new ForAll(List.of(b), b);
	}
	try {
	    boolean result = super.isSameType(a, b);
	    return result;
	} catch (AssertionError err) {
	    System.err.println("a: "+ a);
	    System.err.println("b: "+ b);
	    //throw err;
	    return false;
	}
    }
    /*
    public Type subst(Type t, List<Type> from, List<Type> to) {
	Type result = super.subst(t, from, to);
	System.err.println("subst " +t+", "+from +", "+to+" => "+ result);
	return result;
    }
    */
    public boolean isNumeric(Type type) {
        return (isSameType(type, syms.f3_ByteType) ||
                isSameType(type, syms.f3_ShortType) ||
                isSameType(type, syms.f3_IntegerType) ||
                isSameType(type, syms.f3_LongType) ||
                isSameType(type, syms.f3_FloatType) ||
                isSameType(type, syms.f3_DoubleType));
    }

    public List<String> toF3String(List<Type> ts) {
        List<String> buf = List.nil();
        for (Type t : ts) {
            buf = buf.prepend(toF3String(t));
        }
        return buf.reverse();
    }

    public String toF3String(Type type) {
        StringBuilder buffer = new StringBuilder();
        typePrinter.visit(type, buffer);
        return buffer.toString();
    }

    SimpleVisitor typePrinter = new SimpleVisitor<Void, StringBuilder>() {

        public Void visitType(Type t, StringBuilder buffer) {
            String s = null;
            switch (t.tag) {
                case NONE: s = "<unknown>"; break;
                case UNKNOWN: s = "Object"; break;
                case BYTE: s = "Byte"; break;
                case SHORT: s = "Short"; break;
                case INT: s = "Integer"; break;
                case LONG: s = "Long"; break;
                case FLOAT: s = "Number"; break;
                case DOUBLE: s = "Double"; break;
                case CHAR: s = "Character"; break;
                case BOOLEAN: s = "Boolean"; break;
                case VOID: s = "()"; break;
                default: s = t.toString(); break;
            }
            buffer.append(s);
            return null;
        }

	Set visited = new HashSet();
        @Override
        public Void visitTypeVar(TypeVar t, StringBuilder buffer) {
	    buffer.append(t.tsym.name);
	    if (visited.contains(t)) {
		return null;
	    }
	    visited.add(t);
	    if (t.bound != null && t.bound != syms.objectType) {
		String str = toF3String(t.bound);
		if (!"Object".equals(str)) {
		    buffer.append(" is ");
		    buffer.append(str);
		}
	    }
	    if (t instanceof TypeCons) {
		buffer.append(" of ");
		TypeCons tc = (TypeCons)t;
		List<Type> targs = tc.args;
		String str = "";
		if (targs.size() > 1) {
		    buffer.append("(");
		}
		for (Type targ: targs) {
		    buffer.append(str);
		    visit(targ, buffer);
		    str = ", ";
		}
		if (targs.size() > 1) {
		    buffer.append(")");
		}
	    }
	    return null;
	}

        @Override
        public Void visitWildcardType(WildcardType t, StringBuilder buffer) {
	    System.err.println("wildcard="+t);
	    System.err.println("wildcard.type="+t.type);
	    if (t.bound != null) {
		System.err.println("wildcard.bound="+t.bound.getClass()+": "+t.bound);
	    }
	    if (t.kind == BoundKind.EXTENDS) {
		visit(t.bound, buffer);
		buffer.append(" is ");
		visit(t.type, buffer);
	    } else if (t.kind == BoundKind.SUPER) {
		visit(t.bound, buffer);
		buffer.append(" is ");
		visit(t.bound, buffer);
	    } else {
		//BoundKind.UNBOUND
		buffer.append("?");
	    }
	    return null;
	}

        @Override
        public Void visitForAll(ForAll t, StringBuilder buffer) {
	    if (t.qtype instanceof MethodType) {
		visitMethodType2((MethodType)t.qtype, buffer, t.getTypeArguments());
		return null;
	    }
	    buffer.append("of ");
	    List<Type> targs = t.getTypeArguments();
	    if (targs.nonEmpty()) { // hack
		String str = "";
		if (targs.size() > 1) {
		    buffer.append("(");
		}
		for (Type targ: targs) {
		    buffer.append(str);
		    visit(targ, buffer);
		    str = ", ";
		}
		if (targs.size() > 1) {
		    buffer.append(")");
		}
	    }
	    buffer.append(" ");
	    visit(t.qtype, buffer);
	    return null;
	}

	boolean needsParen(List<Type> args) {
	    return args.size() == 0 || args.size() > 1 ||
	    args.size() == 1 && ((args.head instanceof FunctionType) ||
				 (args.head.getTypeArguments().size() > 0));
	}

        @Override
        public Void visitMethodType(MethodType t, StringBuilder buffer) {
	    return visitMethodType2(t, buffer, null);
	}

        public Void visitMethodType2(MethodType t, StringBuilder buffer,
				     List<Type> targs) {
            if (t.getReturnType() == null) {
                buffer.append("function(?):?");
                return null;
            }
	    buffer.append("function ");
	    if (targs != null && targs.size() > 0) {
		switch (targs.size()) {
		case 0:
		    break;
		case 1:
		    buffer.append("of ");
		    visit(targs.get(0), buffer);
		    buffer.append(" ");
		    break;
		default:
		    {
			buffer.append("of (");
			String str = "";
			for (List<Type> l = targs; l.nonEmpty(); l = l.tail) {
			    buffer.append(str);
			    visit(l.head, buffer);
			    str = ", ";
			}
			buffer.append(") ");
		    }
		}
	    }
	    buffer.append("from ");
            List<Type> args = t.getParameterTypes();
	    /*
	    if ((t.tsym.flags() & Flags.STATIC) == 0) {
		ListBuffer<Type> buf = ListBuffer.lb();
		buf.appendList(args);
		args = buf.toList();
	    }
	    */
	    if (needsParen(args)) {
		buffer.append("(");
	    }
	    String str = "";
            for (List<Type> l = args; l.nonEmpty(); l = l.tail) {
                buffer.append(str);
                visit(l.head, buffer);
		str = ", ";
            }
	    if (needsParen(args)) {
		buffer.append(")");
	    }
            buffer.append(" to ");
            visit(t.getReturnType(), buffer);
            return null;
        }

        @Override
        public Void visitArrayType(ArrayType t, StringBuilder buffer) {
            buffer.append("nativearray of ");
            visit(elemtype(t), buffer);
            return null;
        }

        @Override
        public Void visitClassType(ClassType t, StringBuilder buffer) {
            if (isSameType(t, syms.stringType))
                buffer.append("String");
            else if (isSameType(t, syms.objectType))
                buffer.append("Object");
            else if (isSequence(t)) {
                if (t != syms.f3_EmptySequenceType) {
                   visit(elementType(t), buffer);
                }
                buffer.append("[]");
            }
            else if (t instanceof FunctionType) {
                visit(((FunctionType)t).asMethodOrForAll(), buffer);
            }
            else if (t.isCompound()) {
                visit(supertype(t), buffer);
            }
            else {
		List<Type> targs = t.getTypeArguments();
		if (targs.nonEmpty()) { // hack
		    String str = t.toString();
		    if (str.startsWith("org.f3.functions.Function")) {
			//Thread.currentThread().dumpStack();
			visitMethodType(syms.makeFunctionType(targs).asMethodType(), buffer);
			return null;
		    } 
		    int lt = str.indexOf("<");
		    str = str.substring(0, lt);
		    buffer.append(str);
		    buffer.append(" of ");
		    str = "";
		    if (targs.size() > 1) {
			buffer.append("(");
		    }
		    for (Type targ: targs) {
			buffer.append(str);
			visit(targ, buffer);
			str = ", ";
		    }
		    if (targs.size() > 1) {
			buffer.append(")");
		    }
		} else {
		    buffer.append(t.toString());
		}
	    }
            return null;
        }
    };

    public String toF3String(MethodSymbol sym, List<VarSymbol> params) {
        StringBuilder buffer = new StringBuilder();
        if ((sym.flags() & BLOCK) != 0)
            buffer.append(sym.owner.name);
        else {
            buffer.append(sym.name == sym.name.table.init ? sym.owner.name : sym.name);
            if (sym.type != null) {
                buffer.append('(');
                // FUTURE: check (flags() & VARARGS) != 0
                List<Type> args = sym.type.getParameterTypes();
                for (List<Type> l = args; l.nonEmpty(); l = l.tail) {
                    if (l != args)
                        buffer.append(",");
                    if (params != null && params.nonEmpty()) {
                        VarSymbol param = params.head;
                        if (param != null)
                            buffer.append(param.name);
                        params = params.tail;
                    }
                    buffer.append(" is ");
                    buffer.append(toF3String(l.head));
                }
                buffer.append(')');
            }
        }
        return buffer.toString();
    }

    public String location (Symbol sym, Type site) {
        while ((sym.owner.flags() & BLOCK) != 0 ||
                syms.isRunMethod(sym.owner))
            sym = sym.owner;
        return sym.location(site, this);
    }

    public String location (Symbol sym) {
        while ((sym.owner.flags() & BLOCK) != 0 ||
                syms.isRunMethod(sym.owner))
            sym = sym.owner;
        String loc = sym.location();
	//System.err.println("sym="+sym);
	//System.err.println("loc="+loc);
	return loc;
    }

    /**
     * Computes a type which is suitable as a variable inferred type.
     * This step is needed because the inferred type can contain captured
     * types which makes the inferred type too specific.
     *
     * @param t the type to be normalized
     * @return the normalized type
     */
    public Type normalize(Type t) {
        class TypeNormalizer extends SimpleVisitor<Type, Boolean> {
	    Set visited = new HashSet();
            @Override
            public Type visitTypeVar(TypeVar t0, Boolean preserveWildcards) {
		if (visited.contains(t0)) {
		    return t0;
		}
		visited.add(t0);
		TypeVar t = t0;
                Type upper = visit(t.getUpperBound(), preserveWildcards);
		if ("<captured wildcard>".equals(t.tsym.name.toString())) { // major hack
		    return upper;
		}
		t = new TypeVar(t.tsym, upper, t.lower);
		return t;
            }

            @Override
            public Type visitCapturedType(CapturedType t, Boolean preserveWildcards) {
                Type t1 = visit(t.wildcard, preserveWildcards);
		//System.err.println("captured: "+ t + " => "+ t1);
		return t1;
            }

            @Override
            public Type visitWildcardType(WildcardType t0, Boolean preserveWildcards) {
		if (t0.kind == BoundKind.UNBOUND) {
		    return t0;
		}
		WildcardType t = t0;
		Type vbound = t.bound;
		Type vtype = t.type;
		Type bound1 = null;
		Type type1 = null;
		if (vbound != null) {
		    bound1 = visit(vbound, preserveWildcards);
		} 
		if (!preserveWildcards) {
		    //System.err.println("wildcard: ! "+ t0 + " => "+ bound1);
		    return bound1;
		}
		if (vtype != null) {
		    type1 = visit(vtype, preserveWildcards);
		} 
		//System.err.println("t0="+t0);
		//System.err.println("bound1="+bound1);
		//System.err.println("vtype="+vtype);
		//System.err.println("type="+type1);
		if (bound1 != vbound || vtype != type1) {
		    if (type1 instanceof WildcardType) {
			//t = (WildcardType)type1;
			bound1 = ((WildcardType)type1).bound;
			type1 = ((WildcardType)type1).type;
		    } 
		    if (bound1 != null) {
			t = new WildcardType(type1, t.kind, t.tsym, (TypeVar)bound1);
		    } else {
			t = new WildcardType(type1, t.kind, t.tsym);
		    }
		}
		//System.err.println("wildcard: "+ t0 + " => "+ t);
                return t;
            }

            @Override
            public Type visitClassType(ClassType t0, Boolean preserveWildcards) {
		ClassType t = t0;
                List<Type> args2 = visit(t.getTypeArguments(), true);
                Type encl2 = visit(t.getEnclosingType(), false);
		boolean isFunc = isF3Function(t);
                if (!isFunc &&
		    (!isSameTypes(args2, t.getTypeArguments()) ||
		     !isSameType(encl2, t.getEnclosingType()))) {
		    t = new ClassType(encl2, args2, t.tsym);
                }
		//System.err.println("clazz: "+ t0 + " => "+ t);
                return t;
            }

            public Type visitType(Type t, Boolean preserveWildcards) {
		if (visited.contains(t.tsym)) {
		    return t;
		}
		visited.add(t);
		Type t1 = visitType0(t, preserveWildcards);
		//System.err.println("type "+t + " => " + t1);
		return t1;
	    }

            public Type visitType0(Type t, Boolean preserveWildcards) {
                if (t == syms.botType) {
                    return syms.objectType;
                }
                else if (isSameType(t, syms.f3_EmptySequenceType)) {
                    return sequenceType(syms.objectType);
                }
                else if (t == syms.unreachableType) {
                    return syms.objectType;
                }
                else {
		    if (!isSameType(t, syms.voidType)) {
			return boxedTypeOrType(t);
		    } else {
			return t;
		    }
                }
            }

            public List<Type> visit(List<Type> ts, Boolean preserveWildcards) {
                ListBuffer<Type> buf = ListBuffer.lb();
                for (Type t : ts) {
                    buf.append(visit(t, preserveWildcards));
                }
                return buf.toList();
            }
        }
	if (t == null || t.isPrimitive()) {
	    return t;
	}
	//System.err.println("norm visit: "+ t.getClass() +" "+t);
        return new TypeNormalizer().visit(t, true);
    }

    public Type normalize(Type t, boolean preserveWildcards) {
	if (t.isPrimitive()) {
	    return t;
	}
        return new TypeNormalizer2().visit(t, preserveWildcards);
    }

    class TypeNormalizer2 extends SimpleVisitor<Type, Boolean> {
	@Override
            public Type visitTypeVar(TypeVar t0, Boolean preserveWildcards) {
	    TypeVar t = t0;
	    Type upper = visit(t.getUpperBound(), preserveWildcards);
	    if ("<captured wildcard>".equals(t.tsym.name.toString())) { // major hack
		return upper;
	    }
	    t = new TypeVar(t.tsym, upper, visit(t.lower, preserveWildcards));
	    return t;
	}
	
            @Override
            public Type visitCapturedType(CapturedType t, Boolean preserveWildcards) {
                Type t1 = visit(t.wildcard, preserveWildcards);
		//System.err.println("captured: "+ t + " => "+ t1);
		return t1;
            }

            @Override
            public Type visitWildcardType(WildcardType t0, Boolean preserveWildcards) {
		if (t0.kind == BoundKind.UNBOUND) {
		    return t0;
		}
		WildcardType t = t0;
		Type vbound = t.bound;
		Type vtype = t.type;
		Type bound1 = null;
		Type type1 = null;
		//System.err.println("t0="+t0);
		//System.err.println("bound1="+bound1);
		//System.err.println("vtype="+vtype);
		if (vbound != null) {
		    bound1 = visit(vbound, preserveWildcards);
		} 
		if (bound1 != null && !preserveWildcards) {
		    //System.err.println("wildcard: ! "+ t0 + " => "+ bound1);
		    return bound1;
		}
		if (vtype != null) {
		    type1 = visit(vtype, preserveWildcards);
		    if (!preserveWildcards) {
			//System.err.println("type1="+type1);
			return type1;
		    }
		} 
		if (bound1 != vbound || vtype != type1) {
		    if (type1 instanceof WildcardType) {
			//t = (WildcardType)type1;
			bound1 = ((WildcardType)type1).bound;
			type1 = ((WildcardType)type1).type;
		    } 
		    if (bound1 != null) {
			t = new WildcardType(type1, t.kind, t.tsym, (TypeVar)bound1);
		    } else {
			t = new WildcardType(type1, t.kind, t.tsym);
		    }
		}
		//System.err.println("wildcard: "+ t0 + " => "+ t);
                return t;
            }

            @Override
            public Type visitClassType(ClassType t0, Boolean preserveWildcards) {
		ClassType t = t0;
                List<Type> args2 = visit(t.getTypeArguments(), preserveWildcards);
                Type encl2 = visit(t.getEnclosingType(), preserveWildcards);
		//System.err.println("arg2="+args2);
		boolean isFunc = isF3Function(t);
                if (//!isFunc &&
		    (!isSameTypes(args2, t.getTypeArguments()) ||
		     !isSameType(encl2, t.getEnclosingType()))) {
		    t = new ClassType(encl2, args2, t.tsym);
                }
                return t;
            }

            public Type visitType(Type t, Boolean preserveWildcards) {
		Type t1 = visitType0(t, preserveWildcards);
		//System.err.println("type "+t + " => " + t1);
		return t1;
	    }

            public Type visitType0(Type t, Boolean preserveWildcards) {
                if (t == syms.botType) {
                    return syms.objectType;
                }
                else if (isSameType(t, syms.f3_EmptySequenceType)) {
                    return sequenceType(syms.objectType);
                }
                else if (t == syms.unreachableType) {
                    return syms.objectType;
                }
                else {
		    if (!isSameType(t, syms.voidType)) {
			return boxedTypeOrType(t);
		    } else {
			return t;
		    }
                }
            }

            public List<Type> visit(List<Type> ts, Boolean preserveWildcards) {
                ListBuffer<Type> buf = ListBuffer.lb();
                for (Type t : ts) {
		    Type xt = visit(t, preserveWildcards);
		    if (xt != null) {
			buf.append(xt);
		    } else {
			System.err.println("normalized to null: "+ t);
		    }
                }
                return buf.toList();
            }
    }

    public String toSignature(Type t) {
        return writer.typeSig(t).toString();
    }
}
