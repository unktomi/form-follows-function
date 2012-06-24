/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

import org.f3.runtime.sequence.Sequence;
import org.f3.runtime.sequence.Sequences;

//

// CODING/NAMING RESTRICTIONS - In a perfect world, all F3 classes would inherit
// from F3Base.  However, this is not the case.  It's also possible to inherit
// from pure java classes. To accommodate this requirement, F3Base and F3Object
// are under some strict coding conventions.
//
// When an F3 class inherits from a java class, then all instance fields from
// F3Base are cloned into the F3 class, and accessor functions constructed for
// them.  Therefore;
//
//   - All non-static fields defined in F3Base should have a '$' in the name.
//     That '$' must not be the first character, to avoid conflict with user
//     vars.
//   - All non-static fields must have accessor methods defined in F3Base.
//     The names of the accessors must be in the form 'get' + fieldName and
//     'set' + fieldName.
//   - The accessor method declarations should be added to F3Object, so that
//     java inheriting classes can define their own interface implementations.
//
//  Ex.
//
//    In F3Base we define;
//
//       MyClass myField$;
//
//       public MyClass getmyField$() {
//           return myField$;
//       }
//
//       public void setmyField$(final MyClass value) {
//           myField$ = value;
//       }
//
//     In F3Object we declare;
//
//       public MyClass getmyField$();
//       public void setmyField$(final MyClass value);
//
// When an F3 class inherits from a java class, all non-static methods are
// cloned into the F3 class, with bodies that call the F3Base static version of
// method, inserting 'this' as the first argument.  Therefore;
//
//   - All functionality in F3Base should be defined in static methods,
//     manipulating an F3Object.  The declaration of the method should have an
//     an F3Object first argument.  '$' naming conventions apply.
//   - A non-static method should be defined to relay 'this' and remaining
//     arguments thru to the static methods.
//   - The declaration of the non-static method should be added to F3Object.
//
//  Ex.
//
//    In F3Base we define;
//
//       public int addIt$(int n) {
//           return addIt$(this, n);
//       }
//
//       public static int addIt$(F3Object obj, int n) {
//           return obj.count$() + n;
//       }
//
//     In F3Object we declare;
//
//       public int addIt$(int n);
//
//

