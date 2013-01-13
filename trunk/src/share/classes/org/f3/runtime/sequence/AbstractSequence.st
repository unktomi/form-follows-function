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

import org.f3.runtime.TypeInfo;
import org.f3.runtime.*;
import org.f3.functions.*;

$TEMPLATE_FROM$

/**
 * Abstract base class for sequence classes.  A subclass need only define the size() and get() methods; subclasses
 * may also want to provide optimized versions of some other methods, such as toArray() or getBits().
 *
 * @author Brian Goetz
 * @author Per Bothner
 */
public abstract class AbstractSequence<T> implements Sequence<T>, Formattable {
    protected TypeInfo<T> ti;

    protected AbstractSequence(TypeInfo<T> ti) {
        this.ti = ti;
    }

    public abstract int size();

    public boolean isNumeric() {
        return ti.isNumeric();
    }

    public abstract T get(int position);

    public boolean getAsBoolean(int position) {
        return ((Boolean) get(position)).booleanValue();
    }

    public char getAsChar(int position) {
        return ((Character) get(position)).charValue();
    }

    public byte getAsByte(int position) {
        return ((Number) get(position)).byteValue();
    }

    public short getAsShort(int position) {
        return ((Number) get(position)).shortValue();
    }

    public int getAsInt(int position) {
        return ((Number) get(position)).intValue();
    }

    public long getAsLong(int position) {
        return ((Number) get(position)).longValue();
    }

    public float getAsFloat(int position) {
        return ((Number) get(position)).floatValue();
    }

    public double getAsDouble(int position) {
        return ((Number) get(position)).doubleValue();
    }

    public Sequence<T> getSlice(int startPos, int endPos) {
        return Sequences.subsequence(this, startPos, endPos);
    }

    public BitSet getBits(SequencePredicate<? super T> predicate) {
        final int length = size();
        BitSet bits = new BitSet(length);
        for (int i = 0; i < length; i++)
            if (predicate.matches(this, i, get(i)))
                bits.set(i);
        return bits;
    }

    public TypeInfo<T> getElementType() {
        return ti;
    }

    public T getDefaultValue() {
        return ti.defaultValue;
    }

    public Sequence<T> getEmptySequence() {
        return ti.emptySequence;
    }

    public boolean isEmpty() {
        return (size() == 0);
    }

    public void toArray(Object[] dest, int destOffset) {
        toArray(0, size(), dest, destOffset);
    }
    
    /** Copy a range of our elements into an array.
     * It is the caller's responsibility to verify that sourceOffset and length are in range.
     * @param sourceOffset First element to copy from.
     * @param length Number of elements to copy.
     * @param dest Target.
     * @param destOffset Start offset in target.
     */
    public void toArray(int sourceOffset, int length, Object[] dest, int destOffset) {
        for (int i = 0;  i < length;  i++)
            dest[i + destOffset] = get(i + sourceOffset);
        /*
        int i=0;
        for (Iterator<T> it = iterator(sourceOffset, sourceOffset+length-1); it.hasNext(); i++) {
            dest[i + destOffset] = it.next();
        }
        */
    }

$PREFIX:{T|
    public void toArray(int sourceOffset, int length, $PRIM_MAP.(T)$[] dest, int destOffset) {
        for (int i = 0;  i < length;  i++)
            dest[i + destOffset] = getAs$T$(i + sourceOffset);
    }
}$

    public Sequence<T> get(SequencePredicate<? super T> predicate) {
        return Sequences.filter(this, getBits(predicate));
    }

    public Iterator<T> iterator() {
        return iterator(0, size()-1);
    }
    
