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
import java.util.NoSuchElementException;
import org.f3.runtime.F3TestCase;
import org.f3.runtime.TypeInfo;
import org.junit.Assert;

/**
 *
 * @author Michael Heinrichs
 */
public class AbstractSequenceTest extends F3TestCase {
    
    public static class MyAbstractSequence<T> extends AbstractSequence<T> {

        private final T[] array;
    
        public MyAbstractSequence(TypeInfo<T> ti, T... values) {
            super(ti);
            this.array = values;
        }

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public T get(int position) {
            if (position < 0 || position >= array.length)
                return getDefaultValue();
            else 
                return array[position];
        }
    }

    private Sequence<Integer> EMPTY_SEQUENCE = new MyAbstractSequence<Integer>(TypeInfo.Integer);
    private Sequence<Integer> SINGLETON_SEQUENCE = new MyAbstractSequence<Integer>(TypeInfo.Integer, 1);
    private Sequence<Integer> THREE_ELEMENTS = new MyAbstractSequence<Integer>(TypeInfo.Integer, 1, 2, 3);
    
    private void testIteratorHelper (Iterator<Integer> it, Integer... values) {
        for (Integer x : values) {
            assertEquals(true, it.hasNext());
            assertEquals(x, it.next());
        }
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex);
        }
    }
    
    public void testIterator() {
        testIteratorHelper (EMPTY_SEQUENCE.iterator());
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(), 1);
        testIteratorHelper (THREE_ELEMENTS.iterator(), 1, 2, 3);
        
        testIteratorHelper (EMPTY_SEQUENCE.iterator(-1, 0));
        testIteratorHelper (EMPTY_SEQUENCE.iterator(0, 0));
        testIteratorHelper (EMPTY_SEQUENCE.iterator(0, 1));

        testIteratorHelper (SINGLETON_SEQUENCE.iterator(-1, -1));
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(-1, 0), 1);
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(-1, 1), 1);
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(0, -1));
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(0, 0), 1);
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(0, 1), 1);
        testIteratorHelper (SINGLETON_SEQUENCE.iterator(1, 1));

        testIteratorHelper (THREE_ELEMENTS.iterator(-1, -1));
        testIteratorHelper (THREE_ELEMENTS.iterator(-1, 0), 1);
        testIteratorHelper (THREE_ELEMENTS.iterator(-1, 1), 1, 2);
        testIteratorHelper (THREE_ELEMENTS.iterator(0, -1));
        testIteratorHelper (THREE_ELEMENTS.iterator(0, 0), 1);
        testIteratorHelper (THREE_ELEMENTS.iterator(0, 1), 1, 2);
        testIteratorHelper (THREE_ELEMENTS.iterator(0, 2), 1, 2, 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(0, 3), 1, 2, 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(1, 1), 2);
        testIteratorHelper (THREE_ELEMENTS.iterator(1, 2), 2, 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(1, 3), 2, 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(2, 2), 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(2, 3), 3);
        testIteratorHelper (THREE_ELEMENTS.iterator(3, 3));
    }

    public void testToArray() {
        Object[] actuals = new Object[0];
        
        EMPTY_SEQUENCE.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY_SEQUENCE);
        
        actuals = new Object[1];
        SINGLETON_SEQUENCE.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(SINGLETON_SEQUENCE, 1);

        actuals = new Object[3];
        THREE_ELEMENTS.toArray(0, 3, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2, 3}, actuals);
        assertEquals(THREE_ELEMENTS, 1, 2, 3);
        
        // source-offset
        actuals = new Object[2];
        THREE_ELEMENTS.toArray(0, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1, 2}, actuals);
        assertEquals(THREE_ELEMENTS, 1, 2, 3);
        THREE_ELEMENTS.toArray(1, 2, actuals, 0);
        Assert.assertArrayEquals(new Object[] {2, 3}, actuals);
        assertEquals(THREE_ELEMENTS, 1, 2, 3);
        
        actuals = new Object[2];
        /*try {
            THREE_ELEMENTS.toArray(-1, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
            }
        assertEquals(THREE_ELEMENTS, 1, 2, 3);

        try {
            THREE_ELEMENTS.toArray(2, 2, actuals, 0);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        */
        assertEquals(THREE_ELEMENTS, 1, 2, 3);

        actuals = new Object[0];
        THREE_ELEMENTS.toArray(3, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(THREE_ELEMENTS, 1, 2, 3);


        // dest-offset
        actuals = new Object[4];
        actuals[0] = 2;
        THREE_ELEMENTS.toArray(0, 3, actuals, 1);
        Assert.assertArrayEquals(new Object[] {2, 1, 2, 3}, actuals);
        assertEquals(THREE_ELEMENTS, 1, 2, 3);

        actuals = new Object[3];
        try {
            THREE_ELEMENTS.toArray(0, 3, actuals, -1);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
        try {
            THREE_ELEMENTS.toArray(0, 3, actuals, 1);
            fail("Expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
    }
}
