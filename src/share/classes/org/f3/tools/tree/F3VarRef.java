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
import org.f3.api.tree.Tree.F3Kind;
import com.sun.tools.mjavac.code.Symbol;


/**
 * An identifier
 * @param idname the name
 * @param sym the symbol
 */
public class F3VarRef extends F3Expression {

    private Symbol sym;
    private F3Expression expr;
    private RefKind kind;
    private F3Expression receiver;

    protected F3VarRef(F3Expression expr, RefKind kind) {
        this.kind = kind;
        this.sym = F3TreeInfo.symbolFor(expr);
        this.expr = expr;
        if (!sym.isStatic() && expr.getF3Tag() == F3Tag.SELECT) {
            receiver = ((F3Select)expr).selected;
        }
    }

    public F3Expression getReceiver() {
        return receiver;
    }

    public void setReceiver(F3Expression receiver) {
        this.receiver = receiver;
    }

    public F3Expression getExpression() {
        return expr;
    }

    public Symbol getVarSymbol() {
        return sym;
    }

    public RefKind getVarRefKind() {
        return kind;
    }

    public enum RefKind {
        VARNUM,
        INST;
    }

    public void accept(F3Visitor v) {
        v.visitVarRef(this);
    }

    public F3Tag getF3Tag() {
        return F3Tag.VAR_REF;
    }

    public F3Kind getF3Kind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
