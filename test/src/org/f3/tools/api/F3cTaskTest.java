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
import org.f3.tools.api.F3cTool;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.Tree;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.junit.Test;
import static org.junit.Assert.*;
import static javax.tools.JavaFileObject.Kind.*;
import static javax.tools.StandardLocation.*;

/**
 * Unit test for F3cTask interface and its F3cTaskImpl implementation.
 */
public class F3cTaskTest {
    private static final String testSrc = System.getProperty("test.src.dir", "test/src");
    private static final String testClasses = System.getProperty("build.test.classes.dir");
    
    @Test
    public void parseSingleSource() throws Exception {
        F3cTool instance = new F3cTool();
        MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
        StandardJavaFileManager fm = instance.getStandardFileManager(dl, null, null);
        List<String> options = 
                Arrays.asList("-d", ".", "-sourcepath", testSrc, "-classpath", testClasses);
        File file = new File(testSrc + "/org/f3/tools/api", "Hello.f3");
	Iterable<? extends JavaFileObject> files = fm.getJavaFileObjects(file);
        F3cTask task = instance.getTask(null, fm, dl, null, files);
        assertNotNull("no task returned", task);
        Iterable<? extends UnitTree> result = task.parse();
        assertEquals("parse error(s)", 0, dl.errors());
        assertTrue("no compilation units returned", result.iterator().hasNext());
    }
    
    @Test
    public void parseNullSourceList() throws Exception {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        Iterable<? extends UnitTree> result = task.parse();
        assertFalse("unexpected compilation units returned", result.iterator().hasNext());
    }

    @Test
    public void errorCheckNullSourceList() throws Exception {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        int result = task.errorCheck();
        assertEquals(0, result);
    }

    @Test
    public void analyzeNullSourceList() throws Exception {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        Iterable<? extends UnitTree> result = task.analyze();
        assertNull("unexpected compilation units returned", result);
    }

    @Test
    public void generateNullSourceList() throws Exception {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        Iterable<? extends JavaFileObject> result = task.generate();
        assertFalse("unexpected file objects returned", result.iterator().hasNext());
    }

    @Test
    public void getTypeMirrorNullPath() {
        Iterable<? extends Tree> path = null;
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        TypeMirror result = task.getTypeMirror(path);
        assertNull("unexpected TypeMirror returned", result);
    }

    @Test
    public void getElementsNullSourceList() {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        Elements result = task.getElements();
        assertNotNull("unexpected Elements instance returned", result);
    }

    @Test
    public void getTypesNullSourceList() {
        F3cTool instance = new F3cTool();
        F3cTask task = instance.getTask(null, null, null, null, null);
        Types result = task.getTypes();
        assertNotNull("unexpected Types instance returned", result);
    }

    static class MockDiagnosticListener<T> implements DiagnosticListener<T> {
	public void report(Diagnostic<? extends T> d) {
	    diagCodes.add(d.getCode());
	    System.err.println(d);
	}

	public List<String> diagCodes = new ArrayList<String>();
        public int errors() {
            return diagCodes.size();
        }
    }
}
