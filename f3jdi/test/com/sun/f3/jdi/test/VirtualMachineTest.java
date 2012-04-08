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

import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteType;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharType;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatType;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongType;
import com.sun.jdi.LongValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Basic sanity checks on com.sun.jdi.VirtualMachine object. See base class for
 * virtual machine connection details.
 *
 * @author sundar
 */
public class VirtualMachineTest extends JDITestBase {
    @Test
    public void testVMNotNull() {
        Assert.assertNotNull(getVM());
    }


    @Test
    public void testAllThreads() {
        List<ThreadReference> threads = getVM().allThreads();
        // atleast one thread!
        if (threads.size() < 1) {
            Assert.fail("not a single thread?!");
        }
    }

    @Test
    public void testTopLevelThreadGroups() {
        List<ThreadGroupReference> threadGroups = getVM().topLevelThreadGroups();
        // atleast one thread group!
        if (threadGroups.size() < 1) {
            Assert.fail("not a single thread group?!");
        }
    }

    @Test
    public void testAllClasses() {
        List<ReferenceType> classes = getVM().allClasses();
        if (classes.size() < 5) {
            Assert.fail("Not even 5 classes loader?!");
        }
    }
    @Test
    public void testObjectType() {
        // look for java.lang.Object type
        testCoreType("java.lang.Object");
    }

    @Test
    public void testClassType() {
        // look for java.lang.Class type
        testCoreType("java.lang.Class");
    }

    @Test
    public void testClassLoaderType() {
        // look for java.lang.ClassLoader type
        testCoreType("java.lang.ClassLoader");
    }

    @Test
    public void testStringType() {
        // look for java.lang.String type
        testCoreType("java.lang.String");
    }

    @Test
    public void testThreadType() {
        // look for java.lang.Thread type
        testCoreType("java.lang.Thread");
    }

    @Test
    public void testThreadGroupType() {
        // look for java.lang.ThreadGroup type
        testCoreType("java.lang.ThreadGroup");
    }

    @Test
    public void testMirrors() {
        VirtualMachine vm = getVM();
        StringReference helloMirror = vm.mirrorOf("hello");
        Assert.assertNotNull(helloMirror);
        Assert.assertEquals("hello", helloMirror.value());

        BooleanValue falseValue = vm.mirrorOf(false);
        Assert.assertNotNull(falseValue);
        Assert.assertEquals(false, falseValue.value());

        BooleanValue trueValue = vm.mirrorOf(true);
        Assert.assertNotNull(trueValue);
        Assert.assertEquals(true, trueValue.value());

        ByteValue byteZero = vm.mirrorOf((byte)0);
        Assert.assertNotNull(byteZero);
        Assert.assertEquals((byte)0, byteZero.value());

        ByteValue byteMax = vm.mirrorOf(Byte.MAX_VALUE);
        Assert.assertNotNull(byteMax);
        Assert.assertEquals(Byte.MAX_VALUE, byteMax.value());

        ByteValue byteMin = vm.mirrorOf(Byte.MIN_VALUE);
        Assert.assertNotNull(byteMin);
        Assert.assertEquals(Byte.MIN_VALUE, byteMin.value());

        ShortValue shortZero = vm.mirrorOf((short)0);
        Assert.assertNotNull(shortZero);
        Assert.assertEquals((short)0, shortZero.value());

        ShortValue shortMax = vm.mirrorOf(Short.MAX_VALUE);
        Assert.assertNotNull(shortMax);
        Assert.assertEquals(Short.MAX_VALUE, shortMax.value());

        ShortValue shortMin = vm.mirrorOf(Short.MIN_VALUE);
        Assert.assertNotNull(shortMin);
        Assert.assertEquals(Short.MIN_VALUE, shortMin.value());

        IntegerValue integerZero = vm.mirrorOf(0);
        Assert.assertNotNull(integerZero);
        Assert.assertEquals(0, integerZero.value());

        IntegerValue integerMax = vm.mirrorOf(Integer.MAX_VALUE);
        Assert.assertNotNull(integerMax);
        Assert.assertEquals(Integer.MAX_VALUE, integerMax.value());

        IntegerValue integerMin = vm.mirrorOf(Integer.MIN_VALUE);
        Assert.assertNotNull(integerMin);
        Assert.assertEquals(Integer.MIN_VALUE, integerMin.value());

        LongValue longZero = vm.mirrorOf(0L);
        Assert.assertNotNull(longZero);
        Assert.assertEquals(0, longZero.value());

        LongValue longMax = vm.mirrorOf(Long.MAX_VALUE);
        Assert.assertNotNull(longMax);
        Assert.assertEquals(Long.MAX_VALUE, longMax.value());

        LongValue longMin = vm.mirrorOf(Long.MIN_VALUE);
        Assert.assertNotNull(longMin);
        Assert.assertEquals(Long.MIN_VALUE, longMin.value());

        FloatValue floatZero = vm.mirrorOf(0.0F);
        Assert.assertNotNull(floatZero);
        Assert.assertEquals(0.0F, floatZero.value());

        FloatValue floatMax = vm.mirrorOf(Float.MAX_VALUE);
        Assert.assertNotNull(floatMax);
        Assert.assertEquals(Float.MAX_VALUE, floatMax.value());

        FloatValue floatMin = vm.mirrorOf(Float.MIN_VALUE);
        Assert.assertNotNull(floatMin);
        Assert.assertEquals(Float.MIN_VALUE, floatMin.value());

        DoubleValue doubleZero = vm.mirrorOf(0.0D);
        Assert.assertNotNull(doubleZero);
        Assert.assertEquals(0.0D, doubleZero.value());

        DoubleValue doubleMax = vm.mirrorOf(Double.MAX_VALUE);
        Assert.assertNotNull(doubleMax);
        Assert.assertEquals(Double.MAX_VALUE, doubleMax.value());

        DoubleValue doubleMin = vm.mirrorOf(Double.MIN_VALUE);
        Assert.assertNotNull(doubleMin);
        Assert.assertEquals(Double.MIN_VALUE, doubleMin.value());

        CharValue charValue = vm.mirrorOf('J');
        Assert.assertNotNull(charValue);
        Assert.assertEquals('J', charValue.value());
    }

