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

import com.sun.tools.mjavac.code.Symbol;

/**
 * F3 unary expressions
 *
 * @author Tom Ball
 */
public class F3Unary extends F3Expression implements UnaryTree, Tree {

    private F3Tag opcode;
    public F3Expression arg;
    public Symbol operator;

    protected F3Unary(F3Tag opcode, F3Expression arg) {
        this.opcode = opcode;
        this.arg = arg;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitUnary(this);
    }

    @Override
    public F3Tag getF3Tag() {
        return opcode;
    }
    
    public int getOperatorTag() {
        return opcode.asOperatorTag();
    }

    public F3Expression getExpression() {
        return arg;
    }

    public Symbol getOperator() {
        return operator;
    }

    @Override
    public F3Kind getF3Kind() {
        switch (getF3Tag()) {
            case SIZEOF:
                return F3Kind.SIZEOF;
            case REVERSE:
                return F3Kind.REVERSE;
            default:
                return F3TreeInfo.tagToKind(getF3Tag());
        }
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitUnary(this, data);
    }
}
