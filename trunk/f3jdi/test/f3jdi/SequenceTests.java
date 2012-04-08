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

import java.util.List;
import org.junit.Test;
import junit.framework.Assert;
/**
 *
 * @author ksrini
 */
public class SequenceTests extends JdbBase {
//@BeginTest Foo.f3
//var artists = [ 'John Denver', 'Eric Clapton', 'Paul Simon', 'Art Garfunkel'];
//println(artists);
//delete artists[0..2];
//println(artists);
//@EndTest

    @Test(timeout=5000)
    public void testSequence1() {
        try {
            compile("Foo.f3");
            stop("in Foo.f3$run$");
            f3run();
            resumeToBreakpoint();
            next();
            list();
            print("Foo.artists[0]");
            Assert.assertTrue(verifyValue("Foo.artists[0]", "John Denver"));
            next();
            next();
            Assert.assertTrue(verifyValue("Foo.artists[0]", "Art Garfunkel"));
            resumeToVMDeath();
            quit();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }
}
