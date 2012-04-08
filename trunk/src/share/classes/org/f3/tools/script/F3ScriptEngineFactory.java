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

package org.f3.tools.script;

import javax.script.*;
import java.util.*;

/**
 * This is the script engine factory for "F3" engines, based on 
 * JavaScriptEngineFactory from the Scripting project at 
 * https://scripting.dev.java.net/.
 * 
 * @author A. Sundararajan
 * @author Tom Ball
 */
public class F3ScriptEngineFactory implements ScriptEngineFactory {
    public String getEngineName() { 
        return "F3 Engine";
    }

    public String getEngineVersion() {
        return "1.0";
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public String getLanguageName() {
        return "f3";
    }

    public String getLanguageVersion() {
        return "1.0";
    }

    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder buf = new StringBuilder();
        buf.append(obj);
        buf.append(".");
        buf.append(m);
        buf.append("(");
        if (args.length != 0) {
            int i = 0;
            for (; i < args.length - 1; i++) {
                buf.append(args[i] + ", ");
            }
            buf.append(args[i]);
        }        
        buf.append(")");
        return buf.toString();
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<String> getNames() {
        return names;
    }

    public String getOutputStatement(String toDisplay) {
        StringBuilder buf = new StringBuilder();
        buf.append("java.lang.System.out.print(\"");
        int len = toDisplay.length();
        for (int i = 0; i < len; i++) {
            char ch = toDisplay.charAt(i);
            switch (ch) {
            case '"':
                buf.append("\\\"");
                break;
            case '\\':
                buf.append("\\\\");
                break;
            default:
                buf.append(ch);
                break;
            }
        }
        buf.append("\");");
        return buf.toString();
    }

    public String getParameter(String key) {
        if (key.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(ScriptEngine.NAME)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return "MULTITHREADED";
        } else {
            return null;
        }
    } 

    public String getProgram(String... statements) {
        StringBuilder sb = new StringBuilder();
        for( int i=0; i< statements.length; i++ ) {
            sb.append( statements[i] );
            sb.append("\n");
        }
        return sb.toString();
    }

    public ScriptEngine getScriptEngine() {
        F3ScriptEngineImpl engine = new F3ScriptEngineImpl();
        engine.setFactory(this);
        return engine;
    }

    private static List<String> names;
    private static List<String> extensions;
    private static List<String> mimeTypes;
    static {
        names = new ArrayList<String>(1);
        names.add("f3");
        extensions = Collections.unmodifiableList(names);
        names.add("f3");  // preferred engine name
        names = Collections.unmodifiableList(names);
        mimeTypes = new ArrayList<String>(0);
        mimeTypes.add("application/x-f3-source");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
}
