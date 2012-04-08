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
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author sundar
 */
public class F3ClassType extends F3ReferenceType implements ClassType {
    private boolean isIsF3TypeSet = false;
    private boolean isF3Type = false;

    public F3ClassType(F3VirtualMachine f3vm, ClassType underlying) {
        super(f3vm, underlying);
    }

    public List<InterfaceType> allInterfaces() {
        return F3Wrapper.wrapInterfaceTypes(virtualMachine(), underlying().allInterfaces());
    }

    public F3Method concreteMethodByName(String name, String signature) {
        return F3Wrapper.wrap(virtualMachine(), underlying().concreteMethodByName(name, signature));
    }

    public List<InterfaceType> interfaces() {
        return F3Wrapper.wrapInterfaceTypes(virtualMachine(), underlying().interfaces());
    }

    public F3Value invokeMethod(ThreadReference thread, Method method, List<? extends Value> values, int options)
            throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
        Value value =
                underlying().invokeMethod(
                    F3Wrapper.unwrap(thread), F3Wrapper.unwrap(method),
                    F3Wrapper.unwrapValues(values), options);
        return F3Wrapper.wrap(virtualMachine(), value);
    }

    public boolean isEnum() {
        return underlying().isEnum();
    }

    public F3ObjectReference newInstance(ThreadReference thread, Method method, List<? extends Value> values, int options)
            throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
         ObjectReference result =
                 underlying().newInstance(
                      F3Wrapper.unwrap(thread), F3Wrapper.unwrap(method),
                      F3Wrapper.unwrapValues(values), options);
         return F3Wrapper.wrap(virtualMachine(), result);
    }

    /**
     * JDI extension:  This will call the set function if one exists via invokeMethod.
     * The call to invokeMethod is preceded by a call to {@link F3EventQueue#setEventControl(boolean)} passing true
     * and is followed by a call to {@link F3EventQueue#setEventControl(boolean)} passing false.
     *
     * If an invokeMethod Exception occurs, it is saved and can be accessed by calling 
     * {@link F3VirtualMachine#lastFieldAccessException()}.
     */
    public void setValue(Field field, Value value) throws 
        InvalidTypeException, ClassNotLoadedException {

        virtualMachine().setLastFieldAccessException(null);
        Field jdiField = F3Wrapper.unwrap(field);
        Value jdiValue = F3Wrapper.unwrap(value);
        if (!isF3Type()) {
            underlying().setValue(jdiField, jdiValue);
            return;
        }
        if (isReadOnly(field)) {
            throw new IllegalArgumentException("Error: Cannot set value of a read-only field: " + field);
        } 
        if (isBound(field)) {
            throw new IllegalArgumentException("Error: Cannot set value of a bound field: " + field);
        }

        //get$xxxx methods exist for fields except private fields which have no binders
        List<Method> mth = underlying().methodsByName("set" + jdiField.name());
        if (mth.size() == 0) {
            // there is no setter
            underlying().setValue(jdiField, jdiValue);
            return;
        }
        // there is a setter
        ArrayList<Value> args = new ArrayList<Value>(1);
        args.add(jdiValue);
        Exception theExc = null;
        F3EventQueue eq = virtualMachine().eventQueue();
        try {
            eq.setEventControl(true);
            invokeMethod(virtualMachine().uiThread(), mth.get(0), args, ClassType.INVOKE_SINGLE_THREADED);
        } catch(InvalidTypeException ee) {
            theExc = ee;
        } catch(ClassNotLoadedException ee) {
            theExc = ee;
        } catch(IncompatibleThreadStateException ee) {
            theExc = ee;
        } catch(InvocationException ee) {
            theExc = ee;
        } finally {
            eq.setEventControl(false);
        }
        // We don't have to catch IllegalArgumentException.  It is an unchecked exception for invokeMethod
        // and for getValue

        virtualMachine().setLastFieldAccessException(theExc);
    }

    public List<ClassType> subclasses() {
        return F3Wrapper.wrapClassTypes(virtualMachine(), underlying().subclasses());
    }

    public F3ClassType superclass() {
        return F3Wrapper.wrap(virtualMachine(), underlying().superclass());
    }

    @Override
    protected ClassType underlying() {
        return (ClassType) super.underlying();
    }

    /**
     * JDI addition: Determines if this is a F3 class.
     *
     * @return <code>true</code> if this is a F3 class; false otherwise.
     */
    @Override
    public boolean isF3Type() {
        if (!isIsF3TypeSet) {
            isIsF3TypeSet = true;
            F3VirtualMachine f3vm = virtualMachine();
            InterfaceType f3ObjType = (InterfaceType) F3Wrapper.unwrap(f3vm.f3ObjectType());
            if (f3ObjType != null) {
                ClassType thisType = underlying();
                List<InterfaceType> allIfaces = thisType.allInterfaces();
                for (InterfaceType iface : allIfaces) {
                    if (iface.equals(f3ObjType)) {
                        isF3Type = true;
                        break;
                    }
                }
            }
        }
        return isF3Type;
    }
}
