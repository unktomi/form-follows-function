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

import com.sun.source.tree.TreeVisitor;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCExpression;
import com.sun.tools.mjavac.tree.JCTree.JCStatement;
import com.sun.tools.mjavac.tree.Pretty;
import com.sun.tools.mjavac.tree.TreeScanner;
import com.sun.tools.mjavac.tree.TreeTranslator;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Position;
import org.f3.tools.comp.BlockExprAttr;
import org.f3.tools.comp.BlockExprEnter;
import org.f3.tools.comp.BlockExprMemberEnter;
import org.f3.tools.comp.F3PrepForBackEnd;

/**
 *
 * @author Per Bothner
 * @author Robert Field
 */
public class BlockExprJCBlockExpression extends JCExpression {
    
   public static final int BLOCK_EXPR_TAG = JCTree.LETEXPR + 1;
   
        public long flags;
        public List<JCStatement> stats;
    public JCExpression value;
    /** Position of closing brace, optional. */
    public int endpos = Position.NOPOS;

    public BlockExprJCBlockExpression(long flags, List<JCStatement> stats, JCExpression value) {
        this.stats = stats;
        this.flags = flags;
        this.value = value;
    }

    @Override
    public void accept(Visitor v) {
        // Kludge
        if (v instanceof Pretty) {
            JavaPretty.visitBlockExpression((Pretty) v, this);
        } else if (v instanceof BlockExprAttr) {
            ((BlockExprAttr) v).visitBlockExpression(this);
        } else if (v instanceof BlockExprEnter) {
            ((BlockExprEnter) v).visitBlockExpression(this);
        } else if (v instanceof BlockExprMemberEnter) {
            ((BlockExprMemberEnter) v).visitBlockExpression(this);
        } else if (v instanceof F3PrepForBackEnd) {
            ((F3PrepForBackEnd) v).visitBlockExpression(this);
        } else if (v instanceof TreeScanner) {
            ((TreeScanner) v).scan(stats);
            ((TreeScanner) v).scan(value);
        } else if (v instanceof TreeTranslator) {
            stats = ((TreeTranslator) v).translate(stats);
            value = ((TreeTranslator) v).translate(value);
            ((TreeTranslator) v).result = this;
        } else {
            v.visitTree(this);
        }
    }

    public Kind getKind() {
        return Kind.BLOCK;
    }

    public List<JCStatement> getStatements() {
        return stats;
    }

    public boolean isStatic() {
        return (flags & Flags.STATIC) != 0;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> v, D d) {
        throw new UnsupportedOperationException("This is a back-end node and should not be visable to the API");
    }

    @Override
    public int getTag() {
        return BLOCK_EXPR_TAG;
    }
}
