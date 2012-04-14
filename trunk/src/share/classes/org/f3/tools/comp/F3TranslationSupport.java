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

import java.io.OutputStreamWriter;

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.BoundKind;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Kinds;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.code.TypeTags;
import com.sun.tools.mjavac.tree.JCTree;
import com.sun.tools.mjavac.tree.JCTree.JCAnnotation;
import com.sun.tools.mjavac.tree.JCTree.JCClassDecl;
import com.sun.tools.mjavac.tree.JCTree.JCExpression;
import com.sun.tools.mjavac.tree.JCTree.JCIdent;
import com.sun.tools.mjavac.tree.JCTree.JCMethodDecl;
import com.sun.tools.mjavac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.mjavac.tree.JCTree.JCModifiers;
import com.sun.tools.mjavac.tree.JCTree.JCStatement;
import com.sun.tools.mjavac.tree.JCTree.JCTypeParameter;
import com.sun.tools.mjavac.tree.JCTree.JCVariableDecl;
import com.sun.tools.mjavac.tree.Pretty;
import com.sun.tools.mjavac.tree.TreeMaker;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Position;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3TypeRepresentation;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import static org.f3.tools.comp.F3Defs.*;
import org.f3.tools.tree.*;
import com.sun.tools.mjavac.tree.JCTree.JCAssert;
import com.sun.tools.mjavac.tree.JCTree.JCBlock;
import com.sun.tools.mjavac.tree.JCTree.JCCatch;
import com.sun.tools.mjavac.util.Options;
import java.util.Set;
import java.util.HashSet;

/**
 * Common support routines needed for translation
 *
 * @author Robert Field
 * @author Jim Laskey
 */
public abstract class F3TranslationSupport {
    protected final F3Defs defs;
    protected final Log log;
    protected final F3TreeMaker f3make;
    protected final TreeMaker make; // translation should yield a Java AST, use f3make when building F3 trees
    protected final Name.Table names;
    protected final F3Resolve rs;
    protected final F3Symtab syms;
    protected final F3Types types;
    protected final Options options;
    protected final F3PreTranslationSupport preTrans;

    /*
     * other instance information
     */
    private int syntheticNameCounter = 0;

    protected F3TranslationSupport(Context context) {
        make = TreeMaker.instance(context);
        f3make = F3TreeMaker.instance(context);
        log = Log.instance(context);
        names = Name.Table.instance(context);
        types = F3Types.instance(context);
        syms = (F3Symtab)F3Symtab.instance(context);
        rs = F3Resolve.instance(context);
        defs = F3Defs.instance(context);
        options = Options.instance(context);
        preTrans = F3PreTranslationSupport.instance(context);

        syntheticNameCounter = 0;
    }

    public static class NotYetImplementedException extends RuntimeException {
        NotYetImplementedException(String msg) {
            super(msg);
        }
    }

    static JCExpression TODO(String msg) {
        throw new NotYetImplementedException("Not yet implemented: " + msg);
    }

    protected Symbol expressionSymbol(F3Expression tree) {
        if (tree == null) {
            return null;
        }
        switch (tree.getF3Tag()) {
            case IDENT:
                return ((F3Ident) tree).sym;
            case SELECT:
                return ((F3Select) tree).sym;
            case TYPECAST:
                //TODO: This is suspicious -- expressionSymbol should return
                // a symbol which fully represents the expression.  This is lossy.
                return expressionSymbol(((F3TypeCast)tree).getExpression());
            default:
                return null;
        }
    }

    protected F3VarSymbol varSymbol(F3Expression tree) {
        if (tree == null) {
            return null;
        }
        Symbol sym;
        switch (tree.getF3Tag()) {
            case IDENT:
                sym = ((F3Ident) tree).sym;
                break;
            case SELECT:
                sym = ((F3Select) tree).sym;
                break;
            default:
                return null;
        }
        if (sym instanceof F3VarSymbol)
            return (F3VarSymbol) sym;
        else
            return null;
    }

    protected boolean isValueFromJava(final F3Expression expr) {
        // The value could come from Java if it is a variable, or a function result.
        Symbol sym = expressionSymbol(expr);
        if (sym != null && !types.isF3Class(sym.owner)) {
            return true;
        }

        // test for function
        if (expr.getF3Tag() == F3Tag.APPLY) {
            F3Expression func = ((F3FunctionInvocation)expr).getMethodSelect();
            if (isValueFromJava(func)) {
                return true;
            }
        }
        return false;
    }

    boolean hasSideEffects(F3Expression expr) {
        class SideEffectScanner extends F3TreeScanner {

            boolean hse = false;

            private void markSideEffects() {
                hse = true;
            }

            @Override
            public void visitBlockExpression(F3Block tree) {
                markSideEffects(); // maybe doesn't but covers all statements
            }

            @Override
            public void visitUnary(F3Unary tree) {
                markSideEffects();
            }

            @Override
            public void visitAssign(F3Assign tree) {
                markSideEffects();
            }

            @Override
            public void visitAssignop(F3AssignOp tree) {
                markSideEffects();
            }

            @Override
            public void visitInstanciate(F3Instanciate tree) {
                markSideEffects();
            }

            @Override
            public void visitFunctionInvocation(F3FunctionInvocation tree) {
                markSideEffects();
            }

            @Override
            public void visitSelect(F3Select tree) {
                // Doesn't really have side-effects but the duplicate null checking is awful
                // TODO: do this in a cleaner way
                markSideEffects();
            }
        }
        SideEffectScanner scanner = new SideEffectScanner();
        scanner.scan(expr);
        return scanner.hse;
    }

    /**
     * Return the generated interface name corresponding to the class
     * */
    protected Name interfaceName(F3ClassDeclaration cDecl) {
        Name name = cDecl.getName();
        if (!cDecl.isMixinClass())
            return name;
        return names.fromString(name.toString() + mixinClassSuffix);
    }

    protected boolean isMixinClass(ClassSymbol sym) {
        return (sym.flags_field & F3Flags.MIXIN) != 0;
    }
    
    protected boolean isAnonClass(ClassSymbol sym) {
        final long flags = (Flags.SYNTHETIC | Flags.FINAL);
        return (sym.flags_field & flags) == flags;
    }
    
    protected boolean isLocalClass(ClassSymbol sym) {
        return sym.owner.kind == Kinds.MTH;
    }
    
    protected boolean isBoundFuncClass(ClassSymbol sym) {
        return (sym.flags_field & F3Flags.F3_BOUND_FUNCTION_CLASS) != 0L;
    }

    protected JCExpression makeIdentifier(DiagnosticPosition diagPos, String str) {
        assert str.indexOf('<') < 0 : "attempt to parse a type with 'Identifier'.  Use TypeTree";
        JCExpression tree = null;
        int inx;
        int lastInx = 0;
        do {
            inx = str.indexOf('.', lastInx);
            int endInx;
            if (inx < 0) {
                endInx = str.length();
            } else {
                endInx = inx;
            }
            String part = str.substring(lastInx, endInx);
            Name partName = names.fromString(part);
            tree = tree == null?
                make.at(diagPos).Ident(partName) :
                make.at(diagPos).Select(tree, partName);
            lastInx = endInx + 1;
        } while (inx >= 0);
        return tree;
    }

    protected JCExpression makeQualifiedTree(DiagnosticPosition diagPos, String str) {
        JCExpression tree = null;
        int inx;
        int lastInx = 0;
        do {
            inx = str.indexOf('.', lastInx);
            int endInx;
            if (inx < 0) {
                endInx = str.length();
            } else {
                endInx = inx;
            }
            String part = str.substring(lastInx, endInx);
            Name partName = names.fromString(part);
            tree = tree == null?
                makeIdentOfPresetKind(diagPos, partName, Kinds.PCK) :
                make.at(diagPos).Select(tree, partName);
            lastInx = endInx + 1;
        } while (inx >= 0);
        return tree;
    }

    
    protected JCExpression makeAccessExpression(DiagnosticPosition diagPos, Symbol sym, boolean makeIntf) {
        Symbol owner = sym.owner;
        Name name = sym.name;
        switch (sym.kind) {
            case Kinds.PCK:
                if (name == null || name == name.table.empty) {
                    return null;
                }
                break;
            case Kinds.TYP:
                if (owner.type != null && owner.type.tag == TypeTags.TYPEVAR) {
                    throw new RuntimeException("TYPEVAR: " + owner.type);
                }
                if (makeIntf) {
                    name = names.fromString(name.toString() + mixinClassSuffix);
                }
                break;
            default:
                return null;
        }
        if (owner != null) {
            JCExpression oExpr = makeAccessExpression(diagPos, owner, false);
            if (oExpr != null) {
                return make.at(diagPos).Select(oExpr, name);
            }
        }
        if (sym.kind == Kinds.PCK) {
            return makeIdentOfPresetKind(diagPos, name, Kinds.PCK);
        } else {
            return make.at(diagPos).Ident(name);
        }
    }

    public List<JCExpression> makeTypes(DiagnosticPosition diagPos, List<Type> ts) {
	ListBuffer<JCExpression> lb = ListBuffer.lb();
	for (Type t: ts) lb.append(makeType(diagPos, t));
	return lb.toList();
    }

    /**
     * Build a Java AST representing the specified type.
     * Convert F3 class references to interface references.
     * */
    public JCExpression makeType(DiagnosticPosition diagPos, Type t) {
        return makeType(diagPos, t, true);
    }

