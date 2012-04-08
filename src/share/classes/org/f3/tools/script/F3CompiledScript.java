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
import com.sun.tools.mjavac.code.*;
import java.lang.reflect.*;
import org.f3.runtime.TypeInfo;
import org.f3.runtime.Entry;
import org.f3.runtime.sequence.Sequence;
import com.sun.tools.mjavac.util.Name;

/**
 *
 * @author Per Bothner
 */
public class F3CompiledScript {
    F3ScriptCompiler compiler;
    Scope scriptScope;
    String clazzName;

    public Object eval(F3ScriptContext ctx)  throws Throwable {
        Class clazz;
        try {
            clazz = ctx.loader.loadClass(clazzName);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("no main class found", ex);
        }

        Object result = null;

        // find the main method
        Method mainMethod = clazz.getMethod(Entry.entryMethodName(), Sequence.class);

        // call main method
        Object args = TypeInfo.String.emptySequence;
        try {
            return mainMethod.invoke(null, args);
        }
        catch (InvocationTargetException ex) {
            throw ex.getCause();
        }

    }

    public Symbol lookup (Name name) {
        return compiler.lookup(name);
    }
    public Symbol lookup (String name) {
        return compiler.lookup(name);
    }
}
