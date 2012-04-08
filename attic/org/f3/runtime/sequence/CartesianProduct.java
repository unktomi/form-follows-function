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
 * Special case for n-dimensional foreach comprehension when there are no where clauses on any list and
 * the foreach body always returns a single instance. The results are computed as needed rather than
 * precomputed, to save space.
 *
 * @author Brian Goetz
 */
public class CartesianProduct<T> extends AbstractSequence<T> implements Sequence<T> {

    public interface Mapper<T> {
        public T map(int[] indexes, Object[] values);
    }

    private final Sequence<?>[] sequences;
    private final Mapper<T> mapper;
    private final int size;
    private final int depth;
    private final int[] sizes;

    public CartesianProduct(TypeInfo<T, ?> ti, Mapper<T> mapper, Sequence<?>... sequences) {
        super(ti);
        this.sequences = sequences;
        this.mapper = mapper;
        if (sequences.length == 0) {
            size = 0;
            depth = 0;
        } else {
            int tmpSize = 1;
            int tmpDepth = 0;
            for (Sequence<?> seq : sequences) {
                tmpSize = tmpSize * seq.size();
                tmpDepth = Math.max(tmpDepth, seq.getDepth());
            }
            size = tmpSize;
            depth = tmpDepth + 1;
        }
        sizes = new int[sequences.length];
        for (int i=0; i<sequences.length; i++) {
            int cur = 1;
            for (int j=i+1; j<sequences.length; j++)
                cur *= sequences[j].size();
            sizes[i] = cur;
        }
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public int size() {
        return size;
    }

    public T get(int position) {
        int[] indices = new int[sequences.length];
        Object[] values = new Object[sequences.length];
        int last = sequences.length-1;
        for (int i=0; i<last; i++) {
            indices[i] = position / sizes[i];
            values[i] = sequences[i].get(indices[i]);
            position -= indices[i]*sizes[i];
        }
        indices[last] = position;
        values[last] = sequences[last].get(indices[last]);
        return mapper.map(indices, values);
    }
}