/**
 * Base class for most F3 classes.  The exception being classes that inherit from Java classes.
 *
 * @author Jim Laskey
 * @author Robert Field
 */
 public class F3Base implements F3Object {
    public boolean initialized$internal$ = false;
    public boolean isInitialized$internal$() {
        return initialized$internal$;
    }
    public void setInitialized$internal$(boolean initialized) {
        this.initialized$internal$ = initialized;
    }
    
    // First class count.
    public int count$() { return 0; }
    public static int count$(F3Object obj) { return 0; }
    
    private static final int VCNT$ = 0;
    public static int VCNT$() { return 0; }

    private static final int DCNT$ = 0;
    public static int DCNT$() { return 0; }

    private static final int FCNT$ = 0;
    public static int FCNT$() { return 0; }

    public int getFlags$(final int varNum) {
        return varChangeBits$(varNum, 0, 0);
    }
    public static int getFlags$(F3Object obj, final int varNum) {
        return obj.varChangeBits$(varNum, 0, 0);
    }
    
    public void setFlags$(final int varNum, final int value) {
        varChangeBits$(varNum, VFLGS$ALL_FLAGS, value);
    }
    public static void setFlags$(F3Object obj, final int varNum, final int value) {
        obj.varChangeBits$(varNum, VFLGS$ALL_FLAGS, value);
    }
    
    public boolean varTestBits$(final int varNum, final int maskBits, final int testBits) {
        return (varChangeBits$(varNum, 0, 0) & maskBits) == testBits;
    }
    public static boolean varTestBits$(F3Object obj, final int varNum, final int maskBits, final int testBits) {
        return (obj.varChangeBits$(varNum, 0, 0) & maskBits) == testBits;
    }

    public int varChangeBits$(final int varNum, final int clearBits, final int setBits) {
        return 0;
    }
    public static int varChangeBits$(F3Object obj, final int varNum, final int clearBits, final int setBits) {
        return 0;
    }

    public void restrictSet$(final int flags) {
        if ((flags & VFLGS$IS_READONLY) == VFLGS$IS_READONLY) {
            if ((flags & VFLGS$IS_BOUND) == VFLGS$IS_BOUND) {
                throw new AssignToBoundException("Cannot assign to bound variable");
            } else {
                throw new AssignToDefException("Cannot assign to a variable defined with 'def'");
            }
        }
    }
    public static void restrictSet$(F3Object obj, final int flags) {
        if ((flags & VFLGS$IS_READONLY) == VFLGS$IS_READONLY) {
            if ((flags & VFLGS$IS_BOUND) == VFLGS$IS_BOUND) {
                throw new AssignToBoundException("Cannot assign to bound variable");
            } else {
                throw new AssignToDefException("Cannot assign to a variable defined with 'def'");
            }
        }
    }

    // dependents management
    public WeakBinderRef thisRef$internal$;
    public DepChain depChain$internal$;

    public WeakBinderRef getThisRef$internal$() {
       return thisRef$internal$;
    }

    public void setThisRef$internal$(WeakBinderRef bref) {
       thisRef$internal$ = bref;
    }

   public DepChain getDepChain$internal$() {
        return depChain$internal$;
    }

    public void setDepChain$internal$(DepChain depChain) {
        this.depChain$internal$ = depChain;
    }

    public void addDependent$(final int varNum, F3Object dep, final int depNum) {
        addDependent$(this, varNum, dep, depNum);
    }
    public static void addDependent$(F3Object obj, final int varNum, F3Object dep, final int depNum) {
        assert varNum > -1 && varNum < obj.count$() : "invalid varNum: " + varNum;
        DependentsManager.addDependent(obj, varNum, dep, depNum);
    }
    public void removeDependent$(final int varNum, F3Object dep) {
        removeDependent$(this, varNum, dep);
    }
    public static void removeDependent$(F3Object obj, final int varNum, F3Object dep) {
        assert varNum > -1 && varNum < obj.count$() : "invalid varNum: " + varNum;
        DependentsManager.removeDependent(obj, varNum, dep);
    }
    public void switchDependence$(F3Object oldBindee, final int oldNum, F3Object newBindee, final int newNum, final int depNum) {
        switchDependence$(this, oldBindee, oldNum, newBindee, newNum, depNum);
    }
    public static void switchDependence$(F3Object obj, F3Object oldBindee, final int oldNum, F3Object newBindee, final int newNum, final int depNum) {
        if (oldBindee != newBindee) {
            DependentsManager.switchDependence(obj, oldBindee, oldNum, newBindee, newNum, depNum);
        }
    }
    public void notifyDependents$(final int varNum, final int phase) {
        notifyDependents$(this, varNum, phase);
    }
    public static void notifyDependents$(F3Object obj, final int varNum, final int phase) {
        assert varNum > -1 && varNum < obj.count$() : "invalid varNum: " + varNum;
        DependentsManager.notifyDependents(obj, varNum, 0, Sequences.UNDEFINED_MARKER_INT, Sequences.UNDEFINED_MARKER_INT, phase);
    }
    public void notifyDependents$(int varNum, int startPos, int endPos, int newLength, int phase) {
        notifyDependents$(this, varNum, startPos, endPos, newLength, phase);
    }
    public static void notifyDependents$(F3Object obj, final int varNum, int startPos, int endPos, int newLength, final int phase) {
        assert varNum > -1 && varNum < obj.count$() : "invalid varNum: " + varNum;
        DependentsManager.notifyDependents(obj, varNum, startPos, endPos, newLength, phase);
    }
    public boolean update$(F3Object src, final int depNum, int startPos, int endPos, int newLength, final int phase) { return false; }
    public static boolean update$(F3Object obj, F3Object src, final int depNum, int startPos, int endPos, int newLength, final int phase) { return false; }
    public int getListenerCount$() {
        return DependentsManager.getListenerCount(this);
    }
    public static int getListenerCount$(F3Object src) {
        return DependentsManager.getListenerCount(src);
    }

    public Object get$(int varNum) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public static Object get$(F3Object obj, int varNum) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public void set$(int varNum, Object value) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public static void set$(F3Object obj, int varNum, Object value) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public Class getType$(int varNum) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public static Class getType$(F3Object obj, int varNum) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public void seq$(int varNum, Object value) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public static void seq$(F3Object obj, int varNum, Object value) {
        throw new IllegalArgumentException("no such variable: " + varNum);
    }
    public void invalidate$(int varNum, int startPos, int endPos, int newLength, int phase) {
        // VSGC-3964 - Var invalidate may be optimized away.
    }
    public static void invalidate$(F3Object obj, int varNum, int startPos, int endPos, int newLength, int phase) {
        // VSGC-3964 - Var invalidate may be optimized away.
    }

    /**
     * Constructor called from Java or from object literal with no instance variable initializers
     */
    public F3Base() {
        this(false);
        initialize$(true);
    }

    /**
     * Constructor called for a (non-empty) F3 object literal.
     * @param dummy Marker only. Ignored.
     */
    public F3Base(boolean dummy) {
        // Make sure offsets are set.
        count$();
    }

    public void initialize$(boolean applyDefaults) {
        initVars$();
        if (applyDefaults) applyDefaults$();
        complete$();
    }
    public static void initialize$(F3Object obj, boolean applyDefaults) {
        obj.initVars$();
        if (applyDefaults) obj.applyDefaults$();
        obj.complete$();
    }

    public void complete$() {
        userInit$();
        setInitialized$internal$(true);
        postInit$();
    }
    public static void complete$(F3Object obj) {
        obj.userInit$();
        obj.setInitialized$internal$(true);
        obj.postInit$();
    }

    public void initVars$() {}
    public static void initVars$(F3Object obj) {}

    public void applyDefaults$(final int varNum) {}
    public static void applyDefaults$(F3Object obj, final int varNum) {}

    public void applyDefaults$() {
        int cnt = count$();
        for (int inx = 0; inx < cnt; inx += 1) {
            varChangeBits$(inx, 0, VFLGS$INIT$READY);
            applyDefaults$(inx);
        }
    }
    public static void applyDefaults$(F3Object obj) {
        int cnt = obj.count$();
        for (int inx = 0; inx < cnt; inx += 1) {
            obj.varChangeBits$(inx, 0, VFLGS$INIT$READY);
            obj.applyDefaults$(inx);
        }
    }

    public        void userInit$()             {}
    public static void userInit$(F3Object obj) {}
    public        void postInit$()             {}
    public static void postInit$(F3Object obj) {}

    //
    // makeInitMap$ constructs a field mapping table used in the switch portion
    // of a object literal initialization.  Each entry in the table represents
    // a field in a class.  The value in the slot is zero (no case) or the
    // switch case tag that has a value setting.
    //
    public static short [] makeInitMap$(int count, int... offsets) {
        final short [] map = new short[count];
	System.err.println("init map: "+ count+": ");
        for (int i = 0; i < offsets.length; i++) {
	    System.err.println("offset: "+ i+": "+offsets[i]);
            //map[offsets[i]] = (short)(i + 1);
            map[offsets[i]] = (short)i;
        }
        return map;
    }

    public int size$(int varNum) {
	Sequence<?> seq = (Sequence<?>)get$(varNum);
	return seq == null ? 0 : seq.size();
        //return ((Sequence<?>) get$(varNum)).size();
    }
    public static int size$(F3Object obj, int varNum) {
        return ((Sequence<?>) obj.get$(varNum)).size();
    }

    public Object elem$(int varNum, int position) {
        return ((Sequence<?>) get$(varNum)).get(position);
    }
    public static Object elem$(F3Object obj, int varNum, int position) {
        return ((Sequence<?>) obj.get$(varNum)).get(position);
    }

    public boolean getAsBoolean$(int varNum, int position) {
        return getAsBoolean$(this, varNum, position);
    }
    public static boolean getAsBoolean$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToBoolean(obj.elem$(varNum, position)) :
            false;
    }

    public char getAsChar$(int varNum, int position) {
        return getAsChar$(this, varNum, position);
    }
    public static char getAsChar$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToChar(obj.elem$(varNum, position)) :
            '\0';
    }

    public byte getAsByte$(int varNum, int position) {
        return getAsByte$(this, varNum, position);
    }
    public static byte getAsByte$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToByte(obj.elem$(varNum, position)) :
            0;
    }

    public short getAsShort$(int varNum, int position) {
        return getAsShort$(this, varNum, position);
    }
    public static short getAsShort$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToShort(obj.elem$(varNum, position)) :
            0;
    }

    public int getAsInt$(int varNum, int position) {
        return getAsInt$(this, varNum, position);
    }
    public static int getAsInt$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToInt(obj.elem$(varNum, position)) :
            0;
    }

    public long getAsLong$(int varNum, int position) {
        return getAsLong$(this, varNum, position);
    }
    public static long getAsLong$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToLong(obj.elem$(varNum, position)) :
            0L;
    }

    public float getAsFloat$(int varNum, int position) {
        return getAsFloat$(this, varNum, position);
    }
    public static float getAsFloat$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToFloat(obj.elem$(varNum, position)) :
            0f;
    }

    public double getAsDouble$(int varNum, int position) {
        return getAsDouble$(this, varNum, position);
    }
    public static double getAsDouble$(F3Object obj, int varNum, int position) {
        return Sequences.withinBounds(obj, varNum, position) ?
            Util.objectToDouble(obj.elem$(varNum, position)) :
            0.0;
    }

    public Object invoke$(int number, Object arg1, Object arg2, Object[] rargs) {
        throw new IllegalArgumentException("no such function: " + number);
    }


    public static Object invoke$(F3Object obj, int number, Object arg1, Object arg2, Object[] rargs) {
        throw new IllegalArgumentException("no such function: " + number);
    }
}
