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

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.f3.api.F3Compiler;
import javax.tools.Tool;
import javax.tools.JavaCompiler;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;

/**
 * F3WrapperTestCase
 *
 * @author Brian Goetz
 */
public abstract class TestHelper {
    private static final F3Compiler f3c = f3cLocator();
    private static final JavaCompiler javac = javacLocator();

    public static final String TEST_ROOT = "test";
    public static final String BUILD_ROOT = "build/test";
    public static final String TEST_PREFIX = TEST_ROOT + File.separator;

    public TestHelper(String name, File testFile) {
    }

    protected static File makeBuildDir(File testFile) {
        if (!testFile.getPath().startsWith(TEST_PREFIX))
            throw new IllegalArgumentException("test file path not a relative pathname");
        File buildDir = new File(BUILD_ROOT + File.separator + testFile.getParent().substring(TEST_PREFIX.length()));
        if (!new File(BUILD_ROOT).exists())
            throw new IllegalArgumentException("no " + BUILD_ROOT + " directory in " + new File(".").getAbsolutePath());
        buildDir.mkdirs();
        return buildDir;
    }

    protected static int doCompile(String dir, String classpath, List<String> files, OutputStream out, OutputStream err) {
        List<String> args = new ArrayList<String>();
        args.add("-target");
        args.add("1.5");
        args.add("-d");
        args.add(dir);
        args.add("-cp");
        args.add(classpath);
        Tool compiler = null;
        for (String f : files) {
            Tool ftool;
            if (f.endsWith(".f3"))
                ftool = f3c;
            else if (f.endsWith(".java"))
                ftool = javac;
            else
                ftool = null;
            if (compiler != null && ftool != null && ftool != compiler) {
                throw new IllegalArgumentException("cannot compile both .java and .f3 with same compiler");
            }
            compiler = ftool;
            args.add(f);
        }
        if (compiler == null)
          compiler = f3c;
        return compiler.run(null, out, err, args.toArray(new String[args.size()]));
    }

    protected static F3Compiler f3cLocator() {
        Object tool = compilerLocator(F3Compiler.class);
        if (tool == null)
            throw new IllegalStateException("No F3 compiler found");
        return (F3Compiler) tool;
    }

    protected static JavaCompiler javacLocator() {
        Object tool = compilerLocator(JavaCompiler.class);
        if (tool == null)
            throw new IllegalStateException("No Java compiler found");
        return (JavaCompiler) tool;
    }

    protected static Object compilerLocator(Class compilerClass) {
        Iterator<?> iterator;
        Class<?> loaderClass;
        String loadMethodName;
        boolean usingServiceLoader;

        try {
            loaderClass = Class.forName("java.util.ServiceLoader");
            loadMethodName = "load";
            usingServiceLoader = true;
        } catch (ClassNotFoundException cnfe) {
            try {
                loaderClass = Class.forName("sun.misc.Service");
                loadMethodName = "providers";
                usingServiceLoader = false;
            } catch (ClassNotFoundException cnfe2) {
                throw new AssertionError("Failed discovering ServiceLoader");
            }
        }

        try {
            // java.util.ServiceLoader.load or sun.misc.Service.providers
            Method loadMethod = loaderClass.getMethod(loadMethodName,
                    Class.class,
                    ClassLoader.class);
            ClassLoader cl = TestHelper.class.getClassLoader();
            Object result = loadMethod.invoke(null, compilerClass, cl);

            // For java.util.ServiceLoader, we have to call another
            // method to get the iterator.
            if (usingServiceLoader) {
                Method m = loaderClass.getMethod("iterator");
                result = m.invoke(result); // serviceLoader.iterator();
            }

            iterator = (Iterator<?>) result;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException("Failed accessing ServiceLoader: " + t);
        }

        if (!iterator.hasNext())
            return null;

        return iterator.next();
    }

    protected static void dumpFile(InputStream file, String header, String testName) throws IOException {
        dumpFile(System.out, file, header, testName);
    }

    protected static void dumpFile(PrintStream output, InputStream file, String header, String testName) throws IOException {
        output.println("--" + header + " for " + testName + "--");
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                output.println(line);
            }
        }
        finally {
            reader.close();
        }
    }

    protected static String getClassPath(File buildDir) {
        Path path = new CommandlineJava().createClasspath(new Project());
        path.createPathElement().setPath(System.getProperty("java.class.path"));
        path.createPathElement().setPath(buildDir.getPath());
        return path.toString();
    }

    
}
