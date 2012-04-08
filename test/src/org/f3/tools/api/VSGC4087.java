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
import org.f3.api.tree.ForExpressionInClauseTree;
import org.f3.api.tree.IdentifierTree;
import org.f3.api.tree.F3TreePath;

import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.SourcePositions;
import org.f3.api.tree.Tree;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.VariableTree;
import org.f3.tools.tree.F3FunctionDefinition;
import org.f3.tools.tree.F3OverrideClassVar;
import org.f3.tools.tree.F3Tree;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JavacFileManager;
import com.sun.tools.mjavac.util.Name;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * This test makes sure that the AllTrees.f3 file contains all tree constructs
 * from org.f3.api.tree.Tree.F3Kind.values().
 *
 * @author David Strupl
 */
public class VSGC4087 {

    private static final String SEP = File.pathSeparator;
    private static final String DIR = File.separator;
    private String f3Libs = "dist/lib/shared";
    private String f3DeskLibs = "dist/lib/desktop";
    private String inputDir = "test/sandbox/org/f3/tools/api";
    private F3cTrees trees;
    private UnitTree ut;
    private SourcePositions sp;
    private Context ctx;
    private Elements elements;

    @Before
    public void setup() throws IOException {
        doSetup();
    }

    private void doSetup() throws IOException {
        F3cTool tool = F3cTool.create();
        JavacFileManager manager = tool.getStandardFileManager(null, null, Charset.defaultCharset());

        ArrayList<JavaFileObject> filesToCompile = new ArrayList<JavaFileObject>();
        filesToCompile.add(manager.getFileForInput(inputDir + DIR + "VSGC4087.f3"));

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
        final int[] pos = new int[]{-1};
        final boolean[] checkContext = new boolean[]{false};

        F3TreePathScanner<Void, Void> positionScanner = new F3TreePathScanner<Void, Void>() {
            private boolean inClause = false;
            @Override
            public Void visitIdentifier(IdentifierTree node, Void p) {
                if (inClause) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null && e.getSimpleName().contentEquals("seq")) {
                        pos[0] = (int)sp.getEndPosition(ut, node);
                        System.err.println("ObjectID: " + System.identityHashCode(node));
                    }
                }
                return super.visitIdentifier(node, p);
            }

            @Override
            public Void visitForExpressionInClause(ForExpressionInClauseTree node, Void p) {
                try {
                    inClause = true;
                    return super.visitForExpressionInClause(node, p);
                } finally {
                    inClause = false;
                }
            }
        };

        F3TreePathScanner<Void, Void> accessContext = new F3TreePathScanner<Void, Void>() {
            @Override
            public Void visitVariable(VariableTree node, Void p) {
                if (checkContext[0]) {
                    Element e = trees.getElement(getCurrentPath());
                    if (e != null) {
                        if (e.getSimpleName().contentEquals("aaa")) {
                            trees.getScope(getCurrentPath());
                        }
                    }
                }
                return super.visitVariable(node, p);
            }
        };

        int pass1, pass2;
        positionScanner.scan(ut, null);
        pass1 = pos[0];
        pos[0] = -1;
        System.err.println("End Position: pass 1 = " + pass1);
        positionScanner.scan(ut, null);
        pass2 = pos[0];
        pos[0] = -1;
        System.err.println("End Position: pass 2 = " + pass2);
        assertEquals(pass1, pass2);

        checkContext[0] = true;
        positionScanner.scan(ut, null);
        pass1 = pos[0];
        pos[0] = -1;
        System.err.println("End Position: pass 1 = " + pass1);
        accessContext.scan(ut, null);
        System.err.println("Accessed context");
        positionScanner.scan(ut, null);
        pass2 = pos[0];
        pos[0] = -1;
        System.err.println("End Position: pass 2 = " + pass2);
        assertEquals(pass1, pass2);
    }
}
