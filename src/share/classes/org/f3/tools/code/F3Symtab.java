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

import com.sun.tools.mjavac.code.Symtab;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.ForAll;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Type.*;
import static com.sun.tools.mjavac.jvm.ByteCodes.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.code.TypeTags;
import org.f3.tools.comp.F3Defs;
import com.sun.tools.mjavac.code.Flags;

/**
 *
 * @author Robert Field
 */
public class F3Symtab extends Symtab {

    private static final String anno = F3Defs.annotation_PackageString;
    public static final String privateAnnotationClassNameString = anno + ".Private";
    public static final String protectedAnnotationClassNameString = anno + ".Protected";
    public static final String packageAnnotationClassNameString = anno + ".Package";
    public static final String publicAnnotationClassNameString = anno + ".Public";
    public static final String scriptPrivateAnnotationClassNameString = anno + ".ScriptPrivate";
    public static final String defaultAnnotationClassNameString = anno + ".Default";
    public static final String publicInitAnnotationClassNameString = anno + ".PublicInitable";
    public static final String publicReadAnnotationClassNameString = anno + ".PublicReadable";
    public static final String bindeesAnnotationClassNameString = anno + ".F3Bindees";
    public static final String signatureAnnotationClassNameString = anno + ".F3Signature";
    public static final String defAnnotationClassNameString = anno + ".Def";
    public static final String staticAnnotationClassNameString = anno + ".Static";
    public static final String inheritedAnnotationClassNameString = anno + ".Inherited";
    public static final String sourceNameAnnotationClassNameString = anno + ".SourceName";

    // F3 built-in(value) types
    public final Type f3_BooleanType;
    public final Type f3_CharacterType;
    public final Type f3_ByteType;
    public final Type f3_ShortType;
    public final Type f3_IntegerType;
    public final Type f3_LongType;
    public final Type f3_FloatType;
    public final Type f3_DoubleType;
    public final Type f3_NumberType;
    public final Type f3_StringType;
    public final Type f3_DurationType;
    public final Type f3_LengthType;
    public final Type f3_LengthUnitType;
    public final Type f3_AngleType;
    public final Type f3_AngleUnitType;
    public final Type f3_ColorType;

    // F3 other types
    public final Type f3_AnyType;
    public final Type f3_UnspecifiedType;
    public final Type f3_AutoImportRuntimeType;
    public final Type f3_RuntimeType;
    public final Type f3_VoidType;
    public final Type f3_java_lang_VoidType;
    public final Type f3_SequenceType;
    public final Type f3_FunctorType;
    public final Type f3_MonadType;
    public final Type f3_ComonadType;
    public final Type f3_TypeConsType;
    public final Type[] f3_TypeCons;

    public final Type f3_SequenceRefType;
    public final Type f3_SequenceProxyType;
    public final Type f3_ArraySequenceType;
    public final Type f3_EmptySequenceType;
    public final Type f3_SequenceTypeErasure;
    public final Type f3_TypeConsTypeErasure;
    public final Type[] f3_TypeConsErasure;
    public final Type f3_FunctorTypeErasure;
    public final Type f3_MonadTypeErasure;
    public final Type f3_ComonadTypeErasure;
    public final Type f3_ShortArray;
    public final Type f3_ObjectArray;
    static public final int MAX_FIXED_PARAM_LENGTH = 8;
    public final Type[] f3_FunctionTypes = new Type[MAX_FIXED_PARAM_LENGTH+1];
    public final Type f3_ObjectType;
    public final Type f3_MixinType;
    public final Type f3_BaseType;
    public final Type f3_SequencesType;
    public final Type f3_KeyValueType;
    public final Type f3_KeyFrameType;
    public final Type f3_KeyValueTargetType;
    public final Type f3_PointerType;
    public final Type f3_ReadOnlyPointerType;
    public final Type f3_PointerTypeErasure;
    public final Type f3_ConstantType;
    public final Type f3_BoundForOverSequenceType;
    public final Type f3_BoundForOverNullableSingletonType;
    public final Type f3_BoundForOverSingletonType;
    public final Type f3_ForPartInterfaceType;
    public final Type f3_NonLocalReturnExceptionType;
    public final Type f3_NonLocalBreakExceptionType;
    public final Type f3_NonLocalContinueExceptionType;

