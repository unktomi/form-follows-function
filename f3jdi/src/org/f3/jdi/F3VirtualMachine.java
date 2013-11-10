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

package org.f3.jdi;

import org.f3.jdi.event.F3EventQueue;
import org.f3.jdi.request.F3EventRequestManager;
import com.sun.jdi.Type;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteType;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharType;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatType;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.LongType;
import com.sun.jdi.LongValue;
import com.sun.jdi.Method;
import com.sun.jdi.MonitorInfo;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.Value;
import com.sun.jdi.VoidType;
import com.sun.jdi.VoidValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sundar
 */
public class F3VirtualMachine extends F3Mirror implements VirtualMachine {
    public F3VirtualMachine(VirtualMachine underlying) {
        super(null, underlying);
    }

    public boolean canForceEarlyReturn() {
        return underlying().canForceEarlyReturn();
    }

    public boolean canGetClassFileVersion() {
        return underlying().canGetClassFileVersion();
    }

    public boolean canGetConstantPool() {
        return underlying().canGetConstantPool();
    }

    public boolean canGetInstanceInfo() {
        return underlying().canGetInstanceInfo();
    }

    public boolean canGetMethodReturnValues() {
        return underlying().canGetMethodReturnValues();
    }

    public boolean canGetMonitorFrameInfo() {
        return underlying().canGetMonitorFrameInfo();
    }

    public boolean canRequestMonitorEvents() {
        return underlying().canRequestMonitorEvents();
    }

    public boolean canUseSourceNameFilters() {
        return underlying().canUseSourceNameFilters();
    }

    public long[] instanceCounts(List<? extends ReferenceType> refTypes) {
        return underlying().instanceCounts(F3Wrapper.unwrapReferenceTypes(refTypes));
    }

    public VoidValue mirrorOfVoid() {
        return voidValue();
    }

    @Override
    public F3VirtualMachine virtualMachine() {
        return this;
    }

    public List<ReferenceType> allClasses() {
        return F3Wrapper.wrapReferenceTypes(this, underlying().allClasses());
    }

    public List<ThreadReference> allThreads() {
        return F3Wrapper.wrapThreads(this, underlying().allThreads());
    }

    public boolean canAddMethod() {
        return underlying().canAddMethod();
    }

    public boolean canBeModified() {
        return underlying().canBeModified();
    }

    public boolean canGetBytecodes() {
        return underlying().canGetBytecodes();
    }

    public boolean canGetCurrentContendedMonitor() {
        return underlying().canGetCurrentContendedMonitor();
    }

    public boolean canGetMonitorInfo() {
        return underlying().canGetMonitorInfo();
    }

    public boolean canGetOwnedMonitorInfo() {
        return underlying().canGetOwnedMonitorInfo();
    }

    public boolean canGetSourceDebugExtension() {
        return underlying().canGetSourceDebugExtension();
    }

    public boolean canGetSyntheticAttribute() {
        return underlying().canGetSyntheticAttribute();
    }

    public boolean canPopFrames() {
        return underlying().canPopFrames();
    }

    public boolean canRedefineClasses() {
        return underlying().canRedefineClasses();
    }

    public boolean canRequestVMDeathEvent() {
        return underlying().canRequestVMDeathEvent();
    }

    public boolean canUnrestrictedlyRedefineClasses() {
        return underlying().canUnrestrictedlyRedefineClasses();
    }

    public boolean canUseInstanceFilters() {
        return underlying().canUseInstanceFilters();
    }

    public boolean canWatchFieldAccess() {
        return underlying().canWatchFieldAccess();
    }

    public boolean canWatchFieldModification() {
        return underlying().canWatchFieldModification();
    }

    public List<ReferenceType> classesByName(String name) {
        List<ReferenceType> refTypes = underlying().classesByName(name);
        return F3Wrapper.wrapReferenceTypes(this, refTypes);
    }

    public String description() {
        return underlying().description();
    }

    public void dispose() {
        underlying().dispose();
    }

    
    private F3EventQueue evtQueue;
    public synchronized F3EventQueue eventQueue() {
        if (evtQueue == null) {
            evtQueue = F3Wrapper.wrap(this, underlying().eventQueue());
        }
        return evtQueue;
    }

    private F3EventRequestManager evtManager;
    public synchronized F3EventRequestManager eventRequestManager() {
        if (evtManager == null) {
            evtManager = F3Wrapper.wrap(this, underlying().eventRequestManager());
        }
        return evtManager;
    }

    public void exit(int exitCode) {
        underlying().exit(exitCode);
    }

    public String getDefaultStratum() {
        return underlying().getDefaultStratum();
    }

