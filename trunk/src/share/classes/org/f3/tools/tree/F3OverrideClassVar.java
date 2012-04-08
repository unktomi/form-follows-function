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

import org.f3.api.F3BindStatus;
import org.f3.api.tree.F3TreeVisitor;
import org.f3.api.tree.OverrideClassVarTree;

import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.util.Name;

/**
 * The override of an instance variable
 *
 * @author Robert Field
 */
public class F3OverrideClassVar extends F3AbstractVar implements OverrideClassVarTree {

    private final F3Ident expr;
    
    protected F3OverrideClassVar(
            Name name,
            F3Type type,
            F3Modifiers mods,
            F3Ident expr,
            F3Expression init,
            F3BindStatus bindStat,
            F3OnReplace onReplace,
            F3OnReplace onInvalidate,
            F3VarSymbol sym) {
        super(name, type, mods, init, bindStat, onReplace, onInvalidate, sym);
        this.expr = expr;
    }
    
    public void accept(F3Visitor v) {
        v.visitOverrideClassVar(this);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.OVERRIDE_ATTRIBUTE_DEF;
    }

    public F3Ident getId() {
        return expr;
    }
    
    @Override
    public boolean isOverride() {
        return true;
    }
    
    @Override
    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitOverrideClassVar(this, data);
    }
}
