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
 * Provides a view of an underlying sequence by applying a mapping function to each element of the underlying
 * sequence. The mapping is done lazily, on each call to get(), rather than eagerly, so the time and space
 * construction costs of a MapSequence are O(1).  Mapped sequences should be constructed with the factory method
 * Sequences.map(), rather than with the MapSequence constructor.
 *
 * @author Brian Goetz
 */
class MapSequence<T, U> extends AbstractSequence<U> implements Sequence<U> {

    private final Sequence<T> sequence;
    private final SequenceMapper<T, U> mapper;

    public MapSequence(TypeInfo<U, ?> ti, Sequence<T> sequence, SequenceMapper<T, U> mapper) {
        super(ti);
        this.sequence = sequence;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return sequence.size();
    }

    @Override
    public int getDepth() {
        return sequence.getDepth() + 1;
    }

    @Override
    public U get(int position) {
        return mapper.map(sequence, position, sequence.get(position));
    }
}
