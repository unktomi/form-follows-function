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

package org.f3.runtime;
import org.f3.runtime.sequence.Sequences;
import f3.animation.KeyValueTarget;
import f3.animation.KeyValueTarget.Type;
import org.f3.functions.Function1;

/**
 * Pointers
 *
 * @author Brian Goetz
 * @author A. Sundararajan
 */
//
// Pointers are Functors/Monads/Comonads (i.e they are "containers" of 1 object, namely the thing they point at)
//

public class ConstPointer<This extends F3Object,a> implements Monad<ConstPointer, a>, Comonad<ConstPointer, a>, f3.lang.MemberRef<This>, f3.lang.ConstRef<a> 
{
    final Type type;
    final This obj;
    final int varnum;

    public static class MappedPointer<This extends F3Object, a, b> extends ConstPointer<This, b> {
	final ConstPointer<This, a> self;
	final Function1<? extends b, ? super a> f;
	public MappedPointer(Type type, 
			     This obj, 
			     int varnum, 
			     ConstPointer<This, a> self, 
			     Function1<? extends b, ? super a> f) {
	    super(type, obj, varnum);
	    this.self = self;
	    this.f = f;
	}
	public b getDefaultValue() {
	    return f.invoke(self.getDefaultValue());
	}
	public b get() {
	    return f.invoke(self.get());
	}
	public b get(int pos) {
	    return f.invoke(self.get(pos));
	}
	public boolean equals(Object o) {
	    if (o instanceof MappedPointer) {
		MappedPointer p = (MappedPointer)o;
		return self.equals(p.self) &&
		    f.equals(p.f);
	    }
	    return false;
	}
	public int hashCode() {
	    return super.hashCode() ^ self.hashCode() ^ f.hashCode();
	}
    }

    public static <This extends F3Object, a> 
	ConstPointer<This,a> make(Type type, This obj, int varnum) {
        return new ConstPointer<This,a>(type, obj, varnum);
    }
    
    public ConstPointer(Type type, This obj, int varnum) {
        this.type = type;
        this.obj = obj;
        this.varnum = varnum;
    }

    public <b> ConstPointer<? extends This, ? extends b>
	map(Function1<? extends b, ? super a> f) {
	return new MappedPointer<This, a, b>(type, obj, varnum, this, f);
    }

    public <b> ConstPointer<? extends F3Object, ? extends b>
	flatmap(final Function1<? extends ConstPointer<? extends F3Object, ? extends b>, ? super a> f) {
	final ConstPointer<This, a> self = this;
	return new ConstPointer<F3Object, b>(Type.OBJECT, null, 0) { // uh?
	    public b get() {
		return f.invoke(self.get()).get();
	    }
	    public b getDefaultValue() {
		return f.invoke(self.getDefaultValue()).get();
	    }
	    public b get(int pos) {
		return f.invoke(self.get(pos)).get();
	    }
	    public boolean equals(Object o) {
		return o == this;
	    }
	    public int hashCode() {
		return self.hashCode() ^ f.hashCode();
	    }
	};
    }

    public <b> ConstPointer<? extends F3Object, ? extends b>
	coflatmap(final Function1<? extends b,  ? super ConstPointer<? extends This, ? extends a>> f) {
	final ConstPointer<This, a> self = this;
	return new ConstPointer<F3Object, b>(Type.OBJECT, null, 0) { // uh?
	    public b get() {
		return f.invoke(self);
	    }
	    public b getDefaultValue() {
		return f.invoke(new ConstPointer<This, a>(self.type, self.obj, self.varnum) {
			public a get() {
			    return self.getDefaultValue();
			}
		    });
	    }
	    public b get(final int pos) {
		return f.invoke(new ConstPointer<This, a>(self.type, self.obj, self.varnum) {
			public a get() {
			    return self.get(pos);
			}
		    });
	    }
	    public boolean equals(Object o) {
		return o == this;
	    }
	    public int hashCode() {
		return self.hashCode() ^ f.hashCode();
	    }
	};
    }

    public a extract() {
	return get();
    }

    public a getDefaultValue() {
	return (a)getDefaultValue0();
    }

    Object getDefaultValue0() {
        switch (type) {
            case BYTE: return (byte)0;
            case SHORT: return (short)0;
            case INTEGER: return 0;
            case LONG: return 0L;
            case FLOAT: return 0.0F;
            case DOUBLE: return 0.0D;
            case BOOLEAN: return false;
            case SEQUENCE: return TypeInfo.Object.emptySequence;
            case OBJECT: return null;
        }
        // unknown type, so return null
        return null;
    }

    public This getF3Object() {
        return obj;
    }

    public int getVarNum() {
        return varnum;
    }

    public Type getType() {
        return type;
    }

    public a get() {
        return (a)(obj != null? obj.get$(varnum) : getDefaultValue());
    }

    public a get(int pos) {
        assert type == Type.SEQUENCE : "expecting a sequence type";
        return (a)(obj != null? obj.elem$(varnum, pos) : null);
    }

    public int size() {
        assert type == Type.SEQUENCE : "expecting a sequence type";
        return obj != null? obj.size$(varnum)  : 0;
    }

    public Object getValue() {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConstPointer) {
            ConstPointer other = (ConstPointer)o;
            return obj == other.obj && varnum == other.varnum;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(obj) ^ varnum;
    }

    public void addDependency(F3Object dep) {
        if (obj != null) {
            obj.addDependent$(varnum, dep, 0);
        }
    }

    public void removeDependency(F3Object dep) {
        if (obj != null) {
            obj.removeDependent$(varnum, dep);
        }
    }

    public static void switchDependence(Pointer oldPtr, Pointer newPtr, F3Object dep, int depNum) {
        if (oldPtr != newPtr && dep != null) {
            F3Object oldSrc = (oldPtr != null)? oldPtr.getF3Object() : null;
            F3Object newSrc = (newPtr != null)? newPtr.getF3Object() : null;
            int oldVarNum = (oldPtr != null)? oldPtr.getVarNum() : 0;
            int newVarNum = (newPtr != null)? newPtr.getVarNum() : 0;
            dep.switchDependence$(oldSrc, oldVarNum, newSrc, newVarNum, depNum);
        }
    }
}
