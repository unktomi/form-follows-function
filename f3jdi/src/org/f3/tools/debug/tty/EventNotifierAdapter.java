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

package org.f3.tools.debug.tty;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

/**
 *
 * @author sundar
 */
public class EventNotifierAdapter implements EventNotifier {
    private boolean shouldRemoveListener = false;
    public void removeThisListener() {
        shouldRemoveListener = true;
    }

    public boolean shouldRemoveListener() {
        return shouldRemoveListener;
    }

    public void breakpointEvent(BreakpointEvent e) {
    }

    public void classPrepareEvent(ClassPrepareEvent e) {
    }

    public void classUnloadEvent(ClassUnloadEvent e) {
    }

    public void exceptionEvent(ExceptionEvent e) {
    }

    public void fieldWatchEvent(WatchpointEvent e) {
    }

    public void methodEntryEvent(MethodEntryEvent e) {
    }

    public boolean methodExitEvent(MethodExitEvent e) {
        return true;
    }

    public void receivedEvent(Event event) {
    }

    public void stepEvent(StepEvent e) {
    }

    public void threadDeathEvent(ThreadDeathEvent e) {
    }

    public void threadStartEvent(ThreadStartEvent e) {
    }

    public void vmDeathEvent(VMDeathEvent e) {
    }

    public void vmDisconnectEvent(VMDisconnectEvent e) {
    }

    public void vmInterrupted() {
    }

    public void vmStartEvent(VMStartEvent e) {
    }
}
