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
 * Define a Base and subclass it, override the methods in it. Then for 2 different
 * instances of same object type's methods are invoked and asserted for.
 * @author srikalyanchandrashekar
 */
public class BasicInheritenceTest extends JdbBase {

// @BeginTest BasicInheritence.f3
// class Sooper {
//   var a = 0;
//   function printA():Void {
//       println("print within Sooper and a = {a}");
//   }
// }
// class Sub extends Sooper {
//   override var a = 4;
//   override function printA():Void {
//       println("print within Sub and a = {a}");
//   }
// }
// function run() {
//     var tmp:Sooper = Sub {};
//     tmp.printA();
//     tmp = Sooper {};
//     tmp.printA();
//     println("Test ends here..");
// }
// @EndTest
//    public static String SUB_CONTENT = "print within Sub and a = 4";
//    public static String SOOPER_CONTENT = "print within Sooper and a = 0";
    public static String SUB_CONTENT = "$Sooper$a = 4";
    public static String SOOPER_CONTENT = "$Sooper$a = 0";
    @Test(timeout=5000)
    public void testBasicInheritence() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("BasicInheritence.f3");
            stop("in BasicInheritence.f3$run$");
            stop("in BasicInheritence$Sooper:5");
            stop("in BasicInheritence:16");
            stop("in BasicInheritence$Sub:11");
            stop("in BasicInheritence:18");

            f3run();

            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            print("$Sooper$a");//This is subclass's a
            list();
            Assert.assertTrue(contains(SUB_CONTENT));
            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            print("$Sooper$a");//This is sooperclass's a
            list();
            Assert.assertTrue(contains(SOOPER_CONTENT));
            resumeToBreakpoint();
            list();
            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
