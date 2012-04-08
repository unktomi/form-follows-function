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

/**
 *
 * @author ksrini
 */
import java.util.List;
import org.junit.Test;
import junit.framework.Assert;

public class BasicTest extends JdbBase {

// @BeginTest Foo.f3
// var begin = "Foo.f3 says Hello";
// var msg1  = "Breakpoint 1";
// var msg2  = "Breakpoint 2";
// F3.println(begin);
// F3.println(msg1);
// F3.println(msg2);
// @EndTest

    @Test(timeout=5000)
    public void testHello1() {
        try {
            compile("Foo.f3");
            stop("in Foo.f3$run$");

            f3run();

            resumeToBreakpoint();

            Assert.assertTrue(verifyValue("Foo.begin", "Foo.f3 says Hello"));
            Assert.assertTrue(verifyValue("Foo.msg1", "Breakpoint 1"));
            Assert.assertTrue(verifyValue("Foo.msg2", "Breakpoint 2"));

            where();
            Assert.assertTrue(contains("Foo.f3$run$ (Foo.f3:4)"));

     
            next();
            Assert.assertTrue(lastContains("Foo.f3 says Hello"));

            next();
            Assert.assertTrue(lastContains("Breakpoint 1"));
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }

// @BeginTest Bar.f3
// public function run(args: String[]):Void {
//     println("Bar.f3 says Hello");
//     for (i in args) {
//         println("{i}");
//     }
//     println("This is the end, my friend, this is the end");
// }
// @EndTest

    @Test(timeout=500000)
    public void testHello2() {
        try {
            compile("Bar.f3");
            stop("in Bar.f3$run$");

            f3run("ONE", "TWO", "THREE");
            resumeToBreakpoint();
            where();
            Assert.assertTrue(contains("Bar.f3$run$ (Bar.f3:2)"));

            clearOutput();
            next();
            Assert.assertTrue(lastContains("Bar.f3 says Hello"));

            next();
            Assert.assertTrue(verifyValue("i", "ONE"));
            
            next(); next();
            Assert.assertTrue(verifyValue("i", "TWO"));

            next(); next();
            Assert.assertTrue(verifyValue("i", "THREE"));

            resumeTo("Bar",6);
            clearOutput();
            step();
            where();
            Assert.assertTrue(contains("Builtins.java"));
            List<String> olist = getOutputAsList();
            Assert.assertTrue(olist.get(1).trim().equals("[2] Bar.f3$run$ (Bar.f3:6)"));

            resumeToVMDeath();
            quit();

        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
