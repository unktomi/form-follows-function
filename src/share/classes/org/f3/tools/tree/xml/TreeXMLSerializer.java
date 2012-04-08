/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute instOf and/onReplace modify instOf
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that instOf will block useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY onReplace
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA onReplace visit www.sun.com if you need additional information onReplace
 * have any questions.
 */

package org.f3.tools.tree.xml;

import org.f3.api.tree.Tree.F3Kind;
import org.f3.api.tree.TypeTree;
import org.f3.runtime.Entry;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.util.Position;
import org.f3.tools.code.F3Flags;
import org.f3.tools.tree.F3AngleLiteral;
import org.f3.tools.tree.F3Assign;
import org.f3.tools.tree.F3AssignOp;
import org.f3.tools.tree.F3Binary;
import org.f3.tools.tree.F3Block;
import org.f3.tools.tree.F3Break;
import org.f3.tools.tree.F3Catch;
import org.f3.tools.tree.F3ClassDeclaration;
import org.f3.tools.tree.F3ColorLiteral;
import org.f3.tools.tree.F3Continue;
import org.f3.tools.tree.F3Erroneous;
import org.f3.tools.tree.F3Expression;
import org.f3.tools.tree.F3ForExpression;
import org.f3.tools.tree.F3ForExpressionInClause;
import org.f3.tools.tree.F3ObjectLiteralPart;
import org.f3.tools.tree.F3FunctionDefinition;
import org.f3.tools.tree.F3FunctionInvocation;
import org.f3.tools.tree.F3FunctionValue;
import org.f3.tools.tree.F3Ident;
import org.f3.tools.tree.F3IfExpression;
import org.f3.tools.tree.F3Import;
import org.f3.tools.tree.F3Indexof;
import org.f3.tools.tree.F3InitDefinition;
import org.f3.tools.tree.F3InstanceOf;
import org.f3.tools.tree.F3Instanciate;
import org.f3.tools.tree.F3InterpolateValue;
import org.f3.tools.tree.F3Invalidate;
import org.f3.tools.tree.F3KeyFrameLiteral;
import org.f3.tools.tree.F3LengthLiteral;
import org.f3.tools.tree.F3Literal;
import org.f3.tools.tree.F3Modifiers;
import org.f3.tools.tree.F3OnReplace;
import org.f3.tools.tree.F3OverrideClassVar;
import org.f3.tools.tree.F3Parens;
import org.f3.tools.tree.F3PostInitDefinition;
import org.f3.tools.tree.F3Return;
import org.f3.tools.tree.F3Script;
import org.f3.tools.tree.F3Select;
import org.f3.tools.tree.F3SequenceDelete;
import org.f3.tools.tree.F3SequenceEmpty;
import org.f3.tools.tree.F3SequenceExplicit;
import org.f3.tools.tree.F3SequenceIndexed;
import org.f3.tools.tree.F3SequenceInsert;
import org.f3.tools.tree.F3SequenceRange;
import org.f3.tools.tree.F3SequenceSlice;
import org.f3.tools.tree.F3Skip;
import org.f3.tools.tree.F3StringExpression;
import org.f3.tools.tree.F3Throw;
import org.f3.tools.tree.F3TimeLiteral;
import org.f3.tools.tree.F3Tree;
import org.f3.tools.tree.F3Try;
import org.f3.tools.tree.F3TypeAny;
import org.f3.tools.tree.F3TypeArray;
import org.f3.tools.tree.F3TypeCast;
import org.f3.tools.tree.F3TypeClass;
import org.f3.tools.tree.F3TypeFunctional;
import org.f3.tools.tree.F3TypeUnknown;
import org.f3.tools.tree.F3Unary;
import org.f3.tools.tree.F3Var;
import org.f3.tools.tree.F3VarInit;
import org.f3.tools.tree.F3VarRef;
import org.f3.tools.tree.F3WhileLoop;
import org.f3.tools.tree.F3Visitor;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.tools.JavaFileObject;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.f3.tools.tree.xml.Constants.*;

/**
 * This visitor that outputs SAX parser events for various Tree nodes of AST. 
 * This visitor can block used to generate XML representation of the AST.
 *
 * @author A. Sundararajan
 */
final class TreeXMLSerializer implements F3Visitor {
    // order of the methods as in F3Visitor

