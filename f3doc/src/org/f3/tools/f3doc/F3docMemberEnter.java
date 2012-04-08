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

package org.f3.tools.f3doc;

import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Position;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol.*;
import org.f3.tools.tree.*;
import org.f3.tools.comp.F3MemberEnter;

/**
 *  Javadoc's own memberEnter phase does a few things above and beyond that
 *  done by javac.
 */
public class F3docMemberEnter extends F3MemberEnter {
    public static F3docMemberEnter instance0(Context context) {
        F3MemberEnter instance = context.get(f3MemberEnterKey);
        if (instance == null)
            instance = new F3docMemberEnter(context);
        return (F3docMemberEnter)instance;
    }

    public static void preRegister(final Context context) {
        context.put(f3MemberEnterKey, new Context.Factory<F3MemberEnter>() {
               public F3MemberEnter make() {
                   return new F3docMemberEnter(context);
               }
        });
    }

    final DocEnv docenv;

    protected F3docMemberEnter(Context context) {
        super(context);
        docenv = DocEnv.instance(context);
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        super.visitFunctionDefinition(tree);
        MethodSymbol meth = tree.sym;
        if (meth == null || meth.kind != Kinds.MTH) return;
        String docComment = env.toplevel.docComments.get(tree);
        Position.LineMap lineMap = env.toplevel.lineMap;
        if (meth.isConstructor())
            docenv.makeConstructorDoc(meth, docComment, tree, lineMap);
        else
            docenv.makeFunctionDoc(meth, docComment, tree, lineMap);
    }

    @Override
    public void visitVar(F3Var tree) {
        super.visitVar(tree);
        if (tree.sym != null &&
                tree.sym.kind == Kinds.VAR &&
                !isParameter(tree.sym)) {
            String docComment = env.toplevel.docComments.get(tree);
            Position.LineMap lineMap = env.toplevel.lineMap;
            docenv.makeFieldDoc(tree.sym, docComment, tree, lineMap);
        }
    }

    private static boolean isParameter(VarSymbol var) {
        return (var.flags() & Flags.PARAMETER) != 0;
    }
}
