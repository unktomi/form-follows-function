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

package org.f3.tools.comp;

import com.sun.tools.mjavac.comp.Flow;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.TreeMaker;
import com.sun.tools.mjavac.util.Context;

/**
 * Override error handling in Flow
 * 
 * @author Robert Field
 */
public class F3Flow extends Flow {

    public static void preRegister(final Context context) {
        context.put(flowKey, new Context.Factory<Flow>() {
	       public Flow make() {
		   return new F3Flow(context);
	       }
        });
    }

    /** Construct a new class reader, optionally treated as the
     *  definitive classreader for this invocation.
     */
    protected F3Flow(Context context) {
        super(context);
    }

    /** DO NOT complain that pending exceptions are not caught.
     */
    @Override
    protected void errorUncaught() {
	/* nada */
    }

    /** Perform definite assignment/unassignment analysis on a tree.
     */
    @Override
    public void analyzeTree(JCTree tree, TreeMaker make) {
        // Do not do anything here for now. F3 doesn't need assignment/unassignment, reachability and checked exceptions analysis
    }
}
