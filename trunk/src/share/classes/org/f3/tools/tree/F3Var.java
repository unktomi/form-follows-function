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

package org.f3.tools.tree;

import org.f3.api.tree.*;
import org.f3.api.F3BindStatus;

import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Flags;
/**
 * Variable declaration.
 *
 * @author Robert Field
 * @author Zhiqun Chen
 */
public class F3Var extends F3AbstractVar implements VariableTree {
    
    private F3VarInit varInit;
    
    public Type baseType;

    protected F3Var() {
        this(null, null, null, null, null, null, null, null);
    }

    protected F3Var(Name name,
            F3Type f3type,
            F3Modifiers mods,
            F3Expression init,
            F3BindStatus bindStat,
            F3OnReplace onReplace,
            F3OnReplace onInvalidate,
            F3VarSymbol sym) {
        super(name, f3type, mods, init, bindStat, onReplace, onInvalidate, sym);
    }
    
    public static class This extends F3Var
    {
	public This(Name name,
		    F3Type type,
		    F3Modifiers mods) {
	    super(name, type,
		  mods, null, F3BindStatus.UNBOUND, null, null, null);
	}
    }

    /**
     * @return the varInit
     */
    public F3VarInit getVarInit() {
        return varInit;
    }

    /**
     * @param varInit the varInit to set
     */
    public void setVarInit(F3VarInit varInit) {
        this.varInit = varInit;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.VAR_DEF;
    }
    
    public boolean isOverride() {
        return false;
    }

    public void accept(F3Visitor v) {
        v.visitVar(this);
    }
}
