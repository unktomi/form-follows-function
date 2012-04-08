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

import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.code.*;

import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.comp.Resolve;
import com.sun.tools.mjavac.tree.JCTree.*;

import static com.sun.tools.mjavac.code.Kinds.*;

/** Helper class for name resolution, used mostly by the attribution phase.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class BlockExprResolve extends Resolve {
    BlockExprMemberEnter memberEnter;
    String mixinNameSuffix = "$Mixin";
    public static Resolve instance0(Context context) {
        Resolve instance = context.get(resolveKey);
        if (instance == null)
            instance = new BlockExprResolve(context);
        return instance;
    }

    public static void preRegister(final Context context) {
        context.put(resolveKey, new Context.Factory<Resolve>() {
	       public Resolve make() {
		   return new BlockExprResolve(context);
	       }
        });
    }

    protected BlockExprResolve(Context context) {
        super(context);
        memberEnter = (BlockExprMemberEnter)BlockExprMemberEnter.instance(context);
    }

    /** If `sym' is a bad symbol: report error and return errSymbol
     *  else pass through unchanged,
     *  additional arguments duplicate what has been used in trying to find the
     *  symbol (--> flyweight pattern). This improves performance since we
     *  expect misses to happen frequently.
     *
     *  @param sym       The symbol that was found, or a ResolveError.
     *  @param pos       The position to use for error reporting.
     *  @param site      The original type from where the selection took place.
     *  @param name      The symbol's name.
     *  @param argtypes  The invocation's value arguments,
     *                   if we looked for a method.
     *  @param typeargtypes  The invocation's type arguments,
     *                   if we looked for a method.
     */
    @Override
    public Symbol access(Symbol sym,
                  DiagnosticPosition pos,
                  Type site,
                  Name name,
                  boolean qualified,
                  List<Type> argtypes,
                  List<Type> typeargtypes) {
        if (sym.kind >= AMBIGUOUS) {
//          printscopes(site.tsym.members());//DEBUG
            if (!site.isErroneous() &&
                !Type.isErroneous(argtypes) &&
                (typeargtypes==null || !Type.isErroneous(typeargtypes)))
                if (!(memberEnter.resolvingImport && name.toString().endsWith(mixinNameSuffix))) {
                    ((ResolveError)sym).report(log, pos, site, name, argtypes, typeargtypes);
                }
            do {
                sym = ((ResolveError)sym).sym;
            } while (sym.kind >= AMBIGUOUS);
            if (sym == syms.errSymbol // preserve the symbol name through errors
                || ((sym.kind & ERRONEOUS) == 0 // make sure an error symbol is returned
                    && (sym.kind & TYP) != 0))
                sym = new ErrorType(name, qualified?site.tsym:syms.noSymbol).tsym;
        }
        return sym;
    }
}
