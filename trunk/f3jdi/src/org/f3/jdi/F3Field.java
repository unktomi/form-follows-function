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

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;

/**
 *
 * @author sundar
 */
public class F3Field extends F3TypeComponent implements Field {
    public F3Field(F3VirtualMachine f3vm, Field underlying) {
        super(f3vm, underlying);
    }

    public boolean isEnumConstant() {
        return underlying().isEnumConstant();
    }

    public boolean isTransient() {
        return underlying().isTransient();
    }

    public boolean isVolatile() {
        return underlying().isVolatile();
    }

    public F3Type type() throws ClassNotLoadedException {
        return F3Wrapper.wrap(virtualMachine(), underlying().type());
    }

    public String typeName() {
        return underlying().typeName();
    }

    public int compareTo(Field o) {
        return underlying().compareTo(o);
    }
    
    public String toString() {
        // Need the fully qualified name of the class, and the
        // filtered name of the field.
        StringBuffer buf = new StringBuffer();

        buf.append(declaringType().name());
        buf.append('.');
        buf.append(name());
        return buf.toString();
    }
        
    @Override
    protected Field underlying() {
        return (Field) super.underlying();
    }
}
