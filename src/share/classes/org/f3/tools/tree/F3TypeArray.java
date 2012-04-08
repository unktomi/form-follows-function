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

import org.f3.api.tree.F3TreeVisitor;
import org.f3.api.tree.Tree;
import org.f3.api.tree.Tree.F3Kind;
import org.f3.api.tree.TypeArrayTree;


/**
 * Type referencing a class
 *
 * @author Robert Field
 */
public class F3TypeArray extends F3Type implements TypeArrayTree {
    private F3Type elementType;
    
    /*
     * @param cardinality one of the cardinality constants
     */
    protected F3TypeArray(F3Type elementType) {
        super();
        this.elementType = elementType;
    }
    public void accept(F3Visitor v) {
        v.visitTypeArray(this);
    }

    public F3Type getElementType() {
        return elementType;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.TYPEARRAY;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitTypeArray(this, d);
    }

    @Override
    public F3Kind getF3Kind() {
        return Tree.F3Kind.TYPE_FUNCTIONAL;
    }
}
