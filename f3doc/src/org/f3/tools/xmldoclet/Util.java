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

package org.f3.tools.xmldoclet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class to copy "doc-files" contents for each package to output.
 *
 * @author A. Sundararajan
 */
public class Util {
    // In each source path look for "doc-files" under each package dir and copy the
    // contents appropriate output directory.
    public static void copyDocFiles(String[] packages, String sourcePath, File docsdir) {
        String[] srcpaths = sourcePath.split(File.pathSeparator);
        for (String srcpath : srcpaths) {
            for (String name : packages) {
                // first copy stuff under "com.acme.examplepackage" for every package
                String srcdir = srcpath + File.separator + name;
                String destdir = docsdir + File.separator + name;
                copyDocFiles(srcdir, destdir);
                // now try com/acme/examplepackage subdir as well.
                srcdir = srcpath + File.separator + name.replace('.', File.separatorChar);
                copyDocFiles(srcdir, destdir);
            }
        }
    }
    
    // FIXME: Make this configurable by XMLDoclet's command line options
    private static final String DOC_FILE_DIR_NAME = "doc-files";

    // Copy the given directory contents from the source package directory
    // to the generated documentation directory. 
    private static void copyDocFiles(String src, String dest) {
        File srcdir = new File(src + File.separator + DOC_FILE_DIR_NAME);
        if (!srcdir.exists() || !srcdir.isDirectory()) {
            return;
        }
        try {
            File destdir = new File(dest + File.separator + DOC_FILE_DIR_NAME);
            destdir.mkdirs();
            String[] files = srcdir.list();
            for (int i = 0; i < files.length; i++) {
                File srcfile = new File(srcdir, files[i]);
                File destfile = new File(destdir, files[i]);
                if (srcfile.isFile()) {
                    copyFile(srcfile, destfile);
                }
            }
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // copies source file to destination file.
    private static void copyFile(File srcfile, File destfile)
            throws IOException {
        byte[] buf = new byte[512];
        int length = 0;
        FileInputStream input = new FileInputStream(srcfile);
        File destdir = destfile.getParentFile();
        destdir.mkdirs();
        FileOutputStream output = new FileOutputStream(destfile);
        try {
            while ((length = input.read(buf)) != -1) {
                output.write(buf, 0, length);
            }
        } catch (FileNotFoundException ex) {
        } catch (SecurityException ex) {
        } finally {
            input.close();
            output.close();
        }
    }
}
