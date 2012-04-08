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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ClassLoader that loads .class bytes from memory.
 *
 * @author A. Sundararajan
 */
public final class MemoryClassLoader extends ClassLoader {
    Map<String,MemoryFileManager.ClassOutputBuffer> clbuffers;

    public MemoryClassLoader(Map<String,MemoryFileManager.ClassOutputBuffer> clbuffers,
               ClassLoader parent) {
        super(parent);
        this.clbuffers = clbuffers;
    }

    public Class load(String className) throws ClassNotFoundException {
        return loadClass(className);
    }

    @Override
    protected Class findClass(String className) throws ClassNotFoundException {
        MemoryFileManager.ClassOutputBuffer clbuffer = clbuffers.get(className);
        if (clbuffer != null && clbuffer.bytes != null) {
            byte[] buf = clbuffer.bytes;
            return defineClass(className, buf, 0, buf.length);
        } else {
            return super.findClass(className);
        }
    }

    @Override
    public URL findResource(String name) {
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - 6).replace('/', '.');
            MemoryFileManager.ClassOutputBuffer clbuf = clbuffers.get(name);
            if (clbuf != null) {
                try {
                    return clbuf.toUri().toURL();
                } catch (MalformedURLException ex) {
                    // fall through
                }
            }
        }
        return super.findResource(name);
    }
}