    public F3BooleanValue mirrorOf(boolean value) {
        return new F3BooleanValue(this, underlying().mirrorOf(value));
    }

    public F3ByteValue mirrorOf(byte value) {
        return new F3ByteValue(this, underlying().mirrorOf(value));
    }

    public F3CharValue mirrorOf(char value) {
        return new F3CharValue(this, underlying().mirrorOf(value));
    }

    public F3ShortValue mirrorOf(short value) {
        return new F3ShortValue(this, underlying().mirrorOf(value));
    }

    public F3IntegerValue mirrorOf(int value) {
        return new F3IntegerValue(this, underlying().mirrorOf(value));
    }

    public F3LongValue mirrorOf(long value) {
        return new F3LongValue(this, underlying().mirrorOf(value));
    }

    public F3FloatValue mirrorOf(float value) {
        return new F3FloatValue(this, underlying().mirrorOf(value));
    }

    public F3DoubleValue mirrorOf(double value) {
        return new F3DoubleValue(this, underlying().mirrorOf(value));
    }

    public StringReference mirrorOf(String value) {
        return new F3StringReference(this, underlying().mirrorOf(value));
    }

    // default values

    private F3BooleanValue booleanDefaultValue;
    protected synchronized F3BooleanValue booleanDefaultValue() {
        if (booleanDefaultValue == null) {
            booleanDefaultValue = mirrorOf(false);
        }
        return booleanDefaultValue;
    }

    private F3ByteValue byteDefaultValue;
    protected synchronized F3ByteValue byteDefaultValue() {
        if (byteDefaultValue == null) {
            byteDefaultValue = mirrorOf((byte)0);
        }
        return byteDefaultValue;
    }

    private F3CharValue charDefaultValue;
    protected synchronized F3CharValue charDefaultValue() {
        if (charDefaultValue == null) {
            charDefaultValue = mirrorOf('\u0000');
        }
        return charDefaultValue;
    }

    private F3ShortValue shortDefaultValue;
    protected synchronized F3ShortValue shortDefaultValue() {
        if (shortDefaultValue == null) {
            shortDefaultValue = mirrorOf((short)0);
        }
        return shortDefaultValue;
    }

    private F3IntegerValue integerDefaultValue;
    protected synchronized F3IntegerValue integerDefaultValue() {
        if (integerDefaultValue == null) {
            integerDefaultValue = mirrorOf(0);
        }
        return integerDefaultValue;
    }

    private F3LongValue longDefaultValue;
    protected synchronized F3LongValue longDefaultValue() {
        if (longDefaultValue == null) {
            longDefaultValue = mirrorOf(0l);
        }
        return longDefaultValue;
    }

    private F3FloatValue floatDefaultValue;
    protected synchronized F3FloatValue floatDefaultValue() {
        if (floatDefaultValue == null) {
            floatDefaultValue = mirrorOf(0.0f);
        }
        return floatDefaultValue;
    }

    private F3DoubleValue doubleDefaultValue;
    protected synchronized F3DoubleValue doubleDefaultValue() {
        if (doubleDefaultValue == null) {
            doubleDefaultValue = mirrorOf(0.0d);
        }
        return doubleDefaultValue;
    }

    //////////

    public String name() {
        return underlying().name();
    }

    public Process process() {
        return underlying().process();
    }

    public void redefineClasses(Map<? extends ReferenceType, byte[]> classBytes) {
        Map<ReferenceType, byte[]> unwrappedClassBytes = new HashMap<ReferenceType, byte[]>();
        for (Map.Entry<? extends ReferenceType, byte[]> entry : classBytes.entrySet()) {
            unwrappedClassBytes.put(F3Wrapper.unwrap(entry.getKey()), entry.getValue());
        }
        underlying().redefineClasses(unwrappedClassBytes);
    }

    public void resume() {
        underlying().resume();
    }

    public void setDebugTraceMode(int mode) {
        underlying().setDebugTraceMode(mode);
    }

    public void setDefaultStratum(String stratum) {
        underlying().setDefaultStratum(stratum);
    }

    public void suspend() {
        underlying().suspend();
    }

    public List<ThreadGroupReference> topLevelThreadGroups() {
        return F3Wrapper.wrapThreadGroups(this, underlying().topLevelThreadGroups());
    }

    public String version() {
        return underlying().version();
    }

