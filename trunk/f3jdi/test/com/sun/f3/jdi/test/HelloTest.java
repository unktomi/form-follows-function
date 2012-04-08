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
package org.f3.jdi.test;

import org.f3.jdi.F3StackFrame;
import com.sun.jdi.event.BreakpointEvent;
import org.junit.Test;
import junit.framework.Assert;

/**
 * A simple F3 target test - just tests breakpoint inside "run" method.
 *
 * @author sundar
 */
public class HelloTest extends F3TestBase {
    private static String targetClassName = "org.f3.jdi.test.target.HelloTarget";

    public HelloTest() {
        super(targetClassName);
    }

    @Test
    public void testHello() {
        try {
            startTests();
        } catch (Exception exp) {
            Assert.fail(exp.getMessage());
        }
    }

    protected void runTests() throws Exception {
        startToMain();

        // go to "run" method of F3 class
        BreakpointEvent bpe = resumeTo(targetClassName, f3RunMethodName(),
                f3RunMethodSignature());

        mainThread = bpe.thread();
        if (!mainThread.frame(0).location().method().name().equals(f3RunMethodName())) {
            failure("frame failed");
        }

        Assert.assertEquals(true, mainThread.frame(0) instanceof F3StackFrame);
        Assert.assertEquals(true, ((F3StackFrame)mainThread.frame(0)).isF3Frame());

        /*
         * resume until end
         */
        listenUntilVMDisconnect();

        /*
         * deal with results of test
         */
        if (!testFailed) {
            println("HelloTest: passed");
        } else {
            throw new Exception("HelloTest: failed");
        }
    }
}
