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

package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.UnitTree;
import javax.lang.model.element.Element;

import org.f3.api.tree.Tree;
import org.f3.api.tree.VariableTree;
import java.io.File;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Test;

/**
 * Tests that F3cTrees.getElement can be called from visitor
 * 
 * @author Robert Field
 */
public class VSGC1828Test {

    @Test
    public void testF3TreesGetElement() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {            
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            File file = new File("test/src/org/f3/tools/api/VSGC1828.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file); 
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            Iterable<? extends UnitTree> treeList = f3Task.analyze();
            
            F3cTrees trees = F3cTrees.instance(f3Task);
            VarScanner d = new VarScanner(trees);
            d.scan(treeList, null);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

class VarScanner<EnumSet> extends F3TreePathScanner<Void,EnumSet> {
    F3cTrees trees;
    
    VarScanner(F3cTrees trees) {
        this.trees = trees;
    }
    
    @Override
    public Void visitVariable(VariableTree tree, EnumSet p) {                
        Element e = trees.getElement(getCurrentPath());
        Tree t = trees.getTree(e);
        return null;
    }
}
}
