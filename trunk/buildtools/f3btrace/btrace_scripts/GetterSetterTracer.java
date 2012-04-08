/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

/**
 * Prints trace message at every F3 var getter/setter call entry/return.
 * Also, prints trace messages on internal setter (the "be$foo" methods).
 *
 * @author A. Sundararajan
 */
@BTrace public class GetterSetterTracer { 

    // "get$foo" are called to do (external) var set
    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/get\\$.+/"
    )
    public static void onGetterEnter(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Entering get ");
        print(className);
        print(".");
        println(substr(methodName, strlen("get$")));
    }

    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/get\\$.+/",
        location=@Location(Kind.RETURN)
    )
    public static void onGetterReturn(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Leaving get ");
        print(className);
        print(".");
        println(substr(methodName, strlen("get$")));
    }

    // "set$foo" are called to do (external) var set
    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/set\\$.+/"
    )
    public static void onSetterEnter(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Entering set ");
        print(className);
        print(".");
        println(substr(methodName, strlen("set$")));
    }

    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/set\\$.+/",
        location=@Location(Kind.RETURN)
    )
    public static void onSetterReturn(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Leaving set ");
        print(className);
        print(".");
        println(substr(methodName, strlen("set$")));
    }

    // "be$foo" are called to do recomputation set (internal set)
    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/be\\$.+/"
    )
    public static void onBeEnter(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Entering internal set ");
        print(className);
        print(".");
        println(substr(methodName, strlen("be$")));
    }

    // "be$foo" are called to do recomputation set (internal set)
    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/be\\$.+/",
        location=@Location(Kind.RETURN)
    )
    public static void onBeReturn(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Leaving internal set ");
        print(className);
        print(".");
        println(substr(methodName, strlen("be$")));
    }
}
