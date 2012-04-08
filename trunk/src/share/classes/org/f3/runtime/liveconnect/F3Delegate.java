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

public class F3Delegate implements InvocationDelegate {
    public F3Delegate(Bridge bridge, String scriptClassName) {
        this.bridge = bridge;
        this.scriptClassName = scriptClassName;
    }

    public boolean invoke(String methodName,
                          Object receiver,
                          Object[] arguments,
                          boolean isStatic,
                          boolean objectIsApplet,
                          Result[] result) throws Exception {
        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null)
            return false;
        return delegate.invoke(methodName, (isStatic ? null : box[0]), arguments, isStatic, objectIsApplet, result);
    }

    public boolean getField(String fieldName,
                            Object receiver,
                            boolean isStatic,
                            boolean objectIsApplet,
                            Result[] result) throws Exception {
        if (objectIsApplet) {
            if (fieldName.equalsIgnoreCase("script")) {
                result[0] = new Result(new JavaNameSpace(scriptClassName), false);
                return true;
            }
        }

        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null)
            return false;
        return delegate.getField(fieldName, (isStatic ? null : box[0]), isStatic, objectIsApplet, result);
    }

    public boolean setField(String fieldName,
                            Object receiver,
                            Object value,
                            boolean isStatic,
                            boolean objectIsApplet) throws Exception {
        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null)
            return false;
        return delegate.setField(fieldName, (isStatic ? null : box[0]), value, isStatic, objectIsApplet);
    }

    public boolean hasField(String fieldName,
                            Object receiver,
                            boolean isStatic,
                            boolean objectIsApplet,
                            boolean[] result) {
        if (objectIsApplet) {
            if (fieldName.equalsIgnoreCase("script")) {
                result[0] = true;
                return true;
            }
        }        

        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null)
            return false;
        return delegate.hasField(fieldName, (isStatic ? null : box[0]), isStatic, objectIsApplet, result);
    }

    public boolean hasMethod(String methodName,
                             Object receiver,
                             boolean isStatic,
                             boolean objectIsApplet,
                             boolean[] result) {
        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null) {
            return false;
        }
        return delegate.hasMethod(methodName, (isStatic ? null : box[0]), isStatic, objectIsApplet, result);
    }

    public boolean hasFieldOrMethod(String name,
                                    Object receiver,
                                    boolean isStatic,
                                    boolean objectIsApplet,
                                    boolean[] result) {
        if (objectIsApplet) {
            if (name.equalsIgnoreCase("script")) {
                result[0] = true;
                return true;
            }
        }
        Object[] box = new Object[] { receiver };
        InvocationDelegate delegate = getDelegate(box, isStatic, objectIsApplet);
        if (delegate == null)
            return false;
        return delegate.hasFieldOrMethod(name, (isStatic ? null : box[0]), isStatic, objectIsApplet, result);
    }

    public Object findClass(String name) {
        if (notF3Classes.contains(name)) {
            return null;
        }

        try {
            F3ClassType clazz = context.findClass(name);
            if (clazz != null && clazz.isF3Type()) {
                return clazz;
            }
        } catch (Throwable t) {
        }
        synchronized(this) {
            notF3Classes.add(name);
        }
        return null;
    }

    public Object newInstance(Object clazz,
                              Object[] arguments) throws Exception {
        // FIXME
        throw new UnsupportedOperationException("Instantiation of F3 classes not yet supported");
    }

    //----------------------------------------------------------------------
    // Internals only below this point
    //

    private Bridge bridge;
    private String scriptClassName;
    private F3Local.Context context = F3Local.getContext();
    private Map<F3ClassType, F3ClassDelegate> classDelegates =
        new HashMap<F3ClassType, F3ClassDelegate>();
    private Set<String> notF3Classes = new HashSet<String>();
    // We only need a singleton sequence delegate
    private F3SequenceDelegate sequenceDelegate;

    private synchronized InvocationDelegate getDelegate(Object[] box, boolean isStatic, boolean objectIsApplet) {
        Object obj = box[0];
        if ((obj instanceof F3ClassType) ||
            (obj instanceof F3ObjectValue)) {
            if (isStatic) {
                return getClassDelegate((F3ClassType) obj);
            } else {
                F3ObjectValue f3Obj = (F3ObjectValue) obj;
                return getClassDelegate(f3Obj.getClassType());
            }
        } else if (obj instanceof F3SequenceValue) {
            if (sequenceDelegate == null) {
                sequenceDelegate = new F3SequenceDelegate(bridge);
            }
            return sequenceDelegate;
        } else if (objectIsApplet) {
            // The incoming applet object comes in as a non-F3ObjectValue
            // but needs to be identified as such; we could do this check
            // for other values as well but we prefer not to due to the cost
            F3ObjectValue f3Obj = context.mirrorOf(obj);
            F3ClassType f3Class = f3Obj.getClassType();
            if (f3Class.isF3Type()) {
                // Upgrade the receiver to an F3ObjectValue
                box[0] = f3Obj;
                return getClassDelegate(f3Class);
            }
        }

        return null;
    }

    private InvocationDelegate getClassDelegate(F3ClassType f3Class) {
        F3ClassDelegate delegate = classDelegates.get(f3Class);
        if (delegate == null) {
            delegate = new F3ClassDelegate(f3Class, bridge);
            classDelegates.put(f3Class, delegate);
        }
        return delegate;
    }

    private F3ClassType scriptClass;
    private F3ClassType getScriptClass(String className) {
        if (scriptClass == null) {
            scriptClass = context.findClass(className);
        }
        return scriptClass;
    }
}
