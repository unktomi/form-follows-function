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

package org.f3.runtime;

import org.f3.runtime.refq.RefQ;
import org.f3.runtime.refq.WeakRef;
import java.lang.ref.Reference;

public class WeakBinderRef extends WeakRef<F3Object> {
    private static RefQ<F3Object> refQ = new RefQ<F3Object>();
    /** Chain of Dep instances whose binderRef point back here. */
    Dep bindees;
    /** Increment this to disable checkForCleanups.
     * (I don't know if/when that is needed ...) */
    static volatile int unsafeToCleanup;

    public static WeakBinderRef instance(F3Object bindee) {
        WeakBinderRef bref = bindee.getThisRef$internal$();
        if (bref == null) {
            bref = new WeakBinderRef(bindee);
            bindee.setThisRef$internal$(bref);
        }
        return bref;
    }

    static void checkForCleanups() {
        if (unsafeToCleanup > 0) {
            return;
        }
        Reference<? extends F3Object> ref;
        while ((ref = refQ.poll()) != null) {
            if (ref instanceof WeakBinderRef) {
                ((WeakBinderRef) ref).cleanup();
            }
        }
    }

    WeakBinderRef(F3Object obj) {
        super(obj, refQ);
    }

    void cleanup() {
        for (Dep dep = bindees; dep != null;) {
            Dep next = dep.nextInBindees;
            dep.unlinkFromBindee();
            dep = next;
        }
        bindees = null;
    }
}
