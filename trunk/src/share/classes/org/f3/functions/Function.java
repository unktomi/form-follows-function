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

package org.f3.functions;

import org.f3.runtime.F3Object;

public abstract class Function<R> {
    // Class that implements the function.
    final protected F3Object implementor;
    
    // Function number.
    final protected int number;
    
    public Function() {
        implementor = null;
        number = 0;
    }
    
    public Function(final F3Object implementor, final int number) {
        this.implementor = implementor;
        this.number = number;
    }
    
    /** Normally indirects to the implementor's invoke$ function.
     * Don't override this.
     * If we have 0 arguments, then arg1, arg2 and rargs are null.
     * If we have 1 arguments, then it is passed in arg1, while arg2 and rargs are null.
     * If we have 2 arguments, they are passed in arg1 and arg2, while rargs is null.
     * If we have more than 2 arguments, the first 2 arg passed in arg1 and arg2
     * while the rest are passed in rargs.
     * */
    public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
        return invoke();
    }
    
    /** Used to support "hand-written" Java Function objects.
     * Override this as needed.
     */
    public R invoke() {
        if (implementor != null) {
            return (R) implementor.invoke$(number, null, null, null);
        } else {
            throw new RuntimeException("invoke function missing");
        }
    }
    
    // Format for easier debugging.
    @Override
    public String toString() {
        return implementor.getClass().getName() + ".function<" + number + ">";
    }
}
