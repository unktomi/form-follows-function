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
import org.f3.api.tree.ForExpressionInClauseTree;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.*;
import org.f3.tools.tree.F3Expression;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.MethodType;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.Position;
import java.util.Stack;

/**
 * Convert local contexts into classes if need be.
 * This conversion is needed for local contexts containing binds,
 * on-replace, and some forms of object literal.
 *
 * Also, local context is created for if Pointer.make is called on a
 * local variable or if interpolation attribute is a local variable.
 *
 * This work is broken into chunks, where a chunk is a body of code
 * within which variables can be moved to the top (and initialized
 * in-place).  Chunk boundaries are:
 *
 *   Class Declaration
 *   function body
 *   for-expression body
 *   while-loop body
 *   catch body
 *   on-replace/instanciate body
 *
 * This code is FRAGILE.
 * DO NOT change this code without review from Robert.
 *
 * @author Robert Field
 */
public class F3LocalToClass {

    private final F3PreTranslationSupport preTrans;
    private final F3TreeMaker f3make;
    private final F3Defs defs;
    private final Name.Table names;
    private final F3Types types;
    private final F3Symtab syms;
    private final F3Resolve rs;

    private F3Env<F3AttrContext> env;
    private Symbol owner;
    private boolean isStatic;
    private Stack<Symbol> prevOwners = new Stack();
    private Stack<Boolean> prevIsStatics = new Stack();
    private Stack<Type> prevReturnTypes = new Stack<Type>();

    protected static final Context.Key<F3LocalToClass> localToClass =
            new Context.Key<F3LocalToClass>();

    public static F3LocalToClass instance(Context context) {
        F3LocalToClass instance = context.get(localToClass);
        if (instance == null) {
            instance = new F3LocalToClass(context);
        }
        return instance;
    }

    private F3LocalToClass(Context context) {
        context.put(localToClass, this);

        preTrans = F3PreTranslationSupport.instance(context);
        f3make = F3TreeMaker.instance(context);
        defs = F3Defs.instance(context);
        names = Name.Table.instance(context);
        types = F3Types.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        rs = F3Resolve.instance(context);
    }

    public void inflateAsNeeded(F3Env<F3AttrContext> attrEnv) {
        this.env = attrEnv;
        descend(attrEnv.tree);
    }

    enum BlockKind {
        FUNCTION,
        TRIGGER,
        LOOP,
        CATCH
    }

    /**
     * This class is subclassed by classes which wish to control their descent
     * into other chunks.
     *
     * Where a chunk in a block-expression and all the sharable contexts within.
     * Loop bodies (for and while) are not sharable (because they are multiple).
     * Class declarations, and function definition are
     * separate contexts (thus not sharable).
     *
     * This class should have NO semantics.  Its job it to define the boundaries
     * of chunks.  It is the sole place this is done.
     */
    private abstract class AbstractTreeChunker extends F3TreeScanner {

        abstract void blockWithin(F3Block block, BlockKind bkind);

        abstract void classWithin(F3ClassDeclaration block);

        @Override
        public void visitClassDeclaration(F3ClassDeclaration tree) {
            // The class is a new chunk
            pushOwner(tree.sym, false);
            classWithin(tree);
            popOwner();
        }

        @Override
        public void visitFunctionValue(F3FunctionValue tree) {
            // Don't process parameters

            // The body of the function begins a new chunk
            pushOwner(tree.definition.sym, false);
            Type returnType = (tree.definition.type == null)?
                null : tree.definition.type.getReturnType();
            pushFunctionReturnType(returnType);
            blockWithin(tree.getBodyExpression(), BlockKind.FUNCTION);
            popFunctionReturnType();
            popOwner();
        }

        @Override
        public void visitForExpression(F3ForExpression tree) {
	    if (tree.getMap() != null) {
		scan(tree.getMap());
		return;
	    }
            for (ForExpressionInClauseTree cl : tree.getInClauses()) {
                F3ForExpressionInClause clause = (F3ForExpressionInClause) cl;
                // Don't process induction var
                scan(clause.getSequenceExpression());
                scan(clause.getWhereExpression());
            }
            // The body of the for-expression begins a new chunk
            // Lower has made the body a block-expression
            boolean prevStatic = isStatic;
            isStatic = false;
            pushOwner(preTrans.makeDummyMethodSymbol(owner, defs.boundForPartName), false);
            blockWithin((F3Block) tree.getBodyExpression(), BlockKind.LOOP);
            popOwner();
            isStatic = prevStatic;
        }

