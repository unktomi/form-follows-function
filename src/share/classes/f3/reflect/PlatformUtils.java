/*
 * Copyright 2008-2010 Sun Microsystems, Inc.  All Rights Reserved.
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
import org.f3.runtime.annotation.*;
import org.f3.runtime.annotation.Package;

/**
 * Helper methods to factor out functionality only available on J2SE.
 * @author Per Bothner
 */


class PlatformUtils {
    static Type[] getGenericParameterTypes(Method m) {
        return m.getGenericParameterTypes();
    }

    static Type getGenericReturnType(Method m) {
        return m.getGenericReturnType();
    }

    static boolean isSynthetic(Field fld) {
        return fld.isSynthetic();
    }

    static boolean isSynthetic(Method m) {
        return m.isSynthetic();
    }

    static int checkInherited(Method m) {
        return m.getAnnotation(org.f3.runtime.annotation.Inherited.class) != null ? 1 : 0;
    }

    static String getCanonicalName(Class cls) {
        return cls.getCanonicalName();
    }

    static String getSourceNameFromAnnotation(Field fld) {
        SourceName sname = fld.getAnnotation(SourceName.class);
        return sname == null ? null : sname.value();
    }

    static Annotation getAnnotation (F3Local.ClassType ctype, Class clas) {
        Class cls = ctype.refClass;
        return cls.getAnnotation(clas);
    }

    static <T extends Annotation> T getAnnotation (F3Local.VarMember vmem, Class<T> clas) {
        Field fld = vmem.fld;
        if (fld != null)
            return fld.getAnnotation(clas);
        Method getter = vmem.getter;
        if (getter != null)
            return getter.getAnnotation(clas);
        return null;
    }

    static int checkPublic(F3Local.ClassType ctype) {
        if (ctype.isF3Type())
            return -1;
        return getAnnotation(ctype, Public.class) != null ? 1 : 0;
    }

    static int checkPackage(F3Local.ClassType ctype) {
        if (! ctype.isF3Type())
            return -1;
        return getAnnotation(ctype, Package.class) != null ? 1 : 0;
    }

    static boolean isProtected(F3Local.ClassType ctype) {
        return getAnnotation(ctype, Protected.class) != null;
    }

    static int checkAccess(F3Local.VarMember vmem, Class ann) {
        if (! vmem.getDeclaringClass().isF3Type())
           return -1;
       return getAnnotation(vmem, ann) != null ? 1 : 0;
    }

    static boolean checkAccessAnnotations(F3Local.VarMember vmem) {
        if (getAnnotation(vmem, Public.class) != null)
             vmem.flags |= F3Local.VarMember.IS_PUBLIC;
        if (getAnnotation(vmem, Protected.class) != null)
            vmem.flags |= F3Local.VarMember.IS_PROTECTED;
        if (getAnnotation(vmem, Package.class) != null)
            vmem.flags |= F3Local.VarMember.IS_PACKAGE;
        if (getAnnotation(vmem, PublicInitable.class) != null)
            vmem.flags |= F3Local.VarMember.IS_PUBLIC_INIT;
        if (getAnnotation(vmem, PublicReadable.class) != null)
            vmem.flags |= F3Local.VarMember.IS_PUBLIC_READ;
        return true;
    }

    static int checkDef(F3Local.VarMember vmem) {
        return checkAccess(vmem, Def.class);
    }

    static int checkAccess(F3Local.FunctionMember fmem, Class ann) {
        if (! fmem.getDeclaringClass().isF3Type())
           return -1;
       return fmem.method.getAnnotation(ann) != null ? 1 : 0;
    }

    static int checkPublic(F3Local.FunctionMember fmem) {
        return checkAccess(fmem, Public.class);
    }

    static int checkProtected(F3Local.FunctionMember fmem) {
        return checkAccess(fmem, Protected.class);
    }

    static int checkPackage(F3Local.FunctionMember fmem) {
        return checkAccess(fmem, Package.class);
    }

    // Return either an F3Type, if resolved, or a Type (which
    // the same as typ, though possibly "simplified".
    static Object resolveGeneric (F3Local.Context context, Type typ) {
        if (typ instanceof ParameterizedType) {
            ParameterizedType ptyp = (ParameterizedType) typ;
            Type raw = ptyp.getRawType();
            Type[] targs = ptyp.getActualTypeArguments();
            if (raw instanceof Class) {
                String rawName = ((Class) raw).getName();
                if (F3ClassType.SEQUENCE_CLASSNAME.equals(rawName) &&
                    targs.length == 1) {
                    return new F3SequenceType(context.makeTypeRef(targs[0]));
                }
                if (F3ClassType.OBJECT_VARIABLE_CLASSNAME.equals(rawName) &&
                        targs.length == 1) {
                    return context.makeTypeRef(targs[0]);
                }
                if (F3ClassType.SEQUENCE_VARIABLE_CLASSNAME.equals(rawName) &&
                    targs.length == 1) {
                    return new F3SequenceType(context.makeTypeRef(targs[0]));
                }
                if (rawName.startsWith(F3ClassType.FUNCTION_CLASSNAME_PREFIX)) {
                    F3Type[] prtypes = new F3Type[targs.length-1];
                    for (int i = prtypes.length;  --i >= 0; )
                        prtypes[i] = context.makeTypeRef(targs[i+1]);
                    F3Type rettype;
                    if (targs[0] == java.lang.Void.class)
                        rettype = F3PrimitiveType.voidType;
                    else
                        rettype = context.makeTypeRef(targs[0]);
                    return new F3FunctionType(prtypes, rettype);
                }
            }

            typ = raw;
        }
        if (typ instanceof WildcardType) {
            WildcardType wtyp = (WildcardType) typ;
            Type[] upper = wtyp.getUpperBounds();
            Type[] lower = wtyp.getLowerBounds();
            typ = lower.length > 0 ? lower[0] : wtyp.getUpperBounds()[0];
            if (typ instanceof Class) {
                String rawName = ((Class) typ).getName();
                // Kludge, because generics don't handle primitive types.
                F3Type ptype = context.getPrimitiveType(rawName);
                if (ptype != null)
                    return ptype;
            }
            return context.makeTypeRef(typ);
        }
        if (typ instanceof GenericArrayType) {
            F3Type elType = context.makeTypeRef(((GenericArrayType) typ).getGenericComponentType());
            return new F3JavaArrayType(elType);
        }
        if (typ instanceof TypeVariable) {
            // KLUDGE
            typ = Object.class;
        }
    return typ;
    }
}
