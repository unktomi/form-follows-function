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

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.tools.FileObject;

import org.f3.api.F3BindStatus;
import org.f3.api.tree.SyntheticTree.SynthType;
import org.f3.api.tree.TypeTree;
import org.f3.api.tree.TypeTree.Cardinality;
import com.sun.tools.mjavac.code.Flags;
import static com.sun.tools.mjavac.code.Flags.*;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.Name.Table;
import org.f3.tools.code.F3Flags;
import static org.f3.tools.code.F3Flags.SCRIPT_LEVEL_SYNTH_STATIC;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.tree.*;
import org.f3.tools.util.MsgSym;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class F3ScriptClassBuilder {
    protected static final Context.Key<F3ScriptClassBuilder> f3ModuleBuilderKey =
        new Context.Key<F3ScriptClassBuilder>();

    private final F3Defs defs;
    private Table names;
    private F3TreeMaker f3make;
    private JCDiagnostic.Factory diags;
    private Log log;
    private F3Symtab syms;
    private Set<Name> reservedTopLevelNamesSet;
    private Name pseudoSourceFile;
    private Name pseudoFile;
    private Name pseudoDir;
    private Name pseudoProfile;
    private Name defaultRunArgName;

    public boolean scriptingMode;
    
    private static final boolean debugBadPositions = Boolean.getBoolean("F3ModuleBuilder.debugBadPositions");

    public static F3ScriptClassBuilder instance(Context context) {
        F3ScriptClassBuilder instance = context.get(f3ModuleBuilderKey);
        if (instance == null)
            instance = new F3ScriptClassBuilder(context);
        return instance;
    }

    protected F3ScriptClassBuilder(Context context) {
        context.put(f3ModuleBuilderKey, this);
        defs = F3Defs.instance(context);
        names = Table.instance(context);
        f3make = (F3TreeMaker)F3TreeMaker.instance(context);
        diags = JCDiagnostic.Factory.instance(context);
        log = Log.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        pseudoSourceFile = names.fromString("__SOURCE_FILE__");
        pseudoFile = names.fromString("__FILE__");
        pseudoDir = names.fromString("__DIR__");
        pseudoProfile = names.fromString("__PROFILE__");
        defaultRunArgName = names.fromString("_$UNUSED$_$ARGS$_");
    }

    public void convertAccessFlags(F3Script script) {
        new F3TreeScanner() {
            
            void convertFlags(F3Modifiers mods) {
                long flags = mods.flags;
                long access = flags & (Flags.AccessFlags | F3Flags.PACKAGE_ACCESS);
                if (access == 0L) {
                    flags |= F3Flags.SCRIPT_PRIVATE;
                }
                mods.flags = flags;
            }

            @Override
            public void visitClassDeclaration(F3ClassDeclaration tree) {
                super.visitClassDeclaration(tree);
                convertFlags(tree.getModifiers());
            }

            @Override
            public void visitFunctionDefinition(F3FunctionDefinition tree) {
                super.visitFunctionDefinition(tree);
                convertFlags(tree.getModifiers());
            }

            @Override
            public void visitVar(F3Var tree) {
                super.visitVar(tree);
                convertFlags(tree.getModifiers());
            }
        }.scan(script);
    }
    
    private void checkAndNormalizeUserRunFunction(F3FunctionDefinition runFunc) {
        F3FunctionValue fval = runFunc.operation;
        List<F3Var> params = fval.funParams;
        switch (params.size()) {
            case 0: {
                // no parameter specified, fill it in
                fval.funParams = makeRunFunctionArgs(defaultRunArgName);
                break;
            }
            case 1: {
                F3Type paramType = params.head.getF3Type();
                if (paramType.getCardinality() == Cardinality.ANY &&
                        paramType instanceof F3TypeClass) {
                    F3Expression cnExp = ((F3TypeClass) paramType).getClassName();
                    if (cnExp instanceof F3Ident) {
                        Name cName = ((F3Ident)cnExp).getName();
                        if (cName == syms.stringTypeName) {
                            break;
                        }
                    }
                }
                // not well-formed, fall-through
            }
            default: {
                // bad arguments
                log.error(runFunc.pos(), MsgSym.MESSAGE_F3_RUN_FUNCTION_PARAM);
                fval.funParams = makeRunFunctionArgs(defaultRunArgName);
            }
        }
        
        //TODO: check specified return type
        
        // set return type
        fval.rettype = makeRunFunctionType();
    }


    public F3ClassDeclaration preProcessF3TopLevel(F3Script module) {
        Name moduleClassName = scriptName(module);
        
        if (debugBadPositions) {
            checkForBadPositions(module);
        }

        if (scriptingMode && module.pid != null)
            log.error(module.pos(), MsgSym.MESSAGE_F3_PACKAGE_IN_SCRIPT_EVAL_MODE);

        // check for references to pseudo variables and if found, declare them
        class PseudoIdentScanner extends F3TreeScanner {
            public boolean usesSourceFile;
            public boolean usesFile;
            public boolean usesDir;
            public boolean usesProfile;
            public DiagnosticPosition diagPos;
            @Override
            public void visitIdent(F3Ident id) {
                super.visitIdent(id);
                if (id.getName().equals(pseudoSourceFile)) {
                    usesSourceFile = true;
                    markPosition(id);
                }
                if (id.getName().equals(pseudoFile)) {
                    usesFile = true;
                    markPosition(id);
                }
                if (id.getName().equals(pseudoDir)) {
                    usesDir = true;
                    markPosition(id);
                }
                if (id.getName().equals(pseudoProfile)) {
                    usesProfile = true;
                    markPosition(id);
		}
            }
            void markPosition(F3Tree tree) {
                if (diagPos == null) { // want the first only
                    diagPos = tree.pos();
                }
            }
        }
        PseudoIdentScanner pseudoScanner = new PseudoIdentScanner();
        pseudoScanner.scan(module.defs);
        //debugPositions(module);

        ListBuffer<F3Tree> scriptTops = ListBuffer.<F3Tree>lb();
        final List<F3Tree> pseudoVars = pseudoVariables(module.pos(), moduleClassName, module,
                pseudoScanner.usesSourceFile, pseudoScanner.usesFile, pseudoScanner.usesDir, pseudoScanner.usesProfile);
        scriptTops.appendList(pseudoVars);
        scriptTops.appendList(module.defs);
        
        // Determine if this is a library script
        boolean externalAccessFound = false;
        F3FunctionDefinition userRunFunction = null;
        final long EXTERNALIZING_FLAGS = Flags.PUBLIC | Flags.PROTECTED | F3Flags.PACKAGE_ACCESS | F3Flags.PUBLIC_READ | F3Flags.PUBLIC_INIT;
        for (F3Tree tree : scriptTops) {

            // Protect against erroneous scripts being attributed by IDE plugin
            //
            if (tree == null ) continue;

            switch (tree.getF3Tag()) {
                case CLASS_DEF: {
                    F3ClassDeclaration decl = (F3ClassDeclaration) tree;
                    if ((decl.getModifiers().flags & EXTERNALIZING_FLAGS) != 0) {
                        externalAccessFound = true;
                    }
                    break;
                }
                case FUNCTION_DEF: {
                    F3FunctionDefinition decl =
                            (F3FunctionDefinition) tree;
                    Name name = decl.name;
                    if (name == defs.userRunFunctionName) {
                        if (userRunFunction == null) {
                            checkAndNormalizeUserRunFunction(decl);
                            userRunFunction = decl;
                        } else {
                            log.error(decl.pos(), MsgSym.MESSAGE_F3_RUN_FUNCTION_SINGLE);
                        }
                    }
                    if ((decl.getModifiers().flags & EXTERNALIZING_FLAGS) != 0) {
                        externalAccessFound = true;
                    }
                    break;
                }
                case VAR_DEF: { 
                    F3Var decl = (F3Var) tree;
                    if ((decl.getModifiers().flags & EXTERNALIZING_FLAGS) != 0) {
                        externalAccessFound = true;
                    }
                    break;
                }
            }
        }
        final boolean isLibrary = externalAccessFound || (userRunFunction != null);
        module.isLibrary = isLibrary;
        ListBuffer<F3Tree> scriptClassDefs = new ListBuffer<F3Tree>();
        ListBuffer<F3Expression> stats = new ListBuffer<F3Expression>();
        F3Expression value = null;
       
        // Divide module defs between internsl run function body, Java compilation unit, and module class
        ListBuffer<F3Tree> topLevelDefs = new ListBuffer<F3Tree>();
        F3ClassDeclaration moduleClass = null;
        boolean looseExpressionsSeen = false;
        for (F3Tree tree : scriptTops) {
            
            // Protect against errneous script trees being attributed by
            // IDE plugins.
            //
            if (tree == null) continue;
            if (value != null) {
                stats.append(value);
                value = null;
            }
            switch (tree.getF3Tag()) {
                case IMPORT:
                    topLevelDefs.append(tree);
                    break;
                case CLASS_DEF: {
                    F3ClassDeclaration decl = (F3ClassDeclaration) tree;
                    Name name = decl.getName();
                    checkName(tree.pos, name);
                    if (name == moduleClassName) {
                        moduleClass = decl;
                        // script-class added to topLevelDefs below
                    } else {
                        // classes other than the script-class become nested static classes
                        decl.mods.flags |= STATIC | SCRIPT_LEVEL_SYNTH_STATIC;
                        scriptClassDefs.append(tree);
                    }
                    break;
                }
                case FUNCTION_DEF: {
                    // turn script-level functions into script-class static functions
                    F3FunctionDefinition decl = (F3FunctionDefinition) tree;
                    decl.mods.flags |= STATIC | SCRIPT_LEVEL_SYNTH_STATIC;
                    Name name = decl.name;
                    checkName(tree.pos, name);
                    // User run function isn't used directly.
                    // Guts will be added to internal run function.
                    // Other functions added to the script-class
                    if (name != defs.userRunFunctionName) {
                        scriptClassDefs.append(tree);
                    }
                    break;
                }
                case VAR_DEF: {
                    // turn script-level variables into script-class static variables
                    F3Var decl = (F3Var) tree;
                    if ( (decl.mods.flags & SCRIPT_LEVEL_SYNTH_STATIC) == 0) {
                        // if this wasn't already created as a synthetic
                        checkName(tree.pos, decl.getName());
                    }
                    decl.mods.flags |= STATIC | SCRIPT_LEVEL_SYNTH_STATIC;
                    scriptClassDefs.append(decl);  // declare variable as a static in the script class
                    if (!isLibrary) {
                        // This is a simple-form script where the main-code is just loose at the script-level.
                        // The main-code will go into the run method.  The variable initializations should
                        // be in-place inline.   Place the variable initialization in 'value' so that
                        // it will wind up in the code of the run method.
                        value = f3make.VarInit(decl);
                    }
                    break;
                }
                default: {
		    if (tree instanceof F3TypeAlias) {
			value = (F3Expression) tree;
		    } else {
			// loose expressions, if allowed, get added to the statements/value
			if (isLibrary && !looseExpressionsSeen) {
			    JCDiagnostic reason = externalAccessFound ?
				diags.fragment(MsgSym.MESSAGE_F3_LOOSE_IN_LIB) :
				diags.fragment(MsgSym.MESSAGE_F3_LOOSE_IN_RUN);
			    log.error(tree.pos(), MsgSym.MESSAGE_F3_LOOSE_EXPRESSIONS, reason);
			}
			looseExpressionsSeen = true;
			value = (F3Expression) tree;
		    }
                    break;

                }
            }
        }
        
        {
            // Create the internal run function, take as much as
            // possible from the user run function (if it exists)
            //
            // If there was no user supplied run function then we mark the
            // funcitonas synthetic and make sense of the start and endpos
            // for the node. If the user supplied a run function, then
            // we use the information it gives us and neither flag it as
            // synthetic nor change the node postions.
            //
            SynthType               sType                   = SynthType.SYNTHETIC;
            F3FunctionDefinition   internalRunFunction     = null;

            Name commandLineArgs = defaultRunArgName;
            if (userRunFunction != null) {

                List<F3Var> params = userRunFunction.operation.getParams();

                // Protect IDE plugin against partially typed run function
                // returning null for the parameters, statements or body, by
                // null checking for each of those elements.
                //
                if (params != null && params.size() == 1) {
                    commandLineArgs = params.head.getName();
                }
                // a run function was specified, start the statement, protecting
                // against IDE generated errors.
                //
                F3Block body = userRunFunction.getBodyExpression();
                if (body != null) {

                    int sSize = 0;
                    List<F3Expression> statements = body.getStmts();

                    if (statements != null) {
                        sSize = statements.size();
                    }
                    if (sSize > 0 || body.getValue() != null) {

                        if (value != null) {
                            stats.append(value);
                        }

                        if (sSize > 0) {
                            stats.appendList(body.getStmts());
                        }

                        if (body.getValue() != null) {
                            value = body.getValue();
                        }
                    }
                }
            }
            // If there is a user supplied run function, use content and position from it.
            // Otherwise, unless this is a pure library, create a run function from the loose expressions, 
            // with no position.
            if (userRunFunction != null || !isLibrary || looseExpressionsSeen) {
                internalRunFunction = makeInternalRunFunction(module, commandLineArgs, userRunFunction, stats.toList(), value);
                scriptClassDefs.prepend(internalRunFunction);
                module.isRunnable = true;
            }
        }

        if (moduleClass == null) {

            // Synthesize a Main class definition and flag it as
            // such.
            //
            F3Modifiers cMods = f3make.Modifiers(PUBLIC);
            cMods.setGenType(SynthType.SYNTHETIC);
            moduleClass = f3make.ClassDeclaration(
                    cMods, //public access needed for applet initialization
                    moduleClassName,
                    List.<F3Expression>nil(), // no supertypes
                    scriptClassDefs.toList());
            moduleClass.setGenType(SynthType.SYNTHETIC);
            moduleClass.setPos(module.getStartPosition());
        } else {
            moduleClass.setMembers(scriptClassDefs.appendList(moduleClass.getMembers()).toList());
        }
        
        // Check endpos for IDE
        //
        setEndPos(module, moduleClass, module);
        
        moduleClass.isScriptClass   = true;
        if (scriptingMode)
            moduleClass.setScriptingModeScript();
        moduleClass.runMethod       = userRunFunction;
        topLevelDefs.append(moduleClass);

        module.defs = topLevelDefs.toList();

        // Sort the list into startPosition order for IDEs
        //
        
        ArrayList<F3Tree> sortL = new ArrayList<F3Tree>(moduleClass.getMembers());
        Collections.sort(sortL, new Comparator<F3Tree>() {
            public int compare(F3Tree t1, F3Tree t2) {
                if (pseudoVars.contains(t1)) {
                    return -1;
                } else if (pseudoVars.contains(t2)) {
                    return +1;
                } else {
                    return t1.getStartPosition() - t2.getStartPosition();
                }
            }
        });

/***** This is part of the fix for VSGC-3416.  But, it causes incorrect compile time 
       errors in the ShoppingService sample so we disable this.
       The error msgs in functional/should-fail/AccessModifiersTest.f3.EXPECTED
       are sensitive to this so if you fix the problem, you will have to fix that
       file too.
        // This a hokey way to do this, but we are using mjavac.util.List and it doesn't
        // support much. Fortunately, there won't be thousands of entries in the member lists
        //
        scriptClassDefs.clear();
        for (F3Tree e : sortL) {
            scriptClassDefs.append(e);
        }
        moduleClass.setMembers(scriptClassDefs.toList());
*****/
        convertAccessFlags(module);

        reservedTopLevelNamesSet = null;
        return moduleClass;
    }
    
    /**
     * Helper method that checks to see if we can/need to record the correct end
     * position for any synthesized nodes, based upon the end position of some
     * supplied node that makes sense to use the end position of.
     * 
     * @param module The top level script node
     * @param built  The AST we are synthesizing
     * @param copy   The AST we are copying information from
     */
    protected void setEndPos(final F3Script module, final F3Tree build, final F3Tree copy)
    {
        // We can only calculate end position spans if we have an
        // end position map, for debugging, or for the IDE.
        //
        if  (module.endPositions != null) {
            module.endPositions.put(build, copy.getEndPosition(module.endPositions));
        }
    }

    private void debugPositions(final F3Script module) {
        new F3TreeScanner() {

            @Override
            public void scan(F3Tree tree) {
                super.scan(tree);
                if (tree != null) {
                    System.out.println("[" + tree.getStartPosition() + "," + tree.getEndPosition(module.endPositions) + "]  " + tree.toString());
                }
            }
        }.scan(module);

    }
    
    private List<F3Tree> pseudoVariables(DiagnosticPosition diagPos, Name moduleClassName, F3Script module,
            boolean usesSourceFile, boolean usesFile, boolean usesDir, boolean usesProfile) {
        ListBuffer<F3Tree> pseudoDefs = ListBuffer.<F3Tree>lb();
        if (usesSourceFile) {
            String sourceName = module.getSourceFile().toUri().toString();
            F3Expression sourceFileVar =
                f3make.at(diagPos).Var(pseudoSourceFile, getPseudoVarType(diagPos),
                         f3make.at(diagPos).Modifiers(FINAL|STATIC|SCRIPT_LEVEL_SYNTH_STATIC|F3Flags.IS_DEF),
                         f3make.Literal(sourceName), F3BindStatus.UNBOUND, null, null);
            pseudoDefs.append(sourceFileVar);
        }
        if (usesFile || usesDir) {
            F3Expression moduleClassFQN = module.pid != null ?
                f3make.at(diagPos).Select(module.pid, moduleClassName, false) : f3make.at(diagPos).Ident(moduleClassName);
            F3Expression getFile = f3make.at(diagPos).Identifier("org.f3.runtime.PseudoVariables.get__FILE__");
            F3Expression forName = f3make.at(diagPos).Identifier("java.lang.Class.forName");
            List<F3Expression> args = List.<F3Expression>of(f3make.at(diagPos).Literal(moduleClassFQN.toString()));
            F3Expression loaderCall = f3make.at(diagPos).Apply(List.<F3Expression>nil(), forName, args);
            args = List.<F3Expression>of(loaderCall);
            F3Expression getFileURL = f3make.at(diagPos).Apply(List.<F3Expression>nil(), getFile, args);
            F3Expression fileVar =
                f3make.at(diagPos).Var(pseudoFile, getPseudoVarType(diagPos),
                         f3make.at(diagPos).Modifiers(FINAL|STATIC|SCRIPT_LEVEL_SYNTH_STATIC|F3Flags.IS_DEF),
                         getFileURL, F3BindStatus.UNBOUND, null, null);
            pseudoDefs.append(fileVar);

            if (usesDir) {
                F3Expression getDir = f3make.at(diagPos).Identifier("org.f3.runtime.PseudoVariables.get__DIR__");
                args = List.<F3Expression>of(f3make.at(diagPos).Ident(pseudoFile));
                F3Expression getDirURL = f3make.at(diagPos).Apply(List.<F3Expression>nil(), getDir, args);
                pseudoDefs.append(
                    f3make.at(diagPos).Var(pseudoDir, getPseudoVarType(diagPos),
                             f3make.at(diagPos).Modifiers(FINAL|STATIC|SCRIPT_LEVEL_SYNTH_STATIC|F3Flags.IS_DEF),
                             getDirURL, F3BindStatus.UNBOUND, null, null));
            }
        }
	if (usesProfile) {
           F3Expression getProfile = f3make.at(diagPos).Identifier("org.f3.runtime.PseudoVariables.get__PROFILE__");
           F3Expression getProfileString = f3make.at(diagPos).Apply(List.<F3Expression>nil(), getProfile, List.<F3Expression>nil());
           F3Expression profileVar =
                f3make.at(diagPos).Var(pseudoProfile, getPseudoVarType(diagPos),
                         f3make.at(diagPos).Modifiers(FINAL|STATIC|SCRIPT_LEVEL_SYNTH_STATIC|F3Flags.IS_DEF),
                         getProfileString, F3BindStatus.UNBOUND, null, null);
            pseudoDefs.append(profileVar);
	}
        return pseudoDefs.toList();
    }
    
    private F3Type getPseudoVarType(DiagnosticPosition diagPos) {
        F3Expression fqn = f3make.at(diagPos).Identifier("java.lang.String");
        return f3make.at(diagPos).TypeClass(fqn, TypeTree.Cardinality.SINGLETON);
    }

    private List<F3Var> makeRunFunctionArgs(Name argName) {
        F3Var mainArgs = f3make.Param(argName, f3make.TypeClass(
                f3make.Ident(syms.stringTypeName),
                TypeTree.Cardinality.ANY));
         return List.<F3Var>of(mainArgs);
    }

    private F3Type makeRunFunctionType() {
	F3Expression rettree = f3make.Type(syms.objectType);
        rettree.type = syms.objectType;
        return f3make.TypeClass(rettree, F3Type.Cardinality.SINGLETON);
    }

    /**
     * Constructs the internal static run function when the user has explicitly supplied a
     * declaration and body for that function.
     *
     * TODO: Review whether the caller even needs to copy the statements from the existing
     *       body into stats, or can just use it. This change to code positions was done as
     *       an emergency patch (VSGC-2291) for release 1.0 and I thought
     *       it best to perform minimal surgery on the existing mechanism - Jim Idle.
     *
     * @param module           The Script level node
     * @param argName          The symbol table name of the args array
     * @param userRunFunction  The user written run function (if there is one)
     * @param value            The value of the function
     * @return                 The run function we have constructed
     */
    private F3FunctionDefinition makeInternalRunFunction(F3Script module, Name argName, F3FunctionDefinition userRunFunction, List<F3Expression> stats, F3Expression value) {

        F3Block existingBody = null;
        F3Block body = f3make.at(null).Block(module.getStartPosition(), stats, value);
        int sPos = module.getStartPosition();

        // First assume that this is synthetic
        //
        setEndPos(module, body, module);
        body.setGenType(SynthType.SYNTHETIC);

        // Now, override if it is not synthetic and there is a function body
        //  - there will only not be a body if this is coming from the IDE and the
        // script is in error at this point.
        //
        if  (userRunFunction != null) {

            existingBody = userRunFunction.getBodyExpression();
            body.setGenType(SynthType.COMPILED);

            if  (existingBody != null) {

                body.setPos(existingBody.getStartPosition());
                setEndPos(module, body, existingBody);
                sPos = userRunFunction.getStartPosition();

            }
        }

        // Make the static run function
        //
        F3FunctionDefinition func = f3make.at(sPos).FunctionDefinition(
                f3make.Modifiers(PUBLIC | STATIC | SCRIPT_LEVEL_SYNTH_STATIC | SYNTHETIC),
                defs.internalRunFunctionName,
                makeRunFunctionType(),
                makeRunFunctionArgs(argName),
                body);

        // Construct the source code end position from either the existing
        // function, or the module level.
        //
        if  (userRunFunction != null) {

            setEndPos(module, func, userRunFunction);
            func.operation.setPos(body.getStartPosition());
            F3Var param = func.getParams().head;
            F3Var existingParam = userRunFunction.getParams().head;

            if  (existingParam != null) {

                param.setPos(existingParam.getStartPosition());
                setEndPos(module, param, existingParam);
            }

        } else {

            setEndPos(module, func, module);
            func.setGenType(SynthType.SYNTHETIC);
        }

        setEndPos(module, func.operation, body);
        return func;
    }

    private Name scriptName(F3Script tree) {
        String fileObjName = null;

        FileObject fo = tree.getSourceFile();
        URI uri = fo.toUri();
        String path = uri.toString();
        int i = path.lastIndexOf('/') + 1;
        fileObjName = path.substring(i);
        int lastDotIdx = fileObjName.lastIndexOf('.');
        if (lastDotIdx != -1) {
            fileObjName = fileObjName.substring(0, lastDotIdx);
        }

        return names.fromString(fileObjName);
    }

    private void checkName(int pos, Name name) {
        if (reservedTopLevelNamesSet == null) {
            reservedTopLevelNamesSet = new HashSet<Name>();
            
            // make sure no one tries to declare these reserved names
            reservedTopLevelNamesSet.add(pseudoFile);
            reservedTopLevelNamesSet.add(pseudoDir);
        }
        
        if (reservedTopLevelNamesSet.contains(name)) {
            log.error(pos, MsgSym.MESSAGE_F3_RESERVED_TOP_LEVEL_SCRIPT_MEMBER, name.toString());
        }
    }
    
    private void checkForBadPositions(F3Script testTree) {
        final Map<JCTree, Integer> endPositions = testTree.endPositions;  
        new F3TreeScanner() {

            @Override
            public void scan(F3Tree tree) {
                super.scan(tree);
                
                // A Modifiers instance with no modifier tokens and no annotations
                // is defined as having no position.
                if (tree instanceof F3Modifiers) {
                    F3Modifiers mods = (F3Modifiers)tree;
                    if (mods.getFlags().isEmpty() || 
                        (mods.flags & Flags.SYNTHETIC) != 0)
                        return;
                }
                
                // TypeUnknown trees have no associated tokens.
                if (tree instanceof F3TypeUnknown)
                    return; 
                
                if (tree != null) {
                    if (tree.pos <= 0) {
                        String where = tree.getClass().getSimpleName();
                        try {
                            where = where + ": " + tree.toString();
                        } catch (Throwable exc) {
                            //ignore
                        }
                        System.err.println("Position of " +
                                           tree.pos +
                                           " in ---" +
                                           where);
                    }
                    if (tree.getEndPosition(endPositions) <= 0) {
                        String where = tree.getClass().getSimpleName();
                        try {
                            where = where + ": " + tree.toString();
                        } catch (Throwable exc) {
                            //ignore
                        }
                        System.err.println("End position of " +
                                           tree.getEndPosition(endPositions) +
                                           " in ---" +
                                           where);
                    }
                }
            }
        }.scan(testTree);
    }

}
