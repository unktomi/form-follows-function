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
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidType;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sundar
 */
public class F3Wrapper {
    public static F3VirtualMachine wrap(VirtualMachine vm) {
        return (vm == null)? null : new F3VirtualMachine(vm);
    }

    public static List<VirtualMachine> wrapVirtualMachines(List<VirtualMachine> vms) {
        List<VirtualMachine> res = new ArrayList<VirtualMachine>(vms.size());
        for (VirtualMachine vm : vms) {
            res.add(wrap(vm));
        }
        return res;
    }

    public static F3Type wrap(F3VirtualMachine f3vm, Type type) {
        if (type == null) {
            return null;
        }
        
        if (type instanceof VoidType) {
            return f3vm.voidType((VoidType)type);
        } else if (type instanceof PrimitiveType) {
            if (type instanceof BooleanType) {
                return f3vm.booleanType((BooleanType)type);
            } else if (type instanceof CharType) {
                return f3vm.charType((CharType)type);
            } else if (type instanceof ByteType) {
                return f3vm.byteType((ByteType)type);
            } else if (type instanceof ShortType) {
                return f3vm.shortType((ShortType)type);
            } else if (type instanceof IntegerType) {
                return f3vm.integerType((IntegerType)type);
            } else if (type instanceof LongType) {
                return f3vm.longType((LongType)type);
            } else if (type instanceof FloatType) {
                return f3vm.floatType((FloatType)type);
            } else if (type instanceof DoubleType) {
                return f3vm.doubleType((DoubleType)type);
            } else {
                throw new IllegalArgumentException("illegal primitive type : " + type);
            }
        } else if (type instanceof ReferenceType) {
            return wrap(f3vm, (ReferenceType)type);
        } else {
            throw new IllegalArgumentException("illegal type: " + type);
        }
    }

    public static List<Type> wrapTypes(F3VirtualMachine f3vm, List<Type> types) {
        if (types == null) {
            return null;
        }
        List<Type> result = new ArrayList<Type>(types.size());
        for (Type type : types) {
            result.add(wrap(f3vm, type));
        }
        return result;
    }

    public static F3ReferenceType wrap(F3VirtualMachine f3vm, ReferenceType rt) {
        if (rt == null) {
            return null;
        } else if (rt instanceof ClassType) {
            return f3vm.classType((ClassType)rt);
        } else if (rt instanceof InterfaceType) {
            return f3vm.interfaceType((InterfaceType)rt);
        } else if (rt instanceof ArrayType) {
            return f3vm.arrayType((ArrayType)rt);
        } else {
            return f3vm.referenceType(rt);
        }
    }

    public static F3ClassType wrap(F3VirtualMachine f3vm, ClassType ct) {
        return (ct == null)? null : f3vm.classType(ct);
    }

    public static F3InterfaceType wrap(F3VirtualMachine f3vm, InterfaceType it) {
        return (it == null)? null : f3vm.interfaceType(it);
    }

    public static F3ArrayType wrap(F3VirtualMachine f3vm, ArrayType at) {
        return (at == null)? null : f3vm.arrayType(at);
    }

    public static List<ReferenceType> wrapReferenceTypes(F3VirtualMachine f3vm, List<ReferenceType> refTypes) {
        // Note that VirtualMachineImpl caches the list, and returns an unmodifiable wrapped list.
        // Classes that get loaded in the future are added to its list by an EventListener on ClassPrepared 
        // events.  If we cache our wrapped list, they we would have to do the same thing, or be able
        // to update our cached list when this method is called again.  So for the time being,
        // we won't cache and thus don't have to return an unmodifiable list.
        if (refTypes == null) {
            return null;
        }
        List<ReferenceType> result = new ArrayList<ReferenceType>(refTypes.size());
        for (ReferenceType rt : refTypes) {
            String className = rt.name();
            // f3 generated clases contain $[1-9]Local$ or $ObjLit$
            if (className.indexOf('$') != -1) {
                if (className.indexOf("$ObjLit$") != -1) {
                    continue;
                }
                if (className.matches(".*\\$[0-9]+Local\\$.*")) {
                    continue;
                }
            }
            result.add(F3Wrapper.wrap(f3vm, rt));
        }
        return result;
    }