        @Override
        public void visitWhileLoop(F3WhileLoop tree) {
            scan(tree.cond);
            // The body of the while-loop begins a new chunk
            // Lower has made the body a block-expression
            blockWithin((F3Block) tree.getBody(), BlockKind.LOOP);
        }

        @Override
        public void visitCatch(F3Catch tree) {
            // Skip param
            // The body of the catch begins a new chunk
            blockWithin((F3Block) tree.getBlock(), BlockKind.CATCH);
        }

        @Override
        public void visitOnReplace(F3OnReplace tree) {
            pushOwner(preTrans.makeDummyMethodSymbol(owner), false);
            blockWithin(tree.getBody(), BlockKind.TRIGGER);
            popOwner();
        }

        /****** Visit methods just to keep the owner straight ******/

        //TODO: should this just be on function-value?
        @Override
        public void visitFunctionDefinition(F3FunctionDefinition tree) {
            pushOwner(tree.sym, false);
            super.visitFunctionDefinition(tree);
            popOwner();
        }

        @Override
        public void visitInitDefinition(F3InitDefinition tree) {
            pushOwner(tree.sym, false);
            super.visitInitDefinition(tree);
            popOwner();
        }

        @Override
        public void visitPostInitDefinition(F3PostInitDefinition tree) {
            pushOwner(tree.sym, false);
            super.visitPostInitDefinition(tree);
            popOwner();
        }

        @Override
        public void visitVar(F3Var tree) {
            pushOwner(preTrans.makeDummyMethodSymbol(owner), tree.isStatic());
            scan(tree.getInitializer());
            popOwner();
            scan(tree.getOnReplace());
            scan(tree.getOnInvalidate());
        }
    }

    /**
     * Check if a local context needs to be inflated into a class.
     * If it has bound variables, variables with triggers, or
     * contexts that can reference locals, we need to inflate.
     * @param tree Expression to check
     * @return True if tree needs to be inflated
     */
    private boolean needsToBeInflatedToClass(F3Tree tree) {

        class InflationChecker extends AbstractTreeChunker {

            boolean needed = false;

            void blockWithin(F3Block block, BlockKind bkind) {
                // Do not descend -- this analysis is within the chunk
            }

            void classWithin(F3ClassDeclaration klass) {
                // Do not descend -- this analysis is within the chunk

                // If this block holds a class definition that references a local var that is
                // assigned to (and thus cannot be final), we need to inflate the block
                needed |= referencesMutatedLocal(klass);
            }

            @Override
            public void visitVar(F3Var tree) {
                // Check for bound or triggered
                needed |= tree.isBound();
                needed |= tree.getOnReplace() != null;
                needed |= tree.getOnInvalidate() != null;
                needed |= hasSelfReference(tree);
                super.visitVar(tree);
            }

            @Override
            public void visitForExpression(F3ForExpression tree) {
                needed |= needsToBeInflatedToClass(tree.getBodyExpression()) && referencesMutatedLocal(tree);
                super.visitForExpression(tree);
            }

            @Override
            public void visitCatch(F3Catch tree) {
                needed |= needsToBeInflatedToClass(tree.getBlock()) && referencesMutatedLocal(tree);
                super.visitCatch(tree);
            }

            @Override
            public void visitWhileLoop(F3WhileLoop tree) {
                needed |= needsToBeInflatedToClass(tree.getBody()) && referencesMutatedLocal(tree);
                super.visitWhileLoop(tree);
            }

            @Override
            public void visitFunctionValue(F3FunctionValue tree) {
                // Funtion value may reference (non-final) locals
                needed |= referencesLocal(tree);
                super.visitFunctionValue(tree);
            }

            @Override
            public void visitFunctionInvocation(F3FunctionInvocation tree) {
                Symbol msym = F3TreeInfo.symbol(tree.meth);

                // If the function call is the magic Pointer.make(Object)
                // function and argument involves a local var, then make a local
                // context class.
                if (types.isSyntheticPointerFunction(msym)) {
                    Symbol sym = F3TreeInfo.symbol(tree.args.head);
                    if (sym.isLocal()) {
			System.err.println("inflate due to "+sym);
                        needed = true;
                    }
                } else if (msym != null && (msym.flags() & F3Flags.BOUND) != 0L) {
                    // This is a call to a bound function. If any of the arg involves
                    // a local  variable or literal value, then make a local context class
                    for (F3Expression arg : tree.args) {
                        Symbol sym = F3TreeInfo.symbol(arg);
                        if (sym != null && sym.isLocal()) {
                            //needed = true; // Chris O. changed - this was causing massive unnecessary "inflation" - revisit if problems turn up

                            break;
                        }
                    }
                } else {
                    super.visitFunctionInvocation(tree);
                }
            }

            @Override
            public void visitBlockExpression(F3Block tree) {
                if (tree.isBound()) {
                    needed = true;
                }
                super.visitBlockExpression(tree);
            }

            @Override
            public void visitInterpolateValue(F3InterpolateValue tree) {
                F3Tag tag = F3TreeInfo.skipParens(tree.attribute).getF3Tag();
                if (tag == F3Tag.IDENT) {
                    Symbol sym = F3TreeInfo.symbol(tree.attribute);
                    if (sym.isLocal()) {
                        needed = true;
                    }
                } else {
                    super.visitInterpolateValue(tree);
                }
            }
        }
        InflationChecker ic = new InflationChecker();
        ic.scan(tree);
        return ic.needed;
    }

