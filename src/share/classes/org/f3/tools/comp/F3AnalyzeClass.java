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

import org.f3.api.F3BindStatus;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Scope.Entry;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCExpression;
import com.sun.tools.mjavac.tree.JCTree.JCStatement;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;

import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3AbstractTranslation.*;
import org.f3.tools.tree.*;

import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import static com.sun.tools.mjavac.code.Flags.*;

import java.util.*;

/**
 * This class is used by F3InitializationBuilder to determine which inherited
 * attributes and methods have effect in the current f3 class.
 * @author Robert Field
 * @author Jim Laskey
 */
class F3AnalyzeClass {
    // Invoking translator.
    private final F3InitializationBuilder initBuilder;

    // Position in the current f3 class source.
    private final DiagnosticPosition diagPos;

    // Current class decl.
    private final F3ClassDeclaration currentClassDecl;

    // Current class symbol.
    private final ClassSymbol currentClassSym;

    // Null or symbol for the immediate super class (if a f3 class.)
    private ClassSymbol superClassSym;

    // Resulting list of all superclasses in top down order.
    private ListBuffer<ClassSymbol> superClasses = ListBuffer.lb();

    // Resulting list of immediate mixin classes in left to right order.
    private ListBuffer<ClassSymbol> immediateMixins = ListBuffer.lb();

    // Resulting list of all mixin classes in top down order.
    private ListBuffer<ClassSymbol> allMixins = ListBuffer.lb();

    // Number of vars in the current class (includes mixins.)
    private int classVarCount;

    // Number of vars in the current class (includes mixins.)
    private int scriptVarCount;
    
    // Resulting list of class vars.
    private final ListBuffer<VarInfo> classVarInfos = ListBuffer.lb();

    // Resulting list of script vars.
    private final ListBuffer<VarInfo> scriptVarInfos = ListBuffer.lb();

    // Resulting list of class vars.
    private final ListBuffer<FuncInfo> classFuncInfos = ListBuffer.lb();

    // Resulting list of script vars.
    private final ListBuffer<FuncInfo> scriptFuncInfos = ListBuffer.lb();

    // List of all attributes.  Used to track overridden and mixin attributes.
    private final Map<Name, VarInfo> visitedAttributes = new HashMap<Name, VarInfo>();
    
    // Map of all bind selects used to construct the class update$ method.
    private final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> classUpdateMap = new LinkedHashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>>();

    // Map of all bind selects used to construct the script update$ method.
    private final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> scriptUpdateMap = new LinkedHashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>>();

    // Resulting list of relevant methods.  A map is used to so that only the last occurrence is kept.
    private final Map<String, FuncInfo> needDispatchMethods = new LinkedHashMap<String, FuncInfo>();

    // List of all methods.  Used to track whether a mixin method should be included.
    private final Map<String, FuncInfo> visitedMethods = new HashMap<String, FuncInfo>();

    // List of classes encountered.  Used to prevent duplication.
    private final Set<Symbol> addedBaseClasses = new HashSet<Symbol>();

    // List of vars (attributes) found in the current f3 class (supplied by F3InitializationBuilder.)
    private final List<TranslatedVarInfo> translatedAttrInfo;

    // List of overriding vars (attributes) found in the current f3 class (supplied by F3InitializationBuilder.)
    private final List<TranslatedOverrideClassVarInfo> translatedOverrideAttrInfo;

    // List of functions (methods) found in the current f3 class (supplied by F3InitializationBuilder.)
    private final List<TranslatedFuncInfo> translatedFuncInfo;

    // Global names table (supplied by F3InitializationBuilder.)
    private final Name.Table names;

    // Global types table (supplied by F3InitializationBuilder.)
    private final F3Types types;

    // Global defs table (supplied by F3InitializationBuilder.)
    private final F3Defs defs;

    // Global syms table (supplied by F3InitializationBuilder.)
    private final F3Symtab syms;
     
    // Class reader used to fetch superclass .class files (supplied by F3InitializationBuilder.)
    private final F3ClassReader reader;


    //
    // This class supers all classes used to hold var information. Consumed by
    // F3InitializationBuilder.
    //
    static abstract class VarInfo {
        // Position of the var declaration or current f3 class if read from superclass.
        private final DiagnosticPosition diagPos;

        // Var symbol (unique to symbol.)
        protected final F3VarSymbol sym;

        // Name of the var (is it the same as sym.name?)
        private final Name name;

        // Null or code for initializing the var.
        protected final JCExpression initExpr;

        // The class local enumeration value for this var.
        private int enumeration = -1;

        // Inversion of boundBindees.
        private HashSet<VarInfo> binders = new LinkedHashSet<VarInfo>();
        
        // Inversion of invalidators.
        private ListBuffer<BindeeInvalidator> boundInvalidatees = ListBuffer.lb();;
        
        // True if the var needs to generate mixin interfaces (getMixin$, setMixin$ and getVOFF$)
        private boolean needsMixinInterface = false;

        private VarInfo(DiagnosticPosition diagPos, Name name, F3VarSymbol attrSym, JCExpression initExpr) {
            this.diagPos = diagPos;
            this.name = name;
            this.sym = attrSym;
            this.initExpr = initExpr;
        }

        // Return the var symbol.
        public F3VarSymbol getSymbol() { return sym; }

        // Return the var position.
        public DiagnosticPosition pos() { return diagPos; }

        // Return type information from type translation.
        public Type getRealType()     { return sym.type; }
        public Type getElementType()  { return sym.getElementType(); }
        public boolean useAccessors() { return sym.useAccessors(); }
        public boolean useGetters()   { return sym.useGetters(); }
        public boolean useSetters()   { return sym.useSetters() || needsMixinInterface(); }

        // Return var name.
        public Name getName() { return name; }

        // Return var name as string.
        public String getNameString() { return name.toString(); }

        // Return modifier flags from the symbol.
        public long getFlags() { return sym.flags(); }

        // Return true if the var is a bare bones synthesize var (bind temp.)
        public boolean isBareSynth() {
            return (getFlags() & F3Flags.VARMARK_BARE_SYNTH) != 0;
        }
        
        // Returns true if the var is read only.
        public boolean isReadOnly() {
            return isDef() || (hasBoundDefinition() && !hasBiDiBoundDefinition());
        }
        
        // Returns true if the var needs to generate mixin interfaces (getMixin$, setMixin$ and getVOFF$)
        public boolean needsMixinInterface() {
            return needsMixinInterface;
        }
        
        // Indicate if the var needs a mixin interface.
        public void setNeedsMixinInterface(boolean needs) {
            needsMixinInterface = needs;
        }
        
        // Return true if the var/override has an initializing expression
        public boolean hasInitializer() { return false; }

        // true if this variable is initialized with a "safe" expression -
        // so that we don't need a try..catch error handler wrapper in it's
        // getter method.
        public boolean hasSafeInitializer() { return false; }

        // Return true if the var has a VarInit
        public boolean hasVarInit() { return false; }

        // is this initialzed with a bound function result var?
        public boolean isInitWithBoundFuncResult() { return false; }

        // Is this variable is initialized by another synthetic variable
        // of Pointer type and that var stores result from a bound function call?
        // If so, return the symbol of the synthetic variable.
        public Symbol boundFuncResultInitSym() { return null; }

        // Return true if the var has a bound definition.
        public boolean hasBoundDefinition() { return false; }
        
        // Return true if the var has a bidirectional bind.
        public boolean hasBiDiBoundDefinition() { return false; }
        
        // Return true if the var is an inline bind.
        public boolean isInlinedBind() { return hasBoundDefinition(); }

        // Generally means that the var needs to be included in the current method.
        public boolean needsCloning() { return false; }
        
        // Return true if the var is an override.
        public boolean isOverride() { return false; }