    public void visitScript(F3Script script) {
        endPositions = script.endPositions;
        startElement(F3, script);
        JavaFileObject file = script.getSourceFile();
        if (file != null) {
            sourceFileName = new File(file.getName()).getName();
            int extIndex = sourceFileName.indexOf(FILE_EXT);
            if (extIndex != -1) {
                sourceFileName = sourceFileName.substring(0, extIndex);
            }
            emitElement(FILE, file.toString());
        }
        docComments = script.docComments;
        emitTree(PACKAGE, script.getPackageName());
        startElement(DEFINITIONS);
        emitTreeList(script.defs);
        if (f3EntryMethod != null) {
            insideF3EntryMethod = true;
            try {
                emitTree(f3EntryMethod.getBodyExpression());
            } finally {
                insideF3EntryMethod = false;
            }
        }
        endElement(DEFINITIONS);
        emitAllSymbols();
        emitAllTypes();
        endElement(F3);
    }

    public void visitImport(F3Import imp) {
        startElement(IMPORT, imp);
        emitTree(imp.getQualifiedIdentifier());
        endElement(IMPORT);
    }

    public void visitSkip(F3Skip skip) {
        startElement(EMPTY, skip);
        endElement(EMPTY);
    }

    public void visitWhileLoop(F3WhileLoop whileLoop) {
        startElement(WHILE, whileLoop);
        emitTree(TEST, whileLoop.getCondition());
        emitTree(STATEMENT, whileLoop.getBody());
        endElement(WHILE);
    }

    public void visitTry(F3Try tt) {
        startElement(TRY, tt);
        emitTree(tt.getBlock());
        emitTreeList(CATCHES, tt.catchers);
        emitTree(FINALLY, tt.getFinallyBlock());
        endElement(TRY);
    }

    public void visitCatch(F3Catch ct) {
        startElement(CATCH, ct);
        emitTree(ct.getParameter());
        emitTree(ct.getBlock());
        endElement(CATCH);
    }

    public void visitIfExpression(F3IfExpression ifExpr) {
        startElement(IF, ifExpr);
        emitTree(TEST, ifExpr.getCondition());
        emitTree(THEN, ifExpr.getTrueExpression());
        emitTree(ELSE, ifExpr.getFalseExpression());
        endElement(IF);
    }

    public void visitBreak(F3Break bt) {
        startElement(BREAK, bt);
        Name label = bt.getLabel();
        if (label != null) {
            emitElement(LABEL, label.toString());
        }
        endElement(BREAK);
    }

    public void visitContinue(F3Continue ct) {
        startElement(CONTINUE, ct);
        Name label = ct.getLabel();
        if (label != null) {
            emitElement(LABEL, label.toString());
        }
        endElement(CONTINUE);
    }

    public void visitReturn(F3Return rt) {
        startElement(RETURN, rt);
        emitTree(rt.getExpression());
        endElement(RETURN);
    }
    
    public void visitThrow(F3Throw tt) {
        startElement(THROW, tt);
        emitTree(tt.getExpression());
        endElement(THROW);
    }

    public void visitFunctionInvocation(F3FunctionInvocation invoke) {
        startElement(INVOKE, invoke);
        emitTree(METHOD, invoke.getMethodSelect());
        emitTreeList(ARGUMENTS, invoke.getArguments());
        endElement(INVOKE);
    }

    public void visitParens(F3Parens parens) {
        startElement(PARENTHESIS, parens);
        emitTree(parens.getExpression());
        endElement(PARENTHESIS);
    }
    
    public void visitAssign(F3Assign assign) {
        startElement(ASSIGNMENT, assign);
        emitTree(LEFT, assign.getVariable());
        emitTree(RIGHT, assign.getExpression());
        endElement(ASSIGNMENT);
    }
    
    public void visitAssignop(F3AssignOp assignOp) {
        final String tagName = enumToName(assignOp.getF3Kind());
        startElement(tagName, assignOp);
        emitTree(LEFT, assignOp.getVariable());
        emitTree(RIGHT, assignOp.getExpression());
        endElement(tagName);
    }

    public void visitUnary(F3Unary unary) {
        F3Kind kind = unary.getF3Kind();
        final String tagName = (kind == null) ? SIZEOF : enumToName(unary.getF3Kind());
        startElement(tagName, unary);
        emitTree(unary.getExpression());
        endElement(tagName);
    }
    
    public void visitBinary(F3Binary binary) {
        final String tagName = enumToName(binary.getF3Kind());
        startElement(tagName, binary);
        emitTree(LEFT, binary.getLeftOperand());
        emitTree(RIGHT, binary.getRightOperand());
        endElement(tagName);
    }

    public void visitTypeCast(F3TypeCast typeCast) {
        startElement(CAST, typeCast);
        emitTree(TYPE, typeCast.getType());
        emitTree(EXPRESSION, typeCast.getExpression());
        endElement(CAST);
    }

    public void visitInstanceOf(F3InstanceOf instOf) {
        startElement(INSTANCEOF, instOf);
        emitTree(TYPE, instOf.getType());
        emitTree(EXPRESSION, instOf.getExpression());
        endElement(INSTANCEOF);
    }

