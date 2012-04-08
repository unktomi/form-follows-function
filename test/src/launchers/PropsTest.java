/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ksrini
 */
public class PropsTest extends TestCase {

    static final String sep = File.separator;
    static final String psep = File.pathSeparator;

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
    static final String TESTNAME = "PropsTest";

    boolean verifyProp(List<String> oList, String prop, String strToFind) {
        for (String x : oList) {
            if (x.startsWith("SYSTEM-PROPERTIES")) {
                continue;
            }
            String[] elements = x.split(",");
            for (String y : elements) {
                y = y.trim();
                if (y.startsWith(prop) && y.contains(strToFind)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void testF3Properties() throws IOException {

        String testSrc = Utils.emitPropsTestJava(TESTNAME, null);
        //System.out.println("testSrc" + testSrc);
        Utils.createF3Jar(new File(Utils.workingDir, TESTNAME + ".jar"), testSrc);
        ArrayList<String> cmdsList = new ArrayList<String>();
        List<String> output = null;

        String libPath = sep + "tmp" + sep + "foo" +
                psep + ".." + sep + "xyz" + sep + "baz";
        // using cp
        cmdsList.add(Utils.f3Exe.getAbsolutePath());
        cmdsList.add("-Djava.library.path=" + libPath);
        cmdsList.add("-cp");
        cmdsList.add(TESTNAME + ".jar");
        cmdsList.add(TESTNAME);
        output = Utils.doExec(cmdsList);
        assertTrue(verifyProp(output, "java.library.path", libPath));

        // use jar cmd
        cmdsList.clear();
        cmdsList.add(Utils.f3Exe.getAbsolutePath());
        cmdsList.add("-Djava.library.path=" + libPath);
        cmdsList.add("-jar");
        cmdsList.add(TESTNAME + ".jar");
        output = Utils.doExec(cmdsList);
        assertTrue(verifyProp(output, "java.library.path", libPath));
    }
}
