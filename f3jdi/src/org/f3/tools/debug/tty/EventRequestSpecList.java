/*
 * Copyright 1998-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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

import com.sun.jdi.request.EventRequest;
import com.sun.jdi.event.ClassPrepareEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class EventRequestSpecList {

    private static final int statusResolved = 1;
    private static final int statusUnresolved = 2;
    private static final int statusError = 3;

    // all specs
    private final List<EventRequestSpec> eventRequestSpecs = Collections.synchronizedList(
                                                  new ArrayList<EventRequestSpec>());
    private final Env env;

    EventRequestSpecList(Env env) {
        this.env = env;
    }

    /**
     * Resolve all deferred eventRequests waiting for 'refType'.
     * @return true if it completes successfully, false on error.
     */
    boolean resolve(ClassPrepareEvent event) {
        boolean failure = false;
        synchronized(eventRequestSpecs) {
            for (EventRequestSpec spec : eventRequestSpecs) {
                if (!spec.isResolved()) {
                    try {
                        EventRequest eventRequest = spec.resolve(event);
                        if (eventRequest != null) {
                            env.messageOutput().println("Set deferred", spec.toString());
                        }
                    } catch (Exception e) {
                        env.messageOutput().println("Unable to set deferred",
                                              new Object [] {spec.toString(),
                                                             spec.errorMessageFor(e)});
                        failure = true;
                    }
                }
            }
        }
        return !failure;
    }

    void resolveAll() {
        for (EventRequestSpec spec : eventRequestSpecs) {
            try {
                EventRequest eventRequest = spec.resolveEagerly();
                if (eventRequest != null) {
                    env.messageOutput().println("Set deferred", spec.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    boolean addEagerlyResolve(EventRequestSpec spec) {
        try {
            eventRequestSpecs.add(spec);
            EventRequest eventRequest = spec.resolveEagerly();
            if (eventRequest != null) {
                env.messageOutput().println("Set", spec.toString());
            }
            return true;
        } catch (Exception exc) {
            env.messageOutput().println("Unable to set",
                                  new Object [] {spec.toString(),
                                                 spec.errorMessageFor(exc)});
            return false;
        }
    }

    BreakpointSpec createBreakpoint(String classPattern, int line)
        throws ClassNotFoundException {
        ReferenceTypeSpec refSpec =
            new PatternReferenceTypeSpec(env, classPattern);
        return new BreakpointSpec(env, refSpec, line);
    }

    BreakpointSpec createBreakpoint(String classPattern,
                                 String methodId,
                                    List<String> methodArgs)
                                throws MalformedMemberNameException,
                                       ClassNotFoundException {
        ReferenceTypeSpec refSpec =
            new PatternReferenceTypeSpec(env, classPattern);
        return new BreakpointSpec(env, refSpec, methodId, methodArgs);
    }

    EventRequestSpec createExceptionCatch(String classPattern,
                                          boolean notifyCaught,
                                          boolean notifyUncaught)
                                            throws ClassNotFoundException {
        ReferenceTypeSpec refSpec =
            new PatternReferenceTypeSpec(env, classPattern);
        return new ExceptionSpec(env, refSpec, notifyCaught, notifyUncaught);
    }

    WatchpointSpec createAccessWatchpoint(String classPattern,
                                       String fieldId)
                                      throws MalformedMemberNameException,
                                             ClassNotFoundException {
        ReferenceTypeSpec refSpec =
            new PatternReferenceTypeSpec(env, classPattern);
        return new AccessWatchpointSpec(env, refSpec, fieldId);
    }

    WatchpointSpec createModificationWatchpoint(String classPattern,
                                       String fieldId)
                                      throws MalformedMemberNameException,
                                             ClassNotFoundException {
        ReferenceTypeSpec refSpec =
            new PatternReferenceTypeSpec(env, classPattern);
        return new ModificationWatchpointSpec(env, refSpec, fieldId);
    }

    boolean delete(EventRequestSpec proto) {
        synchronized (eventRequestSpecs) {
            int inx = eventRequestSpecs.indexOf(proto);
            if (inx != -1) {
                EventRequestSpec spec = eventRequestSpecs.get(inx);
                spec.remove();
                eventRequestSpecs.remove(inx);
                return true;
            } else {
                return false;
            }
        }
    }

    List<EventRequestSpec> eventRequestSpecs() {
       // We need to make a copy to avoid synchronization problems
        synchronized (eventRequestSpecs) {
            return new ArrayList<EventRequestSpec>(eventRequestSpecs);
        }
    }
}
