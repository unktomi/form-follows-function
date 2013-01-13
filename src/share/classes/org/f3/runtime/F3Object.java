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

package org.f3.runtime;

// CODING/NAMING RESTRICTIONS - see F3Base for explanation.


/**
 * All F3 classes must extend F3Object; it acts as a marker interface, and also includes methods required for
 * object lifecyle.
 *
 * @author Brian Goetz
 * @author Jim Laskey
 * @author Robert Field
 */
public interface F3Object {
    /**
     * Var flag bits.
     */
    public static final int VFLGS$RESTING_STATE_BIT     = 0x0001;
    public static final int VFLGS$BE_STATE_BIT          = 0x0002;
    public static final int VFLGS$INVALID_STATE_BIT     = 0x0004;
    public static final int VFLGS$DEFAULT_STATE_BIT     = 0x0008;
    public static final int VFLGS$INITIALIZED_STATE_BIT = 0x0010;
    public static final int VFLGS$AWAIT_VARINIT_BIT     = 0x0020;
    public static final int VFLGS$IS_EAGER              = 0x0040;
    public static final int VFLGS$SEQUENCE_LIVE         = 0x0080;
    public static final int VFLGS$IS_BOUND              = 0x0100;
    public static final int VFLGS$IS_READONLY           = 0x0200;
    public static final int VFLGS$FORWARD_ACCESS        = 0x0400;

    /**
     * Var validation states
     */
    public static final int VFLGS$STATE_MASK = VFLGS$RESTING_STATE_BIT | VFLGS$BE_STATE_BIT | VFLGS$INVALID_STATE_BIT;

    public static final int VFLGS$STATE$VALID            = VFLGS$RESTING_STATE_BIT;
    public static final int VFLGS$STATE$CASCADE_INVALID  = VFLGS$INVALID_STATE_BIT;
    public static final int VFLGS$STATE$BE_INVALID       = VFLGS$BE_STATE_BIT | VFLGS$INVALID_STATE_BIT;
    public static final int VFLGS$STATE$TRIGGERED        = VFLGS$RESTING_STATE_BIT | VFLGS$INVALID_STATE_BIT;

    /**
     * Var initialization states
     */
    public static final int VFLGS$INIT$MASK = VFLGS$INITIALIZED_STATE_BIT | VFLGS$DEFAULT_STATE_BIT;
    public static final int VFLGS$INIT_WITH_AWAIT$MASK = VFLGS$INIT$MASK | VFLGS$AWAIT_VARINIT_BIT;

    public static final int VFLGS$INIT$PENDING             = 0;
    public static final int VFLGS$INIT$AWAIT_VARINIT       = VFLGS$AWAIT_VARINIT_BIT;
    public static final int VFLGS$INIT$READY               = VFLGS$DEFAULT_STATE_BIT;
    public static final int VFLGS$INIT$INITIALIZED         = VFLGS$INITIALIZED_STATE_BIT;
    public static final int VFLGS$INIT$INITIALIZED_DEFAULT = VFLGS$INITIALIZED_STATE_BIT | VFLGS$DEFAULT_STATE_BIT;

    public static final int VFLGS$INIT$BOUND_MASK          = VFLGS$IS_BOUND | VFLGS$INIT$MASK;
    public static final int VFLGS$INIT$STATE$MASK          = VFLGS$STATE_MASK | VFLGS$INIT$MASK;

    /**
     * Var flag groups.
     */
    public static final int VFLGS$IS_BOUND_READONLY                            = VFLGS$IS_BOUND | VFLGS$IS_READONLY;
    public static final int VFLGS$IS_BOUND_INVALID                             = VFLGS$IS_BOUND | VFLGS$INVALID_STATE_BIT;
    public static final int VFLGS$VALID_DEFAULT_APPLIED                        = VFLGS$INIT$INITIALIZED_DEFAULT | VFLGS$STATE$VALID;
    public static final int VFLGS$INIT$INITIALIZED_DEFAULT_READONLY            = VFLGS$INIT$INITIALIZED_DEFAULT | VFLGS$IS_READONLY;
    public static final int VFLGS$INIT_OBJ_LIT                                 = VFLGS$INIT$READY;
    public static final int VFLGS$INIT_OBJ_LIT_SEQUENCE                        = VFLGS$INIT$READY | VFLGS$SEQUENCE_LIVE;

    public static final int VFLGS$ALL_FLAGS = -1;

    /**
     * Phase transitions
     * Acceptable current states / Next state / Phase
     * Note: sequences use cascade triggerring
     */
    public static final int PHASE_TRANS$PHASE_SHIFT = 3;
    public static final int PHASE_TRANS$NEXT_STATE_SHIFT = 1 + PHASE_TRANS$PHASE_SHIFT;
    public static final int PHASE_TRANS$PHASE  = 1 << PHASE_TRANS$PHASE_SHIFT;

