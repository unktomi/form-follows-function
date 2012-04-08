/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi.test;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Method;
import com.sun.jdi.Type;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Basic sanity checks with core Java types such as Object, String and Class.
 *
 * @author sundar
 */
public class ClassTypeTest extends JDITestBase {
    @Test
    public void testObjectMethods() {
        ClassType objectType = getCoreClassType("java.lang.Object");
        ClassType stringType = getCoreClassType("java.lang.String");
        ClassType classType = getCoreClassType("java.lang.Class");
        Type integerType = getVM().mirrorOf(0).type();
        Type booleanType = getVM().mirrorOf(false).type();

        // test few methods of java.lang.Object
        try {
            List<Method> methodList;
            methodList = objectType.methodsByName("toString");
            Assert.assertEquals(1, methodList.size());
            Method toString = methodList.get(0);
        
            Assert.assertEquals(true, toString.argumentTypes().isEmpty());
            Assert.assertEquals(stringType, toString.returnType());
        
            methodList = objectType.methodsByName("hashCode");
            Assert.assertEquals(1, methodList.size());
            Method hashCode = methodList.get(0);
        
            Assert.assertEquals(true, hashCode.argumentTypeNames().isEmpty());
            Assert.assertEquals(integerType, hashCode.returnType());

            methodList = objectType.methodsByName("equals");
            Assert.assertEquals(1, methodList.size());
            Method equals = methodList.get(0);
            Assert.assertEquals(1, equals.argumentTypeNames().size());
            Assert.assertEquals(objectType, equals.argumentTypes().get(0));
            Assert.assertEquals(booleanType, equals.returnType());

            methodList = objectType.methodsByName("getClass");
            Assert.assertEquals(1, methodList.size());
            Method getClass = methodList.get(0);
            Assert.assertEquals(0, getClass.argumentTypes().size());
            Assert.assertEquals(classType, getClass.returnType());
        } catch (ClassNotLoadedException cnle) {
            Assert.fail(cnle.getMessage());
        }   
    }

    protected ClassType getCoreClassType(String name) {
        return (ClassType) getVM().classesByName(name).get(0);
    }
}
