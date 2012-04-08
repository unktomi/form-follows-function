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

package org.f3.tools.main;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TaskEvent;
import org.f3.tools.tree.BlockExprJCBlockExpression;
import com.sun.tools.mjavac.comp.AttrContext;
import com.sun.tools.mjavac.comp.Env;
import com.sun.tools.mjavac.main.*;
import com.sun.tools.mjavac.tree.JCTree.JCClassDecl;
import com.sun.tools.mjavac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.mjavac.util.*;
import org.f3.tools.util.F3BackendLog;
import com.sun.tools.mjavac.parser.Parser;
import com.sun.tools.mjavac.parser.Scanner;
import com.sun.tools.mjavac.parser.Token;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCBlock;
import com.sun.tools.mjavac.tree.JCTree.JCExpression;
import com.sun.tools.mjavac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.mjavac.tree.JCTree.JCStatement;
import static com.sun.tools.mjavac.parser.Token.*;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

/**
 *
 * @author Robert
 */
public class F3JavaCompiler extends JavaCompiler {
    /** The context key for the compiler. */
    protected static final Context.Key<F3JavaCompiler> f3JavaCompilerKey =
        new Context.Key<F3JavaCompiler>();
    
    List<JCCompilationUnit> modules; // Current compilation

    final F3BackendLog beLog;

    /** Get the JavaCompiler instance for this context. */
    public static F3JavaCompiler instance(Context context) {
        F3JavaCompiler instance = context.get(f3JavaCompilerKey);
        if (instance == null)
            instance = new F3JavaCompiler(context);
        return instance;
    }

    protected F3JavaCompiler(Context context) {
        super(context);
        Log log = Log.instance(context);
        beLog = (log instanceof F3BackendLog)? (F3BackendLog)log : null;
    }

    @Override
    public void initProcessAnnotations(Iterable<? extends Processor> arg0) {
        // F3 doesn't support annotations
    }

    public void backEnd(List<JCCompilationUnit> externalModules, ListBuffer<JavaFileObject> results) throws IOException {
        modules = externalModules;
        this.results = results;
        compile(null, List.<String>nil(), null);
        this.results = null;
    }
    
    public Name.Table getNames() {
        return names;
    }
    
    public Env<AttrContext> attribute(Env<AttrContext> env) {
        try {
            if (beLog != null) {
                beLog.env = env;
            }
            super.attribute(env);
        } finally {
            if (beLog != null) {
                beLog.env = null;
            }
        }
        return env;
    }
    /**
     * Override of JavaCompiler.generate() to catch list of generated class files.
     * Do not call directly.
     */
    @Override
    public void generate(List<Pair<Env<AttrContext>, JCClassDecl>> list) {
        generate(list, results);
    }
    ListBuffer<JavaFileObject> results = null;

    @Override
    public List<JCCompilationUnit> parseFiles(List<JavaFileObject> fileObjects) throws IOException {
        if (modules != null) {
            return modules;
        } else {
            return super.parseFiles(fileObjects);
        }
    }

    @Override
    protected JCCompilationUnit parse(JavaFileObject filename, CharSequence content) {
        // The following code is cut-pasted (ugly!) from super class and edited to
        // override parser. The parser change here is to support block expressions.
        long msec = System.currentTimeMillis();
        JCCompilationUnit tree = make.TopLevel(List.<JCTree.JCAnnotation>nil(),
                                      null, List.<JCTree>nil());
        if (content != null) {
            if (verbose) {
                printVerbose("parsing.started", filename);
            }
            if (taskListener != null) {
                TaskEvent e = new TaskEvent(TaskEvent.Kind.PARSE, filename);
                taskListener.started(e);
            }
            int initialErrorCount = log.nerrors;
            final Scanner scanner = getScannerFactory().newScanner(content);
            // This was: Parser parser = parserFactory.newParser(scanner, keepComments(), genEndPos);
            Parser parser = new Parser(parserFactory, scanner, keepComments()) {

                @Override
                public JCExpression expression() {
                    Token next = scanner.token();
                    if (next == LBRACE) {
                        return parseBlockExpression();
                    } else {
                        scanner.token(next);
                        return super.expression();
                    }
                }

                @Override
                protected JCExpression term3() {
                    Token next = scanner.token();
                    if (next == LBRACE) {
                        return parseBlockExpression();
                    } else {
                        scanner.token(next);
                        return super.term3();
                    }
                }

                @Override
                protected JCExpression checkExprStat(JCExpression t) {
                    // Block expressions can have last expressions that are not valid
                    // Java expressions! So, don't check for validity.
                    return t;
                }

                private JCExpression parseBlockExpression() {
                    JCBlock blk = block();
                    List<JCStatement> stats = blk.getStatements();
                    JCExpression value = null;
                    JCStatement lastStat = stats.last();
                    ListBuffer<JCStatement> newStats = new ListBuffer<JCStatement>();
                    final int count = stats.size();
                    int index = 1;
                    for (JCStatement s : stats) {
                        // Is this the last statement?
                        if (index == count) {
                            lastStat = s;
                            break;
                        }
                        newStats.append(s);
                        index++;
                    }
                    if (lastStat != null && lastStat.getKind() == Kind.EXPRESSION_STATEMENT) {
                        stats = newStats.toList();
                        value = ((JCExpressionStatement)lastStat).getExpression();
                    }
                    JCExpression expr = new BlockExprJCBlockExpression(0L, stats, value);
                    expr.pos = blk.pos;
                    return expr;
                }
            };
            tree = parser.compilationUnit();
            // HACK: Need to set parseErrors flag in superclass which is private!
            // Use reflection hack to set right value of parseErrors flag.
            setParseErrors(log.nerrors > initialErrorCount);
            if (lineDebugInfo) {
                tree.lineMap = scanner.getLineMap();
            }
            if (verbose) {
                printVerbose("parsing.done", Long.toString(System.currentTimeMillis() - msec));
            }
        }

        tree.sourcefile = filename;

        if (content != null && taskListener != null) {
            TaskEvent e = new TaskEvent(TaskEvent.Kind.PARSE, tree);
            taskListener.finished(e);
        }

        return tree;
    } // where
        private void setParseErrors(boolean newValue) {
            try {
                Field parseErrorsField = JavaCompiler.class.getDeclaredField("parseErrors");
                parseErrorsField.setAccessible(true);
                boolean oldValue = parseErrorsField.getBoolean(this);
                parseErrorsField.setBoolean(this, oldValue |= newValue);
            } catch (Exception ignored) {
            }
       }
}
