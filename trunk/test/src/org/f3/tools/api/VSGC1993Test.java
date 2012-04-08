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
 * This test is a regression test fo the neverending loop in the parser
 * reported as issue VSGC-1993.
 * 
 * @author David Strupl
 */
public class VSGC1993Test implements Runnable {
    private static final String testSrc = System.getProperty("test.src.dir", "test/src");
    private static final String testClasses = System.getProperty("build.test.classes.dir");
    private static final Object lock = new Object();
    private Exception caught;
    private boolean done = false;

    /**
     * Make sure we are able to at least analyze the test file. In other words it
     * should be compilable but this test mimics what the NetBeans editor does
     * with source files (not full compile).
     */
    @Test
    public void testWhetherParsingFinishes() throws Exception {
        Thread t = new Thread(this);
        t.start();
        // Let's give the compiler 10 seconds - that should be enough
        // as this is a very small program and should compile virtually
        // instantly on any system
        // 
		boolean localDone = false;
		int	lc = 0;

		while (!localDone && lc < 100) {

			Thread.sleep(100);	// Give it half a chance

			// See if it finished
			//
        	synchronized (lock) {
				localDone = done;
			}

			lc++;
        }

        if (!localDone) {
            StackTraceElement[] stack = t.getStackTrace();
            for (StackTraceElement s : stack) {
                System.out.println(s);
            }
			fail("Did not finish in time");
        }
        if (caught != null) {
            caught.printStackTrace();
        }
    }

    public void run() {
        try {
            parseFile();
        } catch (Exception x) {
            caught = x;
        }
        synchronized (lock) {
            done = true;
        }
    }

    private void parseFile() throws Exception {
        F3cTool instance = new F3cTool();
        MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
        StandardJavaFileManager fm = instance.getStandardFileManager(dl, null, null);
        List<String> options = 
                Arrays.asList("-d", ".", "-sourcepath", testSrc, "-classpath", testClasses);
        File file = new File(testSrc + "/org/f3/tools/api", "VSGC1993.f3");
    	Iterable<? extends JavaFileObject> files = fm.getJavaFileObjects(file);
        F3cTask task = instance.getTask(null, fm, dl, null, files);
        assertNotNull("no task returned", task);
        Iterable<? extends UnitTree> result1 = task.parse();
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