        // A proxy var serves several roles, but generally means that the proxy's
        // qualified name should be used in place of the current var's qualified name.
        public VarInfo proxyVar() { return null; }
        public boolean hasProxyVar() { return proxyVar() != null; }

        // An override is non-null when a mixin var is overridden in the mixee.
        public VarInfo overrideVar() { return null; }
        public boolean hasOverrideVar() { return overrideVar() != null; }

        // Convenience method to return the current symbol to be used for qualified name.
        public F3VarSymbol proxyVarSym() { return hasProxyVar() ? proxyVar().sym : sym; }
        
        // Predicate for static var test.
        public boolean isStatic() { return sym.isStatic(); }

        // Predicate for synthetic var test.
        public boolean isSynthetic() { return (getFlags() & Flags.SYNTHETIC) != 0L; }

        // Predicate for default var test.
        public boolean isDefault() { return (getFlags() & F3Flags.DEFAULT) != 0L; }
        
        // Predicate for protexted var test.
        public boolean isProtectedAccess() { return (getFlags() & Flags.PROTECTED) != 0L; }
        
        // Predicate for public var test.
        public boolean isPublicAccess() { return (getFlags() & Flags.PUBLIC) != 0L; }
        
        // Predicate for public read var test.
        public boolean isPublicReadAccess() { return (getFlags() & F3Flags.PUBLIC_READ) != 0L; }
        
        // Predicate for public init var test.
        public boolean isPublicInitAccess() { return (getFlags() & F3Flags.PUBLIC_INIT) != 0L; }
        
        // Predicate for is externally seen test.
        public boolean isExternallySeen() { return sym.isExternallySeen(); }
        
        // Predicate for script private var test.
        public boolean hasScriptOnlyAccess() { return sym.hasScriptOnlyAccess(); }

        // Predicate for def (constant) var.
        public boolean isDef() { return sym.isDef(); }

        // Predicate for parameter var.
        public boolean isParameter() { return sym.isParameter(); }

        // Predicate for self-reference in init.
        public boolean hasSelfReference() { return sym.hasSelfReference(); }

        // Predicate whether the var came from a mixin.
        public boolean isMixinVar() { return isMixinClass(sym.owner); }

        // Predicate whether the var came from the current f3 class.
        public boolean isDirectOwner() { return false; }

        // Predicate to test for sequence.
        public boolean isSequence() {
            return sym.isSequence();
        }
        // Returns null or the code for var initialization.
        public JCExpression getDefaultInitExpression() { return initExpr; }

        // Class local enumeration accessors.
        public int  getEnumeration()                { return enumeration; }
        public void setEnumeration(int enumeration) { this.enumeration = enumeration; }
        public boolean hasEnumeration()             { return enumeration != -1; }
        public boolean needsEnumeration() {
            return !isOverride() &&
                   needsCloning() &&
                   sym.needsEnumeration();
        }

        // null or f3 tree for the var's 'on replace'.
        public F3OnReplace onReplace() { return null; }

        // null or Java tree for var's on-replace for use in a setter.
        public JCStatement onReplaceAsInline() { return null; }

        // null or Java tree for var's on-replace for use in change listeber.
        public JCStatement onReplaceAsListenerInstanciation() { return null; }

        // null or f3 tree for the var's 'on invalidate'.
        public F3OnReplace onInvalidate() { return null; }

        // null or Java tree for var's on-invalidate for use in var$invalidate method.
        public JCStatement onInvalidateAsInline() { return null; }

        // Is there a getter expression of bound variable
        public boolean hasBoundInit() { return false; }

        // Null or Java code for getter expression of bound variable
        public JCExpression boundInit() { return null; }

        // Empty or Java preface code for getter of bound variable
        public List<JCStatement> boundPreface() { return List.<JCStatement>nil(); }

        // Empty or Java preface code for setting of bound with inverse variable
        public List<JCStatement> boundInvSetterPreface() { return List.<JCStatement>nil(); }

        // Empty or variable symbols on which this variable depends
        public List<F3VarSymbol> boundBindees() { return List.<F3VarSymbol>nil(); }
        
        // Bound variable symbols on which this variable is used.
        public HashSet<VarInfo> boundBinders() { return binders; }
        
        // Bound sequences that need to be invalidated when invalidated.
        public List<BindeeInvalidator> boundInvalidatees() { return boundInvalidatees.toList(); }

        // Empty or bound select pairs.
        public List<DependentPair> boundBoundSelects() { return List.<DependentPair>nil(); }

        // Predicate for generating sequence style accessors -- if bound, must be virtual
        public boolean generateSequenceAccessors() { return isSequence() &&
                                    (hasBoundDefinition() == isBoundVirtualSequence()); }

        // Predicate for bound sequence represented as virtual sequence
        public boolean isBoundVirtualSequence() { return false; }

        // Null or Java code for getting the element of a bound sequence
        public JCStatement boundElementGetter() { return null; }

        // Null or Java code for getting the size of a bound sequence
        public JCStatement boundSizeGetter() { return null; }
        
        // Null or Java code for invalidation of a bound sequence
        public List<BindeeInvalidator> boundInvalidators() { return List.<BindeeInvalidator>nil(); }
        
        // Return true if the var has dependents.
        public boolean hasDependents() {
            return (getFlags() & (F3Flags.VARUSE_BIND_ACCESS | F3Flags.VARUSE_VARREF)) != 0 || !boundBinders().isEmpty();
        }
        
        // Return true if the var is dependent.
        public boolean isDependent() {
            return (getFlags() & (F3Flags.VARUSE_BOUND_INIT | F3Flags.VARUSE_VARREF)) != 0 || hasBiDiBoundDefinition() ||
                   !boundBindees().isEmpty() || !boundBoundSelects().isEmpty();
        }

        @Override
        public String toString() { return getNameString(); }

