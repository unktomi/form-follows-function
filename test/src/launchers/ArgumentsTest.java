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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ksrini
 */
public class ArgumentsTest  extends TestCase {
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

    public void testF3ToolsHelp() throws IOException {
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;

        // Call the launcher to test -help
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-help");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the launcher to test -X
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-X");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the launcher to test -?
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-?");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the f3c  to test -help
        cmdsList.clear();
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add("-help");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the f3doc  to test -help
        cmdsList.clear();
        cmdsList.add(Utils.f3docExe.toString());
        cmdsList.add("-help");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

    }

    public void testBasicArguments() throws IOException {
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-help");
        assertTrue(Utils.checkExec(cmdsList, null, false));

        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        String filename = "Version";
        FileWriter fw = new FileWriter(new File(Utils.workingDir, filename + ".f3"));
        PrintWriter pw = new PrintWriter(fw);
        try {
            pw.println(Utils.emitVersionF3(false));
        } finally {
            if (pw != null) pw.close();
            if (fw != null) fw.close();
        }

        // Call the compiler
        cmdsList.clear();
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add(filename + ".f3");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the launcher
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-verbose:class");
        cmdsList.add(filename);
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the compiler with -target 1.5
        cmdsList.clear();
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add("-target");
        cmdsList.add("1.5");
        cmdsList.add(filename + ".f3");
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the launcher to test java arguments
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-verbose:class");
        cmdsList.add(filename);
        output = Utils.doExec(cmdsList);
        assertNotNull(output);

        // Call the launcher to test VM arguments
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-J-verbose:class");
        cmdsList.add(filename);
        output = Utils.doExec(cmdsList);
        assertNotNull(output);
    }



    public void testF3ArgumentsPassing() throws IOException {
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;

        // Test the application args to see if they are passed in correctly
        String appargs[] = {"-appflagOne", "One", "-appflagTwo", "Two"};

        // using cp
        cmdsList.add("-cp");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");

        for (String x : appargs) {
            cmdsList.add(x);
        }

        assertTrue(Utils.checkExec(cmdsList, appargs));
        
        // pass -version to the app itself
        cmdsList.clear();
        cmdsList.add("-cp");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "-version"));

        // pass -fullversion to the app itself
        cmdsList.clear();
        cmdsList.add("-cp");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "-fullversion"));


        // pass -help to the app itself
        cmdsList.clear();
        cmdsList.add("-cp");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-help");
        assertTrue(Utils.checkExec(cmdsList, "-help"));

        // pass -X to the app itself
        cmdsList.clear();
        cmdsList.add("-cp");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-X");
        assertTrue(Utils.checkExec(cmdsList, "-X"));

        // use classpath
        cmdsList.clear();
        cmdsList.add("-classpath");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");

        for (String x : appargs) {
            cmdsList.add(x);
        }
        assertTrue(Utils.checkExec(cmdsList, appargs));

        // pass -version to the app itself
        cmdsList.clear();
        cmdsList.add("-classpath");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "-version"));

        // pass -fullversion to the app itself
        cmdsList.clear();
        cmdsList.add("-classpath");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "-fullversion"));
        
        
        // pass -help to the app itself
        cmdsList.clear();
        cmdsList.add("-classpath");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-help");
        assertTrue(Utils.checkExec(cmdsList, "-help"));
        
        // pass -X to the app itself
        cmdsList.clear();
        cmdsList.add("-classpath");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("ArgsTest");
        cmdsList.add("-X");
        assertTrue(Utils.checkExec(cmdsList, "-X"));
        
        // use jar cmd
        cmdsList.clear();
        cmdsList.add("-jar");
        cmdsList.add("ArgsTest.jar");

        for (String x : appargs) {
            cmdsList.add(x);
        }
        assertTrue(Utils.checkExec(cmdsList, appargs));
        
        // pass -version to the app itself
        cmdsList.clear();
        cmdsList.add("-jar");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "-version"));
        
        // pass -fullversion to the app itself
        cmdsList.clear();
        cmdsList.add("-jar");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "-fullversion"));
        
        
        // pass -help to the app itself
        cmdsList.clear();
        cmdsList.add("-jar");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("-help");
        assertTrue(Utils.checkExec(cmdsList, "-help"));
        
        // pass -X to the app itself
        cmdsList.clear();
        cmdsList.add("-jar");
        cmdsList.add("ArgsTest.jar");
        cmdsList.add("-X");
        assertTrue(Utils.checkExec(cmdsList, "-X"));
    }

    static final String XDWARNONUSE_STR = "Foo.f3:1: warning: [warnonuse] Package ersatz.dummy has been used.";
    public void testF3cXDArgs() throws IOException {
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add("Foo.f3");
        
        Utils.createFile("Foo.f3", "import ersatz.dummy.*;");
        output = Utils.doExec(cmdsList, null, true);
        assertTrue(output.contains(XDWARNONUSE_STR));
        
        Utils.createFile("Foo.f3", "import ersatz.dummy.FooBar;");
        output = Utils.doExec(cmdsList, null, true);
        assertTrue(output.contains(XDWARNONUSE_STR));

    }
}
