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
 * Associated bug id VSGC-4455 .Define a bound method , associate it to a variable
 * and make sure it is called automatically whenever deemed necessary , if not
 * then its a BUG!
 * @author srikalyanchandrashekar
 */
public class BoundTest extends JdbBase {

// @BeginTest Bound.f3
//var x = 1;
//bound function fooBar(y:Integer, z:Integer):Integer {
//      var u = y;
//      var v = z;
//      x + u;
//}
// function run() {
//        var a = 4;
//        var b = 5;
//        println("Initializing c");
//        var c = bind fooBar(a, b) on replace {
//           println("Bound function called");
//        };
//        println("Setting x=5");
//        x=5;//bound Function should be called here
//        println("Setting a=5");
//        a=5;//bound Function should be called here
//        println("Setting b=7");
//        b=7;//bound Function should not be called here
// }
// @EndTest
    @Test(timeout=5000)
    public void testBound() {
        try {
            resetOutputs();//Uncomment this if you want to see the output on console
            compile("Bound.f3");
            //TODO: Please uncomment the following 3 lines when the bug is been fixed
//            stop("in Bound:13");
//            stop("in Bound:14");
//            stop("in Bound:15");

            f3run();
            //TODO: Please uncomment the following 6 lines when the bug is been fixed
//            resumeToBreakpoint();
//            list();
//            resumeToBreakpoint();
//            list();
//            resumeToBreakpoint();
//            list();

            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
