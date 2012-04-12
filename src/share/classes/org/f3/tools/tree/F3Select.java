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
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.List;
 /**
 * Selects through packages and classes
 * @param selected selected Tree hierarchy
 * @param selector name of field to select thru
 * @param sym symbol of the selected class
 */
public class F3Select extends F3Expression implements MemberSelectTree {

    public F3Expression selected;
    public Name name;
    public Symbol sym;
    public boolean nullCheck;
    public List<F3Expression> typeArgs;
    public F3Var boundSize;

    protected F3Select(F3Expression selected, Name name, Symbol sym, boolean nullCheck) {
        this.selected = selected;
        this.name = name;
        this.sym = sym;
        this.nullCheck = nullCheck;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitSelect(this);
    }

    @Override
    public F3Kind getF3Kind() {
        return F3Kind.MEMBER_SELECT;
    }

    public F3Expression getExpression() {
        return selected;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitMemberSelect(this, d);
    }

    public Name getIdentifier() {
        return name;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.SELECT;
    }
}