        // Useful diagnostic tool.
        public void printInfo() {
            printInfo(true);
        }
        public void printInfo(boolean detail) {
            System.err.println("    " + getEnumeration() + ". " +
                               getSymbol() +
                               ", type=" + getSymbol().type +
                               ", owner=" + getSymbol().owner +
                               (isStatic() ? ", static" : "") +
                               (isDefault() ? ", default" : "") +
                               (isSynthetic() ? ", synthetic" : "") +
                               (useAccessors() ? ", useAccessors" : "") +
                               (needsCloning() ? ", clone" : "") +
                               (isDef() ? ", isDef" : "") +
                               (!boundBindees().isEmpty() ? ", intra binds" : "") + 
                               (!boundBoundSelects().isEmpty() ? ", inter binds" : "") + 
                               (binders != null ?  ", binders" : "") + 
                               (!boundInvalidatees.isEmpty() ?  ", invalidators" : "") + 
                               (getDefaultInitExpression() != null ? ", init" : "") +
                               (isBareSynth() ? ", bare" : "") +
                               (needsMixinInterface() ? ", needsMixinInterface" : "") +
                               ", class=" + getClass().getSimpleName());
            if (detail) {
                long flags = getFlags();
                System.err.println("    " +
                                   (((flags & F3Flags.VARUSE_HAS_TRIGGER) != 0)       ? ", VARUSE_HAS_TRIGGER" : "") +
                                   (((flags & F3Flags.VARUSE_BOUND_INIT) != 0)        ? ", VARUSE_BOUND_INIT" : "") +
                                   (((flags & F3Flags.VARUSE_ASSIGNED_TO) != 0)       ? ", VARUSE_ASSIGNED_TO" : "") +
                                   (((flags & F3Flags.VARUSE_OBJ_LIT_INIT) != 0)      ? ", VARUSE_OBJ_LIT_INIT" : "") +
                                   (((flags & F3Flags.VARUSE_FORWARD_REFERENCE) != 0) ? ", VARUSE_FORWARD_REFERENCE" : "") +
                                   (((flags & F3Flags.VARUSE_SELF_REFERENCE) != 0)    ? ", VARUSE_SELF_REFERENCE" : "") +
                                   (((flags & F3Flags.VARUSE_OPT_TRIGGER) != 0)       ? ", VARUSE_OPT_TRIGGER" : "") +
                                   (((flags & F3Flags.VARUSE_TMP_IN_INIT_EXPR) != 0)  ? ", VARUSE_TMP_IN_INIT_EXPR" : "") +
                                   (((flags & F3Flags.VARUSE_NEED_ACCESSOR) != 0)     ? ", VARUSE_NEED_ACCESSOR" : "") +
                                   (((flags & F3Flags.VARUSE_NON_LITERAL) != 0)       ? ", VARUSE_NON_LITERAL" : "") +
                                   (((flags & F3Flags.VARUSE_BIND_ACCESS) != 0)       ? ", VARUSE_BIND_ACCESS" : "") +
                                   (((flags & F3Flags.VARUSE_VARREF) != 0)            ? ", VARUSE_VARREF" : "") +
                                   (((flags & F3Flags.VARUSE_SPECIAL) != 0)           ? ", VARUSE_SPECIAL" : "") +
                                   (getSymbol().isExternallySeen()                         ? ", EXTERNALLY SEEN" : ""));
                if (!boundBoundSelects().isEmpty()) {
                    for (DependentPair pair : boundBoundSelects()) {
                        System.err.println("        select=" + pair.instanceSym + " " + pair.referencedSym);
                    }
                }
                if (!boundBindees().isEmpty()) {
                    for (F3VarSymbol bindeeSym : boundBindees()) {
                        System.err.println("        bindee=" + bindeeSym);
                    }
                }
                if (hasProxyVar()) {
                    System.err.print("        proxy=");
                    proxyVar().printInfo(false);
                }
                
                if (hasOverrideVar()) {
                    System.err.print("        override=");
                    overrideVar().printInfo(false);
                }
                
                if (boundElementGetter() != null) {
                    System.err.print("        element getter=");
                    System.err.println(boundElementGetter());
                }
                
                if (boundSizeGetter() != null) {
                    System.err.print("        size getter=");
                    System.err.println(boundSizeGetter());
                }
                
                if (boundInvalidators().size() != 0) {
                    System.err.println("        invalidators=");
                    for (BindeeInvalidator bi : boundInvalidators()) {
                        System.err.println("          " + bi.bindee + " " + bi.invalidator);
                    }
                }
            }
        }
    }

    //
    // This base class is used for vars declared in the current f3 class..
    //
    static abstract class TranslatedVarInfoBase extends VarInfo {

        // Null or f3 code for the var's on replace.
        private final F3OnReplace onReplace;

        // Null or f3 code for the var's on invalidate.
        private final F3OnReplace onInvalidate;

        // The bind status for the var/override
        private final F3BindStatus bindStatus;

        // The does this var have an initializing expression
        private final boolean hasInitializer;

        // Null or java code for the var's on replace inlined in setter.
        private final JCStatement onReplaceAsInline;

        // Null or java code for the var's on invalidate inlined in var$invalidate method.
        private final JCStatement onInvalidateAsInline;

        // Result of bind translation
        private final ExpressionResult bindOrNull;

        TranslatedVarInfoBase(DiagnosticPosition diagPos, Name name, F3VarSymbol attrSym, F3BindStatus bindStatus, boolean hasInitializer, 
                JCExpression initExpr, ExpressionResult bindOrNull,
                F3OnReplace onReplace, JCStatement onReplaceAsInline,
                F3OnReplace onInvalidate, JCStatement onInvalidateAsInline) {
            super(diagPos, name, attrSym, initExpr);
            this.hasInitializer = hasInitializer;
            this.bindStatus = bindStatus;
            this.bindOrNull = bindOrNull;
            this.onReplace = onReplace;
            this.onReplaceAsInline = onReplaceAsInline;
            this.onInvalidate = onInvalidate;
            this.onInvalidateAsInline = onInvalidateAsInline;
        }

        // Return true if the var/override has an initializing expression
        @Override
        public boolean hasInitializer() { return hasInitializer; }

        // Return true if the var has a bound definition.
        @Override
        public boolean hasBoundDefinition() { return bindStatus.isBound(); }

        // Return true if the var has a bidirectional bind.
        @Override
        public boolean hasBiDiBoundDefinition() { return bindStatus.isBidiBind(); }

        // Is there a getter expression of bound variable
        @Override
        public boolean hasBoundInit() { return bindOrNull==null? false : bindOrNull.hasExpr(); }

        // Null or Java code for getter expression of bound variable
        @Override
        public JCExpression boundInit() { return bindOrNull==null? null : bindOrNull.expr(); }

        // Null or Java preface code for getter of bound variable
        @Override
        public List<JCStatement> boundPreface() { return bindOrNull==null? List.<JCStatement>nil() : bindOrNull.statements(); }

        // Empty or Java preface code for setting of bound with inverse variable
        @Override
        public List<JCStatement> boundInvSetterPreface() { return bindOrNull==null? List.<JCStatement>nil() : bindOrNull.setterPreface(); }

        // Variable symbols on which this variable depends
        @Override
        public List<F3VarSymbol> boundBindees() { return bindOrNull==null? List.<F3VarSymbol>nil() : bindOrNull.bindees(); }

        // Empty or bound select pairs.
        @Override
        public List<DependentPair> boundBoundSelects() { return bindOrNull == null? List.<DependentPair>nil() : bindOrNull.interClass(); }

        // Return true if this is a bound sequence represented as virtual sequence
        @Override
        public boolean isBoundVirtualSequence() { return bindOrNull==null? false : bindOrNull.isBoundVirtualSequence(); }

        // Null or Java code for getting the element of a bound sequence
        @Override
        public JCStatement boundElementGetter() { return !generateSequenceAccessors() || bindOrNull == null ? null : bindOrNull.getElementMethodBody(); }

        // Null or Java code for getting the size of a bound sequence
        @Override
        public JCStatement boundSizeGetter() { return !generateSequenceAccessors() || bindOrNull == null ? null : bindOrNull.getSizeMethodBody(); }
        
        // Null or Java code for invalidation of a bound sequence
        @Override
        public List<BindeeInvalidator> boundInvalidators() { return bindOrNull == null ? List.<BindeeInvalidator>nil() : bindOrNull.invalidators(); }

        // Possible f3 code for the var's 'on replace'.
        @Override
        public F3OnReplace onReplace() { return onReplace; }

        // Possible java code for the var's 'on replace' in setter.
        @Override
        public JCStatement onReplaceAsInline() { return onReplaceAsInline; }

        // Possible f3 code for the var's 'on invalidate'.
        @Override
        public F3OnReplace onInvalidate() { return onInvalidate; }

        // Possible java code for the var's 'on invalidate' in var$invalidate method.
        @Override
        public JCStatement onInvalidateAsInline() { return onInvalidateAsInline; }

        // This var is in the current f3 class so it has to be cloned into the java class.
        @Override
        public boolean needsCloning() { return true; }

        // Has to be the current f3 class.
        @Override
        public boolean isDirectOwner() { return true; }
    }

    //
    // This class is used for basic vars declared in the current f3 class.
    //
    static class TranslatedVarInfo extends TranslatedVarInfoBase {
        // Tree for the f3 var.
        private final F3Var var;
        private final Symbol boundFuncResultInitSym;

