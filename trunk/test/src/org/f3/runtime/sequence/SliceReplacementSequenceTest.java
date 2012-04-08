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
public class SliceReplacementSequenceTest extends F3TestCase {
  public void testDummy() { }
  /* ??? HOW MUCH OF THIS CAN BE RE_USED?

    private Sequence<Integer> EMPTY_REPLACES_EMPTY;
    private Sequence<Integer> SINGLETON_REPLACES_EMPTY;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_EMPTY;
    
    private Sequence<Integer> EMPTY_PREPENDED_SINGLETON;
    private Sequence<Integer> EMPTY_REPLACES_SINGLETON;
    private Sequence<Integer> EMPTY_APPENDED_SINGLETON;
    private Sequence<Integer> SINGLETON_PREPENDED_SINGLETON;
    private Sequence<Integer> SINGLETON_REPLACES_SINGLETON;
    private Sequence<Integer> SINGLETON_APPENDED_SINGLETON;
    private Sequence<Integer> THREE_ELEMENT_PREPENDED_SINGLETON;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_SINGLETON;
    private Sequence<Integer> THREE_ELEMENT_APPENDED_SINGLETON;

    private Sequence<Integer> EMPTY_PREPENDED_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_INSERTED_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_REPLACES_FIRST_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_REPLACES_MIDDLE_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_REPLACES_LAST_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_REPLACES_ALL_THREE_ELEMENT;
    private Sequence<Integer> EMPTY_APPENDED_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_PREPENDED_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_INSERTED_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_REPLACES_FIRST_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_REPLACES_MIDDLE_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_REPLACES_LAST_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_REPLACES_ALL_THREE_ELEMENT;
    private Sequence<Integer> SINGLETON_APPENDED_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_PREPENDED_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_INSERTED_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_FIRST_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_LAST_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_REPLACES_ALL_THREE_ELEMENT;
    private Sequence<Integer> THREE_ELEMENT_APPENDED_THREE_ELEMENT;

    @Override
    protected void setUp() {
        Sequence<Integer> empty = TypeInfo.Integer.emptySequence;
        Sequence<Integer> baseSingleton = Sequences.singleton(TypeInfo.Integer, 1);
        Sequence<Integer> baseThree = Sequences.make(TypeInfo.Integer, 1, 2, 3);
        Sequence<Integer> replacementSingleton = Sequences.singleton(TypeInfo.Integer, 11);
        Sequence<Integer> replacementThree = Sequences.make(TypeInfo.Integer, 21, 22, 23);
        
        EMPTY_REPLACES_EMPTY = new SliceReplacementSequence(empty, 0, 0, empty);
        SINGLETON_REPLACES_EMPTY = new SliceReplacementSequence(empty, 0, 0, replacementSingleton);
        THREE_ELEMENT_REPLACES_EMPTY = new SliceReplacementSequence(empty, 0, 0, replacementThree);

        EMPTY_PREPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 0, empty);
        EMPTY_REPLACES_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 1, empty);
        EMPTY_APPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 1, 1, empty);
        SINGLETON_PREPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 0, replacementSingleton);
        SINGLETON_REPLACES_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 1, replacementSingleton);
        SINGLETON_APPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 1, 1, replacementSingleton);
        THREE_ELEMENT_PREPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 0, replacementThree);
        THREE_ELEMENT_REPLACES_SINGLETON = new SliceReplacementSequence(baseSingleton, 0, 1, replacementThree);
        THREE_ELEMENT_APPENDED_SINGLETON = new SliceReplacementSequence(baseSingleton, 1, 1, replacementThree);

        EMPTY_PREPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 0, empty);
        EMPTY_INSERTED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 1, empty);
        EMPTY_REPLACES_FIRST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 1, empty);
        EMPTY_REPLACES_MIDDLE_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 2, empty);
        EMPTY_REPLACES_LAST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 2, 3, empty);
        EMPTY_REPLACES_ALL_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 3, empty);
        EMPTY_APPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 3, 3, empty);
        SINGLETON_PREPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 0, replacementSingleton);
        SINGLETON_INSERTED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 1, replacementSingleton);
        SINGLETON_REPLACES_FIRST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 1, replacementSingleton);
        SINGLETON_REPLACES_MIDDLE_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 2, replacementSingleton);
        SINGLETON_REPLACES_LAST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 2, 3, replacementSingleton);
        SINGLETON_REPLACES_ALL_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 3, replacementSingleton);
        SINGLETON_APPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 3, 3, replacementSingleton);
        THREE_ELEMENT_PREPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 0, replacementThree);
        THREE_ELEMENT_INSERTED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 1, replacementThree);
        THREE_ELEMENT_REPLACES_FIRST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 1, replacementThree);
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 1, 2, replacementThree);
        THREE_ELEMENT_REPLACES_LAST_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 2, 3, replacementThree);
        THREE_ELEMENT_REPLACES_ALL_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 0, 3, replacementThree);
        THREE_ELEMENT_APPENDED_THREE_ELEMENT = new SliceReplacementSequence(baseThree, 3, 3, replacementThree);
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
        testGetHelper(EMPTY_REPLACES_EMPTY);
        testGetHelper(SINGLETON_REPLACES_EMPTY, 11);
        testGetHelper(THREE_ELEMENT_REPLACES_EMPTY, 21, 22, 23);

        testGetHelper(EMPTY_PREPENDED_SINGLETON, 1);
        testGetHelper(EMPTY_REPLACES_SINGLETON);
        testGetHelper(EMPTY_APPENDED_SINGLETON, 1);
        testGetHelper(SINGLETON_PREPENDED_SINGLETON, 11, 1);
        testGetHelper(SINGLETON_REPLACES_SINGLETON, 11);
        testGetHelper(SINGLETON_APPENDED_SINGLETON, 1, 11);
        testGetHelper(THREE_ELEMENT_PREPENDED_SINGLETON, 21, 22, 23, 1);
        testGetHelper(THREE_ELEMENT_REPLACES_SINGLETON, 21, 22, 23);
        testGetHelper(THREE_ELEMENT_APPENDED_SINGLETON, 1, 21, 22, 23);

        testGetHelper(EMPTY_PREPENDED_THREE_ELEMENT, 1, 2, 3);
        testGetHelper(EMPTY_INSERTED_THREE_ELEMENT, 1, 2, 3);
        testGetHelper(EMPTY_REPLACES_FIRST_THREE_ELEMENT, 2, 3);
        testGetHelper(EMPTY_REPLACES_MIDDLE_THREE_ELEMENT, 1, 3);
        testGetHelper(EMPTY_REPLACES_LAST_THREE_ELEMENT, 1, 2);
        testGetHelper(EMPTY_REPLACES_ALL_THREE_ELEMENT);
        testGetHelper(EMPTY_APPENDED_THREE_ELEMENT, 1, 2, 3);
        testGetHelper(SINGLETON_PREPENDED_THREE_ELEMENT, 11, 1, 2, 3);
        testGetHelper(SINGLETON_INSERTED_THREE_ELEMENT, 1, 11, 2, 3);
        testGetHelper(SINGLETON_REPLACES_FIRST_THREE_ELEMENT, 11, 2, 3);
        testGetHelper(SINGLETON_REPLACES_MIDDLE_THREE_ELEMENT, 1, 11, 3);
        testGetHelper(SINGLETON_REPLACES_LAST_THREE_ELEMENT, 1, 2, 11);
        testGetHelper(SINGLETON_REPLACES_ALL_THREE_ELEMENT, 11);
        testGetHelper(SINGLETON_APPENDED_THREE_ELEMENT, 1, 2, 3, 11);
        testGetHelper(THREE_ELEMENT_PREPENDED_THREE_ELEMENT, 21, 22, 23, 1, 2, 3);
        testGetHelper(THREE_ELEMENT_INSERTED_THREE_ELEMENT, 1, 21, 22, 23, 2, 3);
        testGetHelper(THREE_ELEMENT_REPLACES_FIRST_THREE_ELEMENT, 21, 22, 23, 2, 3);
        testGetHelper(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
        testGetHelper(THREE_ELEMENT_REPLACES_LAST_THREE_ELEMENT, 1, 2, 21, 22, 23);
        testGetHelper(THREE_ELEMENT_REPLACES_ALL_THREE_ELEMENT, 21, 22, 23);
        testGetHelper(THREE_ELEMENT_APPENDED_THREE_ELEMENT, 1, 2, 3, 21, 22, 23);
    }
    
    public void testToArray() {
        Object[] actuals = new Object[0];
        EMPTY_REPLACES_EMPTY.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY_REPLACES_EMPTY);
        actuals = new Object[1];
        SINGLETON_REPLACES_EMPTY.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11}, actuals);
        assertEquals(SINGLETON_REPLACES_EMPTY, 11);
        actuals = new Object[3];
        THREE_ELEMENT_REPLACES_EMPTY.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_EMPTY, 21, 22, 23);

        actuals = new Object[1];
        EMPTY_PREPENDED_SINGLETON.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(EMPTY_PREPENDED_SINGLETON, 1);
        actuals = new Object[0];
        EMPTY_REPLACES_SINGLETON.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY_REPLACES_SINGLETON);
        actuals = new Object[1];
        EMPTY_APPENDED_SINGLETON.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(EMPTY_APPENDED_SINGLETON, 1);
        actuals = new Object[2];
        SINGLETON_PREPENDED_SINGLETON.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11, 1}, actuals);
        assertEquals(SINGLETON_PREPENDED_SINGLETON, 11, 1);
        actuals = new Object[1];
        SINGLETON_REPLACES_SINGLETON.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11}, actuals);
        assertEquals(SINGLETON_REPLACES_SINGLETON, 11);
        actuals = new Object[2];
        SINGLETON_APPENDED_SINGLETON.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 11}, actuals);
        assertEquals(SINGLETON_APPENDED_SINGLETON, 1, 11);
        actuals = new Object[4];
        THREE_ELEMENT_PREPENDED_SINGLETON.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23, 1}, actuals);
        assertEquals(THREE_ELEMENT_PREPENDED_SINGLETON, 21, 22, 23, 1);
        actuals = new Object[3];
        THREE_ELEMENT_REPLACES_SINGLETON.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_SINGLETON, 21, 22, 23);
        actuals = new Object[4];
        THREE_ELEMENT_APPENDED_SINGLETON.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_APPENDED_SINGLETON, 1, 21, 22, 23);
        
        actuals = new Object[3];
        EMPTY_PREPENDED_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3}, actuals);
        assertEquals(EMPTY_PREPENDED_THREE_ELEMENT, 1, 2, 3);
        EMPTY_INSERTED_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3}, actuals);
        assertEquals(EMPTY_INSERTED_THREE_ELEMENT, 1, 2, 3);
        actuals = new Object[2];
        EMPTY_REPLACES_FIRST_THREE_ELEMENT.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2, 3}, actuals);
        assertEquals(EMPTY_REPLACES_FIRST_THREE_ELEMENT, 2, 3);
        EMPTY_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 3}, actuals);
        assertEquals(EMPTY_REPLACES_MIDDLE_THREE_ELEMENT, 1, 3);
        EMPTY_REPLACES_LAST_THREE_ELEMENT.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2}, actuals);
        assertEquals(EMPTY_REPLACES_LAST_THREE_ELEMENT, 1, 2);
        actuals = new Object[0];
        EMPTY_REPLACES_ALL_THREE_ELEMENT.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY_REPLACES_ALL_THREE_ELEMENT);
        actuals = new Object[3];
        EMPTY_APPENDED_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3}, actuals);
        assertEquals(EMPTY_APPENDED_THREE_ELEMENT, 1, 2, 3);
        actuals = new Object[4];
        SINGLETON_PREPENDED_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11, 1, 2, 3}, actuals);
        assertEquals(SINGLETON_PREPENDED_THREE_ELEMENT, 11, 1, 2, 3);
        SINGLETON_INSERTED_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 11, 2, 3}, actuals);
        assertEquals(SINGLETON_INSERTED_THREE_ELEMENT, 1, 11, 2, 3);
        actuals = new Object[3];
        SINGLETON_REPLACES_FIRST_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11, 2, 3}, actuals);
        assertEquals(SINGLETON_REPLACES_FIRST_THREE_ELEMENT, 11, 2, 3);
        SINGLETON_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 11, 3}, actuals);
        assertEquals(SINGLETON_REPLACES_MIDDLE_THREE_ELEMENT, 1, 11, 3);
        SINGLETON_REPLACES_LAST_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 11}, actuals);
        assertEquals(SINGLETON_REPLACES_LAST_THREE_ELEMENT, 1, 2, 11);
        actuals = new Object[1];
        SINGLETON_REPLACES_ALL_THREE_ELEMENT.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {11}, actuals);
        assertEquals(SINGLETON_REPLACES_ALL_THREE_ELEMENT, 11);
        actuals = new Object[4];
        SINGLETON_APPENDED_THREE_ELEMENT.toArray(0, 4, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3, 11}, actuals);
        assertEquals(SINGLETON_APPENDED_THREE_ELEMENT, 1, 2, 3, 11);
        actuals = new Object[6];
        THREE_ELEMENT_PREPENDED_THREE_ELEMENT.toArray(0, 6, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23, 1, 2, 3}, actuals);
        assertEquals(THREE_ELEMENT_PREPENDED_THREE_ELEMENT, 21, 22, 23, 1, 2, 3);
        THREE_ELEMENT_INSERTED_THREE_ELEMENT.toArray(0, 6, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 21, 22, 23, 2, 3}, actuals);
        assertEquals(THREE_ELEMENT_INSERTED_THREE_ELEMENT, 1, 21, 22, 23, 2, 3);
        actuals = new Object[5];
        THREE_ELEMENT_REPLACES_FIRST_THREE_ELEMENT.toArray(0, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23, 2, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_FIRST_THREE_ELEMENT, 21, 22, 23, 2, 3);
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 21, 22, 23, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
        THREE_ELEMENT_REPLACES_LAST_THREE_ELEMENT.toArray(0, 5, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_LAST_THREE_ELEMENT, 1, 2, 21, 22, 23);
        actuals = new Object[3];
        THREE_ELEMENT_REPLACES_ALL_THREE_ELEMENT.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_ALL_THREE_ELEMENT, 21, 22, 23);
        actuals = new Object[6];
        THREE_ELEMENT_APPENDED_THREE_ELEMENT.toArray(0, 6, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3, 21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_APPENDED_THREE_ELEMENT, 1, 2, 3, 21, 22, 23);

        // source-offset
        actuals = new Object[2];
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 21}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(3, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {23, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
        
        actuals = new Object[3];
        try {
            THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(-1, 3, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        try {
            THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(3, 3, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[0];
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(2, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
        
        // dest-offset
        actuals = new Object[] {2, 0, 0, 0, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 5, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, 21, 22, 23, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 1, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 2, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, 21}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0, 0, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(0, 4, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, 21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(1, 3, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 21, 22, 23}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0, 0, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(1, 4, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 21, 22, 23, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(3, 2, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 23, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);

        actuals = new Object[] {2, 0};
        THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT.toArray(4, 1, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 3}, actuals);
        assertEquals(THREE_ELEMENT_REPLACES_MIDDLE_THREE_ELEMENT, 1, 21, 22, 23, 3);
    }
  */    
}
