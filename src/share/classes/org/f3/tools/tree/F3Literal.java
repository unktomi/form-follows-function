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
import com.sun.tools.mjavac.code.TypeTags;

/**
 * A constant value given literally.
 * @param value value representation
 */
public class F3Literal extends F3Expression implements LiteralTree {

    public int typetag;
    public Object value;

    protected F3Literal(int typetag, Object value) {
        this.typetag = typetag;
        this.value = value;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitLiteral(this);
    }

    @Override
    public F3Kind getF3Kind() {
        switch (typetag) {
            case TypeTags.INT:
            case TypeTags.SHORT:
            case TypeTags.BYTE:
            case TypeTags.CHAR:
                return F3Kind.INT_LITERAL;
            case TypeTags.LONG:
                return F3Kind.LONG_LITERAL;
            case TypeTags.FLOAT:
                return F3Kind.FLOAT_LITERAL;
            case TypeTags.DOUBLE:
                return F3Kind.DOUBLE_LITERAL;
            case TypeTags.BOOLEAN:
                return F3Kind.BOOLEAN_LITERAL;
            case TypeTags.CLASS:
                return F3Kind.STRING_LITERAL;
            case TypeTags.BOT:
                return F3Kind.NULL_LITERAL;
            default:
                throw new AssertionError("unknown literal kind " + this);
        }
    }

    public Object getValue() {
        switch (typetag) {
            case TypeTags.BOOLEAN:
                int bi = (Integer) value;
                return (bi != 0);
            case TypeTags.CHAR:
                int ci = (Integer) value;
                char c = (char) ci;
                if (c != ci) {
                    throw new AssertionError("bad value for char literal");
                }
                return c;
            default:
                return value;
        }
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitLiteral(this, d);
    }

    @Override
    public F3Literal setType(Type type) {
        super.setType(type);
        return this;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.LITERAL;
    }
}

