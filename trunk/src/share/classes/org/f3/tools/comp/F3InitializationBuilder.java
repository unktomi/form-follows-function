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

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Scope.Entry;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3TypeRepresentation;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.comp.F3AnalyzeClass.*;
import org.f3.tools.comp.F3AbstractTranslation.*;
import org.f3.tools.comp.F3AbstractTranslation.ExpressionResult.*;
import static org.f3.tools.comp.F3Defs.*;
import org.f3.tools.tree.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Build the representation(s) of a F3 class.  Includes class initialization, attribute and function proxies.
 * With support for mixins.
 *
 * @author Robert Field
 * @author Lubo Litchev
 * @author Per Bothner
 * @author Zhiqun Chen
 * @author Jim Laskey
 */
public class F3InitializationBuilder extends F3TranslationSupport {
    protected static final Context.Key<F3InitializationBuilder> f3InitializationBuilderKey =
        new Context.Key<F3InitializationBuilder>();

    private final F3ToJava toJava;
    private final F3ClassReader reader;
    private final F3OptimizationStatistics optStat;
    private final DependencyGraphWriter depGraphWriter;
    private final boolean annoBindees;

    public static class LiteralInitVarMap {
        private int count = 1;
        //public Map<F3VarSymbol, Integer> varMap = new LinkedHashMap<F3VarSymbol, Integer>();
        public Map<Name, Integer> varMap = new LinkedHashMap<Name, Integer>();
        public ListBuffer<F3VarSymbol> varList = ListBuffer.lb();

        public int addVar(F3VarSymbol sym) {
            Integer value = varMap.get(sym.name);

            if (value == null) {
		//System.err.println("not found: "+ sym + " of "+ sym.owner+" in "+ varMap);
                //value = new Integer(count++);
		value = new Integer(size());
                varMap.put(sym.name, value);
                varList.append(sym);
            }

            return value.intValue();
        }

        public int size() {
            return varMap.size();
        }
    }

    public static class LiteralInitClassMap {
        public Map<ClassSymbol, LiteralInitVarMap> classMap = new LinkedHashMap<ClassSymbol, LiteralInitVarMap>();

        public LiteralInitVarMap getVarMap(ClassSymbol sym) {
            LiteralInitVarMap map = classMap.get(sym);

            if (map == null) {
                map = new LiteralInitVarMap();
                classMap.put(sym, map);
            }

            return map;
        }

        public int size() {
            return classMap.size();
        }
    }

    public static F3InitializationBuilder instance(Context context) {
        F3InitializationBuilder instance = context.get(f3InitializationBuilderKey);
        if (instance == null)
            instance = new F3InitializationBuilder(context);
        return instance;
    }

    protected F3InitializationBuilder(Context context) {
        super(context);

        context.put(f3InitializationBuilderKey, this);

        toJava = F3ToJava.instance(context);
        reader = (F3ClassReader) F3ClassReader.instance(context);
        optStat = F3OptimizationStatistics.instance(context);
        depGraphWriter = DependencyGraphWriter.instance(context);
        annoBindees = options.get("annobindees") != null;
    }

    /**
     * Hold the result of analyzing the class.
     * */
    static class F3ClassModel {
        final Name interfaceName;
        final List<JCExpression> interfaces;
        final List<JCTree> iDefinitions;
        final List<JCTree> additionalClassMembers;
        final List<JCExpression> additionalImports;
        final Type superType;
        final ClassSymbol superClassSym;
        final List<ClassSymbol> superClasses;
        final List<ClassSymbol> immediateMixins;
        final List<ClassSymbol> allMixins;

        F3ClassModel(
                Name interfaceName,
                List<JCExpression> interfaces,
                List<JCTree> iDefinitions,
                List<JCTree> addedClassMembers,
                List<JCExpression> additionalImports,
                Type superType,
                ClassSymbol superClassSym,
                List<ClassSymbol> superClasses,
                List<ClassSymbol> immediateMixins,
                List<ClassSymbol> allMixins) {
            this.interfaceName = interfaceName;
            this.interfaces = interfaces;
            this.iDefinitions = iDefinitions;
            this.additionalClassMembers = addedClassMembers;
            this.additionalImports = additionalImports;
            this.superType = superType;
            this.superClassSym = superClassSym;
            this.superClasses = superClasses;
            this.immediateMixins = immediateMixins;
            this.allMixins = allMixins;
        }
    }
    
    /**
     * Analyze the class.
     *
     * Determine what methods will be needed to access attributes.
     * Determine what methods will be needed to proxy to the static implementations of functions.
     * Determine what other misc fields and methods will be needed.
     * Create the corresponding interface.
     *
     * Return all this as a F3ClassModel for use in translation.
     * */
   F3ClassModel createF3ClassModel(F3ClassDeclaration cDecl,
           List<TranslatedVarInfo> translatedAttrInfo,
           List<TranslatedOverrideClassVarInfo> translatedOverrideAttrInfo,
           List<TranslatedFuncInfo> translatedFuncInfo,
           LiteralInitClassMap initClassMap,
           ListBuffer<JCStatement> translatedInitBlocks, ListBuffer<JCStatement> translatedPostInitBlocks) {

        DiagnosticPosition diagPos = cDecl.pos();
        Type superType = types.supertype(cDecl.type);
        ClassSymbol outerTypeSym = outerTypeSymbol(cDecl.sym); // null unless inner class with outer reference
        boolean isLibrary = toJava.getAttrEnv().toplevel.isLibrary;
        boolean isRunnable = toJava.getAttrEnv().toplevel.isRunnable;

        F3AnalyzeClass analysis = new F3AnalyzeClass(this, diagPos,
                cDecl.sym, translatedAttrInfo, translatedOverrideAttrInfo, translatedFuncInfo,
                names, types, defs, syms, reader);
                
        List<VarInfo> classVarInfos = analysis.classVarInfos();
        List<VarInfo> scriptVarInfos = analysis.scriptVarInfos();
        List<FuncInfo> classFuncInfos = analysis.classFuncInfos();
        List<FuncInfo> scriptFuncInfos = analysis.scriptFuncInfos();
        
        boolean hasStatics = !scriptVarInfos.isEmpty() || !cDecl.invokeCases(true).isEmpty();
        
        int classVarCount = analysis.getClassVarCount();
        int scriptVarCount = analysis.getScriptVarCount();
        List<MethodSymbol> needDispatch = analysis.needDispatch();
        ClassSymbol f3SuperClassSym = analysis.getF3SuperClassSym();
        List<ClassSymbol> superClasses = analysis.getSuperClasses();
        List<ClassSymbol> immediateMixinClasses = analysis.getImmediateMixins();
        List<ClassSymbol> allMixinClasses = analysis.getAllMixins();
        
        boolean isMixinClass = cDecl.isMixinClass();
        boolean isScriptClass = cDecl.isScriptClass();
        boolean isAnonClass = isAnonClass(analysis.getCurrentClassSymbol());
        boolean needsGetMap = isAnonClass && cDecl.getObjInitSyms() != null;
        boolean hasF3Super = f3SuperClassSym != null;
        
        // Have to populate the var map for anon classes.
        LiteralInitVarMap varMap = null;
        if (needsGetMap) {
            varMap = initClassMap.getVarMap(analysis.getCurrentClassSymbol());
            populateAnonInitVarMap(cDecl, varMap);
        }

        ListBuffer<JCTree> cDefinitions = ListBuffer.lb();  // additional class members needed
        ListBuffer<JCTree> iDefinitions = ListBuffer.lb();

        JavaCodeMaker javaCodeMaker = new JavaCodeMaker(analysis, cDefinitions);
        
        if (!isMixinClass) {
            javaCodeMaker.makeAttributeNumbers(classVarInfos, classVarCount);
            javaCodeMaker.makeAttributeFlags(classVarInfos);
            javaCodeMaker.makeAttributeFields(classVarInfos);
            javaCodeMaker.makeAttributeAccessorMethods(classVarInfos);
            javaCodeMaker.makeVarNumMethods();

            JCStatement initMap = needsGetMap ? javaCodeMaker.makeInitVarMapInit(varMap) : null;

            if (outerTypeSym == null) {
                javaCodeMaker.makeJavaEntryConstructor();
            } else {
                javaCodeMaker.makeOuterAccessorField(outerTypeSym);
                javaCodeMaker.makeOuterAccessorMethod(outerTypeSym);
            }

            javaCodeMaker.makeFunctionProxyMethods(needDispatch);
            javaCodeMaker.makeF3EntryConstructor(classVarInfos, outerTypeSym);
            javaCodeMaker.makeInitMethod(defs.userInit_F3ObjectMethodName, translatedInitBlocks, immediateMixinClasses);
            javaCodeMaker.makeInitMethod(defs.postInit_F3ObjectMethodName, translatedPostInitBlocks, immediateMixinClasses);
            javaCodeMaker.gatherFunctions(classFuncInfos);

            if (isScriptClass) {
                javaCodeMaker.makeInitClassMaps(initClassMap);
                javaCodeMaker.gatherFunctions(scriptFuncInfos);

                if  (hasStatics) {
                    ListBuffer<JCTree> sDefinitions = ListBuffer.lb();
                     
                    // script-level into class X
                    javaCodeMaker.makeAttributeFields(scriptVarInfos);
                    javaCodeMaker.makeAttributeAccessorMethods(scriptVarInfos);
    
                    // script-level into class X.X$Script
                    javaCodeMaker.setContext(true, sDefinitions);
                    javaCodeMaker.makeAttributeNumbers(scriptVarInfos, scriptVarCount);
                    javaCodeMaker.makeAttributeFlags(scriptVarInfos);
                    javaCodeMaker.makeVarNumMethods();
                    javaCodeMaker.makeF3EntryConstructor(scriptVarInfos, null);
                    javaCodeMaker.makeScriptLevelAccess(cDecl.sym, true);
                    javaCodeMaker.setContext(false, cDefinitions);
    
                    // script-level into class X
                    javaCodeMaker.makeScriptLevelAccess(cDecl.sym, false);
                    javaCodeMaker.makeInitStaticAttributesBlock(cDecl.sym, true, isLibrary, scriptVarInfos, initMap);
                    javaCodeMaker.makeScript(cDecl.typeArgTypes, sDefinitions.toList());
                }
            } else {
                javaCodeMaker.makeInitStaticAttributesBlock(cDecl.sym, false, false, null, initMap);
            }

            if (!hasF3Super) {
                // Has a non-F3 super, so we can't use F3Base, therefore we need
                // to clone the necessary vars and methods.
                // This code must be after all methods have been added to cDefinitions,

                // A set of methods to exclude from cloning.
                HashSet<String> excludes = new HashSet<String>();

                // Exclude any methods generated by the init builder.
                for (JCTree member : cDefinitions) {
                    if (member.getTag() == JCTree.METHODDEF) {
                        JCMethodDecl jcmeth = (JCMethodDecl)member;
                        excludes.add(jcMethodDeclStr(jcmeth));
                     }
                }

                // Clone what is needed from F3Base/F3Object.
                javaCodeMaker.cloneF3Base(excludes);
            }

        } else {
            // Mixin class
            javaCodeMaker.makeAttributeFlags(classVarInfos);
            javaCodeMaker.makeAttributeFields(classVarInfos);
            javaCodeMaker.makeAttributeAccessorMethods(classVarInfos);
            javaCodeMaker.makeVarNumMethods();

            if (isScriptClass) {
                javaCodeMaker.makeInitClassMaps(initClassMap);
                javaCodeMaker.gatherFunctions(scriptFuncInfos);

                if  (hasStatics) {
                    ListBuffer<JCTree> sDefinitions = ListBuffer.lb();
                     
                    // script-level into class X
                    javaCodeMaker.makeAttributeFields(scriptVarInfos);
                    javaCodeMaker.setContext(true, cDefinitions);
                    javaCodeMaker.makeAttributeAccessorMethods(scriptVarInfos);
                    javaCodeMaker.setContext(false, cDefinitions);
    
                    // script-level into class X.X$Script
                    javaCodeMaker.setContext(true, sDefinitions);
                    javaCodeMaker.makeAttributeNumbers(scriptVarInfos, scriptVarCount);
                    javaCodeMaker.makeAttributeFlags(scriptVarInfos);
                    javaCodeMaker.makeVarNumMethods();
                    javaCodeMaker.makeF3EntryConstructor(scriptVarInfos, null);
                    javaCodeMaker.makeScriptLevelAccess(cDecl.sym, true);
                    javaCodeMaker.setContext(false, cDefinitions);
    
                    // script-level into class X
                    javaCodeMaker.makeScriptLevelAccess(cDecl.sym, false);
                    javaCodeMaker.makeInitStaticAttributesBlock(cDecl.sym, true, isLibrary, scriptVarInfos, null);
                    javaCodeMaker.makeScript(cDecl.typeArgTypes, sDefinitions.toList());
                }
            } else {
                javaCodeMaker.makeInitStaticAttributesBlock(cDecl.sym, false, false, null, null);
            }

            javaCodeMaker.makeInitMethod(defs.userInit_F3ObjectMethodName, translatedInitBlocks, immediateMixinClasses);
            javaCodeMaker.makeInitMethod(defs.postInit_F3ObjectMethodName, translatedPostInitBlocks, immediateMixinClasses);
            javaCodeMaker.gatherFunctions(classFuncInfos);
            
            javaCodeMaker.setContext(false, iDefinitions);
            javaCodeMaker.makeMemberVariableAccessorInterfaceMethods(classVarInfos);
            javaCodeMaker.makeMixinDCNT$(analysis.getCurrentClassSymbol(), false);
            javaCodeMaker.makeMixinFCNT$(analysis.getCurrentClassSymbol(), false);
            javaCodeMaker.makeFunctionInterfaceMethods();
            javaCodeMaker.makeOuterAccessorInterfaceMembers();
            javaCodeMaker.setContext(false, cDefinitions);
        }

        Name interfaceName = isMixinClass ? interfaceName(cDecl) : null;

        return new F3ClassModel(
                interfaceName,
                makeImplementingInterfaces(diagPos, cDecl, immediateMixinClasses, javaCodeMaker),
                iDefinitions.toList(),
                cDefinitions.toList(),
                makeAdditionalImports(diagPos, cDecl, immediateMixinClasses),
                superType,
                f3SuperClassSym,
                superClasses,
                immediateMixinClasses,
                allMixinClasses);
    }

    //
    // Build a string that can be compared against MethodSymbol.toString()
    //
    private static String jcMethodDeclStr(JCMethodDecl meth) {
        String str = meth.name.toString() + "(";
        boolean needsComma = false;
        boolean varArgs = (meth.mods.flags & Flags.VARARGS) != 0;
            
        for (JCVariableDecl varDecl : meth.getParameters()) {
            if (needsComma) str += ",";
            str += varDecl.vartype.toString();
            needsComma = true;
        }
        if (varArgs && str.endsWith("[]")) {
            str = str.substring(0, str.length() - 2) + "...";
        }
        
        str += ")";
        return str;
    }

    private List<JCExpression> makeImplementingInterfaces(DiagnosticPosition diagPos,
							  F3ClassDeclaration cDecl,
							  List<ClassSymbol> baseInterfaces,
							  JavaCodeMaker javaCodeMaker) {
        ListBuffer<JCExpression> implementing = ListBuffer.lb();
            
        if (cDecl.isMixinClass()) {
            implementing.append(makeIdentifier(diagPos, cObject));
            implementing.append(makeIdentifier(diagPos, cMixin));
        } else {
            implementing.append(makeIdentifier(diagPos, cObject));
        }

        for (F3Expression intf : cDecl.getImplementing()) {
            implementing.append(makeType(diagPos, intf.type, false));
        }

        for (ClassSymbol baseClass : baseInterfaces) {
            implementing.append(makeType(diagPos, javaCodeMaker.analysis.getType(baseClass), true));
        }

        return implementing.toList();
    }

    private List<JCExpression> makeAdditionalImports(DiagnosticPosition diagPos, F3ClassDeclaration cDecl, List<ClassSymbol> baseInterfaces) {
        // Add import statements for all the base classes and basClass $Mixin(s).
        // There might be references to them when the methods/attributes are rolled up.
        ListBuffer<JCExpression> additionalImports = new ListBuffer<JCExpression>();
        for (ClassSymbol baseClass : baseInterfaces) {
            if (baseClass.type != null && baseClass.type.tsym != null &&
                    baseClass.type.tsym.packge() != cDecl.sym.packge() &&     // Work around javac bug (CR 6695838)
                    baseClass.type.tsym.packge() != syms.unnamedPackage) {    // Work around javac bug. the visitImport of Attr
                // is casting to JCFieldAcces, but if you have imported an
                // JCIdent only a ClassCastException is thrown.
                additionalImports.append(makeType( diagPos,baseClass.type, false));
                additionalImports.append(makeType( diagPos,baseClass.type, true));
            }
        }
        return additionalImports.toList();
    }

    // Add the methods and field for accessing the outer members. Also add a constructor with an extra parameter
    // to handle the instantiation of the classes that access outer members
    @SuppressWarnings("element-type-mismatch")
    private ClassSymbol outerTypeSymbol(Symbol csym) {
        if (csym != null && toJava.getHasOuters().containsKey(csym)) {
            Symbol typeOwner = csym.owner;
            while (typeOwner != null && typeOwner.kind != Kinds.TYP) {
                typeOwner = typeOwner.owner;
            }

            if (typeOwner != null) {
                // Only return an interface class if it's a mixin.
                return (ClassSymbol)typeOwner.type.tsym;
            }
        }
        return null;
    }

    // Add the vars referenced in the object literal init.
    private void populateAnonInitVarMap(F3ClassDeclaration cDecl, LiteralInitVarMap varMap) {
        List<F3VarSymbol> objInitSyms = cDecl.getObjInitSyms();
        
        for (F3VarSymbol varSym : objInitSyms) {
            varMap.addVar(varSym);
        }
    }

    protected String getSyntheticPrefix() {
        return "if3$";
    }

    //-----------------------------------------------------------------------------------------------------------------------------
    //
    // This class is used to simplify the construction of java code in the
    // initialization builder.
    //
    class JavaCodeMaker extends JavaTreeBuilder {
        // The current class analysis/
        final F3AnalyzeClass analysis;
        private ListBuffer<JCTree> definitions;
        private Name scriptName;
        private ClassSymbol scriptClassSymbol;
        private final boolean isBoundFuncClass;
        
        // Accessor body types.
        static final int BODY_NONE = 0;
        static final int BODY_NORMAL = 1;
        static final int BODY_MIXIN = 2;

        JavaCodeMaker(F3AnalyzeClass analysis, ListBuffer<JCTree> definitions) {
            super(null, analysis.getCurrentClassDecl(), false);
            this.analysis = analysis;
            this.definitions = definitions;
            this.scriptClassSymbol = f3make.ScriptSymbol(getCurrentClassSymbol());
            this.scriptName = this.scriptClassSymbol.name;
            this.isBoundFuncClass = (getCurrentOwner().flags() & F3Flags.F3_BOUND_FUNCTION_CLASS) != 0L;
        }
        
        //
        // Method for changing the current definition list.
        //
        public void setContext(boolean isScript, ListBuffer<JCTree> definitions) {
            setIsScript(isScript);
            this.definitions = definitions;
        }
        
        //
        // This method returns the current owner symbol.
        //
        ClassSymbol getCurrentOwner() {
            return isScript() ? scriptClassSymbol : analysis.getCurrentClassSymbol();
        }
        
        //
        // Methods for adding a new definitions.
        //
        private void addDefinition(JCTree member) {
            if (member != null) {
                definitions.append(member);
            }
        }
        private void addDefinitions(List<JCTree> members) {
            if (members != null) {
                definitions.appendList(members);
            }
        }

        //
        // Methods for managing the current diagnostic position.
        //
        private void setDiagPos(VarInfo ai) { setDiagPos(ai.pos()); }
        private void resetDiagPos() { setDiagPos(analysis.getCurrentClassPos()); }

        //
        // Returns the current class symbol.
        //
        public ClassSymbol getCurrentClassSymbol() {
            return analysis.getCurrentClassSymbol();
        }

        //
        // Argument ids
        //
        JCIdent varNumArg() {
            return makeMethodArg(defs.varNum_ArgName, syms.intType);
        }
        
        JCIdent depNumArg() {
            return makeMethodArg(defs.depNum_ArgName, syms.intType);
        }

        JCIdent updateInstanceArg() {
            return makeMethodArg(defs.updateInstance_ArgName, syms.f3_ObjectType);
        }

        JCIdent objArg() {
            return makeMethodArg(defs.obj_ArgName, syms.objectType);
        }

        JCIdent numberArg() {
            return makeMethodArg(defs.number_ArgName, syms.intType);
        }

        JCIdent argsArg() {
            return makeMethodArg(defs.args_ArgName, syms.f3_ObjectArray);
        }

        JCIdent argsFixedArg(int argNum) {
            Name argName = argNum == 0 ? defs.arg1_ArgName : defs.arg2_ArgName;
            return makeMethodArg(argName, syms.objectType);
        }

        JCIdent clearBitsArg() {
            return makeMethodArg(defs.clearBits_ArgName, syms.intType);
        }

        JCIdent setBitsArg() {
            return makeMethodArg(defs.setBits_ArgName, syms.intType);
        }

        //
        // Returns true if the sym is the current class symbol.
        //
        public boolean isCurrentClassSymbol(Symbol sym) {
            return analysis.isCurrentClassSymbol(sym);
        }
        
        //
        // Return raw flags for current class.
        //
        public long rawFlags() {
            return (isMixinClass() && !isScript()) ? (Flags.STATIC | Flags.PUBLIC) : Flags.PUBLIC;
        }
        
        //
        // Create a method symbol.
        //
        public MethodSymbol makeMethodSymbol(long flags, Type returnType, Name methodName, List<Type> argTypes) {
            return makeMethodSymbol(flags, returnType, methodName, getCurrentOwner(), argTypes);
        }

