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

import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.TestCase;
import org.apache.tools.ant.filters.StringInputStream;

/**
 * CompilationErrorWrapper
 *
 * @author Brian Goetz
 */
public class CompilationErrorWrapper extends TestCase {
    private final File testFile;
    private final ByteArrayOutputStream err;

    public CompilationErrorWrapper(File testFile, ByteArrayOutputStream err) {
        super(testFile.toString());
        this.testFile = testFile;
        this.err = err;
    }

    protected void runTest() throws Throwable {
        TestHelper.dumpFile(new StringInputStream(new String(err.toByteArray())), "Compiler Output", testFile.toString());
        fail("Compilation errors in " + testFile);
    }
}
