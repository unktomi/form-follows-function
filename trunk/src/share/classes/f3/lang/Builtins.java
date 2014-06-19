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

package f3.lang;
import org.f3.functions.*;
import org.f3.runtime.*;
import org.f3.runtime.sequence.*;
import java.util.*;


/**
 * These functions are automatically imported for
 * all F3 Files to use
 *
 * @author Brian Goetz
 * @author Saul Wold
 * @profile common
 */
public class Builtins {
    /**
     * Compare 2 F3 Objects
     * 
     * @param a The first object to be compared
     * @param b the second object to be compared
     * @return true if they are the same Object
     */
    public static boolean isSameObject(Object a, Object b) {
        return a == b;
    }

    /**
     * Print the Object 'val'.
     * 
     * @param val The Object to be printed
     */
    public static void print(Object val) {
        if (val == null) {
            System.out.print(val);
        } else if (val instanceof String) {
            System.out.print((String) val);
        } else {
            System.out.print(val.toString());
        }
    }

    /**
     * Print the Object 'val' and a new-line.
     * 
     * @param val The Object to be printed
     */
    public static void printLine(Object val) {
	println(val);
    }

    public static void println(Object val) {
        if (val == null) {
            System.out.println(val);
        } else if (val instanceof String) {
            System.out.println((String) val);
        } else {
            System.out.println(val.toString());
        }
    }

    /**
     * Test if an instance variable has been initialized.
     * <p>
     * Can also be used on the current class via a "this" reference to determine
     * if variable initialization has completed.
     * <p>
     * When used on an instance variable, the semantics are as follows:
     * <ul>
     * <li>Initialized is false for all variables when a class is first initiated
     * <li>As variables have their initial values set this becomes true except for variables that have no default specified
     * <li>Upon setting a variable value to a non-default value this also becomes true
     * </ul>
     *
     * @param instance instance to be tested
     * @param offset offset of variable to be tested
     * @return true if the variable has been initialized
     */
    @org.f3.runtime.annotation.F3Signature("(Ljava/lang/Object;)Z")
    public static boolean isInitialized(F3Object instance, int offset) {
        return instance != null && (
                   offset == -1 ? instance.isInitialized$internal$() // this pointer uses -1
                   : (instance.varTestBits$(offset, F3Object.VFLGS$IS_BOUND, F3Object.VFLGS$IS_BOUND) && instance.varTestBits$(offset, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$PENDING)) ||
                     instance.varTestBits$(offset, F3Object.VFLGS$INIT$MASK, F3Object.VFLGS$INIT$INITIALIZED_DEFAULT)
               );
    }

    /**
     * Test if an instance variable is bound.
     *
     * @param instance instance to be tested
     * @param offset offset of variable to be tested
     * @return true if the variable is bound
     */
    @org.f3.runtime.annotation.F3Signature("(Ljava/lang/Object;)Z")
    public static boolean isReadOnly(F3Object instance, int offset) {
        return offset == -1 ? true // this pointer uses -1 (and is read only)
               : instance.varTestBits$(offset,
                 F3Object.VFLGS$IS_READONLY,
                 F3Object.VFLGS$IS_READONLY);
    }

    public static <a> a id(a x) {
	return x;
    }

