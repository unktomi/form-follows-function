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
 * Have multiple stack frames through function calls, jumping from one to another and unwinding all
 * the calls in the last. Make sure the debugger stops at expected points and
 * verify the location and its attributes.
 * @author srikalyanchandrashekar
 */
public class JumpLocationsTest extends JdbBase {

// @BeginTest Jump.f3
// function foo3():Void {
//     println("In foo3()");
//     println("Unwinding");
// };
// function foo2():Void {
//     println("In foo2()");
//     foo3();
// };
// function foo1():Void {
//     println("In foo1()");
//     foo2();
// };
// function run() {
//     println("Begin Test");
//     foo1();
//     println("Ends here");
// }
// @EndTest
    @Test(timeout=5000)
    public void testJumpLocations() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Jump.f3");
            stop("in Jump:11");
            stop("in Jump:7");
            stop("in Jump:3");
            f3run();
            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();
            wherei();
            Assert.assertTrue(contains("  [1] Jump.foo3 (Jump.f3:3)"));
            Assert.assertTrue(contains("  [2] Jump.foo2 (Jump.f3:7)"));
            Assert.assertTrue(contains("  [3] Jump.foo1 (Jump.f3:11)"));
            Assert.assertTrue(contains("  [4] Jump.f3$run$ (Jump.f3:15)"));
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
