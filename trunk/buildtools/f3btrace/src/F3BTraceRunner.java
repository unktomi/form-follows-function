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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ksrini
 * @author A. Sundararajan (removed csv stuff for now)
 * 
 * Readme:
 * Here are the steps needed to run the f3btrace.
 *
 * 1. Download the btrace binary bundle from https://btrace.dev.java.net/
 * 2. Extract it to BTRACE_HOME
 * 3. Run this class using % java options F3BTraceRunner options
 * In order to the following environment variables or properties must be set,
 * this is to facilitate testing of arbitrary SDK and test specimens.
 *   a. BUILD_DIR   or build.dir     : clean location for the build files
 *   b. F3_HOME or f3.home : location of f3 sdk
 *   c. BTRACE_HOME or btrace.home : location of btrace distro
 *   d. BASE_DIR    or base.dir    : location of the f3btrace directory
 * 
 * Options:
 *   Must set --jar pointer to a jar with a main-class
 *   Optional parameters:
 *   --main     : an alternate main-entry point to use
 *   --duration : how long to run the specimen for in mSecs
 *   --interval : the snapshot collection interval in mSecs
 * 
 * Notes:
 *  VM parameters to the test specimen may be passed as follows:
 *  % java -DF3BTraceRunner.vmoptions="-Xmx512m, -Xss128, -Xfoobar=XX" F3BTraceRunner options
 * 
 * Files:
 *  a. f3/f3-compiler/buildtools/f3btrace/btrace_scripts/*.java
 *     Various btrace script files that can be used to trace F3 apps.
 * 
 *  b. f3/f3-compiler/buildtools/f3btrace/src/F3BTraceRunner.java
 *     This is simply a runner script takes care of compile the script, 
 *     running btrace on the application, killing the application.
 *  
 *  c. Output files in build directory:
 *     <btrace-script-class>.class.btrace : btrace output
 */
public class F3BTraceRunner {
    static final Logger logger = Logger.getLogger(F3BTraceRunner.class.getName());
    static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    static boolean debug = true;
    
    // Paths we need
    static final String BUILD_DIR   = System.getProperty("build.dir",
            System.getenv("BUILD_DIR"));
    static final String F3_HOME = System.getProperty("f3.home", 
            System.getenv("F3_HOME"));
    static final String BTRACE_HOME = System.getProperty("btrace.home", 
            System.getenv("BTRACE_HOME"));
    static final String BASE_DIR    = System.getProperty("base.dir", 
            System.getenv("BASE_DIR"));
   
    static final String F3BTRACERUNNER_NAME = "F3TrackerRunner"; 
    static final String BTRACE_CLIENT_JAR = BTRACE_HOME + "/build/btrace-client.jar";
    static final String F3RT_JAR = F3_HOME + "/lib/shared/f3rt.jar";
    static final String BTRACE_COMPILER = "com.sun.btrace.compiler.Compiler";
    static final String BTRACE_AGENT_OPT = "-javaagent:" + BTRACE_HOME +
                "/build/btrace-agent.jar=unsafe=true,script=";
    
    static String JAVA_HOME = null;
    static String TOOLS_JAR = null;
    static String BTRACE_COMPILE_CP = null;
    static String JAVA_EXE = null;
    static String F3_EXE = null ;
   
    static final String SDK_DIR = "sdk";
    static final String JPSMARKER="JPSMARKER=";
 
    // Command line arguments
    static String btraceScript = null;
    static String appClasspath = null;
    static String mainClass = null;
    static int interval = 5*1000;
    static int duration = 2*60*1000;
    
    static final String msg[] = { "--script your_btrace_script",
                                  "--jar path_to_your_jar",
                                  "(optional) --main entry-point",
                                  "(optional) --interval " + interval + " (in msecs)",
                                  "(optional) --duration " + duration + " (in msecs)"};

    public static void main(String... args) {
        init(args);
            doRun();
            System.exit(0);
    }
    
