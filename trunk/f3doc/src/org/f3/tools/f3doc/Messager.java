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

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import com.sun.javadoc.*;

import com.sun.tools.mjavac.util.Context;

import com.sun.tools.mjavac.util.Log;  // Access to 'javac' output streams

/**
 * Utility for integrating with javadoc tools and for localization.
 * Handle Resources. Access to error and warning counts.
 * Message formatting.
 * <br>
 * Also provides implementation for DocErrorReporter.
 *
 * @see java.util.ResourceBundle
 * @see java.text.MessageFormat
 * @author Neal Gafter (rewrite)
 */
public class Messager extends Log implements DocErrorReporter {

    /** Get the current messager, which is also the compiler log. */
    public static Messager instance0(Context context) {
        Log instance = context.get(logKey);
        if (instance == null || !(instance instanceof Messager))
            throw new InternalError("no messager instance!");
        return (Messager)instance;
    }

    public static void preRegister(final Context context,
                                   final String programName) {
        context.put(logKey, new Context.Factory<Log>() {
            public Log make() {
                return new Messager(context,
                                    programName);
            }
        });
    }
    public static void preRegister(final Context context,
                                   final String programName,
                                   final PrintWriter errWriter,
                                   final PrintWriter warnWriter,
                                   final PrintWriter noticeWriter) {
        context.put(logKey, new Context.Factory<Log>() {
            public Log make() {
                return new Messager(context,
                                    programName,
                                    errWriter,
                                    warnWriter,
                                    noticeWriter);
            }
        });
    }

    public class ExitJavadoc extends Error {
        private static final long serialVersionUID = 0;
    }

    private final String programName;

    private ResourceBundle messageRB = null;

    /** The default writer for diagnostics
     */
    static final PrintWriter defaultErrWriter = new PrintWriter(System.err);
    static final PrintWriter defaultWarnWriter = new PrintWriter(System.err);
    static final PrintWriter defaultNoticeWriter = new PrintWriter(System.out);

    /**
     * Constructor
     * @param programName  Name of the program (for error messages).
     */
    protected Messager(Context context, String programName) {
        this(context, programName, defaultErrWriter, defaultWarnWriter, defaultNoticeWriter);
    }

    /**
     * Constructor
     * @param programName  Name of the program (for error messages).
     * @param errWriter    Stream for error messages
     * @param warnWriter   Stream for warnings
     * @param noticeWriter Stream for other messages
     */
    @SuppressWarnings("deprecation")
    protected Messager(Context context,
                       String programName,
                       PrintWriter errWriter,
                       PrintWriter warnWriter,
                       PrintWriter noticeWriter) {
        super(context, errWriter, warnWriter, noticeWriter);
        this.programName = programName;
    }

    /**
     * Reset resource bundle, eg. locale has changed.
     */
    public void reset() {
        messageRB = null;
    }

    /**
     * Get string from ResourceBundle, initialize ResourceBundle
     * if needed.
     */
    private String getString(String key) {
        ResourceBundle msgRB = this.messageRB;
        if (msgRB == null) {
            try {
                this.messageRB = msgRB =
                    ResourceBundle.getBundle(
                          "org.f3.tools.f3doc.resources.f3doc");
            } catch (MissingResourceException e) {
                throw new Error("Fatal: Resource for f3doc is missing");
            }
        }
        return msgRB.getString(key);
    }

    /**
     * get and format message string from resource
     *
     * @param key selects message from resource
     * @param args optional message arguments
     */
    String getText(String key, Object ... args) {
        try {
            String message = getString(key);
            return MessageFormat.format(message, args);
        } catch (MissingResourceException e) {
            return "********** Resource for f3doc is broken. There is no " +
                key + " key in resource.";
        }
    }

    /**
     * Print error message, increment error count.
     * Part of DocErrorReporter.
     *
     * @param msg message to print
     */
    public void printError(String msg) {
        printError(null, msg);
    }

    /**
     * Print error message, increment error count.
     * Part of DocErrorReporter.
     *
     * @param pos the position where the error occurs
     * @param msg message to print
     */
    public void printError(SourcePosition pos, String msg) {
        String prefix = (pos == null) ? programName : pos.toString();
        errWriter.println(prefix + ": " + getText("javadoc.error") + " - " + msg);
        errWriter.flush();
        prompt();
        nerrors++;
    }

    /**
     * Print warning message, increment warning count.
     * Part of DocErrorReporter.
     *
     * @param msg message to print
     */
    public void printWarning(String msg) {
        printWarning(null, msg);
    }

    /**
     * Print warning message, increment warning count.
     * Part of DocErrorReporter.
     *
     * @param pos the position where the error occurs
     * @param msg message to print
     */
    public void printWarning(SourcePosition pos, String msg) {
        String prefix = (pos == null) ? programName : pos.toString();
        warnWriter.println(prefix +  ": " + getText("javadoc.warning") +" - " + msg);
        warnWriter.flush();
        nwarnings++;
    }

    /**
     * Print a message.
     * Part of DocErrorReporter.
     *
     * @param msg message to print
     */
    public void printNotice(String msg) {
        printNotice(null, msg);
    }

    /**
     * Print a message.
     * Part of DocErrorReporter.
     *
     * @param pos the position where the error occurs
     * @param msg message to print
     */
    public void printNotice(SourcePosition pos, String msg) {
        if (pos == null)
            noticeWriter.println(msg);
        else
            noticeWriter.println(pos + ": " + msg);
        noticeWriter.flush();
    }

    @Override
    public void error(String key, Object... arg1) {
        printError(getText(key, arg1));
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     * @param args optional message arguments
     */
    public void error(SourcePosition pos, String key, Object ... args) {
        printError(pos, getText(key, args));
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     * @param args optional message arguments
     */
    public void warning(SourcePosition pos, String key, Object ... args) {
        printWarning(pos, getText(key, args));
    }

    @Override
    public void warning(String key, Object... args) {
        printWarning(getText(key, args));
    }

    /**
     * Print a message.
     *
     * @param key selects message from resource
     * @param args optional message arguments
     */
    public void notice(String key, Object ... args) {
        printNotice(getText(key, args));
    }

    /**
     * Return total number of errors, including those recorded
     * in the compilation log.
     */
    public int nerrors() { return nerrors; }

    /**
     * Return total number of warnings, including those recorded
     * in the compilation log.
     */
    public int nwarnings() { return nwarnings; }

    /**
     * Print exit message.
     */
    public void exitNotice() {
        int nerrs = nerrors();
        int nwarn = nwarnings();
        if (nerrs > 0) {
            notice((nerrs > 1) ? "main.errors" : "main.error",
                   "" + nerrs);
        }
        if (nwarn > 0) {
            notice((nwarn > 1) ?  "main.warnings" : "main.warning",
                   "" + nwarn);
        }
    }

    /**
     * Force program exit, e.g., from a fatal error.
     * <p>
     * TODO: This method does not really belong here.
     */
    public void exit() {
        throw new ExitJavadoc();
    }

}
