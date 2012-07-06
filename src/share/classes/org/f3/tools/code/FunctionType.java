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

package org.f3.tools.code;

import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;

/**
 *
 * @author bothner
 */
public class FunctionType extends Type.ClassType {

    public MethodType mtype;
    public List<Type> typeArgs = List.<Type>nil();

    public Type asMethodOrForAll() {
	if (typeArgs != null && typeArgs.size() > 0) {
	    return new ForAll(typeArgs, mtype);
	}
	return mtype;
    }
    
    public FunctionType(Type outer, 
			List<Type> typarams, 
			TypeSymbol tsym,
			MethodType mtype) {
        super(outer, typarams, tsym);
        this.mtype = mtype;
    }

    /** Copy constructor. */
    public FunctionType(FunctionType orig) {
        this(orig.getEnclosingType(), orig.typarams_field, orig.tsym,
                orig.mtype);
    }

    @Override
    public List<Type>        getParameterTypes() { return mtype.getParameterTypes(); } 
    @Override
    public Type              getReturnType()     { return mtype.restype; }
    
    @Override
    public MethodType asMethodType () { return mtype; }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("function from ");
        if (mtype == null)
            s.append("?");
        else {
            List<Type> args = mtype.argtypes;
	    if (args.size() > 1) {
		s.append("(");
	    }
            for (List<Type> l = args; l.nonEmpty(); l = l.tail) {
                if (l != args)
                    s.append(',');
                s.append(l.head);
            }
	    if (args.size() > 1) {
		s.append("(");
	    }
        }
        s.append(" to ");
        s.append(mtype == null ? "?" : mtype.restype);
        return s.toString();
    }
    @Override
    public boolean equals(Object t) {
        return super.equals(t);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
