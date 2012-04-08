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

package org.f3.tools.api;

import com.sun.tools.mjavac.code.Symtab;
import com.sun.tools.mjavac.util.Context;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.f3.tools.comp.F3AttrContext;
import org.f3.tools.comp.F3Env;

/**
 * Provides an implementation of Scope.
 *
 * <p><b>This is NOT part of any API supported by Sun Microsystems.
 * If you write code that depends on this, you do so at your own
 * risk.  This code and its internal interfaces are subject to change
 * or deletion without notice.</b></p>
 *
 * @author Jonathan Gibbons;
 */
public class F3cScope implements org.f3.api.tree.Scope {
    protected final F3Env<F3AttrContext> env;
    private final Symtab syms;
    private final Context ctx;

    /** Creates a new instance of JavacScope */
    F3cScope(Context context, F3Env<F3AttrContext> env) {
        env.getClass(); // null-check
        this.env = env;
        this.ctx = context;
        this.syms = Symtab.instance(context);
    }

    public F3cScope getEnclosingScope() {
        if (env.outer != null && env.outer != env)
            return  new F3cScope(ctx, env.outer);
        else {
            // synthesize an outermost "star-import" scope
            return new F3cScope(ctx, env) {
                @Override
                public boolean isStarImportScope() {
                    return true;
                }
                @Override
                public F3cScope getEnclosingScope() {
                    return null;
                }
                @Override
                public Iterable<? extends Element> getLocalElements() {
                    return env.toplevel.starImportScope.getElements();
                }
            };
        }
    }

    public TypeElement getEnclosingClass() {
        // hide the dummy class that javac uses to enclose the top level declarations
        return (env.outer == null || env.outer == env ? null : env.enclClass.sym);
    }

    public ExecutableElement getEnclosingMethod() {
        return (env.enclFunction == null ? null : env.enclFunction.sym);
    }

    public Iterable<? extends Element> getLocalElements() {
        return env.info.getLocalElements();
    }

    public F3Env<F3AttrContext> getEnv() {
        return env;
    }

    public boolean isStarImportScope() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof F3cScope) {
            F3cScope s = (F3cScope) other;
            return (env.equals(s.env)
                && isStarImportScope() == s.isStarImportScope());
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return env.hashCode() + (isStarImportScope() ? 1 : 0);
    }

    @Override
    public String toString() {
        return "F3cScope[env=" + env + ",starImport=" + isStarImportScope() + "]";
    }
}
