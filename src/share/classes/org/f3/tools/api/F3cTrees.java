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

package org.f3.tools.api;

import org.f3.api.*;
import org.f3.api.tree.*;
import java.io.IOException;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.TypeSymbol;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.PackageSymbol;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Pair;
import org.f3.tools.comp.F3Attr;
import org.f3.tools.comp.F3AttrContext;
import org.f3.tools.comp.F3Enter;
import org.f3.tools.comp.F3Env;
import org.f3.tools.comp.F3MemberEnter;
import org.f3.tools.comp.F3Resolve;
import org.f3.tools.tree.*;

/**
 * Provides an implementation of Trees for the F3 compiler, based
 * on JavacTrees.
 *
 * @author Peter von der Ah&eacute;
 * @author Tom Ball
 */
public class F3cTrees {

    private final F3Resolve resolve;
    private final F3Enter enter;
    private final Log log;
    private final F3MemberEnter memberEnter;
    private final F3Attr attr;
    private final F3TreeMaker f3make;
    private final F3cTaskImpl f3cTaskImpl;
    private final Context ctx;

    public static F3cTrees instance(F3Compiler.CompilationTask task) {
        if (!(task instanceof F3cTaskImpl))
            throw new IllegalArgumentException();
        return instance(((F3cTaskImpl)task).getContext());
    }

    public static F3cTrees instance(Context context) {
        F3cTrees instance = context.get(F3cTrees.class);
        if (instance == null)
            instance = new F3cTrees(context);
        return instance;
    }

    private F3cTrees(Context context) {
        context.put(F3cTrees.class, this);
        ctx = context;
        attr = F3Attr.instance(context);
        enter = F3Enter.instance(context);
        log = Log.instance(context);
        resolve = F3Resolve.instance(context);
        f3make = F3TreeMaker.instance(context);
        memberEnter = F3MemberEnter.instance(context);
        f3cTaskImpl = context.get(F3cTaskImpl.class);
    }

    public SourcePositions getSourcePositions() {
        return new SourcePositions() {
                public long getStartPosition(UnitTree file, Tree tree) {
                    return F3TreeInfo.getStartPos((F3Tree) tree);
                }

                public long getEndPosition(UnitTree file, Tree tree) {
                    Map<JCTree,Integer> endPositions = ((F3Script) file).endPositions;
                    return F3TreeInfo.getEndPos((F3Tree)tree, endPositions);
                }
            };
    }

    public ClassDeclarationTree getTree(TypeElement element) {
        return (ClassDeclarationTree) getTree((Element) element);
    }

    public FunctionDefinitionTree getTree(ExecutableElement method) {
        return (FunctionDefinitionTree) getTree((Element) method);
    }

    public Tree getTree(Element element) {
        Symbol symbol = (Symbol) element;
        TypeSymbol enclosing = symbol.enclClass();
        F3Env<F3AttrContext> env = enter.getEnv(enclosing);
        if (env == null)
            return null;
        F3ClassDeclaration classNode = env.enclClass;
        if (classNode != null) {
            if (F3TreeInfo.symbolFor(classNode) == element)
                return classNode;
            for (F3Tree node : classNode.getMembers())
                if (F3TreeInfo.symbolFor(node) == element)
                    return node;
        }
        return null;
    }

    public F3TreePath getPath(UnitTree unit, Tree node) {
        return getPath(new F3TreePath(unit), node);
    }

    public F3TreePath getPath(Element e) {
        final Pair<F3Tree, F3Script> treeTopLevel = getTreeAndTopLevel(e);
        if (treeTopLevel == null)
            return null;
        return getPath(treeTopLevel.snd, treeTopLevel.fst);
    }
    
    /**
     * Gets a tree path for a tree node within a subtree identified by a F3TreePath object.
     * @return null if the node is not found
     */
    public static F3TreePath getPath(F3TreePath path, Tree target) {
        path.getClass();
        target.getClass();

        class Result extends Error {
            static final long serialVersionUID = -5942088234594905625L;
            F3TreePath path;
            Result(F3TreePath path) {
                this.path = path;
            }
        }
        class PathFinder extends F3TreePathScanner<F3TreePath,Tree> {
            @Override
            public F3TreePath scan(Tree tree, Tree target) {
                if (tree == target)
                    throw new Result(new F3TreePath(getCurrentPath(), target));
                return super.scan(tree, target);
            }
        }

        try {
            new PathFinder().scan(path, target);
        } catch (Result result) {
            return result.path;
        }
        return null;
    }

    public Element getElement(F3TreePath path) {
        Tree t = path.getLeaf();
        return F3TreeInfo.symbolFor((F3Tree) t);
    }

    public TypeMirror getTypeMirror(F3TreePath path) {
        Tree t = path.getLeaf();
        return ((F3Tree)t).type;
    }

    public F3cScope getScope(F3TreePath path) {
        return new F3cScope(ctx, getAttrContext(path));
    }

    public boolean isAccessible(Scope scope, TypeElement type) {
        if (scope instanceof F3cScope && type instanceof ClassSymbol) {
            F3Env<F3AttrContext> env = ((F3cScope) scope).env;
            return resolve.isAccessible(env, (ClassSymbol)type);
        } else
            return false;
    }

    public boolean isAccessible(Scope scope, Element member, DeclaredType type) {
        if (scope instanceof F3cScope
                && member instanceof Symbol
                && type instanceof com.sun.tools.mjavac.code.Type) {
            F3Env<F3AttrContext> env = ((F3cScope) scope).env;
            return resolve.isAccessible(env, (com.sun.tools.mjavac.code.Type)type, (Symbol)member);
        } else
            return false;
    }

