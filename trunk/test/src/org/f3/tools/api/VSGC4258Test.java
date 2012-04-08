/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.api.tree.IdentifierTree;

import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.SourcePositions;
import org.f3.api.tree.Tree;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.VariableTree;
import org.f3.tools.comp.F3Enter;
import org.f3.tools.comp.F3Env;
import org.f3.tools.tree.F3ClassDeclaration;
import org.f3.tools.tree.F3FunctionDefinition;
import org.f3.tools.tree.F3Script;
import org.f3.tools.tree.F3Tree;
import org.f3.tools.tree.F3Var;
import org.f3.tools.tree.F3TreeScanner;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JavacFileManager;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * Regression test for VSGC-4258
 *
 */
public class VSGC4258Test {

    private static final String SEP = File.pathSeparator;
    private static final String DIR = File.separator;
    private String f3Libs = "dist/lib/shared";
    private String f3DeskLibs = "dist/lib/desktop";
    private String inputDir = "test/src/org/f3/tools/api";
    private F3cTrees trees;
    private UnitTree ut;
    private SourcePositions sp;
    private Context ctx;
    private Elements elements;

    private static class DeclScanner extends F3TreeScanner {
        Symbol sym;

        public DeclScanner(Symbol sym) {
            this.sym = sym;
        }

        F3Tree result = null;

        public void scan(F3Tree tree) {
            if (tree != null && result == null) {
                tree.accept(this);
            }
        }

        public void visitScript( F3Script that) {
            if (that.packge == sym) {
                result = that;
            } else {
                super.visitScript(that);
            }
        }

        public void visitClassDeclaration( F3ClassDeclaration that) {
            if (that.sym == sym) {
                result = that;
            } else {
                super.visitClassDeclaration(that);
            }
        }

        public void visitFunctionDefinition( F3FunctionDefinition that) {
            if (that.sym == sym) {
                result = that;
            } else {
                super.visitFunctionDefinition(that);
            }
        }


        public void visitVar( F3Var that) {
            if (that.sym == sym) {
                result = that;
            } else {
                super.visitVar(that);
            }
        }
    }

    private Tree getTree(Element e) {
        DeclScanner ds = new DeclScanner((Symbol)e);

        Symbol sym = (Symbol) e;
        F3Enter enter = F3Enter.instance(ctx);
        F3Env env = enter.getEnv(sym.enclClass());
        if (env == null) {
            return null;
        }

        env.tree.accept(ds);
        return ds.result;
    }
    
    @Before
    public void setup() throws IOException {
        doSetup();
    }

    private void doSetup() throws IOException {
        F3cTool tool = F3cTool.create();
        JavacFileManager manager = tool.getStandardFileManager(null, null, Charset.defaultCharset());

        ArrayList<JavaFileObject> filesToCompile = new ArrayList<JavaFileObject>();
        filesToCompile.add(manager.getFileForInput(inputDir + DIR + "GetScope.f3"));

        F3cTask task = tool.getTask(null, null, null, Arrays.asList("-XDdisableStringFolding", "-XDpreserveTrees", "-Xjcov", "-cp",
                f3Libs + DIR + "f3c.jar" + SEP + f3Libs + DIR + "f3rt.jar" + SEP + f3DeskLibs + DIR + "f3-ui-common.jar" + SEP + inputDir), filesToCompile);

        task.parse();
        Iterable analyzeUnits = task.analyze();
        trees = F3cTrees.instance(task);

        ut = (UnitTree) analyzeUnits.iterator().next();
        sp = trees.getSourcePositions();
        ctx = ((F3cTaskImpl) task).getContext();
        elements = ((F3cTaskImpl) task).getElements();
    }

    @After
    public void teardown() {
        trees = null;
        ut = null;
    }

    @Test
    public void testInClausePosition() throws Exception {
        final Tree[] t = new Tree[2];
        final Symbol[] sym = new Symbol[0];

        F3TreePathScanner<Void, Void> defTreeResolver = new F3TreePathScanner<Void, Void>() {
            @Override
            public Void visitIdentifier(IdentifierTree node, Void p) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null && e.getSimpleName().contentEquals("aaa")) {
                    t[1] = getTree(e);
                }
                return super.visitIdentifier(node, p);
            }

            @Override
            public Void visitVariable(VariableTree node, Void p) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null) {
                    if (e.getSimpleName().contentEquals("aaa")) {
                        t[0] = node;
                    }
                }
                return super.visitVariable(node, p);
            }
        };

        F3TreePathScanner<Void, Void> accessScope = new F3TreePathScanner<Void, Void>() {
            @Override
            public Void visitVariable(VariableTree node, Void p) {
                Element e = trees.getElement(getCurrentPath());
                if (e != null) {
                    if (e.getSimpleName().contentEquals("aaa")) {
                        trees.getScope(getCurrentPath());
                    }
                }
                return super.visitVariable(node, p);
            }
        };

        defTreeResolver.scan(ut, null);
        assertEquals(t[0], t[1]);

        accessScope.scan(ut, null);
        defTreeResolver.scan(ut, null);
        assertEquals(t[0], t[1]);
    }
}
