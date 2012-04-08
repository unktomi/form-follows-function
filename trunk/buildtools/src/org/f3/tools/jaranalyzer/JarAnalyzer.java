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
package org.f3.tools.jaranalyzer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * This tool is used to compute from a jar file the size in kbytes used by each
 * package found, as well as the total size.
 * The result files have the right format for being read by the Plot plugin of
 * Hudson.
 * Typical use is to call that tool from an Ant target.
 * Parameters are:
 * <ul>
 * <li>the path to the jar file
 * <li>the path to a place top write result files
 * <li>a URL value that points to one of the result file, the HTML array that
 * lists size per package and total size.
 * </ul>
 *
 * There's alse a legacy way of call it manually in order to compare two
 * successive jar files.
 * 
 * @author ksrini
 */
public class JarAnalyzer {

    static String getPackageName(String name) {
        String out = name.substring(0, name.lastIndexOf("/"));
        return out.replace("/", ".");
    }

    static Hashtable<String, PkgEntry> readJarFile(URL url) {
        FileInputStream fis = null;
        try {
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            return readJarFile(conn.getInputStream());
        } catch (IOException ioe) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ioe);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return null;
    }

    static Hashtable<String, PkgEntry> readJarFile(File inFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inFile);
            return readJarFile(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return null;
    }

    static Hashtable<String, PkgEntry> readJarFile(InputStream in) {
        Hashtable<String, PkgEntry> tbl = new Hashtable<String, PkgEntry>();
        try {
            ZipInputStream zis = new ZipInputStream(in);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String zname = ze.getName();
                if (zname.endsWith(".class")) {
                    String pkgname = getPackageName(zname);
                    if (!tbl.containsKey(pkgname)) {
                        long csize = getCompressedSize(zis);
                        tbl.put(pkgname, new PkgEntry(ze.getSize(), csize));
                    } else {
                        PkgEntry pe  = tbl.get(pkgname);
                        long csize = getCompressedSize(zis);
                        pe.addSizes(ze.getSize(), csize);

                        tbl.put(pkgname, pe);
                    }
                }
                ze = zis.getNextEntry();
            }
        } catch (ZipException ex) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return tbl;
    }

    static void dumpToFile(String name1, String name2, OutputStream ostream,
            Hashtable<String, PkgEntry> tbl1, Hashtable<String, PkgEntry> tbl2) {

        List<String> keyList = new ArrayList<String>();
        for (String x : Collections.list(tbl1.keys())) {
            keyList.add(x);
        }
        Collections.sort(keyList);
        PrintWriter pw = null;
        pw = new PrintWriter(new OutputStreamWriter(ostream));
        pw.printf("\t%s\t%s\n", name1, name2);
        long sum1 = 0L;
        long sum2 = 0L;
        for (String x : keyList) {
            pw.printf("%s\t%s\t%s\n", x, tbl1.get(x).getSize() / 1024, tbl2.get(x).getSize() / 1024);
            sum1 += tbl1.get(x).getSize();
            sum2 += tbl2.get(x).getSize();
        }
        pw.printf("Total\t%s\t%s\n", sum1 / 1024, sum2 / 1024);
        pw.flush();
    }

    static void dumpToFile(String name, OutputStream ostream,
            Hashtable<String, Long> tbl) {

        List<String> keyList = new ArrayList<String>();
        for (String x : Collections.list(tbl.keys())) {
            keyList.add(x);
        }
        Collections.sort(keyList);
        PrintWriter pw = null;
        pw = new PrintWriter(new OutputStreamWriter(ostream));
        pw.println(name);
        long sum = 0L;
        for (String x : keyList) {
            pw.printf("%s\t%s\n", x, tbl.get(x) / 1024);
            sum += tbl.get(x);
        }
        pw.printf("Total\t%s\n", sum / 1024);
        pw.flush();
    }

    static void dumpToFileAllPackages(
            Hashtable<String, PkgEntry> tbl, String outputRootDir, String urlDir)
            throws IOException {

        // report.properties file
        // This is the input file for the plotter plugin
        File file1 = new File(outputRootDir + "/staticsizes");
        file1.createNewFile();
        OutputStream ostream1 = new FileOutputStream(file1);

        // report.properties.html file
        // This is the URL to access detailed packages informations
        File file2 = new File(outputRootDir + "/staticsizes.html");
        file2.createNewFile();
        OutputStream ostream2 = new FileOutputStream(file2);

        File file3 = new File(outputRootDir + "/staticsizes.file-count");
        file3.createNewFile();
        OutputStream ostream3 = new FileOutputStream(file3);

        List<String> keyList = new ArrayList<String>();
        for (String x : Collections.list(tbl.keys())) {
            keyList.add(x);
        }
        Collections.sort(keyList);
  

        // Build the html table detailed packages informations
        PrintWriter pw2 = new PrintWriter(new OutputStreamWriter(ostream2));
        pw2.printf("<HTML>\n");
        pw2.printf("<BODY LANG=\"en-US\" DIR=\"LTR\">\n");
        pw2.printf("<CENTER>\n");
        pw2.printf("\t<TABLE WIDTH=420 BORDER=1 CELLPADDING=4 CELLSPACING=0>\n");
        pw2.printf("\t<TR VALIGN=TOP>\n");
        pw2.printf("\t\t<TH WIDTH=308>");
        pw2.printf("<P>Package</P>");
        pw2.printf("</TH>\n");
        pw2.printf("\t\t<TD WIDTH=95>");
        pw2.printf("<P><B>Size (uncompressed) in kbytes</B></P></TD>\n");
        pw2.printf("\t\t<TD WIDTH=95>");
        pw2.printf("<P><B>Size (compressed) in kbytes</B></P></TD>\n");
        pw2.printf("\t\t<TD WIDTH=50>");
        pw2.printf("<P><B>File Count</B></P></TD>\n");
        pw2.printf("\t</TR>\n");
        long sum  = 0L;
        long csum = 0L;
        int fcount = 0;
        for (String x : keyList) {
            long sz = tbl.get(x).getSize();
            sum += sz;
            long csz = tbl.get(x).getCompressedSize();
            csum += csz;
            int n = tbl.get(x).getCount();
            fcount += n;
            pw2.printf("\t<TR VALIGN=TOP>\n");
            pw2.printf("\t\t<TD WIDTH=308>");
            pw2.printf("<P>" + x + "</P></TD>\n");
            pw2.printf("\t\t<TD WIDTH=95>");
            pw2.printf("<P>" + sz / 1024 + "</P></TD>\n");
            pw2.printf("\t\t<TD WIDTH=50>");
            pw2.printf("<P>" + csz / 1024 + "</P></TD>\n");
            pw2.printf("\t\t<TD WIDTH=50>");
            pw2.printf("<P>" + n + "</P></TD>\n");
            pw2.printf("\t</TR>\n");
        }
        pw2.printf("\t<TR VALIGN=TOP>\n");
        pw2.printf("\t\t<TD WIDTH=308>");
        pw2.printf("<P><B>Total</B></P></TD>\n");
        pw2.printf("\t\t<TD WIDTH=95>");
        pw2.printf("<P>" + sum / 1024 + "</P></TD>\n");
        pw2.printf("\t\t<TD WIDTH=95>");
        pw2.printf("<P>" + csum / 1024 + "</P></TD>\n");
        pw2.printf("\t\t<TD WIDTH=50>");
        pw2.printf("<P>" + fcount + "</P></TD>\n");
        pw2.printf("\t</TR>\n");

        pw2.printf("\t</TABLE>\n");
        pw2.printf("</CENTER>\n");
        pw2.printf("</BODY>\n");
        pw2.printf("</HTML>\n");

        pw2.flush();
        pw2.close();

        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(ostream1));
        pw1.printf("YVALUE=%s\n", sum / 1024);
        pw1.printf("URL=%s\n", urlDir + "/staticsizes.html");
        pw1.flush();
        pw1.close();

        PrintWriter pw3 = new PrintWriter(new OutputStreamWriter(ostream3));
        pw3.printf("YVALUE=%s\n", fcount);
        pw3.printf("URL=%s\n", urlDir + "/staticsizes.html");
        pw3.flush();
        pw3.close();      

    }
    private static final String BLDTAG = "BLDTAG";
    private static final String F3 =
            "http://f3.org/hudson/job/f3-compiler/" +
            BLDTAG + "/artifact/dist/lib/shared/f3rt.jar";

    private static JarStat getTotals(File file) {
        ZipFile zf = null;
        JarStat js = null;
        try {
            js = new JarStat(file);
            zf = new ZipFile(file);
            for (ZipEntry ze : Collections.list(zf.entries())) {
                js.addSizes(zf, ze);
            }
        } catch (ZipException ex) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException ex) {
                }
            }
        }
        return js;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Usage: input_jar_file output_root_dir url_dir");
            System.err.println("Usage: --compare bld# bld#");
            System.exit(1);
        } else if (args[0].endsWith("compare")) {
            try {
                URL url1 = new URL(F3.replace(BLDTAG, args[1]));
                URL url2 = new URL(F3.replace(BLDTAG, args[2]));
                Hashtable<String, PkgEntry> tbl1 = readJarFile(url1);
                Hashtable<String, PkgEntry> tbl2 = readJarFile(url2);
                dumpToFile(args[1], args[2], System.out, tbl1, tbl2);
            } catch (MalformedURLException ex) {
                Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        } else {
            try {
                String inputJarFile = args[0];
                String outputRootDir = args[1];
                String urlDir = args[2];
                Hashtable<String, PkgEntry> tbl = readJarFile(new File(inputJarFile));

                // Plot information for all packages
                dumpToFileAllPackages(tbl, outputRootDir, urlDir);
                JarStat js = getTotals(new File(inputJarFile));
                File outputFile = new File(outputRootDir + "/staticsizes." +
                        "jar-size-compressed");
                js.printSize(outputFile, true, urlDir);
                outputFile = new File(outputRootDir + "/staticsizes." +
                        "jar-size-uncompressed");
                js.printSize(outputFile, false, urlDir);
                // Plot information for single package
                for (String key : tbl.keySet()) {
                    outputFile = new File(outputRootDir + "/staticsizes." + key);
                    outputFile.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(outputFile);
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(ostream));
                    pw.printf("YVALUE=%s\n", tbl.get(key).getSize() / 1024);
                    pw.flush();
                    pw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JarAnalyzer.class.getName()).log(Level.SEVERE,
                        null, ex);
                System.exit(1);
            }
        }
        System.exit(0);
    }

    static long getCompressedSize(InputStream is) {
        DeflaterOutputStream dos = null;
        long size = 0L;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Deflater def = new Deflater();
            def.setLevel(Deflater.BEST_COMPRESSION);
            dos = new DeflaterOutputStream(baos, def);
            byte buf[] = new byte[8192];
            int n = is.read(buf);
            while (n > 0) {
                dos.write(buf);
                n = is.read(buf);
            }
            dos.flush();
            dos.finish();
            size = baos.size();
        } catch (IOException ex) {
            Logger.getLogger(JarStat.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dos.close();
            } catch (IOException ex) {
                Logger.getLogger(JarStat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return size;
    }
}

class PkgEntry {
    private long size;
    private long csize;
    private int  count;

    private PkgEntry() {}

    PkgEntry(long sz, long csz) {
        this.size = sz;
        this.csize = csz;
        this.count = 1;
    }
    
    void addSizes(long sz, long csz) {
        this.size += sz;
        this.csize += csz;
        this.count++;
    }

    long getSize() {
        return this.size;
    }

    long getCompressedSize() {
        return this.csize;
    }

    int getCount() {
        return this.count;
    }
}

class JarStat {

    private File jarFile;
    private long size;  // uncompressed sizes
    private long csize; // compressed sizes

    JarStat(File jarFile) {
        this.jarFile = jarFile;
        size = 0L;
        csize = 0L;
    }

    void addSizes(ZipFile zf, ZipEntry ze) {
        long sz = ze.getSize();
        if (sz == 0) { // don't bother with 0 file size
            return;
        }
        this.size += sz;

        GZIPOutputStream gzos = null;
        try {
            InputStream zis = zf.getInputStream(ze);
            this.csize += JarAnalyzer.getCompressedSize(zis);
        } catch (IOException ex) {
            Logger.getLogger(JarStat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void printSize(File outFile, boolean reportCompressed, String urlDir) {
        try {
            outFile.createNewFile();
            OutputStream ostream = new FileOutputStream(outFile);
            PrintWriter pw = new PrintWriter(ostream);
            pw.println("YVALUE=" + ((reportCompressed) ? this.csize : this.size) / 1024);
            if (urlDir != null) {
                pw.println("URL=" + urlDir + "/staticsizes.html");
            }
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(JarStat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