    public final Type f3_protectedAnnotationType;
    public final Type f3_packageAnnotationType;
    public final Type f3_publicAnnotationType;
    public final Type f3_scriptPrivateAnnotationType;
    public final Type f3_defaultAnnotationType;
    public final Type f3_publicInitAnnotationType;
    public final Type f3_publicReadAnnotationType;
    public final Type f3_signatureAnnotationType;
    public final Type f3_defAnnotationType;
    public final Type f3_staticAnnotationType;
    public final Type f3_inheritedAnnotationType;
    public final Type f3_sourceNameAnnotationType;

    public final Name booleanTypeName;
    public final Name charTypeName;
    public final Name byteTypeName;
    public final Name shortTypeName;
    public final Name integerTypeName;
    public final Name longTypeName;
    public final Name floatTypeName;
    public final Name doubleTypeName;
    public final Name numberTypeName;
    public final Name stringTypeName;
    public final Name voidTypeName;

    public final Name runMethodName;

    /** The type of expressions that never returns a value to its parent.
     * E.g. an expression that always throws an Exception.
     * Likewise, a "return expression" returns from the outer function,
     * which makes any following/surrounding code unreachable.
     */
    public final Type unreachableType;

    private F3Types types;

    public static final String functionClassPrefix =
            "org.f3.functions.Function";

    public static void preRegister(final Context context) {
        if (context.get(symtabKey) == null)
            context.put(symtabKey, new Context.Factory<Symtab>() {
                public Symtab make() {
                    return new F3Symtab(context);
                }
            });
    }

    public static void preRegister(final Context context, Symtab syms) {
        context.put(symtabKey, syms);
    }

