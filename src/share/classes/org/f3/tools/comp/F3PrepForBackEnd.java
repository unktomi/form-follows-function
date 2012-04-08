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

import com.sun.tools.mjavac.tree.*;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.Context;
import org.f3.tools.tree.BlockExprJCBlockExpression;
import java.util.HashSet;
import java.util.Set;

/**
 * Remove symbol and type information.
 * 
 * Already converted to JCTree nodes
 * 
 * @author Robert Field
 */
public class F3PrepForBackEnd extends TreeScanner {

    private boolean checkForUniqueness = false;
    private Set<JCTree> seen = new HashSet<JCTree>();
    private String sourceName = "";
    
    protected static final Context.Key<F3PrepForBackEnd> prepForBackEndKey =
        new Context.Key<F3PrepForBackEnd>();

    public static F3PrepForBackEnd instance(Context context) {
        F3PrepForBackEnd instance = context.get(prepForBackEndKey);
        if (instance == null)
            instance = new F3PrepForBackEnd(context);
        return instance;
    }

    F3PrepForBackEnd(Context context) { 
    }
    
    public void prep(F3Env<F3AttrContext> attrEnv) {
        scan(attrEnv.translatedToplevel);
    }
    
    private void assertUnique(JCTree that) {
        if (checkForUniqueness) {
            boolean added = seen.add(that);
            if (!added) {
                //System.err.println("Node " + that + " already encountered -- unclean " + that.getClass() + " tree in " + sourceName);
            }
            assert added : "Node " + that + " already encountered -- unclean " + that.getClass() + " tree in " + sourceName;
        }
    }

    @Override
    public void visitTopLevel(JCCompilationUnit that) {
        super.visitTopLevel(that);
        sourceName = that.sourcefile.getName();
        assertUnique(that);
        that.type = null;
        that.packge = null;
        that.starImportScope = null;
        that.namedImportScope = null;
    }
    
    @Override
    public void visitImport(JCImport that) {
        super.visitImport(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitClassDef(JCClassDecl that) {
        super.visitClassDef(that);
        assertUnique(that);
        that.type = null;
        that.sym = null;
    }
    
    @Override
    public void visitMethodDef(JCMethodDecl that) {
        super.visitMethodDef(that);
        assertUnique(that);
        that.type = null;
        that.sym = null;
    }
    
    @Override
    public void visitVarDef(JCVariableDecl that) {
        super.visitVarDef(that);
        assertUnique(that);
        that.type = null;
        that.sym = null;
    }
    
    @Override
    public void visitSkip(JCSkip that) {
        super.visitSkip(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitBlock(JCBlock that) {
        super.visitBlock(that);
        for(JCStatement stmt : that.stats) {
            if ( stmt == null ) throw new AssertionError( "Null statement in block" );
        }
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitDoLoop(JCDoWhileLoop that) {
        super.visitDoLoop(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitWhileLoop(JCWhileLoop that) {
        super.visitWhileLoop(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitForLoop(JCForLoop that) {
        super.visitForLoop(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitForeachLoop(JCEnhancedForLoop that) {
        super.visitForeachLoop(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitLabelled(JCLabeledStatement that) {
        super.visitLabelled(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitSwitch(JCSwitch that) {
        super.visitSwitch(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitCase(JCCase that) {
        super.visitCase(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitSynchronized(JCSynchronized that) {
         super.visitSynchronized(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTry(JCTry that) {
        super.visitTry(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitCatch(JCCatch that) {
         super.visitCatch(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitConditional(JCConditional that) {
        super.visitConditional(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitIf(JCIf that) {
        super.visitIf(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitExec(JCExpressionStatement that) {
         super.visitExec(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitBreak(JCBreak that) {
         super.visitBreak(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitContinue(JCContinue that) {
        super.visitContinue(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitReturn(JCReturn that) {
        super.visitReturn(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitThrow(JCThrow that) {
        super.visitThrow(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitAssert(JCAssert that) {
        super.visitAssert(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitApply(JCMethodInvocation that) {
        super.visitApply(that);
        assertUnique(that);
        that.type = null;
        that.varargsElement = null;
    }
    
    @Override
    public void visitNewClass(JCNewClass that) {
        super.visitNewClass(that);
        assertUnique(that);
        that.type = null;
        that.constructor = null;
        that.varargsElement = null;
    }
    
    @Override
    public void visitNewArray(JCNewArray that) {
         super.visitNewArray(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitParens(JCParens that) {
        super.visitParens(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitAssign(JCAssign that) {
        super.visitAssign(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitAssignop(JCAssignOp that) {
        super.visitAssignop(that);
        assertUnique(that);
        that.type = null;
        that.operator = null;
    }
    
    @Override
    public void visitUnary(JCUnary that) {
        super.visitUnary(that);
        assertUnique(that);
        that.type = null;
        that.operator = null;
    }
    
    @Override
    public void visitBinary(JCBinary that) {
        super.visitBinary(that);
        assertUnique(that);
        that.type = null;
        that.operator = null;
    }
    
    @Override
    public void visitTypeCast(JCTypeCast that) {
        super.visitTypeCast(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeTest(JCInstanceOf that) {
        super.visitTypeTest(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitIndexed(JCArrayAccess that) {
         super.visitIndexed(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitSelect(JCFieldAccess that) {
        super.visitSelect(that);
        assertUnique(that);
        that.type = null;
        that.sym = null;
    }
    
    @Override
    public void visitIdent(JCIdent that) {
        super.visitIdent(that);
        assertUnique(that);
        that.type = null;
        that.sym = null;
    }
    
    @Override
    public void visitLiteral(JCLiteral that) {
        super.visitLiteral(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree that) {
        super.visitTypeIdent(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeArray(JCArrayTypeTree that) {
        super.visitTypeArray(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeApply(JCTypeApply that) {
        super.visitTypeApply(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeParameter(JCTypeParameter that) {
        super.visitTypeParameter(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitWildcard(JCWildcard that) {
        super.visitWildcard(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitTypeBoundKind(TypeBoundKind that) {
        super.visitTypeBoundKind(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitAnnotation(JCAnnotation that) {
        super.visitAnnotation(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitModifiers(JCModifiers that) {
        super.visitModifiers(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitErroneous(JCErroneous that) {
        super.visitErroneous(that);
        assertUnique(that);
        that.type = null;
    }
    
    @Override
    public void visitLetExpr(LetExpr that) {
        super.visitLetExpr(that);
        assertUnique(that);
        that.type = null;
    }

    public void visitBlockExpression(BlockExprJCBlockExpression that) {
        assertUnique(that);
        for(JCStatement stmt : that.stats) {
            if ( stmt == null ) throw new AssertionError( "Null statement in block-expression" );
            scan(stmt);
        }
        if (that.value != null)
            scan(that.value);
        that.type = null;
    }
}
