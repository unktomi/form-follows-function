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

package org.f3.jdi.request;

import org.f3.jdi.F3Mirror;
import org.f3.jdi.F3VirtualMachine;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.MonitorContendedEnterRequest;
import com.sun.jdi.request.MonitorContendedEnteredRequest;
import com.sun.jdi.request.MonitorWaitRequest;
import com.sun.jdi.request.MonitorWaitedRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;
import com.sun.jdi.request.WatchpointRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sundar
 */
public class F3EventRequest extends F3Mirror implements EventRequest {
    public F3EventRequest(F3VirtualMachine f3vm, EventRequest underlying) {
        super(f3vm, underlying);
    }

    public void addCountFilter(int count) {
        underlying().addCountFilter(count);
    }

    public void disable() {
        underlying().disable();
    }

    public void enable() {
        underlying().enable();
    }

    public Object getProperty(Object arg0) {
        return underlying().getProperty(arg0);
    }

    public boolean isEnabled() {
        return underlying().isEnabled();
    }

    public void putProperty(Object arg0, Object arg1) {
        underlying().putProperty(arg0, arg1);
    }

    public void setEnabled(boolean enabled) {
        underlying().setEnabled(enabled);
    }

    public void setSuspendPolicy(int policy) {
        underlying().setSuspendPolicy(policy);
    }

    public int suspendPolicy() {
        return underlying().suspendPolicy();
    }

    @Override
    protected EventRequest underlying() {
        return (EventRequest) super.underlying();
    }

    // static utils for wrapping/unwrapping event request objects

    public static F3EventRequest wrap(F3VirtualMachine f3vm, EventRequest req) {
        if (req == null) {
            return null;
        }

        if (req instanceof AccessWatchpointRequest) {
            return new F3AccessWatchpointRequest(f3vm, (AccessWatchpointRequest)req);
        } else if (req instanceof BreakpointRequest) {
            return new F3BreakpointRequest(f3vm, (BreakpointRequest)req);
        } else if (req instanceof ClassPrepareRequest) {
            return new F3ClassPrepareRequest(f3vm, (ClassPrepareRequest)req);
        } else if (req instanceof ClassUnloadRequest) {
            return new F3ClassUnloadRequest(f3vm, (ClassUnloadRequest)req);
        } else if (req instanceof ExceptionRequest) {
            return new F3ExceptionRequest(f3vm, (ExceptionRequest)req);
        } else if (req instanceof MethodEntryRequest) {
            return new F3MethodEntryRequest(f3vm, (MethodEntryRequest)req);
        } else if (req instanceof MethodExitRequest) {
            return new F3MethodExitRequest(f3vm, (MethodExitRequest)req);
        } else if (req instanceof ModificationWatchpointRequest) {
            return new F3ModificationWatchpointRequest(f3vm, (ModificationWatchpointRequest)req);
        } else if (req instanceof MonitorContendedEnterRequest) {
            return new F3MonitorContendedEnterRequest(f3vm, (MonitorContendedEnterRequest)req);
        } else if (req instanceof MonitorContendedEnteredRequest) {
            return new F3MonitorContendedEnteredRequest(f3vm, (MonitorContendedEnteredRequest)req);
        } else if (req instanceof MonitorWaitRequest) {
            return new F3MonitorWaitRequest(f3vm, (MonitorWaitRequest)req);
        } else if (req instanceof MonitorWaitedRequest) {
            return new F3MonitorWaitedRequest(f3vm, (MonitorWaitedRequest)req);
        } else if (req instanceof StepRequest) {
            return new F3StepRequest(f3vm, (StepRequest)req);
        } else if (req instanceof ThreadDeathRequest) {
            return new F3ThreadDeathRequest(f3vm, (ThreadDeathRequest)req);
        } else if (req instanceof ThreadStartRequest) {
            return new F3ThreadStartRequest(f3vm, (ThreadStartRequest)req);
        } else if (req instanceof VMDeathRequest) {
            return new F3VMDeathRequest(f3vm, (VMDeathRequest)req);
        } else if (req instanceof WatchpointRequest) {
            return new F3WatchpointRequest(f3vm, (WatchpointRequest)req);
        } else {
            return new F3EventRequest(f3vm, req);
        }
    }
    
