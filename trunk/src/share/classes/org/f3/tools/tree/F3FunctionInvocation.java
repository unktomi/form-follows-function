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

import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.util.List;

/**
 * A method invocation
 */
public class F3FunctionInvocation extends F3Expression implements FunctionInvocationTree {

    public List<F3Expression> typeargs;
    public F3Expression meth;
    public List<F3Expression> args;
    public Type varargsElement;
    public boolean partial;
    public boolean immutable = false;

    public List<Symbol> resolvedImplicits = List.<Symbol>nil();

    protected F3FunctionInvocation(List<F3Expression> typeargs,
            F3Expression meth,
            List<F3Expression> args) {
        this.typeargs = (typeargs == null) ? List.<F3Expression>nil()
                : typeargs;
        this.meth = meth;
        this.args = args;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitFunctionInvocation(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.METHOD_INVOCATION;
    }

    public List<F3Expression> getTypeArguments() {
        return typeargs;
    }

    public F3Expression getMethodSelect() {
        return meth;
    }

    public List<F3Expression> getArguments() {
        return args;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitMethodInvocation(this, d);
    }

    @Override
    public F3FunctionInvocation setType(Type type) {
        super.setType(type);
        return this;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.APPLY;
    }
}
