/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package org.f3.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.f3.runtime.sequence.Sequence;
import org.f3.runtime.sequence.Sequences;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * F3TestCase
 *
 * @author Brian Goetz
 */
public abstract class F3TestCase extends TestCase {
    private static final double EPSILON = 0.0001;
    private final Pattern methodWithQualifiers = Pattern.compile("(.*)\\((.*)\\)");

    /**
     * Helper method for asserting that a sequence contains a specific set of values;
     * test equality of toString(), by iterating the elements, and by toArray
     */
    protected <T> void assertEquals(Sequence<? extends T> sequence, T... values) {
        final int length = sequence.size();
        assertEquals(length, values.length);
        int index = 0;
        for (T t : sequence) {
            assertEquals(t, values[index++]);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            if (i != 0)
                sb.append(",");
            sb.append(" ");
            sb.append(values[i]);
        }
        sb.append(" ]");
        assertEquals(sb.toString(), sequence.toString());

        T[] array = Util.<T>newObjectArray(length);
        sequence.toArray(0, length, array, 0);
        assertEquals(array.length, values.length);
        for (int i = 0; i < array.length; i++)
            assertEquals(array[i], values[i]);
    }

    protected void assertEquals(Sequence<? extends Double> sequence, Double... values) {
        assertEquals(sequence.size(), values.length);
        int index = 0;
        for (Double t : sequence) {
            Double value = values[index++];
            assertTrue(value + " !~ " + t, Math.abs(t - value) < EPSILON);
        }
    }

    protected void assertArrayEquals(Double[] expected, Double ... values) {
        assertEquals(expected.length, values.length);
        int index = 0;
        for (Double t : expected) {
            Double value = values[index++];
            assertTrue(value + " !~ " + t, Math.abs(t - value) < EPSILON);
        }
    }

    protected void assertEquals(Sequence<? extends Float> sequence, Float... values) {
        assertEquals(sequence.size(), values.length);
        int index = 0;
        for (Float f : sequence) {
            Float value = values[index++];
            assertTrue(value + " !~ " + f, Math.abs(f - value) < EPSILON);
        }
    }

    protected void assertArrayEquals(Float[] expected, Float ... values) {
        assertEquals(expected.length, values.length);
        int index = 0;
        for (Float f : expected) {
            Float value = values[index++];
            assertTrue(value + " !~ " + f, Math.abs(f - value) < EPSILON);
        }
    }
    
    protected <T> void assertEquals(Sequence<? extends T> sequence, T value) {
        if (value == null)
            assertEquals(0, sequence.size());
        else {
            assertEquals(1, sequence.size());
            assertEquals(sequence.get(0), value);
        }
    }

    protected <T> void assertEquals(Sequence<? extends T> sequence, Sequence<? extends T> values) {
      assertEquals((Object) sequence, (Object) values);
    }

    protected<T> void assertEquals(Collection<T> collection, T... values) {
        Collection<T> newCollection = new HashSet<T>();
        for (T val : values)
            newCollection.add(val);
        assertEquals(collection, newCollection);
    }

    protected void assertEquals(int[] array, int... values) {
        assertEquals(array.length, values.length);
        for (int i=0; i<array.length; i++)
            assertEquals(array[i], values[i]);
    }

    protected void assertEquals(double[] array, double... values) {
        assertEquals(array.length, values.length);
        for (int i=0; i<array.length; i++)
            assertEquals(array[i], values[i]);
    }

    protected void assertEquals(long[] array, long... values) {
        assertEquals(array.length, values.length);
        for (int i=0; i<array.length; i++)
            assertEquals(array[i], values[i]);
    }

    protected void assertEquals(boolean[] array, boolean... values) {
        assertEquals(array.length, values.length);
        for (int i=0; i<array.length; i++)
            assertEquals(array[i], values[i]);
    }

    protected interface VoidCallable {
        public void call() throws Exception;
    }

    protected static void assertThrows(Class<? extends Exception> exceptionClass, VoidCallable closure) {
        try {
            closure.call();
            fail("Expected exception " + exceptionClass.getCanonicalName());
        }
        catch (Exception e) {
            if (!exceptionClass.isInstance(e))
                throw new RuntimeException("Expecting exception " + exceptionClass.getCanonicalName() + "; found exception " + e.getClass().getCanonicalName(), e);
        }
    }

    /**
     * Assert that invoking the named method reflectively on the specified target with the specified arguments throws UOE.
     * Because we try and guess the the signature from the arguments, and Class.getMethod() is picky, the methodName string
     * can also take the form name(xxx), where each x is a one-character code for the formal type of the corresponding parameter, derived
     * from the classfile format: B=byte, C=char, D=double, F=float, I=int, J=long, L=Object, S=short, Z=boolean, T=Object,
     * or a sequence of the form Lpackage.classname;
     * (for convenience, T is used for type parameters that erase to Object)
     */
    protected void assertException(Class<? extends Throwable> clazz, Object target, String methodName, Object... arguments) {
        Class[] classes = new Class[arguments.length];
        for (int i=0; i<arguments.length; i++)
            classes[i] = arguments[i].getClass();

        Matcher matcher = methodWithQualifiers.matcher(methodName);
        if (matcher.matches()) {
            methodName = matcher.group(1);
            String types = matcher.group(2);
            for (int charIndex=0, paramIndex=0; charIndex<types.length(); charIndex++, paramIndex++) {
                char ch = types.charAt(charIndex);
                switch (ch) {
                    case 'B' : classes[paramIndex] = Byte.TYPE; break;
                    case 'C' : classes[paramIndex] = Character.TYPE; break;
                    case 'D' : classes[paramIndex] = Double.TYPE; break;
                    case 'F' : classes[paramIndex] = Float.TYPE; break;
                    case 'I' : classes[paramIndex] = Integer.TYPE; break;
                    case 'J' : classes[paramIndex] = Long.TYPE; break;
                    case 'L' : {
                        String rest = types.substring(charIndex+1);
                        int pos = rest.indexOf(';');
                        charIndex = pos;
                        String className = rest.substring(0, pos);
                        try {
                            classes[paramIndex] = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            fail("No such class " + className);
                        }
                        break;
                    }
                    case 'T' : classes[paramIndex] = Object.class; break;
                    case 'S' : classes[paramIndex] = Short.TYPE; break;
                    case 'Z' : classes[paramIndex] = Boolean.TYPE; break;
                    default: break;
                }
            }
        }
        
        Method m = null;
        try {
            m = target.getClass().getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            fail("No such method " + methodName);
        }
        try {
            m.invoke(target, arguments);
            fail("Expected exception " + clazz.getName());
        }
        catch (InvocationTargetException e) {
            // Can't do the test reflectively because of class loader issues :(
            if (!e.getCause().getClass().getName().equals(clazz.getName()))
                fail("Expected exception " + clazz.getName() + ", got " + e.getCause().toString());
        }
        catch (Exception e) {
            fail("Unexpected exception in invoke: " + e.toString());
        }
    }

    protected void assertUOE(Object target, String methodName, Object... arguments) {
        assertException(UnsupportedOperationException.class, target, methodName, arguments);
    }
}
