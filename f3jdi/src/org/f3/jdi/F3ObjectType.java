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

import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;

/**
 * This class represents org.f3.runtime.F3Object interface type.
 *
 * @author sundar
 */
public class F3ObjectType extends F3InterfaceType {
    private Method count$Method;
    private Method get$Method;
    private Method set$Method;
    private Method getType$Method;
    private Method getFlags$Method;
    private Method setFlags$Method;

    public F3ObjectType(F3VirtualMachine f3vm, InterfaceType underlying) {
        super(f3vm, underlying);
        if (! underlying.name().equals(F3VirtualMachine.F3_OBJECT_TYPE_NAME)) {
            throw new IllegalArgumentException("Illegal underlying type: " + underlying);
        }
        count$Method = underlying.methodsByName("count$").get(0);
        get$Method = underlying.methodsByName("get$").get(0);
        set$Method = underlying.methodsByName("set$").get(0);
        getType$Method = underlying.methodsByName("getType$").get(0);
        getFlags$Method = underlying.methodsByName("getFlags$").get(0);
        setFlags$Method = underlying.methodsByName("setFlags$").get(0);
    }

    public Method count$Method() {
        return count$Method;
    }

    public Method get$Method() {
        return get$Method;
    }

    public Method set$Method() {
        return set$Method;
    }

    public Method getType$Method() {
        return getType$Method;
    }

    public Method getFlags$Method() {
        return getFlags$Method;
    }

    public Method setFlags$Method() {
        return setFlags$Method;
    }
}
