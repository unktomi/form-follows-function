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

import com.sun.tools.mjavac.comp.AttrContext;
import com.sun.tools.mjavac.comp.Env;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Options;
import org.f3.tools.main.F3Compiler;
import org.f3.tools.main.Main;
import org.f3.tools.tree.JavaPretty;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Certain errors from the back-end are internal errors; Indicate this.
 *
 * @author Robert Field
 */
public class F3BackendLog extends Log {
    private String crashFileName;
    private PrintWriter crashFileWriter;
    final Context context;
    final Context f3Context;
    public Env<AttrContext> env;
    private boolean dumpOccurred;

    protected F3BackendLog(Context context, final Context f3Context) {
        super(context);
        this.context = context;
        this.f3Context = f3Context;
        this.dumpOccurred = false; // Only once
        
        /* This is a writer for writing the javadump to a file instead of to the default output.
         * We init it to the default output, and then if we actually have to write a dump,
         * we create the file and the PrintWriter for it at that time.  This avoids creating
         * a file we don't need.
         */
        crashFileWriter = getWriterForDiagnosticType(JCDiagnostic.DiagnosticType.ERROR);
    }

    public static void preRegister(final Context context, final Context f3Context) {
        context.put(logKey, new Context.Factory<Log>() {

            public Log make() {
                return new F3BackendLog(context, f3Context);
            }
        });
    }

    private void createCrashFile() {
        if (crashFileName == null) {
            // we haven't created the crashFileWriter
            try {
                // Create temp file writer if we can.
                File crashFile = File.createTempFile("f3_err_", ".txt");
                crashFileWriter = new PrintWriter(crashFile);
                crashFileName = crashFile.getCanonicalPath();
            } catch (Exception e) {
                // Otherwise, we just have to use the default output.
            }
        }
    }

    private void writeToCrashFile(String extra) {
        Log f3Log = Log.instance(f3Context);
        if (crashFileName == null) {
            f3Log.note(MsgSym.MESSAGE_F3_NOTE_INTERNAL_ERROR2);
        } else {
            f3Log.note(MsgSym.MESSAGE_F3_NOTE_INTERNAL_ERROR, crashFileName);
        }
        try {
            Log.printLines(crashFileWriter, Main.getF3LocalizedString(
                                                                      "compiler.note." + MsgSym.MESSAGE_F3_NOTE_INTERNAL_ERROR1,
                                                                      F3Compiler.fullVersion(), 
                                                                      System.getProperty("java.vm.version"),
                                                                      System.getProperty("java.runtime.version"),
                                                                      System.getProperty("os.name"),
                                                                      System.getProperty("os.arch"),
                                                                      extra));
        } catch (Throwable t) {
            System.err.println(extra);
            t.printStackTrace();
        } finally {
            crashFileWriter.flush();
        }
    }

    private void errorPreface() {
        if (!dumpOccurred && env != null) {  // Only add prefix where wanted
            JCTree tree = null;
            if (env.tree != null) {
                tree = env.tree;
            } else if (env.enclMethod != null) {
                tree = env.enclMethod;
            }

            StringWriter sw = new StringWriter();
            if (tree != null) {
                Options options = Options.instance(context);
                String dumpOnFail = options.get("DumpOnFail");
                if (!dumpOccurred && (dumpOnFail == null || !dumpOnFail.toLowerCase().startsWith("n"))) {
                    try {
                        try {
                            new JavaPretty(sw, false, f3Context).printExpr(tree);
                        } finally {
                            sw.close();
                        }
                    } catch (Throwable ex) {
                    }
                    dumpOccurred = true;
                }
            }
            writeToCrashFile(sw.toString());
        }
    }

    @Override
    protected void writeDiagnostic(JCDiagnostic diagnostic) {
        // See vsgc-507.  Don't output NOTEs; let javac do it.  
        if (diagnostic.getType() == JCDiagnostic.DiagnosticType.ERROR) {
            createCrashFile();
            // write the dump if there is one 
            errorPreface();

            // Write the error msg to the file.  Unfortunately, the source line
            // doesn't get written.
            if (crashFileName != null) {
                Log.printLines(crashFileWriter, diagnostic.toString());
                crashFileWriter.flush();
            }

            // Write the error to stdout using the standard javac logger
            Log.instance(f3Context).report(diagnostic);
            nerrors++;
        }
    }
    /*
     * In the future, we might want to consider doing something like this and call it
     * from the Main.java error routines to get stack traces written into the crash file 
     * instead of stdout.
     */
    public void printStackTrace(Throwable ex) {
        createCrashFile();
        writeToCrashFile(null);
        ex.printStackTrace(crashFileWriter);
        crashFileWriter.flush();
    }
}
