/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

import org.f3.runtime.TypeInfo;

$TEMPLATE_FROM$

public class $T.ArraySequence$ extends ArraySequence<$T.boxed$> {
    $T.prim$[] array;

    public $T.ArraySequence$(int initialSize, TypeInfo<$T.boxed$> ti) {
        super(ti);
        this.array = new $T.prim$[initialSize];
        gapStart = 0;
        gapEnd = initialSize;
    }

    public $T.ArraySequence$(int initialSize) {
        this(initialSize, $T.TypeInfo$);
    }

    public $T.ArraySequence$() {
        this(DEFAULT_SIZE, $T.TypeInfo$);
    }

    public $T.ArraySequence$(TypeInfo<$T.boxed$> ti, $T.prim$... values) {
        this(ti, values, false);
    }

    public $T.ArraySequence$(TypeInfo<$T.boxed$> ti, $T.prim$[] values, boolean handoff) {
        super(ti);
        if (handoff) {
            this.array = values;
        }
        else {
            this.array = new $T.prim$[values.length];
            System.arraycopy(values, 0, array, 0, values.length);
        }
        gapStart = gapEnd = values.length;
    }

    public $T.ArraySequence$($T.prim$[] values, int startPos, int size) {
        super($T.TypeInfo$);
        array = new $T.prim$[size];
        System.arraycopy(values, startPos, array, 0, size);
        gapStart = gapEnd = size;
    }

    public $T.ArraySequence$(Sequence<? extends $T.boxed$> seq) {
        super($T.TypeInfo$);
        int size = seq.size();
        this.array = new $T.prim$[size];
        seq.toArray(0, size, array, 0);
        gapStart = gapEnd = size;
    }

    protected Object getRawArray() { return array; }
    protected Object newRawArray(int size) { return new $T.prim$[size]; }
    protected void setRawArray(Object array) { this.array = ($T.prim$[]) array; }
    protected int getRawArrayLength() { return array.length; }
    protected $T.boxed$ getRawArrayElementAsObject(int index) { return array[index]; }

    public $T.ArraySequence$ makeNew(int initializeSize) {
        return new $T.ArraySequence$(initializeSize, getElementType());
    }

    public $T.prim$ getAs$PREFIX$(int position) {
        if (position >= gapStart)
            position += gapEnd - gapStart;
        if (position < 0 || position >= array.length)
            return $V.default$;
        else
            return array[position];
    }

    public void add($T.prim$ element) {
        gapReserve(size(), 1);
        array[gapStart++] = element;
    }

    @Override
    public void add(Sequence<? extends $T.boxed$> elements) {
        final int length = Sequences.size(elements);
        if (length > 0) {
            int size = size();
            gapReserve(size, length);
            elements.toArray(0, length, array, size);
            gapStart += length;
        }
    }

    @Override
    public void toArray(int sourceOffset, int length, $T.prim$[] dest, int destOffset) {
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

    protected $T.ArraySequence$ extractOldValue(int startPos, int endPos) {
        int oldSize = array.length - gapEnd + endPos;
        $T.ArraySequence$ copy = new $T.ArraySequence$(oldSize, getElementType());
        copy.addFromArray(array, 0, startPos);
        copy.addFromArray(array, gapEnd-endPos+startPos, array.length);
        return copy;
    }

    public void replace (int position, $T.prim$ value) {
        if (position >= gapStart)
            position += gapEnd - gapStart;
        if (position < 0 || position >= array.length)
            return; // Sigh - we really should throw an exception.
        array[position] = value;
    }

    public void replace (int startPos, int endPos, $T.prim$ value, boolean hasTrigger) {
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
    }

    protected void replaceRaw(Sequence<? extends $T.boxed$> values, int sourceOffset, int length, int startPos) {
        values.toArray(sourceOffset, length, array, startPos);
    }

   /*public void replace(int startPos, int endPos, Sequence<? extends $T.boxed$> values, boolean hasTrigger) {
        int size = size();
        int nsize = values.size();
        int delta = nsize - (endPos-startPos);
        gapReserve(startPos, hasTrigger ? nsize : delta >= 0 ? delta : 0);
        values.toArray(0, nsize, array, startPos);
        startPos += nsize;
        gapStart=startPos;
        gapEnd = startPos + (array.length - size - delta);
    }*/


    public static $T.prim$ extractOldElement(ArraySequence<$T.boxed$> buffer, Sequence<? extends $T.boxed$> oldValue, int startPos, int endPos, int k) {
        if (oldValue != null)
            return oldValue.get(k);
        if (k >= 0) {
            if (k >= startPos)
                k += buffer.gapEnd - endPos;
            int alen = buffer.getRawArrayLength();
            if (k < alen) {
                return buffer.getRawArrayElementAsObject(k);
            }
       }
      return buffer.getDefaultValue();
  }

  public static $T.prim$ extractNewElement(ArraySequence<$T.boxed$> buffer, int loIndex, Sequence<? extends $T.boxed$> newElements, int k) {
    if (newElements != null)
      return newElements.get(k);
    if (k >= 0) {
      k = k + loIndex;
      if (k < buffer.gapStart)
        return buffer.get(k);
    }
    return buffer.getDefaultValue();
  }

}
