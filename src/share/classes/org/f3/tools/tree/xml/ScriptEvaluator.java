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

import com.sun.tools.mjavac.util.Context;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Support for script evaluation from XSL stylesheets.
 *
 * @author A. Sundararajan
 */
class ScriptEvaluator {
    static Object evalScriptFile(Context context, String file)
            throws ScriptException, FileNotFoundException {
        return getScriptEngine(context).eval(new BufferedReader(new FileReader(file)));
    }

    static Object evalScript(Context context, String code) throws ScriptException {
        return getScriptEngine(context).eval(code);
    }

    //-- Internals only below this point
    private static final String SCRIPT_LANGUAGE_OPTION = "treexsl:scriptlang";
    private static final String DEFAULT_SCRIPT_LANGUAGE = "javascript";

    private static ScriptEngine getScriptEngine(Context context) {
        ScriptEngine engine = context.get(ScriptEngine.class);
        if (engine == null) {
            ScriptEngineManager man = new ScriptEngineManager();
            String lang = Compiler.option(SCRIPT_LANGUAGE_OPTION);
            if (lang == null) {
                lang = DEFAULT_SCRIPT_LANGUAGE;
            }
            engine = man.getEngineByName(lang);
            if (engine == null) {
                throw new IllegalArgumentException("script engine not found : " + lang);
            }
            context.put(ScriptEngine.class, engine);
        }
        return engine;
    }
}
