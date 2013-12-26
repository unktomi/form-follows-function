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

package org.f3.runtime;

import org.f3.runtime.sequence.Sequence;

/**
 * ErrorHandler
 *
 * @author Brian Goetz
 */
public class ErrorHandler {

    private static boolean suppressBindingExceptions = false;

    private static boolean getBoolean(String property) {
        try {
            return Boolean.getBoolean(property);
        } catch (SecurityException ignored) {
            return false;
        }
    }
    
    private final static boolean debug = getBoolean("f3.debug");

    public static boolean isDebug() {
        return debug;
    }

    public static boolean getSuppressBindingExceptions() {
        return suppressBindingExceptions;
    }

    public static void setSuppressBindingExceptions(boolean suppressBindingExceptions) {
        ErrorHandler.suppressBindingExceptions = suppressBindingExceptions;
    }

    /** Called when attempting to insert an element into a sequence at an out-of-bounds location */
    public static<T> void outOfBoundsInsert(Sequence<T> seq, int index, T value) {

    }

    /** Called when attempting to replace an element of a sequence at an out-of-bounds location */
    public static<T> void outOfBoundsReplace(Sequence<T> seq, int index, T value) {

    }

    /** Called when attempting to delete an element of a sequence at an out-of-bounds location */
    public static<T> void outOfBoundsDelete(Sequence<T> seq, int index) {

    }

    /** Called when attempting to read an element of a sequence at an out-of-bounds location */
    public static<T> void outOfBoundsRead(Sequence<T> seq, int index) {

    }

    /** Called when attempting to dereference a null value */
    public static void nullDereference() {

    }

    public static void bindException(RuntimeException e) {
        if (debug || !suppressBindingExceptions) {
            System.err.println("Exception in binding:");
            e.printStackTrace();
        }
        throw new RuntimeException(e);
    }

    public static void triggerException(RuntimeException e) {
        if (debug || !suppressBindingExceptions) {
            System.err.println("Exception in trigger:");
            e.printStackTrace();
        }
        throw new RuntimeException(e);
    }

    /** Called when attempting to coerce a null numeric or boolean value to a primitive */
    public static void nullToPrimitiveCoercion(String type) {
        if (isDebug())
            System.err.println("Coercing " + type + " to null");

    }

    /** Called when attempting to write a null value to a non-nullable variable */
    public static void invalidNullWrite() {

    }
}
