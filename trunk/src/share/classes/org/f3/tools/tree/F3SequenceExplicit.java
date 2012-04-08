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
import org.f3.tools.code.F3VarSymbol;

import com.sun.tools.mjavac.util.List;

/**
 *
 * @author Robert Field
 */
public class F3SequenceExplicit extends F3AbstractSequenceCreator implements SequenceExplicitTree {
    private final List<F3Expression> items;

    public List<F3VarSymbol> boundItemsSyms;
    public List<F3VarSymbol> boundItemLengthSyms;
    public F3VarSymbol boundLowestInvalidPartSym;
    public F3VarSymbol boundHighestInvalidPartSym;
    public F3VarSymbol boundPendingTriggersSym;
    public F3VarSymbol boundDeltaSym;
    public F3VarSymbol boundChangeStartPosSym;
    public F3VarSymbol boundChangeEndPosSym;
    public F3VarSymbol boundIgnoreInvalidationsSym;
    public F3VarSymbol boundSizeSym;

    public F3SequenceExplicit(List<F3Expression> items) {
        this.items = items;
    }

    public void accept(F3Visitor v) {
        v.visitSequenceExplicit(this);
    }

    public List<F3Expression> getItems() {
        return items;
    }
    
    public java.util.List<ExpressionTree> getItemList() {
        return convertList(ExpressionTree.class, items);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.SEQUENCE_EXPLICIT;
    }

    public F3Kind getF3Kind() {
        return F3Kind.SEQUENCE_EXPLICIT;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitSequenceExplicit(this, data);
    }
}
