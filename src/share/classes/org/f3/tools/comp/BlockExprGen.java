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

import org.f3.tools.tree.*;
import com.sun.tools.mjavac.jvm.*;
import com.sun.tools.mjavac.comp.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.tree.*;
import com.sun.tools.mjavac.tree.JCTree.JCReturn;
import com.sun.tools.mjavac.tree.JCTree.JCTry;
import com.sun.tools.mjavac.code.Type;
import org.f3.tools.util.MsgSym;

/**
 *
 * @author bothner
 */
public class BlockExprGen extends Gen {

    public static void preRegister(final Context context) {
        context.put(genKey, new Context.Factory<Gen>() {
            public Gen make() {
                return new BlockExprGen(context);
            }
        });
    }

    public BlockExprGen(Context context) {
        super(context);
    }
    
    public static BlockExprGen instance(Context context) {
        BlockExprGen instance = (BlockExprGen) context.get(genKey);
        if (instance == null)
            instance = new BlockExprGen(context);
        return instance;
    }
    public void visitBlockExpression(BlockExprJCBlockExpression tree) {
        // super.visitBlock(tree, tree.stats, tree.value, tree.endpos);
        int limit = code.nextreg;
        Env<GenContext> localEnv = env.dup(tree, new GenContext());
        genStats(tree.stats, localEnv);
        if (tree.value != null) {
            tree.value.accept(this);
            if (result instanceof Items.LocalItem
                  /* && we're about to exit result's scope -- FIXME */) {
                result = result.load();
            }
        }
        // End the scope of all block-local variables in variable info.
        if (env.tree.getTag() != JCTree.METHODDEF) {
            code.statBegin(tree.endpos);
            code.endScopes(limit);
            code.pendingStatPos = Position.NOPOS;
        }
    }
    
    @Override
    public void visitTry(JCTry tree) {
        // check if stack is not empty -> currentlt not supported
        if (!tree.catchers.isEmpty() && code.state.stacksize > 0) {
            log.error(tree, MsgSym.MESSAGE_CATCH_WITHIN_EXPRESSION);
            nerrs++;
        } else {
            super.visitTry(tree);
        }
    }
  
    @Override
    public void visitReturn(JCReturn tree) {
        // get return-type of enclosing method
        Type localType = pt;
        pt = env.enclMethod.getReturnType().type;
        super.visitReturn(tree);
        pt = localType;
    }

    @Override
    public void visitTree(JCTree tree) {
        if (tree instanceof BlockExprJCBlockExpression)
            visitBlockExpression((BlockExprJCBlockExpression) tree);
        else
            super.visitTree(tree);
    }
}
