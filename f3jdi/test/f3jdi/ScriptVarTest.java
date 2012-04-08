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

import org.f3.jdi.F3StackFrame;
import org.f3.jdi.F3Wrapper;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.BreakpointEvent;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author srikalyanchandrashekar
 */
public class ScriptVarTest extends JdbBase {

// @BeginTest ScriptVar.f3
// var globalV = 1.0;
// function run() {
//     println("globalV is {globalV}");
//     globalV = 2.0;
//     println("End reached");
// }
// @EndTest

      @Test
      public void noop() {
         // NOTE: satisfy junit so that no spurious errors are thrown
         // remove this method/test when the real test gets fixed
         // below.
      }


//    @Test(timeout=10000)
    public void testScriptVar() {
        try {
            resetOutputs();
            compile("ScriptVar.f3");
            stop("in ScriptVar.f3$run$");

            f3run();

            resumeToBreakpoint();

            //Assert.assertTrue(verifyNumValue("ScriptVar.globalV", 1.0));

            list();
//            Assert.assertTrue(contains("ScriptVar.f3$run$ (ScriptVar.f3:2)"));


            next();
            list();
            //next();
            //Assert.assertTrue(lastContains("globalV is 1.0"));

            next();
            list();
            
//            list();
            next();
            //Assert.assertTrue(verifyValue("ScriptVar.globalV", "1.0"));
            //cont();
            //quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
