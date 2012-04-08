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

import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.WatchpointRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * This TC tracks a script variable for all the accesses and changes.
 * @author srikalyanchandrashekar
 */
public class WatchAccessModificationTest extends JdbBase {

// @BeginTest WatchAll.f3
// var numVar = 1.0;
// function run() {
//     numVar = 2.0;
//     println("numVar is {numVar} now");
//     println("WatchAll Ends here");
// }
// @EndTest
/**
 * Checks the type of WatchpointEvent and validates type of field-being-watched
 * @param wpEvent
 */
    private void checkWatchPointEvent(WatchpointEvent wpEvent) {
        if (wpEvent instanceof AccessWatchpointEvent) {
            Assert.assertTrue(((AccessWatchpointEvent)wpEvent).valueCurrent().type().name().equals("float"));
            Assert.assertTrue(wpEvent.getClass().toString().equals("class org.f3.jdi.event.F3AccessWatchpointEvent"));
        } else if (wpEvent instanceof ModificationWatchpointEvent) {
            Assert.assertTrue(((ModificationWatchpointEvent)wpEvent).valueCurrent().type().name().equals("float"));
            Assert.assertTrue(wpEvent.getClass().toString().equals("class org.f3.jdi.event.F3ModificationWatchpointEvent"));
        }
    }
/**
 * Watch all means
 * 1) watch access
 * 2) watch modification of a field
 */
    @Test(timeout=5000)
    public void testWatchAll() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("WatchAll.f3");
            stop("in WatchAll.f3$run$");
            WatchpointRequest watchpointReq = watch("all WatchAll.$numVar");//watch access (checks for access only, and not modifications)
            f3run();
            WatchpointEvent wpEvent = resumeToWatchpoint();
            checkWatchPointEvent(wpEvent);
            
            list();
            step();
            list();
            wpEvent = resumeToWatchpoint();
            checkWatchPointEvent(wpEvent);
            step();
            list();
            wpEvent = resumeToWatchpoint();
            checkWatchPointEvent(wpEvent);
            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
