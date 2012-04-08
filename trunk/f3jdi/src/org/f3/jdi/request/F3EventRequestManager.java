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
import org.f3.jdi.F3Wrapper;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
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
import java.util.List;

/**
 *
 * @author sundar
 */
public class F3EventRequestManager extends F3Mirror implements EventRequestManager {
    public F3EventRequestManager(F3VirtualMachine f3vm, EventRequestManager underlying) {
        super(f3vm, underlying);
    }

    public List<AccessWatchpointRequest> accessWatchpointRequests() {
        return F3EventRequest.wrapAccessWatchpointRequests(virtualMachine(),
                underlying().accessWatchpointRequests());
    }

    public List<BreakpointRequest> breakpointRequests() {
        return F3EventRequest.wrapBreakpointRequests(virtualMachine(),
                underlying().breakpointRequests());
    }

    public List<ClassPrepareRequest> classPrepareRequests() {
        return F3EventRequest.wrapClassPrepareRequests(virtualMachine(),
                underlying().classPrepareRequests());
    }

    public List<ClassUnloadRequest> classUnloadRequests() {
        return F3EventRequest.wrapClassUnloadRequests(virtualMachine(),
                underlying().classUnloadRequests());
    }

    public F3AccessWatchpointRequest createAccessWatchpointRequest(Field arg0) {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createAccessWatchpointRequest(F3Wrapper.unwrap(arg0)));
    }

    public F3BreakpointRequest createBreakpointRequest(Location arg0) {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createBreakpointRequest(F3Wrapper.unwrap(arg0)));
    }

    public F3ClassPrepareRequest createClassPrepareRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createClassPrepareRequest());
    }

    public F3ClassUnloadRequest createClassUnloadRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createClassUnloadRequest());
    }

    public F3ExceptionRequest createExceptionRequest(ReferenceType arg0, boolean arg1, boolean arg2) {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createExceptionRequest(F3Wrapper.unwrap(arg0), arg1, arg2));
    }

    public F3MethodEntryRequest createMethodEntryRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMethodEntryRequest());
    }

    public F3MethodExitRequest createMethodExitRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMethodExitRequest());
    }

    public F3ModificationWatchpointRequest createModificationWatchpointRequest(Field arg0) {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createModificationWatchpointRequest(F3Wrapper.unwrap(arg0)));
    }

    public F3MonitorContendedEnterRequest createMonitorContendedEnterRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMonitorContendedEnterRequest());
    }

    public F3MonitorContendedEnteredRequest createMonitorContendedEnteredRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMonitorContendedEnteredRequest());
    }

    public F3MonitorWaitRequest createMonitorWaitRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMonitorWaitRequest());
    }

    public F3MonitorWaitedRequest createMonitorWaitedRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createMonitorWaitedRequest());
    }

    public F3StepRequest createStepRequest(ThreadReference arg0, int arg1, int arg2) {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createStepRequest(F3Wrapper.unwrap(arg0), arg1, arg2));
    }

    public F3ThreadDeathRequest createThreadDeathRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createThreadDeathRequest());
    }

    public F3ThreadStartRequest createThreadStartRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createThreadStartRequest());
    }

    public F3VMDeathRequest createVMDeathRequest() {
        return F3EventRequest.wrap(virtualMachine(),
                underlying().createVMDeathRequest());
    }

    public void deleteAllBreakpoints() {
        underlying().deleteAllBreakpoints();
    }

    public void deleteEventRequest(EventRequest arg0) {
        underlying().deleteEventRequest(F3EventRequest.unwrap(arg0));
    }

    public void deleteEventRequests(List<? extends EventRequest> arg0) {
        underlying().deleteEventRequests(F3EventRequest.unwrapEventRequests(arg0));
    }

    public List<ExceptionRequest> exceptionRequests() {
        return F3EventRequest.wrapExceptionRequests(virtualMachine(),
                underlying().exceptionRequests());
    }

    public List<MethodEntryRequest> methodEntryRequests() {
        return F3EventRequest.wrapMethodEntryRequests(virtualMachine(),
                underlying().methodEntryRequests());
    }

    public List<MethodExitRequest> methodExitRequests() {
        return F3EventRequest.wrapMethodExitRequests(virtualMachine(),
                underlying().methodExitRequests());
    }

    public List<ModificationWatchpointRequest> modificationWatchpointRequests() {
        return F3EventRequest.wrapModificationWatchpointRequests(virtualMachine(),
                underlying().modificationWatchpointRequests());
    }

    public List<MonitorContendedEnterRequest> monitorContendedEnterRequests() {
        return F3EventRequest.wrapMonitorContendedEnterRequests(virtualMachine(),
                underlying().monitorContendedEnterRequests());
    }

    public List<MonitorContendedEnteredRequest> monitorContendedEnteredRequests() {
        return F3EventRequest.wrapMonitorContendedEnteredRequests(virtualMachine(),
                underlying().monitorContendedEnteredRequests());
    }

    public List<MonitorWaitRequest> monitorWaitRequests() {
        return F3EventRequest.wrapMonitorWaitRequests(virtualMachine(),
                underlying().monitorWaitRequests());
    }

    public List<MonitorWaitedRequest> monitorWaitedRequests() {
        return F3EventRequest.wrapMonitorWaitedRequests(virtualMachine(),
                underlying().monitorWaitedRequests());
    }

    public List<StepRequest> stepRequests() {
        return F3EventRequest.wrapStepRequests(virtualMachine(),
                underlying().stepRequests());
    }

    public List<ThreadDeathRequest> threadDeathRequests() {
        return F3EventRequest.wrapThreadDeathRequests(virtualMachine(),
                underlying().threadDeathRequests());
    }

    public List<ThreadStartRequest> threadStartRequests() {
        return F3EventRequest.wrapThreadStartRequests(virtualMachine(),
                underlying().threadStartRequests());
    }

    public List<VMDeathRequest> vmDeathRequests() {
        return F3EventRequest.wrapVMDeathRequests(virtualMachine(),
                underlying().vmDeathRequests());
    }

    @Override
    protected EventRequestManager underlying() {
        return (EventRequestManager) super.underlying();
    }
}
