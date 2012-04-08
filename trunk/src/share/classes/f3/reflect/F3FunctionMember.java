/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package f3.reflect;

/** A reference to a function in a class.
 * Corresponds to {@code java.lang.reflect.Method}, or
 * {@code com.sun.jdi.Methods}, respectively.
 *
 * @author Per Bothner
 * @profile desktop
 */

public abstract class F3FunctionMember implements F3Member {
    protected F3FunctionMember() {
    }

    /** Associate the method with a receiver object to yield a function. */
    public F3FunctionValue asFunction(final F3ObjectValue owner) {
        return new F3FunctionValue() {
            public F3Value apply(F3Value... arg) {
                return invoke(owner, arg);
            }
            public F3FunctionType getType() {
                return F3FunctionMember.this.getType();
            }
            public boolean isNull() { return false; }

            public String getValueString() { return "("+owner.getValueString()+")."+F3FunctionMember.this; }
        };
    }

    public abstract F3FunctionType getType();

    /** Invoke this method on the given receiver and arguments. */
    public abstract F3Value invoke(F3ObjectValue owner, F3Value... arg);
    
        
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("function ");
        F3ClassType owner = getDeclaringClass();
        if (owner != null) {
            String oname = owner.getName();
            if (oname != null) {
                sb.append(oname);
                sb.append('.');
            }
        }
        sb.append(getName());
        getType().toStringRaw(sb);
        return sb.toString();
    }
}

