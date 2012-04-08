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
import org.junit.Assert;

/**
 *
 * @author Michael Heinrichs
 */
public class CompositeSequenceTest extends F3TestCase {
    public void testDummy() { }
    /* ??? HOW MUCH OF THIS CAN BE RE_USED?
    
    private Sequence<Integer> TWO_EMPTY_SEQUENCES;
    private Sequence<Integer> TWO_SINGLETON_SEQUENCES;
    private Sequence<Integer> LONG_SEQUENCE;

    @Override
    protected void setUp() {
        Sequence<Integer> empty = TypeInfo.Integer.emptySequence;
        TWO_EMPTY_SEQUENCES = new CompositeSequence(TypeInfo.Integer, empty, empty);
        
        Sequence<Integer> seq1 = Sequences.singleton(TypeInfo.Integer, 1);
        Sequence<Integer> seq2 = Sequences.singleton(TypeInfo.Integer, 2);
        TWO_SINGLETON_SEQUENCES = new CompositeSequence(TypeInfo.Integer, seq1, seq2);
        
        Sequence<Integer> seqA = Sequences.make(TypeInfo.Integer, 1, 2, 3);
        Sequence<Integer> seqB = Sequences.make(TypeInfo.Integer, 4, 5, 6);
        Sequence<Integer> seqC = Sequences.make(TypeInfo.Integer, 7, 8, 9);
        LONG_SEQUENCE = new CompositeSequence(TypeInfo.Integer, seqA, empty, seqB, empty, seqC);
    }
    

    public void testToArray() {
        Object[] actuals = new Object[0];
        
        TWO_EMPTY_SEQUENCES.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(TWO_EMPTY_SEQUENCES);
        
        actuals = new Object[2];
        TWO_SINGLETON_SEQUENCES.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2}, actuals);
        assertEquals(TWO_SINGLETON_SEQUENCES, 1, 2);

        actuals = new Object[9];
        LONG_SEQUENCE.toArray(0, 9, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        
        // source-offset
        actuals = new Object[1];
        LONG_SEQUENCE.toArray(4, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {5}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        actuals = new Object[2];
        LONG_SEQUENCE.toArray(3, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {4, 5}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        LONG_SEQUENCE.toArray(4, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {5, 6}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        actuals = new Object[3];
        LONG_SEQUENCE.toArray(3, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {4, 5, 6}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        actuals = new Object[5];
        LONG_SEQUENCE.toArray(0, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3, 4, 5}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        LONG_SEQUENCE.toArray(1, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2, 3, 4, 5, 6}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        LONG_SEQUENCE.toArray(2, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {3, 4, 5, 6, 7}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        LONG_SEQUENCE.toArray(4, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {5, 6, 7, 8, 9}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        
        actuals = new Object[2];
        try {
            LONG_SEQUENCE.toArray(-1, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        try {
            LONG_SEQUENCE.toArray(8, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        actuals = new Object[0];
        LONG_SEQUENCE.toArray(9, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        // dest-offset
        actuals = new Object[4];
        actuals[0] = 2;
        LONG_SEQUENCE.toArray(0, 3, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, 2, 3}, actuals);
        assertEquals(LONG_SEQUENCE, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        actuals = new Object[3];
        try {
            LONG_SEQUENCE.toArray(0, 3, actuals, -1);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        try {
            LONG_SEQUENCE.toArray(0, 3, actuals, 1);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
    }
  */
}
