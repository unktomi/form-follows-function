/*
 * Copyright 2001-2005 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi.test;

import com.sun.jdi.event.BreakpointEvent;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

/**
 * Base class for tests in which the target is a F3 application. This takes
 * care of setting f3rt.jar in classpath in addition to application class path.
 * Also has utility methods for f3 methods.
 *
 * @author sundar
 */
public abstract class F3TestBase extends TestScaffold {
    protected static String classPath() {
        return testBuildDirectory() +
                File.pathSeparator +
                f3rtJarPath();
    }

    protected static String f3rtJarPath() {
        return System.getProperty("f3rt.jar");
    }

    protected static String f3MainClassName() {
        return "org.f3.runtime.Main";
    }

    protected static String f3RunMethodName() {
        return "f3$run$";
    }

    protected static String f3RunMethodSignature() {
        return "(Lorg/f3/runtime/sequence/Sequence;)Ljava/lang/Object;";
    }

    protected static final String[] ARGS = {
        "-J-classpath",
        classPath(),
        f3MainClassName()
    };

    protected static String[] arguments(String targetClassName) {
        String[] args = new String[ARGS.length + 1];
        System.arraycopy(ARGS, 0, args, 0, ARGS.length);
        args[args.length - 1] = targetClassName;
        return args;
    }

    String testClassName;
    File actualFile;
    PrintStream actualOut;
    BufferedReader expectedReader;  // != null means there is a .EXPECTED file
    
    void writeActual(String p1) {
        // Many of the toString methods add object IDs in the form (id=ddd), where ddd can vary.
        String fixit = p1.replaceAll("\\(id=[0-9]+\\)", "");
        if (actualOut == null) {
            try {
                String actualName = System.getProperty("build.test.classes.dir") + 
                    File.separator +
                    testClassName + ".ACTUAL";
                actualFile = new File(actualName);
                actualOut = new PrintStream(new FileOutputStream(actualFile));
            } catch (FileNotFoundException ee) {
                println("Error: Cannot create output file : " + actualFile);
                println(fixit);
                return;
            }
        }
        actualOut.printf(fixit + "\n");
        //println(p1);  // useful for debugging
    }

    boolean didTestPass() {
        if (expectedReader == null) {
            println("Error: Call to writeActual but no .EXPECTED file.  Use println instead of writeActual");
            return false;
        }
        try {
            BufferedReader actualReader =  new BufferedReader(new FileReader(actualFile));
            int lineNum = 0;
            while(true) {
                lineNum++;
                String actualLine = actualReader.readLine();
                String expectedLine = expectedReader.readLine();
                if (actualLine == null) {
                    if (expectedLine == null) {
                        return true;
                    }
                    println("Error: extra line in EXPECTED, line = " + lineNum);
                    println( expectedLine);
                    return false;
                }
                if (expectedLine == null) {
                    println("Error: extra line in ACTUAL: line = " + lineNum);
                    println( actualLine);
                    return false;
                }
                if (!actualLine.equals(expectedLine)) {
                    println("Error:  Output is wrong at line = " + lineNum);
                    println("  ACTUAL   = " + actualLine);
                    println("  EXPECTED = " + expectedLine);
                    return false;
                }
            }
        } catch(Exception ee) {
            println("Error: IO Exception checking output: " + ee);
        }
        return false;
    }

    protected F3TestBase(String targetClassName) {
        super(arguments(targetClassName));
        testClassName = this.getClass().getSimpleName();

        String expectedFileName = System.getProperty("user.dir") + 
            (".test." + 
             this.getClass().getName()).replace(".", File.separator) + ".EXPECTED";
        try {
            expectedReader = new BufferedReader(new FileReader(expectedFileName));
        } catch (FileNotFoundException ee) {
            expectedReader = null;
        }
    }

    protected F3TestBase(String[] args) {
        super(args);
    }

    protected BreakpointEvent startToMain() {
        return startToMain(f3MainClassName());
    }
}
