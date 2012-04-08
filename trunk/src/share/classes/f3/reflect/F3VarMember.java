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

/** A run-time represention of a F3 attribute in a class.
 * Corresponds to {@code java.lang.reflect.Field},
 * and {@code com.sun.jdi.Field}, respectively.
 *
 * @author Per Bothner
 * @profile desktop
 */
public abstract class F3VarMember implements F3Member {
    protected F3VarMember() {
    }

    public abstract F3Type getType();

    /** Get the offset of the attribute. */
    public abstract int getOffset();

    /** Get the value of the attribute in a specified object. */
    public abstract F3Value getValue(F3ObjectValue obj);

    /** Set the value of the attribute in a specified object. */
    public abstract void setValue(F3ObjectValue obj, F3Value newValue);

    /** Get a handle for the attribute in a specific object. */
    public F3Location getLocation(F3ObjectValue obj) {
        return new F3VarMemberLocation(obj, this);
    }

    protected abstract void initVar(F3ObjectValue instance, F3Value value);
    public abstract void initValue(F3ObjectValue obj, F3Value ref);
    
    /** Add an on replace listener to the objects var. **/
    public abstract F3ChangeListenerID addChangeListener(F3ObjectValue instance, F3ChangeListener listener);
    
    /** Remove an on replace listener from the objects var. **/
    public abstract void removeChangeListener(F3ObjectValue instance, F3ChangeListenerID id);
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("var ");
        F3ClassType owner = getDeclaringClass();
        if (owner != null) {
            String oname = owner.getName();
            if (oname != null) {
                sb.append(oname);
                sb.append('.');
            }
        }
        sb.append(getName());
        sb.append(':');
        getType().toStringTerse(sb);
        return sb.toString();
    }

    /** True if {@code public-read} was specified. */
    public abstract boolean isPublicRead();

    /** True if {@code public-init} was specified. */
    public abstract boolean isPublicInit();

    /** True if if the variable is defined with {@code def} rather than {@code var}. */
    public abstract boolean isDef();
}