    public static List<ClassType> wrapClassTypes(F3VirtualMachine f3vm, List<ClassType> classes) {
        if (classes == null) {
            return null;
        }
        List<ClassType> result = new ArrayList<ClassType>(classes.size());
        for (ClassType ct : classes) {
            result.add(F3Wrapper.wrap(f3vm, ct));
        }
        return result;
    }

    public static List<InterfaceType> wrapInterfaceTypes(F3VirtualMachine f3vm, List<InterfaceType> interfaces) {
        if (interfaces == null) {
            return null;
        }
        List<InterfaceType> result = new ArrayList<InterfaceType>(interfaces.size());
        for (InterfaceType it : interfaces) {
            result.add(F3Wrapper.wrap(f3vm, it));
        }
        return result;
    }

    public static F3Location wrap(F3VirtualMachine f3vm, Location loc) {
        return (loc == null)? null : f3vm.location(loc);
    }

    public static List<Location> wrapLocations(F3VirtualMachine f3vm, List<Location> locations) {
        if (locations == null) {
            return null;
        }
        List<Location> result = new ArrayList<Location>(locations.size());
        for (Location loc: locations) {
            result.add(wrap(f3vm, loc));
        }
        return result;
    }

    public static F3Field wrap(F3VirtualMachine f3vm, Field field) {
        return (field == null)? null : f3vm.field(field);
    }

    /*
     * The fields are JDI Fields.
     * Each field can be a user field of an F3 class, an internal field of an F3 class,
     * or a field of a Java class.
     */
    public static List<Field> wrapFields(F3VirtualMachine f3vm, List<Field> fields) {
        // Create F3Field wrappers for each field that is a valid F3 field.
        if (fields == null) {
            return null;
        }
        // We will have far fewer fields than fields.size() due to all the VFLGS etc
        // fields we will discard , so start with some small random amount
        List<Field> result = new ArrayList<Field>(20);

        for (Field fld : fields) {
            String fldName = fld.name();
            int firstDollar = fldName.indexOf('$');
            // java names do not start with $.
            // F3 user names start with a $ but so do various internal names
            // mixin vars are mangled with the mixin classname, et,   $MixinClassName$fieldName
            if (firstDollar != -1) {
                if ((fldName.indexOf("_$",1)    != -1) ||
                    (fldName.indexOf("$$")      != -1) ||
                    (fldName.indexOf("$helper$") == 0) ||
                    (fldName.indexOf("$script$") == 0) ||
                    (fldName.indexOf("$ol$")    != -1)) {
                    // $ol$ means it is a shredded name from a bound obj lit (see F3Lower.java)
                    // _$ means it is a synth var (see F3PreTranslationSupport.java)
                    // $helper$ is in F3Defs.java
                    continue;
                }
            }

            if (fldName.equals("$assertionsDisabled") && fld.declaringType().name().equals("org.f3.runtime.F3Base")) {
                continue;
            }
            /*
              - mixin fields are named $MixinClassName$fieldName
              - a private script field is java private, and is named with its normal name 
              UNLESS it is referenced in a subclass. In this case it is java public and
              its name is $ClassName$fieldName.  
              This mangling in of the classname is not yet handled.
            */
            if (firstDollar <= 0) {
                result.add(f3vm.field(fld));
            }
        }
        return result;
    }

    public static F3Method wrap(F3VirtualMachine f3vm, Method method) {
        return (method == null)? null : f3vm.method(method);
    }

    public static List<Method> wrapMethods(F3VirtualMachine f3vm, List<Method> methods) {
        if (methods == null) {
            return null;
        }
        List<Method> result = new ArrayList<Method>(20);
        for (Method mth : methods) {
            F3Method f3m = f3vm.method(mth);
            if (!f3m.isF3InternalMethod()) {
                result.add(f3m);
            }
        }
        return result;
    }

    public static F3MonitorInfo wrap(F3VirtualMachine f3vm, MonitorInfo monitorInfo) {
        return (monitorInfo == null)? null : f3vm.monitorInfo(monitorInfo);
    }

