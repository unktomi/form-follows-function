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

package org.f3.tools.comp;

import org.f3.api.F3BindStatus;
import org.f3.tools.tree.*;
import org.f3.tools.util.MsgSym;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.JCDiagnostic;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Log;

/**
*
* @author Robert Field
*/
public class F3BoundContextAnalysis extends F3TreeScanner {

    protected static final Context.Key<F3BoundContextAnalysis> bindAnalysisKey =
            new Context.Key<F3BoundContextAnalysis>();

    private final Log log;
    private final JCDiagnostic.Factory diags;

    private F3BindStatus bindStatus;

    public static F3BoundContextAnalysis instance(Context context) {
        F3BoundContextAnalysis instance = context.get(bindAnalysisKey);
        if (instance == null) {
            instance = new F3BoundContextAnalysis(context);
        }
        return instance;
    }

    F3BoundContextAnalysis(Context context) {
        context.put(bindAnalysisKey, this);
        log = Log.instance(context);
        diags = JCDiagnostic.Factory.instance(context);

        bindStatus = F3BindStatus.UNBOUND;
    }

    public void analyzeBindContexts(F3Env<F3AttrContext> attrEnv) {
        scan(attrEnv.tree);
    }

    private void mark(F3BoundMarkable tree) {
        tree.markBound(bindStatus);
    }

    @Override
    public void visitScript(F3Script tree) {
        bindStatus = F3BindStatus.UNBOUND;
        super.visitScript(tree);
    }

    @Override
    public void visitVarInit(F3VarInit tree) {
    }

    private void analyzeVar(F3AbstractVar tree) {
        // any changes here should also go into visitOverrideClassVar
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = tree.isBound()?
                            tree.getBindStatus() :
                            prevBindStatus;
        mark(tree);
        scan(tree.getInitializer());
        bindStatus = prevBindStatus;
        for (F3OnReplace.Kind triggerKind : F3OnReplace.Kind.values()) {
            F3OnReplace trigger = tree.getTrigger(triggerKind);
            if (trigger != null) {
                if (bindStatus != F3BindStatus.UNBOUND) {
                    log.error(trigger.pos(), MsgSym.MESSAGE_TRIGGER_IN_BIND_NOT_ALLOWED, triggerKind);
                }
            }
        }
        scan(tree.getOnReplace());
        scan(tree.getOnInvalidate());
    }

    @Override
    public void visitVar(F3Var tree) {
        analyzeVar(tree);
    }

