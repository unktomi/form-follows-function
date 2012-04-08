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

package org.f3.tools.tree;

import org.f3.api.tree.ForExpressionInClauseTree;
import org.f3.api.tree.Tree;
import com.sun.tools.mjavac.util.List;

/**
 * An abstract tree walker (visitor) for ASTs ({@code F3Tree}s).
 * Each {@code visitXxx} method calls {@code scan} to visit its child
 * trees.  The {@code scan} method calls the {@code F3Tree}-subclass-specific
 * {@code accept} method.  A sub-class can override a specific {@code visitXxx}
 * method, or the {@code scan method}.
 * 
 * @author Robert Field
 * @author Per Bothner
 */
public class F3TreeScanner implements F3Visitor {

    public F3TreeScanner() {
    }

    /** Visitor method: Scan a single node.
   */
    public void scan(F3Tree tree) {
	if(tree!=null) tree.accept(this);
    }

    /** Visitor method: scan a list of nodes.
     */
     public void scan(List<? extends F3Tree> trees) {
	if (trees != null)
	for (List<? extends F3Tree> l = trees; l.nonEmpty(); l = l.tail)
	    scan(l.head);
    }

    /** Visitor method: scan a list of nodes.
     */
    public void scan(java.util.List<? extends Tree> trees) {
	if (trees != null)
            for (Tree t : trees)
                scan((F3Tree)t);
    }


/* ***************************************************************************
 * Visitor methods
 ****************************************************************************/
    
    public void visitScript(F3Script tree) {
        scan(tree.pid);
        scan(tree.defs);
    }

    public void visitImport(F3Import tree) {
        scan(tree.qualid);
    }

    public void visitSkip(F3Skip tree) {
    }

    public void visitWhileLoop(F3WhileLoop tree) {
        scan(tree.cond);
        scan(tree.body);
    }

    public void visitTry(F3Try tree) {
        scan(tree.body);
        scan(tree.catchers);
        scan(tree.finalizer);
    }

    public void visitCatch(F3Catch tree) {
        scan(tree.param);
        scan(tree.body);
    }

    public void visitIfExpression(F3IfExpression tree) {
        scan(tree.cond);
        scan(tree.truepart);
        scan(tree.falsepart);
    }

    public void visitBreak(F3Break tree) {
    }

    public void visitContinue(F3Continue tree) {
    }

    public void visitReturn(F3Return tree) {
        scan(tree.expr);
    }

