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

public abstract class BoundForOverNullableSingleton<T, PT> extends BoundForOverVaryingAbstract<T, PT> {

    public BoundForOverNullableSingleton(F3Object container, int forVarNum, int inductionSeqVarNum, boolean dependsOnIndex) {
        super(container, forVarNum, inductionSeqVarNum, dependsOnIndex);
    }

    protected void restoreValidState(int lowPart, int highPart) {
        //TODO: This maintains existing semantics -- remove for better performance
        inWholesaleUpdate = true;
        for (int ipart = lowPart; ipart < highPart; ++ipart) {
            get(ipart);
        }
        inWholesaleUpdate = false;
    }

    /***
    protected void restoreValidState(int lowPart, int highPart) {
        for (int ipart = lowPart; ipart < highPart; ++ipart) {
            F3ForPart part = getPart(ipart);
            part.varChangeBits$(partResultVarNum, VFLGS$STATE_MASK, VFLGS$STATE$VALID);
        }
    }
     * ***/

    /** Get the size of part ipart. */
    @Override
    protected int size(int ipart) {
        F3ForPart part = getPart(ipart);
        return part.get$(partResultVarNum) == null? 0 : 1;
    }

    /** Get the j'th item of part ipart -- which for a singleton, is just the item. */
    @Override
    protected T get(int ipart, int j) {
        F3ForPart part = getPart(ipart);
        return (T) part.get$(partResultVarNum);
    }
}

