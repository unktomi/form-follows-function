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

package framework;

import java.util.Set;
import junit.framework.TestCase;

/**
 * Test case used by F3CompilerTest to complain if test files are marked as neither test nor subtest
 *
 * @author Brian Goetz
 */
public class OrphanTestFinder extends TestCase {
    private final Set<String> orphanFiles;

    public OrphanTestFinder(Set<String> orphanFiles) {
        super("test");
        this.orphanFiles = orphanFiles;
    }

    public void test() {
        if (orphanFiles == null || orphanFiles.size() == 0)
            return;

        StringBuffer sb = new StringBuffer();
        String NL = System.getProperty("line.separator");
        sb.append(orphanFiles.size());
        sb.append(" files found with neither @test nor @subtest: ");
        sb.append(NL);
        for (String s : orphanFiles) {
            sb.append("  ");
            sb.append(s);
            sb.append(NL);
        }
        fail(sb.toString());
    }
}
