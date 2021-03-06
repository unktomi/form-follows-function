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
import org.f3.functions.*;

/**
 * Pointers
 *
 * @author Brian Goetz
 * @author A. Sundararajan
 */
public class Pointer<This extends F3Object, T> extends ConstPointer<This, T> implements KeyValueTarget<T>, f3.lang.MemberRef<This>, f3.lang.Ref<T>, f3.lang.ObservableRef<T> {
    @org.f3.runtime.annotation.F3Signature("(Ljava/lang/Object;)Lorg/f3/runtime/Pointer;")
    public static <This extends F3Object, T> Pointer<This,T> make(Type type, This obj, int varnum) {
        return new Pointer<This,T>(type, obj, varnum);
    }
    
    public static <This extends F3Object, T> Pointer<This,T> make(This obj, int varnum) {
        return new Pointer<This,T>(Type.OBJECT, obj, varnum);
    }
    
    public static boolean equals(Pointer p1, Pointer p2) {
        return (p1 == null) ? (p2 == null) : p1.equals(p2);
    }

    private Pointer(Type type, This obj, int varnum) {
	super(type, obj, varnum);
    }

    public void set(T value) {
        if (obj != null) {
            obj.set$(varnum, (Object)value);
        }
    }

    public Pointer unwrap() {
        return this;
    }
    
    public void set(int pos, T value) {
        assert type == Type.SEQUENCE : "expecting a sequence type";
        if (obj != null) {
	    Sequences.<T>set(obj, varnum, value, pos);
        }
    }

    public Object getValue() {
	return get();
    }

    public void setValue(Object value) {
	set((T)value);
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

    /**
     * A BoundPointer is returned from the Pointer.bind(Pointer) method.
     * BoundPointer instance has to be kept alive till you want bind to be
     * effective. You can explicitly unbind the pointer using the "unbind"
     * method is this class.
     */
    public static class BoundPointer<a> extends Pointer<F3Object, a> {
        private Pointer srcPtr;
        private F3Object listener;

        private BoundPointer(Pointer<? extends F3Object,a> destPtr, Pointer<? extends F3Object,a> srcPtr, F3Object listener) {
            super(destPtr.getType(), destPtr.getF3Object(), destPtr.getVarNum());
            this.srcPtr = srcPtr;
            this.listener = listener;
        }

	public void set(a value) {
	    srcPtr.set(value);
	}

        /**
         * Uubind the current Pointer from the srcPtr. Repeated calls are fine
         * but subsequent calls are just no-ops. After unbind call, the BoundPointer
         * becomes effectively a regular, unbound Pointer. There is no need to
         * explicitly call 'unbind'. If this BoundPointer instance is unreachable,
         * GC will collect it and srcPtr will eventually see that the listener
         * object is not alive and so remove it from it's dependencies.
         */
        public void unbind() {
            srcPtr.removeDependency(listener);
            // clear everything related to Pointer bind.
            srcPtr = null;
            listener = null;
        }
    }

    /**
     * Implements identity bind expression between "srcPtr" and the current Pointer.
     * Whenever the value pointed by srcPtr changes, the current Pointer's value
     * is set from that.
     *
     * @param srcPtr The source Pointer object to which the current Pointer is bound to
     */
    public BoundPointer<T> bind(Pointer<? extends F3Object, T> srcPtr) {
        final F3Object thisObj = getF3Object();
        final int thisVarNum = getVarNum();
        final int srcVarNum = srcPtr.getVarNum();
        F3Object listener = new F3Base() {
            @Override
            public boolean update$(F3Object src, final int depNum,
				   int startPos, int endPos, int newLength, final int phase) {
                if ((phase & PHASE_TRANS$PHASE) == PHASE$TRIGGER) {
                    // update value from "src"
                    if (thisObj != null) {
                        thisObj.set$(thisVarNum, src.get$(srcVarNum));
                    }
                }
                return true;
            }
        };
        // initial update from "srcPtr"
        this.obj.set$(thisVarNum, (T)srcPtr.get());

        // add dependency so that we will get notified with update$ calls
        srcPtr.addDependency(listener);
        // return a BoundPointer so that use can call call "unbind()" later, if needed
        return new BoundPointer<T>(this, srcPtr, listener);
    }
}
