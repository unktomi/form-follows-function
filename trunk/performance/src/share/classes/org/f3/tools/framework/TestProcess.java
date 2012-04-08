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

package org.f3.tools.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * @author ksrini
 */
public class TestProcess {

    static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    static final boolean debug = Boolean.getBoolean(Runner.RUNNER_NAME + ".debug");
    static final Logger logger = Logger.getLogger(TestProcess.class.getName());
    static final String APP_NAME = System.getProperty("app.name");
    static final String F3_HOME = System.getProperty("f3.home",
            System.getenv("F3_HOME"));
    static final String APP_CLASSPATH =
            new File(System.getProperty("app.classpath", 
            System.getenv("APP_CLASSPATH"))).getAbsolutePath();
    static final String APP_WORKDIR =
            new File(System.getProperty("app.workdir",
            System.getenv("APP_WORKDIR"))).getAbsolutePath();
    static final String SDK_DIR = "sdk";
    static final String JPSMARKER = "JPSMARKER=" + APP_NAME;
    
    static final String JAVA_HOME;
    static final String JAVA_EXE;
    static final String F3_EXE;
    static final String JMAP_EXE;
   
    
    static List<String> doExec(String... cmds) {
        List<String> cmdsList = new ArrayList<String>();
        for (String x : cmds) {
            cmdsList.add(x);
        }
        return doExec(cmdsList);
    }
    
    static {
        File javaHome = null;
        String javaHomeEnv = System.getenv("JAVA_HOME");
        if (javaHomeEnv != null) {
            javaHome = new File(javaHomeEnv);
        } else {
            javaHome = new File(System.getProperty("java.home"));
        }
        if (javaHome.getName().endsWith("jre")) {
            javaHome = javaHome.getParentFile();
        }
        JAVA_HOME = javaHome.getAbsolutePath();
        JAVA_EXE = TestProcess.getExe("java").getAbsolutePath();
        F3_EXE = TestProcess.getExe("f3").getAbsolutePath();
        JMAP_EXE = TestProcess.getExe("jmap").getAbsolutePath();
    }
    
    static String[] getAppPids() {
        String out[] = new String[10];
        File jpsExe = getExe("jps");
        List<String> output = doExec(jpsExe.getAbsolutePath(), "-l", "-v");
        int i = 0;
        for (String x : output) {
            if (x.contains(JPSMARKER)) {
                String fld[] = x.split("\\s");
                out[i] = fld[0];
                i++;
            }
            if (i > 0) {
                return out;
            }
        }
        //logger.severe("could not find the marker <" + JPSMARKER + ">");
        return null;
    }
    
    static List<String> doExec(List<String> cmds) {
        if (debug) {
            System.out.println("----Execution args----");
            System.out.println("CWD=" + APP_CLASSPATH);
            for (String x : cmds) {
                System.out.print(x + " ");
            }
            System.out.println("");
        }
        List<String> outputList = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb = pb.directory(new File(APP_CLASSPATH));
        pb.redirectErrorStream(true);
        FileReader      fr = null;
        BufferedReader  rdr = null;
        try {
            Process p = pb.start();
            rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // note: its a good idea to read the whole stream, half baked
            // reads can cause undesired side-effects on some platforms.
            String in = rdr.readLine();
            if (debug) {
                System.out.println("---output---");
            }
            while (in != null) {
                if (debug) {
                    System.out.println(in + " ");
                }
                outputList.add(in);
                in = rdr.readLine();
            }
            p.waitFor();
            p.exitValue();
            if (p.exitValue() != 0) {
                logger.warning("Unexpected exit value " + p.exitValue());
                return null;
            }
            return outputList;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ioe) {
                    throw new RuntimeException("Error while closing file " + ioe);
                }
            }
        }
    }

    static void killTestApplication() {
        killTestApplication(false);
        killTestApplication(true);
    }
    // Note running kill with force will not allow the shutdownHooks
    // to be run by the VM.
    static void killTestApplication(boolean force) {
        List<String> cmdsList = new ArrayList<String>();
        String appIds[] = getAppPids();
        if (appIds == null) {
            return;
        }
        cmdsList.clear();

        if (isWindows) {
            cmdsList.add("taskkill");
            if (force) {
                cmdsList.add("-F");
            }
            cmdsList.add("-PID");
        } else {
            cmdsList.add("kill");
            cmdsList.add( force ? "-9" : "-15");
        }
        cmdsList.add(appIds[0]);
        doExec(cmdsList);
    }

    static File getExe(String location, String exename) {
        File bindir = new File(location, "bin");
        File outFile = (isWindows)
                ? new File(bindir, exename + ".exe")
                : new File(bindir, exename);
        if (outFile.exists()) {
            return outFile;
        }
        return null;
    }

    static File getExe(String exename) {
        File outFile = getExe(SDK_DIR, exename);
        if (outFile != null) return outFile;
        outFile = getExe(JAVA_HOME, exename);
        if (outFile != null) return outFile;
        outFile = getExe(F3_HOME, exename);
        if (outFile != null) return outFile;        

        throw new RuntimeException("Error: could not find " + exename);
    }
}
