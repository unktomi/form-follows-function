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

import org.f3.jdi.F3Mirror;
import org.f3.jdi.F3VirtualMachine;
import org.f3.jdi.request.F3EventRequest;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.MonitorContendedEnterEvent;
import com.sun.jdi.event.MonitorContendedEnteredEvent;
import com.sun.jdi.event.MonitorWaitEvent;
import com.sun.jdi.event.MonitorWaitedEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author sundar
 */
public class F3Event extends F3Mirror implements Event {
    public F3Event(F3VirtualMachine f3vm, Event underlying) {
        super(f3vm, underlying);
    }

    public F3EventRequest request() {
        return F3EventRequest.wrap(virtualMachine(), underlying().request());
    }

    @Override
    protected Event underlying() {
        return (Event) super.underlying();
    }

    public static Event unwrap(Event evt) {
        return (evt instanceof F3Event)? ((F3Event)evt).underlying() : evt;
    }

    public static F3Event wrap(F3VirtualMachine f3vm, Event evt) {
        if (evt == null) {
            return null;
        }
        if (evt instanceof AccessWatchpointEvent) {
            return new F3AccessWatchpointEvent(f3vm, (AccessWatchpointEvent)evt);
        } else if (evt instanceof BreakpointEvent) {
            return new F3BreakpointEvent(f3vm, (BreakpointEvent)evt);
        } else if (evt instanceof ClassPrepareEvent) {
            return new F3ClassPrepareEvent(f3vm, (ClassPrepareEvent)evt);
        } else if (evt instanceof ClassUnloadEvent) {
            return new F3ClassUnloadEvent(f3vm, (ClassUnloadEvent)evt);
        } else if (evt instanceof ExceptionEvent) {
            return new F3ExceptionEvent(f3vm, (ExceptionEvent)evt);
        } else if (evt instanceof MethodEntryEvent) {
            return new F3MethodEntryEvent(f3vm, (MethodEntryEvent)evt);
        } else if (evt instanceof MethodExitEvent) {
            return new F3MethodExitEvent(f3vm, (MethodExitEvent)evt);
        } else if (evt instanceof ModificationWatchpointEvent) {
            return new F3ModificationWatchpointEvent(f3vm, (ModificationWatchpointEvent)evt);
        } else if (evt instanceof MonitorContendedEnterEvent) {
            return new F3MonitorContendedEnterEvent(f3vm, (MonitorContendedEnterEvent)evt);
        } else if (evt instanceof MonitorContendedEnteredEvent) {
            return new F3MonitorContendedEnteredEvent(f3vm, (MonitorContendedEnteredEvent)evt);
        } else if (evt instanceof MonitorWaitEvent) {
            return new F3MonitorWaitEvent(f3vm, (MonitorWaitEvent)evt);
        } else if (evt instanceof MonitorWaitedEvent) {
            return new F3MonitorWaitedEvent(f3vm, (MonitorWaitedEvent)evt);
        } else if (evt instanceof StepEvent) {
            return new F3StepEvent(f3vm, (StepEvent)evt);
        } else if (evt instanceof ThreadDeathEvent) {
            return new F3ThreadDeathEvent(f3vm, (ThreadDeathEvent)evt);
        } else if (evt instanceof ThreadStartEvent) {
            return new F3ThreadStartEvent(f3vm, (ThreadStartEvent)evt);
        } else if (evt instanceof VMDeathEvent) {
            return new F3VMDeathEvent(f3vm, (VMDeathEvent)evt);
        } else if (evt instanceof VMDisconnectEvent) {
            return new F3VMDisconnectEvent(f3vm, (VMDisconnectEvent)evt);
        } else if (evt instanceof VMStartEvent) {
            return new F3VMStartEvent(f3vm, (VMStartEvent)evt);
        } else if (evt instanceof WatchpointEvent) {
            return new F3WatchpointEvent(f3vm, (WatchpointEvent)evt);
        } else if (evt instanceof LocatableEvent) {
            return new F3LocatableEvent(f3vm, (LocatableEvent)evt);
        } else {
            return new F3Event(f3vm, evt);
        }
    }

    public static Collection unwrapEvents(Collection events) {
        if (events == null) {
            return null;
        }
        ArrayList<Object> result = new ArrayList<Object>();
        for (Object obj : events) {
            result.add((obj instanceof Event)? unwrap((Event)obj) : obj);
        }
        return result;
    }
}