    public final Iterator<T> iterator(final int startPos, final int lastPos) {
        return new Iterator<T>() {
            private int cur = Math.min(Math.max(0, startPos), size());          // 0 <= cur <= size()
            final private int last = Math.min(Math.max(cur, lastPos+1), size()); // cur <= last <= size()

            public boolean hasNext() {
                return cur < last;
            }

            public T next() {
                if (cur >= last)
                    throw new NoSuchElementException();
                else
                    return get(cur++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj instanceof Sequence && Sequences.isEqual(this, (Sequence<T>) obj);
    }

    public Sequence<T> reverse() {
        return Sequences.reverse(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (T val : this) {
            hash = 31 * hash + (val != null ? val.hashCode() : 0);
        }
        return hash;
    }

    public void incrementSharing() { }

    public void decrementSharing() { }

    @Override
    public String toString() {
        if (isEmpty())
            return "[ ]";
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (T val : this) {
            sb.append(val);
            sb.append(", ");
        }
        final int length = sb.length();
        if (length > 1) {
            sb.delete(length-2, length);
        }
        sb.append(" ]");
        return sb.toString();
    }


    // Allow sequences to be formatted - toString() is just for debugging
    // i.e
    // var seq = [1, 2];
    // for (i in seq) "{%d i}"
    // should yield: "12"
    // not: "[1, 2]"
    public void formatTo(Formatter formatter,
                         int flags,
                         int width, 
                         int precision) {
        // TBD handle flags, width, and precision
        for (T val : this) {
            formatter.format("%s", val);
        }
    }

    public T div(Function2<? extends T, ? super T, ? super T> f) {
	return foldLeft(f);
    }

    public T foldLeft(Function2<? extends T, ? super T, ? super T> f) {
	T r = get(0);
	for (int i = 1, len = size(); i < len; i++) {
	    r = f.invoke(r, get(i));
	}
	return r;
    }

    public <S> Sequence<S> mul(Function1<? extends S, ? super T> f) {
	return map(f);
    }

    public <S> S foldLeft(S z, Function2<? extends S, ? super S, ? super T> f) {
	S r = z;
	for (int i = 0, len = size(); i < len; i++) {
	    r = f.invoke(r, get(i));
	}
	return r;
    }

    public <S> S foldRight(S z, Function2<? extends S, ? super T, ? super S> f) {
	S r = z;
	for (int i = size()-1; i > 0; --i) {
	    r = f.invoke(get(i), r);
	}
	return r;
    }

    public <S> S accumulate(S z, Function2<? extends S, ? super S, ? super T> f) {
	S r = z;
	for (int i = 0, len = size(); i < len; i++) {
	    r = f.invoke(r, get(i));
	}
	return r;
    }

    public <S> S accumulateBackwards(S z, 
				     Function2<? extends S, ? super T, ? super S> f) {
	S r = z;
	for (int i = size()-1; i > 0; --i) {
	    r = f.invoke(get(i), r);
	}
	return r;
    }

    public Sequence<T> take(int n) {
	return Sequences.subsequence(this, 0, n);
    }

    public Sequence<T> drop(int n) {
	return Sequences.subsequence(this, n, this.size());
    }

    public Sequence<T> prepend(T x) {
	// fix me !!
	return (Sequence<T>)Sequences.insertBefore(this, x, 0);
    }

    public Sequence<T> append(T x) {
	// fix me !!
	return (Sequence<T>)Sequences.insertBefore(this, x, size());
    }


    @Override
    public <Y> Sequence<Y> map(Function1<? extends Y, ? super T> f) {
	ObjectArraySequence<Y> result = new ObjectArraySequence<Y>(size(), TypeInfo.<Y>getTypeInfo());
	for (int i = 0; i < size(); i++) {
	    Y y = f.invoke(get(i));
	    result.add(y);
	}
	return result;
    }

    public Sequence<T> mplus(Sequence<T> s) {
       if (s.size() == 0) return this;
       if (this.size() == 0) return s;
       ObjectArraySequence<T> result = new ObjectArraySequence<T>(size()+s.size(), TypeInfo.<T>getTypeInfo());
       result.add(this);
       result.add(s);
       return result;
   }

   public Sequence<T> mzero() {
       return TypeInfo.<T>getTypeInfo().emptySequence;
   }

   public <Y> Sequence<Y> coflatmap(Function1<? extends Y, ? super Sequence<T>> f) {
	ObjectArraySequence<Y> result = new ObjectArraySequence<Y>(size(), TypeInfo.<Y>getTypeInfo());
	int len = size();
	for (int i = 0; i < len; i++) {
	    Y y = f.invoke(getSlice(i, len));
	    result.add(y);
	}
	return result;
    }

    public <B> Sequence<B> coextract(B b) {
	return Sequences.singleton(TypeInfo.<B>getTypeInfo(), b);
    }

    public T extract() {
	return get(0);
    }

    public <Y> Sequence<Y> flatmap(Function1<? extends Sequence<Y>, ? super T> f) {
	ObjectArraySequence<Y> result = new ObjectArraySequence<Y>(size(), TypeInfo.<Y>getTypeInfo());
	for (int i = 0; i < size(); i++) {
	    Sequence<Y> ys = f.invoke(get(i));
	    result.add(ys);
	}
	return result;
    }
}
