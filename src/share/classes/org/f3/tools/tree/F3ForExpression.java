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

import org.f3.api.F3BindStatus;
import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Symbol;
import org.f3.tools.code.F3Flags;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import org.f3.api.tree.TypeTree.Cardinality;
import org.f3.tools.code.*;

/**
 * for (name in seqExpr where whereExpr) bodyExpr
 */
public class F3ForExpression extends F3Expression implements ForExpressionTree {

    private final F3Kind f3Kind;
    public final List<F3ForExpressionInClause> inClauses;
    public final F3Expression bodyExpr;
    public boolean isIterable;

    protected F3ForExpression(
            F3Kind f3Kind,
            List<F3ForExpressionInClause> inClauses,
            F3Expression bodyExpr) {
        this.f3Kind = f3Kind;
        this.inClauses = inClauses;
        this.bodyExpr = bodyExpr;
    }

    public void accept(F3Visitor v) {
        v.visitForExpression(this);
    }

    public java.util.List<ForExpressionInClauseTree> getInClauses() {
        return F3Tree.convertList(ForExpressionInClauseTree.class, inClauses);
    }

    public List<F3ForExpressionInClause> getForExpressionInClauses() {
        return inClauses;
    }

    public F3Expression getBodyExpression() {
        return bodyExpr;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.FOR_EXPRESSION;
    }

    public F3Kind getF3Kind() {
        return f3Kind;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitForExpression(this, data);
    }


    F3Expression apply = null;

    public F3Expression getMap() {
        return apply;
    }

    public F3Expression getIdentityMap(F3TreeMaker F, 
				       Name.Table names,
				       Type argType,
				       Type resultType) 
    {
	return null;
    }

    static F3Type makeType(F3TreeMaker f3make,
			   Name.Table names,
			   String typeName,
			   Cardinality card) {
        return 
            f3make.TypeClass(f3make.Identifier(names.fromString(typeName)), card);
    }
    
    public F3Expression getMap(F3TreeMaker F, 
                               Name.Table names,
                               Type argType,
                               Type resultType) {

        if (apply == null) {
            List<F3ForExpressionInClause> clauses = inClauses.reverse(); 
            apply = this.bodyExpr;
            for (List<F3ForExpressionInClause> x = clauses; 
                 x.nonEmpty(); x = x.tail) {
                F3ForExpressionInClause clause = x.head;
                apply = getMap(F, names, argType, resultType, clause, apply);
            }
        }
        return apply;
    }

    public F3Expression getMonadMap(F3TreeMaker F, 
				    Name.Table names,
				    F3Types types,
				    Type[] argTypes,
				    F3Expression[] typeClasses,
				    Type monadType,
				    Type resultType,
				    boolean isBound) {

        if (apply == null) {
            List<F3ForExpressionInClause> clauses = inClauses.reverse(); 
            apply = this.bodyExpr;
	    int i = 0;
	    int j = inClauses.size()-1;
	    System.err.println("monadType="+monadType);
	    System.err.println("resultType="+resultType);
            for (List<F3ForExpressionInClause> x = clauses; 
                 x.nonEmpty(); x = x.tail) {
                F3ForExpressionInClause clause = x.head;
		boolean first = i == 0;
		boolean isMap = first;
                Type argType = argTypes[j];
		F3Expression typeClass = typeClasses[j];
		if (types.isMonad(resultType) && types.isSameType(types.erasure(resultType), types.erasure(monadType))) {
		    if (!types.isSequence(resultType)) {
			isMap = false;
		    }
		}
		Name select =
		    isMap ? names.fromString("map") : names.fromString("flatmap");
		Type type = argType;
		if (!first) {
		    type = monadType;
		}
		boolean second = i == 1;
		if (false && !first) {
		    apply = F.TypeCast(F.Type(monadType), apply);
		}
                apply = getMonadMap(F, names, select, typeClass, argType, type, resultType, clause, apply, isBound);		
		if (false && !first) {
		    apply = F.TypeCast(F.Type(monadType), apply);
		}
		i++;
		j--;
            }
        }
        return apply;
    }

