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
public class ArraySequenceTest extends F3TestCase {
    private Sequence<Integer> EMPTY = new ObjectArraySequence(TypeInfo.Integer, new Integer[0], true);
    private Sequence<Integer> SINGLETON = new ObjectArraySequence(TypeInfo.Integer, new Integer[] {1}, true);
    private Sequence<Integer> THREE_ELEMENTS = new ObjectArraySequence(TypeInfo.Integer, new Integer[] {1, 2, 3}, true);

    public void testToArray() {
        Object[] actuals = new Object[0];
        
        EMPTY.toArray(0, 0, actuals, 0);
        Assert.assertArrayEquals(new Object[0], actuals);
        assertEquals(EMPTY);
        
        actuals = new Object[1];
        SINGLETON.toArray(0, 1, actuals, 0);
        Assert.assertArrayEquals(new Object[] {1}, actuals);
        assertEquals(SINGLETON, 1);

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
        try {
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
