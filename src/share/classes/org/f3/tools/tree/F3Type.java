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

package org.f3.tools.tree;

import org.f3.api.tree.*;
import com.sun.tools.mjavac.code.BoundKind;
/**
 * Abstract base for types
 *
 * @author Robert Field
 */
public abstract class F3Type extends F3Expression implements TypeTree {    
    private final Cardinality cardinality;
    public BoundKind boundKind = BoundKind.UNBOUND;
    /**
     * @param cardinality one of the cardinality constants
     */
    protected F3Type(Cardinality cardinality) {
        this.cardinality = cardinality;
    }
    
    protected F3Type() {
        this(null);
    }
    public Cardinality getCardinality() { return cardinality; }
}
