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

import org.f3.jdi.F3SequenceReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.event.BreakpointEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import junit.framework.Assert;


/**
 * Basic checks for F3SequenceReference/F3SequenceType methods and sequence access
 * from debugger.
 *
 * @author sundar
 */
public class SequenceTest extends F3TestBase {
    private static String targetClassName = "org.f3.jdi.test.target.SequenceTarget";

    public SequenceTest() {
        super(targetClassName);
    }

    @Test
    public void testSequence() {
        try {
            startTests();
        } catch (Exception exp) {
            exp.printStackTrace();
            Assert.fail(exp.getMessage());
        }
    }

    protected void runTests() throws Exception {
        startToMain();

        // break into function printSeq(arg: Integer[])
        BreakpointEvent bpe = resumeTo(targetClassName, "printSeq",
                "(Lorg/f3/runtime/sequence/Sequence;)V");

        mainThread = bpe.thread();

        // get the top frame
        StackFrame frame = mainThread.frame(0);
        // get first argument which is Integer[]
        Value value = frame.getArgumentValues().get(0);
        Assert.assertEquals(true, value instanceof F3SequenceReference);
        F3SequenceReference seq = (F3SequenceReference) value;
        Assert.assertEquals(2, seq.size());
        Assert.assertEquals(2, seq.length());
        Assert.assertEquals(F3SequenceReference.Types.INT, seq.getElementType());
        Value zerothElementAsVal = seq.getValue(0);
        Assert.assertEquals(true, zerothElementAsVal instanceof IntegerValue);
        Assert.assertEquals(1729, ((IntegerValue)zerothElementAsVal).intValue());
        Value firstElementAsVal = seq.getValue(1);
        Assert.assertEquals(true, firstElementAsVal instanceof IntegerValue);
        Assert.assertEquals(9999, ((IntegerValue)firstElementAsVal).intValue());

        // sequence element set
        seq.setValue(0, vm().mirrorOf(1111));
        seq.setValue(1, vm().mirrorOf(2222));
        Assert.assertEquals(1111, ((IntegerValue)seq.getValue(0)).intValue());
        Assert.assertEquals(2222, ((IntegerValue)seq.getValue(1)).intValue());

        // sequence setValues 
        List<Value> newValues = new ArrayList<Value>(2);
        newValues.add(vm().mirrorOf(1234));
        newValues.add(vm().mirrorOf(5678));
        seq.setValues(newValues);
        Assert.assertEquals(1234, ((IntegerValue)seq.getValue(0)).intValue());
        Assert.assertEquals(5678, ((IntegerValue)seq.getValue(1)).intValue());

        // sequence getValues
        List<Value> values = seq.getValues(0, 2);
        Assert.assertEquals(1234, ((IntegerValue)values.get(0)).intValue());
        Assert.assertEquals(5678, ((IntegerValue)values.get(1)).intValue());


        /*
         * resume until end
         */
        listenUntilVMDisconnect();
    }
}
