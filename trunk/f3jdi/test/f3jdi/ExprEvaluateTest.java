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

import org.f3.jdi.F3IntegerType;
import org.f3.jdi.F3IntegerValue;
import com.sun.jdi.Value;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Evaluate a function call and assert the return value, type.
 * @author srikalyanchandrashekar
 */
public class ExprEvaluateTest extends JdbBase {
// @BeginTest Eval.f3
//  function fact(n:Integer):Integer {
//     if ( n <= 0 ) {
//        return 1;
//     } else {
//        return n * fact(n - 1);
//     }
//  }
// function run() {
//     println("fact(3) is 6 but we got {fact(3)}");
//     println("fact(4) is 24 but we got {fact(4)}");
// }
// @EndTest
    @Test(timeout=5000)
    public void testEval() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("Eval.f3");
            stop("in Eval.f3$run$");
            stop("in Eval:10");
            f3run();
            resumeToBreakpoint();
            list();
            Value val = evaluate("Eval.fact(3)");
            Assert.assertTrue(val instanceof F3IntegerValue && val.type() instanceof F3IntegerType);
            Assert.assertTrue(((F3IntegerValue)val).value() == 6);
            Assert.assertTrue(val.type().name().equals("int"));
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
