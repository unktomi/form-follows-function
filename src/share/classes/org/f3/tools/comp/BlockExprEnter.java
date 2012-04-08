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

import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.PackageSymbol;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.code.Type.ClassType;
import com.sun.tools.mjavac.code.Type.ErrorType;
import com.sun.tools.mjavac.comp.AttrContext;
import com.sun.tools.mjavac.comp.Enter;
import com.sun.tools.mjavac.comp.Env;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCClassDecl;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import org.f3.tools.tree.BlockExprJCBlockExpression;

import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Kinds.*;

public class BlockExprEnter extends Enter {
    public static Enter instance0(Context context) {
        Enter instance = context.get(enterKey);
        if (instance == null)
            instance = new BlockExprEnter(context);
        return instance;
    }

    public static void preRegister(final Context context) {
        context.put(enterKey, new Context.Factory<Enter>() {
            public Enter make() {
                return new BlockExprEnter(context);
            }
        });
    }

    protected BlockExprEnter(Context context) {
        super(context);
    }

    /** Visitor method: Scan a single node.
     */
    public void scan(JCTree tree) {
	if(tree!=null) tree.accept(this);
    }

    /** Visitor method: scan a list of nodes.
     */
    public void scan(List<? extends JCTree> trees) {
	if (trees != null)
	for (List<? extends JCTree> l = trees; l.nonEmpty(); l = l.tail)
	    scan(l.head);
    }

    // Override this method and disable the enforcing of public class being in file with the same name.
    @Override
    public void visitClassDef(JCClassDecl tree) {
	Symbol owner = env.info.scope.owner;
	Scope enclScope = enterScope(env);
	ClassSymbol c;
	if (owner.kind == PCK) {
	    // We are seeing a toplevel class.
	    PackageSymbol packge = (PackageSymbol)owner;
	    for (Symbol q = packge; q != null && q.kind == PCK; q = q.owner)
		q.flags_field |= EXISTS;
	    c = reader.enterClass(tree.name, packge);
	    packge.members().enterIfAbsent(c);
// F3 change start
//            if ((tree.mods.flags & PUBLIC) != 0 && !classNameMatchesFileName(c, env)) {
//		log.error(tree.pos(),
//			  "class.public.should.be.in.file", tree.name);
//	    }
// F3 change end
        } else {
	    if (tree.name.len != 0 &&
		!chk.checkUniqueClassName(tree.pos(), tree.name, enclScope)) {
		result = null;
		return;
	    }
	    if (owner.kind == TYP) {
		// We are seeing a member class.
		c = reader.enterClass(tree.name, (TypeSymbol)owner);
		if ((owner.flags_field & INTERFACE) != 0) {
		    tree.mods.flags |= PUBLIC | STATIC;
		}
	    } else {
		// We are seeing a local class.
		c = reader.defineClass(tree.name, owner);
		c.flatname = chk.localClassName(c);
		if (c.name.len != 0)
		    chk.checkTransparentClass(tree.pos(), c, env.info.scope);
	    }
	}
	tree.sym = c;

	// Enter class into `compiled' table and enclosing scope.
	if (chk.compiled.get(c.flatname) != null) {
	    duplicateClass(tree.pos(), c);
	    result = new ErrorType(tree.name, (TypeSymbol)owner);
	    tree.sym = (ClassSymbol)result.tsym;
	    return;
	}
	chk.compiled.put(c.flatname, c);
	enclScope.enter(c);

	// Set up an environment for class block and store in `typeEnvs'
	// table, to be retrieved later in memberEnter and attribution.
	Env<AttrContext> localEnv = classEnv(tree, env);
	typeEnvs.put(c, localEnv);

	// Fill out class fields.
	c.completer = memberEnter;
        boolean wasStatic = (tree.mods.flags & Flags.STATIC) != 0L;
	c.flags_field = chk.checkFlags(tree.pos(), (tree.mods.flags & ~(Flags.STATIC)), c, tree);
        if (wasStatic) {
            c.flags_field |= Flags.STATIC;
        }
        
	c.sourcefile = env.toplevel.sourcefile;
	c.members_field = new Scope(c);

	ClassType ct = (ClassType)c.type;
	if (owner.kind != PCK && (c.flags_field & STATIC) == 0) {
	    // We are seeing a local or inner class.
	    // Set outer_field of this class to closest enclosing class
	    // which contains this class in a non-static context
	    // (its "enclosing instance class"), provided such a class exists.
	    Symbol owner1 = owner;
	    while ((owner1.kind & (VAR | MTH)) != 0 &&
		   (owner1.flags_field & STATIC) == 0) {
		owner1 = owner1.owner;
	    }
	    if (owner1.kind == TYP) {
		ct.setEnclosingType(owner1.type);
	    }
	}

	// Enter type parameters.
	ct.typarams_field = classEnter(tree.typarams, localEnv);

	// Add non-local class to uncompleted, to make sure it will be
	// completed later.
	if (!c.isLocal() && uncompleted != null) uncompleted.append(c);
//	System.err.println("entering " + c.fullname + " in " + c.owner);//DEBUG

	// Recursively enter all member classes.
	classEnter(tree.defs, localEnv);

	result = c.type;
    }

    public void visitBlockExpression(BlockExprJCBlockExpression that) {
        scan(that.stats);
        scan(that.value);
    }
}