    private class VarAndClassConverter extends AbstractTreeChunker {

        final F3ClassSymbol classSym;

        VarAndClassConverter(F3ClassSymbol classSym) {
            this.classSym = classSym;
        }

        ListBuffer<F3Var> vars = ListBuffer.lb();

        List<F3Tree> varsAsMembers() {
            return List.convert(F3Tree.class, vars.toList());
        }

        void blockWithin(F3Block block, BlockKind bkind) {
            // Do not descend -- this inflation is within the chunk
        }

        void classWithin(F3ClassDeclaration block) {
            // Do not descend -- this inflation is within the chunk
        }

        /**
         * Convert any variables with the local context into a VarInit
         */
        private F3Expression convertExprAndScan(F3Expression expr) {
            if (expr instanceof F3Var) {
                F3Var var = (F3Var) expr;
                vars.append(var);
                Scope oldScope = preTrans.getEnclosingScope(var.sym);
                if (oldScope != null) {
                    oldScope.remove(var.sym);
                }
                
                var.sym.name = preTrans.makeUniqueVarNameIn(var.sym.name, classSym);
                
                if (isStatic) {
                    var.sym.flags_field |= Flags.STATIC;
                    var.mods.flags |= Flags.STATIC;
                }
                classSym.members().enter(var.sym);
                var.sym.owner = classSym;
                pushOwner(preTrans.makeDummyMethodSymbol(classSym), isStatic);
                scan(var.getInitializer());
                popOwner();
                scan(var.getOnReplace());
                scan(var.getOnInvalidate());

                // Do the init in-line
                F3Expression vi = f3make.at(var).VarInit(var);
                vi.type = var.type;
                return vi;
            } else {
                // Not a var, just pass through
                scan(expr);
                return expr;
            }
        }

        @Override
        public void visitBlockExpression(F3Block tree) {
            ListBuffer<F3Expression> stmts = ListBuffer.lb();
            for (F3Expression stat : tree.stats) {
                stmts.append(convertExprAndScan(stat));
            }
            // Replace the guts of the block-expression with the var-converted versions
            tree.stats = stmts.toList();
            tree.value = convertExprAndScan(tree.value);
        }
    }

