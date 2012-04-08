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
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.ExceptionRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author srikalyanchandrashekar
 */
public class ExceptionCatchTest extends JdbBase {

// @BeginTest ExceptionBreak.f3
//  function simpleArith() {
//     throw (new java.lang.Exception("Exception of no value"));
//  }
// function run() {
//       simpleArith();
//       println("No Catch block " );
// }
// @EndTest
/**
 * We call this method just before throwing an Exception and make sure it ExceptionEvent.
 */
    private void checkExceptionEvent() {
        Event event = resumeToAnyEvent();
        System.out.println("Exception request is " + event);
        Assert.assertTrue(event instanceof ExceptionEvent || event instanceof ThreadStartEvent);
        list();
    }
/**
 * Try to throw an exception without a try catch block. Tell the debugger to catch the Exception
 * which anyways breaks after the exception event is caught, then we analyze it.
 */
    @Test(timeout=5000)
    public void testExceptionBreak() {
        try {
            resetOutputs();//Uncomment this if you want to see the output on console
            compile("ExceptionBreak.f3");
            stop("in ExceptionBreak:2");
            stop("in ExceptionBreak:6");
            f3run();
            ExceptionRequest exceptionReq = catchException("java.lang.Exception");
            resumeToBreakpoint();//Change the above call to catchException("somejunk") ,this TC doesn't work.
            list();

            checkExceptionEvent();// This should be ExceptionEvent or ThreadStartEvent

            checkExceptionEvent();// This should be ExceptionEvent or ThreadStartEvent

            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