        TranslatedVarInfo(F3Var var, 
                JCExpression initExpr, Symbol boundFuncResultInitSym,
                ExpressionResult bindOrNull, 
                F3OnReplace onReplace, JCStatement onReplaceAsInline,
                F3OnReplace onInvalidate, JCStatement onInvalidateAsInline) {
            super(var.pos(), var.sym.name, var.sym, var.getBindStatus(), var.getInitializer()!=null,
                  initExpr, bindOrNull,
                  onReplace, onReplaceAsInline, onInvalidate, onInvalidateAsInline);
            this.var = var;
            this.boundFuncResultInitSym = boundFuncResultInitSym;
        }

        // Returns the tree for the f3 var.
        public F3Var f3Var() { return var; }

        @Override
        public boolean hasSafeInitializer() {
            // If unexaminable code can be executed (function/method call or init block) or
            // a division can happen then it is not exception safe.
            if (hasInitializer()) {
                class ExceptionThrowingScanner extends F3TreeScanner {

                    boolean safe = true;

                    private void markCanThrowException() {
                        safe = false;
                    }

                    @Override
                    public void visitBinary(F3Binary tree) {
                        switch (tree.getF3Tag()) {
                            case DIV:
                            case MOD:
                                markCanThrowException();
                                break;
                            default:
                                super.visitBinary(tree);
                                break;
                        }
                    }

                    @Override
                    public void visitAssignop(F3AssignOp tree) {
                        switch (tree.getF3Tag()) {
                            case DIV_ASG:
                                markCanThrowException();
                                break;
                            default:
                                super.visitAssignop(tree);
                                break;
                        }
                    }

                    @Override
                    public void visitInstanciate(F3Instanciate tree) {
                        markCanThrowException();
                    }

                    @Override
                    public void visitFunctionInvocation(F3FunctionInvocation tree) {
                        markCanThrowException();
                    }
                }
                ExceptionThrowingScanner scanner = new ExceptionThrowingScanner();
                scanner.scan(var.getInitializer());
                return scanner.safe;
            } else {
                return false;
            }
        }
        
        // Return true if the var has a VarInit
        @Override
        public boolean hasVarInit() { return var.getVarInit() != null; }

        @Override
        public boolean isInitWithBoundFuncResult() {
            return boundFuncResultInitSym != null;
        }

        @Override
        public Symbol boundFuncResultInitSym() {
            return boundFuncResultInitSym;
        }
    }

    //
    // This class represents a var override declared in the current f3 class.
    //
    static class TranslatedOverrideClassVarInfo extends TranslatedVarInfoBase {
        // Reference to the var information the override overshadows.
        private VarInfo proxyVar;
        private final Symbol boundFuncResultInitSym;


        TranslatedOverrideClassVarInfo(F3OverrideClassVar override,
                JCExpression initExpr, Symbol boundFuncResultInitSym,
                ExpressionResult bindOrNull,
                F3OnReplace onReplace, JCStatement onReplaceAsInline,
                F3OnReplace onInvalidate, JCStatement onInvalidateAsInline) {
            super(override.pos(), override.sym.name, override.sym, override.getBindStatus(), override.getInitializer() != null, 
                    initExpr, bindOrNull,
                  onReplace, onReplaceAsInline, onInvalidate, onInvalidateAsInline);
            this.boundFuncResultInitSym = boundFuncResultInitSym;
        }
        
        // Return true if the var is an override.
        @Override
        public boolean isOverride() { return true; }

        // Returns the var information the override overshadows.
        @Override
        public VarInfo proxyVar() { return proxyVar; }

        // Setter for the proxy var information.
        public void setProxyVar(VarInfo proxyVar) { this.proxyVar = proxyVar; }
        
        // Returns true is this var overrides a mixin.
        public boolean overridesMixin() {
            return proxyVar != null && proxyVar instanceof MixinClassVarInfo;
        }

        @Override
        public boolean isInitWithBoundFuncResult() {
            return boundFuncResultInitSym != null;
        }

        @Override
        public Symbol boundFuncResultInitSym() {
            return boundFuncResultInitSym;
        }
    }

    //
    // This class represents a var that is declared in a superclass.  This var may be
    // declared in the same compile unit or read in from a .class file.
    //
    static class SuperClassVarInfo extends VarInfo {
        SuperClassVarInfo(DiagnosticPosition diagPos, F3VarSymbol var) {
            super(diagPos, var.name, var, null);
        }

        // Superclass vars are never cloned.
        @Override
        public boolean needsCloning() { return false; }
    }

    //
    // This class represents a var that is declared in a mixin superclass.  This var may be
    // declared in the same compile unit or read in from a .class file.
    //
    static class MixinClassVarInfo extends VarInfo {
        // Accessors for mixin var.
        ListBuffer<FuncInfo> accessors;
        
        // Override from mixee.
        private VarInfo overrideVar;
        
        MixinClassVarInfo(DiagnosticPosition diagPos, F3VarSymbol var) {
            super(diagPos, var.name, var, null);
            this.accessors = ListBuffer.lb();
            this.overrideVar = null;
        }

        // Returns true if the var needs to generate mixin interfaces (getMixin$, setMixin$ and getVOFF$)
        @Override
        public boolean needsMixinInterface() {
            return true;
        }

        // Return true if the var/override has an initializing expression
        @Override
        public boolean hasInitializer() {
            return hasOverrideVar() && overrideVar().hasInitializer();
        }

        // true if this variable is initialized with a "safe" expression -
        // so that we don't need a try..catch error handler wrapper in it's
        // getter method.
        @Override
        public boolean hasSafeInitializer() {
             return hasOverrideVar() && overrideVar().hasSafeInitializer();
        }

        // Return true if the var has a VarInit
        @Override
        public boolean hasVarInit() {
             return hasOverrideVar() && overrideVar().hasVarInit();
        }

        // is this initialzed with a bound function result var?
        @Override
        public boolean isInitWithBoundFuncResult() {
             return hasOverrideVar() && overrideVar().isInitWithBoundFuncResult();
        }

        // Is this variable is initialized by another synthetic variable
        // of Pointer type and that var stores result from a bound function call?
        // If so, return the symbol of the synthetic variable.
        @Override
        public Symbol boundFuncResultInitSym() {
            return hasOverrideVar() ? overrideVar().boundFuncResultInitSym() : super.boundFuncResultInitSym();
        }

        // Returns null or the code for var initialization.
        @Override
        public JCExpression getDefaultInitExpression() {
            if (hasOverrideVar() && overrideVar().initExpr != null) {
                return overrideVar().initExpr;
            }
            
            return initExpr;
        }

        // Return true if the var has a bound definition.
        @Override
        public boolean hasBoundDefinition() {
            return hasOverrideVar() && overrideVar().hasBoundDefinition();
        }

        // Return true if the var has a bidirectional bind.
        @Override
        public boolean hasBiDiBoundDefinition()  {
            return hasOverrideVar() && overrideVar().hasBiDiBoundDefinition();
        }
        
        // Is there a getter expression of bound variable
        @Override
        public boolean hasBoundInit() {
            return hasOverrideVar() ? overrideVar().hasBoundInit() : super.hasBoundInit();
        }

        // Null or Java code for getter expression of bound variable
        @Override
        public JCExpression boundInit() {
            return hasOverrideVar() ? overrideVar().boundInit() : super.boundInit();
        }

        // Empty or Java preface code for getter of bound variable
        @Override
        public List<JCStatement> boundPreface() {
            return hasOverrideVar() ? overrideVar().boundPreface() : super.boundPreface();
        }

        // Empty or Java preface code for setting of bound with inverse variable
        @Override
        public List<JCStatement> boundInvSetterPreface() {
            return hasOverrideVar() ? overrideVar().boundInvSetterPreface() : super.boundInvSetterPreface();
        }

        // Variable symbols on which this variable depends
        @Override
        public List<F3VarSymbol> boundBindees() {
            ListBuffer<F3VarSymbol> bindees = ListBuffer.lb();
            bindees.appendList(super.boundBindees());
            
            if (hasOverrideVar()) {
                bindees.appendList(overrideVar().boundBindees());
            }

            return bindees.toList();
        }

