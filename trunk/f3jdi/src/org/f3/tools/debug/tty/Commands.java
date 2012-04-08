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

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.*;
import org.f3.tools.debug.expr.ExpressionParser;
import org.f3.tools.debug.expr.ParseException;

import java.text.*;
import java.util.*;
import java.io.*;

class Commands {

    abstract class AsyncExecution {
        abstract void action();

        AsyncExecution() {
            execute();
        }

        void execute() {
            /*
             * Save current thread and stack frame. (BugId 4296031)
             */
            final ThreadInfo threadInfo = env.getCurrentThreadInfo();
            final int stackFrame = threadInfo == null? 0 : threadInfo.getCurrentFrameIndex();
            Thread thread = new Thread("asynchronous jdb command") {
                    public void run() {
                        try {
                            action();
                        } catch (UnsupportedOperationException uoe) {
                            //(BugId 4453329)
                            env.messageOutput().println("Operation is not supported on the target VM");
                        } catch (Exception e) {
                            env.messageOutput().println("Internal exception during operation:",
                                                  e.getMessage());
                        } finally {
                            /*
                             * This was an asynchronous command.  Events may have been
                             * processed while it was running.  Restore the thread and
                             * stack frame the user was looking at.  (BugId 4296031)
                             */
                            if (threadInfo != null) {
                                env.setCurrentThreadInfo(threadInfo);
                                try {
                                    threadInfo.setCurrentFrameIndex(stackFrame);
                                } catch (IncompatibleThreadStateException e) {
                                    env.messageOutput().println("Current thread isnt suspended.");
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    env.messageOutput().println("Requested stack frame is no longer active:",
                                                          new Object []{new Integer(stackFrame)});
                                }
                            }
                            env.printPrompt();
                        }
                    }
                };
            thread.start();
        }
    }

    private final Env env;
    Commands(Env env) {
        this.env = env;
    }

    private Value evaluate(String expr) {
        Value result = null;
        ExpressionParser.GetFrame frameGetter = null;
        try {
            final ThreadInfo threadInfo = env.getCurrentThreadInfo();
            if ((threadInfo != null) && (threadInfo.getCurrentFrame() != null)) {
                frameGetter = new ExpressionParser.GetFrame() {
                        public StackFrame get() throws IncompatibleThreadStateException {
                            return threadInfo.getCurrentFrame();
                        }
                    };
            }
            result = ExpressionParser.evaluate(expr, env.vm(), frameGetter);
        } catch (InvocationException ie) {
            env.messageOutput().println("Exception in expression:",
                                  ie.exception().referenceType().name());
        } catch (Exception ex) {
            String exMessage = ex.getMessage();
            if (exMessage == null) {
                env.messageOutput().printException(exMessage, ex);
            } else {
                String s;
                try {
                    s = MessageOutput.format(exMessage);
                } catch (MissingResourceException mex) {
                    s = ex.toString();
                }
                env.messageOutput().printDirectln(s);// Special case: use printDirectln()
            }
        }
        return result;
    }

    private String getStringValue() {
         Value val = null;
         String valStr = null;
         try {
              val = ExpressionParser.getMassagedValue();
              valStr = val.toString();
         } catch (ParseException e) {
              String msg = e.getMessage();
              if (msg == null) {
                  env.messageOutput().printException(msg, e);
              } else {
                  String s;
                  try {
                      s = MessageOutput.format(msg);
                  } catch (MissingResourceException mex) {
                      s = e.toString();
                  }
                  env.messageOutput().printDirectln(s);
              }
         }
         return valStr;
    }

    ThreadInfo doGetThread(String idToken) {
        ThreadInfo threadInfo = env.getThreadInfo(idToken);
        if (threadInfo == null) {
            env.messageOutput().println("is not a valid thread id", idToken);
        }
        return threadInfo;
    }

    String typedName(Method method) {
        StringBuffer buf = new StringBuffer();
        buf.append(method.name());
        buf.append("(");

        List<String> args = method.argumentTypeNames();
        int lastParam = args.size() - 1;
        // output param types except for the last
        for (int ii = 0; ii < lastParam; ii++) {
            buf.append(args.get(ii));
            buf.append(", ");
        }
        if (lastParam >= 0) {
            // output the last param
            String lastStr = args.get(lastParam);
            if (method.isVarArgs()) {
                // lastParam is an array.  Replace the [] with ...
                buf.append(lastStr.substring(0, lastStr.length() - 2));
                buf.append("...");
            } else {
                buf.append(lastStr);
            }
        }
        buf.append(")");
        return buf.toString();
    }

    void commandConnectors(VirtualMachineManager vmm) {
        Collection<Connector> ccs = vmm.allConnectors();
        if (ccs.isEmpty()) {
            env.messageOutput().println("Connectors available");
        }
        for (Connector cc : ccs) {
            String transportName =
                cc.transport() == null ? "null" : cc.transport().name();
            env.messageOutput().println();
            env.messageOutput().println("Connector and Transport name",
                                  new Object [] {cc.name(), transportName});
            env.messageOutput().println("Connector description", cc.description());

            for (Connector.Argument aa : cc.defaultArguments().values()) {
                    env.messageOutput().println();

                    boolean requiredArgument = aa.mustSpecify();
                    if (aa.value() == null || aa.value() == "") {
                        //no current value and no default.
                        env.messageOutput().println(requiredArgument ?
                                              "Connector required argument nodefault" :
                                              "Connector argument nodefault", aa.name());
                    } else {
                        env.messageOutput().println(requiredArgument ?
                                              "Connector required argument default" :
                                              "Connector argument default",
                                              new Object [] {aa.name(), aa.value()});
                    }
                    env.messageOutput().println("Connector description", aa.description());

                }
            }

    }

    void commandClasses() {
        StringBuffer classList = new StringBuffer();
        for (ReferenceType refType : env.vm().allClasses()) {
            classList.append(refType.name());
            classList.append("\n");
        }
        env.messageOutput().print("** classes list **", classList.toString());
    }

    void commandClass(StringTokenizer t) {
        List<ReferenceType> list = env.vm().allClasses();

        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No class specified.");
            return;
        }

        String idClass = t.nextToken();
        boolean showAll = false;

