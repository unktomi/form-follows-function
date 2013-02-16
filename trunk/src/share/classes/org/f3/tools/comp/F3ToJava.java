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

import java.util.HashMap;
import java.util.Map;

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Type.MethodType;
import com.sun.tools.mjavac.code.Type.ForAll;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3AbstractTranslation.Translator;
import org.f3.tools.comp.F3AnalyzeClass.*;
import static org.f3.tools.comp.F3Defs.*;
import org.f3.tools.comp.F3InitializationBuilder.*;
import org.f3.tools.tree.*;
import static org.f3.tools.comp.F3AbstractTranslation.Yield.*;
import com.sun.tools.mjavac.code.Type.*;
/**
 * Translate F3 ASTs into Java ASTs
 *
 * @author Robert Field
 * @author Per Bothner
 * @author Lubo Litchev
 */
public class F3ToJava extends F3AbstractTranslation {
    protected static final Context.Key<F3ToJava> f3ToJavaKey =
        new Context.Key<F3ToJava>();

    /*
     * modules imported by context
     */
    private final F3InitializationBuilder initBuilder;
    private final F3TranslateBind translateBind;
    private final F3TranslateInvBind translateInvBind;

    /*
     * Buffers holding definitions waiting to be prepended to the current list of definitions.
     * At class or top-level these are the same.
     * Within a method (or block) prependToStatements is split off.
     * They need to be different because anonymous classes need to be declared in the scope of the method,
     * but interfaces can't be declared here.
     */
    private ListBuffer<JCStatement> prependToDefinitions = null;
    private ListBuffer<JCStatement> prependToStatements = null;

    private ListBuffer<JCExpression> additionalImports = null;

    // Stack used to track literal symbols for the current class.
    private Map<Symbol, Name> substitutionMap = new HashMap<Symbol, Name>();

    // Stack used to track literal symbols for the current class.
    private LiteralInitClassMap literalInitClassMap = null;

    /** Class symbols for classes that need a reference to the outer class. */
    private final Map<ClassSymbol, ClassSymbol> hasOuters = new HashMap<ClassSymbol, ClassSymbol>();

    private F3Env<F3AttrContext> attrEnv;
    ReceiverContext inInstanceContext = ReceiverContext.Oops;

    private DependencyGraphWriter depGraphWriter;

    /*
     * Sole instance creation
     */
 
    public static F3ToJava instance(Context context) {
        F3ToJava instance = context.get(f3ToJavaKey);
        if (instance == null)
            instance = new F3ToJava(context);
        return instance;
    }

    protected F3ToJava(Context context) {
        super(context, null);

        context.put(f3ToJavaKey, this);

        this.initBuilder = F3InitializationBuilder.instance(context);
        this.translateBind = F3TranslateBind.instance(context);
        this.translateInvBind = F3TranslateInvBind.instance(context);
        this.depGraphWriter = DependencyGraphWriter.instance(context);
    }

    /**
     * Entry point
     */
    public void toJava(F3Env<F3AttrContext> attrEnv) {
        this.setAttrEnv(attrEnv);

        attrEnv.translatedToplevel = (JCCompilationUnit)((SpecialResult)translateToSpecialResult(attrEnv.toplevel)).tree();
        attrEnv.translatedToplevel.endPositions = attrEnv.toplevel.endPositions;
    }


    /**
     * For special cases where the expression may not be fully attributed.
     * Specifically: package and import names.
     * Do a dumb simple conversion.
     */
    private JCExpression straightConvert(F3Expression tree) {
        if (tree == null) {
            return null;
        }
        DiagnosticPosition diagPos = tree.pos();
        switch (tree.getF3Tag()) {
            case IDENT: {
                F3Ident id = (F3Ident) tree;
                return make.at(diagPos).Ident(id.getName());
            }
            case SELECT: {
                F3Select sel = (F3Select) tree;
                return make.at(diagPos).Select(
                        straightConvert(sel.getExpression()),
                        sel.getIdentifier());
            }
            default:
                throw new RuntimeException("bad conversion");
        }
    }

    private boolean substitute(DiagnosticPosition diagPos, final Symbol sym) {
        final Name name = getSubstitutionMap().get(sym);
        if (name == null) {
            return false;
        } else {
            result = new ExpressionTranslator(diagPos) {
                protected ExpressionResult doit() {
                    return toResult(id(name), sym.type);
                }
            }.doit();
            return true;
        }
    }

    private void setSubstitution(F3Tree target, Symbol sym) {
        if (target instanceof F3Instanciate) {
            // Set up to substitute a reference to the this var within its definition
            ((F3Instanciate) target).varDefinedByThis = sym;
        }
    }

    /**
     * @return the attrEnv
     */
    @Override
    protected F3Env<F3AttrContext> getAttrEnv() {
        return attrEnv;
    }

    @Override
    protected ReceiverContext receiverContext() {
        return inInstanceContext;
    }

    @Override
    protected void setReceiverContext(ReceiverContext rc) {
        inInstanceContext = rc;
    }

    @Override
    protected F3ToJava toJava() {
        return this;
    }

