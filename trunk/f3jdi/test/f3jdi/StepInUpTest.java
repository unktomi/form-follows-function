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
 *
 * @author srikalyanchandrashekar
 */
public class StepInUpTest extends JdbBase {

// @BeginTest Method.f3
// function simpleMethod():Void {
//       println("Inside simple method");
//       println("Dont break at this line");
// }
//
// function run():Void {
//      println("Beginning method calls ...");
//      simpleMethod();
//      println("Method calls Ends here");
// }
// @EndTest
/**
 * Step into another stack frame and step up to return to main stack frame
 */
    @Test(timeout=5000)
    public void testStepInUp() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Method.f3");
            stop("in Method:7");
            f3run();
            resumeToBreakpoint();
            list();

            next();
            list();

            step("in");//This is equivalent to step() command without arguments. Will go into simpleMethod() frame
            list();

            step("up");//This is step up command that brings the execution out of the current stackframe i.e simpleMethod().
            list();
            Assert.assertTrue(contains("Dont break at this line"));
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
