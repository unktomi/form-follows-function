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

package f3.reflect;
import java.util.*;
import org.f3.runtime.Checks;

/** A run-time representation of a F3 class.
 * Corresponds to {@code java.lang.Class}.
 *
 * @author Per Bothner
 * @profile desktop
 */

public abstract class F3ClassType extends F3Type implements F3Member {
    String name;
    F3Context context;
    protected int modifiers;
    protected static final int F3_MIXIN = 1;
    protected static final int F3_CLASS = 2;

    public static final String SEQUENCE_CLASSNAME =
            "org.f3.runtime.sequence.Sequence";
    public static final String OBJECT_VARIABLE_CLASSNAME =
            "org.f3.runtime.location.ObjectVariable";
    public static final String SEQUENCE_VARIABLE_CLASSNAME =
            "org.f3.runtime.location.SequenceVariable";
    public static final String FUNCTION_CLASSNAME_PREFIX =
            "org.f3.functions.Function";
    public static final String GETTER_PREFIX = "get$";
    public static final String SETTER_PREFIX = "set$";
    public static final String LOCATION_GETTER_PREFIX = "loc$";

    protected F3ClassType(F3Context context, int modifiers) {
        this.context = context;
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        String n = getName();
        if (n == null)
            n = "<anonymous>";
        n = "class "+n;
        F3Type[] targs = getTypeArguments();
        if (targs == null || targs.length == 0) {
            targs = getTypeParameters();
        }
        if (targs != null && targs.length > 0) {
            n += " of (";
            String sep = "";
            for (int i = 0; i < targs.length; i++) {
                n += sep;
                n += targs[i];
                sep = ", ";
            }
            n += ")";
        }
        return n;
    }

    public boolean equals (Object obj) {
        if (obj instanceof F3ClassType) {
            F3ClassType other = (F3ClassType)obj;
            return Checks.equals(context, other.context) && Checks.equals(name, other.name);
        }
        return false;
    }

    public boolean isGenericInstance() {
        return false;
    }

    public F3ClassType getGenericBase() {
        return this;
    }

    public abstract boolean isParameterized();

    public abstract F3Type[] getTypeArguments();
    public abstract F3Type[] getTypeParameters();

    public int hashCode() {
        return name.hashCode();
    }

    /** Get list of super-classes.
     * Note we don't distinguish between classes and interfaces.
     * @param all if true include all ancestor classes (including this class).
     * @return the list of super-classes.  It sorted by class name for
     *   convenience and consistency.
     */
    public abstract List<F3ClassType> getSuperClasses(boolean all);
    
    public boolean isMixin() {
        return (modifiers & F3_MIXIN) != 0;
    }

    @Override
    public boolean isF3Type() {
        return (modifiers & F3_CLASS) != 0;
    }

    protected boolean isDirectlyAssignableFrom(F3ClassType cls) {
        return this.equals(cls);
    }

    public boolean isAssignableFrom(F3ClassType cls) {
        if (this.isDirectlyAssignableFrom(cls))
            return true;
        List<F3ClassType> supers = cls.getSuperClasses(false);
        for (F3ClassType s : supers) {
            if (isAssignableFrom(s))
                return true;
        }
        return false;
    }

    public List<F3Member> getMembers(F3MemberFilter filter, boolean all) {
        SortedMemberArray<F3Member> result = new SortedMemberArray<F3Member>();
        if (all) {
            List<F3ClassType> supers = getSuperClasses(all);
            for (F3ClassType cl : supers)
                cl.getMembers(filter, result);
        }
        else
            getMembers(filter, result);
        return result;
    }
    public List<F3Member> getMembers(boolean all) {
        return getMembers(new F3MemberFilter(), all);
    }
    protected void getMembers(F3MemberFilter filter, SortedMemberArray<F3Member> result) {
        getVariables(filter, result);
        getFunctions(filter, result);
    }
    
    public List<F3FunctionMember> getFunctions(F3MemberFilter filter, boolean all) {
        SortedMemberArray<F3FunctionMember> result = new SortedMemberArray<F3FunctionMember>();
        if (all) {
            List<F3ClassType> supers = getSuperClasses(all);
            for (F3ClassType cl : supers)
                cl.getFunctions(filter, result);
        }
        else
            getFunctions(filter, result);
        return result;
    }
    public List<F3FunctionMember> getFunctions(boolean all) {
        return getFunctions(F3MemberFilter.acceptMethods(), all);
    }
    protected abstract void getFunctions(F3MemberFilter filter, SortedMemberArray<? super F3FunctionMember> result);
    
    public List<F3VarMember> getVariables(F3MemberFilter filter, boolean all) {
        SortedMemberArray<F3VarMember> result = new SortedMemberArray<F3VarMember>();
        if (all) {
            List<F3ClassType> supers = getSuperClasses(all);
            boolean isMixin = isMixin();
            for (F3ClassType cl : supers) {
                if (isMixin || !cl.isMixin())
                    cl.getVariables(filter, result);
            }
        }
        else
            getVariables(filter, result);
        return result;
    }
    public List<F3VarMember> getVariables(boolean all) {
        return getVariables(F3MemberFilter.acceptAttributes(), all);
    }
    protected abstract void getVariables(F3MemberFilter filter, SortedMemberArray<? super F3VarMember> result);

    /** Get a member with the matching name and type - NOT IMPLEMENTED YET.
     * (A method has a FunctionType.)
     * (Unimplemented because it requires type matching.)
     */
    public F3Member getMember(String name, F3Type type) {
        throw new UnsupportedOperationException("getMember not implemented yet.");
    }

