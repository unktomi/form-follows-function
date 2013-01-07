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
import org.f3.runtime.Monad;
import org.f3.runtime.F3Object;

public class Function0<R> extends Function<R> implements /* Reader */ Monad<Function0, R> {

    public Function0() {}
    
    public Function0(final F3Object implementor, final int number) {
        super(implementor, number);
    }
    
    // Get the implementor to invoke the function.
    // Don't override this.
    public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
        return invoke();
    }
    
    // Override this
    public R invoke() {
        if (implementor != null) {
            return (R) implementor.invoke$(number, null, null, null);
        } else {
            throw new RuntimeException("invoke function missing");
        }
    }

    public <Y> Function0<Y> map(final Function1<? extends Y, ? super R> f) {
	final Function0<R> self = this;
	return new Function0<Y>() {
	    public Y invoke() {
		return f.invoke(self.invoke());
	    }
	    public String toString() {
		return self + ".map("+f+")";
	    }
	};
    }

    public <Y> Function0<Y> flatmap(final Function1<? extends Function0<? extends Y>, ? super R> f) {
	final Function0<R> self = this;
	return new Function0<Y>() {
	    public Y invoke() {
		final R r = self.invoke();
	        final Function0<? extends Y> g = f.invoke(r);
		return g.invoke();
	    }
	    public String toString() {
		return self + ".flatmap("+f+")";
	    }
	};
    }

}