    public static F3AccessWatchpointRequest wrap(
            F3VirtualMachine f3vm, AccessWatchpointRequest req) {
        return (req == null)? null : new F3AccessWatchpointRequest(f3vm, req);
    }

    public static F3BreakpointRequest wrap(
            F3VirtualMachine f3vm, BreakpointRequest req) {
        return (req == null)? null : new F3BreakpointRequest(f3vm, req);
    }

    public static F3ClassPrepareRequest wrap(
            F3VirtualMachine f3vm, ClassPrepareRequest req) {
        return (req == null)? null : new F3ClassPrepareRequest(f3vm, req);
    }

    public static F3ClassUnloadRequest wrap(
            F3VirtualMachine f3vm, ClassUnloadRequest req) {
        return (req == null)? null : new F3ClassUnloadRequest(f3vm, req);
    }

    public static F3ExceptionRequest wrap(
            F3VirtualMachine f3vm, ExceptionRequest req) {
        return (req == null)? null : new F3ExceptionRequest(f3vm, req);
    }

    public static F3MethodEntryRequest wrap(
            F3VirtualMachine f3vm, MethodEntryRequest req) {
        return (req == null)? null : new F3MethodEntryRequest(f3vm, req);
    }

    public static F3MethodExitRequest wrap(
            F3VirtualMachine f3vm, MethodExitRequest req) {
        return (req == null)? null : new F3MethodExitRequest(f3vm, req);
    }

    public static F3ModificationWatchpointRequest wrap(
            F3VirtualMachine f3vm, ModificationWatchpointRequest req) {
        return (req == null)? null : new F3ModificationWatchpointRequest(f3vm, req);
    }

    public static F3MonitorContendedEnterRequest wrap(
            F3VirtualMachine f3vm, MonitorContendedEnterRequest req) {
        return (req == null)? null : new F3MonitorContendedEnterRequest(f3vm, req);
    }

    public static F3MonitorContendedEnteredRequest wrap(
            F3VirtualMachine f3vm, MonitorContendedEnteredRequest req) {
        return (req == null)? null : new F3MonitorContendedEnteredRequest(f3vm, req);
    }

    public static F3MonitorWaitRequest wrap(
            F3VirtualMachine f3vm, MonitorWaitRequest req) {
        return (req == null)? null : new F3MonitorWaitRequest(f3vm, req);
    }

    public static F3MonitorWaitedRequest wrap(
            F3VirtualMachine f3vm, MonitorWaitedRequest req) {
        return (req == null)? null : new F3MonitorWaitedRequest(f3vm, req);
    }

    public static F3StepRequest wrap(
            F3VirtualMachine f3vm, StepRequest req) {
        return (req == null)? null : new F3StepRequest(f3vm, req);
    }

    public static F3ThreadDeathRequest wrap(
            F3VirtualMachine f3vm, ThreadDeathRequest req) {
        return (req == null)? null : new F3ThreadDeathRequest(f3vm, req);
    }

    public static F3ThreadStartRequest wrap(
            F3VirtualMachine f3vm, ThreadStartRequest req) {
        return (req == null)? null : new F3ThreadStartRequest(f3vm, req);
    }

    public static F3VMDeathRequest wrap(F3VirtualMachine f3vm, VMDeathRequest req) {
        return (req == null)? null : new F3VMDeathRequest(f3vm, req);
    }

    public static List<AccessWatchpointRequest> wrapAccessWatchpointRequests(
            F3VirtualMachine f3vm,  List<AccessWatchpointRequest> reqs) {
        if (reqs == null) {
            return null;
        }
         List<AccessWatchpointRequest> result = new ArrayList<AccessWatchpointRequest>();
         for (AccessWatchpointRequest req : reqs) {
             result.add(wrap(f3vm, req));
         }
         return result;
    }

