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

package org.f3.tools.antlr;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import com.sun.tools.mjavac.util.*;
import org.f3.tools.tree.F3Script;

/**
 *
 * @author Robert Field
 * @author Jim Idle
 */
public class F3SyntacticAnalysis {

    /** The context key for the parser. */
    protected static final Context.Key<F3SyntacticAnalysis> syntaxKey =
            new Context.Key<F3SyntacticAnalysis>();
    
    protected final Context context;
    /** Command line options
     */
    protected final Options options;
    /** The name table.
     */
    protected final Name.Table names;
    
    /** The log to be used for error reporting.
     */
    public Log log;

    public static F3SyntacticAnalysis instance(Context context) {
        F3SyntacticAnalysis instance = context.get(syntaxKey);
        if (instance == null)
            instance = new F3SyntacticAnalysis(context);
        return instance;
    }
    
    protected F3SyntacticAnalysis(final Context context) {
        this.context = context;
        context.put(syntaxKey, this);

        names = Name.Table.instance(context);
        options = Options.instance(context);
        log = Log.instance(context);
    }

    public F3Script parse(CharSequence content, String fileName) {
        F3Script unit = null;
        String parserChoice = options.get("parser");
        if (parserChoice == null) {
            parserChoice = "v4"; // default
        }
        {
            try {
                
                if ( parserChoice.startsWith("v4") ) {
                    
                    // Create input stream from standard input
                    //
                    ANTLRStringStream input = new ANTLRStringStream(content.toString());
                    
                    // Create a lexer attached to that input stream
                    //
                    v4Lexer lexer = new v4Lexer(context, input);
                    
                    // Create a stream of tokens pulled from the lexer
                    //
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    
                    // Create a parser attached to the token stream
                    //
                    v4Parser parser = new v4Parser(tokens);
                    
                    // Set the context
                    //
                    parser.initialize(context);
                    
                    // Invoke the script rule and parse the script. This should
                    // always return a F3Sript.
                    //
                    unit = parser.script();

                    
                    // That's it
                }
               
            } catch (Throwable thr) {
                System.err.println("Error in syntactic analysis in " + fileName + ":");
                thr.printStackTrace(System.err);
            }
        }

        return unit;
    }

    /** The number of errors reported so far.
     */
    public int errorCount() {
        return log.nerrors;
    }

    private void printTree(Tree tree, String prefix) {
        CommonToken token = (CommonToken)((CommonTree)tree).getToken();
        System.out.println(prefix + tree + "  (" + token.getStartIndex() + ")" + token.getLine());
        int n = tree.getChildCount();
        String nextPrefix = prefix + "   ";
        for (int i = 0; i < n; ++i) {
            printTree(tree.getChild(i), nextPrefix);
        }
    }
}

