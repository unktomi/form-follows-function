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
public class ReplacementSequenceTest extends F3TestCase { 
    public void testDummy() { }
    /* ??? HOW MUCH OF THIS CAN BE RE_USED?
   
    private Sequence<Integer> REPLACEMENT_FROM_SINGLETON_SEQUENCE;
    private Sequence<Integer> REPLACEMENT_AT_START;
    private Sequence<Integer> REPLACEMENT_IN_MIDDLE;
    private Sequence<Integer> REPLACEMENT_AT_END;
    private Sequence<Integer> OVERLAPPING_REPLACEMENT;
    private final Integer C = 7;
    private final Integer D = 8;

    @Override
    protected void setUp() {
        REPLACEMENT_FROM_SINGLETON_SEQUENCE = new ReplacementSequence<Integer>(Sequences.singleton(TypeInfo.Integer, 1), 0, 2);
        Sequence<Integer> baseSequence = Sequences.make(TypeInfo.Integer, 1, 2, 3);
        REPLACEMENT_AT_START = new ReplacementSequence<Integer>(baseSequence, 0, C);
        REPLACEMENT_IN_MIDDLE = new ReplacementSequence<Integer>(baseSequence, 1, C);
        REPLACEMENT_AT_END = new ReplacementSequence<Integer>(baseSequence, 2, C);
        OVERLAPPING_REPLACEMENT = new ReplacementSequence<Integer>(REPLACEMENT_IN_MIDDLE, 1, D);
    }
    
    public void testToArray() {
        Object[] actuals = new Object[1];
        REPLACEMENT_FROM_SINGLETON_SEQUENCE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2}, actuals);
        
        actuals = new Object[3];
        REPLACEMENT_AT_START.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C, 2, 3}, actuals);
        assertEquals(REPLACEMENT_AT_START, C, 2, 3);

        REPLACEMENT_IN_MIDDLE.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C, 3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        REPLACEMENT_AT_END.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, C}, actuals);
        assertEquals(REPLACEMENT_AT_END, 1, 2, C);

        OVERLAPPING_REPLACEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, D, 3}, actuals);
        assertEquals(OVERLAPPING_REPLACEMENT, 1, D, 3);

        // source-offset
        actuals = new Object[2];
        REPLACEMENT_IN_MIDDLE.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
        REPLACEMENT_IN_MIDDLE.toArray(1, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C, 3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
        
        // special cases where replacement is not part of the array
        actuals = new Object[1];
        REPLACEMENT_IN_MIDDLE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
        REPLACEMENT_IN_MIDDLE.toArray(2, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
                
        actuals = new Object[2];
        try {
            REPLACEMENT_IN_MIDDLE.toArray(-1, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        try {
            REPLACEMENT_IN_MIDDLE.toArray(2, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        actuals = new Object[0];
        REPLACEMENT_IN_MIDDLE.toArray(3, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
        
        
        // dest-offset
        actuals = new Object[4];
        actuals[0] = D;
        REPLACEMENT_IN_MIDDLE.toArray(0, 3, actuals, 1);
        Assert.assertArrayEquals(new Object[] {D, 1, C, 3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        actuals = new Object[] {D, 0};
        REPLACEMENT_IN_MIDDLE.toArray(0, 1, actuals, 1);
        Assert.assertArrayEquals(new Object[] {D, 1}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        REPLACEMENT_IN_MIDDLE.toArray(2, 1, actuals, 1);
        Assert.assertArrayEquals(new Object[] {D, 3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        actuals = new Object[] {D, 0, 0};
        REPLACEMENT_IN_MIDDLE.toArray(0, 2, actuals, 1);
        Assert.assertArrayEquals(new Object[] {D, 1, C}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);

        REPLACEMENT_IN_MIDDLE.toArray(1, 2, actuals, 1);
        Assert.assertArrayEquals(new Object[] {D, C, 3}, actuals);
        assertEquals(REPLACEMENT_IN_MIDDLE, 1, C, 3);
    }
  */    
}
