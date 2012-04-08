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

package org.f3.api.tree;

/**
 * Common interface for on-replace statement nodes in an abstract syntax tree 
 * for the F3 language.
 *
 * <p><b>WARNING:</b> This interface and its sub-interfaces are 
 * subject to change as the F3 programming language evolves.
 * These interfaces are implemented by the F3 compiler (f3c) 
 * and should not be implemented either directly or indirectly by 
 * other applications.
 *
 * @author Tom Ball
 */

public interface OnReplaceTree extends ExpressionTree {
    VariableTree getFirstIndex();
    VariableTree getLastIndex();
    VariableTree getOldValue();
    VariableTree getNewElements();
    BlockExpressionTree getBody();    
    int getEndKind();
}
