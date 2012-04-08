/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi.event;

import org.f3.jdi.F3VirtualMachine;
import com.sun.jdi.event.EventIterator;

/**
 *
 * @author sundar
 */
public class F3EventIterator implements EventIterator {
    private final F3VirtualMachine f3vm;
    private final EventIterator underlying;

    public F3EventIterator(F3VirtualMachine f3vm, EventIterator underlying) {
        this.f3vm = f3vm;
        this.underlying = underlying;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof F3EventIterator) {
            obj = ((F3EventIterator)obj).underlying();
        }
        return underlying().equals(obj);
    }

    @Override
    public int hashCode() {
        return underlying().hashCode();
    }

    @Override
    public String toString() {
        return underlying().toString();
    }

    public F3Event nextEvent() {
        return F3Event.wrap(virtualMachine(), underlying().nextEvent());
    }

    public boolean hasNext() {
        return underlying().hasNext();
    }

    public F3Event next() {
        return F3Event.wrap(virtualMachine(), underlying().next());
    }

    public void remove() {
        underlying().remove();
    }

    protected F3VirtualMachine virtualMachine() {
        return f3vm;
    }

    protected EventIterator underlying() {
        return underlying;
    }

    public static F3EventIterator wrap(F3VirtualMachine f3vm, EventIterator evtItr) {
        return (evtItr == null)? null : new F3EventIterator(f3vm, evtItr);
    }
}
