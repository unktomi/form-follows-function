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

import com.sun.source.tree.Tree.Kind;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;

import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCAnnotation;
import com.sun.tools.mjavac.tree.JCTree.JCFieldAccess;
import com.sun.tools.mjavac.tree.JCTree.JCImport;
import com.sun.tools.mjavac.tree.Pretty;
import com.sun.tools.mjavac.tree.TreeInfo;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Name;
import org.f3.tools.comp.F3Defs;
import com.sun.tools.mjavac.tree.JCTree.JCClassDecl;
import com.sun.tools.mjavac.tree.JCTree.JCIdent;
import com.sun.tools.mjavac.util.Options;

/** Prints out a tree as an indented Java source program.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code tree depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Robert Field
 */
public class JavaPretty extends Pretty {
	private HashSet<Name> importedPackages = new HashSet<Name>();
	private HashSet<Name> importedClasses = new HashSet<Name>();
    private boolean seenImport;

    public final boolean verbose;
	
    public JavaPretty(Writer out, boolean sourceOutput, Context context) {
        super(out, sourceOutput);

        Options options = Options.instance(context);
        verbose = options.get("dumpverbosejava") != null;

		F3Defs defs = F3Defs.instance(context);
		    importedPackages.add(defs.runtime_PackageName);
		    importedPackages.add(defs.annotation_PackageName);
		    importedPackages.add(defs.sequence_PackageName);
		    importedPackages.add(defs.functions_PackageName);
            importedPackages.add(defs.javaLang_PackageName);
    }

    @Override
    public void visitAnnotation(JCAnnotation tree) {
        if (verbose) {
            // Super class implementation prints only simple name for
            // annotation type. So, org.f3.runtime.Package is
            // printed as Package which clashes with java.lang.Package!!
            try {
                print("@");
                print(tree.annotationType);
                print("(");
                printExprs(tree.args);
                print(")");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void visitBlockExpression(BlockExprJCBlockExpression tree) {
        visitBlockExpression(this, tree);
    }

    public static void visitBlockExpression(Pretty pretty, BlockExprJCBlockExpression tree) {
        try {
            pretty.printFlags(tree.flags);
            pretty.print("{");
            pretty.println();
            pretty.indent();
            pretty.printStats(tree.stats);
            if (tree.value != null) {
                pretty.align();
                pretty.printExpr(tree.value);
                pretty.print(";");
            }
            pretty.undent();
            pretty.println();
            pretty.align();
            pretty.print("}");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
	
    @Override
    public void visitImport(JCImport tree) {
        printRuntimeImports();
		super.visitImport(tree);

		// save imports for later use
		Name name = TreeInfo.name(tree.qualid);
		if (name == name.table.asterisk)
			if (tree.qualid.getTag() == JCTree.SELECT)
				importedPackages.add(TreeInfo.fullName(((JCFieldAccess) tree.qualid).selected));
			else;
		else if (name.contentEquals("**")) {

            // TODO: Handle import of '**'
            //
        } else {
			importedClasses.add(TreeInfo.fullName(tree.qualid));
        }
    }

    @Override
    public void visitClassDef(JCClassDecl tree) {
        printRuntimeImports();
        super.visitClassDef(tree);
    }

    @Override
    public void visitSelect(JCFieldAccess tree) {
        try {
			if (!importedPackages.contains(TreeInfo.fullName(tree.selected)) 
					&& !importedClasses.contains(TreeInfo.fullName(tree))) {
				printExpr(tree.selected, TreeInfo.postfixPrec);
                if (tree.selected.getKind() == Kind.IDENTIFIER) {
                    if (! ((JCIdent)tree.selected).getName().isEmpty()) {
                        print(".");
                    }
                } else {
                    print(".");
                }
			}
            print(tree.name);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void printRuntimeImports() {
        if (! seenImport) {
            for (Name pkg : importedPackages) {
                try {
                    print("import ");
                    print(pkg.toString());
                    print(".*;");
                    println();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        seenImport = true;
    }
}
