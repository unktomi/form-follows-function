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

package org.f3.runtime.util;

/**
 * Linkable represents a class that can be linked together with other objects of its kind into a linked list.  There
 * is a link to the next element, and a link back to the previous element, so that elements can be removed.  Usually
 * the list head is not of the same type as the list elements, which is why getPrev() returns Linkable<T> but getNext()
 * returns T; the forward links are used for iteration, the backward links only for removal.
 * 
 * Relevant methods for manipulation, iteration, etc are in Linkables.
 *
 * @author Brian Goetz
 */
public interface Linkable<T extends Linkable<T>> {
    T getNext();
    Linkable<T> getPrev();

    void setNext(T next);
    void setPrev(Linkable<T> prev);

    public interface MutativeIterationClosure<T> {
        /** Returns true if the element should be kept in the list, false otherwise */
        public boolean action(T element);
    }

    public interface IterationClosure<T> {
        public void action(T element);
    }
}
