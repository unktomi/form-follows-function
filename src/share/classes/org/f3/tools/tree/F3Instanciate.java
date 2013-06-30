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

import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.util.List;

/**
 * A class declaration
 */
public class F3Instanciate extends F3Expression implements InstantiateTree {

    private final F3Kind f3Kind;
    private final F3Expression clazz;
    public F3ClassDeclaration def;
    public List<F3Expression> args;
    public List<F3ObjectLiteralPart> parts;
    private final List<F3Var> localVars;
    public List<Type> typeArgTypes;
    public ClassSymbol sym;
    public Symbol constructor;
    public Symbol varDefinedByThis;
    public boolean genericInstance = false; // hack: flag indicates request to instantiate generic type

    protected F3Instanciate(F3Kind f3Kind, F3Expression clazz, F3ClassDeclaration def, List<F3Expression> args,
            List<F3ObjectLiteralPart> parts, List<F3Var> localVars, ClassSymbol sym) {
        this.f3Kind = f3Kind;
        this.clazz = clazz;
        this.def = def;
        this.args = args;
        this.parts = parts;
        this.localVars = localVars;
        this.sym = sym;
    }

    public void accept(F3Visitor v) {
        v.visitInstanciate(this);
    }

    public F3Expression getIdentifier() {
        return clazz;
    }
    
    public List<F3Expression> getArgs() {
        return args;
    }

    public java.util.List<ExpressionTree> getArguments() {
        return F3Tree.convertList(ExpressionTree.class, args);
    }

    public Symbol getIdentifierSym() {
        switch (clazz.getF3Tag()) {
            case IDENT:
                return ((F3Ident) clazz).sym;
            case SELECT:
                return ((F3Select) clazz).sym;
        }
        assert false;
        return null;
    }

    public java.util.List<VariableTree> getLocalVariables() {
        return convertList(VariableTree.class, localVars);
    }

    /**
     *  For API uses only - object literals locals are desugared in a block
     *  surrounding the object literal. This is done in F3Lower. After lowering,
     *  the compiler doesn't have to deal with them explicitly. Note that we still
     *  need to maintain access for IDE.
     */
    public List<F3Var> getLocalvars() {
        return localVars;
    }

    public List<F3ObjectLiteralPart> getParts() {
        return parts;
    }

    public java.util.List<ObjectLiteralPartTree> getLiteralParts() {
        return F3Tree.convertList(ObjectLiteralPartTree.class, parts);
    }

    public F3ClassDeclaration getClassBody() {
        return def;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.OBJECT_LITERAL;
    }

    public F3Kind getF3Kind() {
        return f3Kind;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitInstantiate(this, data);
    }
}
