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
public class F3ErroneousBlock extends F3Block {

    /**
     * This class is just an Erroneous node masquerading as
     * a Block so that we can create it in the tree. So it
     * stores a local erroneous block and uses this for the
     * vistor pattern etc.
     */
    private F3Erroneous errNode;

    protected F3ErroneousBlock(List<? extends F3Tree> errs) {
        errNode = new F3Erroneous(errs);
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.ERRONEOUS;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitErroneous(errNode);
    }

    @Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        return v.visitErroneous(errNode, d);
    }

    public List<? extends F3Tree> getErrorTrees() {
        return errNode.getErrorTrees();
    }
    
    @Override
    public F3Kind getF3Kind() {
        return F3Kind.ERRONEOUS;
    }
}