    /**
     * Build a Java AST representing the specified type.
     * If "makeIntf" is set, convert F3 class references to interface references.
     * */
    public JCExpression makeType(DiagnosticPosition diagPos, Type t, boolean makeIntf) {
        while (t instanceof CapturedType) {
            WildcardType wtype = ((CapturedType) t).wildcard;
            // A kludge for Class.newInstance (and maybe other cases):
            // Applying newinstance of an object of type Class<? extends T>
            // should yield an instance of T rather than a ? extends T,
            // which would confuse the back-end.
            t = wtype.kind == BoundKind.EXTENDS ? wtype.type : wtype;
        }
        JCExpression texp = makeTypeTreeInner(diagPos, t, makeIntf);
        texp.type = t;
        return texp;
    }

    private JCExpression makeTypeTreeInner(DiagnosticPosition diagPos, Type t, boolean makeIntf) {
        while (t instanceof CapturedType)
            t = ((CapturedType) t).wildcard;
        switch (t.tag) {
            case TypeTags.CLASS: {
                JCExpression texp = null;
                boolean isMixin = types.isMixin(t.tsym);

                if (makeIntf && isMixin) {
                    texp = makeAccessExpression(diagPos, t.tsym, true);
                } else {
                    if (t.isCompound()) {
                        t = types.supertype(t);
                    }
                    texp = makeAccessExpression(diagPos, t.tsym, false);
                }

                // Type outer = t.getEnclosingType();
		if (!types.isSequence(t)) { // hack !!! seems the outer has already been erased at the point, so...
		    t = types.erasure(t);
		}
                if (!t.getTypeArguments().isEmpty()) {
                    List<JCExpression> targs = List.nil();
                    for (Type ta : t.getTypeArguments()) {
                        targs = targs.append(makeTypeTreeInner(diagPos, ta, makeIntf));
                    }
                    texp = make.at(diagPos).TypeApply(texp, targs);
                }
                return texp;
            }
            case TypeTags.BOT: { // it is the null type, punt and make it the Object type
                return makeQualifiedTree(diagPos, syms.objectType.tsym.getQualifiedName().toString());
            }
            case TypeTags.WILDCARD: {
                WildcardType wtype = (WildcardType) t;
                return make.at(diagPos).Wildcard(make.TypeBoundKind(wtype.kind),
                        wtype.kind == BoundKind.UNBOUND ? null
                        : makeTypeTreeInner(diagPos,wtype.type, makeIntf));
            }
            case TypeTags.ARRAY: {
                return make.at(diagPos).TypeArray(makeTypeTreeInner(diagPos,types.elemtype(t), makeIntf));
            }
            default: {
                return make.at(diagPos).Type(t);
            }
        }
    }

    /**
     * Build a Java AST representing the return type of a function.
     * Generate the return type as a Location if "isBound" is set.
     * */
    public JCExpression makeReturnTypeTree(DiagnosticPosition diagPos, MethodSymbol mth, boolean isBound) {
        Type returnType = isBound? syms.f3_PointerType : mth.getReturnType();
        return makeType(diagPos, returnType);
    }

    boolean isValueType(Type type) {
        return types.isSequence(type) ||
               types.isSameType(type, syms.f3_StringType) ||
               types.isSameType(type, syms.f3_DurationType) ||
               types.isSameType(type, syms.f3_LengthType) ||
               types.isSameType(type, syms.f3_AngleType) ||
               types.isSameType(type, syms.f3_ColorType);
    }

    JCExpression makeDefaultValue(DiagnosticPosition diagPos, Type type) {
        return makeDefaultValue(diagPos, types.typeRep(type), type);
    }

    JCExpression makeDefaultValue(DiagnosticPosition diagPos, F3VarSymbol vsym) {
        return makeDefaultValue(diagPos, vsym.getTypeRepresentation(), vsym.type);
    }

    JCExpression makeDefaultValue(DiagnosticPosition diagPos, F3TypeRepresentation typeRep, Type type) {
        if (typeRep.isSequence()) {
            return accessEmptySequence(diagPos, types.elementType(type));
        } else if (typeRep.isObject()) {
            if (types.isSameType(type, syms.f3_StringType)) {
                return make.Literal("");
            }
            if (types.isSameType(type, syms.f3_DurationType)) {
                return makeQualifiedTree(diagPos, F3Defs.zero_DurationFieldName);
            }
            if (types.isSameType(type, syms.f3_LengthType)) {
                return makeQualifiedTree(diagPos, F3Defs.zero_LengthFieldName);
            }
            if (types.isSameType(type, syms.f3_AngleType)) {
                return makeQualifiedTree(diagPos, F3Defs.zero_AngleFieldName);
            }
            if (types.isSameType(type, syms.f3_ColorType)) {
                return makeQualifiedTree(diagPos, F3Defs.black_ColorFieldName);
            }
            // fall through
        }
        return makeLit(diagPos, type, typeRep.defaultValue());
    }

    /** Make an attributed tree representing a literal. This will be
     *  a Literal node.
     *  @param type       The literal's type.
     *  @param value      The literal's value.
     */
    JCExpression makeLit(DiagnosticPosition diagPos, Type type, Object value) {
        int tag = value==null? TypeTags.BOT : type.tag;
        return make.at(diagPos).Literal(tag, value).setType(
            tag == TypeTags.BOT? syms.botType : type.constType(value)); 
    }

    JCExpression call(DiagnosticPosition diagPos, RuntimeMethod meth, List<JCExpression> typeArgs, List<JCExpression> args) {
        JCExpression select = make.at(diagPos).Select(makeQualifiedTree(diagPos, meth.classString), meth.methodName);
        return make.at(diagPos).Apply(typeArgs, select, args);
    }

    JCMethodInvocation call(DiagnosticPosition diagPos, JCExpression receiver, Name methodName, Object args) {
        JCExpression expr = null;
        if (receiver == null) {
            expr = make.at(diagPos).Ident(methodName);
        } else {
            expr = make.at(diagPos).Select(receiver, methodName);
        }
        return make.at(diagPos).Apply(List.<JCExpression>nil(), expr, (args == null) ? List.<JCExpression>nil() : (args instanceof List) ? (List<JCExpression>) args : (args instanceof ListBuffer) ? ((ListBuffer<JCExpression>) args).toList() : (args instanceof JCExpression) ? List.<JCExpression>of((JCExpression) args) : null);
    }

    Name functionInterfaceName(MethodSymbol sym, boolean isBound) {
        return functionName(sym, isBound);
    }

    Name functionName(MethodSymbol sym) {
        return functionName(sym, false);
    }

    Name functionName(MethodSymbol sym, String full, boolean markAsImpl, boolean isBound) {
        if (markAsImpl) {
            full = full + F3Defs.implFunctionSuffix;
        }
        if (isBound) {
            full = full + F3Defs.boundFunctionDollarSuffix + getParameterTypeSuffix(sym);
        }
        return names.fromString(full);
    }

    Name functionName(MethodSymbol sym, boolean isBound) {
        return functionName(sym, false, isBound);
    }

    Name functionName(MethodSymbol sym, boolean markAsImpl, boolean isBound) {
        if (!markAsImpl && !isBound) {
            return sym.name;
        }
        return functionName(sym, sym.name.toString(), markAsImpl, isBound);
    }

    Name varMapName(ClassSymbol sym) {
        return names.fromString(varMap_F3ObjectFieldPrefix + defs.mangleClassName(sym, true));
    }

    Name varGetMapName(ClassSymbol sym) {
        return names.fromString(varGetMapString + defs.mangleClassName(sym, true));
    }
    
    Name attributeOffsetName(Symbol sym) {
        return prefixedAttributeName(sym, offset_AttributeFieldPrefix);
    }
    
    Name attributeFlagsName(Symbol sym) {
        return prefixedAttributeName(sym, flags_AttributeFieldPrefix);
    }
    
    Name attributeValueName(Symbol sym) {
        return prefixedAttributeName(sym, value_AttributeFieldPrefix);
    }

    Name attributeGetterName(Symbol sym) {
        return prefixedAttributeName(sym, get_AttributeMethodPrefix);
    }

    Name attributeSetterName(Symbol sym) {
        return prefixedAttributeName(sym, set_AttributeMethodPrefix);
    }

    Name attributeInvalidateName(Symbol sym) {
        return prefixedAttributeName(sym, invalidate_AttributeMethodPrefix);
    }
    
    Name attributeOnReplaceName(Symbol sym) {
        return prefixedAttributeName(sym, onReplace_AttributeMethodPrefix);
    }
    
    Name attributeGetMixinName(Symbol sym) {
        return prefixedAttributeName(sym, getMixin_AttributeMethodPrefix);
    }
    
    Name attributeGetVOFFName(Symbol sym) {
        return prefixedAttributeName(sym, getVOFF_AttributeMethodPrefix);
    }

    Name attributeSetMixinName(Symbol sym) {
        return prefixedAttributeName(sym, setMixin_AttributeMethodPrefix);
    }
    
    Name attributeGetElementName(Symbol sym) {
        return prefixedAttributeName(sym, getElement_AttributeMethodPrefix);
    }

    Name attributeSizeName(Symbol sym) {
        return prefixedAttributeName(sym, size_AttributeMethodPrefix);
    }
    
    Name attributeSavedName(Symbol sym) {
        return prefixedAttributeName(sym, saved_AttributeFieldPrefix);
    }

    Name attributeInitVarsName(Symbol sym) {
        return prefixedAttributeName(sym, initVars_AttributeMethodPrefix);
    }
 
    Name attributeApplyDefaultsName(Symbol sym) {
        return prefixedAttributeName(sym, applyDefaults_AttributeMethodPrefix);
    }

    Name boundFunctionObjectParamName(Name suffix) {
        return names.fromString(boundFunctionObjectParamPrefix + suffix);
    }

