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

import org.f3.jdi.connect.F3LaunchingConnector;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import java.util.Map;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for JDI testing. This class creates JDI connection and attaches it
 * to a target VirtualMachine. A subclass can have particular tests on the JDI VM.
 *
 * @author sundar
 */
public class JDITestBase {
    private VirtualMachine vm;

    public JDITestBase() {
    }

    @Before
    public void setUp() {
        vm = launchVM();
    }

    @After
    public void tearDown() {
        if (vm != null) {
            vm.dispose();
            vm = null;
        }
    }

    // Override this method in subclass to create a different JDI VM.
    // This implementation uses F3LaunchingConnector to get JDI VM.
    protected VirtualMachine launchVM() {
        F3LaunchingConnector conn = new F3LaunchingConnector();
        Map<String, Connector.Argument> args = conn.defaultArguments();
        Connector.StringArgument arg = (Connector.StringArgument) args.get("main");
        // dummy main class
        arg.setValue("Main");
        try {
            return conn.launch(args);
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    protected VirtualMachine getVM() {
        return vm;
    }
}
