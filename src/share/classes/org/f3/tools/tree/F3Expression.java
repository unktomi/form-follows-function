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

import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.util.List;

public abstract class F3Expression extends F3Tree implements ExpressionTree, F3BoundMarkable {
    
    private F3BindStatus bindStatus;

    public static void setImplicitArgs(F3Expression exp, List<F3Expression> args) {
        if (exp instanceof F3Ident) {
            ((F3Ident)exp).implicitArgs = args;
        } else if (exp instanceof F3Select) {
            ((F3Select)exp).implicitArgs = args;
        }
    }

    /** Initialize tree.
     */
    protected F3Expression() {
        this.bindStatus = F3BindStatus.UNBOUND;
    }

    protected F3Expression(F3BindStatus bindStatus) {
        this.bindStatus = bindStatus == null ? F3BindStatus.UNBOUND : bindStatus;
    }


    @Override
    public F3Expression setType(Type type) {
        super.setType(type);
        return this;
    }

    @Override
    public F3Expression setPos(int pos) {
        super.setPos(pos);
        return this;
    }

    public void markBound(F3BindStatus bindStatus) {
        this.bindStatus = bindStatus;
    }

    public F3BindStatus getBindStatus() {
        return bindStatus;
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
