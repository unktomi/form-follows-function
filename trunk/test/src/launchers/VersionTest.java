
/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package launchers;

import junit.framework.TestCase;
import java.io.IOException;
import java.util.ArrayList;
import javax.script.ScriptException;

import org.f3.runtime.SystemProperties;

public class VersionTest extends TestCase {
   private static final String VERSION_PATTERN_TAIL = "[1-9].[0-9].*";

    // The golden values
    private static final String versionProp =
            SystemProperties.getProperty("f3.version");
    private static final String fullversionProp =
            SystemProperties.getProperty("f3.runtime.version");
       /*
     * perform the mundane setups once.
     */
    @Override
    public void setUp() throws Exception {
        Utils.init();
    }

    /*
     * clean up and be green.
     */
    @Override
    public void tearDown() {
        Utils.reset();
    }

    /*
     * test the runtime version string
     */
    public void testF3Properties() throws ScriptException, IOException {
        // verify the strings are conformant.
        assertTrue(versionProp.matches("^" + VERSION_PATTERN_TAIL));
        assertTrue(fullversionProp.matches("^" + VERSION_PATTERN_TAIL));

        // Now use the f3 method to verify the build results are the same
        String f3version = Utils.getVersionPropFromF3(false);
        String f3fullversion = Utils.getVersionPropFromF3(true);
        assertEquals(versionProp, f3version);
        assertEquals(fullversionProp, f3fullversion);
    }
    
    /*
     * test for f3, using exact match
     */
    public void testF3Version() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "f3 " + versionProp, false));
    }

    public void testF3Fullversion() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3Exe.toString());
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "f3 full version \"" +
                fullversionProp + "\"", false));
    }

    /*
     * test for f3c, using regex pattern
     */
    public void testF3cVersion() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "^f3c " + VERSION_PATTERN_TAIL, true));
    }

    public void testF3cFullversion() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3cExe.toString());
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "^f3c full version \"" +
                VERSION_PATTERN_TAIL + "\"", true));
    }

    /*
     * test for f3docm using regex pattern
     */
    public void testF3DocVersion() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3docExe.toString());
        cmdsList.add("-version");
        assertTrue(Utils.checkExec(cmdsList, "^f3doc " +
                VERSION_PATTERN_TAIL, true));
    }

    public void testF3DocFullversion() {
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(Utils.f3docExe.toString());
        cmdsList.add("-fullversion");
        assertTrue(Utils.checkExec(cmdsList, "^f3doc full version \"" +
                VERSION_PATTERN_TAIL + "\"", true));
    }
}
