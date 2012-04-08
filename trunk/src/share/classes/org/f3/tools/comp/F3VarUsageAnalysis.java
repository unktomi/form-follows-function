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

import org.f3.tools.tree.*;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.util.Context;
import static org.f3.tools.code.F3Flags.*;
import org.f3.api.F3BindStatus;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3Check.ForwardReferenceChecker;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.Name;
import java.util.EnumSet;

/**
 *
 * @author Robert Field
 */
public class F3VarUsageAnalysis extends F3TreeScanner {
    protected static final Context.Key<F3VarUsageAnalysis> varUsageKey =
            new Context.Key<F3VarUsageAnalysis>();
    
    private boolean inLHS;
    private F3BindStatus bindStatus;
    private F3ClassDeclaration currentClass;
    private F3Types types;
    private Name.Table names;
    private F3Defs defs;
    
    public static F3VarUsageAnalysis instance(Context context) {
        F3VarUsageAnalysis instance = context.get(varUsageKey);
        if (instance == null)
            instance = new F3VarUsageAnalysis(context);
        return instance;
    }
    
    F3VarUsageAnalysis(Context context) {
        context.put(varUsageKey, this);
        names = Name.Table.instance(context);
        defs = F3Defs.instance(context);
        types = F3Types.instance(context);
        inLHS = false;
        bindStatus = F3BindStatus.UNBOUND;
    }
    
    public void analyzeVarUse(F3Env<F3AttrContext> attrEnv) {
        //TODO: if cleared, must be at script-level: new ClearOldMarks().scan(attrEnv.tree);
        scan(attrEnv.tree);
    }

    // Clear flags for vars defined in this script
    private class ClearOldMarks extends F3TreeScanner {

        private long ALL_MARKED_VARUSE =
                VARUSE_BOUND_INIT |
                VARUSE_TMP_IN_INIT_EXPR |
                VARUSE_FORWARD_REFERENCE |
                VARUSE_SELF_REFERENCE |
                VARUSE_OBJ_LIT_INIT;

        private void clearMark(Symbol sym) {
            sym.flags_field &= ~ALL_MARKED_VARUSE;
        }

        @Override
        public void visitVar(F3Var tree) {
            super.visitVar(tree);
            clearMark(tree.sym);
        }

        @Override
        public void visitOverrideClassVar(F3OverrideClassVar tree) {
            super.visitOverrideClassVar(tree);
            clearMark(tree.sym);
        }
    }

    private void mark(Symbol sym, long flag) {
        sym.flags_field |= flag;
    }

    private void markVarAccess(Symbol sym) {
        if (sym instanceof VarSymbol && (sym.flags_field & VARUSE_SPECIAL) == 0L) {
            if (bindStatus.isBound()) {
                mark(sym, VARUSE_BIND_ACCESS);
                // Accessors are necessary to send notification.
                mark(sym, VARUSE_NEED_ACCESSOR);
                if (bindStatus.isBidiBind())
                    mark(sym, VARUSE_ASSIGNED_TO);
            } else {
                if (inLHS) {
                    // note the assignment the assignment
                    mark(sym, VARUSE_ASSIGNED_TO);
                }
            }
            
            sym.flags_field &= ~VARUSE_OPT_TRIGGER;
            
            checkExternallySeen(sym);
        }
        if (sym instanceof F3VarSymbol) {
            ((F3VarSymbol)sym).setUsedOutsideSizeof();
        }
    }
    
    private void checkExternallySeen(Symbol sym) {
        if (sym instanceof F3VarSymbol && sym.owner != currentClass.sym) {
            ((F3VarSymbol)sym).setIsExternallySeen();
        }
    }

    @Override
    public void visitScript(F3Script tree) {
       inLHS = false;
       bindStatus = F3BindStatus.UNBOUND;
       super.visitScript(tree);
    }

    @Override
    public void visitVarInit(F3VarInit tree) {
        Symbol sym = tree.getVar().sym;
        if (sym instanceof F3VarSymbol) {
            ((F3VarSymbol)sym).setHasVarInit();
        }
    }

