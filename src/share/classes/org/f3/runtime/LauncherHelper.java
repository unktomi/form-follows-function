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
package org.f3.runtime;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A singleton helper class for the f3 launcher
 * @author ksrini
 */
public enum LauncherHelper {

    INSTANCE;
    private static final String myname = "f3";
    private static final String defaultBundleName =
            "org.f3.runtime.resources.launcher";
    private static ResourceBundle f3rb = null;

    private static void printVersion(boolean fullversion) {
        StringBuilder sb = new StringBuilder(myname + " ");
        if (fullversion) {
            sb = sb.append("full version ");
            sb = sb.append("\"");
            sb = sb.append(SystemProperties.getProperty("f3.runtime.version"));
            sb = sb.append("\"");
        } else {
            sb = sb.append(SystemProperties.getProperty("f3.version"));
        }
        System.err.println(sb.toString());
        System.err.flush();
    }

    /**
     * A  helper method to get a localized message and also
     * apply any arguments that we might pass, though synchronized
     * is really not required, but it is a good practice EJ-Item 71.
     */
    synchronized static  String getLocalizedMessage(String key, Object... args) {
        if (f3rb == null) {
            f3rb = ResourceBundle.getBundle(defaultBundleName);
        }
        String msg = f3rb.getString(key);
        return (args != null) ? MessageFormat.format(msg, args) : msg;
    }

    static StringBuilder getHelpMessage() {
        StringBuilder outBuf = new StringBuilder();
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.header",
                myname));
        outBuf = outBuf.append("\n");
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.datamodel",
                32, getLocalizedMessage("f3.launcher.ifavailable")));
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.datamodel",
                64, getLocalizedMessage("f3.launcher.ifavailable")));
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.vmselect",
                "-client", "client", getLocalizedMessage("f3.launcher.ifavailable")));
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.vmselect",
                "-server", "server", getLocalizedMessage("f3.launcher.ifavailable")));
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.opt.footer",
                File.pathSeparator));
        return outBuf;
    }
    
    private static void printHelpMessage() {
        System.err.println(getHelpMessage().toString());
        System.err.flush();
    }

    static void printHelpMessageX() {
        StringBuilder outBuf = new StringBuilder();
        outBuf = outBuf.append(getLocalizedMessage("f3.launcher.X.usage",
                File.pathSeparator));
        System.err.println(outBuf.toString());
        System.err.flush();
    }

    public static void main(String... args) {
        if (args.length > 0) {
            if (args[0].equals("-fullversion")) {
                printVersion(true);
            } else if (args[0].equals("-version")) {
                printVersion(false);
            } else if (args[0].endsWith("-helpx")) {
                printHelpMessageX();
            } else {
                printHelpMessage();
            }
        }
    }
}