    public static List<MonitorInfo> wrapMonitorInfos(F3VirtualMachine f3vm, List<MonitorInfo> monInfos) {
        if (monInfos == null) {
            return null;
        }
        List<MonitorInfo> result = new ArrayList<MonitorInfo>(monInfos.size());
        for (MonitorInfo mi : monInfos) {
            result.add(wrap(f3vm, mi));
        }
        return result;
    }

    public static F3StackFrame wrap(F3VirtualMachine f3vm, StackFrame frame) {
        return (frame == null)? null : f3vm.stackFrame(frame);
    }

    public static List<StackFrame> wrapFrames(F3VirtualMachine f3vm, List<StackFrame> frames) {
        if (frames == null) {
            return null;
        }
        List<StackFrame> result = new ArrayList<StackFrame>(frames.size());
        for (StackFrame fr : frames) {
            result.add(wrap(f3vm, fr));
        }
        return result;
    }

    public static F3LocalVariable wrap(F3VirtualMachine f3vm, LocalVariable var) {
        return (var == null)? null : f3vm.localVariable(var);
    }

    public static List<LocalVariable> wrapLocalVariables(F3VirtualMachine f3vm, List<LocalVariable> locals) {
        if (locals == null) {
            return null;
        }
        List<LocalVariable> result = new ArrayList<LocalVariable>(locals.size());
        for (LocalVariable var: locals) {
            result.add(wrap(f3vm, var));
        }
        return result;
    }

    public static F3Value wrap(F3VirtualMachine f3vm, Value value) {
        if (value == null) {
            return null;
        }

        if (value instanceof PrimitiveValue) {
            if (value instanceof BooleanValue) {
                return f3vm.booleanValue((BooleanValue)value);
            } else if (value instanceof CharValue) {
                return f3vm.charValue((CharValue)value);
            } else if (value instanceof ByteValue) {
                return f3vm.byteValue((ByteValue)value);
            } else if (value instanceof ShortValue) {
                return f3vm.shortValue((ShortValue)value);
            } else if (value instanceof IntegerValue) {
                return f3vm.integerValue((IntegerValue)value);
            } else if (value instanceof LongValue) {
                return f3vm.longValue((LongValue)value);
            } else if (value instanceof FloatValue) {
                return f3vm.floatValue((FloatValue)value);
            } else if (value instanceof DoubleValue) {
                return f3vm.doubleValue((DoubleValue)value);
            } else {
                throw new IllegalArgumentException("illegal primitive value : " + value);
            }
        } else if (value instanceof VoidValue) {
            return f3vm.voidValue();
        } else if (value instanceof ObjectReference) {
            return  wrap(f3vm, (ObjectReference)value);
        } else {
            throw new IllegalArgumentException("illegal value: " + value);
        }
    }

    public static List<ObjectReference> wrapObjectReferences(F3VirtualMachine f3vm, List<ObjectReference> refs) {
        if (refs == null) {
            return null;
        }
        List<ObjectReference> result = new ArrayList<ObjectReference>(refs.size());
        for (ObjectReference ref : refs) {
            result.add(wrap(f3vm, ref));
        }
        return result;
    }


    public static F3ObjectReference wrap(F3VirtualMachine f3vm, ObjectReference ref) {
        if (ref == null) {
            return null;
        } else if (ref instanceof ArrayReference) {
            return f3vm.arrayReference((ArrayReference)ref);
        } else if (ref instanceof StringReference) {
            return f3vm.stringReference((StringReference)ref);
        } else if (ref instanceof ThreadReference) {
            return f3vm.threadReference((ThreadReference)ref);
        } else if (ref instanceof ThreadGroupReference) {
            return f3vm.threadGroupReference((ThreadGroupReference)ref);
        } else if (ref instanceof ClassLoaderReference) {
            return f3vm.classLoaderReference((ClassLoaderReference)ref);
        } else if (ref instanceof ClassObjectReference) {
            return f3vm.classObjectReference((ClassObjectReference)ref);
        } else {
            return f3vm.objectReference(ref);
        }
    }

    public static F3ArrayReference wrap(F3VirtualMachine f3vm, ArrayReference ref) {
        return (ref == null)? null : f3vm.arrayReference(ref);
    }

