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

package org.f3.tools.code;

import com.sun.tools.mjavac.code.Lint;
import com.sun.tools.mjavac.util.Context;

/**
 * F3 version of javac's Lint service.
 *
 * @author Tom Ball
 */
public class F3Lint extends Lint{

    public static Lint instance(Context context) {
        Lint instance = context.get(lintKey);
        if (instance == null)
            instance = new F3Lint(context);
        return instance;
    }
    
    public static void preRegister(final Context context) {
        context.put(lintKey, new Context.Factory<Lint>() {
            //@Override
            public Lint make() {
                return new F3Lint(context);
            }
        });
    }
    
    public F3Lint(Lint other) {
        super(other);
    }

    public F3Lint(Context context) {
        super(context);
    }

    @Override
    public boolean isSuppressed(LintCategory lc) {
        // Suppress unchecked warnings, since F3 doesn't support annotations.
        return lc == LintCategory.UNCHECKED ? true : super.isSuppressed(lc);
    }
}
