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

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.f3.functions.*;
import org.f3.runtime.F3Object;
import org.f3.runtime.Pointer;
import org.f3.runtime.TypeInfo;
import org.f3.runtime.sequence.Sequence;
import org.f3.runtime.sequence.Sequences;

/**
 * Implement F3 reflection on top of {@java.lang.reflect}.
 * Hence, this implementation can only reflect/mirror values and classes
 * in the same VM that is doing the reflection.
 *
 * @author Per Bothner
 * @profile desktop
 */
public class F3Local {
    public static Context getContext() { return Context.instance; }

    /** Implementation of {@link F3Context} using Java reflection.
     * Can only access objects and types in the current JVM.
     * Normally, this is a singleton, though in the future there might
     * be variants with different class search paths (similar to
     * {@code com.sun.jdi.PathSearchingVirtualMachine}).
     *
     * @profile desktop
     */

    public static class Context extends F3Context {
        static Context instance = new Context();

        private Context () {
        }

	static F3Type[] boxedPrimitives =  
	{
	    F3PrimitiveType.voidType, instance.makeClassRef(F3PrimitiveType.voidType.getBoxed()),
	    F3PrimitiveType.booleanType, instance.makeClassRef(F3PrimitiveType.booleanType.getBoxed()),
	    F3PrimitiveType.byteType, instance.makeClassRef(F3PrimitiveType.byteType.getBoxed()),
	    F3PrimitiveType.charType, instance.makeClassRef(F3PrimitiveType.charType.getBoxed()),
	    F3PrimitiveType.shortType, instance.makeClassRef(F3PrimitiveType.shortType.getBoxed()),
	    F3PrimitiveType.integerType, instance.makeClassRef(F3PrimitiveType.integerType.getBoxed()),
	    F3PrimitiveType.longType, instance.makeClassRef(F3PrimitiveType.longType.getBoxed()),
	    F3PrimitiveType.floatType, instance.makeClassRef(F3PrimitiveType.floatType.getBoxed()),
	    F3PrimitiveType.doubleType, instance.makeClassRef(F3PrimitiveType.doubleType.getBoxed())
	};


        /** Get the default instance. */
        public static Context getInstance() { return instance; }

	public F3FunctionType makeFunctionType(F3Type[] argTypes, F3Type returnType) 
	{
	    return new F3FunctionType(argTypes, returnType);
	}

        /** Create a reference to a given Object. */
        public ObjectValue mirrorOf(Object obj) {
            return new ObjectValue(obj, this);
        }

        public Value mirrorOf(final Object val, final F3Type type) {
            // FIXME Perhaps if val==null we should use MiscValue?
            if (type instanceof ClassType)
                return new F3Local.ObjectValue(val, (ClassType) type);
            else if (type instanceof F3PrimitiveType) {
                return ((F3PrimitiveType) type).mirrorOf(val);
            }
            else if (type instanceof F3SequenceType && val instanceof Sequence) {
                Sequence seq = (Sequence) val;
                return new SequenceValue(seq, (F3SequenceType) type, this);
            }
            else if (type instanceof F3FunctionType && val instanceof Function) {
                final F3FunctionType ftype = (F3FunctionType) type;
                return new FunctionValue((Function) val, ftype, this);
            } else {
                return new MiscValue(val, type);
            }
        }

        public ObjectValue mirrorOf(String val) {
          return new ObjectValue(val, this);
        }

        /** Get the {@code F3ClassType} for the class with the given name. */
        public ClassType findClass(String cname) {
            ClassLoader loader;
            try {
                loader = Thread.currentThread().getContextClassLoader();
            }
            catch (java.lang.SecurityException ex) {
               loader = getClass().getClassLoader();
            }
            return findClass(cname, loader);
        }

        /** Get the {@code F3ClassType} for the class with the given name. */
        public ClassType findClass(String cname, ClassLoader loader) {
            String n = cname;
            Exception ex0 = null;
            for (;;) {
                try {
                    Class cl = Class.forName(n, false, loader);
                    // if (! cl.getCanonicalName().equals(cname)) break;
                    return makeClassRef(cl);
                } catch (Exception ex) {
                    if (ex0 == null)
                        ex0 = ex;
                    int dot = n.lastIndexOf('.');
                    if (dot < 0)
                        break;
                    n = n.substring(0, dot)+'$'+n.substring(dot+1);
                }
            }
            throw new RuntimeException(ex0);
        }

        static final String LOCATION_PREFIX = "org.f3.runtime.location.";
        static final int LOCATION_PREFIX_LENGTH = LOCATION_PREFIX.length();
        static final String VARIABLE_STRING = "Variable";
        static final int VARIABLE_STRING_LENGTH = VARIABLE_STRING.length();

