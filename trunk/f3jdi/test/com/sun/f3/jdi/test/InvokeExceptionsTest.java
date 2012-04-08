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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import org.f3.jdi.F3ReferenceType;
import org.f3.jdi.F3ObjectReference;
import org.f3.jdi.F3VirtualMachine;
import com.sun.jdi.Value;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import org.junit.Test;
import junit.framework.Assert;

public class InvokeExceptionsTest extends F3TestBase {
    ReferenceType targetClass;
    ThreadReference mainThread;

    private static final String targetClassName = "org.f3.jdi.test.target.InvokeExceptionsTarget";

    public InvokeExceptionsTest() {
        super(targetClassName);
    }

    @Test
    public void testVV() {
        try {
            startTests();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }

    void doit(ReferenceType vvRT, ObjectReference vvVal, String fname) {
    }

    /********** test core **********/
    protected void runTests() throws Exception {
        writeActual("stop in = " + "f3.lang.Builtins.println");


        BreakpointEvent bpe = startTo("f3.lang.Builtins", "println", "(Ljava/lang/Object;)V");
        targetClass = bpe.location().declaringType();
        ReferenceType vvRT = vm().classesByName(targetClassName).get(0);
        ObjectReference vvVal = (ObjectReference)vvRT.getValue(vvRT.fieldByName("obj"));
        writeActual("  value of obj = " + vvRT.getValue(vvRT.fieldByName("obj")));

        vm().resume();

        List<Field> allFields = vvRT.allFields();
        List<Field> ivars = new ArrayList<Field>(10);
        try {
            for (Field fld: allFields) {
                if (fld.isStatic()) {
                    writeActual(fld.name() + " = " + 
                                vvRT.getValue(fld) +
                                ", except = " + 
                                ((F3VirtualMachine)vm()).lastFieldAccessException());
                } else {
                    ivars.add(fld);
                    writeActual(fld.name() + " = " + 
                                vvVal.getValue(fld) + 
                                ", except = " + 
                                ((F3VirtualMachine)vm()).lastFieldAccessException());
                }
            }
            
            Map<Field, Value> ivals = vvVal.getValues(ivars);
            writeActual("Except = " + ((F3VirtualMachine)vm()).lastFieldAccessException());
            for (Field fld: ivars) {
                writeActual(fld.name() + " = " +  vvVal.getValue(fld));
            }
            
        } catch(Exception ee) {
            writeActual("Got " + ee);
        }

        testFailed = !didTestPass();
        
        vm().exit(0);
        
        /*
         * deal with results of test
         * if anything has called failure("foo") testFailed will be true
         */
        if (!testFailed) {
            writeActual(testClassName + ": passed");
        } else {
            throw new Exception(testClassName + ": failed");
        }
    }
}
