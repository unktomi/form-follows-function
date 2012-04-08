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

package org.f3.runtime.sequence;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.f3.runtime.TypeInfo;
import org.f3.runtime.Util;

/**
 * Relatively inefficient implementation of a mutable sequence with a slice-replace operation.  This is used internally
 * in sequence binding implementations.
 *
 * @author Brian Goetz
 */
public class DumbMutableSequence<T> implements Iterable<T> {
    private T[] array;
    private int size;

    public DumbMutableSequence(T[] initialValues) {
        this(initialValues.length);
        System.arraycopy(initialValues, 0, array, 0, initialValues.length);
        size = initialValues.length;
    }

    public DumbMutableSequence(int initialSize) {
        this.array = Util.<T>newObjectArray(Util.powerOfTwo(1, initialSize));
        size = 0;
    }

    public DumbMutableSequence() {
        this(8);
    }

    public T get(int i) {
        return (i < 0 || i >= size)
                ? null
                : array[i];
    }

    public void set(int i, T value) {
        if (i < 0 && i > size)
            throw new IndexOutOfBoundsException(Integer.toString(i));
        if (i == size && size + 1 < array.length) {
            T[] temp = Util.<T>newObjectArray(Util.powerOfTwo(size, size + 1));
            System.arraycopy(array, 0, temp, 0, size);
            array[size++] = value;
        }
        else
            array[i] = value;
    }

    public void replaceSlice(int startPos, int endPos/*exclusive*/, T[] newElements) {
        int insertedCount = newElements.length;
        int deletedCount = endPos - startPos;
        int netAdded = insertedCount - deletedCount;
        if (netAdded == 0)
            System.arraycopy(newElements, 0, array, startPos, insertedCount);
        else if (size + netAdded < array.length) {
            System.arraycopy(array, endPos, array, endPos + netAdded, size - endPos);
            System.arraycopy(newElements, 0, array, startPos, insertedCount);
            if (netAdded < 0)
                Arrays.fill(array, size + netAdded, size, null);
            size += netAdded;
        }
        else {
            int newSize = size + netAdded;
            T[] temp = Util.<T>newObjectArray(Util.powerOfTwo(size, newSize));
            System.arraycopy(array, 0, temp, 0, startPos);
            System.arraycopy(newElements, 0, temp, startPos, insertedCount);
            System.arraycopy(array, endPos, temp, startPos + insertedCount, size - endPos);
            array = temp;
            size = newSize;
        }
    }

    public Sequence<? extends T> replaceSlice(int startPos, int endPos/*exclusive*/, Sequence<? extends T> newElements) {
        final int length = Sequences.size(newElements);
        T[] temp = Util.<T>newObjectArray(length);
        newElements.toArray(0, length, temp, 0);
        replaceSlice(startPos, endPos, temp);
        return newElements;
    }

    public Sequence<T> get(Class<T> clazz) {
        return Sequences.make(TypeInfo.getTypeInfo(clazz), array, size);
    }

    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public T next() {
                if (hasNext())
                    return array[index++];
                else
                    throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    void testValid() {
        for (int i = 0; i < size; i++)
            if (array[i] == null)
                throw new AssertionError("Null element at " + i);
        for (int i = size; i < array.length; i++)
            if (array[i] != null)
                throw new AssertionError("Non-null element at " + i);
    }
}