    static void init(String[] args) {
        logger.setLevel(Level.parse(System.getProperty("log.level", "ALL")));
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
        JAVA_HOME=javaHome.getAbsolutePath();
        
        boolean mustExit = false;
        if (BTRACE_HOME == null) {
            logger.severe("BTRACE_HOME is null");
            mustExit = true;
        }
        if (F3_HOME == null) {
            logger.severe("F3_HOME is null");
            mustExit = true;
        }
        if (BASE_DIR == null) {
            logger.severe("BASE_DIR is null");
            mustExit = true;
        }
        if (BUILD_DIR == null) {
            logger.severe("BUILD_DIR is null");
            mustExit = true;
        }        
        if (args != null && args.length > 0) {
            for (int n = 0 ; n < args.length ; n++) {
                if (args[n].equals("--script")) {
                    btraceScript = args[++n];
                } else if (args[n].equals("--jar")) {
                    appClasspath = args[++n];
                } else if (args[n].equals("--main")) {
                    mainClass = args[++n];                 
                } else if (args[n].equals("--interval")) {
                    interval = Integer.parseInt(args[++n]);
                } else if (args[n].equals("--duration")) {
                    duration = Integer.parseInt(args[++n]);
                }
            }
        }
        if (btraceScript == null) {
            logger.severe("--script your_btrace_script must be specified");
            usage(true);
        }
        if (appClasspath == null) {
            logger.severe("--jar classpath_of_your_app must be specified");
            usage(true);
        }
        
        if (mustExit) {
            System.exit(1);
        }
        
        if (mainClass == null && appClasspath.endsWith(".jar")) {
            mainClass = getMainClassFromJar(appClasspath);
        }
        if (mainClass == null) {
            usage(true);
        }
        
        JAVA_EXE = getExe("java").getAbsolutePath();
        TOOLS_JAR = JAVA_HOME + "/lib/tools.jar";
        BTRACE_COMPILE_CP = BTRACE_CLIENT_JAR + File.pathSeparator + TOOLS_JAR;
        F3_EXE = getExe("f3").getAbsolutePath();
    }
    