    private F3Env<F3AttrContext> getAttrContext(F3TreePath path) {
        if (!(path.getLeaf() instanceof F3Tree))  // implicit null-check
            throw new IllegalArgumentException();

        // if we're being invoked via from a JSR199 client, we need to make sure
        // all the classes have been entered; if we're being invoked from JSR269,
        // then the classes will already have been entered.
        if (f3cTaskImpl != null) {
            try {
                f3cTaskImpl.enter();
            } catch (IOException e) {
                throw new Error("unexpected error while entering symbols: " + e);
            }
        }

        F3Script unit = (F3Script) path.getCompilationUnit();
        Copier copier = new Copier(f3make.forToplevel(unit));

        copier.endPositions = unit.endPositions;

        F3Env<F3AttrContext> env = null;
        F3FunctionDefinition function = null;
        F3Var field = null;

        List<Tree> l = List.nil();
        F3TreePath p = path;
        while (p != null) {
            l = l.prepend(p.getLeaf());
            p = p.getParentPath();
        }

        for ( ; l.nonEmpty(); l = l.tail) {
            Tree tree = l.head;
            if (tree instanceof F3Script) {
                env = enter.getTopLevelEnv((F3Script)tree);
            }
            else if (tree instanceof F3ClassDeclaration) {
                env = enter.getClassEnv(((F3ClassDeclaration)tree).sym);
            }
            else if (tree instanceof F3FunctionDefinition) {
                function = (F3FunctionDefinition)tree;
            }
            else if (tree instanceof F3Var) {
                field = (F3Var)tree;
            }
            else if (tree instanceof F3Block) {
                if (function != null)
                    env = memberEnter.getMethodEnv(function, env);
                F3Tree body = copier.copy((F3Tree)tree, (F3Tree) path.getLeaf());
                env = attribStatToTree(body, env, copier.leafCopy);
                return env;
            } else if (field != null && field.getInitializer() == tree) {
                env = memberEnter.getInitEnv(field, env);
                F3Expression expr = copier.copy((F3Expression)tree, (F3Tree) path.getLeaf());
                env = attribExprToTree(expr, env, copier.leafCopy);
                return env;
            }
        }
        return field != null ? memberEnter.getInitEnv(field, env) : env;
    }

    private F3Env<F3AttrContext> attribStatToTree(F3Tree stat, F3Env<F3AttrContext>env, F3Tree tree) {
        JavaFileObject prev = log.useSource(env.toplevel.sourcefile);
        try {
            return attr.attribStatToTree(stat, env, tree);
        } finally {
            log.useSource(prev);
        }
    }

    private F3Env<F3AttrContext> attribExprToTree(F3Expression expr, F3Env<F3AttrContext>env, F3Tree tree) {
        JavaFileObject prev = log.useSource(env.toplevel.sourcefile);
        try {
            return attr.attribExprToTree(expr, env, tree);
        } finally {
            log.useSource(prev);
        }
    }
    
    private Pair<F3Tree, F3Script> getTreeAndTopLevel(Element e) {
        if (e == null)
            return null;

        Symbol sym = (Symbol)e;
        TypeSymbol ts = (sym.kind != Kinds.PCK)
                        ? sym.enclClass()
                        : (PackageSymbol) sym;
        F3Env<F3AttrContext> enterEnv = ts != null ? enter.getEnv(ts) : null;        
        if (enterEnv == null)
            return null;
        
        F3Tree tree = F3TreeInfo.declarationFor(sym, enterEnv.tree);
        if (tree == null || enterEnv.toplevel == null)
            return null;
        return new Pair<F3Tree,F3Script>(tree, enterEnv.toplevel);
    }

    public F3Env<F3AttrContext> getFunctionEnv(F3FunctionDefinition tree, F3Env<F3AttrContext> env) {
        F3Env<F3AttrContext> mEnv = memberEnter.methodEnv(tree, env);
        mEnv.info.lint = mEnv.info.lint.augment(tree.sym.attributes_field, tree.sym.flags());
        for (List<F3Var> l = tree.getParams(); l.nonEmpty(); l = l.tail)
            mEnv.info.scope.enterIfAbsent(l.head.sym);
        return mEnv;
    }

    public F3Env<F3AttrContext> getInitEnv(F3Var tree, F3Env<F3AttrContext> env) {
        F3Env<F3AttrContext> iEnv = memberEnter.initEnv(tree, env);
        return iEnv;
    }

    /**
     * Makes a copy of a tree, noting the value resulting from copying a particular leaf.
     **/
    static class Copier extends F3TreeCopier {
        F3Tree leaf;
        F3Tree leafCopy = null;

        Copier(F3TreeMaker M) {
            super(M);
        }

        public <T extends F3Tree> T copy(T t, F3Tree leaf) {
            this.leaf = leaf;
            return copy(t);
        }
        
        @Override
        public <T extends F3Tree> T copy(T t) {
            T t2 = super.copy(t);
            if (t == leaf)
                leafCopy = t2;
            return t2;
        }

        @Override
        public void visitForExpressionInClause(F3ForExpressionInClause tree) {
            result = maker.InClause(copy(tree.var), copy(tree.getSequenceExpression()), copy(tree.getWhereExpression()));
        }
    }
}
