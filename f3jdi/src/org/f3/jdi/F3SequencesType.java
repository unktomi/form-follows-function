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
import com.sun.jdi.Method;

/**
 * This class represents org.f3.runtime.sequence.Sequences class type.
 *
 * @author sundar
 */
public class F3SequencesType extends F3ClassType {
    // Sequences class methods

    private Method setBooleanElementMethod;
    private Method setCharElementMethod;
    private Method setByteElementMethod;
    private Method setShortElementMethod;
    private Method setIntElementMethod;
    private Method setLongElementMethod;
    private Method setFloatElementMethod;
    private Method setDoubleElementMethod;
    private Method setObjectElementMethod;
    private static final String SET = "set";
    private static final String SIGNATURE_PREFIX = "(Lorg/f3/runtime/sequence/Sequence;";
    private static final String SIGNATURE_SUFFIX = "I)Lorg/f3/runtime/sequence/Sequence;";

    private String setElementSignature(String type) {
        return SIGNATURE_PREFIX + type + SIGNATURE_SUFFIX;
    }

    public F3SequencesType(F3VirtualMachine f3vm, ClassType underlying) {
        super(f3vm, underlying);
        if (!underlying.name().equals(F3VirtualMachine.F3_SEQUENCES_TYPE_NAME)) {
            throw new IllegalArgumentException("Illegal underlying type: " + underlying);
        }
    }

    private void init() {
        if (setBooleanElementMethod == null) {
            setBooleanElementMethod = concreteMethodByName(SET,
                    setElementSignature("Z"));
            setCharElementMethod = concreteMethodByName(SET,
                    setElementSignature("C"));
            setByteElementMethod = concreteMethodByName(SET,
                    setElementSignature("B"));
            setShortElementMethod = concreteMethodByName(SET,
                    setElementSignature("S"));
            setIntElementMethod = concreteMethodByName(SET,
                    setElementSignature("I"));
            setLongElementMethod = concreteMethodByName(SET,
                    setElementSignature("J"));
            setFloatElementMethod = concreteMethodByName(SET,
                    setElementSignature("F"));
            setDoubleElementMethod = concreteMethodByName(SET,
                    setElementSignature("D"));
            setObjectElementMethod = concreteMethodByName(SET,
                    setElementSignature("Ljava/lang/Object;"));
        }
    }

    protected Method setBooleanElementMethod() {
        init();
        return setBooleanElementMethod;
    }

    protected Method setCharElementMethod() {
        init();
        return setCharElementMethod;
    }

    protected Method setByteElementMethod() {
        init();
        return setByteElementMethod;
    }

    protected Method setShortElementMethod() {
        init();
        return setShortElementMethod;
    }

    protected Method setIntElementMethod() {
        init();
        return setIntElementMethod;
    }

    protected Method setLongElementMethod() {
        init();
        return setLongElementMethod;
    }

    protected Method setFloatElementMethod() {
        init();
        return setFloatElementMethod;
    }

    protected Method setDoubleElementMethod() {
        init();
        return setDoubleElementMethod;
    }

    protected Method setObjectElementMethod() {
        init();
        return setObjectElementMethod;
    }
}