        public F3Type makeTypeRef(Type typ) {
            Object t = PlatformUtils.resolveGeneric(this, typ);
            if (t instanceof F3Type)
                return (F3Type) t;
            Class clas = (Class) t;
            if (clas.isArray()) {
                F3Type elType = makeTypeRef(clas.getComponentType());
                return new F3JavaArrayType(elType);
            }
            String rawName = clas.getName();
            int rawLength = rawName.length();
            if (rawLength > LOCATION_PREFIX_LENGTH + VARIABLE_STRING_LENGTH &&
                    rawName.startsWith(LOCATION_PREFIX) &&
                    rawName.endsWith(VARIABLE_STRING)) {
                rawName = rawName.substring(LOCATION_PREFIX_LENGTH,
                        rawLength-VARIABLE_STRING_LENGTH);
                
                if (rawName.endsWith("Sequence")) { 
                    int newLength = rawName.length() - "Sequence".length(); 
                    if (newLength != 0) rawName = rawName.substring(0, newLength - 1); 
                    F3Type ptype = getPrimitiveType(rawName); 
                    if (ptype != null) 
                        return new F3SequenceType(ptype); 
                } 
                F3Type ptype = getPrimitiveType(rawName);
                if (ptype != null)
                    return ptype;
            }
            if (typ == Byte.TYPE || typ == Byte.class)
                return F3PrimitiveType.byteType;
            if (typ == Short.TYPE || typ == Short.class)
                return F3PrimitiveType.shortType;
            if (typ == Integer.TYPE || typ == Integer.class)
                return F3PrimitiveType.integerType;
            if (typ == Long.TYPE || typ == Long.class)
                return F3PrimitiveType.longType;
            if (typ == Float.TYPE || typ == Float.class)
                return F3PrimitiveType.floatType;
            if (typ == Double.TYPE || typ == Double.class)
                return F3PrimitiveType.doubleType;
            if (typ == Character.TYPE || typ == Character.class)
                return F3PrimitiveType.charType;
            if (typ == Boolean.TYPE || typ == Boolean.class)
                return F3PrimitiveType.booleanType;
            if (typ == Void.TYPE || typ == Void.class)
                return F3PrimitiveType.voidType;
            return makeClassRef(clas, typ);
        }

