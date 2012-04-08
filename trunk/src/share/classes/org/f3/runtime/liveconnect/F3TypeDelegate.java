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

package org.f3.runtime.liveconnect;

import java.util.*;

import com.sun.java.browser.plugin2.liveconnect.v1.*;
import f3.reflect.*;

public abstract class F3TypeDelegate implements InvocationDelegate {
    protected F3TypeDelegate() {
        F3Local.Context context = F3Local.getContext();
        voidType = context.getVoidType();
        booleanType = context.getBooleanType();
        integerType = context.getIntegerType();
        numberType = context.getNumberType();
    }

    protected Object unbox(Object obj) {
        if (obj == Void.TYPE) {
            return obj;
        }

        F3Value val = (F3Value) obj;
        if (val == null)
            return null;
        
        F3Type type = val.getType();
        if (type instanceof F3PrimitiveType) {
            return ((F3PrimitiveValue) val).asObject();
        } else if (type instanceof F3Local.ClassType) {
            F3Local.ClassType classType = (F3Local.ClassType) type;
            if (!classType.isF3Type()) {
                // Return Java values as Java objects instead of F3 Script wrappers
                return ((F3Local.ObjectValue) val).asObject();
            }
        }
        // Sequence, etc. that we don't want to convert
        return val;
    }

    //----------------------------------------------------------------------
    // Internals only below this point
    //

    // Need to know about certain primitive types
    protected F3Type voidType;
    protected F3Type booleanType;
    protected F3Type integerType;
    protected F3Type numberType;
}