    /**
     * @param attrEnv the attrEnv to set
     */
    public void setAttrEnv(F3Env<F3AttrContext> attrEnv) {
        this.attrEnv = attrEnv;
    }

    /**
     * @return the substitutionMap
     */
    @Override
    Map<Symbol, Name> getSubstitutionMap() {
        return substitutionMap;
    }

    /**
     * Class symbols for classes that need a reference to the outer class.
     */
    @Override
    Map<ClassSymbol, ClassSymbol> getHasOuters() {
        return hasOuters;
    }

    /**
     * @return the literalInitClassMap
     */
    @Override
    LiteralInitClassMap getLiteralInitClassMap() {
        return literalInitClassMap;
    }

    /**
     * @param literalInitClassMap the literalInitClassMap to set
     */
    private void setLiteralInitClassMap(LiteralInitClassMap literalInitClassMap) {
        this.literalInitClassMap = literalInitClassMap;
    }

    /**
     * Make a version of the on-replace to be used in inline in a setter.
     */
    private JCStatement translateTriggerAsInline(F3VarSymbol vsym, F3OnReplace onReplace) {
        if (onReplace == null) return null;
        boolean isSequence = vsym.isSequence();
        if (isSequence) {
            OnReplaceInfo info = new OnReplaceInfo();
            info.onReplace = onReplace;
            info.vsym = vsym;
            info.outer = onReplaceInfo;
            if (onReplace.getNewElements() != null)
                info.newElementsSym = onReplace.getNewElements().sym;
            onReplaceInfo = info;
        }
        JCStatement ret = translateToStatement(onReplace.getBody(), syms.voidType);
        if (isSequence)
            onReplaceInfo = onReplaceInfo.outer;
        return ret;
    }

    void scriptBegin() {
    }

    private class ClassDeclarationTranslator extends Translator {

        private final F3ClassDeclaration tree;
        private final boolean isMixinClass;
        private final ListBuffer<JCTree> translatedDefs = ListBuffer.lb();

        ClassDeclarationTranslator(F3ClassDeclaration tree) {
            super(tree.pos());
            this.tree = tree;
            isMixinClass = tree.isMixinClass();
        }

