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
import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;
import org.f3.tools.code.F3VarSymbol;

import com.sun.tools.mjavac.util.Name;

/**
 * for (name in seqExpr where whereExpr) bodyExpr
 */
public class F3ForExpressionInClause extends F3Tree implements ForExpressionInClauseTree, F3BoundMarkable {

    public final F3Var var;
    public F3Expression seqExpr;
    private F3Expression whereExpr;
    private boolean hasWhere = false;
    public Name label;

    private boolean indexUsed;
    private F3BindStatus bindStatus = F3BindStatus.UNBOUND;

    public F3Var boundHelper;
    public F3VarSymbol indexVarSym;
    public F3VarSymbol inductionVarSym;
    public F3VarSymbol boundResultVarSym;

    protected F3ForExpressionInClause() {
        this.var        = null;
        this.seqExpr    = null;
        this.whereExpr  = null;
    }

    protected F3ForExpressionInClause(
            F3Var var,
            F3Expression seqExpr,
            F3Expression whereExpr) {
        this.var = var;
        this.seqExpr = seqExpr;
        this.whereExpr = whereExpr;
        this.hasWhere = whereExpr != null;
    }

    public void accept(F3Visitor v) {
        v.visitForExpressionInClause(this);
    }

    public F3Var getVar() {
        return var;
    }

    public F3Var getVariable() {
        return var;
    }

    public F3Expression getSequenceExpression() {
        return seqExpr;
    }

    public F3Expression getWhereExpression() {
        return whereExpr;
    }

    public boolean hasWhereExpression() {
        return hasWhere;
    }

    public void setWhereExpr(F3Expression whereExpr) {
        this.whereExpr = whereExpr;
        if (whereExpr != null) {
            this.hasWhere = true;
        }
    }

    public boolean getIndexUsed() {
        return indexUsed;
    }

    public void setIndexUsed(boolean indexUsed) {
        this.indexUsed = indexUsed;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.FOR_EXPRESSION_IN_CLAUSE;
    }

    public F3Kind getF3Kind() {
        return F3Kind.FOR_EXPRESSION_IN_CLAUSE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitForExpressionInClause(this, d);
    }

    public void markBound(F3BindStatus bindStatus) {
        this.bindStatus = bindStatus;
    }

    public boolean isBound() {
        return bindStatus.isBound();
    }

    public boolean isUnidiBind() {
        return bindStatus.isUnidiBind();
    }

    public boolean isBidiBind() {
        return bindStatus.isBidiBind();
    }
}
