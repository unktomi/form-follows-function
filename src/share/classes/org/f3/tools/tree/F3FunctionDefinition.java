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

import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Name;
import org.f3.tools.code.F3Flags;

/**
 * A function definition.
 */
public class F3FunctionDefinition extends F3Expression implements FunctionDefinitionTree {

    public final F3Modifiers mods;
    public final Name name;
    public final F3FunctionValue operation;
    public MethodSymbol sym;
    public List<F3Expression> typeArgs;

    public F3FunctionDefinition(
            F3Modifiers mods,
            Name name,
            F3FunctionValue operation) {
        this.mods = mods;
        this.name = name;
        this.operation = operation;
    }

    protected F3FunctionDefinition(
            F3Modifiers mods,
            Name name,
            F3Type rettype,
            List<F3Var> funParams,
            F3Block bodyExpression) {
        this.mods = mods;
        this.name = name;
        this.operation = new F3FunctionValue(mods, rettype, funParams, bodyExpression);
    }

    public boolean isStatic() {
        return sym.isStatic();
    }

    public F3Block getBodyExpression() {
        return operation.getBodyExpression();
    }

    public F3Modifiers getModifiers() {
        return mods;
    }

    public boolean isBound() {
        return (mods.flags & F3Flags.BOUND) != 0;
    }

    public Name getName() {
        return name;
    }

    public F3Type getF3ReturnType() {
        return operation.rettype;
    }

    public List<F3Var> getParams() {
        return operation.funParams;
    }

    public F3FunctionValue getFunctionValue() {
        return operation;
    }

    public void accept(F3Visitor v) {
        v.visitFunctionDefinition(this);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.FUNCTION_DEF;
    }

    public F3Kind getF3Kind() {
        return F3Kind.FUNCTION_DEFINITION;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionDefinition(this, data);
    }
}
