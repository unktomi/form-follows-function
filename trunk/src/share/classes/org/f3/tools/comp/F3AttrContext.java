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

import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.code.*;

/** Contains information specific to the attribute and enter
 *  passes, to be used in place of the generic field in environments.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3AttrContext {

    /** The scope of local symbols.
     */
    public Scope scope = null;

    /** The number of enclosing `static' modifiers.
     */
    int staticLevel = 0;

    /** Is this an environment for a this(...) or super(...) call?
     */
    boolean isSelfCall = false;

    /** Are we evaluating the selector of a `super' or type name?
     */
    boolean selectSuper = false;

    /** Are arguments to current function applications boxed into an array for varargs?
     */
    boolean varArgs = false;

    /** A list of type variables that are all-quantifed in current context.
     */
    List<Type> tvars = List.nil();
    /** A record of the lint/SuppressWarnings currently in effect
     */
    public Lint lint;


    /** The variable whose initializer is being attributed
     * useful for detecting self-references in variable initializers
     */
    Symbol enclVar = null;


    /**
     * Is this context nested inside an on-invalidate trigger? Used in
     * conjuction with the above symbol in order to determine whether the
     * target var of an on-invalidate trigger is being referenced from the
     * trigger itself. Such access should be considered illegal.
     */
    boolean inInvalidate = false;

    /** Duplicate this context, replacing scope field and copying all others.
     */
    F3AttrContext dup(Scope scope) {
        F3AttrContext info = new F3AttrContext();
        info.scope = scope;
        info.staticLevel = staticLevel;
        info.isSelfCall = isSelfCall;
        info.selectSuper = selectSuper;
        info.varArgs = varArgs;
        info.tvars = tvars;
        info.lint = lint;
        info.enclVar = enclVar;
        info.inInvalidate = inInvalidate;
        return info;
    }

    /** Duplicate this context, copying all fields.
     */
    F3AttrContext dup() {
        return dup(scope);
    }
    
    public Iterable<Symbol> getLocalElements() {
        if (scope == null)
            return List.nil();
        return scope.getElements();
    }
    
    public String toString() {
        return "F3AttrContext[" + scope.toString() + "]";
    }
}

