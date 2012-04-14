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

import com.sun.tools.mjavac.code.Symbol.TypeSymbol;

/**
 * Type variable
 */
public class F3TypeVar extends F3Type implements TypeClassTree {
    private final F3Expression className;
    private final TypeSymbol sym;
    
    /*
     * @param cardinality one of the cardinality constants
     */
    protected F3TypeVar(F3Expression className,
			Cardinality cardinality,
			TypeSymbol sym) {
        super(cardinality);
        this.className = className;
        this.sym = sym;
    }

    public F3Tree getTypeExpression() {
        return className;
    }

    @Override
    public void accept(F3Visitor v) { v.visitTypeVar(this); }
    
    public F3Expression getClassName() { return className; }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.TYPECLASS;
    }

    @Override
    public F3Kind getF3Kind() {
        return F3Kind.TYPE_CLASS;
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        //return v.visitTypeVar(this, d); !!! FIX ME
	return null;
    }
    
    TypeSymbol getSymbol() {
        return sym;
    }
}
