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

package org.f3.tools.script;

import org.f3.tools.util.F3FileManager;
import java.io.*;
import java.nio.CharBuffer;
import java.util.*;
import java.util.Set;
import javax.lang.model.element.NestingKind;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.net.URL;
import java.net.URI;
import java.nio.charset.CharsetDecoder;
import javax.lang.model.element.Modifier;

/**
 * JavaFileManager that keeps compiled .class bytes in memory.
 *
 * @author A. Sundararajan
 */
public final class MemoryFileManager extends ForwardingJavaFileManager {                 
    private ClassLoader parentClassLoader;

    // A map in which the key is package name and the value is list of
    // classes in that package.
    Map<String, List<String>> packageMap;

    Map<String,ClassOutputBuffer> emittedClasses;

    /** F3 source file extension. */
    private final static String EXT = ".f3";

    public MemoryFileManager(JavaFileManager fileManager, ClassLoader cl,
            Map<String, List<String>> pkgMap,
            Map<String, ClassOutputBuffer> clbuffers) {
        super(fileManager);
        parentClassLoader = cl;
        packageMap = pkgMap;
        this.emittedClasses = clbuffers;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }

    /**
     * A file object used to represent a Java class coming from the parent class loader.
     * Handles the case of .class file as well as the .class jar entry. We can not
     * extend SimpleJavaFileObject here because that class makes certain assumptions
     * about the URI (works only for file system .class files and not for jar:file:.. URIs).
     */
    private static class ClassResource implements JavaFileObject {
        private URL url;
        private String binaryName;
        static URI toURI(URL u) {
	    try {
		return u.toURI();
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}
        
        ClassResource(URL url, String binaryName) {
            this.url = url;
            this.binaryName = binaryName;
        }

        //@Override
        public Kind getKind() {
            return Kind.CLASS;
        }
        
        public String getBinaryName() {
            return binaryName;
	}
        
        //@Override
        public String getName() {
            return getBinaryName();
        }
        
        //@Override
        public boolean isNameCompatible(String simpleName,
                         JavaFileObject.Kind kind) {
            return (kind == Kind.CLASS) && 
                    url.toString().endsWith("/" + simpleName + ".class");    
        }
        
        //@Override
        public boolean delete() {
            return false;
        }
        
        //@Override
        public long getLastModified() {
            return 0L;
        }
        
        //@Override
        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException("openWriter");
        }
        
        //@Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException("getCharContent");
        }
        
        //@Override
        public OutputStream openOutputStream() throws IOException {
            throw new UnsupportedOperationException("openOutputStream");
        }
        
        //@Override
        public InputStream openInputStream() throws IOException {
            return url.openStream();
        }
        
        //@Override
        public URI toUri() {
            return toURI(url);
        }
        
        //@Override
        public NestingKind getNestingKind() { 
            return null; 
        }

        //@Override
        public Modifier getAccessLevel() { 
            return null; 
        }

