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

package f3.reflect;

/** Context for reflective operations.
 * All the various operations are based on a {@code F3Context}.
 * This is similar to JDI's {@code VirtualMachine} interface.
 * In "normal" useage there is a single {@code F3Context} that is
 * basically a wrapper around {@code java.lang.reflect}, but (for
 * example) for remote reflection you could have an implementation
 * based on JDI.
 * Corresponds to {@code com.sun.jdi.VirtualMachine}.
 *
 * @author Per Bothner
 * @profile desktop
 */

public abstract class F3Context {
    /** Find context-dependent default {@code F3Context}.
     * (For now, this always returns the same {@code LocalReflectionContext}.)
     */
    public static F3Context getInstance() {
        // For now - later might do some more fancy searching.
        return F3Local.getContext();
    }

    protected F3Context() {
    }

    public static final String MIXIN_SUFFIX = "$Mixin";
    public static final String F3OBJECT_NAME =
            "org.f3.runtime.F3Object";
    public static final String F3MIXIN_NAME =
            "org.f3.runtime.F3Mixin";
    public static final String F3BASE_NAME =
            "org.f3.runtime.F3Base";
    
    /** Get the {@code F3ClassType} for the class with the given name. */
    public abstract F3ClassType findClass(String name);
    F3Type anyType = findClass("java.lang.Object");

    /** Get the {@code F3Type} for the "any" type. */
    public F3Type getAnyType() { return anyType; }

    final java.util.Map<F3Type, F3Type> typeMap = new java.util.WeakHashMap();

    public F3Type subst(F3Type type) {
        F3Type r = typeMap.get(type);
        if (r != null) return r;
        if (type instanceof F3FunctionType) {
            F3FunctionType funType = (F3FunctionType)type;
            boolean needSubst = false;
            F3Type s = null;
            int k = -1;
            for (int i = 0, count = funType.minArgs(); i < count; i++) {
                F3Type t = funType.getArgumentType(i);
                s = subst(t);
                if (s != t) {
                    needSubst = true;
                    k = i;
                    break;
                }
            }
            if (!needSubst) {
                s = subst(funType.getReturnType());
                if (s != funType.getReturnType()) {
                    needSubst = true;
                }
            }
            if (needSubst) {
                F3Type[] args = new F3Type[funType.minArgs()];
                for (int i = 0, count = funType.minArgs(); i < count; i++) {
                    F3Type t = funType.getArgumentType(i);
                    args[i] = i == k ? s : subst(t);
                }
                F3Type ret = k == -1 ? s : subst(funType.getReturnType());
                F3Type newType = makeFunctionType(args, ret);
                typeMap.put(type, newType);
                return newType;
            }
        }
        if (type instanceof F3ClassType) {
            final F3ClassType clazzType = (F3ClassType)type;
            if (clazzType.isParameterized()) {
                final F3Type[] typeParams = clazzType.getTypeParameters();
                final F3Type[] typeArgs = clazzType.getTypeArguments();
                for (int i = 0; i < typeParams.length; i++) {
                    final F3Type s = subst(typeParams[i]);
                    if (s != typeParams[i] && typeArgs.length == 0 || s != typeArgs[i]) {
                        type = instantiateType(clazzType, subst(typeParams));
                        typeMap.put(clazzType, type);
                        break;
                    }
                }
            }
        }
        return type;
    }

    public abstract F3ClassType instantiateType(final F3ClassType base, final F3Type[] typeParams);
    public abstract F3FunctionType makeFunctionType(F3Type[] argTypes, F3Type returnType);

    public F3Type[] subst(F3Type[] params) {
        boolean needSubst = false;
        F3Type substituted = null;
        int k = -1;
        for (int i = 0; i < params.length; i++) {
            if ((substituted = subst(params[i])) != params[i]) {
                k = i;
                needSubst = true;
                break;
            }
        }
        if (needSubst) {
            F3Type[] result = new F3Type[params.length];
            for (int i = 0; i < params.length; i++) {
                result[i] = k == i ? substituted : subst(params[i]);
            }
            return result;
        }
        return params;
    }

    public F3PrimitiveType getPrimitiveType(String typeName) {
        if (typeName.startsWith("java.lang."))
            typeName = typeName.substring(10);
        else if (typeName.indexOf('.') >= 0)
            return null;
        if (typeName.equals("Boolean"))
            return getBooleanType();
        if (typeName.equals("Character"))
            return getCharacterType();
        if (typeName.equals("Byte"))
            return getByteType();
        if (typeName.equals("Short"))
            return getShortType();
        if (typeName.equals("Integer") || typeName.equals("Int"))
            return getIntegerType();
        if (typeName.equals("Long"))
            return getLongType();
        if (typeName.equals("Float"))
            return getFloatType();
        if (typeName.equals("Double"))
            return getDoubleType();
        if (typeName.equals("Void"))
            return F3PrimitiveType.voidType;
        return null;
    }

