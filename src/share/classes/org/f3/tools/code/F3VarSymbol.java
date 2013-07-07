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

package org.f3.tools.code;

import com.sun.tools.mjavac.code.Kinds;
import org.f3.tools.code.F3ClassSymbol;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.VarSymbol;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Name;
import static com.sun.tools.mjavac.code.Flags.*;

import static org.f3.tools.code.F3Flags.*;

/**
 * Class to hold and access variable information.
 *
 * @author Robert Field
 */
public class F3VarSymbol extends VarSymbol {

    private F3TypeRepresentation typeRepresentation;
    private Type elementType = null;
    private static int IS_DOT_CLASS        = 0x0001;
    private static int IS_EXTERNALLY_SEEN  = 0x0002;
    private static int USED_IN_SIZEOF      = 0x0004;
    private static int USED_OUTSIDE_SIZEOF = 0x0008;
    private static int HAS_VAR_INIT        = 0x0010;
    private int extraFlags;
    private int varIndex = -1;

    private Type lastSeenType;
    private final F3Types types;

    private List<Symbol> overridingClasses = List.nil();
    
    public Type refinedThis;

/****
    private boolean isForwardReferenced = false;
    private boolean hasForwardReferencesInInit = false;
****/
    
    /** Construct a variable symbol, given its flags, name, type and owner.
     */
    public F3VarSymbol(F3Types types, Name.Table names, long flags, Name name, Type type, Symbol owner) {
        super(flags, name, type, owner);
        this.types = types;
        if (name == names._class)
            extraFlags |= IS_DOT_CLASS;
    }
    
    public boolean hasVarInit() {
        return (extraFlags & HAS_VAR_INIT) != 0;
    }
    
    public void setHasVarInit() {
        extraFlags |= HAS_VAR_INIT;
    }

    private void syncType() {
        if (lastSeenType != type) {
            typeRepresentation = types.typeRep(type);
            switch (typeRepresentation) {
                case TYPE_REPRESENTATION_SEQUENCE:
                    elementType = types.elementType(type);
                    break;
                case TYPE_REPRESENTATION_OBJECT:
                    elementType = type;
                    break;
                default:
                    elementType = null;
                    break;
            }
            lastSeenType = type;
        }
    }

    public boolean isMember() {
        return owner.kind == Kinds.TYP && (extraFlags & IS_DOT_CLASS) == 0;
    }

    public boolean isF3Member() {
        return isMember() && types.isF3Class(owner);
    }

    public boolean isSequence() {
        syncType();
        return typeRepresentation.isSequence();
    }

    public Type getElementType() {
        syncType();
        return elementType;
    }

    public F3TypeRepresentation getTypeRepresentation() {
        syncType();
        return typeRepresentation;
    }

    public long instanceVarAccessFlags() {
        return flags_field & F3AllInstanceVarFlags;
    }

    public boolean hasScriptOnlyAccess() {
        long access = instanceVarAccessFlags();
        return access == SCRIPT_PRIVATE || access == PRIVATE;
    }

    public boolean isSpecial() {
        return (flags_field & F3Flags.VARUSE_SPECIAL) != 0;
    }

    public boolean isSynthetic() {
        return (flags_field & SYNTHETIC) != 0;
    }

    public boolean isBindAccess() {
        return (flags_field & F3Flags.VARUSE_BIND_ACCESS) != 0;
    }
    
    public boolean isInitializedInObjectLiteral() {
        return (flags_field & F3Flags.VARUSE_OBJ_LIT_INIT) != 0;
    }

    public boolean isInMixin() {
        return (owner.flags_field & MIXIN) != 0;
    }

    public boolean isReferenced() {
        return (flags_field & F3Flags.VARUSE_VARREF) != 0;
    }
    
    public boolean isInScriptingModeScript() {
        return owner instanceof F3ClassSymbol &&
               ((F3ClassSymbol) owner).isScriptingModeScript();
    }
    
    public boolean isDefault() {
        return (flags_field & DEFAULT) != 0;
    }

    private boolean accessorsRequired() {
        return (flags_field & (VARUSE_BIND_ACCESS | VARUSE_BOUND_INIT | VARUSE_HAS_TRIGGER | VARUSE_VARREF | VARUSE_FORWARD_REFERENCE)) != 0;
    }
    
    public boolean useAccessors() {
        return 
                isF3Member() &&
                !isSpecial() &&
                (   !hasScriptOnlyAccess() ||
                    isInMixin() ||
                    accessorsRequired() ||
                    isInScriptingModeScript() ||
                    (   isBindAccess() &&
                        isAssignedTo()
                    )
                );
    }
    
