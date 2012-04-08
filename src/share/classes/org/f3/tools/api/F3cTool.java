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

package org.f3.tools.api;

import org.f3.api.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.*;

import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JavacFileManager;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Options;
import org.f3.tools.main.F3Option;
import org.f3.tools.main.Main;
import org.f3.tools.main.RecognizedOptions.GrumpyHelper;
import org.f3.tools.main.RecognizedOptions;
import org.f3.tools.util.F3FileManager;
import org.f3.tools.util.MsgSym;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;

/**
 * The Tool API implementation for the F3 compiler, based on the
 * javac compiler tool implementation.
 *
 * @author Tom Ball
 */
public final class F3cTool implements F3Compiler {
    private final Context dummyContext = new Context();

    private final PrintWriter silent = new PrintWriter(new OutputStream(){
        @Override
        public void write(int b) {}
    });

    private final Main sharedCompiler = new Main("f3c", silent);
    {
        sharedCompiler.setOptions(Options.instance(dummyContext));
    }

    /**
     * Static factory method for creating new instances of this tool.
     * @return new instance of this tool
     */
    public static F3cTool create() {
        return new F3cTool();
    }

    //@Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        if (err == null)
            err = System.err;
        for (String argument : arguments)
            argument.getClass(); // null check
        return org.f3.tools.Main.compile(arguments, new PrintWriter(err, true));
    }

    //@Override
    public Set<SourceVersion> getSourceVersions() {
        return Collections.unmodifiableSet(EnumSet.range(SourceVersion.RELEASE_3,
                                                         SourceVersion.latest()));
    }

    //@Override
    public JavacFileManager getStandardFileManager(
        DiagnosticListener<? super JavaFileObject> diagnosticListener,
        Locale locale,
        Charset charset) {
        Context context = new Context();
        if (diagnosticListener != null)
            context.put(DiagnosticListener.class, diagnosticListener);
        context.put(Log.outKey, new PrintWriter(System.err, true)); // FIXME
        return new F3FileManager(context, true, charset);
    }

    //@Override
    public F3cTask getTask(Writer out,
                             JavaFileManager fileManager,
                             DiagnosticListener<? super JavaFileObject> diagnosticListener,
                             Iterable<String> options,
                             Iterable<? extends JavaFileObject> compilationUnits) {
        return getTask(new Context(), out, fileManager, diagnosticListener, options, compilationUnits);
    }

   public F3cTaskImpl getTask(Context context,
                             Writer out,
                             JavaFileManager fileManager,
                             DiagnosticListener<? super JavaFileObject> diagnosticListener,
                             Iterable<String> options,
                             Iterable<? extends JavaFileObject> compilationUnits)
    
    {
        final String kindMsg = "All compilation units must be of SOURCE kind";
        if (options != null)
            for (String option : options)
                option.getClass(); // null check
        if (compilationUnits != null) {
            for (JavaFileObject cu : compilationUnits) {
                if (!cu.getKind().name().equals("SOURCE")) // implicit null check
                    throw new IllegalArgumentException(kindMsg);
            }
        }
        if (diagnosticListener != null)
            context.put(DiagnosticListener.class, diagnosticListener);

        PrintWriter cout = context.get(Log.outKey);
        if (cout == null) {
            if (out == null)
                cout = new PrintWriter(System.err, true);
            else
                cout = new PrintWriter(out, true);
            context.put(Log.outKey, cout);
        }

        if (fileManager == null)
            fileManager = getStandardFileManager(diagnosticListener, null, null);
        context.put(JavaFileManager.class, fileManager);
        processOptions(context, fileManager, options);
        Main compiler = new Main("javacTask", cout);
        return new F3cTaskImpl(this, compiler, options, context, compilationUnits);
    }

    private static void processOptions(Context context,
                                       JavaFileManager fileManager,
                                       Iterable<String> options)
    {
        if (options == null)
            return;

        Options optionTable = Options.instance(context);

        F3Option[] recognizedOptions =
            RecognizedOptions.getJavacToolOptions(new GrumpyHelper());
        Iterator<String> flags = options.iterator();
        while (flags.hasNext()) {
            String flag = flags.next();
            int j;
            for (j=0; j<recognizedOptions.length; j++)
                if (recognizedOptions[j].matches(flag))
                    break;

            if (j == recognizedOptions.length) {
                if (fileManager.handleOption(flag, flags)) {
                    continue;
                } else {
                    String msg = Main.getLocalizedString(MsgSym.MESSAGE_ERR_INVALID_FLAG, flag);
                    throw new IllegalArgumentException(msg);
                }
            }

            F3Option option = recognizedOptions[j];
            if (option.hasArg()) {
                if (!flags.hasNext()) {
                    String msg = Main.getLocalizedString(MsgSym.MESSAGE_ERR_REQ_ARG, flag);
                    throw new IllegalArgumentException(msg);
                }
                String operand = flags.next();
                if (option.process(optionTable, flag, operand))
                    // should not happen as the GrumpyHelper will throw exceptions
                    // in case of errors
                    throw new IllegalArgumentException(flag + " " + operand);
            } else {
                if (option.process(optionTable, flag))
                    // should not happen as the GrumpyHelper will throw exceptions
                    // in case of errors
                    throw new IllegalArgumentException(flag);
            }
        }
    }

    //@Override
    public int isSupportedOption(String option) {
        F3Option[] recognizedOptions =
            RecognizedOptions.getJavacToolOptions(new GrumpyHelper());
        for (F3Option o : recognizedOptions) {
            if (o.matches(option))
                return o.hasArg() ? 1 : 0;
        }
        return -1;
    }
}
