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

package f3.util;

/**
 *  Bit operations
 * @profile common
 */

public class Bits {

    public static int shiftLeft(int value, int shift) {
        return value << shift;
    }

    public static int shiftRight(int value, int shift) {
        return value >> shift;
    }

    public static int unsignedShiftRight(int value, int shift) {
        return value >>> shift;
    }

    public static int bitOr(int value, int mask) {
        return value | mask;
    }

    public static int bitAnd(int value, int mask) {
        return value & mask;
    }
    
    public static int bitXor(int value, int mask) {
        return value ^ mask;
    }

    public static int complement(int value) {
        return ~value;
    }

    public static int add(int value, int mask) {
        return value | mask;
    }

    public static int remove(int value, int mask) {
        return value & ~mask;
    }

    public static boolean contains(int value, int mask) {
        return (value & mask) != 0;
    }


    public static long shiftLeft(long value, int shift) {
        return value << shift;
    }

    public static long shiftRight(long value, int shift) {
        return value >> shift;
    }

    public static long unsignedShiftRight(long value, int shift) {
        return value >>> shift;
    }

    public static long bitOr(long value, long mask) {
        return value | mask;
    }

    public static long bitAnd(long value, long mask) {
        return value & mask;
    }
    
    public static long bitXor(long value, long mask) {
        return value ^ mask;
    }

    public static long complement(long value) {
        return ~value ;
    }

    public static long add(long value, long mask) {
        return value | mask;
    }

    public static long remove(long value, long mask) {
        return value & ~mask;
    }

    public static boolean contains(long value, long mask) {
        return (value & mask) != 0;
    }

}
