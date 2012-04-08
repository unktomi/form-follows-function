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

package org.f3.tools.f3doc;

import com.sun.tools.mjavac.code.Symbol.PackageSymbol;
import com.sun.tools.mjavac.jvm.ClassReader;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JavacFileManager;

import java.io.File;
import java.util.EnumSet;
import javax.tools.JavaFileObject;

/** 
 * Javadoc uses an extended class reader that records package.html entries.
 * Note this is the "low-level" class reader, that reads JVM-style symbols.
 * The "f3-level" class reader is a F3ClassReader that <i>delegates</i>
 * to a F3docClassReader.  This may be fragile, given that (unlike f3c)
 * we only have set of Symbols and Types, but it seems to work - for now.
 */
class F3docClassReader extends ClassReader {
    private DocEnv docenv;
    private EnumSet<JavaFileObject.Kind> all = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                          JavaFileObject.Kind.SOURCE,
                                                          JavaFileObject.Kind.HTML);
    private EnumSet<JavaFileObject.Kind> noSource = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                               JavaFileObject.Kind.HTML);

    public F3docClassReader(Context context) {
        super(context, false);
        docenv = DocEnv.instance(context);
    }

    /**
     * Override getPackageFileKinds to include search for package.html
     */
    @Override
    protected EnumSet<JavaFileObject.Kind> getPackageFileKinds() {
        return docenv.docClasses ? noSource : all;
    }

    /**
     * Override extraFileActions to check for package documentation
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void extraFileActions(PackageSymbol pack, JavaFileObject fo) {
        CharSequence fileName = com.sun.tools.mjavac.util.Old199.getName(fo);
        if (docenv != null && fileName.equals("package.html")) {
            if (fo instanceof JavacFileManager.ZipFileObject) {
                JavacFileManager.ZipFileObject zfo = (JavacFileManager.ZipFileObject) fo;
                String zipName = zfo.getZipName();
                String entryName = zfo.getZipEntryName();
                int lastSep = entryName.lastIndexOf("/");
                String classPathName = entryName.substring(0, lastSep + 1);
                docenv.getPackageDoc(pack).setDocPath(zipName, classPathName);
            }
            else if (fo instanceof JavacFileManager.ZipFileIndexFileObject) {
                JavacFileManager.ZipFileIndexFileObject zfo = (JavacFileManager.ZipFileIndexFileObject) fo;
                String zipName = zfo.getZipName();
                String entryName = zfo.getZipEntryName();
                if (File.separatorChar != '/') {
                    entryName = entryName.replace(File.separatorChar, '/');
                }

                int lastSep = entryName.lastIndexOf("/");
                String classPathName = entryName.substring(0, lastSep + 1);
                docenv.getPackageDoc(pack).setDocPath(zipName, classPathName);
            }
            else {
                File fileDir = new File(com.sun.tools.mjavac.util.Old199.getPath(fo)).getParentFile();
                docenv.getPackageDoc(pack).setDocPath(fileDir.getAbsolutePath());
            }
        }
    }
}
