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

package f3.animation;

/**
 * Interface to KeyValue targets, which are variables of KeyValue
 * targets.
 * 
 * @author Tom Ball
 */
/**
 * @profile common
 */
public interface KeyValueTarget<a> {

    /**
     * The types of KeyValue targets.
     *  
     * @profile common
     */
    public enum Type {
        BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN, SEQUENCE, OBJECT
    }

    /**
     * Returns the value of this KeyValue target.
     * @return  value of the variable
     * 
     * @profile common
     */
    a get();

    /**
     * Returns the type of KeyValue target.
     * 
     * @return variable type
     * 
     * @profile common
     */
    Type getType();

    /**
     * Returns the value of this KeyValue target (equivalent to get()).
     * 
     * @return value of the variable
     * 
     * @profile common
     */
    Object getValue();

    /**
     * Sets the value of the variable the KeyValue targets.
     * @param value new value of the target variable
     * 
     * @profile common
     */
    void set(a value);

    /**
     * Sets the value of the variable the KeyValue targets (equivalent to set()).
     * 
     * @param o new value of the target variable
     * 
     * @profile common
     */
    void setValue(a o);

    /**
     * If the target of the KeyValue is another KeyValueTarget, return that
     * target; otherwise, return this instance.
     * 
     * @return o another {@code KeyValueTarget} if it is wrapped inside the instance's {@code KeyValue}
     *           otherwise, return this instance.
     * 
     * @profile common
     */
    KeyValueTarget unwrap();
}
