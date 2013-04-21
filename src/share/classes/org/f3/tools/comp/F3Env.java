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

package org.f3.tools.comp;

import com.sun.tools.mjavac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.code.Symbol;
import org.f3.tools.tree.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** A class for environments, instances of which are passed as
 *  arguments to tree visitors.  Environments refer to important ancestors
 *  of the subtree that's currently visited, such as the enclosing method,
 *  the enclosing class, or the enclosing toplevel node. They also contain
 *  a generic component, represented as a type parameter, to carry further
 *  information specific to individual passes.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3Env<A> implements Iterable<F3Env<A>> {

    /** The next enclosing environment.
     */
    public F3Env<A> next;

    /** The environment enclosing the current class.
     */
    public F3Env<A> outer;

    /** The tree with which this environment is associated.
     */
    public F3Tree tree;

    /** The enclosing toplevel tree.
     */
    public F3Script toplevel;

    /** The translated toplevel tree.
     */
    public JCCompilationUnit translatedToplevel;

    /** The next enclosing class definition.
     */
    public F3ClassDeclaration enclClass;

    /** The next enclosing method definition.
     */
    public F3FunctionDefinition enclFunction;

    public F3Var enclVar;

    public F3Var thisVar;

    /* implicit the uses */
    List<Symbol> implicitArgs = List.nil();

    /** Location info for debugging
     */
    public F3Tree where;

    /** A generic field for further information.
     */
    public A info;

    /** Is this an environment for evaluating a base clause?
     */
    public boolean baseClause = false;

    /** Create an outermost environment for a given (toplevel)tree,
     *  with a given info field.
     */
    public F3Env(F3Tree tree, A info) {
	this.next = null;
	this.outer = null;
	this.tree = tree;
	this.toplevel = null;
	this.enclClass = null;
	this.enclFunction = null;
	this.info = info;
    }

    /** Duplicate this environment, updating with given tree and info,
     *  and copying all other fields.
     */
    public F3Env<A> dup(F3Tree tree, A info) {
	return dupto(new F3Env<A>(tree, info));
    }

    /** Duplicate this environment into a given Environment,
     *  using its tree and info, and copying all other fields.
     */
    public F3Env<A> dupto(F3Env<A> that) {
	that.next = this;
	that.outer = this.outer;
	that.toplevel = this.toplevel;
 	that.enclClass = this.enclClass;
	that.enclFunction = this.enclFunction;
	return that;
    }

    /** Duplicate this environment, updating with given tree,
     *  and copying all other fields.
     */
    public F3Env<A> dup(F3Tree tree) {
	return dup(tree, this.info);
    }

    /** Return closest enclosing environment which points to a tree with given tag.
     */
    public F3Env<A> enclosing(F3Tag tag) {
	F3Env<A> env1 = this;
	while (env1 != null && env1.tree.getF3Tag() != tag) env1 = env1.next;
	return env1;
    }
    
    @Override
    public String toString() {
        return "F3Env[" + info + (outer == null ? "" : ",outer=" + outer) + "]";
    }

    public Iterator<F3Env<A>> iterator() {
        return new Iterator<F3Env<A>>() {
            F3Env<A> next = F3Env.this;
            public boolean hasNext() {
                return next.outer != null;
            }
            public F3Env<A> next() {
                if (hasNext()) {
                    F3Env<A> current = next;
                    next = current.outer;
                    return current;
                }
                throw new NoSuchElementException();

            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
