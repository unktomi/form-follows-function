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
 * This class represents org.f3.runtime.sequence.Sequence interface type.
 *
 * @author sundar
 */
public class F3SequenceType extends F3InterfaceType {
    // Sequence interface methods
    private Method sizeMethod;
    private Method getMethod;
    private Method getAsBooleanMethod;
    private Method getAsCharMethod;
    private Method getAsByteMethod;
    private Method getAsShortMethod;
    private Method getAsIntMethod;
    private Method getAsLongMethod;
    private Method getAsFloatMethod;
    private Method getAsDoubleMethod;
    private Method getElementTypeMethod;

    public F3SequenceType(F3VirtualMachine f3vm, InterfaceType underlying) {
        super(f3vm, underlying);
        if (! underlying.name().equals(F3VirtualMachine.F3_SEQUENCE_TYPE_NAME)) {
            throw new IllegalArgumentException("Illegal underlying type: " + underlying);
        }
    }

    // If this is done in the ctor, recursion happens in wrapping a method
    private void init() {
        if (sizeMethod == null) {
            sizeMethod = methodsByName("size").get(0);
            getMethod = methodsByName("get").get(0);
            getAsBooleanMethod = methodsByName("getAsBoolean").get(0);
            getAsCharMethod = methodsByName("getAsChar").get(0);
            getAsByteMethod = methodsByName("getAsByte").get(0);
            getAsShortMethod = methodsByName("getAsShort").get(0);
            getAsIntMethod = methodsByName("getAsInt").get(0);
            getAsLongMethod = methodsByName("getAsLong").get(0);
            getAsFloatMethod = methodsByName("getAsFloat").get(0);
            getAsDoubleMethod = methodsByName("getAsDouble").get(0);
            getElementTypeMethod = methodsByName("getElementType").get(0);
        }
    }

    protected Method sizeMethod() {
        init();
        return sizeMethod;
    }

    protected Method getMethod() {
        init();
        return getMethod;
    }

    protected Method getAsBooleanMethod() {
        init();
        return getAsBooleanMethod;
    }

    protected Method getAsCharMethod() {
        init();
        return getAsCharMethod;
    }

    protected Method getAsByteMethod() {
        init();
        return getAsByteMethod;
    }

    protected Method getAsShortMethod() {
        init();
        return getAsShortMethod;
    }

    protected Method getAsIntMethod() {
        init();
        return getAsIntMethod;
    }

    protected Method getAsLongMethod() {
        init();
        return getAsLongMethod;
    }

    protected Method getAsFloatMethod() {
        init();
        return getAsFloatMethod;
    }

    protected Method getAsDoubleMethod() {
        init();
        return getAsDoubleMethod;
    }

    protected Method getElementTypeMethod() {
        init();
        return getElementTypeMethod;
    }
}
