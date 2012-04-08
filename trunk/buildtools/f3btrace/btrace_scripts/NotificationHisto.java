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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

/**
 * Prints histogram of F3Base.notifyDependents$ calls once every 10 sec.
 *
 * @author A. Sundararajan
 */
@BTrace public class NotificationHisto {
    private static Map<String, AtomicInteger> histo = newHashMap();

    @OnMethod(
        clazz="org.f3.runtime.F3Base",
        method="notifyDependents$"
    )
    public static void onNotifyDependents(@Self Object obj) {
        String cn = name(classOf(obj));
        AtomicInteger ai = get(histo, cn);
        if (ai == null) {
            ai = newAtomicInteger(1);
            put(histo, cn, ai);
        } else {
            incrementAndGet(ai);
        }     
    }

    @OnTimer(10000)
    public static void print() {
        if (size(histo) != 0) {
            printNumberMap("Notification Histogram", histo);
            println("======================================");
        }
    }
}
