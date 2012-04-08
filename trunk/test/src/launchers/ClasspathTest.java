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
package launchers;

import junit.framework.TestCase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This tests to make sure there are no collisions in the package namespace
 * of f3c.jar and tools.jar on systems other than macs. On macs the javac
 * compiler is already on the bootclasspath as classes.jar
 */

public class ClasspathTest extends TestCase {

    static final String sep = File.separator;
    static final String psep = File.pathSeparator;

    static boolean isMac() {
        return System.getProperty("os.name").startsWith("Mac");
    }

    @Override
    public void setUp() throws Exception {
        Utils.init();
    }

    /*
     * clean up and be green.
     */
    @Override
    public void tearDown() {
        Utils.reset();
    }
    static final String TESTNAME = "ClasspathTest";

    boolean verifyProp(List<String> oList, String prop, String strToFind) {
        for (String x : oList) {
            String[] elements = x.split(",");
            for (String y : elements) {
                y = y.trim();
                if (y.startsWith(prop) && y.contains(strToFind)) {
                    return true;
                }
            }
        }
        return false;
    }

    File getToolsJar() {
        // usually ends with a jre, so get the basename
        File javahomeDir = new File(System.getProperty("java.home")).getParentFile();
        File toolsJar = new File(javahomeDir, "lib" + File.separator + "tools.jar");
        if (!toolsJar.exists()) {
            throw new RuntimeException("tools.jar not found");
        }
        return toolsJar;
    }
    
    public void testBootClasspath() throws IOException {

        String testSrc = 
                "import f3.util.F3Evaluator;\n" +
                "F3Evaluator.eval(\"println(\\\"Hello World\\\")\");";

        Utils.createF3Jar(new File(Utils.workingDir, TESTNAME + ".jar"), testSrc);
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;

        File libDir = new File(Utils.getDistDir(),"lib" +
                File.separator + "shared");
        File f3cFile = new File(libDir, "f3c.jar");

        // use cp
        cmdsList.add(Utils.f3Exe.getAbsolutePath());

        if (!isMac()) { // no tools.jar on mac
            cmdsList.add("-J-Xbootclasspath/p:" + getToolsJar());
        }
        cmdsList.add("-cp");
        cmdsList.add(TESTNAME + ".jar" + File.pathSeparator +
                f3cFile.getAbsolutePath());
        cmdsList.add(TESTNAME);
        output = Utils.doExec(cmdsList);
        assertTrue(output.toString().contains("Hello World"));

        // use jar cmd
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.getAbsolutePath());
        if (isMac()) { // no tools.jar on macs
            cmdsList.add("-J-Xbootclasspath/p:" + f3cFile);
        } else {
            cmdsList.add("-J-Xbootclasspath/p:" + getToolsJar() +
                    File.pathSeparator + f3cFile);
        }
        cmdsList.add("-jar");
        cmdsList.add(TESTNAME + ".jar");
        output = Utils.doExec(cmdsList);
        assertTrue(output.toString().contains("Hello World"));
    }
}