        //
        // Create a var symbol.
        //
        public F3VarSymbol makeVarSymbol(long flags, Type type, Name varName) {
            return new F3VarSymbol(types, names, flags, varName, type, getCurrentOwner());
        }
        
        //
        // This method creates a member field field.
        //
        private JCVariableDecl makeField(long flags, Type varType, Name name, JCExpression varInit) {

            F3VarSymbol varSym = makeVarSymbol(flags, varType, name);
            return Var(flags, varType, name, varInit, varSym);
        }

        //
        //
        // This method generates a simple java integer field then adds to the buffer.
        //
        private JCVariableDecl addSimpleIntVariable(long flags, Name name, int value) {
            return makeField(flags, syms.intType, name, Int(value));
        }

        //
        // This method generates a java field for a varInfo.
        //
        private JCVariableDecl makeVariableField(VarInfo varInfo, JCModifiers mods, Type varType, Name name, JCExpression varInit) {
            // Get the var symbol.
            F3VarSymbol varSym = varInfo.getSymbol();
            // Construct the variable itself.
	    //System.err.println("make field: "+ name+": "+varType+": "+makeType(varType));
            JCVariableDecl var = Var(mods, makeType(varType), name, varInit, varSym);
            // Update the statistics.
            optStat.recordClassVar(varSym);
            optStat.recordConcreteField();

            return var;
        }
        
        //
        // Determine if a var can be initialized simply.
        //
        public boolean useSimpleInit(VarInfo varInfo) {
            if (!varInfo.useAccessors() && varInfo instanceof TranslatedVarInfo) {
                F3Var var = ((TranslatedVarInfo)varInfo).f3Var();
                return var.isLiteralInit();
            }
            
            return false;
        }
        
        //
        // Generate a simple init expression for the var.
        //
        public JCExpression getSimpleInit(VarInfo varInfo) {
            if (useSimpleInit(varInfo)) {
                F3Var var = ((TranslatedVarInfo)varInfo).f3Var();
                if (var.getInitializer().type.tag == TypeTags.BOT) {
                    return DefaultValue(var.type);
                }
                else {
                    return toJava.translateToExpression(var.getInitializer(), varInfo.getRealType());
                }
            }
            
            return null;
        }
        
        //
        // Generate a string containing bindee information used in the bindees annotation.
        //
        private String makeAnnoBindeesString(VarInfo varInfo) {
            String annoBindeesString = "";
            
            if (annoBindees) {
                 Set<String> bindeesSet = new HashSet<String>();
                 
                 for (VarSymbol sym : varInfo.boundBindees()) {
                    bindeesSet.add(attributeValueName(sym).toString());
                 }
                 
                 for (DependentPair pair : varInfo.boundBoundSelects()) {
                    bindeesSet.add(attributeValueName(pair.instanceSym) + "." + attributeValueName(pair.referencedSym));
                 }
                 
                 for (String bindee : bindeesSet) {
                     if (annoBindeesString.length() != 0) annoBindeesString += ",";
                    annoBindeesString += bindee;
                 }
           }
            
            return annoBindeesString;
        }
        

        //
        // Build the location and value field for each attribute.
        //
        public void makeAttributeFields(List<? extends VarInfo> attrInfos) {
            for (VarInfo ai : attrInfos) {
                // Only process attributes declared in this class (includes mixins.)
                if (ai.needsCloning() && !ai.isOverride()) {
                    // Set the current diagnostic position.
                    setDiagPos(ai);
                    // Grab the variable symbol.
                    F3VarSymbol varSym = ai.getSymbol();
                    long flags = attributeFieldAccessFlags(ai);
                    if (ai.isStatic()) flags |= Flags.STATIC;
                    if (ai.isDefault()) flags |= F3Flags.DEFAULT;
                    JCModifiers mods = m().Modifiers(flags);

                    // Apply annotations, if current class then add source annotations.
                    List<JCAnnotation> annotations;
                    JCAnnotation annoSource = m().Annotation(
                                makeIdentifier(diagPos, F3Symtab.sourceNameAnnotationClassNameString),
                                List.<JCExpression>of(String(varSym.name.toString())));
                    String annoBindeesString = makeAnnoBindeesString(ai);

                    if (annoBindeesString.length() != 0) {
                        JCAnnotation annoBindees = m().Annotation(
                                    makeIdentifier(diagPos, F3Symtab.bindeesAnnotationClassNameString),
                                    List.<JCExpression>of(String(annoBindeesString)));
                        annotations = List.<JCAnnotation>of(annoSource, annoBindees);
                    } else {
                        annotations = List.<JCAnnotation>of(annoSource);
                    }
                    if (! isCurrentClassSymbol(varSym.owner))
                        annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.inheritedAnnotationClassNameString), List.<JCExpression>nil()));
                    mods = addAccessAnnotationModifiers(null, varSym.flags(), mods, annotations);

                    // Construct the value field
                    boolean simple = useSimpleInit(ai);
                    JCExpression init = simple                         ? getSimpleInit(ai) :
                                        isValueType(ai.getRealType())  ? defaultValue(ai) :
                                                                         null;

                    // We only want a breakpoint for simple initializers - if
                    // we're just setting the default-value we're presumably
                    // doing the real initialization else - i.e. applyDefaulst$.
                    // Furthermore, setting a breakpoint in <clinit> can
                    // cause problems when loading the class, and it has
                    // marginal usefulness.
                    setDiagPos(simple && ! ai.isStatic() ? ai.pos() : null);

                    addDefinition(makeVariableField(ai, mods, ai.getRealType(), attributeValueName(varSym), init));
                }
            }
        }
      
        //
        // Return a receiver$, scriptLevelAccess$() or null depending on the context.
        //
        private JCExpression getReceiver(VarInfo varInfo) {
            return getReceiver(varInfo.getSymbol());
        }
        private JCExpression getReceiverOrThis(VarInfo varInfo) {
            return getReceiverOrThis(varInfo.getSymbol());
        }
        
        //
        // This method gathers all the translated functions in funcInfos.
        //
        public void gatherFunctions(List<FuncInfo> funcInfos) {
            for (FuncInfo func : funcInfos) {
                if (func instanceof TranslatedFuncInfo) {
                    addDefinitions(((TranslatedFuncInfo)func).jcFunction());
                }
            }
        }