        protected StatementsResult doit() {
            final ListBuffer<JCStatement> translatedInitBlocks = ListBuffer.lb();
            final ListBuffer<JCStatement> translatedPostInitBlocks = ListBuffer.lb();
            
            ListBuffer<TranslatedVarInfo> attrInfo = ListBuffer.lb();
            ListBuffer<TranslatedOverrideClassVarInfo> overrideInfo = ListBuffer.lb();
            ListBuffer<TranslatedFuncInfo> funcInfo = ListBuffer.lb();

            ReceiverContext prevReceiverContext = receiverContext();

            // translate all the definitions that make up the class.
            // collect any prepended definitions, and prepend then to the tranlations
            ListBuffer<JCStatement> prevPrependToDefinitions = prependToDefinitions;
            ListBuffer<JCStatement> prevPrependToStatements = prependToStatements;
            prependToStatements = prependToDefinitions = ListBuffer.lb();
            {
                for (F3Tree def : tree.getMembers()) {
                    switch (def.getF3Tag()) {
                        case INIT_DEF: {
                            setContext(false);
                            translateAndAppendStaticBlock(((F3InitDefinition) def).getBody(), translatedInitBlocks);
                            inInstanceContext = ReceiverContext.Oops;
                            break;
                        }
                        case POSTINIT_DEF: {
                            setContext(false);
                            translateAndAppendStaticBlock(((F3PostInitDefinition) def).getBody(), translatedPostInitBlocks);
                            inInstanceContext = ReceiverContext.Oops;
                            break;
                        }
                        case VAR_DEF: {
                            F3Var attrDef = (F3Var) def;
                            setContext(attrDef.isStatic());
                            F3Expression initializer = attrDef.getInitializer();
                            boolean initWithBoundFuncResult = 
                                (initializer instanceof F3Ident) &&
                                isBoundFunctionResult(((F3Ident)initializer).sym);
                            ExpressionResult bindResult = translateBind(attrDef);
                            TranslatedVarInfo ai = new TranslatedVarInfo(
                                    attrDef,
                                    translateVarInit(attrDef),
                                    initWithBoundFuncResult? ((F3Ident)initializer).sym : null,
                                    bindResult,
                                    attrDef.getOnReplace(),
                                    translateTriggerAsInline(attrDef.sym, attrDef.getOnReplace()),
                                    attrDef.getOnInvalidate(),
                                    translateTriggerAsInline(attrDef.sym, attrDef.getOnInvalidate()));
                            attrInfo.append(ai);
                            break;
                        }
                        case OVERRIDE_ATTRIBUTE_DEF: {
                            F3OverrideClassVar override = (F3OverrideClassVar) def;
                            setContext(override.isStatic());
                            F3Expression initializer = override.getInitializer();
                            boolean initWithBoundFuncResult =
                                (initializer instanceof F3Ident) &&
                                isBoundFunctionResult(((F3Ident)initializer).sym);
                            ExpressionResult bindResult = translateBind(override);
                            TranslatedOverrideClassVarInfo ai = new TranslatedOverrideClassVarInfo(
                                    override,
                                    translateVarInit(override),
                                    initWithBoundFuncResult? ((F3Ident)initializer).sym : null,
                                    bindResult,
                                    override.getOnReplace(),
                                    translateTriggerAsInline(override.sym, override.getOnReplace()),
                                    override.getOnInvalidate(),
                                    translateTriggerAsInline(override.sym, override.getOnInvalidate()));
                            overrideInfo.append(ai);
                            break;
                        }
                        case FUNCTION_DEF: {
                            F3FunctionDefinition funcDef = (F3FunctionDefinition) def;
                            setContext(funcDef.isStatic());
                            funcInfo.append(new TranslatedFuncInfo(funcDef, translateToSpecialResult(funcDef).trees()));
                            break;
                        }
                        case CLASS_DEF: {
                            // Handle other classes.
                            inInstanceContext = ReceiverContext.Oops;
                            translatedDefs.appendList(translateToStatementsResult((F3ClassDeclaration)def, syms.voidType).trees());
                            break;
                        }
                        default: {
                            assert false : "Unexpected class member: " + def;
			    if (false) {
				inInstanceContext = ReceiverContext.Oops;
				translatedDefs.appendList(translateToSpecialResult(def).trees());
			    }
                            break;
                        }
                    }
                }
            }
            
            inInstanceContext = ReceiverContext.Oops;

            // the translated defs have prepends in front
            for (JCTree prepend : prependToDefinitions) {
                translatedDefs.prepend(prepend);
            }

            inInstanceContext = ReceiverContext.Oops;

            prependToDefinitions = prevPrependToDefinitions;
            prependToStatements = prevPrependToStatements;
            // WARNING: translate can't be called directly or indirectly after this point in the method, or the prepends won't be included

            F3ClassModel model = initBuilder.createF3ClassModel(tree, 
								attrInfo.toList(),
								overrideInfo.toList(),
								funcInfo.toList(),
								getLiteralInitClassMap(),
								translatedInitBlocks,
								translatedPostInitBlocks);
            additionalImports.appendList(model.additionalImports);
            translatedDefs.appendList(model.additionalClassMembers);
            // build the list of implemented interfaces
            List<JCExpression> implementing = model.interfaces;
            // include the interface only once
            if (!tree.hasBeenTranslated) {
                if (isMixinClass) {
                    JCModifiers mods = m().Modifiers(Flags.PUBLIC | Flags.INTERFACE);
                    mods = addAccessAnnotationModifiers(diagPos, tree.mods.flags, mods);
		    //System.err.println("generating: "+ model.interfaceName);
		    ///System.err.println(tree);
		    //System.err.println(tree.typeArgTypes);
                    JCClassDecl cInterface = m().ClassDef(mods,
							  model.interfaceName, 
							  translateTypeParams(tree, tree.typeArgTypes),
							  null,
							  implementing, 
							  model.iDefinitions);
        
                    cInterface.sym = makeClassSymbol(mods.flags, cInterface.name, 
						     tree.typeArgTypes, 
						     tree.sym.owner);
                    
                    membersToSymbol(cInterface);
                    //System.err.println("generated: "+ cInterface);
                    prependToDefinitions.append(cInterface); // prepend to the enclosing class or top-level
                }
                tree.hasBeenTranslated = true;
            }

            // Class must be visible for reflection.
            long flags = tree.mods.flags & (Flags.FINAL | Flags.ABSTRACT | Flags.SYNTHETIC);
            if ((flags & Flags.SYNTHETIC) == 0) {
                flags |= Flags.PUBLIC;
            }
            if (tree.sym.owner.kind == Kinds.TYP) {
                flags |= Flags.STATIC;
            }
            // VSGC-2831 - Mixins should be abstract.
            if (tree.sym.kind == Kinds.TYP && isMixinClass) {
                flags |= Flags.ABSTRACT;
            }

            JCModifiers classMods = make.at(diagPos).Modifiers(flags);
            classMods = addAccessAnnotationModifiers(diagPos, tree.mods.flags, classMods);
	    //System.err.println("generating: "+ tree);
	    //System.err.println("generating with: "+ tree.typeArgTypes);
            JCClassDecl res = m().ClassDef(
					   classMods,
					   tree.getName(),
					   translateTypeParams(tree, tree.typeArgTypes),
					   model.superType == null ? null : makeType(model.superType, false),
					   implementing,
					   translatedDefs.toList());
	    //	    System.err.println("classDecl="+res);
            res.sym = tree.sym;
            res.type = tree.type;

            membersToSymbol(res);

            setReceiverContext(prevReceiverContext);

            return new StatementsResult(res);
        }

        private void setContext(boolean isStatic) {
            setReceiverContext( isStatic ?
                  ReceiverContext.ScriptAsStatic
                : isMixinClass ?
                          ReceiverContext.InstanceAsStatic
                        : ReceiverContext.InstanceAsInstance );

        }

        private ExpressionResult translateBind(F3AbstractVar var) {
            return var.isBidiBind() ?
                    translateInvBind.translate(var.getInitializer(), var.type, var.sym) :
                    var.isBound() ?
                        translateBind.translateBoundExpression(var.getInitializer(), var.sym) :
                        null;
        }

