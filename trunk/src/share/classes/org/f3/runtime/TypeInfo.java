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

package org.f3.runtime;

import java.util.HashMap;
import java.util.Map;

import org.f3.runtime.sequence.*;

/**
 * TypeInfo
 *
 * @author Brian Goetz
 */
public class TypeInfo<T> {
    public final T defaultValue;
    public final Types type;
    public final ArraySequence<T> emptySequence;

    public enum Types { INT, FLOAT, OBJECT, DOUBLE, BOOLEAN, LONG, SHORT, BYTE, CHAR, OTHER }

    private static final Object[] emptyArray = new Object[0];

    protected TypeInfo(T defaultValue, Types type) {
        this.defaultValue = defaultValue;
        this.type = type;
        ArraySequence<?> empty;
        switch (type) {
            case BOOLEAN: empty = new BooleanArraySequence(0, (TypeInfo<Boolean>) this); break;
            case CHAR:empty = new CharArraySequence(0, (TypeInfo<Character>) this); break;
            case BYTE: empty = new ByteArraySequence(0, (TypeInfo<Byte>) this); break;
            case SHORT: empty = new ShortArraySequence(0, (TypeInfo<Short>) this); break;
            case INT: empty = new IntArraySequence(0, (TypeInfo<Integer>) this); break;
            case LONG: empty = new LongArraySequence(0, (TypeInfo<Long>) this); break;
            case FLOAT: empty = new FloatArraySequence(0, (TypeInfo<Float>) this); break;
            case DOUBLE: empty = new DoubleArraySequence(0, (TypeInfo<Double>) this); break;
            default:
                empty = new ObjectArraySequence<T>(this, (T[]) emptyArray, true);
        }
        empty.setMaxShared();
        this.emptySequence = (ArraySequence<T>) empty;
    }

    public boolean isNumeric() {
        return false;
    }

    public static final TypeInfo<Object> Object = new TypeInfo<Object>(null, Types.OBJECT);
    public static final TypeInfo<Boolean> Boolean = new TypeInfo<Boolean>(false, Types.BOOLEAN);
    public static final TypeInfo<Character> Character = new TypeInfo<Character>('\0', Types.CHAR);
    public static final TypeInfo<String> String = new TypeInfo<String>("", Types.OTHER);
    public static final NumericTypeInfo<Byte> Byte = new NumericTypeInfo<Byte>((byte)0, Types.BYTE);
    public static final NumericTypeInfo<Short> Short = new NumericTypeInfo<Short>((short)0, Types.SHORT);
    public static final NumericTypeInfo<Integer> Integer = new NumericTypeInfo<Integer>(0, Types.INT);
    public static final NumericTypeInfo<Long> Long = new NumericTypeInfo<Long>(0L, Types.LONG);
    public static final NumericTypeInfo<Float> Float = new NumericTypeInfo<Float>(0.0f, Types.FLOAT);
    public static final NumericTypeInfo<Double> Double = new NumericTypeInfo<Double>(0.0, Types.DOUBLE);

    private static final Map<Class<?>, TypeInfo<?>> map = new HashMap<Class<?>, TypeInfo<?>>();
    static {
        // map.put(Number.class, Number);
        map.put(Byte.class, Byte);
        map.put(Short.class, Short);
        map.put(Integer.class, Integer);
        map.put(Long.class, Long);
        map.put(Float.class, Float);
        map.put(Double.class, Double);
        map.put(Boolean.class, Boolean);
        map.put(Character.class, Character);
        map.put(String.class, String);
    }

    @SuppressWarnings("unchecked")
    public static<T> TypeInfo<T> getTypeInfo() {
        return (TypeInfo)Object;
    }

    @SuppressWarnings("unchecked")
    public static<T> TypeInfo<T> getTypeInfo(Class<T> clazz) {
        TypeInfo<T> ti = (TypeInfo<T>) map.get(clazz);
        if (ti == null)
            ti = TypeInfo.getTypeInfo();
        return ti;
    }

    public static<T> TypeInfo<T> makeTypeInfo(T defaultValue) {
        return new TypeInfo<T>(defaultValue, Types.OTHER);
    }

    public static<T> TypeInfo<T> makeAndRegisterTypeInfo(Class clazz, T defaultValue) {
        TypeInfo<T> ti = new TypeInfo<T>(defaultValue, Types.OTHER);
        map.put(clazz, ti);
        return ti;
    }

    public static<T> TypeInfo<T> makeAndRegisterTypeInfo(T defaultValue) {
        return makeAndRegisterTypeInfo(defaultValue.getClass(), defaultValue);
    }
}
