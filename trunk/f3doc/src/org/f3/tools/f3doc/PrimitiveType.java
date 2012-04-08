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

class PrimitiveType implements com.sun.javadoc.Type {

    private final String name;

    static final PrimitiveType voidType = new PrimitiveType("Void");
    static final PrimitiveType booleanType = new PrimitiveType("Boolean");
    static final PrimitiveType charType = new PrimitiveType("Character");
    static final PrimitiveType byteType = new PrimitiveType("Byte");
    static final PrimitiveType shortType = new PrimitiveType("Short");
    static final PrimitiveType intType = new PrimitiveType("Integer");
    static final PrimitiveType longType = new PrimitiveType("Long");
    static final PrimitiveType floatType = new PrimitiveType("Float");
    static final PrimitiveType doubleType = new PrimitiveType("Double");
    static final PrimitiveType numberType = new PrimitiveType("Number");

    // error type, should never actually be used
    static final PrimitiveType errorType = new PrimitiveType("");

    PrimitiveType(String name) {
        this.name = name;
    }

    /**
     * Return unqualified name of type excluding any dimension information.
     * <p>
     * For example, a two dimensional array of String returns 'String'.
     */
    public String typeName() {
        return name;
    }

    /**
     * Return qualified name of type excluding any dimension information.
     *<p>
     * For example, a two dimensional array of String
     * returns 'java.lang.String'.
     */
    public String qualifiedTypeName() {
        return name;
    }

    /**
     * Return the simple name of this type.
     */
    public String simpleTypeName() {
        return name;
    }

    /**
     * Return the type's dimension information, as a string.
     * <p>
     * For example, a two dimensional array of String returns '[][]'.
     */
    public String dimension() {
        return "";
    }

    /**
     * Return this type as a class.  Array dimensions are ignored.
     *
     * @return a ClassDocImpl if the type is a Class.
     * Return null if it is a primitive type..
     */
    public ClassDoc asClassDoc() {
        return null;
    }

    /**
     * Return null, as this is not an annotation type.
     */
    public AnnotationTypeDoc asAnnotationTypeDoc() {
        return null;
    }

    /**
     * Return null, as this is not an instantiation.
     */
    public ParameterizedType asParameterizedType() {
        return null;
    }

    /**
     * Return null, as this is not a type variable.
     */
    public TypeVariable asTypeVariable() {
        return null;
    }

    /**
     * Return null, as this is not a wildcard type;
     */
    public WildcardType asWildcardType() {
        return null;
    }

    /**
     * Returns a string representation of the type.
     *
     * Return name of type including any dimension information.
     * <p>
     * For example, a two dimensional array of String returns
     * <code>String[][]</code>.
     *
     * @return name of type including any dimension information.
     */
    public String toString() {
        return qualifiedTypeName();
    }

    /**
     * Return true if this is a primitive type.
     */
    public boolean isPrimitive() {
        return true;
    }
}