    Name boundFunctionVarNumParamName(Name suffix) {
        return names.fromString(boundFunctionVarNumParamPrefix + suffix);
    }

    Name depName(Symbol selector, Symbol sym) {
        String selectorString = "";
        
        if (sym.isStatic()) {
            selectorString = defs.mangleClassName(sym.owner, false);
        } else if (selector != null &&
                   !(selector instanceof F3VarSymbol && ((F3VarSymbol)selector).isSpecial())) {
            selectorString = selector.toString();
        }

        return names.fromString(dep_F3ObjectFieldString + selectorString + "$_$" + sym.toString());
    }

    Name classDCNT$Name(Symbol classSym) {
        return names.fromString(depCount_F3ObjectFieldString + classSym.toString().replace('.', '$'));
    }

    Name classFCNT$Name(Symbol classSym) {
        return names.fromString(funcCount_F3ObjectFieldString + classSym.toString().replace('.', '$'));
    }

    boolean isBoundFunctionResult(Symbol sym) {
        // Is this symbol result var storing bound function's result value?
        // Check if the variable is synthetic, type is Pointer and naming convention
        // is followed for bound function result value.
        return ((sym.flags() & Flags.SYNTHETIC) != 0L) &&
            types.isSameType(syms.f3_PointerType, sym.type) &&
            sym.name.startsWith(defs.boundFunctionResultName);
    }

    Name paramOldValueName(F3OnReplace onReplace) {
        return onReplace == null || onReplace.getOldValue() == null ? defs.varOldValue_LocalVarName
                : onReplace.getOldValue().getName();
    }

    Name paramNewValueName(F3OnReplace onReplace) {
        return onReplace == null || onReplace.getNewElements() == null ? defs.varNewValue_ArgName
                : onReplace.getNewElements().getName();
    }

    Name paramStartPosName(F3OnReplace onReplace) {
        return onReplace == null || onReplace.getFirstIndex() == null ? defs.startPos_ArgName
                : onReplace.getFirstIndex().getName();
    }

    Name paramEndPosName(F3OnReplace onReplace) {
        return onReplace == null || onReplace.getLastIndex() == null ||
                      onReplace.getEndKind() != F3SequenceSlice.END_EXCLUSIVE ? defs.endPos_ArgName
                : onReplace.getLastIndex().getName();
    }

    Name paramNewElementsLengthName(F3OnReplace onReplace) {
        F3Var newElements = onReplace == null ? null : onReplace.getNewElements();
        if (newElements == null)
            return defs.newLength_ArgName;
        else
            return newElements.sym.name.append(defs.lengthSuffixName);
    }

    private Name prefixedAttributeName(Symbol sym, String prefix) {
        Symbol owner = sym.owner;
        if (!types.isF3Class(owner)) {
            return sym.name;
        }
        F3VarSymbol vsym = (F3VarSymbol) sym; //TODO: make parameter a F3VarSymbol
        String sname = sym.name.toString();
        // VSGC-2837 - Mixins: script-private vars no longer hidden -- var with same name as
        // var in subclass, but with different type fails
        if (!vsym.isStatic() && vsym.hasScriptOnlyAccess() && (vsym.isExternallySeen() || types.isMixin(owner))) {
            // mangle name to hide it
            sname = defs.mangleClassName(owner, false) + '$' + sname;
        }
        return names.fromString( prefix + sname );
    }

    private String getParameterTypeSuffix(MethodSymbol sym) {
        StringBuilder sb = new StringBuilder();
        if (sym != null && sym.type != null) {
            Type mtype = sym.type;
            if (sym.type.tag == TypeTags.FORALL) {
                mtype = ((ForAll) mtype).asMethodType();
            }
            if (mtype.tag == TypeTags.METHOD) {
                List<Type> argtypes = ((MethodType) mtype).getParameterTypes();
                int argtypesCount = argtypes.length();
                int counter = 0;
                for (Type argtype : argtypes) {
                    sb.append(escapeTypeName(types.erasure(argtype)));
                    if (counter < argtypesCount - 1) {
                        // Don't append type separator after the last type in the signature.
                        sb.append(F3Defs.escapeTypeChar);
                        // Double separator between type names.
                        sb.append(F3Defs.escapeTypeChar);
                    }
                    counter++;
                }
            }
        }
        return sb.toString();
    }

    JCExpression accessEmptySequence(DiagnosticPosition diagPos, Type elemType) {
        return make.at(diagPos).Select(TypeInfo(diagPos, elemType), defs.emptySequence_FieldName);
    }

    private String escapeTypeName(Type type) {
        return type.toString().replace(F3Defs.typeCharToEscape, F3Defs.escapeTypeChar);
    }

    private JCExpression primitiveTypeInfo(DiagnosticPosition diagPos, Name typeName) {
        return make.at(diagPos).Select(makeQualifiedTree(diagPos, cTypeInfo), typeName);
    }
    
    /**
     * Given type, return an expression whose value is the corresponding TypeInfo.
     * @param diagPos
     * @param type
     * @return expression representing the TypeInfo of the class
     */
    JCExpression TypeInfo(DiagnosticPosition diagPos, Type type) {
        Type ubType = types.unboxedType(type);
        if (ubType.tag != TypeTags.NONE)
            type = ubType;
        if (types.isSameType(type, syms.f3_BooleanType)) {
            return primitiveTypeInfo(diagPos, syms.booleanTypeName);
        } else if (types.isSameType(type, syms.f3_CharacterType)) {
            return primitiveTypeInfo(diagPos, syms.charTypeName);
        } else if (types.isSameType(type, syms.f3_ByteType)) {
            return primitiveTypeInfo(diagPos, syms.byteTypeName);
        } else if (types.isSameType(type, syms.f3_ShortType)) {
            return primitiveTypeInfo(diagPos, syms.shortTypeName);
        } else if (types.isSameType(type, syms.f3_IntegerType)) {
            return primitiveTypeInfo(diagPos, syms.integerTypeName);
        } else if (types.isSameType(type, syms.f3_LongType)) {
            return primitiveTypeInfo(diagPos, syms.longTypeName);
        } else if (types.isSameType(type, syms.f3_FloatType)) {
            return primitiveTypeInfo(diagPos, syms.floatTypeName);
        } else if (types.isSameType(type, syms.f3_DoubleType)) {
            return primitiveTypeInfo(diagPos, syms.doubleTypeName);
        } else if (types.isSameType(type, syms.f3_StringType)) {
            return primitiveTypeInfo(diagPos, syms.stringTypeName);
        } else if (types.isSameType(type, syms.f3_DurationType)) {
            JCExpression fieldRef = make.at(diagPos).Select(makeType(diagPos, type), defs.defaultingTypeInfo_FieldName);
            // If TYPE_INFO becomes a Location again, ad back this line
            //    fieldRef = getLocationValue(diagPos, fieldRef, TYPE_KIND_OBJECT);
            return fieldRef;
        } else {
            assert !type.isPrimitive();
            List<JCExpression> typeArgs = List.of(makeType(diagPos, type, true));
	    if (!(type instanceof TypeVar)) {
		return call(diagPos, defs.TypeInfo_getTypeInfo, typeArgs, List.<JCExpression>nil());
	    } else {
		JCExpression typeInfoQualName = 
		    makeQualifiedTree(diagPos, 
				      defs.TypeInfo_getTypeInfo.classString);
		JCExpression typeApply = make.at(diagPos).TypeApply(typeInfoQualName,
								    List.of(makeType(diagPos, type)));
		return make.at(diagPos).TypeCast(typeApply, call(diagPos, defs.TypeInfo_getTypeInfo, null, List.<JCExpression>nil()));
	    }
        }
    }

    protected Type operationalType(Type srcType) {
        switch (srcType.tag) {
            case TypeTags.BYTE:
            case TypeTags.SHORT:
                return syms.intType;
            default:
                return srcType;
        }
    }

    protected abstract String getSyntheticPrefix();

    Name getSyntheticName(String kind) {
        return names.fromString(getSyntheticPrefix() + syntheticNameCounter++ + kind);
    }

    public Name indexVarName(F3ForExpressionInClause clause) {
        return indexVarName(clause.getVar().getName(), names);
    }
    public Name indexVarName(F3Ident var) {
        return indexVarName(var.getName(), names);
    }
    public static Name indexVarName(Name name, Name.Table names) {
        return names.fromString("$indexof$" + name.toString());
    }

    JCIdent makeIdentOfPresetKind(DiagnosticPosition diagPos, Name name, int pkind) {
        AugmentedJCIdent id = new AugmentedJCIdent(name, pkind);
        id.pos = (diagPos == null ? Position.NOPOS : diagPos.getStartPosition());
        return id;
    }