    private void scanVar(F3AbstractVar tree) {
        F3BindStatus wasBindStatus = bindStatus;
        bindStatus = tree.getBindStatus();
        if (bindStatus.isBound()) {
            mark(tree.sym, VARUSE_BOUND_INIT);
            mark(tree.sym, VARUSE_NEED_ACCESSOR);
        }
        if (tree.getInitializer() != null) {
            tree.sym.flags_field |= VARUSE_TMP_IN_INIT_EXPR;
            scan(tree.getInitializer());
            tree.sym.flags_field &= ~VARUSE_TMP_IN_INIT_EXPR;
            if (!tree.isLiteralInit()) {
                mark(tree.sym, VARUSE_NON_LITERAL);
            }
        }
        bindStatus = wasBindStatus;
        if (tree.getOnReplace() != null) {
            mark(tree.sym, VARUSE_HAS_TRIGGER);
            mark(tree.sym, VARUSE_NEED_ACCESSOR);
            scan(tree.getOnReplace());
        }
        if (tree.getOnInvalidate() != null) {
            mark(tree.sym, VARUSE_HAS_TRIGGER);
            mark(tree.sym, VARUSE_NEED_ACCESSOR);
            scan(tree.getOnInvalidate());
        }
        
        checkExternallySeen(tree.sym);
    }

    @Override
    public void visitVar(F3Var tree) {
        scanVar(tree);
    }
    
    @Override
    public void visitVarRef(F3VarRef tree) {
        mark(tree.getVarSymbol(), VARUSE_NEED_ACCESSOR | VARUSE_VARREF);
    }

