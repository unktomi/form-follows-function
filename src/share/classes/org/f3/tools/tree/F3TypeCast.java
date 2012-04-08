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
import org.f3.api.tree.Tree.F3Kind;
import org.f3.tools.code.F3VarSymbol;

/**
 * A type cast.
 */
public class F3TypeCast extends F3Expression implements TypeCastTree {

    public F3Tree clazz;
    public F3Expression expr;

    public F3VarSymbol boundArraySizeSym;

    protected F3TypeCast(F3Tree clazz, F3Expression expr) {
        this.clazz = clazz;
        this.expr = expr;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitTypeCast(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.TYPE_CAST;
    }

    public F3Tree getType() {
        return clazz;
    }

    public F3Expression getExpression() {
        return expr;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitTypeCast(this, d);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.TYPECAST;
    }
}