    public void visitSelect(F3Select select) {
        startElement(SELECT, select, select.sym);
        emitTree(EXPRESSION, select.getExpression());
        Name name = select.getIdentifier();
        if (name != null) {
            emitElement(MEMBER, name.toString());
        }
        endElement(SELECT);
    }
    
    public void visitIdent(F3Ident ident) {
        startElement(IDENTIFIER, ident, ident.sym);
        Name name = ident.getName();
        if (name != null) {
            emitData(name.toString());
        }
        endElement(IDENTIFIER);
    }

    public void visitLiteral(F3Literal literal) {
        String tagName;
        F3Kind kind = literal.getF3Kind();
        switch (kind) {
            case INT_LITERAL:
                tagName = INT_LITERAL;
                break;
            case LONG_LITERAL:
                tagName = LONG_LITERAL;
                break;
            case FLOAT_LITERAL:
                tagName = FLOAT_LITERAL;
                break;
            case DOUBLE_LITERAL:
                tagName = DOUBLE_LITERAL;
                break;
            case BOOLEAN_LITERAL:
                tagName = Boolean.TRUE.equals(literal.getValue()) ? TRUE : FALSE;
                break;
            case STRING_LITERAL:
                tagName = STRING_LITERAL;
                break;
            case NULL_LITERAL:
                tagName = NULL;
                break;
            default:
                throw new IllegalArgumentException("unknown literal kind : " + kind);
        }
        startElement(tagName, literal);
        Object value = literal.getValue();
        if (value != null && !(value instanceof Boolean)) {
            emitData(value.toString());
        }
        endElement(tagName);
    }

    public void visitModifiers(F3Modifiers modifiers) {
        emitModifiers((F3Modifiers)modifiers);
    }

    public void visitErroneous(F3Erroneous error) {
        startElement(ERROR, error);
        emitTreeList(error.getErrorTrees());
        endElement(ERROR);
    }

    public void visitClassDeclaration(F3ClassDeclaration classDecl) {
        List<F3Tree> members = classDecl.getMembers();
        List<F3Tree> staticMembers = new ArrayList<F3Tree>();
        List<F3Tree> instanceMembers = new ArrayList<F3Tree>();
        for (F3Tree m : members) {
            if (m instanceof F3FunctionDefinition) {
                F3FunctionDefinition func = (F3FunctionDefinition)m;
                if (f3EntryMethodName.equals(func.getName().toString())) {
                    f3EntryMethod = func;
                }
                if (func.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    staticMembers.add(m);
                } else {
                    instanceMembers.add(m);
                }
            } else if (m instanceof F3Var) {
                F3Var var = (F3Var)m;
                if (var.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    staticMembers.add(m);
                } else {
                    instanceMembers.add(m);
                }
            } else if (m instanceof F3ClassDeclaration) {
                staticMembers.add(m);
            } else {
                // add anything else to instance members list
                instanceMembers.add(m);
            }
        }

        // no instance member => this is a module class generated
        // to hold file level variables and functions.
        if (instanceMembers.isEmpty()) {
            emitTreeList(classDecl.getMembers());
        } else {
            // emit static members that appear before the class in source order
            int classPos = classDecl.pos;
            for (F3Tree item : staticMembers) {
                if (item.pos <= classPos) {
                    emitTree(item);
                }
            }
            startElement(CLASS, classDecl, classDecl.sym);
            Name name = classDecl.getSimpleName();
            if (name != null) {
                emitElement(NAME, name.toString());
            }
            emitModifiers(classDecl.getModifiers());
            emitTreeList(EXTENDS, classDecl.getSupertypes());
            emitTreeList(MEMBERS, instanceMembers);
            endElement(CLASS);
            // emit static members that appear after the class in source order
            for (F3Tree item : staticMembers) {
                if (item.pos > classPos) {
                    emitTree(item);
                }
            }
        }
    }

    public void visitFunctionDefinition(F3FunctionDefinition funcDef) {
        if (funcDef.equals(f3EntryMethod)) {
            // handled specially, return from here
            return;
        } else {
            startElement(FUNCTION, funcDef, funcDef.sym);
            Name name = funcDef.getName();
            if (name != null) {
                emitElement(NAME, name.toString());
            }
            emitModifiers(funcDef.getModifiers());
            emitTree(RETURN_TYPE, funcDef.getF3ReturnType());
            emitTreeList(PARAMETERS, funcDef.getParams());
            emitTree(funcDef.getBodyExpression());
            endElement(FUNCTION);
        }
    }

