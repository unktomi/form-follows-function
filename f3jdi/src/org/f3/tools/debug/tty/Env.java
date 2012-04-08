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
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import java.util.*;
import java.io.*;


class Env {

    private EventRequestSpecList specList = new EventRequestSpecList(this);

    private boolean exitDebuggerVM = true;

    private VMConnection connection;

    private SourceMapper sourceMapper = new SourceMapper("");
    private List<String> excludes;

    private static final int SOURCE_CACHE_SIZE = 5;
    private List<SourceCode> sourceCache = new LinkedList<SourceCode>();

    private HashMap<String, Value> savedValues = new HashMap<String, Value>();
    private Method atExitMethod;
    private MessageOutput messageOutput = new MessageOutput();

    // This is a list of all known ThreadInfo objects. It survives
    // env.invalidateAllThreadInfo, unlike the other thread related fields below.
    private final List<ThreadInfo> threads = Collections.synchronizedList(new ArrayList<ThreadInfo>());
    private boolean gotInitialThreads = false;
    private ThreadInfo currentThread = null;
    private ThreadGroupReference currentThreadGroup = null;

    void init(String connectSpec, boolean openNow, int flags) {
        connection = new VMConnection(this, connectSpec, flags);
        if (!connection.isLaunch() || openNow) {
            connection.open();
        }
    }

    EventRequestSpecList getSpecList() {
        return specList;
    }

    void setExitDebuggerVM(boolean flag) {
        exitDebuggerVM = flag;
    }

    boolean getExitDebuggerVM() {
        return exitDebuggerVM;
    }

    VMConnection connection() {
        return connection;
    }

    VirtualMachine vm() {
        return connection.vm();
    }

    MessageOutput messageOutput() {
        return messageOutput;
    }

    void printPrompt() {
        messageOutput().printPrompt(getCurrentThreadInfo());
    }

    void fatalError(String messageKey) {
        messageOutput().fatalError(messageKey);
        shutdown();
    }

    void shutdown() {
        shutdown(null);
    }

    void shutdown(String message) {
        invalidateAllThreadInfo();
        threads.clear();
        
        if (connection != null) {
            try {
                connection.disposeVM();
            } catch (VMDisconnectedException e) {
                // Shutting down after the VM has gone away. This is
                // not an error, and we just ignore it.
            }
        }
        if (message != null) {
            messageOutput().lnprint(message);
            messageOutput().println();
        }
        if (exitDebuggerVM) {
            System.exit(0);
        }
    }

    void setSourcePath(String srcPath) {
        sourceMapper = new SourceMapper(srcPath);
        sourceCache.clear();
    }

    void setSourcePath(List<String> srcList) {
        sourceMapper = new SourceMapper(srcList);
        sourceCache.clear();
    }

    String getSourcePath() {
        return sourceMapper.getSourcePath();
    }

    private List<String> excludes() {
        if (excludes == null) {
            setExcludes("java.*, javax.*, sun.*, com.sun.*");
        }
        return excludes;
    }

    String excludesString() {
        StringBuffer buffer = new StringBuffer();
        for (String pattern : excludes()) {
            buffer.append(pattern);
            buffer.append(",");
        }
        return buffer.toString();
    }

    void addExcludes(StepRequest request) {
        for (String pattern : excludes()) {
            request.addClassExclusionFilter(pattern);
        }
    }

    void addExcludes(MethodEntryRequest request) {
        for (String pattern : excludes()) {
            request.addClassExclusionFilter(pattern);
        }
    }

    void addExcludes(MethodExitRequest request) {
        for (String pattern : excludes()) {
            request.addClassExclusionFilter(pattern);
        }
    }

    void setExcludes(String excludeString) {
        StringTokenizer t = new StringTokenizer(excludeString, " ,;");
        List<String> list = new ArrayList<String>();
        while (t.hasMoreTokens()) {
            list.add(t.nextToken());
        }
        excludes = list;
    }

    Method atExitMethod() {
        return atExitMethod;
    }

    void setAtExitMethod(Method mmm) {
        atExitMethod = mmm;
    }