    protected JCModifiers addAccessAnnotationModifiers(DiagnosticPosition diagPos, long flags, JCModifiers mods, List<JCAnnotation> annotations) {
        make.at(diagPos);
        JCModifiers ret = mods;
        if ((flags & Flags.PUBLIC) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.publicAnnotationClassNameString), List.<JCExpression>nil()));
        }
        else if ((flags & Flags.PROTECTED) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.protectedAnnotationClassNameString), List.<JCExpression>nil()));
        }
        else if ((flags & Flags.PRIVATE) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.privateAnnotationClassNameString), List.<JCExpression>nil()));
        }
        else if ((flags & F3Flags.SCRIPT_PRIVATE) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.scriptPrivateAnnotationClassNameString), List.<JCExpression>nil()));
        }
        else {        // otherwise it is package access
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.packageAnnotationClassNameString), List.<JCExpression>nil()));
        }

        if ((flags & F3Flags.DEFAULT) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.defaultAnnotationClassNameString), List.<JCExpression>nil()));
        }
        if ((flags & F3Flags.PUBLIC_INIT) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.publicInitAnnotationClassNameString), List.<JCExpression>nil()));
        }
        if ((flags & F3Flags.PUBLIC_READ) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.publicReadAnnotationClassNameString), List.<JCExpression>nil()));
        }

        if ((flags & F3Flags.IS_DEF) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.defAnnotationClassNameString), List.<JCExpression>nil()));
        }

        if ((flags & Flags.STATIC) != 0) {
            annotations = annotations.prepend(make.Annotation(makeIdentifier(diagPos, F3Symtab.staticAnnotationClassNameString), List.<JCExpression>nil()));
        }

        if (annotations.nonEmpty()) {
            ret = make.Modifiers(mods.flags, annotations);
        }
        return ret;
    }

    protected JCModifiers addAccessAnnotationModifiers(DiagnosticPosition diagPos, long flags, JCModifiers mods) {
        return addAccessAnnotationModifiers(diagPos, flags, mods, List.<JCAnnotation>nil());
    }

    protected JCModifiers addInheritedAnnotationModifiers(DiagnosticPosition diagPos, long flags, JCModifiers mods) {
        return make.Modifiers(mods.flags, List.of(make.Annotation(makeIdentifier(diagPos, F3Symtab.inheritedAnnotationClassNameString), List.<JCExpression>nil())));
    }

    protected void pretty(JCTree tree) {
        OutputStreamWriter osw = new OutputStreamWriter(System.out);
        Pretty pretty = new Pretty(osw, false);
        try {
            pretty.println();
            pretty.print("+++++++++++++++++++++++++++++++++");
            pretty.println();
            pretty.printExpr(tree);
            pretty.println();
            pretty.print("---------------------------------");
            pretty.println();
            osw.flush();
        }catch(Exception ex) {
            System.err.println("Pretty print got: " + ex);
        }
    }

    protected void f3Pretty(F3Tree tree) {
        OutputStreamWriter osw = new OutputStreamWriter(System.out);
        F3Pretty pretty = new F3Pretty(osw, false);
        try {
            pretty.println();
            pretty.print("+++++++++++++++++++++++++++++++++");
            pretty.println();
            pretty.printExpr(tree);
            pretty.println();
            pretty.print("---------------------------------");
            pretty.println();
            osw.flush();
        }catch(Exception ex) {
            System.err.println("Pretty print got: " + ex);
        }
    }

    protected JCExpression castFromObject (JCExpression arg, Type castType) {
       return make.TypeCast(makeType(arg.pos(), types.boxedTypeOrType(castType)), arg);
    }

    protected class JavaTreeBuilder {

        protected DiagnosticPosition diagPos;
        private final F3ClassDeclaration enclosingClassDecl;
        private boolean isScript;

        protected JavaTreeBuilder(DiagnosticPosition diagPos, F3ClassDeclaration enclosingClassDecl, boolean isScript) {
            this.diagPos = diagPos;
            this.enclosingClassDecl = enclosingClassDecl;
            this.isScript = isScript;
        }


        //
        // Returns the current class decl.
        //
        public F3ClassDeclaration getCurrentClassDecl() { return enclosingClassDecl; }

        //
        // Returns true if the class (or current class) is a mixin.
        //
        public boolean isMixinClass() {
            return F3TranslationSupport.this.isMixinClass((ClassSymbol)enclosingClassDecl.sym);
        }

        public boolean isMixinClass(ClassSymbol sym) {
            return F3TranslationSupport.this.isMixinClass(sym);
        }
        
        public boolean isAnonClass() {
            return F3TranslationSupport.this.isAnonClass((ClassSymbol)enclosingClassDecl.sym);
        }
        
        public boolean isAnonClass(ClassSymbol sym) {
            return F3TranslationSupport.this.isAnonClass(sym);
        }

        public boolean isLocalClass() {
            return F3TranslationSupport.this.isLocalClass((ClassSymbol)enclosingClassDecl.sym);
        }
                
        public boolean isLocalClass(ClassSymbol sym) {
            return F3TranslationSupport.this.isLocalClass(sym);
        }
        
        public boolean isBoundFuncClass() {
            return F3TranslationSupport.this.isBoundFuncClass((ClassSymbol)enclosingClassDecl.sym);
        }
                
        public boolean isBoundFuncClass(ClassSymbol sym) {
            return F3TranslationSupport.this.isBoundFuncClass(sym);
        }
        
        public boolean isMixinVar(Symbol sym) {
            F3VarSymbol varSym = (F3VarSymbol)sym;
            Symbol owner = varSym.owner;
            return owner instanceof ClassSymbol && isMixinClass((ClassSymbol)owner) && !varSym.isStatic();
        }
        
        public boolean isLocalClassVar(Symbol sym) {
            F3VarSymbol varSym = (F3VarSymbol)sym;
            Symbol owner = varSym.owner;
            return owner instanceof ClassSymbol && isLocalClass((ClassSymbol)owner);
        }
        
        public boolean isBoundFuncParameter(Symbol sym) {
            F3VarSymbol varSym = (F3VarSymbol)sym;
            Symbol owner = varSym.owner;
            return owner instanceof ClassSymbol && isBoundFuncClass((ClassSymbol)owner);
        }
        
        public boolean setIsScript(boolean newState) {
            boolean oldState = isScript;
            isScript = newState;
            return oldState;
        }
        
        public boolean isScript() {
            return isScript;
        }


        protected void setDiagPos(DiagnosticPosition diagPos) {
            this.diagPos = diagPos;
        }

        protected void clearDiagPos() {
            this.diagPos = null;
        }

        protected TreeMaker m() {
            return make.at(diagPos);
        }

        /**
         * Make an identifier which references the specified variable declaration
         */
        protected JCIdent id(JCVariableDecl aVar) {
            return id(aVar.name);
        }

        /**
         * Make an identifier of the given name
         */
        protected JCIdent id(Name name) {
            return m().Ident(name);
        }

        /**
         * Make an identifier of the given symbol
         */
        protected JCIdent id(Symbol sym) {
            return m().Ident(sym.name);
        }

        /**
         * Make an identifier of the given string
         */
        protected JCIdent id(String string) {
            return m().Ident(names.fromString(string));
        }

        /**
         * Make a member select or an identifier depending on the selector
         */
        protected JCExpression Select(JCExpression selector, Name name) {
            return (selector==null)? id(name) : m().Select(selector, name);
        }

        /**
         * An assertion
         */
        protected JCAssert Assert(JCExpression cond) {
            return m().Assert(cond, null);
        }

        /**
         * Standard method parameters
         */

        JCIdent makeMethodArg(Name name, Type type) {
            JCIdent id = id(name);
            id.type = type;
            return id;
        }

        JCIdent posArg() {
            return makeMethodArg(defs.pos_ArgName, syms.intType);
        }

        JCIdent startPosArg() {
            return makeMethodArg(defs.startPos_ArgName, syms.intType);
        }

        JCIdent endPosArg() {
            return makeMethodArg(defs.endPos_ArgName, syms.intType);
        }

        JCIdent newLengthArg() {
            return makeMethodArg(defs.newLength_ArgName, syms.intType);
        }

        JCIdent phaseArg() {
            return makeMethodArg(defs.phase_ArgName, syms.intType);
        }

        /**
         * Set the phase state part of the flag to the next state part of the phase transition
         */
        JCStatement SetNextVarFlagsStateFromPhaseTransition(F3VarSymbol sym) {
            return
                FlagChangeStmt(sym,
                        id(defs.varFlagSTATE_MASK),
                        SHIFTR(
                            phaseArg(),
                            id(defs.phaseTransitionNEXT_STATE_SHIFT)
                        )
                );
        }

        /**
         * Clear the BE bits from the phase transition
         */
        JCStatement ClearBeFromPhaseTransition() {
            return
                Stmt(
                    m().Assignop(JCTree.BITAND_ASG,
                        phaseArg(),
                        id(defs.phaseTransitionCLEAR_BE)
                    ));
        }

        JCStatement PhaseCheckedBlock(F3VarSymbol sym, JCStatement... stmts) {
            return
                If (id(defs.wasInvalid_LocalVarName),
                    Block(
                        Stmts(
                            SetNextVarFlagsStateFromPhaseTransition(sym),
                            ClearBeFromPhaseTransition()
                        ).appendList(List.from(stmts))
                    )
                );
        }

        JCExpression IsInvalidatePhase() {
            return EQ(BITAND(phaseArg(), id(defs.phaseTransitionPHASE)), id(defs.phaseINVALIDATE));
        }
        
        JCExpression IsTriggerPhase() {
            return EQ(BITAND(phaseArg(), id(defs.phaseTransitionPHASE)), id(defs.phaseTRIGGER));
        }

        /**
         * Convert type to JCExpression
         */
        protected JCExpression makeType(Type type, boolean makeIntf) {
            return F3TranslationSupport.this.makeType(diagPos, type, makeIntf);
        }

        protected JCExpression makeType(Type type) {
            return makeType(type, true);
        }

        protected JCExpression makeType(Symbol sym) {
            return makeType(sym.type, true);
        }

        protected JCExpression makeKeyValueTargetType(Type type) {
            Name fieldName;
            if (type.isPrimitive()) {
                switch (type.getKind()) {
                    case BYTE:
                        fieldName = defs.BYTE_KeyValueTargetTypeFieldName;
                        break;
                    case SHORT:
                        fieldName = defs.SHORT_KeyValueTargetTypeFieldName;
                        break;
                    case INT:
                    case CHAR:
                        fieldName = defs.INTEGER_KeyValueTargetTypeFieldName;
                        break;
                    case LONG:
                        fieldName = defs.LONG_KeyValueTargetTypeFieldName;
                        break;
                    case FLOAT:
                        fieldName = defs.FLOAT_KeyValueTargetTypeFieldName;
                        break;
                    case DOUBLE:
                        fieldName = defs.DOUBLE_KeyValueTargetTypeFieldName;
                        break;
                    case BOOLEAN:
                        fieldName = defs.BOOLEAN_KeyValueTargetTypeFieldName;
                        break;
                    default:
                        fieldName = defs.OBJECT_KeyValueTargetTypeFieldName;
                        break;
                }
            } else if (types.isSequence(type)) {
                fieldName = defs.SEQUENCE_KeyValueTargetTypeFieldName;
            } else {
                fieldName = defs.OBJECT_KeyValueTargetTypeFieldName;
            }

            return Select(makeQualifiedTree(diagPos, F3Defs.cKeyValueTargetType), fieldName);
        }

        // Return a receiver$, scriptLevelAccess$() or null depending on the context.
        //
        protected JCExpression getReceiver() {
            return resolveThis(enclosingClassDecl.sym, true);
        }

        protected JCExpression getReceiverOrThis() {
            return resolveThis(enclosingClassDecl.sym, false);
        }

        protected JCExpression getReceiverOrThis(boolean isStatic) {
            Symbol cSym = enclosingClassDecl.sym;
            if (isStatic) {
                return Select(makeType(cSym.type, false), f3make.ScriptAccessSymbol(cSym).name);
            } else if(isMixinClass()) {
                return id(defs.receiverName);
            }
            return resolveThisInternal(cSym, false);
        }

        protected JCExpression getReceiver(Symbol sym) {
            if (sym.isStatic()) {
                return Select(makeType(sym.owner.type, false), f3make.ScriptAccessSymbol(sym.owner).name);
            }
            return resolveThis(sym.owner, true);
        }

        protected JCExpression getReceiverOrThis(Symbol sym) {
            if (sym.isStatic()) {
                return Select(makeType(sym.owner.type, false), f3make.ScriptAccessSymbol(sym.owner).name);
            }
            return resolveThis(sym.owner, false);
        }
        
        protected JCExpression resolveThis(Symbol sym, boolean nullForThis) {
            return (isMixinClass() && !isScript) ?
                id(defs.receiverName) :
                resolveThisInternal(sym, nullForThis);
        }
        //where
        private JCExpression resolveThisInternal(Symbol owner, boolean nullForThis) {
            JCExpression _this = owner.kind == Kinds.TYP ?
                resolveThisInternal(owner, enclosingClassDecl.sym, false) :
                id(names._this);
            return (nullForThis && _this.getTag() == JCTree.IDENT) ?
                null :
                _this;
        }
        //where
        private JCExpression resolveThisInternal(Symbol ownerThis, Symbol currentThis, boolean rec) {
            JCExpression thisExpr = rec ? 
                Select(makeType(currentThis.type), names._this) :
                id(names._this);
            if (!currentThis.isSubClass(ownerThis, types)) {
                Type encl = currentThis.type.getEnclosingType();
                if (encl == null || encl == Type.noType || types.isMixin(encl.tsym)) {
                    return resolveThisInternal(ownerThis, currentThis, thisExpr);
                }
                return resolveThisInternal(ownerThis, currentThis.type.getEnclosingType().tsym, true);
            }
            else {
                return thisExpr;
            }
        }
        //where
        private JCExpression resolveThisInternal(Symbol ownerThis, Symbol currentThis, JCExpression receiver) {
            if (currentThis == null) {
                throw new AssertionError("Cannot find owner");
            }
            else if (!currentThis.isSubClass(ownerThis, types)) {
                return resolveThisInternal(ownerThis, currentThis.owner.enclClass(), Call(receiver, defs.outerAccessor_MethodName));
            }
            else {
                return receiver;
            }
        }
        
        protected JCExpression resolveSuper(Symbol owner) {
            return resolveSuperInternal(owner, enclosingClassDecl.sym, false);
        }

        private JCExpression resolveSuperInternal(Symbol ownerSym, Symbol currentSym, boolean rec) {
            JCExpression superExpr = rec ?
                Select(makeType(currentSym.type), names._super) :
                id(names._super);
            if (!currentSym.isSubClass(ownerSym, types)) {
                Type encl = currentSym.type.getEnclosingType();
                return resolveSuperInternal(ownerSym, currentSym.type.getEnclosingType().tsym, true);
            }
            else {
                return superExpr;
            }
        }
        
        //
        // Private support methods for testing/setting/clearing a var flag.
        //
        
        protected boolean isJCIdentName(JCExpression ident, Name name) {
            return ident instanceof JCIdent && ((JCIdent)ident).getName() == name;
        }
        
        protected JCExpression GetFlags(F3VarSymbol varSym) {
            if (isMixinClass()) {
                return Call(getReceiver(varSym), defs.getFlags_F3ObjectMethodName, Offset(varSym));
            } else {
                return VarFlags(varSym);
            }
        }
        
        protected JCExpression flagCast(JCExpression expr) {
            return m().TypeCast(makeType(syms.shortType, true), expr);
        }

        private JCExpression FlagAction(F3VarSymbol varSym, Name action, Name clearBits, Name setBits, boolean isStmt) {
            return FlagAction(varSym, action,
                        clearBits != null ? id(clearBits) : null,
                        setBits != null ? id(setBits) : null,
                        isStmt);
        }
        private JCExpression FlagAction(F3VarSymbol varSym, Name action, JCExpression clearBits, JCExpression setBits, boolean isStmt) {
            assert clearBits != null || setBits != null : "Need to specify which bits";
            
            boolean  clearBitsNull = clearBits == null;
            boolean  setBitsNull = setBits == null;
            if (clearBitsNull) clearBits = Int(0);
            if (setBitsNull) setBits = Int(0);

            clearDiagPos();
            if (action == defs.varFlagActionTest) {
                return EQ(BITAND(GetFlags(varSym), clearBits), setBits);
            } else if (isMixinClass() && !varSym.isStatic()) {
                return Call(getReceiver(varSym), action, Offset(varSym), clearBits, setBits);
            } else /* if (action == defs.varFlagActionChange) */ {
                JCExpression assignExpr;

                if (isStmt) {
                    if (isJCIdentName(clearBits, defs.varFlagALL_FLAGS)) {
                        assignExpr = m().Assign(VarFlags(varSym), flagCast(setBits));
                    } else if (clearBitsNull) {
                        assignExpr = m().Assignop(JCTree.BITOR_ASG, VarFlags(varSym), flagCast(setBits));
                    } else if (setBitsNull) {
                        assignExpr = m().Assignop(JCTree.BITAND_ASG, VarFlags(varSym), flagCast(BITNOT(clearBits)));
                    } else {
                        assignExpr = m().Assign(VarFlags(varSym), flagCast(BITOR(BITAND(VarFlags(varSym), BITNOT(clearBits)), setBits)));
                    }
                    
                    return assignExpr;
                } else {
                    ListBuffer<JCStatement> stmts = ListBuffer.lb();
                    JCVariableDecl bitsVar;
                
                    if (isJCIdentName(clearBits, defs.varFlagALL_FLAGS)) {
                        bitsVar = TmpVar(syms.intType, setBits);
                        assignExpr = m().Assign(VarFlags(varSym), flagCast(id(bitsVar.name)));
                    } else if (clearBitsNull) {
                        bitsVar = TmpVar(syms.intType, setBits);
                        assignExpr = m().Assignop(JCTree.BITOR_ASG, VarFlags(varSym), flagCast(id(bitsVar.name)));
                    } else if (setBitsNull) {
                        bitsVar = TmpVar(syms.intType, clearBits);
                        assignExpr = m().Assignop(JCTree.BITAND_ASG, VarFlags(varSym), flagCast(BITNOT(id(bitsVar.name))));
                    } else {
                        JCVariableDecl clearBitsVar = TmpVar(syms.intType, clearBits);
                        JCVariableDecl setBitsVar = TmpVar(syms.intType, setBits);
                        stmts.append(clearBitsVar);
                        stmts.append(setBitsVar);
                        bitsVar = TmpVar(syms.intType, BITOR(id(clearBitsVar.name), id(setBitsVar.name)));
                        assignExpr = m().Assign(VarFlags(varSym), flagCast(BITOR(BITAND(VarFlags(varSym), BITNOT(id(clearBitsVar.name))), id(bitsVar.name))));
                    }
                    
                    stmts.append(bitsVar);
                    JCVariableDecl testVar = TmpVar(syms.booleanType, EQ(BITAND(VarFlags(varSym), id(bitsVar.name)), id(bitsVar.name)));
                    stmts.append(testVar);
                    stmts.append(Stmt(assignExpr));
                    
                    return BlockExpression(stmts, id(testVar.name));
                }
            }
        }
        private JCExpression FlagAction(JCExpression offset, Name action, Name clearBits, Name setBits, boolean isStmt) {
            return Call(action, offset,
                        clearBits != null ? id(clearBits) : Int(0),
                        setBits != null ? id(setBits) : Int(0));
        }

        //
        // These methods return an expression for testing a var flag.
        //

        protected JCExpression FlagTest(Name flagsVar, Name clearBits, Name setBits) {
            return EQ(BITAND(id(flagsVar), clearBits == null ? Int(0) : id(clearBits)), setBits == null ? Int(0) : id(setBits));
        }
        protected JCExpression FlagTest(F3VarSymbol varSym, Name clearBits, Name setBits) {
            return FlagAction(varSym, defs.varFlagActionTest, clearBits, setBits, false);
        }
        protected JCExpression FlagTest(F3VarSymbol varSym, JCExpression clearBits, JCExpression setBits) {
            return FlagAction(varSym, defs.varFlagActionTest, clearBits, setBits, false);
        }
        protected JCExpression FlagTest(JCExpression offset, Name clearBits, Name setBits) {
            return FlagAction(offset, defs.varFlagActionTest, clearBits, setBits, false);
        }


        //
        // These methods returns a statement for setting/clearing a var flag.
        //

        protected JCStatement FlagChangeStmt(F3VarSymbol varSym, Name clearBits, Name setBits) {
            return Stmt(FlagAction(varSym, defs.varFlagActionChange, clearBits, setBits, true));
        }

        protected JCStatement FlagChangeStmt(F3VarSymbol varSym, JCExpression clearBits, JCExpression setBits) {
            return Stmt(FlagAction(varSym, defs.varFlagActionChange, clearBits, setBits, true));
        }

        protected JCStatement FlagChangeStmt(JCExpression offset, Name clearBits, Name setBits) {
            return Stmt(FlagAction(offset, defs.varFlagActionChange, clearBits, setBits, true));
        }

        // Specialized test for checking to see if a var is init ready in a bind expression.
        // The var flags have been copied to a local varFlags$.
        protected JCExpression bindNeedsDefault(F3VarSymbol varSym) {
            return EQ(BITAND(id(defs.varFlags_LocalVarName),
                             id(defs.varFlagINITIALIZED_STATE_BIT)),
                      Int(0));
        }

        //
        // Methods to generate simple constants.
        //
        protected JCExpression Int(int value)         { return m().Literal(TypeTags.INT, value); }
        protected JCExpression Byte(int value)        { return m().Literal(TypeTags.BYTE, value); }
        protected JCExpression Null()                 { return m().Literal(TypeTags.BOT, null); }
        protected JCExpression String(String str)     { return m().Literal(TypeTags.CLASS, str); }
        protected JCExpression Boolean(boolean value) { return m().Literal(TypeTags.BOOLEAN, value ? 1 : 0); }
        protected JCExpression True()                 { return Boolean(true); }
        protected JCExpression False()                { return Boolean(false); }

        protected JCStatement Stmt(JCExpression expr) {
            return m().Exec(expr);
        }

        protected JCStatement Return(JCExpression expr) {
            return m().Return(expr);
        }

        protected JCStatement Stmt(JCExpression expr, Type returnType) {
            return (returnType==null || returnType==syms.voidType)? 
                  Stmt(expr)
                : Return(expr);
        }

        //
        // Binary and Unary operators
        //

        JCExpression LT(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.LT, v1, v2);
        }
        JCExpression LE(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.LE, v1, v2);
        }
        JCExpression GT(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.GT, v1, v2);
        }
        JCExpression GE(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.GE, v1, v2);
        }
        JCExpression EQ(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.EQ, v1, v2);
        }
        JCExpression NE(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.NE, v1, v2);
        }
        JCExpression AND(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.AND, v1, v2);
        }
        JCExpression OR(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.OR, v1, v2);
        }
        JCExpression PLUS(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.PLUS, v1, v2);
        }
        JCExpression MINUS(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.MINUS, v1, v2);
        }
        JCExpression MUL(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.MUL, v1, v2);
        }
        JCExpression MOD(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.MOD, v1, v2);
        }
        JCExpression DIV(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.DIV, v1, v2);
        }
        JCExpression NEG(JCExpression v1) {
            return m().Unary(JCTree.NEG, v1);
        }
        JCExpression NOT(JCExpression v1) {
            return m().Unary(JCTree.NOT, v1);
        }
        JCExpression BITAND(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.BITAND, v1, v2);
        }
        JCExpression BITOR(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.BITOR, v1, v2);
        }
        JCExpression BITXOR(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.BITXOR, v1, v2);
        }
        JCExpression BITNOT(JCExpression v1) {
            return m().Binary(JCTree.BITXOR, v1, Int(-1));
        }
        JCExpression SHIFTL(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.SL, v1, v2);
        }
        JCExpression SHIFTR(JCExpression v1, JCExpression v2) {
            return m().Binary(JCTree.SR, v1, v2);
        }

        /**
         * Compare against null
         */
        protected JCExpression EQnull(JCExpression targ) {
            return EQ(targ, Null());
        }

        protected JCExpression NEnull(JCExpression targ) {
            return NE(targ, Null());
        }

        /**
         * Make a variable -- final by default
         */

        protected JCVariableDecl Var(JCModifiers modifiers, JCExpression varType, Name varName, JCExpression initialValue, F3VarSymbol varSym) {
            JCVariableDecl varDecl = m().VarDef(
                                            modifiers,
                                            varName,
                                            varType,
                                            initialValue);
            varDecl.sym = varSym;
            return varDecl;
        }

        protected JCVariableDecl Var(long flags, Type varType, Name varName, JCExpression initialValue, F3VarSymbol varSym) {
            return Var(m().Modifiers(flags), makeType(varType), varName, initialValue, varSym);
        }

        protected JCVariableDecl Var(long flags, JCExpression varType, Name varName, JCExpression initialValue) {
            return Var(m().Modifiers(flags), varType, varName, initialValue, null);
        }

        protected JCVariableDecl Var(long flags, Type varType, Name varName, JCExpression initialValue) {
            return Var(flags, varType, varName, initialValue, null);
        }

        protected JCVariableDecl Var(Type varType, Name varName, JCExpression value) {
            return Var(Flags.FINAL, varType, varName, value);
        }

        protected JCVariableDecl Var(long flags, Type varType, String varName, JCExpression initialValue) {
            return Var(flags, varType, names.fromString(varName), initialValue);
        }
        
        /**
         * Make a method paramter
         */
        protected JCVariableDecl Param(Type varType, Name varName) {
            return Var(Flags.PARAMETER | Flags.FINAL, varType, varName, null);
        }

       /**
        * Make a receiver parameter.
        * Its type is that of the corresponding interface and it is a final parameter.
        * */
        JCVariableDecl ReceiverParam(F3ClassDeclaration cDecl) {
            return m().VarDef(
                    m().Modifiers(Flags.PARAMETER | Flags.FINAL),
                    defs.receiverName,
                    id(interfaceName(cDecl)),
                    null);
        }

        /**
         * Make a variable (synthethic name) -- final by default
         */

        protected JCVariableDecl MutableTmpVar(String root, Type varType, JCExpression initialValue) {
            return TmpVar(0L, root, varType, initialValue);
        }

        protected JCVariableDecl TmpVar(Type type, JCExpression value) {
            return TmpVar("tmp", type, value);
        }

        protected JCVariableDecl TmpVar(String root, Type varType, JCExpression value) {
            return TmpVar(Flags.FINAL, root, varType, value);
        }

        protected JCVariableDecl TmpVar(long flags, String root, Type varType, JCExpression initialValue) {
            return Var(flags, varType, getSyntheticName(root), initialValue);
        }

       /**
         * Block Expressions
         */

        BlockExprJCBlockExpression BlockExpression(List<JCStatement> stmts, JCExpression value) {
            BlockExprJCBlockExpression bexpr = new BlockExprJCBlockExpression(0L, stmts, value);
            bexpr.pos = (diagPos == null ? Position.NOPOS : diagPos.getStartPosition());
            return bexpr;
        }

        BlockExprJCBlockExpression BlockExpression(ListBuffer<JCStatement> stmts, JCExpression value) {
            return BlockExpression(stmts.toList(), value);
        }

        BlockExprJCBlockExpression BlockExpression(JCStatement stmt1, JCExpression value) {
            return BlockExpression(List.of(stmt1), value);
        }

        BlockExprJCBlockExpression BlockExpression(JCStatement stmt1, JCStatement stmt2, JCExpression value) {
            return BlockExpression(List.of(stmt1, stmt2), value);
        }

       /**
         * Block
         */

        JCBlock Block(List<JCStatement> prolog, JCStatement... epilog) {
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            for (JCStatement p : prolog) stmts.append(p);
            for (JCStatement e : epilog) stmts.append(e);
            return Block(stmts);
        }

        JCBlock Block(ListBuffer<JCStatement> prolog, JCStatement... epilog) {
            ListBuffer<JCStatement> stmts = ListBuffer.lb();
            for (JCStatement p : prolog) stmts.append(p);
            for (JCStatement e : epilog) stmts.append(e);
            return Block(stmts);
        }

        JCBlock Block(List<JCStatement> stmts) {
            ListBuffer<JCStatement> nonNull = ListBuffer.lb();
            for (JCStatement stmt : stmts) {
                if (stmt != null) {
                    nonNull.append(stmt);
                }
            }
            return m().Block(0L, nonNull.toList());
        }

        JCBlock Block(ListBuffer<JCStatement> stmts) {
            return Block(stmts.toList());
        }

                JCBlock Block(JCStatement... stmts) {
            return Block(List.from(stmts));
        }
        
        boolean isBlockEmpty(JCBlock block) {
            return block == null || block.getStatements().isEmpty();
        }

        List<JCStatement> Stmts(JCStatement... stmts) {
            return List.from(stmts);
        }

       /**
         * If / Condition
         */

        JCStatement If(JCExpression cond, JCStatement thenStmt, JCStatement elseStmt) {
            return m().If(cond, thenStmt, elseStmt);
        }

        JCStatement If(JCExpression cond, JCStatement thenStmt) {
            return m().If(cond, thenStmt, null);
        }

        JCExpression If(JCExpression cond, JCExpression thenExpr, JCExpression elseExpr) {
            return m().Conditional(cond, thenExpr, elseExpr);
        }
        
        
       /**
         * Optimal If
         */

        JCStatement OptIf(JCExpression cond, JCStatement thenStmt) {
            return OptIf(cond, thenStmt, null);
        }

        JCStatement OptIf(JCExpression cond, JCStatement thenStmt, JCStatement elseStmt) {
            boolean noThen = thenStmt == null || (thenStmt instanceof JCBlock && isBlockEmpty((JCBlock)thenStmt));
            boolean noElse = elseStmt == null || (elseStmt instanceof JCBlock && isBlockEmpty((JCBlock)elseStmt));
            
            if (!noThen) {
                return If(cond, thenStmt, noElse ? null : elseStmt);
            } if (!noElse) {
                return If(NOT(cond), elseStmt, null);
            }
            
            return null;
        }

        /**
         * Try
         */
        JCStatement Try(JCBlock body, JCCatch cat, JCBlock finalizer) {
            ListBuffer<JCCatch> catches = ListBuffer.lb();
            catches.append(cat);
            return m().Try(body, catches.toList(), finalizer);
        }
        JCStatement Try(JCBlock body, JCCatch cat) {
            return Try(body, cat, null);
        }

        /**
         * Make methods
         */

        protected JCMethodDecl Method(long flags, Type returnType, Name methodName, List<JCVariableDecl> params, List<JCStatement> stmts, MethodSymbol methSym) {
            return Method(m().Modifiers(flags), returnType, methodName, params, stmts, methSym);
        }

        protected JCMethodDecl Method(long flags, Type returnType, Name methodName, List<Type> paramTypes, List<JCVariableDecl> params, Symbol owner, List<JCStatement> stmts) {
            MethodSymbol methSym = makeMethodSymbol(flags, returnType, methodName, owner, paramTypes);
            return Method(m().Modifiers(flags), returnType, methodName, params, stmts, methSym);
        }

        protected JCMethodDecl Method(JCModifiers modifiers, Type returnType, Name methodName, List<JCVariableDecl> params, List<JCStatement> stmts, MethodSymbol methSym) {
            JCMethodDecl methDecl = m().MethodDef(
                                        modifiers,
                                        methodName,
                                        makeType(returnType),
                                        List.<JCTypeParameter>nil(),
                                        params != null ? params : List.<JCVariableDecl>nil(),
                                        List.<JCExpression>nil(),
                                        stmts == null ? null : Block(stmts),
                                        null);
            methDecl.sym = methSym;
            return methDecl;
        }

        protected JCExpression QualifiedTree(String str) {
            return F3TranslationSupport.this.makeQualifiedTree(diagPos, str);
        }

        /**
         * Var accessors -- returning a JCExpression
         */

        public JCExpression Get(Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (varSym.isSpecial()) {
                JCExpression receiver = getReceiver(varSym);
                return receiver == null ? id(varSym.name) : receiver;
            } else if (isMixinVar(varSym)) {
                return Call(attributeGetMixinName(varSym));
            } else if (varSym.isStatic()) {
                return id(attributeValueName(varSym));
            } else {
                return Select(getReceiver(varSym), attributeValueName(varSym));
            }
        }
        public JCExpression Get(JCExpression selector, Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (isMixinVar(varSym)) {
                return Call(selector, attributeGetMixinName(varSym));
            } else {
                return Select(selector, attributeValueName(varSym));
            }
        }

        public JCExpression Offset(Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (isMixinVar(varSym)) {
                return Call(getReceiver(), attributeGetVOFFName(varSym));
            } else {
                JCExpression klass = makeType(varSym.owner.type, false);
                
                if (varSym.isStatic()) {
                    klass = Select(klass, f3make.ScriptSymbol(varSym.owner).name);
                }
                
                return Select(klass, attributeOffsetName(varSym));
            }
        }
        public JCExpression Offset(JCExpression selector, Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (selector != null && isMixinVar(varSym)) {
                return Call(selector, attributeGetVOFFName(varSym));
            }
            
            return Offset(varSym);
        }

        public JCExpression DepNum(JCExpression selector, Symbol selectorSym, Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            Name depName = depName(selectorSym, varSym);
            
            if (isMixinClass()) {
                return PLUS(Call(classDCNT$Name(enclosingClassDecl.sym)), id(depName));
            }
            
            return Select(selector, depName);
        }

        public JCExpression FuncNum(int number) {
            JCExpression baseExpr;
            
            if (isMixinClass() && !isScript()) {
                baseExpr = Call(classFCNT$Name(enclosingClassDecl.sym));
            } else if (isScript()) {
                baseExpr = Select(id(f3make.ScriptAccessSymbol(enclosingClassDecl.sym).name), defs.funcCount_F3ObjectFieldName);
            } else {
                baseExpr = id(defs.funcCount_F3ObjectFieldName);
            }
            
            return PLUS(baseExpr, Int(number));
        }

        public JCExpression VarFlags(Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            return Select(getReceiver(varSym), attributeFlagsName(varSym));
        }
        public JCExpression VarFlags(JCExpression selector, Symbol sym) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            return Select(selector, attributeFlagsName(varSym));
        }

        public JCExpression Set(Symbol sym, JCExpression value) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (isMixinVar(varSym)) {
                return Call(attributeSetMixinName(varSym), value);
            } else if (varSym.isStatic()) {
                return m().Assign(id(attributeValueName(varSym)), value);
            } else {
                return m().Assign(Select(getReceiver(varSym), attributeValueName(varSym)), value);
            }
        }
        public JCExpression Set(JCExpression selector, Symbol sym, JCExpression value) {
            assert sym instanceof F3VarSymbol : "Expect a var symbol, got " + sym;
            F3VarSymbol varSym = (F3VarSymbol)sym;
            
            if (isMixinVar(varSym)) {
                return Call(selector, attributeSetMixinName(varSym), value);
            } else {
                return m().Assign(Select(selector, attributeValueName(varSym)), value);
            }
        }
        public JCStatement SetStmt(Symbol sym, JCExpression value) {
            return Stmt(Set(sym, value));
        }
        public JCStatement SetStmt(JCExpression selector, Symbol sym, JCExpression value) {
            return Stmt(Set(selector, sym, value));
        }
        
        public JCExpression Getter(Symbol sym) {
            return Getter(sym.isStatic() ?
                    makeType(sym.owner.type, false) :
                    getReceiver(sym), sym);
        }

        public JCExpression Getter(JCExpression selector, Symbol sym) {
            F3VarSymbol vsym = (F3VarSymbol) sym;
            if (vsym.isSpecial()) {
                return Get(vsym);
            } else if (vsym.isMember()) {
                if (vsym.useGetters()) {
                    return Call(selector, attributeGetterName(vsym));
                } else {
                    return Get(selector, vsym);
                }
            } else {
                // Local variable
                return id(vsym.name);
            }
        }
        
        public JCExpression Setter(Symbol sym, JCExpression value) {
            return Setter(sym.isStatic() ?
                    makeType(sym.owner.type, false) :
                    getReceiver(sym), sym, value);
        }

        public JCExpression Setter(JCExpression selector, Symbol sym, JCExpression value) {
            F3VarSymbol vsym = (F3VarSymbol) sym;
            if (vsym.isSpecial()) {
                return Set(selector, sym, value);
            } else if (vsym.isMember()) {
                if (vsym.useSetters()) {
                    return Call(selector, attributeSetterName(sym), value);
                } else {
                    return Set(selector, sym, value);
                }
            } else {
                // Local variable
                return m().Assign(id(sym.name), value);
            }
        }
        
        public JCStatement SetterStmt(Symbol sym, JCExpression value) {
            return Stmt(Setter(sym, value));
        }
        public JCStatement SetterStmt(JCExpression selector, Symbol sym, JCExpression value) {
            return Stmt(Setter(selector, sym, value));
        }
        
       
        /**
         * Method call support
         */

        private List<JCExpression> callArgs(JCExpression[] args) {
            // Convert args to list.
            ListBuffer<JCExpression> argBuffer = ListBuffer.lb();
            for (JCExpression arg : args) {
                argBuffer.append(arg);
            }

            return argBuffer.toList();
        }

        /**
         * Method calls -- returning a JCExpression
         */

        JCExpression Call(JCExpression receiver, Name methodName, List<JCExpression> typeArgs, List<JCExpression> args) {
            JCExpression expr = Select(receiver, methodName);
            return m().Apply(typeArgs, expr, args);
        }

        JCExpression Call(JCExpression receiver, Name methodName, List<JCExpression> args) {
            return Call(receiver, methodName, List.<JCExpression>nil(), args);
        }

        JCExpression Call(JCExpression receiver, Name methodName, ListBuffer<JCExpression> args) {
            return Call(receiver, methodName, args.toList());
        }

        JCExpression Call(JCExpression receiver, Name methodName, JCExpression... args) {
            return Call(receiver, methodName, callArgs(args));
        }


        JCExpression Call(Name methodName, List<JCExpression> args) {
            return Call(getReceiver(), methodName, args);
        }

        JCExpression Call(Name methodName, ListBuffer<JCExpression> args) {
            return Call(getReceiver(), methodName, args.toList());
        }

        JCExpression Call(Name methodName, JCExpression... args) {
            return Call(getReceiver(), methodName, callArgs(args));
        }


        JCExpression Call(RuntimeMethod meth, List<JCExpression> typeArgs, List<JCExpression> args) {
            return Call(QualifiedTree(meth.classString), meth.methodName, typeArgs, args);
        }

        JCExpression Call(RuntimeMethod meth, ListBuffer<JCExpression> typeArgs, ListBuffer<JCExpression> args) {
            return Call(meth, typeArgs.toList(), args.toList());
        }

        JCExpression Call(RuntimeMethod meth, List<JCExpression> args) {
            return Call(meth, List.<JCExpression>nil(), args);
        }

        JCExpression Call(RuntimeMethod meth, ListBuffer<JCExpression> args) {
            return Call(meth, args.toList());
        }

        JCExpression Call(RuntimeMethod meth, JCExpression... args) {
            return Call(meth, callArgs(args));
        }

        /**
         * Method calls -- returning a JCStatement
         */

        JCStatement CallStmt(JCExpression receiver, Name methodName, List<JCExpression> args) {
            return Stmt(Call(receiver, methodName, args));
        }

        JCStatement CallStmt(JCExpression receiver, Name methodName, ListBuffer<JCExpression> args) {
            return Stmt(Call(receiver, methodName, args.toList()));
        }

        JCStatement CallStmt(JCExpression receiver, Name methodName, JCExpression... args) {
            return Stmt(Call(receiver, methodName, callArgs(args)));
        }


        JCStatement CallStmt(Name methodName, List<JCExpression> args) {
            return Stmt(Call(getReceiver(), methodName, args));
        }

        JCStatement CallStmt(Name methodName, ListBuffer<JCExpression> args) {
            return Stmt(Call(getReceiver(), methodName, args.toList()));
        }

        JCStatement CallStmt(Name methodName, JCExpression... args) {
            return Stmt(Call(getReceiver(), methodName, callArgs(args)));
        }


        JCStatement CallStmt(RuntimeMethod meth, List<JCExpression> args) {
            return Stmt(Call(meth, args));
        }

        JCStatement CallStmt(RuntimeMethod meth, ListBuffer<JCExpression> args) {
            return Stmt(Call(meth, args));
        }

        JCStatement CallStmt(RuntimeMethod meth, JCExpression... args) {
            return Stmt(Call(meth, args));
        }

        /**
         * Invalidation support
         */

        private JCStatement CallInvalidate(Symbol sym, Name flag) {
            clearDiagPos();
            return CallStmt(attributeInvalidateName(sym), id(flag));
        }

        JCStatement CallInvalidate(Symbol sym) {
            return CallInvalidate(sym, defs.phaseTransitionCASCADE_INVALIDATE);
        }

        JCStatement CallTrigger(Symbol sym) {
            return CallInvalidate(sym, defs.phaseTransitionCASCADE_TRIGGER);
        }

        JCStatement CallBeInvalidate(Symbol sym) {
            return CallInvalidate(sym, defs.phaseTransitionBE_INVALIDATE);
        }

        JCStatement CallBeTrigger(Symbol sym) {
            return CallInvalidate(sym, defs.phaseTransitionBE_TRIGGER);
        }

        /**
         * Sequence invalidation support
         */

        JCExpression Undefined() {
            return Int(F3Defs.UNDEFINED_MARKER_INT);
        }

        JCStatement CallSeqInvalidate(Symbol sym, JCExpression begin, JCExpression end, JCExpression newLen) {
            clearDiagPos();
            return CallStmt(attributeInvalidateName(sym), begin, end, newLen, id(defs.phaseTransitionCASCADE_INVALIDATE));
        }

        JCStatement CallSeqTrigger(Symbol sym, JCExpression begin, JCExpression end, JCExpression newLen) {
            clearDiagPos();
            return CallStmt(attributeInvalidateName(sym), begin, end, newLen, id(defs.phaseTransitionCASCADE_TRIGGER));
        }

        JCStatement CallSeqInvalidateUndefined(Symbol sym) {
            return CallSeqInvalidate(sym, Int(0), Undefined(), Undefined());
        }

        JCStatement CallSeqTriggerInitial(Symbol sym, JCExpression initialSize) {
            return CallSeqTrigger(sym, Int(0), Int(0), initialSize);
        }

        JCStatement CallSeqTriggerUnchanged(Symbol sym) {
            return CallSeqTrigger(sym, Undefined(), Undefined(), Int(0));
        }

        JCExpression IsUnchangedTrigger() {
            return LT(startPosArg(), Int(0));
        }

        /**
         * These methods simplify throw statements.
         */
        JCStatement Throw(Type type, String message) {
            if (message != null) {
                return m().Throw(m().NewClass(null, null, makeType(type), List.<JCExpression>of(String(message)), null));
            } else {
                return m().Throw(m().NewClass(null, null, makeType(type), List.<JCExpression>nil(), null));
            }
        }
        JCStatement Throw(Type type) {
            return Throw(type, null);
        }

        JCExpression typeCast(final Type targetType, final Type inType, final JCExpression expr) {
            if (types.typeRep(inType).isObject()) {
                // We can't just cast the Object to Float (for example)
                // because if the Object is not Float, we will get a ClassCastException at runtime.
                // And we can't just call java.lang.Number.floatValue() because java.lang.Number
                // doesn't exist on mobile, at least not as of Jan 2009.
                F3TypeRepresentation targetKind = types.typeRep(targetType);
                if (targetKind.isPrimitive()) {
                    return Call(defs.Util_objectTo[targetKind.ordinal()], expr);
                }
            }

            // The makeTypeCast below is usually redundant, since translateAsValue
            // takes care of most conversions - except in the case of a plain object cast.
            // It would be cleaner to move the makeTypeCast to translateAsValue,
            // but it's painful to get it right.  FIXME.
            return TypeCast(targetType, inType, expr);
        }

        JCExpression TypeCast(Type clazztype, Type exprtype, JCExpression translatedExpr) {
            if (types.isSameType(clazztype, exprtype)) {
                return translatedExpr;
            } else {
                Type castType = clazztype;
                if (exprtype != Type.noType && !exprtype.isPrimitive()) {
                    castType = types.boxedTypeOrType(castType);
                }
                JCTree clazz = makeType(castType, true);
                return m().TypeCast(clazz, translatedExpr);
            }
        }

        /* Default value per type */
        JCExpression DefaultValue(Type type) {
            return F3TranslationSupport.this.makeDefaultValue(diagPos, type);
        }
        
        /*
         * Construct a symbol and type for a new class.
         */
        protected ClassSymbol makeClassSymbol(long flags, Name name, Symbol owner) {
            ClassSymbol classSym = new ClassSymbol(flags, name, owner);
            ClassType type = new ClassType(Type.noType, List.<Type>nil(), classSym);
            classSym.type = type;
            return classSym;
        }

        /**
         * Create a method symbol.
         */
        public MethodSymbol makeMethodSymbol(long flags, Type returnType, Name methodName, Symbol owner, List<Type> argTypes) {
            MethodType methodType = new MethodType(argTypes, returnType, List.<Type>nil(), syms.methodClass);
            return new MethodSymbol(flags, methodName, methodType, owner);
        }

        /*
         * Copy the members of a newly created JCClassDecl to it's symbol.
         */
        protected void membersToSymbol(JCClassDecl cls) {
            ClassSymbol cSym = cls.sym;
            Scope members = new Scope(cSym);
            
            for (JCTree tree : cls.getMembers()) {
                if (tree instanceof JCVariableDecl) {
                    JCVariableDecl varDecl = (JCVariableDecl)tree;
                    
                    if (varDecl.sym != null) {
                        members.enter(varDecl.sym);
                    }
                } else if (tree instanceof JCMethodDecl) {
                    JCMethodDecl methDecl = (JCMethodDecl)tree;
                    
                    if (methDecl.sym != null) {
                        members.enter(methDecl.sym);
                    }
                } else if (tree instanceof JCClassDecl) {
                    JCClassDecl classDecl = (JCClassDecl)tree;
                    
                    if (classDecl.sym != null) {
                        members.enter(classDecl.sym);
                    }
                }
            }
            
            cSym.members_field = members;
        }
        protected void membersToSymbol(ClassSymbol cSym, List<JCTree> adding) {
            HashSet<Symbol> symbols = new HashSet<Symbol>();
            Scope members = cSym.members();
           
            for (Scope.Entry e = members.elems; e != null && e.sym != null; e = e.sibling) {
                symbols.add(e.sym);
            }

            for (JCTree tree : adding) {
                if (tree instanceof JCVariableDecl) {
                    JCVariableDecl varDecl = (JCVariableDecl)tree;
                    
                    if (varDecl.sym != null && symbols.add(varDecl.sym)) {
                        members.enter(varDecl.sym);
                    }
                } else if (tree instanceof JCMethodDecl) {
                    JCMethodDecl methDecl = (JCMethodDecl)tree;
                    
                    if (methDecl.sym != null && symbols.add(methDecl.sym)) {
                        members.enter(methDecl.sym);
                    }
                } else if (tree instanceof JCClassDecl) {
                    JCClassDecl classDecl = (JCClassDecl)tree;
                    
                    if (classDecl.sym != null && symbols.add(classDecl.sym)) {
                        members.enter(classDecl.sym);
                    }
                }
            }
        }
        
        /* Debugging support */

        JCStatement Println(String msg) {
            return
                CallStmt(
                    QualifiedTree("java.lang.System.out"),
                    names.fromString("println"),
                    String(msg));
        }

        JCStatement Debug(String msg) {
            return Debug(msg, null);
        }

        JCStatement Debug(String msg, JCExpression obj) {
            return CallStmt(QualifiedTree("java.lang.System.err"), names.fromString("println"),
                    obj==null?
                          String(msg)
                        : PLUS(String(msg + " "), obj));
        }

        List<JCStatement> makeDebugTrace(String msg) {
            return makeDebugTrace(msg, String(""));
        }

        List<JCStatement> makeDebugTrace(String msg, JCExpression obj) {
            String trace = options.get("debugTrace");
            return trace != null ?
                List.<JCStatement>of(Debug(msg, obj))
              : List.<JCStatement>nil();
        }
    }
}
