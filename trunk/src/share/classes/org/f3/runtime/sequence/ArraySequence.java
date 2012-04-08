/*
 * Copyright 2008, 2009 Sun Microsystems, Inc.  All Rights Reserved.
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
 * Sequence implementation class that stores sequence elements in an array.
 *
 * The {@code ArraySequence} can be in one of two modes:
 * <ul>
 * <li>In unshared/writabable mode there is only a single "owner" of the
 *     sequence, so modifying the sequence in-place is OK.
 *     For example inserting into a sequence variable can be implemented
 *     by changing the sequence object itself.
 * <li>In shared/read-only mode there is (or at least may be) multiple
 *     references to this sequence, so modifications are not allowed.
 *     For example inserting into a sequence variable has to be implemented
 *     by assigning a modified sequence.
 * </ul>
 *
 * An {@code ArraySequence} starts out unshared, and can be set to shared.
 * The compiler inserts calls to {@code incrementShared} when "reading" a sequence
 * variable in a way that may cause it to be shared.  Getting a single
 * item from the sequence does not require {@code shared} to be set.
 * We never go back from shared to unshared.
 *
 * We use a <a href="http://en.wikipedia.org/wiki/Buffer_gap">gap buffer</a>
 * as in the Emacs text editor and {@code javax.swing.text.GapContent}.
 * This provies efficient memory usage and locality.  It should perform
 * well in most uses.  The exception is if there are lots of insertions and
 * deletions at widely separated offsets, since that requires "moving" the
 * gap to the insertion or deletion point.
 *
 * @author Brian Goetz
 * @author Per Bothner
 */

public abstract class ArraySequence<T> extends AbstractSequence<T> {

    int gapStart, gapEnd;

    protected final static int DEFAULT_SIZE = 16;

    /* A simple (conservative) reference count.
     * If sharing==1, there are no "other" references, so operations like
     * insertion and concatenation can re-use this object, modifying the
     * object in-place.  This leads to major performance improvements - but
     * the compiler and library must be careful to set sharing > 1 whenever
     * sharing happens or can happen.  This is done with {@code #incrementSharing}.
     * The count is "sticky" once it wraps around.  Of course that's not going
     * to happen as long as we use an int, but in future if it saves space
     * we might reduce the number of bits used for {@code sharing}.
     */
    private int sharing;

    protected ArraySequence(TypeInfo<T> ti) {
        super(ti);
    }

    @Override
    public void incrementSharing() {
        int sh = sharing + 1;
        if (sh >= 0)
            sharing = sh;
    }

    @Override
    public void decrementSharing() {
        int sh = sharing;
        if (sh > 0)
            sharing = sh - 1;
    }

    public void setMaxShared() {
        sharing = (-1) >>> 1;
    }

    public boolean isShared() {
        return sharing > 1;
    }

    protected abstract Object getRawArray();
    protected abstract Object newRawArray(int size);
    protected abstract void setRawArray(Object array);
    protected abstract int getRawArrayLength();
    protected abstract T getRawArrayElementAsObject(int index);

    public ArraySequence<T> makeNew(int initializeSize) {
        return new ObjectArraySequence(initializeSize, getElementType());
    }

    /** Used by triggers to copy the "old value" of a sequence that got replace.
     * Assumes we're preserving the replaced elements in the buffer gap.
     * @param startPos
     * @param endPos
     * @return
     */
    protected abstract ArraySequence extractOldValue(int startPos, int endPos);
    
    public abstract void add(Sequence<? extends T> elements);

    public void addFromArray(Object data, int loIndex, int hiIndex) {
        int length = hiIndex - loIndex;
        int size = size();
        gapReserve(size, length);
        System.arraycopy(data, loIndex, getRawArray(), size, length);
        gapStart += length;
    }

    protected void shiftGap(int newGapStart) {
        int delta = newGapStart - gapStart;
        Object array = getRawArray();
        if (delta > 0)
            System.arraycopy(array, gapEnd, array, gapStart, delta);
        else if (delta < 0)
            System.arraycopy(array, newGapStart, array, gapEnd + delta, - delta);
        gapEnd += delta;
        gapStart = newGapStart;

        /*
         * When shifting gap, we need to clear unused portions of the "new" gap
         * with nulls. Without that ObjectArraySequences will leak objects.
         * See VSGC-3337 (memory leak in bound for).
         *
         * For large gaps, we don't want to null every slot in new gap. We can
         * ignore slots overlapping with "old" gap. In the line diagrams below,
         * NGS is New Gap Start, NGE is New Gap End, OGS is Old Gap Start and
         * OGE is Old Gap End. To clear with null slots, we re-use "clearOldValues"
         * that is used to clean the preserved old elements for triggers.
         */
        int gapSize = gapEnd - gapStart;
        if (delta > 0) {
            if (gapSize < delta) {
                //
                //  |<--gapSize-->|--------|<--gapSize-->|
                //  OGS          OGE      NGS           NGE
                //  |<------ delta ------->|
                //
                // need to clear the entire new gap
                clearOldValues(gapSize);
            } else {
                //
                //  |<--delta-->|--------|<--delta-->|
                //  OGS         NGS     OGE         NGE
                //  |<------ gapSize---->|
                //
                // need to clear only the "delta" between NGE and OGE
                clearOldValues(delta);
            }

        } else { // negative delta

            if (gapSize < -delta) {

                //
                //  |<--gapSize-->|--------|<--gapSize-->|
                //  NGS          NGE      OGS           OGE
                //  |<------ delta ------->|
                //
                // need to clear the entire new gap
                clearOldValues(gapSize);
            } else {
                //
                //  |<--delta-->|--------|<--delta-->|
                //  NGS         OGS     NGE         OGE
                //  |<------ gapSize---->|
                //
                // need to clear only the "delta" between NGS and OGS

                // do it by temporarily moving NGE to where OGS is
                // and clear "delta" values from there.
                int tmpGapEnd = gapEnd;
                gapEnd = gapStart - delta;
                clearOldValues(-delta);
                gapEnd = tmpGapEnd;
            }
        }
    }

