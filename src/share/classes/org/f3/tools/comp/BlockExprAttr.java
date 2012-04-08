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

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import static com.sun.tools.mjavac.code.Kinds.*;
import com.sun.tools.mjavac.comp.Attr;
import com.sun.tools.mjavac.comp.AttrContext;
import com.sun.tools.mjavac.comp.Env;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.*;
import org.f3.tools.tree.*;
import org.f3.tools.util.MsgSym;

/** This is the main context-dependent analysis phase in GJC. It
 *  encompasses name resolution, type checking and constant folding as
 *  subtasks. Some subtasks involve auxiliary classes.
 *  @see Check
 *  @see Resolve
 *  @see ConstFold
 *  @see Infer
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class BlockExprAttr extends Attr  {
    
    public static Attr instance0(Context context) {
        Attr instance = context.get(attrKey);
        if (instance == null)
            instance = new BlockExprAttr(context);
        return instance;
    }

    public static void preRegister(final Context context) {
        context.put(attrKey, new Context.Factory<Attr>() {
	       public Attr make() {
		   return new BlockExprAttr(context);
	       }
        });
    }

    protected BlockExprAttr(Context context) {
        super(context);
    }


    public void visitBlockExpression(BlockExprJCBlockExpression tree) {
        // Create a new local environment with a local scope.
        Env<AttrContext> localEnv =
                env.dup(tree,
                env.info.dup(env.info.scope.dup()));
        for (List<JCStatement> l = tree.stats; l.nonEmpty(); l = l.tail)
            attribStat(l.head, localEnv);
        if (tree.value == null) {
            result = check(tree, syms.voidType, VAL, pkind, pt);
        } else {
            Type valtype = attribExpr(tree.value, localEnv);
            valtype = valtype.baseType();
            result = check(tree, valtype, VAL, pkind, pt);
        }
        localEnv.info.scope.leave();
    }

    /** Finish the attribution of a class. */
    @Override
    protected void attribClassBody(Env<AttrContext> env, ClassSymbol c) {
        JCClassDecl tree = (JCClassDecl)env.tree;
        assert c == tree.sym;

        // Validate annotations
        chk.validateAnnotations(tree.mods.annotations, c);

        // Validate type parameters, supertype and interfaces.
        attribBounds(tree.typarams);
        chk.validateTypeParams(tree.typarams);
        chk.validate(tree.extending);
        chk.validate(tree.implementing);

        // If this is a non-abstract class, check that it has no abstract
        // methods or unimplemented methods of an implemented interface.
        if ((c.flags() & (Flags.ABSTRACT | Flags.INTERFACE)) == 0) {
            if (!relax)
                chk.checkAllDefined(tree.pos(), c);
        }

        if ((c.flags() & Flags.ANNOTATION) != 0) {
            if (tree.implementing.nonEmpty())
                log.error(tree.implementing.head.pos(),
                          MsgSym.MESSAGE_CANNOT_EXTEND_INTERFACE_ANNOTATION);
            if (tree.typarams.nonEmpty())
                log.error(tree.typarams.head.pos(),
                          MsgSym.MESSAGE_INTF_ANNOTATION_CANNOT_HAVE_TYPE_PARAMS);
        } else {
            // Check that all extended classes and interfaces
            // are compatible (i.e. no two define methods with same arguments
            // yet different return types).  (JLS 8.4.6.3)
            chk.checkCompatibleSupertypes(tree.pos(), c.type);
        }

        // Check that class does not import the same parameterized interface
        // with two different argument lists.
        chk.checkClassBounds(tree.pos(), c.type);

        tree.type = c.type;

        boolean assertsEnabled = false;
        assert assertsEnabled = true;
        if (assertsEnabled) {
            for (List<JCTypeParameter> l = tree.typarams;
                 l.nonEmpty(); l = l.tail)
                assert env.info.scope.lookup(l.head.name).scope != null;
        }

        // Check that a generic class doesn't extend Throwable
        if (!c.type.allparams().isEmpty() && types.isSubtype(c.type, syms.throwableType))
            log.error(tree.extending.pos(), MsgSym.MESSAGE_GENERIC_THROWABLE);

        // Check that all methods which implement some
        // method conform to the method they implement.
        chk.checkImplementations(tree);

        for (List<JCTree> l = tree.defs; l.nonEmpty(); l = l.tail) {
            // Attribute declaration
            attribStat(l.head, env);
            // Check that declarations in inner classes are not static (JLS 8.1.2)
            // Make an exception for static constants.
            // F3 doesn't have this restriction
//            if (c.owner.kind != PCK &&
//                ((c.flags() & STATIC) == 0 || c.name == names.empty) &&
//                (TreeInfo.flags(l.head) & (STATIC | INTERFACE)) != 0) {
//                Symbol sym = null;
//                if (l.head.getTag() == JCTree.VARDEF) sym = ((JCVariableDecl) l.head).sym;
//                if (sym == null ||
//                    sym.kind != VAR ||
//                    ((F3VarSymbol) sym).getConstValue() == null)
//                    log.error(l.head.pos(), "icls.cant.have.static.decl");
//            }
        }

        // Check for cycles among non-initial constructors.
        chk.checkCyclicConstructors(tree);

        // Check for cycles among annotation elements.
        chk.checkNonCyclicElements(tree);

        // Check for proper use of serialVersionUID
        if (env.info.lint.isEnabled(Lint.LintCategory.SERIAL) &&
            isSerializable(c) &&
            (c.flags() & Flags.ENUM) == 0 &&
            (c.flags() & Flags.ABSTRACT) == 0) {
            checkSerialVersionUID(tree, c);
        }
    }

    /**
     * Force an expected kind that was provided by the F3 front-end
     */
    public void visitAugmentedIdent(AugmentedJCIdent tree) {
        if (tree.pkind != Kinds.NIL && tree.sym == null) {
            pkind = tree.pkind;
        }
        visitIdent(tree);
    }
}