    public void visitInitDefinition(F3InitDefinition initDef) {
        startElement(INIT, initDef);
        emitTree(initDef.getBody());
        endElement(INIT);
    }

    public void visitPostInitDefinition(F3PostInitDefinition postInitDef) {
        startElement(POSTINIT, postInitDef);
        emitTree(postInitDef.getBody());
        endElement(POSTINIT);
    }

    public void visitStringExpression(F3StringExpression strExpr) {
        startElement(STRING_EXPRESSION, strExpr);
        String translationKey = strExpr.translationKey;
        if (translationKey != null) {
            emitElement(STR_TRANS_KEY, translationKey);
        }
        List<F3Expression> parts = strExpr.getParts();
        int i;
        for (i = 0; i < parts.size() - 1; i += 3) {
            emitTree(PART, parts.get(i));
            startElement(PART);
            F3Expression format = parts.get(i + 1);
            if (format != null) {
                emitTree(FORMAT, format);
            }
            emitTree(EXPRESSION, parts.get(i + 2));
            endElement(PART);
        }
        emitTree(PART, parts.get(i));
        endElement(STRING_EXPRESSION);
    }

    public void visitInstanciate(F3Instanciate instanciate) {
        List<F3Expression> args = instanciate.getArgs();
        final String tagName = ((args == null) || args.isEmpty())? OBJECT_LITERAL : NEW;
        startElement(tagName, instanciate);
        emitTree(CLASS, instanciate.getIdentifier());
        emitTreeList(ARGUMENTS, instanciate.getArgs());
        startElement(DEFINITIONS);
        emitTreeList(instanciate.getLocalvars());
        emitTreeList(instanciate.getParts());
        F3ClassDeclaration clazz = instanciate.getClassBody();
        if (clazz != null) {
            emitTreeList(clazz.getMembers());
        }
        endElement(DEFINITIONS);
        endElement(tagName);
    }

    public void visitObjectLiteralPart(F3ObjectLiteralPart objLitPart) {
        startElement(OBJECT_LITERAL_INIT, objLitPart, objLitPart.sym);
        Name name = objLitPart.getName();
        if (name != null) {
            emitElement(NAME, name.toString());
        }
        emitElement(BIND_STATUS, bindStatusToString(objLitPart.getBindStatus()));
        emitTree(EXPRESSION, objLitPart.getExpression());
        endElement(OBJECT_LITERAL_INIT);
    }

    public void visitTypeAny(F3TypeAny typeAny) {
        startElement(TYPE_ANY, typeAny);
        TypeTree.Cardinality cardinality = typeAny.getCardinality();
        emitElement(CARDINALITY, cardinalityToString(cardinality));
        endElement(TYPE_ANY);
    }

    public void visitTypeClass(F3TypeClass typeClass) {
        startElement(TYPE_CLASS, typeClass, getSymbolField(typeClass));
        F3Expression name = typeClass.getClassName();
        if (name instanceof F3Ident) {
            String mappedName = primTypeNames.get(name.toString());
            if (mappedName != null) {
                startElement(CLASS);
                    startElement(IDENTIFIER);
                        emitData(mappedName);
                    endElement(IDENTIFIER);
                endElement(CLASS);
            } else {
                emitTree(CLASS, name);
            }
        } else {
            emitTree(CLASS, typeClass.getClassName());
        }
        TypeTree.Cardinality cardinality = typeClass.getCardinality();
        emitElement(CARDINALITY, cardinalityToString(cardinality));
        endElement(TYPE_CLASS);
    }

    public void visitTypeFunctional(F3TypeFunctional typeFunc) {
        startElement(TYPE_FUNCTIONAL, typeFunc);
        emitTreeList(PARAMETERS, typeFunc.getParams());
        emitTree(RETURN_TYPE, typeFunc.restype);
        TypeTree.Cardinality cardinality = typeFunc.getCardinality();
        emitElement(CARDINALITY, cardinalityToString(cardinality));
        endElement(TYPE_FUNCTIONAL);
    }

    public void visitTypeArray(F3TypeArray typeArray) {
        startElement(TYPE_ARRAY, typeArray);
        emitTree(typeArray.getElementType());
        endElement(TYPE_ARRAY);
    }

    public void visitTypeUnknown(F3TypeUnknown typeUnknown) {
        startElement(TYPE_UNKNOWN, typeUnknown);
        endElement(TYPE_UNKNOWN);
    }

