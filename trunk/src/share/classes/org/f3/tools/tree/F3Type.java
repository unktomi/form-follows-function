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
import com.sun.tools.mjavac.code.BoundKind;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.util.List;
/**
 * Abstract base for types
 *
 * @author Robert Field
 */
public abstract class F3Type extends F3Expression implements TypeTree {    
    private final Cardinality cardinality;
    public BoundKind boundKind = BoundKind.UNBOUND;
    public F3Type upperBound;
    /**
     * @param cardinality one of the cardinality constants
     */
    protected F3Type(Cardinality cardinality) {
        this.cardinality = cardinality;
    }
    
    protected F3Type() {
        this(null);
    }
    public Cardinality getCardinality() { return cardinality; }

    public static class RawSequenceType extends F3TypeAny {
	protected RawSequenceType() {
	    super(Cardinality.ANY);
	}
    }

    public static class TypeApply extends F3TypeAny {
	public F3Expression className;
	public List<F3Expression> args;
	protected TypeApply(F3Expression className, Cardinality card, List<F3Expression> args) {
	    super(card);
	    this.className = className;
	    this.args = args;
	}
    }

    public static class TheType extends F3TypeAny {
	final public F3Type theType;
	public Symbol resolvedSymbol;
	public F3Expression accessExpr;
	protected TheType(F3Type t) {
	    super(Cardinality.SINGLETON);
	    theType = t;
	}
    }
}
