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

package org.f3.runtime.util;

/**
 * MathUtil
 *
 * @author Brian Goetz
 */
public class MathUtil {
    private static final byte[] lookupTable = new byte[256];

    public static int log2(/* unsigned */ int x) {
        if (x == 0)
            return 0;
        else {
            /* Compute number of leading zeros for an unsigned int; from Hacker's Delight, fig 5-6. */
            int n = 1;
            if ((x >>> 16) == 0) { n += 16; x <<= 16; }
            if ((x >>> 24) == 0) { n += 8; x <<= 8; }
            if ((x >>> 28) == 0) { n += 4; x <<= 4; }
            if ((x >>> 30) == 0) { n += 2; x <<= 2; }
            n -= (x >>> 31);
            return 31 - n;
        }
    }
}