    public static F3ThreadReference wrap(F3VirtualMachine f3vm, ThreadReference ref) {
        return (ref == null)? null : f3vm.threadReference(ref);
    }


    public static F3ThreadGroupReference wrap(F3VirtualMachine f3vm, ThreadGroupReference ref) {
        return (ref == null)? null : f3vm.threadGroupReference(ref);
    }

    public static List<ThreadReference> wrapThreads(F3VirtualMachine f3vm, List<ThreadReference> threads) {
        if (threads == null) {
            return null;
        }
        List<ThreadReference> result = new ArrayList<ThreadReference>(threads.size());
        for (ThreadReference tref : threads) {
            result.add(wrap(f3vm, tref));
        }
        return result;
    }

    public static List<ThreadGroupReference> wrapThreadGroups(F3VirtualMachine f3vm, List<ThreadGroupReference> threadGroups) {
        if (threadGroups == null) {
            return null;
        }
        List<ThreadGroupReference> result = new ArrayList<ThreadGroupReference>(threadGroups.size());
        for (ThreadGroupReference tref : threadGroups) {
            result.add(wrap(f3vm, tref));
        }
        return result;
    }

    public static F3ClassLoaderReference wrap(F3VirtualMachine f3vm, ClassLoaderReference ref) {
        return (ref == null)? null : f3vm.classLoaderReference(ref);
    }

    public static F3ClassObjectReference wrap(F3VirtualMachine f3vm, ClassObjectReference ref) {
        return (ref == null)? null : f3vm.classObjectReference(ref);
    }

    public static List<Value> wrapValues(F3VirtualMachine f3vm, List<Value> values) {
        if (values == null) {
            return null;
        }
        List<Value> result = new ArrayList<Value>(values.size());
        for (Value v : values) {
            result.add(wrap(f3vm, v));
        }
        return result;
    }

    public static Location unwrap(Location loc) {
        return (loc instanceof F3Location)? ((F3Location)loc).underlying() : loc;
    }

    public static StackFrame unwrap(StackFrame frame) {
        return (frame instanceof F3StackFrame)? ((F3StackFrame)frame).underlying() : frame;
    }

    public static LocalVariable unwrap(LocalVariable var) {
        return (var instanceof F3LocalVariable)? ((F3LocalVariable)var).underlying() : var;
    }
    
    public static Value unwrap(Value value) {
        return (value instanceof F3Value)? ((F3Value)value).underlying() : value;
    }

    public static List<? extends Value> unwrapValues(List<? extends Value> values) {
        if (values == null) {
            return null;
        }
        List<Value> result = new ArrayList<Value>(values.size());
        for (Value v : values) {
            result.add(unwrap(v));
        }
        return result;
    }

    public static Field unwrap(Field field) {
        return (field instanceof F3Field)? ((F3Field)field).underlying() : field;
    }

    public static Method unwrap(Method method) {
        return (method instanceof F3Method)? ((F3Method)method).underlying() : method;
    }

    public static ObjectReference unwrap(ObjectReference ref) {
        return (ref instanceof F3ObjectReference)? ((F3ObjectReference)ref).underlying() : ref;
    }

    public static ThreadReference unwrap(ThreadReference ref) {
        return (ref instanceof F3ThreadReference)? ((F3ThreadReference)ref).underlying() : ref;
    }

    public static ReferenceType unwrap(ReferenceType rt) {
        return (rt instanceof F3ReferenceType)? ((F3ReferenceType)rt).underlying() : rt;
    }

    public static List<? extends ReferenceType> unwrapReferenceTypes(List<? extends ReferenceType> refTypes) {
        if (refTypes == null) {
            return null;
        }
        List<ReferenceType> result = new ArrayList<ReferenceType>(refTypes.size());
        for (ReferenceType rt : refTypes) {
            result.add(unwrap(rt));
        }
        return result;
    }

    // event requests
    public static F3EventRequestManager wrap(F3VirtualMachine f3vm, EventRequestManager man) {
        return (man == null)? null : new F3EventRequestManager(f3vm, man);
    }

    // event queue
    public static F3EventQueue wrap(F3VirtualMachine f3vm, EventQueue evtQueue) {
        return (evtQueue == null)? null : new F3EventQueue(f3vm, evtQueue);
    }
}
