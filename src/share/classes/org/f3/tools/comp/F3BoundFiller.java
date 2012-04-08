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
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.*;
import org.f3.tools.tree.F3Expression;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.MethodSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;

/**
 * Fill in the synthetic definitions needed in a bound function
 * and bound object literal.
 * 
 * @author A. Sundararajan
 * @author Robert Field
 */
public class F3BoundFiller extends F3TreeScanner {

    private final F3PreTranslationSupport preTrans;
    private final F3TreeMaker f3make;
    private final F3Defs defs;
    private final F3Symtab syms;
    protected final F3Types types;
    private final Name.Table names;

    protected static final Context.Key<F3BoundFiller> boundFuncFill =
            new Context.Key<F3BoundFiller>();

    public static F3BoundFiller instance(Context context) {
        F3BoundFiller instance = context.get(boundFuncFill);
        if (instance == null) {
            instance = new F3BoundFiller(context);
        }
        return instance;
    }

    private F3BoundFiller(Context context) {
        context.put(boundFuncFill, this);

        preTrans = F3PreTranslationSupport.instance(context);
        f3make = F3TreeMaker.instance(context);
        defs = F3Defs.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        types = F3Types.instance(context);
        names = Name.Table.instance(context);
    }

    public void fill(F3Env<F3AttrContext> attrEnv) {
        scan(attrEnv.tree);
    }

    /**
     * Add variables needed when a bound for-expression body is converted into a class:
     * 
     *           var $indexof$x : Integer = $index$;
     *           var x : T;
     *           def result = bind block_value;
     */
    @Override
    public void visitForExpression(F3ForExpression tree) {
        if (tree.isBound()) {
            if (tree.getMap() == null) {
                F3Block body = (F3Block) tree.getBodyExpression();
                assert tree.getInClauses().size() == 1 : "lower is supposed to flatten to exactly one in-clause";
                F3ForExpressionInClause clause = tree.getForExpressionInClauses().get(0);
                MethodSymbol dummyOwner = preTrans.makeDummyMethodSymbol(clause.var.sym.owner);
                F3Var idxv = createIndexVar(clause, dummyOwner);
                F3Var iv = createInductionVar(clause, idxv.sym, dummyOwner);
                F3Var rv = createResultVar(clause, body, dummyOwner, tree.type);
                body.stats = body.stats.prepend(iv).prepend(idxv).append(rv);
                body.value = preTrans.defaultValue(body.type); // just fill the spot
                scan(clause);
                scan(body);
            } else {
                scan(tree.getMap());
            }
        } else {
            super.visitForExpression(tree);
        }
    }

    /**
     * Create the '$indexof$x' variable
     */
    private F3Var createIndexVar(F3ForExpressionInClause clause, Symbol owner) {
        // Create the method parameter
        // $index$
        Name indexParamName = names.fromString(defs.dollarIndexNamePrefix());
        Name indexName = F3TranslationSupport.indexVarName(clause.getVar().getName(), names);
        F3VarSymbol indexParamSym = new F3VarSymbol(types, names,Flags.FINAL | Flags.PARAMETER, indexParamName, syms.intType, owner);

        // Create the index var
        // var $indexof$x = $index$
        F3Var indexVar = preTrans.LocalVar(clause.pos(), syms.intType, indexName, f3make.Ident(indexParamSym), owner);
        // Stash the created variable so it can be used when we visit a
        // F3Indexof, where we convert that to a F3Ident referencing the indexDecl.
        clause.indexVarSym = indexVar.sym;
        return indexVar;
    }

    /**
     * Create the induction var in the body
     *  var x
     */
    private F3Var createInductionVar(F3ForExpressionInClause clause, F3VarSymbol boundIndexVarSym, Symbol owner) {
        F3Var param = clause.getVar();
        F3Var inductionVar =  preTrans.LocalVar(param.pos(), param.type, param.name, null, owner);
        clause.inductionVarSym = inductionVar.sym = param.sym;
        return inductionVar;
    }

