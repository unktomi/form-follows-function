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
import org.f3.api.tree.SequenceSliceTree;
import org.f3.api.tree.Tree.F3Kind;
import org.f3.tools.code.F3Symtab;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import org.f3.tools.code.FunctionType;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3TypeRepresentation;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3Defs.RuntimeMethod;
import org.f3.tools.comp.F3InitializationBuilder.LiteralInitClassMap;
import org.f3.tools.comp.F3InitializationBuilder.LiteralInitVarMap;
import org.f3.tools.tree.*;
import com.sun.tools.mjavac.code.Type.MethodType;
import com.sun.tools.mjavac.jvm.Target;
import com.sun.tools.mjavac.tree.JCTree.JCFieldAccess;
import com.sun.tools.mjavac.tree.TreeInfo;
import com.sun.tools.mjavac.tree.TreeTranslator;
import java.util.Map;
import f3.lang.AngleUnit;
import f3.lang.LengthUnit;
import javax.lang.model.type.TypeKind;
import static org.f3.tools.comp.F3AbstractTranslation.Yield.*;

/**
 * Common translation mechanism
 *
 * @author Robert Field
 */

public abstract class F3AbstractTranslation
                             extends F3TranslationSupport
                             implements F3Visitor {

    /*
     * the result of translating a tree by a visit method
     */
    Result result;

    final F3OptimizationStatistics optStat;
    final Target target;

    Type targetType;
    Yield yieldKind;

    private F3ToJava toJava; //TODO: this should go away

    protected F3AbstractTranslation(Context context, F3ToJava toJava) {
        super(context);
        this.optStat = F3OptimizationStatistics.instance(context);
        this.toJava = toJava; 
        this.target = Target.instance(context);
    }

    /********** translation state tracking types and methods **********/

    protected enum ReceiverContext {
        // In a script function or script var init, implemented as a static method
        ScriptAsStatic,
        // In an instance function or instance var init, implemented as static
        InstanceAsStatic,
        // In an instance function or instance var init, implemented as an instance method
        InstanceAsInstance,
        // Should not see code in this state
        Oops
    }

    enum Yield {
        ToExpression,
        ToStatement
    }

    Yield yield() {
        return yieldKind;
    }

    F3ClassDeclaration currentClass() {
        return getAttrEnv().enclClass;
    }

    F3FunctionDefinition currentFunction() {
        return getAttrEnv().enclFunction;
    }

    void setCurrentClass(F3ClassDeclaration tree) {
        getAttrEnv().enclClass = tree;
    }

    void setCurrentFunction(F3FunctionDefinition tree) {
        getAttrEnv().enclFunction = tree;
    }

    protected F3Env<F3AttrContext> getAttrEnv() {
        return toJava.getAttrEnv();
    }

    protected ReceiverContext receiverContext() {
        return toJava.receiverContext();
    }

    protected void setReceiverContext(ReceiverContext rc) {
        toJava.setReceiverContext(rc);
    }

    protected F3ToJava toJava() {
        return toJava;
    }

    /********** Utility routines **********/

    /**
     * @return the substitutionMap
     */
    Map<Symbol, Name> getSubstitutionMap() {
        return toJava.getSubstitutionMap();
    }

    /**
     * Class symbols for classes that need a reference to the outer class.
     */
    Map<ClassSymbol, ClassSymbol> getHasOuters() {
        return toJava.getHasOuters();
    }

    /**
     * @return the literalInitClassMap
     */
    LiteralInitClassMap getLiteralInitClassMap() {
        return toJava.getLiteralInitClassMap();
    }

    /** Box up a single primitive expression. */
    JCExpression makeBox(DiagnosticPosition diagPos, JCExpression translatedExpr, Type primitiveType) {
        make.at(translatedExpr.pos());
        Type boxedType = types.boxedTypeOrType(primitiveType);
        JCExpression box;
        if (target.boxWithConstructors()) {
            Symbol ctor = lookupConstructor(translatedExpr.pos(),
                    boxedType,
                    List.<Type>nil().prepend(primitiveType));
            box = make.Create(ctor, List.of(translatedExpr));
        } else {
            Symbol valueOfSym = lookupMethod(translatedExpr.pos(),
                    names.valueOf,
                    boxedType,
                    List.<Type>nil().prepend(primitiveType));
//            JCExpression meth =makeIdentifier(valueOfSym.owner.type.toString() + "." + valueOfSym.name.toString());
            JCExpression meth = make.Select(makeType(diagPos, valueOfSym.owner.type), valueOfSym.name);
            TreeInfo.setSymbol(meth, valueOfSym);
            meth.type = valueOfSym.type;
            box = make.App(meth, List.of(translatedExpr));
        }
        return box;
    }
    /** Look up a method in a given scope.
     */
    private MethodSymbol lookupMethod(DiagnosticPosition pos, Name name, Type qual, List<Type> args) {
        return rs.resolveInternalMethod(pos, getAttrEnv(), qual, name, args, null);
    }
    //where
    /** Look up a constructor.
     */
    private MethodSymbol lookupConstructor(DiagnosticPosition pos, Type qual, List<Type> args) {
        return rs.resolveInternalConstructor(pos, getAttrEnv(), qual, args, null);
    }

    ExpressionResult convertTranslated(ExpressionResult res, DiagnosticPosition diagPos, Type targettedType) {
        return (targettedType == null || targettedType == syms.voidType) ?
              res
            : new ExpressionResult(
                diagPos,
                res.statements(),
                new TypeConversionTranslator(diagPos, res.expr(), res.resultType, targettedType).doitExpr(),
                res.bindees(),
                res.invalidators(),
                res.interClass,
                res.setterPreface(),
                targettedType);
    }

    JCExpression convertTranslated(JCExpression translated, DiagnosticPosition diagPos,
            Type sourceType, Type targettedType) {
        return (targettedType == null || targettedType == syms.voidType) ?
              translated
            : new TypeConversionTranslator(diagPos, translated, sourceType, targettedType).doitExpr();
    }

    /**
     * Special handling for Strings, Durations, Lengths, Angles, and Colors. If a value
     * assigned to one of these is null, the default value for the type must be substituted.
     * inExpr is the input expression.  outType is the desired result type.
     * expr is the result value to use in the normal case.
     * This doesn't handle the case   var ss: String = if (true) null else "Hi there, sailor"
     * But it does handle nulls coming in from Java method returns, and variables.
     */
    protected JCExpression convertNullability(final DiagnosticPosition diagPos, final JCExpression expr,
            final F3Expression inExpr, final Type outType) {
        class ConvertNullabilityTranslator extends Translator {
            ConvertNullabilityTranslator(DiagnosticPosition diagPos) { super(diagPos); }

            Result doit() {
                throw new IllegalArgumentException();
            }

            JCExpression doitExpr() {
                if (outType != syms.stringType && outType != syms.f3_DurationType
                    && outType != syms.f3_LengthType && outType != syms.f3_AngleType
                    && outType != syms.f3_ColorType) {
                    return expr;
                }

                final Type inType = inExpr.type;
                if (inType == syms.botType || inExpr.getF3Kind() == F3Kind.NULL_LITERAL) {
                    return DefaultValue(outType);
                }

                if (!types.isSameType(inType, outType) || isValueFromJava(inExpr)) {
                    JCVariableDecl daVar = TmpVar(outType, expr);
                    JCExpression toTest = id(daVar.name);
                    JCExpression cond = NEnull(toTest);
                    JCExpression ret = 
                        If (cond,
                            id(daVar.name),
                            DefaultValue(outType));
                    return BlockExpression(daVar, ret);
                }
                return expr;
            }
        }
        return new ConvertNullabilityTranslator(diagPos).doitExpr();
    }

    /********** Result types **********/

    public static abstract class Result {
        final DiagnosticPosition diagPos;
        Result(DiagnosticPosition diagPos) {
            this.diagPos = diagPos;
        }
        abstract List<JCTree> trees();
    }

    public static abstract class AbstractStatementsResult extends Result {
        private final List<JCStatement> stmts;
        AbstractStatementsResult(DiagnosticPosition diagPos, List<JCStatement> stmts) {
            super(diagPos);
            this.stmts = stmts;
        }
        public List<JCStatement> statements() {
            return stmts;
        }
        List<JCTree> trees() {
            ListBuffer<JCTree> ts = ListBuffer.lb();
            for (JCTree t : stmts) {
                ts.append(t);
            }
            return ts.toList();
        }
    }

    public static class StatementsResult extends AbstractStatementsResult {
        StatementsResult(DiagnosticPosition diagPos, List<JCStatement> stmts) {
            super(diagPos, stmts);
        }
        StatementsResult(DiagnosticPosition diagPos, ListBuffer<JCStatement> buf) {
            super(diagPos, buf.toList());
        }
        StatementsResult(JCStatement stmt) {
            super(stmt.pos(), List.of(stmt));
        }
    }

    public static class DependentPair {

        public final F3VarSymbol instanceSym;
        public final Symbol referencedSym;

        DependentPair(F3VarSymbol instanceSym, Symbol referencedSym) {
            this.instanceSym = instanceSym;
            this.referencedSym = referencedSym;
        }
    }

    /**
     * A variable (represented as a Symbol) on which the current variable depends
     * and the, optional, invalidation code to be placed in the variable's
     * invalidation method to corrected invalidate the current variable.
     */
    public static class BindeeInvalidator {

        // Variable symbols on which the current variable depends
        public final F3VarSymbol bindee;

        // Invalidation code to be placed in bindee.
        // Optional.  If null, use default invalidation of the current variable.
        public final JCStatement invalidator;

        BindeeInvalidator(F3VarSymbol bindee, JCStatement invalidator) {
            this.bindee = bindee;
            this.invalidator = invalidator;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BindeeInvalidator)) {
                return false;
            }
            BindeeInvalidator other = (BindeeInvalidator)obj;
            return other.bindee == bindee && other.invalidator == invalidator;
        }

        @Override
        public int hashCode() {
            return bindee.hashCode() + (invalidator == null ? 0 : invalidator.hashCode());
        }

        @Override
        public String toString() {
            return "BindeeInvalidator " + bindee.name + " / " + invalidator;
        }
    }

    public static class ExpressionResult extends AbstractStatementsResult {
        private final JCExpression value;
        private final List<F3VarSymbol> bindees;
        private final List<BindeeInvalidator> invalidators;
        private final List<DependentPair> interClass;
        private final List<JCStatement> setterPreface;
        private final Type resultType;

        ExpressionResult(DiagnosticPosition diagPos, List<JCStatement> stmts, JCExpression value, 
                List<F3VarSymbol> bindees, List<BindeeInvalidator> invalidators, List<DependentPair> interClass,
                List<JCStatement> setterPreface, Type resultType) {
            super(diagPos, stmts);
            this.value = value;
            this.bindees = bindees;
            this.invalidators = invalidators;
            this.interClass = interClass;
            this.setterPreface = setterPreface;
            this.resultType = resultType;
        }

        public JCExpression expr() {
            return value;
        }
        public boolean hasExpr() {
            return value != null;
        }
        // Invalidators for this variable
        public List<BindeeInvalidator> invalidators() {
            return invalidators;
        }

        public List<F3VarSymbol> bindees() {
            return bindees;
        }
        public List<DependentPair> interClass() {
            return interClass;
        }
        public List<JCStatement> setterPreface() {
            return setterPreface;
        }
        public Type resultType() {
            return resultType;
        }
        public boolean isBoundVirtualSequence() {
            return false;
        }
        public JCStatement getElementMethodBody() {
            TODO("sequence element getter for: " + value);
            return null;
        }
        public JCStatement getSizeMethodBody() {
            TODO("sequence size getter for: " + value);
            return null;
        }

        @Override
        List<JCTree> trees() {
            List<JCTree> ts = super.trees();
            return value==null? ts : ts.append(value);
        }
    }

    /**
     * Bound sequence get element / size method body pair as Result
     */
    public static class BoundSequenceResult extends ExpressionResult {
        private final JCStatement getElement;
        private final JCStatement getSize;
        BoundSequenceResult(List<F3VarSymbol> bindees, List<BindeeInvalidator> invalidators, List<DependentPair> interClass, JCStatement getElement, JCStatement getSize) {
            this(null, null, bindees, invalidators, interClass, getElement, getSize, null);
        }
        BoundSequenceResult(
                List<JCStatement> stmts, JCExpression value,
                List<F3VarSymbol> bindees, List<BindeeInvalidator> invalidators, List<DependentPair> interClass,
                JCStatement getElement, JCStatement getSize, Type resultType) {
            super(getElement.pos(), stmts, value, bindees, invalidators, interClass, null, resultType);
            this.getElement = getElement;
            this.getSize = getSize;
        }
        @Override
        public boolean isBoundVirtualSequence() {
            return true;
        }
        // Java code for getting the element of a bound sequence
        @Override
        public JCStatement getElementMethodBody() {
            return getElement;
        }
        // Java code for getting the size of a bound sequence
        @Override
        public JCStatement getSizeMethodBody() {
            return getSize;
        }
        @Override
        public String toString() {
            return "SequenceElementSizeResult- get element: " + getElement.getClass() + " = " + getElement + ", size: " + getSize.getClass() + " = " + getSize;
        }
        @Override
        List<JCTree> trees() {
            return List.<JCTree>of(getElement, getSize);
        }
    }

    public static class SpecialResult extends Result {
        private final JCTree tree;
        SpecialResult(JCTree tree) {
            super(tree.pos());
            this.tree = tree;
        }
        JCTree tree() {
            return tree;
        }
        @Override
        public String toString() {
            return "SpecialResult-" + tree.getClass() + " = " + tree;
        }
        List<JCTree> trees() {
            return tree==null? List.<JCTree>nil() : List.of(tree);
        }
    }

    /********** translation support **********/

    private void translateCore(F3Tree expr, Type targettedType, Yield yield) {
            F3Tree prevWhere = getAttrEnv().where;
            Yield prevYield = yield();
            Type prevTargetType = targetType;
            getAttrEnv().where = expr;
            yieldKind = yield;
            targetType = targettedType;
            expr.accept(this);
            yieldKind = prevYield;
            targetType = prevTargetType;
            getAttrEnv().where = prevWhere;
    }

    ExpressionResult translateToExpressionResult(F3Expression expr, Type targettedType) {
        if (expr == null) {
            return null;
        } else {
            translateCore(expr, targettedType, ToExpression);
	    ExpressionResult ret; 
	    if (this.result instanceof StatementsResult) {
		//System.err.println("result="+result);
		return new ExpressionResult(this.result.diagPos, 
					    ((StatementsResult)this.result).statements(), 
					    null, 
					    List.<F3VarSymbol>nil(), 
					    List.<BindeeInvalidator>nil(), 
					    List.<DependentPair>nil(), 
					    List.<JCStatement>nil(), 
					    targettedType);
	    } else {
		ret = (ExpressionResult)this.result;
	    }
            this.result = null;
            return ret.hasExpr()?
                  convertTranslated(ret, expr.pos(), targettedType)
                : ret;
        }
    }

    StatementsResult translateToStatementsResult(F3Expression expr, Type targettedType) {
        if (expr == null) {
            return null;
        } else {
            translateCore(expr, targettedType, ToStatement);
            Result ret = this.result;
            this.result = null;
            if (ret instanceof StatementsResult) {
                return (StatementsResult) ret; // already converted
            } else if (ret instanceof ExpressionResult) {
                return new StatementsResult(expr.pos(), asStatements((ExpressionResult) ret, targettedType));
            } 
        }
	return null;
    }

    class JCConverter extends JavaTreeBuilder {

        private final AbstractStatementsResult res;
        private final Type type;

        JCConverter(AbstractStatementsResult res, Type type) {
            super(res.diagPos, currentClass(), receiverContext() == ReceiverContext.ScriptAsStatic);
	    if (type == null) {
		//throw new Error("type is null: "+res);
	    }
            this.res = res;
            this.type = type;
        }

        List<JCStatement> asStatements() {
            int typeTag = type.tag; // Blow up if we are passed null as the type of statements
            List<JCStatement> stmts = res.statements();
            if (res instanceof ExpressionResult) {
                ExpressionResult eres = (ExpressionResult) res;
                JCExpression expr = eres.expr();
                if (expr != null) {
                    stmts = stmts.append(Stmt(convertedExpression(eres), type));
                }
            }
            return stmts;
        }

        JCStatement asStatement() {
            List<JCStatement> stmts = asStatements();
            if (stmts.length() == 1) {
                return stmts.head;
            } else {
                return asBlock();
            }
        }

        JCBlock asBlock() {
            return Block(asStatements());
        }

        JCExpression asExpression() {
            if (res instanceof ExpressionResult) {
                ExpressionResult er = (ExpressionResult) res;
                if (er.statements().nonEmpty()) {
                    BlockExprJCBlockExpression bexpr = new BlockExprJCBlockExpression(0L, er.statements(), convertedExpression(er));
                    bexpr.pos = er.expr().pos;
                    return bexpr;
                } else {
                    return convertedExpression(er);
                }
            } else {
                throw new IllegalArgumentException("must be ExpressionResult -- was: " + res);
            }
        }

        private JCExpression convertedExpression(ExpressionResult eres) {
            return convertTranslated(eres.expr(), diagPos, eres.resultType, type);
        }
    }

    JCBlock asBlock(AbstractStatementsResult res, Type targettedType) {
        return new JCConverter(res, targettedType).asBlock();
    }

    JCStatement asStatement(AbstractStatementsResult res, Type targettedType) {
        return new JCConverter(res, targettedType).asStatement();
    }

    List<JCStatement> asStatements(AbstractStatementsResult res, Type targettedType) {
        return new JCConverter(res, targettedType).asStatements();
    }

    JCExpression asExpression(AbstractStatementsResult res, Type targettedType) {
        return new JCConverter(res, targettedType).asExpression();
    }

    JCExpression translateToExpression(F3Expression expr, Type targettedType) {
        return asExpression(translateToExpressionResult(expr, targettedType), targettedType);
    }

    JCStatement translateToStatement(F3Expression expr, Type targettedType) {
        return asStatement(translateToStatementsResult(expr, targettedType), targettedType);
    }

    JCBlock translateToBlock(F3Expression expr, Type targettedType) {
        if (expr == null) {
            return null;
        } else {
            return asBlock(translateToStatementsResult(expr, targettedType), targettedType);
        }
    }

    JCTree translateFunction(F3FunctionDefinition tree, boolean maintainContext) {
        return new FunctionTranslator(tree, maintainContext).doit().tree();
    }

    /** Translate a single tree.
     */
    SpecialResult translateToSpecialResult(F3Tree tree) {
        SpecialResult ret;

        F3Tree prevWhere = getAttrEnv().where;
        getAttrEnv().where = tree;
        tree.accept(this);
        getAttrEnv().where = prevWhere;
        ret = (SpecialResult) this.result;
        this.result = null;
        return ret;
    }

    static class OnReplaceInfo {
        public OnReplaceInfo outer;
        F3VarSymbol vsym;
        public F3OnReplace onReplace;
        Symbol newElementsSym;
        Type arraySequenceType;
        Type seqWithExtendsType;
    }

    OnReplaceInfo onReplaceInfo;

    OnReplaceInfo findOnReplaceInfo(Symbol sym) {
        OnReplaceInfo info = onReplaceInfo;
        while (info != null && sym != info.newElementsSym)
            info = info.outer;
        return info;
    }

    /**** utility methods ******/

    private UseSequenceBuilder useSequenceBuilder(DiagnosticPosition diagPos, Type elemType, final int initLength, final boolean nonLocal) {
        return new UseSequenceBuilder(diagPos, elemType, null) {

            @Override
            JCStatement addElement(F3Expression exprToAdd) {
                JCExpression expr = translateToExpression(exprToAdd, targettedType(exprToAdd));
                JCVariableDecl varDef = TmpVar(targettedType(exprToAdd), expr);
                return nonLocal ?
                    Block(varDef, makeAdd(id(varDef))) :
                    makeAdd(expr);
            }

            @Override
            List<JCExpression> makeConstructorArgs() {
                ListBuffer<JCExpression> lb = ListBuffer.lb();
                if (initLength != -1) {
                    lb.append(make.at(diagPos).Literal(Integer.valueOf(initLength)));
                }
                if (addTypeInfoArg)
                    lb.append(TypeInfo(diagPos, elemType));
                return lb.toList();
            }

            @Override
            JCExpression makeToSequence() {
                return makeBuilderVarAccess();
            }
        };
    }

    UseSequenceBuilder useSequenceBuilder(DiagnosticPosition diagPos, Type elemType, boolean nonLocal) {
        return useSequenceBuilder(diagPos, elemType, -1, nonLocal);
    }

    abstract class UseSequenceBuilder extends JavaTreeBuilder {
        final Type elemType;
        private final String seqBuilder;
        boolean addTypeInfoArg = true;

        // Sequence builder temp var name "sb"
        private final Name sbName = getSyntheticName("sb");

        private UseSequenceBuilder(DiagnosticPosition diagPos, Type elemType, String seqBuilder) {
            super(diagPos, currentClass(), receiverContext() == ReceiverContext.ScriptAsStatic);
            this.elemType = elemType;
            this.seqBuilder = seqBuilder;
        }

        Type targettedType(F3Expression exprToAdd) {
            Type exprType = exprToAdd.type;
            if (types.isArray(exprType) || types.isSequence(exprType)) {
                return types.sequenceType(elemType);
            } else {
                Type unboxed = types.unboxedType(elemType);
                return (unboxed.tag != TypeTags.NONE) ? unboxed : elemType;
            }
        }

        JCStatement makeBuilderVar() {
            String localSeqBuilder = this.seqBuilder;
            boolean primitive = false;
            if (localSeqBuilder == null) {
                if (elemType.isPrimitive()) {
                    primitive = true;
                    addTypeInfoArg = false;
                    F3TypeRepresentation typeRep = types.typeRep(elemType);
                    localSeqBuilder = "org.f3.runtime.sequence." + F3Defs.getTypePrefix(typeRep.ordinal()) + "ArraySequence"; //TODO: put in defs
                }
                else
                    localSeqBuilder = F3Defs.cObjectArraySequence;
            }
            JCExpression builderTypeExpr = QualifiedTree(localSeqBuilder);
            JCExpression builderClassExpr = QualifiedTree(localSeqBuilder);
            if (! primitive) {
                builderTypeExpr = m().TypeApply(builderTypeExpr,
                        List.of(makeType(elemType)));
                // class name -- SequenceBuilder<elemType>
                builderClassExpr = m().TypeApply(builderClassExpr,
                        List.<JCExpression>of(makeType(elemType)));
            }

            // Build "sb" initializing expression -- new SequenceBuilder<T>(clazz)
            JCExpression newExpr = m().NewClass(
                null,                               // enclosing
                List.<JCExpression>nil(),           // type args
                builderClassExpr, // class name -- SequenceBuilder<elemType>
                makeConstructorArgs(),              // args
                null                                // empty body
                );

            // Build the sequence builder variable
            return Var(0L, builderTypeExpr, sbName, newExpr);
        }

        JCIdent makeBuilderVarAccess() {
            return id(sbName);
        }

        abstract JCStatement addElement(F3Expression expr);

        abstract List<JCExpression> makeConstructorArgs();

        JCStatement makeAdd(JCExpression expr) {
            return CallStmt(makeBuilderVarAccess(), names.fromString("add"), expr);
        }

        abstract JCExpression makeToSequence();
    }

    /****************************** Translators ******************************/

    abstract class Translator extends JavaTreeBuilder {

        Translator(DiagnosticPosition diagPos) {
            super(diagPos, currentClass(), receiverContext() == ReceiverContext.ScriptAsStatic);
            optStat.recordTranslator(this.getClass());
        }

        abstract Result doit();

        F3TreeMaker f3m() {
            return f3make.at(diagPos);
        }

        JCVariableDecl convertParam(F3Var param) {
	    //	    System.out.println("convert param: "+ param.name+" "+param.type);
            return Param(param.type, param.name);
        }
    }

    abstract class ExpressionTranslator extends Translator {

        private final ListBuffer<JCStatement> stmts = ListBuffer.lb();
        private final ListBuffer<BindeeInvalidator> invalidators = ListBuffer.lb();
        private final ListBuffer<F3VarSymbol> bindees = ListBuffer.lb();
        private final ListBuffer<DependentPair> interClass = ListBuffer.lb();
        private final ListBuffer<JCStatement> setterPreface = ListBuffer.lb();

        ExpressionTranslator(DiagnosticPosition diagPos) {
            super(diagPos);
        }

        JCExpression mergeResults(ExpressionResult res) {
            addPreface(res.statements());
            addBindees(res.bindees());
            addInterClassBindees(res.interClass());
            addInvalidators(res.invalidators());
            return res.expr();
        }

        JCExpression mergeResultsToBlockExpression(ExpressionResult res) {
            addBindees(res.bindees());
            addInterClassBindees(res.interClass());
            if (!res.statements().isEmpty()) {
                return BlockExpression(res.statements(), res.expr());
            } else {
                return res.expr();
            }
        }

        JCExpression translateExpr(F3Expression expr, Type type) {
            ExpressionResult result = translateToExpressionResult(expr, type);
            return mergeResults(result);
        }

        JCExpression translateExprToBlockExpression(F3Expression expr, Type type) {
            ExpressionResult result = translateToExpressionResult(expr, type);
            return mergeResultsToBlockExpression(result);
        }

        List<JCExpression> translateExprs(List<F3Expression> list) {
            ListBuffer<JCExpression> trans = ListBuffer.lb();
            for (List<F3Expression> l = list; l.nonEmpty(); l = l.tail) {
                JCExpression res = translateExpr(l.head, null);
                if (res != null) {
                    trans.append(res);
                }
            }
            return trans.toList();
        }

        void translateStmt(F3Expression expr, Type targettedType) {
            //TODO: My guess is that statements will need to preserve bindee info for block-expressions
            StatementsResult res = translateToStatementsResult(expr, targettedType);
            addPreface(res.statements());
        }

        void addPreface(JCStatement stmt) {
            stmts.append(stmt);
        }

        void addPreface(List<JCStatement> list) {
            stmts.appendList(list);
        }

        void addInvalidator(F3VarSymbol sym, JCStatement invStmt) {
            addInvalidator(new BindeeInvalidator(sym, invStmt));
        }

        void addInvalidators(List<BindeeInvalidator> bis) {
            for (BindeeInvalidator bi : bis)
                addInvalidator(bi);
        }

        void addInvalidator(BindeeInvalidator bi) {
            if (!invalidators.contains(bi)) {
                invalidators.append(bi);
            }
        }

        void addBindee(F3VarSymbol sym) {
            if (types.isF3Class(sym.owner)) {
                bindees.append(sym);
            }
        }

        void addBindees(List<F3VarSymbol> syms) {
            bindees.appendList(syms);
        }

        void addInterClassBindee(F3VarSymbol instanceSym, Symbol referencedSym) {
            if (types.isF3Class(instanceSym.owner) && types.isF3Class(referencedSym.owner)) {
                interClass.append(new DependentPair( instanceSym,  referencedSym));
            }
        }

        void addInterClassBindees(List<DependentPair> pairs) {
            for (DependentPair pair : pairs) {
                interClass.append(pair);
            }
        }

        void addSetterPreface(JCStatement stmt) {
            setterPreface.append(stmt);
        }

        ExpressionResult toResult(JCExpression translated, Type resultType) {
            return new ExpressionResult(diagPos, statements(), translated, bindees(), invalidators(), interClass(), setterPreface(), resultType);
        }

        StatementsResult toStatementResult(JCExpression translated, Type resultType, Type targettedType) {
            return toStatementResult(
                    (targettedType == null || targettedType == syms.voidType) ?
                          Stmt(translated)
                        : Return(
                            convertTranslated(translated, diagPos, resultType, targettedType)));
        }

        StatementsResult toStatementResult(JCStatement translated) {
            assert invalidators.length() == 0;
            return new StatementsResult(diagPos, stmts.append(translated));
        }

         StatementsResult toStatementResult() {
            assert invalidators.length() == 0;
            return new StatementsResult(diagPos, stmts);
        }

        List<JCStatement> statements() {
            return stmts.toList();
        }

        List<F3VarSymbol> bindees() {
            return bindees.toList();
        }

        List<BindeeInvalidator> invalidators() {
            return invalidators.toList();
        }

        List<DependentPair> interClass() {
            return interClass.toList();
        }

        List<JCStatement> setterPreface() {
            return setterPreface.toList();
        }

        @Override
        abstract AbstractStatementsResult doit();

        JCExpression staticReference(Symbol sym) {
            Symbol owner = sym.owner;
            Symbol encl = currentClass().sym;
            if (encl.name.endsWith(defs.scriptClassSuffixName) && owner == encl.owner) {
                return null;
            } else {
                //TODO: see init builder getStaticContext() for a better implementation
                Type classType = types.erasure(owner.type);
                return makeType(classType, false);
            }
        }
        
        JCExpression reference(Symbol sym) {
            if (sym.isStatic()) {
                return Select(staticReference(sym), sym.name);
            } else if (sym.isLocal()) {
                return id(sym);
            } else {
                return Select(getReceiver(sym), sym.name);
            }
        }

        JCExpression translateSizeof(F3Expression expr, JCExpression transExpr) {
            if (expr instanceof F3Ident) {
                F3Ident varId = (F3Ident) expr;
                OnReplaceInfo info = findOnReplaceInfo(varId.sym);
                if (info != null) {
                    return id(paramNewElementsLengthName(info.onReplace));
                }
            }
            return Call(defs.Sequences_size, transExpr);
        }

   }

    class LiteralTranslator extends ExpressionTranslator {

        protected final F3Literal tree;

        LiteralTranslator(F3Literal tree) {
            super(tree.pos());
            this.tree = tree;
        }

        @Override
        protected ExpressionResult doit() {
            if (tree.typetag == TypeTags.BOT && types.isSequence(tree.type)) {
                throw new AssertionError("Should have been converted");
            }
            // Just translate to literal value
            return toResult(m().Literal(tree.typetag, tree.value), tree.type);
        }
    }

    class StringExpressionTranslator extends ExpressionTranslator {

        private final F3StringExpression tree;
        StringExpressionTranslator(F3StringExpression tree) {
            super(tree.pos());
            this.tree = tree;
        }

        protected ExpressionResult doit() {
            StringBuffer sb = new StringBuffer();
            List<F3Expression> parts = tree.getParts();
            ListBuffer<JCExpression> values = new ListBuffer<JCExpression>();

            F3Literal lit = (F3Literal) (parts.head);            // "...{
            sb.append((String) lit.value);
            parts = parts.tail;
            boolean containsDateTimeFormat = false;

            while (parts.nonEmpty()) {
                lit = (F3Literal) (parts.head);                  // optional format (or null)
                String format = (String) lit.value;
                if ((!containsDateTimeFormat) && format.length() > 0
                    && F3Defs.DATETIME_FORMAT_PATTERN.matcher(format).find()) {
                    containsDateTimeFormat = true;
                }
                parts = parts.tail;
                F3Expression exp = parts.head;
                JCExpression texp;
                if (exp != null && types.isSameType(exp.type, syms.f3_DurationType)) {
                    texp = Call(translateExpr(exp, syms.f3_DurationType), defs.toMillis_DurationMethodName);
                    texp = typeCast(syms.f3_LongType, syms.f3_DoubleType, texp);
                    sb.append(format.length() == 0 ? "%dms" : format);
                } else {
                    texp = translateExpr(exp, null);
                    sb.append(format.length() == 0 ? "%s" : format);
                }
                values.append(texp);
                parts = parts.tail;

                lit = (F3Literal) (parts.head);                  // }...{  or  }..."
                String part = (String)lit.value;
                sb.append(part.replace("%", "%%"));              // escape percent signs
                parts = parts.tail;
            }
            values.prepend(String(sb.toString()));
            RuntimeMethod formatMethod;
            if (tree.translationKey != null) {
                formatMethod = defs.StringLocalization_getLocalizedString;
                if (tree.translationKey.length() == 0) {
                    values.prepend(Null());
                } else {
                    values.prepend(String(tree.translationKey));
                }
                String resourceName =
                       currentClass().sym.flatname.toString().replace('.', '/').replaceAll("\\$.*", "");
                values.prepend(String(resourceName));
            } else if (containsDateTimeFormat) {
                formatMethod = defs.F3Formatter_sprintf;
            } else {
                formatMethod = defs.String_format;
            }
            return toResult(
                    Call(formatMethod, values),
                    syms.stringType);
        }
    }

    abstract class MemberReferenceTranslator extends ExpressionTranslator {

        protected MemberReferenceTranslator(DiagnosticPosition diagPos) {
            super(diagPos);
        }

        JCExpression convertVariableReference(JCExpression varRef, Symbol sym) {
            JCExpression expr = varRef;

            if (sym instanceof VarSymbol) {
                final F3VarSymbol vsym = (F3VarSymbol) sym;
                boolean isF3MemberVar = vsym.isF3Member();

                if (isF3MemberVar) {
                    // this is a reference to a F3 class variable, use getter
                    JCExpression instance;
                    // find referenced instance, null for current
                    switch (expr.getTag()) {
                        case JCTree.IDENT:
                            // if we are in a mixin class reference variables through the receiver
                            instance = currentClass().isMixinClass()?
                                  id(defs.receiverName)
                                : null;
                            break;
                        case JCTree.SELECT:
                            instance = ((JCFieldAccess) varRef).getExpression();
                            break;
                        default:
                            throw new AssertionError();
                    }
                    expr = makeAccess(instance, vsym);
                }
            }

            return expr;
        }

        JCExpression makeAccess(JCExpression instance, F3VarSymbol vsym) {
            return Getter(instance, vsym);
        }

    }

    abstract class NullCheckTranslator extends MemberReferenceTranslator {

        protected final Symbol refSym;             //
        protected final Type fullType;             // Type, before conversion, of expression
        protected final Type resultType;           // Type of final generated expression
        protected boolean staticReference;   // Is this a static reference

        NullCheckTranslator(DiagnosticPosition diagPos, Symbol sym, Type fullType) {
            super(diagPos);
            this.refSym = sym;
            this.fullType = fullType;
            this.resultType = targetType==null? fullType : targetType; // use targetType, if any
            this.staticReference = refSym != null && refSym.isStatic();
        }

        abstract F3Expression getToCheck();

        abstract JCExpression fullExpression(JCExpression mungedToCheckTranslated);

        boolean needNullCheck() {
            return getToCheck() != null && !staticReference && !getToCheck().type.isPrimitive() && possiblyNull(getToCheck());
        }

        boolean canChange() {
            return getToCheck() != null && !getToCheck().type.isPrimitive() && possiblyNull(getToCheck());
        }

        protected JCExpression preserveSideEffects(Type type, F3Expression expr, JCExpression trans) {
            if (needNullCheck() && expr!=null && hasSideEffects(expr)) {
                // if there is going to be a null check (which thus could keep expr
                // from being evaluated), and expr has side-effects, then eval
                // it first and put it in a temp var.
                return addTempVar(type, trans);
            } else {
                // no side-effects, just pass-through
                return trans;
            }
        }

        protected JCExpression addTempVar(Type varType, JCExpression trans) {
            if (varType == syms.voidType) {
                // If void is passed in then the temp will be ignored.
                return trans;
            } else {
		Type normType = types.normalize(varType);
                JCVariableDecl tmpVar = TmpVar("pse", 
					       normType,
					       trans);
		//System.err.println("trans="+trans);
		//System.err.println("vartype="+varType);
		//System.err.println("normtype="+normType);
		//System.err.println("tmpVar="+tmpVar);
                addPreface(tmpVar);
                return id(tmpVar);
            }
        }

        /**
         * Translate the 'toCheck' of 'toCheck.name'.
         * Override to specialize the translation.
         * Note: 'toCheck'  may or may not be in a LHS but in either
         * event the selector is a value expression
         */
        JCExpression translateToCheck(F3Expression expr) {
            if (staticReference) {
                return staticReference(refSym);
            } else if (expr == null) {
                if (refSym != null && refSym.owner.kind == Kinds.TYP) {
                    // it is a non-static attribute or function class member
                    // reference it through the receiver
                    return getReceiver(refSym);
                }
                return null;
            }
            Symbol selectorSym = expressionSymbol(expr);
            // If this is OuterClass.memberName or MixinClass.memberName, then
            // we want to create expression to get the proper receiver.
            if (selectorSym != null && selectorSym.kind == Kinds.TYP) {
                return getReceiver(refSym);
            }
            Type exprType = expr.type;

            // translate normally, preserving side-effects if need be
            JCExpression tExpr = preserveSideEffects(exprType, expr, translateExpr(expr, exprType));

            // if expr is primitve, box it
            // expr.type is null for package symbols.
            if (exprType != null && exprType.isPrimitive()) {
                return makeBox(diagPos, tExpr, exprType);
            }

            return tExpr;
        }

        @Override
        protected AbstractStatementsResult doit() {
            JCExpression tToCheck = translateToCheck(getToCheck());
            JCExpression full = fullExpression(tToCheck);
            full = convertTranslated(full, diagPos, fullType, resultType);
            if (yield() == ToStatement) {
                // a statement is the desired result of the translation
                return toStatementResult(wrapInNullCheckStatement(full, tToCheck, resultType, fullType));
            } else {
                // an expression is the desired result of the translation, convert it to a conditional expression
                // if it would dereference null, then the full expression instead yields the default value
                return toResult(wrapInNullCheckExpression(full, tToCheck, resultType, fullType), resultType);
            }
        }
        
        protected JCExpression copyOfTranslatedToCheck(JCExpression tToCheck) {
            // Make an expression to use in null test.
            // If translated toCheck is an identifier (tmp var or not), just make a new identifier.
            // Otherwise, retranslate.
            return (tToCheck instanceof JCIdent) ? id(((JCIdent)tToCheck).name) : translateToCheck(getToCheck());
        }

        private JCExpression makeNullCheckCondition(JCExpression tToCheck) {
            return NEnull(copyOfTranslatedToCheck(tToCheck));
        }

        private JCExpression makeDefault(Type theResultType, Type theFullType) {
            JCExpression defaultValue = DefaultValue(theFullType);
            // Return default value for Pointer type to be null
            return defaultValue.type == syms.botType ?
                DefaultValue(theResultType) :
                (types.isSameType(theResultType, syms.f3_PointerType)) ?
                Null() :
                convertTranslated(defaultValue, diagPos, theFullType, theResultType);
        }

        protected JCExpression wrapInNullCheckExpression(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
            if (needNullCheck()) {
                // Do a null check
                // we have a testable guard for null, test before the invoke (boxed conversions don't need a test)
                // an expression is the desired result of the translation, convert it to a conditional expression
                // if it would dereference null, then the full expression instead yields the default value
                return 
                    If (makeNullCheckCondition(tToCheck),
                        full,
                        makeDefault(theResultType, theFullType));
            } else {
                return full;
            }
        }

        protected JCStatement wrapInNullCheckStatement(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
            if (needNullCheck()) {
                // Do a null check
                // we have a testable guard for null, test before the invoke (boxed conversions don't need a test)
                // a statement is the desired result of the translation, return the If-statement
                JCStatement nullAction = null;
                if (theResultType != null && theResultType != syms.voidType) {
                    nullAction = Stmt(makeDefault(theResultType, theFullType), theResultType);
                }
                return If(makeNullCheckCondition(tToCheck), 
                        Stmt(full, theResultType),
                        nullAction);
            } else {
                return Stmt(full, theResultType);
            }
        }

        private boolean possiblyNull(F3Expression expr) {
            if (expr == null) {
                return true;
            }
            switch (expr.getF3Tag()) {
               case ASSIGN:
                   return possiblyNull(((F3Assign)expr).getExpression());
               case APPLY:
                   return true;
               case BLOCK_EXPRESSION:
                   return possiblyNull(((F3Block)expr).getValue());
               case IDENT: {
                   if (((F3Ident)expr).sym instanceof VarSymbol) {
                       Symbol sym = ((F3Ident)expr).sym;
                       return sym.name != names._this && sym.name != names._super;
                   } else {
                       return false;
                   }
               }
               case CONDEXPR:
                   return possiblyNull(((F3IfExpression)expr).getTrueExpression()) || possiblyNull(((F3IfExpression)expr).getFalseExpression());
               case LITERAL:
                   return expr.getF3Kind() == F3Kind.NULL_LITERAL;
               case PARENS:
                   return possiblyNull(((F3Parens)expr).getExpression());
               case SELECT:
                   return ((F3Select)expr).sym instanceof VarSymbol;
               case SEQUENCE_INDEXED:
                   return true;
               case TYPECAST:
                   return possiblyNull(((F3TypeCast)expr).getExpression());
               case VAR_DEF:
                   return possiblyNull(((F3Var)expr).getInitializer());
                default:
                    return false;
            }
        }
    }

    class SelectTranslator extends NullCheckTranslator {

        protected final F3Select tree;
        protected final Name name;

        protected SelectTranslator(F3Select tree) {
            super(tree.pos(), tree.sym, tree.type);
            this.tree = tree;
            this.name = tree.getIdentifier();
        }

        @Override
        boolean needNullCheck() {
            return !tree.nullCheck && super.needNullCheck();
        }

        @Override
        F3Expression getToCheck() {
            return tree.getExpression();
        }

        @Override
        JCExpression fullExpression(JCExpression tToCheck) {
            JCExpression translated = Select(tToCheck, name);
            return convertVariableReference(translated, refSym);
        }
    }

    class SelectElementTranslator extends NullCheckTranslator {

        private final F3Select tree;
        private final Name name;
        private final JCExpression tIndex;

        private SelectElementTranslator(F3Select tree, JCExpression tIndex) {
            super(tree.pos(), tree.sym, types.elementType(tree.type));
            this.tree = tree;
            this.name = tree.getIdentifier();
            this.tIndex = tIndex;
        }

        @Override
        boolean needNullCheck() {
            return tree.nullCheck && super.needNullCheck();
        }

        @Override
        F3Expression getToCheck() {
            return tree.getExpression();
        }

        @Override
        JCExpression fullExpression(JCExpression tToCheck) {
            JCExpression translated = Select(tToCheck, name);
            return convertVariableReference(translated, refSym);
        }

        @Override
        JCExpression makeAccess(JCExpression instance, F3VarSymbol vsym) {
            return Call(instance, attributeGetElementName(vsym), tIndex);
        }
    }

    class IdentElementTranslator extends IdentTranslator {

        private final JCExpression tIndex;

        IdentElementTranslator(F3Ident tree, JCExpression tIndex) {
            super(tree);
            this.tIndex = tIndex;
        }

        @Override
        JCExpression makeAccess(JCExpression instance, F3VarSymbol vsym) {
            return Call(instance, attributeGetElementName(vsym), tIndex);
        }
    }

    class FunctionCallTranslator extends NullCheckTranslator {

        // Function determination
        protected final F3Expression meth;
        protected final F3Expression selector;
        protected final boolean thisCall;
        protected final boolean superCall;
        protected final MethodSymbol msym;
        protected final Symbol funcSym;
        protected final Symbol selectorSym;
        protected final boolean renameToThis;
        protected final boolean renameToSuper;
        protected final boolean superToStatic;
        protected final boolean useInvoke;
        protected final boolean callBound;
        protected final boolean magicPointerMakeFunction;
        protected final boolean selectorNullCheck;

        // Call info
        protected final List<F3Expression> typeargs;
        protected final List<F3Expression> args;

        // Null Checking control
        protected final boolean knownNonNull;

        // are we in the middle of translating argument expressions?
        private boolean inTranslateArgs;
        // preface statements generated when translating argument expressions
        private ListBuffer<JCStatement> argumentPreface;

        FunctionCallTranslator(final F3FunctionInvocation tree) {
            // If this is an invoke (later "useInvoke") then the named meth is not the refSym
            // since it will be wrapped with a ".invoke()"
            super(
                    tree.pos(),
                    (tree.meth.type instanceof FunctionType)? null : expressionSymbol(tree.meth),
                    tree.type);

            // Function determination
            meth = tree.meth;
            F3Select fieldAccess = meth.getF3Tag() == F3Tag.SELECT ? (F3Select) meth : null;
            selector = fieldAccess != null ? fieldAccess.getExpression() : null;
            selectorNullCheck = fieldAccess != null ? fieldAccess.nullCheck : false;
            msym = (refSym instanceof MethodSymbol) ? (MethodSymbol) refSym : null;
            funcSym = expressionSymbol(tree.meth); //either MethodSymbol or VarSymbol
            Name selectorIdName = (selector != null && selector.getF3Tag() == F3Tag.IDENT) ? ((F3Ident) selector).getName() : null;
            thisCall = selectorIdName == names._this;
            superCall = selectorIdName == names._super;
            ClassSymbol csym = currentClass().sym;

            useInvoke = (msym == null) || (meth.type instanceof FunctionType);
	    //System.err.println("meth.type="+meth.type.getClass());
	    //System.err.println("useInvoke="+useInvoke);
            selectorSym = selector != null? expressionSymbol(selector) : null;
            boolean namedSuperCall = isNamedSuperCall();
            boolean isMixinSuper = namedSuperCall && (selectorSym.flags_field & F3Flags.MIXIN) != 0;
            boolean canRename = namedSuperCall && !isMixinSuper;
            renameToThis = canRename && selectorSym == csym;
            renameToSuper = canRename && selectorSym != csym;
            superToStatic = (superCall || namedSuperCall) && isMixinSuper;

            callBound = msym != null && !useInvoke &&
                  ((msym.flags() & F3Flags.BOUND) != 0);

            magicPointerMakeFunction = types.isSyntheticPointerFunction(msym);

            // Call info
            this.typeargs = tree.getTypeArguments();
            this.args = tree.getArguments();

            // Null Checking control
            boolean selectorImmutable =
                    msym == null ||
                    msym.isStatic() ||
                    selector == null ||
                    selector.type.isPrimitive() ||
                    namedSuperCall ||
                    superCall ||
                    thisCall;
            knownNonNull =  selectorImmutable && !useInvoke;
       }

        @Override
        void addPreface(JCStatement stat) {
            if (inTranslateArgs) {
                // translating args
                if (argumentPreface == null) {
                    argumentPreface = new ListBuffer<JCStatement>();
                }
                // put it in argument preface statements
                argumentPreface.append(stat);
            } else {
                super.addPreface(stat);
            }
        }

        @Override
        JCExpression translateToCheck(F3Expression expr) {
            JCExpression newExpr = null;
            if (renameToSuper || superCall) {
                newExpr = resolveSuper(funcSym.owner);
            } else if (renameToThis || thisCall) {
                newExpr = getReceiver(funcSym);
            } else if (superToStatic) {
                newExpr = staticReference(funcSym);
            }
            if (newExpr != null) {
                return !useInvoke ? newExpr :
                    Select(newExpr, attributeValueName(funcSym));
            }
            else {
                return super.translateToCheck(expr);
            }
        }

        private boolean isNamedSuperCall() {
            if (msym == null || msym.isStatic() ||
                    !(selectorSym instanceof ClassSymbol))
                return false;
            else {
                Type currentType = currentClass().type;
                while (currentType != Type.noType) {
                    // selector type is same as some enclosing type, so not a super call
                    if (types.isSameType(currentType, selectorSym.type)) {
                        return false;
                    }
                    if (types.isSubtype(currentType, selectorSym.type)) {
                        return true;
                    }
                    currentType = currentType.getEnclosingType();
                }
                return false;
            }
        }

        JCExpression fullExpression(JCExpression mungedToCheckTranslated) {
            JCExpression tMeth = Select(mungedToCheckTranslated, methodName());
            JCMethodInvocation app = m().Apply(translateExprs(typeargs), tMeth, determineArgs());

            JCExpression full = null;
            if (callBound) {
                // Call to bound functions in bind context is handled elsewhere.

                /*
                 * call Pointer.get() on the return value of bound function to get
                 * computed value of bound function. We need to cast to the right
                 * type as Pointer.get() returns Object type value.
                 */
                full = castFromObject(Call(app, defs.get_PointerMethodName), resultType);
            } else {
                full = app;
                if (useInvoke) {
                    if (resultType != syms.voidType) {
                        full = typeCast(resultType, syms.objectType, full);
                    }
                }
            }
            return full;
        }

        @Override
        protected JCStatement wrapInNullCheckStatement(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
            JCStatement resStmt = super.wrapInNullCheckStatement(full, tToCheck, resultType, fullType);
            if (argumentPreface != null) {
                argumentPreface.append(resStmt);
                resStmt = m().Block(0L, argumentPreface.toList());
            }
            return resStmt;
        }

        @Override
        protected JCExpression wrapInNullCheckExpression(JCExpression full, JCExpression tToCheck, Type theResultType, Type theFullType) {
            full = super.wrapInNullCheckExpression(full, tToCheck, resultType, fullType);
            if (argumentPreface != null) {
                full = new BlockExprJCBlockExpression(0L, argumentPreface.toList(), full);
            }
            return full;
        }

        Name methodName() {
            return useInvoke? defs.invoke_F3ObjectMethodName : (msym == null ? funcSym.name : functionName(msym, superToStatic, callBound));
        }

        @Override
        F3Expression getToCheck() {
            return useInvoke? meth : selector;
        }

        @Override
        boolean needNullCheck() {
            return !selectorNullCheck && !knownNonNull && super.needNullCheck();
        }

        // make it final and set "inTranslateArgs" flag
        final List<JCExpression> determineArgs() {
            try {
                inTranslateArgs = true;
                return determineArgsImpl();
            } finally {
                inTranslateArgs = false;
            }
        }

        /**
         * Compute the translated arguments.
         */
        List<JCExpression> determineArgsImpl() {
            final List<Type> formals = meth.type.getParameterTypes();
            final boolean usesVarArgs = args != null && msym != null &&
                    (msym.flags() & Flags.VARARGS) != 0 &&
                    (formals.size() != args.size() ||
                    types.isConvertible(args.last().type,
                    types.elemtype(formals.last())));
            ListBuffer<JCExpression> targs = ListBuffer.lb();
            // if this is a super.foo(x) call, "super" will be translated to referenced class,
            // so we add a receiver arg to make a direct call to the implementing method  MyClass.foo(receiver$, x)
            if (superToStatic) {
                targs.append(id(defs.receiverName));
            }

            if (callBound) {
                // This section handles argument expression for bound function calls
                // from non-bind call site.

                /*
                 * For each source bound function argument expression, we create a wrapper
                 * variable of type F3Constant and pass it along with F3Constant.VOFF$value
                 * as the arguments to the bound function.
                 */
                Type formal = null;
                List<Type> t = formals;
                for (F3Expression arg : args) {
                    formal = t.head;
                    t = t.tail;
                    // pass F3Constant wrapper for argument expression
                    targs.append(Call(defs.F3Constant_make, translateExpr(arg, formal)));

                    // pass F3Constant.VOFF$value as offset value
                    targs.append(Select(makeType(syms.f3_ConstantType), defs.varOFF$valueName));
                }
            } else {
                boolean handlingVarargs = false;
                Type formal = null;
                List<Type> t = formals;
                ListBuffer<JCExpression> rargs = null;
                int argNum = 0;
                for (List<F3Expression> l = args; l.nonEmpty(); l = l.tail) {
                    F3Expression arg = l.head;
                    if (!handlingVarargs) {
                        formal = t.head;
                        t = t.tail;
                        if (usesVarArgs && t.isEmpty()) {
                            formal = types.elemtype(formal);
                            handlingVarargs = true;
                        }
                    }
                    JCExpression argExpr = translateArg(arg, formal);
                    
                    // Proper cast to Object.
                    if (useInvoke) {
                        argExpr = typeCast(types.boxedTypeOrType(formal), formal, argExpr);
                    }
                    if (! useInvoke || argNum < 2)
                        targs.append(argExpr);
                    else {
                        if (rargs == null)
                            rargs = ListBuffer.lb();
                        rargs.append(argExpr);
                    }
                    argNum++;
                }
                if (useInvoke) {
                    for (; argNum < 2; argNum++)
                        targs.append(Null()); // arg1, arg2
                    if (argNum <= 2)
                        targs.append(Null());
                    else
                        targs.append(m().NewArray(makeType(syms.objectType), List.of(Int(argNum-2)), rargs.toList()));
                }
                if (magicPointerMakeFunction) {
                    // Pointer.make has just two arguments (inst, varNum) -- we need to
                    // add an extra argument - so that the Pointer.make(Type, F3Object, int) is called.
                    F3VarRef varRef = (F3VarRef)args.head;
                    JCExpression varType = makeKeyValueTargetType(varRef.getVarSymbol().type);
                    targs.prepend(varType);
                }
            }
            return targs.toList();
        }

        JCExpression translateArg(F3Expression arg, Type formal) {
            return preserveSideEffects(formal, arg, translateExpr(arg, formal));
        }
    }

    class TimeLiteralTranslator extends ExpressionTranslator {

        F3Expression value;

        TimeLiteralTranslator(F3TimeLiteral tree) {
            super(tree.pos());
            this.value = tree.value;
        }

        protected ExpressionResult doit() {
            return toResult(
                    Call(defs.Duration_valueOf, translateExpr(value, syms.doubleType)),
                    syms.f3_DurationType);
        }
    }

    class LengthLiteralTranslator extends ExpressionTranslator {

        F3Expression value;
        LengthUnit units;

        LengthLiteralTranslator(F3LengthLiteral tree) {
            super(tree.pos());
            this.value = tree.value;
            this.units = tree.units;
        }

        protected ExpressionResult doit() {
            JCExpression unitSelect = Select(makeType(syms.f3_LengthUnitType), names.fromString(units.name()));
            return toResult(
                    Call(defs.Length_valueOf, translateExpr(value, syms.doubleType), unitSelect),
                    syms.f3_LengthType);
        }
    }

    class AngleLiteralTranslator extends ExpressionTranslator {

        F3Expression value;
        AngleUnit units;

        AngleLiteralTranslator(F3AngleLiteral tree) {
            super(tree.pos());
            this.value = tree.value;
            this.units = tree.units;
        }

        protected ExpressionResult doit() {
            JCExpression unitSelect = Select(makeType(syms.f3_AngleUnitType), names.fromString(units.name()));
            return toResult(
                    Call(defs.Angle_valueOf, translateExpr(value, syms.doubleType), unitSelect),
                    syms.f3_AngleType);
        }
    }

    class ColorLiteralTranslator extends ExpressionTranslator {

        F3Expression value;

        ColorLiteralTranslator(F3ColorLiteral tree) {
            super(tree.pos());
            this.value = tree.value;
        }

        protected ExpressionResult doit() {
            return toResult(
                    Call(defs.Color_valueOf, translateExpr(value, syms.intType), m().Literal(TypeTags.BOOLEAN, 1)),
                    syms.f3_ColorType);
        }
    }

    class FunctionTranslator extends Translator {

        final F3FunctionDefinition tree;
        final boolean maintainContext;
        final MethodType mtype;
        final MethodSymbol sym;
        final Symbol owner;
        final Name name;
        final boolean isBound;
        final boolean isRunMethod;
        final boolean isAbstract;
        final boolean isStatic;
        final boolean isSynthetic;
        final boolean isInstanceFunction;
        final boolean isInstanceFunctionAsStaticMethod;
        final boolean isMixinClass;

        FunctionTranslator(F3FunctionDefinition tree, boolean maintainContext) {
            super(tree.pos());
            this.tree = tree;
            this.maintainContext = maintainContext;
            this.mtype = (MethodType)tree.type.asMethodType();
            this.sym = (MethodSymbol) tree.sym;
            this.owner = sym.owner;
            this.name = tree.name;
            this.isBound = (sym.flags() & F3Flags.BOUND) != 0;
            this.isRunMethod = syms.isRunMethod(tree.sym);
            this.isMixinClass = currentClass().isMixinClass();
            long originalFlags = tree.mods.flags;
            this.isAbstract = (originalFlags & Flags.ABSTRACT) != 0L;
            this.isSynthetic = (originalFlags & Flags.SYNTHETIC) != 0L;
            this.isStatic = (originalFlags & Flags.STATIC) != 0L;
            this.isInstanceFunction = !isAbstract && !isStatic && !isSynthetic;
            this.isInstanceFunctionAsStaticMethod = isInstanceFunction && isMixinClass;
        }

        private JCBlock makeRunMethodBody(F3Block bexpr) {
            final F3Expression value = bexpr.value;
            JCBlock block;
            if (value == null || value.type == syms.voidType) {
                // the block has no value: translate as simple statement and add a null return
                block = translateToBlock(bexpr, syms.voidType);
                clearDiagPos();
                block.stats = block.stats.append(Return(Null()));
            } else {
                // block has a value, return it
                block = translateToBlock(bexpr, value.type);
                final Type valueType = value.type;
                if (valueType != null && valueType.isPrimitive()) {
                    // box up any primitives returns so they return Object -- the return type of the run method
                    new TreeTranslator() {

                        @Override
                        public void visitReturn(JCReturn tree) {
                            tree.expr = makeBox(tree.expr.pos(), tree.expr, valueType);
                            result = tree;
                        }
                        // do not descend into inner classes

                        @Override
                        public void visitClassDef(JCClassDecl tree) {
                            result = tree;
                        }
                    }.translate(block);
                }
            }
            return block;
        }

        private long methodFlags() {
            long methodFlags = tree.mods.flags;
            methodFlags &= ~Flags.PROTECTED;
            methodFlags &= ~Flags.SYNTHETIC;
            methodFlags |= Flags.PUBLIC;
            if (isInstanceFunctionAsStaticMethod) {
                methodFlags |= Flags.STATIC;
            }
            return methodFlags;
        }

        private List<JCVariableDecl> methodParameters() {
            ListBuffer<JCVariableDecl> params = ListBuffer.lb();
            if (isInstanceFunctionAsStaticMethod) {
                // if we are converting a standard instance function (to a static method), the first parameter becomes a reference to the receiver
                params.prepend(ReceiverParam(currentClass()));
            }
            if (isBound) {
                for (F3Var f3Var : tree.getParams()) {
                    params.append(Param(syms.f3_ObjectType,
                            boundFunctionObjectParamName(f3Var.name)));
                    params.append(Param(syms.f3_IntegerType,
                            boundFunctionVarNumParamName(f3Var.name)));
                }
            } else {
                for (F3Var f3Var : tree.getParams()) {
                    params.append(convertParam(f3Var));
                }
            }
            return params.toList();
        }

        private JCBlock methodBody() {
            // construct the body of the translated function
            F3Block bexpr = tree.getBodyExpression();
            JCBlock body;
            if (bexpr == null) {
                body = null; // null if no block expression
            } else if (isRunMethod) {
                // it is a module level run method, do special translation
                body = makeRunMethodBody(bexpr);
            } else {
                // the "normal" case
                ListBuffer<JCStatement> stmts = ListBuffer.lb();
                if (! isBound) {
                    for (F3Var f3Var : tree.getParams()) {
                        if (types.isSequence(f3Var.sym.type)) {
                            setDiagPos(f3Var);
                            stmts.append(CallStmt(id(f3Var.getName()), defs.incrementSharing_SequenceMethodName));
                        }
                    }
                } // else FIXME: what should we do for bound function sequence params?

                setDiagPos(bexpr);
                stmts.appendList(translateToStatementsResult(bexpr, isBound? syms.f3_PointerType : mtype.getReturnType()).statements());
                body = Block(stmts);
                body.endpos = bexpr.endpos;
            }

            if (isInstanceFunction && !isMixinClass) {
                //TODO: unfortunately, some generated code still expects a receiver$ to always be present.
                // In the instance as instance case, there is no receiver param, so allow generated code
                // to function by adding:   var receiver = this;
                //TODO: this should go away
                clearDiagPos();
                body.stats = body.stats.prepend( m().VarDef(
                        m().Modifiers(Flags.FINAL),
                        defs.receiverName,
                        id(interfaceName(currentClass())),
                        id(names._this)));
            }
            return body;
        }

        private JCMethodDecl makeMethod(long flags, JCBlock body, List<JCVariableDecl> params) {
	    ListBuffer<Type> targsBuf = ListBuffer.lb();
	    if (isInstanceFunctionAsStaticMethod) {
		targsBuf.appendList(sym.owner.type.getTypeArguments());
	    }
	    targsBuf.appendList(sym.type.getTypeArguments());
	    List<Type> targs = targsBuf.toList();
	    //System.err.println("sym="+sym);
	    //System.err.println("sym.owner="+sym.owner);
	    //System.err.println("targs="+targs);
            JCMethodDecl meth = m().MethodDef(
                    addAccessAnnotationModifiers(diagPos, tree.mods.flags, m().Modifiers(flags)),
                    functionName(sym, isInstanceFunctionAsStaticMethod, isBound),
                    makeReturnTypeTree(diagPos, sym, isBound),
                    m().TypeParams(targs),
                    params,
                    m().Types(mtype.getThrownTypes()), // makeThrows(diagPos), //
                    body,
                    null);
            meth.sym = sym;
            meth.type = tree.type;
            if (isBound) {
                meth.mods.annotations = meth.mods.annotations.append(methodSignature());
            }
            return meth;
        }

        private JCAnnotation methodSignature() {
            JCAnnotation sig = make.Annotation(
                    makeIdentifier(diagPos, F3Symtab.signatureAnnotationClassNameString),
                    List.<JCExpression>of(makeLit(diagPos, syms.stringType, types.toSignature(sym.type))));
            return sig;
        }

        protected SpecialResult doit() {
            F3Tree prevWhere = getAttrEnv().where;
            Yield prevYield = yield();
            Type prevTargetType = targetType;
            getAttrEnv().where = tree;
            yieldKind = ToStatement;
            targetType = null;

            ReceiverContext prevContext = receiverContext();
            if (!maintainContext) {
                setReceiverContext(isStatic ?
                    ReceiverContext.ScriptAsStatic :
                    isInstanceFunctionAsStaticMethod ?
                        ReceiverContext.InstanceAsStatic :
                        ReceiverContext.InstanceAsInstance);
            }

            try {
                return new SpecialResult(makeMethod(methodFlags(), methodBody(), methodParameters()));
            } finally {
                setReceiverContext(prevContext);
                yieldKind = prevYield;
                targetType = prevTargetType;
                getAttrEnv().where = prevWhere;
            }
        }
    }

    class IdentTranslator extends MemberReferenceTranslator {
        protected final F3Ident tree;
        protected final Symbol sym;
        IdentTranslator(F3Ident tree) {
            super(tree.pos());
            this.tree = tree;
            this.sym = tree.sym;
        }

        protected ExpressionResult doit() {
            return toResult(doitExpr(), tree.type);
        }

        protected JCExpression doitExpr() {
            if (tree.getName() == names._this) {
                // in the static implementation method, "this" becomes "receiver$"
                return getReceiverOrThis(sym);
            } else if (tree.getName() == names._super) {
                if (types.isMixin(tree.type.tsym)) {
                    // "super" becomes just the class where the static implementation method is defined
                    //  the rest of the implementation is in visitFunctionInvocation
                    return id(tree.type.tsym.name);
                } else {
                    // Just use super.
                    return resolveSuper(tree.sym.owner);
                }
            }

            int kind = sym.kind;
            if (kind == Kinds.TYP) {
                // This is a class name, replace it with the full name (no generics)
                //return makeType(types.erasure(sym.type), false); // hack!!
                return makeType(sym.type, false);
            }

            // if this is an instance reference to an attribute or function, it needs to go the the "receiver$" arg,
            // and possible outer access methods
            JCExpression convert;
            if (sym.isStatic()) {
                // make class-based direct static reference:   Foo.x
                convert = reference(sym);
            } else {
                if ((kind == Kinds.VAR || kind == Kinds.MTH) &&
                        sym.owner.kind == Kinds.TYP) {
                    // it is a non-static attribute or function class member
                    // reference it through the receiver
                    convert = Select(getReceiver(sym),tree.getName());
                } else {
                    convert = id(tree.getName());
                }
            }

            return convertVariableReference(convert, sym);
        }
    }

    /**
     * Bound identifier reference (non-sequence)
     */
    class BoundIdentTranslator extends IdentTranslator {

        BoundIdentTranslator(F3Ident tree) {
            super(tree);
        }
        
        protected void addIdentInterClassBindee(F3VarSymbol vsym) {
            if (vsym.owner.kind == Kinds.TYP && !vsym.isSpecial()) {
                if (vsym.isStatic()) {
                    // Script var
                    F3ClassSymbol classSym = (F3ClassSymbol) vsym.owner;
                    F3VarSymbol scriptAccess = f3make.ScriptAccessSymbol(classSym);
                    addInterClassBindee(scriptAccess, vsym);
                } else {
                    // Outer class reference through "this"
                    addInterClassBindee(f3make.ThisSymbol(vsym.owner.type), vsym);
                }
            }
        }

        @Override
        protected ExpressionResult doit() {
            if (sym instanceof F3VarSymbol) {
                F3VarSymbol vsym = (F3VarSymbol) sym;
                boolean isScriptContext = receiverContext() == ReceiverContext.ScriptAsStatic;
                if ((isScriptContext == sym.isStatic())  && currentClass().sym.isSubClass(sym.owner, types)) {
                    // The var is in our class (or a superclass)
                    addBindee(vsym);
                } else {
                    // Possible script or outer class reference
                    addIdentInterClassBindee(vsym);
                }
            }
            return super.doit();
        }
    }

    /**
     * Bound member select (non-sequence)
     */
    class BoundSelectTranslator extends SelectTranslator {

        protected final F3VarSymbol selectResSym;

        BoundSelectTranslator(F3Select tree, F3VarSymbol selectResSym) {
            super(tree);
            this.selectResSym = selectResSym;
        }

        @Override
        protected ExpressionResult doit() {
            F3Expression selectorExpr = tree.getExpression();
            if (selectorExpr instanceof F3Ident) {
                F3Ident selector = (F3Ident) selectorExpr;
                
                if (selector.sym instanceof F3VarSymbol) {
                    F3VarSymbol selectorSym = (F3VarSymbol)selector.sym;
                    
                    if (selectorSym.isSpecial()) {
                        addInterClassBindee(selectorSym, refSym);
                    } else if (canChange()) {
                        if (tree.sym instanceof F3VarSymbol) {
                            // cases that need a null check are the same as cases that have changing dependencies
                            addBindee(selectorSym);
                            addInterClassBindee(selectorSym, refSym);
                        }
                    }
                }
            }
            return (ExpressionResult) super.doit();
        }
    }

    class BoundBlockExpressionTranslator extends ExpressionTranslator {

        private final F3Expression value;
        private final List<F3Expression> statements;

        BoundBlockExpressionTranslator(F3Block tree) {
            super(tree.pos());
            this.value = tree.value;
            this.statements = tree.getStmts();
        }

        protected ExpressionResult doit() {
            for (F3Expression expr : statements) {
                translateStmt(expr, syms.voidType);
            }
            JCExpression tvalue = translateExpr(value, targetType);
            return toResult(tvalue, targetType);
        }
    }

    /**
     * Translator for assignment, and other mutating operations
     */
    abstract class AssignTranslator extends NullCheckTranslator {

        protected final F3Expression ref;
        protected final F3Expression indexOrNull;
        protected final F3Expression rhs;
        protected final F3Expression selector;
        protected final JCExpression rhsTranslated;
        protected final boolean useAccessors;
        protected final boolean selectorNullCheck;

        /**
         *
         * @param diagPos
         * @param ref Variable being referenced (different from LHS if indexed -- where it is sequence or array)
         * @param indexOrNull The index into the variable reference.  Or null if not indexed.
         * @param fullType The type of the resultant expression
         * @param rhs The expression acting on ref
         */
        AssignTranslator(final DiagnosticPosition diagPos, final F3Expression ref, final F3Expression indexOrNull, Type fullType, final F3Expression rhs) {
            super(diagPos, expressionSymbol(ref), fullType);
            this.ref = ref;
            this.indexOrNull = indexOrNull;
            this.rhs = rhs;
            this.selector = (ref instanceof F3Select) ? ((F3Select) ref).getExpression() : null;
            this.selectorNullCheck = (ref instanceof F3Select) ? ((F3Select) ref).nullCheck : false;
            if (rhs != null) {
                JCExpression translated = convertNullability(diagPos, translateExpr(rhs, rhsType()), rhs, rhsType());
                this.rhsTranslated = preserveSideEffects(fullType, rhs, translated);
            } else {
                this.rhsTranslated = null;
            }
            this.useAccessors = (refSym!=null && refSym.kind==Kinds.VAR)?
                  ((F3VarSymbol)refSym).useAccessors()
                : false;
        }

        /**
         * Constructor for assignment forms: =, ++, +=, etc
         * @param diagPos
         * @param lhs
         * @param rhs
         */
        AssignTranslator(final DiagnosticPosition diagPos, final F3Expression lhs, final F3Expression rhs) {
            this(
                    diagPos,
                    lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED? ((F3SequenceIndexed)lhs).getSequence() : lhs,
                    lhs.getF3Tag() == F3Tag.SEQUENCE_INDEXED? ((F3SequenceIndexed)lhs).getIndex() : null,
                    lhs.type,
                    rhs);
        }

        JCExpression buildRHS(JCExpression rhsTranslated) {
            return rhsTranslated;
        }

        JCExpression defaultFullExpression(JCExpression lhsTranslated, JCExpression rhsTranslated) {
            throw new AssertionError("should not reach here");
        }

        @Override
        F3Expression getToCheck() {
            return selector;
        }

        @Override
        boolean needNullCheck() {
            return !selectorNullCheck && selector != null && super.needNullCheck();
        }

        JCExpression translateIndex() {
            return indexOrNull==null? null : translateExpr(indexOrNull, syms.intType);
        }

        // Figure out the instance containing the variable
        JCExpression instance(JCExpression tToCheck) {
            if (staticReference) {
                return getReceiver(refSym);
            } else if (tToCheck == null) {
                return id(names._this);
            } else {
                return tToCheck;
            }
        }

        JCExpression sequencesOp(RuntimeMethod meth, JCExpression tToCheck) {
            ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
            F3VarSymbol vsym = (F3VarSymbol) refSym;
            if (! vsym.useAccessors()) {
                // In this case make a block expression - roughly:
                // { Foo tmp = rhs;
                //   lhs = sequenceAction(lhs, tmp);
                //   tmp;
                // }
                args.append(Getter(tToCheck, vsym));
                JCVariableDecl tv;
                // The special case for JCLiteral is to avoid a bug in Gen - it
                // optimizes away initializing a variable that is constant,
                // but somehow gets confused when later trying to load the variable.
                // Probably something to do with BlockExpressions confusing it.
                if (rhsTranslated instanceof JCLiteral || targetType == syms.voidType) {
                    tv = null;
                    args.append(rhsTranslated);
                }
                else {
                    tv = TmpVar(rhsType(), buildRHS(rhsTranslated));
                    args.append(id(tv));
                }
                JCExpression tIndex = translateIndex();
                if (tIndex != null) {
                    args.append(tIndex);
                }
                JCExpression assign = Setter(tToCheck, vsym, Call(meth, args));
                if (targetType == syms.voidType)
                    return assign;
                if (rhsTranslated instanceof JCLiteral)
                    return BlockExpression(Stmt(assign), translateExpr(rhs, rhsType()));
                return BlockExpression(
                        tv,
                        Stmt(assign),
                        id(tv));
            } else {
                // Instance variable sequence -- roughly:
                // sequenceAction(instance, varNum, rhs);
                args.append(instance(tToCheck));
                args.append(Offset(copyOfTranslatedToCheck(tToCheck), vsym));
                args.append(buildRHS(rhsTranslated));
                JCExpression tIndex = translateIndex();
                if (tIndex != null) {
                    args.append(tIndex);
                }
                return Call(meth, args);
            }
        }

        JCExpression makeSliceEndPos(F3SequenceSlice tree) {
            JCExpression endPos;
            if (tree.getLastIndex() == null) {
                endPos = Call(
                        translateExpr(tree.getSequence(), null),
                        defs.size_SequenceMethodName);
                if (tree.getEndKind() == SequenceSliceTree.END_EXCLUSIVE) {
                    endPos = MINUS(endPos, Int(1));
                }
            } else {
                endPos = translateExpr(tree.getLastIndex(), syms.intType);
                if (tree.getEndKind() == SequenceSliceTree.END_INCLUSIVE) {
                    endPos = PLUS(endPos, Int(1));
                }
            }
            return endPos;
        }

        @Override
        JCExpression fullExpression(JCExpression tToCheck) {
            if (indexOrNull != null) {
                if (ref.type.tag == TypeTags.ARRAY) {
                    // set of an array element --  s[i]=8, set the array element
                    JCExpression tArray = translateExpr(ref, ref.type);
                    return m().Assign(m().Indexed(tArray, translateIndex()), buildRHS(rhsTranslated));
                } else {
                    // set of a sequence element --  s[i]=8, call the sequence set method
                    return sequencesOp(defs.Sequences_set, tToCheck);
                }
            } else {
                if (useAccessors) {
                    return postProcessExpression(buildSetter(tToCheck, buildRHS(rhsTranslated)));
                } else if (refSym instanceof VarSymbol && ((F3VarSymbol)refSym).isF3Member()) {
                    if (((F3VarSymbol)refSym).useGetters()) {
                        return Setter(tToCheck, refSym, rhsTranslated);
                    }
                    else {
                    JCExpression lhsTranslated = selector != null ?
                        Select(tToCheck, attributeValueName(refSym)) :
                        Getter(refSym);
                    JCExpression res =  defaultFullExpression(lhsTranslated, rhsTranslated);
                    return res;
                    }
                } else {
                    //TODO: possibly should use, or be unified with convertVariableReference
                    JCExpression lhsTranslated = selector != null ?
                        Select(tToCheck, refSym.name) :
                        reference(refSym);
                    JCExpression res =  defaultFullExpression(lhsTranslated, rhsTranslated);
                    return res;
                }
            }
        }

        /**
         * Override to change the translation type of the right-hand side
         */
        protected Type rhsType() {
            if (indexOrNull != null) {
                // Indexed assignment
                if (types.isArray(rhs.type) || types.isSequence(rhs.type)) {
                    return ref.type;
                } else {
                    return types.arrayOrSequenceElementType(ref.type);
                }
            } else {
                if (refSym == null) {
                    return ref.type;
                } else {
                    // Handle type inferencing not reseting the ident type
                    return refSym.type;
                }
            }
        }

        /**
         * Override to change result in the non-default case.
         */
        protected JCExpression postProcessExpression(JCExpression built) {
            return built;
        }

        JCExpression buildSetter(JCExpression tc, JCExpression rhsComplete) {
            return Setter(tc, refSym, rhsComplete);
        }

        JCExpression buildGetter(JCExpression tc) {
            return Getter(tc, refSym);
        }
    }

    class UnaryOperationTranslator extends ExpressionTranslator {

        private final F3Unary tree;
        private final F3Expression expr;
        private final JCExpression transExpr;

        UnaryOperationTranslator(F3Unary tree) {
            super(tree.pos());
            this.tree = tree;
            this.expr = tree.getExpression();
            this.transExpr = translateExpr(expr, expr.type);
        }

        protected AbstractStatementsResult doit() {
            switch (tree.getF3Tag()) {
                case SIZEOF:
                    if (expr.type.tag == TypeTags.ARRAY) {
                        return toResult(Select(transExpr, defs.length_ArrayFieldName), syms.intType);
                    }
                    return toResult(translateSizeof(expr, transExpr), syms.intType);
                case REVERSE:
                    if (types.isSequence(expr.type)) {
                        // call runtime reverse of a sequence
                        return toResult(
                             Call(defs.Sequences_reverse, transExpr),
                             expr.type);
                    } else {
                        // this isn't a sequence, just make it a sequence
                        return toResult(convertTranslated(transExpr, diagPos, expr.type, targetType), targetType);
                    }
                case NEG:
                    if (tree.operator == null || types.isSameType(tree.type, syms.f3_DurationType)) {
                        return toResult(
                                Call(translateExpr(tree.arg, tree.arg.type), defs.negate_DurationMethodName),
                                syms.f3_DurationType);
                    }
                    if (types.isSameType(tree.type, syms.f3_LengthType)) {
                        return toResult(
                                Call(translateExpr(tree.arg, tree.arg.type), defs.negate_LengthMethodName),
                                syms.f3_LengthType);
                    }
                    if (types.isSameType(tree.type, syms.f3_AngleType)) {
                        return toResult(
                                Call(translateExpr(tree.arg, tree.arg.type), defs.negate_AngleMethodName),
                                syms.f3_AngleType);
                    }
                    if (types.isSameType(tree.type, syms.f3_ColorType)) {
                        return toResult(
                                Call(translateExpr(tree.arg, tree.arg.type), defs.negate_ColorMethodName),
                                syms.f3_ColorType);
                    }
                default:
                    return toResult(
                            m().Unary(tree.getOperatorTag(), transExpr),
                            tree.type);
            }
        }
    }

    class BinaryOperationTranslator extends ExpressionTranslator {

        final F3Binary tree;
        final Type lhsType;
        final Type rhsType;

        BinaryOperationTranslator(DiagnosticPosition diagPos, final F3Binary tree) {
            super(diagPos);
            this.tree = tree;
            this.lhsType = tree.lhs.type;
            this.rhsType = tree.rhs.type;
        }

        JCExpression lhs(Type type) {
            return translateExpr(tree.lhs, type);
        }

        JCExpression lhs() {
            return lhs(null);
        }

        JCExpression rhs(Type type) {
            return translateExpr(tree.rhs, type);
        }

        JCExpression rhs() {
            return rhs(null);
        }

        //TODO: after type system is figured out, this needs to be revisited
        /**
         * Check if a primitive has the default value for its type.
         */
        private JCExpression makePrimitiveNullCheck(Type argType, JCExpression arg) {
            F3TypeRepresentation typeRep = types.typeRep(argType);
            JCExpression defaultValue = makeLit(diagPos, argType, typeRep.defaultValue());
            return EQ(arg, defaultValue);
        }

        /**
         * Check if a non-primitive has the default value for its type.
         */
        private JCExpression makeObjectNullCheck(Type argType, JCExpression arg) {
            if (types.isSequence(argType) || types.isSameType(argType, syms.f3_StringType)) {
                return Call(defs.Checks_isNull, arg);
            } else {
                return EQnull(arg);
            }
        }

        /**
         * Make a .equals() comparison with a null check on the receiver
         */
        private JCExpression makeFullCheck(JCExpression lhs, JCExpression rhs) {
            return Call(defs.Checks_equals, lhs, rhs);
        }

        /**
         * Return the translation for a == comparision
         */
        private JCExpression translateEqualsEquals() {
            final boolean reqSeq = types.isSequence(lhsType) ||
                    types.isSequence(rhsType);

            Type expected = tree.operator.type.getParameterTypes().head;
            if (reqSeq) {
                Type left = types.isSequence(lhsType) ? types.elementType(lhsType) : lhsType;
                Type right = types.isSequence(rhsType) ? types.elementType(rhsType) : rhsType;
                if (left.isPrimitive() && right.isPrimitive() && left == right) {
                    expected = left;
                }
            }
            Type req = reqSeq ? types.sequenceType(expected) : null;

            // this is an x == y
            if (lhsType.getKind() == TypeKind.NULL) {
                if (rhsType.getKind() == TypeKind.NULL) {
                    // both are known to be null
                    return True();
                } else if (rhsType.isPrimitive()) {
                    // lhs is null, rhs is primitive, do default check
                    return makePrimitiveNullCheck(rhsType, rhs(req));
                } else {
                    // lhs is null, rhs is non-primitive, figure out what check to do
                    return makeObjectNullCheck(rhsType, rhs(req));
                }
            } else if (lhsType.isPrimitive()) {
                if (rhsType.getKind() == TypeKind.NULL) {
                    // lhs is primitive, rhs is null, do default check on lhs
                    return makePrimitiveNullCheck(lhsType, lhs(req));
                } else if (rhsType.isPrimitive()) {
                    // both are primitive, use ==
                    return EQ(lhs(req), rhs(req));
                } else {
                    // lhs is primitive, rhs is non-primitive, use equals(), but switch them
                    JCVariableDecl sl = TmpVar(req!=null? req : lhsType, lhs(req));  // eval first to keep the order correct
                    return BlockExpression(
                            sl,
                            makeFullCheck(rhs(req), id(sl.name)));
                }
            } else {
                if (rhsType.getKind() == TypeKind.NULL) {
                    // lhs is non-primitive, rhs is null, figure out what check to do
                    return makeObjectNullCheck(lhsType, lhs(req));
                } else {
                    //  lhs is non-primitive, use equals()
                    return makeFullCheck(lhs(req), rhs(req));
                }
            }
        }

        JCExpression op(JCExpression leftSide, Name methodName, JCExpression rightSide) {
            return Call(leftSide, methodName, rightSide);
        }

        boolean isDuration(Type type) {
            return types.isSameType(type, syms.f3_DurationType);
        }

        final Type durationNumericType = syms.f3_NumberType;

        JCExpression durationOp() {
            switch (tree.getF3Tag()) {
                case PLUS:
                    return op(lhs(), defs.add_DurationMethodName, rhs());
                case MINUS:
                    return op(lhs(), defs.sub_DurationMethodName, rhs());
                case DIV:
                    return op(lhs(), defs.div_DurationMethodName, rhs());
                              //rhs(isDuration(rhsType)? null : durationNumericType));
                case MUL: {
                    // lhs.mul(rhs);
                    JCExpression rcvr;
                    JCExpression arg;
                    /*
                    if (isDuration(lhsType)) {
                        rcvr = lhs();
                        arg = rhs(durationNumericType);
                    } else {
                        //TODO: This may get side-effects out-of-order.
                        // A simple fix is to use a static Duration.mul(double,Duration).
                        // Another is to use a Block and a temporary.
                        rcvr = rhs();
                        arg = lhs(durationNumericType);
                    }
                    */
                    rcvr = lhs();
                    arg = rhs();
                    return op(rcvr, defs.mul_DurationMethodName, arg);
                }
                case LT:
                    return op(lhs(), defs.lt_DurationMethodName, rhs());
                case LE:
                    return op(lhs(), defs.le_DurationMethodName, rhs());
                case GT:
                    return op(lhs(), defs.gt_DurationMethodName, rhs());
                case GE:
                    return op(lhs(), defs.ge_DurationMethodName, rhs());
            }
            throw new RuntimeException("Internal Error: bad Duration operation");
        }

        boolean isLength(Type type) {
            return types.isSameType(type, syms.f3_LengthType);
        }

        final Type lengthNumericType = syms.f3_NumberType;

        JCExpression lengthOp() {
            switch (tree.getF3Tag()) {
                case PLUS:
                    return op(lhs(), defs.add_LengthMethodName, rhs());
                case MINUS:
                    return op(lhs(), defs.sub_LengthMethodName, rhs());
                case DIV:
                    return op(lhs(), defs.div_LengthMethodName, rhs(isLength(rhsType)? null : lengthNumericType));
                case MUL: {
                    // lhs.mul(rhs);
                    JCExpression rcvr;
                    JCExpression arg;
                    if (isLength(lhsType)) {
                        rcvr = lhs();
                        arg = rhs(lengthNumericType);
                    } else {
                        //TODO: This may get side-effects out-of-order.
                        // A simple fix is to use a static Length.mul(double,Length).
                        // Another is to use a Block and a temporary.
                        rcvr = rhs();
                        arg = lhs(lengthNumericType);
                    }
                    return op(rcvr, defs.mul_LengthMethodName, arg);
                }
                case LT:
                    return op(lhs(), defs.lt_LengthMethodName, rhs());
                case LE:
                    return op(lhs(), defs.le_LengthMethodName, rhs());
                case GT:
                    return op(lhs(), defs.gt_LengthMethodName, rhs());
                case GE:
                    return op(lhs(), defs.ge_LengthMethodName, rhs());
            }
            throw new RuntimeException("Internal Error: bad Length operation");
        }

        boolean isAngle(Type type) {
            return types.isSameType(type, syms.f3_AngleType);
        }

        final Type angleNumericType = syms.f3_NumberType;

        JCExpression angleOp() {
            switch (tree.getF3Tag()) {
                case PLUS:
                    return op(lhs(), defs.add_AngleMethodName, rhs());
                case MINUS:
                    return op(lhs(), defs.sub_AngleMethodName, rhs());
                case DIV:
                    return op(lhs(), defs.div_AngleMethodName, rhs(isAngle(rhsType)? null : angleNumericType));
                case MUL: {
                    // lhs.mul(rhs);
                    JCExpression rcvr;
                    JCExpression arg;
                    if (isAngle(lhsType)) {
                        rcvr = lhs();
                        arg = rhs(angleNumericType);
                    } else {
                        //TODO: This may get side-effects out-of-order.
                        // A simple fix is to use a static Angle.mul(double,Angle).
                        // Another is to use a Block and a temporary.
                        rcvr = rhs();
                        arg = lhs(angleNumericType);
                    }
                    return op(rcvr, defs.mul_AngleMethodName, arg);
                }
                case LT:
                    return op(lhs(), defs.lt_AngleMethodName, rhs());
                case LE:
                    return op(lhs(), defs.le_AngleMethodName, rhs());
                case GT:
                    return op(lhs(), defs.gt_AngleMethodName, rhs());
                case GE:
                    return op(lhs(), defs.ge_AngleMethodName, rhs());
            }
            throw new RuntimeException("Internal Error: bad Angle operation");
        }

        boolean isColor(Type type) {
            return types.isSameType(type, syms.f3_ColorType);
        }

        final Type colorNumericType = syms.f3_NumberType;

        JCExpression colorOp() {
            switch (tree.getF3Tag()) {
                case PLUS:
                    return op(lhs(), defs.add_ColorMethodName, rhs());
                case MINUS:
                    return op(lhs(), defs.sub_ColorMethodName, rhs());
                case DIV:
                    return op(lhs(), defs.div_ColorMethodName, rhs(isColor(rhsType)? null : colorNumericType));
                case MUL: {
                    // lhs.mul(rhs);
                    JCExpression rcvr;
                    JCExpression arg;
                    if (isColor(lhsType)) {
                        rcvr = lhs();
                        arg = rhs(colorNumericType);
                    } else {
                        //TODO: This may get side-effects out-of-order.
                        // A simple fix is to use a static Color.mul(double,Color).
                        // Another is to use a Block and a temporary.
                        rcvr = rhs();
                        arg = lhs(colorNumericType);
                    }
                    return op(rcvr, defs.mul_ColorMethodName, arg);
                }
                case LT:
                    return op(lhs(), defs.lt_ColorMethodName, rhs());
                case LE:
                    return op(lhs(), defs.le_ColorMethodName, rhs());
                case GT:
                    return op(lhs(), defs.gt_ColorMethodName, rhs());
                case GE:
                    return op(lhs(), defs.ge_ColorMethodName, rhs());
            }
            throw new RuntimeException("Internal Error: bad Color operation");
        }

        /**
         * Translate a binary expressions
         */
        protected ExpressionResult doit() {
            return toResult(doitExpr(), tree.type);
        }

        JCExpression doitExpr() {
            //TODO: handle <>
            if (tree.getF3Tag() == F3Tag.EQ) {
                return translateEqualsEquals();
            } else if (tree.getF3Tag() == F3Tag.NE) {
                return NOT(translateEqualsEquals());
            } else {
                // anything other than == or !=
                // Duration type operator overloading
                if (// ((isDuration(lhsType) || isDuration(rhsType))) &&
                        tree.operator == null) { // operator check is to try to get a decent error message by falling through if the Duration method isn't matched
                    return durationOp();
                }
                // Length type operator overloading
                if ((isLength(lhsType) || isLength(rhsType)) &&
                        tree.operator == null) { // operator check is to try to get a decent error message by falling through if the Length method isn't matched
                    return lengthOp();
                }
                // Angle type operator overloading
                if ((isAngle(lhsType) || isAngle(rhsType)) &&
                        tree.operator == null) { // operator check is to try to get a decent error message by falling through if the Angle method isn't matched
                    return angleOp();
                }
                // Color type operator overloading
                if ((isColor(lhsType) || isColor(rhsType)) &&
                        tree.operator == null) { // operator check is to try to get a decent error message by falling through if the Color method isn't matched
                    return colorOp();
                }
                return m().Binary(tree.getOperatorTag(), lhs(), rhs());
            }
        }
    }

    class TypeConversionTranslator extends ExpressionTranslator {

        final JCExpression translated;
        final Type sourceType;
        final Type targettedType;
        final boolean sourceIsSequence;
        final boolean targetIsSequence;
        final boolean sourceIsArray;
        final boolean targetIsArray;

        TypeConversionTranslator(DiagnosticPosition diagPos, JCExpression translated, Type sourceType, Type targettedType) {
            super(diagPos);
            this.translated = translated;
            this.sourceType = sourceType;
            this.targettedType = targettedType;
            this.sourceIsSequence = types.isSequence(sourceType);
            this.targetIsSequence = types.isSequence(targettedType);
            this.sourceIsArray = types.isArray(sourceType);
            this.targetIsArray = types.isArray(targettedType);
        }

        private JCExpression convertNumericSequence(final DiagnosticPosition diagPos,
                final JCExpression expr, final Type inElementType, final Type targetElementType) {
            JCExpression inTypeInfo = TypeInfo(diagPos, inElementType);
            JCExpression targetTypeInfo = TypeInfo(diagPos, targetElementType);
            return Call(
                    defs.Sequences_convertNumberSequence,
                    targetTypeInfo, inTypeInfo, expr);
        }

        private JCExpression convertNumericToCharSequence(final DiagnosticPosition diagPos,
                final JCExpression expr, final Type inElementType) {
            JCExpression inTypeInfo = TypeInfo(diagPos, inElementType);
            return Call(
                    defs.Sequences_convertNumberToCharSequence,
                    inTypeInfo, expr);
        }

        private JCExpression convertCharToNumericSequence(final DiagnosticPosition diagPos,
                final JCExpression expr, final Type targetElementType) {
            JCExpression targetTypeInfo = TypeInfo(diagPos, targetElementType);
            return Call(
                    defs.Sequences_convertCharToNumberSequence,
                    targetTypeInfo, expr);
        }

        protected ExpressionResult doit() {
            return toResult(doitExpr(), targettedType);
        }

        JCExpression doitExpr() {
            assert sourceType != null;
            assert targettedType != null;
            if (targettedType.tag == TypeTags.UNKNOWN) {
                //TODO: this is bad attribution
                return translated;
            }
            if (types.isSameType(targettedType, sourceType)) {
                return translated;
            }
            if (targetIsArray) {
                Type elemType = types.elemtype(targettedType);
                if (sourceIsSequence) {
                    if (elemType.isPrimitive()) {
                        return Call(defs.Sequences_toArray[types.typeRep(elemType).ordinal()], translated);
                    }
                    ListBuffer<JCStatement> stats = ListBuffer.lb();
                    JCVariableDecl tmpVar = TmpVar(sourceType, translated);
                    stats.append(tmpVar);
                    JCVariableDecl sizeVar = TmpVar(syms.intType, Call(id(tmpVar), defs.size_SequenceMethodName));
                    stats.append(sizeVar);
                    JCVariableDecl arrVar = TmpVar("arr", targettedType, m().NewArray(
                            makeType(elemType, true),
                            List.<JCExpression>of(id(sizeVar.name)),
                            null));
                    stats.append(arrVar);
                    stats.append(CallStmt(id(tmpVar.name), defs.toArray_SequenceMethodName, List.of(
                            Int(0),
                            id(sizeVar),
                            id(arrVar),
                            Int(0))));
                    return BlockExpression(stats, id(arrVar));
                } else {
                    //TODO: conversion may be needed here, but this is better than what we had
                    return translated;
                }
            } else if (sourceIsArray && targetIsSequence) {
                Type sourceElemType = types.elemtype(sourceType);
                List<JCExpression> args;
                if (sourceElemType.isPrimitive()) {
                    args = List.of(translated);
                } else {
                    args = List.of(TypeInfo(diagPos, sourceElemType), translated);
                }
                return Call(defs.Sequences_fromArray, args);
            }
            if (targetIsSequence && sourceIsSequence) {
                Type sourceElementType = types.elementType(sourceType);
                Type targetElementType = types.elementType(targettedType);
                if (types.isSameType(sourceElementType, targetElementType))
                    return translated;
                else if (types.isNumeric(sourceElementType) && types.isNumeric(targetElementType)) {
                    return convertNumericSequence(diagPos,
                            translated,
                            sourceElementType,
                            targetElementType);
                }
                else if (types.isNumeric(sourceElementType) &&
                        types.isSameType(targetElementType, syms.charType)) {
                    //numeric seq to char seq
                    return convertNumericToCharSequence(diagPos,
                            translated,
                            sourceElementType);
                }
                else if (types.isNumeric(targetElementType) &&
                        types.isSameType(sourceElementType, syms.charType)) {
                    //char seq to numeric seq
                    return convertCharToNumericSequence(diagPos,
                            translated,
                            targetElementType);
                }
            }

            // Convert primitive/Object types
            if (sourceType.isCompound() || sourceType.isPrimitive()) {
                return make.at(diagPos).TypeCast(makeType(types.erasure(targettedType), true), translated);
            }

            // We should add a cast "when needed".  Then visitTypeCast would just
            // call this function, and not need to call makeTypeCast on the result.
            // However, getting that to work is a pain - giving up for now.  FIXME
            return translated;
        }
    }

    class FunctionValueTranslator extends ExpressionTranslator {

        private JCExpression meth;
        private final F3FunctionDefinition def;
        private final MethodType mtype;
        private final Type resultType;
        private final Name name;
        
        FunctionValueTranslator(JCExpression meth, DiagnosticPosition diagPos, MethodType mtype, Type resultType) {
            super(diagPos);
            this.meth = meth;
            this.def = null;
            this.mtype = mtype;
            this.resultType = resultType;
            this.name = null;
        }

        FunctionValueTranslator(JCExpression meth, F3FunctionDefinition def, DiagnosticPosition diagPos, MethodType mtype, Type resultType) {
            super(diagPos);
            this.meth = meth;
            this.def = def;
            this.mtype = mtype;
            this.resultType = resultType;
            this.name = null;
        }

        protected ExpressionResult doit() {
            return toResult(doitExpr(), resultType);
        }
        
        protected JCTree translateInvokeCase() {
            setDiagPos(diagPos);
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            JCBlock body;
            int argNum = 0;
            
	    F3Block bexpr = def.getBodyExpression();

	    for (F3Var f3Var : def.getParams()) {
		setDiagPos(f3Var);
		Name paramName = f3Var.getName();
		Type paramType = f3Var.sym.type;
		if (!types.isSequence(paramType)) { // hack !!!
		    //paramType = types.erasure(paramType);
		}
                JCExpression arg;
                if (argNum < 2)
                    arg = id(argNum == 0 ? defs.arg1_ArgName : defs.arg2_ArgName);
                else
                    arg = m().Indexed(id(defs.args_ArgName), Int(argNum-2));
                JCExpression initialValue = typeCast(paramType, syms.objectType, arg);
                stmts.append(Var(Flags.FINAL, paramType, paramName, initialValue));

		if (types.isSequence(paramType)) {
		    stmts.append(CallStmt(id(f3Var.getName()), defs.incrementSharing_SequenceMethodName));
		}

		argNum++;
	    }

	    setDiagPos(bexpr);
	    stmts.appendList(translateToStatementsResult(bexpr, mtype.getReturnType()).statements());
            
            JCBlock block = Block(stmts);
            
            // Replace any void returns to return null.
            if (mtype.getReturnType() == syms.voidType) {
                new TreeTranslator() {
                    @Override
                    public void visitReturn(JCReturn tree) {
                        if (tree.expr == null) {
                            tree.expr = Null();
                        }
                        result = tree;
                    }

                    // do not descend into inner classes
                    @Override
                    public void visitClassDef(JCClassDecl tree) {
                        result = tree;
                    }
                }.translate(block);
            }
            
            return block;
        }

        JCExpression doitExpr() {
            boolean isScriptContext = receiverContext() == ReceiverContext.ScriptAsStatic;
            
            int nargs = mtype.argtypes.size();
            Type functionType = syms.f3_FunctionTypes[nargs];
            JCExpression functionTypeExpr = QualifiedTree(functionType.tsym.getQualifiedName().toString());
            
            ListBuffer<JCExpression> typeArgs = ListBuffer.lb();
            Type resType = types.boxedTypeOrType(mtype.restype);
            typeArgs.append(makeType(resType));
            for (Type argType : mtype.argtypes) {
                Type paramType = types.boxedTypeOrType(argType);
                typeArgs.append(makeType(paramType));
            }
            JCExpression funcClassType = m().TypeApply(functionTypeExpr, typeArgs.toList());
            
            JCExpression receiverExpr = getReceiverOrThis(isScriptContext);
            int number = currentClass().addInvokeCase(translateInvokeCase(), isScriptContext);
            List<JCExpression> funcValueArgs = List.<JCExpression>of(receiverExpr, FuncNum(number));
	    //System.err.println("mtype="+mtype);
            ListBuffer<JCExpression> typeParams = ListBuffer.lb();
            JCExpression res = m().NewClass(null, typeParams.toList(), funcClassType, funcValueArgs, null);
	    //System.err.println("res="+res);
	    return res;
        }
    }

    abstract class NewInstanceTranslator extends ExpressionTranslator {

        // Statements to set symbols with initial values.
        protected ListBuffer<JCStatement> varInits = ListBuffer.lb();

        // Symbols corresponding to caseStats.
        protected ListBuffer<F3VarSymbol> varSyms = ListBuffer.lb();
        
        // Name to use as a temp.
        protected Name tmpVarName;

        NewInstanceTranslator(DiagnosticPosition diagPos, Name tmpVarName) {
            super(diagPos);
            this.tmpVarName = tmpVarName;
        }

        /**
         * Does this instance have any instance initializations?
         */
        protected abstract boolean hasInstanceVariableInits();

        /**
         * Initialize the instance variables of the instance
         * @param instName
         */
        protected abstract void initInstanceVariables(Name instName);

        /**
         * buildInstance calls this just after object is created, but before
         * it's instance variables are initialized. Override to generate
         * statements/expressions just after new object is created.
         */
        protected void postInstanceCreation(Name instName) {
            makeInitSupportCall(defs.initVars_F3ObjectMethodName, instName);
        }

        /**
         * @return the constructor args -- translating any supplied args
         */
        protected abstract List<JCExpression> completeTranslatedConstructorArgs();

        protected JCExpression translateInstanceVariableInit(F3Expression init, F3VarSymbol vsym) {
            ExpressionResult eres = translateToExpressionResult(init, vsym.type);
            mergeResults(eres);
            return convertNullability(init.pos(), eres.expr(), init, vsym.type);
        }

        void setInstanceVariable(Name instanceName, F3BindStatus bindStatus, F3VarSymbol vsym, F3Expression init) {
            JCExpression transInit = translateInstanceVariableInit(init, vsym);
            JCExpression tc = instanceName == null ? null : id(instanceName);
            JCStatement def;
            clearDiagPos();
            if (vsym.useAccessors()) {
                if (vsym.isSequence()) {
                    def = CallStmt(defs.Sequences_set, tc,
                            Offset(id(instanceName), vsym), transInit);
                } else {
                    def = SetterStmt(tc, vsym, transInit);
                }
            } else {
                def = SetStmt(tc, vsym, transInit);
            }
            varInits.append(def);
            varSyms.append(vsym);
        }

        void makeInitSupportCall(Name methName, Name receiverName) {
            addPreface(CallStmt(id(receiverName), methName));
        }

        JCVariableDecl makeTmpLoopVar(int initValue) {
            return m().VarDef(m().Modifiers(0),
                    getSyntheticName("loop"),
                    makeType(syms.intType),
                    Int(initValue));
        }

        void makeInitApplyDefaults(Type classType, Name receiverName) {
            ClassSymbol classSym = (ClassSymbol)classType.tsym;
            int count = varSyms.size();

            clearDiagPos();
            JCVariableDecl loopVar = makeTmpLoopVar(0);
            Name loopName = loopVar.name;
            JCExpression loopLimit = Call(id(receiverName), defs.count_F3ObjectMethodName);
            JCVariableDecl loopLimitVar = TmpVar("count", syms.intType, loopLimit);
            addPreface(loopLimitVar);
            JCExpression loopTest = LT(id(loopName), id(loopLimitVar.name));
            List<JCExpressionStatement> loopStep = List.of(m().Exec(m().Assignop(JCTree.PLUS_ASG, id(loopName), Int(1))));
            JCStatement loopBody;

            JCStatement applyDefaultsExpr =
                    CallStmt(
                        id(receiverName),
                        defs.applyDefaults_F3ObjectMethodName,
                        id(loopName));

            if (1 < count) {
                // final short[] f3$0map = GETMAP$X();
                JCExpression getmapExpr = Call(null, varGetMapName(classSym)); //static method in toplevel class - no need for receiver
                JCVariableDecl mapVar = TmpVar("map", syms.f3_ShortArray, getmapExpr);
                addPreface(mapVar);

                LiteralInitVarMap varMap = getLiteralInitClassMap().getVarMap(classSym);
                int[] tags = new int[count];

                int index = 0;
                for (F3VarSymbol varSym : varSyms) {
                    tags[index++] = varMap.addVar(varSym);
		    //System.err.println("tag "+tags[index-1]+ " = " + varSym);
                }

                ListBuffer<JCCase> cases = ListBuffer.lb();
                index = 0;
                for (JCStatement varInit : varInits) {
                    cases.append(m().Case(Int(tags[index++]), List.<JCStatement>of(varInit, m().Break(null))));
                }

                cases.append(m().Case(null, List.<JCStatement>of(applyDefaultsExpr, m().Break(null))));

                JCExpression mapExpr = m().Indexed(id(mapVar), id(loopName));
                loopBody = m().Switch(mapExpr, cases.toList());
		//System.err.println("loopBody: "+ loopBody);
            } else {
                F3VarSymbol varSym = varSyms.first();
                JCExpression varOffsetExpr = Offset(id(receiverName), varSym);
                JCVariableDecl offsetVar = TmpVar("off", syms.intType, varOffsetExpr);
                addPreface(offsetVar);
                loopBody = If(EQ(id(loopName), id(offsetVar)),
                        varInits.first(),
                        applyDefaultsExpr);
            }

            // Ready to init value.
            JCStatement clearFlagsStmt = CallStmt(id(receiverName), defs.varFlagActionChange, id(loopName),
                                                  Int(0), id(defs.varFlagINIT_READY));

            addPreface(m().ForLoop(List.<JCStatement>of(loopVar), loopTest, loopStep, Block(clearFlagsStmt, loopBody)));
        }

        void makeSetVarFlags(Name receiverName, Type contextType) {
            for (F3VarSymbol vsym : varSyms) {
                if (vsym.useAccessors()) {
                    Name objLitFlag = vsym.isSequence() ?
                        defs.varFlagINIT_OBJ_LIT_SEQUENCE :
                        defs.varFlagINIT_OBJ_LIT;
                    JCExpression flagsToSet = id(objLitFlag);
                    addPreface(CallStmt(
                            id(receiverName),
                            defs.varFlagActionChange,
                            Offset(id(receiverName), vsym),
                            id(defs.varFlagALL_FLAGS),
                            flagsToSet));
                }
            }
        }

        /**
         * Return the instance building expression
         * @param declaredType
         * @param cdef
         * @param isF3
         * @return
         */
        protected ExpressionResult buildInstance(Type declaredType, 
						 List<Type> typeArgTypes,
						 F3ClassDeclaration cdef, boolean isF3) {
            Type type;

            if (cdef == null) {
                type = declaredType;
            } else {
                translateStmt(cdef, syms.voidType);
                type = cdef.type;
            }
            JCExpression classTypeExpr = makeType(type, false);

            List<JCExpression> newClassArgs = completeTranslatedConstructorArgs();

            if (tmpVarName == null) {
                tmpVarName = getSyntheticName("objlit");
            }
            
            boolean hasVars = hasInstanceVariableInits();
            JCExpression instExpression;
            if (hasVars || (isF3 && newClassArgs.nonEmpty()) || cdef != null) {
                // it is a instanciation of a F3 class which has instance variable initializers
                // (or is anonymous, or has an outer class argument)
                //
                //   {
                //       final X f3$0objlit = new X(true);
                //       final short[] f3$0map = GETMAP$X();
                //
                //       for (int f3$0initloop = 0; i < X.$VAR_COUNT; i++) {
                //           if (!isInitialized(f3$0initloop) {
                //               switch (f3$0map[f3$0initloop]) {
                //                   1: f3$0objlit.set$a(0); break;
                //                   2: f3$0objlit.set$b(0); break;
                //                   ...
                //                   n: f3$0objlit.set$z(0); break;
                //                   default: f3$0objlit.applyDefaults$(f3$0initloop);
                //               }
                //           }
                //       }
                //
                //       f3$0objlit.complete$();
                //       f3$0objlit
                //   }

                // Use the F3 constructor by adding a marker argument. The "true" in:
                //       ... new X(true);
                newClassArgs = newClassArgs.append(True());

                // Create the new instance, placing it in a temporary variable "f3$0objlit"
                //       final X f3$0objlit = new X(true);
                addPreface(Var(
                        type,
                        tmpVarName,
                        m().NewClass(null, null, classTypeExpr, newClassArgs, null)));

                // generate stuff just after new object is created
                postInstanceCreation(tmpVarName);

                // now initialize it's instance variables
                initInstanceVariables(tmpVarName);

                // (Re-)initialize the flags for the variables set in object literal
                makeSetVarFlags(tmpVarName, declaredType);

                // Apply defaults to the instance variables
                //
                //       final short[] f3$0map = GETMAP$X();
                //       for (int f3$0initloop = 0; i < X.$VAR_COUNT; i++) {
                //           ...
                //       }
                if (varSyms.nonEmpty()) {
                    makeInitApplyDefaults(type, tmpVarName);
                } else {
                    makeInitSupportCall(defs.applyDefaults_F3ObjectMethodName, tmpVarName);
                }

                // Call complete$ to do user's init and postinit blocks
                //       f3$0objlit.complete$();
                makeInitSupportCall(defs.complete_F3ObjectMethodName, tmpVarName);

                // Return the instance from the block expressions
                //       f3$0objlit
                instExpression = id(tmpVarName);

            } else {
                // this is a Java class or has no instance variable initializers, just instanciate it
                instExpression = m().NewClass(null, typeArgTypes != null ? makeTypes(null, typeArgTypes): null, classTypeExpr, newClassArgs, null);
            }

            return toResult(instExpression, type);
        }
    }

    /**
     * Translator for object literals
     */
    class InstanciateTranslator extends NewInstanceTranslator {

        protected final F3Instanciate tree;
        private final ClassSymbol idSym;

        InstanciateTranslator(final F3Instanciate tree) {
            super(tree.pos(), tree.varDefinedByThis != null ? tree.varDefinedByThis.name : null);
            this.tree = tree;
            this.idSym = (ClassSymbol)F3TreeInfo.symbol(tree.getIdentifier());
        }

        @Override
        protected boolean hasInstanceVariableInits() {
            return tree.getParts().nonEmpty();
        }

        @Override
        protected void initInstanceVariables(Name instName) {
            for (F3ObjectLiteralPart olpart : tree.getParts()) {
                diagPos = olpart.pos(); // overwrite diagPos (must restore)
                F3BindStatus bindStatus = olpart.getBindStatus();
                F3Expression init = olpart.getExpression();
                F3VarSymbol vsym = (F3VarSymbol) olpart.sym;
                setInstanceVariable(instName, bindStatus, vsym, init);
            }
            diagPos = tree.pos();
        }

        protected List<JCExpression> translatedConstructorArgs() {
            List<F3Expression> args = tree.getArgs();
            Symbol sym = tree.constructor;
            if (sym != null && sym.type != null) {
                ListBuffer<JCExpression> translated = ListBuffer.lb();
                List<Type> formals = sym.type.asMethodType().getParameterTypes();
                boolean usesVarArgs = (sym.flags() & Flags.VARARGS) != 0L &&
                        (formals.size() != args.size() ||
                        types.isConvertible(args.last().type, types.elemtype(formals.last())));
                boolean handlingVarargs = false;
                Type formal = null;
                List<Type> t = formals;
                for (List<F3Expression> l = args; l.nonEmpty(); l = l.tail) {
                    if (!handlingVarargs) {
                        formal = t.head;
                        t = t.tail;
                        if (usesVarArgs && t.isEmpty()) {
                            formal = types.elemtype(formal);
                            handlingVarargs = true;
                        }
                    }
                    JCExpression targ = translateExpr(l.head, formal);
                    if (targ != null) {
                        translated.append(targ);
                    }
                }
                return translated.toList();
            } else {
                return translateExprs(args);
            }
        }

        @Override
        protected List<JCExpression> completeTranslatedConstructorArgs() {
            List<JCExpression> translated = translatedConstructorArgs();
            ClassSymbol clazz = tree.getClassBody() != null ?
                tree.getClassBody().sym :
                idSym;
            if (getHasOuters().containsKey(clazz)) {
                JCExpression receiver = resolveThis(getHasOuters().get(clazz), false);
                translated = translated.prepend(receiver);
            }
            return translated;
        }

        protected ExpressionResult doit() {
            return buildInstance(tree.type, tree.typeArgTypes, 
				 tree.getClassBody(), types.isF3Class(idSym));
        }
    }

    class TypeCastTranslator extends ExpressionTranslator {

        private final F3Expression expr;
        private final F3Tree clazz;

        TypeCastTranslator(final F3TypeCast tree) {
            super(tree.pos());
            this.expr = tree.getExpression();
            this.clazz = tree.clazz;
        }

        protected ExpressionResult doit() {
            JCExpression tExpr = translateExpr(expr, clazz.type);
            // The makeTypeCast below is usually redundant, since translateAsValue
            // takes care of most conversions - except in the case of a plain object cast.
            // It would be cleaner to move the makeTypeCast to translateAsValue,
            // but it's painful to get it right.  FIXME.
	    Type t = clazz.type;
	    if (t instanceof MethodType) { // hack !!!!
		t = syms.makeFunctionType((MethodType)t);
	    }
	    //System.err.println("type cast: "+ expr+": "+t+": "+expr.type);
            JCExpression ret = typeCast(t, expr.type, tExpr);
            ret = convertNullability(diagPos, ret, expr, t);
            return toResult(ret, t);
        }
    }

    class InstanceOfTranslator extends ExpressionTranslator {

        private final Type classType;
        private final F3Expression expr;

        InstanceOfTranslator(F3InstanceOf tree) {
            super(tree.pos());
            this.classType = types.boxedTypeOrType(tree.clazz.type);
            this.expr = tree.getExpression();
        }

        protected ExpressionResult doit() {
            JCExpression tExpr = translateExpr(expr, null);
            if (expr.type.isPrimitive()) {
                tExpr = makeBox(expr.pos(), tExpr, expr.type);
            }
            if (types.isSequence(expr.type) && !types.isSequence(classType)) {
                tExpr = Call(defs.Sequences_getSingleValue, tExpr);
            }
            tExpr = typeCast(syms.objectType, expr.type, tExpr);
            JCTree clazz = makeType(classType);
            return toResult(
                    m().TypeTest(tExpr, clazz),
                    syms.booleanType);
        }
    }

    class SequenceEmptyTranslator extends ExpressionTranslator {

        private final Type type;

        SequenceEmptyTranslator(F3SequenceEmpty tree) {
            super(tree.pos());
            this.type = tree.type;
        }

        protected ExpressionResult doit() {
            return toResult(doitExpr(), type);
        }

        protected JCExpression doitExpr() {
            if (types.isSequence(type)) {
                Type elemType = types.boxedElementType(type);
                JCExpression expr = accessEmptySequence(diagPos, elemType);
                return castFromObject(expr, syms.f3_SequenceTypeErasure);
            } else {
                return Null();
            }
        }
    }

    /**
     * Translate if-expression
     */
    class IfTranslator extends ExpressionTranslator {

        private final F3IfExpression tree;

        IfTranslator(F3IfExpression tree) {
            super(tree.pos());
            this.tree = tree;
        }

        JCExpression sideExpr(F3Expression expr) {
            ExpressionResult res = translateToExpressionResult(expr, targetType);
            addBindees(res.bindees());
            addInterClassBindees(res.interClass());
            return asExpression(res, targetType);
        }

        JCStatement sideStmt(F3Expression expr) {
            if (expr == null) {
                return null;
            } else {
                return translateToStatement(expr, targetType);
            }
        }

        protected AbstractStatementsResult doit() {
            JCExpression cond = translateExpr(tree.getCondition(), syms.booleanType);
            F3Expression trueSide = tree.getTrueExpression();
            F3Expression falseSide = tree.getFalseExpression();
            if (yield() == ToExpression) {
                return toResult(
                    If (cond,
                        sideExpr(trueSide),
                        sideExpr(falseSide)),
                    targetType);
            } else {
                 return toStatementResult(
                     If (cond,
                        sideStmt(trueSide),
                        sideStmt(falseSide)));
            }
        }
    }

    class IndexOfTranslator extends ExpressionTranslator {
        final F3Indexof tree;
        IndexOfTranslator(F3Indexof tree) {
            super(tree.pos());
            assert tree.clause.getIndexUsed() : "assert that index used is set correctly";
            this.tree = tree;
        }
            protected ExpressionResult doit() {
                return toResult(id(indexVarName(tree.fname)), tree.type);
            }
    }

    class SequenceIndexedTranslator extends ExpressionTranslator {

        private final F3Expression seq;
        private final JCExpression tSeq;
        private final boolean isTSeqDirect;
        private final JCExpression tIndex;
        private final Type resultType;

        SequenceIndexedTranslator(DiagnosticPosition diagPos, F3Expression seq, JCExpression tSeq, JCExpression tIndex, Type resultType) {
            super(diagPos);
            this.seq = seq;
            this.tSeq = tSeq;
            this.isTSeqDirect = false;
            this.tIndex = tIndex;
            this.resultType = resultType;
        }

        SequenceIndexedTranslator(F3SequenceIndexed tree) {
            super(tree.pos());
            this.seq = tree.getSequence();
            this.tSeq = translateExpr(seq, null);  
            this.isTSeqDirect = true;
            this.tIndex = translateExpr(tree.getIndex(), syms.intType);
            this.resultType = tree.type;
        }

        protected ExpressionResult doit() {
            return toResult(
                        doitExpr(),
                        resultType);
        }

        protected JCExpression doitExpr() {
            if (seq.type.tag == TypeTags.ARRAY) {
                // It is a native array, just index into it
                return m().Indexed(tSeq, tIndex);
            }
            F3TypeRepresentation typeRep = types.typeRep(resultType);
            if (seq instanceof F3Ident) {
                F3Ident var = (F3Ident) seq;
                OnReplaceInfo info = findOnReplaceInfo(var.sym);
                if (info != null
                        && (var.sym.flags_field & F3Flags.VARUSE_OPT_TRIGGER) != 0) {
                    F3OnReplace onReplace = info.onReplace;
                    ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
                    args.append(getReceiverOrThis(info.vsym));
                    args.append(Offset(info.vsym));
                    args.append(id(paramStartPosName(onReplace)));
                    args.append(id(paramNewElementsLengthName(onReplace)));
                    args.append(tIndex);
                    List<JCExpression> typeArgs;
                    if (typeRep.isObject()) {
                        /*
                         * We are calling SequencesBase.getFromNewElements() which
                         * accepts type argument for the returned sequence element type.
                         * If we don't pass correct type argument, we will get Object type.
                         * For example, for Sequence<? extends String> we want to pass "String"
                         * as type arg, so that the return type is "String" and not "Object".
                         */
                        ListBuffer<JCExpression> typeArgsBuf = ListBuffer.lb();
                        typeArgsBuf.append(makeType(seq.type.getTypeArguments().head.removeBounds()));
                        typeArgs = typeArgsBuf.toList();
                    } else {
                        typeArgs = List.<JCExpression>nil();
                    }
                    return Call(defs.Sequences_getAsFromNewElements[typeRep.ordinal()], typeArgs, args.toList());
                }
            }
            if (isTSeqDirect) {
                F3VarSymbol vsym = varSymbol(seq);
                if (vsym != null
                        && vsym.useAccessors()
                        && types.isSameType(vsym.getElementType(), resultType)) {
                    // Using elem$ is critical to non-boxing behavior of bound sequences
                    // Use elem$seq(pos) form
                    switch (seq.getF3Tag()) {
                        case SELECT: {
                            Yield prevYield = yieldKind;
                            yieldKind = ToExpression;  // Force expression result so that the merge works
                            try {
                                return mergeResults((ExpressionResult) new SelectElementTranslator((F3Select) seq, tIndex).doit());
                            } finally {
                                yieldKind = prevYield;
                            }
                        }
                        case IDENT:
                            return mergeResults(new IdentElementTranslator((F3Ident) seq, tIndex).doit());
                    }
                }
            }

            // Use seq.get(pos) form
            Name getMethodName = defs.typedGet_SequenceMethodName[typeRep.ordinal()];
            return Call(tSeq, getMethodName, tIndex);
        }
    }

    class SequenceSliceTranslator extends ExpressionTranslator {

        private final Type type;
        private final F3Expression seq;
        private final int endKind;
        private final F3Expression firstIndex;
        private final F3Expression lastIndex;

        SequenceSliceTranslator(F3SequenceSlice tree) {
            super(tree.pos());
            this.type = tree.type;
            this.seq = tree.getSequence();
            this.endKind = tree.getEndKind();
            this.firstIndex = tree.getFirstIndex();
            this.lastIndex = tree.getLastIndex();
        }

        JCExpression computeSliceEnd() {
            JCExpression endPos;
            if (lastIndex == null) {
                endPos = Call(translateExpr(seq, null), defs.size_SequenceMethodName);
                if (endKind == SequenceSliceTree.END_EXCLUSIVE) {
                    endPos = MINUS(endPos, Int(1));
                }
            } else {
                endPos = translateExpr(lastIndex, syms.intType);
                if (endKind == SequenceSliceTree.END_INCLUSIVE) {
                    endPos = PLUS(endPos, Int(1));
                }
            }
            return endPos;
        }

        protected ExpressionResult doit() {
            return toResult(doitExpr(), type);
        }

        protected JCExpression doitExpr() {
            JCExpression tFirstIndex = translateExpr(firstIndex, syms.intType);
            return Call(translateExpr(seq, null), defs.getSlice_SequenceMethodName, tFirstIndex, computeSliceEnd());
        }
    }

    class ExplicitSequenceTranslator extends ExpressionTranslator {

        final List<F3Expression> items;
        final Type elemType;
        final Type resultType;

        ExplicitSequenceTranslator(DiagnosticPosition diagPos, List<F3Expression> items, Type elemType, Type resultType) {
            super(diagPos);
            this.items = items;
            this.elemType = elemType;
            this.resultType = resultType;
        }

        /***
         * In cases where the components of an explicitly constructed
         * sequence are all singletons, we can revert to this (more
         * optimal) implementation.

        DiagnosticPosition diagPos = tree.pos();
        JCExpression meth = ((F3TreeMaker)make).at(diagPos).Identifier(sequencesMakeString);
        Type elemType = tree.type.getTypeArguments().get(0);
        ListBuffer<JCExpression> args = ListBuffer.<JCExpression>lb();
        List<JCExpression> typeArgs = List.<JCExpression>of(makeTypeTree(elemType, diagPos));
        // type name .class
        args.append(makeTypeInfo(diagPos, elemType));
        args.appendList( translate( tree.getItems() ) );
        result = make.at(diagPos).Apply(typeArgs, meth, args.toList());
        */
        protected ExpressionResult doit() {
            UseSequenceBuilder builder = useSequenceBuilder(diagPos, elemType, items.length(), false);
            addPreface(builder.makeBuilderVar());
            for (F3Expression item : items) {
                if (item.getF3Kind() != F3Kind.NULL_LITERAL) {
                    // Insert all non-null elements
                    addPreface(builder.addElement(item));
                }
            }
            return toResult(
                    builder.makeToSequence(),
                    resultType);
        }
    }

    class SequenceRangeTranslator extends ExpressionTranslator {

        private final F3Expression lower;
        private final F3Expression upper;
        private final F3Expression step;
        private final boolean hasStep;
        private final boolean exclusive;
        private final Type type;

        SequenceRangeTranslator(F3SequenceRange tree) {
            super(tree.pos());
            this.lower = tree.getLower();
            this.upper = tree.getUpper();
            this.hasStep = tree.getStepOrNull() != null;
            this.step = tree.getStepOrNull();
            this.exclusive = tree.isExclusive();
            this.type = tree.type;
        }

        protected ExpressionResult doit() {
            RuntimeMethod rm = exclusive ? defs.Sequences_rangeExclusive : defs.Sequences_range;
            Type elemType = syms.f3_IntegerType;
            int ltag = lower.type.tag;
            int utag = upper.type.tag;
            int stag = hasStep ? step.type.tag : TypeTags.INT;
            if (ltag == TypeTags.FLOAT || ltag == TypeTags.DOUBLE ||
                    utag == TypeTags.FLOAT || utag == TypeTags.DOUBLE ||
                    stag == TypeTags.FLOAT || stag == TypeTags.DOUBLE) {
                elemType = syms.f3_NumberType;
            }
            ListBuffer<JCExpression> args = ListBuffer.lb();
            args.append(translateExpr(lower, elemType));
            args.append(translateExpr(upper, elemType));
            if (hasStep) {
                args.append(translateExpr(step, elemType));
            }
            return toResult(
                    Call(rm, args),
                    type);
        }
    }

    /**
     * assume seq is a sequence of element type U
     * convert   for (x in seq where cond) { body }
     * into the following block expression
     *
     *   {
     *     SequenceBuilder<T> sb = new SequenceBuilder<T>(clazz);
     *     for (U x : seq) {
     *       if (!cond)
     *         continue;
     *       sb.add( { body } );
     *     }
     *     sb.toSequence()
     *   }
     *
     * **/

    class ForExpressionTranslator extends ExpressionTranslator {

        final F3ForExpression tree;

        ForExpressionTranslator(F3ForExpression tree) {
            super(tree.pos());
            this.tree = tree;
        }

        private JCStatement wrapWithInClause(F3ForExpression tree, JCStatement coreStmt) {
            JCStatement stmt = coreStmt;
            for (int inx = tree.getInClauses().size() - 1; inx >= 0; --inx) {
                F3ForExpressionInClause clause = (F3ForExpressionInClause) tree.getInClauses().get(inx);
                stmt = new InClauseTranslator(clause, stmt).doitStmt();
            }
            return stmt;
        }

        protected AbstractStatementsResult doit() {
            // sub-translation in done inline -- no super.visitForExpression(tree);
            if (yield() == ToStatement && targetType == syms.voidType) {
                return new StatementsResult(wrapWithInClause(tree, translateToStatement(tree.getBodyExpression(), targetType)));
            } else {
                // body has value (non-void)
                assert tree.type != syms.voidType : "should be handled above";
                JCStatement stmt;
                JCExpression value;

                // Compute the element type from the sequence type
                assert tree.type.getTypeArguments().size() == 1;
                Type elemType = types.elementType(tree.type);

                UseSequenceBuilder builder = useSequenceBuilder(diagPos, elemType, true);
                addPreface(builder.makeBuilderVar());

                // Build innermost loop body
                stmt = builder.addElement(tree.getBodyExpression());

                stmt = wrapWithInClause(tree, stmt);
                addPreface(stmt);

                // Build the result value
                value = builder.makeToSequence();
                if (yield() == ToStatement) {
                    return toStatementResult(value, tree.type, targetType);
                } else {
                    // Build the block expression -- which is what we translate to
                    return toResult(
                            convertTranslated(value, diagPos, tree.type, targetType),
                            targetType);
                }
            }
        }
    }

    /**
     * Translator class for for-expression in/where clauses
     */
    private class InClauseTranslator extends ExpressionTranslator {
        final F3ForExpressionInClause clause;      // in clause being translated
        final F3Var var;                           // user named F3 induction variable
        final Type inductionVarType;                // type of the induction variable
        final JCVariableDecl inductionVar;          // generated induction variable
        JCStatement body;                           // statement being generated by wrapping
        boolean indexedLoop;

        InClauseTranslator(F3ForExpressionInClause clause, JCStatement coreStmt) {
            super(clause);
            this.clause = clause;
            this.var = clause.getVar();
            this.inductionVarType = var.type;
            this.body = coreStmt;
            F3Expression seq = clause.seqExpr;
            this.indexedLoop =
                    (seq.getF3Tag() == F3Tag.SEQUENCE_SLICE ||
                     (seq.getF3Tag() != F3Tag.SEQUENCE_RANGE &&
                      types.isSequence(seq.type)));
            this.inductionVar = MutableTmpVar("ind", indexedLoop ? syms.intType : inductionVarType, null);
        }

        private JCVariableDecl TmpVar(String root, JCExpression value) {
            return TmpVar(root, inductionVarType, value);
        }

        private JCVariableDecl Var(Name varName, JCExpression value) {
            return Var(inductionVarType, varName, value);
        }

        @Override
        protected JCVariableDecl TmpVar(long flags, String root, Type varType, JCExpression initialValue) {
            Name varName = names.fromString(var.name.toString() + "$" + root);
            return Var(flags, varType, varName, initialValue);
        }

        /**
         * Generate a range sequence conditional test.
         * Result depends on if the range is ascending/descending and if the range is exclusive
         */
        private JCExpression condTest(F3SequenceRange range, boolean stepNegative, JCVariableDecl upperVar) {
            int op;
            if (stepNegative) {
                if (range.isExclusive()) {
                    op = JCTree.GT;
                } else {
                    op = JCTree.GE;
                }
            } else {
                if (range.isExclusive()) {
                    op = JCTree.LT;
                } else {
                    op = JCTree.LE;
                }
            }
            return m().Binary(op, id(inductionVar), id(upperVar));
        }

        /**
         * Determine if a literal is negative
         */
        private boolean isNegative(F3Expression expr) {
            F3Literal lit = (F3Literal) expr;
            Object val = lit.getValue();

            switch (lit.typetag) {
                case TypeTags.INT:
                    return ((int) (Integer) val) < 0;
                case TypeTags.SHORT:
                    return ((short) (Short) val) < 0;
                case TypeTags.BYTE:
                    return ((byte) (Byte) val) < 0;
                case TypeTags.CHAR:
                    return ((char) (Character) val) < 0;
                case TypeTags.LONG:
                    return ((long) (Long) val) < 0L;
                case TypeTags.FLOAT:
                    return ((float) (Float) val) < 0.0f;
                case TypeTags.DOUBLE:
                    return ((double) (Double) val) < 0.0;
                default:
                    throw new AssertionError("unexpected literal kind " + this);
            }
        }

        JCStatement makeForLoop(List<JCStatement> init, JCExpression cond, List<JCExpressionStatement> step, JCStatement body) {
            JCStatement loop = m().ForLoop(init, cond, step, body);
            if (clause.label != null) {
                // Wrap in a labeled stmt if the for has a label (that was created because
                // it translated into nested loops, and the body contained a break or continue.)
                loop = m().Labelled(clause.label, loop);
                }
            return loop;
        }

        JCStatement makeForEachLoop(JCVariableDecl var, JCExpression iterable, JCStatement body) {
            JCStatement loop = m().ForeachLoop(var, iterable, body);
            if (clause.label != null) {
                // Wrap in a labeled stmt if the for has a label (that was created because
                // it translated into nested loops, and the body contained a break or continue.)
                loop = m().Labelled(clause.label, loop);
                }
            return loop;
        }

        /**
         * Generate the loop for a slice sequence.  Loop wraps the current body.
         * For the loop:
         *    for (x in seq[lo..<hi]) body
         * Generate:
         *     for (int $i = max(lo,0);  i++; $i < min(hi,seq.size())) { def x = seq[$i]; body }
         * (We may need to put seq and/or hi in a temporary variable first.)
         */
        void translateSliceInClause(F3Expression seq, F3Expression first, F3Expression last, int endKind, JCVariableDecl seqVar) {
            // Collect all the loop initializing statements (variable declarations)
            ListBuffer<JCStatement> tinits = ListBuffer.lb();
            boolean needSeqVar;
            if (! (seq instanceof F3Ident))
                needSeqVar = true;
            else  {
                Symbol seqsym = ((F3Ident) seq).sym;
                OnReplaceInfo info = findOnReplaceInfo(seqsym);
                needSeqVar = info == null ||
                        (seqsym.flags_field & F3Flags.VARUSE_OPT_TRIGGER) == 0;
            }
            if (needSeqVar)
                tinits.append(seqVar);
            JCExpression init;
            boolean maxForStartNeeded = true;
            if (first == null)
                init = Int(0);
            else {
                init = translateToExpression(first, syms.intType);
                if (first.getF3Tag() == F3Tag.LITERAL && ! isNegative(first))
                    maxForStartNeeded = false;
                // FIXME set maxForStartNeeded false if first is replace-trigger startPos and seq is oldValue
                if (maxForStartNeeded)
                    setDiagPos(first);
                    init = Call(defs.Math_max, init, Int(0));
            }
            setDiagPos(clause);
            inductionVar.init = init;
            tinits.append(inductionVar);
            JCExpression sizeExpr = translateSizeof(seq, id(seqVar));
            //call(diagPos, ident(seqVar), "size");
            JCExpression limitExpr;
            // Compare the logic in makeSliceEndPos.
            if (last == null) {
                limitExpr = sizeExpr;
                if (endKind == SequenceSliceTree.END_EXCLUSIVE)
                    limitExpr = MINUS(limitExpr, Int(1));
            }
            else {
                limitExpr = translateToExpression(last, syms.intType);
                if (endKind == SequenceSliceTree.END_INCLUSIVE)
                    limitExpr = PLUS(limitExpr, Int(1));
                // FIXME can optimize if last is replace-trigger endPos and seq is oldValue
                if (true)
                    setDiagPos(last);
                    limitExpr = Call(defs.Math_min, limitExpr, sizeExpr);
            }
            setDiagPos(clause);
            JCVariableDecl limitVar = TmpVar("limit", syms.intType, limitExpr);
            tinits.append(limitVar);
            // The condition that will be tested each time through the loop
            JCExpression tcond = LT(id(inductionVar), id(limitVar));
            // Generate the step statement as: x += 1
            List<JCExpressionStatement> tstep = List.of(m().Exec(m().Assignop(JCTree.PLUS_ASG, id(inductionVar), m().Literal(TypeTags.INT, 1))));
            tinits.append(makeForLoop(List.<JCStatement>nil(), tcond, tstep, body));
            body = Block(tinits);
        }

        /**
         * Generate the loop for a range sequence.  Loop wraps the current body.
         * For the loop:
         *    for (x in [lo..hi step st]) body
         * Generate (assuming x is float):
         *    for (float x = lo, final float x$upper = up, final float x$step = st;, final boolean x$negative = x$step < 0.0;
         *        x$negative? x >= x$upper : x <= x$upper;
         *        x += x$step)
         *            body
         * Without a step specified (or a literal step) the form reduces to:
         *    for (float x = lo, final float x$upper = up;
         *        x <= x$upper;
         *        x += 1)
         *            body
         */
        void translateRangeInClause() {
            F3SequenceRange range = (F3SequenceRange) clause.seqExpr;
            // Collect all the loop initializing statements (variable declarations)
            ListBuffer<JCStatement> tinits = ListBuffer.lb();
            // Set the initial value of the induction variable to be the low end of the range, and add it the the initializing statements
            inductionVar.init = translateToExpression(range.getLower(), inductionVarType);
            tinits.append(inductionVar);
            // Record the upper end of the range in a final variable, and add it the the initializing statements
            JCVariableDecl upperVar = TmpVar("upper", translateToExpression(range.getUpper(), inductionVarType));
            tinits.append(upperVar);
            // The expression which will be used in increment the induction variable
            JCExpression tstepIncrExpr;
            // The condition that will be tested each time through the loop
            JCExpression tcond;
            // The user's step expression, or null if none specified
            F3Expression step = range.getStepOrNull();
            if (step != null) {
                // There is a user specified step expression
                JCExpression stepVal = translateToExpression(step, inductionVarType);
                if (step.getF3Tag() == F3Tag.LITERAL) {
                    // The step expression is a literal, no need for a variable to hold it, and we can test if the range is scending at compile time
                    tstepIncrExpr = stepVal;
                    tcond = condTest(range, isNegative(step), upperVar);
                } else {
                    // Arbitrary step expression, do all the madness shown in the method comment
                    JCVariableDecl stepVar = TmpVar("step", stepVal);
                    tinits.append(stepVar);
                    tstepIncrExpr = id(stepVar);
                    JCVariableDecl negativeVar = TmpVar("negative", syms.booleanType, LT(id(stepVar), m().Literal(inductionVarType.tag, 0)));
                    tinits.append(negativeVar);
                    tcond = 
                        If(id(negativeVar),
                            condTest(range, true, upperVar),
                            condTest(range, false, upperVar));
                }
            } else {
                // No step expression, use one as the increment
                tstepIncrExpr = m().Literal(inductionVarType.tag, 1);
                tcond = condTest(range, false, upperVar);
            }
            // Generate the step statement as: x += x$step
            List<JCExpressionStatement> tstep = List.of(m().Exec(m().Assignop(JCTree.PLUS_ASG, id(inductionVar), tstepIncrExpr)));
            // Finally, build the for loop
            body = makeForLoop(tinits.toList(), tcond, tstep, body);
        }

        /**
         * Core of the in-clause translation.  Given an in-clause and the current body, build the for-loop
         */
        protected StatementsResult doit() {
            return new StatementsResult(doitStmt());
        }
        protected JCStatement doitStmt() {
            // If there is a where expression, make the execution of the body conditional on the where condition
            if (clause.getWhereExpression() != null) {
                body = If(translateExprToBlockExpression(clause.getWhereExpression(), syms.booleanType), body);
            }

            // Because the induction variable may be used in inner contexts, make a final
            // variable inside the loop that holds the current iterations value.
            // Same with the index if used.   That is:
            //   for (x in seq) body
            // Becomes (assume Number sequence):
            //   x$incrindex = 0;
            //   loop over x$ind {
            //     final int x$index = x$incrindex++;
            //     final float x = x$ind;
            //     body;
            //   }
            JCVariableDecl incrementingIndexVar = null;
            F3Expression seq = clause.seqExpr;
            JCVariableDecl seqVar = null;
            {
                ListBuffer<JCStatement> stmts = ListBuffer.lb();
                setDiagPos(var);
                if (clause.getIndexUsed()) {
                    incrementingIndexVar = MutableTmpVar("incrindex", syms.f3_IntegerType, Int(0));
                    JCVariableDecl finalIndexVar = Var(
                            syms.f3_IntegerType,
                            indexVarName(clause),m().Unary(JCTree.POSTINC, id(incrementingIndexVar)));
                    stmts.append(finalIndexVar);
                }
                JCExpression varInit;     // Initializer for var.

                if (indexedLoop) {
                    F3Expression sseq;
                    if (clause.seqExpr instanceof F3SequenceSlice) {
                        sseq = ((F3SequenceSlice) clause.seqExpr).getSequence();
                    } else {
                        sseq = clause.seqExpr;
                    }
                    seqVar = TmpVar("seq", seq.type, translateToExpression(sseq, seq.type));
                    varInit = new SequenceIndexedTranslator(diagPos, sseq, id(seqVar), id(inductionVar), inductionVarType).doitExpr();
                } else {
                    varInit = id(inductionVar);
                }

                stmts.append(Var(var.getName(), varInit));
                stmts.append(body);
                body = Block(stmts);
            }

            // Translate the sequence into the loop
            setDiagPos(seq);
            if (seq.getF3Tag() == F3Tag.SEQUENCE_RANGE) {
                // Iterating over a range sequence
                translateRangeInClause();
            } else if (seq.getF3Tag() == F3Tag.SEQUENCE_SLICE) {
                F3SequenceSlice slice = (F3SequenceSlice) clause.seqExpr;
                translateSliceInClause(slice.getSequence(), slice.getFirstIndex(), slice.getLastIndex(),
                        slice.getEndKind(), seqVar);
            } else {
                // We will be using the sequence as a whole, so translate it
                JCExpression tseq = translateToExpression(seq, null);
                if (types.isSequence(seq.type)) {
                    // Iterating over a non-range sequence, use a foreach loop, but first convert null to an empty sequence
                    tseq = Call(defs.Sequences_forceNonNull,
                            TypeInfo(diagPos, inductionVarType), tseq);
                    translateSliceInClause(seq, null, null, SequenceSliceTree.END_INCLUSIVE, seqVar);
                    //body = m().ForeachLoop(inductionVar, tseq, body);
                } else if (seq.type.tag == TypeTags.ARRAY ||
                             types.asSuper(seq.type, syms.iterableType.tsym) != null) {
                    // Iterating over an array or iterable type, use a foreach loop
                    body = makeForEachLoop(inductionVar, tseq, body);
                } else {
                    // The "sequence" isn't aactually a sequence, treat it as a singleton.
                    // Compile: { var tmp = seq; if (tmp!=null) body; }
                    if (!inductionVarType.isPrimitive()) {
                        body = If (NEnull(id(inductionVar)),
                                body);
                    }
                    // the "induction" variable will have only one value, set it to that
                    inductionVar.init = tseq;
                    // wrap the induction variable and the body in a block to protect scope
                    body = Block(inductionVar, body);
                }
            }

            if (clause.getIndexUsed()) {
                // indexof is used, define the index counter variable at the top of everything
                body = Block(incrementingIndexVar, body);
            }

            return body;
        }
    }

    class VarInitTranslator extends ExpressionTranslator {
        private final F3Var var;
        private final F3VarSymbol vsym;

        VarInitTranslator(F3VarInit tree) {
            super(tree.pos());
            this.var = tree.getVar();
            this.vsym = tree.getSymbol();
        }

        /**
         * No longer waiting for the VarInit (this is it).
         * applyDefaults.
         * value is var value.
         */
        ExpressionResult doit() {
            JCExpression tor;
            clearDiagPos();
            if (!vsym.useAccessors() && var.isLiteralInit()) {
                tor =   Get(vsym);
            } else if (vsym.isSynthetic()) {
                tor =   BlockExpression(
                            If (FlagTest(vsym, defs.varFlagINIT_MASK, defs.varFlagINIT_READY),
                                CallStmt(getReceiver(vsym), defs.applyDefaults_F3ObjectMethodName, Offset(vsym))
                            ),
                            Get(vsym)
                        );
            } else {
                tor =   BlockExpression(
                            FlagChangeStmt(vsym, defs.varFlagINIT_WITH_AWAIT_MASK, defs.varFlagINIT_READY),
                            CallStmt(getReceiver(vsym), defs.applyDefaults_F3ObjectMethodName, Offset(vsym)),
                            Get(vsym)
                        );
            }
            return toResult(tor, vsym.type);
        }
    }

    class VarRefTranslator extends ExpressionTranslator {

        Symbol varSymbol;
        F3Expression receiver;
        F3VarRef.RefKind kind;
        Type expectedType;


        VarRefTranslator(F3VarRef tree) {
            super(tree.pos());
            this.varSymbol = tree.getVarSymbol();
            this.receiver = tree.getReceiver();
            this.kind = tree.getVarRefKind();
            this.expectedType = tree.type;
        }

        ExpressionResult doit() {
            JCExpression receiverExpr = receiver != null ? translateExpr(receiver, expectedType) : null;
            switch (kind) {
                case INST: return toResult(receiverExpr != null ? receiverExpr : getReceiverOrThis(varSymbol), expectedType);
                case VARNUM: return toResult(Offset(receiverExpr, varSymbol), expectedType);
            }
            throw new AssertionError("Shouldn't be here!");
        }
    }

    /***********************************************************************
     * Bad visitor support
     */

    void badVisitor(String msg) {
        throw new AssertionError(msg);
    }

    private void disallowedInBind() {
        badVisitor("should not be processed as part of a binding");
    }

    private void processedInParent() {
        badVisitor("should be processed by parent tree");
    }


    /***********************************************************************
     *
     * Visitors  (alphabetical order)
     * 
     * Disallow constructs disallowed in bind -- override where non-bound contructs are allowed (F3ToJava)
     * Assume non-bound non-notifying implementations -- override where needed
     */

    public void visitAssign(F3Assign tree) {
        disallowedInBind();
    }

    public void visitAssignop(F3AssignOp tree) {
        disallowedInBind();
    }

    public void visitBinary(F3Binary tree) {
        result = (new BinaryOperationTranslator(tree.pos(), tree)).doit();
    }

    public void visitBreak(F3Break tree) {
        disallowedInBind();
    }

    public void visitCatch(F3Catch tree) {
        processedInParent();
    }

    public void visitClassDeclaration(F3ClassDeclaration tree) {
        // redirected back to F3ToJava
        toJava.visitClassDeclaration(tree);
        result = toJava.result;
    }

    public void visitContinue(F3Continue tree) {
        disallowedInBind();
    }

    public void visitErroneous(F3Erroneous tree) {
        badVisitor("erroneous nodes shouldn't have gotten this far");
    }

    public void visitForExpression(F3ForExpression tree) {
        result = (new ForExpressionTranslator(tree)).doit();
    }

    public void visitForExpressionInClause(F3ForExpressionInClause that) {
        processedInParent();
    }

    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        disallowedInBind();
    }

    public void visitFunctionInvocation(final F3FunctionInvocation tree) {
        result = new FunctionCallTranslator(tree).doit();
    }

    public void visitFunctionValue(F3FunctionValue tree) {
        disallowedInBind();
    }

    public abstract void visitIdent(F3Ident tree);

    public void visitIfExpression(F3IfExpression tree) {
        result = new IfTranslator(tree).doit();
    }

    public void visitImport(F3Import tree) {
        processedInParent();
    }

    public void visitIndexof(final F3Indexof tree) {
        result = new IndexOfTranslator(tree).doit();
    }

    public void visitInitDefinition(F3InitDefinition tree) {
        processedInParent();
    }

    public void visitInstanceOf(F3InstanceOf tree) {
        result = new InstanceOfTranslator(tree).doit();
    }

    public void visitInstanciate(F3Instanciate tree) {
        result = new InstanciateTranslator(tree).doit();
    }

    public void visitInterpolateValue(final F3InterpolateValue tree) {
        throw new AssertionError("KeyFrame should have been lowered");
    }

    public void visitInvalidate(F3Invalidate tree) {
        disallowedInBind();
    }

    public void visitKeyFrameLiteral(F3KeyFrameLiteral tree) {
        disallowedInBind();
    }

    public void visitLiteral(F3Literal tree) {
         result = new LiteralTranslator(tree).doit();
    }

   public void visitModifiers(F3Modifiers tree) {
        processedInParent();
    }

    public void visitObjectLiteralPart(F3ObjectLiteralPart that) {
        processedInParent();
    }

    public void visitOnReplace(F3OnReplace tree) {
        processedInParent();
    }

    public void visitOverrideClassVar(F3OverrideClassVar tree) {
        processedInParent();
    }

    public void visitParens(F3Parens tree) {
        result = translateToExpressionResult(tree.expr, targetType);
    }

    public void visitPostInitDefinition(F3PostInitDefinition tree) {
        processedInParent();
    }

    public void visitReturn(F3Return tree) {
        disallowedInBind();
    }

    public void visitScript(F3Script tree) {
        disallowedInBind();
    }

    public void visitSequenceDelete(F3SequenceDelete tree) {
        disallowedInBind();
    }

    public void visitSequenceEmpty(F3SequenceEmpty tree) {
        result = new SequenceEmptyTranslator(tree).doit();
    }

    public void visitSequenceExplicit(F3SequenceExplicit tree) {
        result = new ExplicitSequenceTranslator(
                tree.pos(),
                tree.getItems(),
                types.elementType(tree.type),
                tree.type)
             .doit();
    }

    public void visitSequenceIndexed(final F3SequenceIndexed tree) {
        result = new SequenceIndexedTranslator(tree).doit();
    }

    public void visitSequenceInsert(F3SequenceInsert tree) {
        disallowedInBind();
    }

    public void visitSequenceRange(F3SequenceRange tree) {
        result = new SequenceRangeTranslator(tree).doit();
    }

    public void visitSequenceSlice(F3SequenceSlice tree) {
        result = new SequenceSliceTranslator(tree).doit();
    }

    public void visitSkip(F3Skip tree) {
        disallowedInBind();
    }

    public void visitStringExpression(F3StringExpression tree) {
        result = new StringExpressionTranslator(tree).doit();
    }

    public void visitThrow(F3Throw tree) {
        disallowedInBind();
    }

    public void visitTimeLiteral(final F3TimeLiteral tree) {
        result = new TimeLiteralTranslator(tree).doit();
   }

    public void visitLengthLiteral(final F3LengthLiteral tree) {
        result = new LengthLiteralTranslator(tree).doit();
   }

    public void visitAngleLiteral(final F3AngleLiteral tree) {
        result = new AngleLiteralTranslator(tree).doit();
   }

    public void visitColorLiteral(final F3ColorLiteral tree) {
        result = new ColorLiteralTranslator(tree).doit();
   }

    public void visitTree(F3Tree that) {
        badVisitor("Should not be here!!!");
    }

    public void visitTry(F3Try tree) {
        disallowedInBind();
    }

    public void visitTypeAny(F3TypeAny that) {
	// hack: Since type aliases are funneled through type any (another hack)
	// they end up here - just return an empty statement list 
	result = new StatementsResult(that, List.<JCStatement>nil());
    }

    public void visitTypeCast(final F3TypeCast tree) {
        result = new TypeCastTranslator(tree).doit();
    }

    public void visitTypeClass(F3TypeClass that) {
	//System.err.println(that);
        processedInParent();
    }

    public void visitTypeVar(F3TypeVar that) {
	//System.err.println(that);
        processedInParent();
    }

    public void visitTypeFunctional(F3TypeFunctional that) {
        processedInParent();
    }

    public void visitTypeArray(F3TypeArray tree) {
        processedInParent();
    }

    public void visitTypeUnknown(F3TypeUnknown that) {
        processedInParent();
    }

    public void visitUnary(F3Unary tree) {
        if (tree.getF3Tag().isIncDec()) {
            //we shouldn't be here - arithmetic unary expressions should
            //have been lowered to standard binary expressions
            badVisitor("Unexpected unary operator tag: " + tree.getF3Tag());
        }
        result = new UnaryOperationTranslator(tree).doit();
    }

    public void visitVar(F3Var tree) {
        disallowedInBind();
    }

    public void visitVarInit(F3VarInit tree) {
        result = new VarInitTranslator(tree).doit();
    }

    public void visitVarRef(F3VarRef tree) {
        result = new VarRefTranslator(tree).doit();
    }

    public void visitWhileLoop(F3WhileLoop tree) {
        disallowedInBind();
    }
}