    F3Expression getMonadMap(F3TreeMaker F, 
			     Name.Table names,
			     Name map,
			     F3Expression typeClass,
			     Type argType,
			     Type monadType,
			     Type resultType,
			     F3ForExpressionInClause clause,
			     F3Expression bodyExpr,
			     boolean isBound) {
        // we want to turn 
        // bind foreach (x in xs, y in ys) f(x, y)
        // into
        // xs.flatmap(function(x) { ys.map(function(y) { f(x, y)}) })
        F3Modifiers mods = F.Modifiers(F3Flags.BOUND);
        F3Var var = clause.getVar();
        // this tmp var is a hack to work around existing bind gen bugs
        Name tmpName = names.fromString(var.name+"0$");
        F3Var tmpVar = F.at(var.pos).Var(var.name,
					 F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), Cardinality.SINGLETON),
					 var.mods,
					 F.at(var.pos).Ident(tmpName),
					 isBound ? F3BindStatus.UNIDIBIND: F3BindStatus.UNBOUND,
					 null, null);
	
        ListBuffer<F3Expression> blockBuffer = ListBuffer.lb();
        blockBuffer.append(tmpVar);
        F3Block body = F.at(bodyExpr.pos).Block(0L, blockBuffer.toList(), bodyExpr);
        ListBuffer<F3Var> parmsBuffer = ListBuffer.lb();
        parmsBuffer.append(F.at(var.pos).Param(tmpName, F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), Cardinality.SINGLETON)));
        List<F3Var> params = parmsBuffer.toList();
	Cardinality card = Cardinality.SINGLETON;
        F3FunctionValue fun = 
            F.FunctionValue(mods,
                            //F.at(bodyExpr.pos).TypeClass(F.at(bodyExpr.pos).Type(monadType), Cardinality.SINGLETON),
			    F.at(bodyExpr.pos).TypeUnknown(),
                            params,
                            body);
        F3Ident id = F.at(bodyExpr.pos).Ident(var.name);
        F3Select sel = typeClass == null ?
            F.at(bodyExpr.pos).Select(clause.getSequenceExpression(), map, false) :
            F.at(bodyExpr.pos).Select(typeClass, map, false);
	
        ListBuffer<F3Expression> argsBuffer = ListBuffer.lb();
	if (typeClass != null) {
	    argsBuffer.append(clause.getSequenceExpression());
	}
	argsBuffer.append(fun);
        F3Expression apply = F.at(bodyExpr.pos).Apply(null, 
                                                      sel,
                                                      argsBuffer.toList());
	//apply = F.at(apply.pos).TypeCast(F.Type(resultType), apply);	
	//System.err.println(apply);
	return apply;
    }

    public F3Expression getComonadMap(F3TreeMaker F, 
				      Name.Table names,
				      F3Types types,
				      Type[] comonadType,
				      F3Expression[] typeClasses,
				      boolean isBound) {

        if (apply == null) {
            List<F3ForExpressionInClause> clauses = inClauses.reverse(); 
            apply = this.bodyExpr;
	    boolean last = true;
	    int i = 0;
	    int j = clauses.size()-1;
            for (List<F3ForExpressionInClause> x = clauses; 
                 x.nonEmpty(); x = x.tail) {
                F3ForExpressionInClause clause = x.head;
                Type argType = comonadType[j];
		Type resultType = types.comonadElementType(argType);
		F3Expression typeClass = typeClasses[j];
                apply = getComonadMap(F, names, types, typeClass, argType, resultType, clause, apply, isBound, j == 0);		
		i++;
		j--;
            }
        }
        return apply;
    }

    F3Expression getComonadMap(F3TreeMaker F, 
			       Name.Table names,
			       F3Types types,
			       F3Expression typeClass,
			       Type argType,
			       Type resultType,
			       F3ForExpressionInClause clause,
			       F3Expression bodyExpr,
			       boolean isBound,
			       boolean last) {
        // we want to turn 
        // bind foreach ([1, 2, 3] into xs, [4, 5, 6] into ys) f(xs, ys)
        // into
        // xs.coflatmap(function(xs) { ys.coflatmap(function(ys) { f(xs, ys)}) })
	System.err.println("argType="+argType);
	System.err.println("resultType="+resultType);
	Name map = names.fromString("coflatmap");
        F3Modifiers mods = F.Modifiers(F3Flags.BOUND);
        F3Var var = clause.intoVar;
        // this tmp var is a hack to work around existing bind gen bugs
        Name tmpName = names.fromString(var.name+"0$");
	Cardinality card = Cardinality.SINGLETON;
	if (types.isSequence(argType)) {
	    card = Cardinality.ANY;
	    argType = types.elementType(argType);
	}
        F3Var tmpVar = F.at(var.pos).Var(var.name,
					 F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), card),
					 var.mods,
					 F.at(var.pos).Ident(tmpName),
					 isBound ? F3BindStatus.UNIDIBIND: F3BindStatus.UNBOUND,
					 null, null);
	
        ListBuffer<F3Expression> blockBuffer = ListBuffer.lb();
        blockBuffer.append(tmpVar);
        F3Block body = F.at(bodyExpr.pos).Block(0L, blockBuffer.toList(), bodyExpr);
        ListBuffer<F3Var> parmsBuffer = ListBuffer.lb();
        parmsBuffer.append(F.at(var.pos).Param(tmpName, F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), card)));
        List<F3Var> params = parmsBuffer.toList();
        F3FunctionValue fun = 
            F.FunctionValue(mods,
			    F.at(bodyExpr.pos).TypeUnknown(),
			    //F.at(bodyExpr.pos).TypeClass(F.at(bodyExpr.pos).Type(resultType), Cardinality.SINGLETON),
                            params,
                            body);
        F3Ident id = F.at(bodyExpr.pos).Ident(var.name);
        F3Select sel = typeClass == null ?
            F.at(bodyExpr.pos).Select(clause.getSequenceExpression(), map, false) :
            F.at(bodyExpr.pos).Select(typeClass, map, false);
	
        ListBuffer<F3Expression> argsBuffer = ListBuffer.lb();
	if (typeClass != null) {
	    argsBuffer.append(clause.getSequenceExpression());
	}
	argsBuffer.append(fun);
        F3Expression apply = F.at(bodyExpr.pos).Apply(null, 
                                                      sel,
                                                      argsBuffer.toList());
	if (!last) {
	    apply = F.at(bodyExpr.pos).Apply(null, F.at(bodyExpr.pos).Select(apply, names.fromString("extract"), false), 
					     List.<F3Expression>nil());
	}
	//apply = F.at(apply.pos).TypeCast(F.Type(resultType), apply);	
	//System.err.println(apply);
	return apply;
    }

    F3Expression getMap(F3TreeMaker F, 
                        Name.Table names,
                        Type argType,
                        Type resultType,
                        F3ForExpressionInClause clause,
                        F3Expression bodyExpr) {
        // we want to turn 
        // bind for (x in xs) f(x)
        // into
        // map(xs, bound function(x) {f(x)})
        // and 
        // we want to turn 
        // bind for (x in xs, y in ys where cond) f(x, y)
        // into
        // map(xs, bound function(x) {map(ys, function(y) {if (cond) f(x, y) else []})})

        F3Modifiers mods = F.Modifiers(F3Flags.BOUND);
        F3Var var = clause.getVar();
        // this tmp var is a hack to work around existing bind gen bugs
        Name tmpName = names.fromString(var.name+"0$");
        F3Var tmpVar = F.at(var.pos).Var(var.name,
                                             F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), Cardinality.SINGLETON),
                                             var.mods,
                                             F.at(var.pos).Ident(tmpName),
                                             F3BindStatus.UNIDIBIND,
                                             null, null);
                                             
        ListBuffer<F3Expression> blockBuffer = ListBuffer.lb();
        blockBuffer.append(tmpVar);
        if (clause.hasWhereExpression()) {
            bodyExpr = F.at(bodyExpr.pos).Conditional(clause.getWhereExpression(),
                                                      bodyExpr,
                                                      F.at(bodyExpr.pos).EmptySequence());
        }
        if (false) {
            F3Var tmpVar2 = F.at(var.pos).Var(names.fromString(var.name.toString()+"$mapped"),
                                                  F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), Cardinality.ANY),
                                                  F.at(var.pos).Modifiers(0L),
                                                  bodyExpr,
                                                  F3BindStatus.UNIDIBIND,
                                                  null, null);
            blockBuffer.append(tmpVar2);
            bodyExpr = F.at(var.pos).Ident(tmpVar2.name);
        }
        F3Block body = F.at(bodyExpr.pos).Block(0L, blockBuffer.toList(), bodyExpr);
        ListBuffer<F3Var> parmsBuffer = ListBuffer.lb();
        parmsBuffer.append(F.at(var.pos).Param(tmpName, F.at(var.pos).TypeClass(F.at(var.pos).Type(argType), Cardinality.SINGLETON)));
        List<F3Var> params = parmsBuffer.toList();
        F3FunctionValue fun = 
            F.FunctionValue(mods,
                            F.at(bodyExpr.pos).TypeClass(F.at(bodyExpr.pos).Type(resultType), Cardinality.ANY),
                            params,
                            body);
        // cast fun to function(Object):Object[];
        // for now we'll have to go through a cast to Object to do this
        ListBuffer<F3Type> typeList = ListBuffer.lb(); 
        typeList.append(makeType(F, names, "Object", Cardinality.SINGLETON));
        F3Expression toObject = 
            F.at(bodyExpr.pos).TypeCast(makeType(F, names, 
                                                 "Object", 
                                                 Cardinality.SINGLETON),
                                        fun);
        F3Expression objFun = 
            F.at(bodyExpr.pos).TypeCast(F.at(bodyExpr.pos).TypeFunctional(typeList.toList(), 
                                                                          makeType(F, names, "Object", 
                                                                                   Cardinality.ANY), 
                                                                          Cardinality.SINGLETON), 
                                        toObject);
        // cast clause input seq to Object[]
        F3Expression objInp = F.at(bodyExpr.pos).TypeCast(makeType(F,
                                                                   names,
                                                                   "Object",
                                                                   Cardinality.ANY), clause.getSequenceExpression());
        ListBuffer<F3Expression> argsBuffer = ListBuffer.lb();
        argsBuffer.append(objInp);
        argsBuffer.append(objFun);
        F3Ident id = F.at(bodyExpr.pos).Ident(names.fromString("f3"));
        F3Select sel = 
            F.at(bodyExpr.pos).Select(id, names.fromString("lang"), false);
        sel = F.at(bodyExpr.pos).Select(sel, names.fromString("Functions"), false);
        sel = F.at(bodyExpr.pos).Select(sel, names.fromString("map"), false);
        F3Expression apply = F.at(bodyExpr.pos).Apply(null, 
                                                      sel,
                                                      argsBuffer.toList());
        // now add a cast to the actual result type
        apply = F.at(bodyExpr.pos).TypeCast(F.at(bodyExpr.pos).TypeClass(F.at(bodyExpr.pos).Type(resultType), Cardinality.ANY), apply);

        return apply;
    }
}
