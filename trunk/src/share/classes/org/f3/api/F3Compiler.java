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

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.OptionChecker;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;

/**
 * The interface to invoke F3 compilers from programs, used with 
 * ServiceLoader lookup.  This interface is based on the JavaCompiler interface;
 * see its documentation for detailed descriptions.
 * @see javax.tools.JavaCompiler
 * 
 * @author tball
 */
public interface F3Compiler extends Tool, OptionChecker {

    /**
     * Creates a future for a compilation task with the given
     * components and arguments.  The compilation might not have
     * completed as described in the CompilationTask interface.
     *
     * <p>If a file manager is provided, it must be able to handle all
     * locations defined in {@link javax.tools.StandardLocation}.
     *
     * @param out a Writer for additional output from the compiler;
     * use {@code System.err} if {@code null}
     * @param fileManager a file manager; if {@code null} use the
     * compiler's standard filemanager
     * @param diagnosticListener a diagnostic listener; if {@code
     * null} use the compiler's default method for reporting
     * diagnostics
     * @param options compiler options, {@code null} means no options
     * @param compilationUnits the compilation units to compile, {@code
     * null} means no compilation units
     * @return an object representing the compilation
     * @throws RuntimeException if an unrecoverable error
     * occurred in a user supplied component.  The
     * {@linkplain Throwable#getCause() cause} will be the error in
     * user code.
     * @throws IllegalArgumentException if any of the given
     * compilation units are of other kind than
     * {@linkplain javax.tools.JavaFileObject.Kind#SOURCE source}
     */
    F3cTask getTask(Writer out,
                        JavaFileManager fileManager,
                        DiagnosticListener<? super JavaFileObject> diagnosticListener,
                        Iterable<String> options,
                        Iterable<? extends JavaFileObject> compilationUnits);

    /**
     * Gets a new instance of the standard file manager implementation
     * for this tool.  The file manager will use the given diagnostic
     * listener for producing any non-fatal diagnostics.  Fatal errors
     * will be signalled with the appropriate exceptions.
     *
     * <p>The standard file manager will be automatically reopened if
     * it is accessed after calls to {@code flush} or {@code close}.
     * The standard file manager must be usable with other tools.
     *
     * @param diagnosticListener a diagnostic listener for non-fatal
     * diagnostics; if {@code null} use the compiler's default method
     * for reporting diagnostics
     * @param locale the locale to apply when formatting diagnostics;
     * {@code null} means the {@linkplain Locale#getDefault() default locale}.
     * @param charset the character set used for decoding bytes; if
     * {@code null} use the platform default
     * @return the standard file manager
     */
    StandardJavaFileManager getStandardFileManager(
        DiagnosticListener<? super JavaFileObject> diagnosticListener,
        Locale locale,
        Charset charset);

    /**
     * Interface representing a future for a compilation task.  The
     * compilation task has not yet started.  To start the task, call
     * the {@linkplain #call call} method.
     */
    public interface CompilationTask extends Callable<Boolean> {

        /**
         * Performs this compilation task.  The compilation may only
         * be performed once.  Subsequent calls to this method throw
         * IllegalStateException.
         *
         * @return true if and only all the files compiled without errors;
         * false otherwise
         *
         * @throws RuntimeException if an unrecoverable error occurred
         * in a user-supplied component.  The
         * {@linkplain Throwable#getCause() cause} will be the error
         * in user code.
         * @throws IllegalStateException if called more than once
         */
        //@Override
        Boolean call();
    }
}