        // Bound variable symbols on which this variable is used.
        @Override
        public HashSet<VarInfo> boundBinders() {
            return hasOverrideVar() ? overrideVar().boundBinders() : super.boundBinders();
        }

        // Bound sequences that need to be invalidated when invalidated.
        @Override
        public List<BindeeInvalidator> boundInvalidatees() {
            return hasOverrideVar() ? overrideVar().boundInvalidatees() : super.boundInvalidatees();
        }

        // Empty or bound select pairs.
        @Override
        public List<DependentPair> boundBoundSelects() {
            return hasOverrideVar() ? overrideVar().boundBoundSelects() : super.boundBoundSelects();
        }

        // Return true if this is a bound sequence represented as virtual sequence
        @Override
        public boolean isBoundVirtualSequence() {
            return hasOverrideVar() ? overrideVar().isBoundVirtualSequence() : super.isBoundVirtualSequence();
        }

        // Null or Java code for getting the element of a bound sequence
        @Override
        public JCStatement boundElementGetter() {
            return hasOverrideVar() ? overrideVar().boundElementGetter() : super.boundElementGetter();
        }

        // Null or Java code for getting the size of a bound sequence
        @Override
        public JCStatement boundSizeGetter() {
            return hasOverrideVar() ? overrideVar().boundSizeGetter() : super.boundSizeGetter();
        }
        
        // Null or Java code for invalidation of a bound sequence
        @Override
        public List<BindeeInvalidator> boundInvalidators() {
            return hasOverrideVar() ? overrideVar().boundInvalidators() : super.boundInvalidators();
        }

        // Possible f3 code for the var's 'on replace'.
        @Override
        public F3OnReplace onReplace() {
            return hasOverrideVar() ? overrideVar().onReplace() : super.onReplace();
        }

        // Possible java code for the var's 'on replace' in setter.
        @Override
        public JCStatement onReplaceAsInline() {
            return hasOverrideVar() ? overrideVar().onReplaceAsInline() : super.onReplaceAsInline();
        }

        // Possible f3 code for the var's 'on invalidate'.
        @Override
        public F3OnReplace onInvalidate() {
            return hasOverrideVar() ? overrideVar().onInvalidate() : super.onInvalidate();
        }

        // Possible java code for the var's 'on invalidate' in var$invalidate method.
        @Override
        public JCStatement onInvalidateAsInline() {
            return hasOverrideVar() ? overrideVar().onInvalidateAsInline() : super.onInvalidateAsInline();
        }

        // Mixin vars are always cloned.
        @Override
        public boolean needsCloning() { return true; }
        
        // Fetch the override.
        @Override
        public VarInfo overrideVar() { return overrideVar; }
        
        // Fetch the override.
        public void setOverride(VarInfo override) { overrideVar = override; }
        
        // Add an accessor function.
        public void addAccessor(FuncInfo accessor) {
            accessors.append(accessor);
        }
        
        // Add an accessor function.
        public List<FuncInfo> getAccessors() {
            return accessors.toList();
        }
    }

    //
    // Set up the analysis.
    //
    F3AnalyzeClass(
            F3InitializationBuilder initBuilder,
            DiagnosticPosition diagPos,
            ClassSymbol currentClassSym,
            List<TranslatedVarInfo> translatedAttrInfo,
            List<TranslatedOverrideClassVarInfo> translatedOverrideAttrInfo,
            List<TranslatedFuncInfo> translatedFuncInfo,
            Name.Table names,
            F3Types types,
            F3Defs defs,
            F3Symtab syms,
            F3ClassReader reader) {
        this.initBuilder = initBuilder;
        this.names = names;
        this.types = types;
        this.defs = defs;
        this.syms = syms;
        this.reader = reader;
        this.diagPos = diagPos;
        this.currentClassDecl = types.getF3Class(currentClassSym);
        this.currentClassSym = currentClassSym;
        this.translatedAttrInfo = translatedAttrInfo;
        this.translatedOverrideAttrInfo = translatedOverrideAttrInfo;
        this.translatedFuncInfo = translatedFuncInfo;
        this.classVarCount = 0;
        this.scriptVarCount = 0;
        
        // Start by analyzing the current class.
        analyzeCurrentClass();

        // Assign var enumeration and binders.
        for (VarInfo ai : classVarInfos) {
            if (ai.needsEnumeration()) {
                ai.setEnumeration(classVarCount++);
		//ai.printInfo(true);
            }
           
            addBinders(ai);
        }
        for (VarInfo ai : scriptVarInfos) {
           if (ai.needsEnumeration()) {
               ai.setEnumeration(scriptVarCount++);
           }
           
           addBinders(ai);
        }

        if (initBuilder.options.get("dumpvarinfo") != null) {
            printAnalysis(false);
        }
    }
    
    private void addInterClassBinder(VarInfo varInfo, F3VarSymbol instanceSymbol, F3VarSymbol referenceSymbol) {
        // Get the correct update map.
        HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap =
            varInfo.isStatic() ? scriptUpdateMap : classUpdateMap;
        
        // Get instance level map.
        HashMap<F3VarSymbol, HashSet<VarInfo>> instanceMap = updateMap.get(instanceSymbol);
        
        // Add new entry if not found.
        if (instanceMap == null) {
            instanceMap = new LinkedHashMap<F3VarSymbol, HashSet<VarInfo>>();
            updateMap.put(instanceSymbol, instanceMap);
        }
        
        // Get reference level map.
        HashSet<VarInfo> referenceSet = instanceMap.get(referenceSymbol);
        
        // Add new entry if not found.
        if (referenceSet == null) {
            referenceSet = new LinkedHashSet<VarInfo>();
            instanceMap.put(referenceSymbol, referenceSet);
        }
        
        // Add symbol to set.
        referenceSet.add(varInfo);
    }
    
    private void addBinders(VarInfo ai) {
        // Add any bindees to binders.
        for (F3VarSymbol bindeeSym : ai.boundBindees()) {
            // Find the varInfo
            VarInfo bindee = visitedAttributes.get(initBuilder.attributeValueName(bindeeSym));
            
            if (bindee != null) {
                bindee.binders.add((VarInfo)ai);
            }
        }
            
        // Add any bind select pairs to update map.
        for (DependentPair pair : ai.boundBoundSelects()) {
            addInterClassBinder(ai, pair.instanceSym, (F3VarSymbol)pair.referencedSym);
        }
        
        // If the ai has invalidators.
        for (BindeeInvalidator invalidator: ai.boundInvalidators()) {
            // Find the varInfo
            VarInfo bindee = visitedAttributes.get(initBuilder.attributeValueName(invalidator.bindee));
            
            if (bindee != null && invalidator.invalidator != null) {
               bindee.boundInvalidatees.append(invalidator);
            }
        }
    }
    
    //
    // This class supers all classes used to hold function information. Consumed by
    // F3InitializationBuilder.
    //
    static abstract class FuncInfo {
        // Position of the function declaration or current f3 class if read from superclass.
        private final DiagnosticPosition diagPos;

        // Function symbol (unique to symbol.)
        private final MethodSymbol funcSym;
        
        FuncInfo(DiagnosticPosition diagPos, MethodSymbol funcSym) {
            this.diagPos = diagPos;
            this.funcSym = funcSym;
        }

        // Return the function position.
        public DiagnosticPosition pos() { return diagPos; }

        // Return the function symbol.
        public MethodSymbol getSymbol() { return funcSym; }

        // Return modifier flags from the symbol.
        public long getFlags() { return funcSym.flags(); }

        // Predicate for static func test.
        public boolean isStatic() { return (getFlags() & Flags.STATIC) != 0; }
        
