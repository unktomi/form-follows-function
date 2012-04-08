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
 * Prints trace message at every invalidation call entry/return.
 * Also, increments a MBean property on every invalidation.
 *
 * @author A. Sundararajan
 */
@BTrace public class InvalidationTracer { 
    // expose number of invalidations (monotonically increasing)
    // as a MBean property
    @Property
    public static long invalidations;

    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/invalidate\\$.+/"
    )
    public static void onInvalidateEnter(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        invalidations++;
        print("Entering invalidate ");
        print(className);
        print(".");
        println(substr(methodName, strlen("invalidate$")));
    }

    @OnMethod(
        clazz="+org.f3.runtime.F3Object",
        method="/invalidate\\$.+/",
        location=@Location(Kind.RETURN)
    )
    public static void onInvalidateReturn(
        @ProbeClassName String className, @ProbeMethodName String methodName) {
        print("Leaving invalidate ");
        print(className);
        print(".");
        println(substr(methodName, strlen("invalidate$")));
    }
}