    /** Get the run-time representation of the F3 {@code Boolean} type. */
    public F3PrimitiveType getBooleanType() {
        return F3PrimitiveType.booleanType;
    }

    /** Get the run-time representation of the F3 {@code Character} type. */
    public F3PrimitiveType getCharacterType() {
        return F3PrimitiveType.charType;
    }

    /** Get the run-time representation of the F3 {@code Byte} type. */
    public F3PrimitiveType getByteType() {
        return F3PrimitiveType.byteType;
    }

     /** Get the run-time representation of the F3 {@code Short} type. */
    public F3PrimitiveType getShortType() {
        return F3PrimitiveType.shortType;
    }

   /** Get the run-time representation of the F3 {@code Integer} type. */
    public F3PrimitiveType getIntegerType() {
        return F3PrimitiveType.integerType;
    }

   /** Get the run-time representation of the F3 {@code Long} type. */
    public F3PrimitiveType getLongType() {
        return F3PrimitiveType.longType;
    }

     public F3PrimitiveType getFloatType() {
        return F3PrimitiveType.floatType;
    }

    public F3PrimitiveType getDoubleType() {
        return F3PrimitiveType.doubleType;
    }

   /** Get the run-time representation of the F3 {@code Number} type. */
    public F3PrimitiveType getNumberType() {
        return getFloatType();
    }

    public F3ClassType getStringType() {
        return findClass("java.lang.String");
    }

    /** Get the run-time representation of the F3 {@code Void} type. */
    public F3PrimitiveType getVoidType() {
        return F3PrimitiveType.voidType;
    }

    /** Create a helper object for building a sequence value. */
    public F3SequenceBuilder makeSequenceBuilder(F3Type elementType) {
        return new F3SequenceBuilder(this, elementType);
    }

    /** Create a sequence value from one or more F3Values.
     * Concatenates all of the input values (which might be themselves
     * sequences or null).
     * @param elementType
     * @param values the values to be concatenated
     * @return the new sequence value
     */
    public F3Value makeSequence(F3Type elementType, F3Value... values) {
        F3SequenceBuilder builder = makeSequenceBuilder(elementType);
            for (int i = 0; i < values.length; i++)
                builder.append(values[i]);
        return builder.getSequence();
    }

    /** Create a sequence value from an array of singleton F3Values.
     * This is a low-level routine than {@link #makeSequence},
     * which takes arguments than can be nul or themselves sequences.
     * @param values Input values.  (This array is re-used, not copied.
     *    It must not be modified after the method is called.)
     *    All of the values must be singleton values.
     * @param nvalues Number of items in the sequence.
     *    (We require that {@code nvalues <= values.length}.)
     * @param elementType
     * @return the new sequence value
     */
    public F3Value makeSequenceValue(F3Value[] values, int nvalues, F3Type elementType) {
        return new F3SequenceValue(values, nvalues, elementType);
    }
    
    /* Create an {@code Boolean} value from a {@code boolean}. */
    public F3Local.Value mirrorOf (boolean value) {
        return new F3BooleanValue(value, getBooleanType());
    }

    /* Create an {@code Integer} value from an {@code char}. */
    public F3Local.Value mirrorOf (char value)  {
        return new F3IntegerValue(value, getCharacterType());
    }

    /* Create an {@code Integer} value from an {@code int}. */
    public F3Local.Value mirrorOf (byte value)  {
        return new F3IntegerValue(value, getByteType());
    }

    public F3Local.Value mirrorOf (short value)  {
        return new F3IntegerValue(value, getShortType());
    }

    /* Create an {@code Integer} value from an {@code int}. */
    public F3Local.Value mirrorOf (int value)  {
        return new F3IntegerValue(value, getIntegerType());
    }

    public F3Local.Value mirrorOf (long value)  {
        return new F3LongValue(value, getLongType());
    }

    /* Create an {@code Float} value from a {@code float}. */
    public F3Local.Value mirrorOf (float value) {
        return new F3FloatValue(value, getFloatType());
    }

    /* Create an {@code Double} value from a {@code double}. */
    public F3Local.Value mirrorOf (double value) {
        return new F3DoubleValue(value, getDoubleType());
    }

    public abstract F3Value mirrorOf (String value);
}
