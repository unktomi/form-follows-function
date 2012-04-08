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
 * Special case for two-dimensional foreach comprehension when there are no where clauses on either list and
 * the foreach body always returns a single instance. The results are computed as needed rather than
 * precomputed, to save space.
 * 
 * @author Brian Goetz
 */
public class CartesianProduct2D<T, U, V> extends AbstractSequence<T> implements Sequence<T> {

    public interface Mapper<T, U, V> {
        public T map(int index1, U value1, int index2, V value2);
    }

    private final Sequence<U> seq1;
    private final Sequence<V> seq2;
    private final Mapper<T, U, V> mapper;

    public CartesianProduct2D(TypeInfo<T, ?> ti, Sequence<U> seq1, Sequence<V> seq2, Mapper<T, U, V> mapper) {
        super(ti);
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.mapper = mapper;
    }

    @Override
    public int getDepth() {
        return Math.max(seq1.getDepth(), seq2.getDepth()) + 1;
    }

    public int size() {
        return seq1.size() * seq2.size();
    }

    public T get(int position) {
        int index1 = position / seq2.size();
        int index2 = position % seq2.size();
        return mapper.map(index1, seq1.get(index1), index2, seq2.get(index2));
    }
}
