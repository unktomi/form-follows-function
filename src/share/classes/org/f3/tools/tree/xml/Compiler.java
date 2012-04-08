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

package org.f3.tools.tree.xml;

import org.f3.api.tree.UnitTree;
import com.sun.source.tree.LineMap;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.model.JavacElements;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Convert;
import com.sun.tools.mjavac.util.Options;
import com.sun.tools.mjavac.util.Position;
import org.f3.tools.code.F3Types;
import java.util.HashMap;
import java.util.Map;

/**
 * API for XSL stylesheets. This class provides access to symbols, types, command
 * line options and line/colum number information to XSL stylesheets. While XSL 
 * can access types and symbols in the input AST document, more specific queries
 * like subtype etc. will need API calls.
 *
 * XSL stylesheets can invoke methods of this class using the namespace URI
 * http://xml.apache.org/xalan/java/org.f3.tools.tree.xml.Compiler.
 *
 * @author A. Sundararajan
 */
public final class Compiler {
    // This method escapes special characters in String so that it can be used
    // as string literal in source code.

    public static String quoteString(String str) {
        return Convert.quote(str);
    }

    // This method escapes special characters so that it can be used
    // as character literal in source code.
    public static String quoteChar(String str) {
        return Convert.quote(str.charAt(0));
    }
    // type mirror from a given String name of type

    public static Type typeMirror(String name) {
        return typeSymbol(name).asType();
    }

    // type element from string name
    public static Symbol typeElement(String name) {
        return typeSymbol(name);
    }

    // synonym for typeElement
    public static Symbol typeSymbol(String name) {
        return elements().getTypeElement(name);
    }

    // package name from string name
    public static Symbol packageElement(String name) {
        return packageSymbol(name);
    }

    // synonym for packageSymbol
    public static Symbol packageSymbol(String name) {
        return elements().getTypeElement(name);
    }

    // symbol from symbol referen id
    public static Symbol symbol(String id) {
        return getState().converter.idToSymbol(id);
    }

    // synonym for symbol
    public static Symbol element(String id) {
        return symbol(id);
    }

    // type from type reference id
    public static Type type(String id) {
        return getState().converter.idToType(id);
    }

    public static F3Types types() {
        return F3Types.instance(getState().context);
    }

    public static JavacElements symbols() {
        return JavacElements.instance(getState().context);
    }

    // synonym to symbols
    public static JavacElements elements() {
        return symbols();
    }

    // access to command line options
    public static String option(String name) {
        return Options.instance(getState().context).get(name);
    }

    // line, column and position information
    public static long line(long position) {
        LineMap lm = getState().compilationUnit.getLineMap();
        return (lm != null) ? lm.getLineNumber(position) : 0;
    }

    public static long column(long position) {
        LineMap lm = getState().compilationUnit.getLineMap();
        return (lm != null) ? lm.getColumnNumber(position) : 0;
    }

    public static long position(long line, long column) {
        LineMap lm = getState().compilationUnit.getLineMap();
        return (lm != null) ? lm.getPosition(line, column) : Position.NOPOS;
    }

    public static long startPosition(long line) {
        LineMap lm = getState().compilationUnit.getLineMap();
        return (lm != null) ? lm.getStartPosition(line) : Position.NOPOS;
    }

    // get/put globals in compiler context
    public static Object getGlobal(String name) {
        return getGlobalsMap().get(name);
    }

    public static Object putGlobal(String name, Object value) {
        Object res = getGlobalsMap().put(name, value);
        return (res != null)? res : "";
    }

    // scripting extension
    // evaluate given script code
    public static Object evalScriptFile(String file) throws Exception {
        if (scriptingSupported) {
            return ScriptEvaluator.evalScriptFile(getState().context, file);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Object evalScript(String code) throws Exception {
        if (scriptingSupported) {
            return ScriptEvaluator.evalScript(getState().context, code);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    // package privates below this point
    // enter compiler into specific context
    static void enter(Context context, UnitTree cu, TreeXMLSerializer converter) {
        context.getClass(); // null check
        cu.getClass(); // null check
        converter.getClass(); // null check

        State s = new State();
        s.context = context;
        s.compilationUnit = cu;
        s.converter = converter;
        state.set(s);
    }

    // remove compiler state
    static void leave() {
        state.remove();
    }

    // -- Internals only below this point
    private static boolean scriptingSupported;
    static {
        try {
            Class.forName("javax.script.ScriptEngine");
            scriptingSupported = true;
        } catch (ClassNotFoundException cnfe) {
            scriptingSupported = false;
        }
    }

    private static class State {
        Context context;
        UnitTree compilationUnit;
        TreeXMLSerializer converter;
    }
    private static ThreadLocal<State> state = new ThreadLocal<State>();

    private static State getState() {
        State s = state.get();
        if (s == null) {
            throw new IllegalStateException("compiler state is null!");
        }
        return s;
    }

    private static class Globals extends HashMap<String, Object> {
    }

    private static Map<String, Object> getGlobalsMap() {
        Context context = getState().context;
        Globals globals = context.get(Globals.class);
        if (globals == null) {
            globals = new Globals();
            context.put(Globals.class, globals);
        }
        return globals;
    }
    // Don't create me!
    private Compiler() {
    }
}