    public static Symtab instance(Context context) {
        Symtab instance = context.get(Symtab.symtabKey);
        if (instance == null)
            instance = new F3Symtab(context);
        return instance;
    }
    
    
    /** Creates a new instance of F3Symtab */
    F3Symtab(Context context) {
        super(context);

        // FIXME It would be better to make 'names' in super-class be protected.
        Name.Table names = Name.Table.instance(context);
        types = F3Types.instance(context);
        Options options = Options.instance(context);
        String numberChoice = options.get("Number");

        // Make the array length var symbol a F3 var symbol
        F3VarSymbol f3LengthVar = new F3VarSymbol(
            types,
            names,
            Flags.PUBLIC | Flags.FINAL ,
            names.length,
            intType,
            arrayClass);
        arrayClass.members().remove(lengthVar);
        arrayClass.members().enter(f3LengthVar);

        f3_BooleanType = booleanType;
        f3_CharacterType = charType;
        f3_ByteType = byteType;
        f3_ShortType = shortType;
        f3_IntegerType = intType;
        f3_LongType = longType;
        f3_FloatType = floatType;
        f3_DoubleType = doubleType;
        if (numberChoice == null) {
            //default
            f3_NumberType = floatType;
        } else if (numberChoice.equals("Float")) {
            f3_NumberType = floatType;
        } else if (numberChoice.equals("Double")) {
            f3_NumberType = doubleType;
        } else {
            throw new IllegalArgumentException("Bad argument for Number, must be Float pr Double");
        }
        f3_StringType = stringType;
        f3_DurationType = enterClass("f3.lang.Duration");
        f3_LengthType = enterClass("f3.lang.Length");
        f3_LengthUnitType = enterClass("f3.lang.LengthUnit");
        f3_AngleType = enterClass("f3.lang.Angle");
        f3_AngleUnitType = enterClass("f3.lang.AngleUnit");
        f3_ColorType = enterClass("f3.lang.Color");

        f3_AnyType = objectType;
        f3_UnspecifiedType = unknownType;
        f3_VoidType = voidType;

        f3_AutoImportRuntimeType = enterClass("f3.lang.Builtins");
        f3_RuntimeType = enterClass("f3.lang.F3");
        unreachableType = new Type(TypeTags.VOID, null);
        unreachableType.tsym = new TypeSymbol(0, names.fromString("<unreachable>"), Type.noType, rootPackage);
        f3_java_lang_VoidType = types.boxedClass(voidType).type;
        f3_SequenceType = enterClass(F3Defs.cSequence);
        f3_FunctorType = enterClass(F3Defs.cFunctor);
        f3_MonadType = enterClass(F3Defs.cMonad);
        f3_ComonadType = enterClass(F3Defs.cComonad);
        f3_TypeConsType = enterClass(F3Defs.cTypeCons);
	f3_TypeCons = new Type[5];
	f3_TypeConsErasure = new Type[5];
	for (int i = 1; i <= 5; i++) {
	    f3_TypeCons[i-1] = enterClass(F3Defs.cTypeCons+""+i);
	    f3_TypeConsErasure[i-1] = types.erasure(f3_TypeCons[i-1]);
	}
        f3_SequenceRefType = enterClass(F3Defs.cSequenceRef);
        f3_SequenceProxyType = enterClass(F3Defs.cSequenceProxy);
        f3_ArraySequenceType = enterClass(F3Defs.cArraySequence);
        f3_SequencesType = enterClass(F3Defs.cSequences);
        f3_EmptySequenceType = types.sequenceType(objectType);
        f3_SequenceTypeErasure = types.erasure(f3_SequenceType);
        f3_TypeConsTypeErasure = types.erasure(f3_TypeConsType);
        f3_FunctorTypeErasure = types.erasure(f3_FunctorType);
        f3_MonadTypeErasure = types.erasure(f3_MonadType);
        f3_ComonadTypeErasure = types.erasure(f3_ComonadType);
        f3_ShortArray = new ArrayType(shortType, arrayClass);
        f3_ObjectArray = new ArrayType(objectType, arrayClass);
        f3_KeyValueType = enterClass("f3.animation.KeyValue");
        f3_KeyFrameType = enterClass("f3.animation.KeyFrame");
        f3_KeyValueTargetType = enterClass("f3.animation.KeyValueTarget");
        f3_PointerType = enterClass("org.f3.runtime.Pointer");
        f3_ReadOnlyPointerType = enterClass("org.f3.runtime.ConstPointer");
        f3_PointerTypeErasure = types.erasure(f3_PointerType);
        f3_ConstantType = enterClass("org.f3.runtime.F3Constant");
        f3_BoundForOverSequenceType = enterClass(F3Defs.cBoundForOverSequence);
        f3_BoundForOverNullableSingletonType = enterClass(F3Defs.cBoundForOverNullableSingleton);
        f3_BoundForOverSingletonType = enterClass(F3Defs.cBoundForOverSingleton);
        f3_ForPartInterfaceType = enterClass(F3Defs.cBoundForPartI);
        f3_NonLocalReturnExceptionType = enterClass(F3Defs.cNonLocalReturnException);
        f3_NonLocalBreakExceptionType = enterClass(F3Defs.cNonLocalBreakException);
        f3_NonLocalContinueExceptionType = enterClass(F3Defs.cNonLocalContinueException);
        f3_protectedAnnotationType = enterClass(protectedAnnotationClassNameString);
        f3_packageAnnotationType = enterClass(packageAnnotationClassNameString);
        f3_publicAnnotationType = enterClass(publicAnnotationClassNameString);
        f3_scriptPrivateAnnotationType = enterClass(scriptPrivateAnnotationClassNameString);
        f3_defaultAnnotationType = enterClass(defaultAnnotationClassNameString);
        f3_publicInitAnnotationType = enterClass(publicInitAnnotationClassNameString);
        f3_publicReadAnnotationType = enterClass(publicReadAnnotationClassNameString);
        f3_signatureAnnotationType = enterClass(signatureAnnotationClassNameString);
        f3_defAnnotationType = enterClass(defAnnotationClassNameString);
        f3_staticAnnotationType = enterClass(staticAnnotationClassNameString);
        f3_inheritedAnnotationType = enterClass(inheritedAnnotationClassNameString);
        f3_sourceNameAnnotationType = enterClass(sourceNameAnnotationClassNameString);
        for (int i = MAX_FIXED_PARAM_LENGTH; i >= 0;  i--) {
            f3_FunctionTypes[i] = enterClass(functionClassPrefix+i);
        }

        booleanTypeName = names.fromString("Boolean");
        charTypeName = names.fromString("Character");
        byteTypeName = names.fromString("Byte");
        shortTypeName = names.fromString("Short");
        integerTypeName = names.fromString("Integer");
        longTypeName = names.fromString("Long");
        floatTypeName = names.fromString("Float");
        doubleTypeName = names.fromString("Double");
        numberTypeName  = names.fromString("Number");
        stringTypeName = names.fromString("String");
        voidTypeName = names.fromString("Void");

        runMethodName = names.fromString(F3Defs.internalRunFunctionString);

        f3_ObjectType = enterClass(F3Defs.cObject);
        f3_MixinType = enterClass(F3Defs.cMixin);
        f3_BaseType = enterClass(F3Defs.cBase);
        
        enterOperators();
    }