        private JCExpression translateVarInit(F3AbstractVar var) {
            if (var.getInitializer()==null || var.isBound()) {
                // no init, or init handled by bind or F3VarInit
                return null;
            }
            Name instanceName = (var.isStatic() || !isMixinClass) ? null : defs.receiverName;
            return translateInitExpression(
                    var.pos(),
                    var.getInitializer(),
                    var.sym,
                    instanceName
                 ).expr();
        }

        private void translateAndAppendStaticBlock(F3Block block, ListBuffer<JCStatement> translatedBlocks) {
            JCStatement stmt = translateToStatement(block, syms.voidType);
            if (stmt != null) {
                translatedBlocks.append(stmt);
            }
        }


    }

    private JCExpression translateNonBoundInit(DiagnosticPosition diagPos,
                                F3Expression init,
                                F3VarSymbol vsym) {
        // normal init -- unbound
        if (init == null) {
            // no initializing expression determine a default value from the type
            return makeDefaultValue(diagPos, vsym);
        } else {
            // do a vanilla translation of the expression
            Type resultType = vsym.type;
            JCExpression trans = translateToExpression(init, resultType);
            return convertNullability(diagPos, trans, init, resultType);
        }
    }

    private ExpressionResult translateDefinitionalAssignmentToSetExpression(final DiagnosticPosition diagPos,
            final F3Expression init,
            final F3VarSymbol vsym,
            final Name instanceName) {

        class DefinitionalAssignmentTranslator extends ExpressionTranslator {
            DefinitionalAssignmentTranslator(DiagnosticPosition diagPos) { super(diagPos); }
            
            protected ExpressionResult doit() {
                assert !vsym.isParameter() : "Parameters are not initialized";
                setSubstitution(init, vsym);
                final JCExpression nonNullInit = translateNonBoundInit(diagPos, init, vsym);
                final boolean isLocal = !vsym.isMember();
                assert !isLocal || instanceName == null;
                JCExpression res;
                if (vsym.useAccessors()) {
                    if (vsym.isMember() && vsym.isSequence()) {
                        JCExpression tc =
                                instanceName == null ? getReceiverOrThis(vsym) : id(instanceName);
                        res = Call(defs.Sequences_set, tc, Offset(vsym), nonNullInit);
                    } else {
                        JCExpression tc = instanceName == null ? null : id(instanceName);
                        res = Setter(tc, vsym, nonNullInit);
                    }
                } else {
                    res = nonNullInit;
                    if (vsym.isSequence())
                        res = Call(defs.Sequences_incrementSharing, res);
                    res = Set(vsym, res);
                }
                return toResult(nonNullInit, vsym.type);
            }
        }
        return new DefinitionalAssignmentTranslator(diagPos).doit();
    }

    private ExpressionResult translateInitExpression(final DiagnosticPosition diagPos,
            final F3Expression init,
            final F3VarSymbol vsym,
            final Name instanceName) {

        class InitTranslator extends ExpressionTranslator {
            InitTranslator(DiagnosticPosition diagPos) { super(diagPos); }
            
            protected ExpressionResult doit() {
                assert !vsym.isParameter() : "Parameters are not initialized";
                setSubstitution(init, vsym);
                final JCExpression nonNullInit = translateNonBoundInit(diagPos, init, vsym);
                final boolean isLocal = !vsym.isMember();
                assert !isLocal || instanceName == null;
                return toResult(nonNullInit, vsym.type);
            }
        }
        
        return new InitTranslator(diagPos).doit();
    }

    /**
     * Translate a local variable
     */
    private class VarTranslator extends ExpressionTranslator {

        final F3Var tree;
        final F3VarSymbol vsym;        
        final long modFlags;

        VarTranslator(F3Var tree) {
            super(tree.pos());
            this.tree = tree;
            F3Modifiers mods = tree.getModifiers();
            vsym = tree.getSymbol();
            assert !vsym.isMember() : "attributes are processed in the class and should never come here: " + tree.name;
            assert !vsym.isParameter() : "we should not see parameters here" + tree.name;
            modFlags = (mods.flags & ~Flags.FINAL) | (vsym.isMutatedWithinScript() ? 0L : Flags.FINAL);
        }

        protected AbstractStatementsResult doit() {
            optStat.recordLocalVar(vsym, tree.getBindStatus().isBound(), false);
            if (vsym.hasForwardReference()) {
                // create a blank variable declaration and move the declaration to the beginning of the block
                JCExpression init = makeDefaultValue(null, vsym);
                prependToStatements.prepend(Var(modFlags, tree.type, tree.name, init));
                return translateDefinitionalAssignmentToSetExpression(diagPos, tree.getInitializer(), vsym, null);
            } else {
                // Translate in-place
                JCExpression init = translateNonBoundInit(diagPos, tree.getInitializer(), vsym);
                if (vsym.isSequence())
                    init = Call(defs.Sequences_incrementSharing, init);
                JCStatement var = Var(modFlags, tree.type, tree.name, init);
                return new StatementsResult(var);
            }
        }
    }

