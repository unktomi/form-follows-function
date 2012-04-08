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

package org.f3.api;

/**
 * Effectively an enum for the possible bind status values
 * 
 * @author Robert Field
 */
public enum F3BindStatus {
    
    UNBOUND   ("unbound",                   false, false),
    UNIDIBIND ("unidirectional bind",       true,  false),
    BIDIBIND  ("bidirectional bind",        false, true);
    
    private final String description;
    private final boolean isBound;
    private final boolean isUnidiBind;
    private final boolean isBidiBind;
    
    public String description()  { return description; }
    public boolean isBound()     { return isBound; }
    public boolean isUnidiBind() { return isUnidiBind; }
    public boolean isBidiBind()  { return isBidiBind; }

    /** Creates a new instance of F3BindStatus */
    private F3BindStatus(String description, boolean isUnidiBind, boolean isBidiBind) {
        this.description = description;
        this.isUnidiBind = isUnidiBind;
        this.isBidiBind = isBidiBind;
        this.isBound = isUnidiBind || isBidiBind;
    }
}
