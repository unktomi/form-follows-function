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

package org.f3.tools.comp;

import com.sun.tools.mjavac.comp.Lower;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.Context;
import org.f3.tools.tree.BlockExprJCBlockExpression;

/**
 * @author bothner
 */
public class BlockExprLower extends Lower {

    public static void preRegister(final Context context) {
        context.put(lowerKey, new Context.Factory<Lower>() {
            public Lower make() {
                return new BlockExprLower(context);
            }
        });
    }

    public static BlockExprLower instance(Context context) {
        BlockExprLower instance = (BlockExprLower) context.get(lowerKey);
        if (instance == null)
            instance = new BlockExprLower(context);
        return instance;
    }

    protected BlockExprLower(Context context) {
        super(context);
    }

    public void visitBlockExpression(BlockExprJCBlockExpression tree) {
        tree.stats = translate(tree.stats);
        tree.value = translate(tree.value);
        result = tree;
    }

    @Override
    public void visitTree(JCTree tree) {
        if (tree instanceof BlockExprJCBlockExpression)
            visitBlockExpression((BlockExprJCBlockExpression) tree);
        else
            super.visitTree(tree);
    }
}
