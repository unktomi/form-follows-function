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

package org.f3.tools.code;

/**
 * Enum representing type representation.
 *
 * @author Robert Field
 */
public enum F3TypeRepresentation {
    TYPE_REPRESENTATION_OBJECT     (null,     "Object",   ""),
    TYPE_REPRESENTATION_BOOLEAN    (0,        "Boolean",  "AsBoolean"),
    TYPE_REPRESENTATION_CHAR       (0,        "Char",     "AsChar"),
    TYPE_REPRESENTATION_BYTE       ((byte)0,  "Byte",     "AsByte"),
    TYPE_REPRESENTATION_SHORT      ((short)0, "Short",    "AsShort"),
    TYPE_REPRESENTATION_INT        (0,        "Int",      "AsInt"),
    TYPE_REPRESENTATION_LONG       (0L,       "Long",     "AsLong"),
    TYPE_REPRESENTATION_FLOAT      (0.0f,     "Float",    "AsFloat"),
    TYPE_REPRESENTATION_DOUBLE     (0.0,      "Double",   "AsDouble"),
    TYPE_REPRESENTATION_SEQUENCE   (null,     "Sequence", "AsSequence");
    
    private final Object defaultValue; 
    private final String prefix;
    private final String suffix;

    private F3TypeRepresentation(Object defaultValue, String prefix, String suffix) {
        this.defaultValue = defaultValue;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Object defaultValue() { return defaultValue; }

    public String prefix() { return prefix; }
    public String suffix() { return suffix; }

    public boolean isSequence() { return this == TYPE_REPRESENTATION_SEQUENCE; }
    public boolean isObject() { return this == TYPE_REPRESENTATION_OBJECT; }
    public boolean isPrimitive() { return this != TYPE_REPRESENTATION_OBJECT && this != TYPE_REPRESENTATION_SEQUENCE; }

}
