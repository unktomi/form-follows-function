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
import org.f3.api.tree.ClassDeclarationTree;
import org.f3.api.tree.F3TreePath;

import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.UnitTree;
import com.sun.tools.mjavac.util.JavacFileManager;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import javax.lang.model.element.Element;
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
public class VSGC3787Test {

    private static final String SEP = File.pathSeparator;
    private static final String DIR = File.separator;

    private String f3Libs = "dist/lib/shared";
    private String f3DeskLibs = "dist/lib/desktop";

    private String inputDir = "test/src/org/f3/tools/api";

    private F3cTrees trees;
    private UnitTree ut;

    @Before
    public void setup() throws IOException {
        F3cTool tool = F3cTool.create ();
        JavacFileManager manager = tool.getStandardFileManager (null, null, Charset.defaultCharset ());

        ArrayList<JavaFileObject> filesToCompile = new ArrayList<JavaFileObject> ();
        filesToCompile.add (manager.getFileForInput (inputDir + DIR + "VSGC3787.f3"));

        F3cTask task = tool.getTask (null, null, null, Arrays.asList ("-XDdisableStringFolding", "-XDpreserveTrees", "-cp",
            f3Libs + DIR + "f3c.jar" + SEP + f3Libs + DIR + "f3rt.jar" + SEP + f3DeskLibs + DIR + "f3-ui-common.jar" + SEP + inputDir
        ), filesToCompile);

        task.parse();
        Iterable analyzeUnits = task.analyze();
        trees = F3cTrees.instance(task);
        ut = (UnitTree)analyzeUnits.iterator().next();
    }

    @After
    public void teardown() {
        trees = null;
        ut = null;
    }

    @Test
    public void testParenthesizedPositions() throws Exception {
        F3TreePathScanner<Void, Void> scanner = new F3TreePathScanner<Void, Void>() {

            @Override
            public Void visitClassDeclaration(ClassDeclarationTree node, Void p) {
                Element e = trees.getElement(getCurrentPath());
                F3TreePath path = trees.getPath(e);
                assertNotNull("Returned null path for class definition!", path);
                return super.visitClassDeclaration(node, p);
            }
        };
        scanner.scan(ut, null);
    }
}
