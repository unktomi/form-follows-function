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

import org.f3.runtime.sequence.Sequence;

/**
 * Utility class for various static utility methods, such as methods that launder generic type errors that are
 * known to be safe.
 *
 * @author Brian Goetz
 */
public class Util {
    @SuppressWarnings("unchecked")
    public static<T> T[] newObjectArray(int size) {
        return (T[]) new Object[size];
    }

    @SuppressWarnings("unchecked")
    public static<T extends Comparable> T[] newComparableArray(int size) {
        return (T[]) new Comparable[size];
    }

    @SuppressWarnings("unchecked")
    public static<T extends Number> T[] newNumberArray(int size) {
        return (T[]) new Number[size];
    }

    @SuppressWarnings("unchecked")
    public static<T> Sequence<T>[] newSequenceArray(int size) {
        return (Sequence<T>[]) new Sequence[size];
    }

    public static int powerOfTwo(int current, int desired) {
        int capacity = current == 0 ? 1 : current;
        while (capacity < desired)
            capacity <<= 1;
        return capacity;
    }

    /** 
     * The following are used to in f3 casts of object to a primitive type,
     * eg:    function(pp) { pp as Float}
     * If the Object passed in is not a Number or Character, then a ClassCastException will
     * occur, which is ok because it isn't legal in f3 to cast a non numeric
     * to a numeric.
     */
    static public char objectToChar(Object p1) {
        if (p1 == null) {
            return 0;
        }
        if (p1 instanceof Character) {
            return ((Character)p1).charValue();
        }
        return (char)((Number)p1).intValue();
    }

    static public byte objectToByte(Object p1) {
        if (p1 == null) {
            return 0;
        }
        if (p1 instanceof Character) {
            return (byte)((Character)p1).charValue();
        }
        return ((Number)p1).byteValue();
    }

    static public short objectToShort(Object p1) {
        if (p1 == null) {
            return 0;
        }
        if (p1 instanceof Character) {
            return (short)((Character)p1).charValue();
        }
        return ((Number)p1).shortValue();
    }

    static public int objectToInt(Object p1) {
        if (p1 == null) {
            return 0;
        }
        if (p1 instanceof Character) {
            return (int)((Character)p1).charValue();
        }
        return ((Number)p1).intValue();
    }

    static public long objectToLong(Object p1) {
        if (p1 == null) {
            return 0l;
        }
        if (p1 instanceof Character) {
            return (long)((Character)p1).charValue();
        }
        return ((Number)p1).longValue();
    }

    static public float objectToFloat(Object p1) {
        if (p1 == null) {
            return 0.0f;
        }
        if (p1 instanceof Character) {
            return (float)((Character)p1).charValue();
        }
        return ((Number)p1).floatValue();
    }

    static public double objectToDouble(Object p1) {
        if (p1 == null) {
            return 0.0d;
        }
        if (p1 instanceof Character) {
            return (double)((Character)p1).charValue();
        }
        return ((Number)p1).doubleValue();
    }

    static public boolean objectToBoolean(Object p1) {
        if (p1 == null) {
            return false;
        }
        return (boolean)(Boolean)p1;
    }
}
