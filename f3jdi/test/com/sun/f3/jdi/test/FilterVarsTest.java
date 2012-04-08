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

/*
 * This is a basic test to show that internal instance vars are properly filtered out
 * and that the internal naming scheme for user instance vars is filtered out.
 */

package org.f3.jdi.test;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import java.util.List;
import org.junit.Test;
import junit.framework.Assert;

public class FilterVarsTest extends F3TestBase {
    ReferenceType targetClass;
    ThreadReference mainThread;

    private static final String targetClassName = "org.f3.jdi.test.target.FilterVarsTarget";

    public FilterVarsTest() {
        super(targetClassName);
    }

    @Test
    public void testFilterVars() {
        try {
            startTests();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }

    /********** test core **********/

    protected void runTests() throws Exception {
        BreakpointEvent bpe = startTo(targetClassName + "$FilterVarsTargetSub", "stopHere", "()V");
        targetClass = bpe.location().declaringType();
        writeActual("------ fields for class " + targetClass.name());
        writeActual("------ all fields ------");
        List<Field> allFields = targetClass.allFields();
        for (Field ii: allFields) {
            writeActual("field = " + ii.toString());
        }

        writeActual("\n----- exclude inherited fields ------");
        List<Field> someFields = targetClass.fields();
        for (Field ii: someFields) {
            writeActual("field = " + ii.toString());
        }
       
       writeActual("\n----- visible fields ------");
       List<Field> visibleFields = targetClass.visibleFields();
       for (Field ii: visibleFields) {
           writeActual("field = " + ii.toString());
       }
       
       /*
         - mixin fields are named $MixinClassName$fieldName
         - a private script field is java private, and is named with its normal name 
           UNLESS it is referenced in a subclass. In this case it is java public and
           its name is $ClassName$fieldName.  
        This mangling in of the classname is not yet handled.
       */
       writeActual("\n----- fields by name ------");
       writeActual("field = " + targetClass.fieldByName("baseivar1"));     // fails and returns null
       writeActual("field = " + targetClass.fieldByName("FilterVarsTarget$baseivar1"));
       writeActual("field = " + targetClass.fieldByName("ivarOverride"));  // fails and returns null
       writeActual("field = " + targetClass.fieldByName("subivar1"));
       writeActual("field = " + targetClass.fieldByName("obj"));

       testFailed = !didTestPass();
        
       /*
        * resume until end
        */
       listenUntilVMDisconnect();

       /*
        * deal with results of test
        * if anything has called failure("foo") testFailed will be true
        */
       if (testFailed) {
           throw new Exception(testClassName + ": failed");
       }
    }
}
