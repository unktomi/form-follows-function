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

import java.util.ArrayList;
import java.util.List;

/**
 * Manages dependents of a particular F3Object.
 *
 * @author A. Sundararajan
 */
public final class DependentsManager {
    public static void addDependent(F3Object bindee, final int varNum, F3Object binder, final int depNum) {
        Dep dep = Dep.newDependency(binder, depNum);
        dep.linkToBindee(bindee, varNum);
        // FIXME: revisit this - is this a good time to call cleanup?
        WeakBinderRef.checkForCleanups();
    }

    public static void removeDependent(F3Object bindee, final int varNum, F3Object binder) {
        // We need to find the "intersection Dep" between the Dep chain
        // associated with binder, and that associated with (bindee,varNum).
        // (Note that this manager is associated with bindee.)
        // Since the Dep chain assocated with the binder is limited by the
        // complexity of the bind expression, that chain is likely to be short,
        // so a linear search is OK.
        // We still search for the correct DepChain, so we know when to stop.
        // However, that is logarithmic on the maximum varNum, so should also
        // be fast.  An alternative is to store (bindee,varNum) in each Dep,
        // which would cost one more field per Dep (compared to storing
        // the DepChain).
        DepChain chain = DepChain.find(varNum, bindee.getDepChain$internal$());
        if (chain == null)
            return;
        Dep prev = null;
        WeakBinderRef binderRef = WeakBinderRef.instance(binder);
        for (Dep dep = binderRef.bindees; dep != null; ) {
            Dep next = dep.nextInBindees;
            if (dep.chain == chain) {
                if (prev == null)
                    binderRef.bindees = next;
                else
                    prev.nextInBindees = next;
                dep.unlinkFromBindee();
                return;
            }
            prev = dep;
            dep = next;
        }
    }

    public static void switchDependence(F3Object binder, F3Object oldBindee, final int oldNum, F3Object newBindee, final int newNum, final int depNum) {
        if (oldBindee != null) {
            oldBindee.removeDependent$(oldNum, binder);
        }
        if (newBindee != null) {
            newBindee.addDependent$(newNum, binder, depNum);
        }
    }

    public static void notifyDependents(F3Object bindee, final int varNum, int startPos, int endPos, int newLength, final int phase) {
        DepChain chain = DepChain.find(varNum, bindee.getDepChain$internal$());
        if (chain == null)
            return;
        for (Dep dep = chain.dependencies; dep != null;) {
            Dep next = dep.nextInBinders;
            WeakBinderRef binderRef = dep.binderRef;
            // Note that unlinkFromBindee might have been called earlier
            // on Dep, in which case binderRef will be null.  In that case,
            // we don't need to do anything, except move on to the next Dep.
            if (binderRef != null) {
                F3Object binder = binderRef.get();
                if (binder == null) {
                    binderRef.cleanup();
                } else {
                     boolean handled = true;
                    try {
                        handled = binder.update$(bindee, dep.depNum, startPos, endPos, newLength, phase);
                    } catch (RuntimeException re) {
                        ErrorHandler.bindException(re);
                    }
                    if (!handled) {
                        Dep prev = null;
                        for (Dep d = binderRef.bindees; d != null; ) {
                            Dep nextInBindees = d.nextInBindees;
                            if (d == dep) {
                                if (prev == null)
                                    binderRef.bindees = nextInBindees;
                                else
                                    prev.nextInBindees = nextInBindees;
                                dep.unlinkFromBindee();
                                break;
                            }
                            prev = d;
                            d = nextInBindees;
                        }
                    }
                }
            }
            dep = next;
        }
    }

    public static int getListenerCount(F3Object bindee) {
        return getListenerCount(bindee.getDepChain$internal$());
    }

    private static int getListenerCount(DepChain chain) {
        if (chain == null)
            return 0;
        int count = 0;
        for (Dep dep = chain.dependencies; dep != null;) {
            WeakBinderRef binderRef = dep.binderRef;
            if (binderRef != null) {
                if (binderRef.get() != null) {
                    count++;
                }
            }
            dep = dep.nextInBinders;
        }
        return count + getListenerCount(chain.child0) + getListenerCount(chain.child1);
    }

    public static List<F3Object> getDependents(F3Object bindee) {
        List<F3Object> res = new ArrayList<F3Object>();
        getDependents(bindee.getDepChain$internal$(), res);
        return res;
    }

    private static void getDependents(DepChain chain, List<F3Object> res) {
        if (chain == null)
            return;
        for (Dep dep = chain.dependencies; dep != null;) {
            WeakBinderRef binderRef = dep.binderRef;
            if (binderRef != null) {
                if (binderRef.get() != null) {
                    res.add(binderRef.get());
                }
            }
            dep = dep.nextInBinders;
        }
        getDependents(chain.child0, res);
        getDependents(chain.child1, res);
    }
}

interface BinderLinkable {
    void setNextBinder(Dep next);
};



class Dep implements BinderLinkable {
    /* DEBUGGING
    static int counter;
    int id = ++counter;
    public String toString() { return "Dep#"+id; }
    */

    WeakBinderRef binderRef;
    Dep nextInBinders;

    public void setNextBinder(Dep next) {
        nextInBinders = next;
    }

    /** Back-pointer corresponding to nextInBinders.
     * Either the previous Dep such that
     * {@code ((Dep) prevInBinders).nextInBinders==this},
     * or (if this is the first dep) the DepChain list head such that
     * {@code ((DepChain) prevInBinders).dependencies==this}.
     */
    BinderLinkable prevInBinders;

    Dep nextInBindees;
    DepChain chain;

    int depNum;

    static Dep newDependency(F3Object binder, int depNum) {
        Dep dep = new Dep();
        dep.depNum = depNum;
        WeakBinderRef binderRef = WeakBinderRef.instance(binder);
        dep.binderRef = binderRef;
        // Link into bindee chain of binderRef
        Dep firstBindee = binderRef.bindees;
        dep.nextInBindees = firstBindee;
        binderRef.bindees = dep;
        return dep;
    }

    void linkToBindee(F3Object bindee, int bindeeVarNum) {
        WeakBinderRef bref = WeakBinderRef.instance(bindee);
        DepChain chain = DepChain.findForce(bindeeVarNum, bindee.getDepChain$internal$(), bref);
        // Link into binder chain of bindee
        Dep firstBinder = chain.dependencies;
        nextInBinders = firstBinder;
        if (firstBinder != null) {
            firstBinder.prevInBinders = this;
        }
        prevInBinders = chain;
        chain.dependencies = this;
        this.chain = chain;
    }

    /**
     * Unlink from dependency chain of bindee.
     */
    void unlinkFromBindee() {
        BinderLinkable prevBinder = prevInBinders;
        if (prevBinder == null)
            return;
        // Note that removeDependent might call this method while
        // notifyDependents is in the middle of a dependency chain.
        // Since notifyDependents needs the nextInBinders field,
        // we can't null it out, but it can be GC'd as soon as
        // notifyDependents gets past this Dep.
        // We do null out binderRef and prevInBinders to indicate
        // that this Dep has already been unlinked.
        binderRef = null;
        prevInBinders = null;
        Dep next = nextInBinders;
        if (prevBinder instanceof DepChain) {
            DepChain chain = (DepChain) prevBinder;
            chain.dependencies = next;
            if (next == null) {
                if (chain.child0 == null)
                    chain.replaceParent(chain.child1);
                else if (chain.child1 == null)
                    chain.replaceParent(chain.child0);
            }
        }
        else
            prevBinder.setNextBinder(next);
        if (next != null) {
            next.prevInBinders = prevBinder;
        }
    }
}