    /**
     * Inflate a block-expression into a class:
     *   {
     *     var x = 4;
     *     ++x;
     *     var y = x + 100;
     *     def z = bind y + 1;
     *     println(z);
     *     x + z
     *   }
     * Should become:
     *   {
     *     class local_klass44 {
     *       var x = 4;
     *       var y = x + 100;
     *       def z = bind y + 1;
     *       function doit$23(0 {
     *         VarInit x;
     *         ++x;
     *         VarInit y;
     *         ;
     *         println(z);
     *         x + z
     *       }
     *     }
     *     (new local_klass44()).doit$();
     *   }
     */
    private void inflateBlockToClass(F3Block block, BlockKind bkind) {
        final Name funcName = preTrans.syntheticName(defs.doitDollarNamePrefix());

        String classNamePrefix;
        if (owner instanceof MethodSymbol && (owner.flags() & F3Flags.BOUND) != 0L) {
            classNamePrefix = F3Defs.boundFunctionClassPrefix;
        } else if (owner instanceof MethodSymbol && owner.name == defs.boundForPartName) {
            classNamePrefix = F3Defs.boundForPartClassPrefix;
        } else {
            classNamePrefix = F3Defs.localContextClassPrefix;
        }
        final Name className = preTrans.syntheticName(classNamePrefix);

        final F3ClassSymbol classSymbol = preTrans.makeClassSymbol(className, owner);
        classSymbol.flags_field |= Flags.FINAL;

        final MethodType funcType = new MethodType(
                List.<Type>nil(),    // arg types
                types.normalize(block.type),  // return type
                List.<Type>nil(),    // Throws type
                syms.methodClass);   // TypeSymbol
        final MethodSymbol funcSym = new MethodSymbol(F3Flags.FUNC_SYNTH_LOCAL_DOIT, funcName, funcType, classSymbol);

        class BlockVarAndClassConverter extends VarAndClassConverter {
            List<F3Tag> nonLocalExprTags = List.nil();
            List<F3Catch> nonLocalCatchers = List.nil();
            Type returnType = null;

            BlockVarAndClassConverter() {
                super(classSymbol);
            }
            @Override
            public void visitReturn(F3Return tree) {
                // This is a return in a local context inflated to class
                // Handle it as a non-local return
                tree.nonLocalReturn = true;
                returnType = tree.getExpression() != null ?
                    topFunctionReturnType() :
                    syms.voidType;
                if (!nonLocalExprTags.contains(tree.getF3Tag())) {
                    F3Var param = makeExceptionParameter(syms.f3_NonLocalReturnExceptionType);
                    F3Return catchBody = f3make.Return(null);
                    if (returnType.tag != TypeTags.VOID) {
                        F3Ident nlParam = f3make.Ident(param);
                        nlParam.type = param.type;
                        nlParam.sym = param.sym;
                        F3Select nlValue = f3make.Select(nlParam,
                        defs.value_NonLocalReturnExceptionFieldName, false);
                        nlValue.type = syms.objectType;
                        nlValue.sym = rs.findIdentInType(env, syms.f3_NonLocalReturnExceptionType, nlValue.name, Kinds.VAR);
                        catchBody.expr = f3make.TypeCast(preTrans.makeTypeTree(returnType), nlValue).setType(returnType);
                    }
                    nonLocalExprTags = nonLocalExprTags.append(tree.getF3Tag());
                    nonLocalCatchers = nonLocalCatchers.append(makeCatchExpression(param, catchBody, returnType));
                }
                scan(tree.expr);
            }
            @Override
            public void visitBreak(F3Break tree) {
                // This is a break in a local context inflated to class
                // Handle it as a non-local break
                tree.nonLocalBreak = true;
                if (!nonLocalExprTags.contains(tree.getF3Tag())) {
                    F3Var param = makeExceptionParameter(syms.f3_NonLocalBreakExceptionType);
                    F3Break catchBody = f3make.Break(tree.label);
                    nonLocalExprTags = nonLocalExprTags.append(tree.getF3Tag());
                    nonLocalCatchers = nonLocalCatchers.append(makeCatchExpression(param, catchBody, syms.unreachableType));
                }
            }
            @Override
            public void visitContinue(F3Continue tree) {
                // This is a continue in a local context inflated to class
                // Handle it as a non-local continue
                tree.nonLocalContinue = true;
                if (!nonLocalExprTags.contains(tree.getF3Tag())) {
                    F3Var param = makeExceptionParameter(syms.f3_NonLocalContinueExceptionType);
                    F3Continue catchBody = f3make.Continue(tree.label);
                    nonLocalExprTags = nonLocalExprTags.append(tree.getF3Tag());
                    nonLocalCatchers = nonLocalCatchers.append(makeCatchExpression(param, catchBody, syms.unreachableType));
                }
            }

            @Override
            public void visitVar(F3Var tree) {
                if ((tree.mods.flags & Flags.PARAMETER) == 0L) {
                    throw new AssertionError("all vars should have been processed in the block expression");
                }
            }

            private F3Var makeExceptionParameter(Type exceptionType) {
                F3Var param = f3make.Param(preTrans.syntheticName(defs.exceptionDollarNamePrefix()), preTrans.makeTypeTree(exceptionType));
                param.setType(exceptionType);
                param.sym = new F3VarSymbol(types, names, 0L, param.name, param.type, owner);
                return param;
            }

            private F3Catch makeCatchExpression(F3Var param, F3Expression body, Type bodyType) {
                return f3make.Catch(param,
                                (F3Block)f3make.Block(0L,
                                    List.<F3Expression>nil(),
                                    body).setType(bodyType));
            }
        }
        BlockVarAndClassConverter vc = new BlockVarAndClassConverter();
        vc.scan(block);

        // set position of class etc as block-expression position
        f3make.at(block.pos());

        // Create whose vars are the block's vars and having a doit function with the content

        F3Type f3type = f3make.TypeUnknown();
        f3type.type = block.type;

        F3Block body = f3make.Block(block.flags, block.getStmts(), block.getValue());
        body.type = block.type;
        body.pos = Position.NOPOS;

        F3FunctionDefinition doit = f3make.FunctionDefinition(
                f3make.Modifiers(F3Flags.SCRIPT_PRIVATE),
                funcName,
                f3type,
                List.<F3Var>nil(),
                body);
        doit.pos = Position.NOPOS;
        doit.sym = funcSym;
        doit.type = funcType;

        final F3ClassDeclaration cdecl = f3make.ClassDeclaration(
                f3make.Modifiers(Flags.FINAL | Flags.SYNTHETIC),
                className,
                List.<F3Expression>nil(),
                vc.varsAsMembers().append(doit));
        cdecl.sym = classSymbol;
        cdecl.type = classSymbol.type;
        types.addF3Class(classSymbol, cdecl);
        cdecl.setDifferentiatedExtendingImplementingMixing(List.<F3Expression>nil(), List.<F3Expression>nil(), List.<F3Expression>nil());

        F3Ident classId = f3make.Ident(className);
        classId.sym = classSymbol;
        classId.type = classSymbol.type;

        F3Instanciate inst = f3make.InstanciateNew(classId, null);
        inst.sym = classSymbol;
        inst.type = classSymbol.type;

        F3Select select = f3make.Select(inst, funcName, false);
        select.sym = funcSym;
        select.type = funcSym.type;

        F3FunctionInvocation apply = f3make.Apply(null, select, null);
        apply.type = block.type;

        List<F3Expression> stats = List.<F3Expression>of(cdecl);
        F3Expression value = apply;

	if (apply.type != syms.voidType) {
	    value = f3make.TypeCast(preTrans.makeTypeTree(F3TranslationSupport.ERASE_BACK_END ? types.erasure(apply.type) : apply.type),
				    value);
	    
	    //System.err.println("apply.type="+apply.type);
	    value.setType(apply.type);
	}

        if (vc.nonLocalCatchers.size() > 0) {

            F3Block tryBody = (F3Block)f3make.Block(0L, stats, value).setType(block.type);
            
            stats = List.<F3Expression>of(
                f3make.Try(
                    tryBody,
                    vc.nonLocalCatchers,
                    null));

            if (block.type != syms.voidType) {
                F3VarSymbol resVarSym = new F3VarSymbol(types,
                        names,
                        0L,
                        preTrans.syntheticName(defs.resDollarNamePrefix()),
                        types.normalize(block.type),
                        doit.sym);
                F3Var resVar = f3make.Var(resVarSym.name,
                    preTrans.makeTypeTree(resVarSym.type),
                    f3make.Modifiers(resVarSym.flags_field),
                    null,
                    F3BindStatus.UNBOUND,
                    null, null);
                resVar.type = resVarSym.type;
                resVar.sym = resVarSym;
                F3Ident resVarRef = f3make.Ident(resVarSym);
                resVarRef.sym = resVarSym;
                resVarRef.type = resVar.type;
                tryBody.value = f3make.Assign(resVarRef, apply).setType(block.type);

                value = (bkind == BlockKind.FUNCTION &&
                        vc.returnType != null &&
                        vc.returnType.tag != TypeTags.VOID) ?
                        f3make.Return(
                            f3make.TypeCast(
                                preTrans.makeTypeTree(vc.returnType),
                                resVarRef
                            ).setType(vc.returnType)
                        ).setType(syms.unreachableType) :
                    resVarRef;
                stats = stats.prepend(resVar);
            }
            else {
                value = null;
            }
        }

        preTrans.liftTypes(cdecl, classSymbol.type, funcSym);

        // Replace the guts of the block-expression with the class wrapping the previous body
        // and a call to the doit function of that class.
        block.stats = stats;
        block.value = value;

    }

