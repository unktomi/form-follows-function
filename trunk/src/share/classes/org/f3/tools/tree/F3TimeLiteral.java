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

/**
 * Tree node for time literals, such as "100ms" or "3m".
 * @author tball
 */
public class F3TimeLiteral extends F3Expression implements TimeLiteralTree {
    public F3Literal value;
    public Duration duration;
    
   protected F3TimeLiteral(){
        this.value = null;
        this.duration = null;
    }

    protected F3TimeLiteral(F3Literal value, Duration duration) {
        this.value = value;
        this.duration = duration;
    }

    public F3Tag getF3Tag() {
        return F3Tag.TIME_LITERAL;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitTimeLiteral(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.TIME_LITERAL;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitTimeLiteral(this, data);
    }

    public Duration getDuration() {
        return duration;
    }

    public F3Literal getValue() {
        return value;
    }
}
