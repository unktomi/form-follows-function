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

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import java.util.List;

/**
 *
 * @author sundar
 */
public class F3InterfaceType extends F3ReferenceType implements InterfaceType {
    public F3InterfaceType(F3VirtualMachine f3vm, InterfaceType ifaceType) {
        super(f3vm, ifaceType);
    }

    public List<ClassType> implementors() {
        return F3Wrapper.wrapClassTypes(virtualMachine(), underlying().implementors());
    }

    public List<InterfaceType> subinterfaces() {
        return F3Wrapper.wrapInterfaceTypes(virtualMachine(), underlying().subinterfaces());
    }

    public List<InterfaceType> superinterfaces() {
        return F3Wrapper.wrapInterfaceTypes(virtualMachine(), underlying().superinterfaces());
    }

    @Override
    protected InterfaceType underlying() {
        return (InterfaceType) super.underlying();
    }

    private boolean isIsF3TypeSet = false;
    private boolean isF3Type = false; 
    /**
     * JDI addition: Determines if this is a F3 type.
     *
     * @return <code>true</code> if this is a F3 type; false otherwise.
     */
    public boolean isF3Type() {
        if (!isIsF3TypeSet) {
            isIsF3TypeSet = true;
            F3VirtualMachine f3vm = virtualMachine();
            InterfaceType f3ObjType = (InterfaceType) F3Wrapper.unwrap(f3vm.f3ObjectType());
            if (f3ObjType != null) {
                InterfaceType thisType = underlying();
                List<InterfaceType> allIfaces = thisType.superinterfaces();
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