    boolean isInnerFunction(MethodSymbol sym) {
        return sym.owner != null && sym.owner.kind != Kinds.TYP && (sym.flags() & Flags.SYNTHETIC) == 0;
    }

    private class BlockExpressionTranslator extends ExpressionTranslator {

        private final F3Expression value;
        private final List<F3Expression> statements;

        BlockExpressionTranslator(F3Block tree) {
            super(tree.pos());
            this.value = tree.value;
            this.statements = tree.getStmts();
        }

        protected AbstractStatementsResult doit() {
            ListBuffer<JCStatement> prevPrependToStatements = prependToStatements;
            try {
                prependToStatements = ListBuffer.lb();

                for (F3Expression expr : statements) {
                    JCStatement stmt = translateToStatement(expr, syms.voidType);
                    if (stmt != null) {
                        addPreface(stmt);
                    }
                }

                if (yield() == ToExpression) {
                    // make into block expression
                    //TODO: this may be unneeded, or even wrong
                    F3Expression rawValue = (value.getF3Tag() == F3Tag.RETURN)?
                         ((F3Return) value).getExpression()
                        : value;

                    JCExpression tvalue = translateExpr(rawValue, targetType); // must be before prepend
                    List<JCStatement> localDefs = prependToStatements.appendList(statements()).toList();
                    return new ExpressionResult(
                            diagPos,
                            List.<JCStatement>nil(), //TODO: statements rolled into expression (below) is this needed?
                            localDefs.size() == 0 ? tvalue : BlockExpression(localDefs, tvalue),
                            bindees(),
                            invalidators(),
                            interClass(),
                            setterPreface(),
                            targetType);
                } else {
                    // make into block
                    if (value != null) {
                        if (value.getF3Tag() == F3Tag.VAR_SCRIPT_INIT && targetType != syms.voidType) {
                            translateStmt(value, syms.voidType);
                            addPreface(Stmt(Get(((F3VarInit) value).getSymbol()), targetType));
                        } else {
			    if (targetType == null) {
				System.err.println("statements="+statements);
			    }
                            translateStmt(value, targetType);
                        }
                    }
                    List<JCStatement> localDefs = prependToStatements.appendList(statements()).toList();
                    return new StatementsResult(localDefs.size() == 1 ? localDefs.head : Block(localDefs));
                }
            } finally {
                prependToStatements = prevPrependToStatements;
            }
        }
    }

    class SequenceActionTranslator extends AssignTranslator {

        final RuntimeMethod meth;

        /**
         *
         * @param diagPos
         * @param ref Variable being referenced (different from LHS if indexed -- where it is sequence or array)
         * @param indexOrNull The index into the variable reference.  Or null if not indexed.
         * @param rhs The expression acting on ref or null
         */
        SequenceActionTranslator(DiagnosticPosition diagPos, F3Expression ref, RuntimeMethod meth, F3Expression indexOrNull, F3Expression rhs) {
            this(diagPos, ref, meth, indexOrNull, syms.voidType, rhs);
        }

        SequenceActionTranslator(DiagnosticPosition diagPos, F3Expression ref, RuntimeMethod meth, F3Expression indexOrNull, Type fullType, F3Expression rhs) {
            super(diagPos, ref, indexOrNull, fullType, rhs);
            this.meth = meth;
        }

        SequenceActionTranslator(DiagnosticPosition diagPos, F3Expression ref, RuntimeMethod meth, F3Expression indexOrNull) {
            this(diagPos, ref, meth, indexOrNull, null);
        }

        @Override
        JCExpression fullExpression(JCExpression tToCheck) {
            return sequencesOp(meth, tToCheck);
        }

        @Override
        JCExpression sequencesOp(RuntimeMethod meth, JCExpression tToCheck) {
            ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
            F3VarSymbol vsym = (F3VarSymbol) refSym;
            boolean useAccessor = vsym.useAccessors();
            JCExpression tRHS = buildRHS(rhsTranslated);
            JCVariableDecl newResult = null;
            if (targetType != syms.voidType) {
                newResult = TmpVar(rhsType(), tRHS);
                tRHS = id(newResult);
            }
            if (! useAccessor) {
                // Non-accessor-using variable sequence -- roughly:
                // lhs = sequenceAction(lhs, rhs);
                args.append(Getter(copyOfTranslatedToCheck(tToCheck), vsym));
            } else {
                // Instance variable sequence -- roughly:
                // sequenceAction(instance, varNum, rhs);
                args.append(instance(tToCheck));
                args.append(Offset(copyOfTranslatedToCheck(tToCheck), vsym));
            }
            if (tRHS != null) {
                args.append(tRHS);
            }
            JCExpression tIndex = translateIndex();
            if (tIndex != null) {
                args.append(tIndex);
                JCExpression tIndex2 = translateIndex2();
                if (tIndex2 != null) {
                    args.append(tIndex2);
                }
            }
            JCExpression res = Call(meth, args);
            if (! useAccessor) {
                res = Setter(tToCheck, vsym, res);
            }
            if (newResult != null) {
                res = BlockExpression(newResult, Stmt(res), id(newResult));
            }
            return res;
        }

        JCExpression translateIndex2() {
            return null;
        }

