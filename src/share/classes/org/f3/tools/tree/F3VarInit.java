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

import com.sun.tools.mjavac.util.Name;
import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.util.ListBuffer;
import java.util.List;

/**
 * Initialization of a var inline in a local context.
 * This includes both script bodies (its original use)
 * and local contexts inflated into classes.
 * Initialization code remains attached to the var.
 * VarInit holds the actual var tree, thus access must
 * carefully consider this.
 *
 * @author Robert Field
 */
public class F3VarInit extends F3Expression implements VariableTree {
    private F3Var var;
    private ListBuffer<F3VarInit> shreddedVarInits;

    protected F3VarInit(F3Var var) {
        this.var = var;
        if (var!=null)
            var.setVarInit(this);
    }
    
    public F3Var getVar() {
        return var;
    }

    public void resetVar(F3Var res) {
        var = res;
        var.setVarInit(this);
    }

    public F3VarSymbol getSymbol() {
        return var.getSymbol();
    }

    public Name getName() {
        return var.getName();
    }

    // for VariableTree
    public F3Tree getType() {
        return var.getType();
    }

    public F3Expression getInitializer() {
        return var.getInitializer();
    }

    public void accept(F3Visitor v) {
        v.visitVarInit(this);
    }

    public void addShreddedVarInit(F3VarInit vi) {
        if (shreddedVarInits == null) {
            shreddedVarInits = ListBuffer.lb();
        }
        shreddedVarInits.append(vi);
    }

    public ListBuffer<F3VarInit> getShreddedVarInits() {
        if (shreddedVarInits == null) {
            return ListBuffer.<F3VarInit>lb();
        } else {
            return shreddedVarInits;
        }
    }

    public F3Type getF3Type() {
        return var.getF3Type();
    }

    public OnReplaceTree getOnReplaceTree() {
        return var.getOnReplaceTree();
    }
    
    public F3OnReplace getOnReplace() {
        return var.getOnReplace();
    }

    public OnReplaceTree getOnInvalidateTree() {
        return var.getOnInvalidateTree();
    }

    public F3OnReplace getOnInvalidate() {
        return var.getOnInvalidate();
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.VAR_SCRIPT_INIT;
    }
    
    public F3Modifiers getModifiers() {
        return var.getModifiers();
    }
    
    public boolean isOverride() {
        return false;
    }

    public F3Kind getF3Kind() {
        return F3Kind.VARIABLE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitVariable(this, data);
     }
}