    @Test
    public void testMirrorOfVoidValue() {
        VoidValue vv = getVM().mirrorOfVoid();
        Assert.assertNotNull(vv);
    }

    @Test
    public void testPrimitiveTypes() {
        VirtualMachine vm = getVM();
        Type booleanType = vm.mirrorOf(false).type();
        Assert.assertEquals(true, booleanType instanceof BooleanType);
        Assert.assertEquals("boolean", booleanType.name());
        Assert.assertEquals("Z", booleanType.signature());

        Type charType = vm.mirrorOf('J').type();
        Assert.assertEquals(true, charType instanceof CharType);
        Assert.assertEquals("char", charType.name());
        Assert.assertEquals("C", charType.signature());

        Type byteType = vm.mirrorOf((byte)0).type();
        Assert.assertEquals(true, byteType instanceof ByteType);
        Assert.assertEquals("byte", byteType.name());
        Assert.assertEquals("B", byteType.signature());

        Type shortType = vm.mirrorOf((short)0).type();
        Assert.assertEquals(true, shortType instanceof ShortType);
        Assert.assertEquals("short", shortType.name());
        Assert.assertEquals("S", shortType.signature());

        Type integerType = vm.mirrorOf(0).type();
        Assert.assertEquals(true, integerType instanceof IntegerType);
        Assert.assertEquals("int", integerType.name());
        Assert.assertEquals("I", integerType.signature());

        Type longType = vm.mirrorOf(0L).type();
        Assert.assertEquals(true, longType instanceof LongType);
        Assert.assertEquals("long", longType.name());
        Assert.assertEquals("J", longType.signature());

        Type floatType = vm.mirrorOf(0.0F).type();
        Assert.assertEquals(true, floatType instanceof FloatType);
        Assert.assertEquals("float", floatType.name());
        Assert.assertEquals("F", floatType.signature());

        Type doubleType = vm.mirrorOf(0.0D).type();
        Assert.assertEquals(true, doubleType instanceof DoubleType);
        Assert.assertEquals("double", doubleType.name());
        Assert.assertEquals("D", doubleType.signature());

        ReferenceType stringType = vm.mirrorOf("JDI").referenceType();
        Assert.assertNotNull(stringType);
        Assert.assertEquals("java.lang.String", stringType.name());
        Assert.assertEquals("Ljava/lang/String;", stringType.signature());
    }

    @Test
    public void testEventQueueNotNull() {
        EventQueue evtQ = getVM().eventQueue();
        if (evtQ == null) {
            Assert.fail("EventQueue is null!");
        }
    }

    @Test
    public void testEventRequestManagerNotNull() {
        EventRequestManager evtReqMan = getVM().eventRequestManager();
        if (evtReqMan == null) {
            Assert.fail("EventRequestManager is null!");
        }
    }

    // internals only below this point
    private void testCoreType(String name) {
        List<ReferenceType> refTypes = getVM().classesByName(name);
        if (refTypes.isEmpty()) {
            Assert.fail("Core type " + name + " not found!");
        }
        if (refTypes.size() != 1) {
            Assert.fail(name + " is not unique!");
        }
        ReferenceType refType = refTypes.get(0);
        Assert.assertEquals(true, refType instanceof ClassType);
        Assert.assertEquals(name, refType.name());
    }
}
