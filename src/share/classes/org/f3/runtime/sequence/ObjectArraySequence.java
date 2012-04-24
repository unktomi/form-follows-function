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

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.f3.runtime.TypeInfo;
import org.f3.runtime.Util;

import org.f3.functions.*;

public class ObjectArraySequence<T> extends ArraySequence<T> implements Sequence<T> {

    T[] array;

    public ObjectArraySequence(int initialSize, TypeInfo<T> ti) {
        super(ti);
        this.array =  Util.<T>newObjectArray(initialSize);
        gapStart = 0;
        gapEnd = initialSize;
    }

    public ObjectArraySequence(TypeInfo<T> ti) {
        this(DEFAULT_SIZE, ti);
    }

    public ObjectArraySequence(TypeInfo<T> ti, T... values) {
        this(ti, values, false);
    }

    public ObjectArraySequence(TypeInfo<T> ti, T[] values, boolean handoff) {
        super(ti);
        if (handoff) {
            this.array = values;
        }
        else {
            this.array = Util.<T>newObjectArray(values.length);
            System.arraycopy(values, 0, array, 0, values.length);
        }
        gapStart = gapEnd = values.length;
        checkForNulls();
    }

    public ObjectArraySequence(TypeInfo<T> ti, T[] values, int startPos, int size) {
        super(ti);
        this.array =  Util.<T>newObjectArray(size);
        System.arraycopy(values, startPos, array, 0, size);
        gapStart = gapEnd = size;
        checkForNulls();
    }
    
    @SuppressWarnings("unchecked")
    public ObjectArraySequence(TypeInfo<T> ti, List<? extends T> values) {
        super(ti);
        this.array = (T[]) values.toArray();
        gapStart = gapEnd = array.length;
        checkForNulls();
    }

    /*public ObjectArraySequence(TypeInfo<T> ti, Sequence<? extends T>... sequences) {
        super(ti);
        int size = 0;
        for (Sequence<? extends T> seq : sequences)
            size += seq.size();
        this.array = Util.<T>newObjectArray(size);
        int next = 0;
        for (Sequence<? extends T> seq : sequences) {
            final int l = seq.size();
            seq.toArray(0, l, array, next);
            next += l;
        }
        gapStart = gapEnd = size;
    }*/

    public ObjectArraySequence(TypeInfo<T> ti, Sequence<? extends T> seq) {
        super(ti);
        int size = seq.size();
        this.array = Util.<T>newObjectArray(size);
        seq.toArray(0, size, array, 0);
        gapStart = gapEnd = size;
    }

    private void checkForNulls() {
        int limit = gapStart;
        for (int i = 0; ; i++) {
            // limit is either gapStart or array.length.
            if (i == limit) {
                if (limit != gapStart)
                    break;
                i = gapEnd;
                limit = array.length;
                if (i == limit)
                    break;
            }
            if (array[i] == null)
                throw new IllegalArgumentException("cannot create sequence with null values");
        }
    }

    protected Object getRawArray() { return array; }
    protected Object newRawArray(int size) { return Util.<T>newObjectArray(size); }
    protected void setRawArray(Object array) { this.array = (T[]) array; }
    protected int getRawArrayLength() { return array.length; }
    protected T getRawArrayElementAsObject(int index) { return array[index]; }

    protected ObjectArraySequence extractOldValue(int startPos, int endPos) {
        int oldSize = array.length - gapEnd + endPos;
        ObjectArraySequence<T> copy = new ObjectArraySequence(oldSize, getElementType());
        copy.addFromArray(array, 0, startPos);
        copy.addFromArray(array, gapEnd-endPos+startPos, array.length);
        return copy;
    }

   @Override
    public T get(int position) {
        if (position >= gapStart)
            position += gapEnd - gapStart;
        if (position < 0 || position >= array.length)
            return getDefaultValue();
        else 
            return array[position];
    }


    // optimized versions
    @Override
    public BitSet getBits(SequencePredicate<? super T> predicate) {
        int sz = size();
        BitSet bits = new BitSet(sz);
        for (int i = 0; i < sz; i++) {
            int j = i;
            if (j >= gapStart)
                j += gapEnd - gapStart;
            if (predicate.matches(this, i, array[j]))
                bits.set(i);
        }
        return bits;
    }

    @Override
    public void toArray(int sourceOffset, int length, Object[] dest, int destOffset) {
        if (sourceOffset < 0 || length < 0 || sourceOffset + length > size())
            throw new ArrayIndexOutOfBoundsException();
        int beforeGap = gapStart - sourceOffset;
        if (beforeGap >= 0) {
            if (length <= beforeGap)
                beforeGap = length;
            System.arraycopy(array, sourceOffset, dest, destOffset, beforeGap);
            length -= beforeGap;
            destOffset += beforeGap;
            sourceOffset = gapEnd;
        }
        else
            sourceOffset += gapEnd - gapStart;
        System.arraycopy(array, sourceOffset, dest, destOffset, length);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int index;

            public boolean hasNext() {
                if (index == gapStart)
                    index = gapEnd;
                return index < array.length;
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

    /** Add a single element to the sequence, modifying it.
     * This must only be called when the sequence is unshared. */
    public void add(T element) {
        if (element != null) {
            gapReserve(size(), 1);
            array[gapStart++] = element;
        }
    }

    /** Add the contents of an existing sequence to the sequence.
     * This must only be called when the sequence is unshared. */
    @Override
    public void add(Sequence<? extends T> elements) {
        final int length = Sequences.size(elements);
        if (length > 0) {
            int size = size();
            gapReserve(size, length);
            elements.toArray(0, length, array, size);
            gapStart += length;
        }
    }

    public void add(T[] data, int loIndex, int hiIndex) { // deprecated FIXME
        addFromArray(data, loIndex, hiIndex);
        /*
        int length = hiIndex - loIndex;
        int size = size();
        gapReserve(size, length);
        System.arraycopy(data, loIndex, array, size, length);
        gapStart += length;
         * */
    }

    /** Internal method to replace a value. */
    public void replace (int position, T value) {
        if (position >= gapStart)
            position += gapEnd - gapStart;
        if (position < 0 || position >= array.length)
            return; // Sigh - we really should throw an exception.
        array[position] = value;
    }

    /** Replace a slice of this array.
     * @param startPos Starting position of the slice, inclusive, may be 0..size.
     * @param endPos Ending position of the slice, exclusive, may be startPos..size.
     * @param value The single replement value - must be non-null
     */
    public/*FIXME*/ void replace (int startPos, int endPos, T value, boolean hasTrigger) {
        if (endPos == startPos+1 && ! hasTrigger) {
            replace(startPos, value);
            return;
        }
        int size = size();
        int removed = endPos-startPos;
        gapReserve(startPos, removed == 0 || hasTrigger? 1 : 0);
        gapEnd = startPos + array.length - size + removed;
        array[startPos++] = value;
        gapStart=startPos;
        if (! hasTrigger)
            clearOldValues(removed);
    }

    @Override
    protected void replaceRaw(Sequence<? extends T> values, int sourceOffset, int length, int destPos) {
        values.toArray(sourceOffset, length, array, destPos);
    }

    /** Internal method to insert values.
     * Does not check shared flag, and does not do any notifications.
     */
    protected <T> void insert (Sequence<? extends T> values, int vsize, int where) {
        gapReserve(where, vsize);
        values.toArray(array, where);
        gapStart += vsize;
    }

    @Override
    public void clearOldValues (int oldLength) {
        for (int i = gapEnd-oldLength;  i < gapEnd;  i++)
            array[i] = null;
    }

}
