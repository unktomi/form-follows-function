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

import org.f3.jdi.F3Method;
import org.f3.jdi.F3VirtualMachine;
import org.f3.jdi.F3Wrapper;
import com.sun.jdi.event.MethodEntryEvent;

/**
 *
 * @author sundar
 */
public class F3MethodEntryEvent extends F3LocatableEvent implements MethodEntryEvent {
    public F3MethodEntryEvent(F3VirtualMachine f3vm, MethodEntryEvent underlying) {
        super(f3vm, underlying);
    }

    public F3Method method() {
        return F3Wrapper.wrap(virtualMachine(), underlying().method());
    }

    @Override
    protected MethodEntryEvent underlying() {
        return (MethodEntryEvent) super.underlying();
    }
}
