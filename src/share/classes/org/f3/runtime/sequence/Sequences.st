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

package org.f3.runtime.sequence;

import java.util.*;

import org.f3.runtime.AssignToBoundException;
import org.f3.runtime.NumericTypeInfo;
import org.f3.runtime.TypeInfo;
import org.f3.runtime.Util;
import org.f3.runtime.F3Object;

/**
 * Sequences -- static helper methods for constructing derived sequences. Implements heuristics for reducing time and
 * space overhead, such as flattening complicated sequence trees where appropriate and ignoring null transformations
 * (such as appending an empty sequence). These methods are generally preferable to the constructors for
 * CompositeSequence, FilterSequence, SubSequence, etc, because they implement heuristics for sensible time-space
 * tradeoffs.
 *
 * @author Brian Goetz
 */
public final class Sequences extends SequencesBase {

    // Inhibit instantiation
    private Sequences() { }

$TEMPLATE_FROM$

$PREFIX,BOXED,PRIM:{T,Tboxed,Tprim|
    public static $Tprim$ getAs$T$FromNewElements(F3Object instance, int varNum, int loIndex, int inserted, int k) {
        if (k >= inserted)
            k = -1;
        else if (k >= 0)
            k += loIndex;
        return instance.getAs$T$\$(varNum, k);
    }

    private static Sequence<? extends $Tboxed$> replaceSlice(Sequence<? extends $Tboxed$> oldValue, $Tprim$ newValue, int startPos, int endPos/*exclusive*/) {
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;

        /* Probably not a worthwhile optimization.
        if (endPos == startPos+1 && newValue == oldValue.getAs$T$(startPos)) {
            return oldValue;
        }
        */
        $T$ArraySequence arr;
        if (! (oldValue instanceof $T$ArraySequence)
            || (arr = ($T$ArraySequence) oldValue).isShared()) {
            arr = new $T$ArraySequence(oldValue);
            arr.incrementSharing();
        }
        arr.replace(startPos, endPos, newValue, false);
        return arr;
    }

    public static void replaceSlice(F3Object instance, int varNum, $Tprim$ newValue, int startPos, int endPos/*exclusive*/) {
        int flags = instance.getFlags\$(varNum);
		boolean wasUninitialized = (flags & F3Object.VFLGS\$INITIALIZED_STATE_BIT) == 0;
        instance.setFlags\$(varNum, flags | F3Object.VFLGS\$INIT\$INITIALIZED_DEFAULT);
        if ((flags & F3Object.VFLGS\$IS_BOUND_READONLY) == F3Object.VFLGS\$IS_BOUND_READONLY) {
            throw new AssignToBoundException("Cannot mutate bound sequence");
        }
        Sequence<? extends $Tboxed$> oldValue = (Sequence<? extends $Tboxed$>) instance.get\$(varNum);
        while (oldValue instanceof SequenceProxy) {
            SequenceProxy sp = (SequenceProxy) oldValue;
            instance = sp.instance();
            varNum = sp.varNum();
            instance.varChangeBits\$(varNum, 0, F3Object.VFLGS\$INIT\$INITIALIZED_DEFAULT);
            oldValue = (Sequence<? extends $Tboxed$>) instance.get\$(varNum);
        }
        int oldSize = oldValue.size();
        if (startPos < 0)
            startPos = 0;
        else if (startPos > oldSize)
            startPos = oldSize;
        if (endPos > oldSize)
            endPos = oldSize;
        else if (endPos < startPos)
            endPos = startPos;

        if (endPos != startPos+1 || newValue != oldValue.getAs$T$(startPos) ||
            wasUninitialized) {
            instance.invalidate\$(varNum, startPos, endPos, 1, F3Object.PHASE_TRANS\$CASCADE_INVALIDATE);
            $T$ArraySequence arr;
            if (! (oldValue instanceof $T$ArraySequence)
                || (arr = ($T$ArraySequence) oldValue).isShared()) {
                arr = new $T$ArraySequence(oldValue);
                arr.incrementSharing();
            }
            arr.replace(startPos, endPos, newValue, true);
            if (arr != oldValue)
                instance.seq\$(varNum, arr);
            instance.invalidate\$(varNum, startPos, endPos, 1, F3Object.PHASE_TRANS\$CASCADE_TRIGGER);
        }
    }

    public static Sequence<? extends $Tboxed$> set(Sequence<? extends $Tboxed$> oldValue, $Tprim$ newValue, int index) {
        return replaceSlice(oldValue, newValue, index, index + 1);
    }

    public static $Tprim$ set(F3Object instance, int varNum, $Tprim$ newValue, int index) {
        replaceSlice(instance, varNum, newValue, index, index + 1);
        return newValue;
    }

    public static Sequence<? extends $Tboxed$> insert(Sequence<? extends $Tboxed$> oldValue, $Tprim$ newValue) {
        int oldSize = oldValue.size();
        return replaceSlice(oldValue, newValue, oldSize, oldSize);
    }

    public static void insert(F3Object instance, int varNum, $Tprim$ newValue) {
        int oldSize = instance.size\$(varNum);
        replaceSlice(instance, varNum, newValue, oldSize, oldSize);
    }

    public static <T> Sequence<? extends $Tboxed$> insertBefore(Sequence<? extends $Tboxed$> oldValue, $Tprim$ value, int position) {
        return replaceSlice(oldValue, value, position, position);
    }

    public static void insertBefore(F3Object instance, int varNum, $Tprim$ value, int position) {
        replaceSlice(instance, varNum, value, position, position);
    }

 }$
}