    public static final int PHASE$INVALIDATE  = 0;
    public static final int PHASE$TRIGGER     = PHASE_TRANS$PHASE;

    public static final int PHASE_TRANS$CASCADE_INVALIDATE  = (VFLGS$STATE$VALID)
                                                            | (VFLGS$STATE$CASCADE_INVALID << PHASE_TRANS$NEXT_STATE_SHIFT)
                                                            | PHASE$INVALIDATE;

    public static final int PHASE_TRANS$BE_INVALIDATE       = (VFLGS$STATE$VALID)
                                                            | (VFLGS$STATE$BE_INVALID      << PHASE_TRANS$NEXT_STATE_SHIFT)
                                                            | PHASE$INVALIDATE;

    public static final int PHASE_TRANS$CASCADE_TRIGGER     = (VFLGS$STATE$CASCADE_INVALID)
                                                            | (VFLGS$STATE$TRIGGERED       << PHASE_TRANS$NEXT_STATE_SHIFT)
                                                            | PHASE$TRIGGER;

    public static final int PHASE_TRANS$BE_TRIGGER          = (VFLGS$STATE$CASCADE_INVALID | VFLGS$STATE$BE_INVALID)
                                                            | (VFLGS$STATE$TRIGGERED       << PHASE_TRANS$NEXT_STATE_SHIFT)
                                                            | PHASE$TRIGGER;

    public static final int PHASE_TRANS$CLEAR_BE            = ~((VFLGS$BE_STATE_BIT)
                                                            | (VFLGS$BE_STATE_BIT          << PHASE_TRANS$NEXT_STATE_SHIFT));
    public boolean isInitialized$internal$();
    public void setInitialized$internal$(boolean initialized);

    public int getFlags$(final int varNum);
    public void setFlags$(final int varNum, final int value);
    public boolean varTestBits$(final int varNum, final int maskBits, final int testBits);
    public int varChangeBits$(final int varNum, final int clearBits, final int setBits);
    public void restrictSet$(final int varNum);

    // dependents management
    public WeakBinderRef getThisRef$internal$();
    public void setThisRef$internal$(WeakBinderRef bref);
    public DepChain getDepChain$internal$();
    public void setDepChain$internal$(DepChain depChain);
    
    public void     addDependent$        (final int varNum, F3Object dep, final int depNum);
    public void     removeDependent$     (final int varNum, F3Object dep);
    // Earlier 'this' object was dependent on { oldBindee, varNum }.
    // Now, change the dependence to { newBindee, varNum }
    public void     switchDependence$    (F3Object oldBindee, final int oldNum, F3Object newBindee, final int newNum, final int depNum);
    public void     notifyDependents$    (final int varNum, final int phase);
    public void     notifyDependents$    (int varNum, int startPos, int endPos, int newLength, int phase);
    public boolean  update$ (F3Object src, int depNum, int startPos, int endPos, int newLength, int phase);
// for testing - the listener count is the number of distinct {varNum, dep} pairs
    public int      getListenerCount$();

    // instance variable access by varNum
    public Object   get$(int varNum);
    public void     set$(int varNum, Object value);
    // type of a particular instance variable
    public Class    getType$(int varNum);
    public void     seq$(int varNum, Object value);
    public void     invalidate$(int varNum, int startPos, int endPos, int newLength, int phase);

    public void     initialize$   (boolean applyDefaults);
    public void     initVars$     ();
    public void     applyDefaults$();
    public void     applyDefaults$(final int varNum);
    public void     applyDefaultsDebug$();
    public void     applyDefaultsDebug$(final int varNum);
    public void     userInit$     ();
    public void     postInit$     ();
    public void     complete$     ();
    public int      count$        ();

    public int size$(int varNum);
    public Object elem$(int varNum, int position);
    public boolean getAsBoolean$(int varNum, int position);
    public char getAsChar$(int varNum, int position);
    public byte getAsByte$(int varNum, int position);
    public short getAsShort$(int varNum, int position);
    public int getAsInt$(int varNum, int position);
    public long getAsLong$(int varNum, int position);
    public float getAsFloat$(int varNum, int position);
    public double getAsDouble$(int varNum, int position);

    /* Used to invoke function value handled by this object.
     * @param number The index of function that is being invoked.
     * If we have 0 arguments, then arg1, arg2 and rargs are null.
     * If we have 1 arguments, then it is passed in arg1, while arg2 and rargs are null.
     * If we have 2 arguments, they are passed in arg1 and arg2, while rargs is null.
     * If we have more than 2 arguments, the first 2 arg passed in arg1 and arg2
     * while the rest are passed in rargs.
     */
    public Object invoke$(int number, Object arg1, Object arg2, Object[] rargs);
}
