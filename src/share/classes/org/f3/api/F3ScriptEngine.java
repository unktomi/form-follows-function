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

import java.io.Reader;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

/**
 * The interface to the F3 scripting engine API.  Its use is
 * optional, as full script functionality is available via the javax.script
 * API.  In addition to the full script API, this interface defines versions
 * of the <code>eval</code> and <code>compile</code> <code>ScriptEngine</code>
 * methods which take a <code>DiagnosticListener</code> for accessing warning
 * and error diagnostics reported during script parsing.
 * 
 * @author Tom Ball
 */
public interface F3ScriptEngine extends ScriptEngine, Compilable, Invocable {
    
    /**
     * Causes the immediate execution of the script whose source is the String 
     * passed as the first argument. The script may be reparsed or recompiled 
     * before execution. State left in the engine from previous executions, 
     * including variable values and compiled procedures may be visible during 
     * this execution.
     * 
     * @param script   The script to be executed by the script engine.
     * @param context  A <code>ScriptContext</code> exposing sets of attributes in 
     *           different scopes. The meanings of the scopes 
     *           <code>ScriptContext.GLOBAL_SCOPE</code>, and 
     *           <code>ScriptContext.ENGINE_SCOPE</code> are defined in the 
     *           JSR-223 specification.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *           errors found during script parsing are reported.
     * @return   The value returned from the execution of the script.
     * @throws   ScriptException: if an error occurs in script. ScriptEngines 
     *           should create and throw ScriptException wrappers for checked 
     *           Exceptions thrown by underlying scripting implementations.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(String script, ScriptContext context, 
                DiagnosticListener<JavaFileObject> listener) throws ScriptException;
    
    /**
     * Same as <code>eval(String, ScriptContext, DiagnosticListener)</code>
     * where the source of the script is read from a Reader.
     * 
     * @param reader   The source of the script to be executed by the script engine.
     * @param context  A <code>ScriptContext</code> exposing sets of attributes in 
     *                 different scopes. The meanings of the scopes 
     *                 <code>ScriptContext.GLOBAL_SCOPE</code>, and 
     *                 <code>ScriptContext.ENGINE_SCOPE</code> are defined in the 
     *                 JSR-223 specification.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *                 errors found during script parsing are reported.
     * @return   The value returned from the execution of the script.
     * @throws   ScriptException: if an error occurs in script. ScriptEngines 
     *           should create and throw ScriptException wrappers for checked 
     *           Exceptions thrown by underlying scripting implementations.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(Reader reader, ScriptContext context, 
                DiagnosticListener<JavaFileObject> listener) throws ScriptException;
    
    /**
     * Executes the specified script. The default <code>ScriptContext</code>
     * for the <code>ScriptEngine</code> is used.
     * 
     * @param script   The script to be executed by the script engine.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *           errors found during script parsing are reported.
     * @return   The value returned from the execution of the script.
     * @throws   ScriptException: if an error occurs in script. ScriptEngines 
     *           should create and throw ScriptException wrappers for checked 
     *           Exceptions thrown by underlying scripting implementations.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(String script, DiagnosticListener<JavaFileObject> listener) throws ScriptException;
    
    /**
     * Same as <code>eval(String)</code> except that the source of 
     * the script is provided as a <code>Reader.</code>
     * 
     * @param script   The script to be executed by the script engine.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *           errors found during script parsing are reported.
     * @return   The value returned from the execution of the script.
     * @throws   ScriptException: if an error occurs in script. ScriptEngines 
     *           should create and throw ScriptException wrappers for checked 
     *           Exceptions thrown by underlying scripting implementations.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(Reader script, DiagnosticListener<JavaFileObject> listener) throws ScriptException;
    
    /**
     * Executes the script using the <code>Bindings</code> argument as the 
     * <code>ENGINE_SCOPE Bindings</code> of the <code>ScriptEngine</code> 
     * during the script execution. The <code>Reader</code>, <code>Writer</code> 
     * and non-<code>ENGINE_SCOPE Bindings</code> of the default 
     * <code>ScriptContext</code> are used. The <code>ENGINE_SCOPE Bindings</code> 
     * of the <code>ScriptEngine</code> is not changed, and its mappings are 
     * unaltered by the script execution.
     * 
     * @param script   The source for the script.
     * @param bindings The Bindings of attributes to be used for script execution.
     * @return   The value returned by the script.
     * @throws   ScriptException: if an error occurrs in script.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(String script, Bindings bindings, 
                DiagnosticListener<JavaFileObject> listener) throws ScriptException;    

    /**
     * Same as <code>eval(String, Bindings)</code> except that the source of 
     * the script is provided as a <code>Reader.</code>
     * 
     * @param reader   The source for the script.
     * @param bindings The Bindings of attributes to be used for script execution.
     * @return   The value returned by the script.
     * @throws   ScriptException: if an error occurrs in script.
     * @throws   NullPointerException: if either argument is null.
     */
    Object eval(Reader reader, Bindings bindings, 
                DiagnosticListener<JavaFileObject> listener) throws ScriptException;
    
    /**
     * Compiles the script (source represented as a <code>String</code>) for 
     * later execution.
     * 
     * @param script   The source of the script, represented as a <code>String</code>.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *           errors found during script parsing are reported.
     * @return   An subclass of <code>CompiledScript</code> to be executed later
     *           using one of the eval methods of <code>CompiledScript</code>.
     * @throws   ScriptException: if compilation fails.
     * @throws   NullPointerException: if the argument is null.
     */
    CompiledScript compile(String script, DiagnosticListener<JavaFileObject> listener)
            throws ScriptException;

    /**
     * Compiles the script (source read from <code>Reader</code>) for later 
     * execution. Functionality is identical to <code>compile(String)</code> 
     * other than the way in which the source is passed.
     * 
     * @param script   The reader from which the script source is obtained.
     * @param listener A <code>DiagnosticListener</code> to which warnings and
     *           errors found during script parsing are reported.
     * @return   An subclass of <code>CompiledScript</code> to be executed later
     *           using one of the eval methods of <code>CompiledScript</code>.
     * @throws   ScriptException: if compilation fails.
     * @throws   NullPointerException: if the argument is null.
     */
    CompiledScript compile(Reader script, DiagnosticListener<JavaFileObject> listener)
            throws ScriptException;
}
