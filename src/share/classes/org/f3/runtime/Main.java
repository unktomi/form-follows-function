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
package org.f3.runtime;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * The main method in this class calls the
 * Entry.start() to initialize F3 runtime
 * and invoke user's F3 class. This is to
 * avoid running static initializer of user's
 * class from the main thread. See: VSGC-1888.
 *
 * @author A. Sundararajan
 */
public class Main {
    private static final String MAIN_CLASS_PREFIX = "MainF3Class=";

    private static String getErrorMessage(String key, Object... args) {
        return LauncherHelper.getLocalizedMessage("f3.launcher.err.main." + key, args);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            errorExit(LauncherHelper.getHelpMessage().toString());
        }

        String[] argsToMain = new String[args.length - 1];
        if (argsToMain.length != 0) {
            System.arraycopy(args, 1, argsToMain, 0, argsToMain.length);
        }

        Class mainClass = null;
        String mclassname = args[0];
        try {
            if (mclassname.endsWith(".jar")) {
		// executable Jar case
                mclassname = getMainClass(mclassname);
            } else if (mclassname.startsWith(MAIN_CLASS_PREFIX)) {
		// webstart case
                mclassname = mclassname.substring(mclassname.indexOf('=')+1);
	    }
            // load the user's F3 class but do *not* initialize!
            mainClass = Class.forName(mclassname, false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException cnfe) {
            errorExit(getErrorMessage("notfound", mclassname), cnfe);
        } catch (IOException ioe) {
            errorExit(getErrorMessage("loadfailed", mclassname), ioe);
        }

        // if it is a F3 class, call Entry.start() 
        // else just execute "main" method.
        if (F3Object.class.isAssignableFrom(mainClass)) {
            try {
                Entry.start(mainClass, argsToMain);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        } else {
            Method mainMethod = null;
            try {
                mainMethod = mainClass.getMethod("main", String[].class);
            } catch (Exception exp) {
                errorExit(getErrorMessage("nomethod", mainClass));
            }
            try {
                mainMethod.invoke(null, (Object) argsToMain);
            } catch (Exception exp) {
                errorExit("", exp);
            }
        }
    }

    private static void errorExit(String msg) {
        errorExit(msg, null);
    }

    private static void errorExit(String msg, Exception exp) {
        System.err.println(msg);
        if (exp != null) {
            exp.printStackTrace();
        }
        System.exit(1);
    }

    static String getMainClass(String jarfilename) throws IOException {
        JarFile jf = new JarFile(jarfilename);
        try {
            Manifest mf = jf.getManifest();
            if (mf != null) {
                Attributes attr = mf.getMainAttributes();
                if (attr != null) {
                    String mainclassname = attr.getValue("Main-Class");
                    if (mainclassname != null) {
                        return mainclassname;
                    }
                }
            }
            String msg = getErrorMessage("notfound", getErrorMessage("reason", jarfilename));
            errorExit(msg, new IOException("Main-Class not found in " + jarfilename));
            return null;
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                } catch (IOException ex) { /* swallow the exception */ }
            }
        }
    }
}
