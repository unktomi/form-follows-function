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
 * Make a series of nested method calls and pop off the most recent stackframe
 * from the call stack and validate you are on the right stackframe.
 * @author srikalyanchandrashekar
 */
public class MethodUnwindTest extends JdbBase {

// @BeginTest Unwind.f3
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
    public void testUnwind() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Unwind.f3");
            stop("in Unwind:11");
            stop("in Unwind:7");
            stop("in Unwind:2");
            stop("in Unwind:3");
            f3run();
            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();
            wherei();

            pop();//Pop off the stack frame foo3

            list();
            wherei();
            Assert.assertTrue(contains("  [2] Unwind.foo2 (Unwind.f3:7)"));
            Assert.assertTrue(contains("  [3] Unwind.foo1 (Unwind.f3:11)"));
            Assert.assertTrue(contains("  [4] Unwind.f3$run$ (Unwind.f3:15)"));

            resumeToBreakpoint();
            list();
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
