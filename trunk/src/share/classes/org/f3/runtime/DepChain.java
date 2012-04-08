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


/** Each DepChain is a linked list of Deps for a given bindeeVarNum.
 * In addition, a DepChain may have two child DepChains that are
 * organized as a binary trie.
 */
public class DepChain implements BinderLinkable {
    int bindeeVarNum;

    /** The lowest power of 2 such that {@code nextBit > bindeeVarNum}.
     * If {@code child0 != null}, then {@code child0.nextBit > this.nextBit}.
     * If {@code child1 != null}, then {@code child1.nextBit > this.nextBit}.
     */
    int nextBit;

    /** The chain of dependencies for the given bindeeVarNum. */
    Dep dependencies;

    /** Children that have the same varNum prefix string.
     * We view varNum as a bit-string in little-endian order.
     * For all p such that p is child0 or a descendant of child0,
     * we have that {@code (p.varNum & nextBit) == 0} and {@code p.varNum & (nextBit-1) == varNum}.
     * Likewise, for all p such that p is child1 or a descendant of child1,
     * we have that {@code (p.varNum & nextBit) != 0} and {@code p.varNum & (nextBit-1) == varNum}.
     */
    DepChain child0, child1;

    Object /*union<DepChain,WeakBinderRef>*/ parent;

    public void setNextBinder(Dep next) {
        dependencies = next;
    }

    /** Find the DepChain for the given varNum, or return null if not found. */
    public static DepChain find(int varNum, DepChain cur) {
        for (;;) {
            if (cur == null)
                return null;
            int curVarNum = cur.bindeeVarNum;
            if (varNum == curVarNum)
                return cur;
            int curBit = cur.nextBit;
            int mask = curBit-1;
            if ((varNum & mask) != (curVarNum & mask))
                return null;
            cur = (varNum & curBit) == 0 ? cur.child0 : cur.child1;
        }
    }

    /** Find the DepChain for the given varNum, or create it if not found. */
    public static DepChain findForce(int varNum, DepChain cur, WeakBinderRef bindee) {
        Object parent = bindee;
        // If selector==-1 then cur == ((WeakBinderRef) parent).get().getDepChain$internal$()
        // If selector==0 then cur == ((DepChain) parent).child0.
        // If selector==1 then cur == ((DepChain) parent).child1.
        int selector = -1;
        for (;;) {
            if (cur == null)
                break;
            int curVarNum = cur.bindeeVarNum;
            if (varNum == curVarNum)
                return cur;
            int curBit = cur.nextBit;
            int mask = curBit-1;
            if ((varNum & mask) != curVarNum) {
                DepChain p = new DepChain();
                int nextBit = 1;
                while ((varNum & nextBit) == (curVarNum & nextBit) &&
                        nextBit <= varNum)
                    nextBit <<= 1;
                p.bindeeVarNum = varNum & (nextBit-1);
                p.nextBit = nextBit;
                replace(parent, selector, p);
                if ((curVarNum & nextBit) == 0) {
                    p.child0 = cur;
                    selector = 1;
                } else {
                    p.child1 = cur;
                    selector = 0;
                }
                p.parent = parent;
                cur.parent = p;
                if (p.bindeeVarNum == varNum)
                    return p;
                parent = p;
                break;
            }
            parent = cur;
            if ((varNum & curBit) == 0) {
                selector = 0;
                cur = cur.child0;
            }
            else {
                selector = 1;
                cur = cur.child1;
            }
        }
        cur = new DepChain();
        cur.bindeeVarNum = varNum;
        cur.parent = parent;
        int nextBit = 1;
        while (nextBit <= varNum)
            nextBit <<= 1;
        cur.nextBit = nextBit;
        DepChain old = replace(parent, selector, cur);
        if (old != null) {
             if ((old.bindeeVarNum & nextBit) == 0)
                 cur.child0 = old;
             else
                 cur.child1 = old;
        }
        return cur;
    }

    /** Replace parent.selector by dep, returning old value. */
    private static DepChain replace(Object parent, int selector, DepChain dep) {
        DepChain old = null;
        if (selector == -1) {
            WeakBinderRef wref = (WeakBinderRef) parent;
            F3Object f3Obj = wref.get();
            // FIXME : do we need this null check here?
            if (f3Obj != null) {
                old = f3Obj.getDepChain$internal$();
                f3Obj.setDepChain$internal$(dep);
            }
        }
        else {
            DepChain pchain = (DepChain) parent;
            if (selector == 0) {
                old = pchain.child0;
                pchain.child0 = dep;
            } else {
                old = pchain.child1;
                pchain.child1 = dep;
            }
        }
        return old;
    }

    /** Replace this.parent by replacement. */
    void replaceParent(DepChain replacement) {
        if (parent instanceof WeakBinderRef) {
            F3Object obj = ((WeakBinderRef)parent).get();
            // FIXME : do we need this null check here?
            if (obj != null) {
                obj.setDepChain$internal$(replacement);
            }
        } else {
            DepChain pchain = (DepChain) parent;
            if (pchain.child0 == this)
                pchain.child0 = replacement;
            if (pchain.child1 == this)
                pchain.child1 = replacement;
        }
    }

    /* DEBUGGING:
    static int counter;
    int id = ++counter;
    public String toString() { return "DepChain#"+id+"-vn:"+bindeeVarNum; }
    public static String xtoString(DepChain d) {
        if (d==null) return "null";
        return "DepChain[#"+d.id+"-vn:"+d.bindeeVarNum+" nB:"+d.nextBit+" ch0: "+xtoString(d.child0)+" ch1: "+xtoString(d.child1)
                +" p:"+d.parent+" d:"+xstr(d.dependencies)+"]";
    }
    public static String xstr(Dep d) {
        if (d == null)
            return "";
        if (d.nextInBinders==null) return ""+d;
        return ""+d+" "+xstr(d.nextInBinders);
    }
    */
}
