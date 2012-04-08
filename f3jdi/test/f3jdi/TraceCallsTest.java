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

package f3jdi;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Verify if the trace on method calls are being received properly.
 * @author srikalyanchandrashekar
 */
public class TraceCallsTest extends JdbBase {

// @BeginTest Method.f3
// function methodOne():Void {
//      println("methodOne call");
//      println("methodOne end");
// }
// function run():Void {
//      println("Start");
//      methodOne();
//      println("End");
// }
// @EndTest
/**
 * Convenient method that compares the method name (extracted from event) with the given set of names.
 * @param event
 * @param methodName1
 * @param methodName2
 */
    private void checkEntryExit(Event event, String methodName1, String methodName2) {
            if (event instanceof MethodEntryEvent) {
                System.out.println("Starting method = " + ((MethodEntryEvent)event).method().toString());
                Assert.assertTrue(((MethodEntryEvent)event).method().toString().equals(methodName1) || ((MethodEntryEvent)event).method().toString().equals(methodName2));
            }else if (event instanceof MethodExitEvent) {
                System.out.println("Exiting method = " + ((MethodExitEvent)event).method().toString());
                Assert.assertTrue(((MethodExitEvent)event).method().toString().equals(methodName1) || ((MethodExitEvent)event).method().toString().equals(methodName2));
            }
    }

/**
 * Trace entry and exit of method calls.
 */
//TODO: Please uncomment the below annotation when the TC is fixed.
    @Test(timeout=5000)
    public void testTrace() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Method.f3");
            stop("in Method:7");
            stop("in Method:4");
            stop("in Method:8");
            f3run();
            resumeToBreakpoint();
            trace("go methods");//Trace all the method's entry and exit

            where();
            list();
            Event event = resumeToAnyEvent();//This is definitely start of methodOne()
           checkEntryExit(event, "Method.methodOne()", "f3.lang.Builtins.println(java.lang.Object)");

            where();
            list();

            event = resumeToAnyEvent();//This is exit of either methodOne() or println() because the breakpoint is at end of methodOne()
           checkEntryExit(event, "Method.methodOne()", "f3.lang.Builtins.println(java.lang.Object)");
            where();
            list();
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
