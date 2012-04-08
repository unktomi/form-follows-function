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
public class SubSequenceTest extends F3TestCase {
    
    private Sequence<Integer> SUBSEQUENCE_FROM_EMPTY_SEQUENCE;
    private Sequence<Integer> SUBSEQUENCE_FROM_SINGLETON_SEQUENCE;
    private Sequence<Integer> EMPTY_SUBSEQUENCE;
    private Sequence<Integer> SUBSEQUENCE_AT_START;
    private Sequence<Integer> SUBSEQUENCE_IN_MIDDLE;
    private Sequence<Integer> SUBSEQUENCE_AT_END;
    private Sequence<Integer> OVERLAPPING_SUBSEQUENCE;

    @Override
    protected void setUp() {
        Sequence<Integer> baseSequence = Sequences.make(TypeInfo.Integer, 1, 2, 3);

        SUBSEQUENCE_FROM_EMPTY_SEQUENCE = new SubSequence<Integer>(TypeInfo.Integer.emptySequence, 1, 1);
        SUBSEQUENCE_FROM_SINGLETON_SEQUENCE = new SubSequence<Integer>(Sequences.singleton(TypeInfo.Integer, 1), 0, 1);

        EMPTY_SUBSEQUENCE = new SubSequence<Integer>(baseSequence, 1, 1);
        SUBSEQUENCE_AT_START = new SubSequence<Integer>(baseSequence, 0, 2);
        SUBSEQUENCE_AT_END = new SubSequence<Integer>(baseSequence, 1, 3);
        SUBSEQUENCE_IN_MIDDLE = new SubSequence<Integer>(baseSequence, 1, 2);
        OVERLAPPING_SUBSEQUENCE = new SubSequence<Integer>(SUBSEQUENCE_AT_START, 1, 2);
    }
    
    public void testToArray() {
        Object[] actuals = new Object[0];
        
        SUBSEQUENCE_FROM_EMPTY_SEQUENCE.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(SUBSEQUENCE_FROM_EMPTY_SEQUENCE);
        
        EMPTY_SUBSEQUENCE.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY_SUBSEQUENCE);
        
        actuals = new Object[1];
        SUBSEQUENCE_FROM_SINGLETON_SEQUENCE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(SUBSEQUENCE_FROM_SINGLETON_SEQUENCE, 1);

        SUBSEQUENCE_IN_MIDDLE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2}, actuals);
        assertEquals(SUBSEQUENCE_IN_MIDDLE, 2);

        OVERLAPPING_SUBSEQUENCE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2}, actuals);
        assertEquals(OVERLAPPING_SUBSEQUENCE, 2);
        
        actuals = new Object[2];
        SUBSEQUENCE_AT_START.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2}, actuals);
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);

        SUBSEQUENCE_AT_END.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2, 3}, actuals);
        assertEquals(SUBSEQUENCE_AT_END, 2, 3);


        // source-offset
        actuals = new Object[1];
        SUBSEQUENCE_AT_START.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);
        SUBSEQUENCE_AT_START.toArray(1, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2}, actuals);
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);
        
        actuals = new Object[2];
        try {
            SUBSEQUENCE_AT_START.toArray(-1, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);

        try {
            SUBSEQUENCE_AT_START.toArray(1, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);

        actuals = new Object[0];
        SUBSEQUENCE_AT_START.toArray(2, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(SUBSEQUENCE_AT_START, 1, 2);
        
        // dest-offset
        actuals = new Object[] {2, 0};
        SUBSEQUENCE_IN_MIDDLE.toArray(0, 1, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 2}, actuals);
        assertEquals(SUBSEQUENCE_IN_MIDDLE, 2);
    }
    
}
