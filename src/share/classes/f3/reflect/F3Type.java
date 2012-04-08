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

/** A run-time representation of a F3 type.
 * Corresponds to {@code java.lang.reflect.Type}.
 *
 * @author Per Bothner
 * @profile desktop
 */

public abstract class F3Type {
    F3Type() {
    }

    /** Return name of type, or null ofr an unnamed type. */
    public String getName() {
        return null;
    }
    
    protected void toStringTerse(StringBuilder sb) {
        String name = getName();
        sb.append(name == null ? "<anonymous>" : name);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringTerse(sb);
        return sb.toString();
    }

    /** Get a {@code F3SequenceType} using this as the item type. */
    public F3SequenceType getSequenceType() {
        return new F3SequenceType(this);
    }
    
    public boolean isF3Type() {
        return true;
    }

    /** Coerce argument to this type.
     * <em>This is a placeholder - not yet implemented. </em>
     * @param val values to coerce/convert
     * @return convert, or null if cannot be coerced
     */
    public F3Value coerceOrNull (F3Value val) {
        return val; // FIXME
    }

    /** For now too conservative - if not comparing F3ClassType types,
     * uses equals.
     */
    public boolean isAssignableFrom(F3Type cls) {
        if (this instanceof F3ClassType && cls instanceof F3ClassType)
            return ((F3ClassType) this).isAssignableFrom((F3ClassType) cls);
        // FIXME
        return equals(cls);
    }
}