    public void visitVar(F3Var var) {
        F3Modifiers mods = var.getModifiers();
        String tagName = VAR;
        if (mods != null) {
            // ignore static variables inside "run" method
            if (insideF3EntryMethod && (mods.flags & Flags.STATIC) != 0) {
                return;
            }
            if ((mods.flags & F3Flags.IS_DEF) != 0) {
                tagName = DEF;
            }
        }
        startElement(tagName, var, var.sym);
        Name name = var.getName();
        if (name != null) {
            emitElement(NAME, name.toString());
        }
        emitModifiers(mods);
        emitTree(TYPE, var.getF3Type());
        emitElement(BIND_STATUS, bindStatusToString(var.getBindStatus()));
        emitTree(INITIAL_VALUE, var.getInitializer());
        F3OnReplace onReplace = var.getOnReplace();
        emitTree(onReplace);
        F3OnReplace onInvalidate = var.getOnInvalidate();
        emitTree(onInvalidate);
        endElement(tagName);
    }

    public void visitVarInit(F3VarInit tree) {
        // ignore - not from source introduced later
    }

    public void visitVarRef(F3VarRef tree) {
        // ignore - not in source introduced in lower
    }

    public void visitOnReplace(F3OnReplace onReplace) {
        final String tagName =
            (onReplace.getTriggerKind() == F3OnReplace.Kind.ONINVALIDATE)?
            ON_INVALIDATE : ON_REPLACE;
        startElement(tagName, onReplace);
        emitTree(FIRST_INDEX, onReplace.getFirstIndex());
        emitTree(LAST_INDEX, onReplace.getLastIndex());
        emitTree(NEW_ELEMENTS, onReplace.getNewElements());
        emitTree(OLD_VALUE, onReplace.getOldValue());
        if (onReplace.getEndKind() == F3SequenceSlice.END_EXCLUSIVE) {
            emitElement(SLICE_END_KIND, EXCLUSIVE);
        }
        emitTree(onReplace.getBody());
        endElement(tagName);
    }

    public void visitBlockExpression(F3Block block) {
        startElement(BLOCK_EXPRESSION, block);
        emitTreeList(STATEMENTS, block.getStmts());
        emitTree(VALUE, block.getValue());
        endElement(BLOCK_EXPRESSION);
    }

    public void visitFunctionValue(F3FunctionValue funcValue) {
        startElement(ANON_FUNCTION, funcValue);
        emitTree(RETURN_TYPE, funcValue.getType());
        emitTreeList(PARAMETERS, funcValue.getParams());
        emitTree(funcValue.getBodyExpression());
        endElement(ANON_FUNCTION);
    }

    public void visitSequenceEmpty(F3SequenceEmpty seqEmpty) {
        startElement(SEQUENCE_EMPTY, seqEmpty);
        endElement(SEQUENCE_EMPTY);
    }

    public void visitSequenceRange(F3SequenceRange seqRange) {
        startElement(SEQUENCE_RANGE, seqRange);
        emitTree(LOWER, seqRange.getLower());
        emitTree(UPPER, seqRange.getUpper());
        emitTree(STEP, seqRange.getStepOrNull());
        emitElement(EXCLUSIVE, Boolean.toString(seqRange.isExclusive()));
        endElement(SEQUENCE_RANGE);
    }

    public void visitSequenceExplicit(F3SequenceExplicit seqExplicit) {
        startElement(SEQUENCE_EXPLICIT, seqExplicit);
        emitTreeList(ITEMS, seqExplicit.getItems());
        endElement(SEQUENCE_EXPLICIT);
    }

    public void visitSequenceIndexed(F3SequenceIndexed seqIndexed) {
        startElement(SEQUENCE_INDEXED, seqIndexed);
        emitTree(SEQUENCE, seqIndexed.getSequence());
        emitTree(INDEX, seqIndexed.getIndex());
        endElement(SEQUENCE_INDEXED);
    }

    public void visitSequenceSlice(F3SequenceSlice seqSlice) {
        startElement(SEQUENCE_SLICE, seqSlice);
        emitTree(SEQUENCE, seqSlice.getSequence());
        emitTree(FIRST, seqSlice.getFirstIndex());
        emitTree(LAST, seqSlice.getLastIndex());
        if (seqSlice.getEndKind() == seqSlice.END_EXCLUSIVE) {
            emitElement(SLICE_END_KIND, EXCLUSIVE);
        }
        endElement(SEQUENCE_SLICE);
    }

    public void visitSequenceInsert(F3SequenceInsert seqInsert) {
        startElement(SEQUENCE_INSERT, seqInsert);
        emitTree(SEQUENCE, seqInsert.getSequence());
        emitTree(ELEMENT, seqInsert.getElement());
        endElement(SEQUENCE_INSERT);
    }

    public void visitSequenceDelete(F3SequenceDelete seqDelete) {
        startElement(SEQUENCE_DELETE, seqDelete);
        emitTree(SEQUENCE, seqDelete.getSequence());
        emitTree(ELEMENT, seqDelete.getElement());
        endElement(SEQUENCE_DELETE);
    }