    private F3ThreadReference cacheUiThread = null;
    /**
     * JDI addition: Return the thread upon which invokeMethods are performed to get/set fields
     *
     * @return the thread upon which invokeMethods are performed by F3-JDI to get/set fields 
     * that have getters/setters
     */
    public F3ThreadReference uiThread() {
        if (cacheUiThread == null) {
            F3Field uiThreadField = f3EntryType().fieldByName("uiThread");
            if (uiThreadField != null) {
                cacheUiThread = (F3ThreadReference) ((F3ReferenceType)f3EntryType()).getValue(uiThreadField);
            } else {
                throw new RuntimeException("org.f3.runtime.Entry.uiThread not found");
            }
        }
        return cacheUiThread;
    }

    @Override
    protected VirtualMachine underlying() {
        return (VirtualMachine) super.underlying();
    }

    // F3 types
    public static final String F3_ENTRY_TYPE_NAME = "org.f3.runtime.Entry";
    private F3ClassType f3EntryType;
    public synchronized F3ClassType f3EntryType() {
        if (f3EntryType == null) {
            List<ReferenceType> refTypes = classesByName(F3_ENTRY_TYPE_NAME);
            f3EntryType = refTypes.isEmpty() ? null : (F3ClassType) refTypes.get(0);
        }
        return f3EntryType;
    }

    public static final String F3_OBJECT_TYPE_NAME = "org.f3.runtime.F3Object";
    private F3ObjectType f3ObjectType;
    public synchronized F3ObjectType f3ObjectType() {
        if (f3ObjectType == null) {
            List<ReferenceType> refTypes = classesByName(F3_OBJECT_TYPE_NAME);
            f3ObjectType = refTypes.isEmpty() ? null : (F3ObjectType) refTypes.get(0);
        }
        return f3ObjectType;
    }

    public static final String F3_MIXIN_TYPE_NAME = "org.f3.runtime.F3Mixin";
    private F3InterfaceType f3MixinType;
    public synchronized F3ReferenceType f3MixinType() {
        if (f3MixinType == null) {
            List<ReferenceType> refTypes = classesByName(F3_MIXIN_TYPE_NAME);
            f3MixinType = refTypes.isEmpty()? null : (F3InterfaceType) refTypes.get(0);
        }
        return f3MixinType;
    }

    public static final String F3_SEQUENCE_TYPE_NAME = "org.f3.runtime.sequence.Sequence";
    private F3SequenceType f3SequenceType;
    public synchronized F3SequenceType f3SequenceType() {
        if (f3SequenceType == null) {
            List<ReferenceType> refTypes = classesByName(F3_SEQUENCE_TYPE_NAME);
            f3SequenceType = refTypes.isEmpty() ? null : (F3SequenceType) refTypes.get(0);
        }
        return f3SequenceType;
    }

    public static final String F3_SEQUENCES_TYPE_NAME = "org.f3.runtime.sequence.Sequences";
    private F3SequencesType f3SequencesType;
    public synchronized F3SequencesType f3SequencesType() {
        if (f3SequencesType == null) {
            List<ReferenceType> refTypes = classesByName(F3_SEQUENCES_TYPE_NAME);
            if (refTypes.isEmpty()) {
                // ensure that the debuggee has loaded and initialized Sequences type
                f3SequencesType = (F3SequencesType) classType(initSequencesType());
            } else {
                f3SequencesType = (F3SequencesType) refTypes.get(0);
            }
        }
        return f3SequencesType;
    }

    private F3VoidValue voidValue;
    protected synchronized F3VoidValue voidValue() {
        if (voidValue == null) {
            voidValue = new F3VoidValue(this, underlying().mirrorOfVoid());
        }
        return voidValue;
    }

    // wrapper methods

    // primitive type accessors
    private F3VoidType voidType;
    protected synchronized F3VoidType voidType(VoidType vt) {
        if (voidType == null) {
            voidType = new F3VoidType(this, vt);
        }
        return voidType;
    }

    private F3BooleanType booleanType;
    protected synchronized F3BooleanType booleanType(BooleanType bt) {
        if (booleanType == null) {
            booleanType = new F3BooleanType(this, bt);
        }
        return booleanType;
    }

    private F3CharType charType;
    protected synchronized F3CharType charType(CharType ct) {
        if (charType == null) {
            charType = new F3CharType(this, ct);
        }
        return charType;
    }

    private F3ByteType byteType;
    protected synchronized F3ByteType byteType(ByteType bt) {
        if (byteType == null) {
            byteType = new F3ByteType(this, bt);
        }
        return byteType;
    }

    private F3ShortType shortType;
    protected synchronized F3ShortType shortType(ShortType st) {
        if (shortType == null) {
            shortType = new F3ShortType(this, st);
        }
        return shortType;
    }

