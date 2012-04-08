/*
 * Copyright 2001-2005 Sun Microsystems, Inc.  All Rights Reserved.
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

import org.junit.Test;
import junit.framework.Assert;

/**
 * Test for breakpoints set inside the onreplace trigger . 
 * @author srikalyanchandrashekar
 */
public class BreakAtOnReplaceTest extends JdbBase {
//@BeginTest OnReplace.f3
// var binder = 1.0;
// var bindee = bind binder on replace {
//        println("Within onreplace..");
//        println("Break Here");
// };
// function run() {
//     println("Begin");
//     for (i in [1..5]) {
//          binder = i as Number;
//     }
//     println("Test Ends here");
// }
//@EndTest

    @Test(timeout=5000)
    public void testOnReplace() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("OnReplace.f3");
            stop("in OnReplace:4");
            f3run();
            
            for (int i = 1;i <= 5;i++) {
                resumeToBreakpoint();
                Assert.assertTrue(contains("Within onreplace.."));
                list();
            }

            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
