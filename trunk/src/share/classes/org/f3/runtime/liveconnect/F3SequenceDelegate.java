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

public class F3SequenceDelegate extends F3TypeDelegate {

    public F3SequenceDelegate(Bridge bridge) {
        this.bridge = bridge;
    }

    public boolean invoke(String methodName,
                          Object receiver,
                          Object[] arguments,
                          boolean isStatic,
                          boolean objectIsApplet,
                          Result[] result) throws Exception {
        throw new UnsupportedOperationException("Sequences do not have methods");
    }

    public boolean getField(String fieldName,
                            Object receiver,
                            boolean isStatic,
                            boolean objectIsApplet,
                            Result[] result) throws Exception {
        F3Value val = getField0(fieldName, (F3SequenceValue) receiver, isStatic, objectIsApplet);
        if (val != null) {
            // NOTE: we always unbox values because F3 sequences
            // conceptually contain primitives and not boxing objects
            result[0] = new Result(unbox(val), false);
        }
        return true;
    }

    private F3Value getField0(String fieldName,
                              F3SequenceValue receiver,
                              boolean isStatic,
                              boolean objectIsApplet) throws Exception {
        if ("length".equals(fieldName)) {
            return context.mirrorOf(receiver.getItemCount());
        } else {
            int index = Integer.parseInt(fieldName);
            return receiver.getItem(index);
        }
    }            

    public boolean setField(String fieldName,
                            Object receiver,
                            Object value,
                            boolean isStatic,
                            boolean objectIsApplet) throws Exception {
        throw new UnsupportedOperationException("Setting sequence elements not yet supported");

        /*
        // FIXME: need setItem() on F3SequenceValue
        int index = Integer.parseInt(fieldName);
        F3SequenceValue seq = (F3SequenceValue) receiver;
        F3Value val = (F3Value) bridge.convert(value, seq.getType().getComponentType());
        seq.setItem(index, val);
        */
    }

    public boolean hasField(String fieldName,
                            Object receiver,
                            boolean isStatic,
                            boolean objectIsApplet,
                            boolean[] result) {
        result[0] = hasField0(fieldName, (F3SequenceValue) receiver, objectIsApplet);
        return true;
    }

    private boolean hasField0(String fieldName,
                              F3SequenceValue receiver,
                              boolean objectIsApplet) {
        if ("length".equals(fieldName)) {
            return true;
        } else {
            try {
                int index = Integer.parseInt(fieldName);
                return (index >= 0 && index < receiver.getItemCount());
            } catch (Exception e) {
                return false;
            }
        }
    }

    public boolean hasMethod(String methodName,
                             Object receiver,
                             boolean isStatic,
                             boolean objectIsApplet,
                             boolean[] result) {
        // Sequences don't have methods
        result[0] = false;
        return true;
    }

    public boolean hasFieldOrMethod(String name,
                                    Object receiver,
                                    boolean isStatic,
                                    boolean objectIsApplet,
                                    boolean[] result) {
        return hasField(name, receiver, isStatic, objectIsApplet, result);
    }

    public Object findClass(String name) {
        return null;
    }

    public Object newInstance(Object clazz,
                              Object[] arguments) throws Exception {
        return null;
    }

    //----------------------------------------------------------------------
    // Internals only below this point
    //
    private F3Local.Context context = F3Local.getContext();
    private Bridge bridge;
}
