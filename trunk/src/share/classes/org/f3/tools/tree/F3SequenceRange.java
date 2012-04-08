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
public class F3SequenceRange extends F3AbstractSequenceCreator implements SequenceRangeTree {
    private final F3Expression lower;
    private final F3Expression upper;
    private final F3Expression stepOrNull;
    private final boolean exclusive;

    public F3Var boundSizeVar;

    public F3SequenceRange(F3Expression lower, F3Expression upper, F3Expression stepOrNull, boolean exclusive) {
        this.lower = lower;
        this.upper = upper;
        this.stepOrNull = stepOrNull;
        this.exclusive = exclusive;
    }

    public void accept(F3Visitor v) {
        v.visitSequenceRange(this);
    }

    public F3Expression getLower() {
        return lower;
    }
    
    public F3Expression getUpper() {
        return upper;
    }
    
    public F3Expression getStepOrNull() {
        return stepOrNull;
    }
    
    public boolean isExclusive() {
        return exclusive;
    }
    
    @Override
    public F3Tag getF3Tag() {
        return F3Tag.SEQUENCE_RANGE;
    }

    public F3Kind getF3Kind() {
        return F3Kind.SEQUENCE_RANGE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitSequenceRange(this, data);
    }
}
