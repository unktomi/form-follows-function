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

package org.f3.api;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.util.logging.Logger;

/**
 * F3 version of javac's ToolProvider class.
 *
 * @author Tom Ball
 */
public class ToolProvider {
    private static Logger logger = Logger.getLogger("org.f3");

    private ToolProvider() {}

    /**
     * Gets a F3 compiler instance.
     * @return the compiler instance or {@code null} if no compiler
     *         is included as part of the application classpath
     */
    public static F3Compiler getF3Compiler() {
        try {
            URL[] urls = new URL[] {
                getPath("org.f3.tools.api.F3cTool"),
                getPath("com.sun.tools.mjavac.util.Context")
            };
            ClassLoader cl = createPrivilegedClassLoader(urls);
            Class<?> cls = Class.forName("org.f3.tools.api.F3cTool", false, cl);
            return (F3Compiler)cls.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Gets a F3 engine instance.  This is an alternative
     * to the general use where the script engine is looked up as a service.
     * @return the script engine instance or {@code null} if no script engine
     *         is included as part of the application classpath
     */
    public static F3ScriptEngine getF3ScriptEngine() {
        try {
            URL[] urls = new URL[] {
                getPath("org.f3.tools.script.F3ScriptEngineImpl"),
                getPath("com.sun.tools.mjavac.util.Context")
            };
            ClassLoader cl = createPrivilegedClassLoader(urls);
            Class<?> cls = Class.forName("org.f3.tools.script.F3ScriptEngineImpl", false, cl);
            return (F3ScriptEngine)cls.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static ClassLoader createPrivilegedClassLoader(final URL[] urls) throws PrivilegedActionException {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ClassLoader parent = F3Compiler.class.getClassLoader();
                return new URLClassLoader(urls, parent);
            }
        });
    }

    /**
     * Return the classpath element that contains the specified class.
     * @return the classpath element, usually the class's jar file
     * @throws java.net.MalformedURLException
     */
    private static URL getPath(String className) throws MalformedURLException {
        String classFile = className.replace('.', '/') + ".class";
        ClassLoader cl = ToolProvider.class.getClassLoader();
        if (cl == null)
            cl = ClassLoader.getSystemClassLoader();
        URL classURL = cl.getResource(classFile);
        String path = classURL.getPath();
        assert path.endsWith(classFile);
        path = path.substring(0, path.indexOf(classFile) - 1);
        if (path.endsWith("!")) {
            path = path.substring(0, path.length() - 1);
        }
        File f = new File(path);
        if (f.exists()) {
            return f.toURI().toURL();
        }
        return new URL(path);
    }
}