    public void visitInvalidate(F3Invalidate invalidate) {
        startElement(INVALIDATE, invalidate);
        startElement(VAR);
        emitTree(invalidate.getVariable());
        endElement(VAR);
        endElement(INVALIDATE);
    }

    public void visitForExpression(F3ForExpression forExpr) {
        startElement(FOR, forExpr);
        emitTreeList(IN, forExpr.getForExpressionInClauses());
        emitTree(BODY, forExpr.getBodyExpression());
        endElement(FOR);
    }

    public void visitForExpressionInClause(F3ForExpressionInClause inClause) {
        startElement(LIST_ITEM, inClause);
        emitTree(inClause.getVariable());
        emitTree(SEQUENCE, inClause.getSequenceExpression());
        emitTree(WHERE, inClause.getWhereExpression());
        endElement(LIST_ITEM);
    }

    public void visitIndexof(F3Indexof indexOf) {
        startElement(INDEXOF, indexOf);
        emitTree(indexOf.getForVarIdentifier());
        endElement(INDEXOF);
    }

    public void visitTimeLiteral(F3TimeLiteral timeLiteral) {
        startElement(TIME_LITERAL, timeLiteral);
        emitData(timeLiteral.getValue().toString());
        endElement(TIME_LITERAL);
    }

    public void visitLengthLiteral(F3LengthLiteral lengthLiteral) {
        startElement(LENGTH_LITERAL, lengthLiteral);
        emitData(lengthLiteral.getValue().toString());
        endElement(LENGTH_LITERAL);
    }

    public void visitAngleLiteral(F3AngleLiteral angleLiteral) {
        startElement(ANGLE_LITERAL, angleLiteral);
        emitData(angleLiteral.getValue().toString());
        endElement(ANGLE_LITERAL);
    }

    public void visitColorLiteral(F3ColorLiteral colorLiteral) {
        startElement(COLOR_LITERAL, colorLiteral);
        emitData(colorLiteral.getValue().toString());
        endElement(COLOR_LITERAL);
    }

    public void visitOverrideClassVar(F3OverrideClassVar overrideVar) {
        startElement(OVERRIDE_VAR, overrideVar);
        emitElement(NAME, overrideVar.getName().toString());
        emitTree(EXPRESSION, overrideVar.getInitializer());
        emitTree(overrideVar.getOnReplace());
        emitTree(overrideVar.getOnInvalidate());
        endElement(OVERRIDE_VAR);
    }

    public void visitInterpolateValue(F3InterpolateValue interpolateValue) {
        startElement(INTERPOLATE_VALUE, interpolateValue);
        emitTree(ATTRIBUTE, interpolateValue.getAttribute());
        emitTree(VALUE, interpolateValue.getValue());
        emitTree(INTERPOLATION, interpolateValue.getInterpolation());
        endElement(INTERPOLATE_VALUE);
    }

    public void visitKeyFrameLiteral(F3KeyFrameLiteral keyFrame) {
        startElement(KEYFRAME_LITERAL, keyFrame);
        emitTree(START_DURATION, keyFrame.getStartDuration());
        emitTreeList(INTERPOLATION_VALUES, keyFrame.getInterpolationValues());
        emitTree(TRIGGER, keyFrame.getTrigger());
        endElement(KEYFRAME_LITERAL);
    }

    // package private stuff below this point

    // accepts SAX content handler on which SAX events are called
    TreeXMLSerializer(ContentHandler handler) {
        this.handler = handler;
        this.f3EntryMethodName = Entry.entryMethodName();
    }

