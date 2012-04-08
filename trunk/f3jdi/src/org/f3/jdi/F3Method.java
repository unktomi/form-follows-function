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

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.Type;
import java.util.List;

/**
 *
 * @author sundar
 */
public class F3Method extends F3TypeComponent implements Method {
    public F3Method(F3VirtualMachine f3vm, Method underlying) {
        super(f3vm, underlying);
    }

    public List<Location> allLineLocations() throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().allLineLocations());
    }

    public List<Location> allLineLocations(String stratum, String sourceName) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().allLineLocations(stratum, sourceName));
    }

    public List<String> argumentTypeNames() {
        return underlying().argumentTypeNames();
    }

    public List<Type> argumentTypes() throws ClassNotLoadedException {
        return F3Wrapper.wrapTypes(virtualMachine(), underlying().argumentTypes());
    }

    public List<LocalVariable> arguments() throws AbsentInformationException {
        return F3Wrapper.wrapLocalVariables(virtualMachine(), underlying().arguments());
    }

    public byte[] bytecodes() {
        return underlying().bytecodes();
    }

    public boolean isAbstract() {
        return underlying().isAbstract();
    }

    public boolean isBridge() {
        return underlying().isBridge();
    }

    public boolean isConstructor() {
        return underlying().isConstructor();
    }

    public boolean isNative() {
        return underlying().isNative();
    }

    public boolean isObsolete() {
        return underlying().isObsolete();
    }

    public boolean isStaticInitializer() {
        return underlying().isStaticInitializer();
    }

    public boolean isSynchronized() {
        return underlying().isSynchronized();
    }

    public boolean isVarArgs() {
        return underlying().isVarArgs();
    }

    public boolean isF3Method() {
        return declaringType().isF3Type();
    }

    public boolean isF3InternalMethod() {
        return declaringType().isInternalJavaType() ||
            (isF3Method() && isInternalMethod());
    }

    public F3Location location() {
        return F3Wrapper.wrap(virtualMachine(), underlying().location());
    }

    public F3Location locationOfCodeIndex(long index) {
        return F3Wrapper.wrap(virtualMachine(), underlying().locationOfCodeIndex(index));
    }

    public List<Location> locationsOfLine(int line) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().locationsOfLine(line));
    }

    public List<Location> locationsOfLine(String stratum, String sourceName, int line) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().locationsOfLine(stratum, sourceName, line));
    }

    public F3Type returnType() throws ClassNotLoadedException {
        return F3Wrapper.wrap(virtualMachine(), underlying().returnType());
    }

    public String returnTypeName() {
        return underlying().returnTypeName();
    }

    public List<LocalVariable> variables() throws AbsentInformationException {
        return F3Wrapper.wrapLocalVariables(virtualMachine(), underlying().variables());
    }

    public List<LocalVariable> variablesByName(String name) throws AbsentInformationException {
        return F3Wrapper.wrapLocalVariables(virtualMachine(), underlying().variablesByName(name));
    }

    public int compareTo(Method o) {
        return underlying().compareTo(F3Wrapper.unwrap(o));
    }

    @Override
    protected Method underlying() {
        return (Method) super.underlying();
    }

    private static final String[] INTERNAL_METHOD_PREFIXES = {
        "getFlags$",
        "setFlags$",
        "getVOFF$",
        "getMixin$",
        "setMixin$",
        "initVars$",
        "DCNT$",
        "FCNT$",
        "VCNT$"
    };

    private static final String[] INTERNAL_METHOD_NAMES = {
        "isInitialized$internal$",
        "setInitialized$internal$",
        "varTestBits$",
        "varChangeBits$",
        "restrictSet$",
        "getThisRef$internal$",
        "setThisRef$internal$",
        "getDepChain$internal$",
        "setDepChain$internal$",
        "addDependent$",
        "removeDependent$",
        "switchDependence$",
        "notifyDependents$",
        "update$",
        "getListenerCount$",
        "count$",
        "get$",
        "set$",
        "size$",
        "invalidate$"
    };

    private boolean isInternalMethod() {
        // FIXME: is there better way to detect internal (compiler generated) method?
        String methodName = name();
        if (methodName.indexOf('$') != -1) {
            for (String mn : INTERNAL_METHOD_PREFIXES) {
                if (methodName.startsWith(mn)) {
                    return true;
                }
            }
            for (String mn : INTERNAL_METHOD_NAMES) {
                if (methodName.equals(mn)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    // not used as of now..
    // FIXME: Do we want to consider existence of LineNumberTable info
    // to detect internal methods?
    private boolean isLNTPresent() {
        try {
            allLineLocations();
            return true;
        } catch (AbsentInformationException exp) {
            return false;
        }
    }
}