        /**
         * If we are operating on an array or sequence, convert to the sequence type.
         * Otherwise, to the element type.
         */
        @Override
        protected Type rhsType() {
            if (types.isArray(rhs.type) || types.isSequence(rhs.type))
                return ref.type;
            else
                return types.elementType(ref.type);
        }
    }

    class SequenceSliceActionTranslator extends SequenceActionTranslator {

        private final F3SequenceSlice slice;

        SequenceSliceActionTranslator(F3SequenceSlice slice, RuntimeMethod meth, Type fullType, F3Expression rhs) {
            super(slice.pos(), slice.getSequence(), meth, slice.getFirstIndex(), fullType, rhs);
            this.slice = slice;
        }

        @Override
        JCExpression translateIndex2() {
            return makeSliceEndPos(slice);
        }
    }

    class SequenceInsertTranslator extends SequenceActionTranslator {
        F3SequenceInsert tree;

        public SequenceInsertTranslator(F3SequenceInsert tree) {
            super(
                    tree.pos(),
                    tree.getSequence(),
                    (tree.getPosition() == null)? defs.Sequences_insert : defs.Sequences_insertBefore,
                    tree.getPosition(),
                    tree.getElement());
            this.tree = tree;
        }

        @Override
        JCExpression translateIndex() {
            if (indexOrNull == null) {
                return null;
            }
            JCExpression position = translateExpr(indexOrNull, syms.intType);
            if (tree.shouldInsertAfter()) {
                position = PLUS(position, Int(1));
            }
            return position;
        }
    }

    private class TryTranslator extends ExpressionTranslator {

        private final F3Try tree;

        TryTranslator(F3Try tree) {
            super(tree.pos());
            this.tree = tree;
        }

        protected StatementsResult doit() {
            ListBuffer<JCCatch> tCatchers = ListBuffer.lb();
            for (List<F3Catch> l = tree.catchers; l.nonEmpty(); l = l.tail) {
                F3Catch cat = l.head;
                JCVariableDecl param = convertParam(cat.param);
                JCBlock tCatBody = translateToBlock(cat.body, syms.voidType);
                tCatchers.append(m().Catch(param, tCatBody));
            }
            JCBlock body = translateToBlock(tree.body, syms.voidType);
            JCBlock finalizer = translateToBlock(tree.finalizer, syms.voidType);
            return new StatementsResult(m().Try(body, tCatchers.toList(), finalizer));
        }
    }

    private class WhileTranslator extends ExpressionTranslator {

        private final F3WhileLoop tree;

        WhileTranslator(F3WhileLoop tree) {
            super(tree);
            this.tree = tree;
        }

        protected StatementsResult doit() {
            JCExpression cond = translateToExpression(tree.cond, syms.booleanType);

            JCStatement body = translateToStatement(tree.body, syms.voidType);

            return toStatementResult(m().WhileLoop(cond, body));
        }
    }

    class ScriptTranslator extends Translator {

        final F3Script tree;

        ScriptTranslator(F3Script tree) {
            super(tree.pos());
            this.tree = tree;
        }

        SpecialResult doit() {
            // add to the hasOuters set the class symbols for classes that need a reference to the outer class
            fillClassesWithOuters(tree);

            ListBuffer<JCTree> translatedDefinitions = ListBuffer.lb();
            ListBuffer<JCTree> imports = ListBuffer.lb();
            additionalImports = ListBuffer.lb();
            prependToStatements = prependToDefinitions = ListBuffer.lb();
            for (F3Tree def : tree.defs) {
                switch (def.getF3Tag()) {
                    case IMPORT:
                        // ignore import
                        break;
                    case CLASS_DEF:
                        translatedDefinitions.appendList(translateToStatementsResult((F3ClassDeclaration) def, syms.voidType).trees());
                        break;
                    default:
                        assert false : "something wierd in the script: " + def;
                        break;
                }
            }

            // order is imports, any prepends, then the translated non-imports
            for (JCTree prepend : prependToDefinitions) {
                translatedDefinitions.prepend(prepend);
            }

            for (JCTree prepend : imports) {
                translatedDefinitions.prepend(prepend);
            }

            for (JCExpression prepend : additionalImports) {
                translatedDefinitions.append(m().Import(prepend, false));
            }

            prependToStatements = prependToDefinitions = null; // shouldn't be used again until the next top level

            JCExpression pid = straightConvert(tree.pid);
            JCCompilationUnit translated = m().TopLevel(List.<JCAnnotation>nil(), pid, translatedDefinitions.toList());
            translated.sourcefile = tree.sourcefile;
            // System.err.println("<translated src="+tree.sourcefile+">"); System.err.println(translated); System.err.println("</translated>");
            translated.docComments = null;
            translated.lineMap = tree.lineMap;
            translated.flags = tree.flags;
            return new SpecialResult(translated);
        }
    }

    class InvalidateTranslator extends ExpressionTranslator {

        private final F3Expression varRef;
        private final Symbol vsym;
        private JCVariableDecl invalVar = null;
        private JCVariableDecl newLenVar = null;

        InvalidateTranslator(F3Invalidate tree) {
            super(tree.pos());
            this.varRef = tree.getVariable();
            this.vsym = F3TreeInfo.symbol(varRef);
        }

