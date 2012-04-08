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

package org.f3.tools.f3doc;

import com.sun.javadoc.*;

import com.sun.tools.mjavac.code.Symbol.VarSymbol;

/**
 * ParameterImpl information.
 * This includes a parameter type and parameter name.
 *
 * @author Kaiyang Liu (original)
 * @author Robert Field (rewrite)
 * @author Scott Seligman (generics, annotations)
 */
public class ParameterImpl implements Parameter {

    private final VarSymbol sym;
    private final com.sun.javadoc.Type type;

    /**
     * Constructor of parameter info class.
     */
    ParameterImpl(DocEnv env, VarSymbol sym) {
        this.sym = sym;
        this.type = TypeMaker.getType(env, sym.type, false);
    }

    /**
     * Get the type of this parameter.
     */
    public com.sun.javadoc.Type type() {
        return type;
    }
    
    public com.sun.tools.mjavac.code.Type rawType() {
        sym.complete();
        return sym.type; 
    }

    /**
     * Get local name of this parameter.
     * For example if parameter is the short 'index', returns "index".
     */
    public String name() {
        return sym.toString();
    }

    /**
     * Get type name of this parameter.
     * For example if parameter is the short 'index', returns "short".
     */
    public String typeName() {
        return (type instanceof ClassDoc || type instanceof TypeVariable)
                ? type.typeName()       // omit formal type params or bounds
                : type.toString();
    }

    /**
     * Returns a string representation of the parameter.
     * <p>
     * For example if parameter is the short 'index', returns "short index".
     *
     * @return type name and parameter name of this parameter.
     */
    @Override
    public String toString() {
        return typeName() + " " + sym;
    }

    /**
     * Get the annotations of this parameter.
     * Return an empty array if there are none.
     */
    public AnnotationDesc[] annotations() {
        return new AnnotationDesc[0];
    }
}