    @Override
    public void enterOperators() {
        super.enterOperators();

        enterBinop("<>", objectType, objectType, booleanType, if_acmpne);
        enterBinop("<>", booleanType, booleanType, booleanType, if_icmpne);
        enterBinop("<>", doubleType, doubleType, booleanType, dcmpl, ifne);
        enterBinop("<>", floatType, floatType, booleanType, fcmpl, ifne);
        enterBinop("<>", longType, longType, booleanType, lcmp, ifne);
        enterBinop("<>", intType, intType, booleanType, if_icmpne);

        enterBinop("and", booleanType, booleanType, booleanType, bool_and);
        enterBinop("or", booleanType, booleanType, booleanType, bool_or);

        // Enter F3 operators.
        enterUnop("sizeof", f3_SequenceType, f3_IntegerType, 0);

        //TODO: I think these are ancient garbage, needs verification
        enterUnop("lazy", doubleType, doubleType, 0);
        enterUnop("lazy", intType, intType, 0);
        enterUnop("lazy", booleanType, booleanType, 0);
        enterUnop("lazy", objectType, objectType, 0);

        enterUnop("bind", doubleType, doubleType, 0);
        enterUnop("bind", intType, intType, 0);
        enterUnop("bind", booleanType, booleanType, 0);
        enterUnop("bind", objectType, objectType, 0);
    }

    public boolean isRunMethod(Symbol sym) {
        return sym.name == runMethodName;
    }

    private Type boxedTypeOrType(Type elemType) {
        if (elemType.isPrimitive() || elemType == voidType)
            return types.boxedClass(elemType).type;
        else
            return elemType;
    }

    public FunctionType makeFunctionType(int nargs, List<Type> typarams) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        Type restype = null;
        for (List<Type> l = typarams; l.nonEmpty();  l = l.tail) {
            Type a = l.head;
            if (a instanceof WildcardType)
                a = ((WildcardType) a).type;
            if (restype == null) {
                if (a.tsym != null && a.tsym.name == f3_java_lang_VoidType.tsym.name) {
                    a = voidType;
                }
                restype = a;
            }
            else
                argtypes.append(a);
        }
        MethodType mtype = new MethodType(argtypes.toList(), restype, List.<Type>nil(), methodClass);
        return makeFunctionType(nargs, typarams, mtype);
    }

    public FunctionType makeFunctionType(List<Type> typarams) {
        ListBuffer<Type> argtypes = new ListBuffer<Type>();
        Type restype = null;
        for (List<Type> l = typarams; l.nonEmpty();  l = l.tail) {
            Type a = l.head;
            if (a instanceof WildcardType)
                a = ((WildcardType) a).type;
            if (restype == null) {
                if (a.tsym != null && a.tsym.name == f3_java_lang_VoidType.tsym.name) {
                    a = voidType;
                }
                restype = a;
            }
            else
                argtypes.append(a);
        }
        MethodType mtype = new MethodType(argtypes.toList(), restype, List.<Type>nil(), methodClass);
        return makeFunctionType(typarams, mtype);
    }

    public FunctionType makeFunctionType(List<Type> typarams, MethodType mtype) {
        int nargs = typarams.size()-1;
	if (nargs < 0) nargs = 0;
        return makeFunctionType(nargs, typarams, mtype);
    }

    public FunctionType makeFunctionType(int nargs, List<Type> typarams, MethodType mtype) {
        assert (nargs <= MAX_FIXED_PARAM_LENGTH);
        Type funtype = f3_FunctionTypes[nargs];
        FunctionType ftype = 
	    new FunctionType(funtype.getEnclosingType(), typarams, funtype.tsym, mtype);
	ftype.typeArgs = mtype.getTypeArguments();
	return ftype;
    }

    public FunctionType asFunctionType(Type t) {
	if (t instanceof ForAll) {
	    return makeFunctionType((ForAll)t);
	}
	if (t instanceof FunctionType) {
	    return (FunctionType)t;
	}
	return makeFunctionType(t.asMethodType());
    }

    public FunctionType makeFunctionType(ForAll fa) {
	FunctionType ft = makeFunctionType(fa.asMethodType());
	ft.typeArgs = fa.getTypeArguments();
	return ft;
    }

    /** Given a MethodType, create the corresponding FunctionType.
     */
    public FunctionType makeFunctionType(MethodType mtype) {
        Type rtype = mtype.restype;
        ListBuffer<Type> typarams = new ListBuffer<Type>();
        typarams.append(boxedTypeOrType(rtype));
        for (List<Type> l = mtype.argtypes; l.nonEmpty(); l = l.tail) {
            typarams.append(boxedTypeOrType(l.head));
        }
        int nargs = typarams.size()-1;
	if (nargs < 0) nargs = 0;
        return makeFunctionType(nargs, typarams.toList(), mtype);
    }

    /** Make public. */
    @Override
    public Type enterClass(String name) {
        return super.enterClass(name);
    }
}
