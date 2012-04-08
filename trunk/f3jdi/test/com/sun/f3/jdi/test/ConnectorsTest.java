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

import org.f3.jdi.*;
import org.f3.jdi.connect.F3LaunchingConnector;
import org.f3.jdi.connect.F3ProcessAttachingConnector;
import org.f3.jdi.connect.F3RawLaunchingConnector;
import org.f3.jdi.connect.F3SharedMemoryAttachingConnector;
import org.f3.jdi.connect.F3SharedMemoryListeningConnector;
import org.f3.jdi.connect.F3SocketAttachingConnector;
import org.f3.jdi.connect.F3SocketListeningConnector;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Checks for F3-JDI connector classes and F3Bootstrap.
 *
 * @author sundar
 */
public class ConnectorsTest {
    @Test
    public void testF3Connectors() {
        LaunchingConnector conn = F3Bootstrap.virtualMachineManager().defaultConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3LaunchingConnector", conn.name());
        
        F3LaunchingConnector conn1 = new F3LaunchingConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3LaunchingConnector", conn1.name());
        Assert.assertEquals(true, conn1 instanceof LaunchingConnector);

        F3ProcessAttachingConnector conn2 = new F3ProcessAttachingConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3ProcessAttachingConnector", conn2.name());
        Assert.assertEquals(true, conn2 instanceof AttachingConnector);

        F3RawLaunchingConnector conn3 = new F3RawLaunchingConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3RawLaunchingConnector", conn3.name());
        Assert.assertEquals(true, conn3 instanceof LaunchingConnector);

        F3SocketAttachingConnector conn4 = new F3SocketAttachingConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3SocketAttachingConnector", conn4.name());
        Assert.assertEquals(true, conn4 instanceof AttachingConnector);

        F3SocketListeningConnector conn5 = new F3SocketListeningConnector();
        Assert.assertEquals("org.f3.jdi.connect.F3SocketListeningConnector", conn5.name());
        Assert.assertEquals(true, conn5 instanceof ListeningConnector);

        // Conditionally adding F3 shared mem connectors - because underlying platform shared
        // memory connectors are not available on all platforms
        if (F3SharedMemoryAttachingConnector.isAvailable()) {
            F3SharedMemoryAttachingConnector conn6 = new F3SharedMemoryAttachingConnector();
            Assert.assertEquals("org.f3.jdi.connect.F3SharedMemoryAttachingConnector", conn6.name());
            Assert.assertEquals(true, conn6 instanceof AttachingConnector);
        }

        if (F3SharedMemoryListeningConnector.isAvailable()) {
            F3SharedMemoryListeningConnector conn7 = new F3SharedMemoryListeningConnector();
            Assert.assertEquals("org.f3.jdi.connect.F3SharedMemoryListeningConnector", conn7.name());
            Assert.assertEquals(true, conn7 instanceof ListeningConnector);
        }
    }
}