    /**
     * Return a Reader cooresponding to the source of this location.
     * Return null if not available.
     * Note: returned reader must be closed.
     */
    BufferedReader sourceReader(Location location) {
        return sourceMapper.sourceReader(location);
    }

    synchronized String sourceLine(Location location, int lineNumber)
                                          throws IOException {
        if (lineNumber == -1) {
            throw new IllegalArgumentException();
        }

        try {
            String fileName = location.sourceName();

            Iterator<SourceCode> iter = sourceCache.iterator();
            SourceCode code = null;
            while (iter.hasNext()) {
                SourceCode candidate = iter.next();
                if (candidate.fileName().equals(fileName)) {
                    code = candidate;
                    iter.remove();
                    break;
                }
            }
            if (code == null) {
                BufferedReader reader = sourceReader(location);
                if (reader == null) {
                    throw new FileNotFoundException(fileName);
                }
                code = new SourceCode(fileName, reader);
                if (sourceCache.size() == SOURCE_CACHE_SIZE) {
                    sourceCache.remove(sourceCache.size() - 1);
                }
            }
            sourceCache.add(0, code);
            return code.sourceLine(lineNumber);
        } catch (AbsentInformationException e) {
            throw new IllegalArgumentException();
        }
    }

    /** Return a description of an object. */
    String description(ObjectReference ref) {
        ReferenceType clazz = ref.referenceType();
        long id = ref.uniqueID();
        if (clazz == null) {
            return toHex(id);
        } else {
            return MessageOutput.format("object description and hex id",
                                        new Object [] {clazz.name(),
                                                       toHex(id)});
        }
    }

    /** Convert a long to a hexadecimal string. */
    static String toHex(long n) {
        char s1[] = new char[16];
        char s2[] = new char[18];

        /* Store digits in reverse order. */
        int i = 0;
        do {
            long d = n & 0xf;
            s1[i++] = (char)((d < 10) ? ('0' + d) : ('a' + d - 10));
        } while ((n >>>= 4) > 0);

        /* Now reverse the array. */
        s2[0] = '0';
        s2[1] = 'x';
        int j = 2;
        while (--i >= 0) {
            s2[j++] = s1[i];
        }
        return new String(s2, 0, j);
    }

