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

import org.f3.jdi.F3SequenceReference;
import com.sun.jdi.StringReference;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author sundar
 */
public class VSGC4440Test extends JdbBase {

// @BeginTest VSGC4440Test.f3
// var seq : String[];
// function run() {
//     seq[0] = "hello";
//     func();
// }
// function func() {
//   println("func start");
// }
// @EndTest

    @Test
    public void testStringSequencesSet() {
        compile("VSGC4440Test.f3");
        
        stop("in VSGC4440Test.func");
        f3run();
        resumeToBreakpoint();
        F3SequenceReference seq = (F3SequenceReference) evaluate("VSGC4440Test.seq");
        // used to get NPE from this setValue call.
        seq.setValue(0, vm().mirrorOf("sun"));
        Assert.assertEquals(((StringReference)seq.getValue(0)).value(), "sun");
    }
}
