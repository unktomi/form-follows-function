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
import com.sun.btrace.AnyType;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import org.f3.runtime.F3Base;

/**
 * This script prints count$() value of all F3Base subclasses.
 * (note: count$() returns number of F3 instance variables in a class).
 *
 * @author A. Sundararajan
 */
@BTrace public class InstVarCount {
    // classname-to-count$ map
    private static Map<String, Integer> instVarCounts = new HashMap<String, Integer>();

    // a file that is used to sigal event to target VM to dump the data
    private static File evt = new File(System.getProperty("user.home"), "InstVarCount");

    @OnMethod(
        clazz="org.f3.runtime.F3Base",
        method="<init>"
    )
    public static void onNewF3Object(@Self F3Base self, AnyType[] args) {
        String className = self.getClass().getName();
        if (! instVarCounts.containsKey(className)) {
            instVarCounts.put(className, self.count$());
        }
    }

    @OnTimer(1000)
    public static void onTimer() {
        // on timer, check if the user has created the file
        if (evt.exists()) {
            // print the data
            printMap(instVarCounts);
            // delete the "event" file
            evt.delete();
        }
    }
}