/* In an ideal world this is what we would like.        
        //
        // Returns access flags appropriate for an attribute's field.
        //
        private long attributeFieldAccessFlags(VarInfo varInfo) {
            long flags = Flags.PUBLIC;
            
            if (!varInfo.isMixinVar()) {
                flags = varInfo.isPublicAccess()          ? Flags.PUBLIC :     // User specified
                        varInfo.isProtectedAccess()       ? Flags.PROTECTED :  // User specified
                        varInfo.isStatic()                ? Flags.PUBLIC :     // Can't change access in subclass
                        varInfo.hasScriptOnlyAccess() &&
                           !varInfo.isExternallySeen()    ? Flags.PRIVATE :    // Internal var
                        varInfo.useAccessors()            ? Flags.PROTECTED :  // Subclasses need access for overrides
                        varInfo.isExternallySeen()        ? 0 :                // Package private
                                                            Flags.PRIVATE;
            }
            
            return flags;
        }
        
        //
        // Returns access flags appropriate for an attribute's method.
        //
        private long attributeMethodAccessFlags(VarInfo varInfo) {
            long flags = Flags.PUBLIC;
            
            // TODO - Handle public read/init properly.
            if (!varInfo.isMixinVar()) {
                flags = varInfo.isPublicAccess() ||
                          varInfo.isPublicReadAccess() ||
                          varInfo.isPublicInitAccess()    ? Flags.PUBLIC :     // User specified
                        varInfo.isProtectedAccess()       ? Flags.PROTECTED :  // User specified
                        varInfo.isStatic()                ? Flags.PUBLIC :     // Can't change access in subclass
                        varInfo.hasScriptOnlyAccess() &&
                           !varInfo.isExternallySeen()    ? Flags.PRIVATE :    // Internal vars
                        varInfo.useAccessors()            ? Flags.PUBLIC :     // Generally visible
                                                            0;                 // Package private
            }
            
            return flags;
        }
however this is what we need */ 
        //
        // Returns access flags appropriate for an attribute's field.
        //
        private long attributeFieldAccessFlags(VarInfo varInfo) {
            long flags = Flags.PUBLIC;
            
            if (!varInfo.isMixinVar()) {
                flags = varInfo.isPublicAccess()          ? Flags.PUBLIC :     // User specified
                        varInfo.isProtectedAccess()       ? Flags.PUBLIC :     // User specified
                        varInfo.isStatic()                ? Flags.PUBLIC :     // Can't change access in subclass
                        varInfo.hasScriptOnlyAccess() &&
                           !varInfo.isExternallySeen()    ? Flags.PRIVATE :    // Internal var
                        varInfo.useAccessors()            ? Flags.PUBLIC :     // Subclasses need access for overrides
                        varInfo.isExternallySeen()        ? Flags.PUBLIC :     // Package private
                                                            Flags.PRIVATE;
            }
            
            return flags;
        }
        
        //
        // Returns access flags appropriate for an attribute's method.
        //
        private long attributeMethodAccessFlags(VarInfo varInfo) {
            long flags = Flags.PUBLIC;
            
            // TODO - Handle public read/init properly.
            if (!varInfo.isMixinVar()) {
                flags = varInfo.isPublicAccess() ||
                          varInfo.isPublicReadAccess() ||
                          varInfo.isPublicInitAccess()    ? Flags.PUBLIC :     // User specified
                        varInfo.isProtectedAccess()       ? Flags.PUBLIC :     // User specified
                        varInfo.isStatic()                ? Flags.PUBLIC :     // Can't change access in subclass
                        varInfo.hasScriptOnlyAccess() &&
                           !varInfo.isExternallySeen()    ? Flags.PRIVATE :    // Internal vars
                        varInfo.useAccessors()            ? Flags.PUBLIC :     // Generally visible
                                                            Flags.PUBLIC;      // Package private
            }
            
            return flags;
        }

        // This method generates code for setting a non-sequence var.
        public List<JCStatement> makeSetAttributeCode(VarInfo varInfo, Name newValueName) {
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            F3VarSymbol varSym = varInfo.getSymbol();
            F3VarSymbol proxyVarSym = varInfo.proxyVarSym();
            Type type = varInfo.getRealType();
            
            boolean needsInvalidate = needInvalidateAccessorMethod(varInfo);
            boolean needsOnReplace = needOnReplaceAccessorMethod(varInfo);
            
            // Set read only bit (trapdoor.)
            if (varInfo.isReadOnly()) {
                 stmts.append(FlagChangeStmt(proxyVarSym, null, defs.varFlagIS_READONLY));
            }
                
            if (needsInvalidate || needsOnReplace) {
                // T varOldValue$ = $var;
                stmts.append(Var(Flags.FINAL, type, defs.varOldValue_LocalVarName, Get(proxyVarSym)));
                
                // short varFlags$ = VFLG$var;
                stmts.append(Var(Flags.FINAL, syms.intType, defs.varFlags_LocalVarName, GetFlags(proxyVarSym)));

                // Set the state valid and mark defaults as applied
                stmts.append(FlagChangeStmt(proxyVarSym, null, defs.varFlagINIT_INITIALIZED_DEFAULT));
                
                ListBuffer<JCStatement> changedStmts = ListBuffer.lb();
                
                if (needsInvalidate) {
                    changedStmts.append(CallBeInvalidate(varSym));
                }
                
                changedStmts.append(SetStmt(proxyVarSym, id(newValueName)));
                
                if (needsInvalidate) {
                    changedStmts.append(CallBeTrigger(varSym));
                }
                 
                if (needsOnReplace) {
                    changedStmts.append(CallStmt(attributeOnReplaceName(varSym), id(defs.varOldValue_LocalVarName), id(newValueName)));
                }
                
                // varOldValue$ != varNewValue$
                // or !varOldValue$.equals(varNewValue$) test for Object value types
                JCExpression valueChangedTest = isValueType(type) ?
                    NOT(Call(defs.Checks_equals, id(defs.varOldValue_LocalVarName), id(newValueName)))
                  : NE(id(defs.varOldValue_LocalVarName), id(newValueName));
                // Default-Not_applied
                JCExpression defaultAppliedTest = FlagTest(defs.varFlags_LocalVarName, defs.varFlagINITIALIZED_STATE_BIT, null);

                stmts.append(
                    OptIf (OR(valueChangedTest, defaultAppliedTest), Block(changedStmts), null));
            } else {
                // Set the state valid and mark defaults as applied
                stmts.append(FlagChangeStmt(proxyVarSym, null, defs.varFlagINIT_INITIALIZED_DEFAULT));
            
                // var = varNewValue$
                stmts.append(SetStmt(proxyVarSym, id(newValueName)));
            }
            
            if (needsInvalidate) {
                // Set the state valid and mark defaults as applied, but don't cancel an invalidation in progress
                stmts.append(
                    FlagChangeStmt(proxyVarSym, defs.varFlagSTATE_MASK, defs.varFlagSTATE_VALID));
            }
            
            return stmts.toList();
        }

        //
        // This class is designed to reduce the repetitiveness of constructing methods.
        //
        public class MethodBuilder {
            // Name of method to generate.
            protected Name methodName;
            // Method return type.
            protected Type returnType;
            // True if the return type is void.
            protected boolean isVoidReturnType;
            // True if we're to stop the build.
            protected boolean stopBuild = false;
            // True if needs a receiver arg.
            protected boolean needsReceiver = isMixinClass() && !isScript();

            // True if body is required.
            protected int bodyType = BODY_NORMAL;
            // Cached method symbol.
            MethodSymbol methodSymbol = null;
            
            // Grab the super class.
            ClassSymbol superClassSym = analysis.getF3SuperClassSym();
            // Grab the mixin classes.
            public List<ClassSymbol> immediateMixinClasses = analysis.getImmediateMixins();
            
            // Stack of nested statements.
            protected Stack<ListBuffer<JCStatement>> stmtsStack = new Stack<ListBuffer<JCStatement>>();
            // Current set of statements.
            protected ListBuffer<JCStatement> stmts = ListBuffer.lb();
            
            void buildIf(boolean condition) {
                stopBuild = !condition;
            }
            
            // List of parameter types.
            ListBuffer<Type> paramTypes = ListBuffer.lb();
            // List of parameter names.
            ListBuffer<Name> paramNames = ListBuffer.lb();
            
            MethodBuilder(Name methodName, Type returnType) {
                this.methodName = methodName;
                this.returnType = returnType;
                this.isVoidReturnType = returnType == syms.voidType;
            }
            
            // This method saves the current list of statements and starts a new one.
            public void beginBlock() {
                stmtsStack.push(stmts);
                stmts = ListBuffer.lb();
            }
            
            // This method restores the previous list of statements and returns the current
            // list of statements in a block.
            public JCBlock endBlock() {
                return Block(endBlockAsList());
            }
            
            // This method restores the previous list of statements and returns the current
            // list of statements.
            public List<JCStatement> endBlockAsList() {
                assert !stmtsStack.empty() : "MethodBuilder: mismatched blocks";
                List<JCStatement> result = stmts.toList();
                stmts = stmtsStack.pop();
                return result;
            }
            
            // This method restores the previous list of statements and returns the current
            // list buffer of statements.
            public ListBuffer<JCStatement> endBlockAsBuffer() {
                assert !stmtsStack.empty() : "MethodBuilder: mismatched blocks";
                ListBuffer<JCStatement> result = stmts;
                stmts = stmtsStack.pop();
                return result;
            }
            
            // This method adds a new statement to the current lists of statements.
            public void addStmt(JCStatement stmt) {
                if (stmt != null) {
                    stmts.append(stmt);
                }
            }
            
            // This method adds a new statement to the front of the current lists of statements.
            public void prependStmt(JCStatement stmt) {
                if (stmt != null) {
                    stmts.prepend(stmt);
                }
            }
            
            // This method adds several statements to the current lists of statements.
            public void addStmts(List<JCStatement> list) {
                stmts.appendList(list);
            }
            public void addStmts(ListBuffer<JCStatement> list) {
                stmts.appendList(list.toList());
            }
            public void addStmts(JCStatement... stmts) {
                for (JCStatement stmt : stmts)
                    addStmt(stmt); // handle nulls this way
            }
            
            // This method adds a new parameter type and name to the current method.
            public void addParam(Type type, Name name) {
                paramTypes.append(type);
                paramNames.append(name);
            }

            // This method adds a new parameter type and name to the current method.
            public void addParam(JCIdent arg) {
                addParam(arg.type, arg.name);
            }

            // This method returns all the parameters for the current method as a
            // list of JCVariableDecl.
            protected List<JCVariableDecl> paramList() {
                Iterator<Type> typeIter = paramTypes.iterator();
                Iterator<Name> nameIter = paramNames.iterator();
                ListBuffer<JCVariableDecl> params = ListBuffer.lb();
                
                if (needsReceiver) {
                    params.append(ReceiverParam(getCurrentClassDecl()));
                }
     
                while (typeIter.hasNext() && nameIter.hasNext()) {
                    params.append(makeParam(typeIter.next(), nameIter.next()));
                }
                
                return params.toList();
            }
            
            protected JCVariableDecl makeParam(Type varType, Name varName) {
                return Param(varType, varName);
            }

            // This method returns all the parameters for the current method as a
            // list of JCExpression.
            protected List<JCExpression> argList() {
                ListBuffer<JCExpression> args = ListBuffer.lb();
                
                for (Name name : paramNames) {
                    args.append(id(name));
                }
                
                return args.toList();
            }
            
            // This method returns the rawFlags for the method.
            protected long rawFlags() {
                return JavaCodeMaker.this.rawFlags();
            }
            
            // This method generates a method symbol for the current method.
            protected MethodSymbol methodSymbol() {
                if (methodSymbol == null) {
                    ListBuffer<Type> argtypes = ListBuffer.lb();
                    
                    if (needsReceiver) {
                        argtypes.append(getCurrentOwner().type);
                    }
                    
                    for (Type type : paramTypes) {
                        argtypes.append(type);
                    }
                    
                    methodSymbol = makeMethodSymbol(flags().flags, returnType, methodName, argtypes.toList());
                }
                
                return methodSymbol;
            }
            
            // This method generates a call statement to the mixin symbol.
            public JCStatement callMixinStmt(ClassSymbol mixin) {
                List<JCExpression> mixinArgs =  List.<JCExpression>of(getReceiverOrThis()).appendList(argList());
                JCExpression selector = makeType(types.erasure(mixin.type), false);
 
                if (isVoidReturnType) {
                    return CallStmt(selector, methodName, mixinArgs);
                } else {
                   return Return(Call(selector, methodName, mixinArgs));
                }
            }

            // This method generates a call to the mixin symbol.
            public void callMixin(ClassSymbol mixin) {
                addStmt(callMixinStmt(mixin));
            }

            // This method generates all the calls for immediate mixins.
            public void callMixins() {
                for (ClassSymbol mixin : immediateMixinClasses) {
                    callMixin(mixin);
                }
            }
            
            // This method generates the call statement for the super class.
            public JCStatement callSuperStmt() {
                if (superClassSym != null && !isMixinClass()) {
                    List<JCExpression> superArgs = argList();
                    
                    if (isVoidReturnType) {
                        return CallStmt(id(names._super), methodName, superArgs);
                    } else {
                        return Return(Call(id(names._super), methodName, superArgs));
                    }
                 }
                 
                 return null;
            }

            // This method generates the call for the super class.
            public void callSuper() {
                if (superClassSym != null && !isMixinClass()) {
                    addStmt(callSuperStmt());
                }
            }
            
            // Return the method flags.
            public JCModifiers flags() {
                return m().Modifiers(rawFlags());
            }
            
            // Driver method to construct the current method.
            public void build() {
                // Initialize for method.
                initialize();
                
                // Generate the code.
                if (!stopBuild) {
                    switch (bodyType) {
                    case BODY_NONE:
                        break;
                    case BODY_NORMAL:
                        generate();
                        break;
                    case BODY_MIXIN:
                        generateMixin();
                        break;
                    }
                }
                
                // Produce no method if generate indicates stopBuild.
                if (!stopBuild) {
                    // Record method.
                    optStat.recordProxyMethod();
    
                    // Construct method.
                    addDefinition(Method(flags(),
                                             returnType,
                                             methodName,
                                             paramList(),
                                             bodyType != BODY_NONE ? stmts.toList() : null,
                                             methodSymbol()));
                }
            }

            // This method generates the statements for the method.
            public void generate() {
                // Emit method body.
                body();
            }

            // This method generates the statements for a mixin proxy.
            public void generateMixin() {
            }
            
            // This method contains any code to initialize the builder.
            public void initialize() {
            }
            
            // This method generates the body of the method.
            public void body() {
                statements();
            }
            
            // This method generates specialized code for the body.
            public void statements() {
            }
        }
        
        //
        // This class is designed to generate a method whose body is a static
        // utility.
        //
        public class StaticMethodBuilder extends MethodBuilder {
            StaticMethodBuilder(Name methodName, Type returnType) {
                super(methodName, returnType);
            }
            
            // Return the method flags.
            @Override
            public JCModifiers flags() {
                return m().Modifiers(Flags.STATIC | Flags.PUBLIC);
            }
        }
        
        //
        // This class is designed to generate a method whose body is a var
        // accessor.
        //
        public class VarAccessorMethodBuilder extends MethodBuilder {
            // Current var info.
            protected VarInfo varInfo;
            // Symbol used on the method.
            protected F3VarSymbol varSym;
            // Symbol used when accessing the variable.
            protected F3VarSymbol proxyVarSym;
            // Is a sequence type.
            protected boolean isSequence;
            // Real type of the var.
            protected Type type;
            // Element type of the var.
            protected Type elementType;

            
            VarAccessorMethodBuilder(Name methodName, Type returnType, VarInfo varInfo, int bodyType) {
                super(methodName, returnType);
                this.varInfo = varInfo;
                this.bodyType = bodyType;
                this.varSym = varInfo.getSymbol();
                this.proxyVarSym = varInfo.proxyVarSym();
                this.isSequence = varInfo.isSequence();
                this.type = varInfo.getRealType();
                this.elementType = isSequence ? varInfo.getElementType() : null;
                this.needsReceiver = isMixinClass() && bodyType == BODY_NORMAL && !varInfo.isStatic();
            }
            
            // Return the raw Method flags.
            @Override
            public long rawFlags() {
                long flags = attributeMethodAccessFlags(varInfo);
                
                if (bodyType == BODY_NONE) {
                    flags |= Flags.ABSTRACT;
                } else if (isMixinClass() || varInfo.isStatic()) {
                    flags |= Flags.STATIC;
                }
                
                return flags;
            }

            // This method generates the statements for a mixin proxy.
            @Override
            public void generateMixin() {
                callMixin((ClassSymbol)varSym.owner);
            }
        }
        
        //
        // This class is designed to generate a method whose body is switched
        // on var offsets.
        //
        public class VarCaseMethodBuilder extends MethodBuilder {
            // List of attributes to scan.
            protected List<VarInfo> attrInfos;
            // Total count of attributes.
            protected int varCount;
            // True if overrides should be included.
            protected boolean allowOverides;
            // Current attribute.
            protected VarInfo varInfo;
            // Symbol used on the method.
            protected F3VarSymbol varSym;
            // Symbol used when accessing the variable.
            protected F3VarSymbol proxyVarSym;
            // Is a sequence type.
            protected boolean isSequence;
            // Real type of the var.
            protected Type type;
            // Element type of the var.
            protected Type elementType;

            VarCaseMethodBuilder(Name methodName, Type returnType, List<VarInfo> attrInfos, int varCount) {
                super(methodName, returnType);
                this.attrInfos = attrInfos;
                this.varCount = varCount;
                allowOverides = false;
                addParam(varNumArg());
            }
            
            // Specialized body the handles a case per var.
            @Override
            public void body() {
                // Prepare to accumulate cases.
                ListBuffer<JCCase> cases = ListBuffer.lb();
                // Prepare to accumulate ifs.
                JCStatement ifStmt = null;
                
                // Iterate thru each var.
                for (VarInfo ai : attrInfos) {
                    clearDiagPos();
                    // Constrain the var.
                    if (ai.needsCloning() && !ai.isBareSynth()) {
                        // Prepare for the var.
                        this.varInfo = ai;
                        this.varSym = ai.getSymbol();
                        this.proxyVarSym = ai.proxyVarSym();
                        this.isSequence = ai.isSequence();
                        this.type = ai.getRealType();
                        this.elementType = isSequence ? ai.getElementType() : null;
                        
                        // Construct the case.
                        beginBlock();
                        statements();
                        List<JCStatement> caseStmts = endBlockAsList();
                        
                        if (!caseStmts.isEmpty()) {
                            if (isMixinClass()) {
                                // Test to see if it's the correct var.
                                ifStmt = OptIf(EQ(Offset(ai.getSymbol()), varNumArg()), Block(caseStmts), ifStmt);
                            } else if (ai.hasEnumeration()) {
                                // case tag number
                                JCExpression tag = Int(analysis.isFirstTier() ? ai.getEnumeration() :
                                                                               (ai.getEnumeration() - varCount));
				//System.err.println("isFirstTier: "+ analysis.isFirstTier()+": "+methodName+": "+varSym+" => "+ tag);
                                // Add the case, something like:
                                // case i: statement;
                                cases.append(m().Case(tag, caseStmts));
                            } else if (allowOverides && varInfo.isOverride()) {
                                // Test to see if it's the correct var.
                                ifStmt = OptIf(EQ(Offset(ai.getSymbol()), varNumArg()), Block(caseStmts), ifStmt);
                            }
                        }
                    }
                }
                
                // Add statement if there were some cases.
                if (cases.nonEmpty()) { 
                    // Add if as default case.
                    if (ifStmt != null) {
                        cases.append(m().Case(null, List.<JCStatement>of(ifStmt)));
                    }
                
                    // varNum - VCNT$
                    JCExpression tagExpr = analysis.isFirstTier() ? varNumArg() : MINUS(varNumArg(), id(defs.count_F3ObjectFieldName));
		    //System.err.println("tagExpr="+tagExpr);
                    // Construct and add: switch(varNum - VCNT$) { ... }
                    addStmt(m().Switch(tagExpr, cases.toList()));
                } else {
                    // No switch just rest.
                    addStmt(ifStmt);
                }
                
                if (stmts.nonEmpty()) {
                    // Call the super version.
                    callSuper();
                } else {
                    // Control build.
                    buildIf(false);
                }
            }
        }

        //
        // This method returns the default statement for a given var.
        //
        public List<JCStatement> getDefaultInitStatements(VarInfo varInfo) {
            F3VarSymbol varSym = varInfo.getSymbol();
            F3VarSymbol proxyVarSym = varInfo.proxyVarSym();
            boolean hasOnReplace = varInfo.onReplaceAsInline() != null;
            boolean isOverride = varInfo.isOverride();
            boolean isBound = varInfo.hasBoundDefinition();
            ListBuffer<JCStatement> stmts = ListBuffer.lb();

            if (isBound) {
                if (!varInfo.isSynthetic()) {
                    stmts.appendList(Stmts(
                        CallInvalidate(varSym),
                        CallTrigger(varSym),
                        If (NOT(FlagTest(proxyVarSym, BITOR(id(defs.varFlagIS_EAGER), id(defs.varFlagFORWARD_ACCESS)), null)),
                            Block(
                                Stmt(Getter(varSym))
                            )
                        )
                    ));
                }  
            } else {
                JCExpression init = varInfo.getDefaultInitExpression();
                
                if (init != null) {
                    if (proxyVarSym.useAccessors()) {
                        if (proxyVarSym.isSequence()) {
                            stmts.append(CallStmt(defs.Sequences_set, getReceiverOrThis(), Offset(proxyVarSym), init));
                        } else if (varInfo.useSetters()) {
                            stmts.append(SetterStmt(proxyVarSym, init));
                        } else {
                            // Nest to allow duplicate varNewValue.
                            ListBuffer<JCStatement> inlineSetterStmts = ListBuffer.lb();
                            inlineSetterStmts.append(Var(varInfo.getRealType(), defs.varNewValue_ArgName, init));
                            inlineSetterStmts.appendList(makeSetAttributeCode(varInfo, defs.varNewValue_ArgName));
                            stmts.append(Block(inlineSetterStmts));
                        }
                    } else {
                        if (proxyVarSym.isSequence()) {
                            init = Call(defs.Sequences_incrementSharing, init);
                        }                    
                        stmts.append(SetStmt(proxyVarSym, init));
                    }
                } else {
                    if (hasOnReplace && !isOverride) {
                        stmts.append(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED));
                        stmts.append(CallStmt(attributeOnReplaceName(proxyVarSym), Get(proxyVarSym), Get(proxyVarSym)));
                    } else if (!varInfo.useAccessors()) {
                        stmts.append(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED));
                    }

                }
            }

            return stmts.toList();
        }

        //
        // This method returns the default statements for a given sequence var.
        //
        private List<JCStatement> getSeqDefaultInitStatement(VarInfo varInfo) {
            F3VarSymbol varSym = varInfo.getSymbol();
            F3VarSymbol proxyVarSym = varInfo.proxyVarSym();
            boolean hasOnReplace = varInfo.onReplaceAsInline() != null;
            boolean isOverride = varInfo.isOverride();
            boolean isBound = varInfo.hasBoundDefinition();
            ListBuffer<JCStatement> stmts = ListBuffer.lb();

            if (varInfo.getDefaultInitExpression() != null) {
                stmts.appendList(getDefaultInitStatements(varInfo));
            }

            if (isBound) {
                if (hasOnReplace) {
                    stmts.append(CallStmt(attributeSizeName(varSym)));
                } else {
                    stmts.append(
                        If (NOT(FlagTest(proxyVarSym, BITOR(id(defs.varFlagIS_EAGER), id(defs.varFlagFORWARD_ACCESS)), null)),
                            Block(
                                CallStmt(attributeSizeName(varSym))
                            ),
                        /*else*/
                            Block(
                                FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED)
                            )
                        )
                    );
                }
            } else if (varInfo.getDefaultInitExpression() == null) {
                if (hasOnReplace && !isOverride) {
                    stmts.append(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED));
                    stmts.append(CallSeqInvalidate(varSym, Int(0), Int(0), Int(0)));
                    stmts.append(CallSeqTriggerInitial(varSym, Int(0)));
                    if (needOnReplaceAccessorMethod(varInfo)) {
                        stmts.append(
                            // If it didn't get initialized to default along the way, send the on-replace (because it would be blocked)
                            If (FlagTest(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED),
                                CallStmt(attributeOnReplaceName(proxyVarSym), Int(0), Int(0), Int(0))
                            ));
                    }
                } else if (varInfo.useAccessors()) {
                    stmts.append(CallStmt(defs.Sequences_replaceSlice, getReceiverOrThis(), Offset(varSym), Get(varSym), Int(0), Int(0)));
                } else {
                    stmts.append(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_INITIALIZED));
                }
            }

            return stmts.toList();
        }

        //
        // Determine if this override needs an invalidate method
        // Must be in sync with makeInvalidateAccessorMethod
        //
        private boolean needOverrideInvalidateAccessorMethod(VarInfo varInfo) {
            if (varInfo.isMixinVar() ||
                    varInfo.onReplace() != null ||
                    varInfo.onInvalidate() != null ||
                    !varInfo.boundInvalidatees().isEmpty()) {
                // based on makeInvalidateAccessorMethod
                return true;
            } else if (varInfo.hasBoundDefinition()) {
                return false;
            } else {
                if (varInfo instanceof TranslatedVarInfoBase) {
                    return ((TranslatedVarInfoBase) varInfo).boundBinders().size() != 0;
                } else {
                    return false;
                }
            }
        }
        
        //
        // Determine if this var needs an invalidate method
        // Must be in sync with makeInvalidateAccessorMethod
        //
        private boolean needInvalidateAccessorMethod(VarInfo varInfo) {
            return (varInfo.isOverride() && needOverrideInvalidateAccessorMethod(varInfo)) ||
                    varInfo.generateSequenceAccessors() ||
                    !isLeaf(varInfo) ||
                        varInfo.hasDependents() || varInfo.isDependent() ||
                        !varInfo.boundInvalidatees().isEmpty() ||
                        varInfo.isMixinVar() ||
                        (varInfo.isStatic() && !varInfo.getSymbol().hasScriptOnlyAccess()) ||
                        varInfo.onInvalidate() != null;
        }

        //
        // Determine if this var needs to call switchDependence in onReplace
        //
        private boolean needSwitchDependence(VarInfo varInfo) {
            for (VarInfo dependent : varInfo.boundBinders()) {
                for (DependentPair depPair : dependent.boundBoundSelects()) {
                    // static variables and sequences are handled diffently
                    if (depPair.instanceSym != varInfo.sym ||
                        depPair.referencedSym.isStatic() ||
                        types.isSequence(depPair.referencedSym.type)) {
                        continue;
                    }
                    return true;
                }
            }
            return false;
        }
        
        //
        // Determine if this var needs an on replace method.
        //
        private boolean needOnReplaceAccessorMethod(VarInfo varInfo) {
            return needSwitchDependence(varInfo) ||
                   varInfo.onReplace() != null ||
                   varInfo.isMixinVar() ||
                   !(!varInfo.isOverride() && isLeaf(varInfo));
        }
       
        //
        // Returns true if the var can not be overridden.
        //
        private boolean isLeaf(VarInfo varInfo) {
            F3VarSymbol varSym = (F3VarSymbol)varInfo.getSymbol();
            long flags = varSym.flags();
            return isAnonClass() ||
                   varSym.isDef() ||
                   varSym.hasScriptOnlyAccess() && (flags & F3Flags.OVERRIDE) == 0;
        }
        
        //-----------------------------------------------------------------------------------------------------------------------------
        //
        // Sequence var accessors.
        //
        
        //
        // This method constructs the getter method for a sequence attribute.
        //
        private void makeSeqGetterAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeGetterName(varInfo.getSymbol()),
                                                                         varInfo.getRealType(),
                                                                         varInfo, bodyType) {
                @Override
                public void statements() {
                    if (isBoundFuncClass && varInfo.isParameter()) {
                        // Prepare to accumulate body of if.
                        beginBlock();

                        /*
                         * if "foo" is the variable name, then we generate
                         *
                         *     var = (cast)$$boundInstance$foo.get($$boundVarNum$foo);
                         *
                         */
                        JCExpression get$call = Call(
                                id(boundFunctionObjectParamName(varSym.name)),
                                defs.get_F3ObjectMethodName,
                                id(boundFunctionVarNumParamName(varSym.name)));
                        JCExpression castGet = typeCast(varInfo.getRealType(), syms.objectType, get$call);
                        addStmt(SetStmt(varSym, castGet));

                        // Release cycle lock.
                        addStmt(FlagChangeStmt(proxyVarSym, null, defs.varFlagINIT_INITIALIZED_DEFAULT));

                        // Is it invalid?
                        JCExpression condition = FlagTest(proxyVarSym, defs.varFlagIS_BOUND_INVALID, defs.varFlagIS_BOUND_INVALID);
                        
                        // if (invalid) { set$var(init/bound expression); }
                        addStmt(OptIf(condition, endBlock(), null));
                    } else {  
                        // Begin if block.
                        beginBlock();

                        // seq$ = new SequenceRef(<<typeinfo T>>, this, VOFF$seq);
                        List<JCExpression> args = List.<JCExpression>of(
                                TypeInfo(diagPos, elementType),
                                getReceiverOrThis(proxyVarSym),
                                Offset(varSym));
                        JCExpression newExpr = m().NewClass(null, null, makeType(types.erasure(syms.f3_SequenceRefType)), args, null);
                        
                        // If (seq$ == null && isBound) { seq$ = new SequenceRef(<<typeinfo T>>, this, VOFF$seq); }
                        addStmt(
                            OptIf (AND(
                                    EQ(Get(proxyVarSym), defaultValue(varInfo)),
                                    FlagTest(proxyVarSym, defs.varFlagIS_BOUND, defs.varFlagIS_BOUND)),
                                Block(
                                    // Be sure the sequence is initialized before returning the SequenceRef -- call the size accessor to initialize
                                    CallStmt(attributeSizeName(varSym)),
                                    // If the size method didn't set the sequence value, make it a SequenceRef
                                    OptIf( EQ(Get(proxyVarSym), defaultValue(varInfo)),
                                        SetStmt(proxyVarSym, newExpr)
                                    )
                                )
                            ));
                    }
                    
                    // Construct and add: return $var;
                    addStmt(Return(Get(proxyVarSym)));
                }
            };

            vamb.build();
        }
        
        //
        // This method constructs the get element method for a sequence attribute.
        //
        private void makeSeqGetElementAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeGetElementName(varInfo.getSymbol()),
                                                                         varInfo.getElementType(),
                                                                         varInfo, bodyType) {
                @Override
                public void initialize() {
                    addParam(posArg());
                }

                // Construct and add: return $var.get(pos$);
                private JCStatement accessorGet() {
                    F3TypeRepresentation typeRep = types.typeRep(varInfo.getElementType());
                    Name getMethodName = defs.typedGet_SequenceMethodName[typeRep.ordinal()];
                    return Return(Call(Get(proxyVarSym), getMethodName, posArg()));
                }
                
                @Override
                public void statements() {
                    if (varInfo.hasBoundDefinition()) {
                        if (isBoundFuncClass && varInfo.isParameter()) {
                            JCExpression apply = Call(
                                    id(boundFunctionObjectParamName(varSym.name)),
                                    defs.getElement_F3ObjectMethodName,
                                    id(boundFunctionVarNumParamName(varSym.name)),
                                    posArg());
                            addStmt(Return(castFromObject(apply, varInfo.getElementType())));
                        } else if (varInfo.isInitWithBoundFuncResult()) {
                            /**
                             * If this var "foo" is initialized with bound function result var, then
                             * we want to get element from the Pointer. We translate as:
                             *
                             *    public static int elem$foo(final int pos$) {
                             *        final Pointer if3$0tmp = get$$$bound$result$foo();
                             *        return if3$0tmp != null ? (Integer)if3$0tmp.get(pos$) : 0;
                             *    }
                             */
                            JCVariableDecl tmpPtrVar = TmpVar("tmp", syms.f3_PointerTypeErasure, Getter(varInfo.boundFuncResultInitSym()));
                            addStmt(tmpPtrVar);

                            JCExpression ptrNonNullCond = NEnull(id(tmpPtrVar));
                            JCExpression apply = Call(
                                    id(tmpPtrVar),
                                    defs.get_PointerMethodName,
                                    posArg());
                            addStmt(Return(If(ptrNonNullCond,
                                        castFromObject(apply, varInfo.getElementType()),
                                        makeDefaultValue(varInfo.pos(), varInfo.getElementType()))));
                        } else if (varInfo.isSynthetic()) {
                            addStmt(varInfo.boundElementGetter());
                        } else {
                            addStmt(
                                If (FlagTest(proxyVarSym, defs.varFlagIS_BOUND, defs.varFlagIS_BOUND),
                                    If (FlagTest(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING),
                                        Block(
                                            FlagChangeStmt(proxyVarSym, null, defs.varFlagFORWARD_ACCESS),
                                            Return (DefaultValue(this.elementType))
                                        ),
                                    /*else (active)*/
                                        varInfo.boundElementGetter()
                                    ),
                                /*else (not bound)*/
                                    accessorGet()
                                )
                            );
                        }
                    } else if (varInfo.useAccessors()) {
                        addStmt(accessorGet());
                    }
                }
            };

            vamb.build();
        }

        
        //
        // This method constructs the getter method for a sequence attribute.
        //
        private void makeSeqGetSizeAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeSizeName(varInfo.getSymbol()),
                                                                         syms.intType,
                                                                         varInfo, bodyType) {
                // Size from sequence
                private JCStatement accessorSize() {
                    return Return(Call(Get(proxyVarSym), defs.size_SequenceMethodName));
                }

                @Override
                public void statements() {
                    if (varInfo.hasBoundDefinition()) {
                        if (isBoundFuncClass && varInfo.isParameter()) {
                            JCExpression apply = Call(
                                    id(boundFunctionObjectParamName(varSym.name)),
                                    defs.size_F3ObjectMethodName,
                                    id(boundFunctionVarNumParamName(varSym.name)));
                            addStmt(Return(apply));
                        } else if (varInfo.isInitWithBoundFuncResult()) {
                            /**
                             * If this var "foo" is initialized with bound function result var, then
                             * we want to get sequence size from the Pointer. We translate as:
                             *
                             *    public static int size$foo() {
                             *        Pointer oldPtr = $$$bound$result$$foo;
                             *        Pointer newPtr = get$$$bound$result$$foo();
                             *        Pointer.switchDependence(oldPtr, newPtr, receiver);
                             *
                             *        <make-it-valid>
                             *        if (newPtr != null) {
                             *            return (Integer)if3$0tmp.size();
                             *        } else {
                             *            return 0;
                             *        }
                             *    }
                             */
                            Name ptrVarName = attributeValueName(varInfo.boundFuncResultInitSym());
                            // declare a temp variable of type Pointer to store old value of Pointer field
                            JCVariableDecl oldPtrVar = TmpVar("old", syms.f3_PointerTypeErasure, id(ptrVarName));
                            addStmt(oldPtrVar);

                            JCVariableDecl newPtrVar = TmpVar("new", syms.f3_PointerTypeErasure, Getter(varInfo.boundFuncResultInitSym()));
                            addStmt(newPtrVar);

                            // Add the receiver of the current Var symbol as dependency to the Pointer, so that
                            // we will get notification whenever the result of the bound function evaluation changes.
                            addStmt(CallStmt(defs.Pointer_switchDependence,
                                             id(oldPtrVar),
                                             id(newPtrVar),
                                             getReceiverOrThis(varSym),
                                             DepNum(getReceiver(varSym), null, varInfo.boundFuncResultInitSym())));

                            JCStatement setValid = FlagChangeStmt(proxyVarSym, defs.varFlagINIT_STATE_MASK, defs.varFlagVALID_DEFAULT_APPLIED);
                            addStmt(setValid);

                            JCExpression apply = Call(
                                    Getter(varInfo.boundFuncResultInitSym()),
                                    defs.size_PointerMethodName);
                            addStmt(OptIf(NEnull(id(newPtrVar)),
                                        Block(setValid, Return(apply)),
                                        Return(Int(0))
                                      )
                                   );
                        } else if (varInfo.isSynthetic()) {
                            addStmt(varInfo.boundSizeGetter());
                        } else {
                            addStmt(
                                If (FlagTest(proxyVarSym, defs.varFlagIS_BOUND, defs.varFlagIS_BOUND),
                                    If (FlagTest(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING),
                                        Block(
                                            FlagChangeStmt(proxyVarSym, null, defs.varFlagFORWARD_ACCESS),
                                            Return (Int(0))
                                        ),
                                    /*else (active)*/
                                        varInfo.boundSizeGetter()
                                    ),
                                /*else (not bound)*/
                                    accessorSize()
                                )
                            );
                        }
                    } else if (varInfo.useAccessors()) {
                        // Construct and add: return $var.size();
                        addStmt(accessorSize());
                    }
                }
            };

            vamb.build();
        }

        //
        // This method constructs the invalidate method for a sequence attribute.
        //
        private void makeSeqInvalidateAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeInvalidateName(varInfo.getSymbol()),
                                                                         syms.voidType,
                                                                         varInfo, bodyType) {
                @Override
                public void initialize() {
                    addParam(startPosArg());
                    addParam(endPosArg());
                    addParam(newLengthArg());
                    addParam(phaseArg());
                }

                @Override
                public void statements() {
                    // Handle invalidators if present.
                    List<BindeeInvalidator> invalidatees = varInfo.boundInvalidatees();
                    boolean hasInvalidators = !invalidatees.isEmpty();

                    beginBlock();

                    if (hasInvalidators) {
                        // Insert invalidators.
                        for (BindeeInvalidator invalidator : invalidatees) {
                            addStmt(invalidator.invalidator);
                        }
                    }

                    boolean override = varInfo.isOverride();
                    boolean mixin = !isMixinClass() && varInfo instanceof MixinClassVarInfo;

                    // Call super.
                    if (override || varInfo instanceof SuperClassVarInfo) {
                        callSuper();
                    } else if (mixin) {
                        // Mixin.invalidate$var(this, phase$);
                        callMixin((ClassSymbol)varSym.owner);
                        override = true;
                    }

                    for (VarInfo otherVar : varInfo.boundBinders()) {
                        if (depGraphWriter != null) {
                            depGraphWriter.writeDependency(otherVar.sym, varSym);
                        }

                        // invalidate$var(phase$);
                        if (!otherVar.generateSequenceAccessors()) {
                            addStmt(CallStmt(attributeInvalidateName(otherVar.getSymbol()), phaseArg()));
                        } else {
                            addStmt(CallStmt(attributeInvalidateName(otherVar.getSymbol()),
                                             startPosArg(),
                                             endPosArg(),
                                             newLengthArg(),
                                             phaseArg()));
                        }
                    }

                    if (!override) {
                        // notifyDependents(VOFF$var, phase$);
                        addStmt(CallStmt(getReceiver(varInfo), defs.notifyDependents_F3ObjectMethodName, Offset(proxyVarSym),
                                startPosArg(), endPosArg(), newLengthArg(),
                                phaseArg()));
                    }

                    if (!override || varInfo.onReplace() != null || varInfo.onInvalidate() != null) {
                        // if (trigger-phase and real-trigger) { call-on-invalidate; call-on-replace; }
                        addStmt(
                            OptIf(
                                AND(AND(
                                    IsTriggerPhase(),
                                    GE(startPosArg(), Int(0))),
                                    FlagTest(proxyVarSym, defs.varFlagINIT_INITIALIZED_DEFAULT, defs.varFlagINIT_INITIALIZED_DEFAULT)
                                ),
                                Block(
                                    (varInfo.onInvalidate() == null)? null :
                                        varInfo.onInvalidateAsInline(),
                                    (override || !varInfo.sym.useTrigger())? null :
                                        CallStmt(attributeOnReplaceName(proxyVarSym),
                                                                startPosArg(),
                                                                endPosArg(),
                                                                newLengthArg())
                                )
                            )
                        );
                    }

                    //TODO: no test needed if non-bound and not overriddable
                    addStmt(
                        OptIf (FlagTest(proxyVarSym, defs.varFlagINIT_INITIALIZED, defs.varFlagINIT_INITIALIZED),
                            endBlock()
                        )
                    );
                }
            };

            vamb.build();
        }

        //
        // This method constructs the onreplace$ method for a sequence attribute.
        //
        private void makeSeqOnReplaceAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeOnReplaceName(varInfo.getSymbol()),
                                                                         syms.voidType,
                                                                         varInfo, bodyType) {
                Name oldValueName;
                Name newValueName;
                Name firstIndexName;
                Name lastIndexName;
                Name newElementsName;
                Name newLengthName;

                @Override
                public void initialize() {
                    F3OnReplace onReplace = varInfo.onReplace();
                    
                    newValueName = defs.varNewValue_ArgName;
                    firstIndexName = paramStartPosName(onReplace);
                    lastIndexName = paramEndPosName(onReplace);
                    newLengthName = paramNewElementsLengthName(onReplace);

                    addParam(syms.intType, firstIndexName);
                    addParam(syms.intType, lastIndexName);
                    addParam(syms.intType, newLengthName);
                    
                    buildIf(varSym.useTrigger());
                }
                
                @Override
                public void statements() {
                    // Call super first.
                    if (varInfo.isOverride()) {
                        callSuper();
                    }

                    // Mixin onreplace$
                    if (!isMixinClass() && varInfo instanceof MixinClassVarInfo) {
                        callMixin((ClassSymbol)varSym.owner);
                    }

                    // Fetch the on replace statement or null.
                    F3OnReplace onReplace = varInfo.onReplace();

                    if (onReplace != null) {
                        F3Var lastIndex = varInfo.onReplace().getLastIndex();
                        F3Var newElements = varInfo.onReplace().getNewElements();
                        if (lastIndex != null && varInfo.onReplace().getEndKind() == F3SequenceSlice.END_INCLUSIVE) {
                            addStmt(Var(syms.intType, lastIndex.name,
                                    MINUS(endPosArg(), Int(1))));
                        }
                        F3VarSymbol savedVarSym = onReplace.getSaveVar() != null ? onReplace.getSaveVar().sym : null;
                        // The idea of the following is to implement:
                        //   var x : T[] = ... on replace oldV[i..j] = newV { something };
                        // as if it were:
                        //   var x$save$ : T[];
                        //   var x : T[] = ... on replace [i..j] = newV {
                        //     def oldV = Sequences.copy(x$save$);
                        //     x$save$[i..j] = newV;
                        //     something
                        //   };

                        if (savedVarSym != null) {
                            // FIXME  Some performance tweaking makes sense:
                            // - If the oldValue is only used for indexing or sizeof, then we
                            // can extract the value of the "gap" of the saved-dalue ArraySequence,
                            // as in the 1.2 compiler.
                            // - The getNewElements call should be combined with the replaceSlice.
                            addStmt(Var(type, onReplace.getOldValue().getName(),
                                    Call(defs.Sequences_incrementSharing, Get(savedVarSym))));                            
                        }
                        if (newElements != null
                                && (newElements.sym.flags() & F3Flags.VARUSE_OPT_TRIGGER) == 0) {
                                   JCExpression seq = Getter(varSym);
                                   JCExpression init = Call(defs.Sequences_getNewElements, seq, id(firstIndexName), id(newLengthName));
                            addStmt(Var(newElements.type, newElements.name, init));
                        }

                        // Insert the trigger.
                        JCStatement triggerBody = varInfo.onReplaceAsInline();
                        if (savedVarSym != null) {
                            JCStatement decr = CallStmt(Get(savedVarSym), names.fromString("decrementSharing"));
                            JCStatement update =
                              SetStmt(savedVarSym,
                                    Call(defs.Sequences_replaceSlice,
                                        Get(savedVarSym),
                                        Call(defs.Sequences_getNewElements, Getter(varSym), id(firstIndexName), id(newLengthName)),
                                        id(firstIndexName),
                                        endPosArg()
                                    ));
                            triggerBody = m().Try(Block(triggerBody), List.<JCCatch>nil(), Block(decr, update));
                        }
                        addStmt(triggerBody);
                    }
                }
            };

            vamb.build();
        }

        //-----------------------------------------------------------------------------------------------------------------------------
        //
        // Normal var accessors.
        //


        //
        // This method constructs the getter method for the specified attribute.
        //
        private void makeGetterAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeGetterName(varInfo.getSymbol()),
                                                                         varInfo.getRealType(),
                                                                         varInfo, bodyType) {

                boolean needsInvalidate = needInvalidateAccessorMethod(varInfo);
                boolean needsOnReplace = needOnReplaceAccessorMethod(varInfo);

                @Override
                public void statements() {
                    clearDiagPos();
                    if (varInfo.isBareSynth()) {
                        // short varFlags$ = VFLG$var;
                        addStmt(Var(Flags.FINAL, syms.intType, defs.varFlags_LocalVarName, GetFlags(proxyVarSym)));
                        
                        // for a bare-synthethic, just return bound-expression
                        JCExpression returnVal = varInfo.boundInit();
                        if (varInfo.isInitWithBoundFuncResult()) {
                            JCVariableDecl newPtrVar = TmpVar("new", syms.f3_PointerTypeErasure, returnVal);
                            addStmt(newPtrVar);

                            returnVal = If(NEnull(id(newPtrVar)),
                                    castFromObject(Call(
                                        id(newPtrVar),
                                        defs.get_PointerMethodName), varSym.type),
                                    defaultValue(varInfo));
                        }
                        addStmt(
                            TryWithErrorHandler(varInfo,
                                varInfo.boundPreface(),
                                Return(returnVal),
                                Return(defaultValue(varInfo))));
                    } else {
                        if (isBoundFuncClass && varInfo.isParameter()) {
                            // Wrapping if (!init pending)
                            beginBlock();
    
                            // Prepare to accumulate body of if.
                            beginBlock();

                            // short varFlags$ = VFLG$var;
                            addStmt(Var(Flags.FINAL, syms.intType, defs.varFlags_LocalVarName, GetFlags(proxyVarSym)));
                            // Lock cycles.
                            addStmt(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING));
                            
                            /*
                             * if "foo" is the variable name, then we generate
                             *
                             *     set$var((cast)$$boundInstance$foo.get($$boundVarNum$foo));
                             *
                             */
                            JCExpression get$call = Call(
                                    id(boundFunctionObjectParamName(varSym.name)),
                                    defs.get_F3ObjectMethodName,
                                    id(boundFunctionVarNumParamName(varSym.name)));
                            JCExpression castGet = typeCast(varInfo.getRealType(), syms.objectType, get$call);
                            // T varNewValue$ = cast value
                            addStmt(Var(Flags.FINAL, type, defs.varNewValue_ArgName, castGet));
                            // Set the var.
                            addStmts(makeBoundGetAttributeCode());
                            
                            // Is it invalid?
                            JCExpression condition = FlagTest(proxyVarSym, defs.varFlagIS_BOUND_INVALID, defs.varFlagIS_BOUND_INVALID);

                            // if (invalid) { set$var(init/bound expression); }
                            addStmt(OptIf(condition, 
                                    endBlock(),
                                    null));

                            // if (!init pending)
                            JCExpression initPendingExpr = NOT(FlagTest(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING));
                            addStmt(OptIf(initPendingExpr,
                                          endBlock(),
                                          null));
                        } else if (varInfo.hasBoundDefinition()) {
                            // Prepare to accumulate body of if.
                            beginBlock();

                            // Set to new value. Bogus assert, it seems an local var can be bound have no init.
                            // assert varInfo.boundInit() != null : "Oops! No boundInit.  varInfo = " + varInfo + ", preface = " + varInfo.boundPreface();

                            // short varFlags$ = VFLG$var;
                            addStmt(Var(Flags.FINAL, syms.intType, defs.varFlags_LocalVarName, GetFlags(proxyVarSym)));
                            // Lock cycles.
                            addStmt(FlagChangeStmt(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING));
                            
                            // set$var(init/bound expression)   
                            JCExpression initValue = varInfo.boundInit();
                            if (initValue == null) {
                                initValue = defaultValue(varInfo);
                            }
                            if (varInfo.isInitWithBoundFuncResult()) {
                                addStmts(varInfo.boundPreface());
                                /**
                                 * For a field named "foo" that is initialized from the bound function
                                 * result Pointer, we generate the following:
                                 *
                                 * Pointer oldPtr = $$$bound$result$$foo;
                                 * Pointer newPtr = get$$$bound$result$$foo();
                                 * Pointer.switchDependence(oldPtr, newPtr, receiver);
                                 *
                                 * if (newPtr != null) {
                                 *      set$foo((ExpectedType)newPtr.get());
                                 * } else {
                                 *      set$foo(<default-value>);
                                 * }
                                 */
                                Name ptrVarName = attributeValueName(varInfo.boundFuncResultInitSym());
                                // declare a temp variable of type Pointer to store old value of Pointer field
                                JCVariableDecl oldPtrVar = TmpVar("old", syms.f3_PointerTypeErasure, id(ptrVarName));
                                addStmt(oldPtrVar);

                                JCVariableDecl newPtrVar = TmpVar("new", syms.f3_PointerTypeErasure, initValue);
                                addStmt(newPtrVar);

                                // Add the receiver of the current Var symbol as dependency to the Pointer, so that
                                // we will get notification whenever the result of the bound function evaluation changes.
                                addStmt(CallStmt(defs.Pointer_switchDependence,
                                                 id(oldPtrVar),
                                                 id(newPtrVar),
                                                 getReceiverOrThis(varSym),
                                                 DepNum(getReceiver(varSym), null, varInfo.boundFuncResultInitSym())));

                                // We have a Pointer - we need to call Pointer.get() and cast the result.
                                initValue = castFromObject(Call(id(newPtrVar), defs.get_PointerMethodName), varSym.type);
                                initValue = If(NEnull(id(newPtrVar)), initValue, defaultValue(varInfo));

                                // T varNewValue$ = default value
                                addStmt(Var(Flags.FINAL, type, defs.varNewValue_ArgName, initValue));
                                // Set the var.
                                addStmts(makeBoundGetAttributeCode());
                            } else {
                                // T varNewValue$
                                addStmt(Var(0, type, defs.varNewValue_ArgName, null));

                                addStmt(
                                    TryWithErrorHandler(varInfo,
                                        varInfo.boundPreface(),
                                        Stmt(m().Assign(id(defs.varNewValue_ArgName), initValue)),
                                    /*on exception*/
                                        Stmt(m().Assign(id(defs.varNewValue_ArgName), defaultValue(varInfo)))));
                                        
                                // Set the var.
                                addStmts(makeBoundGetAttributeCode());
                            }

                            // if (pending) { mark forward access } else if (bound and invalid) { set$var(init/bound expression); }
                            addStmt(
                                OptIf (FlagTest(proxyVarSym, defs.varFlagINIT_MASK, defs.varFlagINIT_PENDING),
                                    Block(
                                        FlagChangeStmt(proxyVarSym, null, defs.varFlagFORWARD_ACCESS)
                                    ),
                                /*else (active)*/
                                    OptIf (FlagTest(proxyVarSym, defs.varFlagIS_BOUND_INVALID, defs.varFlagIS_BOUND_INVALID),
                                        endBlock()
                                    )
                                )
                            );
                        } else if (varInfo.onInvalidate() != null) {
                            // unbound with on-invalidate -- reset validity
                            addStmt(FlagChangeStmt(proxyVarSym, defs.varFlagSTATE_MASK, defs.varFlagSTATE_VALID));
                        }

                        // Construct and add: return $var;
                        addStmt(Return(Get(proxyVarSym)));
                    }
                }

                // This method generates code for setting a non-sequence var.
                public List<JCStatement> makeBoundGetAttributeCode() {
                    Name newValueName = defs.varNewValue_ArgName;
                    Name oldValueName = defs.varOldValue_LocalVarName;
                    ListBuffer<JCStatement> bgstmts = ListBuffer.lb();

                    // Set read only bit (trapdoor.)
                    //TODO: is this still needed?
                    if (varInfo.isReadOnly()) {
                        bgstmts.append(FlagChangeStmt(proxyVarSym, null, defs.varFlagIS_READONLY));
                    }

                    JCStatement finish;
                    if (needsOnReplace) {
                        // varOldValue$ != varNewValue$
                        // or !varOldValue$.equals(varNewValue$) test for Object value types
                        JCExpression valueChangedTest = isValueType(type)
                                ? NOT(Call(defs.Checks_equals, id(oldValueName), id(newValueName)))
                                : NE(id(oldValueName), id(newValueName));
                        // Default-Not_applied
                        JCExpression defaultAppliedTest = FlagTest(defs.varFlags_LocalVarName, defs.varFlagINITIALIZED_STATE_BIT, null);

                        finish =
                            Block(
                                //Debug("BGet-ApplyOnr "+proxyVarSym, id(newValueName)),
                                Var(Flags.FINAL, type, oldValueName, Get(proxyVarSym)),
                                FlagChangeStmt(proxyVarSym, defs.varFlagSTATE_MASK, defs.varFlagVALID_DEFAULT_APPLIED),
                                If (OR(valueChangedTest, defaultAppliedTest),
                                    Block(
                                        //Debug("Onr "+proxyVarSym, id(newValueName)),
                                        SetStmt(proxyVarSym, id(newValueName)),
                                        CallStmt(attributeOnReplaceName(varSym), id(oldValueName), id(newValueName))
                                    )
                                )
                            );
                    } else {
                        finish =
                            Block(
                                //Debug("BGet-Apply "+proxyVarSym, id(newValueName)),
                                FlagChangeStmt(proxyVarSym, defs.varFlagSTATE_MASK, defs.varFlagVALID_DEFAULT_APPLIED),
                                SetStmt(proxyVarSym, id(newValueName))
                            );
                    }
                    if (needsInvalidate) {
                        finish =
                                If (FlagTest(proxyVarSym, defs.varFlagSTATE_TRIGGERED, defs.varFlagINVALID_STATE_BIT),
                                    Block(
                                        // Initialized (not yet triggered state), restore flags, wait for the trigger before actually changing
                                        //Debug("BGet-Revert "+proxyVarSym, id(newValueName)),
                                        FlagChangeStmt(proxyVarSym, id(defs.varFlagALL_FLAGS), id(defs.varFlags_LocalVarName)),
                                        Return (id(newValueName))
                                    ),
                                /*else (all valid)*/
                                    finish
                                );
                    }
                    bgstmts.append(finish);

                    return bgstmts.toList();
                }

                // generates try {preface, action} catch(RuntimeException re) { ErrorHandler.bindException(re); <onCatchStat> }
                JCStatement TryWithErrorHandler(VarInfo varInfo, List<JCStatement> preface, JCStatement action, JCStatement onCatchStat) {
                    boolean isSafe = varInfo.hasSafeInitializer();
                    if (isSafe) {
                        // No exceptions can be thrown, just in-line it
                        return
                            Block(
                                preface,
                                action
                            );
                    }

                    JCVariableDecl tmpVar = TmpVar(syms.runtimeExceptionType, null);
                    JCStatement callErrorHandler = CallStmt(defs.ErrorHandler_bindException, id(tmpVar));

                    // The Javac backend expects a line-number of the catch-clause;
                    // otherwise it sometimes emits "line 0".
                    // But we need and want no line-number on the other boiler-plate.
                    clearDiagPos();
                    JCBlock cblock = Block(callErrorHandler, onCatchStat);
                    setDiagPos(varInfo);
                    JCCatch tcatch  = m().Catch(tmpVar, cblock);
                    clearDiagPos();
                    return
                        Try(
                            Block(preface, action),
                            tcatch
                        );
                }
            };

            clearDiagPos();
            vamb.build();
        }

        private JCExpression validBindeesTest(VarInfo varInfo) {
            Set<F3VarSymbol> unique = new LinkedHashSet<F3VarSymbol>();
            for (F3VarSymbol vsym : varInfo.boundBindees()) {
                if (!vsym.isSpecial() && !vsym.isSequence()) {
                    unique.add(vsym);
                }
            }

            if (unique.size() == 0) {
                return null;
            } else {
                JCExpression test = null;
                for (F3VarSymbol vsym : unique) {
                    JCExpression t = FlagTest(vsym, defs.varFlagSTATE_TRIGGERED, defs.varFlagINVALID_STATE_BIT);
                    if (test == null) {
                        test = t;
                    } else {
                        test = OR(test, t);
                    }
                }
                return test;
            }
        }

       //
        // This method constructs the setter method for the specified attribute.
        //
        private void makeSetterAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeSetterName(varInfo.getSymbol()),
                                                                         varInfo.getRealType(),
                                                                         varInfo, bodyType) {
                @Override
                public void initialize() {
                    addParam(type, defs.varNewValue_ArgName);
                }

                @Override
                public void statements() {
                    boolean isLeaf = isLeaf(varInfo);

                    if (isLeaf) {
                        if (varInfo.isReadOnly()) {
                            addStmt(CallStmt(getReceiver(varSym), defs.restrictSet_F3ObjectMethodName, GetFlags(varSym)));
                        }
                    } else {
                        // Restrict setting.
                        beginBlock();
                        addStmt(CallStmt(getReceiver(varSym), defs.restrictSet_F3ObjectMethodName, GetFlags(varSym)));
                        JCExpression ifReadonlyTest = FlagTest(varSym, defs.varFlagIS_READONLY, null);
                        // if (isReadonly$(VOFF$var)) { restrictSet$(VOFF$var); }
                        addStmt(OptIf(NOT(ifReadonlyTest),
                                endBlock()));
                    }

                    if (varInfo.hasBoundDefinition() && varInfo.hasBiDiBoundDefinition()) {
                        // Begin bidi block.
                        beginBlock();
                        // Preface to setter.
                        addStmts(varInfo.boundInvSetterPreface());
                        // Test to see if bound.
                        JCExpression ifBoundTest = FlagTest(varSym, defs.varFlagIS_BOUND, defs.varFlagIS_BOUND);
                        // if (isBound$(VOFF$var)) { set$other(inv bound expression); }
                        addStmt(OptIf(ifBoundTest,
                                endBlock()));
                    }

                    // Set the var.
                    addStmts(makeSetAttributeCode(varInfo, defs.varNewValue_ArgName));
                    
                    // return $var;
                    addStmt(Return(Get(proxyVarSym)));
                }
            };

            vamb.build();
        }

        //
        // This method constructs the invalidate method for the specified attribute.
        //
        private void makeInvalidateAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeInvalidateName(varInfo.getSymbol()),
                                                                         syms.voidType,
                                                                         varInfo, bodyType) {

                private void abortIfInvalidComponents() {
                    JCExpression vbt = validBindeesTest(varInfo);
                    if (vbt != null) {
                        addStmt(
                            If (AND(IsTriggerPhase(), vbt),
                                // Abort
                                Block(
                                    // Some component is invalid -- wait for it to come around triggered
                                    //Debug("Inv-Abort "+proxyVarSym),
                                    Return (null)
                                )
                            )
                        );
                    }
                }

                @Override
                public void initialize() {
                    addParam(phaseArg());
                }
                                                                         
                @Override
                public void statements() {
                    // Handle invalidators if present.
                    List<BindeeInvalidator> invalidatees = varInfo.boundInvalidatees();
                    boolean hasInvalidators = !invalidatees.isEmpty();

                    JCVariableDecl varState = Var(syms.intType,
                            defs.varState_LocalVarName,
                            BITAND(GetFlags(proxyVarSym), id(defs.varFlagSTATE_MASK)));
                    JCVariableDecl wasValidVar = Var(syms.booleanType,
                            defs.wasInvalid_LocalVarName,
                            EQ(BITAND(id(varState), phaseArg()), id(varState)));
                    addStmt(varState);
                    addStmt(wasValidVar);

                    if (hasInvalidators) {
                        //Abort if invalid
                        abortIfInvalidComponents();
                        // Insert invalidators.
                        for (BindeeInvalidator invalidator : invalidatees) {
                            addStmt(invalidator.invalidator);
                        }

                        //TODO: not generating the rest of invalidation is only going to work if all things with invalidators are shredded
                        //  note the assymetry with sequence invalidators, which are not all shredded
                        return;
                    }

                    // Prepare to accumulate if statements.
                    beginBlock();
                    //Abort if invalid
                    abortIfInvalidComponents();
                    //addStmt(Debug("InvalidateGO "+proxyVarSym, phaseArg()));

                    boolean mixin = !isMixinClass() && varInfo instanceof MixinClassVarInfo;
                    boolean notifyDependents = false;

                    if (varInfo.isOverride() || varInfo instanceof SuperClassVarInfo) {
                        // Call super first.
                        callSuper();
                    } else if (mixin) {
                        callMixin((ClassSymbol)varSym.owner);
                    } else {
                        // Set the phase state part of the flag to the next state part of the phase transition
                        addStmt(SetNextVarFlagsStateFromPhaseTransition(proxyVarSym));

                        notifyDependents = !isLeaf(varInfo) || varInfo.hasDependents();
                    }
                     
                    // Strip phase down to the non-BE form before propagating
                    addStmt(ClearBeFromPhaseTransition());

                    if (notifyDependents) {
                        // notifyDependents(VOFF$var, phase$);
                        addStmt(CallStmt(getReceiver(varInfo), defs.notifyDependents_F3ObjectMethodName, Offset(proxyVarSym), phaseArg()));
                    }
                    
                    for (VarInfo otherVar : varInfo.boundBinders()) {
                        // invalidate$var(phase$);
                        if (!otherVar.generateSequenceAccessors()) {
                            if (depGraphWriter != null) {
                                depGraphWriter.writeDependency(otherVar.sym, varSym);
                            }
                            addStmt(CallStmt(attributeInvalidateName(otherVar.getSymbol()), phaseArg()));
                        } else if (isBoundFunctionResult(varSym)) {
                            // This is bound function result variable. And the dependent is a sequence
                            // So, make sure we call the right sequence invalidate method with correct
                            // computed old and new sizes.
                            if (depGraphWriter != null) {
                                depGraphWriter.writeDependency(otherVar.sym, varSym);
                            }

                            JCVariableDecl oldPtrVar = TmpVar("old", syms.f3_PointerTypeErasure, Get(varSym));
                            JCVariableDecl newPtrVar = TmpVar("new", syms.f3_PointerTypeErasure, Getter(varSym));
                            JCVariableDecl oldSizeVar = TmpVar("oldSize", syms.intType,
                                         If (NEnull(id(oldPtrVar)),
                                             Call(id(oldPtrVar), defs.size_PointerMethodName),
                                             Int(0)
                                         ));
                            JCVariableDecl newSizeVar = TmpVar("newSize", syms.intType, 
                                         If (NEnull(id(newPtrVar)),
                                             Call(id(newPtrVar), defs.size_PointerMethodName),
                                             Int(0)
                                         ));

                            Symbol otherSym = otherVar.getSymbol();
                            addStmt(
                                    If(IsInvalidatePhase(),
                                        Block(
                                            CallSeqInvalidateUndefined(otherSym)
                                        ),
                                    /*Else (Trigger phase)*/
                                        Block(
                                            oldPtrVar,
                                            newPtrVar,
                                            oldSizeVar,
                                            newSizeVar,
                                            CallSeqTrigger(otherSym, Int(0), id(oldSizeVar), id(newSizeVar))
                                        )
                                    )
                            );
                        }
                    }
                    
                    // Graph back to inverse.
                    if (depGraphWriter != null && varInfo.hasBoundDefinition() && varInfo.hasBiDiBoundDefinition()) {
                        for (F3VarSymbol bindeeSym : varInfo.boundBindees()) {
                            depGraphWriter.writeDependency(bindeeSym, varSym);
                            break; // Only need the first entry (rest are duplicates)
                        }
                    }

                    if (varInfo.onReplace() != null || varInfo.onInvalidate() != null) {
                        // Begin the get$ block.
                        beginBlock();

                        // Add on-invalidate trigger if any
                        if (varInfo.onInvalidate() != null) {
                            addStmt(OptIf(OR(id(wasValidVar), FlagTest(proxyVarSym, defs.varFlagIS_BOUND, null)),
                                varInfo.onInvalidateAsInline()));
                        }

                        // Call the get$var to force evaluation.
                        if (varInfo.onReplace() != null) {
                            addStmt(
                                If (FlagTest(proxyVarSym, defs.varFlagIS_EAGER, defs.varFlagIS_EAGER),
                                    Block(
                                        Stmt(Getter(proxyVarSym))
                                    )
                                ));
                        }

                        // if (phase$ == VFLGS$NEEDS_TRIGGER) { get$var(); }
                        addStmt(OptIf(IsTriggerPhase(),
                                endBlock()));
                    }

                    // Wrap up main block.
                    JCBlock mainBlock = endBlock();

                    // Necessary to call mixin parent in else in case the var is a bare synth.
                    JCStatement mixinBlock = null;
                    if (mixin) {
                        beginBlock();
                        callMixin((ClassSymbol)varSym.owner);
                        mixinBlock = endBlock();
                    } else {
                        //mixinBlock = Block(Debug("InvalidateFail "+proxyVarSym, phaseArg()), Debug("..InvalidateFail "+proxyVarSym, id(varState)));
                    }

                    // if (!isValidValue$(VOFF$var)) { ... invalidate  code ... }
                    addStmt(If(id(wasValidVar),
                            mainBlock, mixinBlock));
                }

                // phase non-FINAL
                @Override
                protected JCVariableDecl makeParam(Type varType, Name varName) {
                    return Var(Flags.PARAMETER, varType, varName, null);
                }

            };
            clearDiagPos();
            vamb.build();
        }

        //
        // This method constructs the onreplace$ method for the specified attribute.
        //
        private void makeOnReplaceAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeOnReplaceName(varInfo.getSymbol()),
                                                                         syms.voidType,
                                                                         varInfo, bodyType) {
                Name oldValueName;
                Name newValueName;

                @Override
                public void initialize() {
                    F3OnReplace onReplace = varInfo.onReplace();

                    oldValueName = paramOldValueName(onReplace);
                    newValueName = paramNewValueName(onReplace);
                    
                    addParam(type, oldValueName);
                    addParam(type, newValueName);
                    
                    buildIf(needOnReplaceAccessorMethod(varInfo) && (!varInfo.isOverride() || onReplace != null));
                }
                
                @Override
                public void statements() {
                    // Call super first.
                    if (varInfo.isOverride()) {
                        callSuper();
                    }

                    // Mixin onreplace$
                    if (!isMixinClass() && varInfo instanceof MixinClassVarInfo) {
                        callMixin((ClassSymbol)varSym.owner);
                    }

                    generateSwitchDependences();

                    // Fetch the on replace statement or null.
                    JCStatement onReplace = varInfo.onReplaceAsInline();
    
                    // Need to capture init state if has trigger.
                    if (onReplace != null) {
                        // Insert the trigger.
                        addStmt(onReplace);
                    }
                }

                // This method generates F3Base.switchDependence$ calls for all the
                // bound select expressions that use the current var as the selector.
                private void generateSwitchDependences() {
                    for (VarInfo dependent : varInfo.boundBinders()) {
                        JCExpression rcvr = getReceiverOrThis(varInfo.sym);
                        for (DependentPair depPair : dependent.boundBoundSelects()) {
                            // static variables and sequences are handled diffently
                            if (depPair.instanceSym != varInfo.sym ||
                                depPair.referencedSym.isStatic() ||
                                types.isSequence(depPair.referencedSym.type)) {
                                continue;
                            }
                            if (isMixinVar(depPair.referencedSym)) {
                                JCExpression oldOffset = If(EQnull(id(oldValueName)),
                                    Int(0),
                                    Offset(id(oldValueName), depPair.referencedSym));
                                JCExpression newOffset = If(EQnull(id(newValueName)),
                                    Int(0),
                                    Offset(id(newValueName), depPair.referencedSym));
                                addStmt(CallStmt(defs.F3Base_switchDependence,
                                    rcvr,
                                    id(oldValueName), oldOffset,
                                    id(newValueName), newOffset,
                                    DepNum(getReceiver(depPair.instanceSym), depPair.instanceSym, depPair.referencedSym)));
                            } else {
                                JCVariableDecl offsetVar = TmpVar(syms.intType, Offset(depPair.referencedSym));
                                addStmt(offsetVar);
                                addStmt(CallStmt(defs.F3Base_switchDependence,
                                    rcvr,
                                    id(oldValueName), id(offsetVar),
                                    id(newValueName), id(offsetVar),
                                    DepNum(getReceiver(depPair.instanceSym), depPair.instanceSym, depPair.referencedSym)));
                            }
                        }
                    }
                }
            };

            vamb.build();
        }

        //-----------------------------------------------------------------------------------------------------------------------------
        //
        // Mixin var accessors.
        //
        
        //
        // This method constructs a getMixin$ method.
        //
        private void makeGetMixinAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeGetMixinName(varInfo.getSymbol()),
                                                                         varInfo.getRealType(),
                                                                         varInfo, bodyType) {
                @Override
                public void statements() {
                    // Construct and add: return $var;
                    addStmt(Return(id(attributeValueName(proxyVarSym))));
                }
            };
             
            vamb.build();
        }

        //
        // This method constructs a getVOFF$ method.
        //
        private void makeGetVOFFAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeGetVOFFName(varInfo.getSymbol()),
                                                                         syms.intType,
                                                                         varInfo, bodyType) {
                @Override
                public void statements() {
                    addStmt(Return(id(attributeOffsetName(proxyVarSym))));
                }
            };
             
            vamb.build();
        }
        
        //
        // This method constructs a setMixin$ method.
        //
        private void makeSetMixinAccessorMethod(VarInfo varInfo, int bodyType) {
            VarAccessorMethodBuilder vamb = new VarAccessorMethodBuilder(attributeSetMixinName(varInfo.getSymbol()),
                                                                         varInfo.getRealType(),
                                                                         varInfo, bodyType) {
                @Override
                public void initialize() {
                    addParam(type, defs.varNewValue_ArgName);
                }
                
                @Override
                public void statements() {
                    // Construct and add: return $var;
                    addStmt(Return(m().Assign(id(attributeValueName(proxyVarSym)), id(defs.varNewValue_ArgName))));
                }
            };
             
            vamb.build();
        }
        
        //-----------------------------------------------------------------------------------------------------------------------------
        
        //
        // This method constructs the accessor methods for an attribute.
        //
        public void makeAnAttributeAccessorMethods(VarInfo ai, int bodyType) {
            if (!ai.useAccessors()) {
                if (ai.useGetters() && !ai.isOverride()) {
                    makeGetterAccessorMethod(ai, bodyType);
                }
            } else {
                if (!(ai instanceof MixinClassVarInfo)) {
                    if (ai.generateSequenceAccessors()) {
                        if (!ai.isOverride()) {
                            makeSeqGetterAccessorMethod(ai, bodyType);
                            makeSeqGetElementAccessorMethod(ai, bodyType);
                            makeSeqGetSizeAccessorMethod(ai, bodyType);
                            makeSeqInvalidateAccessorMethod(ai, bodyType);
                            makeSeqOnReplaceAccessorMethod(ai, bodyType);
                        } else if (bodyType != BODY_NONE) {
                            if (ai.hasInitializer()) {
                                // We only need to worry about computational methods
                                // The getter and be are generic.
                                makeSeqGetElementAccessorMethod(ai, bodyType);
                                makeSeqGetSizeAccessorMethod(ai, bodyType);
                            }
                            if (needOverrideInvalidateAccessorMethod(ai)) {
                                makeSeqInvalidateAccessorMethod(ai, bodyType);
                                }
                            if (ai.onReplace() != null || ai.isMixinVar()) {
                                makeSeqOnReplaceAccessorMethod(ai, bodyType);
                            }
                        }
                   } else {
                        if (!ai.isOverride()) {
                            makeGetterAccessorMethod(ai, bodyType);
                            if (ai.useSetters()) {
                                makeSetterAccessorMethod(ai, bodyType);
                            }
                            if (needInvalidateAccessorMethod(ai)) {
                                makeInvalidateAccessorMethod(ai, bodyType);
                            }
                            makeOnReplaceAccessorMethod(ai, bodyType);
                        } else if (bodyType != BODY_NONE) {
                            if (ai.hasInitializer()) {
                                // Bound or not, we need getter & setter on override since we
                                // may be switching between bound and non-bound or visa versa
                                makeGetterAccessorMethod(ai, bodyType);
                                makeSetterAccessorMethod(ai, bodyType);
                            }
                            if (needOverrideInvalidateAccessorMethod(ai)) {
                                makeInvalidateAccessorMethod(ai, bodyType);
                            }
                            if (ai.onReplace() != null || ai.isMixinVar()) {
                                makeOnReplaceAccessorMethod(ai, bodyType);
                            }
                        }
                    }                    
               } else {
                    // Mixins in a normal class.
                    if (ai.needsCloning()) {
                        boolean hasInit = ai.hasInitializer() || ai.hasBoundDefinition();
                        bodyType = hasInit ? BODY_NORMAL : BODY_MIXIN;
                        
                        if (ai.generateSequenceAccessors()) {
                            makeSeqGetterAccessorMethod(ai, BODY_MIXIN);
                            makeSeqGetElementAccessorMethod(ai, bodyType);
                            makeSeqGetSizeAccessorMethod(ai, bodyType);
                            makeSeqInvalidateAccessorMethod(ai, BODY_NORMAL);
                            makeSeqOnReplaceAccessorMethod(ai, BODY_NORMAL);
                        } else {
                            makeGetterAccessorMethod(ai, bodyType);
                            makeSetterAccessorMethod(ai, bodyType);
                            makeInvalidateAccessorMethod(ai, BODY_NORMAL);
                            makeOnReplaceAccessorMethod(ai, BODY_NORMAL);
                        }
                    }
                }
            }
        }
        
        //
        // This method constructs mixin interfaces for the specified var.
        //
        public void makeAttributeMixinInterfaces(VarInfo ai, int bodyType) {
            makeGetMixinAccessorMethod(ai, bodyType);
            makeGetVOFFAccessorMethod(ai, bodyType);
            makeSetMixinAccessorMethod(ai, bodyType);
        }
        
        //
        // This method constructs the accessor methods for each attribute.
        //
        public void makeAttributeAccessorMethods(List<VarInfo> attrInfos) {
            for (VarInfo ai : attrInfos) {
                // Only create accessors for declared and proxied vars.
                if (ai.needsCloning()) {
                    makeAnAttributeAccessorMethods(ai, BODY_NORMAL);
                } else {
                    // If a super has binders we need to emit an overriding invalidate$.
                    if (ai.boundBinders().size() != 0) {
                        if (ai.generateSequenceAccessors())
                            makeSeqInvalidateAccessorMethod(ai, BODY_NORMAL);
                        else
                            makeInvalidateAccessorMethod(ai, BODY_NORMAL);
                    }
                }
                
                if (ai.needsMixinInterface()) {
                    makeAttributeMixinInterfaces(ai, BODY_NORMAL);
                }
            }
        }

        //
        // This method constructs the abstract interfaces for the accessors in
        // a mixin class.
        //
        public void makeMemberVariableAccessorInterfaceMethods(List<VarInfo> attrInfos) {
            // Only for vars within the class.
            for (VarInfo ai : attrInfos) {
                if (ai.needsCloning()) {
                    makeAnAttributeAccessorMethods(ai, BODY_NONE);
                    
                    if (isMixinClass()) {
                        makeAttributeMixinInterfaces(ai, BODY_NONE);
                    }
                }
            }
        }
        
        // Returns true if VCNT$ is needed.
        public boolean needsVCNT$() {
            boolean hasVars = (isScript() ? analysis.getScriptVarCount() : analysis.getClassVarCount()) != 0;
            boolean hasJavaSuperClass = analysis.getF3SuperClassSym() == null;
            boolean hasMixins = !isScript() && !isMixinClass() && !analysis.getImmediateMixins().isEmpty();
            
            return hasVars || hasJavaSuperClass || hasMixins || isMixinClass();
        }

        // Returns true if DCNT$ is needed.
        public boolean needsDCNT$() {
            HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap =
                isScript() ? analysis.getScriptUpdateMap() : analysis.getClassUpdateMap();
            List<VarInfo> varInfos = isScript() ? analysis.scriptVarInfos() : analysis.classVarInfos();
            HashMap<Name, Integer> depMap = getDepMap(varInfos, updateMap);

            boolean hasDeps = !getDepMap(varInfos, updateMap).isEmpty();
            boolean hasJavaSuperClass = analysis.getF3SuperClassSym() == null;
            boolean hasMixins = !isScript() && !isMixinClass() && !analysis.getImmediateMixins().isEmpty();
            
            return hasDeps || hasJavaSuperClass || hasMixins || isMixinClass();
        }
        
        // Returns true if FCNT$ is needed.
        public boolean needsFCNT$() {
            List<JCTree> invokeCases = getCurrentClassDecl().invokeCases(isScript());
            
            boolean hasFuncs = !invokeCases.isEmpty();
            boolean hasJavaSuperClass = analysis.getF3SuperClassSym() == null;
            boolean hasMixins = !isScript() && !isMixinClass() && !analysis.getAllMixins().isEmpty();
            
            return hasFuncs || hasJavaSuperClass || hasMixins || isMixinClass();
        }

        //
        // This method generates an enumeration for each of the class's instance attributes.
        //
        public void makeAttributeNumbers(List<VarInfo> attrInfos, int varCount) {
            if (!needsVCNT$()) return;

            // Construct a static count variable (VCNT$), -1 indicates count has not been initialized.
            int initCount = analysis.isFirstTier() ? varCount : -1;
            addDefinition(addSimpleIntVariable(Flags.STATIC | Flags.PRIVATE, defs.count_F3ObjectFieldName, initCount));

            // Construct a static count accessor method (VCNT$)
            makeVCNT$(attrInfos, varCount);

            // Construct a virtual count accessor method (count$)
            makecount$(varCount);

            // Accumulate variable numbering.
            for (VarInfo ai : attrInfos) {
                // Only variables actually declared.
                if (ai.hasEnumeration()) {
                    // Construct offset var.
                    Name name = attributeOffsetName(ai.getSymbol());
                    JCExpression init = analysis.isFirstTier() ? Int(ai.getEnumeration()) : null;
		    //System.err.println("init "+ name+": "+init);
                    long flags = analysis.isFirstTier() && isLeaf(ai) ? (Flags.FINAL | Flags.STATIC | Flags.PUBLIC) :
                                                                        (Flags.STATIC | Flags.PUBLIC);
                    // Construct and add: public static int VOFF$name = n;
                    addDefinition(makeField(flags, syms.intType, name, init));
                }
            }
        }

        //
        // This method generates a flags field for each of the class's instance attributes.
        //
        public void makeAttributeFlags(List<VarInfo> attrInfos) {
            // Define attribute flags.
            for (VarInfo ai : attrInfos) {
                // Only variables actually declared.
                if (ai.hasEnumeration()) {
                    // Construct flags var.
                    Name name = attributeFlagsName(ai.getSymbol());
                    // Determine access flags.
                    long flags = attributeFieldAccessFlags(ai);
                    if (isScript() || isMixinClass()) flags |= Flags.STATIC;
                    boolean zero = false;
                    JCExpression init = null;
                    
                    if (!ai.isOverride()) {
                        if (ai instanceof MixinClassVarInfo) {
                            init = updateVarBits(ai, Select(makeType(types.erasure(ai.getSymbol().owner.type), false), name));
                            
                            if (init == null) {
                                // TODO - fix when overridden twice.
                                init = Select(makeType(types.erasure(ai.getSymbol().owner.type), false), name);
                            }
                        } else {
                            init = initialVarBits(ai);
                            zero = init == null;
                        }
                    } else if (isMixinClass()) {
                        // TODO - fix when overridden twice.
                        init = updateVarBits(ai, Select(makeType(types.erasure(ai.getSymbol().owner.type), false), name));
                    } else {
                        // done in init.
                    }
                    
                    if (zero || init != null) {
                        // Construct and add: public static short VFLGS$name = n;
                        addDefinition(makeField(flags, syms.shortType, name, init));
                    }
                }
            }
        }

        //
        // The method constructs the VCNT$ method for the current class.
        //
        public void makeVCNT$(final List<VarInfo> attrInfos, final int varCount) {
            StaticMethodBuilder smb = new StaticMethodBuilder(defs.count_F3ObjectFieldName, syms.intType) {
                @Override
                public void statements() {
                    if (analysis.isFirstTier()) {
                        addStmt(Return(Int(varCount)));
                    } else {
                        // Start if block.
                        beginBlock();
            
                        // VCNT$ = super.VCNT$() + n  or VCNT$ = n;
                        JCExpression setVCNT$Expr = superClassSym == null ?  Int(varCount) :
			    PLUS(Call(makeType(types.erasure(superClassSym.type)), defs.count_F3ObjectFieldName),
                                                                                  Int(varCount));
                        Name countName = names.fromString("$count");
                        // final int $count = VCNT$ = super.VCNT$() + n;
                        addStmt(makeField(Flags.FINAL, syms.intType, countName, m().Assign(id(defs.count_F3ObjectFieldName), setVCNT$Expr)));
            
                        for (VarInfo ai : attrInfos) {
                            // Only variables actually declared.
                            if (ai.hasEnumeration()) {
                                // Offset var name.
                                Name name = attributeOffsetName(ai.getSymbol());
                                // VCNT$ - n + i;
                                JCExpression setVOFF$Expr = PLUS(id(countName), Int(ai.getEnumeration() - varCount));
                                // VOFF$var = VCNT$ - n + i;
                                addStmt(Stmt(m().Assign(id(name), setVOFF$Expr)));
                            }
                        }
        
                        // VCNT$ == -1
                        JCExpression condition = EQ(id(defs.count_F3ObjectFieldName), Int(-1));
                        // if (VCNT$ == -1) { ...
                        addStmt(OptIf(condition,
                                endBlock()));
                        // return VCNT$;
                        addStmt(Return(id(defs.count_F3ObjectFieldName)));
                    }
                }
            };
            clearDiagPos();
            smb.build();
        }

        //
        // The method constructs the count$ method for the current class.
        //
        public void makecount$(final int varCount) {
            MethodBuilder smb = new MethodBuilder(defs.count_F3ObjectMethodName, syms.intType) {
                @Override
                public void statements() {
                    if (analysis.isFirstTier()) {
                        // Construct and add: return n;
                        addStmt(Return(Int(varCount)));
                    } else {
                        // Construct and add: return VCNT$();
                        addStmt(Return(Call(defs.count_F3ObjectFieldName)));
                    }
                }
            };
            clearDiagPos();
            smb.build();
        }
        
        //
        // Clones a field declared in F3Base as an non-static field.  It also creates
        // F3Object accessor method.
        //
        private void cloneF3BaseVar(F3VarSymbol var, HashSet<String> excludes) {
            // Var name as a string.
            String str = var.name.toString();
            String upperStr = str.substring(0, 1).toUpperCase() + str.substring(1);
            // Var modifier flags.
            long flags = var.flags();
            
            // If it's an excluded name or a static then skip it.
            if (excludes.contains(str) ||
                (flags & (Flags.SYNTHETIC | Flags.STATIC)) != 0) {
                return;
            }
            
            // Var F3 type.
            Type type = var.asType();
            
            // Clone the var.
            addDefinition(Var(flags, type, names.fromString(str), null, var));
            
            // Construct the getter.
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            boolean isBoolean = var.getTypeRepresentation() == F3TypeRepresentation.TYPE_REPRESENTATION_BOOLEAN;
            Name name = names.fromString((isBoolean ? "is" : "get") + upperStr);
            stmts.append(Return(id(var)));
            // public int getVar { return Var; }
            MethodSymbol getMethSym = makeMethodSymbol(flags, type, name, List.<Type>nil());
            JCMethodDecl getMeth = Method(flags, type, name, List.<JCVariableDecl>nil(), stmts.toList(), getMethSym);
            // Add to definitions.
            addDefinition(getMeth);
            // Add to the exclusion set.
            excludes.add(jcMethodDeclStr(getMeth));

            // Construct the setter.
            stmts = ListBuffer.lb();
            name = names.fromString("set" + upperStr);
            Name argName = names.fromString("value");
            JCVariableDecl arg = Param(type, argName);
            stmts.append(m().Exec(m().Assign(id(var), id(argName))));
            // public void setVar(final int value) { Var = value; }
            MethodSymbol setMethSym = makeMethodSymbol(flags, syms.voidType, name, List.<Type>of(type));
            JCMethodDecl setMeth = Method(flags, syms.voidType, name, List.<JCVariableDecl>of(arg), stmts.toList(), setMethSym);
            // Add to definitions.
            addDefinition(setMeth);
            // Add to the exclusion set.
            excludes.add(jcMethodDeclStr(setMeth));
        }

        //
        // Clones a method declared as an F3Object interface to call the static 
        // equivalent in F3Base. 
        //
        private void cloneF3BaseMethod(MethodSymbol method, HashSet<String> excludes) {
            // Method modifier flags.
            long flags = method.flags();
            
            // If it's an excluded name or a static then skip it.
            if (excludes.contains(method.toString()) ||
                (flags & (Flags.SYNTHETIC | Flags.STATIC)) != 0) {
                return;
            }

            // List of arguments to new method.
            ListBuffer<JCVariableDecl> args = ListBuffer.lb();
            // List of arguments to call supporting F3Base method.
            ListBuffer<JCExpression> callArgs = ListBuffer.lb();
            // Add this to to the call.
            callArgs.append(id(names._this));
            
            // Add arguments to both lists.
            for (VarSymbol argSym : method.getParameters()) {
                args.append(Param(argSym.asType(), argSym.name));
                callArgs.append(id(argSym));
            }

            // Buffer for statements.
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            // Method return type.
            Type returnType = method.getReturnType();
            // Basic call to supporting F3Base method.
            JCExpression f3BaseCall = Call(makeType(syms.f3_BaseType), method.name, callArgs);
           
            // Exec or return based on return type.
            if (returnType == syms.voidType) {
                stmts.append(Stmt(f3BaseCall));
            } else {
                stmts.append(Return(f3BaseCall));
            }
    
            //  public type meth$(t0 arg0, ...) { return F3Base.meth$(this, arg0, ...); }
            addDefinition(Method(Flags.PUBLIC, returnType, method.name, args.toList(), stmts.toList(), method));
        }

        //
        // This method clones the contents of F3Base and F3Object when inheriting
        // from a java class.
        //
        public void cloneF3Base(HashSet<String> excludes) {
            // Retrieve F3Base and F3Object.
            ClassSymbol f3BaseSym = (ClassSymbol)syms.f3_BaseType.tsym;
            ClassSymbol f3ObjectSym = (ClassSymbol)syms.f3_ObjectType.tsym;
            Entry e;

            // Clone the vars in F3Base.
            for (e = f3BaseSym.members().elems; e != null && e.sym != null; e = e.sibling) {
                if (e.sym instanceof VarSymbol) {
                     cloneF3BaseVar((F3VarSymbol)e.sym, excludes);
                }
            }

            // Clone the interfaces in F3Object.
            for (e = f3ObjectSym.members().elems; e != null && e.sym != null; e = e.sibling) {
                if (e.sym instanceof MethodSymbol) {
                     cloneF3BaseMethod((MethodSymbol)e.sym, excludes);
                }
            }
        }
        
        //-----------------------------------------------------------------------------------------------------------------------------
        //
        // VarNum method generation.
        //
        
        //
        // This method coordinates the generation of instance level varnum methods.
        //
        public void makeVarNumMethods() {
            final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap =
                isScript() ? analysis.getScriptUpdateMap() : analysis.getClassUpdateMap();
            final List<VarInfo> varInfos = isScript() ? analysis.scriptVarInfos() : analysis.classVarInfos();
            final int varCount = isScript() ? analysis.getScriptVarCount() : analysis.getClassVarCount();
            final List<JCTree> invokeCases = getCurrentClassDecl().invokeCases(isScript());
            HashMap<Name, Integer> depMap = getDepMap(varInfos, updateMap);
            final boolean useMixins = !isScript() && !isMixinClass();
            List<ClassSymbol> mixinClasses = useMixins ? analysis.getAllMixins() : null;
            boolean useConstants = analysis.isFirstTierNoMixins() || isMixinClass();
            
            makeApplyDefaultsMethod(varInfos, varCount);
            makeInvokeMethod(useConstants, invokeCases, mixinClasses);
            
            makeInitVarsMethod(varInfos, updateMap);
            makeDependencyNumbers(useConstants, depMap, mixinClasses);
            makeFunctionNumbers(useConstants, invokeCases, mixinClasses);
             
            if (useMixins) {
                makeNeededMixinDCNT$(mixinClasses);
                makeNeededMixinFCNT$(mixinClasses);
            }

            makeUpdateMethod(useConstants, varInfos, updateMap, depMap, mixinClasses);
            
            if ((isScript() || !isMixinClass()) && varCount > 0) {
                makeGetMethod(varInfos, varCount);
                makeGetElementMethod(varInfos, varCount);
                makeGetAsMethods(varInfos, varCount);
                makeSizeMethod(varInfos, varCount);
                makeSetMethod(varInfos, varCount);
                makeSeqMethod(varInfos, varCount);
                makeInvalidateMethod(varInfos, varCount);
                makeVarChangeBitsMethod(varInfos, varCount);
            }
        }

        //
        // This method constructs the current class's applyDefaults$ method.
        //
        public void makeApplyDefaultsMethod(final List<VarInfo> attrInfos, final int count) {
            MethodBuilder vcmb = new VarCaseMethodBuilder(defs.applyDefaults_F3ObjectMethodName, syms.voidType,
                                                          attrInfos, count) {
                @Override
                public void initialize() {
                    allowOverides = true;
                }

                @Override
                public void statements() {
                    // Constrain the var.
                    if (varInfo.needsCloning() &&
                        !varInfo.isBareSynth() &&
                        !useSimpleInit(varInfo) &&
                         (!varInfo.isOverride() || varInfo.hasInitializer() || varInfo instanceof MixinClassVarInfo)) {
                        if (varInfo instanceof MixinClassVarInfo && !varInfo.hasInitializer()) {
                            // Call the appropriate mixin owner.
                            callMixin((ClassSymbol)varInfo.getSymbol().owner);
                        } else {
                            setDiagPos(varInfo.pos());
                            // Get body of applyDefaults$.
                            if (varInfo.generateSequenceAccessors()) {
                                addStmts(getSeqDefaultInitStatement(varInfo));
                            } else {
                                addStmts(getDefaultInitStatements(varInfo));
                            }
                            clearDiagPos();
                        }
                        
                        if (!stmts.isEmpty()) {
                            addStmt(Return(null));
                        }
                    }
                }
     
                // Specialized body that wraps the case body.
                @Override
                public void body() {
                    // Start inner block.
                    beginBlock();
                    
                    // Fill in body.
                    super.body();
                    
                    // if (init ready)
                    JCExpression ifExpr = FlagTest(varNumArg(), defs.varFlagINIT_WITH_AWAIT_MASK, defs.varFlagINIT_READY);
                    // if (init ready) { body }
                    addStmt(OptIf(ifExpr, endBlock()));
                }
            };
            
            vcmb.build();
        }
        
        //
        // This method generates an count for the class's function values.
        //
        public void makeFunctionNumbers(final boolean useConstants, List<JCTree> invokeCases, List<ClassSymbol> mixinClasses) {
            if (!needsFCNT$()) return;
            
            // Construct a static count variable (FCNT$), -1 indicates function count has not been initialized.
            int initCount = useConstants ? 0 : -1;
            addDefinition(addSimpleIntVariable(Flags.STATIC | Flags.PRIVATE, defs.funcCount_F3ObjectFieldName, initCount));
            
            // Mixin class base numbering.
            if (mixinClasses != null) {
                for (ClassSymbol classSym : mixinClasses) {
                    // Construct and add: public static int DEP$name;
                    addDefinition(makeField(Flags.STATIC | Flags.PUBLIC, syms.intType, classFCNT$Name(classSym), null));
                }
            }

            // Construct a static count accessor method (FCNT$)
            makeFCNT$(useConstants, invokeCases, mixinClasses);
        }

        //
        // The method constructs the FCNT$ method for the current class.
        //
        public void makeFCNT$(final boolean useConstants, final List<JCTree> invokeCases, final List<ClassSymbol> mixinClasses) {
            StaticMethodBuilder smb = new StaticMethodBuilder(defs.funcCount_F3ObjectFieldName, syms.intType) {
                @Override
                public void initialize() {
                    needsReceiver = false;
                }

                @Override
                public void statements() {
                    // Number of function values in this class.
                    int funcCount = invokeCases.size();
                    
                    if (useConstants) {
                        addStmt(Return(Int(funcCount)));
                    } else {
                        // Start if block.
                        beginBlock();
                        
                        // Check if super is required.
                        boolean isFirstTier = analysis.isFirstTier() || superClassSym == null;

                        // Base for first function number.
                        JCExpression countExpr = isFirstTier ? Int(0) : Call(makeType(types.erasure(superClassSym.type)), defs.funcCount_F3ObjectFieldName);
                            
                        // Create base numbers for mixins
                        if (mixinClasses != null && !mixinClasses.isEmpty()) {
                            for (ClassSymbol classSym : mixinClasses) {
                                Name mixinName = classFCNT$Name(classSym);
                                addStmt(Stmt(m().Assign(id(mixinName), countExpr)));
                                countExpr = PLUS(id(mixinName),
                                                 Call(makeType(types.erasure(classSym.type), false), defs.funcCount_F3ObjectFieldName));
                            }
                            // last mixin count
                        } else {
                            // super class count
                            countExpr = isFirstTier ? Int(0) : Call(makeType(types.erasure(superClassSym.type)), defs.funcCount_F3ObjectFieldName);
                        }
                        
                        // Set this classes count.
                        Name countName = names.fromString("$count");
                        addStmt(makeField(Flags.FINAL, syms.intType, countName, m().Assign(id(defs.funcCount_F3ObjectFieldName), countExpr)));
        
                        // FCNT$ == -1
                        JCExpression condition = EQ(id(defs.funcCount_F3ObjectFieldName), Int(-1));
                        // if (FCNT$ == -1) { ...
                        addStmt(OptIf(condition,
                                endBlock()));
                        // return FCNT$ + funcCount;
                        addStmt(Return(PLUS(id(defs.funcCount_F3ObjectFieldName), Int(funcCount))));
                    }
                }
            };
            
            smb.build();
        }
        
        //
        // This method constructs an interface for a mixin's FCNT$.
        //
        public void makeMixinFCNT$(final ClassSymbol classSym, final boolean needsBody) {
            MethodBuilder mb = new MethodBuilder(classFCNT$Name(classSym), syms.intType) {
                @Override
                public void initialize() {
                    bodyType = needsBody ? BODY_NORMAL : BODY_NONE;
                    needsReceiver = false;
                }
                
                @Override
                public long rawFlags() {
                    return needsBody ? Flags.PUBLIC : (Flags.PUBLIC | Flags.ABSTRACT);
                }
                
                @Override
                public void statements() {
                    addStmt(Return(id(classFCNT$Name(classSym))));
                }
            };
            
            mb.build();
        }
        
        //
        // This method generates all the mixin FCNT$ for the current class.
        //
        public void makeNeededMixinFCNT$(List<ClassSymbol> mixinClasses) {
            for (ClassSymbol classSym : mixinClasses) {
                makeMixinFCNT$(classSym, true);
            }
        }
        
        //
        // This method constructs the invoke method.
        //
        public void makeInvokeMethod(final boolean useConstants, final List<JCTree> invokeCases, final List<ClassSymbol> mixinClasses) {
            MethodBuilder vcmb = new MethodBuilder(defs.invoke_F3ObjectMethodName, syms.objectType) {
                @Override
                public void initialize() {
                    addParam(numberArg());
                    addParam(argsFixedArg(0));
                    addParam(argsFixedArg(1));
                    addParam(argsArg());
                }

                @Override
                public void statements() {
                    // Function number count.
                    int funcCount = invokeCases.size();
                    
                    // Prepare to accumulate cases.
                    ListBuffer<JCCase> cases = ListBuffer.lb();
                    
                    // Case number.
                    int tag = 0;
                    
                    // Iterate thru each invoke case.
                    for (JCTree invoke : invokeCases) {
                        cases.append(m().Case(Int(tag), Stmts((JCBlock)invoke,
                                                         m().Break(null))));
                        tag++;
                    }
                    
                    // Start default block.
                    beginBlock();
                                        
                    // Add mixins to the default chain.
                    if (mixinClasses != null) {
                        for (ClassSymbol classSym : mixinClasses) {
                            // Begin if block.
                            beginBlock();
                            
                            // Call mixin update.
                            callMixin(classSym);
                            
                            // if (depNum$ >= FCNT$mixn) 
                            prependStmt(If(GE(numberArg(), id(classFCNT$Name(classSym))),
                                           endBlock(),
                                           null));
                        }
                    }
                    
                    // Call the super version.
                    if (cases.nonEmpty()) {
                        // Add in super call.
                        callSuper();
                    }
                        
                    // Default statements.
                    List<JCStatement> defaults = endBlockAsList();
                    
                    if (cases.nonEmpty()) {
                        if (!defaults.isEmpty()) {
                            cases.append(m().Case(null, defaults));
                        }
                    
                        JCExpression tagExpr = isMixinClass() && !isScript() ? MINUS(numberArg(), Call(classFCNT$Name(getCurrentOwner()))) :
                                               useConstants                  ? numberArg() :
                                                                               MINUS(numberArg(), id(defs.funcCount_F3ObjectFieldName));
                        // Construct and add: switch(FCNT$ + number) { ... }
                        addStmt(m().Switch(tagExpr, cases.toList()));
                        
                        // Default returns null (for void);
                        addStmt(Return(Null()));
                    } else if (!defaults.isEmpty()) {
                        addStmts(defaults);
                        
                        if (isMixinClass() || superClassSym == null) {
                            addStmt(Return(Null()));
                        } else {
                            callSuper();
                        }
                    } else {
                        buildIf(false);
                    }
                }
            };
            
            vcmb.build();
        }
        
        //
        // This method simplifies the bitor-ing of several flags.
        //
        private JCExpression bitOrFlags(JCExpression initial, Name... flags) {
            JCExpression expr = initial;
            
            for (Name flag : flags) {
                if (expr == null) {
                    expr = id(flag);
                } else {
                    expr = BITOR(expr, id(flag));
                }
            }
            
            return expr;
        }
        
        //
        // Return the initialize settings of a vars flags.
        //
        private JCExpression initialVarBits(VarInfo ai) {
            boolean isBound = ai.hasBoundDefinition();
            boolean isSynthetic = ai.isSynthetic();
            boolean isReadonly = ai.isReadOnly();
            boolean isEager = ai.onReplace() != null;
            Name initialState = defs.varFlagSTATE_VALID;
            JCExpression setBits = null;
  
            if (useSimpleInit(ai)) {
                setBits = bitOrFlags(setBits, defs.varFlagINIT_INITIALIZED_DEFAULT);
            } else {
                if (isSynthetic) {
                    setBits = bitOrFlags(setBits, defs.varFlagINIT_READY);
                    if (isBound) {
                        initialState = defs.varFlagSTATE_TRIGGERED;
                    }
                } else if (ai.hasVarInit()) {
                    setBits = bitOrFlags(setBits, defs.varFlagINIT_AWAIT_VARINIT);
                } 

                if (isBound) {
                    setBits = bitOrFlags(setBits, defs.varFlagIS_BOUND);
                }
            }
            setBits = bitOrFlags(setBits, initialState);
            
            if (ai.generateSequenceAccessors() && !isBound && (ai.hasInitializer() || (ai.isDirectOwner() && !ai.isOverride()))) {
                // Non-bound sequences are immediately live
                setBits = bitOrFlags(setBits, defs.varFlagSEQUENCE_LIVE);
            }

            // Read only is normally marked after the default is set (set once).
            if (isReadonly && (isBound ||
                               (!ai.hasInitializer() && !(ai instanceof MixinClassVarInfo)) ||
                                !ai.useAccessors())) {
                setBits = bitOrFlags(setBits, defs.varFlagIS_READONLY);
            }
            
            if (isEager) {
                setBits = bitOrFlags(setBits, defs.varFlagIS_EAGER);
            }
            
            return setBits;
        }
        
        //
        // This method generates code to update overridden var flags.
        //
        public JCExpression updateVarBits(VarInfo ai, JCExpression oldFlags) {
            JCExpression setBits = initialVarBits(ai);
            JCExpression clearBits = oldFlags;
            
            if (ai.hasBoundDefinition() || ai.hasInitializer()) {
                clearBits = BITAND(oldFlags, id(defs.varFlagIS_EAGER));
            }

            if (setBits != null) {
                setBits = BITOR(clearBits, setBits);
            } else if (clearBits != oldFlags) {
                setBits = clearBits;
            }
            
            return setBits == null ? null : flagCast(setBits);
        }
        
        //
        // This method sets up the initial var state.
        //
        private void makeInitVarsMethod(final List<VarInfo> attrInfos,
                                        final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap) {
            MethodBuilder mb = new MethodBuilder(defs.initVars_F3ObjectMethodName, syms.voidType) {
                @Override
                public void statements() {
                    // Begin collecting statements.
                    beginBlock();

                    // Include mixins.
                    callMixins();
                    
                    // Add "this" and "script access" dependencies.
                    for (F3VarSymbol instanceVar : updateMap.keySet()) {
                        HashMap<F3VarSymbol, HashSet<VarInfo>> instanceMap = updateMap.get(instanceVar);

                        for (F3VarSymbol referenceVar : instanceMap.keySet()) {
                            if (instanceVar.isSpecial()) {
                                // Dependent on var referenced via "this"
                                addFixedDependent(instanceVar, referenceVar);
                            } else if (referenceVar.isStatic()) {
                                // Dependent on a script-level var, reference via the script-level var
                                F3ClassSymbol classSym = (F3ClassSymbol) referenceVar.owner;
                                F3VarSymbol scriptAccess = f3make.ScriptAccessSymbol(classSym);
                                addFixedDependent(scriptAccess, referenceVar);
                            }
                        }
                    }
            
                    if (isBoundFuncClass) {
                        /*
                         * For each bound function param (F3Object+varNum pair), at the
                         * end of object creation register "this" as a dependent by
                         * calling addDependent$ method:
                         *
                         *     boundFuncObjParam1.addDependent$(boundFunctionVarNumParam1, this);
                         *     boundFuncObjParam2.addDependent$(boundFunctionVarNumParam2, this);
                         *     ....
                         */
                        for (VarInfo vi : attrInfos) {
                            if (vi.isParameter()) {
                                // call F3Object.addDependent$(int varNum, F3Object dep)
                                Symbol varSym = vi.getSymbol();
                                addStmt(CallStmt(
                                        id(boundFunctionObjectParamName(varSym.name)),
                                        defs.F3Base_addDependent.methodName,
                                        id(boundFunctionVarNumParamName(varSym.name)),
                                        getReceiverOrThis(),
                                        DepNum(null, null, varSym)));
                            }
                        }
                    }

                    ListBuffer<JCStatement> inits = endBlockAsBuffer();
                    
                    // Emit super vars first
                    callSuper();
                    
                    // Mixins and current class next.
                    addStmts(inits);
                    
                    // Emit method only if there was anything beyond the super call.
                    buildIf(!inits.isEmpty());
                }

                private void addFixedDependent(F3VarSymbol instanceVar, F3VarSymbol referenceVar) {
                    addStmt(CallStmt(defs.F3Base_addDependent,
                            Get(instanceVar),
                            Offset(Get(instanceVar), referenceVar),
                            getReceiverOrThis(),
                            DepNum(null, instanceVar, referenceVar)));
                }
            };
            
            mb.build();
        }
        
        //
        // This method generates a map for the class's dependency numbers.
        //
        public HashMap<Name, Integer> getDepMap(List<VarInfo> attrInfos,
                                                final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap) {
            // Set up dependency map.
            HashMap<Name, Integer> depMap = new HashMap<Name, Integer>();
            int depCount = 0;
            
            for (VarInfo vi : attrInfos) {
                if (vi.isInitWithBoundFuncResult()) {
                    F3VarSymbol varSym = vi.getSymbol();
                    Symbol initSym = vi.boundFuncResultInitSym();
                    Name depName = depName(null, initSym);
                    
                    if (!depMap.containsKey(depName)) {
                        depMap.put(depName, new Integer(depCount));
                        depCount++;
                    }
                }
            }

            if (isBoundFuncClass) {
                MethodSymbol msym = (MethodSymbol) getCurrentClassSymbol().owner;
                List<VarSymbol> params = msym.params();

                for (VarSymbol mParam : params) {
                    Scope.Entry e = getCurrentClassSymbol().members().lookup(mParam.name);
                    if (e.sym.kind == Kinds.VAR) {
                        F3VarSymbol param = (F3VarSymbol) e.sym;
                        Name depName = depName(null, param);
                        
                        if (!depMap.containsKey(depName)) {
                            depMap.put(depName, new Integer(depCount));
                            depCount++;
                        }
                    }
                }
            }

            for (F3VarSymbol instanceVar : updateMap.keySet()) {
                HashMap<F3VarSymbol, HashSet<VarInfo>> instanceMap = updateMap.get(instanceVar);

                for (F3VarSymbol referenceVar : instanceMap.keySet()) {
                    Name depName = depName(instanceVar, referenceVar);
                    
                    if (!depMap.containsKey(depName)) {
                        depMap.put(depName, new Integer(depCount));
                        depCount++;
                    }
                }
            }
            
            return depMap;
        }

        //
        // This method generates an enumeration for each of the class's dependencies.
        //
        public void makeDependencyNumbers(final boolean useConstants, final HashMap<Name, Integer> depMap, List<ClassSymbol> mixinClasses) {
            if (!needsDCNT$()) return;
            
            // Construct a static count variable (DCNT$), -1 indicates dep count has not been initialized.
            int initCount = useConstants ? depMap.size() : -1;
            addDefinition(addSimpleIntVariable(Flags.STATIC | Flags.PRIVATE, defs.depCount_F3ObjectFieldName, initCount));
            
            // Mixin class base numbering.
            if (mixinClasses != null) {
                for (ClassSymbol classSym : mixinClasses) {
                    // Construct and add: public static int DEP$name;
                    addDefinition(makeField(Flags.STATIC | Flags.PUBLIC, syms.intType, classDCNT$Name(classSym), null));
                }
            }

            // Accumulate dependency numbering.
            for (Name depName : depMap.keySet()) {
                int num = depMap.get(depName).intValue();
                JCExpression init = useConstants ? Int(num) : null;
                long flags = useConstants ? (Flags.FINAL | Flags.STATIC | Flags.PUBLIC) :
                                            (Flags.STATIC | Flags.PUBLIC);
                // Construct and add: public static int DEP$name = n;
                addDefinition(makeField(flags, syms.intType, depName, init));
            }
            
            // Construct a static count accessor method (DCNT$)
            makeDCNT$(useConstants, depMap, mixinClasses);
        }
        
        //
        // The method constructs the DCNT$ method for the current class.
        //
        public void makeDCNT$(final boolean useConstants, final HashMap<Name, Integer> depMap, final List<ClassSymbol> mixinClasses) {
            StaticMethodBuilder smb = new StaticMethodBuilder(defs.depCount_F3ObjectFieldName, syms.intType) {
                @Override
                public void initialize() {
                    needsReceiver = false;
                }

                @Override
                public void statements() {
                    // Number fo dependencies in this class.
                    int depCount = depMap.size();
              
                    if (useConstants) {
                        addStmt(Return(Int(depCount)));
                    } else {
                        // Start if block.
                        beginBlock();
                        
                        // Check if super is required.
                        boolean isFirstTier = analysis.isFirstTier() || superClassSym == null;

                        // Base for first dependency number.
                        JCExpression countExpr = isFirstTier ? Int(0) : Call(makeType(types.erasure(superClassSym.type)), defs.depCount_F3ObjectFieldName);
                            
                        // Create base counts for mixins
                        if (mixinClasses != null && !mixinClasses.isEmpty()) {
                            for (ClassSymbol classSym : mixinClasses) {
                                Name mixinName = classDCNT$Name(classSym);
                                addStmt(Stmt(m().Assign(id(mixinName), countExpr)));
                                countExpr = PLUS(id(mixinName),
                                                 Call(makeType(types.erasure(classSym.type), false), defs.depCount_F3ObjectFieldName));
                            }
                            
                            // last mixin + depCount
                            countExpr = PLUS(countExpr, Int(depCount));
                        } else {
                            // super class + depCount
                            countExpr = isFirstTier ? Int(depCount) :
				PLUS(Call(makeType(types.erasure(superClassSym.type)), defs.depCount_F3ObjectFieldName),
                                                           Int(depCount));
                        }
                        
                        // Set this classes count.
                        Name countName = names.fromString("$count");
                        addStmt(makeField(Flags.FINAL, syms.intType, countName, m().Assign(id(defs.depCount_F3ObjectFieldName), countExpr)));
            
                        // Accumulate dependency numbering.
                        for (Name depName : depMap.keySet()) {
                            int num = depMap.get(depName).intValue();
                            // DCNT$ - n + i;
                            JCExpression setDEP$Expr = PLUS(id(countName), Int(num - depCount));
                            // DEP$ = DCNT$ - n + i;
                            addStmt(Stmt(m().Assign(id(depName), setDEP$Expr)));
                        }
        
                        // DCNT$ == -1
                        JCExpression condition = EQ(id(defs.depCount_F3ObjectFieldName), Int(-1));
                        // if (DCNT$ == -1) { ...
                        addStmt(OptIf(condition,
                                endBlock()));
                        // return DCNT$;
                        addStmt(Return(id(defs.depCount_F3ObjectFieldName)));
                    }
                }
            };
            
            smb.build();
        }
        
        //
        // This method constructs an interface for a mixin's DCNT$.
        //
        public void makeMixinDCNT$(final ClassSymbol classSym, final boolean needsBody) {
            MethodBuilder mb = new MethodBuilder(classDCNT$Name(classSym), syms.intType) {
                @Override
                public void initialize() {
                    bodyType = needsBody ? BODY_NORMAL : BODY_NONE;
                    needsReceiver = false;
                }
                
                @Override
                public long rawFlags() {
                    return needsBody ? Flags.PUBLIC : (Flags.PUBLIC | Flags.ABSTRACT);
                }
                
                @Override
                public void statements() {
                    addStmt(Return(id(classDCNT$Name(classSym))));
                }
            };
            
            mb.build();
        }
        
        //
        // This method generates all the mixin DCNT$ for the current class.
        //
        public void makeNeededMixinDCNT$(List<ClassSymbol> mixinClasses) {
            for (ClassSymbol classSym : mixinClasses) {
                makeMixinDCNT$(classSym, true);
            }
        }
        
        //
        // This method constructs the current class's update$ method.
        //
        public void makeUpdateMethod(final boolean useConstants,
                                     final List<VarInfo> varInfos,
                                     final HashMap<F3VarSymbol, HashMap<F3VarSymbol, HashSet<VarInfo>>> updateMap,
                                     final HashMap<Name, Integer> depMap,
                                     final List<ClassSymbol> mixinClasses) {
            MethodBuilder mb = new MethodBuilder(defs.update_F3ObjectMethodName, syms.booleanType) {
                // Number fo dependencies in this class.
                int depCount = depMap.size();
                // Condition expression for each dependent.
                JCExpression[] depCondExpr;
                // Statement lists for each dependent.
                ListBuffer<JCStatement>[] depStats;
                
                @Override
                public void initialize() {
                    addParam(updateInstanceArg());
                    addParam(depNumArg());
                    addParam(startPosArg());
                    addParam(endPosArg());
                    addParam(newLengthArg());
                    addParam(phaseArg());
                    
                    // Construct buffers for each dependency.
                    depCondExpr = new JCExpression[depCount];
                    depStats = new ListBuffer[depCount];
                    for (int i = 0; i < depCount; i++) {
                        depStats[i] = ListBuffer.lb();
                    }
                }
                
                // This method adds a clause to the list of statements for a dependency.
                public void addDepClause(Symbol selector, Symbol sym, JCExpression objCond, JCStatement invalStmt) {
                    Name depName = depName(selector, sym);
                    Integer enumeration = depMap.get(depName);
                    assert enumeration != null : "Missing dependency.";
                    int i = enumeration.intValue();
                    depCondExpr[i] = objCond;
                    depStats[i].append(invalStmt);
                }
                
                // This method constructs an invalidate statement for the given var.
                JCStatement invalidate(boolean isSequence, F3VarSymbol vsym) {
                    if (isSequence) {
                        // Sequence: update$ is only used on select, so, for sequences, we can just pass through
                        return CallStmt(attributeInvalidateName(vsym),
                                startPosArg(),
                                endPosArg(),
                                newLengthArg(),
                                phaseArg());
                    } else {
                        // Non-sequence
                        return CallStmt(attributeInvalidateName(vsym), phaseArg());
                    }
                }

                // This method adds clauses for bound function results.
                public void addBoundFuncResultClauses() {
                    //
                    // If the current class has bound function call expressions in bind call sites,
                    // then we would have introduced Pointer synthetic instance vars to store
                    // bound call result. We need to check if the update$ call is from the Pointer
                    // value change. If so, invalidate appropriate bound function result cache var.
                    // Note that the bound function call may not yet have been called - only after
                    // first call, the Pointer synthetic var has non-null value. So we need to check
                    // check null Pointer value.
                    //
                    for (VarInfo vi : varInfos) {
                        JCStatement ifReferenceStmt = null;
                        if (vi.isInitWithBoundFuncResult()) {
                            F3VarSymbol varSym = vi.getSymbol();
                            Symbol initSym = vi.boundFuncResultInitSym();
                            Name ptrVarName = attributeValueName(initSym);

                            //
                            // For each "foo" field that stores result of a bound function call expression,
                            // we generate pointer dependency update check as follows:
                            //
                            //    if (instance$ == $$$bound$result$foo.getF3Object()) {
                            //        invalidate$foo(phase$);
                            //    }
                            //
                            // instance$ == ptrVar.getF3Object()
                            JCExpression objCond = EQ(updateInstanceArg(), Call(id(ptrVarName), defs.getF3Object_PointerMethodName));
                            // invalidate$foo(phase$);
                            JCStatement invalStat = invalidate(types.isSequence(varSym.type), varSym);
                            // Add statement to dependency.
                            addDepClause(null, initSym, objCond, invalStat);
                        }
                    }
                }
                
                // This method adds clauses for bound function parameters.
                public void addBoundFuncParamClauses() {
                    //
                    // For bound functions, we generate a local class. If the current
                    // class is such a class, we need to invalidate the synthetic
                    // bound function param instance fields from the input F3Object
                    // and varNum pairs.
                    //
                    if (isBoundFuncClass) {
                        MethodSymbol msym = (MethodSymbol) getCurrentClassSymbol().owner;
                        List<VarSymbol> params = msym.params();

                        //
                        // For each bound function param "foo", we generate
                        //
                        //     if (varNum$ == $$boundVarNum$foo && instance$ == $$boundInstance$foo) {
                        //          invalidate$local_klass2$foo(phase$);
                        //          // or sequence version of invalidate..
                        //     }
                        //
                        for (VarSymbol mParam : params) {
                            Scope.Entry e = getCurrentClassSymbol().members().lookup(mParam.name);
                            if (e.sym.kind == Kinds.VAR) {
                                F3VarSymbol param = (F3VarSymbol) e.sym;
                                
                                // instance$ == $$boundInstance$foo
                                JCExpression objCond = EQ(updateInstanceArg(), id(boundFunctionObjectParamName(param.name)));
                                // invalidate$local_klass2$foo(phase$);
                                JCStatement invalStat = invalidate(types.isSequence(param.type), param);
                                // Add statement to dependency.
                                addDepClause(null, param, objCond, invalStat);
                            }
                        }
                    }
                }
                
                // This method adds clauses for inter-instance dependencies.
                public void addInstanceClauses() {
                    // Loop for instance symbol.
                    for (F3VarSymbol instanceVar : updateMap.keySet()) {
                        F3VarSymbol scriptAccess = null;
                        HashMap<F3VarSymbol, HashSet<VarInfo>> instanceMap = updateMap.get(instanceVar);
 
                        // Loop for reference symbol.
                        JCStatement ifReferenceStmt = null;
                        for (F3VarSymbol referenceVar : instanceMap.keySet()) {
                            HashSet<VarInfo> referenceSet = instanceMap.get(referenceVar);
 
                            // Loop for local vars.
                            for (VarInfo varInfo : referenceSet) {
                                if (depGraphWriter != null) {
                                    depGraphWriter.writeInterObjectDependency(instanceVar, referenceVar);
                                }
                                
                                if (referenceVar.isStatic() && !instanceVar.isSpecial()) {
                                    F3ClassSymbol classSym = (F3ClassSymbol)referenceVar.owner;
                                    scriptAccess = f3make.ScriptAccessSymbol(classSym);
                                }

                                // instance == selector
                                JCExpression objCond = EQ(updateInstanceArg(), Get(scriptAccess != null ? scriptAccess : instanceVar));
                                // invalidate$var(phase$);
                                JCStatement invalStat = invalidate(varInfo.generateSequenceAccessors(), varInfo.proxyVarSym());
                                // Add statement to dependency.
                                addDepClause(instanceVar, referenceVar, objCond, invalStat);
                            }
                        }
                    }
                }
            
                @Override
                public void statements() {
                    // Gather clauses for invalidation. 
                    addBoundFuncResultClauses();
                    addBoundFuncParamClauses();
                    addInstanceClauses();
                    
                    // Construct the switch.
                    ListBuffer<JCCase> cases = ListBuffer.lb();
                    for (Name depName : depMap.keySet()) {
                        Integer enumeration = depMap.get(depName);
                        int i = enumeration.intValue();
                        JCStatement caseBody = If(depCondExpr[i],
                                                    Block(depStats[i].toList(),
                                                    Return(True())),
                                                 // else 
                                                    null);
                        JCExpression tag = Int(useConstants ? i : i - depCount);
                        cases.append(m().Case(tag, List.<JCStatement>of(caseBody, m().Break(null))));
                    }

                    // Prepare to accumulate default.
                    beginBlock();
                    
                    // Add mixins to the default chain.
                    if (mixinClasses != null) {
                        for (ClassSymbol classSym : mixinClasses) {
                            // Begin if block.
                            beginBlock();
                            
                            // Call mixin update.
                            callMixin(classSym);
                            
                            // if (depNum$ >= DCNT$mixn) 
                            prependStmt(If(GE(depNumArg(), id(classDCNT$Name(classSym))),
                                           endBlock(),
                                           null));
                        }
                    }
                    
                    if (cases.nonEmpty()) {
                        // Add in super call.
                        callSuper();
                    }
                        
                    // Default statements.
                    List<JCStatement> defaults = endBlockAsList();
                    
                    if (cases.nonEmpty()) {
                        if (!defaults.isEmpty()) {
                            cases.append(m().Case(null, defaults));
                        }
                    
                        JCExpression tagExpr = isMixinClass() && !isScript() ? MINUS(depNumArg(), Call(classDCNT$Name(getCurrentOwner()))) :
                                               useConstants                  ? depNumArg() :
                                                                               MINUS(depNumArg(), id(defs.depCount_F3ObjectFieldName));
                        // Construct and add: switch(depNum - DCNT$) { ... }
                        addStmt(m().Switch(tagExpr, cases.toList()));
                        
                        addStmt(Return(False()));
                    } else if (!defaults.isEmpty()) {
                        addStmts(defaults);
                        
                        if (isMixinClass() || superClassSym == null) {
                            addStmt(Return(False()));
                        } else {
                            callSuper();
                        }
                    } else {
                        buildIf(false);
                    }
                }
            };
            
            mb.build();
        }
        
        //
        // This method constructs the current class's get$ method.
        //
        public void makeGetMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.get_F3ObjectMethodName, syms.objectType,
                                                                 attrInfos, varCount) {
                @Override
                public void statements() {
                    if (!varInfo.isOverride()) {
                        clearDiagPos();
                        // get$var()
                        JCExpression getterExp = Getter(varSym);
                        // return get$var()
                        addStmt(Return(getterExp));
                    }
                }
            };
            
            vcmb.build();
        }
        
        //
        // This method constructs the current class's elem$(varnum, pos) method.
        //
        public void makeGetElementMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.getElement_F3ObjectMethodName, syms.objectType,
                                                                 attrInfos, varCount) {
                @Override
                public void initialize() {
                    addParam(posArg());
                }
                
                @Override
                public void statements() {
                    if (varInfo.useAccessors() && !varInfo.isOverride()) {
                        if (varInfo.generateSequenceAccessors()) {
                            // return elem$var(pos$)
                            addStmt(Return(Call(attributeGetElementName(varSym), posArg())));
                        }
                    }
                }
            };
            
            vcmb.build();
        }
         
        //
        // This method constructs the current class's getAsXXX$ methods.
        //
        public void makeGetAsMethods(List<VarInfo> attrInfos, final int varCount) {
            int typeRepCnt = F3TypeRepresentation.values().length;
            ListBuffer<VarInfo>[] seqVars = new ListBuffer[typeRepCnt];
            boolean hasSequences = false;
            
            // Iterate thru each var.
            for (VarInfo varInfo : attrInfos) {
                // Constrain the var.
                if (varInfo.needsEnumeration() &&
                    !varInfo.isBareSynth() &&
                    varInfo.useAccessors() &&
                    varInfo.generateSequenceAccessors()) {
                    // Element type of sequence.
                    Type elemType = varInfo.getElementType();
                    // Type representation of sequence element type.
                    F3TypeRepresentation typeRep = types.typeRep(elemType);
                    
                    // Only care about primitive types.
                    if (typeRep.isPrimitive()) {
                        //  Ordinal is used as index.
                        int ordinal = typeRep.ordinal();
                        
                        // If this type of sequence has not been encountered before.
                        if (seqVars[ordinal] == null) {
                            // Init the list buffer for this type.
                            seqVars[ordinal] = ListBuffer.lb();
                            // Worth the effort to iterate thru the list later.
                            hasSequences = true;
                        }
                        
                        // Add var to list to include in method generation.
                        seqVars[ordinal].append(varInfo);
                    }
                }
            }
            
            // Worth the effort.
            if (hasSequences) {
                // For each element type.
                for (F3TypeRepresentation typeRep : F3TypeRepresentation.values()) {
                    //  Ordinal is used as index.
                    int ordinal = typeRep.ordinal();
                    
                    // Only include method if a sequence of that type exists.
                    if (seqVars[ordinal] != null) {
                        // All vars of that element type.
                        List<VarInfo> seqVarInfos = seqVars[ordinal].toList();
                        // Use the first as a sample to get the type.
                        final Type elemType = seqVarInfos.get(0).getElementType();
                        
                        // Can use case method with a subset of vars.
                        VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.getAs_F3ObjectMethodName[ordinal], elemType,
                                                                             seqVarInfos, varCount) {
                            @Override
                            public void initialize() {
                                addParam(posArg());
                            }
                            
                            @Override
                            public void statements() {
                                // The vars have already been filtered.
                                addStmt(Return(Call(attributeGetElementName(varSym), posArg())));
                            }
                        };
                        
                        // Build the method.
                        vcmb.build();
                    }
                }
            }
        }
         
        //
        // This method constructs the current class's size$(varnum) method.
        //
        public void makeSizeMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.size_F3ObjectMethodName, syms.intType,
                                                                 attrInfos, varCount) {
                @Override
                public void statements() {
                    if (varInfo.useAccessors() && !varInfo.isOverride()) {
                        if (varInfo.generateSequenceAccessors()) {
                            // return size$var()
                            addStmt(Return(Call(attributeSizeName(varSym))));
                        }
                    }
                }
            };
            
            vcmb.build();
        }
       
        //
        // This method constructs the current class's set$ method.
        //
        public void makeSetMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.set_AttributeMethodPrefixName, syms.voidType,
                                                                 attrInfos, varCount) {
                @Override
                public void initialize() {
                    addParam(objArg());
                 }
                
                @Override
                public void statements() {
                    if (varInfo.useAccessors() && !varInfo.isDef() && !varInfo.isOverride() && !varInfo.isBareSynth()) {
                         // (type)object$
                        JCExpression objCast = typeCast(varInfo.getRealType(), syms.objectType, objArg());
			//System.err.println("typecast: "+ varSym+": "+objCast);
			//System.err.println("type="+varSym.type);
			//System.err.println("type*="+varInfo.getRealType());
                        if (varInfo.generateSequenceAccessors()) {
                            addStmt(CallStmt(defs.Sequences_set, id(names._this), Offset(varSym), objCast));
                        } else {
                            // set$var((type)object$)
                            addStmt(SetterStmt(varSym, objCast));
                        }
                        
                        // return
                        addStmt(Return(null));
                    }
                }
            };
            
            vcmb.build();
        }
       
        //
        // This method constructs the current class's seq$ method.
        //
        public void makeSeqMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.seq_AttributeMethodPrefixName, syms.voidType,
                                                                 attrInfos, varCount) {
                @Override
                public void initialize() {
                    addParam(objArg());
                }
                @Override
                public void statements() {
                    if (varInfo.useAccessors() && !varInfo.isOverride() && !varInfo.isBareSynth() && varInfo.generateSequenceAccessors()) {
                        // (type)object$
                        JCExpression objCast = typeCast(varInfo.getRealType(), syms.objectType, objArg());
                        // $var = value
                        addStmt(SetStmt(proxyVarSym, objCast));
                        // return
                        addStmt(Return(null));
                    }
                }
            };

            vcmb.build();
        }
         
        //
        // This method constructs the current class's invalidate$(varnum, ...) method.
        //
        public void makeInvalidateMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.invalidate_F3ObjectMethodName, syms.voidType,
                                                                 attrInfos, varCount) {
                @Override
                public void initialize() {
                    addParam(startPosArg());
                    addParam(endPosArg());
                    addParam(newLengthArg());
                    addParam(phaseArg());
                }
                
                @Override
                public void statements() {
                    if (varInfo.useAccessors() && !varInfo.isOverride() && needInvalidateAccessorMethod(varInfo)) {
                        if (varInfo.generateSequenceAccessors()) {
                            addStmt(CallStmt(attributeInvalidateName(varSym),
                                    startPosArg(), endPosArg(), newLengthArg(), phaseArg()));
                        } else {
                            addStmt(CallStmt(attributeInvalidateName(varSym), phaseArg()));
                        }
                        
                        addStmt(Return(null));
                    }
                }
            };
            
            vcmb.build();
        }
        
        //
        // This method constructs the current class's varChangeBits$(varnum, ...) method.
        //
        public void makeVarChangeBitsMethod(List<VarInfo> attrInfos, int varCount) {
            VarCaseMethodBuilder vcmb = new VarCaseMethodBuilder(defs.varFlagActionChange, syms.intType,
                                                                 attrInfos, varCount) {
                @Override
                public void initialize() {
                    addParam(clearBitsArg());
                    addParam(setBitsArg());
                }
                
                @Override
                public void statements() {
                    if (varInfo.needsCloning()) {
                        JCExpression clearBits = BITAND(VarFlags(varSym), BITNOT(clearBitsArg()));
                        JCExpression setBits = BITOR(clearBits, setBitsArg());
                        JCExpression assignBits = m().Assign(VarFlags(varSym), flagCast(setBits));
                        addStmt(Return(assignBits));
                    }
                }
            };
            
            vcmb.build();
        }

        //
        // This method constructs the initializer for a var map.
        //
        public JCExpression makeInitVarMapExpression(ClassSymbol cSym, LiteralInitVarMap varMap) {
            // Build up the argument list for the call.
            ListBuffer<JCExpression> args = ListBuffer.lb();
            // X.VCNT$()
            args.append(Call(makeType(types.erasure(cSym.type)), defs.count_F3ObjectFieldName));

            // For each var declared in order (to make the switch tags align to the vars.)
            for (F3VarSymbol vSym : varMap.varList.toList()) {
                // ..., X.VOFF$x, ...
		if (vSym.useAccessors()) {
		    args.append(Select(makeType(types.erasure(cSym.type)), attributeOffsetName(vSym)));
		}
            }
	    //System.err.println("init var map: "+ args.toList());
            // F3Base.makeInitMap$(X.VCNT$(), X.VOFF$a, ...)
            return Call(defs.F3Base_makeInitMap, args);
        }

        //
        // This method constructs a single var map declaration.
        //
        private JCVariableDecl makeInitVarMapDecl(ClassSymbol cSym, LiteralInitVarMap varMap) {
            // Fetch name of map.
            Name mapName = varMapName(cSym);
            // static short[] Map$X;
            return makeField(Flags.STATIC, syms.f3_ShortArray, mapName, null);
        }

        //
        // This method constructs a single var map initial value.
        //
        public JCStatement makeInitVarMapInit(LiteralInitVarMap varMap) {
            // Get current class symbol.
            ClassSymbol cSym = getCurrentClassSymbol();
            // Fetch name of map.
            Name mapName = varMapName(cSym);
            // Map$X = F3Base.makeInitMap$(X.VCNT$(), X.VOFF$a, ...);
            return Stmt(m().Assign(id(mapName), makeInitVarMapExpression(cSym, varMap)));
        }

        //
        // This method constructs declarations for var maps used by literal initializers.
        //
        public void makeInitClassMaps(LiteralInitClassMap initClassMap) {
            // For each class initialized in the current class.
            for (ClassSymbol cSym : initClassMap.classMap.keySet()) {
                // Get the var map for the referencing class.
                LiteralInitVarMap varMap = initClassMap.classMap.get(cSym);
                // Add to var.
                addDefinition(makeInitVarMapDecl(cSym, varMap));

                // Fetch name of map.
                Name mapName = varMapName(cSym);
                // Prepare to accumulate statements.
                ListBuffer<JCStatement> stmts = ListBuffer.lb();

                if (isAnonClass(cSym)) {
                    // Construct and add: return MAP$X;
                    stmts.append(Return(id(mapName)));
                } else {
                    // MAP$X == null
                    JCExpression condition = EQnull(id(mapName));
                    // MAP$X = F3Base.makeInitMap$(X.VCNT$, X.VOFF$a, ...)
                    JCExpression assignExpr = m().Assign(id(mapName), makeInitVarMapExpression(cSym, varMap));
                    // Construct and add: return MAP$X == null ? (MAP$X = F3Base.makeInitMap$(X.VCNT$, X.VOFF$a, ...)) : MAP$X;
                    stmts.append(
                        Return(
                            If (condition,
                                assignExpr,
                                id(mapName))));
                }
                
                // Construct the method symbol.
                MethodSymbol methSym = makeMethodSymbol(Flags.PUBLIC | Flags.STATIC,
                                                        syms.f3_ShortArray,
                                                        varGetMapName(cSym),
                                                        List.<Type>nil());

                // Construct lazy accessor method.
                JCMethodDecl method = Method(Flags.PUBLIC | Flags.STATIC,
                                                 syms.f3_ShortArray,
                                                 varGetMapName(cSym),
                                                 List.<JCVariableDecl>nil(),
                                                 stmts.toList(), 
                                                 methSym);
                                                 
                // Add method.
                addDefinition(method);
            }
        }

        private JCExpression defaultValue(VarInfo varInfo) {
            return makeDefaultValue(varInfo.pos(), varInfo.getSymbol());
        }

        //
        // This method constructs a super call with appropriate arguments.
        //
        private JCStatement makeSuperCall(ClassSymbol cSym, Name name, JCExpression... args) {
            ListBuffer<JCExpression> buffer = ListBuffer.lb();
            
            for (JCExpression arg : args) {
                buffer.append(arg);
            }
            
            return makeSuperCall(cSym, name, buffer.toList());
        }
        private JCStatement makeSuperCall(ClassSymbol cSym, Name name, List<JCExpression> args) {
            // If this is from a mixin class then we need to use receiver$ otherwise this.
            boolean fromMixinClass = isMixinClass();
            // If this is to a mixin class then we need to use receiver$ otherwise this.
            boolean toMixinClass = F3AnalyzeClass.isMixinClass(cSym);
            // If this class doesn't have a f3 super then punt to F3Base.
            boolean toF3Base = cSym == null;

            // Add in the receiver if necessary.
            if (toMixinClass || toF3Base) {
                // Determine the receiver name.
                Name receiver = fromMixinClass ? defs.receiverName : names._this;
                args.prepend(id(receiver));
            }

            // Determine the selector.
            JCExpression selector;
            if (toMixinClass) {
                selector = makeType(types.erasure(cSym.type), false);
            } else if (toF3Base) {
                selector = makeType(types.erasure(syms.f3_BaseType), false);
            } else {
                selector = id(names._super);
            }

            // Construct the call.
            return CallStmt(selector, name, args);
        }

        //
        // Construct the static block for setting defaults
        //
        public void makeInitStaticAttributesBlock(ClassSymbol sym, boolean isScriptLevel, boolean isLibrary, List<VarInfo> attrInfo, JCStatement initMap) {
            // Buffer for init statements.
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
    
            // Initialize the var map for anon class.
            if (initMap != null) {
                stmts.append(initMap);
            }

            Symbol scriptLevelAccessSym = f3make.ScriptAccessSymbol(sym);
            
            if (isScriptLevel) {
                stmts.append(Stmt(m().Assign(id(scriptLevelAccessSym),
                                             m().NewClass(null, null, id(scriptName), List.<JCExpression>of(False()), null))));
                stmts.append(CallStmt(id(scriptLevelAccessSym), defs.initialize_F3ObjectMethodName, False()));
            }
            
            if (isLibrary) {
                stmts.append(CallStmt(id(scriptLevelAccessSym), defs.applyDefaults_F3ObjectMethodName));
            }
             
            addDefinition(m().Block(Flags.STATIC, stmts.toList()));
        }

        //
        // This method generates the code for a userInit or postInit method.
        public void makeInitMethod(Name methName, ListBuffer<JCStatement> translatedInitBlocks, List<ClassSymbol> immediateMixinClasses) {
            ClassSymbol superClassSym = analysis.getF3SuperClassSym();
           
            // Only create method if necessary (rely on F3Base.)
            if (translatedInitBlocks.nonEmpty() || immediateMixinClasses.nonEmpty() || isMixinClass()) {
                List<JCVariableDecl> receiverVarDeclList;
                MethodSymbol methSym;
                ListBuffer<JCStatement> stmts = ListBuffer.lb();
    
                // Mixin super calls will be handled when inserted into real classes.
                if (!isMixinClass()) {
                    receiverVarDeclList = List.<JCVariableDecl>nil();
    
                    if (superClassSym != null) {
                        stmts.append(CallStmt(id(names._super), methName));
                    }
    
                    for (ClassSymbol mixinClassSym : immediateMixinClasses) {
			Type memberType = analysis.getType(mixinClassSym);
			//memberType = types.erasure(memberType);
                        JCExpression selector = makeType(types.erasure(memberType), false);
                        stmts.append(CallStmt(selector, methName,  id(names._this)));
					      //m().TypeCast(makeType(memberType), id(names._this))));
			//System.err.println("stmts="+stmts.toList());
                        //JCExpression selector = makeType(types.erasure(mixinClassSym.type), false);
                        //stmts.append(CallStmt(selector, methName,  m().TypeCast(makeType(mixinClassSym), id(names._this))));
                    }
                    
                    methSym = makeMethodSymbol(rawFlags(), syms.voidType, methName, List.<Type>nil());
                } else {
                    receiverVarDeclList = List.<JCVariableDecl>of(ReceiverParam(getCurrentClassDecl()));
                    methSym = makeMethodSymbol(rawFlags(), syms.voidType, methName, List.<Type>of(getCurrentClassSymbol().type));
                }
    
                stmts.appendList(translatedInitBlocks);
                addDefinition(Method(rawFlags(),
                                         syms.voidType,
                                         methName,
                                         receiverVarDeclList,
                                         stmts.toList(),
                                         methSym));
            }
        }

        private void makeConstructor(List<JCVariableDecl> params, List<Type> types, List<JCStatement> cStats) {
            addDefinition(Method(Flags.PUBLIC,
                          syms.voidType,
                          names.init,
                          params,
                          cStats,
                          makeMethodSymbol(Flags.PUBLIC, syms.voidType, names.init, types)));
    
        }
        
        //
        // Make a constructor to be called by Java code.
        // Simply pass up to super, unless this is the last F3 class, in which case add object initialization
        //
        public void makeJavaEntryConstructor() {
            //    public Foo() {
            //        this(false);
            //        initialize$(true);
            //    }
            makeConstructor(List.<JCVariableDecl>nil(), List.<Type>nil(),
                Stmts(
                    CallStmt(names._this, False()),
                    CallStmt(defs.initialize_F3ObjectMethodName, isScript() || isAnonClass() ? False() : True())
                )
            );
        }

        //
        // Make a constructor to be called by F3 code.
        //
        public void makeF3EntryConstructor(List<VarInfo> varInfos, ClassSymbol outerTypeSym) {
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            Name dummyParamName = names.fromString("dummy");
    
            // call the F3 version of the constructor in the superclass
            //    public Foo(boolean dummy) {
            //        super(dummy);
            //    }
            if (analysis.getF3SuperClassSym() != null || isScript()) {
                Symbol outerSuper = outerTypeSymbol(types.supertype(getCurrentClassDecl().type).tsym);
                if (outerSuper == null) {
                    stmts.append(CallStmt(names._super, id(dummyParamName)));
                }
                else {
                    stmts.append(CallStmt(names._super, resolveThis(outerSuper, false), id(dummyParamName)));
                }
            }
            
            if (!analysis.isFirstTierNoMixins()) {
                if (needsVCNT$()) {
                    stmts.append(CallStmt(defs.count_F3ObjectFieldName));
                }
                
                if (needsDCNT$()) {
                    stmts.append(CallStmt(defs.depCount_F3ObjectFieldName));
                }
                
                if (needsFCNT$()) {
                    stmts.append(CallStmt(defs.funcCount_F3ObjectFieldName));
                }
            }

            // Update any local flag changes.
            for (VarInfo ai : varInfos) {
                if (ai.needsCloning() && ai.isOverride()) {
                    Name flagName = attributeFlagsName(ai.proxyVarSym());
                    JCExpression update = updateVarBits(ai, id(flagName));
                    
                    if (update != null) {
                        stmts.append(Stmt(m().Assign(id(flagName), update)));
                    }
                }
            }
    
            // Construct the parameters
            ListBuffer<JCVariableDecl> params = ListBuffer.lb();
            ListBuffer<Type> types = ListBuffer.lb();
            if (outerTypeSym != null) {
                // add a parameter and a statement to constructor for the outer instance reference
                params.append(Param(outerTypeSym.type, defs.outerAccessor_F3ObjectFieldName) );
                types.append(outerTypeSym.type);
                JCExpression cSelect = Select(id(names._this), defs.outerAccessor_F3ObjectFieldName);
                stmts.append(Stmt(m().Assign(cSelect, id(defs.outerAccessor_F3ObjectFieldName))));
            }
            params.append(Param(syms.booleanType, dummyParamName));
            types.append(syms.booleanType);
    
            makeConstructor(params.toList(), types.toList(), stmts.toList());
        }
    

        //
        // Make the field for accessing the outer members
        //
        public void makeOuterAccessorField(ClassSymbol outerTypeSym) {
            // Create the field to store the outer instance reference
            addDefinition(makeField(Flags.PUBLIC, outerTypeSym.type, defs.outerAccessor_F3ObjectFieldName, null));
        }
    
        //
        // Make the method for accessing the outer members
        //
        public void makeOuterAccessorMethod(ClassSymbol outerTypeSym) {
            ListBuffer<JCStatement> stmts = ListBuffer.lb();

            F3VarSymbol vs = new F3VarSymbol(types, names,Flags.PUBLIC, defs.outerAccessor_F3ObjectFieldName, outerTypeSym.type, getCurrentClassSymbol());
            stmts.append(Return(id(vs)));
            MethodSymbol methSym = makeMethodSymbol(Flags.PUBLIC, outerTypeSym.type, defs.outerAccessor_MethodName, List.<Type>nil());
            addDefinition(Method(Flags.PUBLIC, outerTypeSym.type, defs.outerAccessor_MethodName, List.<JCVariableDecl>nil(), stmts.toList(), methSym));
        }
        
        // 
        // Test to see if a name is a var accessor function.
        //
        private boolean isVarAccessor(Name name) {
            return name.startsWith(defs.get_AttributeMethodPrefixName) ||
                   name.startsWith(defs.set_AttributeMethodPrefixName) ||
                   name.startsWith(defs.seq_AttributeMethodPrefixName) ||
                   name.startsWith(defs.invalidate_F3ObjectMethodName) ||
                   name.startsWith(defs.onReplaceAttributeMethodPrefixName) ||
                   name.startsWith(defs.getElement_F3ObjectMethodName) ||
                   name.startsWith(defs.size_F3ObjectMethodName) ||
                   name.startsWith(defs.applyDefaults_F3ObjectMethodName) ||
                   name.startsWith(defs.count_F3ObjectMethodName) ||
                   name.startsWith(defs.getFlags_F3ObjectMethodName) ||
                   name.startsWith(defs.setFlags_F3ObjectMethodName);
        }
        
        //
        // Make a method from a MethodSymbol and an optional method body.
        // Make a bound version if "isBound" is set.
        //
        private void appendMethodClones(final MethodSymbol methSym, final int bodyType) {
            final boolean isBound = (methSym.flags() & F3Flags.BOUND) != 0;
            final boolean isStatic = methSym.isStatic();
            final Name functionName = functionName(methSym, false, isBound);
            
            List<VarSymbol> parameters = methSym.getParameters();
            ListBuffer<JCStatement> stmts = null;
            ListBuffer<JCVariableDecl> params = ListBuffer.lb();
            ListBuffer<JCExpression> args = ListBuffer.lb();
            ListBuffer<Type> argTypes = ListBuffer.lb();
            
            boolean isProxy = isStatic &&
                              !parameters.isEmpty() &&
                              parameters.get(0).type == methSym.owner.type &&
                              isVarAccessor(methSym.name);
            
            if (!isStatic || isProxy) {
                args.append(id(names._this));
            }
            
            boolean skipFirst = isProxy;
            for (VarSymbol vsym : parameters) {
                if (!skipFirst) {
                   args.append(id(vsym.name));
                   params.append(Param(vsym.type, vsym.name));
                   argTypes.append(vsym.type);
                }
                
                skipFirst = false;
            }

            if (bodyType != BODY_NONE) {
                stmts = ListBuffer.lb();
                
                Name callName = functionName(methSym, !isStatic, isBound);
                JCExpression receiver = makeType(types.erasure(methSym.owner.type), false);
                
                if (methSym.getReturnType() == syms.voidType) {
                    stmts.append(CallStmt(receiver, callName, args));
                } else {
                    stmts.append(Return(Call(receiver, callName, args)));
                }
            }
            
            long flags = bodyType != BODY_NONE ? Flags.PUBLIC : (Flags.PUBLIC | Flags.ABSTRACT);
            JCModifiers mods = m().Modifiers(flags);
            
            if (isCurrentClassSymbol(methSym.owner))
                mods = addAccessAnnotationModifiers(diagPos, methSym.flags(), mods);
            else
                mods = addInheritedAnnotationModifiers(diagPos, methSym.flags(), mods);
                
            Type returnType = isBound? syms.f3_PointerTypeErasure : methSym.getReturnType();
            
            addDefinition(Method(
                          mods,
                          returnType,
                          functionName,
                          params.toList(),
                          bodyType != BODY_NONE ? stmts.toList() : null,
                          makeMethodSymbol(mods.flags, returnType, functionName, methSym.owner, argTypes.toList())));
                          
            if (bodyType != BODY_NONE) {
                optStat.recordProxyMethod();
            }
        }
    
    
        //
        // Add proxies which redirect to the static implementation for every concrete method
        //
        public void makeFunctionProxyMethods(List<MethodSymbol> needDispatch) {
            for (MethodSymbol sym : needDispatch) {
                appendMethodClones(sym, BODY_NORMAL);
            }
        }

        //
        // Add interface declarations for declared methods.
        //
        public void makeFunctionInterfaceMethods() {
            for (F3Tree def : getCurrentClassDecl().getMembers()) {
                if (def.getF3Tag() == F3Tag.FUNCTION_DEF) {
                    F3FunctionDefinition func = (F3FunctionDefinition) def;
                    MethodSymbol sym = func.sym;
                    
                    if ((sym.flags() & (Flags.SYNTHETIC | Flags.STATIC | Flags.PRIVATE)) == 0) {
                        appendMethodClones(sym, BODY_NONE);
                    }
                }
            }
        }

        //
        // This method constructs a script class.
        //
        public void makeScript(List<Type> typarams, List<JCTree> definitions) {
            long flags = Flags.PUBLIC | Flags.STATIC;
            JCModifiers classMods = m().Modifiers(flags);
            classMods = addAccessAnnotationModifiers(diagPos, flags, classMods);
            JCClassDecl script = m().ClassDef(
                    classMods,
                    scriptName,
                    translateTypeParams(null, typarams),
                    makeType((syms.f3_BaseType)),
                    List.of(makeType(syms.f3_ObjectType)),
                    definitions);
            script.sym = scriptClassSymbol;
        
            membersToSymbol(script);

            addDefinition(script);
        }
                
        //
        // Methods for accessing the outer members.
        //
        public void makeOuterAccessorInterfaceMembers() {
            ClassSymbol cSym = getCurrentClassSymbol();
            
            if (cSym != null && toJava.getHasOuters().containsKey(cSym)) {
                Symbol typeOwner = cSym.owner;
                
                while (typeOwner != null && typeOwner.kind != Kinds.TYP) {
                    typeOwner = typeOwner.owner;
                }
    
                if (typeOwner != null) {
                    ClassSymbol returnSym = reader.enterClass(names.fromString(typeOwner.type.toString() + mixinClassSuffix));
                    JCMethodDecl accessorMethod = Method(
                            Flags.PUBLIC,
                            returnSym.type,
                            defs.outerAccessor_MethodName,
                            List.<JCVariableDecl>nil(),
                            null,
                            makeMethodSymbol(Flags.PUBLIC, returnSym.type, defs.outerAccessor_MethodName, List.<Type>nil()));
                    addDefinition(accessorMethod);
                    optStat.recordProxyMethod();
                }
            }
        }

        //
        // Add definitions to class to access the script-level sole instance.
        //
        public void makeScriptLevelAccess(ClassSymbol sym, boolean scriptLevel) {
            if (!scriptLevel) {
                Symbol scriptLevelAccessSym = f3make.ScriptAccessSymbol(sym);
                addDefinition(makeField(scriptLevelAccessSym.flags_field & ~Flags.FINAL, scriptLevelAccessSym.type, scriptLevelAccessSym.name, null));
            }
        }
    }
}
