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
import org.f3.api.tree.Tree.F3Kind;

import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.util.List;

/**
 * Type constructor
 */

public class F3TypeCons extends F3TypeVar {
    private final List<F3Expression> typeArgs;
    public F3TypeCons(F3Expression className,
		      Cardinality cardinality,
		      List<F3Expression> typeArgs,
		      TypeSymbol sym) {
	super(className, cardinality, sym);
	this.typeArgs = typeArgs;
    }

    public List<F3Expression> getArgs() {
	return typeArgs;
    }
}