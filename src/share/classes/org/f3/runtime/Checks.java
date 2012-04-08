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
import org.f3.runtime.sequence.Sequences;

/**
 * Various runtime checks too messy to do inline
 * @author Robert Field
 */
public class Checks {
    
    /**
     * Do an equals() compare that is safe 
     * @param a
     * @param b
     * @return true if they are equal
     */
    public static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    /**
     * Do an equals() compare on sequences that is safe 
     * @param a
     * @param b
     * @return true if they are equal
     */
    public static boolean equals(Sequence a, Sequence b) {
        return Sequences.isEqual(a, b);
    }

    /**
     * Do an equals() compare on Strings that is safe
     * @param a
     * @param b
     * @return true if they are equal
     */
    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null || b.length() == 0;
        } else if (b == null) {
            return a.length() == 0;
        } else {
            return a.equals(b);
        }
    }

    /**
     * Compare an Object with an int
     * @param a Object
     * @param b int
     * @return true if they are equal
     */
    public static boolean equals(Object obj, int i) {
        if (obj == null) {
            return i == 0;  // compare to default value
        } else if (obj instanceof Integer) {
            return ((Integer)obj).intValue() == i;
        } else if (obj instanceof Float) {
            return ((Float)obj).floatValue() == i;
        } else if (obj instanceof Double) {
            return ((Double)obj).doubleValue() == i;
        } else if (obj instanceof Long) {
            return ((Long)obj).longValue() == i;
        } else if (obj instanceof Byte) {
            return ((Byte)obj).byteValue() == i;
        } else if (obj instanceof Short) {
            return ((Short)obj).shortValue() == i;
        } else if (obj instanceof Character) {
            return ((Character)obj).charValue() == i;
        } else {
            return false;
        }
    }

    /**
     * Compare an Object with an float
     * @param a Object
     * @param b float
     * @return true if they are equal
     */
    public static boolean equals(Object obj, float x) {
        if (obj == null) {
            return x == 0.0f;  // compare to default value
        } else if (obj instanceof Float) {
            return ((Float)obj).floatValue() == x;
        } else if (obj instanceof Integer) {
            return ((Integer)obj).intValue() == x;
        } else if (obj instanceof Double) {
            return ((Double)obj).doubleValue() == x;
        } else if (obj instanceof Long) {
            return ((Long)obj).longValue() == x;
        } else if (obj instanceof Byte) {
            return ((Byte)obj).byteValue() == x;
        } else if (obj instanceof Short) {
            return ((Short)obj).shortValue() == x;
        } else if (obj instanceof Character) {
            return ((Character)obj).charValue() == x;
        } else {
            return false;
        }
    }

    /**
     * Compare an Object with an double
     * @param a Object
     * @param b double
     * @return true if they are equal
     */
    public static boolean equals(Object obj, double x) {
        if (obj == null) {
            return x == 0.0;  // compare to default value
        } else if (obj instanceof Double) {
            return ((Double)obj).doubleValue() == x;
        } else if (obj instanceof Integer) {
            return ((Integer)obj).intValue() == x;
        } else if (obj instanceof Float) {
            return ((Float)obj).floatValue() == x;
        } else if (obj instanceof Long) {
            return ((Long)obj).longValue() == x;
        } else if (obj instanceof Byte) {
            return ((Byte)obj).byteValue() == x;
        } else if (obj instanceof Short) {
            return ((Short)obj).shortValue() == x;
        } else if (obj instanceof Character) {
            return ((Character)obj).charValue() == x;
        } else {
            return false;
        }
    }

    /**
     * Compare an Object with an long
     * @param a Object
     * @param b long
     * @return true if they are equal
     */
    public static boolean equals(Object obj, long i) {
        if (obj == null) {
            return i == 0L;  // compare to default value
        } else if (obj instanceof Long) {
            return ((Long)obj).longValue() == i;
        } else if (obj instanceof Integer) {
            return ((Integer)obj).intValue() == i;
        } else if (obj instanceof Float) {
            return ((Float)obj).floatValue() == i;
        } else if (obj instanceof Double) {
            return ((Double)obj).doubleValue() == i;
        } else if (obj instanceof Byte) {
            return ((Byte)obj).byteValue() == i;
        } else if (obj instanceof Short) {
            return ((Short)obj).shortValue() == i;
        } else if (obj instanceof Character) {
            return ((Character)obj).charValue() == i;
        } else {
            return false;
        }
    }

    /**
     * Compare an Object with an int
     * @param a Object
     * @param b int
     * @return true if they are equal
     */
    public static boolean equals(Object obj, boolean bool) {
        if (obj == null) {
            return bool == false;  // compare to default value
        } else if (obj instanceof Boolean) {
            return ((Boolean)obj).booleanValue() == bool;
        } else {
            return false;
        }
    }

    /**
     * Check if the sequence parameter is null, or equivalent to null
     * @param a Sequence
     * @return true if a is null or null equivalent
     */
    public static boolean isNull(Sequence a) {
        return (a == null) ||  a.size() == 0;
    }

    /**
     * Check if the String parameter is null, or equivalent to null
     * @param a String
     * @return true if a is null or null equivalent
     */
    public static boolean isNull(String a) {
        return (a == null) ||  a.length() == 0;
    }
}
