/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.api.tree.F3TreeScanner;
import org.f3.api.tree.VariableTree;
import org.f3.api.tree.UnitTree;

import org.f3.tools.tree.F3TypeClass;
import java.io.File;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test that checks inferred variable types
 *
 * @author mcimadamore
 */
public class VSGC4023Test {

    @Test
    public void testPreserveParens() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            File file = new File("test/src/org/f3/tools/api/VSGC4023.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file);
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            Iterable<? extends UnitTree> treeList = f3Task.analyze();
            VarDeclTester pf = new VarDeclTester();
            pf.scan(treeList, null);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    static class VarDeclTester extends F3TreeScanner<Void,Object> {

        @Override
        public Void visitVariable(VariableTree node, Object p) {
            if (node.getF3Type() instanceof F3TypeClass) {
                F3TypeClass tc = (F3TypeClass)node.getF3Type();
                assertTrue(!tc.getClassName().toString().equals(node.getName().toString()));
            }
            return null;
        }
    }
}
