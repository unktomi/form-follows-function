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

package org.f3.tools.f3doc;


import com.sun.javadoc.*;

import com.sun.tools.mjavac.code.Type;


/**
 * Abstract implementation of <code>Type</code>, with useful
 * defaults for the methods in <code>Type</code> (and a couple from
 * <code>ProgramElementDoc</code>).
 *
 * @author Scott Seligman
 * @since 1.5
 */
abstract class AbstractTypeImpl implements com.sun.javadoc.Type {

    protected final DocEnv env;
    protected final Type type;

    protected AbstractTypeImpl(DocEnv env, Type type) {
        this.env = env;
        this.type = type;
    }

    public String typeName() {
        return type.tsym.name.toString();
    }

    public String qualifiedTypeName() {
        return type.tsym.getQualifiedName().toString();
    }

    public String simpleTypeName() {
        return type.tsym.name.toString();
    }

    public String name() {
        return typeName();
    }

    public String qualifiedName() {
        return qualifiedTypeName();
    }

    @Override
    public String toString() {
        return qualifiedTypeName();
    }

    public String dimension() {
        return "";
    }

    public boolean isPrimitive() {
        return false;
    }

    public ClassDoc asClassDoc() {
        return null;
    }
}