        // Useful diagnostic tool.
        public void printInfo() {
            System.err.println("    " + getSymbol() +
                               (isStatic() ? ", static" : ""));
        }
    }
    
    //
    // This class is used for basic functions declared in the current f3 class.
    //
    static class TranslatedFuncInfo extends FuncInfo {
        // F3 definition of the function.
        private final F3FunctionDefinition f3FuncDef;
        
        // Java translation of the function.
        private final List<JCTree> jcFuncDef;
        
        TranslatedFuncInfo(F3FunctionDefinition f3FuncDef, List<JCTree> jcFuncDef) {
            super(f3FuncDef, f3FuncDef.sym);
            this.f3FuncDef = f3FuncDef;
            this.jcFuncDef = jcFuncDef;
        }

        // Return the f3 definition of the function.
        public F3FunctionDefinition f3Function() { return f3FuncDef; }

        // Return the java translation of the function.
        public List<JCTree> jcFunction() { return jcFuncDef; }

    }
    
    //
    // This class represents a function that is declared in a superclass. 
    // This function may be declared in the same compile unit or read in from a .class file.
    //
    static class SuperClassFuncInfo extends FuncInfo {
        SuperClassFuncInfo(MethodSymbol funcSym) {
            super(null, funcSym);
        }
    }
    
    //
    // This class represents a function that is declared in a mixin superclass.  This function may be
    // This function may be declared in the same compile unit or read in from a .class file.
    //
    static class MixinFuncInfo extends FuncInfo {
        MixinFuncInfo(MethodSymbol funcSym) {
            super(null, funcSym);
        }
    }

    //
    // Returns the current class position.
    //
    public DiagnosticPosition getCurrentClassPos() { return diagPos; }

    //
    // Returns the current class decl.
    //
    public F3ClassDeclaration getCurrentClassDecl() { return currentClassDecl; }

    //
    // Returns the current class symbol.
    //
    public ClassSymbol getCurrentClassSymbol() { return currentClassSym; }

    //
    // Returns true if specified symbol is the current class symbol.
    //
    public boolean isCurrentClassSymbol(Symbol sym) { return sym == currentClassSym; }

    //
    // Returns true if specified class is the F3Base class.
    //
    public boolean isF3Base(Symbol sym) { return sym == syms.f3_BaseType.tsym; }

    //
    // Returns true if specified class is the F3Object class.
    //
    public boolean isF3Object(Symbol sym) { return sym == syms.f3_ObjectType.tsym; }

    //
    // Returns true if specified class is either the F3Base or the F3Object class.
    //
    public boolean isRootClass(Symbol sym) { return isF3Base(sym) || isF3Object(sym); }

    //
    // Returns true if the current class inherits directly from F3Base.
    //
    public boolean isFirstTier() { return superClassSym !=  null && isF3Base(superClassSym); }

    //
    // Returns true if the current class inherits directly from F3Base and has no mixins.
    //
    public boolean isFirstTierNoMixins() { return isFirstTier() && allMixins.isEmpty(); }

    //
    // Returns the var count for the current class.
    //
    public int getClassVarCount() { return classVarCount; }

    //
    // Returns the var count for the current script.
    //
    public int getScriptVarCount() { return scriptVarCount; }

    //
    // Returns the translatedAttrInfo for the current class.
    //
    public List<TranslatedVarInfo> getTranslatedAttrInfo() { return translatedAttrInfo; }

    //
    // Returns the translatedOverrideAttrInfo for the current class.
    //
    public List<TranslatedOverrideClassVarInfo> getTranslatedOverrideAttrInfo() {
        return translatedOverrideAttrInfo;
    }

    //
    // Returns the translatedFuncInfo for the current class.
    //
    public List<TranslatedFuncInfo> getTranslatedFuncVarInfo() {
        return translatedFuncInfo;
    }

    //
    // Returns the resulting list of class vars.
    //
    public List<VarInfo> classVarInfos() {
        return classVarInfos.toList();
    }

    //
    // Returns the resulting list of script vars.
    //
    public List<VarInfo> scriptVarInfos() {
        return scriptVarInfos.toList();
    }

    //
    // Returns the resulting list of class funcs.
    //
    public List<FuncInfo> classFuncInfos() {
        return classFuncInfos.toList();
    }

    //
    // Returns the resulting list of script func.
    //
    public List<FuncInfo> scriptFuncInfos() {
        return scriptFuncInfos.toList();
    }
    
    //
    // Returns the map used to construct the class update$ method.
    //
    public final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> getClassUpdateMap() {
        return classUpdateMap;
    }
    
    //
    // Returns the map used to construct the script update$ method.
    //
    public final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> getScriptUpdateMap() {
        return scriptUpdateMap;
    }

    //
    // Returns the resulting list of methods needing $impl dispatching.
    //
    public List<MethodSymbol> needDispatch() {
        ListBuffer<MethodSymbol> meths = ListBuffer.lb();
        
        // Never add dispatch methods to mixins.
        if (!isMixinClass()) {
            for (FuncInfo fi : needDispatchMethods.values()) {
                meths.append(fi.getSymbol());
            }
        }

        return meths.toList();
    }

    //
    // Returns true if the type is a valid class worthy of analysis.
    //
    private boolean isValidClass(Type type) {
        return type != null && type.tsym != null && type.tsym.kind == Kinds.TYP;
    }

    //
    // Returns true if the class (or current class) is a mixin.
    //
    public boolean isMixinClass() {
        return isMixinClass(currentClassSym);
    }
    public static boolean isMixinClass(Symbol cSym) {
        return (cSym.flags() & F3Flags.MIXIN) != 0;
    }

    //
    // Returns true if the class (or current class) is FINAL.
    //
    public boolean isFinal() {
        return isFinal(currentClassSym);
    }
    public static boolean isFinal(Symbol cSym) {
        return (cSym.flags() & Flags.FINAL) != 0;
    }

    //
    // Returns true if the class is a Interface.
    //
    public boolean isInterface(Symbol cSym) {
        return (cSym.flags() & Flags.INTERFACE) != 0;
    }

    //
    // Returns null or the superclass symbol if it is a f3 class.
    //
    public ClassSymbol getF3SuperClassSym() { return superClassSym; }

    //
    // Returns resulting list of all superclasses in top down order.
    //
    public List<ClassSymbol> getSuperClasses() { return superClasses.toList(); }

    //
    // Returns resulting list of immediate mixin classes in left to right order.
    //
    public List<ClassSymbol> getImmediateMixins() { return immediateMixins.toList(); }

    //
    // Returns resulting list of all mixin classes in top down order.
    //
    public List<ClassSymbol> getAllMixins() { return allMixins.toList(); }
    
    //
    // Add a var to the proper vars list.
    //
    public void addVarToList(VarInfo varInfo) {
        if (varInfo.isStatic()) {
            scriptVarInfos.append(varInfo);
        } else {
            classVarInfos.append(varInfo);
        }
    }
    
    //
    // Check to see if a mixin has an override.
    //
    public void checkMixinOverride(MixinClassVarInfo varInfo) {
        for (TranslatedOverrideClassVarInfo tai : translatedOverrideAttrInfo) {
            if (tai.getSymbol() == varInfo.getSymbol()) {
                varInfo.setOverride(tai);
                tai.setProxyVar(varInfo);
                break;
            }
        }
    }

