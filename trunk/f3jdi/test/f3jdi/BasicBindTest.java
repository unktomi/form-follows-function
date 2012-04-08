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
 * We break at the points after the binder's value is changed assert and resume operation. We make
 * sure execution pauses at all the breakpoints.
 * @author srikalyanchandrashekar
 */
public class BasicBindTest extends JdbBase {

// @BeginTest BasicBind.f3
// var binder = 1.0;
// var bindee = bind binder;
// function run() {
//     println("Initial bindee is {bindee}");
//     binder = 2.0;
//     println("Bindee is {bindee} now");
//     binder = 3.0;
//     println("Bindee is {bindee} now");
//     println("BasicBindTest Ends here");
// }
// @EndTest

    @Test(timeout=5000)
    public void testBasicBindVar() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("BasicBind.f3");
            stop("in BasicBind.f3$run$");
            stop("in BasicBind:6");
            stop("in BasicBind:8");
            stop("in BasicBind:9");

            f3run();

            resumeToBreakpoint();
            Assert.assertTrue(verifyNumValue("BasicBind.$bindee", 1.0));
            System.out.println("List 1");
            list();
            resumeToBreakpoint();
            Assert.assertTrue(verifyNumValue("BasicBind.$bindee", 2.0));
            System.out.println("List 2");
            list();
            resumeToBreakpoint();
            Assert.assertTrue(verifyNumValue("BasicBind.$bindee", 3.0));
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
