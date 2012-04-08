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
 * Represents a sequence with a single element.  Singleton sequences should be created with the Sequences.singleton()
 * factory, not the SingletonSequence constructor.  O(1) space and time construction costs.
 *
 * @author Brian Goetz
 */
class SingletonSequence<T> extends AbstractSequence<T> {
    private final T value;

    public SingletonSequence(TypeInfo<T> ti, T value) {
        super(ti);
        this.value = value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public T get(int position) {
        if (position == 0)
            return value;
        else
            return getDefaultValue();
    }
}
