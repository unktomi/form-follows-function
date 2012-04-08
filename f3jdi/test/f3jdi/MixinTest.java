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
 *   M(mixin)        B
 *    \            / |
 *     \__________/  |
 *          |        |
 *          |        |
 *          C        A
 * There  is method called foo() defined in M , B, A. When you invoke foo() in M, B
 * from another method in C say bar() its OK, but you cannot invoke foo() in A from
 * bar() in C.
 * @author srikalyanchandrashekar
 */
public class MixinTest extends JdbBase {

// @BeginTest Mixin.f3
//var mFooCalled = "false";
//var bFooCalled = "false";
//mixin class M {
//    public function foo() : Void {
//        mFooCalled = "true";
//        println("M foo");
//    }
//}
//
//class B {
//    public function foo() : Void {
//        bFooCalled = "true";
//        println("B foo");
//    }
//}
//
//class A extends B {
//    override public function foo() : Void {
//        println("A foo");
//    }
//}
//
//class C extends B, M {
//    public function bar() : Void {
//        B.foo();   //  LEGAL
//        M.foo();   //  LEGAL
//        //A.foo();   //  ILLEGAL: Not direct superclass or parent
//    }
//}
// function run() {
//     var tmp:C = C {};
//     tmp.bar();
//     println("Test ends here..");
// }
// @EndTest
    public static String B_FOO = "Mixin.$bFooCalled";
    public static String M_FOO = "Mixin.$mFooCalled";
    @Test(timeout=5000)
    public void testMixin() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Mixin.f3");
            stop("in Mixin.f3$run$");
            stop("in Mixin:32");
            stop("in Mixin$C:25");
            stop("in Mixin$C:26");
            stop("in Mixin$C:28");

            f3run();

            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();
            resumeToBreakpoint();
            list();

            resumeToBreakpoint();
            list();
            print(B_FOO);//This will print the value of F3 variable
            //System.out.println(verifyValue(B_FOO, "true"));
            Assert.assertTrue(contains(B_FOO + " = \"true\""));

            resumeToBreakpoint();
            list();
            print(M_FOO);//This will print the value of F3 variable
            //System.out.println(verifyValue(M_FOO, "true"));
            Assert.assertTrue(contains(M_FOO + " = \"true\""));

            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
