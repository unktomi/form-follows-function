/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.api.tree.OnReplaceTree;

import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.util.Name;

/**
 *
 * @author Robert Field
 */
public abstract class F3AbstractVar extends F3Expression implements VariableTree {
    public Name name;
    private F3Type f3type;
    public final F3Modifiers mods;
    private final F3Expression init;
    private final F3OnReplace[] triggers;
    
    public F3VarSymbol sym;

    protected F3AbstractVar(
            Name name,
            F3Type f3type,
            F3Modifiers mods,
            F3Expression init,
            F3BindStatus bindStatus,
            F3OnReplace onReplace,
            F3OnReplace onInvalidate,
            F3VarSymbol sym) {
        super(bindStatus);
        this.name = name;
        this.f3type = f3type;
        this.mods = mods;
        this.init = init;
        this.triggers = new F3OnReplace[F3OnReplace.Kind.values().length];
        this.triggers[F3OnReplace.Kind.ONREPLACE.ordinal()] = onReplace;
        this.triggers[F3OnReplace.Kind.ONINVALIDATE.ordinal()] = onInvalidate;
        this.sym = sym;
    }

    public abstract boolean isOverride();

    public boolean isStatic() {
        return sym.isStatic();
    }

    public F3Expression getInitializer() {
        return init;
    }

    public F3OnReplace getOnInvalidate() {
        return triggers[F3OnReplace.Kind.ONINVALIDATE.ordinal()];
    }

    public OnReplaceTree getOnInvalidateTree() {
        return triggers[F3OnReplace.Kind.ONINVALIDATE.ordinal()];
    }

    public F3OnReplace getOnReplace() {
        return triggers[F3OnReplace.Kind.ONREPLACE.ordinal()];
    }

    public OnReplaceTree getOnReplaceTree() {
        return triggers[F3OnReplace.Kind.ONREPLACE.ordinal()];
    }

    public F3OnReplace getTrigger(F3OnReplace.Kind triggerKind) {
        return triggers[triggerKind.ordinal()];
    }

    public OnReplaceTree getTriggerTree(F3OnReplace.Kind triggerKind) {
        return triggers[triggerKind.ordinal()];
    }

    public F3VarSymbol getSymbol() {
        return sym;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitVariable(this, data);
    }

    public F3Kind getF3Kind() {
        return F3Kind.VARIABLE;
    }

    public Name getName() {
        return name;
    }

    public F3Tree getType() {
        return f3type;
    }

    public F3Type getF3Type() {
        return f3type;
    }

    public void setF3Type(F3Type type) {
        f3type = type;
    }

    public F3Modifiers getModifiers() {
        return mods;
    }
    
    public boolean isLiteralInit() {
        return init != null && init instanceof F3Literal;
    }
}
