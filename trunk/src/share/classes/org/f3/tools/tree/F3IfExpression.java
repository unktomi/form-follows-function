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

/**
 * "if {cond} truePart [else falsePart]" conditional expression
 */
public class F3IfExpression extends F3Expression implements ConditionalExpressionTree {

    public final F3Expression cond;
    public final F3Expression truepart;
    public final F3Expression falsepart;

    public F3Var boundCondVar;
    public F3Var boundThenVar;
    public F3Var boundElseVar;
    public F3Var boundSizeVar;

    public boolean isThen;

    protected F3IfExpression(F3Expression cond,
            F3Expression truepart,
            F3Expression falsepart) {
        this.cond = cond;
        this.truepart = truepart;
        this.falsepart = falsepart;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitIfExpression(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.CONDITIONAL_EXPRESSION;
    }

    public F3Expression getCondition() {
        return cond;
    }

    public F3Expression getTrueExpression() {
        return truepart;
    }

    public F3Expression getFalseExpression() {
        return falsepart;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitConditionalExpression(this, d);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.CONDEXPR;
    }
}

