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

package org.f3.tools.script;

import f3.util.F3Evaluator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tball
 */
public class EvalTest {
    @Test
    public void basicEval() throws Exception {
        F3Evaluator.eval("1 + 1");
    }

    @Test
    public void basicEvalReturn() throws Exception {
        Object n = F3Evaluator.eval("1 + 1");
        assertTrue((Integer)n == 2);
    }
    
    @Test
    public void evalSumExpression() throws Exception {
        Object n = F3Evaluator.eval("{ var n; for (i in [1..5]) n += i; n }");
        assertTrue((Double)n == 15.0);
    }
    
    @Test
    public void evalEval() throws Exception {
        Object n = F3Evaluator.eval("f3.util.F3Evaluator.eval(\"1 + 1\")");
        assertTrue((Integer)n == 2);        
    }
}