    @Override
    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        scanVar(tree);
        mark(tree.sym, OVERRIDE);
    }

    @Override
    public void visitOnReplace(F3OnReplace tree) {
        if (tree.getOldValue() != null)
            mark(tree.getOldValue().sym, VARUSE_OPT_TRIGGER);
        if (tree.getNewElements() != null)
            mark(tree.getNewElements().sym, VARUSE_OPT_TRIGGER);
        super.visitOnReplace(tree);
    }

    @Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
       F3ClassDeclaration previousClass = currentClass;
       currentClass = tree;
       // these start over in a class definition
       boolean wasLHS = inLHS;
       F3BindStatus wasBindStatus = bindStatus;
       inLHS = false;
       bindStatus = F3BindStatus.UNBOUND;

       super.visitClassDeclaration(tree);
       if (tree.isScriptClass) {
           markForwardReferences(tree);
       }

       bindStatus = wasBindStatus;
       inLHS = wasLHS;
       currentClass = previousClass;
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
       // these start over in a function definition
       boolean wasLHS = inLHS;
       F3BindStatus wasBindStatus = bindStatus;
       inLHS = false;

       bindStatus = tree.getBindStatus();
       // don't use super, since we don't want to cancel the inBindContext
       for (F3Var param : tree.getParams()) {
           scan(param);
       }
       scan(tree.getBodyExpression());

       bindStatus = wasBindStatus;
       inLHS = wasLHS;
    }
   
    @Override
    public void visitFunctionValue(F3FunctionValue tree) {
       // these start over in a function value
       boolean wasLHS = inLHS;
       F3BindStatus wasBindStatus = bindStatus;
       inLHS = false;
       bindStatus = F3BindStatus.UNBOUND;

       super.visitFunctionValue(tree);

       bindStatus = wasBindStatus;
       inLHS = wasLHS;
    }

    @Override
    public void visitFunctionInvocation(F3FunctionInvocation tree) {
        super.visitFunctionInvocation(tree);
    }

    @Override
    public void visitObjectLiteralPart(F3ObjectLiteralPart tree) {
        F3BindStatus wasBindStatus = bindStatus;

        bindStatus = tree.getBindStatus();
        mark(tree.sym, VARUSE_OBJ_LIT_INIT);
        scan(tree.getExpression());

        checkExternallySeen(tree.sym);
        
        bindStatus = wasBindStatus;
    }

    @Override
    public void visitAssign(F3Assign tree) {
        boolean wasLHS = inLHS;
        inLHS = true;
        scan(tree.lhs);
        inLHS = wasLHS;
        scan(tree.rhs);
    }

    @Override
    public void visitAssignop(F3AssignOp tree) {
        boolean wasLHS = inLHS;
        inLHS = true;
        scan(tree.lhs);
        inLHS = wasLHS;
        scan(tree.rhs);
    }

    @Override
    public void visitUnary(F3Unary tree) {
        boolean wasLHS = inLHS;
        F3VarSymbol sym = null;
        boolean restoreOptTrigger = false;
        boolean restoreUsedOutsideSizeof = false;
        switch (tree.getF3Tag()) {
            case PREINC:
            case PREDEC:
            case POSTINC:
            case POSTDEC:
                inLHS = true;
                break;
            case SIZEOF:
               if (tree.arg instanceof F3Ident) {
                   sym = (F3VarSymbol) ((F3Ident) tree.arg).sym;
                   restoreOptTrigger = (sym.flags_field & VARUSE_OPT_TRIGGER) != 0;
                   restoreUsedOutsideSizeof = ! sym.isUsedOutsideSizeof();
                   sym.setUsedInSizeof();
               }
        }
        scan(tree.arg);
        if (restoreOptTrigger) {
             sym.flags_field |= VARUSE_OPT_TRIGGER;
        }
        if (restoreUsedOutsideSizeof)
            sym.clearUsedOutsideSizeof();
        inLHS = wasLHS;
    }

    @Override
    public void visitIdent(F3Ident tree) {
        markVarAccess(tree.sym);
    }
    
    @Override
    public void visitInitDefinition(F3InitDefinition that) {
        assert !inLHS : "cannot have init blocks on LHS";
        assert !bindStatus.isBound() : "cannot have init blocks on bind";

        scan((F3Block)that.getBody());

        that.sym.owner.flags_field |= CLASS_HAS_INIT_BLOCK;
    }

    @Override
    public void visitInterpolateValue(final F3InterpolateValue tree) {
        F3BindStatus wasBindStatus = bindStatus;
        bindStatus = F3BindStatus.UNIDIBIND;

        mark(tree.sym, VARUSE_OBJ_LIT_INIT);
        super.visitInterpolateValue(tree);

        bindStatus = wasBindStatus;
    }

    @Override
    public void visitSelect(F3Select tree) {
        // this may or may not be in a LHS but in either
        // event the selector is a value expression
        boolean wasLHS = inLHS;
        inLHS = false;
        scan(tree.selected);
        inLHS = wasLHS;
        
        markVarAccess(tree.sym);
    }

    @Override
    public void visitSequenceInsert(F3SequenceInsert tree) {
        boolean wasLHS = inLHS;
        inLHS = true;
        scan(tree.getSequence());
        inLHS = wasLHS;
        scan(tree.getElement());
    }

    @Override
    public void visitSequenceDelete(F3SequenceDelete tree) {
        boolean wasLHS = inLHS;
        inLHS = true;
        scan(tree.getSequence());
        inLHS = wasLHS;
        scan(tree.getElement());
    }

    @Override
    public void visitSequenceIndexed(F3SequenceIndexed tree) {
        Symbol sym;
        boolean restoreOptTrigger;
        if (tree.getSequence() instanceof F3Ident) {
            sym = ((F3Ident) tree.getSequence()).sym;
            restoreOptTrigger = (sym.flags_field & VARUSE_OPT_TRIGGER) != 0;
        }
        else {
            sym = null;
            restoreOptTrigger = false;
        }
        scan(tree.getSequence());
        if (restoreOptTrigger)
            sym.flags_field |= VARUSE_OPT_TRIGGER;
        boolean wasLHS = inLHS;
        inLHS = false;
        scan(tree.getIndex());
        inLHS = wasLHS;
    }

    @Override
    public void visitSequenceSlice(F3SequenceSlice tree) {
        scan(tree.getSequence());
        boolean wasLHS = inLHS;
        inLHS = false;
        scan(tree.getFirstIndex());
        scan(tree.getLastIndex());
        inLHS = wasLHS;
    }

    @Override
    public void visitForExpressionInClause(F3ForExpressionInClause that) {
        scan(that.getVar());
        if (bindStatus.isBound()) {
            mark(that.getVar().sym, VARUSE_ASSIGNED_TO);
        }
        Symbol sym = null;
        boolean restoreOptTrigger = false;
        F3Expression seq = that.getSequenceExpression();
        if (seq instanceof F3Ident) {
            sym = ((F3Ident) seq).sym;
            restoreOptTrigger = (sym.flags_field & VARUSE_OPT_TRIGGER) != 0;
        }
        else if (seq instanceof F3SequenceSlice) {
            F3SequenceSlice slice = (F3SequenceSlice) seq;
            F3Expression sseq = slice.getSequence();
            if (sseq instanceof F3Ident) {
                sym = ((F3Ident) sseq).sym;
                restoreOptTrigger = (sym.flags_field & VARUSE_OPT_TRIGGER) != 0;
            }
        }
        scan(seq);
        if (restoreOptTrigger)
            sym.flags_field |= VARUSE_OPT_TRIGGER;
        scan(that.getWhereExpression());
    }

    F3OnReplace findOnReplace(Symbol sym, F3OnReplace current) {
        //for
        return null;
    }

    void markForwardReferences(F3Tree tree) {
        new ForwardReferenceChecker(names, types, defs, EnumSet.allOf(ForwardReferenceChecker.ScopeKind.class)) {
            @Override
            protected void reportForwardReference(DiagnosticPosition pos, boolean selfReference, Symbol s, boolean potential) {
                if (selfReference) {
                    mark(s, VARUSE_SELF_REFERENCE);
                }
                else {
                    mark(s, VARUSE_FORWARD_REFERENCE);
                    mark(s, VARUSE_NEED_ACCESSOR);
                }
            }
        }.scan(tree);
    }
}
