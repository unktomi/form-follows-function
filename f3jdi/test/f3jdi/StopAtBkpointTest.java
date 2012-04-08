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

import junit.framework.Assert;
import org.junit.Test;

/**
 * This Test is associated with JIRA VSGC-4419. All the waitForBreakpointEvent() calls
 * have been replaced by resumeToBreakpoint() call.
 * @author srikalyanchandrashekar
 */
public class StopAtBkpointTest extends JdbBase {

// @BeginTest StopAtBkpoint.f3
// var binder = 1.0;
// var bindee = bind binder;
// function run() {
//     println("Initial bindee is {bindee}");
//     binder = 2.0;
//     println("Bindee is {bindee} now");
//     binder = 3.0;
//     println("Bindee is {bindee} now");
//     println("StopAtBkpointTest Ends here");
// }
// @EndTest

    @Test(timeout=10000)
    public void testStopAtBkpoint() {
        try {
            resetOutputs();
            compile("StopAtBkpoint.f3");
            stop("in StopAtBkpoint.f3$run$");
            stop("in StopAtBkpoint:6");
            stop("in StopAtBkpoint:8");
            stop("in StopAtBkpoint:9");

            f3run();

            resumeToBreakpoint();
            System.out.println("List 1");
            list();

            resumeToBreakpoint();
            System.out.println("List 2");
            list();

            resumeToBreakpoint();
            System.out.println("List 3");
            list();

            resumeToBreakpoint();
            System.out.println("List 4");
            list();

            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
