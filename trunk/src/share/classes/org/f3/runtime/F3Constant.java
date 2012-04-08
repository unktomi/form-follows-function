/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.runtime;

/**
 * This class was copied and modified from F3Variable.java. This class ignores
 * all dependent registration methods and also the state fir dependents management.
 * If you just need to store something in an F3Object without requiring any dependent
 * notification, then you can use this class.
 *
 * Currently, this class is used when calling a bound function from a non-bind
 * context. A bound function is translated to a method that accepts F3Object+varNum
 * pair for each source function parameter and returns a Pointer value. When a
 * bound function is called from a bind context, we have pass F3Object+varNum from
 * bind call site. When a bound function is called from a non-bind site, we may
 * not have F3Object+varNum for each argument expression (for eg, literal value
 * passed as an argument). We wrap each argument with a F3Constant instance so
 * that we can pass F3Object+varNum to the bound function.
 *
 * @author A. Sundararajan
 */
public final class F3Constant extends F3Base {
    public static int VCNT$() {
        return 1;
    }
    public static int DCNT$() {
        return 0;
    }

    @Override
    public int count$() {
        return 1;
    }

    public static final int VOFF$value = 0;


    public Object $value;


    public Object get$value() {
        return $value;
    }


    public void invalidate$value(final int phase$) {
        notifyDependents$(F3Constant.VOFF$value, phase$);
    }


    @Override
    public void applyDefaults$(final int varNum$) {
    }

    @Override
    public Object get$(final int varNum$) {
        return get$value();
    }

    @Override
    public Class getType$(final int varNum$) {
        return java.lang.Object.class;
    }

    public F3Constant() {
        this(false);
        initialize$(true);
    }

    public F3Constant(final boolean dummy) {
        count$();
    }

    public static F3Constant make() {
        return new F3Constant();
    }

    public static F3Constant make(Object init) {
        F3Constant var = new F3Constant();
        var.invalidate$value(PHASE_TRANS$BE_INVALIDATE);
        var.$value = init;
        var.invalidate$value(PHASE_TRANS$BE_TRIGGER);
        return var;
    }
}
