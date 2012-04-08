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
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * This wrapper class allows the client to control which events are suppressed and which are passed to the client while
 * an internal invokeMethod is in progress.  Internal invokeMethods are used by get/setValue(s) methods in F3ReferenceType, 
 * F3ClassType, and F3ObjectReference if the field involved has a getter/setter.
 *
 * @author sundar
 */
public class F3EventQueue extends F3Mirror implements EventQueue {

    // Note that this class is not used for the internal EventQueue - 
    // that is not exposed in the F3-JDI layer


    // In normal operation, events are not controlled, ie, they are delivered
    // to the caller in normal JDI fashion. 
    // However, in some cases, NetBeans needs events to be 'controlled'.  In these
    // cases, some events are passed thru in normal fashion and the remaining events are
    // skipped - their eventSets are just resumed without reporting to the clent.

    // This flag determines if event control is in effect or not.
    private volatile boolean eventControl = false;

    // This flag determines if the calls to change eventControl should be ignored or not.
    // The idea is that these calls are in F3-JDI, but by default they are disabled, thus
    // there is no event control.  The debugger can set this flag to true to allow the
    // calls in F3-JDI to turn event control on / off.
    private volatile boolean allowEventControl = false;

    // If allowEventControl is true, this is the list of Events to pass.  EventSets containing
    // other events are just resumed and discarded.
    private List<Class> eventsToBePassed = new ArrayList<Class>();
    
    // Filter for EventSets that should be passed thru in controlled mode.
    private boolean shouldPassEventSet(F3EventSet eventSet) {
        if (eventsToBePassed == null) {
            return true;
        }
        F3Event newEvt = eventSet.eventIterator().next();
        for (Class evtClass: eventsToBePassed) {
            if (evtClass.isInstance(newEvt)) {
                return true;
            }
        }
        return false;
    }


    /**
     * JDI addition:  This isn't intended to be called by a client.  This is
     * called with a value of true before internal calls to invokeMethod and 
     * is called with a value of false after these calls.  This causes some
     * events to not be generated during these calls; see {@link #setEventsToBePassed(List)}.
     * @param value true means to suppress selected events while false means to not
     * suppress events.
     */
    public void setEventControl(boolean value) {
        if (allowEventControl) {
            eventControl = value;
        }
    }

    // This can be called by the debugger to disable
    // the setEventControl calls in f3jdi.  EG, if the debugger
    // does not want events to be 'controlled' during the execution
    // of internal invokeMethod calls, it can pass false to this.
    /**
     * JDI addition: Allow/disallow event suppression via {@link #setEventControl(boolean)}.
     * This can be called by the debugger to cause events to not be suppressed during
     * internal invokeMethod calls caused by calls to get/setValue(s).
     * @param value true means to to allow events to be supressed while false means
     * to ignore calls to {@link #setEventControl(boolean)}
     */
    public void setAllowEventControl(boolean value) {
        allowEventControl = value;
        if (!allowEventControl) {
            eventControl = false;
        }
    }

    /**
     * JDI Addition:  Specify Events to be passed through while event control is enabled.
     * If passThese is null, then all events are passed.  
     * By default, these events are passed through when event control is enabled:
     *    ClassPrepareEvent, VMDeathEvent, VMDisconnectEvent, VMStartEvent
     *
     * BreakPointEvent, StepEvent, MethodEntryEvent are all handled the same, E.G.
     * if any of these is on the passThese list, then all three are passed.
     * @param passThese the list of events to pass on to the client instead of suppressing.
     */
    public void setEventsToBePassed(List<Class> passThese) {
        if (passThese == null) {
            eventsToBePassed = null;
            return;
        }
        List<Class> newList = new ArrayList<Class>(passThese.size() + 2);
        boolean tripleSeen = false;
        for (Class evtClass: passThese) {
            if (evtClass.isInstance(F3BreakpointEvent.class) ||
                evtClass.isInstance(F3StepEvent.class) ||
                evtClass.isInstance(F3MethodEntryEvent.class)) {
                if (!tripleSeen) {
                    newList.add(F3BreakpointEvent.class);
                    newList.add(F3StepEvent.class);
                    newList.add(F3MethodEntryEvent.class);
                    tripleSeen = true;
                }
            } else {
                newList.add(evtClass);
            }
        }
        eventsToBePassed = Collections.unmodifiableList(newList);
    }

    /**
     * JDI Addition: Return a list of events that will be passed to the client while event 
     * control is enabled.
     * @return a list of events that will be passed to the client while event control is enabled.
     */
    public List<Class> getEventsToBePassed() {
        return eventsToBePassed;
    }

    public F3EventQueue(F3VirtualMachine f3vm, EventQueue underlying) {
        super(f3vm, underlying);
        
        eventsToBePassed.add(F3ClassPrepareEvent.class);
        eventsToBePassed.add(F3VMDeathEvent.class);
        eventsToBePassed.add(F3VMDisconnectEvent.class);
        eventsToBePassed.add(F3VMStartEvent.class);
    }

    public F3EventSet remove() throws InterruptedException {
        return remove(0);
    }

    public F3EventSet remove(long arg0) throws InterruptedException {
        F3EventSet eventSet;
        while(true) {
            // we are normally waiting under here when eventControl changes
            eventSet = F3EventSet.wrap(virtualMachine(), underlying().remove(arg0));
            if (!eventControl) {
                // eventSet will be null if it timed out.
                return eventSet;
            }
            if (eventSet == null) {
                // timed out, and eventControl is true
                return null;
            }
            if (shouldPassEventSet(eventSet)) {
                return eventSet;
            }
            eventSet.resume();
        }
    }

    @Override
    protected EventQueue underlying() {
        return (EventQueue) super.underlying();
    }
}