    @Override
    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        analyzeVar(tree);
    }

    @Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
        // these start over in a class definition
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;

        super.visitClassDeclaration(tree);

        bindStatus = prevBindStatus;
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        // start over in a function definition
        F3BindStatus prevBindStatus = bindStatus;

        bindStatus = tree.isBound()? F3BindStatus.UNIDIBIND : F3BindStatus.UNBOUND;
        // don't use super, since we don't want to cancel the inBindContext
        scan(tree.getParams());
        scan(tree.getBodyExpression());
        bindStatus = prevBindStatus;
    }

    @Override
    public void visitForExpressionInClause(F3ForExpressionInClause tree) {
        mark(tree);
        super.visitForExpressionInClause(tree);
    }

    @Override
    public void visitFunctionValue(F3FunctionValue tree) {
        // these start over in a function value
        F3BindStatus prevBindStatus = bindStatus;
        //bindStatus = F3BindStatus.UNBOUND;
        bindStatus = tree.isBound()? F3BindStatus.UNIDIBIND : F3BindStatus.UNBOUND;
        super.visitFunctionValue(tree);
        bindStatus = prevBindStatus;
    }

    @Override
    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = tree.isExplicitlyBound()?
                            tree.getBindStatus() :
                            prevBindStatus;
        tree.markBound(bindStatus);
        scan(tree.getExpression());
        bindStatus = prevBindStatus;
    }

    @Override
    public void visitAssignop(F3AssignOp tree) {
        if (bindStatus != F3BindStatus.UNBOUND) {
            log.error(tree.pos(), MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT, "compound assignment");
        }
        super.visitAssignop(tree);
    }

    @Override
    public void visitAssign(F3Assign tree) {
        if (bindStatus != F3BindStatus.UNBOUND) {
            log.error(tree.pos(), MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT, "=");
        }
        super.visitAssign(tree);
    }

    @Override
    public void visitUnary(F3Unary tree) {
        mark(tree);
        if (bindStatus != F3BindStatus.UNBOUND) {
            switch (tree.getF3Tag()) {
                case PREINC:
                case POSTINC:
                    log.error(tree.pos(), MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT, "++");
                    break;
                case PREDEC:
                case POSTDEC:
                    log.error(tree.pos(), MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT, "--");
                    break;
            }
        }
        super.visitUnary(tree);
    }

    @Override
    public void visitInterpolateValue(final F3InterpolateValue tree) {
        F3BindStatus prevBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNBOUND;  //TODO: ???
        super.visitInterpolateValue(tree);
        bindStatus = prevBindStatus;
    }

    @Override
    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        if (bindStatus != F3BindStatus.UNBOUND) {
            log.error(tree.pos(),
                    MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT,
                    diags.fragment(MsgSym.MESSAGE_F3_KEYFRAME_LIT));
        }
        super.visitKeyFrameLiteral(tree);
    }

    @Override
    public void visitTry(F3Try tree) {
        if (bindStatus != F3BindStatus.UNBOUND) {
            log.error(tree.pos(),
                    MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT,
                    diags.fragment(MsgSym.MESSAGE_F3_TRY_CATCH));
        }
        super.visitTry(tree);
    }

    @Override
    public void visitBlockExpression(F3Block tree) {
        mark(tree);
        if (bindStatus != F3BindStatus.UNBOUND) {
            for (List<F3Expression> l = tree.stats; l.nonEmpty(); l = l.tail) {
                if (l.head.getF3Tag() != F3Tag.VAR_DEF) {
                    //log.error(l.head.pos(), MsgSym.MESSAGE_F3_NOT_ALLOWED_IN_BIND_CONTEXT, l.head.toString());
                }
            }
        }
        super.visitBlockExpression(tree);
    }

    @Override
    public void visitIfExpression(F3IfExpression tree) {
        mark(tree);
        super.visitIfExpression(tree);
    }

    @Override
    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        mark(tree);
        super.visitFunctionInvocation(tree);
    }

    @Override
    public void visitParens(F3Parens tree) {
        mark(tree);
        super.visitParens(tree);
    }

    @Override
    public void visitBinary(F3Binary tree) {
        mark(tree);
        super.visitBinary(tree);
    }

    @Override
    public void visitTypeCast(F3TypeCast tree) {
        mark(tree);
        super.visitTypeCast(tree);
    }

    @Override
    public void visitInstanceOf(F3InstanceOf tree) {
        mark(tree);
        super.visitInstanceOf(tree);
    }

    @Override
    public void visitSelect(F3Select tree) {
        mark(tree);
        super.visitSelect(tree);
    }

    @Override
    public void visitIdent(F3Ident tree) {
        mark(tree);
        super.visitIdent(tree);
    }

    @Override
    public void visitLiteral(F3Literal tree) {
        mark(tree);
        super.visitLiteral(tree);
    }

    @Override
    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        mark(tree);
        super.visitSequenceEmpty(tree);
    }

    @Override
    public void visitSequenceRange(F3SequenceRange tree) {
        mark(tree);
        super.visitSequenceRange(tree);
    }

    @Override
    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        mark(tree);
        super.visitSequenceExplicit(tree);
    }

    @Override
    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        mark(tree);
        super.visitSequenceIndexed(tree);
    }

    @Override
    public void visitSequenceSlice(F3SequenceSlice tree) {
        mark(tree);
        super.visitSequenceSlice(tree);
    }

    @Override
    public void visitStringExpression(F3StringExpression tree) {
        mark(tree);
        super.visitStringExpression(tree);
    }

    @Override
    public void visitInstanciate(F3Instanciate tree) {
        mark(tree);
        super.visitInstanciate(tree);
    }

    @Override
    public void visitForExpression(F3ForExpression tree) {
        mark(tree);
        super.visitForExpression(tree);
    }

    @Override
    public void visitIndexof(F3Indexof tree) {
        mark(tree);
        super.visitIndexof(tree);
    }

    @Override
    public void visitTimeLiteral(F3TimeLiteral tree) {
        mark(tree);
        super.visitTimeLiteral(tree);
    }

    @Override
    public void visitLengthLiteral(F3LengthLiteral tree) {
        mark(tree);
        super.visitLengthLiteral(tree);
    }

    @Override
    public void visitAngleLiteral(F3AngleLiteral tree) {
        mark(tree);
        super.visitAngleLiteral(tree);
    }

    @Override
    public void visitColorLiteral(F3ColorLiteral tree) {
        mark(tree);
        super.visitColorLiteral(tree);
    }
}

