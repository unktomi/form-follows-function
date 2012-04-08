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

import java.util.*;

import org.f3.runtime.AssignToBoundException;
import org.f3.runtime.TypeInfo;
import org.f3.runtime.Util;
import org.f3.runtime.F3Object;
import org.f3.runtime.NumericTypeInfo;

/**
 * SequencesBase
 *
 * @author Brian Goetz
 */
public class SequencesBase {

    // Must match F3Defs.UNDEFINED_MARKER_INT
    public static final int UNDEFINED_MARKER_INT = -1000;
   
    /*******************************************/
    /* Converting between sequences and arrays */
    /*******************************************/

    /** Convert a T[] to a Sequence<T> */
    public static<T> Sequence<T> fromArray(TypeInfo<T> ti, T[] values) {
        if (values == null)
            return ti.emptySequence;
        return new ObjectArraySequence<T>(ti, values);
    }

    /** Convert a long[] to a Sequence<Long> */ 
    public static Sequence<Long> fromArray(long[] values) {
        if (values == null)
            return TypeInfo.Long.emptySequence;
        return new LongArraySequence(values, 0, values.length);
    }

    /** Convert an int[] to a Sequence<Integer> */
    public static Sequence<Integer> fromArray(int[] values) {
        if (values == null)
            return TypeInfo.Integer.emptySequence;
        return new IntArraySequence(values, 0, values.length);
    }

    /** Convert a short[] to a Sequence<Short> */
    public static Sequence<Short> fromArray(short[] values) {
        if (values == null)
            return TypeInfo.Short.emptySequence;
        return new ShortArraySequence(values, 0, values.length);
    }

    /** Convert a char[] to a Sequence<Character> */
    public static Sequence<Character> fromArray(char[] values) {
        if (values == null)
            return TypeInfo.Character.emptySequence;
        return new CharArraySequence(values, 0, values.length);
    }

    /** Convert a byte[] to a Sequence<Byte> */
    public static Sequence<Byte> fromArray(byte[] values) {
        if (values == null)
            return TypeInfo.Byte.emptySequence;
        return new ByteArraySequence(values, 0, values.length);
    }

    /** Convert a double[] to a Sequence<Double> */
    public static Sequence<Double> fromArray(double[] values) {
        if (values == null)
            return TypeInfo.Double.emptySequence;
        return new DoubleArraySequence(values, 0, values.length);
    }

    /** Convert a float[] to a Sequence<Float> */
    public static Sequence<Float> fromArray(float[] values) {
        if (values == null)
            return TypeInfo.Float.emptySequence;
        return new FloatArraySequence(values, 0, values.length);
    }

    /** Convert a boolean[] to a Sequence<Boolean> */
    public static Sequence<Boolean> fromArray(boolean[] values) {
        if (values == null)
            return TypeInfo.Boolean.emptySequence;
        return new BooleanArraySequence(values, 0, values.length);
    }

    /** Convert a Sequence<T> to an array */
    public static<T> T[] toArray(Sequence<? extends T> seq) {
        T[] unboxed = Util.<T>newObjectArray(seq.size());
        int i=0;
        for (T val : seq) {
            unboxed[i++] = val;
        }
        return unboxed;
    }

    /** Convert a Sequence<Long> to an array */
    public static long[] toLongArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        long[] unboxed = new long[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsLong(i);
        return unboxed;
    }

    /** Convert a Sequence<Integer> to an array */
    public static int[] toIntArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        int[] unboxed = new int[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsInt(i);
        return unboxed;
    }

    /** Convert a Sequence<Short> to an array */
    public static short[] toShortArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        short[] unboxed = new short[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsShort(i);
        return unboxed;
    }

    /** Convert a Sequence<Byte> to an array */
    public static byte[] toByteArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        byte[] unboxed = new byte[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsByte(i);
        return unboxed;
    }

    /** Convert a Sequence<Double> to a double array */
    public static double[] toDoubleArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        double[] unboxed = new double[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsDouble(i);
        return unboxed;
    }

