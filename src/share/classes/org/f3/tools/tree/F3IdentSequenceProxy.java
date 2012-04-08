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

package org.f3.tools.tree;

import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.util.Name;

/**
 * A special translation-support identifier which proxies for a sequence
 */
public class F3IdentSequenceProxy extends F3Ident {

    private F3VarSymbol boundSizeSym;

    protected F3IdentSequenceProxy(Name name, Symbol sym, F3VarSymbol boundSizeSym) {
        super(name, sym);
        this.boundSizeSym = boundSizeSym;
    }

    public F3VarSymbol boundSizeSym() {
        return boundSizeSym;
    }
}