    /** Get the attribute (field) of this class with a given name. */
    public F3VarMember getVariable(String name) {
        F3MemberFilter filter = new F3MemberFilter();
        filter.setAttributesAccepted(true);
        filter.setRequiredName(name);
        List<F3VarMember> attrs = getVariables(filter, true);
        return attrs.isEmpty() ? null : attrs.get(attrs.size() - 1);
    }

    /** Find the function that (best) matches the name and argument types. */
    public abstract F3FunctionMember getFunction(String name, F3Type... argType);

    public F3Context getReflectionContext() {
        return context;
    }

    /** Return raw uninitialized object. */
    public abstract F3ObjectValue allocate ();

    /** Create a new initialized object.
     * This is just {@code allocate}+{@code F3ObjectValue.initialize}.
     */
    public F3ObjectValue newInstance() {
        return allocate().initialize();
    }

    static class SortedMemberArray<T extends F3Member> extends AbstractList<T> {
        F3Member[] buffer = new F3Member[4];
        int sz;
        public T get(int index) {
            if (index >= sz)
                throw new IndexOutOfBoundsException();
            return (T) buffer[index];
        }
        public int size() { return sz; }
        // This is basically 'add' under a different non-public name.
        boolean insert(T cl) {
            String clname = cl.getName();
            // We could use binary search, but the lack of a total order
            // for ClassLoaders complicates that.  Linear search should be ok.
            int i = 0;
            for (; i < sz; i++) {
                F3Member c = buffer[i];
                // First compare by name.
                int cmp = c.getName().compareToIgnoreCase(clname);
                if (cmp == 0)
                    cmp = c.getName().compareTo(clname);
                if (cmp > 0)
                    break;
                if (cmp < 0)
                    continue;
                // Next compare by owner. Inherited members go earlier.
                F3ClassType clowner = cl.getDeclaringClass();
                F3ClassType cowner = c.getDeclaringClass();
                boolean clAssignableFromC = clowner.isAssignableFrom(cowner);
                boolean cAssignableFromCl = cowner.isAssignableFrom(clowner);
                if (clAssignableFromC && ! cAssignableFromCl)
                    break;
                if (cAssignableFromCl && ! clAssignableFromC)
                    continue;
                // Next compare by owner name.
                String clownerName = clowner.getName();
                String cownerName = cowner.getName();
                cmp = cownerName.compareToIgnoreCase(clownerName);
                if (cmp == 0)
                    cmp = cownerName.compareTo(clownerName);
                if (cmp > 0)
                    break;
                if (cmp < 0)
                    continue;
                // Sort member classes before other members.
                if (cl instanceof F3ClassType)
                    break;
                if (c instanceof F3ClassType)
                    continue;
                // Sort var after member classes, but before other members.
                if (cl instanceof F3VarMember)
                    break;
                if (c instanceof F3VarMember)
                    continue;
                if (cl instanceof F3FunctionMember && c instanceof F3FunctionMember) {
                    String scl = ((F3FunctionMember) cl).getType().toString();
                    String sc = ((F3FunctionMember) c).getType().toString();
                    cmp = sc.compareToIgnoreCase(scl);
                    if (cmp == 0)
                        cmp = sc.compareTo(scl);
                    if (cmp < 0)
                        continue;
                }
                // Otherwise arbitrary order.
                break;
            }
            if (sz == buffer.length) {
                F3Member[] tmp = new F3Member[2*sz];
                System.arraycopy(buffer, 0, tmp, 0, sz);
                buffer = tmp;
            }
            System.arraycopy(buffer, i, buffer, i+1, sz-i);
            buffer[i] = cl;
            sz++;
            return true;
        }
    }

    public F3VarMember getThis() {

        return new F3VarMember() {

            public F3Type getType() {
                return F3ClassType.this;
            }

            /** Get the offset of the attribute. */
            public int getOffset() {
                return -1;
            }

            /** Get the value of the attribute in a specified object. */
            public F3Value getValue(F3ObjectValue obj) {
                return obj;
            }

            /** Set the value of the attribute in a specified object. */
            public void setValue(F3ObjectValue obj, F3Value newValue) {
                throw new UnsupportedOperationException();
            }

            /** Get a handle for the attribute in a specific object. */
            public F3Location getLocation(final F3ObjectValue obj) {
                return new F3Location() {
                    public F3Value getValue() {
                        return obj;
                    }

                    public void setValue(F3Value newValue) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            protected void initVar(F3ObjectValue instance, F3Value value) {
            }
            public void initValue(F3ObjectValue obj, F3Value ref) {
            }
    
            /** Add an on replace listener to the objects var. **/
            public F3ChangeListenerID addChangeListener(F3ObjectValue instance, F3ChangeListener listener) {
                return null;
            }
            
            /** Remove an on replace listener from the objects var. **/
            public void removeChangeListener(F3ObjectValue instance, F3ChangeListenerID id) {

            }
            /** True if {@code public-read} was specified. */
            public boolean isPublicRead() {
                return false;
            }
            
            /** True if {@code public-init} was specified. */
            public boolean isPublicInit() {
                return false;
            }
            
            /** True if if the variable is defined with {@code def} rather than {@code var}. */
            public boolean isDef() {
                return true;
            }

            public String getName() {
                return "this";
            }
            
            public F3ClassType getDeclaringClass() {
                return F3ClassType.this;
            }

            public boolean isStatic() {
                return false;
            }

            /** The member was specified to have public access. */
            public boolean isPublic() {
                return true;
            }

            /** The member was specified to have package access.
             * For a member of a Java class, returns true if the member has default (blank) access.
             */
            public boolean isPackage() {
                return false;
            }

            /** The member was specified to have protected access. */
            public boolean isProtected() {
                return false;
            }
        };
    }
}