    public static List<BreakpointRequest> wrapBreakpointRequests(
            F3VirtualMachine f3vm,  List<BreakpointRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<BreakpointRequest> result = new ArrayList<BreakpointRequest>();
        for (BreakpointRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ClassPrepareRequest> wrapClassPrepareRequests(
            F3VirtualMachine f3vm,  List<ClassPrepareRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ClassPrepareRequest> result = new ArrayList<ClassPrepareRequest>();
        for (ClassPrepareRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ClassUnloadRequest> wrapClassUnloadRequests(
            F3VirtualMachine f3vm, List<ClassUnloadRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ClassUnloadRequest> result = new ArrayList<ClassUnloadRequest>();
        for (ClassUnloadRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ExceptionRequest> wrapExceptionRequests(
            F3VirtualMachine f3vm, List<ExceptionRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ExceptionRequest> result = new ArrayList<ExceptionRequest>();
        for (ExceptionRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MethodEntryRequest> wrapMethodEntryRequests(
            F3VirtualMachine f3vm, List<MethodEntryRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MethodEntryRequest> result = new ArrayList<MethodEntryRequest>();
        for (MethodEntryRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MethodExitRequest> wrapMethodExitRequests(
            F3VirtualMachine f3vm, List<MethodExitRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MethodExitRequest> result = new ArrayList<MethodExitRequest>();
        for (MethodExitRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ModificationWatchpointRequest> wrapModificationWatchpointRequests(
            F3VirtualMachine f3vm, List<ModificationWatchpointRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ModificationWatchpointRequest> result = new ArrayList<ModificationWatchpointRequest>();
        for (ModificationWatchpointRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MonitorContendedEnterRequest> wrapMonitorContendedEnterRequests(
            F3VirtualMachine f3vm, List<MonitorContendedEnterRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MonitorContendedEnterRequest> result = new ArrayList<MonitorContendedEnterRequest>();
        for (MonitorContendedEnterRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MonitorContendedEnteredRequest> wrapMonitorContendedEnteredRequests(
            F3VirtualMachine f3vm, List<MonitorContendedEnteredRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MonitorContendedEnteredRequest> result = new ArrayList<MonitorContendedEnteredRequest>();
        for (MonitorContendedEnteredRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MonitorWaitRequest> wrapMonitorWaitRequests(
            F3VirtualMachine f3vm, List<MonitorWaitRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MonitorWaitRequest> result = new ArrayList<MonitorWaitRequest>();
        for (MonitorWaitRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<MonitorWaitedRequest> wrapMonitorWaitedRequests(
            F3VirtualMachine f3vm, List<MonitorWaitedRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<MonitorWaitedRequest> result = new ArrayList<MonitorWaitedRequest>();
        for (MonitorWaitedRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<StepRequest> wrapStepRequests(
            F3VirtualMachine f3vm, List<StepRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<StepRequest> result = new ArrayList<StepRequest>();
        for (StepRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ThreadDeathRequest> wrapThreadDeathRequests(
            F3VirtualMachine f3vm, List<ThreadDeathRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ThreadDeathRequest> result = new ArrayList<ThreadDeathRequest>();
        for (ThreadDeathRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<ThreadStartRequest> wrapThreadStartRequests(
            F3VirtualMachine f3vm, List<ThreadStartRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<ThreadStartRequest> result = new ArrayList<ThreadStartRequest>();
        for (ThreadStartRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }

    public static List<VMDeathRequest> wrapVMDeathRequests(
            F3VirtualMachine f3vm, List<VMDeathRequest> reqs) {
        if (reqs == null) {
            return null;
        }
        List<VMDeathRequest> result = new ArrayList<VMDeathRequest>();
        for (VMDeathRequest req : reqs) {
            result.add(wrap(f3vm, req));
        }
        return result;
    }
    
    // unwrap methods
    public static EventRequest unwrap(EventRequest req) {
        return (req instanceof F3EventRequest)? ((F3EventRequest)req).underlying() : req;
    }

    public static <T extends EventRequest> List<T> unwrapEventRequests(List<T> requests) {
        List result = new ArrayList();
        for (EventRequest req : requests) {
            result.add(unwrap(req));
        }
        return result;
    }
}
