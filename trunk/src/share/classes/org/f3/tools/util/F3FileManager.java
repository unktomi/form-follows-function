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

package org.f3.tools.util;

import com.sun.tools.mjavac.util.BaseFileObject;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JavacFileManager;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class F3FileManager extends JavacFileManager {
    
    /**
     * The F3 source file extension.
     * @see javax.tools.JavaFileObject.Kind.SOURCE
     */
    public static final String F3_SOURCE_SUFFIX = ".f3";

    /**
     * Register a Context.Factory to create a F3FileManager.
     */
    public static void preRegister(final Context context) {
        context.put(JavaFileManager.class, new Context.Factory<JavaFileManager>() {
            public JavaFileManager make() {
                return new F3FileManager(context, true, null);
            }
        });
    }

    public F3FileManager(Context context, boolean register, Charset charset) {
        super(context, register, charset);  
    }

    @Override
    protected JavaFileObject.Kind getKind(String extension) {
        if (extension.equals(JavaFileObject.Kind.CLASS.extension))
            return JavaFileObject.Kind.CLASS;
        else if (extension.equals(F3_SOURCE_SUFFIX))
            return JavaFileObject.Kind.SOURCE;
        else if (extension.equals(JavaFileObject.Kind.HTML.extension))
            return JavaFileObject.Kind.HTML;
        else
            return JavaFileObject.Kind.OTHER;
    }

    @Override
    public JavaFileObject getRegularFile(File file) {
        return new DelegateJavaFileObject(super.getRegularFile(file));
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(
        Iterable<? extends File> files)
    {
        Iterable<? extends JavaFileObject> objs = super.getJavaFileObjectsFromFiles(files);
        ArrayList<DelegateJavaFileObject> result = new ArrayList<DelegateJavaFileObject>();
        for (JavaFileObject jfo : objs)
            result.add(new DelegateJavaFileObject(jfo));
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location,
                                              String className,
                                              JavaFileObject.Kind kind)
        throws IOException
    {
        nullCheck(location);
        // validateClassName(className);
        nullCheck(className);
        nullCheck(kind);
        if (!sourceOrClass.contains(kind))
            throw new IllegalArgumentException("Invalid kind " + kind);

        String name = externalizeFileName(className, kind);
        Iterable<? extends File> path = getLocation(location);
        if (path == null)
            return null;

        for (File dir: path) {
            if (dir.isDirectory()) {
                File f = new File(dir, name.replace('/', File.separatorChar));
                if (f.exists())
                    return new DelegateJavaFileObject(getRegularFile(f));
            } else {
                Archive a = openArchive(dir);
                if (a.contains(name)) {
                    int i = name.lastIndexOf('/');
                    String dirname = name.substring(0, i+1);
                    String basename = name.substring(i+1);
                    return new DelegateJavaFileObject(a.getFileObject(dirname, basename));
                }

            }
        }
        return null;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location,
                                         String packageName,
                                         Set<JavaFileObject.Kind> kinds,
                                         boolean recurse)
        throws IOException
    {
        if (!kinds.contains(JavaFileObject.Kind.SOURCE))
            return super.list(location, packageName, kinds, recurse);

        nullCheck(packageName);
        nullCheck(kinds);

        Iterable<? extends File> path = getLocation(location);
        if (path == null)
            return List.nil();
        String subdirectory = packageName.replace('.', File.separatorChar);
        ListBuffer<JavaFileObject> results = new ListBuffer<JavaFileObject>();

        for (File directory : path)
            listDirectory(directory, subdirectory, kinds, recurse, results);

        return results.toList();
    }

    private static <T> T nullCheck(T o) {
        o.getClass(); // null check
        return o;
    }

    private static String externalizeFileName(CharSequence name, JavaFileObject.Kind kind) {
        String basename = name.toString().replace('.', File.separatorChar);
        String suffix = kind == JavaFileObject.Kind.SOURCE ? 
            F3_SOURCE_SUFFIX : kind.extension;
        return basename + suffix;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
                                               String className,
                                               JavaFileObject.Kind kind,
                                               FileObject sibling)
        throws IOException {
        if (sibling != null && sibling instanceof DelegateJavaFileObject) {
            sibling = ((DelegateJavaFileObject)sibling).delegate;
        }
        return super.getJavaFileForOutput(location, className, kind, sibling);        
    }

    /**
     * Insert all files in subdirectory `subdirectory' of `directory' which end
     * in one of the extensions in `extensions' into packageSym.
     */
    private void listDirectory(File directory,
                               String subdirectory,
                               Set<JavaFileObject.Kind> fileKinds,
                               boolean recurse,
                               ListBuffer<JavaFileObject> l) {

        boolean isFile = directory.isFile();

        if (isFile) {
            Archive archive = null;
            try {
                archive = openArchive(directory);
            } catch (IOException ex) {
                log.error("error.reading.file",
                   directory, ex.getLocalizedMessage());
                return;
            }
            if (subdirectory.length() != 0) {
                subdirectory = subdirectory.replace('\\', '/');
                if (!subdirectory.endsWith("/")) 
                    subdirectory = subdirectory + "/";
            }
            else {
                if (File.separatorChar == '/') {
                    subdirectory = subdirectory.replace('\\', '/');
                }
                else {
                    subdirectory = subdirectory.replace('/', '\\');
                }

                if (!subdirectory.endsWith(File.separator)) 
                    subdirectory = subdirectory + File.separator;
            }

            List<String> files = archive.getFiles(subdirectory);
            if (files != null) {
                for (String file; !files.isEmpty(); files = files.tail) {
                    file = files.head;
                    if (isValidFile(file, fileKinds)) {
                        l.append(archive.getFileObject(subdirectory, file));
                    }
                }
            }
            if (recurse) {
                for (String s: archive.getSubdirectories()) {
                    if (s.startsWith(subdirectory) && !s.equals(subdirectory)) {
                        // Because the archive map is a flat list of directories,
                        // the enclosing loop will pick up all child subdirectories.
                        // Therefore, there is no need to recurse deeper.
                        listDirectory(directory, s, fileKinds, false, l);
                    }
                }
            }
        } else {
            File d = subdirectory.length() != 0
                ? new File(directory, subdirectory)
                : directory;
            if (!caseMapCheck(d, subdirectory))
                return;

            File[] files = d.listFiles();
            if (files == null)
                return;

            for (File f: files) {
                String fname = f.getName();
                if (f.isDirectory()) {
                    if (recurse && SourceVersion.isIdentifier(fname)) {
                        listDirectory(directory,
                                      subdirectory + File.separator + fname,
                                      fileKinds,
                                      recurse,
                                      l);
                    }
                } else {
                    if (isValidFile(fname, fileKinds)) {
                        JavaFileObject fe =
                        new DelegateJavaFileObject(super.getRegularFile(new File(d, fname)));
                        l.append(fe);
                    }
                }
            }
        }
    }

    private boolean isValidFile(String s, Set<JavaFileObject.Kind> fileKinds) {
        int lastDot = s.lastIndexOf(".");
        String extn = (lastDot == -1 ? s : s.substring(lastDot));
        JavaFileObject.Kind kind = getKind(extn);
        return fileKinds.contains(kind);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        file.getClass(); // null check
        location.getClass(); // null check
        // Need to match the path semantics of list(location, ...)
        Iterable<? extends File> path = getLocation(location);
        if (path == null) {
            return null;
        }

        if (file instanceof DelegateJavaFileObject) {
            DelegateJavaFileObject r = (DelegateJavaFileObject) file;
            String rPath = r.getPath();
            for (File dir: path) {
                String dPath = dir.getPath();
                if (!dPath.endsWith(File.separator))
                    dPath += File.separator;
                if (rPath.regionMatches(true, 0, dPath, 0, dPath.length())
                    && new File(rPath.substring(0, dPath.length())).equals(new File(dPath))) {
                    String relativeName = rPath.substring(dPath.length());
                    return removeExtension(relativeName).replace(File.separatorChar, '.');
                }
            }
        }
        return super.inferBinaryName(location, file);
    }

    private static String removeExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1 ? fileName : fileName.substring(0, lastDot));
    }
        
    private static final boolean fileSystemIsCaseSensitive =
        File.separatorChar == '/';

    /** Hack to make Windows case sensitive. Test whether given path
     *  ends in a string of characters with the same case as given name.
     *  Ignore file separators in both path and name.
     */
    private boolean caseMapCheck(File f, String name) {
        if (fileSystemIsCaseSensitive) return true;
        // Note that getCanonicalPath() returns the case-sensitive
        // spelled file name.
        String path;
        try {
            path = f.getCanonicalPath();
        } catch (IOException ex) {
            return false;
        }
        char[] pcs = path.toCharArray();
        char[] ncs = name.toCharArray();
        int i = pcs.length - 1;
        int j = ncs.length - 1;
        while (i >= 0 && j >= 0) {
            while (i >= 0 && pcs[i] == File.separatorChar) i--;
            while (j >= 0 && ncs[j] == File.separatorChar) j--;
            if (i >= 0 && j >= 0) {
                if (pcs[i] != ncs[j]) return false;
                i--;
                j--;
            }
        }
        return j < 0;
    }

    private final Set<JavaFileObject.Kind> sourceOrClass =
        EnumSet.of(JavaFileObject.Kind.SOURCE, JavaFileObject.Kind.CLASS);
    
    private static class DelegateJavaFileObject extends BaseFileObject {
        JavaFileObject delegate;
        boolean isF3SourceFile;
        
        DelegateJavaFileObject(JavaFileObject jfo) {
            delegate = jfo;
            isF3SourceFile = jfo.getName().endsWith(F3_SOURCE_SUFFIX);
        }

        @Override
        public Kind getKind() {
            return isF3SourceFile ? JavaFileObject.Kind.SOURCE : delegate.getKind();
        }

        //@Override
        public boolean isNameCompatible(String cn, Kind kind) {
            cn.getClass(); // null check
            if (kind == Kind.OTHER && getKind() != kind)
                return false;
            String suffix = (kind == JavaFileObject.Kind.SOURCE) ? ".f3" : kind.extension;
            String n = cn + suffix;
            if (delegate.getName().equals(n))
                return true;
            if (getName().equalsIgnoreCase(n)) {
                try {
                    // allow for Windows
                    File f = new File(getPath());
                    return (f.getCanonicalFile().getName().equals(n));
                } catch (IOException e) {
                }
            }
            return false;
        }

        @Override
        public NestingKind getNestingKind() {
            return delegate.getNestingKind();
        }

        @Override
        public Modifier getAccessLevel() {
            return delegate.getAccessLevel();
        }

        public URI toUri() {
            return delegate.toUri();
        }

        public String getName() {
            return delegate.getName();
        }

        /** @deprecated see bug 6410637 */
        @Deprecated @Override
        public String getPath() {
            return delegate instanceof BaseFileObject ? 
                ((BaseFileObject)delegate).getPath() : getName();
        }

        public InputStream openInputStream() throws IOException {
            return delegate.openInputStream();
        }

        public OutputStream openOutputStream() throws IOException {
            return delegate.openOutputStream();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return delegate.openReader(ignoreEncodingErrors);
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return delegate.getCharContent(ignoreEncodingErrors);
        }

        public Writer openWriter() throws IOException {
            return delegate.openWriter();
        }

        public long getLastModified() {
            return delegate.getLastModified();
        }

        public boolean delete() {
            return delegate.delete();
        }
        
        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}