        private JCExpression receiver() {
            return invalVar == null ? null : id(invalVar);
        }

        private void callInvalidate(Name phase) {
            ListBuffer<JCExpression> args = ListBuffer.lb();
            if (types.isSequence(vsym.type)) {
                addPreface(newLenVar);
                args.append(Int(0));
                args.append(MINUS(id(newLenVar), Int(1)));
                args.append(id(newLenVar));
            }

            args.append(id(phase));

            addPreface(CallStmt(receiver(), attributeInvalidateName(vsym), args.toList()));
        }

        protected AbstractStatementsResult doit() {
            if (varRef.getF3Tag() == F3Tag.SELECT) {
                F3Select sel = (F3Select) varRef;
                JCExpression selected = translateToExpression(sel.selected, sel.selected.type);
                if (selected != null) {
                    invalVar = TmpVar("inval", sel.selected.type, selected);
                    addPreface(invalVar);
                }
            }

            if (types.isSequence(vsym.type)) {
                newLenVar = TmpVar("newLen", syms.intType, Call(receiver(), attributeSizeName(vsym)));
                addPreface(newLenVar);
            }

            callInvalidate(defs.phaseTransitionCASCADE_INVALIDATE);
            callInvalidate(defs.phaseTransitionCASCADE_TRIGGER);

            return toStatementResult();
        }
    }

    /***********************************************************************
     *
     * Utilities
     *
     */

    protected String getSyntheticPrefix() {
        return "f3$";
    }

    private void fillClassesWithOuters(F3Script tree) {
        class FillClassesWithOuters extends F3TreeScanner {
            F3ClassDeclaration currentClass;

            @Override
            public void visitClassDeclaration(F3ClassDeclaration tree) {
                F3ClassDeclaration prevClass = currentClass;
                try {
                    currentClass = tree;
                    super.visitClassDeclaration(tree);
                }
                finally {
                    currentClass = prevClass;
                }
            }

            @Override
            public void visitIdent(F3Ident tree) {
                super.visitIdent(tree);
                if (currentClass != null && tree.sym.kind != Kinds.TYP) {
                    addOutersForOuterAccess(tree.sym, currentClass.sym);
                }
            }

            @Override
            public void visitSelect(F3Select tree) {
                super.visitSelect(tree);
                Symbol sym = expressionSymbol(tree.selected);
                if (currentClass != null && sym != null && sym.kind == Kinds.TYP) {
                    addOutersForOuterAccess(tree.sym, currentClass.sym);
                }
            }

            @Override // Need this because F3TreeScanner is not visiting the args of the F3Instanciate tree. Starting to visit them generate tons of errors.
            public void visitInstanciate(F3Instanciate tree) {
                super.visitInstanciate(tree);
                super.scan(tree.getArgs());
            }

            private void addOutersForOuterAccess(Symbol sym, Symbol currentClass) {
                if (sym != null && (sym.kind == Kinds.VAR || sym.kind == Kinds.MTH)
                        && !sym.isStatic() && sym.owner.kind == Kinds.TYP && currentClass != null) {
                    Type ctype = currentClass.type;
                    boolean foundOwner = false;
                    while (ctype != Type.noType &&
                            types.isMixin(ctype.tsym) == types.isMixin(currentClass)) {
                        if (ctype.tsym.isSubClass(sym.owner, types)) {
                            foundOwner = true;
                            break;
                        }
                        ctype = ctype.getEnclosingType();
                    }
                    if (!foundOwner) {
                        Symbol csym = null;
                        while (currentClass != null) {
                            if (currentClass.isSubClass(sym.owner, types)) {
                                getHasOuters().put((ClassSymbol)csym, (ClassSymbol)currentClass);
                                break;
                            }
                            csym = currentClass;
                            currentClass = currentClass.owner.enclClass();
                        }
                    }
                }
            }
        }

        new FillClassesWithOuters().scan(tree);
    }

    /***********************************************************************
     *
     * Visitors  (alphabetical order)
     *
     * Overrides to add contructs allowed in non-bound contexts.
     */

    @Override
    public void visitAssign(final F3Assign tree) {
        if (types.isSequence(tree.lhs.type)) {
            if (tree.lhs.getF3Tag() == F3Tag.SEQUENCE_SLICE) {
                result = new SequenceSliceActionTranslator((F3SequenceSlice) tree.lhs, defs.Sequences_replaceSlice, tree.type, tree.rhs).doit();
            } else {
                result = new SequenceActionTranslator(tree.pos(), tree.lhs, defs.Sequences_set, null, tree.type, tree.rhs) {

                    @Override
                    protected Type rhsType() {
                        return tree.type;
                    }
                }.doit();
            }
        } else {
            result = new AssignTranslator(tree.pos(), tree.lhs, tree.rhs) {

                @Override
                JCExpression defaultFullExpression(JCExpression lhsTranslated, JCExpression rhsTranslated) {
                    return m().Assign(lhsTranslated, rhsTranslated);
                }
            }.doit();
        }
    }

