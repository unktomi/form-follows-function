/*
 * Copyright 2002-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package org.f3.tools.classreporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author ksrini
 */
public class ClassReporter {
    static boolean debug = false;
    
    /**
     * All this really does is execute a class and create the report file for
     * the hudson plotter, by harvesting the output of -verbose:class.
     * usage: -jar="Foo" -build.dir="build_dir" -dist.dir="dist"
     *        -reference.url="http//foobar...job/id"
     */

    private static  String f3classname  = "HelloWorld";
    private static  String jarfilename  = null;
    private static  String builddir    = ".";
    private static  String distdir  = "dist";
    private static  String referenceurl = "UNKNOWN";
    private static final Hashtable<String, String> loadedClasses =
            new Hashtable<String, String>();
    
    static void generateReports() throws IOException  {
        parseOutput();

        // print grand total
        printReport(new File(builddir, f3classname + "-Total-classes.txt"),
                loadedClasses.size());
        
        Hashtable<String, Integer> jarClasses = new Hashtable<String, Integer>();
        
        // compute the totals for each of the jars
        for (String jarname : loadedClasses.values()) {
            Integer ivalue = jarClasses.get(jarname);
            if (ivalue == null) {
                jarClasses.put(jarname, new Integer(1));
            } else {
                jarClasses.put(jarname, new Integer(ivalue.intValue() + 1));
            }           
        }
        
        // print the total for each of the jars
        for (String jarname : jarClasses.keySet()) {
            printReport(new File(builddir, f3classname + "-" + jarname + "-classes.txt"),
                    jarClasses.get(jarname).intValue());
        }

        printToCSV(new File(builddir, f3classname + ".csv"));
    }

    static void printToCSV(File csvFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(csvFile);
        PrintStream ps = new PrintStream(fos);
        
        for (String classname : Collections.list(loadedClasses.keys())) {
            ps.println(classname + "\t" + loadedClasses.get(classname));
        }

        ps.close();
        fos.close();
    }
    static void printReport(File reportFile, int value) throws IOException {
        FileOutputStream fos = new FileOutputStream(reportFile);
        PrintStream ps = new PrintStream(fos);
        ps.println("YVALUE=" + value);
        ps.println("URL=" + referenceurl);
        ps.close();
        fos.close();
    }
    
    /*
     * emit our F3 code
     */
    static void CreateSampleF3(File f3File) {
        FileOutputStream fos = null;
        PrintStream ps = null;
        try {
            fos = new FileOutputStream(f3File);
            ps = new PrintStream(fos);
            ps.println("F3.println(\"Hello World\");");            
        } catch (IOException ioe) {
            Logger.getLogger(ClassReporter.class.getName()).log(Level.SEVERE, null, ioe);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClassReporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        doExec(getExe("f3c").getAbsolutePath(), f3File.getAbsolutePath());
    }

    static String getJarName(String inName) {
        File f = new File(inName);
        String jarname = f.getName();
        String out = "ANON";
        if (inName.contains(".jar")) {
            out = jarname.substring(0, jarname.indexOf(".jar"));
        }
        return out;
    }

    static void parseOutput() {
        List<String> output = null;
        if (jarfilename == null) {
            File f3File = new File(builddir, f3classname + ".f3");
            CreateSampleF3(f3File);
            output = doExec(getExe("f3").getAbsolutePath(), "-cp",
                builddir, "-verbose:class", f3classname  );
        } else {
            output = doExec(getExe("f3").getAbsolutePath(),
                    "-verbose:class", "-jar", jarfilename  );
        }
        for (String x : output) {
            if (x.startsWith("[Loaded")) {
                String[] fields = x.split("\\s");
                // The last field is the jarname.
                String jarname = getJarName(fields[fields.length - 1]);
                loadedClasses.put(fields[1], jarname);
            }
        }
    }

    static List<String> doExec(String... cmds) {
        List<String> cmdsList = new ArrayList<String>();
        for (String x : cmds) {
            cmdsList.add(x);
        }
        return doExec(cmdsList);
    }
    
    static List<String> doExec(List<String> cmds) {
        if (debug) {
            System.out.println("----Execution args----");
            for (String x : cmds) {
                System.out.println(x);
            }
        }
        List<String> outputList = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb = pb.directory(new File(builddir));
        FileReader      fr = null;
        BufferedReader  rdr = null;
        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // note: its a good idea to read the whole stream, half baked
            // reads can cause undesired side-effects.
            String in = rdr.readLine();
            if (debug) {
                System.out.println("---output---");
            }
            while (in != null) {
                if (debug) {
                    System.out.println(in);
                }
                outputList.add(in);
                in = rdr.readLine();
            }
            p.waitFor();
            p.destroy();
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

    static void usage(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    static File getExe(String exename) {
        File bindir = new File(distdir, "bin");
        if (System.getProperty("os.name").startsWith("Windows")) {
            return new File(bindir, exename + ".exe");
        }
        return new File(bindir, exename);
    }

    public static void main(String[] args) {
        try {
            if (args != null) {
                for (int i = 0 ; i < args.length ; i++) {
                    if (args[i].startsWith("-jar")) {
                        f3classname = args[i].substring(args[i].indexOf("=") + 1);
                        if (f3classname.endsWith(".jar")) {
                            jarfilename=f3classname;
                            String jfname = new File(jarfilename).getName();
                            f3classname = jfname.substring(0, jfname.indexOf(".jar"));
                        }
                    } else if (args[i].startsWith("-build.dir")) {
                        builddir = args[i].substring(args[i].indexOf("=") + 1);
                    } else if (args[i].startsWith("-dist.dir")) {
                        distdir = args[i].substring(args[i].indexOf("=") + 1);
                    } else if (args[i].startsWith("-reference.url")) {
                        referenceurl = args[i].substring(args[i].indexOf("=") + 1);
                    }
                }
            }
            generateReports();
        } catch (IOException ex) {
            Logger.getLogger(ClassReporter.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}
