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

/**
 *
 * @author bothner
 */
public class F3FunctionValue extends F3Expression implements FunctionValueTree {

    public F3Type rettype;
    public List<F3Var> funParams;
    public F3Block bodyExpression;
    public F3FunctionDefinition definition;
    public F3Modifiers mods;
    public List<F3Expression> typeArgs;

    public boolean infer = false;

    public F3FunctionValue(F3Modifiers mods,
			   F3Type rettype,
			   List<F3Var> params,
			   F3Block bodyExpression) {
        this.mods = mods;
        this.rettype = rettype;
        this.funParams = params;
        this.bodyExpression = bodyExpression;

        if  (bodyExpression != null) {
            this.pos = bodyExpression.pos;
        }
    }

    public F3Type getF3ReturnType() {
        return rettype;
    }

    public F3Type getType() {
        return rettype;
    }

    public List<F3Var> getParams() {
        return funParams;
    }

    public java.util.List<? extends VariableTree> getParameters() {
        return (java.util.List) funParams;
    }

    public F3Block getBodyExpression() {
        return bodyExpression;
    }

    public void accept(F3Visitor v) {
        v.visitFunctionValue(this);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.FUNCTIONEXPRESSION;
    }

    public F3Kind getF3Kind() {
        return F3Kind.FUNCTION_VALUE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionValue(this, data);
    }
}