    @Override
    public void visitAssignop(final F3AssignOp tree) {
        badVisitor("Assignop should have been lowered");
    }

    public void visitBlockExpression(F3Block tree) {
        result = new BlockExpressionTranslator(tree).doit();
    }

    @Override
    public void visitBreak(F3Break tree) {
        result = new StatementsResult(make.at(tree.pos).Break(tree.label));
    }

    @Override
    public void visitClassDeclaration(F3ClassDeclaration tree) {
        F3ClassDeclaration prevClass = currentClass();
        setCurrentClass(tree);

        if (tree.isScriptClass()) {
            scriptBegin();
        }

        try {
            if (tree.isScriptClass()) {
                setLiteralInitClassMap(new LiteralInitClassMap());
            }
            result = new ClassDeclarationTranslator(tree).doit();
        } finally {
            setCurrentClass(prevClass);
        }
    }

    @Override
    public void visitContinue(F3Continue tree) {
        result = new StatementsResult(make.at(tree.pos).Continue(tree.label));
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        F3FunctionDefinition prevFunction = currentFunction();
        try {
            setCurrentFunction(tree);
            result = new FunctionTranslator(tree, false).doit();
        }
        finally {
            setCurrentFunction(prevFunction);
        }
    }

    @Override
    public void visitFunctionValue(F3FunctionValue tree) {
        F3FunctionDefinition def = tree.definition;
	Type t = tree.type;
	//System.err.println("TREE = "+tree);
	//System.err.println("T="+t);
	result = new FunctionValueTranslator(make.Ident(defs.lambda_MethodName), def, tree.pos(), t.asMethodType(), tree.type).doit();
    }

    public void visitIdent(F3Ident tree) {
        result = new IdentTranslator(tree).doit();
    }

    @Override
    public void visitInvalidate(final F3Invalidate tree) {
        result = new InvalidateTranslator(tree).doit();
    }

    @Override
    public void visitParens(F3Parens tree) {
        if (yield() == ToExpression) {
            result = translateToExpressionResult(tree.getExpression(), targetType);
        } else {
            result = translateToStatementsResult(tree.getExpression(), targetType);
        }
    }

    @Override
    public void visitReturn(F3Return tree) {
        F3Expression exp = tree.getExpression();
        if (exp == null) {
            result = new StatementsResult(make.at(tree).Return(null));
        } else {
            result = translateToStatementsResult(exp, exp.type);
        }
    }

    @Override
    public void visitScript(F3Script tree) {
        if (depGraphWriter != null) {
            depGraphWriter.start(tree);
        }
        try {
            result = new ScriptTranslator(tree).doit();
        } finally {
            if (depGraphWriter != null) {
                depGraphWriter.end();
            }
        }
    }

    public void visitSelect(F3Select tree) {
	Symbol sitesym = F3TreeInfo.symbol(tree.selected);
	if (F3Attr.isType(sitesym)) {
	    if (!tree.sym.isStatic()) {
		// need to generate a lambda here...
		//
		System.err.println("not implemented: "+tree);
	    }
	}
	result = new SelectTranslator(tree).doit();
    }

    @Override
    public void visitSequenceDelete(F3SequenceDelete tree) {
        DiagnosticPosition diagPos = tree.pos();
        F3Expression seq = tree.getSequence();
        SequenceActionTranslator trans;
        if (tree.getElement() != null) {
            trans = new SequenceActionTranslator(diagPos, seq, defs.Sequences_deleteValue, null, tree.getElement());
        } else {
            switch (seq.getF3Tag()) {
                case SEQUENCE_INDEXED:
                    F3SequenceIndexed si = (F3SequenceIndexed) seq;
                    trans = new SequenceActionTranslator(diagPos, si.getSequence(), defs.Sequences_deleteIndexed, si.getIndex());
                    break;
                case SEQUENCE_SLICE:
                    final F3SequenceSlice ss = (F3SequenceSlice) seq;
                    trans = new SequenceSliceActionTranslator((F3SequenceSlice) seq, defs.Sequences_deleteSlice, syms.voidType, null);
                    break;
                default:
                    if (types.isSequence(seq.type)) {
                        trans = new SequenceActionTranslator(diagPos, seq, defs.Sequences_deleteAll, null);
                    } else {
                        TODO("delete non-sequence");
                        trans = null; //shut-up
                    }
            }
        }
        result = trans.doit();
    }

    @Override
    public void visitSequenceInsert(F3SequenceInsert tree) {
        result = new SequenceInsertTranslator(tree).doit();
    }

    @Override
    public void visitSkip(F3Skip tree) {
        result = new StatementsResult(make.at(tree.pos).Skip());
    }

    @Override
    public void visitThrow(F3Throw tree) {
        JCTree expr = translateToExpression(tree.expr, tree.type);
        result = new StatementsResult(make.at(tree.pos).Throw(expr));
    }

    @Override
    public void visitTry(F3Try tree) {
        result = new TryTranslator(tree).doit();
    }

    @Override
    public void visitVar(F3Var tree) {
        result = new VarTranslator(tree).doit();
    }

    @Override
    public void visitWhileLoop(F3WhileLoop tree) {
        result = new WhileTranslator(tree).doit();
    }

}
