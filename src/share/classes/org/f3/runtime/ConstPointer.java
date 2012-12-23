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


/**
 * Pointers
 *
 * @author Brian Goetz
 * @author A. Sundararajan
 */
public class ConstPointer<a> {
    final Type type;
    final F3Object obj;
    final int varnum;

    public static ConstPointer make(Type type, F3Object obj, int varnum) {
        return new ConstPointer(type, obj, varnum);
    }
    
    public ConstPointer(Type type, F3Object obj, int varnum) {
        this.type = type;
        this.obj = obj;
        this.varnum = varnum;
    }

    public Object getDefaultValue() {
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

    public F3Object getF3Object() {
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
