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

import org.f3.runtime.TypeInfo;

/**
 * Special case implementation for sequences that are ranges of floating point, such as [1.0 .. 2.0 BY .1].
 * Range sequences should be constructed with the Sequences.range() factory method rather than with the
 * NumberRangeSequence constructor. Unlike integer range sequences, the step is required.
 * O(1) space and time construction costs.
 *
 * @author Brian Goetz
 */
class NumberRangeSequence extends AbstractSequence<Float> implements Sequence<Float> {

    private final float start, step;
    private final int size;


    public NumberRangeSequence(float start, float bound, float step, boolean exclusive) {
        super(TypeInfo.Float);
        if (step == 0.0f)
            throw new IllegalArgumentException("Range step of zero");
        this.start = start;
        this.step = step;
        this.size = Sequences.calculateFloatRangeSize(start, bound, step, exclusive);
    }

    public NumberRangeSequence(float start, float bound, float step) {
        this(start, bound, step, false);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Float get(int position) {
        if (position < 0 || position >= size)
            return 0.0f;
        else
            return (start + position * step);
    }

    @Override
    public float getAsFloat(int position) {
        if (position < 0 || position >= size)
            return 0.0f;
        else
            return (start + position * step);
    }

    @Override
    public void toArray(int sourceOffset, int length, Object[] dest, int destOffset) {
        if (sourceOffset < 0 || (length > 0 && sourceOffset + length > size))
            throw new ArrayIndexOutOfBoundsException();

        int index = destOffset;
        for (float value = start + sourceOffset*step; index < destOffset+length; value += step, index++)
            dest[index] = value;
    }
}