    static String getMainClassFromJar(String jarfilename) {
        JarFile jf = null;
        String mainclassname = null;
        try {
            jf = new JarFile(jarfilename);
            Manifest mf = jf.getManifest();
            if (mf != null) {
                Attributes attr = mf.getMainAttributes();
                if (attr != null) {
                    mainclassname = attr.getValue("Main-Class");
                }
            }
        } catch (IOException ioe) {
            logger.severe("Processing: " + jarfilename + ":" + ioe.getMessage());
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                } catch (IOException ignore) { /* swallow the exception */ }
            }
        }
        return mainclassname;
    }      
    
    static void usage(boolean mustExit) {
        String out = "";
        for (String x: msg) {
            out = out.concat(x + "\n");
        }
        logger.severe(out);
        if (mustExit) {
            System.exit(1);
        }
    }
   
    static void doRun() {
        List<String> cmdsList = new ArrayList<String>();
        // compile the btrace script
        cmdsList.add(JAVA_EXE);
        cmdsList.add("-cp");
        cmdsList.add(BTRACE_COMPILE_CP); 
        cmdsList.add(BTRACE_COMPILER);
        cmdsList.add("-unsafe");
        cmdsList.add("-classpath");
        cmdsList.add(F3RT_JAR);
        cmdsList.add(btraceScript + ".java");
        doExec(cmdsList);
        
        // The btrace compiler does not return the exit codes properly, and the
        // btrace launcher will continue if the script file is non-existent, so
        // we test for the existence of the resultant class file for success.
        
        File clsFile = new File(BUILD_DIR, btraceScript + ".class");
        if (!clsFile.exists()) {
            throw new RuntimeException(btraceScript + ".class not found");
        }
        

        // run the f3btrace script and application
        cmdsList.clear();
        cmdsList.add(F3_EXE);
        cmdsList.add("-D" + F3BTRACERUNNER_NAME + ".interval=" + interval);
        String vmProps = System.getProperty(F3BTRACERUNNER_NAME + ".vmoptions");
        // ant could pass the property itself if undefined, ignore it.
        if (vmProps != null && !vmProps.contains(F3BTRACERUNNER_NAME + ".vmoptions")) {
            String vmOpts[] = vmProps.split(",\\s");
            for (String x : vmOpts) {
                cmdsList.add(x);
            }
        }
        cmdsList.add(BTRACE_AGENT_OPT + 
                clsFile.getAbsolutePath());
        cmdsList.add("-cp");
        cmdsList.add(TOOLS_JAR + File.pathSeparator + appClasspath);
        cmdsList.add(mainClass);
        startTestApplication(cmdsList);
    }
   
    static void killTestApplication() {
        List<String> cmdsList = new ArrayList<String>();
        String appId = getAppPid(F3BTRACERUNNER_NAME);
        cmdsList.clear();

        if (isWindows) {
            cmdsList.add("taskkill");
            cmdsList.add("-PID");
        } else {
            cmdsList.add("kill");
            cmdsList.add("-15");
        }
        cmdsList.add(appId);
        doExec(cmdsList);
    }

    static List<String> doExec(String... cmds) {
        List<String> cmdsList = new ArrayList<String>();
        for (String x : cmds) {
            cmdsList.add(x);
        }
        return doExec(cmdsList);
    }
    
    static String getAppPid(String appName) {
        File jpsExe = getExe("jps");
        List<String> output = doExec(jpsExe.getAbsolutePath(), "-l", "-v");
        for (String x : output) {
            if (x.contains(JPSMARKER + appName)) {
                String fld[] = x.split("\\s");
                return fld[0];
            }
        }
        throw new RuntimeException("Error: could not find the JPSMARKER");
    }
    
    static List<String> doExec(List<String> cmds) {
        if (debug) {
            System.out.println("----Execution args----");
            System.out.println("CWD=" + BUILD_DIR);
            for (String x : cmds) {
                System.out.print(x + " ");
            }
            System.out.println("");
        }
        List<String> outputList = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb = pb.directory(new File(BUILD_DIR));
        FileReader      fr = null;
        BufferedReader  rdr = null;
        try {
            Process p = pb.start();
            pb.redirectErrorStream(true);
            rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // note: its a good idea to read the whole stream, half baked
            // reads can cause undesired side-effects.
            String in = rdr.readLine();
            if (debug) {
                System.out.println("---output---");
            }
            while (in != null) {
                if (debug) {
                    System.out.println(in + " ");
                }
                System.out.println("");
                outputList.add(in);
                in = rdr.readLine();
            }
            p.waitFor();
            p.exitValue();
            if (p.exitValue() != 0) {
                System.out.println("Error: Unexpected exit value " +
                        p.exitValue());
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

    static void startTestApplication(List<String> cmds) {
        if (debug) {
            System.out.println("----Test-Execution args----");
            for (String x : cmds) {
                System.out.println(x);
            }
        }
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb = pb.directory(new File(BUILD_DIR));
        pb.redirectErrorStream(true);
        FileReader      fr = null;
        BufferedReader  rdr = null;
        try {
            final Process p = pb.start();
            rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // note: its a good idea to read the whole stream, half baked
            // reads can cause undesired side-effects.
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (debug) {
                        System.out.println("----Destroying test process----");
                    }
                    killTestApplication();
                    p.destroy();
                }
            }, duration);

            String in = rdr.readLine();
            if (debug) {
                System.out.println("---output---");
            }
            while (in != null) {
//                if (debug) {
//                    System.out.println(in + " ");
//                }
//                System.out.println("");
                in = rdr.readLine();
            }
            p.waitFor();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
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
