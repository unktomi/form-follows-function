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
import org.f3.runtime.Functor;
import org.f3.runtime.Monad;

public class Function1<R, A1> extends Function<R> 
    implements Monad<Function1, R> // , Comonad<Function1, R>
{

    public Function1<? extends Function1<?,?>, Function1<? extends Function1<?, ?>, 
					                 ? super Function1<? extends R, ? super A1>>>
	add(final Function1<R, A1> n0) 
    {
	final Function1<R, A1> n = (Function1<R, A1>)n0;
	final Function1<R, A1> m = this;
	return new Function1<Function1<?,?>, 
	                     Function1<? extends Function1<?, ?>, 
	                               ? super Function1<? extends R, ? super A1>>>() 
	{
	    public Function1<?, ?> invoke(final Function1<? extends Function1<?, ?>, 
					                  ? super Function1<? extends R, ? super A1>> f) 
	    {
		final Function1 a = f.invoke(m);
		final Function1 b = f.invoke(n);
		return a.mul(b);
	    }
	    public String toString() {
		return "("+m +") + ("+ n + ")";
	    }
	};
    }

    public <B> Function1<? extends R, ? super B> mul(final Function1<? extends A1, ? super B> f) {
	final Function1<R, A1> self = this;
	return new Function1<R, B>() {
	    public R invoke(final B b) {
		return self.invoke(f.invoke(b));
	    }
	    public String toString() {
		return "("+self +") * ("+ f + ")";
	    }
	};
    }
    
    public <B> Function1<? extends R, ? super B> composeWith(final Function1<? extends A1, ? super B> f) {
	final Function1<R, A1> self = this;
	return new Function1<R, B>() {
	    public R invoke(final B b) {
		return self.invoke(f.invoke(b));
	    }
	};
    }

    public <B> Function1<? extends B, ? super A1> andThen(final Function1<? extends B, ? super R> f) {
	final Function1<R, A1> self = this;
	return new Function1<B, A1>() {
	    public B invoke(final A1 a1) {
		return f.invoke(self.invoke(a1));
	    }
	};
    }

    public <Y> Function1<Y, A1> map(final Function1<? extends Y, ? super R> f) {
	final Function1<R, A1> self = this;
	return new Function1<Y, A1>() {
	    public Y invoke(final A1 a1) {
		return f.invoke(self.invoke(a1));
	    }
	};
    }

    public <Y> Function1<Y, A1> flatmap(final Function1<? extends Function1<Y, A1>, ? super R> f) {
	final Function1<R, A1> self = this;
	return new Function1<Y, A1>() {
	    public Y invoke(A1 x1) {
		final R r = self.invoke(x1);
	        Function1<Y, A1> g = f.invoke(r);
		return g.invoke(x1);
	    }
	};
    }

    /*
    public R extract() {
	this.invoke(zero)
    }

    public <Y> Function1<Y, A1> coflatmap(final Function1<? extends Y ? super Function1<R, A1>> f) {
	final Function1<R, A1> self = this;	
	return new Function1<Y, A1>() {
	    public Y invoke(A1 x1) {
		return f.invoke(new Function<R, A1>() {
			public R invoke(A1 x2) {
			    return self.invoke(add(x1, x2));
			}
		    });
	    }
	};
    }
    */

    public Function1() {}
    
    public Function1(final F3Object implementor, final int number) {
        super(implementor, number);
    }
    
    // Get the implementor to invoke the function.
    // Don't override this.
    public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
	//System.err.println("this="+this+", arg1="+arg1);
        return invoke((A1)arg1);
    }

    // Override this
    public R invoke(A1 x1) {
        if (implementor != null) {
            return (R) implementor.invoke$(number, x1, null, null);
        } else {
            throw new RuntimeException("invoke function missing in "+this);
        }
    }
}