    //
    // This method analyzes the current f3 class.
    //
    private void analyzeCurrentClass() {
        // Make sure we don't recursively redo this class.
        addedBaseClasses.add(currentClassSym);

        // Process up the super class chain first.
        Type superType = currentClassSym.getSuperclass();

        // Make sure the super is a valid java class (is this always true?)
        if (isValidClass(superType)) {
            // Analyze the super class, but note that we don't want to clone
            // anything in the super chain.
            analyzeClass(superType.tsym, true, false);
        }

        // Track the current vars to the instance attribute results.
        for (TranslatedVarInfo tai : translatedAttrInfo) {
            // Track the var for overrides and mixin duplication.
            visitedAttributes.put(initBuilder.attributeValueName(tai.getSymbol()), tai);
        }

        // Map the current methods so they are filtered out of the results.
        for (TranslatedFuncInfo func : translatedFuncInfo) {
            visitedMethods.put(methodSignature(func.getSymbol()), func);
        }

        // Lastly, insert any mixin vars and methods from the interfaces.
        for (F3Expression supertype : currentClassDecl.getSupertypes()) {
            // This will technically only analyze mixin classes.
            // We also want to clone all mixin vars amnd methods.
            analyzeClass(supertype.type.tsym, true, true);
        }

        // Track the override vars to the instance attribute results.
        for (TranslatedOverrideClassVarInfo tai : translatedOverrideAttrInfo) {
            // Find the overridden var.
            VarInfo oldVarInfo = visitedAttributes.get(initBuilder.attributeValueName(tai.getSymbol()));

            // Test because it's possible to find the override before the var.
            if (oldVarInfo != null && !(oldVarInfo instanceof MixinClassVarInfo)) {
                // Proxy to the overridden var.
                tai.setProxyVar(oldVarInfo);
                tai.setNeedsMixinInterface(tai.needsMixinInterface() || oldVarInfo.needsMixinInterface());
                oldVarInfo.setNeedsMixinInterface(false);
            }
            
            // Track the var for overrides and mixin duplication.
            visitedAttributes.put(initBuilder.attributeValueName(tai.getSymbol()), tai);
        }

        // Add the current vars to the var results.
        // VSGC-3043 - This needs to be done after mixins.
        for (TranslatedVarInfo tai : translatedAttrInfo) {
            addVarToList(tai);
        }

        // Add the override vars to the var results.
        // VSGC-3043 - This needs to be done after mixins.
        for (TranslatedOverrideClassVarInfo tai : translatedOverrideAttrInfo) {
            if (!tai.overridesMixin()) {
                addVarToList(tai);
            }
        }

        // Add the functions to the function results.
        // VSGC-3043 - This needs to be done after mixins.
        for (TranslatedFuncInfo tfi : translatedFuncInfo) {
            if (tfi.isStatic()) {
                scriptFuncInfos.append(tfi);
            } else {
                classFuncInfos.append(tfi);
            }
        }
    }
    
    private void analyzeClass(Symbol sym, boolean isImmediateSuper, boolean needsCloning) {
        // Ignore pure java interfaces, classes we've visited before and non-f3 classes.
        if (!isInterface(sym) && !addedBaseClasses.contains(sym) && types.isF3Class(sym)) {
            // Get the current class symbol and add it to the visited map.
            ClassSymbol cSym = (ClassSymbol)sym;
            addedBaseClasses.add(cSym);

            // Only continue cloning up the hierarchy if this is a mixin class.
            boolean isMixinClass = isMixinClass(cSym);
            needsCloning = needsCloning && isMixinClass;
            
            // Process up the super class chain first.
            Type superType = cSym.getSuperclass();
            if (isValidClass(superType)) {
                // Analyze the super class, but note that we don't want to clone
                // anything in the super chain.
                analyzeClass(superType.tsym, false, false);
            }
            // Class loaded from .class file.
            if (cSym.members() != null) {
                // Scope information is held in reverse order of declaration.
                ListBuffer<Symbol> reversed = ListBuffer.lb();
                for (Entry e = cSym.members().elems; e != null && e.sym != null; e = e.sibling) {
                    reversed.prepend(e.sym);
                }
                
                // Track mixin var accessors.
                HashMap<Name, MixinClassVarInfo> mixinVarMap = new LinkedHashMap<Name, MixinClassVarInfo>();

                // Scan attribute members.
                for (Symbol varMem : reversed) {
                    if (varMem instanceof F3VarSymbol) {
                        // Attribute member.
                        F3VarSymbol var = (F3VarSymbol)varMem;
                        
                        // Filter out methods generated by the compiler.
                        if (isRootClass(cSym) || !filterVars(var)) {
                            processAttribute(var, cSym, needsCloning, mixinVarMap);
                        }
                    }
                }

                // Scan attribute/method members.
                for (Symbol methMem : reversed) {
                    if (methMem.kind == Kinds.MTH) {
                        // Method member.
                        MethodSymbol meth = (MethodSymbol)methMem;

                        // Workaround for VSGC-3040 - Compile failure building
                        // runtime/f3-ui-controls/f3/scene/control/Button.f3
                        //if (!needsCloning) continue;

                        // Filter out methods generated by the compiler.
                        if (isRootClass(cSym) || !filterMethods(meth)) {
                            processMethod(meth, needsCloning, cSym, mixinVarMap);
                        }
                    }
                }
            }
            
            // Now analyze interfaces.
            for (Type supertype : cSym.getInterfaces()) {
                ClassSymbol iSym = (ClassSymbol) supertype.tsym;
                analyzeClass(iSym, isImmediateSuper && isMixinClass, needsCloning);
            }

            // Record the superclass in top down order.
	    //System.err.println("csym="+cSym);
	    //System.err.println("csym.type="+cSym.type);
	    //Thread.currentThread().dumpStack();
            recordClass(cSym, isImmediateSuper);
        }
    }

    //
    // Predicate method indicates if the method should be include in processing.
    // Should filter out unrelated methods generated by the compiler.
    //
    private boolean filterMethods(MethodSymbol meth) {
        Name name = meth.name;
        
        // if this is a main method in an F3 class then it is synthetic, ignore it
        if (name == defs.main_MethodName) {
            if (meth.type instanceof MethodType) {
                MethodType mt = (MethodType)meth.type;
                List<Type> paramTypes = mt.getParameterTypes();
                if (paramTypes.size() == 1 && paramTypes.head instanceof ArrayType) {
                    Type elemType = ((ArrayType) paramTypes.head).getComponentType();
                    if (elemType.tsym.name == syms.stringType.tsym.name) {
                        return true;
                    }
                }
            }
        }

        // ignore GETMAPxxx methods
        if (name.toString().startsWith(defs.varGetMapString)) {
            return true;
        }
        
        return name == names.init || name == names.clinit ||
               name == defs.internalRunFunctionName || 
               name == defs.applyDefaults_F3ObjectMethodName ||
               name == defs.count_F3ObjectMethodName ||
               name == defs.get_F3ObjectMethodName ||
               name == defs.set_F3ObjectMethodName ||
               name == defs.invalidate_F3ObjectMethodName ||
               name == defs.notifyDependents_F3ObjectMethodName ||
               name == defs.getElement_F3ObjectMethodName ||
               name == defs.size_F3ObjectMethodName ||
               name == defs.update_F3ObjectMethodName ||
               name == defs.complete_F3ObjectMethodName ||
               name == defs.initialize_F3ObjectMethodName ||
               name == defs.userInit_F3ObjectMethodName ||
               name == defs.postInit_F3ObjectMethodName ||
               name == defs.initVars_F3ObjectMethodName ||
               name == defs.invoke_F3ObjectMethodName;
    }

    //
    // Predicate method indicates if the var should be include in processing.
    // Should filter out unrelated methods generated by the compiler.
    //
    private boolean filterVars(F3VarSymbol var) {
        Name name = var.name;
        String nameString = name.toString();
        
        return nameString.startsWith(F3Defs.varMap_F3ObjectFieldPrefix) ||
               nameString.startsWith(F3Defs.count_F3ObjectFieldString) ||
               nameString.startsWith(F3Defs.offset_AttributeFieldPrefix) ||
               nameString.startsWith(F3Defs.flags_AttributeFieldPrefix) ||
               nameString.startsWith(F3Defs.varFlags_F3ObjectFieldPrefix);
    }

