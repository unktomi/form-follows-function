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

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.util.Position;

import org.f3.tools.tree.F3FunctionDefinition;
import java.lang.reflect.Modifier;

/**
 * Represents a method of a java class.
 *
 * @since 1.2
 * @author Robert Field
 * @author Neal Gafter (rewrite)
 */

public class FunctionDocImpl
        extends ExecutableMemberDocImpl implements MethodDoc {

    /**
     * constructor.
     */
    public FunctionDocImpl(DocEnv env, MethodSymbol sym) {
        super(env, sym);
    }

    /**
     * constructor.
     */
    public FunctionDocImpl(DocEnv env, MethodSymbol sym,
                         String docComment, F3FunctionDefinition tree, Position.LineMap lineMap) {
        super(env, sym, docComment, tree, lineMap);
    }

    /**
     * Return true if it is a method, which it is.
     * Note: constructors are not methods.
     *
     * @return true
     */
    @Override
    public boolean isMethod() {
        return true;
    }

    /**
     * Return true if this method is abstract
     */
    public boolean isAbstract() {
        //### This is dubious, but old 'javadoc' apparently does it.
        //### I regard this as a bug and an obstacle to treating the
        //### doclet API as a proper compile-time reflection facility.
        //### (maddox 09/26/2000)
        if (containingClass().isInterface()) {
            //### Don't force creation of ClassDocImpl for super here.
            // Abstract modifier is implicit.  Strip/canonicalize it.
            return false;
        }
        return (getFlags() & Flags.ABSTRACT) != 0;
    }

    /**
     * Get return type.
     *
     * @return the return type of this method, null if it
     * is a constructor.
     */
    public com.sun.javadoc.Type returnType() {
        sym.complete();
        return TypeMaker.getType(env, sym.type.getReturnType(), false);
    }
    
    public com.sun.tools.mjavac.code.Type rawReturnType() {
        sym.complete();
        return sym.type.getReturnType();
    }

    /**
     * Return the class that originally defined the method that
     * is overridden by the current definition, or null if no
     * such class exists.
     *
     * @return a ClassDocImpl representing the superclass that
     * originally defined this method, null if this method does
     * not override a definition in a superclass.
     */
    public ClassDoc overriddenClass() {
        com.sun.javadoc.Type t = overriddenType();
        return (t != null) ? t.asClassDoc() : null;
    }

    /**
     * Return the type containing the method that this method overrides.
     * It may be a <code>ClassDoc</code> or a <code>ParameterizedType</code>.
     */
    public com.sun.javadoc.Type overriddenType() {

        if ((sym.flags() & Flags.STATIC) != 0) {
            return null;
        }

        ClassSymbol origin = (ClassSymbol)sym.owner;
        for (Type t = env.types.supertype(origin.type);
             t.tag == TypeTags.CLASS;
             t = env.types.supertype(t)) {
            ClassSymbol c = (ClassSymbol)t.tsym;
            for (Scope.Entry e = c.members().lookup(sym.name); e.scope != null; e = e.next()) {
                if (sym.overrides(e.sym, origin, env.types, true)) {
                    return TypeMaker.getType(env, t);
                }
            }
        }
        return null;
    }

    /**
     * Return the method that this method overrides.
     *
     * @return a MethodDoc representing a method definition
     * in a superclass this method overrides, null if
     * this method does not override.
     */
    public MethodDoc overriddenMethod() {

        // Real overriding only.  Static members are simply hidden.
        // Likewise for constructors, but the MethodSymbol.overrides
        // method takes this into account.
        if ((sym.flags() & Flags.STATIC) != 0) {
            return null;
        }

        // Derived from  com.sun.tools.mjavac.comp.Check.checkOverride .

        ClassSymbol origin = (ClassSymbol)sym.owner;
        for (Type t = env.types.supertype(origin.type);
             t.tag == TypeTags.CLASS;
             t = env.types.supertype(t)) {
            ClassSymbol c = (ClassSymbol)t.tsym;
            for (Scope.Entry e = c.members().lookup(sym.name); e.scope != null; e = e.next()) {
                if (sym.overrides(e.sym, origin, env.types, true)) {
                    return env.getFunctionDoc((MethodSymbol)e.sym);
                }
            }
        }
        return null;
    }

    /**
     * Tests whether this method overrides another.
     * The overridden method may be one declared in a superclass or
     * a superinterface (unlike {@link #overriddenMethod()}).
     *
     * <p> When a non-abstract method overrides an abstract one, it is
     * also said to <i>implement</i> the other.
     *
     * @param meth  the other method to examine
     * @return <tt>true</tt> if this method overrides the other
     */
    public boolean overrides(MethodDoc meth) {        
        MethodSymbol overridee = ((FunctionDocImpl) meth).sym;
        ClassSymbol origin = (ClassSymbol) sym.owner;
        overridee.complete();
        origin.complete();

        return sym.name == overridee.name &&

               // not reflexive as per JLS
               sym != overridee &&

               // we don't care if overridee is static, though that wouldn't
               // compile
               !sym.isStatic() &&

               // sym, whose declaring type is the origin, must be
               // in a subtype of overridee's type
               env.types.asSuper(origin.type, overridee.owner) != null &&

               // check access and signatures; don't check return types
               sym.overrides(overridee, origin, env.types, false);
    }


    public String name() {
        return sym.name.toString();
    }

    public String qualifiedName() {
        return sym.enclClass().getQualifiedName() + "." + sym.name;
    }

    /**
     * Returns a string representation of this method.  Includes the
     * qualified signature, the qualified method name, and any type
     * parameters.  Type parameters follow the class name, as they do
     * in the syntax for invoking methods with explicit type parameters.
     */
    @Override
    public String toString() {
        return sym.enclClass().getQualifiedName() +
                "." + name() + signature();
    }
}