        public ClassType makeClassRef(Class cls) {
	    return makeClassRef(cls, cls);
	}
        /** Create a reference to a given Class. */
        public ClassType makeClassRef(Class cls, Type type) {
	    if (boxedPrimitives != null) {
		for (int i = 0; i < boxedPrimitives.length; i += 2) {
		    F3PrimitiveType t = (F3PrimitiveType)boxedPrimitives[i];
		    if (t.getBoxed() == cls) {
			return (ClassType)boxedPrimitives[i+1];
		    }
		}
	    }
            int modifiers = 0;
            try {
                Class[] interfaces = cls.getInterfaces();
                for (int i = 0;  i < interfaces.length;  i++ ) {
                    String iname = interfaces[i].getName();
                    if (iname.equals(F3OBJECT_NAME)) {
                        modifiers |= F3ClassType.F3_CLASS;
                    } else if (iname.equals(F3MIXIN_NAME)) {
                        modifiers |= F3ClassType.F3_MIXIN;
                    } 
                }
                Class clsInterface = null;
                if ((modifiers & F3ClassType.F3_MIXIN) != 0) {
                    String cname = cls.getName();
                    
                    if (cname.endsWith(MIXIN_SUFFIX)) {
                        cname = cname.substring(0, cname.length() - MIXIN_SUFFIX.length());
                        clsInterface = cls;
                        cls = Class.forName(cname, false, cls.getClassLoader());
                        if (cls == null) throw new RuntimeException("Missing mixin class " + cname);
                       
                    } else {
                        String intfName = cname + MIXIN_SUFFIX;
                        clsInterface = Class.forName(intfName, false, cls.getClassLoader());
                        if (clsInterface == null) throw new RuntimeException("Missing mixin interface " + intfName);
                    }
                }
 
                return new ClassType(this, modifiers, cls, clsInterface, type);
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    
        public static Class asClass (F3Type type) {
            if (type instanceof F3PrimitiveType)
                return ((F3PrimitiveType) type).clas;
            else if (type instanceof JavaArrayType)
                return ((JavaArrayType) type).getJavaClass();
            else if (type instanceof F3SequenceType)
                return Sequence.class;
            else { // FIXME - handle other cases
                ClassType ctyp = (ClassType) type;
                return ctyp.isMixin() ? ctyp.refInterface : ctyp.refClass;
            }
        }

        @Override
        public Value makeSequenceValue(F3Value[] values, int nvalues, F3Type elementType) {
            return new SequenceValue(values, nvalues, elementType, this);
        }
    }

    static class JavaArrayType extends F3JavaArrayType {
        Class cls;
        JavaArrayType(F3Type componentType, Class cls) {
            super(componentType);
            this.cls = cls;
        }

        public Class getJavaClass() { return cls; }
    }


    /** A mirror of a {@code Class} in the current JVM.
     * @profile desktop
     */
    public static class ClassType extends F3ClassType {
	Type type;
        Class refClass;
        Class refInterface;
	protected static int VOFF_INITIALIZED = 1 << 16; // high to avoid collisions with masks added in parent class.

        public ClassType(Context context, int modifiers,
			 Class refClass, Class refInterface, Type type) {
            super(context, modifiers);
            this.refClass = refClass;
            this.refInterface = refInterface;
            this.name = PlatformUtils.getCanonicalName(refClass);
	    this.type = type;
        }

	public boolean isConvertibleFrom(F3Type typ) {
	    if (typ instanceof F3PrimitiveType) {
		for (int i = 0; i < Context.boxedPrimitives.length; i += 2) {
		    if (Context.boxedPrimitives[i] == typ) {
			typ = Context.boxedPrimitives[i+1];
			break;
		    }
		}
	    }
	    return isAssignableFrom(typ);
	}

        static final F3Type[] EMPTY_TYPE_ARRAY = new F3Type[0];

        public F3Type[] getTypeArguments() {
            if (type instanceof ParameterizedType) {
                Type[] tvars = ((ParameterizedType)type).getActualTypeArguments();
                F3Type[] result = new F3Type[tvars.length];
                for (int i = 0; i < tvars.length; i++) {
                    result[i] = F3Local.getContext().makeTypeRef(tvars[i]);
                }
                return result;
            } 
            return getTypeParameters();
        }

        public F3Type[] getTypeParameters() {
            TypeVariable<?>[] tvars = refClass.getTypeParameters();
            F3Type[] result = new F3Type[tvars.length];
            for (int i = 0; i < tvars.length; i++) {
                result[i] = F3Local.getContext().makeTypeRef(tvars[i]);
            }
            return result;
        }

	public Type getType() { return type; }
        public Class getJavaImplementationClass() { return refClass; }
        public Class getJavaInterfaceClass() { return refInterface; }

        public boolean isTypeVariable() {
            return type instanceof TypeVariable;
        }

        public String getTypeVariable() {
            return ((TypeVariable)type).getName();
        }

        @Override
        public Context getReflectionContext() {
            return (Context) super.getReflectionContext();
        }

        /** Returns a hash-code.
         * @return the hash-code of the name.
         */
        @Override
        public int hashCode() {
            return (name != null ? name : refClass.getName()).hashCode();
        }
    
        @Override
        public boolean equals (Object obj) {
            return obj instanceof ClassType
                && refClass == ((ClassType) obj).refClass;
        }

        void getSuperClasses(boolean all, SortedClassArray result) {
            boolean isMixin = this.isMixin();
            Class cls = isMixin ? refInterface : refClass;
            Class[] interfaces = cls.getInterfaces();
            Context context = getReflectionContext();
            if (! isMixin) {
                Class s = cls.getSuperclass();
                if (s != null) {
                    ClassType cl = (ClassType) context.makeClassRef(s);
                    if (result.insert(cl) && all)
                        cl.getSuperClasses(all, result);
                }
            }
            for (int i = 0;  i < interfaces.length;  i++) {
                Class iface = interfaces[i];
                String iname = iface.getName();
                if (iname.equals(Context.F3OBJECT_NAME) || iname.equals(Context.F3MIXIN_NAME))
                    continue;
                ClassType cl = (ClassType) context.makeClassRef(iface);
                if (result.insert(cl) && all)
                    cl.getSuperClasses(all, result);
            }
        }

        public List<F3ClassType> getSuperClasses(boolean all) {
            SortedClassArray result = new SortedClassArray();
            if (all)
                result.insert(this);
            getSuperClasses(all, result);
            return result;
        }
    
        public F3FunctionMember getFunction(String name, F3Type... argType) {
            int nargs = argType.length;
            Class[] ctypes = new Class[nargs];
            for (int i = 0;  i < nargs;  i++) {
                ctypes[i] = Context.asClass(argType[i]);
            }
            try {
                Method meth;
                try {
                    meth = refClass.getMethod(name, ctypes);
                } catch (NoSuchMethodException ex) {
                    if (isMixin())
                        meth = null;
                    else
                        throw ex;
                }
                if (isMixin())
                    if (meth == null ||
                            (meth.getModifiers() &  Modifier.STATIC) == 0) {
                    meth = refInterface.getMethod(name, ctypes);
                }
                return asFunctionMember(meth, getReflectionContext());
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    
        private <M extends Member> M[] filter(M[] methods, Class declaringClass) {
            List<M> result = new ArrayList<M>();
            for (M m : methods) {
                if (m.getDeclaringClass() == declaringClass) {
                    result.add(m);
                }
            }
            return result.toArray(methods);
        }
        
        static final String[] SYSTEM_METHOD_EXCLUDES = {
            // keep in alphabetical order 
            "addDependent$",
            "complete$",
            "count$",
            "getAsBoolean$",
            "getAsByte$",
            "getAsChar$",
            "getAsDouble$",
            "getAsFloat$",
            "getAsInt$",
            "getAsLong$",
            "getAsShort$",
            "getDepChain$internal$",
            "getThisRef$internal$",
            "getListenerCount$",
            "getType$",
            "initialize$",
            "isInitialized$",
            "isInitialized$internal$",
            "f3$run$",
            "makeInitMap$",
            "notifyDependents$",
            "postInit$",
            "printBits$",
            "printBitsAction$",
            "removeDependent$",
            "restrictSet$",
            "setDepChain$internal$",
            "setInitialized$internal$",
            "setThisRef$internal$",
            "switchDependence$",
            "userInit$",
            "getFlags$",
            "setFlags$",
            "varChangeBits$",
            "varTestBits$",
            "VCNT$"
        };
        static final String[] SYSTEM_METHOD_PREFIXES = {
            "applyDefaults$",
            "get$",
            "elem$",
            "initVars$",
            "invalidate$",
            "invoke$",
            "onReplace$",
            "seq$",
            "set$",
            "size$",
            "update$",
            "DCNT$",
            "DEP$",
            "FCNT$",
            "GETMAP$",
            "VOFF$"
        };
        static final String[] SYSTEM_METHOD_SUFFIXES = {
            "$impl"
        };

        protected void getFunctions(F3MemberFilter filter, SortedMemberArray<? super F3FunctionMember> result) {
            final Class cls = refClass;
            final Context context = getReflectionContext();
            {
                Method[] methods;
                try {
                    methods = cls.getDeclaredMethods();
                } catch (SecurityException e) {
                    methods = filter(cls.getMethods(), cls);
                }
                skip: for (int i = 0;  i < methods.length;  i++) {
                    Method m = methods[i];
                    if (PlatformUtils.isSynthetic(m))
                        continue;
                    if (PlatformUtils.checkInherited(m) > 0)
                        continue;
                    if (isMixin()) {
                        try {
                            final int dollar = m.getName().lastIndexOf("$impl");
                            if (dollar > 0) {
                                m = refInterface.getDeclaredMethod(m.getName().substring(0, dollar), 
                                                                   java.util.Arrays.copyOfRange(m.getParameterTypes(), 
                                                                                                1, 
                                                                                                m.getParameterTypes().length));
                            }
                        }
                        catch (Exception ex) {
                            // Just ignore ???
                            ex.printStackTrace();
                        }
                    }
                    final String mname = m.getName();
                    for (String exclude : SYSTEM_METHOD_EXCLUDES) {
                        if (mname.equals(exclude))
                            continue skip;
                    }
                    for (String prefix : SYSTEM_METHOD_PREFIXES) {
                        if (mname.startsWith(prefix))
                            continue skip;
                    }
                    for (String suffix : SYSTEM_METHOD_SUFFIXES) {
                        if (mname.endsWith(suffix))
                            continue skip;
                    }
                    final F3FunctionMember mref = asFunctionMember(m, context);
                    if (filter != null && filter.accept(mref)) {
                        result.insert(mref);
                    }
                }
            }
            if (!isF3Type()) {
                Constructor[] methods;
                try {
                    methods = cls.getDeclaredConstructors();
                } catch (SecurityException e) {
                    methods = filter(cls.getConstructors(), cls);
                }
                skip: for (int i = 0;  i < methods.length;  i++) {
                    final Constructor m = methods[i];
                    final F3FunctionMember mref = asFunctionMember(m, context);
                    if (filter != null && filter.accept(mref)) {
                        result.insert(mref);
                    }
                }
            }
        }
    
        F3FunctionMember asFunctionMember(Method m, Context context) {
            java.lang.reflect.Type[] ptypes = PlatformUtils.getGenericParameterTypes(m);
            /*
            if (m.isVarArgs()) {
                // ????
            }
            */
            F3Type[] prtypes = new F3Type[ptypes.length];
            for (int j = 0; j < ptypes.length;  j++)
                prtypes[j] = context.makeTypeRef(ptypes[j]);
            java.lang.reflect.Type gret = PlatformUtils.getGenericReturnType(m);
            F3FunctionType type = new F3FunctionType(prtypes, context.makeTypeRef(gret));
            return new F3Local.MethodFunctionMember(m, this, type);
        }
    
        F3FunctionMember asFunctionMember(Constructor m, Context context) {
            java.lang.reflect.Type[] ptypes = PlatformUtils.getGenericParameterTypes(m);
            F3Type[] prtypes = new F3Type[ptypes.length];
            for (int j = 0; j < ptypes.length;  j++)
                prtypes[j] = context.makeTypeRef(ptypes[j]);
            java.lang.reflect.Type gret = PlatformUtils.getGenericReturnType(m);
            F3FunctionType type = new F3FunctionType(prtypes, context.makeTypeRef(gret));
            return new F3Local.CtorMember(m, this, type);
        }
    
        private Field[] filter(Field[] fields, Class declaringClass) {
            List<Field> result = new ArrayList<Field>();
            for (Field f : fields) {
                if (f.getDeclaringClass() == declaringClass) {
                    result.add(f);
                }
            }
            return result.toArray(new Field[0]);
        }

        static protected java.lang.reflect.Method getMethodOrNull(Class cls, String name, Class... types) {
            java.lang.reflect.Method method = null;
            try {
                method = cls.getMethod(name, types);
            } catch (Throwable ex) {
            }
            return method;
        }

        static Object[] NO_ARGS = { };
        
        static protected int callMethodIntOrDefault(java.lang.reflect.Method method, int deflt) {
            if (method != null) {
                try {
                    deflt = ((Integer)method.invoke(null, NO_ARGS)).intValue();
                } catch (Throwable ex) {
                }
            }
            return deflt;
        }

        static final String[] SYSTEM_VAR_PREFIXES = {
            "DCNT$",
            "DEP$",
            "FCNT$",
            "VFLG$",
            "VCNT$",
            "VOFF$",
            "MAP$",
            "$script$"
        };

	private void ensureVOffInitialized() {
	    // if no instances of this class have been created, there's no guarantee that VOFF$xxx are initialized,
	    // force initialization.
	    if ((this.modifiers & VOFF_INITIALIZED) == 0) {
		try {
		    refClass.getMethod("VCNT$").invoke(null, NO_ARGS);
		} catch (Throwable e) {
		} 
		this.modifiers |= VOFF_INITIALIZED;
	    }
	}


        protected void getVariables(F3MemberFilter filter, SortedMemberArray<? super F3VarMember> result) {
            VarMember[] varTable = this.variables;
            if (varTable != null) {
                String requiredName = filter.getRequiredName();
                if (requiredName != null) {
                    VarMember v = varTable[searchVariable(varTable, requiredName)];
                    if (v != null)
                        result.insert(v);
                } else {
                    for (int i = varTable.length; --i >= 0; ) {
                        VarMember v = varTable[i];
                        if (v != null && filter.accept(v))
                            result.insert(v);
                    }
                }
                return;
            }
            Context ctxt = getReflectionContext();
            Class cls = refClass;
            java.lang.reflect.Field[] fields;
            ArrayList<VarMember> varList = new ArrayList<VarMember>();
            try {
                fields = cls.getDeclaredFields();
            } catch (SecurityException e) {
                fields = filter(cls.getFields(), cls);
            }
            fieldLoop: for (java.lang.reflect.Field fld : fields) {
                if (PlatformUtils.isSynthetic(fld)) {
                    continue;
                }
                String fname = fld.getName();
                
                String sname = PlatformUtils.getSourceNameFromAnnotation(fld);
                if (sname == null) {
                    int dollar = fname.lastIndexOf('$');
                    if (dollar == -1) {
                        sname = fname;
                    } else {
                        for (String prefix : SYSTEM_VAR_PREFIXES) {
                            if (fname.startsWith(prefix)) {
                                continue fieldLoop;
                            }
                        }
                        if (fname.endsWith("$internal$"))
                            continue fieldLoop;
                        sname = fname.substring(dollar + 1);
                    }
                }
               
                F3Type tr = ctxt.makeTypeRef(fld.getGenericType());
		
		ensureVOffInitialized();
                int offset;
                try {
                    java.lang.reflect.Field field = cls.getField("VOFF" + fname);
                    offset = field.getInt(null);
                } catch (Throwable ex) {
                    offset = -1;
                }

                VarMember ref = new VarMember(sname, this, tr, offset);
                ref.fld = fld;
                varList.add(ref);

                if (filter != null && filter.accept(ref))
                    result.insert(ref);
            }

            // Enter varList into the varTable hash-table.
            int nvars = varList.size();
            // We want the varTable hash-table to be at most 75% full.
            int varTableSize = 8;
            while (4 * nvars > 3 * varTableSize)
                varTableSize += varTableSize;
            varTable = new VarMember[varTableSize];
            while (--nvars >= 0) {
                VarMember v = varList.get(nvars);
                varTable[searchVariable(varTable, v.name)] = v;
            }
            this.variables = varTable;
        }

        // A simple hash-table keyed vt variable name.
        VarMember[] variables;

        /** A simple hash-table search algorithm.
         * @param variables The hash-table.  Its length must be a power of two,
         *    and there must be at least one unused slot.
         * @param name The name of the VarMember to search for.
         * @return The index of a slot in {@code variables} that matches the name,
         *     or null if there is no such slot.
         */
        static int searchVariable(VarMember[] variables, String name) {
            int hash = name.hashCode();
            int size = variables.length;
            int h = 12347 * hash + hash;
            int mask = size - 1;
            int i = h & mask;
            for (;;) {
                VarMember v = variables[i];
                if (v == null || v.name.equals(name))
                    return i;
                i = (i + 53) & mask;
            }
        }

        public ObjectValue allocate () {
            Class cls = refClass;
            Context context = getReflectionContext();
            try {
                Object instance;
                if (isF3Type()) {
                    // Note that Java5-style varargs are not supported on CDC.
                    Class[] types = { Boolean.TYPE };
                    Constructor cons = cls.getDeclaredConstructor(types);
                    Object[] args = { Boolean.TRUE };
                    instance = cons.newInstance(args);
                }
                else {
                    instance = cls.newInstance();
                }
                return new ObjectValue(instance, this);
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public F3ClassType getDeclaringClass() {
            return null;
        }

        public boolean isStatic() {
            return true;
        }

        Annotation getAnnotation (Class clas) {
            Class cls = refClass;
            return cls.getAnnotation(clas);
        }

        public boolean isPublic() {
            int p = PlatformUtils.checkPublic(this);
            if (p >= 0)
                return p > 0;
            Class cls = refClass;
            return (cls.getModifiers() & Modifier.PUBLIC) != 0;
        }

        public boolean isProtected() {
            return PlatformUtils.isProtected(this);
        }

        public boolean isPackage() {
            int p = PlatformUtils.checkPackage(this);
            if (p >= 0)
                return p > 0;
            Class cls = refClass;
            return (cls.getModifiers() & Modifier.PUBLIC) == 0;
        }
    }
    
    static class SortedClassArray extends AbstractList<F3ClassType> {
        ClassType[] buffer = new ClassType[4];
        int sz;
        public F3ClassType get(int index) {
            if (index >= sz)
                throw new IndexOutOfBoundsException();
            return buffer[index];
        }
        public int size() { return sz; }
        // This is basically 'add' under a different non-public name.
        boolean insert(ClassType cl) {
            String clname = cl.getName();
            // We could use binary search, but the lack of a total order
            // for ClassLoaders complicates that.  Linear search should be ok.
            int i = 0;
            for (; i < sz; i++) {
                ClassType c = buffer[i];
                String cname = c.getName();
                int cmp = cname == clname ? 0
                        : cname == null ? -1 : clname == null ? 1
                        : cname.compareTo(clname);
                if (cmp > 0)
                    break;
                if (cmp == 0) {
                    if (c.refClass == cl.refClass)
                        return false;
                    // Arbitrary order if same name but different loaders.
                    break;
                }
            }
            if (sz == buffer.length) {
                ClassType[] tmp = new ClassType[2*sz];
                System.arraycopy(buffer, 0, tmp, 0, sz);
                buffer = tmp;
            }
            System.arraycopy(buffer, i, buffer, i+1, sz-i);
            buffer[i] = cl;
            sz++;
            return true;
        }
    }

    public static class VarMember extends F3VarMember {
        Field fld;
        Method getter;
        Method setter;
        F3Type type;
        String name;
        ClassType owner;
        int offset;
        static final int GETTER_SETTER_SET = 1;
        static final int ACCESS_FLAGS_SET = 2;
        static final int IS_PUBLIC = 4;
        static final int IS_PROTECTED = IS_PUBLIC << 1;
        static final int IS_PACKAGE = IS_PUBLIC << 2;
        static final int IS_PUBLIC_INIT = IS_PUBLIC << 3;
        static final int IS_PUBLIC_READ = IS_PUBLIC << 4;
        int flags;
    
        public VarMember(String name, ClassType owner, F3Type type, int offset) {
            this.name = name;
            this.type = type;
            this.owner = owner;
            this.offset = offset;
        }

        @Override
        public F3Type getType() {
            return type;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        private void checkGetterSetter() {
            if ((flags & GETTER_SETTER_SET) != 0)
                return;
            Class cls = owner.refInterface;
            if (cls == null)
                cls = owner.refClass;
            String get, set;
            boolean isF3 = owner.isF3Type();
            if (isF3) {
                get = "get$";
                set = "set$";
            } else {
                get = "get";
                set = "set";
            }
            Method g = ClassType.getMethodOrNull(cls, get + name);
            String xname = name;
            if (g == null && isF3) {
                xname = cls.getSimpleName() + "$" + name;
                g = ClassType.getMethodOrNull(cls, get + xname);
            }
            getter = g;
            flags |= GETTER_SETTER_SET;
            if (g != null) {
                Class rtype = g.getReturnType();
                setter = ClassType.getMethodOrNull(cls, set + xname, rtype);
            }
        }

        @Override
        public F3Value getValue(F3ObjectValue obj) {
            Object robj = obj == null ? null : ((ObjectValue) obj).obj;
            try {
                checkGetterSetter();
                if (fld != null || getter != null) {
                    Context context =
                        (Context) owner.getReflectionContext();
                    Object val;
                    if (getter != null) {
                        val = getter.invoke(robj, new Object[0]);
                    } else {
                        fld.setAccessible(true);
                        val = fld.get(robj);
                    }
                    // FIXME: yet to be implemented for compiled binds
                    return context.mirrorOf(val, type);
                }
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                if (fld != null)
                    throw new RuntimeException("Illegal access of field " + fld);
                else
                    throw new RuntimeException("Illegal access of field getter " + getter);
            }
            throw new UnsupportedOperationException("Not supported yet - "+type+"["+type.getClass().getName()+"]");
        }
        
        public F3Location getLocation(F3ObjectValue obj) {
            return new VarMemberLocation(obj, this);
        }

	public Pointer getPointer(F3Object obj) {
	    return Pointer.make(obj, offset);
	}

        static final Object[] noObjects = {};

        protected void initVar(F3ObjectValue instance, F3Value value) {
            instance.initVar(this, value);
        }

        @Override
        public void initValue(F3ObjectValue instance, F3Value value) {
            instance.initVar(this, value);
        }

        @Override
        public void setValue(F3ObjectValue obj, F3Value value) {
            Object robj = obj == null ? null : ((ObjectValue) obj).obj;
            try {
                if (type instanceof F3SequenceType && robj instanceof F3Object) {
                    Sequences.set((F3Object)robj, offset,(Sequence)((Value) value).asObject());
                    return;
                }
                checkGetterSetter();
                if (fld != null || setter != null) {
                    if (setter != null) {
                        Object[] args = { ((Value) value).asObject() };
                        setter.invoke(robj, args);
                        return;
                    } else {
                        // FIXME: yet to be implemented for compiled binds
                        if (fld != null) {
                            fld.setAccessible(true);
                            fld.set(robj, ((Value) value).asObject());
                            return;
                        }
                    }

                }
		if (robj instanceof F3Object) {
		    ((F3Object)robj).set$(offset, ((Value)value).asObject());
		}
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getName() {
            return name;
        }

        public F3ClassType getDeclaringClass() {
            return owner;
        }

        private void checkAccessFlags() {
            if ((flags & ACCESS_FLAGS_SET) != 0)
                return;
            flags |= ACCESS_FLAGS_SET;
            checkGetterSetter();
            if (! getDeclaringClass().isF3Type()
                    || ! PlatformUtils.checkAccessAnnotations(this)) {
                int mods = getter != null ? getter.getModifiers()
                        : fld.getModifiers();
                if ((mods & Modifier.PUBLIC) != 0)
                    flags |= IS_PUBLIC;
                if ((mods & Modifier.PROTECTED) != 0)
                    flags |= IS_PROTECTED;
                int mask = Modifier.PUBLIC|Modifier.PROTECTED|Modifier.PRIVATE;
                if ((mods & mask) == 0)
                    flags |= IS_PACKAGE;
             }
        }

        public boolean isStatic() {
            checkGetterSetter();
            int mods = getter != null ? getter.getModifiers()
                    : fld.getModifiers();
            return (mods & Modifier.STATIC) != 0;
        }

        public boolean isPublic() {
            checkAccessFlags();
            return (flags & IS_PUBLIC) != 0;
        }

        public boolean isProtected() {
             checkAccessFlags();
            return (flags & IS_PROTECTED) != 0;
        }

        public boolean isPackage() {
            checkAccessFlags();
            return (flags & IS_PACKAGE) != 0;
        }

        public boolean isPublicInit() {
            checkAccessFlags();
            return (flags & IS_PUBLIC_INIT) != 0;
        }

        public boolean isPublicRead() {
            checkAccessFlags();
            return (flags & IS_PUBLIC_READ) != 0;
        }

        public boolean isDef() {
            int d = PlatformUtils.checkDef(this);
            if (d >= 0)
                return d > 0;
            return fld != null && (fld.getModifiers() & Modifier.FINAL) != 0;
        }

        static class ListenerAdapter extends org.f3.runtime.F3Base implements F3ChangeListenerID {
            final F3ChangeListener listener;
            
            ListenerAdapter(F3ChangeListener listener) {
                this.listener = listener;
            }
            
            @Override
            public boolean update$(F3Object src, final int depNum, int startPos, int endPos, int newLength, int phase) {
                // varNum does not matter, there is one change listener per <src, varNum> tuple.
                if ((phase & PHASE_TRANS$PHASE) == PHASE$TRIGGER) {
                    this.listener.onChange();
                }
                return true;
            }
        }

        public F3ChangeListenerID addChangeListener(F3ObjectValue instance, F3ChangeListener listener) {
	    if (!this.owner.isAssignableFrom(instance.getType()))
		throw new IllegalArgumentException("not an instance of " + this.owner);
	    // check if instance acually has a variable represented by this
	    F3Object src = (F3Object)((Value)instance).asObject();
	    ListenerAdapter adapter = new ListenerAdapter(listener);
	    src.addDependent$(this.offset, adapter, 0);
            return adapter;
        }
        
        public void removeChangeListener(F3ObjectValue instance, F3ChangeListenerID id) {
	    if (!this.owner.isAssignableFrom(instance.getType()))
		throw new IllegalArgumentException("not an instance of " + this.owner);
	    F3Object src = (F3Object)((Value)instance).asObject();
	    src.removeDependent$(this.offset, (ListenerAdapter)id);
        }

    }

    static abstract class FunctionMember<M extends Member> extends F3FunctionMember {
        M method;
        F3ClassType owner;
        String name;
        F3FunctionType type;

        abstract public Annotation getAnnotation(Class clazz);

        FunctionMember(M method, ClassType owner, F3FunctionType type, String name) {
            this.method = method;
            this.owner = owner;
            this.type = type;
            this.name = name;
        }

        public String getName() { return name; }

        public F3ClassType getDeclaringClass() { return owner; }
    
        public abstract boolean isStatic();

        public F3FunctionType getType() {
            return type;
        }

        Object unwrap(F3Value value) {
            if (value == null)
                return null;
            return ((Value) value).asObject();
        }

        abstract protected Object invoke(Object target, Object[] args) throws Exception;

        /** Invoke this method on the given receiver and arguments. */
        public F3Value invoke(F3ObjectValue obj, F3Value... arg) {
            int alen = arg.length;
            Object[] rargs = new Object[alen];
            for (int i = 0;  i < alen;  i++) {
                rargs[i] = unwrap(arg[i]);
            }
            try {
                Object result = invoke(unwrap(obj), rargs);
                Context context =
                        (Context) owner.getReflectionContext();
                if (result == null && getType().getReturnType() == F3PrimitiveType.voidType)
                    return null;
                return context.mirrorOf(result, getType().getReturnType());
            }
             catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public boolean isPublic() {
            int p = PlatformUtils.checkPublic(this);
            if (p >= 0)
                return p > 0;
            else
                return (method.getModifiers() & Modifier.PUBLIC) != 0;
        }

        public boolean isProtected() {
            int p = PlatformUtils.checkProtected(this);
            if (p >= 0)
                return p > 0;
            else
                return (method.getModifiers() & Modifier.PROTECTED) != 0;
        }

        public boolean isPackage() {
            int p = PlatformUtils.checkPackage(this);
            if (p >= 0)
                return p > 0;
            int mods = method.getModifiers();
            int mask = Modifier.PUBLIC|Modifier.PROTECTED|Modifier.PRIVATE;
            return (mods & mask) == 0;
        }
    }
    
    static class MethodFunctionMember extends FunctionMember<Method> {
        MethodFunctionMember(Method method, ClassType owner, F3FunctionType type) {
            super(method, owner, type, method.getName());
        }

        @Override
        public boolean isStatic() {
            return (method.getModifiers() &  Modifier.STATIC) != 0;
        }

        @Override 
        protected Object invoke(Object target, Object[] args) throws Exception {
            return method.invoke(target, args);
        }

        @Override
        public Annotation getAnnotation(Class clazz) {
            return method.getAnnotation(clazz);
        }

    }

    static String shortName(Constructor clazz) {
        final String name = clazz.getName();
        int p = name.lastIndexOf('$');
        if (p < 0) {
            p = name.lastIndexOf('.');
        }
        return p < 0 ? name: name.substring(p+1);
    }

    static class CtorMember extends FunctionMember<Constructor> {
        CtorMember(Constructor method, ClassType owner, F3FunctionType type) {
            super(method, owner, type, "new "+shortName(method));
        }

        @Override
        public boolean isStatic() {
            return true;
        }

        @Override 
            protected Object invoke(Object target, Object[] args) throws Exception {
            return method.newInstance(args);
        }

        @Override
        public Annotation getAnnotation(Class clazz) {
            return method.getAnnotation(clazz);
        }
    }

    /** A value in the current JVM.
     *
     * @profile desktop
     */
    public static interface Value extends F3Value {
        public abstract Object asObject();
    }

    static class MiscValue implements F3Local.Value {
        Object val;
        F3Type type;
        public MiscValue(Object value, F3Type type) {
            this.val = value;
            this.type = type;
        }

        public String getValueString() { return val == null ? "(null)" : val.toString(); }
        public F3Type getType() { return type; }
        public boolean isNull() { return val == null; }
        public Object asObject() { return val; }
        public F3Value getItem(int index) { return this; }
        public int getItemCount() { return isNull() ? 0 : 1; }
    };

    /** A mirror of an {@code Object} in the current JVM.
     *
     * @profile desktop
     */
    public static class ObjectValue extends F3ObjectValue implements F3Local.Value {
        // FIXME It might be cleaner to require obj!=null,
        // and instead use MiscValue for null.
        Object obj;
        ClassType type;
        ClassType classType;
        int count;
        F3VarMember[] initMembers;
        F3Value[] initValues;
        

        public ObjectValue(Object obj, Context context) {
            type = obj == null ? (ClassType) context.anyType
                    : context.makeClassRef(obj.getClass());
            this.obj = obj;
            if (obj instanceof F3Object) 
                count = ((F3Object) obj).count$();
        }

        public ObjectValue(Object obj, ClassType type) {
            this.type = type;
            this.obj = obj;
            if (obj instanceof F3Object) 
                count = ((F3Object) obj).count$();
        }

        public F3ClassType getType() {
            return type;
        }
        
        public F3ClassType getClassType() {
            if (classType == null) {
                if (obj == null)
                    classType = type;
                else {
                    Class cls = obj.getClass();
                    classType = type.getJavaImplementationClass() == cls ? type
                            : type.getReflectionContext().makeClassRef(cls);
                }
            }
            return classType;
        }

        public boolean isNull() {
            return obj == null;
        }

        public String getValueString() {
            if (obj == null)
                return null;
            else
                return obj.toString();
        }

        public ObjectValue initialize() {
            if (obj instanceof F3Object) {
                F3Object instance = (F3Object)obj;
            
                if (initMembers == null) {
                    instance.initialize$(true);
                } else {
                    int count = count();
                    
                    instance.initVars$();
                    
                    for (int offset = 0; offset < count; offset++ ) {
                        instance.varChangeBits$(offset, 0, F3Object.VFLGS$INIT$READY);
                        
                        if (initMembers[offset] != null) {
                            initMembers[offset].setValue(this, initValues[offset]);
                        } else {
                            instance.applyDefaults$(offset);
                        }
                    }
                    
                    instance.complete$();
                }
            }
            return this;
        }

        public Object asObject() { return obj; }
        
        private int count() {
            return obj instanceof F3Object ? ((F3Object) obj).count$() : 0;
        }
        
        public void initVar(F3VarMember attr, F3Value value) {
            int offset = attr.getOffset();

            if (offset == -1) {
                attr.setValue(this, value);
            } else {
                if (initMembers == null) {
                    int count = count();
                
                    initMembers = new F3VarMember[count];
                    initValues = new F3Value[count];
                }
                
                initMembers[offset] = attr;
                initValues[offset] = value;
                int flag = attr.getType() instanceof F3SequenceType ? F3Object.VFLGS$INIT_OBJ_LIT_SEQUENCE
                    : F3Object.VFLGS$INIT_OBJ_LIT;
                ((F3Object)obj).setFlags$(offset, flag);
            }
        }
    }

    static class SequenceValue extends F3SequenceValue implements F3Local.Value {
        Sequence seq;
        Context context;

        public SequenceValue(F3Value[] values, int nvalues, F3Type elementType,
                Context context) {
            super(values, nvalues, elementType);
            this.context = context;
        }

        public SequenceValue(Sequence seq, F3SequenceType sequenceType,
                Context context) {
            super(seq.size(), sequenceType);
            this.seq = seq;
            this.context = context;
        }

        public F3Value getItem(int index) {
            if (index < 0 || index >= nvalues )
                return null;
            if (values == null)
                values = new F3Value[nvalues];
            if (values[index] == null && seq != null)
                values[index] =
                        context.mirrorOf(seq.get(index), type.getComponentType()); 
            return values[index];
        }
    
        public Sequence asObject() {
            if (seq == null) {
                F3Type elementType = type.getComponentType();
                Object[] objs = new Object[nvalues];
                for (int i = 0;  i < nvalues;  i++)
                    objs[i] = ((F3Local.Value) values[i]).asObject();
                return Sequences.make(TypeInfo.getTypeInfo(context.asClass(elementType)), objs);
            }
            return seq;
        }
    }

    /** Mirror a {@code Function} value in the current JVM.
     *
     * @profile desktop
     */
    public static class FunctionValue extends F3FunctionValue implements F3Local.Value {
        Function val;
        F3FunctionType ftype;
        Context context;
        public FunctionValue(Function val, F3FunctionType ftype, Context context) {
            this.val = val;
            this.ftype = ftype;
            this.context = context;
        }

        public F3Value apply(F3Value... arg) {
            Object result;
            int nargs = arg.length;
            if (nargs > 8) throw new IllegalArgumentException();
            Object[] rargs = nargs > 2 ? new Object[nargs] : null;
            Object arg1 = null, arg2 = null;
            for (int i = 0;  i < nargs;  i++) {
                Object targ = ((F3Local.Value) arg[i]).asObject();
                if (i == 0)
                    arg1 = targ;
                else if (i == 1)
                    arg2 = targ;
                else
                    rargs[i-2] = targ;
            }
            result = ((Function) val).invoke$(arg1, arg2, rargs);
            return context.mirrorOf(result, ftype.getReturnType());
        }
        public F3FunctionType getType() {
            return ftype;
        }
        public boolean isNull() { return false; }
        public String getValueString() { return ftype.toString()+"{...}"; };
        public Function asObject() { return val; }
    }

    /**
     *
     * @profile desktop
     */
    public static class VarMemberLocation extends F3VarMemberLocation {
        VarMember var;

        public VarMemberLocation(F3ObjectValue object, VarMember var) {
            super(object, var);
            this.var = var;
        }

        // FIXME: yet to be implemented for compiled binds
        //    public AbstractVariable getAbstractVariable(F3ObjectValue obj) {...}
    }


    /*

    public F3FunctionMember makeFunctionMember(final String name,
					       final F3FunctionType funType,
					       final org.f3.function.Function impl)
	return new F3FunctionMember() {
	    public String getName() {return name;}
	    public F3ClassType getDeclaringClass() { return null }
	    public boolean isStatic() { return true; }
	    public boolean isPublic() {return true; }
	    public boolean isPackage() { return false; }
	    public boolean isProtected() { return false; }
	    public F3FunctionType getType() { return funType; }
	    public F3Value invoke(F3ObjectValue owner, F3Value... arg)
	    {
	    }
	}
    }
    */
}