        //@Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream(), getDecoder(ignoreEncodingErrors));
        }

        protected CharsetDecoder getDecoder(boolean ignoreEncodingErrors) {
            throw new UnsupportedOperationException("getDecoder");
        }
    }

    /**
     * A file object used to represent Java source coming from a string.
     */
    private static class StringInputBuffer extends SimpleJavaFileObject {
        final String code;
        final boolean isF3SourceFile;
	String binaryName;
        
	public String getBinaryName() {
	    return binaryName.equals("__F3_SCRIPT__.f3") ? "__F3_SCRIPT__" : binaryName;
	}

        StringInputBuffer(String name, String code) {
            super(toURI(name), Kind.SOURCE);
            this.code = code;
	    binaryName = name;
            isF3SourceFile = name.endsWith(F3FileManager.F3_SOURCE_SUFFIX);
        }
        
        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }

        public Reader openReader() {
            return new StringReader(code);
        }

        @Override
        public Kind getKind() {
            //return isF3SourceFile ? JavaFileObject.Kind.SOURCE : super.getKind();
	    return JavaFileObject.Kind.SOURCE;
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public NestingKind getNestingKind() {
            return super.getNestingKind();
        }

        @Override
        public boolean isNameCompatible(String simpleName, Kind kind) {
            return super.isNameCompatible(simpleName, kind);
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return super.openInputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return super.openOutputStream();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return super.openReader(ignoreEncodingErrors);
        }

        @Override
        public Writer openWriter() throws IOException {
            return super.openWriter();
        }
    }

    /**
     * A file object that stores Java bytecode into the emittedClasses map.
     */
    public class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        ClassOutputBuffer(String name) { 
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

	public String getBinaryName() {
	    return name;
	}

        byte[] bytes;

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
                    bytes = bos.toByteArray();
                }
            };
        }
        @Override
        public InputStream openInputStream() throws IOException {
            if (bytes == null)
                throw new UnsupportedOperationException("openInputStream");
            return new ByteArrayInputStream(bytes);
        }
    }
    
    @Override
    public JavaFileObject getJavaFileForInput(JavaFileManager.Location location,
					      String className,
					      Kind kind) throws IOException {
        if (kind == Kind.CLASS) {
	    URL res = 
		parentClassLoader.getResource(className.replace('.', '/') + ".class");
            if (res != null) {
		return new ClassResource(res, className);
	    }
	}
	return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location,
                                    String className,
                                    Kind kind,
                                    FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            ClassOutputBuffer buf = new ClassOutputBuffer(className);
	    emittedClasses.put(className, buf);
	    return buf;
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }
    
    
    @Override
    public Iterable list(JavaFileManager.Location location,
			 String packageName,
			 Set kinds,
			 boolean recurse)
        throws IOException
    {
        List results = new LinkedList();
        if (kinds.contains(Kind.CLASS)) {
            // From the list of .class entries of the given package,
            // construct JavaFileObjects for that package.
            if (packageMap.containsKey(packageName)) {
                for (String cl : packageMap.get(packageName)) {
                    String dir = packageName.replace('.', '/');
                    URL res = parentClassLoader.getResource(dir + "/" + cl + ".class");
                    // add a JavaFileObject only if the class loader can find
                    // resource URL for the given .class.
	            if (res != null) {
                        String binaryName = packageName + "." + cl;
                        results.add(new ClassResource(res, binaryName));
                    }
                }
            }
        }
	Iterable result = super.list(location, packageName, kinds, recurse);
	for (Object o : result) {
	    results.add(o);
	}
	String prefix = packageName.equals("") ? "" : packageName + ".";
        for (ClassOutputBuffer b : emittedClasses.values()) {
	    String name = b.getName().replace("/", ".");
            name = name.substring(1, name.length() - (name.endsWith(EXT) ? EXT.length() : 0));
            if (prefix.length() == 0) {
		if (!name.contains(".")) {
		    results.add(b);
		}
	    } else {
		if (name.startsWith(prefix)) {
		    name = name.substring(prefix.length());
		    if (!name.contains(".")) {
			results.add(b);
		    }
		}
	    }
        }
	return results;
    }
    
    JavaFileObject makeStringSource(String name, String code) {
	return new StringInputBuffer(name, code);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
	if (file instanceof StringInputBuffer) {
	    return ((StringInputBuffer)file).getBinaryName();
	} else if (file instanceof ClassOutputBuffer) {
	    return ((ClassOutputBuffer)file).getBinaryName();
	} else if (file instanceof ClassResource) {
            return ((ClassResource)file).getBinaryName();
        }
	return super.inferBinaryName(location, file);
    }

    static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file.toURI();
        } else {
            try {
                final StringBuilder newUri = new StringBuilder();
                newUri.append("mfm:///");
                newUri.append(name.replace('.', '/'));
                if(name.endsWith(EXT)) newUri.replace(newUri.length() - EXT.length(), newUri.length(), EXT);
                return URI.create(newUri.toString());
            } catch (Exception exp) {
                return URI.create("mfm:///org/f3/tools/script/f3_source");
            }
        }
    }
}
