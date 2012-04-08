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

package org.f3.jdi.connect;

import org.f3.jdi.F3VirtualMachine;
import org.f3.jdi.F3Wrapper;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author sundar
 */
public class F3RawLaunchingConnector extends F3Connector implements LaunchingConnector {
    public F3RawLaunchingConnector() {
        this(makePlatformConnector());
    }

    public F3RawLaunchingConnector(LaunchingConnector underlying) {
        super(underlying);
    }

    public F3VirtualMachine launch(Map<String, ? extends Argument> args)
            throws IOException, IllegalConnectorArgumentsException, VMStartException {
        return F3Wrapper.wrap(underlying().launch(args));
    }

    @Override
    protected LaunchingConnector underlying() {
        return (LaunchingConnector) super.underlying();
    }

    private static final String RAW_COMMANDLINE_LAUNCHER = "com.sun.tools.jdi.RawCommandLineLauncher";
    private static LaunchingConnector makePlatformConnector() {
        Class connectorClass = null;
        try {
            connectorClass = Class.forName(RAW_COMMANDLINE_LAUNCHER);
        } catch (ClassNotFoundException cnfe) {
        }
        if (connectorClass == null) {
            throw new RuntimeException("can not load class: " + RAW_COMMANDLINE_LAUNCHER);
        }
        try {
            return (LaunchingConnector) connectorClass.newInstance();
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
