/*
 * Copyright 2001-2005 Sun Microsystems, Inc.  All Rights Reserved.
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

import org.junit.Test;
import junit.framework.Assert;

/**
 *
 * @author srikalyanchandrashekar
 */
public class HeteroObjectSequenceTest extends JdbBase {
//@BeginTest ObjSequence.f3
// var objSequence = [];
// function run() {
//      var mapObj:java.util.Map = new java.util.HashMap();
//      var intObj:java.lang.Integer = new java.lang.Integer(0);
//      insert mapObj into objSequence;
//      println(objSequence);
//      insert intObj into objSequence;
//      println(objSequence);
//      delete objSequence[0];
//      println(objSequence);
// }
//@EndTest

    @Test(timeout=5000)
    public void testObjSequence() {
        try {
            //resetOutputs();//Uncomment this if you want to see the output on console
            compile("ObjSequence.f3");
            stop("in ObjSequence:7");
            stop("in ObjSequence:9");
            stop("in ObjSequence:10");
            f3run();
            resumeToBreakpoint();
            list();
            Assert.assertTrue(verifyValue("ObjSequence.objSequence[0]", "{}"));
            resumeToBreakpoint();
            list();
            Assert.assertTrue(verifyValue("ObjSequence.objSequence", "[ {}, 0 ]"));
            resumeToBreakpoint();
            list();
            Assert.assertTrue(verifyValue("ObjSequence.objSequence[0]", "0"));
            cont();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
