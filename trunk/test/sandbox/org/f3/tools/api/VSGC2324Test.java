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

import org.f3.api.tree.VariableTree;
import org.f3.api.F3cTask;

import org.f3.api.tree.UnitTree;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test makes sure that the AllTrees.f3 file contains all tree constructs
 * from org.f3.api.tree.Tree.F3Kind.values().
 *
 * @author David Strupl
 */
public class VSGC2324Test {
    private static final String testSrc = System.getProperty("test.src.dir", "test/sandbox");
    private static final String testClasses = System.getProperty("build.test.classes.dir");
    private static F3cTask task;
    private VariableTree node;
    private VariableTree a;

    /**
     * Make sure we are able to at least analyze the test file. In other words it
     * should be compilable but this test mimics what the NetBeans editor does
     * with source files (not full compile).
     */
    @Test
    public void analyzeTest() throws Exception {
        F3cTool instance = new F3cTool();
        MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
        StandardJavaFileManager fm = instance.getStandardFileManager(dl, null, null);
        List<String> options =
                Arrays.asList("-d", ".", "-sourcepath", testSrc, "-classpath", testClasses);
        File file = new File(testSrc + "/org/f3/tools/api", "VSGC2324.f3");
        Iterable<? extends JavaFileObject> files = fm.getJavaFileObjects(file);
        task = instance.getTask(null, fm, dl, null, files);
        assertNotNull("no task returned", task);
        Iterable<? extends UnitTree> result1 = task.parse();
//        assertEquals("parse error(s)", 0, dl.errors());
        assertTrue("no compilation units returned", result1.iterator().hasNext());
        Iterable<? extends UnitTree> result2 = task.analyze();
    }


    static class MockDiagnosticListener<T> implements DiagnosticListener<T> {
	public void report(Diagnostic<? extends T> d) {
	    diagCodes.add(d.getCode());
	}

	public List<String> diagCodes = new ArrayList<String>();
        public int errors() {
            return diagCodes.size();
        }
    }

}
