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

package org.f3.tools.script;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.code.*;
import java.lang.reflect.*;
import java.util.*;
import java.lang.ref.*;
import f3.reflect.*;
//import com.sun.tools.mjavac.code.*;

/**
 * Run-time context used for evaluating scripts.
 * @author Per Bothner
 */
public class F3ScriptContext {
    F3ScriptCompiler compiler;
    MemoryClassLoader loader;

    protected F3ScriptContext() {
    }

    public F3ScriptContext(ClassLoader parentClassLoader) {
        compiler = new F3ScriptCompiler(parentClassLoader);
        loader = new MemoryClassLoader(compiler.clbuffers, parentClassLoader);
    }

    public Symbol lookupSymbol (Name name) {
        return compiler.namedImportScope.lookup(name).sym;
    }

    public Symbol lookupSymbol (String name) {
        return lookupSymbol(compiler.names.fromString(name));
    }

    public boolean containsSymbol (Name name) {
        return lookupSymbol(name) != null;
    }

    public boolean containsSymbol (String name) {
        return lookupSymbol(compiler.names.fromString(name)) != null;
    }

    protected Class loadSymbolClass (Symbol sym) {
        String cname = ((Symbol.ClassSymbol) sym.owner).flatname.toString();
        try {
            return loader.loadClass(cname);
        } catch (ClassNotFoundException ex) {
            String sname = sym.getSimpleName().toString();
            throw new RuntimeException("no class "+cname+" for "+sname, ex);
        }
    }

    F3VarMember reflectSymbol (Symbol sym) {
        SoftReference<F3VarMember> ref = symbolMap.get(sym);
        if (ref != null) {
            F3VarMember rvar = ref.get();
            if (rvar != null)
                return rvar;
        }
        String cname = ((Symbol.ClassSymbol) sym.owner).flatname.toString();
        String sname = sym.getSimpleName().toString();
        Class clazz;
        try {
            clazz = loader.loadClass(cname);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("no class "+cname+" for "+sname, ex);
        }
        try {
            F3Local.Context rcontext = F3Local.getContext();
            F3Local.ClassType rclass = rcontext.makeClassRef(clazz);
            F3VarMember rvar = rclass.getVariable(sname);
            ref = new SoftReference(rvar);
            symbolMap.put(sym, ref);
            return rvar;
        }
        catch (Exception ex) {
            throw new RuntimeException("no field in "+cname+" for "+sname, ex);
        }
    }

    public Object getVarValue (Symbol sym)  {
        F3VarMember rvar = reflectSymbol(sym);
        F3Value rvalue = rvar.getValue(null);
        return ((F3Local.Value) rvalue).asObject();
    }

    public Object getVarValue(Name name) {
        Scope.Entry entry = compiler.namedImportScope.lookup(name);
        return getVarValue(entry.sym); // FIXME check for errors
    }

    public Object getVarValue(String name) {
        Name nname = compiler.names.fromString(name);
        Scope.Entry entry = compiler.namedImportScope.lookup(nname);
        return getVarValue(entry.sym); // FIXME check for errors
    }

    static WeakHashMap<Symbol,SoftReference<F3VarMember>> symbolMap =
        new WeakHashMap<Symbol,SoftReference<F3VarMember>>();

    public void setVarValue(Symbol sym, Object newValue) {
        F3VarMember rvar = reflectSymbol(sym);
        F3Local.Context rcontext = F3Local.getContext();
        rvar.setValue(null, rcontext.mirrorOf(newValue));
    }

    public void setVarValue(Name name, Object newValue) {
        Scope.Entry entry = compiler.namedImportScope.lookup(name);
        setVarValue(entry.sym, newValue); // FIXME check for errors
    }

    public void setVarValue(String name, Object newValue) {
        Name nname = compiler.names.fromString(name);
        Scope.Entry entry = compiler.namedImportScope.lookup(nname);
        setVarValue(entry.sym, newValue); // FIXME check for errors
    }

    private static Map<Class,Class> wrappers = new HashMap<Class,Class>();
    static {
        wrappers.put(boolean.class, Boolean.class);
        wrappers.put(byte.class, Byte.class);
        wrappers.put(char.class, Character.class);
        wrappers.put(double.class, Double.class);
        wrappers.put(float.class, Float.class);
        wrappers.put(int.class, Integer.class);
        wrappers.put(long.class, Long.class);
        wrappers.put(short.class, Short.class);
    }

    static Method findMethod(Class clazz, String name, Object[] args) {
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++)
            argTypes[i] = args[i].getClass();

        try {
            return clazz.getMethod(name, argTypes);
        } catch (NoSuchMethodException e) {
        // fall-through
        }

        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name)) {
                if (m.isVarArgs())
                    return m;
                Class[] parameters = m.getParameterTypes();
                if (args.length != parameters.length)
                    continue;
                if (argsMatch(argTypes, parameters))
                    return m;
                }
            }
        return null;
    }

    static boolean argsMatch(Class[] argTypes, Class[] parameterTypes) {
        for (int i = 0; i < argTypes.length; i++) {
            Class arg = argTypes[i];
            Class param = parameterTypes[i];
            if (param.isPrimitive())
                param = wrappers.get(param);
            if (param == null || !(param.isAssignableFrom(arg)))
                return false;
            }
        return true;
    }
}
