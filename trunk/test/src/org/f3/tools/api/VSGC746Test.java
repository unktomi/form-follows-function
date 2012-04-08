/*
 * Copyright 2003-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.tools.api.F3cTool;
import org.f3.tools.api.F3cTrees;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.ExpressionTree;
import org.f3.api.tree.Tree;
import org.f3.api.tree.SourcePositions;
import java.io.File;
import java.util.List;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Customer-supplied test for VSGC-746 issue.
 */
public class VSGC746Test {
    private static final String testSrc = System.getProperty("test.src.dir", "test/src");

    @Test
    public void testVSGC746() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            /* The javac library uses the context classloader to load the
             * javac implementation. In a NetBeans module, it needs to
             * be loaded by the module's classloader to make sure that the
             * version of javac this compiler requires takes precedence
             * over the JDK's version.
             */
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            File file = new File("test/src/org/f3/tools/api/Test.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            List<? extends UnitTree> treeList = (List)f3Task.parse();
            assertTrue("AST list size should be 1!", treeList.size() == 1);

            SourcePositions sp = F3cTrees.instance(f3Task).getSourcePositions();
            UnitTree tree = treeList.iterator().next();
            ExpressionTree pkg = tree.getPackageName();
            long start = sp.getStartPosition(tree, pkg);
            long end = sp.getEndPosition(tree, pkg);
            String pkgName = pkg.toString();
            assertTrue("Package AST end-start <" + (end-start) + "> should be same as pkgName len <" + pkgName.length() + ">", end - start == pkgName.length());
            
            Tree cls = tree.getTypeDecls().iterator().next();
            start = sp.getStartPosition(tree, cls);
            end = sp.getEndPosition(tree, cls);
            String clsDecl = "class Test{}";
            assertTrue("Class AST end-start <"+ (end-start+1) + "> should be same as class length <" + clsDecl.length() + ">", end - start +1  == clsDecl.length());
            // For V3 assertTrue("Class AST end-start <"+ (end-start)+ "> should be same as class length <" + clsDecl.length() + ">", end - start  == clsDecl.length());
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
}