    /**
     * Flatten any vars defined in var initializer block expressions into the class level
     */
    private void flattenVarsIntoClass(F3ClassDeclaration klass) {
        final F3ClassSymbol classSym = (F3ClassSymbol) klass.sym;
        VarAndClassConverter vc = new VarAndClassConverter(classSym);
        for (F3Tree mem : klass.getMembers()) {
            if (needsToBeInflatedToClass(mem)) {
                vc.scan(mem);
            }
        }
        klass.setMembers(klass.getMembers().appendList(vc.varsAsMembers()));
    }

    /**
     * Descend to the next level of chunk
     */
    private void descend(F3Tree tree) {
        BottomUpChunkWalker bucw = new BottomUpChunkWalker();
        bucw.scan(tree);
    }

    private class BottomUpChunkWalker extends AbstractTreeChunker {

        void blockWithin(F3Block block, BlockKind bkind) {
            // Descend into inner chunks
            descend(block);

            // check if the block needs inflation, if so, inflate
            if (needsToBeInflatedToClass(block)) {
                inflateBlockToClass(block, bkind);
            }
        }

        void classWithin(F3ClassDeclaration klass) {
            for (F3Tree member : klass.getMembers()) {
                descend(member);
            }
            flattenVarsIntoClass(klass);
        }
    }


