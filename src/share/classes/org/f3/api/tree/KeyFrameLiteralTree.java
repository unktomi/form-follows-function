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

import java.util.List;


/**
 * A tree node for a F3 keyframe literal.
 * @author tball
 */
public interface KeyFrameLiteralTree extends ExpressionTree {
    
    /**
     * @return the elapsed time to wait before start running the keyframe.
     */
    ExpressionTree getStartDuration();
    
    /**
     * @return the interpolation expression, which is the set of actions this
     * keyframe makes during its execution.
     */
    List<? extends ExpressionTree> getInterpolationValues();
    
    /**
     * @return the expression to be executed when the keyframe finishes, or
     *         null if no trigger expression is specified.
     */
    ExpressionTree getTrigger();
}
