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
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.tree.JCTree;
import org.f3.tools.comp.F3Enter;
import org.f3.tools.tree.*;
import java.util.HashMap;
import javax.tools.JavaFileObject;

/**
 *  Javadoc's own enter phase does a few things above and beyond that
 *  done by javac.
 *  @author Neal Gafter
 */
public class F3docEnter extends F3Enter {
    public static F3docEnter instance0(Context context) {
        F3Enter instance = context.get(f3EnterKey);
        if (instance == null)
            instance = new F3docEnter(context);
        return (F3docEnter)instance;
    }

    public static void preRegister(final Context context) {
        context.put(f3EnterKey, new Context.Factory<F3Enter>() {
               public F3Enter make() {
                   return new F3docEnter(context);
               }
        });
    }

    protected F3docEnter(Context context) {
        super(context);
        messager = Messager.instance0(context);
        docenv = DocEnv.instance(context);
    }

    final Messager messager;
    final DocEnv docenv;

    @Override
    public void main(List<F3Script> trees) {
        // count all Enter errors as warnings.
        int nerrors = messager.nerrors;
        super.main(trees);
        messager.nwarnings += (messager.nerrors - nerrors);
        messager.nerrors = nerrors;
    }

    @Override
    public void visitScript(F3Script tree) {
        if (tree.docComments == null)
            tree.docComments = new HashMap<JCTree, String>();
        super.visitScript(tree);
        if (tree.sourcefile.isNameCompatible("package-info", JavaFileObject.Kind.SOURCE)) {
            String comment = tree.docComments.get(tree);
            docenv.makePackageDoc(tree.packge, comment, tree);
        }
    }

    @Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
        super.visitClassDeclaration(tree);
        if (tree.sym != null && tree.sym.kind == Kinds.TYP) {
            if (tree.sym == null) return;
            String comment = env.toplevel.docComments.get(tree);
            ClassSymbol c = tree.sym;
            docenv.makeClassDoc(c, comment, tree, env.toplevel.lineMap);
        }
    }

    /** Don't complain about a duplicate class. */
    @Override
    protected void duplicateClass(DiagnosticPosition pos, ClassSymbol c) {}
}
