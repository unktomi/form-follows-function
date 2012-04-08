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

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.*;
import java.util.*;
import javax.script.*;
import javax.tools.*;
import com.sun.tools.mjavac.util.Name;
import org.f3.api.F3ScriptEngine;
import com.sun.tools.mjavac.code.*;

/**
 * This is script engine for the F3 language, based on
 * the https://scripting.dev.java.net Java language script engine by
 * A. Sundararajan.
 */
public class F3ScriptEngineImpl extends AbstractScriptEngine
        implements F3ScriptEngine {

    public F3ScriptEngineImpl() {
    }

    // my factory, may be null
    private ScriptEngineFactory factory;

    WeakHashMap<Bindings, F3ScriptContext> contextMap =
            new WeakHashMap<Bindings, F3ScriptContext>();

    F3ScriptContext getF3ScriptContext(ScriptContext ctx) {
        Bindings bindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
        return getF3ScriptContext(bindings);
    }

    F3ScriptContext getF3ScriptContext(Bindings bindings) {
        F3ScriptContext scontext = contextMap.get(bindings);
        if (scontext == null) {
            scontext = new F3ScriptContext(Thread.currentThread().getContextClassLoader());
            contextMap.put(bindings, scontext);
        }
        return scontext;
    }

    // my implementation for CompiledScript
    private class F3ScriptCompiledScript extends CompiledScript {
        F3CompiledScript compiled;

        F3ScriptCompiledScript(F3CompiledScript compiled) {
            this.compiled = compiled;
        }

        public F3ScriptEngineImpl getEngine() {
            return F3ScriptEngineImpl.this;
        }

        public Object eval(ScriptContext ctx) throws ScriptException {
            F3ScriptContext scontext = getF3ScriptContext(ctx);
            try {
                // FIXME - set to false if using (unimplemented) "synchronized"
                // implementation of ScriptContext and ScriptBindings.
                boolean copyVars = true;
                if (copyVars) {
                    Bindings globals = ctx.getBindings(ScriptContext.GLOBAL_SCOPE);
                    if (globals != null) {
                        for (Map.Entry<String, Object> entry : globals.entrySet()) {
                            String key = entry.getKey();
                            if (key.indexOf('.') >= 0)
                                continue; // Kludge FIXME
                            Symbol sym = compiled.lookup(key);
                            if (compiled.scriptScope.lookup(sym.name).sym == sym)
                                continue;
                            scontext.setVarValue(sym, entry.getValue());
                        }
                    }
                    for (Map.Entry<String, Object> entry : ctx.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
                        String key = entry.getKey();
                        if (key.indexOf('.') >= 0)
                            continue; // Kludge FIXME
                        Symbol sym = compiled.lookup(key);
                        if (sym == null)
                            continue;
                        if (compiled.scriptScope.lookup(sym.name).sym == sym)
                            continue;
                        scontext.setVarValue(sym, entry.getValue());
                    }
                }
                Object result = compiled.eval(scontext);
                if (copyVars) {
                    for (Scope.Entry e = compiled.compiler.namedImportScope.elems;
                        e != null; e = e.sibling) {
                        if ((e.sym.flags() & Flags.SYNTHETIC) != 0)
                        continue;
                        String name = e.sym.toString();
                        if (! (e.sym.owner instanceof Symbol.ClassSymbol))// FIXME - need flag for non-imports.
                            continue;
                        if (! (e.sym instanceof Symbol.VarSymbol))// FIXME - need flag for non-imports.
                            continue;
                        Object value = scontext.getVarValue(e.sym);
                        ctx.setAttribute(name, value, ScriptContext.ENGINE_SCOPE);
                    }
                }
                return result;
            } catch (RuntimeException exp) {
                throw exp;
            } catch (Error exp) {
                throw exp;
             } catch (Throwable exp) {
                throw new ScriptException((Exception) exp);
            }
        }

        public String getName() {
            return compiled.clazzName;
        }
    }

    public CompiledScript compile(String script) throws ScriptException {
        return compile(script, null);
    }

    public CompiledScript compile(Reader reader) throws ScriptException {
        return compile(readFully(reader));
    }

    public CompiledScript compile(String script, DiagnosticListener<JavaFileObject> listener)
            throws ScriptException {
        return parse(script, context, listener);
    }

    public CompiledScript compile(Reader script, DiagnosticListener<JavaFileObject> listener)
            throws ScriptException {
        return compile(readFully(script), listener);
    }
    
    public Object eval(String script, DiagnosticListener<JavaFileObject> listener) 
            throws ScriptException {
        return eval(script, getContext(), listener);
    }

    public Object eval(Reader script, DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        return eval(script, getContext(), listener);
    }

    public Object eval(String str, ScriptContext ctx)
            throws ScriptException {
        return eval(str, ctx, null);
    }

    public Object eval(Reader reader, ScriptContext ctx)
            throws ScriptException {
        return eval(readFully(reader), ctx);
    }

    public Object eval(String script, ScriptContext context, DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        F3ScriptCompiledScript cscript = parse(script, context, listener);
        return cscript.eval(context);
    }

    public Object eval(Reader reader, ScriptContext context, DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        return eval(readFully(reader), context, listener);
    }

    public Object eval(String script, Bindings bindings, DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        ScriptContext ctx = getContext();
        ctx.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        return eval(script, ctx, listener);
    }

    public Object eval(Reader reader, Bindings bindings, DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        ScriptContext ctx = getContext();
        ctx.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        return eval(reader, ctx, listener);
    }

    public ScriptEngineFactory getFactory() {
        if (factory == null) {
            factory = new F3ScriptEngineFactory();
        }
        return factory;
    }

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    void setFactory(ScriptEngineFactory factory) {
        this.factory = factory;
    }

    // Internals only below this point

    int counter;

    private F3ScriptCompiledScript parse(String str, ScriptContext ctx,
            final DiagnosticListener<JavaFileObject> listener) throws ScriptException {
        String fileName = getFileName(ctx);
        if ("<STDIN>".equals(fileName))
            fileName = "stdin" + ++counter;
        String sourcePath = getSourcePath(ctx);
        String classPath = getClassPath(ctx);
        String script = str;
        F3ScriptContext scontext = getF3ScriptContext(ctx);
        boolean copyVars = true;
        // JSR-223 requirement - but unsure if it's a good idea.
        // ctx.setAttribute("context", ctx, ScriptContext.ENGINE_SCOPE);	
        if (copyVars) {
            Bindings globals = ctx.getBindings(ScriptContext.GLOBAL_SCOPE);
            if (globals != null) {
                for (Map.Entry<String, Object> entry : globals.entrySet()) {
                    String key = entry.getKey();
                    if (key.indexOf('.') >= 0)
                        continue; // Kludge FIXME
                    Symbol sym = scontext.compiler.names == null ? null : scontext.compiler.lookup(key);
                    if (sym == null) {
                        scontext.compiler.compile(fileName+"_"+key, "public var <<"+key+">>;",
                        ctx.getErrorWriter(), null, classPath, listener);
                    }
                }
            }
            for (Map.Entry<String, Object> entry : ctx.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
                String key = entry.getKey();
                if (key.indexOf('.') >= 0)
                    continue; // Kludge FIXME
                Symbol sym = scontext.compiler.names == null ? null : scontext.compiler.lookup(key);
                if (sym == null) {
                    scontext.compiler.compile(fileName+"_"+key, "public var <<"+key+">>;",
                       ctx.getErrorWriter(), null, classPath, listener);
                }
            }
        }

        F3CompiledScript compiled = scontext.compiler.compile(fileName, script,
                ctx.getErrorWriter(), sourcePath, classPath, listener);
        if (compiled == null) {
            throw new ScriptException("compilation failed");
        }

        return new F3ScriptCompiledScript(compiled);
    }

    private static String getFileName(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(ScriptEngine.FILENAME);
        if (scope != -1) {
            Object fn = ctx.getAttribute(ScriptEngine.FILENAME, scope);
            return fn.toString();
        } else {
            return "___F3_SCRIPT___.f3";
        }
    }

    // for certain variables, we look for System properties. This is
    // the prefix used for such System properties
    private static final String SYSPROP_PREFIX = "org.f3.tools.script.";

    private static final String SOURCEPATH = "sourcepath";
    private static String getSourcePath(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(SOURCEPATH);
        if (scope != -1) {
            return ctx.getAttribute(SOURCEPATH).toString();
        } else {
            // look for "org.f3.tools.script.sourcepath"
            return System.getProperty(SYSPROP_PREFIX + SOURCEPATH);
        }
    }

    private static final String CLASSPATH = "classpath";
    private static String getClassPath(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(CLASSPATH);
        if (scope != -1) {
            return ctx.getAttribute(CLASSPATH).toString();
        } else {
            // look for "org.f3.tools.script.classpath"
            String res = System.getProperty(SYSPROP_PREFIX + CLASSPATH);
            if (res == null) {
                res = System.getProperty("java.class.path");
            }
            return res;
        }
    }

    // read a Reader fully and return the content as string
    private String readFully(Reader reader) throws ScriptException {
        try {
            return F3ScriptCompiler.readFully(reader);
        } catch (IOException exp) {
            throw new ScriptException(exp);
        }
    }

    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        if (thiz == null)
            throw new ScriptException("target object not specified");
        if (name == null)
            throw new ScriptException("method name not specified");
        Method method = F3ScriptContext.findMethod(thiz.getClass(), name, args);
        if (method == null)
            throw new ScriptException(new NoSuchMethodException());
        try {
            method.setAccessible(true);
            return method.invoke(thiz, args);
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        if (name == null)
            throw new ScriptException("method name not specified");
        F3ScriptContext scontext = getF3ScriptContext(getContext());
        Name nname = scontext.compiler.names.fromString(name);
        for (Scope.Entry e = scontext.compiler.namedImportScope.lookup(nname);
             e.sym != null; e = e.next()) {
            // FIXME - should also handle VarSymbol whose type is a FunctionType.
            if (e.sym instanceof Symbol.MethodSymbol) {
                Class script = scontext.loadSymbolClass(e.sym);
               Method method = F3ScriptContext.findMethod(script, name, args);
               if (method != null) {
                   try {
                        Constructor cons = findDefaultConstructor(script);
                        cons.setAccessible(true);
                        Object instance = cons.newInstance();
                        method.setAccessible(true);
                        return method.invoke(instance, args);
                    } catch (Exception ex) {
                        throw new ScriptException(ex);
                    }
                }
            }
        }
        throw new ScriptException(new NoSuchMethodException(name));
    }

    private Constructor findDefaultConstructor(Class script) throws NoSuchMethodException {
        Constructor[] cs = script.getDeclaredConstructors();
        for (Constructor c : cs) {
            if (c.getParameterTypes().length == 0) {
                return c;
            }
        }
        throw new NoSuchMethodException("default constructor");
    }

    public <T> T getInterface(Class<T> clazz) {
        return makeInterface(null, clazz);
    }

    public <T> T getInterface(Object thiz, Class<T> clazz) {
        if (thiz == null) {
            throw new IllegalArgumentException("script object is null");
        }
        return makeInterface(thiz, clazz);
    }

    private <T> T makeInterface(Object obj, Class<T> clazz) {
        final Object thiz = obj;
        if (clazz == null || !clazz.isInterface()) {
            throw new IllegalArgumentException("interface Class expected");
        }
        return (T) Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class[] { clazz },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method m, Object[] args)
                            throws Throwable {
                    if (thiz == null)
                            return invokeFunction(m.getName(), args);
                        return invokeMethod(thiz, m.getName(), args);
                    }
                });
    }

    // Workarounds for backward compatibility with old JSR-223 api on mac os

    public Object invoke(String name, Object...args) throws ScriptException, NoSuchMethodException
    {
	return invokeFunction(name, args);
    }

    public Object invoke(Object thiz, String name, Object...args) throws ScriptException, NoSuchMethodException {
	return invokeMethod(thiz, name, args);
    }
}
