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

import java.util.Iterator;

import org.f3.runtime.F3TestCase;
import org.f3.runtime.TypeInfo;
import org.junit.Assert;

/**
 *
 * @author Michael Heinrichs
 */
public class ElementReplacementSequenceTest extends F3TestCase {
    public void testDummy() { }
    /* ??? HOW MUCH OF THIS CAN BE RE_USED?
    
    private Sequence<Integer> INSERT_EMPTY;
    
    private Sequence<Integer> PREPEND_SINGLETON;
    private Sequence<Integer> REPLACES_SINGLETON;
    private Sequence<Integer> APPEND_SINGLETON;

    private Sequence<Integer> PREPEND_THREE_ELEMENT;
    private Sequence<Integer> INSERT_THREE_ELEMENT;
    private Sequence<Integer> REPLACE_FIRST_IN_THREE_ELEMENT;
    private Sequence<Integer> REPLACE_MIDDLE_IN_THREE_ELEMENT;
    private Sequence<Integer> REPLACE_LAST_IN_THREE_ELEMENT;
    private Sequence<Integer> REPLACES_ALL_IN_THREE_ELEMENT;
    private Sequence<Integer> APPEND_THREE_ELEMENT;
    private static final Integer C = 11;

    @Override
    protected void setUp() {
        Sequence<Integer> empty = TypeInfo.Integer.emptySequence;
        Sequence<Integer> baseSingleton = Sequences.singleton(TypeInfo.Integer, 1);
        Sequence<Integer> baseThree = Sequences.make(TypeInfo.Integer, 1, 2, 3);
        
        INSERT_EMPTY = new ElementReplacementSequence<Integer>(empty, 0, 0, C);

        PREPEND_SINGLETON = new ElementReplacementSequence<Integer>(baseSingleton, 0, 0, C);
        REPLACES_SINGLETON = new ElementReplacementSequence<Integer>(baseSingleton, 0, 1, C);
        APPEND_SINGLETON = new ElementReplacementSequence<Integer>(baseSingleton, 1, 1, C);

        PREPEND_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 0, 0, C);
        INSERT_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 1, 1, C);
        REPLACE_FIRST_IN_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 0, 1, C);
        REPLACE_MIDDLE_IN_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 1, 2, C);
        REPLACE_LAST_IN_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 2, 3, C);
        REPLACES_ALL_IN_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 0, 3, C);
        APPEND_THREE_ELEMENT = new ElementReplacementSequence<Integer>(baseThree, 3, 3, C);
    }
    
    private void testGetHelper (Sequence<Integer> seq, Integer... values) {
        assertEquals(values.length, seq.size());
        int i = 0;
        for (Integer val : seq) {
            assertEquals(values[i++], val);
        }
        assertEquals(Integer.valueOf(0), seq.get(-1));
        assertEquals(Integer.valueOf(0), seq.get(values.length));
    }
    
    public void testGet() {
        testGetHelper(INSERT_EMPTY, C);

        testGetHelper(PREPEND_SINGLETON, C, 1);
        testGetHelper(REPLACES_SINGLETON, C);
        testGetHelper(APPEND_SINGLETON, 1, C);

        testGetHelper(PREPEND_THREE_ELEMENT, C, 1, 2, 3);
        testGetHelper(INSERT_THREE_ELEMENT, 1, C, 2, 3);
        testGetHelper(REPLACE_FIRST_IN_THREE_ELEMENT, C, 2, 3);
        testGetHelper(REPLACE_MIDDLE_IN_THREE_ELEMENT, 1, C, 3);
        testGetHelper(REPLACE_LAST_IN_THREE_ELEMENT, 1, 2, C);
        testGetHelper(REPLACES_ALL_IN_THREE_ELEMENT, C);
        testGetHelper(APPEND_THREE_ELEMENT, 1, 2, 3, C);
    }
    
    public void testToArray() {
        Object[] actuals = new Object[1];
        INSERT_EMPTY.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C}, actuals);
        assertEquals(INSERT_EMPTY, C);

        actuals = new Object[2];
        PREPEND_SINGLETON.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C, 1}, actuals);
        assertEquals(PREPEND_SINGLETON, C, 1);
        actuals = new Object[1];
        REPLACES_SINGLETON.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C}, actuals);
        assertEquals(REPLACES_SINGLETON, C);
        actuals = new Object[2];
        APPEND_SINGLETON.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C}, actuals);
        assertEquals(APPEND_SINGLETON, 1, C);
        
        actuals = new Object[4];
        PREPEND_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C, 1, 2, 3}, actuals);
        assertEquals(PREPEND_THREE_ELEMENT, C, 1, 2, 3);
        INSERT_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C, 2, 3}, actuals);
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);
        actuals = new Object[3];
        REPLACE_FIRST_IN_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C, 2, 3}, actuals);
        assertEquals(REPLACE_FIRST_IN_THREE_ELEMENT, C, 2, 3);
        REPLACE_MIDDLE_IN_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C, 3}, actuals);
        assertEquals(REPLACE_MIDDLE_IN_THREE_ELEMENT, 1, C, 3);
        REPLACE_LAST_IN_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, C}, actuals);
        assertEquals(REPLACE_LAST_IN_THREE_ELEMENT, 1, 2, C);
        actuals = new Object[1];
        REPLACES_ALL_IN_THREE_ELEMENT.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {C}, actuals);
        assertEquals(REPLACES_ALL_IN_THREE_ELEMENT, C);
        actuals = new Object[4];
        APPEND_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3, C}, actuals);
        assertEquals(APPEND_THREE_ELEMENT, 1, 2, 3, C);

        // source-offset
        actuals = new Object[2];
        INSERT_THREE_ELEMENT.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, C}, actuals);
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);
        INSERT_THREE_ELEMENT.toArray(2, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2, 3}, actuals);
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);
        
        actuals = new Object[3];
        try {
            INSERT_THREE_ELEMENT.toArray(-1, 3, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);

        try {
            INSERT_THREE_ELEMENT.toArray(2, 3, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);

        actuals = new Object[0];
        INSERT_THREE_ELEMENT.toArray(2, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);
        
        // dest-offset
        actuals = new Object[] {2, 0, 0};
        INSERT_THREE_ELEMENT.toArray(0, 2, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, C}, actuals);
        assertEquals(INSERT_THREE_ELEMENT, 1, C, 2, 3);
    }
  */    
}
