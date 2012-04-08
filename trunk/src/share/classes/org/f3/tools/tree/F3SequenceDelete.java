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
 *
 * @author Robert Field
 */
public class F3SequenceDelete extends F3Expression implements SequenceDeleteTree {
    private F3Expression sequence;
    private final F3Expression element;

    public F3SequenceDelete(F3Expression sequence, F3Expression element) {
        this.sequence = sequence;
        this.element = element;
    }

    public void accept(F3Visitor v) {
        v.visitSequenceDelete(this);
    }

    public F3Expression getSequence() {
        return sequence;
    }
    
    public F3Expression getElement() {
        return element;
    }
    
    @Override
    public F3Tag getF3Tag() {
        return F3Tag.DELETE;
    }

    public F3Kind getF3Kind() {
        return F3Kind.SEQUENCE_DELETE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitSequenceDelete(this, data);
    }
}
