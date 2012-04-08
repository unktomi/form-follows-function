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

package framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.f3.runtime.ErrorHandler;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * F3UnitTestCase
 *
 * @author Brian Goetz
 */
public class F3UnitTestWrapper extends TestCase {
    private final TestCase object;
    private final File testFile;
    private final Method testMethod;
    private final Method setUp;
    private final Method tearDown;

    public F3UnitTestWrapper(String name, File testFile, TestCase object, Method testMethod, Method setUp, Method tearDown) {
        super(name);
        this.testFile = testFile;
        this.object = object;
        this.testMethod = testMethod;
        this.setUp = setUp;
        this.tearDown = tearDown;
    }

    @Override
    protected void setUp() throws Exception {
        if (setUp != null && testMethod != null) {
            System.out.println("SetUp(f3unit): " + testFile + " - " + testMethod.getName());
            setUp.invoke(object);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (tearDown != null && testMethod != null) {
            System.out.println("TearDown(f3unit): " + testFile + " - " + testMethod.getName());
            tearDown.invoke(object);
        }
    }

    @Override
    protected void runTest() throws Throwable {
        if (testMethod != null) {
            System.out.println("Test(f3unit): " + testFile + " - " + testMethod.getName());
            try {
                boolean suppress = ErrorHandler.getSuppressBindingExceptions();
                try {
                    ErrorHandler.setSuppressBindingExceptions(true);
                    testMethod.invoke(object);
                }
                finally {
                    ErrorHandler.setSuppressBindingExceptions(suppress);
                }
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    public static TestSuite makeSuite(File testFile, String name) throws Exception {
        TestSuite suite = new TestSuite(name);
        File buildDir = TestHelper.makeBuildDir(testFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        List<String> files = new ArrayList<String>();
        files.add(testFile.getPath());
        int errors = TestHelper.doCompile(buildDir.getPath(), TestHelper.getClassPath(buildDir), files, out, err);
        if (errors == 0) {
            ClassLoader cl = new URLClassLoader(new URL[] { buildDir.toURL() }, F3UnitTestWrapper.class.getClassLoader());
            String className = testFile.getName();
            className = className.substring(0, className.length() - ".f3".length());
            Class<? extends TestCase> clazz = (Class<? extends TestCase>) cl.loadClass(className);
            List<TestCase> tests = new ArrayList<TestCase>();
            Constructor<? extends TestCase> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            TestCase instance = ctor.newInstance();
            Method[] methods = clazz.getMethods();
            Method setUp = null;
            Method tearDown = null;
            for (Method m : methods) {
                if (m.getName().equals("setUp") && !Modifier.isStatic(m.getModifiers()))
                    setUp = m;
                if (m.getName().equals("tearDown") && !Modifier.isStatic(m.getModifiers()))
                    tearDown = m;
            }
            for (Method m : methods) {
                if (m.getName().startsWith("test") && !Modifier.isStatic(m.getModifiers())) {
                    m.setAccessible(true);
                    suite.addTest(new F3UnitTestWrapper(m.getName(), testFile, instance, m, setUp, tearDown));
                }
            }
        }
        else {
            // capture the errors in a test case that will fail
            suite.addTest(new CompilationErrorWrapper(testFile, err));
        }
        return suite;
    }
}
