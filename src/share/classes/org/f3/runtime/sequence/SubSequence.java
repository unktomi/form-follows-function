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

/**
 * Represents a portion of another sequence.  Subsequences should be created with the Sequences.subsequence() factory
 * method, rather than with the SubSequence constructor.  O(1) space and time construction costs.
 *
 * @author Brian Goetz
 * @author Per Bothner
 */
class SubSequence<T> extends AbstractSequence<T> {
    protected final Sequence<? extends T> sequence;
    protected final int size;
    private final int startPos;
    private final int step;

    /** Create a slice of a sequence.
     * Uses a default step-size of 1.
     * @param sequence The sequence that we create a subsequence from.
     * @param startPos The startPos index.  Caller needs to validate that {@code 0 <= startPos <= sequence.size()}.
     * @param endPos The end index, exclusive.  Caller needs to validate that {@code startPos <= endPos <= sequence.size()}.
     */
    public SubSequence(Sequence<T> sequence, int startPos, int endPos) {
        this(sequence, endPos-startPos, startPos, 1);
    }

    private SubSequence(Sequence<T> sequence, int size, int startPos, int step) {
        super(sequence.getElementType());
        this.startPos = startPos;
        this.step = step;
        this.sequence = sequence;
        this.size = size;
    }

    /** Create a slice of a sequence.
     * Unlike the constructor, this will "collapse" the cases of creating
     * a slice from a SubSequence, or when the size is zero.
     * WARNING The generalization to step!=1, except as used by the Sequence.reverse
     * method, is UNTESTED.  That is why this method is PACKAGE-PRIVATE for now.
     * @param sequence The sequence that we create a subsequence from.
     * @param startPos The startPos index.
     *   Caller needs to validate that {@code 0 <= startPos <= sequence.size()}.
     * @param size Number of elements in the slice.
     *   Caller needs to validate that this is correct: I.e. {@code size >= 0} and
     *   and {@code startPos >= 0 && startPos + step*(size-1) < sequence.size()} when {@code step >= 0 && size > 0},
     *   or {@code startPos < sequence.size() && startPos + step*(size-1) >= 0} when {@code step < 0 && size > 0},
     *   or {@code 0 <= startPos <= sequence.size()} when {@code size == 0}.
     * @param step The step size (stride) between selected elements in the base {@code sequence}.
     *
     */
    static <T> Sequence make(Sequence<T> sequence, int size, int start, int step) {
        if (size <= 0)
            return sequence.getElementType().emptySequence;
        if (sequence instanceof SubSequence) {
            SubSequence sseq = (SubSequence) sequence;
            start = sseq.startPos + sseq.step * start;
            step = sseq.step * step;
            sequence = sseq.sequence;
        }
        sequence.incrementSharing();
        return new SubSequence(sequence, size, start, step);
    }

    @Override
    public T get(int position) {
        if (position < 0 || position >= size)
            return getDefaultValue();
        else
            return sequence.get(startPos + step * position);
    }
    
    @Override
    public boolean getAsBoolean(int position) {
        if (position < 0 || position >= size)
            return false;
        else
            return sequence.getAsBoolean(startPos + step * position);
    }

    @Override
    public char getAsChar(int position) {
        if (position < 0 || position >= size)
            return '\0';
        else
            return sequence.getAsChar(startPos + step * position);
    }

    @Override
    public byte getAsByte(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsByte(startPos + step * position);
    }

    @Override
    public short getAsShort(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsShort(startPos + step * position);
    }

    @Override
    public int getAsInt(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsInt(startPos + step * position);
    }

    @Override
    public long getAsLong(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsLong(startPos + step * position);
    }

    @Override
    public float getAsFloat(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsFloat(startPos + step * position);
    }

    @Override
    public double getAsDouble(int position) {
        if (position < 0 || position >= size)
            return 0;
        else
            return sequence.getAsDouble(startPos + step * position);
    }

    public int size() {
        return size;
    }

    @Override
    public void toArray(int sourceOffset, int length, Object[] dest, int destOffset) {
        if (sourceOffset < 0 || (length > 0 && sourceOffset + length > size()))
            throw new ArrayIndexOutOfBoundsException();
        if (step == 1)
            sequence.toArray(startPos+sourceOffset, length, dest, destOffset);
        else {
            int j = startPos + step * sourceOffset;
            for (int i = 0;  i < length;  i++, j += step)
                dest[i + destOffset] = sequence.get(j);
        }
    }
}
