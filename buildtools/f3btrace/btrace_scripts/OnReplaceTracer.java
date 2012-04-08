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
 * Prints trace message at every on replace call entry/return.
 *
 * @author A. Sundararajan
 */
@BTrace public class OnReplaceTracer { 
    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/onReplace\\$.+/"
    )
    public static void onReplaceEnter(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Entering on replace ");
        print(className);
        print(".");
        println(substr(methodName, strlen("onReplace$")));
    }

    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/onReplace\\$.+/",
        location=@Location(Kind.RETURN)
    )
    public static void onOnReplaceReturn(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Leaving on replace ");
        print(className);
        print(".");
        println(substr(methodName, strlen("onReplace$")));
    }
}
