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

import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;

import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3VarSymbol;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.tree.JCTree;

/**
 * A class declaration
 */
public class F3ClassDeclaration extends F3Expression implements ClassDeclarationTree {
    public final F3Modifiers mods;
    private final Name name;
    public List<F3Expression> typeArgs;
    private List<F3Expression> extending;
    private List<F3Expression> implementing;
    private List<F3Expression> mixing;
    private List<F3Tree> defs;
    private List<F3Expression> supertypes;
    private List<F3VarSymbol> objInitSyms;
    
    public ClassSymbol sym;
    public F3FunctionDefinition runMethod;
    public Scope runBodyScope;
    public boolean isScriptClass;
    private boolean isScriptingModeScript;
    public boolean hasBeenTranslated; // prevent multiple translations
    
    private ListBuffer<JCTree> classInvokeCases;
    private ListBuffer<JCTree> scriptInvokeCases;

    protected F3ClassDeclaration() {
        this.mods = null;
        this.name = null;
        this.extending = null;
        this.implementing = null;
        this.mixing = null;
        this.defs = null;
        this.supertypes = null;
        this.objInitSyms = null;
        
        this.sym = null;
        this.runMethod = null;
        this.runBodyScope = null;
        this.isScriptClass = false;
        this.hasBeenTranslated = false;
        
        this.classInvokeCases = ListBuffer.lb();
        this.scriptInvokeCases = ListBuffer.lb();
    }
    protected F3ClassDeclaration(F3Modifiers mods,
            Name name,
            List<F3Expression> supertypes,
            List<F3Tree> declarations,
            ClassSymbol sym) {
        this.mods = mods;
        this.name = name;           
        this.extending = null;
        this.implementing = null;
        this.mixing = null;
        this.defs = declarations;
        this.supertypes = supertypes;
        this.objInitSyms = null;
        
        this.sym = sym;
        this.runMethod = null;
        this.runBodyScope = null;
        this.isScriptClass = false;
        this.hasBeenTranslated = false;
        
        this.classInvokeCases = ListBuffer.lb();
        this.scriptInvokeCases = ListBuffer.lb();
    }

    public boolean isScriptClass() {
        return isScriptClass;
    }

    public java.util.List<ExpressionTree> getSupertypeList() {
        return convertList(ExpressionTree.class, supertypes);
    }

    public F3Modifiers getModifiers() {
        return mods;
    }

    public Name getName() {
        return name;
    }

    public List<F3Expression> getSupertypes() {
        return supertypes;
    }

    public List<F3Tree> getMembers() {
        return defs;
    }

    public void setMembers(List<F3Tree> members) {
        defs = members;
    }

    public List<F3Expression> getImplementing() {
        return implementing;
    }

    public List<F3Expression> getExtending() {
        return extending;
    }

    public List<F3Expression> getMixing() {
        return mixing;
    }

    public void setDifferentiatedExtendingImplementingMixing(List<F3Expression> extending,
                                                       List<F3Expression> implementing,
                                                       List<F3Expression> mixing) {
        this.extending    = extending;
        this.implementing = implementing;
        this.mixing       = mixing;
        
        // VSGC-2820 - Reorder the supertypes during attribution.
        ListBuffer<F3Expression> orderedSuperTypes = new ListBuffer<F3Expression>();
        
        // Add supers according to declaration and normal, mixin and interface constraints.
        for (F3Expression extend    : extending)    orderedSuperTypes.append(extend);
        for (F3Expression mixin     : mixing)       orderedSuperTypes.append(mixin);
        for (F3Expression implement : implementing) orderedSuperTypes.append(implement);
        
        // Replace supertypes so that all references use the correct ordering.
        supertypes = orderedSuperTypes.toList();
    }
    
    public boolean isMixinClass() {
        return (sym.flags_field & F3Flags.MIXIN) != 0;
    }
    
    public boolean isBoundFuncClass() {
        return (sym.flags_field & F3Flags.F3_BOUND_FUNCTION_CLASS) != 0L;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.CLASS_DEF;
    }
    
    public void accept(F3Visitor v) {
        v.visitClassDeclaration(this);
    }

    public F3Kind getF3Kind() {
        return F3Kind.CLASS_DECLARATION;
    }

    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitClassDeclaration(this, data);
    }

    public javax.lang.model.element.Name getSimpleName() {
        return (javax.lang.model.element.Name)name;
    }

    public java.util.List<ExpressionTree> getImplements() {
        return F3Tree.convertList(ExpressionTree.class, implementing);
    }

    public java.util.List<Tree> getClassMembers() {
        return convertList(Tree.class, defs);
    }

    public java.util.List<ExpressionTree> getExtends() {
        return convertList(ExpressionTree.class, extending);
    }

    public java.util.List<ExpressionTree> getMixins() {
        return convertList(ExpressionTree.class, mixing);
    }
    
    public void setObjInitSyms(List<F3VarSymbol> syms) {
        objInitSyms = syms;
    }
    
    public List<F3VarSymbol> getObjInitSyms() {
        return objInitSyms;
    }
    
    public int addInvokeCase(JCTree invokeCase, boolean isScript) {
        if (isScript) {
            scriptInvokeCases.append(invokeCase);
            return scriptInvokeCases.size() - 1;
        } else {
            classInvokeCases.append(invokeCase);
            return classInvokeCases.size() - 1;
        }
    }
    
    public List<JCTree> invokeCases(boolean isScript) {
        if (isScript) {
            return scriptInvokeCases.toList();
        } else {
            return classInvokeCases.toList();
        }
    }

    public boolean isScriptingModeScript() {
        return isScriptingModeScript;
    }

    public void setScriptingModeScript() {
        isScriptingModeScript = true;
    }
}