    private F3IntegerType integerType;
    protected synchronized F3IntegerType integerType(IntegerType it) {
        if (integerType == null) {
            integerType = new F3IntegerType(this, it);
        }
        return integerType;
    }

    private F3LongType longType;
    protected synchronized F3LongType longType(LongType lt) {
        if (longType == null) {
            longType = new F3LongType(this, lt);
        }
        return longType;
    }

    private F3FloatType floatType;
    protected synchronized F3FloatType floatType(FloatType ft) {
        if (floatType == null) {
            floatType = new F3FloatType(this, ft);
        }
        return floatType;
    }


    private F3DoubleType doubleType;
    protected synchronized F3DoubleType doubleType(DoubleType dt) {
        if (doubleType == null) {
            doubleType = new F3DoubleType(this, dt);
        }
        return doubleType;
    }

    protected F3Location location(Location loc) {
        return new F3Location(this, loc);
    }

    private final Map<ReferenceType, F3ReferenceType> refTypesCache =
            new HashMap<ReferenceType, F3ReferenceType>();

    protected F3ReferenceType referenceType(ReferenceType rt) {
        synchronized (refTypesCache) {
            if (! refTypesCache.containsKey(rt)) {
                refTypesCache.put(rt, new F3ReferenceType(this, rt));
            }
            return refTypesCache.get(rt);
        }
    }

    protected F3ClassType classType(ClassType ct) {
        synchronized (refTypesCache) {
            if (! refTypesCache.containsKey(ct)) {
                String name = ct.name();
                if (name.equals(F3_SEQUENCES_TYPE_NAME)) {
                    refTypesCache.put(ct, new F3SequencesType(this, ct));
                } else {
                    refTypesCache.put(ct, new F3ClassType(this, ct));
                }
            }
            return (F3ClassType) refTypesCache.get(ct);
        }
    }

    protected F3InterfaceType interfaceType(InterfaceType it) {
        synchronized (refTypesCache) {
            if (! refTypesCache.containsKey(it)) {
                String name = it.name();
                if (name.equals(F3_OBJECT_TYPE_NAME)) {
                   refTypesCache.put(it, new F3ObjectType(this, it));
                } else if (name.equals(F3_SEQUENCE_TYPE_NAME)) {
                   refTypesCache.put(it, new F3SequenceType(this, it));
                } else {
                   refTypesCache.put(it, new F3InterfaceType(this, it));
                }
            }
            return (F3InterfaceType) refTypesCache.get(it);
        }
    }

    protected F3ArrayType arrayType(ArrayType at) {
        synchronized (at) {
            if (! refTypesCache.containsKey(at)) {
                refTypesCache.put(at, new F3ArrayType(this, at));
            }
            return (F3ArrayType) refTypesCache.get(at);
        }
    }

    protected F3Field field(Field field) {
        return new F3Field(this, field);
    }

    protected F3Method method(Method method) {
        return new F3Method(this, method);
    }

    protected F3LocalVariable localVariable(LocalVariable var) {
        return new F3LocalVariable(this, var);
    }

    protected F3BooleanValue booleanValue(BooleanValue value) {
        return new F3BooleanValue(this, value);
    }

    protected F3CharValue charValue(CharValue value) {
        return new F3CharValue(this, value);
    }

    protected F3ByteValue byteValue(ByteValue value) {
        return new F3ByteValue(this, value);
    }

    protected F3ShortValue shortValue(ShortValue value) {
        return new F3ShortValue(this, value);
    }

    protected F3IntegerValue integerValue(IntegerValue value) {
        return new F3IntegerValue(this, value);
    }

    protected F3LongValue longValue(LongValue value) {
        return new F3LongValue(this, value);
    }

    protected F3FloatValue floatValue(FloatValue value) {
        return new F3FloatValue(this, value);
    }

    protected F3DoubleValue doubleValue(DoubleValue value) {
        return new F3DoubleValue(this, value);
    }

    protected F3ObjectReference objectReference(ObjectReference ref) {
        ReferenceType rt = ref.referenceType();
        if (rt instanceof ClassType) {
            ClassType ct = (ClassType) rt;
            boolean isSeq =  ct.allInterfaces().contains(F3Wrapper.unwrap(f3SequenceType()));
            if (isSeq) {
                return new F3SequenceReference(this, ref);
            }
        }
        return new F3ObjectReference(this, ref);
    }

    protected F3ThreadReference threadReference(ThreadReference tref) {
        return new F3ThreadReference(this, tref);
    }

    protected F3ThreadGroupReference threadGroupReference(ThreadGroupReference tgref) {
        return new F3ThreadGroupReference(this, tgref);
    }

