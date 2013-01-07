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
import org.f3.runtime.Pair;
import org.f3.runtime.Functor;

public class Function2<R, A1, A2> extends Function<R> implements Functor<Function2, R> {

    public <Y> Function2<Y, A1, A2>  map(final Function1<? extends Y, ? super R> f) {
	final Function2<R, A1, A2> self = this;
	return new Function2<Y, A1, A2>() {
	    public Y invoke(A1 x1, A2 x2) {
		final R r = self.invoke(x1, x2);
		return f.invoke(r);
	    }
	    public String toString() {
		return self+"map("+f+")";
	    }
	};
    }

    public Function2<? extends R, ? super A2, ? super A1> flip()  {
	final Function2<R, A1, A2> self = this;
	return new Function2<R, A2, A1>() 
	    {
		public R invoke(A2 x1, A1 x2) 
		{
		    return self.invoke(x2, x1);
		}
		public String toString() {
		    return self+"flip()";
		}
	    };
    }
    
    public <Y> Function2<R, A1, Y> mul(final Function1<? extends A2, ? super Y> f) {
	final Function2<R, A1, A2> self = this;
	return new Function2<R, A1, Y>() {
	    public R invoke(A1 x1, Y x2) {
		return self.invoke(x1, f.invoke(x2));
	    }
	    public String toString() {
		return self+" * "+f;
	    }
	};
    }

    public <Y, Z> Function2<R, Y, Z> mul(final Pair<Function1<? extends A1, ? super Y>, Function1<? extends A2, ? super Z> > p) {
	final Function2<R, A1, A2> self = this;
	return new Function2<R, Y, Z>() {
	    public R invoke(Y x1, Z x2) {
		return self.invoke(p.first.invoke(x1), p.second.invoke(x2));
	    }
	};
    }

    public Function1<? extends Function1<? extends R, ? super A2>, ? super A1> curry() {
	final Function2<R, A1, A2> self = this;
	return new Function1<Function1<? extends R, ? super A2>, A1>() {
	    public Function1<? extends R, ? super A2> invoke(final A1 x1) {
		return new Function1<R, A2>() {
		    public R invoke(A2 x2) {
			return self.invoke(x1, x2);
		    }
		    public String toString() {
			return self+".curry().apply("+x1+")";
		    }
		};
	    }
	    public String toString() {
		return self+".curry()";
	    }
	};
    }

    public R apply(final A1 x1, final A2 x2) {
	return invoke(x1, x2);
    }

    public Function1<R, A2> apply(final A1 x1) {
	final Function2<R, A1, A2> self = this;
	return new Function1<R, A2>() {
	    public R invoke(final A2 x2) {
		return self.invoke(x1, x2);
	    }
	    public String toString() {
		return self + ".apply("+x1+")";
	    }
	};
    }

    public Function2() {}
    
    public Function2(final F3Object implementor, final int number) {
        super(implementor, number);
    }

    
    // Get the implementor to invoke the function.
    // Don't override this.
    public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
        return invoke((A1)arg1, (A2)arg2);
    }
    
    // Override this
    public R invoke(A1 x1, A2 x2) {
        if (implementor != null) {
            return (R) implementor.invoke$(number, x1, x2, null);
        } else {
            throw new RuntimeException("invoke function missing");
        }
    }
}
