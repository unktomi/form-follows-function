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

import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.code.Symbol;
import org.f3.api.F3BindStatus;

/**
 * In object literal  "Identifier ':' [ 'bind'] expression"
 */
public class F3ObjectLiteralPart extends F3Expression implements ObjectLiteralPartTree {
    public final Name name;
    public final F3Expression expr;
    private final F3BindStatus explicitBindStatus;
    public Symbol sym;
    public boolean isVarAssignment;
    public boolean isMethodDef = false;
   /*
    * @param selector member name and class name of member
    * @param init type of attribute
    * @param sym attribute symbol
    */
    protected F3ObjectLiteralPart(
            Name name,
            F3Expression expr,
            F3BindStatus bindStatus,
            Symbol sym) {
        super(bindStatus);
        this.explicitBindStatus = bindStatus;
        this.name = name;
        this.expr = expr;
        this.sym = sym;
    }

    public void accept(F3Visitor v) {
        v.visitObjectLiteralPart(this);
    }

    public javax.lang.model.element.Name getName() {
        return name;
    }

    public F3Expression getExpression() {
        return expr;
    }

    public F3BindStatus getExplicitBindStatus() {
        return explicitBindStatus;
    }

    public boolean isExplicitlyBound() {
        return explicitBindStatus.isBound();
    }
    
    @Override
    public F3Tag getF3Tag() {
        return F3Tag.OBJECT_LITERAL_PART;
    }

    public F3Kind getF3Kind() {
        return F3Kind.OBJECT_LITERAL_PART;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitObjectLiteralPart(this, data);
    }
}
