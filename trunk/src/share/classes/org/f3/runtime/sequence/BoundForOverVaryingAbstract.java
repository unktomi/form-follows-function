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

public abstract class BoundForOverVaryingAbstract<T, PT> extends BoundFor<T, PT> {

    protected int[] cumulatedLengths;
    private int cacheIndex;
    private int cachePart;

    public BoundForOverVaryingAbstract(F3Object container, int forVarNum, int inductionSeqVarNum, boolean dependsOnIndex) {
        super(container, forVarNum, inductionSeqVarNum, dependsOnIndex);
    }


    /** Get the size of part ipart. */
    protected abstract int size(int ipart);

    /** Get the j'th item of part ipart. */
    protected abstract T get(int ipart, int j);
    
    public int size() {
        initializeIfNeeded();
        if (state != BOUND_FOR_STATE_PARTS_STABLE || pendingTriggers > 0) {
            return cumLength(numParts);
        } else {
            return sizeAtLastTrigger;
        }
    }

    @Override
    protected int decacheLengths() {
        cachePart = 0;
        int previousSize = sizeAtLastTrigger;
        if (cumulatedLengths == null ||
                    cumulatedLengths.length < numParts ||
                    cumulatedLengths.length > (numParts+10)) {
            cumulatedLengths = new int[numParts];
        }
        sizeAtLastTrigger = calculateCumLength(numParts, cumulatedLengths);
        return previousSize;
    }

    private int calculateCumLength(int ipart, int[] lengths) {
        inWholesaleUpdate = true;
        int sum = 0;
        for (int ips = 0; ips < ipart; ++ips) {
            sum += size(ips);
            if (lengths != null) {
                cumulatedLengths[ips] = sum;
            }
        }
        inWholesaleUpdate = false;
        return sum;
    }

    protected int cumLength(int ipart) {
        if (ipart <= 0) {
            return 0;
        } else if (state != BOUND_FOR_STATE_PARTS_STABLE || pendingTriggers > 0) {
            // Calculate without touching cache
            return calculateCumLength(ipart, null);
        } else {
            return cumulatedLengths[ipart - 1];
        }
    }

    protected int cachedCumLength(int ipart) {
        if (ipart <= 0) {
            return 0;
        } else {
            return cumulatedLengths[ipart - 1];
        }
    }

    public T get(int index) {
        initializeIfNeeded();

        if (index < 0)
            return null;

        int i = 0;
        int cumPrev = 0;
        if (state != BOUND_FOR_STATE_PARTS_STABLE || pendingTriggers > 0) {
            // Calculate without touching cache
            for (;; i++) {
                if (i >= numParts) {
                    return null;
                }
                int cum = cumLength(i + 1);
                if (index < cum) {
                    return get(i, index - cumPrev);
                }
                cumPrev = cum;
            }
        }
        // FIXME - should use binary search if not in cache.
        if (index >= cacheIndex) {
            i = cachePart;
            cumPrev = cumLength(i);
        }
        for (;; i++) {
            if (i >= numParts)
                return null;
            int cum = cumLength(i+1);
            if (index < cum) {
                cachePart = i;
                cacheIndex = cumPrev;
                return get(i, index-cumPrev);
            }
            cumPrev = cum;
        }
    }
}

