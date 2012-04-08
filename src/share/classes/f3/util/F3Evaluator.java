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

package f3.util;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.f3.tools.script.F3ScriptEngineFactory;

// factored out to avoid linkage error for javax.script.* on Java 1.5
class Evaluator {
    static Object eval(String script) throws ScriptException {
        F3ScriptEngineFactory fac = new F3ScriptEngineFactory();
        ScriptEngine engine = fac.getScriptEngine();
        if (engine == null)
            throw new ScriptException("no scripting engine available");
        return engine.eval(script);
    }
}

/**
 *
 * @author Saul Wold
 * @profile desktop
 */
public class F3Evaluator {

    /**
     * <p>
     * Evaluates a F3 source string and returns its result, if any.
     * This method depends upon the F3 compiler API being accessible
     * by the application, such as including the <code>f3c.jar</code> file
     * in the application's classpath.
     * </p>
     * <p>
     * This method also depends upon the JSR-223 API classes being accessible
     * by the application, such as including the <code>script-api.jar</code> 
     * file in the application's classpath or the application must be run on 
     * JDK 6+ where JSR-223 API classes are part of the platform API. For
     * JDK 5, script-api.jar has to be in application's classpath.
     * </p>
     * <p> 
     * Note: This method provides only the simplest scripting functionality;
     * the script is evaluated without any specified context state, nor can 
     * any state it creates during evaluation be reused by other scripts.  For
     * sophisticated scripting applications, use the Java Scripting API
     * (<code>javax.script</code>).
     * </p>
     * 
     * @param script the F3 source to evaluate
     * @return the results from evaluating the script, or null if no results
     *         are returned by the script.
     * @throws javax.script.ScriptException
     */
    public static Object eval(String script) {
        try {
            return Evaluator.eval(script);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