    //
    // Record the superclasses and mixins in top down order.
    //
    private void recordClass(ClassSymbol cSym, boolean isImmediateSuper) {
        // Make a distinction between superclasses and mixins.
        if (isMixinClass(cSym)) {
            // Record immediate mixin classes in left to right order.
            if (isImmediateSuper) {
                immediateMixins.append(cSym);
            }

            // Record all mixin classes in top down order.
            allMixins.append(cSym);
        } else {
            // Record the immediate superclass.
            if (isImmediateSuper) {
                superClassSym = cSym;
            }

            // Record all superclasses in top down order.
            superClasses.append(cSym);
        }
    }
    
    //
    // This method strips the var name out of an accessor method.
    //
    private Name getAccessorVarName(Name name) {
        for (Name prefix : defs.accessorPrefixes) {
            if (name.startsWith(prefix)) {
                return name.subName(prefix.length() - 1, name.length());
            }
        }

        return null;
    }

    //
    // This method determines if a method should be added to the list of methods
    // needing dispatch.  This method is only called for inherited methods.
    //
    private void processMethod(MethodSymbol sym, boolean needsCloning, ClassSymbol cSym, HashMap<Name, MixinClassVarInfo> mixinVarMap) {
        long flags = sym.flags();
        
        // Only look at real instance methods.
        if ((flags & (Flags.ABSTRACT | Flags.SYNTHETIC)) == 0) {
            // Generate a name/signature string for uniqueness.
            String nameSig = methodSignature(sym);
            // Look to see if we've seen a method like this before.
            FuncInfo oldMethod = visitedMethods.get(nameSig);
            // See if the current method is a mixin.
            boolean newIsMixin = isMixinClass(sym.owner);
            // See if the previous methods is a mixin.
            boolean oldIsMixin = oldMethod != null && isMixinClass(oldMethod.getSymbol().owner);
            // Create new info.
            FuncInfo newMethod = newIsMixin ? new MixinFuncInfo(sym) :  new SuperClassFuncInfo(sym);

            // Are we are still cloning this far up the hierarchy?
            if (needsCloning && (sym.flags() & Flags.PRIVATE) == 0) {
                // If the method didn't occur before or is a real method overshadowing a prior mixin.
                if ((oldMethod == null || (oldIsMixin && !newIsMixin)) && sym.owner == cSym) {
                      
                      Name varName = getAccessorVarName(sym.name);
                      if (varName != null) {
                          // Associate the accessor with the var.
                          MixinClassVarInfo varInfo = mixinVarMap.get(varName);
                          if (varInfo != null) {
                              varInfo.addAccessor(newMethod);
                          }
                      } else {
                          // Add to the methods needing $impl dispatch.
                          needDispatchMethods.put(nameSig, newMethod);
                      }
                  }
            }

            // Map the fact we've seen this name/signature.
            visitedMethods.put(nameSig, newMethod);
        }
    }

    //
    // This method determines if the var needs to be handled in the current class.
    // This method is only called for inherited attributes.
    //
    private void processAttribute(F3VarSymbol var, ClassSymbol cSym, boolean needsCloning, HashMap<Name, MixinClassVarInfo> mixinVarMap) {
        boolean isStatic = (var.flags() & Flags.STATIC) != 0;

        // If the var is in a class and not a static (ie., an instance attribute.)
        if (var.isMember() && !isStatic) {
            // See if we've seen this var before.
            VarInfo oldVarInfo = visitedAttributes.get(initBuilder.attributeValueName(var));

            // If we've seen this class before, it must be the same symbol and type,
            // otherwise in doesn't conflict.
            if (oldVarInfo != null &&
                (!oldVarInfo.getSymbol().name.equals(var.name) ||
                 !types.isSameType(oldVarInfo.getSymbol().type, var.type))) {
                oldVarInfo = null;
            }

            // Is the var in a mixin class and needs cloning.
            boolean newIsMixin = isMixinClass(var.owner);
            if (newIsMixin && needsCloning) {
                // Only process the mixin var if we've not seen it before.
                if ((oldVarInfo == null || oldVarInfo instanceof TranslatedOverrideClassVarInfo) && (var.flags() & Flags.PRIVATE) == 0) {
                    // Construct a new mixin VarInfo.
                    MixinClassVarInfo newVarInfo = new MixinClassVarInfo(diagPos, var);
                    // Check for overriding var.
                    checkMixinOverride(newVarInfo);
                    
                    // Add var to map.
                    Name varName = initBuilder.attributeValueName(var);
                    mixinVarMap.put(varName, newVarInfo);
                    
                    // Don't add mixin vars to mixin classes.
                    if (!isMixinClass()) {
                        // Add the new mixin VarInfo to the result list.
                        addVarToList(newVarInfo);
                    }
                    
                    // Map the fact we've seen this var.
                    visitedAttributes.put(initBuilder.attributeValueName(var), newVarInfo);
                } else if (oldVarInfo != null) {
                    // Still needs the interface.
                    oldVarInfo.setNeedsMixinInterface(true);
                }
            } else {
                // Construct a new superclass VarInfo.
                SuperClassVarInfo newVarInfo = new SuperClassVarInfo(diagPos, var);
                // Add the new superclass VarInfo to the result list.
                classVarInfos.append(newVarInfo);
                // Map the fact we've seen this var.
                visitedAttributes.put(initBuilder.attributeValueName(var), newVarInfo);
            }
        }
    }

    //
    // This method constructs a name/signature string for the supplied method
    // symbol.
    //
    private String methodSignature(MethodSymbol meth) {
        StringBuilder nameSigBld = new StringBuilder();
        nameSigBld.append(meth.name.toString());
        nameSigBld.append(":");
        nameSigBld.append(meth.getReturnType().tsym.flatName());
        nameSigBld.append(":");
        for (VarSymbol param : meth.getParameters()) {
            nameSigBld.append(param.type.tsym.flatName());
            nameSigBld.append(":");
        }
        return nameSigBld.toString();
    }

    //
    // This method can be used to dump the state of a VarInfo or subclass.
    // The before flag cam be used to dump the VarInfo supplied by the
    // F3InitializationBuilder.
    //
    private void printAnalysis(boolean before) {
        System.err.println("Analyzed : " + currentClassSym);

        if (before) {
            System.err.println("  translatedAttrInfo");
            for (TranslatedVarInfo ai : translatedAttrInfo) ai.printInfo();
            System.err.println("  translatedOverrideAttrInfo");
            for (TranslatedOverrideClassVarInfo ai : translatedOverrideAttrInfo) ai.printInfo();
        }

        System.err.println("  superClass");
        System.err.println("    " + superClassSym);
        System.err.println("  superClasses");
        for (ClassSymbol cs : superClasses) System.err.println("    " + cs);
        System.err.println("  immediate mixins");
        for (ClassSymbol cs : immediateMixins) System.err.println("    " + cs);
         System.err.println("  all mixins");
        for (ClassSymbol cs : allMixins) System.err.println("    " + cs);

        System.err.println("  classVarInfos");
        for (VarInfo ai : classVarInfos) ai.printInfo();
        System.err.println("  scriptVarInfos");
        for (VarInfo ai : scriptVarInfos) ai.printInfo();
        System.err.println("  classFuncInfos");
        for (FuncInfo fi : classFuncInfos) fi.printInfo();
        System.err.println("  scriptFuncInfos");
        for (FuncInfo fi : scriptFuncInfos) fi.printInfo();
        System.err.println("  needDispatchMethods");
        for (MethodSymbol m : needDispatch()) {
            System.err.println("    " + m + ", owner=" + m.owner);
        }
        System.err.println();
        System.err.println();
    }

}