    /** Convert hexadecimal strings to longs. */
    static long fromHex(String hexStr) {
        String str = hexStr.startsWith("0x") ?
            hexStr.substring(2).toLowerCase() : hexStr.toLowerCase();
        if (hexStr.length() == 0) {
            throw new NumberFormatException();
        }

        long ret = 0;
        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                ret = (ret * 16) + (c - '0');
            } else if (c >= 'a' && c <= 'f') {
                ret = (ret * 16) + (c - 'a' + 10);
            } else {
                throw new NumberFormatException();
            }
        }
        return ret;
    }

    ReferenceType getReferenceTypeFromToken(String idToken) {
        ReferenceType cls = null;
        if (Character.isDigit(idToken.charAt(0))) {
            cls = null;
        } else if (idToken.startsWith("*.")) {
        // This notation saves typing by letting the user omit leading
        // package names. The first
        // loaded class whose name matches this limited regular
        // expression is selected.
        idToken = idToken.substring(1);
        for (ReferenceType type : vm().allClasses()) {
            if (type.name().endsWith(idToken)) {
                cls = type;
                break;
            }
        }
    } else {
            // It's a class name
            List<ReferenceType> classes = vm().classesByName(idToken);
            if (classes.size() > 0) {
                // TO DO: handle multiples
                cls = classes.get(0);
            }
        }
        return cls;
    }

    Set<String> getSaveKeys() {
        return savedValues.keySet();
    }

    Value getSavedValue(String key) {
        return savedValues.get(key);
    }

    void setSavedValue(String key, Value value) {
        savedValues.put(key, value);
    }

    // Current thread/threadGroup methods
    private ThreadInfo createThreadInfo(ThreadReference thread) {
        if (thread == null) {
            fatalError("Internal error: null ThreadInfo created");
        }
        return new ThreadInfo(thread);
    }

    private void initThreads() {
        if (!gotInitialThreads) {
            for (ThreadReference thread : vm().allThreads()) {
                threads.add(createThreadInfo(thread));
            }
            gotInitialThreads = true;
        }
    }

    void addThread(ThreadReference thread) {
        synchronized (threads) {
            initThreads();
            ThreadInfo ti = createThreadInfo(thread);
            // Guard against duplicates. Duplicates can happen during
            // initialization when a particular thread might be added both
            // by a thread start event and by the initial call to threads()
            if (getThreadInfo(thread) == null) {
                threads.add(ti);
            }
        }
    }

    void removeThread(ThreadReference thread) {
        if (thread.equals(currentThread)) {
            // Current thread has died.

            // Be careful getting the thread name. If its death happens
            // as part of VM termination, it may be too late to get the
            // information, and an exception will be thrown.
            String currentThreadName;
            try {
               currentThreadName = "\"" + thread.name() + "\"";
            } catch (Exception e) {
               currentThreadName = "";
            }

            setCurrentThread(null);

            messageOutput().println();
            messageOutput().println("Current thread died. Execution continuing...",
                                  currentThreadName);
        }
        threads.remove(getThreadInfo(thread));
    }

    List<ThreadInfo> threads() {
        synchronized(threads) {
            initThreads();
            // Make a copy to allow iteration without synchronization
            return new ArrayList<ThreadInfo>(threads);
        }
    }

    void invalidateAllThreadInfo() {
        currentThread = null;
        currentThreadGroup = null;
        synchronized (threads) {
            for (ThreadInfo ti : threads()) {
                ti.invalidate();
            }
        }
    }

    void setThreadGroup(ThreadGroupReference tg) {
        currentThreadGroup = tg;
    }

    void setCurrentThread(ThreadReference tr) {
        if (tr == null) {
            setCurrentThreadInfo(null);
        } else {
            ThreadInfo tinfo = getThreadInfo(tr);
            setCurrentThreadInfo(tinfo);
        }
    }

    void setCurrentThreadInfo(ThreadInfo tinfo) {
        currentThread = tinfo;
        if (currentThread != null) {
            currentThread.invalidate();
        }
    }

    /**
     * Get the ThreadInfo object.
     *
     * @return the ThreadInfo for the currentThread thread.
     */
    ThreadInfo getCurrentThreadInfo() {
        return currentThread;
    }


    ThreadGroupReference getCurrentThreadGroup() {
        if (currentThreadGroup == null) {
            // Current current thread group defaults to the first top level
            // thread group.
            setThreadGroup(vm().topLevelThreadGroups().get(0));
        }
        return currentThreadGroup;
    }

    ThreadInfo getThreadInfo(long id) {
        ThreadInfo retInfo = null;

        synchronized (threads) {
            for (ThreadInfo ti : threads()) {
                if (ti.getThread().uniqueID() == id) {
                   retInfo = ti;
                   break;
                }
            }
        }
        return retInfo;
    }

    ThreadInfo getThreadInfo(ThreadReference tr) {
        return getThreadInfo(tr.uniqueID());
    }

    ThreadInfo getThreadInfo(String idToken) {
        ThreadInfo tinfo = null;
        if (idToken.startsWith("t@")) {
            idToken = idToken.substring(2);
        }
        try {
            long threadId = Long.decode(idToken).longValue();
            tinfo = getThreadInfo(threadId);
        } catch (NumberFormatException e) {
            tinfo = null;
        }
        return tinfo;
    }

    static class SourceCode {
        private String fileName;
        private List<String> sourceLines = new ArrayList<String>();

        SourceCode(String fileName, BufferedReader reader)  throws IOException {
            this.fileName = fileName;
            try {
                String line = reader.readLine();
                while (line != null) {
                    sourceLines.add(line);
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        }

        String fileName() {
            return fileName;
        }

        String sourceLine(int number) {
            int index = number - 1; // list is 0-indexed
            if (index >= sourceLines.size()) {
                return null;
            } else {
                return sourceLines.get(index);
            }
        }
    }
}
