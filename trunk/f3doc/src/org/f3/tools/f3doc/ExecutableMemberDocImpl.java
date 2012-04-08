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

import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Position;
import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.code.Symbol.*;

import org.f3.tools.code.F3Flags;
import org.f3.tools.tree.F3FunctionDefinition;
import java.text.CollationKey;

import java.lang.reflect.Modifier;
import java.util.logging.Logger;

/**
 * Represents a method or constructor of a java class.
 *
 * @since 1.2
 * @author Robert Field
 * @author Neal Gafter (rewrite)
 * @author Scott Seligman (generics, annotations)
 */

public abstract class ExecutableMemberDocImpl
        extends MemberDocImpl implements ExecutableMemberDoc {

    protected final MethodSymbol sym;

    /**
     * Constructor.
     */
    public ExecutableMemberDocImpl(DocEnv env, MethodSymbol sym,
                                   String rawDocs, F3FunctionDefinition tree, Position.LineMap lineMap) {
        super(env, sym, rawDocs, tree, lineMap);
        this.sym = sym;
    }

    /**
     * Constructor.
     */
    public ExecutableMemberDocImpl(DocEnv env, MethodSymbol sym) {
        this(env, sym, null, null, null);
    }

    /**
     * Identify the containing class
     */
    protected ClassSymbol getContainingClass() {
        return sym.enclClass();
    }

    /**
     * Return true if this method is native
     */
    public boolean isNative() {
        return (getFlags() & Flags.NATIVE) != 0;
    }

    /**
     * Return true if this method is synchronized
     */
    public boolean isSynchronized() {
        return (getFlags() & Flags.SYNCHRONIZED) != 0;
    }

    /**
     * Return true if this method was declared to take a variable number
     * of arguments.
     */
    public boolean isVarArgs() {
        return ((sym.flags() & Flags.VARARGS) != 0
                && !env.legacyDoclet);
    }

    /**
     * Returns true if this field was synthesized by the compiler.
     */
    public boolean isSynthetic() {
        return ((sym.flags() & Flags.SYNTHETIC) != 0);
    }

    public boolean isIncluded() {
        return containingClass().isIncluded() && env.shouldDocument(sym);
    }

    /**
     * Return the throws tags in this method.
     *
     * @return an array of ThrowTagImpl containing all {@code @exception}
     * and {@code @throws} tags.
     */
    public ThrowsTag[] throwsTags() {
        return comment().throwsTags();
    }

    /**
     * Return the param tags in this method, excluding the type
     * parameter tags.
     *
     * @return an array of ParamTagImpl containing all {@code @param} tags.
     */
    public ParamTag[] paramTags() {
        return comment().paramTags();
    }

    /**
     * Return the type parameter tags in this method.
     */
    public ParamTag[] typeParamTags() {
        return env.legacyDoclet
            ? new ParamTag[0]
            : comment().typeParamTags();
    }

    /**
     * Return exceptions this method or constructor throws.
     *
     * @return an array of ClassDoc[] representing the exceptions
     * thrown by this method.
     */
    public ClassDoc[] thrownExceptions() {
        ListBuffer<ClassDocImpl> l = new ListBuffer<ClassDocImpl>();
        for (Type ex : sym.type.getThrownTypes()) {
            ex = env.types.erasure(ex);
            //### Will these casts succeed in the face of static semantic
            //### errors in the documented code?
            ClassDocImpl cdi = env.getClassDoc((ClassSymbol)ex.tsym);
            if (cdi != null) l.append(cdi);
        }
        return l.toArray(new ClassDocImpl[l.length()]);
    }

    /**
     * Return exceptions this method or constructor throws.
     * Each array element is either a <code>ClassDoc</code> or a
     * <code>TypeVariable</code>.
     */
    public com.sun.javadoc.Type[] thrownExceptionTypes() {
        return TypeMaker.getTypes(env, sym.type.getThrownTypes());
    }

    /**
     * Get argument information.
     *
     * @see ParameterImpl
     *
     * @return an array of ParameterImpl, one element per argument
     * in the order the arguments are present.
     */
    public Parameter[] parameters() {
        // generate the parameters on the fly:  they're not cached
        Parameter[] result = new Parameter[0];
        try {
            sym.complete();
            List<VarSymbol> params = sym.params();
            result = new Parameter[params.length()];

            int i = 0;
            for (VarSymbol param : params) {
                result[i++] = new ParameterImpl(env, param);
            }
        } catch (Exception e) {
            Logger.getAnonymousLogger().warning(e.toString());
        }
        return result;
    }

    /**
     * Return the formal type parameters of this method or constructor.
     * Return an empty array if there are none.
     */
    public TypeVariable[] typeParameters() {
        if (env.legacyDoclet || sym.type == null) {
            return new TypeVariable[0];
        }
        List<Type> typeargs = sym.type.getTypeArguments();
        TypeVariable res[] = new TypeVariable[typeargs.length()];
        TypeMaker.getTypes(env, typeargs, res);
        return res;
    }

    /**
     * Get the signature. It is the parameter list, type is qualified.
     * For instance, for a method <code>mymethod(String x, int y)</code>,
     * it will return <code>(java.lang.String,int)</code>.
     */
    public String signature() {
        return makeSignature(true);
    }

    /**
     * Get flat signature.  All types are not qualified.
     * Return a String, which is the flat signiture of this member.
     * It is the parameter list, type is not qualified.
     * For instance, for a method <code>mymethod(String x, int y)</code>,
     * it will return <code>(String, int)</code>.
     */
    public String flatSignature() {
        return makeSignature(false);
    }

    private String makeSignature(boolean full) {
        sym.complete();
        StringBuffer result = new StringBuffer();
        result.append("(");
        for (List<Type> types = sym.type.getParameterTypes(); types.nonEmpty(); ) {
            Type t = types.head;
            result.append(TypeMaker.getTypeString(env, t, full));
            types = types.tail;
            if (types.nonEmpty()) {
                result.append(", ");
            }
        }
        if (isVarArgs()) {
            int len = result.length();
            result.replace(len - 2, len, "...");
        }
        result.append(")");
        return result.toString();
    }

    /**
     * Generate a key for sorting.
     */
    @Override
    CollationKey generateKey() {
        String k = name() + flatSignature();
        // ',' and '&' are between '$' and 'a':  normalize to spaces.
        k = k.replace(',', ' ').replace('&', ' ');
        // System.out.println("COLLATION KEY FOR " + this + " is \"" + k + "\"");
        return env.doclocale.collator.getCollationKey(k);
    }

    /**
     * Return the source position of the entity, or null if
     * no position is available.
     */
    @Override
    public SourcePosition position() {
        if (sym.enclClass().sourcefile == null) return null;
        return SourcePositionImpl.make(sym.enclClass().sourcefile.toString(),
                                       (tree==null) ? 0 : tree.pos,
                                       lineMap);
    }
    
    public boolean isBound() {
        sym.complete();
        return (sym.flags() & F3Flags.BOUND) != 0;
    }
}