    protected F3StringReference stringReference(StringReference sref) {
        return new F3StringReference(this, sref);
    }

    protected F3ClassLoaderReference classLoaderReference(ClassLoaderReference clref) {
        return new F3ClassLoaderReference(this, clref);
    }

    protected F3ClassObjectReference classObjectReference(ClassObjectReference coref) {
        return new F3ClassObjectReference(this, coref);
    }

    protected F3ArrayReference arrayReference(ArrayReference aref) {
        return new F3ArrayReference(this, aref);
    }

    protected F3MonitorInfo monitorInfo(MonitorInfo monitorInfo) {
        return new F3MonitorInfo(this, monitorInfo);
    }

    protected F3StackFrame stackFrame(StackFrame frame) {
        return new F3StackFrame(this, frame);
    }

    protected Exception lastFieldAccessException = null;
    protected void setLastFieldAccessException(Exception ee) {
        lastFieldAccessException = ee;
    }

    /**
     * JDI addition: Return the exception thrown by an invokeMethod call that was 
     * performed in the most recent setValue, getValue, or getValues method call.
     * 
     * @return the exception thrown by an invokeMethod call that was
     * performed in the most recent setValue, getValue, or getValues method call, or
     * null if no such exception was thrown.
     */
    public Exception lastFieldAccessException() {
        return lastFieldAccessException;
    }

    // cache these masks 
    private int invalidFlagMask = 0;
    private int readOnlyFlagMask = 0;
    private int boundFlagMask = 0;
    private int getFlagMask(String maskName) {
        int flagMask = 0;
        // we only work with underlying JDI objects here
        List<ReferenceType> rtx =  this.underlying().classesByName("org.f3.runtime.F3Object");
        if (rtx.size() != 1) {
            System.out.println("Can't find the ReferenceType for org.f3.runtime.F3Object");
            return 0;
        }
        ReferenceType f3ObjectRefType = rtx.get(0);
        Field fieldx = f3ObjectRefType.fieldByName(maskName);
        Value flagValue = f3ObjectRefType.getValue(fieldx);
        return ((IntegerValue)flagValue).value();
    }

    protected int F3ReadOnlyFlagMask() {
        if (readOnlyFlagMask == 0) {
            readOnlyFlagMask = getFlagMask("VFLGS$IS_READONLY");
        }
        return readOnlyFlagMask;
    }

    protected int F3InvalidFlagMask() {
        if (invalidFlagMask == 0) {
            invalidFlagMask = getFlagMask("VFLGS$IS_BOUND_INVALID");
        }
        return invalidFlagMask;
    }

    protected int F3BoundFlagMask() {
        if (boundFlagMask == 0) {
            boundFlagMask = getFlagMask("VFLGS$IS_BOUND");
        }
        return boundFlagMask;
    }
    
    protected Value defaultValue(Type type) {
        if (type instanceof BooleanType) {
            return booleanDefaultValue();
        }
        if (type instanceof ByteType) {
            return byteDefaultValue();
        }
        if (type instanceof CharType) {
            return charDefaultValue();
        }
        if (type instanceof DoubleType) {
            return doubleDefaultValue();
        }
        if (type instanceof FloatType) {
            return floatDefaultValue();
        }
        if (type instanceof IntegerType) {
            return integerDefaultValue();
        }
        if (type instanceof LongType) {
            return longDefaultValue();
        }
        if (type instanceof ShortType) {
            return shortDefaultValue();
        }
        // else it is an object/array/sequence/...
        return null;
    }

    // ensure that the debuggee VM has loaded and initialized Sequences type
    private synchronized ClassType initSequencesType() {
        VirtualMachine vm = underlying();
        ClassType classType = (ClassType) vm.classesByName("java.lang.Class").get(0);
        Method forName = classType.concreteMethodByName("forName",
                "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;");

        // Class.forName(F3_SEQUENCES_TYPE_NAME, true, Entry.class.getClassLoader());
        try {
            List<Value> args = new ArrayList<Value>(3);
            args.add(vm.mirrorOf(F3_SEQUENCES_TYPE_NAME));
            args.add(vm.mirrorOf(true));
            args.add(F3Wrapper.unwrap(f3EntryType().classLoader()));
            ClassObjectReference retVal = (ClassObjectReference)classType.invokeMethod(
                                                 F3Wrapper.unwrap(uiThread()), forName, args, 0);
            // retVal must be a ClassObjectReference for the Sequences class
            return (ClassType)retVal.reflectedType();
        } catch (RuntimeException exp) {
            throw exp;
        } catch (Exception exp) {
            // exp.printStackTrace();
            throw new RuntimeException(exp);
        }
    }
}
