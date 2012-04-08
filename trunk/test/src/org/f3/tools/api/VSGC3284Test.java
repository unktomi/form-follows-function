/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.F3TreePath;
import org.f3.api.tree.ReturnTree;
import org.f3.api.tree.Tree.F3Kind;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.SourcePositions;

import java.io.File;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests F3TreePathScanner.visitReturn() works correctly
 * 
 * @author Anton Chechel
 * @author A. Sundararajan 
 *             * removed unwanted stuff 
 *             * changed test src path
 *             * changed package
 */
public class VSGC3284Test {
    @Test
    public void testVisitReturn() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            File file = new File("test/src/org/f3/tools/api/ReturnTest.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            Iterable<? extends UnitTree> treeList = f3Task.analyze();

            F3cTrees trees = F3cTrees.instance(f3Task);
            SourcePositions sp = trees.getSourcePositions();
            UnitTree unit = treeList.iterator().next();

            DetectorVisitor d = new DetectorVisitor(trees, sp, unit);
            d.scan(treeList, null);

            Assert.assertEquals(d.getRetCounter(), 4);

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    class DetectorVisitor<Void, EnumSet> extends F3TreePathScanner<Void, EnumSet> {

        F3cTrees trees;
        SourcePositions sp;
        UnitTree unit;
        int retCounter;

        DetectorVisitor(F3cTrees trees, SourcePositions sp, UnitTree unit) {
            this.trees = trees;
            this.sp = sp;
            this.unit = unit;
        }

        @Override
        public Void visitReturn(ReturnTree tree, EnumSet p) {
            retCounter++;
            
            F3TreePath path = trees.getPath(unit, tree);
            Assert.assertNotNull(path);

            F3Kind kind = tree.getF3Kind();
            Assert.assertEquals(kind, F3Kind.RETURN);

            return null;
        }

        public int getRetCounter() {
            return retCounter;
        }
    }
}
