/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package org.f3.runtime.sequence;

import org.f3.runtime.F3TestCase;
import org.f3.runtime.TypeInfo;

/**
 * ComprehensionsTest
 *
 * @author Brian Goetz
 */
public class ComprehensionsTest extends F3TestCase {
    /** select x from foo where x > 3
     *  select x from foo where indexof x % 2 != 0
     */
    public void testSimpleSelect() {
        Sequence<Integer> five = Sequences.range(1, 5);
        Sequence<Integer> six = Sequences.range(1, 6);
        SequencePredicate<Integer> greaterThanThree = new SequencePredicate<Integer>() {
            public boolean matches(Sequence<? extends Integer> sequence, int index, Integer value) {
                return value > 3;
            }
        };
        SequencePredicate<Integer> oddIndex = new SequencePredicate<Integer>() {
            public boolean matches(Sequence<? extends Integer> sequence, int index, Integer value) {
                return index % 2 != 0;
            }
        };
        assertEquals(five.get(greaterThanThree), 4, 5);
        assertEquals(six.get(greaterThanThree), 4, 5, 6);
        assertEquals(five.get(oddIndex), 2, 4);
        assertEquals(six.get(oddIndex), 2, 4, 6);
    }

//    /** foreach (i in outer) foreach (j in inner) { content } */
//    public void test2Dforeach() {
//        Sequence<Integer> outer = Sequences.range(1, 2);
//        Sequence<Integer> inner = Sequences.range(1, 3);
//        ArraySequence<Integer> sb = new ArraySequence<Integer>(outer.size() * inner.size(), TypeInfo.Integer);
//        for (Integer i : outer) {
//            for (Integer j : inner) {
//                sb.add(1);
//            }
//        }
//        Sequence<Integer> result = sb;
//        assertEquals(result, 1, 1, 1, 1, 1, 1);
//        Sequence<Integer> c2dResult = new CartesianProduct2D<Integer, Integer, Integer>(TypeInfo.Integer, outer, inner,
//                new CartesianProduct2D.Mapper<Integer, Integer, Integer>() {
//                    public Integer map(int index1, Integer value1, int index2, Integer value2) {
//                        return 1;
//                    }
//                });
//        assertEquals(result, c2dResult);
//
//        Sequence<Integer> cnResult = new CartesianProduct<Integer>(TypeInfo.Integer,
//                new CartesianProduct.Mapper<Integer>() {
//                    public Integer map(int[] indexes, Object[] values) {
//                        return 1;
//                    }
//                }, outer, inner);
//        assertEquals(result, cnResult);
//
//        // outer = [ 1..2 ], inner = [ 1..3 ], content = [ i*j ]
//        outer = Sequences.range(1, 2);
//        inner = Sequences.range(1, 3);
//        sb = new ArraySequence<Integer>(outer.size() * inner.size(), TypeInfo.Integer);
//        for (Integer i : outer) {
//            for (Integer j : inner) {
//                sb.add(i*j);
//            }
//        }
//        result = sb;
//        assertEquals(result, 1, 2, 3, 2, 4, 6);
//        c2dResult = new CartesianProduct2D<Integer, Integer, Integer>(TypeInfo.Integer, outer, inner,
//                new CartesianProduct2D.Mapper<Integer, Integer, Integer>() {
//                    public Integer map(int index1, Integer value1, int index2, Integer value2) {
//                        return value1 * value2;
//                    }
//                });
//        assertEquals(result, c2dResult);
//
//        cnResult = new CartesianProduct<Integer>(TypeInfo.Integer,
//                new CartesianProduct.Mapper<Integer>() {
//                    public Integer map(int[] indexes, Object[] values) {
//                        return ((Integer) values[0]) * ((Integer) values[1]);
//                    }
//                }, outer, inner);
//        assertEquals(result, cnResult);
//    }
//
    /** foreach (i in [1..2], j in [1..3], k in [1..4]) { i*j*k } */
//    public void test3Dforeach() {
//        Sequence<Integer> first = Sequences.range(1, 2);
//        Sequence<Integer> second = Sequences.range(1, 3);
//        Sequence<Integer> third = Sequences.range(1, 4);
//        ArraySequence<Integer> sb = new ArraySequence<Integer>(TypeInfo.Integer);
//        for (Integer i : first) {
//            for (Integer j : second) {
//                for (Integer k : third) {
//                    sb.add(i*j*k);
//                }
//            }
//        }
//        Sequence<Integer> result = sb;
//        assertEquals(result, 1, 2, 3, 4, 2, 4, 6, 8, 3, 6, 9, 12, 2, 4, 6, 8, 4, 8, 12, 16, 6, 12, 18, 24);
//
//        Sequence<Integer> cnResult = new CartesianProduct<Integer>(TypeInfo.Integer,
//                new CartesianProduct.Mapper<Integer>() {
//                    public Integer map(int[] indexes, Object[] values) {
//                        return ((Integer) values[0]) * ((Integer) values[1]) * ((Integer) values[2]);
//                    }
//                }, first, second, third);
//        assertEquals(result, cnResult);
//    }
}
