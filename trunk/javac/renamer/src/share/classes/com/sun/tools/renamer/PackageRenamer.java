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
package com.sun.tools.renamer;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ClassTranslator;
import com.sun.tools.classfile.ClassWriter;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPool.CONSTANT_Utf8_info;
import com.sun.tools.classfile.ConstantPool.CPInfo;
import com.sun.tools.classfile.ConstantPoolException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author ksrini
 */
public class PackageRenamer {

    private static Logger logger = Logger.getLogger(PackageRenamer.class.getName());
    private static Properties config = null;
    private static HashSet<PatternContainer> renameList = null;
    private static int renamecount = 0;

    private static void usage(int exitcode) {
        System.out.println("Usage: PackageRenamer [-v ] -i input-jar -o output-jar [-c config-file]\n");
        System.out.println("       Ex: config file  may contain com.sun.tools.javac=com.sun.tools.mjavac\n");
        System.exit(exitcode);
    }

    static void initConfig(String configfile) throws IOException {
        renameList = new HashSet<PatternContainer>();
        if (configfile == null) {
            renameList.add(new PatternContainer("com/sun/tools/javac",
                    "com/sun/tools/mjavac"));
            renameList.add(new PatternContainer("com.sun.tools.javac",
                    "com.sun.tools.mjavac"));
        } else {
            config = new Properties();
            config.load(new FileReader(configfile));
            for (Object o : Collections.list(config.keys())) {
                String key = (String) o;
                String value = config.getProperty(key);
                renameList.add(new PatternContainer(key, value));
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ZipFile zf = null;
        FileOutputStream fos = null;
        ZipOutputStream zstream = null;

        String infile = null;
        String outfile = null;
        String configfile = null;
        if (args == null || args.length < 4) {
            usage(1);
        }
        logger.setLevel(Level.WARNING);
        for (int n = 0 ; n < args.length ; n++) {
            if (args[n].equals("-v")) {
                logger.setLevel(Level.INFO);
            }
            if (args[n].equals("-i")) {
                n++;
                infile = args[n];
            }
            if (args[n].equals("-o")) {
                n++;
                outfile = args[n];
            }
            if (args[n].equals("-c")) {
                n++;
                configfile = args[n];
            }
        }
        try {
            initConfig(configfile);
            zf = new ZipFile(infile);
            fos = new FileOutputStream(outfile);
            zstream = new ZipOutputStream(fos);
            RenamedEntry zout = null;
            for (ZipEntry ze : Collections.list(zf.entries())) {
                if (ze.isDirectory()) {
                    continue;
                }
                logger.info("processing: " + ze);
                InputStream is = zf.getInputStream(ze);
                zout = transForm(ze, is);
                is.close();
                zout.finish();
                if (zout != null) {
                    zstream.putNextEntry(zout);
                    zstream.write(zout.getByteArray());
                    zstream.closeEntry();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageRenamer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException ex) { /* ignore */ }
            }
            if (zstream != null) {
                try {
                    zstream.close();
                } catch (IOException ex) { /* ignore */ }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) { /* ignore */ }
            }
        }
        logger.info("Renamed " + renamecount + " instances.");
    }

    private static String replaceString(String in) {
        for (PatternContainer pc : renameList) {
            if (in.contains(pc.source)) {
                renamecount++;
                String out = pc.getPattern().matcher(in).replaceAll(pc.target);
                logger.info("renaming: " + in + " to " + out);
                return out;
            }
        }
        return in;
    }

    private static CONSTANT_Utf8_info replaceString(CONSTANT_Utf8_info cinfo) {
        String s = replaceString(cinfo.value);
        return new CONSTANT_Utf8_info(s);
    }

    private static RenamedEntry transForm(ZipEntry ze, InputStream in) {
        RenamedEntry re = null;
        try {
            if (ze.getName().endsWith(".class")) {
                ClassFile cf = ClassFile.read(in);
                CPInfo[] ncpinfos = new CPInfo[cf.constant_pool.size()];
                for (int n = 1; n < cf.constant_pool.size(); n++) {
                    CPInfo cinfo = null;
                    try {
                        cinfo = cf.constant_pool.get(n);
                    } catch (ConstantPool.InvalidIndex ii) {                        
                        /* ignore happens for Long and Doubles*/
                    }
                    if (cinfo != null && cinfo.getTag() == ConstantPool.CONSTANT_Utf8) {
                        CONSTANT_Utf8_info ncinfo =
                                replaceString((CONSTANT_Utf8_info) cinfo);
                        ncpinfos[n] = ncinfo;
                    } else {
                        ncpinfos[n] = cinfo;
                    }
                }
                Map<Object, Object> xmap = new HashMap<Object, Object>();
                xmap.put(cf.constant_pool, new ConstantPool(ncpinfos));
                ClassTranslator translator = new ClassTranslator();
                ClassFile ncf = translator.translate(cf, xmap);
                String cname = replaceString(ze.getName());
                ZipEntry nze = new ZipEntry(cname);
                nze.setTime(ze.getTime());
                nze.setMethod(ze.getMethod());
                nze.setSize(ze.getSize());
                ClassWriter cw = new ClassWriter();
                re = new RenamedEntry(nze);
                cw.write(ncf, re.getOutputStream());
            } else if (ze.getName().toLowerCase().startsWith("meta-inf")) {
                re = new RenamedEntry(ze);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(re.getOutputStream()));
                String line = br.readLine();
                while (line != null) {
                    line = replaceString(line);
                    bw.write(line + "\n");
                    line = br.readLine();
                }
                bw.flush();
            } else {
                re = new RenamedEntry(ze, in);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ConstantPoolException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return re;
    }
}
