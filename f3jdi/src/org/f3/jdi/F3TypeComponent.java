/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.TypeComponent;

/**
 *
 * @author sundar
 */
public class F3TypeComponent extends F3Mirror implements TypeComponent {
    public F3TypeComponent(F3VirtualMachine f3vm, TypeComponent underlying) {
        super(f3vm, underlying);
    }

    public F3ReferenceType declaringType() {
        return F3Wrapper.wrap(virtualMachine(), underlying().declaringType());
    }

    public String genericSignature() {
        return underlying().genericSignature();
    }

    public boolean isFinal() {
        return underlying().isFinal();
    }

    public boolean isStatic() {
        return underlying().isStatic();
    }

    public boolean isSynthetic() {
        return underlying().isSynthetic();
    }

    public String name() {
        String realName = underlying().name();
        if (realName.charAt(0) != '$') {
            return realName;
        }
        return realName.substring(1);

        /*****
          A first cut at dealing with the fact that if an ivar is refd
          in a subclass, its name is mangled with the classname.
          EG:
             class sam {
                 var ivar0: Number;
                 var ivar1: Number
                 :
             }
             var samObj = sam{}
             samObj.ivar1 = 89;
          ivar0 is named 'ivar0' but ivar1 is named sam$ivar1.  I suppose
          there could also be a class fred with the same contents, so
          the fred$ and sam$ are needed to distinguish.
          For now we will do nothing with this.  The field name seen 
          by the debugger will be sam$ivar1.

        int nextDollar = realName.indexOf('$', 1);
        if ( nextDollar == -1) {
            return realName.substring(1);
        }
        String prefix = realName.substring(1, nextDollar);
        String fullClassName = declaringType().name();
        String className = fullClassName.substring(fullClassName.indexOf('$') + 1);
        if (className.equals(prefix)) {
            return realName.substring(nextDollar + 1);
        }
        ****/
    }

    public String signature() {
        return underlying().signature();
    }

    public boolean isPackagePrivate() {
        return underlying().isPackagePrivate();
    }

    public boolean isPrivate() {
        return underlying().isPrivate();
    }

    public boolean isProtected() {
        return underlying().isProtected();
    }

    public boolean isPublic() {
        return underlying().isPublic();
    }

    public int modifiers() {
        return underlying().modifiers();
    }

    @Override
    protected TypeComponent underlying() {
        return (TypeComponent) super.underlying();
    }
}
