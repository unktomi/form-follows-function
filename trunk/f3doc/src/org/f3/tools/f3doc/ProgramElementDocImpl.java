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

package org.f3.tools.f3doc;

import com.sun.javadoc.*;

import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.code.Symbol.ClassSymbol;

import org.f3.tools.tree.F3Tree;

import com.sun.tools.mjavac.util.Position;

import org.f3.tools.code.F3Flags;
import java.lang.reflect.Modifier;
import java.text.CollationKey;

/**
 * Represents a java program element: class, interface, field,
 * constructor, or method.
 * This is an abstract class dealing with information common to
 * these elements.
 *
 * @see MemberDocImpl
 * @see ClassDocImpl
 *
 * @author Robert Field
 * @author Neal Gafter (rewrite)
 * @author Scott Seligman (generics, enums, annotations)
 */
public abstract class ProgramElementDocImpl
        extends DocImpl implements ProgramElementDoc {

    // For source position information.
    F3Tree tree = null;
    Position.LineMap lineMap = null;

    private final long flags;

    protected ProgramElementDocImpl(DocEnv env, Symbol sym,
                                    String doc, F3Tree tree, Position.LineMap lineMap) {
        super(env, doc);
        this.tree = tree;
        this.lineMap = lineMap;
        this.flags = sym.flags_field;
    }

    void setTree(F3Tree tree) {
        this.tree = tree;
    }

    /**
     * Subclasses override to identify the containing class
     */
    protected abstract ClassSymbol getContainingClass();

    /**
     * Returns the flags in terms of javac's flags
     */
    protected long getFlags() {
        return flags;
    }

    /**
     * Get the containing class of this program element.
     *
     * @return a ClassDocImpl for this element's containing class.
     * If this is a class with no outer class, return null.
     */
    public ClassDoc containingClass() {
        if (getContainingClass() == null) {
            return null;
        }
        return env.getClassDoc(getContainingClass());
    }

    /**
     * Return the package that this member is contained in.
     * Return "" if in unnamed package.
     */
    public PackageDoc containingPackage() {
        return env.getPackageDoc(getContainingClass().packge());
    }

    public int modifierSpecifier() {
        throw new UnsupportedOperationException("cannot use modifierSpecifier() with F3");
    }

    /**
     * Get modifiers string.
     * <pre>
     * Example, for:
     *   public abstract int foo() { ... }
     * modifiers() would return:
     *   'public abstract'
     * </pre>
     * Annotations are not included.
     */
    public String modifiers() {
        long aflags = getFlags();
        if (isAnnotationTypeElement() ||
                (isMethod() && containingClass().isInterface())) {
            // Remove the implicit abstract modifier.
            aflags &= ~Modifier.ABSTRACT;
        }
        return modifiers(aflags);
    }

    protected String modifiers(long aflags) {
        StringBuffer sb = new StringBuffer();

	if ((aflags  & F3Flags.PUBLIC_INIT) != 0)	sb.append("public-init ");
	if ((aflags  & F3Flags.PUBLIC_READ) != 0)	sb.append("public-read ");
	if ((aflags  & Flags.PUBLIC) != 0)	sb.append("public ");
	if ((aflags  & Flags.PROTECTED) != 0)	sb.append("protected ");
	if ((aflags  & (Flags.PUBLIC | Flags.PROTECTED | F3Flags.SCRIPT_PRIVATE)) == 0)	sb.append("package ");
	if ((aflags  & F3Flags.BOUND) != 0)	sb.append("bound ");
	if ((aflags  & Flags.ABSTRACT) != 0)	sb.append("abstract ");

	int len = sb.length();
	if (len > 0)	/* trim trailing space */
	    return sb.toString().substring(0, len-1);
	return "";
    }

    /**
     * Get the annotations of this program element.
     * Return an empty array if there are none.
     */
    public AnnotationDesc[] annotations() {
        return new AnnotationDesc[0];
    }

    /**
     * Return true if this program element is public
     */
    public boolean isPublic() {
        return (getFlags() & Flags.PUBLIC) != 0;
    }

    /**
     * Return true if this program element is protected
     */
    public boolean isProtected() {
        return (getFlags() & Flags.PROTECTED) != 0;
    }

    /**
     * Return true if this program element is private
     */
    public boolean isPrivate() {
        return (getFlags() & Flags.PRIVATE) != 0;
    }

    /**
     * Returns true if this program element is script-private
     */
    public boolean isScriptPrivate() {
        return (getFlags() & F3Flags.SCRIPT_PRIVATE) != 0;
    }

    /**
     * Return true if this program element is package private
     */
    public boolean isPackagePrivate() {
        return !(isPublic() || isScriptPrivate() || isPrivate() || isProtected());
    }

    /**
     * Return true if this program element is static
     */
    public boolean isStatic() {
        return (getFlags() & Flags.STATIC) != 0;
    }

    /**
     * Return true if this program element is final
     */
    public boolean isFinal() {
        return (getFlags() & Flags.FINAL) != 0;
    }

    /**
     * Generate a key for sorting.
     */
    @Override
    CollationKey generateKey() {
        String k = name();
        // System.out.println("COLLATION KEY FOR " + this + " is \"" + k + "\"");
        return env.doclocale.collator.getCollationKey(k);
    }

}
