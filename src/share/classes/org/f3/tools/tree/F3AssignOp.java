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
 * An assignment with "+=", "|=" ...
 */
public class F3AssignOp extends F3Expression implements CompoundAssignmentTree {

    private F3Tag opcode;
    public F3Expression lhs;
    public F3Expression rhs;
    public Symbol operator;

    protected F3AssignOp(F3Tag opcode, F3Expression lhs, F3Expression rhs, Symbol operator) {
        this.opcode = opcode;
        this.lhs = (F3Expression) lhs;
        this.rhs = (F3Expression) rhs;
        this.operator = operator;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitAssignop(this);
    }

    public F3Kind getF3Kind() {
        return F3TreeInfo.tagToKind(getF3Tag());
    }

    public F3Expression getVariable() {
        return lhs;
    }

    public F3Expression getExpression() {
        return rhs;
    }

    public Symbol getOperator() {
        return operator;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitCompoundAssignment(this, d);
    }

    @Override
    public F3Tag getF3Tag() {
        return opcode;
    }

    public F3Tag getNormalOperatorF3Tag() {
        switch (opcode) {
            case PLUS_ASG:
                return F3Tag.PLUS;
            case MINUS_ASG:
                return F3Tag.MINUS;
            case MUL_ASG:
                return F3Tag.MUL;
            case DIV_ASG:
                return F3Tag.DIV;
            default:
                throw new RuntimeException("bad assign op tag: " + opcode);
        }
    }

    public int getOperatorTag() {
        return opcode.asOperatorTag();
    }
}