    public boolean needsEnumeration() {
        return useAccessors() ||
               useGetters() ||
               hasVarInit() ||
               isExternallySeen() ||
               isInitializedInObjectLiteral();
    }

    public boolean hasFlags() {
        return needsEnumeration();
    }

    public boolean useGetters() {
        return !isSpecial() && (useAccessors() || (flags_field & VARUSE_NON_LITERAL) != 0);
    }

    public boolean useSetters() {
        return
                isF3Member() &&
                !isSpecial() &&
                (   !hasScriptOnlyAccess() ||
                    isInMixin() ||
                    isReferenced() ||
                    (   (isAssignedTo() || isInitializedInObjectLiteral()) &&
                        accessorsRequired()
                    )
                );
    }

    /** Either has a trigger or a sub-class may have a trigger. */
    public boolean useTrigger() {
        return ! hasScriptOnlyAccess() || (flags_field & VARUSE_HAS_TRIGGER) != 0;
    }

    // Predicate for def (constant) var.
    public boolean isDef() {
        return (flags_field & IS_DEF) != 0;
    }

    public boolean isParameter() {
        return (flags_field & PARAMETER) != 0;
    }

    public void setIsExternallySeen() {
        extraFlags |= IS_EXTERNALLY_SEEN;
    }

    public boolean isExternallySeen() {
        return (extraFlags & IS_EXTERNALLY_SEEN) != 0;
    }

    public boolean isUsedInSizeof() {
        return (extraFlags & USED_IN_SIZEOF) != 0;
    }

    public boolean isUsedOutsideSizeof() {
        return (extraFlags & USED_OUTSIDE_SIZEOF) != 0;
    }

    public void clearUsedOutsideSizeof() {
        extraFlags &= ~USED_OUTSIDE_SIZEOF;
    }

    public void setUsedInSizeof() {
        extraFlags |= USED_IN_SIZEOF;
    }

    public void setUsedOutsideSizeof() {
        extraFlags |= USED_OUTSIDE_SIZEOF;
    }

    // Predicate for self-reference in init.
    public boolean hasSelfReference() {
        return (flags_field & VARUSE_SELF_REFERENCE) != 0;
    }
/****
    public void setIsForwardReferenced() {
        isForwardReferenced = true;
    }

    public boolean isForwardReferenced() {
        return isForwardReferenced;
    }
***/
    public boolean hasForwardReference() {
        return (flags_field & VARUSE_FORWARD_REFERENCE) != 0;
    }

    public boolean isAssignedTo() {
        return (flags_field & VARUSE_ASSIGNED_TO) != 0;
    }

    public boolean isDefinedBound() {
        //TODO: this bit, it would appear, is not see for shreds
        return (flags_field & VARUSE_BOUND_INIT) != 0;
    }

    public boolean isWritableOutsideScript() {
        return !isDef() && (flags_field & (PUBLIC | PROTECTED | PACKAGE_ACCESS)) != 0;
    }

    public boolean isMutatedWithinScript() {
        return (flags_field & (VARUSE_ASSIGNED_TO | VARUSE_SELF_REFERENCE | VARUSE_FORWARD_REFERENCE)) != 0L;
    }

    public boolean canChange() {
        return isWritableOutsideScript() || isMutatedWithinScript() || isDefinedBound();
    }

    public boolean isMutatedLocal() {
        return !isMember() && isMutatedWithinScript();
    }

    public int getVarIndex() {
        return varIndex;
    }

    public void setVarIndex(int varIndex) {
        this.varIndex = varIndex;
    }

    public int getAbsoluteIndex(Type site) {
        types.supertypesClosure(site);
        return isLocal() || isStatic() ?
            getVarIndex() :
            getVarIndex() + baseIndex((TypeSymbol)owner, site);
    }
    //where
    private int baseIndex(TypeSymbol tsym, Type site) {
        List<Type> closure = types.supertypesClosure(site, false, true);
        int baseIdx = 0;
        for (Type t : closure) {
            if (types.isSameType(t, tsym.type)) break;
            baseIdx += ((F3ClassSymbol)t.tsym).getMemberVarCount();
        }
        return baseIdx;
    }

    public void addOverridingClass(Symbol s) {
        overridingClasses = overridingClasses.append(s);
    }

    public boolean isOverridenIn(Symbol s) {
        return overridingClasses.contains(s);
    }
}