        if (t.hasMoreTokens()) {
            if (t.nextToken().toLowerCase().equals("all")) {
                showAll = true;
            } else {
                env.messageOutput().println("Invalid option on class command");
                return;
            }
        }
        ReferenceType type = env.getReferenceTypeFromToken(idClass);
        if (type == null) {
            env.messageOutput().println("is not a valid id or class name", idClass);
            return;
        }
        if (type instanceof ClassType) {
            ClassType clazz = (ClassType)type;
            env.messageOutput().println("Class:", clazz.name());

            ClassType superclass = clazz.superclass();
            while (superclass != null) {
                env.messageOutput().println("extends:", superclass.name());
                superclass = showAll ? superclass.superclass() : null;
            }

            List<InterfaceType> interfaces =
                showAll ? clazz.allInterfaces() : clazz.interfaces();
            for (InterfaceType interfaze : interfaces) {
                env.messageOutput().println("implements:", interfaze.name());
            }

            for (ClassType sub : clazz.subclasses()) {
                env.messageOutput().println("subclass:", sub.name());
            }
            for (ReferenceType nest : clazz.nestedTypes()) {
                env.messageOutput().println("nested:", nest.name());
            }
        } else if (type instanceof InterfaceType) {
            InterfaceType interfaze = (InterfaceType)type;
            env.messageOutput().println("Interface:", interfaze.name());
            for (InterfaceType superinterface : interfaze.superinterfaces()) {
                env.messageOutput().println("extends:", superinterface.name());
            }
            for (InterfaceType sub : interfaze.subinterfaces()) {
                env.messageOutput().println("subinterface:", sub.name());
            }
            for (ClassType implementor : interfaze.implementors()) {
                env.messageOutput().println("implementor:", implementor.name());
            }
            for (ReferenceType nest : interfaze.nestedTypes()) {
                env.messageOutput().println("nested:", nest.name());
            }
        } else {  // array type
            ArrayType array = (ArrayType)type;
            env.messageOutput().println("Array:", array.name());
        }
    }

    void commandMethods(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No class specified.");
            return;
        }

        String idClass = t.nextToken();
        ReferenceType cls = env.getReferenceTypeFromToken(idClass);
        if (cls != null) {
            StringBuffer methodsList = new StringBuffer();
            for (Method method : cls.allMethods()) {
                methodsList.append(method.declaringType().name());
                methodsList.append(" ");
                methodsList.append(typedName(method));
                methodsList.append('\n');
            }
            env.messageOutput().print("** methods list **", methodsList.toString());
        } else {
            env.messageOutput().println("is not a valid id or class name", idClass);
        }
    }

    void commandFields(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No class specified.");
            return;
        }

        String idClass = t.nextToken();
        ReferenceType cls = env.getReferenceTypeFromToken(idClass);
        if (cls != null) {
            List<Field> fields = cls.allFields();
            List<Field> visible = cls.visibleFields();
            StringBuffer fieldsList = new StringBuffer();
            for (Field field : fields) {
                String s;
                if (!visible.contains(field)) {
                    s = MessageOutput.format("list field typename and name hidden",
                                             new Object [] {field.typeName(),
                                                            field.name()});
                } else if (!field.declaringType().equals(cls)) {
                    s = MessageOutput.format("list field typename and name inherited",
                                             new Object [] {field.typeName(),
                                                            field.name(),
                                                            field.declaringType().name()});
                } else {
                    s = MessageOutput.format("list field typename and name",
                                             new Object [] {field.typeName(),
                                                            field.name()});
                }
                fieldsList.append(s);
            }
            env.messageOutput().print("** fields list **", fieldsList.toString());
        } else {
            env.messageOutput().println("is not a valid id or class name", idClass);
        }
    }

    private void printThreadGroup(ThreadGroupReference tg) {
        ThreadIterator threadIter = new ThreadIterator(tg);

        env.messageOutput().println("Thread Group:", tg.name());
        int maxIdLength = 0;
        int maxNameLength = 0;
        while (threadIter.hasNext()) {
            ThreadReference thr = threadIter.next();
            maxIdLength = Math.max(maxIdLength,
                                   env.description(thr).length());
            maxNameLength = Math.max(maxNameLength,
                                     thr.name().length());
        }

        threadIter = new ThreadIterator(tg);
        while (threadIter.hasNext()) {
            ThreadReference thr = threadIter.next();
            if (thr.threadGroup() == null) {
                continue;
            }
            // Note any thread group changes
            if (!thr.threadGroup().equals(tg)) {
                tg = thr.threadGroup();
                env.messageOutput().println("Thread Group:", tg.name());
            }

            /*
             * Do a bit of filling with whitespace to get thread ID
             * and thread names to line up in the listing, and also
             * allow for proper localization.  This also works for
             * very long thread names, at the possible cost of lines
             * being wrapped by the display device.
             */
            StringBuffer idBuffer = new StringBuffer(env.description(thr));
            for (int i = idBuffer.length(); i < maxIdLength; i++) {
                idBuffer.append(" ");
            }
            StringBuffer nameBuffer = new StringBuffer(thr.name());
            for (int i = nameBuffer.length(); i < maxNameLength; i++) {
                nameBuffer.append(" ");
            }

            /*
             * Select the output format to use based on thread status
             * and breakpoint.
             */
            String statusFormat;
            switch (thr.status()) {
            case ThreadReference.THREAD_STATUS_UNKNOWN:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name unknownStatus BP";
                } else {
                    statusFormat = "Thread description name unknownStatus";
                }
                break;
            case ThreadReference.THREAD_STATUS_ZOMBIE:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name zombieStatus BP";
                } else {
                    statusFormat = "Thread description name zombieStatus";
                }
                break;
            case ThreadReference.THREAD_STATUS_RUNNING:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name runningStatus BP";
                } else {
                    statusFormat = "Thread description name runningStatus";
                }
                break;
            case ThreadReference.THREAD_STATUS_SLEEPING:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name sleepingStatus BP";
                } else {
                    statusFormat = "Thread description name sleepingStatus";
                }
                break;
            case ThreadReference.THREAD_STATUS_MONITOR:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name waitingStatus BP";
                } else {
                    statusFormat = "Thread description name waitingStatus";
                }
                break;
            case ThreadReference.THREAD_STATUS_WAIT:
                if (thr.isAtBreakpoint()) {
                    statusFormat = "Thread description name condWaitstatus BP";
                } else {
                    statusFormat = "Thread description name condWaitstatus";
                }
                break;
            default:
                throw new InternalError(MessageOutput.format("Invalid thread status."));
            }
            env.messageOutput().println(statusFormat,
                                  new Object [] {idBuffer.toString(),
                                                 nameBuffer.toString()});
        }
    }

    boolean commandThreads(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            printThreadGroup(env.getCurrentThreadGroup());
            return true;
        }
        String name = t.nextToken();
        ThreadGroupReference tg = ThreadGroupIterator.find(env.vm(), name);
        if (tg == null) {
            env.messageOutput().println("is not a valid threadgroup name", name);
            return false;
        } else {
            printThreadGroup(tg);
            return true;
        }
    }

    void commandThreadGroups() {
        ThreadGroupIterator it = new ThreadGroupIterator(env.vm());
        int cnt = 0;
        while (it.hasNext()) {
            ThreadGroupReference tg = it.nextThreadGroup();
            ++cnt;
            env.messageOutput().println("thread group number description name",
                                  new Object [] { new Integer (cnt),
                                                  env.description(tg),
                                                  tg.name()});
        }
    }

    boolean commandThread(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Thread number not specified.");
            return false;
        }
        ThreadInfo threadInfo = doGetThread(t.nextToken());
        if (threadInfo != null) {
            env.setCurrentThreadInfo(threadInfo);
            return true;
        } else {
            return false;
        }
    }

    boolean commandThreadGroup(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Threadgroup name not specified.");
            return false;
        }
        String name = t.nextToken();
        ThreadGroupReference tg = ThreadGroupIterator.find(env.vm(), name);
        if (tg == null) {
            env.messageOutput().println("is not a valid threadgroup name", name);
            return false;
        } else {
            env.setThreadGroup(tg);
            return true;
        }
    }

    boolean commandRun(StringTokenizer t) {
        /*
         * The 'run' command makes little sense in a
         * that doesn't support restarts or multiple VMs. However,
         * this is an attempt to emulate the behavior of the old
         * JDB as much as possible. For new users and implementations
         * it is much more straightforward to launch immedidately
         * with the -launch option.
         */
        VMConnection connection = env.connection();
        if (!connection.isLaunch()) {
            if (!t.hasMoreTokens()) {
                commandCont();
                return true;
            } else {
                env.messageOutput().println("run <args> command is valid only with launched VMs");
                return false;
            }
        }
        if (connection.isOpen()) {
            env.messageOutput().println("VM already running. use cont to continue after events.");
            return false;
        }

        /*
         * Set the main class and any arguments. Note that this will work
         * only with the standard launcher, "com.sun.jdi.CommandLineLauncher"
         */
        String args;
        if (t.hasMoreTokens()) {
            args = t.nextToken("");
            boolean argsSet = connection.setConnectorArg("main", args);
            if (!argsSet) {
                env.messageOutput().println("Unable to set main class and arguments");
                return false;
            }
        } else {
            args = connection.connectorArg("main");
            if (args.length() == 0) {
                env.messageOutput().println("Main class and arguments must be specified");
                return false;
            }
        }
        env.messageOutput().println("run", args);

        /*
         * Launch the VM.
         */
        connection.open();
        return connection.isOpen();
    }

    void commandLoad(StringTokenizer t) {
        env.messageOutput().println("The load command is no longer supported.");
    }

    private List<ThreadReference> allThreads(ThreadGroupReference group) {
        List<ThreadReference> list = new ArrayList<ThreadReference>();
        list.addAll(group.threads());
        for (ThreadGroupReference child : group.threadGroups()) {
            list.addAll(allThreads(child));
        }
        return list;
    }

    boolean commandSuspend(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.vm().suspend();
            env.messageOutput().println("All threads suspended.");
            return true;
        } else {
            boolean suspended = false;
            while (t.hasMoreTokens()) {
                ThreadInfo threadInfo = doGetThread(t.nextToken());
                if (threadInfo != null) {
                    threadInfo.getThread().suspend();
                    suspended |= true;
                }
            }
            return suspended;
        }
    }

    boolean commandResume(StringTokenizer t) {
         if (!t.hasMoreTokens()) {
             env.invalidateAllThreadInfo();
             env.vm().resume();
             env.messageOutput().println("All threads resumed.");
             return true;
         } else {
             boolean resumed = false;
             while (t.hasMoreTokens()) {
                ThreadInfo threadInfo = doGetThread(t.nextToken());
                if (threadInfo != null) {
                    threadInfo.invalidate();
                    threadInfo.getThread().resume();
                    resumed |= true;
                }
            }
            return resumed;
        }
    }

    boolean commandCont() {
        if (env.getCurrentThreadInfo() == null) {
            env.messageOutput().println("Nothing suspended.");
            return false;
        }
        env.invalidateAllThreadInfo();
        env.vm().resume();
        return true;
    }

    void clearPreviousStep(ThreadReference thread) {
        /*
         * A previous step may not have completed on this thread;
         * if so, it gets removed here.
         */
         EventRequestManager mgr = env.vm().eventRequestManager();
         for (StepRequest request : mgr.stepRequests()) {
             if (request.thread().equals(thread)) {
                 mgr.deleteEventRequest(request);
                 break;
             }
         }
    }
    /* step
     *
     */
    StepRequest commandStep(StringTokenizer t) {
        return commandStep(t, true);
    }

    StepRequest commandStep(StringTokenizer t, boolean resume) {
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("Nothing suspended.");
            return null;
        }
        int depth;
        if (t.hasMoreTokens() &&
                  t.nextToken().toLowerCase().equals("up")) {
            depth = StepRequest.STEP_OUT;
        } else {
            depth = StepRequest.STEP_INTO;
        }

        clearPreviousStep(threadInfo.getThread());
        EventRequestManager reqMgr = env.vm().eventRequestManager();
        StepRequest request = reqMgr.createStepRequest(threadInfo.getThread(),
                                                       StepRequest.STEP_LINE, depth);
        if (depth == StepRequest.STEP_INTO) {
            env.addExcludes(request);
        }
        // We want just the next step event and no others
        request.addCountFilter(1);
        request.enable();
        env.invalidateAllThreadInfo();
        if (resume) {
            env.vm().resume();
        }
        return request;
    }

    /* stepi
     * step instruction.
     */
    StepRequest commandStepi() {
        return commandStepi(true);
    }

    StepRequest commandStepi(boolean resume) {
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("Nothing suspended.");
            return null;
        }
        clearPreviousStep(threadInfo.getThread());
        EventRequestManager reqMgr = env.vm().eventRequestManager();
        StepRequest request = reqMgr.createStepRequest(threadInfo.getThread(),
                                                       StepRequest.STEP_MIN,
                                                       StepRequest.STEP_INTO);
        env.addExcludes(request);
        // We want just the next step event and no others
        request.addCountFilter(1);
        request.enable();
        env.invalidateAllThreadInfo();
        if (resume) {
            env.vm().resume();
        }
        return request;
    }

    StepRequest commandNext() {
        return commandNext(true);
    }

    StepRequest commandNext(boolean resume) {
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("Nothing suspended.");
            return null;
        }
        clearPreviousStep(threadInfo.getThread());
        EventRequestManager reqMgr = env.vm().eventRequestManager();
        StepRequest request = reqMgr.createStepRequest(threadInfo.getThread(),
                                                       StepRequest.STEP_LINE,
                                                       StepRequest.STEP_OVER);
        env.addExcludes(request);
        // We want just the next step event and no others
        request.addCountFilter(1);
        request.enable();
        env.invalidateAllThreadInfo();
        if (resume) {
            env.vm().resume();
        }
        return request;
    }

    void doKill(ThreadReference thread, StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No exception object specified.");
            return;
        }
        String expr = t.nextToken("");
        Value val = evaluate(expr);
        if ((val != null) && (val instanceof ObjectReference)) {
            try {
                thread.stop((ObjectReference)val);
                env.messageOutput().println("killed", thread.toString());
            } catch (InvalidTypeException e) {
                env.messageOutput().println("Invalid exception object");
            }
        } else {
            env.messageOutput().println("Expression must evaluate to an object");
        }
    }

    void doKillThread(final ThreadReference threadToKill,
                      final StringTokenizer tokenizer) {
        new AsyncExecution() {
                void action() {
                    doKill(threadToKill, tokenizer);
                }
            };
    }

    void commandKill(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Usage: kill <thread id> <throwable>");
            return;
        }
        ThreadInfo threadInfo = doGetThread(t.nextToken());
        if (threadInfo != null) {
            env.messageOutput().println("killing thread:", threadInfo.getThread().name());
            doKillThread(threadInfo.getThread(), t);
            return;
        }
    }

    void listCaughtExceptions() {
        boolean noExceptions = true;

        // Print a listing of the catch patterns currently in place
        for (EventRequestSpec spec : env.getSpecList().eventRequestSpecs()) {
            if (spec instanceof ExceptionSpec) {
                if (noExceptions) {
                    noExceptions = false;
                    env.messageOutput().println("Exceptions caught:");
                }
                env.messageOutput().println("tab", spec.toString());
            }
        }
        if (noExceptions) {
            env.messageOutput().println("No exceptions caught.");
        }
    }

    private EventRequestSpec parseExceptionSpec(StringTokenizer t) {
        String notification = t.nextToken();
        boolean notifyCaught = false;
        boolean notifyUncaught = false;
        EventRequestSpec spec = null;
        String classPattern = null;

        if (notification.equals("uncaught")) {
            notifyCaught = false;
            notifyUncaught = true;
        } else if (notification.equals("caught")) {
            notifyCaught = true;
            notifyUncaught = false;
        } else if (notification.equals("all")) {
            notifyCaught = true;
            notifyUncaught = true;
        } else {
            /*
             * Handle the same as "all" for backward
             * compatibility with existing .jdbrc files.
             *
             * Insert an "all" and take the current token as the
             * intended classPattern
             *
             */
            notifyCaught = true;
            notifyUncaught = true;
            classPattern = notification;
        }
        if (classPattern == null && t.hasMoreTokens()) {
            classPattern = t.nextToken();
        }
        if ((classPattern != null) && (notifyCaught || notifyUncaught)) {
            try {
                spec = env.getSpecList().createExceptionCatch(classPattern,
                                                         notifyCaught,
                                                         notifyUncaught);
            } catch (ClassNotFoundException exc) {
                env.messageOutput().println("is not a valid class name", classPattern);
            }
        }
        return spec;
    }

    ExceptionRequest commandCatchException(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            listCaughtExceptions();
            return null;
        } else {
            EventRequestSpec spec = parseExceptionSpec(t);
            if (spec != null) {
                return (ExceptionRequest) resolveNow(spec);
            } else {
                env.messageOutput().println("Usage: catch exception");
                return null;
            }
        }
    }

    boolean commandIgnoreException(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            listCaughtExceptions();
            return true;
        } else {
            EventRequestSpec spec = parseExceptionSpec(t);
            if (env.getSpecList().delete(spec)) {
                env.messageOutput().println("Removed:", spec.toString());
                return true;
            } else {
                if (spec != null) {
                    env.messageOutput().println("Not found:", spec.toString());
                }
                env.messageOutput().println("Usage: ignore exception");
                return false;
            }
        }
    }

    boolean commandUp(StringTokenizer t) {
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("Current thread not set.");
            return false;
        }

        int nLevels = 1;
        if (t.hasMoreTokens()) {
            String idToken = t.nextToken();
            int i;
            try {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setParseIntegerOnly(true);
                Number n = nf.parse(idToken);
                i = n.intValue();
            } catch (java.text.ParseException jtpe) {
                i = 0;
            }
            if (i <= 0) {
                env.messageOutput().println("Usage: up [n frames]");
                return false;
            }
            nLevels = i;
        }

        try {
            threadInfo.up(nLevels);
            return true;
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Current thread isnt suspended.");
        } catch (ArrayIndexOutOfBoundsException e) {
            env.messageOutput().println("End of stack.");
        }
        return false;
    }

    boolean commandDown(StringTokenizer t) {
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("Current thread not set.");
            return false;
        }

        int nLevels = 1;
        if (t.hasMoreTokens()) {
            String idToken = t.nextToken();
            int i;
            try {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setParseIntegerOnly(true);
                Number n = nf.parse(idToken);
                i = n.intValue();
            } catch (java.text.ParseException jtpe) {
                i = 0;
            }
            if (i <= 0) {
                env.messageOutput().println("Usage: down [n frames]");
                return false;
            }
            nLevels = i;
        }

        try {
            threadInfo.down(nLevels);
            return true;
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Current thread isnt suspended.");
        } catch (ArrayIndexOutOfBoundsException e) {
            env.messageOutput().println("End of stack.");
        }
        return false;
    }

    private void dumpStack(ThreadInfo threadInfo, boolean showPC) {
        List<StackFrame> stack = null;
        try {
            stack = threadInfo.getStack();
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Current thread isnt suspended.");
            return;
        }
        if (stack == null) {
            env.messageOutput().println("Thread is not running (no stack).");
        } else {
            int nFrames = stack.size();
            for (int i = threadInfo.getCurrentFrameIndex(); i < nFrames; i++) {
                StackFrame frame = stack.get(i);
                dumpFrame (i, showPC, frame);
            }
        }
    }

    private void dumpFrame (int frameNumber, boolean showPC, StackFrame frame) {
        Location loc = frame.location();
        long pc = -1;
        if (showPC) {
            pc = loc.codeIndex();
        }
        Method meth = loc.method();

        long lineNumber = loc.lineNumber();
        String methodInfo = null;
        if (meth.isNative()) {
            methodInfo = MessageOutput.format("native method");
        } else if (lineNumber != -1) {
            try {
                methodInfo = loc.sourceName() +
                    MessageOutput.format("line number",
                                         new Object [] {new Long(lineNumber)});
            } catch (AbsentInformationException e) {
                methodInfo = MessageOutput.format("unknown");
            }
        }
        if (pc != -1) {
            env.messageOutput().println("stack frame dump with pc",
                                  new Object [] {new Integer(frameNumber + 1),
                                                 meth.declaringType().name(),
                                                 meth.name(),
                                                 methodInfo,
                                                 new Long(pc)});
        } else {
            env.messageOutput().println("stack frame dump",
                                  new Object [] {new Integer(frameNumber + 1),
                                                 meth.declaringType().name(),
                                                 meth.name(),
                                                 methodInfo});
        }
    }

    boolean commandWhere(StringTokenizer t, boolean showPC) {
        if (!t.hasMoreTokens()) {
            ThreadInfo threadInfo = env.getCurrentThreadInfo();
            if (threadInfo == null) {
                env.messageOutput().println("No thread specified.");
                return false;
            }
            dumpStack(threadInfo, showPC);
            return true;
        } else {
            String token = t.nextToken();
            if (token.toLowerCase().equals("all")) {
                for (ThreadInfo threadInfo : env.threads()) {
                    env.messageOutput().println("Thread:",
                                          threadInfo.getThread().name());
                    dumpStack(threadInfo, showPC);
                }
                return true;
            } else {
                ThreadInfo threadInfo = doGetThread(token);
                if (threadInfo != null) {
                    env.setCurrentThreadInfo(threadInfo);
                    dumpStack(threadInfo, showPC);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    boolean commandInterrupt(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            ThreadInfo threadInfo = env.getCurrentThreadInfo();
            if (threadInfo == null) {
                env.messageOutput().println("No thread specified.");
                return false;
            }
            threadInfo.getThread().interrupt();
            return true;
        } else {
            ThreadInfo threadInfo = doGetThread(t.nextToken());
            if (threadInfo != null) {
                threadInfo.getThread().interrupt();
                return true;
            } else {
                return false;
            }
        }
    }

    void commandMemory() {
        env.messageOutput().println("The memory command is no longer supported.");
    }

    void commandGC() {
        env.messageOutput().println("The gc command is no longer necessary.");
    }

    /*
     * The next two methods are used by this class and by EventHandler
     * to print consistent locations and error messages.
     */
    static String locationString(Location loc) {
        return MessageOutput.format("locationString",
                                    new Object [] {loc.declaringType().name(),
                                                   loc.method().name(),
                                                   new Integer (loc.lineNumber()),
                                                   new Long (loc.codeIndex())});
    }

    void listBreakpoints() {
        boolean noBreakpoints = true;

        // Print set breakpoints
        for (EventRequestSpec spec : env.getSpecList().eventRequestSpecs()) {
            if (spec instanceof BreakpointSpec) {
                if (noBreakpoints) {
                    noBreakpoints = false;
                    env.messageOutput().println("Breakpoints set:");
                }
                env.messageOutput().println("tab", spec.toString());
            }
        }
        if (noBreakpoints) {
            env.messageOutput().println("No breakpoints set.");
        }
    }


    private void printBreakpointCommandUsage(String atForm, String inForm) {
        env.messageOutput().println("printbreakpointcommandusage",
                              new Object [] {atForm, inForm});
    }

    protected BreakpointSpec parseBreakpointSpec(StringTokenizer t,
                                             String atForm, String inForm) {
        BreakpointSpec breakpoint = null;
        try {
            String token = t.nextToken(":( \t\n\r");

            // We can't use hasMoreTokens here because it will cause any leading
            // paren to be lost.
            String rest;
            try {
                rest = t.nextToken("").trim();
            } catch (NoSuchElementException e) {
                rest = null;
            }

            if ((rest != null) && rest.startsWith(":")) {
                t = new StringTokenizer(rest.substring(1));
                String classId = token;
                String lineToken = t.nextToken();

                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setParseIntegerOnly(true);
                Number n = nf.parse(lineToken);
                int lineNumber = n.intValue();

                if (t.hasMoreTokens()) {
                    printBreakpointCommandUsage(atForm, inForm);
                    return null;
                }
                try {
                    breakpoint = env.getSpecList().createBreakpoint(classId,
                                                               lineNumber);
                } catch (ClassNotFoundException exc) {
                    env.messageOutput().println("is not a valid class name", classId);
                }
            } else {
                // Try stripping method from class.method token.
                int idot = token.lastIndexOf(".");
                if ( (idot <= 0) ||                     /* No dot or dot in first char */
                     (idot >= token.length() - 1) ) { /* dot in last char */
                    printBreakpointCommandUsage(atForm, inForm);
                    return null;
                }
                String methodName = token.substring(idot + 1);
                String classId = token.substring(0, idot);
                List<String> argumentList = null;
                if (rest != null) {
                    if (!rest.startsWith("(") || !rest.endsWith(")")) {
                        env.messageOutput().println("Invalid method specification:",
                                              methodName + rest);
                        printBreakpointCommandUsage(atForm, inForm);
                        return null;
                    }
                    // Trim the parens
                    rest = rest.substring(1, rest.length() - 1);

                    argumentList = new ArrayList<String>();
                    t = new StringTokenizer(rest, ",");
                    while (t.hasMoreTokens()) {
                        argumentList.add(t.nextToken());
                    }
                }
                try {
                    breakpoint = env.getSpecList().createBreakpoint(classId,
                                                               methodName,
                                                               argumentList);
                } catch (MalformedMemberNameException exc) {
                    env.messageOutput().println("is not a valid method name", methodName);
                } catch (ClassNotFoundException exc) {
                    env.messageOutput().println("is not a valid class name", classId);
                }
            }
        } catch (Exception e) {
            printBreakpointCommandUsage(atForm, inForm);
            return null;
        }
        return breakpoint;
    }

    private EventRequest resolveNow(EventRequestSpec spec) {
        boolean success = env.getSpecList().addEagerlyResolve(spec);
        if (success && !spec.isResolved()) {
            env.messageOutput().println("Deferring.", spec.toString());
        }

        return (success && spec.isResolved())? spec.resolved() : null;
    }

    BreakpointRequest commandStop(StringTokenizer t) {
        Location bploc;
        String atIn;
        byte suspendPolicy = EventRequest.SUSPEND_ALL;

        if (t.hasMoreTokens()) {
            atIn = t.nextToken();
            if (atIn.equals("go") && t.hasMoreTokens()) {
                suspendPolicy = EventRequest.SUSPEND_NONE;
                atIn = t.nextToken();
            } else if (atIn.equals("thread") && t.hasMoreTokens()) {
                suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
                atIn = t.nextToken();
            }
        } else {
            listBreakpoints();
            return null;
        }

        BreakpointSpec spec = parseBreakpointSpec(t, "stop at", "stop in");
        if (spec != null) {
            // Enforcement of "at" vs. "in". The distinction is really
            // unnecessary and we should consider not checking for this
            // (and making "at" and "in" optional).
            if (atIn.equals("at") && spec.isMethodBreakpoint()) {
                env.messageOutput().println("Use stop at to set a breakpoint at a line number");
                printBreakpointCommandUsage("stop at", "stop in");
                return null;
            }
            spec.suspendPolicy = suspendPolicy;
            return (BreakpointRequest) resolveNow(spec);
        } else {
            return null;
        }
    }

    boolean commandClear(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            listBreakpoints();
            return true;
        }

        BreakpointSpec spec = parseBreakpointSpec(t, "clear", "clear");
        if (spec != null) {
            if (env.getSpecList().delete(spec)) {
                env.messageOutput().println("Removed:", spec.toString());
                return true;
            } else {
                env.messageOutput().println("Not found:", spec.toString());
                return false;
            }
        } else {
            return false;
        }
    }

    private List<WatchpointSpec> parseWatchpointSpec(StringTokenizer t) {
        List<WatchpointSpec> list = new ArrayList<WatchpointSpec>();
        boolean access = false;
        boolean modification = false;
        int suspendPolicy = EventRequest.SUSPEND_ALL;

        String fieldName = t.nextToken();
        if (fieldName.equals("go")) {
            suspendPolicy = EventRequest.SUSPEND_NONE;
            fieldName = t.nextToken();
        } else if (fieldName.equals("thread")) {
            suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
            fieldName = t.nextToken();
        }
        if (fieldName.equals("access")) {
            access = true;
            fieldName = t.nextToken();
        } else if (fieldName.equals("all")) {
            access = true;
            modification = true;
            fieldName = t.nextToken();
        } else {
            modification = true;
        }
        int dot = fieldName.lastIndexOf('.');
        if (dot < 0) {
            env.messageOutput().println("Class containing field must be specified.");
            return list;
        }
        String className = fieldName.substring(0, dot);
        fieldName = fieldName.substring(dot+1);

        try {
            WatchpointSpec spec;
            if (access) {
                spec = env.getSpecList().createAccessWatchpoint(className,
                                                           fieldName);
                spec.suspendPolicy = suspendPolicy;
                list.add(spec);
            }
            if (modification) {
                spec = env.getSpecList().createModificationWatchpoint(className,
                                                                 fieldName);
                spec.suspendPolicy = suspendPolicy;
                list.add(spec);
            }
        } catch (MalformedMemberNameException exc) {
            env.messageOutput().println("is not a valid field name", fieldName);
        } catch (ClassNotFoundException exc) {
            env.messageOutput().println("is not a valid class name", className);
        }
        return list;
    }

    WatchpointRequest commandWatch(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Field to watch not specified");
            return null;
        }

        WatchpointRequest req = null;
        for (WatchpointSpec spec : parseWatchpointSpec(t)) {
            req = (WatchpointRequest) resolveNow(spec);
        }
        return req;
    }

    void commandUnwatch(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Field to unwatch not specified");
            return;
        }

        for (WatchpointSpec spec : parseWatchpointSpec(t)) {
            if (env.getSpecList().delete(spec)) {
                env.messageOutput().println("Removed:", spec.toString());
            } else {
                env.messageOutput().println("Not found:", spec.toString());
            }
        }
    }

    void turnOnExitTrace(ThreadInfo threadInfo, int suspendPolicy) {
        EventRequestManager erm = env.vm().eventRequestManager();
        MethodExitRequest exit = erm.createMethodExitRequest();
        if (threadInfo != null) {
            exit.addThreadFilter(threadInfo.getThread());
        }
        env.addExcludes(exit);
        exit.setSuspendPolicy(suspendPolicy);
        exit.enable();

    }

    static String methodTraceCommand = null;

    void commandTrace(StringTokenizer t) {
        String modif;
        int suspendPolicy = EventRequest.SUSPEND_ALL;
        ThreadInfo threadInfo = null;
        String goStr = " ";

        /*
         * trace [go] methods [thread]
         * trace [go] method exit | exits [thread]
         */
        if (t.hasMoreTokens()) {
            modif = t.nextToken();
            if (modif.equals("go")) {
                suspendPolicy = EventRequest.SUSPEND_NONE;
                goStr = " go ";
                if (t.hasMoreTokens()) {
                    modif = t.nextToken();
                }
            } else if (modif.equals("thread")) {
                // this is undocumented as it doesn't work right.
                suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
                if (t.hasMoreTokens()) {
                    modif = t.nextToken();
                }
            }

            if  (modif.equals("method")) {
                String traceCmd = null;

                if (t.hasMoreTokens()) {
                    String modif1 = t.nextToken();
                    if (modif1.equals("exits") || modif1.equals("exit")) {
                        if (t.hasMoreTokens()) {
                            threadInfo = doGetThread(t.nextToken());
                        }
                        if (modif1.equals("exit")) {
                            StackFrame frame;
                            try {
                                frame = env.getCurrentThreadInfo().getCurrentFrame();
                            } catch (IncompatibleThreadStateException ee) {
                                env.messageOutput().println("Current thread isnt suspended.");
                                return;
                            }
                            env.setAtExitMethod(frame.location().method());
                            traceCmd = MessageOutput.format("trace" +
                                                    goStr + "method exit " +
                                                    "in effect for",
                                                    env.atExitMethod().toString());
                        } else {
                            traceCmd = MessageOutput.format("trace" +
                                                   goStr + "method exits " +
                                                   "in effect");
                        }
                        commandUntrace(new StringTokenizer("methods"));
                        turnOnExitTrace(threadInfo, suspendPolicy);
                        methodTraceCommand = traceCmd;
                        return;
                    }
                } else {
                   env.messageOutput().println("Can only trace");
                   return;
                }
            }
            if (modif.equals("methods")) {
                // Turn on method entry trace
                MethodEntryRequest entry;
                EventRequestManager erm = env.vm().eventRequestManager();
                if (t.hasMoreTokens()) {
                    threadInfo = doGetThread(t.nextToken());
                }
                if (threadInfo != null) {
                    /*
                     * To keep things simple we want each 'trace' to cancel
                     * previous traces.  However in this case, we don't do that
                     * to preserve backward compatibility with pre JDK 6.0.
                     * IE, you can currently do
                     *   trace   methods 0x21
                     *   trace   methods 0x22
                     * and you will get xxx traced just on those two threads
                     * But this feature is kind of broken because if you then do
                     *   untrace  0x21
                     * it turns off both traces instead of just the one.
                     * Another bogosity is that if you do
                     *   trace methods
                     *   trace methods
                     * and you will get two traces.
                     */

                    entry = erm.createMethodEntryRequest();
                    entry.addThreadFilter(threadInfo.getThread());
                } else {
                    commandUntrace(new StringTokenizer("methods"));
                    entry = erm.createMethodEntryRequest();
                }
                env.addExcludes(entry);
                entry.setSuspendPolicy(suspendPolicy);
                entry.enable();
                turnOnExitTrace(threadInfo, suspendPolicy);
                methodTraceCommand = MessageOutput.format("trace" + goStr +
                                                          "methods in effect");

                return;
            }

            env.messageOutput().println("Can only trace");
            return;
        }

        // trace all by itself.
        if (methodTraceCommand != null) {
            env.messageOutput().printDirectln(methodTraceCommand);
        }

        // More trace lines can be added here.
    }

    void commandUntrace(StringTokenizer t) {
        // untrace
        // untrace methods

        String modif = null;
        EventRequestManager erm = env.vm().eventRequestManager();
        if (t.hasMoreTokens()) {
            modif = t.nextToken();
        }
        if (modif == null || modif.equals("methods")) {
            erm.deleteEventRequests(erm.methodEntryRequests());
            erm.deleteEventRequests(erm.methodExitRequests());
            env.setAtExitMethod(null);
            methodTraceCommand = null;
        }
    }

    void commandList(StringTokenizer t) {
        StackFrame frame = null;
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("No thread specified.");
            return;
        }
        try {
            frame = threadInfo.getCurrentFrame();
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Current thread isnt suspended.");
            return;
        }

        if (frame == null) {
            env.messageOutput().println("No frames on the current call stack");
            return;
        }

        Location loc = frame.location();
        if (loc.method().isNative()) {
            env.messageOutput().println("Current method is native");
            return;
        }

        String sourceFileName = null;
        try {
            sourceFileName = loc.sourceName();

            ReferenceType refType = loc.declaringType();
            int lineno = loc.lineNumber();

            if (t.hasMoreTokens()) {
                String id = t.nextToken();

                // See if token is a line number.
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setParseIntegerOnly(true);
                    Number n = nf.parse(id);
                    lineno = n.intValue();
                } catch (java.text.ParseException jtpe) {
                    // It isn't -- see if it's a method name.
                        List<Method> meths = refType.methodsByName(id);
                        if (meths == null || meths.size() == 0) {
                            env.messageOutput().println("is not a valid line number or method name for",
                                                  new Object [] {id, refType.name()});
                            return;
                        } else if (meths.size() > 1) {
                            env.messageOutput().println("is an ambiguous method name in",
                                                  new Object [] {id, refType.name()});
                            return;
                        }
                        loc = meths.get(0).location();
                        lineno = loc.lineNumber();
                }
            }
            int startLine = Math.max(lineno - 4, 1);
            int endLine = startLine + 9;
            if (lineno < 0) {
                env.messageOutput().println("Line number information not available for");
            } else if (env.sourceLine(loc, lineno) == null) {
                env.messageOutput().println("is an invalid line number for",
                                      new Object [] {new Integer (lineno),
                                                     refType.name()});
            } else {
                for (int i = startLine; i <= endLine; i++) {
                    String sourceLine = env.sourceLine(loc, i);
                    if (sourceLine == null) {
                        break;
                    }
                    if (i == lineno) {
                        env.messageOutput().println("source line number current line and line",
                                              new Object [] {new Integer (i),
                                                             sourceLine});
                    } else {
                        env.messageOutput().println("source line number and line",
                                              new Object [] {new Integer (i),
                                                             sourceLine});
                    }
                }
            }
        } catch (AbsentInformationException e) {
            env.messageOutput().println("No source information available for:", loc.toString());
        } catch(FileNotFoundException exc) {
            env.messageOutput().println("Source file not found:", sourceFileName);
        } catch(IOException exc) {
            env.messageOutput().println("I/O exception occurred:", exc.toString());
        }
    }

    boolean commandLines(StringTokenizer t) { // Undocumented command: useful for testing
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Specify class and method");
            return false;
        } else {
            String idClass = t.nextToken();
            String idMethod = t.hasMoreTokens() ? t.nextToken() : null;
            try {
                ReferenceType refType = env.getReferenceTypeFromToken(idClass);
                if (refType != null) {
                    List<Location> lines = null;
                    if (idMethod == null) {
                        lines = refType.allLineLocations();
                    } else {
                        for (Method method : refType.allMethods()) {
                            if (method.name().equals(idMethod)) {
                                lines = method.allLineLocations();
                            }
                        }
                        if (lines == null) {
                            env.messageOutput().println("is not a valid method name", idMethod);
                            return false;
                        }
                    }
                    for (Location line : lines) {
                        env.messageOutput().printDirectln(line.toString());// Special case: use printDirectln()
                    }
                    return true;
                } else {
                    env.messageOutput().println("is not a valid id or class name", idClass);
                    return false;
                }
            } catch (AbsentInformationException e) {
                env.messageOutput().println("Line number information not available for", idClass);
                return false;
            }
        }
    }

    boolean commandClasspath(StringTokenizer t) {
        if (env.vm() instanceof PathSearchingVirtualMachine) {
            PathSearchingVirtualMachine vm = (PathSearchingVirtualMachine)env.vm();
            env.messageOutput().println("base directory:", vm.baseDirectory());
            env.messageOutput().println("classpath:", vm.classPath().toString());
            env.messageOutput().println("bootclasspath:", vm.bootClassPath().toString());
            return true;
        } else {
            env.messageOutput().println("The VM does not use paths");
            return false;
        }
    }

    /* Get or set the source file path list. */
    void commandUse(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().printDirectln(env.getSourcePath());// Special case: use printDirectln()
        } else {
            /*
             * Take the remainder of the command line, minus
             * leading or trailing whitespace.  Embedded
             * whitespace is fine.
             */
            env.setSourcePath(t.nextToken("").trim());
        }
    }

    /* Print a stack variable */
    private void printVar(LocalVariable var, Value value) {
        env.messageOutput().println("expr is value",
                              new Object [] {var.name(),
                                             value == null ? "null" : value.toString()});
    }

    /* Print all local variables in current stack frame. */
    boolean commandLocals() {
        StackFrame frame;
        ThreadInfo threadInfo = env.getCurrentThreadInfo();
        if (threadInfo == null) {
            env.messageOutput().println("No default thread specified:");
            return false;
        }
        try {
            frame = threadInfo.getCurrentFrame();
            if (frame == null) {
                throw new AbsentInformationException();
            }
            List<LocalVariable> vars = frame.visibleVariables();

            if (vars.size() == 0) {
                env.messageOutput().println("No local variables");
                return true;
            }
            Map<LocalVariable, Value> values = frame.getValues(vars);

            env.messageOutput().println("Method arguments:");
            for (LocalVariable var : vars) {
                if (var.isArgument()) {
                    Value val = values.get(var);
                    printVar(var, val);
                }
            }
            env.messageOutput().println("Local variables:");
            for (LocalVariable var : vars) {
                if (!var.isArgument()) {
                    Value val = values.get(var);
                    printVar(var, val);
                }
            }
            return true;
        } catch (AbsentInformationException aie) {
            env.messageOutput().println("Local variable information not available.");
        } catch (IncompatibleThreadStateException exc) {
            env.messageOutput().println("Current thread isnt suspended.");
        }
        return false;
    }

    private void dump(ObjectReference obj, ReferenceType refType,
                      ReferenceType refTypeBase) {
        for (Field field : refType.fields()) {
            StringBuffer o = new StringBuffer();
            o.append("    ");
            if (!refType.equals(refTypeBase)) {
                o.append(refType.name());
                o.append(".");
            }
            o.append(field.name());
            o.append(MessageOutput.format("colon space"));
            o.append(obj.getValue(field));
            env.messageOutput().printDirectln(o.toString()); // Special case: use printDirectln()
        }
        if (refType instanceof ClassType) {
            ClassType sup = ((ClassType)refType).superclass();
            if (sup != null) {
                dump(obj, sup, refTypeBase);
            }
        } else if (refType instanceof InterfaceType) {
            for (InterfaceType sup : ((InterfaceType)refType).superinterfaces()) {
                dump(obj, sup, refTypeBase);
            }
        } else {
            /* else refType is an instanceof ArrayType */
            if (obj instanceof ArrayReference) {
                for (Iterator<Value> it = ((ArrayReference)obj).getValues().iterator();
                     it.hasNext(); ) {
                    env.messageOutput().printDirect(it.next().toString());// Special case: use printDirect()
                    if (it.hasNext()) {
                        env.messageOutput().printDirect(", ");// Special case: use printDirect()
                    }
                }
                env.messageOutput().println();
            }
        }
    }

    /* Print a specified reference.
     */
    void doPrint(StringTokenizer t, boolean dumpObject) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No objects specified.");
            return;
        }

        while (t.hasMoreTokens()) {
            String expr = t.nextToken("");
            Value val = evaluate(expr);
            if (val == null) {
                env.messageOutput().println("expr is null", expr.toString());
            } else if (dumpObject && (val instanceof ObjectReference) &&
                       !(val instanceof StringReference)) {
                ObjectReference obj = (ObjectReference)val;
                ReferenceType refType = obj.referenceType();
                env.messageOutput().println("expr is value",
                                      new Object [] {expr.toString(),
                                                     MessageOutput.format("grouping begin character")});
                dump(obj, refType, refType);
                env.messageOutput().println("grouping end character");
            } else {
                  String strVal = getStringValue();
                  if (strVal != null) {
                     env.messageOutput().println("expr is value", new Object [] {expr.toString(),
                                                                      strVal});
                   }
            }
        }
    }

    void commandPrint(final StringTokenizer t, final boolean dumpObject) {
        new AsyncExecution() {
                void action() {
                    doPrint(t, dumpObject);
                }
            };
    }

    void commandSet(final StringTokenizer t) {
        String all = t.nextToken("");

        /*
         * Bare bones error checking.
         */
        if (all.indexOf('=') == -1) {
            env.messageOutput().println("Invalid assignment syntax");
            env.printPrompt();
            return;
        }

        /*
         * The set command is really just syntactic sugar. Pass it on to the
         * print command.
         */
        commandPrint(new StringTokenizer(all), false);
    }

    void doLock(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No object specified.");
            return;
        }

        String expr = t.nextToken("");
        Value val = evaluate(expr);

        try {
            if ((val != null) && (val instanceof ObjectReference)) {
                ObjectReference object = (ObjectReference)val;
                String strVal = getStringValue();
                if (strVal != null) {
                    env.messageOutput().println("Monitor information for expr",
                                      new Object [] {expr.trim(),
                                                     strVal});
                }
                ThreadReference owner = object.owningThread();
                if (owner == null) {
                    env.messageOutput().println("Not owned");
                } else {
                    env.messageOutput().println("Owned by:",
                                          new Object [] {owner.name(),
                                                         new Integer (object.entryCount())});
                }
                List<ThreadReference> waiters = object.waitingThreads();
                if (waiters.size() == 0) {
                    env.messageOutput().println("No waiters");
                } else {
                    for (ThreadReference waiter : waiters) {
                        env.messageOutput().println("Waiting thread:", waiter.name());
                    }
                }
            } else {
                env.messageOutput().println("Expression must evaluate to an object");
            }
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Threads must be suspended");
        }
    }

    void commandLock(final StringTokenizer t) {
        new AsyncExecution() {
                void action() {
                    doLock(t);
                }
            };
    }

    private void printThreadLockInfo(ThreadInfo threadInfo) {
        ThreadReference thread = threadInfo.getThread();
        try {
            env.messageOutput().println("Monitor information for thread", thread.name());
            List<ObjectReference> owned = thread.ownedMonitors();
            if (owned.size() == 0) {
                env.messageOutput().println("No monitors owned");
            } else {
                for (ObjectReference monitor : owned) {
                    env.messageOutput().println("Owned monitor:", monitor.toString());
                }
            }
            ObjectReference waiting = thread.currentContendedMonitor();
            if (waiting == null) {
                env.messageOutput().println("Not waiting for a monitor");
            } else {
                env.messageOutput().println("Waiting for monitor:", waiting.toString());
            }
        } catch (IncompatibleThreadStateException e) {
            env.messageOutput().println("Threads must be suspended");
        }
    }

    void commandThreadlocks(final StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            ThreadInfo threadInfo = env.getCurrentThreadInfo();
            if (threadInfo == null) {
                env.messageOutput().println("Current thread not set.");
            } else {
                printThreadLockInfo(threadInfo);
            }
            return;
        }
        String token = t.nextToken();
        if (token.toLowerCase().equals("all")) {
            for (ThreadInfo threadInfo : env.threads()) {
                printThreadLockInfo(threadInfo);
            }
        } else {
            ThreadInfo threadInfo = doGetThread(token);
            if (threadInfo != null) {
                env.setCurrentThreadInfo(threadInfo);
                printThreadLockInfo(threadInfo);
            }
        }
    }

    void doDisableGC(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No object specified.");
            return;
        }

        String expr = t.nextToken("");
        Value val = evaluate(expr);
        if ((val != null) && (val instanceof ObjectReference)) {
            ObjectReference object = (ObjectReference)val;
            object.disableCollection();
            String strVal = getStringValue();
            if (strVal != null) {
                 env.messageOutput().println("GC Disabled for", strVal);
            }
        } else {
            env.messageOutput().println("Expression must evaluate to an object");
        }
    }

    void commandDisableGC(final StringTokenizer t) {
        new AsyncExecution() {
                void action() {
                    doDisableGC(t);
                }
            };
    }

    void doEnableGC(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No object specified.");
            return;
        }

        String expr = t.nextToken("");
        Value val = evaluate(expr);
        if ((val != null) && (val instanceof ObjectReference)) {
            ObjectReference object = (ObjectReference)val;
            object.enableCollection();
            String strVal = getStringValue();
            if (strVal != null) {
                 env.messageOutput().println("GC Enabled for", strVal);
            }
        } else {
            env.messageOutput().println("Expression must evaluate to an object");
        }
    }

    void commandEnableGC(final StringTokenizer t) {
        new AsyncExecution() {
                void action() {
                    doEnableGC(t);
                }
            };
    }

    void doSave(StringTokenizer t) {// Undocumented command: useful for testing.
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No save index specified.");
            return;
        }

        String key = t.nextToken();

        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No expression specified.");
            return;
        }
        String expr = t.nextToken("");
        Value val = evaluate(expr);
        if (val != null) {
            env.setSavedValue(key, val);
            String strVal = getStringValue();
            if (strVal != null) {
                 env.messageOutput().println("saved", strVal);
            }
        } else {
            env.messageOutput().println("Expression cannot be void");
        }
    }

    void commandSave(final StringTokenizer t) { // Undocumented command: useful for testing.
        if (!t.hasMoreTokens()) {
            Set<String> keys = env.getSaveKeys();
            if (keys.isEmpty()) {
                env.messageOutput().println("No saved values");
                return;
            }
            for (String key : keys) {
                Value value = env.getSavedValue(key);
                if ((value instanceof ObjectReference) &&
                    ((ObjectReference)value).isCollected()) {
                    env.messageOutput().println("expr is value <collected>",
                                          new Object [] {key, value.toString()});
                } else {
                    if (value == null){
                        env.messageOutput().println("expr is null", key);
                    } else {
                        env.messageOutput().println("expr is value",
                                              new Object [] {key, value.toString()});
                    }
                }
            }
        } else {
            new AsyncExecution() {
                    void action() {
                        doSave(t);
                    }
                };
        }

    }

   void commandBytecodes(final StringTokenizer t) { // Undocumented command: useful for testing.
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No class specified.");
            return;
        }
        String className = t.nextToken();

        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No method specified.");
            return;
        }
        // Overloading is not handled here.
        String methodName = t.nextToken();

        List<ReferenceType> classes = env.vm().classesByName(className);
        // TO DO: handle multiple classes found
        if (classes.size() == 0) {
            if (className.indexOf('.') < 0) {
                env.messageOutput().println("not found (try the full name)", className);
            } else {
                env.messageOutput().println("not found", className);
            }
            return;
        }

        ReferenceType rt = classes.get(0);
        if (!(rt instanceof ClassType)) {
            env.messageOutput().println("not a class", className);
            return;
        }

        byte[] bytecodes = null;
        for (Method method : rt.methodsByName(methodName)) {
            if (!method.isAbstract()) {
                bytecodes = method.bytecodes();
                break;
            }
        }

        StringBuffer line = new StringBuffer(80);
        line.append("0000: ");
        for (int i = 0; i < bytecodes.length; i++) {
            if ((i > 0) && (i % 16 == 0)) {
                env.messageOutput().printDirectln(line.toString());// Special case: use printDirectln()
                line.setLength(0);
                line.append(String.valueOf(i));
                line.append(": ");
                int len = line.length();
                for (int j = 0; j < 6 - len; j++) {
                    line.insert(0, '0');
                }
            }
            int val = 0xff & bytecodes[i];
            String str = Integer.toHexString(val);
            if (str.length() == 1) {
                line.append('0');
            }
            line.append(str);
            line.append(' ');
        }
        if (line.length() > 6) {
            env.messageOutput().printDirectln(line.toString());// Special case: use printDirectln()
        }
    }

    void commandExclude(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().printDirectln(env.excludesString());// Special case: use printDirectln()
        } else {
            String rest = t.nextToken("");
            if (rest.equals("none")) {
                rest = "";
            }
            env.setExcludes(rest);
        }
    }

    boolean commandRedefine(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("Specify classes to redefine");
            return false;
        } else {
            String className = t.nextToken();
            List<ReferenceType> classes = env.vm().classesByName(className);
            if (classes.size() == 0) {
                env.messageOutput().println("No class named", className);
                return false;
            }
            if (classes.size() > 1) {
                env.messageOutput().println("More than one class named", className);
                return false;
            }
            env.setSourcePath(env.getSourcePath());
            ReferenceType refType = classes.get(0);
            if (!t.hasMoreTokens()) {
                env.messageOutput().println("Specify file name for class", className);
                return false;
            }
            String fileName = t.nextToken();
            File phyl = new File(fileName);
            byte[] bytes = new byte[(int)phyl.length()];
            try {
                InputStream in = new FileInputStream(phyl);
                in.read(bytes);
                in.close();
            } catch (Exception exc) {
                env.messageOutput().println("Error reading file",
                             new Object [] {fileName, exc.toString()});
                return false;
            }
            Map<ReferenceType, byte[]> map
                = new HashMap<ReferenceType, byte[]>();
            map.put(refType, bytes);
            try {
                env.vm().redefineClasses(map);
                return true;
            } catch (Throwable exc) {
                env.messageOutput().println("Error redefining class to file",
                             new Object [] {className,
                                            fileName,
                                            exc});
                return false;
            }
        }
    }

    boolean commandPopFrames(StringTokenizer t, boolean reenter) {
        ThreadInfo threadInfo;

        if (t.hasMoreTokens()) {
            String token = t.nextToken();
            threadInfo = doGetThread(token);
            if (threadInfo == null) {
                return false;
            }
        } else {
            threadInfo = env.getCurrentThreadInfo();
            if (threadInfo == null) {
                env.messageOutput().println("No thread specified.");
                return false;
            }
        }

        try {
            StackFrame frame = threadInfo.getCurrentFrame();
            threadInfo.getThread().popFrames(frame);
            threadInfo = env.getCurrentThreadInfo();
            env.setCurrentThreadInfo(threadInfo);
            if (reenter) {
                commandStepi();
            }
            return true;
        } catch (Throwable exc) {
            env.messageOutput().println("Error popping frame", exc.toString());
            return false;
        }
    }

    void commandExtension(StringTokenizer t) {
        if (!t.hasMoreTokens()) {
            env.messageOutput().println("No class specified.");
            return;
        }

        String idClass = t.nextToken();
        ReferenceType cls = env.getReferenceTypeFromToken(idClass);
        String extension = null;
        if (cls != null) {
            try {
                extension = cls.sourceDebugExtension();
                env.messageOutput().println("sourcedebugextension", extension);
            } catch (AbsentInformationException e) {
                env.messageOutput().println("No sourcedebugextension specified");
            }
        } else {
            env.messageOutput().println("is not a valid id or class name", idClass);
        }
    }

    void commandVersion(String debuggerName,
                        VirtualMachineManager vmm) {
        env.messageOutput().println("minus version",
                              new Object [] { debuggerName,
                                              new Integer(vmm.majorInterfaceVersion()),
                                              new Integer(vmm.minorInterfaceVersion()),
                                                  System.getProperty("java.version")});
        if (env.connection() != null) {
            try {
                env.messageOutput().printDirectln(env.vm().description());// Special case: use printDirectln()
            } catch (VMNotConnectedException e) {
                env.messageOutput().println("No VM connected");
            }
        }
    }
}
