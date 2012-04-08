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

/**
 *
 * @author Robert Field
 * @author Zhiqun Chen
 */

package org.f3.tools.tree;

import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;

public class F3OnReplace extends F3Tree implements OnReplaceTree {
    
    private final F3Var firstIndex;
    private final F3Var oldValue;
    private final F3Block body;
    private int endKind;
    private Kind triggerKind;
    private F3Var lastIndex;
    private F3Var newElements;
    private F3Var saveVar;

    
    public F3OnReplace(Kind triggerKind) {
        this(null, null, null, 0, null, null, null, triggerKind);
    }

    public F3OnReplace( F3Var oldValue, F3Block body, Kind triggerKind) {
        this(oldValue, null, null, 0, null, null, body, triggerKind);
    }
    
    
    public F3OnReplace(F3Var oldValue, F3Var firstIndex, F3Var lastIndex,
            int endKind, F3Var newElements, F3Var saveVar, F3Block body, Kind triggerKind) {
        this.oldValue = oldValue;
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.endKind = endKind;
        this.newElements = newElements;
        this.body = body;
        this.triggerKind = triggerKind;
        this.saveVar = saveVar;
    }
    
    public void accept(F3Visitor v) {
        v.visitOnReplace(this);
    }
    
    public F3Tag getF3Tag() {
        return F3Tag.ON_REPLACE;
    }
    
    public F3Var getOldValue() {
        return oldValue;
    }
    
    public F3Block getBody() {
        return body;
    }
    
    public F3Var getFirstIndex () {
        return firstIndex;
    }

    public F3Var getLastIndex () {
        return lastIndex;
    }

    public F3Var getNewElements () {
        return newElements;
    }

    public F3Var getSaveVar () {
        return saveVar;
    }

    public F3Kind getF3Kind() {
        return F3Kind.ON_REPLACE;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitOnReplace(this, data);
    }

    public int getEndKind () {
        return endKind;
    }

    public Kind getTriggerKind () {
        return triggerKind;
    }

    public enum Kind {
        ONREPLACE("on replace"),
        ONINVALIDATE("on invalidate");

        String displayName;

        Kind(String displayName) {
            this.displayName = displayName;
        }

        public String toString() {
            return displayName;
        }
    }
}
