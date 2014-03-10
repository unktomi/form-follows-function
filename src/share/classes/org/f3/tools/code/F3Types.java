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
import org.f3.tools.comp.F3TranslationSupport;
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
import org.f3.tools.comp.F3Attr; // hack
import org.f3.tools.comp.F3Attr.TypeCons; // hack
import org.f3.tools.comp.F3Attr.ConstI; // hack
import org.f3.tools.comp.F3Attr.TypeVarDefn; // hack
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
	return getFunctor(upperBound(type)) != null;
    }

    public boolean isMonad(Type type) {
	return getMonad(upperBound(type)) != null;
    }

    public boolean isComonad(Type type) {
	return getComonad(upperBound(type)) != null;
    }

    // T a b c -> TypeApply<TypeApply<TypeApply<T, a>, b, c>
    public Type makeTypeApply(Type thisType, List<Type> args) {
	for (Type t: args) {
	    thisType = applySimpleGenericType(syms.f3_TypeApplyType, List.of(t));
	}
	return thisType;
    }

    public Type makeTypeCons(Type thisType, List<Type> args) {
	int i = isTypeConsType(thisType);
	if (i > 0) {
	    //System.err.println("thisType="+thisType);
	    //System.err.println("args="+args);
	    //Thread.currentThread().dumpStack();
	    if (thisType.getTypeArguments().head instanceof TypeCons) {
		thisType = thisType.getTypeArguments().head;
	    }
	}
	List<Type> list = List.of(thisType);
	if (thisType.getTypeArguments().size() > 0) {
	    if (thisType instanceof TypeCons) {
		TypeCons cons = (TypeCons)thisType;
		if (cons.ctor != null) {
		    //System.err.println("ctor="+cons.ctor.getClass()+": "+cons.ctor);
		    list.head = cons.ctor;
		    args = cons.args.appendList(args);
		} else {
		    //list.head = erasure(syms.f3_TypeCons[args.size()]);
		}
	    } else {
		list.head = erasure(thisType);
		args = thisType.getTypeArguments().appendList(args);
	    }
	}
	if (!(list.head instanceof TypeVar)) {
	    list.head = new WildcardType(list.head, BoundKind.EXTENDS, syms.boundClass);
	}
	/*
	if (list.head instanceof TypeCons) {
	    TypeCons k = (TypeCons)list.head;
	    if (k.ctor == null) {
		Thread.currentThread().dumpStack();
	    }
	}
	System.err.println("make type cons: "+ list.head+": "+args.size() + ": "+args);
	*/
	int n = args.size();
	list = list.appendList(args);
        return applySimpleGenericType(syms.f3_TypeCons[n], list);
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

    public Type functorTypeClass(Type elemType) {
        elemType = boxedTypeOrType(elemType);
	elemType = erasure(elemType);
	if (!isWildcard(elemType)) {
	    elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	} else {
	    //System.err.println("elemType="+elemType);
	} 
        return applySimpleGenericType(syms.f3_FunctorTypeClass, elemType);
    }

    public Type monadTypeClass(Type elemType) {
        elemType = boxedTypeOrType(elemType);
	elemType = erasure(elemType);
	if (!isWildcard(elemType)) {
	    elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	} else {
	    //System.err.println("elemType="+elemType);
	} 
        return applySimpleGenericType(syms.f3_MonadTypeClass, elemType);
    }

    public Type comonadTypeClass(Type elemType) {
        elemType = boxedTypeOrType(elemType);
	elemType = erasure(elemType);
	if (!isWildcard(elemType)) {
	    elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	} else {
	    //System.err.println("elemType="+elemType);
	} 
        return applySimpleGenericType(syms.f3_ComonadTypeClass, elemType);
    }

    public Type getTypeConsThis(Type type)  {
	if (type == null) return null;
	if (type instanceof FunctionType) {
	    return type;
	}
	if (isSequence(type)) {
	    return type;
	}
	Type t = getTypeCons(type);
	if (t == null) return type;
	List<Type> targs = t.getTypeArguments();
	if (targs.size() > 0) {
	    Type result = targs.get(0);
	    targs = targs.tail;
	    if (targs.size() > 0) {
		result = applySimpleGenericType(result, targs.toArray(new Type[targs.size()]));
		//System.err.println(type+" => "+result);
	    }
	    //System.err.println("result="+result);
	    return result;
	}
	return type;
    }

    public Type applyTypeCons(Type t) {
	Type t1 = applyTypeCons0(t);
	if (t1 != t) {
	    //System.err.println(t + " = = > " +t1);
	}
	return t1;
    }

    public Type applyTypeCons0(Type t) {
	if (false) {
	    return applyTypeConsOrig(t);
	}
	if (t instanceof TypeCons) {
	    return t;
	}
	List<Type> args = t.getTypeArguments();
	if (args.head == null) {
	    //System.err.println("applyTypeCons "+ t+ " =/=> "+t);
	    return t;
	}
	Type headArg = args.head;
	if (headArg instanceof WildcardType) {
	    headArg = ((WildcardType)headArg).type;
	}
	if (headArg instanceof TypeVar || isTypeConsType(headArg) >= 0) {
	    return t;
	}
	int i = isTypeConsType(t);
	if (i < 0) {
	    //System.err.println("applyTypeCons "+ t+ " =/=> "+t);
	    return t;
	}
	List<Type> headArgs = headArg.getTypeArguments();
	if (headArgs.size() > 0) {
	    Type base = headArg.tsym.type;
	    if (base.getTypeArguments().size() == headArgs.size()) {
		//System.err.println("f-ed up: "+args.head + " is " + base);
		headArgs = List.<Type>nil();
	    }
	}
	List<Type> allArgs = args.tail == null? headArgs: headArgs.appendList(args.tail);
	Type ctor = erasure(headArg);
	///System.err.println(ctor + " of ("+allArgs+")");
	if (allArgs.size() == 0) {
	    //System.err.println("applyTypeCons "+ t+ " ===> "+ctor);
	    return ctor;
	}
	Type result = applySimpleGenericType(ctor, allArgs);
	//System.err.println("applyTypeCons "+ t+ " => "+result);
	return result;
    }

    public Type applyTypeConsOrig(Type t) {
	if (t instanceof TypeCons) {
	    return t;
	}
	List<Type> args = t.getTypeArguments();
	int i = isTypeConsType(t);
	if (i < 0 || args.size() < 1) {
	    return t;
	}
	if (args.tail == null) {

	    return args.head;
	}
	if (args.head instanceof TypeVar) {
	    return t;
	}
	return applySimpleGenericType(upperBound(args.head), args.tail);
    }

    public Type getTypeCons(Type type) {
	if (type == null) {
	    return null;
	}
	if (type instanceof TypeCons) {
	    return null;
	}
	if (isTypeConsType(type) >= 0) {
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
	    return -1;
	}
	if (type instanceof TypeCons) {
	    return -1;
	}
	Type t;
	try {
	    t = erasure(type);
	} catch (Exception exc) {
	    //exc.printStackTrace();
	    System.err.println("doh! type was: "+type.getClass()+": "+ toF3String(type));
	    return -1;
	}
	for (int i = 0; i < syms.f3_TypeCons.length; i++) {
	    if (t == syms.f3_TypeConsErasure[i]) {
		return i;
	    }
	}
	if (t == syms.f3_TypeConsTypeErasure) {
	    return 0;
	}
	return -1;
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
	Type elemType = functorElementType0(type);
	/*
	if (elemType != null && 
	    !(elemType instanceof WildcardType)) {
	    elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	}
	*/
	return elemType;
    }

    Type functorElementType0(Type type) {
	Type functor = getFunctor(type);
	if (functor != null) {
	    //System.err.println("*functor="+toF3String(functor));
	    List<Type> list = getTypeArgs(functor);
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

    List<Type> getTypeArgs(Type t) {
	if (t instanceof TypeCons) {
	    return ((TypeCons)t).args;
	}
	return t.getTypeArguments();
    }

    public Type monadElementType(Type type) {
	return functorElementType(type);
	/*
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
	*/
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

    public Type idType(Type elemType) {
	elemType = boxedTypeOrType(elemType);
        return applySimpleGenericType(syms.f3_IdType, 
				      List.<Type>of(elemType));
    }

    public Type idElementType(Type type) {
	if (isId(type)) {
	    List<Type> list = getTypeArgs(type);
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

    public boolean isId(Type type) {
        return type != Type.noType && type != null
            && type.tag != TypeTags.ERROR
            && type.tag != TypeTags.METHOD && type.tag != TypeTags.FORALL
            && erasure(type) == syms.f3_IdTypeErasure;
    }

    public Type pointerType(Type siteType, Type elemType, boolean readOnly) {
	elemType = boxedTypeOrType(elemType);
	if (false && !(elemType instanceof WildcardType) && !(elemType instanceof TypeVar)) {
	    elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	}
        return applySimpleGenericType(readOnly ? syms.f3_ReadOnlyPointerType : syms.f3_PointerType, 
				      List.<Type>of(siteType, elemType));
    }

    public Type sequenceType(Type elemType) {
        return sequenceType(elemType, true);
    }

    public boolean isWildcard(Type t) {
	if (t instanceof WildcardType) {
	    return true;
	}
	if (t instanceof CapturedType) {
	    return true;
	}
	if (t instanceof TypeVar) {
	    if (true) {
		return true;
	    }
	    TypeVar tv = (TypeVar)t;
	    if (isWildcard(tv.lower)) {
		return true;
	    }
	}
	return false;
    }

    public Type sequenceType(Type elemType, boolean withExtends) {
	if (isSequence(elemType)) {
	    return elemType;
	}
        elemType = boxedTypeOrType(elemType);
        if (withExtends) {
	    //Thread.currentThread().dumpStack();
	    if (!isWildcard(elemType)) {
		elemType = new WildcardType(elemType, BoundKind.EXTENDS, syms.boundClass);
	    } else {
		//System.err.println("elemType="+elemType);
	    } 
	}
        return applySimpleGenericType(syms.f3_SequenceType, elemType);
    }

    public Type applySimpleGenericType(Type base, Type... parameter) {
        List<Type> actuals = List.from(parameter);
	return applySimpleGenericType(base, actuals);
    }

    public Type applySimpleGenericType(Type base, List<Type> actuals) {
	if (base instanceof TypeCons) {
	    TypeCons cons = (TypeCons)base;
	    if (cons.ctor == null) {
		TypeCons c = new TypeCons(cons.tsym.name,
					  cons.tsym,
					  cons.lower,
					  actuals);
		//c.bound = cons.bound;
		c.bound = cons.bound;
		c.ctor = cons;
		return c;
	    }
	}
	//System.err.println("base="+base.getClass()+": "+base + ": ("+actuals+")");
	if (base instanceof ConstI) {
	    return actuals.get(((ConstI)base).i);
	}
	if (base instanceof MethodType) {
	    //System.err.println("base="+base);
	    //System.err.println("actuals="+actuals);
	    return new ForAll(actuals, base);
	}
	if (isTypeConsType(base) >= 0) {
	    //System.err.println("apply simple="+base+" of ("+actuals+")");
	}
	if (base instanceof TypeVar) {
	    TypeVar v = (TypeVar)base;
	    if (false) {
		TypeCons cons = new TypeCons(v.tsym.name,
					     v.tsym,
					     v.lower,
					     actuals);
		cons.bound = v.bound;
		return cons;
	    }
	    TypeCons cons = null;
	    if (v instanceof TypeCons) {
		cons = (TypeCons)v;
		if (cons.ctor != null) {
		    v = (TypeVar)cons.ctor;
		}
	    }
	    base = syms.f3_TypeCons[actuals.size()];
	    Type ctor = v;
	    ctor = new WildcardType(v, BoundKind.EXTENDS, syms.boundClass);
	    List<Type> newArgs = List.<Type>nil();
	    for (List<Type> l1 = actuals, l2 = v.getTypeArguments(); l1.head != null; l1 = l1.tail) {
		Type arg = l1.head;
		if (l2 != null) {
		    Type at = l2.head;
		    if (at instanceof TypeVarDefn) {
			if (at instanceof F3Attr.TypeVarDefn) {
			    final F3Attr.TypeVarDefn def = (F3Attr.TypeVarDefn)at;
			    arg = new WildcardType(arg, def.variance, syms.boundClass);
			}
		    }
		} 
		newArgs = newArgs.append(arg);
		l2 = l2.tail;
	    }
	    actuals = newArgs;
	    actuals = actuals.prepend(ctor);
	    //System.err.println("base="+base.getClass()+": "+base + " / " + base.tsym.type);
	}
	if (base.getEnclosingType() == null) {
	    //System.err.println("base is "+base.getClass()+ ": "+base);
	}
        return newClassType(base.getEnclosingType(), actuals, base.tsym);
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

    public Type pointerElementType(Type seqType) {
        Type elemType = seqType;
        while (elemType instanceof CapturedType)
            elemType = ((CapturedType) elemType).wildcard;
        while (elemType instanceof WildcardType)
            elemType = ((WildcardType) elemType).type;
	if (elemType == null || elemType.getTypeArguments().size() < 2) {
	    return syms.f3_AnyType;
	}
	elemType = elemType.getTypeArguments().tail.head;
        while (elemType instanceof CapturedType)
            elemType = ((CapturedType) elemType).wildcard;
        while (elemType instanceof WildcardType)
            elemType = ((WildcardType) elemType).type;
        if (elemType == null)
            return syms.f3_AnyType;
        return elemType;
    }

    public Type boxedElementType(Type seqType) {
	if (!isSequence(seqType)) {
	    return seqType;
	}
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
	/*
	if ((t instanceof MethodType) || (t instanceof ForAll)) {
	    return syms.asFunctionType(t);
	}
	*/
	if (t == syms.botType) return t;
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
    
    boolean WARNED_BUG = false;
    @Override
    public boolean isSubtype(Type t, Type s, boolean capture) {
	if (t == s) {
	    return true;
	}
	if (!(t instanceof WildcardType) && !(t instanceof MethodType)) {
	    try {
		if (super.isSubtype(t, s, capture)) {
		    return true;
		}
	    } catch (AssertionError err) {
		if (!WARNED_BUG) {
		    WARNED_BUG = true;
		    //System.err.println("t="+t+", s="+s);
		    //System.err.println(err);
		    //Thread.currentThread().dumpStack();
		}
	    }
	}
	if (true) {
	    int i = isTypeConsType(s);
	    if (i >= 0) {
		if (t instanceof TypeCons) {
		    TypeCons tc = (TypeCons)t;
		    if (tc.ctor != null) {
			Type tc_enc = makeTypeCons(tc.ctor, tc.getTypeArguments());
			int j = isTypeConsType(tc_enc);
			if (i == j) {
			    for (List<Type> x = s.getTypeArguments(), y = tc_enc.getTypeArguments();
				 x != null && x.head != null && y != null && y.head != null; x = x.tail, y = y.tail) {
				Type t0 = x.head;
				Type t1 = y.head;
				if (i == j) {
				    t0 = upperBound(t0);
				    t1 = upperBound(t1);
				}
				if (!isSameType(t0, t1)) {
				    break;
				}
				//System.err.println("s.arg="+toF3String(t0)+ " / " + System.identityHashCode(t0));
				//System.err.println("tc.arg="+toF3String(t1)+ " / " + System.identityHashCode(t1));
				j--;
			    }
			}
			if (j == 0) {
			    return true;
			}
			//boolean b = isSameType(tc_enc, s);
			//System.err.println("typecons="+toF3String(t)+": "+toF3String(tc_enc)+", s="+toF3String(s)+ ": "+b);
		    }
		}

		if (isSameTypeCons(s, t)) {
		    return true;
		}

		Type tt = subst(t, t.getTypeArguments(), s.getTypeArguments());
		//System.err.println("S="+toF3String(s));
		//System.err.println("TT="+toF3String(tt));
		if (s.getTypeArguments().size() > 0) {
		    for (Type st : supertypesClosure(t)) {
			//System.err.println("st="+toF3String(st));
			if (isConvertible(st, s)) {
			    //System.err.println("isSubtype? "+st +": "+s);
			    return true;
			}
		    }
		}
	    }
	}

	if (s == syms.f3_AnyType) {
	    return true;
	}
	if (s.toString().equals("<any?>")) { // major hack
	    return true;
	}
	try {
	    if (isSameType(t, s, true)) {
		return true;
	    }
	} catch (Throwable err) {
	    // hack;
	    err.printStackTrace();
	}
	if (t.tag == METHOD) { // fix me !!!!
	    t = syms.makeFunctionType((MethodType)t);
	} else {
	    //if (t instanceof WildcardType) {
		t = upperBound(t);
		//}
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
	    //Thread.currentThread().dumpStack();
	    return false;
	}
    }

    public boolean isValueType(Type t) {
	for (Type st : supertypesClosure(t)) {
	    if (isSameType(st, syms.f3_ValueType)) {
		return true;
	    }
	}
	return false;
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

    public Type expandTypeVar(final Type t) {
	Type x = t;
	if (x instanceof F3Attr.TypeVarDefn) {
	    final F3Attr.TypeVarDefn def = (F3Attr.TypeVarDefn)x;
	    return new WildcardType(def, def.variance, syms.boundClass); 
	}
	if (x instanceof ClassType) {
	    ClassSymbol def = (ClassSymbol)x.tsym;
	    if (x.isParameterized()) {
		//System.err.println("x="+x);
		//System.err.println("def="+def.type);
		for (List<Type> l0 = x.getTypeArguments(),
			 l1 = def.type.getTypeArguments();
		     l0 != null && l1 != null && l0.head != null && l1.head != null;
		     l0 = l0.tail, l1 = l1.tail) {
		    //  System.err.println("l1.head="+l1.head.getClass()+": "+ l1.head);
		    //System.err.println("l0.head="+l0.head.getClass()+": "+ l0.head);
		    Type u = unexpandWildcard(l1.head);
		    if (u instanceof F3Attr.TypeVarDefn) {
			F3Attr.TypeVarDefn d = (F3Attr.TypeVarDefn)u;
			while (l0.head instanceof CapturedType) {
			    l0.head = ((CapturedType)l0.head).wildcard;
			}
			while (l0.head instanceof WildcardType) {
			    l0.head = ((WildcardType)l0.head).type;
			}
			//if (!(l0.head instanceof TypeVar)) {
			    l0.head = new WildcardType(l0.head, d.variance, syms.boundClass);
			    //}
		    }
		}
		//System.err.println("x'="+x);
	    }
	}
	//System.err.println("expanding: "+ t + " => "+ x);
	return x;
    }

    public Type unexpandWildcard(final Type t0) {
	return unexpandWildcard(t0, false);
    }

    public Type unexpandWildcard(final Type t0, boolean unconditionally) {
	Type t = t0;
	if (t instanceof CapturedType) {
	    t = ((CapturedType)t).wildcard;
	}
	if (t instanceof WildcardType) {
	    final Type x = ((WildcardType)t).type;
	    if (unconditionally || (x instanceof F3Attr.TypeVarDefn)) {
		return x;
	    }
	}
	return t0;
    }

    @Override
    public boolean isConvertible (Type t, Type s, Warner warn) {
	//t = expandTypeVar(t);
	//s = expandTypeVar(s);
        boolean tPrimitive = t.isPrimitive();
        boolean sPrimitive = s.isPrimitive();
        if (tPrimitive == sPrimitive)
            if (isSubtypeUnchecked(t, s, warn)) {
		return true;
	    }
        if (tPrimitive
            ? isSubtype(boxedClass(t).type, s)
            : isSubtype(unboxedType(t), s)) {
	    return true;
	}
	if (isSameType(t, s, true)) {
	    return true;
	}
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

    boolean isSameTypeCons(Type t, Type s) {
	return isSameTypeCons(t, s, false);
    }

    boolean isSameTypeCons(Type t, Type s, boolean debug) {
	//System.err.println("s="+s.getClass()+": "+s);
	//System.err.println("t="+t.getClass()+": "+t);
	if (t instanceof TypeCons) {
	    Type t1 = applySimpleGenericType(t, t.getTypeArguments());
	    return isSameType(t1, s);
	} else if (s instanceof TypeCons) {
	    Type s1 = applySimpleGenericType(s, s.getTypeArguments());
	    return isSameType(t, s1);
	}
	int i = isTypeConsType(s);
	boolean doit = false;
	if (i >= 0) {
	    Type s0 = s;
	    s = applyTypeCons(s);
	    doit = s != s0;
	}
	i = isTypeConsType(t);
	if (i >= 0) {
	    Type t0 = t;
	    t = applyTypeCons(t);
	    doit = t0 != t;
	}
	//System.err.println("s'="+s);
	//System.err.println("t'="+t);
	if (doit) {
	    boolean result = isSameType(erasure(s), erasure(t)) && isSameType(s, t);
	    //System.err.println(s + " ==  " + t + " => "+ result);
	    return result;
	}
	return false;
    }


    public boolean overrideEquivalent(Type t, Type s) {
	Type t1 = normalize(t); Type s1 = normalize(s);
	boolean result = super.overrideEquivalent(t1, s1);
	if (false && !result) {
	    String str = "overrideEq\nt="+t1+"\ns="+ s1;
	    String str1 = "overrideEq\nt="+toF3String(t1)+"\ns="+ toF3String(s1);
	    if (str.indexOf("TypeCons") > 0) {
		System.err.println(str);
		System.err.println(str1);
	    }
	}
	return result;
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
	//System.err.println("has same bounds: "+ t.getClass()+ " and " +s.getClass()+": "+result);
	//System.err.println("has same bounds: "+ t+ " and " +s+": "+result);
	if (result) {
	    Type q = t.qtype;
	    Type r = subst(s.qtype, s.tvars, t.tvars);
	    //System.err.println("q="+q);
	    //System.err.println("r="+r);
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
	    //System.err.println("x="+x+", y="+y);
	    //System.err.println("isSameType: "+ isSameType(x, y));
	    //System.err.println("x contains y: "+ containsType(x, y));
	    //System.err.println("y contains x: "+ containsType(y, x));
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
	if (isMonad(t) && isMonad(s)) {
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
	}
	return result;
    }
    */

    /** A replacement for MethodSymbol.overrides. */
    public boolean overrides(Symbol sym, Symbol _other, TypeSymbol origin, boolean checkResult) {
        if (sym.isConstructor() || _other.kind != MTH) return false;
        if (sym == _other) return true;
        MethodSymbol other = (MethodSymbol)_other;
        assert asSuper(origin.type, other.owner) != null;
	Type mt_1 = sym.type;
	Type ot_1 = other.type;
        Type mt0 = this.memberType(origin.type, sym);
        Type ot0 = this.memberType(origin.type, other);
	Type mt = mt0;
	Type ot = ot0;
	/*
	for (List<Type> x = mt.getParameterTypes(), y = ot.getParameterTypes();
	     x != null && y != null; x = x.tail, y = y.tail) {
	    if (x.head != null && boxedTypeOrType(x.head) == y.head) {
		x.head = y.head;
	    }
	}
	*/
	if (F3TranslationSupport.ERASE_BACK_END) {
	    mt = erasure(mt);
	    ot = erasure(ot);
	} else {
	    mt = normalize(mt);
	    ot = normalize(ot);
	}
        boolean r = overrideEquivalent(mt, ot) &&
            //this.isSubSignature(mt, ot) &&
            (!checkResult || this.resultSubtype(mt, ot, Warner.noWarnings));
	if (false) {
	    System.err.println("mt-1="+mt_1);
	    System.err.println("mt0="+mt0);
	    System.err.println("mt1="+mt);
	    System.err.println("ot-1="+ot_1);
	    System.err.println("ot0="+ot0);
	    System.err.println("ot1="+ot);
	}
	return r;
    }

    public boolean resultSubtype(Type t, Type s, Warner warner) {
	Type rt = t.getReturnType();
	Type st = s.getReturnType();
	if (isSameType(rt, st)) {
	    return true;
	}
	return super.resultSubtype(t, s, warner);
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
                closure.reverse().tail.reverse() :  (closure.tail == null ? List.<Type>nil() : closure.tail);
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
	/*
	if (a instanceof ForAll) {
	    ForAll t = (ForAll)a;
	    if (t.tvars == null || t.tvars.size() == 0) {
		a = t.qtype;
	    }
	}
	if (b instanceof ForAll) {
	    ForAll t = (ForAll)b;
	    if (t.tvars == null || t.tvars.size() == 0) {
		b = t.qtype;
	    }
	}
	*/
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
	return isSameType(a, b, false);
    }

    public boolean isSameType(Type a, Type b, boolean checkTypeCons) {
	if (a == b) {
	    return true;
	}
	if (checkTypeCons) {
	    if (isSameTypeCons(a, b) ||
		isSameTypeCons(b, a)) {
		return true;
	    }
	}
	if (a instanceof WildcardType && b instanceof WildcardType) {
	    WildcardType wc1 = (WildcardType)a;
	    WildcardType wc2 = (WildcardType)b;
	    if (wc1.kind == wc2.kind) {
		return isSameType(wc1.type, wc2.type);
	    }
	}
	if (true) {
	    if (a.tag == TYPEVAR && b.tag == TYPEVAR) { // hack: fix me (I have duplicate type vars somewhere)
		TypeVar ta = (TypeVar)a;
		TypeVar tb = (TypeVar)b;
		if (ta.tsym == tb.tsym) {
		    return true;
		}
		a = newForAll(List.of(a), a);
		b = newForAll(List.of(b), b);
	    }
	}
	if (a == syms.unknownType) {
	    return false;
	}
	try {
	    if ((a instanceof ClassType) && a.getEnclosingType() == null) {
		System.err.println("null enclosing: "+ toF3String(a));
	    }
	    if ((b instanceof ClassType) && b.getEnclosingType() == null) {
		System.err.println("null enclosing: "+ toF3String(b));
	    }
	    boolean result = super.isSameType(a, b);
	    //System.err.println(a + " ==  " + b + " => "+ result);
	    return result;
	} catch (Throwable err) {
	    System.err.println(err);
	    System.err.println("a: "+ a);
	    System.err.println("b: "+ b);
	    //throw err;
	    return false;
	}
    }

    public Type subst(Type t, List<Type> from, List<Type> to) {
	Type result = subst2(t, from, to);
	//Type result = super.subst(t, from, to);
	//System.err.println("subst " +t+", "+from +", "+to+" => "+ result);
	return result;
    }

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
	visited.clear();
        return buffer.toString();
    }

    final Set visited = new HashSet();

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
            default: {
                s = t.toString();
            } break;
            }
            buffer.append(s);
            return null;
        }

        @Override
        public Void visitTypeVar(TypeVar t, StringBuilder buffer) {
	    if (visited.contains(t)) {
		buffer.append(t.tsym.name);
		return null;
	    }
	    visited.add(t);
	    if (t instanceof TypeCons) {
		TypeCons tc = (TypeCons)t;
		//System.err.println("tc="+tc);
		//System.err.println("tc.ctor="+tc.ctor);
		//System.err.println("tc.args="+tc.args);
		if (tc.ctor == null) {
		    buffer.append("class ");
		}
		buffer.append(t.tsym.name);
		buffer.append(" of ");
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
	    } else {
		String lower = null;
		String upper = null;
		if (t.bound != null && t.bound != syms.objectType) {
		    upper = toF3String(t.bound);
		    if ("java.lang.Object".equals(upper)) {
			upper = null;
		    }
		    if ("Object".equals(upper)) {
			upper = null;
		    }
		}
		if (t.lower != null && t.lower != syms.botType) {
		    if (t.lower == syms.objectType) {
			System.err.println("bad lower bound: "+ t.lower);
		    } else {
			lower = toF3String(t.lower);
			if ("<nulltype>".equals(lower)) {
			    lower = null;
			}
		    }
		}
		if (lower != null || upper != null) {
		    buffer.append("(");
		    buffer.append(t.tsym.name);
		    buffer.append(" is ");
		    if (lower != null) {
			buffer.append(lower);
		    }
		    if (upper != lower) {
			buffer.append("..");
		    }
		    if (upper != null) {
			buffer.append(upper);
		    }
		    buffer.append(")");
		} else {		    
		    buffer.append(t.tsym.name);
		}
	    }
	    return null;
	}
	@Override
	public Void visitCapturedType(CapturedType t, StringBuilder buffer) {
	    if (t.wildcard.kind == BoundKind.EXTENDS) {
		buffer.append("*");
	    } 
	    visit(t.wildcard, buffer);
	    if (t.wildcard.kind == BoundKind.SUPER) {
		buffer.append("*");
	    } 
	    return null;
	}

        @Override
        public Void visitWildcardType(WildcardType t, StringBuilder buffer) {
	    if (t.kind == BoundKind.EXTENDS) {
		if (t.bound == null) {
		} else {
		    if (false) {
			visit(t.bound, buffer);
			buffer.append(" is ");
		    }
		}
		buffer.append("..");
		try {
		    visit(t.type, buffer);
		} catch (NullPointerException exc) {
		}
	    } else if (t.kind == BoundKind.SUPER) {
		if (t.bound == null) {
		} else {
		    if (false) {
			visit(t.bound, buffer);
			buffer.append(" is ");
		    }
		}
		visit(t.type, buffer);
		buffer.append("..");
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

	boolean isSameType0(Type x, Type y) {
	    return x == y;
	}

        @Override
        public Void visitClassType(ClassType ct, StringBuilder buffer) {
	    Type t = ct;
	    Type unboxed = unboxedType(t);
	    if (unboxed.isPrimitive()) {
		visit(unboxed, buffer);
		return null;
	    }
            if (isSameType0(t, syms.stringType))
                buffer.append("String");
            else if (isSameType0(t, syms.objectType))
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
		int k = isTypeConsType(t);
		if (k >= 0) {
		    t = targs.head;
		    targs = targs.tail;
		    //System.err.println("t="+t);
		    //System.err.println("targs="+targs);
		    if (t instanceof WildcardType) {
			t = ((WildcardType)t).type;
		    }
		    while (isTypeConsType(t) >= 0) {
			List<Type> targs1 = t.getTypeArguments();
			if (targs1.head != null) {
			    t = targs1.head;
			    if (targs1.tail != null) {
				targs = targs.appendList(targs1.tail);
			    }
			    if (t instanceof WildcardType) {
				t = ((WildcardType)t).type;
			    }
			} else {
			    break;
			}
		    }
		}
		if (t != ct) {
		    //visit(t, buffer);
		    //return null;
		}
		if (ct.getEnclosingType() == null) {
		    System.err.println(ct.getClass() + ": "+ t.tsym.name);
		}
		if (targs.nonEmpty()) { // hack
		    //System.err.println("t="+t);
		    //System.err.println("t.tsym="+t.tsym);
		    //System.err.println("t.tsym.name="+t.tsym.name);
		    String str = t.tsym.name.toString();
		    if (str.startsWith("org.f3.functions.Function")) {
			//Thread.currentThread().dumpStack();
			visitMethodType(syms.makeFunctionType(targs).asMethodType(), buffer);
			return null;
		    } 
		    int lt = str.indexOf("<");
		    if (lt > 0) {
			str = str.substring(0, lt);
		    }
		    buffer.append(str);
		    buffer.append(" of ");
		    str = "";
		    if (targs.size() > 1) {
			buffer.append("(");
		    }
		    for (Type targ: targs) {
			buffer.append(str);
			/*
			if (!(targ instanceof WildcardType)) {
			    visit(targ, buffer);
			    buffer.append("..");
			}
			*/
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
	Symbol owner = sym.owner;
        if (owner.name == null || owner.name.len == 0) {
            return owner.toString();
        }
        if (owner.type.tag == CLASS) {
            Type ownertype = asOuterSuper(site, owner);
            if (ownertype != null) return toF3String(ownertype);
        }
	if (owner instanceof MethodSymbol) {
	    return toF3String((MethodSymbol)owner, ((MethodSymbol)owner).params);
	}
        return owner.toString();

	//        return sym.location(site, this);
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

    public List<Type> normalize(List<Type> input) {
	ListBuffer<Type> result = ListBuffer.lb();
	for (Type t: input) {
	    result.append(normalize(t));
	}
	return result.toList();
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
		if (t0 instanceof TypeCons) {
		    //System.err.println("NORMALIZE: "+ t0);
		    TypeCons tcons = (TypeCons)t0;
		    if (tcons.ctor != null) {
			Type x = applySimpleGenericType(t0, visit(t0.getTypeArguments(), true));
			//System.err.println("returning: "+ x);
			return x;
		    }
		    //System.err.println("returning: "+ t0);
		    return t0;
		}
		if (t0 instanceof ConstI) {
		    return t0;
		}
		visited.add(t0);
		if (preserveWildcards) {
		    Type x = t0;
		    if (x instanceof F3Attr.TypeVarDefn) {
			F3Attr.TypeVarDefn def = (F3Attr.TypeVarDefn)x;
			x = new WildcardType(def, def.variance, syms.boundClass); 
			return x;
		    }
		}
		TypeVar t = t0;
                Type upper = visit(t.getUpperBound(), preserveWildcards);
		if ("<captured wildcard>".equals(t.tsym.name.toString())) { // major hack
		    return upper;
		}
		if (upper == null) {
		    upper = syms.objectType;
		}
		t = newTypeVar(t.tsym, upper, t.lower);
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
		    if (type1 instanceof CapturedType) {
			type1 = ((CapturedType)type1).wildcard;
		    }
		    if (type1 instanceof WildcardType) {
			//t = (WildcardType)type1;
			bound1 = ((WildcardType)type1).bound;
			type1 = ((WildcardType)type1).type;
		    } 
		    if (bound1 instanceof TypeVar) {
			t = new WildcardType(type1, t.kind, t.tsym, (TypeVar)bound1);
		    } else {
			t = new WildcardType(type1, t.kind, t.tsym);
		    }
		}
		//System.err.println("wildcard: "+ t0 + " => "+ t);
                return t;
            }
	    /*
	    public Type visitForAll(ForAll t, Boolean pw) {
		if (t.tvars == null || t.tvars.size() == 0) {
		    return t.qtype;
		}
		return t;
	    }
	    */

	    public Type visitMethodType(MethodType t, Boolean pw) {
		List<Type> argtypes = visit(t.argtypes, pw);
		List<Type> unexpanded = List.<Type>nil();
		boolean diff = false;
		for (Type x: argtypes) {
		    Type g = unexpandWildcard(x);
		    if (g != x) diff = true;
		    unexpanded = unexpanded.append(g);
		}
		if (false && diff) {
		    argtypes = unexpanded;
		}
		Type restype = visit(t.restype, pw);
		List<Type> thrown = visit(t.thrown, pw);
		if (argtypes == t.argtypes &&
		    restype == t.restype &&
		    thrown == t.thrown)
		    return t;
		else {
		    Type n = new MethodType(argtypes, restype, thrown, t.tsym);
		    //System.err.println("method "+t +" => "+n);
		    return n;
		}
	    }

            @Override
            public Type visitClassType(ClassType t0, Boolean preserveWildcards) {
		ClassType t = t0;
		if (isSequence(t)) {//hack
		    t = (ClassType)sequenceType(elementType(t));
		}
                List<Type> args2 = visit(t.getTypeArguments(), true);
                Type encl2 = visit(t.getEnclosingType(), false);
		boolean isFunc = isF3Function(t);
                if (!isFunc &&
		    (!isSameTypes(args2, t.getTypeArguments()) ||
		     !isSameType(encl2, t.getEnclosingType()))) {
		    t = newClassType(encl2, args2, t.tsym);
                }
		//System.err.println("clazz: "+ t0 + " => "+ t);
		if (isTypeConsType(t) >= 0) {
		    Type r = applyTypeCons(t);
		    //System.err.println("APPLY TCONS "+t+" => "+r);
		    return r;
		}
                return expandTypeVar(t);
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
                    return t;
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
	if (t == syms.botType) {
	    return t;
	}
        return new TypeNormalizer2().visit(t, preserveWildcards);
    }

    class TypeNormalizer2 extends SimpleVisitor<Type, Boolean> {
	    Set visited = new HashSet();
	    @Override
            public Type visitTypeVar(TypeVar t0, Boolean preserveWildcards) 
	    {
		if (t0 instanceof TypeCons) {
		    //System.err.println("NORMALIZE': "+ t0);
		    TypeCons c = (TypeCons)t0;
		    if (c.ctor == null) {
			return c;
		    }
		    Type x = applySimpleGenericType(t0, visit(t0.getTypeArguments(), preserveWildcards));
		    //System.err.println("returning: "+ x);
		    return x;
		}
		if (t0 instanceof ConstI) {
		    return t0;
		}
		if (visited.contains(t0)) {
		    return t0;
		}
		visited.add(t0);
		if (preserveWildcards) {
		    Type x = t0;
		    if (x instanceof F3Attr.TypeVarDefn) {
			F3Attr.TypeVarDefn def = (F3Attr.TypeVarDefn)x;
			x = new WildcardType(def.base, def.variance, syms.boundClass); 
			return x;
		    }
		}
		TypeVar t = t0;
		Type upper = visit(t.getUpperBound(), preserveWildcards);
		if ("<captured wildcard>".equals(t.tsym.name.toString())) { // major hack
		    return upper;
		}
		t = newTypeVar(t.tsym, upper, visit(t.lower, preserveWildcards));
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
		    return vtype;
		}
		if (vtype != null) {
		    type1 = visit(vtype, preserveWildcards);
		    if (!preserveWildcards) {
			//System.err.println("type1="+type1);
			return type1;
		    }
		} 
		if (bound1 != vbound || vtype != type1) {
		    if (type1 instanceof CapturedType) {
			type1 = ((CapturedType)type1).wildcard;
		    }
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
		    t = newClassType(encl2, args2, t.tsym);
                }
		if (isTypeConsType(t) >= 0) {
		    Type r = applyTypeCons(t);
		    //System.err.println("APPLY TCONS "+t+" => "+r);
		    return r;
		}
                return expandTypeVar(t);
            }

            public Type visitType(Type t, Boolean preserveWildcards) {
		Type t1 = visitType0(t, preserveWildcards);
		//System.err.println("type "+t + " => " + t1);
		return t1;
	    }

            public Type visitType0(Type t, Boolean preserveWildcards) {
                if (t == syms.botType) {
                    return t;
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
			//System.err.println("normalized to null: "+ t);
		    }
                }
                return buf.toList();
            }
	    /*
	    public Type visitForAll(ForAll t, Boolean pw) {
		if (t.tvars == null || t.tvars.size() == 0) {
		    return t.qtype;
		}
		return t;
	    }
	    */
    }

    public String toSignature(Type t) {
        String r = writer.typeSig(t).toString();
	//System.err.println("sig for : "+ toF3String(t)+": "+r);
	return r;
    }

    public Type memberType(Type t, Symbol sym) {
	Type result = memberType0(t, sym);
	//System.err.println("result of member type "+ t +": "+ result);
	if (sym instanceof MethodSymbol) {
	    MethodSymbol msym = (MethodSymbol)sym;
	    List<VarSymbol> params = List.<VarSymbol>nil();
	    List<Type> ptypes = List.<Type>nil();
	    boolean sawImplicit = false;
	    if (msym.params != null) {
		for (VarSymbol varSym: ((MethodSymbol)msym).params) {
		    if ((varSym.flags() & F3Flags.IMPLICIT_PARAMETER) != 0) {
			sawImplicit = true;
		    } else {
			params = params.append(varSym);
			ptypes = ptypes.append(varSym.type);
		    }
		}
		if (sawImplicit) {
		    MethodType mtype = result.asMethodType();
		    mtype = new ExplicitMethodType(ptypes, mtype.restype, mtype.getTypeArguments(), syms.methodClass, result);
		    //System.err.println("created explicit method type: "+mtype);
		    if (result instanceof ForAll) {
			result = newForAll(((ForAll)result).tvars, mtype);
		    } else {
			result = mtype;
		    }
		}
	    }
	}
	return result;
    }

    public static class ExplicitMethodType extends MethodType {
	public Type implicit;
        public ExplicitMethodType(List<Type> argtypes,
				  Type restype,
				  List<Type> thrown,
				  TypeSymbol methodClass,
				  Type implict) {
	    super(argtypes, restype, thrown, methodClass);
	    this.implicit = implicit;
	}
    }


    public Type memberType0(Type t, Symbol sym) {
        return (sym.flags() & STATIC) != 0
            ? sym.type
            : memberType.visit(t, sym);
    }
    // where
        private SimpleVisitor<Type,Symbol> memberType = new SimpleVisitor<Type,Symbol>() {

            public Type visitType(Type t, Symbol sym) {
		//System.err.println("VISIT: "+t+" in "+sym);
                return sym.type;
            }

            @Override
            public Type visitWildcardType(WildcardType t, Symbol sym) {
                return memberType(upperBound(t), sym);
            }

            @Override
            public Type visitClassType(ClassType t, Symbol sym) {
                Symbol owner = sym.owner;
                long flags = sym.flags();
		if (owner == null) {
		    return sym.type;
		}
		if (owner.type == null) {
		    //System.err.println("owner.type of "+sym+" is null: "+ owner.name);
		    return sym.type;
		}
                if (((flags & STATIC) == 0) && owner.type.isParameterized()) {
                    Type base = asOuterSuper(t, owner);
		    //System.err.println("t="+toF3String(t));
		    //System.err.println("base="+toF3String(base));
                    if (base != null) {
                        List<Type> ownerParams = owner.type.allparams();
                        List<Type> baseParams = base.allparams();
                        if (ownerParams.nonEmpty()) {
                            if (baseParams.isEmpty()) {
                                // then base is a raw type
                                return erasure(sym.type);
                            } else {
				if (false) {
				    //System.err.println("sym="+sym+", sym.type="+sym.type);
				    for (Type t1: sym.type.getTypeArguments()) {
					System.err.println("sym.type.param="+t1.getClass()+"@"+System.identityHashCode(t1) + ": "+t1);
				    }
				    for (Type t1: ownerParams) {
					System.err.println("ownerParam="+t1.getClass()+"@"+System.identityHashCode(t1) + ": "+t1);
				    }
				    for (Type t1: baseParams) {
					System.err.println("baseParam="+t1.getClass()+"@"+System.identityHashCode(t1) + ": "+t1);
				    }
				    if (sym.type instanceof MethodType) {
					MethodType mt= (MethodType)sym.type;
					for (Type t1: mt.argtypes) {
					    System.err.println("arg'="+t1.getClass()+"@"+System.identityHashCode(t1) + ": "+t1);
					}
				    }
				}
				Type r = subst2(sym.type, ownerParams, baseParams, true);
				//if (true) System.err.println("subst "+sym.type +" => "+r);
				return r;
                            }
                        }
                    }
                }
                return sym.type;
            }

            @Override
            public Type visitTypeVar(TypeVar t, Symbol sym) {
		//System.err.println("member type "+ toF3String(t));
		//System.err.println("bound="+toF3String(t.bound));
		if (t instanceof TypeCons) {
		}
                return memberType(t.bound, sym);
            }

            @Override
            public Type visitErrorType(ErrorType t, Symbol sym) {
                return t;
            }
        };
    // </editor-fold>


    public List<Type> subst2(List<Type> ts,
			     List<Type> from,
			     List<Type> to) {
        return new Subst(from, to).subst(ts);
    }

    public Type subst2(Type t, List<Type> from, List<Type> to) {
        return new Subst(from, to).subst(t);
    }

    boolean HACK;

    public Type subst2(Type t, List<Type> from, List<Type> to, boolean hack) {
	ListBuffer<Type> froms = ListBuffer.lb();
	for (Type x: from) {
	    froms.append(unexpandWildcard(x));
	}
	from = froms.toList();
        Subst s = new Subst(from, to);
	try {
	    HACK = hack;
	    return s.subst(t);
	} finally {
	    HACK = false;
	}
    }

    private class Subst extends UnaryVisitor<Type> {

        List<Type> from;
        List<Type> to;

        public Subst(List<Type> from, List<Type> to) {
            int fromLength = from.length();
            int toLength = to.length();
            while (fromLength > toLength) {
                fromLength--;
                from = from.tail;
            }
            while (fromLength < toLength) {
                toLength--;
                to = to.tail;
            }
            this.from = from;
            this.to = to;
        }

        Type subst(Type t) {
	    if (t == null) {
		//System.err.println("type is null");
		//Thread.currentThread().dumpStack();
		return syms.botType;
	    }
            if (from.tail == null)
                return t;
            else
                return visit(t);
        }

        List<Type> subst(List<Type> ts) {
            if (from.tail == null)
                return ts;
            boolean wild = false;
            if (ts.nonEmpty() && from.nonEmpty()) {
                Type head1 = subst(ts.head);
                List<Type> tail1 = subst(ts.tail);
                if (head1 != ts.head || tail1 != ts.tail)
                    return tail1.prepend(head1);
            }
            return ts;
        }

        public Type visitType(Type t, Void ignored) {
            return t;
        }

        @Override
        public Type visitMethodType(MethodType t, Void ignored) {
            List<Type> argtypes = subst(t.argtypes);
            Type restype = subst(t.restype);
            List<Type> thrown = subst(t.thrown);
            if (argtypes == t.argtypes &&
                restype == t.restype &&
                thrown == t.thrown)
                return t;
            else
                return new MethodType(argtypes, restype, thrown, t.tsym);
        }

	java.util.Map<Symbol, Type> visited = new java.util.HashMap();

        @Override
        public Type visitTypeVar(TypeVar t, Void ignored) {
	    Type t1 = visitTypeVar0(t, ignored);
	    //System.err.println("SUBST "+ toF3String(t) +" => "+toF3String(t1)+ ", from="+from+", to="+to);
	    return t1;
	}

        public Type visitTypeVar0(TypeVar t, Void ignored) {
	    //System.err.println("Subst t="+System.identityHashCode(t)+"@"+t.getClass()+": "+t);
	    final TypeSymbol tsym = t.tsym;
	    Type seen = visited.get(tsym);
	    if (seen != null) {
		return seen;
	    }
	    visited.put(tsym, t);
            for (List<Type> from = this.from, to = this.to;
                 from.nonEmpty();
                 from = from.tail, to = to.tail) {
		//System.err.println("Subst from.head="+System.identityHashCode(from.head)+"@"+from.head.getClass()+": "+from.head);
		//System.err.println("t="+t);
		//System.err.println("from="+from.head);
		//System.err.println("to="+to.head);
		if (t instanceof TypeCons) {
		    TypeCons tc1 = (TypeCons)t;
		    if (from.head instanceof TypeCons) {
			TypeCons tc2 = (TypeCons)from.head;
			if (tc1.ctor == tc2) {
			    //System.err.println("t="+t);
			    //System.err.println("from.head="+from.head);
			    //System.err.println("to.head="+to.head);
			    Type res = makeTypeCons(to.head, tc1.getTypeArguments());
			    //System.err.println("res="+res);
			    res = subst2(res, from, to);
			    //System.err.println("res'="+res);
			    return res;
			} else {
			    //System.err.println("no match "+tc1.ctor+" "+tc2);
			}
		    }
		}
                if (t == from.head //) { 
		    || t.tsym == from.head.tsym
		    || (HACK && t.tsym.name == from.head.tsym.name)) { // hack!!!
                    Type rt = to.head.withTypeVar(t);
		    visited.put(tsym, rt);
		    return rt;
                }
            }
	    TypeVar t1 = t;
	    if (t.lower != syms.botType || t.bound != syms.objectType) {
		t1 = newTypeVar(tsym, syms.objectType, syms.botType);
		visited.put(tsym, t1);
		Type lower = visit(t.lower, null);
		if (t.bound == null) {
		    t.bound = syms.objectType;
		    //System.err.println("bound was null: "+ t);
		} 
		Type upper = visit(t.bound, null);
		t1.bound = upper;
		t1.lower = lower;
		if (t instanceof TypeCons) {
		    TypeCons c = (TypeCons)t;
		    if (c.ctor != null) {
			//System.err.println("typecons => "+t);
			//System.err.println("typecons' => "+t1);
			t = new TypeCons(t1.tsym.name,
					 t1.tsym,
					 c.bound,
					 subst(t.getTypeArguments()));
		    }
		} else {
		    t = t1;
		}
	    }
            return t;
        }

        @Override
        public Type visitClassType(ClassType t, Void ignored) {
            if (!t.isCompound()) {
                List<Type> typarams = t.getTypeArguments();
                List<Type> typarams1 = subst(typarams);
                Type outer = t.getEnclosingType();
                Type outer1 = subst(outer);
                if (typarams1 == typarams && outer1 == outer)
                    return t;
                else
                    return newClassType(outer1, typarams1, t.tsym);
            } else {
                Type st = subst(supertype(t));
                List<Type> is = upperBounds(subst(interfaces(t)));
                if (st == supertype(t) && is == interfaces(t))
                    return t;
                else
                    return makeCompoundType(is.prepend(st));
            }
        }

        @Override
        public Type visitWildcardType(WildcardType t, Void ignored) {
            Type bound = t.type;
            if (t.kind != BoundKind.UNBOUND) {
                bound = subst(bound);
	    } 
            if (bound == t.type) {
                return t;
            } else {
                if (t.isExtendsBound() && bound.isExtendsBound())
                    bound = upperBound(bound);
                return new WildcardType(bound, t.kind, syms.boundClass, t.bound);
            }
        }

        @Override
        public Type visitArrayType(ArrayType t, Void ignored) {
            Type elemtype = subst(t.elemtype);
            if (elemtype == t.elemtype)
                return t;
            else
                return new ArrayType(upperBound(elemtype), t.tsym);
        }

        @Override
        public Type visitForAll(ForAll t, Void ignored) {
            List<Type> tvars1 = F3Types.this.subst2(t.tvars, from, to);
            Type qtype1 = subst(t.qtype);
            if (tvars1 == t.tvars && qtype1 == t.qtype) {
                return t;
            } else if (tvars1 == t.tvars && (qtype1 instanceof ForAll)) {
                return newForAll(tvars1, qtype1);
            } else {
		Type x = F3Types.this.subst2(qtype1, t.tvars, tvars1);
		if (false && x instanceof ClassType) {
		    System.err.println("from="+from+", to="+to);
		    System.err.println("tvars="+t.tvars);
		    System.err.println("tvars1="+tvars1);
		    System.err.println("x="+x);
		    System.err.println("qtype1="+qtype1);
		    //return x;
		}
                return newForAll(tvars1, x);
            }
        }

        @Override
        public Type visitErrorType(ErrorType t, Void ignored) {
            return t;
        }
    }

    TypeVar newTypeVar(TypeSymbol sym, Type bound, Type lower) {
	if (sym.type instanceof TypeCons) {
	    //throw new Error("typecons "+ sym);
	}
	if (sym.type instanceof TypeVarDefn) {
	    TypeVarDefn base = (TypeVarDefn)sym.type;
	    //TypeVarDefn def = new TypeVarDefn(base, base.variance);
	    //def.lower = lower;
	    //def.bound = bound;
	    return base;
	}
	return new SubstTypeVar(sym, bound, lower);
    }

    public static class SubstTypeVar extends TypeVar {
	public SubstTypeVar(TypeSymbol sym, Type bound, Type lower) {
	    super(sym, bound, lower);
	}
    }

    public ForAll newForAll(List<Type> targs, Type t) {
	if (t instanceof FunctionType) {
	    t = ((FunctionType)t).asMethodOrForAll();
	}
	if (t instanceof ClassType) {
	    //throw new IllegalArgumentException(t.getClass()+ ": "+toF3String(t));
	}
	return new ForAll(targs, t);
    }

    public ClassType newClassType(Type enclosing, List<Type> targs, TypeSymbol sym) {
	if (enclosing == null) {
	    //System.err.println(sym.name + " "+targs);
	    throw new NullPointerException("enclosing is null: "+sym.name);
	}
	return new ClassType(enclosing, targs, sym);
    }
    /*
    public Type upperBound(Type t) {
	if (t instanceof TypeVar) {
	    Thread.currentThread().dumpStack();
	}
	return super.upperBound(t);
    }
    */

    // <editor-fold defaultstate="collapsed" desc="Internal utility methods">
    private List<Type> upperBounds(List<Type> ss) {
        if (ss.isEmpty()) return ss;
        Type head = upperBound(ss.head);
        List<Type> tail = upperBounds(ss.tail);
        if (head != ss.head || tail != ss.tail)
            return tail.prepend(head);
        else
            return ss;
    }
    /*
    public BoundKind variance(final TypeVar t, BoundKind in) {
        class VarianceAnalyzer extends SimpleVisitor<BoundKind, BoundKind> {
	    Set visited = new HashSet();
            @Override
            public BoundKind visitTypeVar(TypeVar t0, BoundKind bk) {
		if (visited.contains(t0)) {
		    return bk;
		}
		visited.add(t0);
		TypeVar t = t0;
                BoundKind bk = visit(t.getUpperBound(), bk);
		if ("<captured wildcard>".equals(t.tsym.name.toString())) { // major hack
		    return upper;
		}
		t = newTypeVar(t.tsym, upper, t.lower);
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
		    t = newClassType(encl2, args2, t.tsym);
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
                    return syms.botType;
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
        return new VarianceAnalyzer().visit(t, true);
    }
    */


    public List<Type> interfaces(Type t) {
        return interfaces.visit(t);
    }
    // where
        private UnaryVisitor<List<Type>> interfaces = new UnaryVisitor<List<Type>>() {

            public List<Type> visitType(Type t, Void ignored) {
                return List.nil();
            }

            @Override
            public List<Type> visitClassType(ClassType t, Void ignored) {
                if (t.interfaces_field == null) {
                    List<Type> interfaces = ((ClassSymbol)t.tsym).getInterfaces();
                    if (t.interfaces_field == null) {
                        // If t.interfaces_field is null, then t must
                        // be a parameterized type (not to be confused
                        // with a generic type declaration).
                        // Terminology:
                        //    Parameterized type: List<String>
                        //    Generic type declaration: class List<E> { ... }
                        // So t corresponds to List<String> and
                        // t.tsym.type corresponds to List<E>.
                        // The reason t must be parameterized type is
                        // that completion will happen as a side
                        // effect of calling
                        // ClassSymbol.getInterfaces.  Since
                        // t.interfaces_field is null after
                        // completion, we can assume that t is not the
                        // type of a class/interface declaration.
                        assert t != t.tsym.type : t.toString();
                        List<Type> actuals = t.allparams();
                        List<Type> formals = t.tsym.type.allparams();
                        if (actuals.isEmpty()) {
                            if (formals.isEmpty()) {
                                // In this case t is not generic (nor raw).
                                // So this should not happen.
                                t.interfaces_field = interfaces;
                            } else {
                                t.interfaces_field = erasure(interfaces);
                            }
                        } else {
			    List<Type> s = subst2(interfaces, formals, actuals);
                            t.interfaces_field = upperBounds(s);
                        }
                    }
                }
                return t.interfaces_field;
            }

            @Override
            public List<Type> visitTypeVar(TypeVar t, Void ignored) {
                if (t.bound.isCompound())
                    return interfaces(t.bound);

                if (t.bound.isInterface())
                    return List.of(t.bound);

                return List.nil();
            }
        };
    // </editor-fold>

    boolean isSameSymbol(Symbol x, Symbol y) {
	if (x.name == y.name) {
	    if (x.owner == y.owner) {
		return true;
	    }
	    //System.err.println("different owners: "+ x.owner + " " + y.owner);
	}
	return false;
    }

    private DefaultTypeVisitor<Type,Type> fixW = new DefaultTypeVisitor<Type,Type>() {

	public void visit(List<Type> ts) {
	    for (Type t : ts) {
		visit(t, t);
	    }
	}

        public Type visitWildcardType(WildcardType t, Type s) {
	    if (t.type instanceof WildcardType) {
		t.type = (WildcardType)t.type;
	    }
	    return t;
	}
        public Type visitClassType(ClassType t, Type s) { 
	    visit(t.getTypeArguments());
	    return t;
	}
        public Type visitArrayType(ArrayType t, Type s) { 
	    visit(t.elemtype, s);
	    return t;
	}
        public Type visitMethodType(MethodType t, Type s)     { 
	    visit(t.argtypes);
	    visit(t.restype, s);
	    return t;
	}

        public Type visitPackageType(PackageType t, Type s)   { return visitType(t, s); }
        public Type visitTypeVar(TypeVar t, Type s)           { return visitType(t, s); }
        public Type visitCapturedType(CapturedType t, Type s) { 
	    visit(t.getTypeArguments());
	    visit(t.wildcard, s);
	    return t;
	}
        public Type visitForAll(ForAll t, Type s)             { 
	    visit(t.qtype, s);
	    return visitType(t, s); 
	}
        public Type visitUndetVar(UndetVar t, Type s)         { return visitType(t, s); }
        public Type visitErrorType(ErrorType t, Type s)       { return visitType(t, s); }
	public Type visitType(Type t, Type s) {
	    return t;
	}
    };
    public Type fixWildcards(Type t) {
	fixW.visit(t, t);
	return t;
    }
}