    /************************** Utilities ******************************/

    private boolean referencesMutatedLocal(F3Tree tree) {

        class ReferenceChecker extends F3TreeScanner {

            boolean hasMutatedLocal = false;

            @Override
            public void visitIdent(F3Ident tree) {
                if (tree.sym instanceof VarSymbol) {
                    F3VarSymbol vsym = (F3VarSymbol) tree.sym;
                    if (vsym.isMutatedLocal()) {
                        hasMutatedLocal = true;
                    }
                }
            }
        }
        ReferenceChecker rc = new ReferenceChecker();
        rc.scan(tree);
        return rc.hasMutatedLocal;
    }

    private boolean referencesLocal(F3Tree tree) {

        class ReferenceChecker extends F3TreeScanner {

            boolean hasLocal = false;

            @Override
            public void visitIdent(F3Ident tree) {
                if (tree.sym instanceof VarSymbol) {
                    F3VarSymbol vsym = (F3VarSymbol) tree.sym;
                    if (vsym.isLocal()) {
                        hasLocal = true;
                    }
                }
                
                super.visitIdent(tree);
            }
            
            @Override
            public void visitIndexof(F3Indexof tree) {
                hasLocal = true;
                super.visitIndexof(tree);
            }
        }
        ReferenceChecker rc = new ReferenceChecker();
        rc.scan(tree);
        return rc.hasLocal;
    }

    private boolean hasSelfReference(F3Var checkedVar) {
        return checkedVar.sym.hasSelfReference();
    }

    private void pushOwner(Symbol newOwner, boolean newIsStatic) {
        prevOwners.push(owner);
        prevIsStatics.push(isStatic);
        owner = newOwner;
        isStatic = newIsStatic;
    }

    private void popOwner() {
       owner = prevOwners.pop();
       isStatic = prevIsStatics.pop();
    }

    private void pushFunctionReturnType(Type returnType) {
        prevReturnTypes.push(returnType);
    }

    private void popFunctionReturnType() {
        prevReturnTypes.pop();
    }

    private Type topFunctionReturnType() {
        return prevReturnTypes.peek();
    }
}

