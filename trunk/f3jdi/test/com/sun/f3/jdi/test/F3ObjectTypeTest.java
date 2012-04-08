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

import org.f3.jdi.F3ObjectType;
import org.f3.jdi.F3VirtualMachine;
import org.f3.jdi.F3ReferenceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import org.junit.Test;
import junit.framework.Assert;

/**
 * Basic sanity check for F3ObjectType (which wraps org.f3.runtime.F3Object)
 *
 * @author sundar
 */
public class F3ObjectTypeTest extends F3TestBase {
    // any F3 class will do..
    private static String targetClassName = "org.f3.jdi.test.target.HelloTarget";

    public F3ObjectTypeTest() {
        super(targetClassName);
    }

    @Test
    public void testF3ObjectType() {
        try {
            startTests();
        } catch (Exception exp) {
            Assert.fail(exp.getMessage());
        }
    }

    protected void runTests() throws Exception {
        startToMain();
        // run till f3$run$ - so that org.f3.runtime.F3Object is loaded!
        resumeTo(targetClassName, f3RunMethodName(), f3RunMethodSignature());

        // look for F3Object type
        ReferenceType rt = vm().classesByName(F3VirtualMachine.F3_OBJECT_TYPE_NAME).get(0);
        // it has to be F3ObjectType
        Assert.assertEquals(true, rt instanceof F3ObjectType);
        // check few methods of F3ObjectType
        // We are checking for internal methods that are filtered out by F3ReferenceType, so
        // we have to use the underlying JDI ReferenceType
        F3ObjectType f3ObjType = (F3ObjectType)rt;
        Method count$Method = f3ObjType.count$Method();
        Assert.assertEquals("count$", count$Method.name());
        Assert.assertEquals("()I", count$Method.signature());
        Method get$Method = f3ObjType.get$Method();
        Assert.assertEquals("get$", get$Method.name());
        Assert.assertEquals("(I)Ljava/lang/Object;", get$Method.signature());
        Method set$Method = f3ObjType.set$Method();
        Assert.assertEquals("set$", set$Method.name());
        Assert.assertEquals("(ILjava/lang/Object;)V", set$Method.signature());

        /*
         * resume until end
         */
        listenUntilVMDisconnect();
    }
}