    public void visitThrow(F3Throw tree) {
        scan(tree.expr);
    }

    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        scan(tree.meth);
        scan(tree.args);
    }

    public void visitParens(F3Parens tree) {
        scan(tree.expr);
    }

    public void visitAssign(F3Assign tree) {
        scan(tree.lhs);
        scan(tree.rhs);
    }

    public void visitAssignop(F3AssignOp tree) {
        scan(tree.lhs);
        scan(tree.rhs);
    }

    public void visitUnary(F3Unary tree) {
        scan(tree.arg);
    }

    public void visitBinary(F3Binary tree) {
        scan(tree.lhs);
        scan(tree.rhs);
    }

    public void visitTypeCast(F3TypeCast tree) {
        scan(tree.clazz);
        scan(tree.expr);
    }

    public void visitInstanceOf(F3InstanceOf tree) {
        scan(tree.expr);
        scan(tree.clazz);
    }

    public void visitSelect(F3Select tree) {
        scan(tree.selected);
    }

    public void visitIdent(F3Ident tree) {
    }

    public void visitLiteral(F3Literal tree) {
    }

    public void visitModifiers(F3Modifiers tree) {
    }

    public void visitErroneous(F3Erroneous tree) {
    }

    public void visitClassDeclaration(F3ClassDeclaration tree) {
        scan(tree.mods);
        for (Tree member : tree.getMembers()) {
            scan((F3Tree)member);
        }
    }
    
    public void visitFunctionValue(F3FunctionValue tree) {
        for (F3Var param : tree.getParams()) {
            scan(param);
        }
        scan(tree.getBodyExpression());
    }

    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        scan(tree.getModifiers());
        scan(tree.getF3ReturnType());
        visitFunctionValue(tree.operation);
    }

    public void visitInitDefinition(F3InitDefinition tree) {
        scan((F3Block)tree.getBody());
    }

    public void visitPostInitDefinition(F3PostInitDefinition tree) {
        scan((F3Block)tree.getBody());
    }

    public void visitSequenceEmpty(F3SequenceEmpty tree) {
    }
    
    public void visitSequenceRange(F3SequenceRange tree) {
        scan( tree.getLower() );
        scan( tree.getUpper() );
        scan( tree.getStepOrNull() );
    }
    
    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        scan( tree.getItems() );
    }

    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        scan(tree.getSequence());
        scan(tree.getIndex());
    }
    
    public void visitSequenceSlice(F3SequenceSlice tree) {
        scan(tree.getSequence());
        scan(tree.getFirstIndex());
        scan(tree.getLastIndex());
    }
    
    public void visitSequenceInsert(F3SequenceInsert tree) {
        scan(tree.getSequence());
        scan(tree.getElement());
    }
    
    public void visitSequenceDelete(F3SequenceDelete tree) {
        scan(tree.getSequence());
        scan(tree.getElement());
    }

    public void visitInvalidate(F3Invalidate tree) {
        scan(tree.getVariable());
    }

    public void visitStringExpression(F3StringExpression tree) {
        List<F3Expression> parts = tree.getParts();
        parts = parts.tail;
        while (parts.nonEmpty()) {
            parts = parts.tail;
            scan(parts.head);
            parts = parts.tail;
            parts = parts.tail;
        }
    }
    
    public void visitInstanciate(F3Instanciate tree) {
       scan(tree.getIdentifier());
       scan(tree.getArgs());
       scan(tree.getParts());
       scan(tree.getLocalvars());
       scan(tree.getClassBody());
    }
    
    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        scan(tree.getExpression());
    }  
    
    public void visitTypeAny(F3TypeAny tree) {
    }
    
    public void visitTypeClass(F3TypeClass tree) {
    }
    
    public void visitTypeFunctional(F3TypeFunctional tree) {
        for (F3Tree param : (List<F3Type>)tree.getParameters()) {
            scan(param);
        }
        scan((F3Type)tree.getReturnType());
    }
    
    public void visitTypeArray(F3TypeArray tree) {
        scan((F3Type)tree.getElementType());
    }

    public void visitTypeUnknown(F3TypeUnknown tree) {
    }
    
    public void visitVarInit(F3VarInit tree) {
    }

    public void visitVarRef(F3VarRef tree) {
    }

    public void visitVar(F3Var tree) {
        scan(tree.getF3Type());
        scan(tree.mods);
        scan(tree.getInitializer());
        scan(tree.getOnReplace());
        scan(tree.getOnInvalidate());
    }

    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        scan(tree.getId());
        scan(tree.getInitializer());
        scan(tree.getOnReplace());
        scan(tree.getOnInvalidate());
    }

    public void visitOnReplace(F3OnReplace tree) {
        scan(tree.getFirstIndex());
        scan(tree.getLastIndex());
        scan(tree.getOldValue());
        scan(tree.getNewElements());
        scan(tree.getBody());
    }
    
    
    public void visitForExpression(F3ForExpression tree) {
        for (ForExpressionInClauseTree cl : tree.getInClauses()) {
            F3ForExpressionInClause clause = (F3ForExpressionInClause)cl;
            scan(clause);
        }
        scan(tree.getBodyExpression());
    }
    
    public void visitForExpressionInClause(F3ForExpressionInClause tree) {
        scan(tree.getVar());
        scan(tree.getSequenceExpression());
        scan(tree.getWhereExpression());
    }
    
    public void visitBlockExpression(F3Block tree) {
        scan(tree.stats);
        scan(tree.value);
    }
    
    public void visitIndexof(F3Indexof tree) {
    }

    public void visitTimeLiteral(F3TimeLiteral tree) {
    }

    public void visitLengthLiteral(F3LengthLiteral tree) {
    }

    public void visitAngleLiteral(F3AngleLiteral tree) {
    }

    public void visitColorLiteral(F3ColorLiteral tree) {
    }

    public void visitInterpolateValue(F3InterpolateValue tree) {
        scan(tree.attribute);
        scan(tree.value);
        if  (tree.interpolation != null) {
            scan(tree.interpolation);
        }
    }
    
    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        scan(tree.start);
        for (F3Expression value: tree.values)
            scan(value);
        if (tree.trigger != null)
            scan(tree.trigger);
    }

    public void visitTree(F3Tree tree) {
        assert false : "Should not be here!!!";
    }
}
