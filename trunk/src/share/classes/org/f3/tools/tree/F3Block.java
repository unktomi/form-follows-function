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

import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.util.Position;

/**
 *
 * @author bothner
 */
public class F3Block extends F3Expression implements BlockExpressionTree {

    public long flags;
    public List<F3Expression> stats;
    public F3Expression value;
    // During attribution we rewrite return statement with the expression
    // returned. See F3Attr.finishFunctionDefinition. In such cases, we
    // save actual return statement in this field.
    public F3Return returnStatement;
    /** Position of closing brace, optional. */
    public int endpos = Position.NOPOS;
    public boolean isVoidValueAllowed = true;

    protected F3Block(long flags, List<F3Expression> stats, F3Expression value) {
        this.stats = stats;
        this.flags = flags;
        this.value = value;
    }

    protected F3Block() {
        this.stats = null;
        this.flags = 0;
        this.value = null;
    }

    /*
     * This is used only by the extranal tree walkers. Internal tree walkers
     * always use "stats" field directly. For external walkers, we want to
     * present source return statement, if any instead of value alone. So, we
     * return a modified list of statements with return statement or return value
     * appended. See also VSGC-3284.
     */
    public java.util.List<ExpressionTree> getStatements() {
        // form a new list with possible return statement, if any
        List<F3Expression> statements;
        if (returnStatement == null && value == null) {
            statements = stats;
        } else {
            statements = stats.append((returnStatement != null)? returnStatement : value);
        }
        return convertList(ExpressionTree.class, statements);
    }

    public List<F3Expression> getStmts() {
        return stats;
    }

   public F3Expression getValue() {
        return value;
    }

    public boolean isStatic() {
        return (flags & Flags.STATIC) != 0;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.BLOCK_EXPRESSION;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitBlockExpression(this, d);
    }

    public void accept(F3Visitor v) {
        v.visitBlockExpression(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.BLOCK_EXPRESSION;
    }
}