    /**
     * Create the bound result:
     *  def result = bind block_value;
     */
    private F3Var createResultVar(F3ForExpressionInClause clause, F3Block body, Symbol owner, Type seqType) {
        F3Expression value = body.value;
        Type valtype = value.type;
        if (clause.getWhereExpression() != null) {
            // There is a where-clause, convert to an if-expression
            F3Expression nada;
            if (types.isSequence(valtype)) {
                nada = f3make.EmptySequence();
            } else {
                // For now, at least, if there is a where clause, we need to be
                // able to return null, so box the type
                nada = f3make.Literal(TypeTags.BOT, null);
                valtype = types.boxedElementType(seqType);
                value = preTrans.makeCastIfNeeded(value, valtype);
                value.type = valtype;
            }
            nada.type = valtype;
            value = f3make.Conditional(clause.getWhereExpression(), value, nada);
            value.type = valtype;
            clause.setWhereExpr(null);
        }
        body.type = valtype;
        F3Var param = clause.getVar();
        Name resName = resultVarName(param.name);
        F3Var resultVar =  preTrans.BoundLocalVar(clause.pos(), valtype, resName, value, owner);
        resultVar.sym.flags_field |= F3Flags.VARUSE_BIND_ACCESS;
        clause.boundResultVarSym = resultVar.sym;
        return resultVar;
    }

    public Name resultVarName(Name name) {
        return names.fromString(defs.resultDollarNamePrefix() + name.toString());
    }

    @Override
    public void visitIndexof(F3Indexof tree) {
        // Convert
        super.visitIndexof(tree);
    }

    @Override
    public void visitFunctionDefinition(F3FunctionDefinition tree) {
        if (tree.isBound()) {
            // Fill out the bound function support vars before
            // local inflation
            boundFunctionFiller(tree);
        }
        super.visitFunctionDefinition(tree);
    }

    private void boundFunctionFiller(F3FunctionDefinition tree) {
        /*
         * For bound functions, make a synthetic bound variable with
         * initialization expression to be the return expression and return
         * the Pointer of the synthetic variable as the result.
         */
        F3Block blk = tree.getBodyExpression();
        if (blk != null) {
            F3Expression returnExpr = (blk.value instanceof F3Return) ? ((F3Return) blk.value).getExpression() : blk.value;
            if (returnExpr != null) {
                f3make.at(blk.value.pos);
                ListBuffer<F3Expression> stmts = ListBuffer.lb();
                /*
                 * Generate a local variable for each parameter. We will later
                 * transform each param as F3Object+varNum pair during translation.
                 * These locals will be converted into instance variables of the
                 * local context class.
                 */
                for (F3Var f3Var : tree.getParams()) {
                    F3Var localVar = f3make.Var(
                            f3Var.name,
                            f3Var.getF3Type(),
                            f3make.Modifiers(f3Var.mods.flags & ~Flags.PARAMETER),
                            preTrans.defaultValue(f3Var.type),
                            F3BindStatus.UNIDIBIND, null, null);
                    localVar.type = f3Var.type;
                    localVar.sym = f3Var.sym;
                    stmts.append(localVar);
                }

                stmts.appendList(blk.stats);

                // is return expression a variable declaration?
                boolean returnExprIsVar = (returnExpr.getF3Tag() == F3Tag.VAR_DEF);
                if (returnExprIsVar) {
                    stmts.append(returnExpr);
                }
                F3Var returnVar = f3make.Var(
                        defs.boundFunctionResultName,
                        f3make.TypeUnknown(),
                        f3make.Modifiers(0),
                        returnExprIsVar ? f3make.Ident((F3Var) returnExpr) : returnExpr,
                        F3BindStatus.UNIDIBIND, null, null);
                returnVar.type = tree.sym.type.getReturnType();
                returnVar.sym = new F3VarSymbol(types, names,0L, defs.boundFunctionResultName, returnVar.type, tree.sym);
                returnVar.sym.flags_field |= F3Flags.VARUSE_BIND_ACCESS;
                returnVar.markBound(F3BindStatus.UNIDIBIND);
                stmts.append(returnVar);

                // find the symbol of Pointer.make(Object) method.
                // The select expression Pointer.make
                F3Select select = f3make.Select(f3make.Type(syms.f3_PointerType), defs.make_PointerMethodName, false);
                select.sym = preTrans.makeSyntheticPointerMake();
                select.type = select.sym.type;


                // args for Pointer.make(Object)
                F3Ident ident = f3make.Ident(returnVar);
                ident.type = returnVar.type;
                ident.sym = returnVar.sym;
                ListBuffer<F3Expression> pointerMakeArgs = ListBuffer.lb();
                pointerMakeArgs.append(f3make.VarRef(ident, F3VarRef.RefKind.INST).setType(syms.f3_ObjectType));
                pointerMakeArgs.append(f3make.VarRef(ident, F3VarRef.RefKind.VARNUM).setType(syms.intType));

                // call Pointer.make($$bound$result$)
                F3FunctionInvocation apply = f3make.Apply(null, select, pointerMakeArgs.toList());
                apply.type = syms.f3_PointerType;

                blk.stats = stmts.toList();
                blk.value = apply;
                blk.type = apply.type;
            }
        }
    }
}