    // start outputting SAX events based on given compilation unit instanciate
    void start(F3Tree ut) {
        try {
            handler.startDocument();
            ut.accept(this);
            handler.endDocument();
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    Symbol idToSymbol(String id) {
        return idToSymbol.get(id);
    }

    Type idToType(String id) {
        return idToType.get(id);
    }

    //-- Internals only below this point

    private static Map<String, String> primTypeNames;
    static {
        primTypeNames = new HashMap<String, String>();
        primTypeNames.put("boolean", "Boolean");
        primTypeNames.put("char", "Character");
        primTypeNames.put("byte", "Byte");
        primTypeNames.put("short", "Short");
        primTypeNames.put("int", "Integer");
        primTypeNames.put("long", "Long");
        primTypeNames.put("float", "Float");
        primTypeNames.put("double", "Double");
    }

    private Map<JCTree, String> docComments;

    /*
     * Symbols and types are networks (and not trees). We handle cycles by
     * generating initDef and idrefs (as is common in XML representations). Note
     * that the symbols and types are emitted only if XML representation is
     * created after "enter" onReplace "analyze" phase. If XML document is created
     * just after "parse" phase, we emit only the instanciate nodes.
     */
    // next symbol initDef to block used -- symbol initDef is just a common
    // prefix concatenated with a number
    private int nextSymbol = 1;

    // Symbol to symbol initDef map
    private Map<Symbol, String> symbolToId = new HashMap<Symbol, String>();
    private Map<String, Symbol> idToSymbol = new HashMap<String, Symbol>();

    // next type initDef to block used --  type initDef is just a common
    // prefix concatenated with a number
    private int nextType = 1;
    // Type to type initDef map
    private Map<Type, String> typeToId = new HashMap<Type, String>();
    private Map<String, Type> idToType = new HashMap<String, Type>();

    private final AttributesImpl attrs = new AttributesImpl();

    // SAX sink to output SAX events
    private ContentHandler handler;

    // end positions map of the current compilation unit
    private Map<JCTree, Integer> endPositions;
    private String f3EntryMethodName;
    private F3FunctionDefinition f3EntryMethod;
    private boolean insideF3EntryMethod;
    private String sourceFileName;

    private Symbol getSymbolField(F3Tree jcTree) {
        try {
            // Only few JCTree subclasses have "sym" field.
            // So, we need to use reflection to access the same.
            Field field = jcTree.getClass().getDeclaredField("sym");
            field.setAccessible(true);
            return (Symbol) field.get(jcTree);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    // put Symbol into symbol map and return initDef
    private String putSymbol(Symbol sym) {
        if (symbolToId.containsKey(sym)) {
            return symbolToId.get(sym);
        }
        String id = SYMID_PREFIX + nextSymbol;
        symbolToId.put(sym, id);
        idToSymbol.put(id, sym);
        Type type = sym.asType();
        if (type != null) {
            putType(type);
        }
        nextSymbol++;
        Symbol owner = sym.getEnclosingElement();
        if (owner != null) {
            putSymbol(owner);
        }
        return id;
    }

    // put Type into type map and return initDef
    private String putType(Type type) {
        if (typeToId.containsKey(type)) {
            return typeToId.get(type);
        }
        String id = TYPEID_PREFIX + nextType;
        typeToId.put(type, id);
        idToType.put(id, type);
        nextType++;
        return id;
    }

    // emit all symbol elements
    private void emitAllSymbols() {
        if (symbolToId.isEmpty()) {
            return;
        }
        startElement(SYMBOLS);
        for (Map.Entry<Symbol, String> entry : symbolToId.entrySet()) {
            Symbol sym = entry.getKey();
            Type type = sym.asType();
            attrs.clear();
            attrs.addAttribute(NULL_NS_URI, ID, ID, ATTR_ID, entry.getValue());
            if (type != null && typeToId.containsKey(type)) {
                String ref = typeToId.get(type);
                attrs.addAttribute(NULL_NS_URI, TYPEREF, TYPEREF, ATTR_IDREF, ref);
            }
            startElement(SYMBOL, attrs);
            Name name = sym.getSimpleName();
            if (name != null) {
                emitElement(NAME, name.toString());
                Name qualifiedName = sym.getQualifiedName();
                if (qualifiedName != null && !name.equals(qualifiedName)) {
                    emitElement(FULL_NAME, qualifiedName.toString());
                }
            }
            emitElement(KIND, enumToName(sym.getKind()));
            startElement(MODIFIERS);
            emitFlags(sym.flags());
            endElement(MODIFIERS);
            attrs.clear();
            Symbol owner = sym.getEnclosingElement();
            if (owner != null) {
                attrs.addAttribute(NULL_NS_URI, SYMREF, SYMREF, ATTR_IDREF, symbolToId.get(owner));
                startElement(OWNER, attrs);
                endElement(OWNER);
            }
            endElement(SYMBOL);
        }
        endElement(SYMBOLS);
    }

    // emit all type elements
    private void emitAllTypes() {
        if (typeToId.isEmpty()) {
            return;
        }
        startElement(TYPES);
        for (Map.Entry<Type, String> entry : typeToId.entrySet()) {
            attrs.clear();
            Type type = entry.getKey();
            String id = entry.getValue();
            attrs.addAttribute(NULL_NS_URI, ID, ID, ATTR_ID, entry.getValue());
            startElement(TYPE, attrs);
            emitElement(NAME, type.toString());
            emitElement(KIND, enumToName(type.getKind()));
            endElement(TYPE);
        }
        endElement(TYPES);
    }

    private void startElement(String name, F3Tree tree) {
        startElement(name, tree, null);
    }

    private void startElement(String name, F3Tree tree, Symbol sym) {
        attrs.clear();
        if (sym != null) {
            String ref = putSymbol(sym);
            attrs.addAttribute(NULL_NS_URI, SYMREF, SYMREF, ATTR_IDREF, ref);
        }

        Type type = tree.type;
        if (type != null) {
            String ref = putType(type);
            attrs.addAttribute(NULL_NS_URI, TYPEREF, TYPEREF, ATTR_IDREF, ref);
        }

        if (tree.pos != Position.NOPOS) {
            attrs.addAttribute(NULL_NS_URI, POSITION, POSITION, ATTR_CDATA, Integer.toString(tree.pos));
        }
        if (endPositions != null) {
            int endPos = tree.getEndPosition(endPositions);
            if (endPos != Position.NOPOS) {
                attrs.addAttribute(NULL_NS_URI, END_POSITION, END_POSITION, ATTR_CDATA, Integer.toString(endPos));
            }
        }
        startElement(name, attrs);
        if (docComments != null && docComments.containsKey(tree)) {
            emitElement(DOC_COMMENT, docComments.get(tree));
        }
    }

    private void startElement(String element) {
        attrs.clear();
        startElement(element, attrs);
    }

    private void startElement(String element, Attributes attrs) {
        try {
            handler.startElement(F3ASTXML_NS, element, F3ASTXML_PREFIX + element, attrs);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void endElement(String element) {
        try {
            handler.endElement(F3ASTXML_NS, element, F3ASTXML_PREFIX + element);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void emitData(String data) {
        if (data == null) {
            return;
        }
        char[] chars = data.toCharArray();
        try {
            handler.characters(chars, 0, chars.length);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void emitElement(String name, String d) {
        if (d != null) {
            startElement(name);
            emitData(d);
            endElement(name);
        }
    }

    private void emitModifiers(F3Modifiers mods) {
        if (mods != null) {
            startElement(MODIFIERS, mods);
            emitFlags(mods.flags);
            endElement(MODIFIERS);
        }
    }
    
    private void emitFlags(long flagBits) {
        // Java flags applicable here
        if ((flagBits & Flags.ABSTRACT) != 0) {
            emitListItem(ABSTRACT);
        }
        if ((flagBits & Flags.PROTECTED) != 0) {
            emitListItem(PROTECTED);
        }
        if ((flagBits & Flags.PUBLIC) != 0) {
            emitListItem(PUBLIC);
        }
            
        // Now handle F3 specific flags
        if ((flagBits & F3Flags.DEFAULT) != 0) {
            emitListItem(DEFAULT);
        }
        if ((flagBits & F3Flags.PUBLIC_INIT) != 0) {
            emitListItem(PUBLIC_INIT);
        }
        if ((flagBits & F3Flags.PUBLIC_READ) != 0) {
            emitListItem(PUBLIC_READ);
        }
        if ((flagBits & F3Flags.PACKAGE_ACCESS) != 0) {
            emitListItem(PACKAGE_ACCESS);
        }
        if ((flagBits & F3Flags.SCRIPT_PRIVATE) != 0) {
            emitListItem(SCRIPT_PRIVATE);
        }
        if ((flagBits & F3Flags.OVERRIDE) != 0) {
            emitListItem(OVERRIDE);
        }
        if ((flagBits & F3Flags.MIXIN) != 0) {
            emitListItem(MIXIN);
        }
        if ((flagBits & F3Flags.BOUND) != 0) {
            emitListItem(BOUND);
        }
    }

    private void emitListItem(String data) {
        startElement(LIST_ITEM);
        emitData(data);
        endElement(LIST_ITEM);
    }

    private void emitTree(F3Tree t) {
        if (t != null) {
            t.accept(this);
        }
    }

    private void emitTree(String name, F3Tree t) {
        if (t != null) {
            if (name != null) {
                startElement(name);
            }
            t.accept(this);
            if (name != null) {
                endElement(name);
            }
        }
    }

    private void emitTreeList(List<? extends F3Tree> list) {
        emitTreeList(null, null, list);
    }

    private void emitTreeList(String name, List<? extends F3Tree> list) {
        emitTreeList(name, null, list);
    }

    private void emitTreeList(String name, String itemName, List<? extends F3Tree> list) {
        if (list != null && !list.isEmpty()) {
            if (name != null) {
                startElement(name);
            }
            for (F3Tree item : list) {
                emitTree(itemName, item);
            }
            if (name != null) {
                endElement(name);
            }
        }
    }

    private RuntimeException wrapException(Exception exp) {
        if (exp instanceof RuntimeException) {
            return (RuntimeException) exp;
        } else {
            return new RuntimeException(exp);
        }
    }
}