    static class Curry<R,A1,A2> extends Function1<Function1<R, A2>, A1> {
        final Function2<? extends R, ? super A1, ? super A2> fun;
        public Curry(Function2<? extends R, ? super A1, ? super A2> fun) {
            this.fun = fun;
        }
        public String toString() {
            return "curry "+fun;
        }
        public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
            final A1 a1 = (A1)arg1;
            return new Function1<R,A2>() {
                public String toString() {
                    return "curry "+fun+ " "+a1;
                }
		/*
                public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
                    final A2 a2 = (A2)arg1;
                    return fun.invoke(a1, a2);
                }
		*/
		public R invoke(A2 a2) {
                    return fun.invoke(a1, a2);
		}
            };
        }
    }

    public static <R, A1, A2> Function1<Function1<R,A2>, A1> curry(final Function2<? extends R, ? super A1, ? super A2> fun) {
        return new Curry<R, A1, A2>(fun);
    }

    public static <R, A1, A2> Function2<? extends R, ? super A1, ? super A2> 
        uncurry(final Function1<? extends Function1<? extends R, ? super A2>, ? super A1> fun) {
        if (fun instanceof Curry) {
            return (Function2<? extends R, ? super A1, ? super A2>)((Curry)fun).fun;
        }
        return new Function2<R,A1,A2>() {
            public String toString() {
                return "uncurry "+fun;
            }
            public Object invoke$(Object arg1, Object arg2, Object[] rargs) {
                final A1 a1 = (A1)arg1;
                final A2 a2 = (A2)arg2;
                return fun.invoke(a1).invoke(a2);
            }
        };
    }
    /*
    public static <a,b> Either<a, b> Former(a x) 
    {
	return Either.former(x);
    }

    public static <a,b> Either<a, b> Latter(b y) 
    {
	return Either.latter(y);
    }
    */

    public static <a,b> Pair<? extends a, ? extends b> both(a x,  b y) 
    {
	return Pair.both(x, y);
    }

    public static <a, F extends Functor> Sequence<a> toSequence(Functor<F, a> xs)
    {
	final List<a> elems = new LinkedList();
	xs.map(new Function1<Void, a>() {
		public Void invoke(a x) {
		    elems.add(x);
		    return null;
		}
	    });
	return Sequences.fromCollection(TypeInfo.<a>getTypeInfo(), elems);
    }

    public static <a, b> a constant(a x, b y) 
    {
	return x;
    }

    public static <a, b> Pair<? extends a,? extends b> $comma(a x, b y) 
    {
	return Pair.both(x, y);
    }

    /*

    public static <a> Pair<? extends a,? extends java.lang.Integer> $comma(a x, int y) 
    {
	return Pair.<a,java.lang.Integer>both(x, y);
    }

    public static <a> Pair<? extends java.lang.Integer, ? extends a> $comma(int x, a y) 
    {
	return Pair.<java.lang.Integer, a>both(x, y);
    }

    public static <a> Pair<? extends a,? extends java.lang.Float> $comma(a x, float y) 
    {
	return Pair.<a,java.lang.Float>both(x, y);
    }

    public static <a> Pair<? extends java.lang.Float, ? extends a> $comma(float x, a y) 
    {
	return Pair.<java.lang.Float, a>both(x, y);
    }
    */

    public final static class NumberOperators {
        public static float negate(float n) {
            return -n;
        }
        public Function1<Float, Float> add(final float x) {
            return new Function1<Float,Float>() {
                public Float invoke(Float y) {
                    return x + y;
                }
            };
        }
        public Function1<Float, Float> subtract(final float x) {
            return new Function1<Float,Float>() {
                public Float invoke(Float y) {
                    return x - y;
                }
            };
        }
        public Function1<Float, Float> multiply(final float x) {
            return new Function1<Float,Float>() {
                public Float invoke(Float y) {
                    return x * y;
                }
            };
        }
        public Function1<Float, Float> divide(final float x) {
            return new Function1<Float,Float>() {
                public Float invoke(Float y) {
                    return x / y;
                }
            };
        }
        public Function1<Float, Float> remainder(final float x) {
            return new Function1<Float,Float>() {
                public Float invoke(Float y) {
                    return x % y;
                }
            };
        }
        public static float add(float x, float y) {
            return x + y;
        }
        public static float subtract(float x, float y) {
            return x - y;
        }
        public static float multiply(float x, float y) {
            return x * y;
        }
        public static float divide(float x, float y) {
            return x / y;
        }
        public static float remainder(float x, float y) {
            return x % y;
        }
        public static boolean lessThan(float x, float y) {
            return x < y;
        }
        public static boolean greaterThan(float x, float y) {
            return x > y;
        }
        public static boolean lessOrEqual(float x, float y) {
            return x <= y;
        }
        public static boolean greaterOrEqual(float x, float y) {
            return x >= y;
        }
        public static boolean equal(float x, float y) {
            return x == y;
        }
        public static boolean notEqual(float x, float y) {
            return x != y;
        }
    }

    public final static class IntegerOperators {
        public Function1<Integer, Integer> add(final int x) {
            return new Function1<Integer,Integer>() {
                public Integer invoke(Integer y) {
                    return x + y;
                }
            };
        }
        public Function1<Integer, Integer> subtract(final int x) {
            return new Function1<Integer,Integer>() {
                public Integer invoke(Integer y) {
                    return x - y;
                }
            };
        }
        public Function1<Integer, Integer> multiply(final int x) {
            return new Function1<Integer,Integer>() {
                public Integer invoke(Integer y) {
                    return x * y;
                }
            };
        }
        public Function1<Integer, Integer> divide(final int x) {
            return new Function1<Integer,Integer>() {
                public Integer invoke(Integer y) {
                    return x / y;
                }
            };
        }
        public Function1<Integer, Integer> remainder(final int x) {
            return new Function1<Integer,Integer>() {
                public Integer invoke(Integer y) {
                    return x % y;
                }
            };
        }
        public static int negate(int n) {
            return -n;
        }
        public static int add(int x, int y) {
            return x + y;
        }
        public static int subtract(int x, int y) {
            return x - y;
        }
        public static int multiply(int x, int y) {
            return x * y;
        }
        public static int divide(int x, int y) {
            return x / y;
        }
        public static int remainder(int x, int y) {
            return x % y;
        }
        public static boolean lessThan(int x, int y) {
            return x < y;
        }
        public static boolean greaterThan(int x, int y) {
            return x > y;
        }
        public static boolean lessOrEqual(int x, int y) {
            return x <= y;
        }
        public static boolean greaterOrEqual(int x, int y) {
            return x >= y;
        }
        public static boolean equal(int x, int y) {
            return x == y;
        }
        public static boolean notEqual(int x, int y) {
            return x != y;
        }
    }

    public final static class BooleanOperators {

        public static <a> a If (boolean condition, a _then, a _else) {
            return condition ? _then : _else; 
        }

        public static boolean yes() {
            return true;
        }

        public static boolean no() {
            return false;
        }

        public static boolean Yes() {
            return true;
        }

        public static boolean No() {
            return false;
        }

        public static boolean not(boolean value) {
            return !value;
        }

        public static boolean and(final boolean former, final boolean latter) {
            return former && latter;
        }

        public static Function1<Boolean, Boolean> and(final boolean former) {
            return new Function1<Boolean, Boolean>() {
                public Boolean invoke(Boolean latter) {
                    return former && latter;
                }
            };
        }

        public static boolean or(final boolean former, final boolean latter) {
            return former || latter;
        }

        public static Function1<Boolean, Boolean> or(final boolean former) {
            return new Function1<Boolean, Boolean>() {
                public Boolean invoke(Boolean latter) {
                    return former || latter;
                }
            };
        }
        public static boolean equal(boolean former, boolean latter) {
            return former == latter;
        }
        public static boolean notEqual(boolean former, boolean latter) {
            return former != latter;
        }
    }

    public static Void unit() { return null; }
}
