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

package org.f3.runtime.sequence;
import org.f3.runtime.F3Object;

public abstract class BoundForOverSequence<T, PT> extends BoundForOverVaryingAbstract<T, PT> {

    public BoundForOverSequence(F3Object container, int forVarNum, int inductionSeqVarNum, boolean dependsOnIndex) {
        super(container, forVarNum, inductionSeqVarNum, dependsOnIndex);
    }

    protected void restoreValidState(int lowPart, int highPart) {
        //TODO: This maintains existing semantics -- remove for better performance
        inWholesaleUpdate = true;
        for (int ipart = lowPart; ipart < highPart; ++ipart) {
            size(ipart);
        }
        inWholesaleUpdate = false;
    }

    /** Get the size of part ipart. */
    protected int size(int ipart) {
        F3ForPart part = getPart(ipart);
        return part.size$(partResultVarNum); // sequence version
    }

    /** Get the j'th item of part ipart. */
    protected T get(int ipart, int j) {
        F3ForPart part = getPart(ipart);
        return (T) part.elem$(partResultVarNum, j);
    }
}

