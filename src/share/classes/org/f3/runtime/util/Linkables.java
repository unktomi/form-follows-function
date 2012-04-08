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
 * Linkables -- methods for manipulating linked lists.
 *
 * @author Brian Goetz
 */
public abstract class Linkables {

    public static<T extends Linkable<T>> boolean isUnused(T element) {
        return element.getPrev() == null && element.getNext() == null;
    }

    public static<T extends Linkable<T>> Linkable<T> findLast(Linkable<T> host) {
        Linkable<T> cur = host;
        T next;
        while ((next = cur.getNext()) != null)
            cur = next;
        return cur;
    }

    public static<T extends Linkable<T>> void addAfter(Linkable<T> existing, T element) {
        /*assert(existing.getNext() == null);
        assert(element.getPrev() == null);
        assert(element.getNext() == null);*/
        T next = existing.getNext();
        existing.setNext(element);
        element.setPrev(existing);
        element.setNext(next);
        if (next != null)
            next.setPrev(element);
    }

    public static<T extends Linkable<T>> void addAtEnd(Linkable<T> host, T element) {
        addAfter(findLast(host), element);
    }

    public static<T extends Linkable<T>> boolean remove(T element) {
        T next = element.getNext();
        Linkable<T> prev = element.getPrev();
        if (next == null && prev == null)
            return false;
        else {
            if (prev != null)
                prev.setNext(next);
            if (next != null)
                next.setPrev(prev);
            element.setNext(null);
            element.setPrev(null);
            return true;
        }
    }

    public static<T extends Linkable<T>> int size(Linkable<T> first) {
        int size = 0;
        for (Linkable<T> cur = first; cur != null; cur = cur.getNext())
            ++size;
        return size;
    }

}
