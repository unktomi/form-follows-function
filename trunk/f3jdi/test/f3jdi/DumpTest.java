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

import junit.framework.Assert;
import org.junit.Test;

/**
 * Define a a set of variables of type number, sequence, object literal and assert
 * their structure, content and type .
 * @author srikalyanchandrashekar
 */
public class DumpTest extends JdbBase {

// @BeginTest DumpVar.f3
// var globalV = 1.0;
// var numSeq = [ 23, 34, 45, 56 ];
// class SomeJunk {
//     var x = "NothingHere";
// }
// var objLiteral = SomeJunk {};
// function run() {
//     println("globalV is {globalV}");
//     globalV = 2.0;
//     println("End reached");
// }
// @EndTest
    private static String[] expectedStrSeq = {"DumpVar.globalV = 2.0",
                                                "DumpVar.numSeq = {" ,
                                                "array: instance of int[4]",
                                                "org.f3.runtime.sequence.ArraySequence.gapStart: 4" ,
                                                "org.f3.runtime.sequence.ArraySequence.gapEnd: 4",
                                                "org.f3.runtime.sequence.ArraySequence.DEFAULT_SIZE: 16",
                                                "org.f3.runtime.sequence.ArraySequence.sharing: 1",
                                                "org.f3.runtime.sequence.AbstractSequence.ti: instance of org.f3.runtime.NumericTypeInfo",
                                                "}",
                                                "DumpVar.objLiteral = {",
                                                "x: \"NothingHere\"",
                                                "}"
    };

    @Test(timeout=5000)
    public void testDump() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("DumpVar.f3");
            stop("in DumpVar:10");
            f3run();
            resumeToBreakpoint();
            list();
            dump("DumpVar.globalV");
            dump("DumpVar.numSeq");
            dump("DumpVar.objLiteral");
            Assert.assertTrue(containsInTandem(expectedStrSeq));
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
