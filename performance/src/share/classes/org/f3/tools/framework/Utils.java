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
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 *
 * @author ksrini
 */

public class Utils {
    static final Logger logger = Logger.getLogger(Utils.class.getName());
    private static String buildId = null;
    private static final String F3_HOME = System.getProperty("f3.home",
            System.getenv("F3_HOME"));
    private static final String HUDSON_URL = System.getenv("HUDSON_URL");
    private static final String HUDSON_JOB = System.getenv("JOB_NAME");
    private static final String HUDSON_BLD = System.getenv("BUILD_NUMBER");
    private static final String LAST_BLD   = "lastSuccessfulBuild";
    static final String CSV_FORMAT_STRING = "%s %s %s\n";
    static final String RESULTS_CSV = "results.csv";
    private static final String HG_REPO =
            "http://kenai.com/hg/f3-compiler~soma-master/file/tip/";
    private static final String BENCHMARKS_SRC =
            "performance/benchmarks/src/";

    static String getChangesUrl() {
        return HUDSON_URL + "job/" + HUDSON_JOB + "/changes";
    }

    static String getJobUrl(boolean isLast) {
       return HUDSON_URL + "job/" + HUDSON_JOB + "/" + ((isLast) ? LAST_BLD : HUDSON_BLD);
    }
    
    static String getArtifactsUrl(boolean isLast) {
        return getJobUrl(isLast) + "/artifact/build/performance/output";
    }
    
    static void toPlotFile(File outputFile, String yvalue) {
        toPlotFile(outputFile, yvalue, null);
    }

    static void toCsvFile(String pvalue, String mvalue) {
        FileOutputStream fos = null;
        PrintStream ps = null;
        try {
            File csvFile = new File(TestProcess.APP_WORKDIR, RESULTS_CSV);
            String header = null;
            if (!csvFile.exists()) {
                header = "App Name Perf.  Footprint";
            }
            fos = new FileOutputStream(csvFile, true);
            ps = new PrintStream(fos);
            if (header != null)
                ps.println(header);
            ps.printf(CSV_FORMAT_STRING, TestProcess.APP_NAME, pvalue, mvalue);
        } catch (IOException ioe) {
            TestProcess.logger.severe(ioe.toString());
        } finally {
            close(ps);
            close(fos);
        }
    }

    static void toPlotFile(File outFile, String yvalue, String uvalue) {
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        PrintStream ps = null;
        try {
            fos = new FileOutputStream(outFile);
            ps = new PrintStream(fos);
            ps.println("YVALUE=" + yvalue);
            if (uvalue == null) {
                ps.println("URL=" + Utils.getChangesUrl());
            } else {
                ps.println("URL=" + uvalue);
            }
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
        } finally {
            close(ps);
            close(fos);
        }
    }
    
    public static String getBuildId() {
        if (buildId != null) return buildId;
        FileReader rdr = null;
        BufferedReader br = null;
        try {
            rdr = new FileReader(new File(F3_HOME, "timestamp"));
            br = new BufferedReader(rdr);
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("Build-Number")) {
                    String[] flds = line.split(":");
                    if (flds[1] != null) {
                        buildId = flds[1].trim();
                    }
                }
                line = br.readLine();
            }     
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
            throw new RuntimeException(ioe);
        } finally {
            close(br);
            close(rdr);
        }  
        return buildId;
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
            close((Closeable)jf);
        }
        return mainclassname;
    }

    static Map<String, ResultData> readCsv(InputStream is) {
        InputStreamReader rdr = new InputStreamReader(is);
        return readCsv(rdr);
    }

    protected static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {
            }
        }
    }

    static Map<String, ResultData> readCurrentResultsCsv() {
        return readCsv(RESULTS_CSV);
    }

    static Map<String, ResultData> readResults12Csv() {
       return readCsv("results-12.csv");
    }

    static Map<String, ResultData> readResults13Csv() {
          return readCsv("results-13.csv");
    }

    static Map<String, ResultData> readGoalsCsv() {
            return readCsv("goals.csv");
    }
    static Map<String, ResultData> readLastBuildCsv() {
        URL lastBuildUrl = null;
        InputStream conns = null;
        try {
            lastBuildUrl = new URL(getArtifactsUrl(true) + "/" + RESULTS_CSV);
            logger.info("last-build="+lastBuildUrl);
            conns = lastBuildUrl.openStream();
            return readCsv(conns);
        } catch (Exception ex) {
            logger.warning(ex.toString());
        } finally {
            close(conns);
        }
        return null;
    }

    static Map<String, ResultData> readCsv(String infile) {
        FileReader frdr = null;
        try {
            frdr = new FileReader(infile);
            return readCsv(frdr);
        } catch (IOException ioe) {
            logger.warning(ioe.toString());
        } finally {
            close(frdr);
        }
        return null;
    }

    static Map<String, ResultData> readCsv(Reader rdr) {
        BufferedReader br = null;
        HashMap<String, ResultData> out = new HashMap<String, ResultData>();
        try {
            br = new BufferedReader(rdr);
            String line = br.readLine();
            line = br.readLine(); // skip the header
            while (line != null) {
                String[] flds = line.split("\\s");
                ResultData rd = new ResultData(flds);
                out.put(rd.getName(), rd);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            logger.severe(ioe.toString());
        } finally {
            close(br);
        }
        return out;
    }

    static String getBenchmarkSourceLink(String benchmark) {
        return HG_REPO + BENCHMARKS_SRC + benchmark + ".f3";
    }
}
