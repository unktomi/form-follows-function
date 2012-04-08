/*
 * Copyright 2001-2002 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
package org.f3.tools.debug.tty;

import java.io.PrintStream;
import java.util.*;
import java.text.MessageFormat;
/**
 * Internationalization (i18n) convenience methods for jdb.
 *
 * All program output should flow through these methods, and this is
 * the only class that should be printing directly or otherwise
 * accessing System.[out,err].
 *
 * @bug 4348376
 * @author Tim Bell
 */
public class MessageOutput {
    /**
     * The resource bundle containing localizable message content.
     */
    private static ResourceBundle textResources;
    static {
        textResources = ResourceBundle.getBundle
            ("org.f3.tools.debug.tty.TTYResources",
             Locale.getDefault());
    }

    /** Our message formatter.  Allocated once, used many times */
    private static MessageFormat messageFormat;

    private PrintStream msErr = System.err;
    private PrintStream msOut = System.out;

    void resetOutputs() {
        msErr = System.err;
        msOut = System.out;
    }

    void setOutput(PrintStream out) {
        if (out != null) {
            msOut = out;
        }
    }

    void setError(PrintStream err) {
        if (err != null) {
            msErr = err;
        }
    }

    /**
     * Fatal error notification.  This is sent to msErr
     * instead of msOut
     */
    void fatalError(String messageKey) {
        msErr.println();
        msErr.println(format("Fatal error"));
        msErr.println(format(messageKey));
    }

    /**
     * "Format" a string by doing a simple key lookup.
     */
    static String format(String key) {
        return (textResources.getString(key));
    }

    /**
     * Fetch and format a message with one string argument.
     * This is the most common usage.
     */
    static String format(String key, String argument) {
        return format(key, new Object [] {argument});
    }

    /**
     * Fetch a string by key lookup and format in the arguments.
     */
    static synchronized String format(String key, Object [] arguments) {
        if (messageFormat == null) {
            messageFormat = new MessageFormat (textResources.getString(key));
        } else {
            messageFormat.applyPattern (textResources.getString(key));
        }
        return (messageFormat.format (arguments));
    }

    /**
     * Print directly to msOut.
     * Every rule has a few exceptions.
     * The exceptions to "must use the MessageOutput formatters" are:
     *     VMConnection.dumpStream()
     *     TTY.monitorCommand()
     *     TTY.TTY() (for the '!!' command only)
     *     Commands.java (multiple locations)
     * These are the only sites that should be calling this
     * method.
     */
    void printDirectln(String line) {
        msOut.println(line);
        msOut.flush();
    }
    void printDirect(String line) {
        msOut.print(line);
    }
    void printDirect(char c) {
        msOut.print(c);
    }

    /**
     * Print a newline.
     * Use this instead of '\n'
     */
    void println() {
        msOut.println();
        msOut.flush();
    }

    /**
     * Format and print a simple string.
     */
    void print(String key) {
        msOut.print(format(key));
    }
    /**
     * Format and print a simple string.
     */
    void println(String key) {
        msOut.println(format(key));
        msOut.flush();
    }


    /**
     * Fetch, format and print a message with one string argument.
     * This is the most common usage.
     */
    void print(String key, String argument) {
        msOut.print(format(key, argument));
    }
    void println(String key, String argument) {
        msOut.println(format(key, argument));
        msOut.flush();
    }

    /**
     * Fetch, format and print a message with an arbitrary
     * number of message arguments.
     */
    void println(String key, Object [] arguments) {
        msOut.println(format(key, arguments));
        msOut.flush();
    }

    /**
     * Print a newline, followed by the string.
     */
    void lnprint(String key) {
        msOut.println();
        msOut.print(textResources.getString(key));
    }

    void lnprint(String key, String argument) {
        msOut.println();
        msOut.print(format(key, argument));
        msOut.flush();
    }

    void lnprint(String key, Object [] arguments) {
        msOut.println();
        msOut.print(format(key, arguments));
        msOut.flush();
    }

    /**
     * Print an exception message with a stack trace.
     */
    void printException(String key, Exception e) {
        if (key != null) {
            try {
                println(key);
            } catch (MissingResourceException mex) {
                printDirectln(key);
            }
        }
        msOut.flush();
        e.printStackTrace();
    }

    void printPrompt(ThreadInfo threadInfo) {
        if (threadInfo == null) {
            msOut.print
                (format("jdb prompt with no current thread"));
        } else {
            msOut.print
                (format("jdb prompt thread name and current stack frame",
                                      new Object [] {
                                          threadInfo.getThread().name(),
                                          new Integer (threadInfo.getCurrentFrameIndex() + 1)}));
        }
        msOut.flush();
    }
}