    /** Make sure gap is at least 'needed' elements long. */
    protected void gapReserve(int where, int needed) {
        if (needed > gapEnd - gapStart) {
            int oldLength = getRawArrayLength();
            int newLength = oldLength < 16 ? 16 : 2 * oldLength;
            int minLength = oldLength - (gapEnd - gapStart) + needed;
            if (newLength < minLength)
                newLength = minLength;
            Object newArray = newRawArray(newLength);
            int oldGapSize = gapEnd - gapStart;
            int size = oldLength-oldGapSize;
            int newGapEnd = newLength - size + where;
            int gapDelta = gapStart - where;
            int startLength;
            Object oldArray = getRawArray();
            if (gapDelta >= 0) {
                startLength = where;
                int endLength = oldLength - gapEnd;
                System.arraycopy(oldArray, gapEnd, newArray, newLength - endLength, endLength);
                if (gapDelta > 0)
                    System.arraycopy(oldArray, where, newArray, newGapEnd, gapDelta);
                }
            else {
                startLength = gapStart;
                int endLength = newLength - newGapEnd;
                System.arraycopy(oldArray, oldLength-endLength, newArray, newGapEnd, endLength);
                System.arraycopy(oldArray, gapEnd, newArray, gapStart, -gapDelta);
            }
            System.arraycopy(oldArray, 0, newArray, 0, startLength);
            setRawArray(newArray);
            gapStart = where;
            gapEnd = newGapEnd;
        }
        else if (where != gapStart)
            shiftGap(where);
    }

    @Override
    public int size() {
        return getRawArrayLength() - (gapEnd - gapStart);
    }

    @Override
    public T get(int position) {
        if (position >= gapStart)
            position += gapEnd - gapStart;
        if (position < 0 || position >= getRawArrayLength())
            return getDefaultValue();
        else
            return getRawArrayElementAsObject(position);
    }

    /** Replace a slice of this array.
     * @param startPos Starting position of the slice, inclusive, may be 0..size.
     * @param endPos Ending position of the slice, exclusive, may be startPos..size.
     * @param value The replacement source
     * @param sourceStart Starting position in source.
     * @param sourceEnd Ending position in source.
     * @param hasTrigger If true, keep removed source in the gap.
     */
    public void replace(int startPos, int endPos, Sequence<? extends T> source, int sourceStart, int sourceEnd, boolean hasTrigger) {
        int size = size();
        int nsize = sourceEnd-sourceStart;
        int delta = nsize - (endPos-startPos);
        gapReserve(startPos, hasTrigger ? nsize : delta >= 0 ? delta : 0);
        if (nsize != 0)
            replaceRaw(source, sourceStart, nsize, startPos);
        startPos += nsize;
        gapStart=startPos;
        gapEnd = startPos + (getRawArrayLength() - size - delta);
    }

    protected abstract void replaceRaw(Sequence<? extends T> values, int sourceOffset, int length, int startPos);

    public void clearOldValues (int oldLength) {
    }

    /* DEBUGGING code:
    int id=++counter;
    static int counter;
    public String toXString() {
        StringBuilder sbuf = new StringBuilder();
        int alen = getRawArrayLength();
        int sz = size();
        sbuf.append("[#"+id+"(sharing:"+sharing+" gap:"+gapStart+"..<"+gapEnd+" alen:"+alen+" size:"+sz+")");
        if (false) {
            for (int i = 0; i < sz; i++)
                sbuf.append(" "+get(i));
        } else {
            for (int i = 0; ; i++) {
                if (gapStart == i) sbuf.append(" [gap: ");
                if (gapEnd == i) sbuf.append(" ] ");
                if (i >= alen)
                    break;
                sbuf.append(" "+getRawArrayElementAsObject(i));
            }
        }
        sbuf.append(']');
        return sbuf.toString();
    }
    public String toString() { return toXString(); }
    */
}