    /** Convert a Sequence<Double> to a float array */
    public static float[] toFloatArray(Sequence<? extends Number> seq) {
        int size = seq.size();
        float[] unboxed = new float[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsFloat(i);
        return unboxed;
    }

    /** Convert a Sequence<Boolean> to an array */
    public static boolean[] toBooleanArray(Sequence<? extends Boolean> seq) {
        int size = seq.size();
        boolean[] unboxed = new boolean[size];
        for (int i = size;  --i >= 0; )
            unboxed[i] = seq.getAsBoolean(i);
        return unboxed;
    }

    @SuppressWarnings("unchecked")
    public static<T> ObjectArraySequence<T> forceNonSharedObjectArraySequence(TypeInfo<T> typeInfo, Sequence<? extends T> value) {
        ObjectArraySequence<T> arr;
        block: {
            if (value instanceof ObjectArraySequence) {
                arr = (ObjectArraySequence) value;
                if (! arr.isShared()) {
                    // FIXME: arr.setElementType(typeInfo);
                    return arr;
                }
                // Special case - we might as well re-use an empty array.
                if (arr.array.length == 0) {
                    arr = new ObjectArraySequence(typeInfo, arr.array, true);
                    break block;
                }
            }
            arr = new ObjectArraySequence(typeInfo, value);
        }
        arr.incrementSharing();
        return arr;
    }

    public static<T> ArraySequence<T> forceNonSharedArraySequence(TypeInfo<T> typeInfo, Sequence<? extends T> value) {
        ArraySequence<T> arr;
        block: {
            if (value instanceof ArraySequence) {
                arr = (ArraySequence) value;
                if (! arr.isShared()) {
                    // FIXME: arr.setElementType(typeInfo);
                    return arr;
                }
            }
            arr = typeInfo.emptySequence.makeNew(0);
            arr.add(value);
        }
        arr.incrementSharing();
        return arr;
    }

    public static<T> T incrementSharing(T value) {
        if (value instanceof ArraySequence)
            ((ArraySequence) value).incrementSharing();
        return value;
    }

    /***************************************************/
    /* Methods for constructing sequences from scratch */
    /***************************************************/


    /** Factory for simple sequence generation */
    public static<T> Sequence<T> make(TypeInfo<T> ti, T... values) {
        if (values == null || values.length == 0)
            return ti.emptySequence; 
        else
            return new ObjectArraySequence<T>(ti, values);
    }

    /** Factory for simple sequence generation */
    public static<T> Sequence<T> make(TypeInfo<T> ti, T[] values, int size) {
        if (values == null || size <= 0)
            return ti.emptySequence;
        else
            return new ObjectArraySequence<T>(ti, values, 0, size);
    }

    public static<T> Sequence<T> makeViaHandoff(TypeInfo<T> ti, T[] values) {
        return new ObjectArraySequence<T>(ti, values, true);
    }

    /** Factory for simple sequence generation */
    public static<T> Sequence<T> make(TypeInfo<T> ti, List<? extends T> values) {
        if (values == null || values.size() == 0)
            return ti.emptySequence;
        else
            return new ObjectArraySequence<T>(ti, values);
    }

    /** Create an Integer range sequence ranging from lower to upper inclusive. */
    public static Sequence<Integer> range(int lower, int upper) {
        return new IntRangeSequence(lower, upper);
    }

    /** Create an Integer range sequence ranging from lower to upper inclusive, incrementing by the specified step. */
    public static Sequence<Integer> range(int lower, int upper, int step) {
        return new IntRangeSequence(lower, upper, step);
    }

    /** Create an Integer range sequence ranging from lower to upper exclusive. */
    public static Sequence<Integer> rangeExclusive(int lower, int upper) {
        return new IntRangeSequence(lower, upper, true);
    }

    /** Create an Integer range sequence ranging from lower to upper exnclusive, incrementing by the specified step. */
    public static Sequence<Integer> rangeExclusive(int lower, int upper, int step) {
        return new IntRangeSequence(lower, upper, step, true);
    }

    /** Create a double range sequence ranging from lower to upper inclusive, incrementing by 1.0 */
    public static Sequence<Float> range(float lower, float upper) {
        return new NumberRangeSequence(lower, upper, 1.0f);
    }

    /** Create a double range sequence ranging from lower to upper inclusive, incrementing by the specified step. */
    public static Sequence<Float> range(float lower, float upper, float step) {
        return new NumberRangeSequence(lower, upper, step);
    }

    /** Create a double range sequence ranging from lower to upper exnclusive */
     public static Sequence<Float> rangeExclusive(float lower, float upper) {
        return new NumberRangeSequence(lower, upper, 1.0f, true);
    }
    /** Create a double range sequence ranging from lower to upper exnclusive, incrementing by the specified step. */
    public static Sequence<Float> rangeExclusive(float lower, float upper, float step) {
        return new NumberRangeSequence(lower, upper, step, true);
    }

    /** Create a filtered sequence.  A filtered sequence contains some, but not necessarily all, of the elements
     * of another sequence, in the same order as that sequence.  If bit n is set in the BitSet, then the element
     * at position n of the original sequence appears in the filtered sequence.  */
    public static<T> Sequence<T> filter(Sequence<T> seq, BitSet bits) {
        int cardinality = bits.cardinality();
        if (cardinality == 0)
            return seq.getEmptySequence();
        else if (cardinality == seq.size() && bits.nextClearBit(0) == seq.size())
            return seq;
        else {
            ObjectArraySequence<T> result = new ObjectArraySequence(cardinality, seq.getElementType());
            for (int i = bits.nextSetBit(0), next = 0; i >= 0; i = bits.nextSetBit(i + 1))
                result.add(seq.get(i));
            return result;
        }
    }

    /** Extract a subsequence from the specified sequence, starting as the specified start position, and up to but
     * not including the specified end position.  If the start position is negative it is assumed to be zero; if the
     * end position is greater than seq.size() it is assumed to be seq.size().  */
    public static<T> Sequence<T> subsequence(Sequence<T> seq, int start, int end) {
        // OPT: for small sequences, just copy the elements
        if (start >= end)
            return seq.getEmptySequence();
        else if (start <= 0 && end >= seq.size())
            return seq;
        else {
            start = Math.max(start, 0);
            end = Math.min(end, seq.size());
            return SubSequence.make(seq, end-start, start, 1);
        }
    }

    public static int calculateIntRangeSize(int lower, int upper, int step, boolean exclusive) {
        if (step == 0) {
            // Undo VSGC-3735 - because of forward reference binds, zero happens frequently.
            return 0;
        }
        if (Math.abs((long) lower - (long) upper) + ((long) (exclusive ? 0 : 1)) > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Range sequence too big");
        if (upper == lower) {
            return exclusive ? 0 : 1;
        }
        else {
            int size = Math.max(0, ((upper - lower) / step) + 1);
            if (exclusive) {
                boolean tooBig = (step > 0)
                        ? (lower + (size-1)*step >= upper)
                        : (lower + (size-1)*step <= upper);
                if (tooBig && size > 0)
                    --size;
            }
            return (int) size;
        }
    }

    public static int calculateFloatRangeSize(float lower, float upper, float step, boolean exclusive) {
        if (step == 0.0f) {
            // Undo VSGC-3735 - because of forward reference binds, zero happens frequently.
            return 0;
        }
        if (upper == lower) {
            return exclusive ? 0 : 1;
        } else {
            long sz = ((upper < lower && step > 0.0f) ||
                    (upper > lower && step < 0.0f)) ? 0
                    : Math.max(0, (((long) ((upper - lower) / step)) + 1));
            if (exclusive) {
                boolean tooBig = (step > 0.0f)
                        ? (lower + (sz - 1) * step >= upper)
                        : (lower + (sz - 1) * step <= upper);
                if (tooBig && sz > 0) {
                    --sz;
                }
            }
            if (sz > Integer.MAX_VALUE || sz < 0) {
                throw new IllegalArgumentException("Range sequence too big");
            } else {
                return (int) sz;
            }
        }
    }

    /* NOTE Possible future functionality, to allow a step in a slice expression.
     * This is a sketch, which needs some tweaking to handle corner cases,
     * plus some compiler work.
     * NOTE The generalization to step!=1, except as used by the reverse
     * function, is UNTESTED.
    public static<T> Sequence<T> subsequence(Sequence<T> seq, int start, int bound, int step, boolean exclusive) {
        // FIXME canonicalize start (if out of range)
        int size = calculateSize(start, bound, step, exclusive);
        return SubSequence.make(seq, start, size, step);
    }
    */

    /** Create a sequence containing a single element, the specified value */
    public static<T> Sequence<T> singleton(TypeInfo<T> ti, T t) {
        if (t == null)
            return ti.emptySequence;
        else
            return new SingletonSequence<T>(ti, t);
    }

    /** Create an empty sequence */
    public static<T> Sequence<T> emptySequence(Class<T> clazz) {
        return TypeInfo.getTypeInfo(clazz).emptySequence;
    }

    /** Reverse an existing sequence */
    public static<T> Sequence<T> reverse(Sequence<T> sequence) {
        int ssize = sequence.size();
        return SubSequence.make(sequence, ssize, ssize-1, -1);
    }

    /** Convert a Collection<T> to a Sequence<T> */
    @SuppressWarnings("unchecked")
    public static<T> Sequence<T> fromCollection(TypeInfo<T> ti, Collection<T> values) {
        if (values == null)
            return ti.emptySequence;
        // OPT: Use handoff, pre-size array
        return new ObjectArraySequence<T>(ti, (T[]) values.toArray());
    }

    /**********************************************/
    /* Utility methods for dealing with sequences */
    /**********************************************/


    /** Upcast a sequence of T to a sequence of superclass-of-T */
    @SuppressWarnings("unchecked")
    public static<T> Sequence<T> upcast(Sequence<? extends T> sequence) {
        return (Sequence<T>) sequence;
    }

    /** Convert any numeric sequence to any other numeric sequence */
    @SuppressWarnings("unchecked")
    public static Sequence<? extends Object> convertObjectToSequence(Object obj) {
        if (obj instanceof Sequence) {
            return (Sequence<? extends Object>)obj;
        }
        else
            return singleton(TypeInfo.Object, obj);
    }

    /** Convert any numeric sequence to any other numeric sequence */
    public static<T extends Number, V extends Number>
    Sequence<T> convertNumberSequence(NumericTypeInfo<T> toType, NumericTypeInfo<V> fromType, Sequence<? extends V> seq) {
        if (Sequences.size(seq) == 0) 
            return toType.emptySequence;

        int length = seq.size();
        T[] toArray = toType.makeArray(length);
        int i=0;
        for (V val : seq) {
            toArray[i++] = toType.asPreferred(fromType, val);
        }
        return new ObjectArraySequence<T>(toType, toArray, 0, length);
    }

    /** Convert any numeric sequence to a char sequence */
    public static<V extends Number>
    Sequence<Character> convertNumberToCharSequence(NumericTypeInfo<V> fromType, Sequence<? extends V> seq) {
        if (Sequences.size(seq) == 0)
            return TypeInfo.Character.emptySequence;

        int length = seq.size();
        Character[] toArray = new Character[length];
        int i=0;
        for (V val : seq) {
            toArray[i++] = (char)val.intValue();
        }
        return new ObjectArraySequence<Character>(TypeInfo.Character, toArray, 0, length);
    }

    /** Convert a char sequence to any numeric sequence */
    public static<V extends Number>
    Sequence<V> convertCharToNumberSequence(NumericTypeInfo<V> targetType, Sequence<? extends Character> seq) {
        if (Sequences.size(seq) == 0)
            return targetType.emptySequence;

        int length = seq.size();
        V[] toArray = targetType.makeArray(length);
        int i=0;
        for (Character val : seq) {
            toArray[i++] = targetType.asPreferred(TypeInfo.Integer, (int)val.charValue());
        }
        return new ObjectArraySequence<V>(targetType, toArray, 0, length);
    }

    /** How large is this sequence?  Can be applied to any object.  */
    public static int size(Object seq) {
        if (seq instanceof Sequence)
            return ((Sequence) seq).size();
        else
            return seq == null ? 0 : 1;
    }

    /** How large is this sequence?  */
    public static int size(Sequence seq) {
        return (seq == null) ? 0 : seq.size();
    }

    @SuppressWarnings("unchecked")
    public static<T> Iterator<T> iterator(Sequence<T> seq) {
        return (seq == null)? (Iterator<T>) TypeInfo.Object.emptySequence.iterator() : seq.iterator();
    }

    @SuppressWarnings("unchecked")
    public static<T> Iterator<T> iterator(Sequence<T> seq, int startPos, int endPos) {
        return (seq == null)? (Iterator<T>) TypeInfo.Object.emptySequence.iterator() : seq.iterator(startPos, endPos);
    }

    public static<T> boolean isEqual(Sequence<?> one, Sequence<?> other) {
        int oneSize = size(one);
        int otherSize = size(other);
        if (oneSize == 0)
            return (otherSize == 0);
        else if (oneSize != otherSize)
            return false;
        else {
            Iterator<?> it1 = one.iterator();
            Iterator<?> it2 = other.iterator();
            while (it1.hasNext()) {
                if (! it1.next().equals(it2.next()))
                    return false;
            }
            return true;
        }
    }

    public static<T> boolean isEqualByContentIdentity(Sequence<? extends T> one, Sequence<? extends T> other) {
        int oneSize = size(one);
        if (oneSize == 0)
            return size(other) == 0;
        else if (oneSize != size(other))
            return false;
        else {
            Iterator<? extends T> it1 = one.iterator();
            Iterator<? extends T> it2 = other.iterator();
            while (it1.hasNext()) {
                if (it1.next() != it2.next())
                    return false;
            }
            return true;
        }
    }

    public static<T> boolean sliceEqual(Sequence<? extends T> seq, int startPos, int endPos/*exclusive*/, Sequence<? extends T> slice) {
        int size = size(slice);
        if (endPos - startPos != size)
            return false;
        /* For most (but not all) Sequences types, indexing is faster.
        Iterator<? extends T> seqIterator = iterator(seq, startPos, endPos-1);
        for (Iterator<? extends T> sliceIterator = iterator(slice); sliceIterator.hasNext(); ) {
            if (!seqIterator.next().equals(sliceIterator.next())) {
                return false;
            }
        }
        */
        for (int i = 0;  i < size;  i++) {
            if (!seq.get(startPos+i).equals(slice.get(i)))
                return false;
        }
        return true;
    }

    public static<T> Sequence<? extends T> forceNonNull(TypeInfo<T> typeInfo, Sequence<? extends T> seq) {
        return seq == null ? typeInfo.emptySequence : seq;
    }

    /**
     * Return the single value of a sequence.
     * Return null if the sequence zero zero or more than 1 elements.
     * Thid is used to implement 'seq instanceof T'.
     */
    public static <T> T getSingleValue (Sequence<T> seq) {
        if (seq == null || seq.size() != 1)
            return null;
        return seq.get(0);
    }


    /*************************/
    /* Sorting and searching */
    /*************************/

    /**
     * Searches the specified sequence for the specified object using the
     * binary search algorithm. The sequence must be sorted into ascending
     * order according to the natural ordering of its elements (as by
     * the sort(Sequence<T>) method) prior to making this call.
     *
     * If it is not sorted, the results are undefined. If the array contains
     * multiple elements equal to the specified object, there is no guarantee
     * which one will be found.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise, (-(insertion point) - 1). The insertion point is
     *         defined as the point at which the key would be inserted into the
     *         array: the index of the first element greater than the key, or
     *         a.length if all elements in the array are less than the specified
     *         key. Note that this guarantees that the return value will be >= 0
     *         if and only if the key is found.
     */
    public static <T extends Comparable> int binarySearch (Sequence<? extends T> seq, T key) {
        if (seq.isEmpty())
            return -1;
        final int length = seq.size();
        T[] array = Util.<T>newComparableArray(length);
        seq.toArray(0, length, array, 0);
        return Arrays.binarySearch(array, key);
    }

    /**
     * Searches the specified array for the specified object using the
     * binary search algorithm. The array must be sorted into ascending
     * order according to the specified comparator (as by the
     * sort(Sequence<T>, Comparator<? super T>)  method) prior to making
     * this call.
     *
     * If it is not sorted, the results are undefined. If the array contains
     * multiple elements equal to the specified object, there is no guarantee
     * which one will be found.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @param c The comparator by which the array is ordered. A null value
     *          indicates that the elements' natural ordering should be used.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise, (-(insertion point) - 1). The insertion point is
     *         defined as the point at which the key would be inserted into the
     *         array: the index of the first element greater than the key, or
     *         a.length if all elements in the array are less than the specified
     *         key. Note that this guarantees that the return value will be >= 0
     *         if and only if the key is found.
     */
    public static <T> int binarySearch(Sequence<? extends T> seq,  T key,  Comparator<? super T> c) {
        if (seq.isEmpty())
            return -1;
        final int length = seq.size();
        T[] array = Util.<T>newObjectArray(length);
        seq.toArray(0, length, array, 0);
        return Arrays.binarySearch(array, (T)key, c);
    }

    /**
     * Searches the specified sequence for the specified object.
     *
     * If the sequence contains multiple elements equal to the specified object,
     * the first occurence in the sequence will be returned.
     *
     * The method nextIndexOf can be used in consecutive calls to iterate
     * through all occurences of a specified object.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise -1.
     */
    public static<T> int indexByIdentity(Sequence<? extends T> seq, T key) {
        return nextIndexByIdentity(seq, key, 0);
    }

    /**
     * Searches the specified sequence for an object with the same value. The
     * objects are compared using the method equals(). If the sequence is sorted,
     * binarySearch should be used instead.
     *
     * If the sequence contains multiple elements equal to the specified object,
     * the first occurence in the sequence will be returned.
     *
     * The method nextIndexOf can be used in consecutive calls to iterate
     * through all occurences of a specified object.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise -1.
     */
    public static<T> int indexOf(Sequence<? extends T> seq, T key) {
        return nextIndexOf(seq, key, 0);
    }

    /**
     * Returns the element with the maximum value in the specified sequence,
     * according to the natural ordering  of its elements. All elements in the
     * sequence must implement the Comparable interface. Furthermore, all
     * elements in the sequence must be mutually comparable (that is,
     * e1.compareTo(e2) must not throw a ClassCastException  for any elements
     * e1 and e2 in the sequence).
     *
     * If the sequence contains multiple elements with the maximum value,
     * there is no guarantee which one will be found.
     *
     * @param seq The sequence to be searched.
     * @return The element with the maximum value.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T max (Sequence<T> seq) {
        if (seq == null || seq.isEmpty())
            throw new IllegalArgumentException("empty sequence passed to Sequences.max");

        T result = seq.get(0);
        for (T val : seq) {
            if (result.compareTo(val) < 0) {
                result = val;
            }
        }
        return result;
    }

    /**
     * Returns the element with the maximum value in the specified sequence,
     * according to the specified comparator. All elements in the sequence must
     * be mutually comparable by the specified comparator (that is,
     * c.compare(e1, e2) must not throw a ClassCastException  for any elements
     * e1 and e2 in the sequence).
     *
     * If the sequence contains multiple elements with the maximum value,
     * there is no guarantee which one will be found.
     *
     * @param seq The sequence to be searched.
     * @param c The comparator to determine the order of the sequence.
     *          A null value indicates that the elements' natural ordering
     *          should be used.
     * @return The element with the maximum value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T max (Sequence<T> seq, Comparator<? super T> c) {
        if (seq == null || seq.isEmpty())
            throw new IllegalArgumentException("empty sequence passed to Sequences.max");
        if (c == null)
            return (T)max((Sequence<Comparable>)seq);

        T result = seq.get(0);
        for (T val : seq) {
            if (c.compare(result, val) < 0) {
                result = val;
            }
        }
        return result;
    }

    /**
     * Returns the element with the minimum value in the specified sequence,
     * according to the natural ordering  of its elements. All elements in the
     * sequence must implement the Comparable interface. Furthermore, all
     * elements in the sequence must be mutually comparable (that is,
     * e1.compareTo(e2) must not throw a ClassCastException  for any elements
     * e1 and e2 in the sequence).
     *
     * If the sequence contains multiple elements with the minimum value,
     * there is no guarantee which one will be found.
     *
     * @param seq The sequence to be searched.
     * @return The element with the maximum value.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T min (Sequence<T> seq) {
        if (seq == null || seq.isEmpty())
            throw new IllegalArgumentException("empty sequence passed to Sequences.min");

        T result = seq.get(0);
        for (T val : seq) {
            if (result.compareTo(val) > 0) {
                result = val;
            }
        }
        return result;
    }

    /**
     * Returns the element with the minimum value in the specified sequence,
     * according to the specified comparator. All elements in the sequence must
     * be mutually comparable by the specified comparator (that is,
     * c.compare(e1, e2) must not throw a ClassCastException  for any elements
     * e1 and e2 in the sequence).
     *
     * If the sequence contains multiple elements with the minimum value,
     * there is no guarantee which one will be found.
     *
     * @param seq The sequence to be searched.
     * @param c The comparator to determine the order of the sequence.
     *          A null value indicates that the elements' natural ordering
     *          should be used.
     * @return The element with the minimum value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T min (Sequence<T> seq, Comparator<? super T> c) {
        if (seq == null || seq.isEmpty())
            throw new IllegalArgumentException("empty sequence passed to Sequences.min");
        if (c == null)
            return (T)min((Sequence<Comparable>)seq);

        T result = seq.get(0);
        for (T val : seq) {
            if (c.compare(result, val) > 0)
                result = val;
        }
        return result;
    }

    /**
     * Searches the specified sequence for an object with the same value,
     * starting the search at the specified position. The objects are compared
     * using the method equals().
     *
     * If the sequence contains multiple elements equal to the specified object,
     * the first occurence in the subsequence will be returned.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @param pos The position in the sequence to start the search. If pos is
     *            negative or 0 the whole sequence will be searched.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise -1.
     */
    public static<T> int nextIndexByIdentity(Sequence<? extends T> seq, T key, int pos) {
        if (seq == null)
            return -1;
        if (key == null)
            throw new NullPointerException();

        Iterator<? extends T> it = seq.iterator();
        int i;
        for (i=0; i<pos && it.hasNext(); ++i)
            it.next();
        for (; it.hasNext(); ++i)
            if (it.next() ==  key)
                return i;
        return -1;
    }

    /**
     * Searches the specified sequence for the specified object, starting the
     * search at the specified position.
     *
     * If the sequence contains multiple elements equal to the specified object,
     * the first occurence in the subsequence will be returned.
     *
     * @param seq The sequence to be searched.
     * @param key The value to be searched for.
     * @param pos The position in the sequence to start the search. If pos is
     *            negative or 0 the whole sequence will be searched.
     * @return Index of the search key, if it is contained in the array;
     *         otherwise -1.
     */
    public static<T> int nextIndexOf(Sequence<? extends T> seq, T key, int pos) {
        if (seq == null)
            return -1;
        if (key == null)
            throw new NullPointerException();

        Iterator<? extends T> it = seq.iterator();
        int i;
        for (i=0; i<pos && it.hasNext(); ++i)
            it.next();
        for (; it.hasNext(); ++i)
            if (it.next().equals(key))
                return i;
        return -1;
    }

    /**
     * Sorts the specified sequence of objects into ascending order, according
     * to the natural ordering  of its elements. All elements in the sequence
     * must implement the Comparable interface. Furthermore, all elements in
     * the sequence must be mutually comparable (that is, e1.compareTo(e2)
     * must not throw a ClassCastException  for any elements e1 and e2 in the
     * sequence).
     *
     * This method is immutative, the result is returned in a new sequence,
     * while the original sequence is left untouched.
     *
     * This sort is guaranteed to be stable: equal elements will not be
     * reordered as a result of the sort.
     *
     * The sorting algorithm is a modified mergesort (in which the merge is
     * omitted if the highest element in the low sublist is less than the
     * lowest element in the high sublist). This algorithm offers guaranteed
     * n*log(n) performance.
     *
     * @param seq The sequence to be sorted.
     * @return The sorted sequence.
     */
    public static <T extends Comparable> Sequence<? extends T> sort (Sequence<T> seq) {
        if (seq.isEmpty())
            return seq.getEmptySequence();
        final int length = seq.size();
        T[] array = Util.<T>newComparableArray(length);
        seq.toArray(0, length, array, 0);
        Arrays.sort(array);
        return Sequences.<T>make(seq.getElementType(), array);
    }

    /**
     * Sorts the specified sequence of objects according to the order induced
     * by the specified comparator. All elements in the sequence must be
     * mutually comparable by the specified comparator (that is,
     * c.compare(e1, e2) must not throw a ClassCastException  for any elements
     * e1 and e2 in the sequence).
     *
     * This method is immutative, the result is returned in a new sequence,
     * while the original sequence is left untouched.
     *
     * This sort is guaranteed to be stable: equal elements will not be
     * reordered as a result of the sort.
     *
     * The sorting algorithm is a modified mergesort (in which the merge is
     * omitted if the highest element in the low sublist is less than the
     * lowest element in the high sublist). This algorithm offers guaranteed
     * n*log(n) performance.
     *
     * @param seq The sequence to be sorted.
     * @param c The comparator to determine the order of the sequence.
     *          A null value indicates that the elements' natural ordering
     *          should be used.
     * @return The sorted sequence.
     */
    public static <T> Sequence<? extends T> sort (Sequence<T> seq, Comparator<? super T> c) {
        if (seq.isEmpty())
            return seq.getEmptySequence();
        final int length = seq.size();
        T[] array = Util.<T>newObjectArray(length);
        seq.toArray(0, length, array, 0);
        Arrays.sort(array, c);
        return Sequences.<T>make(seq.getElementType(), array);
    }

    /** Returns a new sequence containing the randomly shuffled
     * contents of the existing sequence
     * */
    public static <T> Sequence<T> shuffle (Sequence<T> seq) {
        T[] array = toArray(seq);
        List<? extends T> list = Arrays.asList(array);
        Collections.shuffle(list);
        return Sequences.make(seq.getElementType(), list);
    }

    public static <T> T getFromNewElements(F3Object instance, int varNum, int loIndex, int inserted, int k) {
        if (k >= inserted)
            k = -1;
        else if (k >= 0)
            k += loIndex;
        return (T) instance.elem$(varNum, k);
    }

    public static <T> Sequence<? extends T> getNewElements(Sequence<? extends T> current, int startPos, int inserted) {
         return Sequences.subsequence(current, startPos, startPos+inserted);
    }

    public static <T> Sequence<? extends T> replaceSlice(Sequence<? extends T> oldValue, T newValue, int startPos, int endPos/*exclusive*/) {
        if (preReplaceSlice(oldValue, newValue, startPos, endPos)) {
            return replaceSliceInternal(oldValue, newValue, startPos, endPos, false);
        }
        else
            return oldValue;
    }
    //where
    private static <T> boolean preReplaceSlice(Sequence<? extends T> oldValue, T newValue, int startPos, int endPos/*exclusive*/) {
        if (newValue == null)
            return preReplaceSlice(oldValue, (Sequence<? extends T>) null, startPos, endPos);
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;
        if (endPos == startPos+1 && newValue.equals(oldValue.get(startPos))) {
            // FIXME set valid??
            return false;
        }
        else {
            return true;
        }
    }
    //where
    private static <T> Sequence<? extends T> replaceSliceInternal(Sequence<? extends T> oldValue, T newValue, int startPos, int endPos/*exclusive*/, boolean hasTrigger) {
        if (newValue == null)
            return replaceSliceInternal(oldValue, (Sequence<? extends T>) null, startPos, endPos, hasTrigger);
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;
        ObjectArraySequence<T> arr = forceNonSharedObjectArraySequence((TypeInfo<T>) oldValue.getElementType(), oldValue);
        arr.replace(startPos, endPos, (T) newValue, hasTrigger);
        if (hasTrigger)
            arr.clearOldValues(endPos-startPos);
        return arr;
    }

    public static <T> void replaceSlice(F3Object instance, int varNum, T newValue, int startPos, int endPos/*exclusive*/) {
        boolean wasUninitialized =
                instance.varTestBits$(varNum, F3Object.VFLGS$INITIALIZED_STATE_BIT, 0);
        instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
        if (instance.varTestBits$(varNum, F3Object.VFLGS$IS_BOUND_READONLY, F3Object.VFLGS$IS_BOUND_READONLY)) {
            throw new AssignToBoundException("Cannot mutate bound sequence");
        }
        Sequence<? extends T> oldValue = (Sequence<? extends T>) instance.get$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
            oldValue = (Sequence<? extends T>) instance.get$(varNum);
        }
        if (preReplaceSlice(oldValue, newValue, startPos, endPos) ||
                wasUninitialized) {
            int newLength = newValue==null?0:1;
            instance.invalidate$(varNum, startPos, endPos, newLength, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
            Sequence<? extends T> arr = replaceSliceInternal(oldValue, newValue, startPos, endPos, true);
            instance.seq$(varNum, arr);
            instance.invalidate$(varNum, startPos, endPos, newLength,  F3Object.PHASE_TRANS$CASCADE_TRIGGER);
        }
    }

    public static <T> Sequence<? extends T> replaceSlice(Sequence<? extends T> oldValue, Sequence<? extends T> newValues, int startPos, int endPos/*exclusive*/) {
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;
        /*
        // Calling sliceEqual is probably not worth it,
        // at least if oldValue is non-shared, and we can just copy.
        // sliceEqual is especially bad if it needs to do boxing.
        if (newValues == null ? startPos == endPos
            : sliceEqual(oldValue, startPos, endPos, newValues)) {
            // FIXME set valid??
            return oldValue;
        }
        */
        int inserted = newValues==null? 0 : newValues.size();
        // If we are replacing it all, and, since we don't want copies of SequenceRef, if it isn't a SequenceRef
        if (startPos == 0 && endPos == oldSize && !(newValues instanceof SequenceRef) && !(newValues instanceof SequenceProxy)) {
            if (newValues == null)
                newValues = oldValue.getEmptySequence();
            newValues.incrementSharing();
            return newValues;
        }
        ArraySequence<T> arr = forceNonSharedArraySequence((TypeInfo<T>) oldValue.getElementType(), oldValue);
        arr.replace(startPos, endPos, newValues, 0, inserted, false);
        return arr;
    }
    //where
    private static <T> boolean preReplaceSlice(Sequence<? extends T> oldValue, Sequence<? extends T> newValues, int startPos, int endPos/*exclusive*/) {
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;
        if (newValues == null ? startPos == endPos
            : sliceEqual(oldValue, startPos, endPos, newValues)) {
            // FIXME set valid??
            return false;
        }
        else {
            return true;
        }
    }
    //where
    private static <T> Sequence<? extends T> replaceSliceInternal(Sequence<? extends T> oldValue, Sequence<? extends T> newValues, int startPos, int endPos/*exclusive*/, boolean hasTrigger) {
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;

        int inserted = newValues==null? 0 : newValues.size();
        // If we are replacing it all, and, since we don't want copies of SequenceRef, if it isn't a SequenceRef
        if (startPos == 0 && endPos == oldSize && !(newValues instanceof SequenceRef) && !(newValues instanceof SequenceProxy)) {

            if (newValues == null)
                newValues = oldValue.getEmptySequence();
            newValues.incrementSharing();
            return newValues;
        }
        ArraySequence<T> arr = forceNonSharedArraySequence((TypeInfo<T>) oldValue.getElementType(), oldValue);
        arr.replace(startPos, endPos, newValues, 0, inserted, hasTrigger);
        if (hasTrigger)
            arr.clearOldValues(endPos-startPos);
        return arr;
    }

    public static <T> void replaceSlice(F3Object instance, int varNum, Sequence<? extends T> newValues, int startPos, int endPos/*exclusive*/) {
        boolean wasUninitialized = 
                instance.varTestBits$(varNum, F3Object.VFLGS$INITIALIZED_STATE_BIT, 0);
        instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
        if (instance.varTestBits$(varNum, F3Object.VFLGS$IS_BOUND_READONLY, F3Object.VFLGS$IS_BOUND_READONLY)) {
            throw new AssignToBoundException("Cannot mutate bound sequence");
        }
        Sequence<? extends T> oldValue = (Sequence<? extends T>) instance.get$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
            oldValue = (Sequence<? extends T>) instance.get$(varNum);
        }
        if (preReplaceSlice(oldValue, newValues, startPos, endPos) ||
                wasUninitialized) {
            int newLength = newValues == null ? 0 : newValues.size();
            instance.invalidate$(varNum, startPos, endPos, newLength, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
            Sequence<? extends T> arr = replaceSliceInternal(oldValue, newValues, startPos, endPos, true);
            instance.seq$(varNum, arr);
            instance.invalidate$(varNum, startPos, endPos, newLength,  F3Object.PHASE_TRANS$CASCADE_TRIGGER);
        }
    }

    public static <T> Sequence<? extends T> set(Sequence<? extends T> oldValue, Sequence<? extends T> newValue) {
        if (newValue instanceof SequenceRef || newValue instanceof SequenceProxy) {
            // Can't have any copies of a SequenceRef
            return replaceSlice(oldValue, newValue, 0, oldValue.size());
        }
        newValue.incrementSharing();
        return newValue;
    }

    public static <T> Sequence<? extends T> set(F3Object instance, int varNum, Sequence<? extends T> newValue) { 
        //TODO: should give slice invalidations, as if below, but should actually set to the new sequence
        replaceSlice(instance, varNum, newValue, 0, instance.size$(varNum));
        return newValue;
    }

    public static <T> Sequence<? extends T> set(Sequence<? extends T> oldValue, T newValue, int index) {
        return replaceSlice(oldValue, newValue, index, index + 1);
    }

    public static <T> T set(F3Object instance, int varNum, T newValue, int index) {
        replaceSlice(instance, varNum, newValue, index, index + 1);
        return newValue;
    }

    public static <T> ArraySequence<? extends T> copy(Sequence<? extends T> oldValue) {
        ArraySequence<T> arr =  ((TypeInfo<T>) oldValue.getElementType()).emptySequence.makeNew(0);
        arr.add(oldValue);
        arr.incrementSharing();
        return arr;
    }

    public static <T> Sequence<? extends T> insert(Sequence<? extends T> oldValue, T newValue) {
        if (newValue == null)
            return oldValue;
        int oldSize = oldValue.size();
        ObjectArraySequence<T> arr = forceNonSharedObjectArraySequence((TypeInfo<T>) oldValue.getElementType(), oldValue);
        arr.replace(oldSize, oldSize, (T) newValue, true);
        return arr;
    }

    public static <T> void insert(F3Object instance, int varNum, T newValue) {
        if (newValue == null)
            return;
        instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
        Sequence<? extends T> oldValue = (Sequence<? extends T>) instance.get$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
            oldValue = (Sequence<? extends T>) instance.get$(varNum);
        }
        int oldSize = oldValue.size();
        int newLength = newValue==null?0:1;
        Sequence<? extends T> arr = insert(oldValue, newValue);
        instance.invalidate$(varNum, oldSize, oldSize, newLength, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        instance.seq$(varNum, arr);
        instance.invalidate$(varNum, oldSize, oldSize, newLength,  F3Object.PHASE_TRANS$CASCADE_TRIGGER);
    }

    public static <T> Sequence<? extends T> insert(Sequence<? extends T> oldValue, Sequence<? extends T> values) {
        int inserted = values.size();
        if (inserted == 0)
            return oldValue;
        int oldSize = oldValue.size();
        ArraySequence<T> arr = forceNonSharedArraySequence((TypeInfo<T>) oldValue.getElementType(), oldValue);
        arr.replace(oldSize, oldSize, values, 0, inserted, true);
        return arr;
    }

    public static <T> void insert(F3Object instance, int varNum, Sequence<? extends T> values) {
        int inserted = values.size();
        if (inserted == 0)
            return;
        instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
        Sequence<? extends T> oldValue = (Sequence<? extends T>) instance.get$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            instance.varChangeBits$(varNum, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT);
            oldValue = (Sequence<? extends T>) instance.get$(varNum);
        }
        int oldSize = oldValue.size();
        int newLength = values == null ? 0 : values.size();
        Sequence<? extends T> arr = insert(oldValue, values);
        instance.invalidate$(varNum, oldSize, oldSize, newLength, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        instance.seq$(varNum, arr);
        instance.invalidate$(varNum, oldSize, oldSize, newLength,  F3Object.PHASE_TRANS$CASCADE_TRIGGER);

    }

    public static <T> void insertBefore(F3Object instance, int varNum, T value, int position) {
        replaceSlice(instance, varNum, value, position, position);
    }

    public static <T> void insertBefore(F3Object instance, int varNum, Sequence<? extends T> values, int position) {
        replaceSlice(instance, varNum, values, position, position);
    }

    public static <T> Sequence<? extends T> insertBefore(Sequence<? extends T> oldValue, T value, int position) {
        return replaceSlice(oldValue, value, position, position);
    }

    public static <T> Sequence<? extends T> insertBefore(Sequence<? extends T> oldValue, Sequence<? extends T> values, int position) {
        return replaceSlice(oldValue, values, position, position);
    }

    public static <T> void deleteIndexed(F3Object instance, int varNum, int position) {
        replaceSlice(instance, varNum, (Sequence<? extends T>)null, position, position+1);
    }

    public static <T> Sequence<? extends T> deleteIndexed(Sequence<? extends T> oldValue, int position) {
        return replaceSlice(oldValue, (Sequence<? extends T>)null, position, position+1);
    }

    public static <T> void deleteSlice(F3Object instance, int varNum, int begin, int end) {
        replaceSlice(instance, varNum, (Sequence<? extends T>)null, begin, end);
    }

    public static <T> Sequence<? extends T> deleteSlice(Sequence<? extends T> oldValue, int begin, int end) {
        return replaceSlice(oldValue, (Sequence<? extends T>)null, begin, end);
    }

    public static <T> void deleteValue(F3Object instance, int varNum, T value) {
        if (instance.varTestBits$(varNum, F3Object.VFLGS$IS_BOUND_READONLY, F3Object.VFLGS$IS_BOUND_READONLY)) {
            throw new AssignToBoundException("Cannot mutate bound sequence");
        }
        Sequence<? extends T> oldValue = (Sequence<? extends T>) instance.get$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            oldValue = (Sequence<? extends T>) instance.get$(varNum);
        }
        // It's tempting to just do:
        //   Sequence<? extends T> arr = deleteValue(oldValue, value);
        //   instance.seq$(varNum, arr);
        // However, in that case triggers won't run properly.
        int hi = -1;
        for (int i = oldValue.size();  ; ) {
            boolean matches = --i < 0 ? false : oldValue.get(i).equals(value);
            if (matches) {
                if (hi < 0)
                    hi = i;
            }
            else if (hi >= 0) {
                deleteSlice(instance, varNum, i+1, hi+1);
                // The following may be redundant - but just in case:
                oldValue = (Sequence<? extends T>) instance.get$(varNum);
                hi = -1;
            }
            if (i < 0) break;
        }
    }

    public static <T> Sequence<? extends T> deleteValue(Sequence<? extends T> oldValue, T value) {
        int hi = -1;
        for (int i = oldValue.size();  ; ) {
            boolean matches = --i < 0 ? false : oldValue.get(i).equals(value);
            if (matches) {
                if (hi < 0)
                    hi = i;
            }
            else if (hi >= 0) {
                oldValue = deleteSlice(oldValue, i+1, hi+1);
                hi = -1;
            }
            if (i < 0) break;
        }
        return oldValue;
    }

    public static <T> Sequence<? extends T> deleteAll(Sequence<? extends T> oldValue) {
        return oldValue.getEmptySequence();
    }

    public static <T> void deleteAll(F3Object instance, int varNum) {
        int oldSize = instance.size$(varNum);
        replaceSlice(instance, varNum, (Sequence<? extends T>)null, 0, oldSize);
    }

    public static boolean withinBounds(F3Object obj, int varNum, int position) {
        return (position >= 0 && position < obj.size$(varNum));
    }
}
