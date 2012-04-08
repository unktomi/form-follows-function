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
import org.f3.jdi.F3VirtualMachine;
import org.f3.jdi.F3Wrapper;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.BreakpointEvent;
import org.junit.Test;
import junit.framework.Assert;

/**
 *
 * @author sundar
 */
public class LocalVarTest extends JdbBase {

// @BeginTest LocalVar.f3
// function run() {
//     println("LocalVar");
// }
// @EndTest

    @Test(timeout=5000)
    public void testHello1() {
        try {
            compile("LocalVar.f3");
            stop("in LocalVar.f3$run$");

            f3run();

            BreakpointEvent bkpt = resumeToBreakpoint();
            // We hide F3 synthetic variables.
            F3StackFrame frame = (F3StackFrame) bkpt.thread().frame(0);
            LocalVariable var = frame.visibleVariableByName("_$UNUSED$_$ARGS$_");
            Assert.assertNull(var);

            // underlying (java) frame object exposes this variable.
            StackFrame jframe = F3Wrapper.unwrap(frame);
            var = jframe.visibleVariableByName("_$UNUSED$_$ARGS$_");
            Assert.assertNotNull(var);

            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
