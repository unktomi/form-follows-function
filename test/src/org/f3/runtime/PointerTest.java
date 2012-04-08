package org.f3.runtime;

import f3.animation.KeyValueTarget.Type;
import org.f3.runtime.sequence.*;

/**
 * This class tests Pointer access of F3Base instance variables.
 *
 * @author A. Sundararajan
 */
public class PointerTest extends F3TestCase {
    private static class Counter extends F3Base {
        public static final int VCNT$ = 1;
        public int count$() { return VCNT$; }

        private int $count;
        public static final int VOFF$count = 0;
           
        public int get$count() { return $count; }
        public void set$count(int count) { $count = count; } 

        public Object get$(int varNum) {
            switch (varNum) {
                case 0: return get$count();
                default: return super.get$(varNum);
            }
        }

        public void set$(int varNum, Object value) {
            switch (varNum) {
                case 0: set$count((Integer)value); break;
                default: super.set$(varNum, value);
            }
        }

        public Class getType$(int varNum) {
            switch (varNum) {
                case 0: return Integer.TYPE;
                default: return super.getType$(varNum);
            }
        }
    };
 
    public void testPointerMake() {
        Counter counter = new Counter();
        // make a Pointer for varNum 0
        assertNotNull(Pointer.make(Type.INTEGER, counter, 0));
    }

    public void testPointerAccess() {
        Counter counter = new Counter();
        // make a Pointer for varNum 0
        Pointer ptr = Pointer.make(Type.INTEGER, counter, 0);
        // check the type of the pointer
        assertEquals(Type.INTEGER, ptr.getType());

        // set through varNum
        counter.set$(0, 1729);
        // get through varNum, particular accessor and pointer
        assertEquals(1729, counter.get$(0));
        assertEquals(1729, counter.get$count());
        assertEquals(1729, ptr.get());

        // set through accessor
        counter.set$count(1674);
        // get through varNum, particular accessor and pointer
        assertEquals(1674, counter.get$(0));
        assertEquals(1674, counter.get$count());
        assertEquals(1674, ptr.get());

        // set through pointer
        ptr.set(3333);
        // get through varNum, particular accessor and pointer
        assertEquals(3333, counter.get$(0));
        assertEquals(3333, counter.get$count());
        assertEquals(3333, ptr.get());
    } 

    // test pointer access for all types
    private static class AllTypeVarsHolder extends F3Base {
        public static final int VCNT$ = 10;
        public static int VCNT$() {
            return VCNT$;
        }
        
        public int count$() {
            return VCNT$();
        }

        public static final int VOFF$byteVar = 0;
        public static final int VOFF$shortVar = 1;
        public static final int VOFF$intVar = 2;
        public static final int VOFF$longVar = 3;
        public static final int VOFF$floatVar = 4;
        public static final int VOFF$doubleVar = 5;
        public static final int VOFF$charVar = 6;
        public static final int VOFF$booleanVar = 7;
        public static final int VOFF$sequenceVar = 8;
        public static final int VOFF$stringVar = 9;
        
        public byte $byteVar = 0;
        public short $shortVar = 0;
        public int $intVar = 0;
        public long $longVar = 0L;
        public float $floatVar = 0.0F;
        public double $doubleVar = 0.0;
        public char $charVar = ' ';
        public boolean $booleanVar = false;
        public Sequence<Integer> $sequenceVar;
        public String $stringVar = "";
        
        public byte get$byteVar() {
            return $byteVar;
        }
        
        public void set$byteVar(byte varNewValue$) {
            $byteVar = varNewValue$;
        }
        
        public short get$shortVar() {
            return $shortVar;
        }
        
        public void set$shortVar(short varNewValue$) {
            $shortVar = varNewValue$;
        }
        
        public int get$intVar() {
            return $intVar;
        }
        
        public void set$intVar(int varNewValue$) {
            $intVar = varNewValue$;
        } 
        
        public long get$longVar() {
            return $longVar;
        }
        
        public void set$longVar(long varNewValue$) {
            $longVar = varNewValue$;
        }
        
        public float get$floatVar() {
            return $floatVar;
        }
        
        public void set$floatVar(float varNewValue$) {
            $floatVar = varNewValue$;
        }
        
        public double get$doubleVar() {
            return $doubleVar;
        }
        
        public void set$doubleVar(double varNewValue$) {
            $doubleVar = varNewValue$;
        }
        
        public char get$charVar() {
            return $charVar;
        }
        
        public void set$charVar(char varNewValue$) {
            $charVar = varNewValue$;
        }
        
        public boolean get$booleanVar() {
            return $booleanVar;
        }
        
        public void set$booleanVar(boolean varNewValue$) {
            $booleanVar = varNewValue$;
        }

        public Sequence<Integer> get$sequenceVar() {
            return $sequenceVar;
        }
        
        public void set$sequenceVar(Sequence<Integer> varNewValue$) {
            $sequenceVar = varNewValue$;
        }

        public String get$stringVar() {
            return $stringVar;
        }

        public void set$stringVar(String varNewValue$) {
            $stringVar = varNewValue$;
        }

        public Object get$(int varNum) {
            switch (varNum) {
                case VOFF$byteVar: return get$byteVar();
                case VOFF$shortVar: return get$shortVar();
                case VOFF$intVar: return get$intVar();
                case VOFF$longVar: return get$longVar();
                case VOFF$floatVar: return get$floatVar();
                case VOFF$doubleVar: return get$doubleVar();
                case VOFF$charVar: return get$charVar();
                case VOFF$booleanVar: return get$booleanVar();
                case VOFF$sequenceVar: return get$sequenceVar();
                case VOFF$stringVar: return get$stringVar();
                default: return super.get$(varNum); 
            }
        }

        public void set$(int varNum, Object value) {
            switch (varNum) {
                case VOFF$byteVar: set$byteVar(((Number)value).byteValue()); break;
                case VOFF$shortVar: set$shortVar(((Number)value).shortValue()); break;
                case VOFF$intVar: set$intVar(((Number)value).intValue()); break;
                case VOFF$longVar: set$longVar(((Number)value).longValue()); break;
                case VOFF$floatVar: set$floatVar(((Number)value).floatValue()); break;
                case VOFF$doubleVar: set$doubleVar(((Number)value).doubleValue()); break;
                case VOFF$charVar: set$charVar(((Character)value).charValue()); break;
                case VOFF$booleanVar: set$booleanVar(((Boolean)value).booleanValue()); break;
                case VOFF$sequenceVar: set$sequenceVar((Sequence<Integer>)value); break;
                case VOFF$stringVar: set$stringVar((String)value); break;
                default: super.set$(varNum, value);  break;
            }
        }

        public Class getType$(int varNum) {
            switch (varNum) {
                case VOFF$byteVar: return Byte.TYPE;
                case VOFF$shortVar: return Short.TYPE;
                case VOFF$intVar: return Integer.TYPE;
                case VOFF$longVar: return Long.TYPE;
                case VOFF$floatVar: return Float.TYPE;
                case VOFF$doubleVar: return Double.TYPE;
                case VOFF$charVar: return Character.TYPE;
                case VOFF$booleanVar: return Boolean.TYPE;
                case VOFF$sequenceVar: return Sequence.class;
                case VOFF$stringVar: return String.class;
                default: return super.getType$(varNum); 
            }
        }
    }

    public void testPointerTypes() {
        AllTypeVarsHolder f3Obj = new AllTypeVarsHolder();

        Pointer bytePtr = Pointer.make(Type.BYTE, f3Obj, f3Obj.VOFF$byteVar);
        Pointer shortPtr = Pointer.make(Type.SHORT, f3Obj, f3Obj.VOFF$shortVar);
        Pointer intPtr = Pointer.make(Type.INTEGER, f3Obj, f3Obj.VOFF$intVar);
        Pointer longPtr = Pointer.make(Type.LONG, f3Obj, f3Obj.VOFF$longVar);
        Pointer floatPtr = Pointer.make(Type.FLOAT, f3Obj, f3Obj.VOFF$floatVar);
        Pointer doublePtr = Pointer.make(Type.DOUBLE, f3Obj, f3Obj.VOFF$doubleVar);
        Pointer charPtr = Pointer.make(Type.INTEGER, f3Obj, f3Obj.VOFF$charVar);
        Pointer booleanPtr = Pointer.make(Type.BOOLEAN, f3Obj, f3Obj.VOFF$booleanVar);
        Pointer sequencePtr = Pointer.make(Type.SEQUENCE, f3Obj, f3Obj.VOFF$sequenceVar);
        Pointer stringPtr = Pointer.make(Type.OBJECT, f3Obj, f3Obj.VOFF$stringVar);

        bytePtr.set(Byte.valueOf((byte)1));
        shortPtr.set(Short.valueOf((short)2));
        intPtr.set(1729);
        longPtr.set(444444L);
        floatPtr.set(3.14F);
        doublePtr.set(Math.E);
        charPtr.set('J');
        booleanPtr.set(true);
        IntArraySequence seq = new IntArraySequence();
        assertEquals(seq, seq);
        int[] arr = { 8, 5, 7, 1, -1 };
        seq.addFromArray(arr, 0, arr.length);
        sequencePtr.set(seq);
        stringPtr.set("F3");

        assertEquals(Byte.valueOf((byte)1), bytePtr.get());	
        assertEquals(Byte.valueOf((byte)1), f3Obj.get$(f3Obj.VOFF$byteVar));	
        assertEquals(Short.valueOf((short)2), shortPtr.get());
        assertEquals(Short.valueOf((short)2), f3Obj.get$(f3Obj.VOFF$shortVar));
        assertEquals(1729, intPtr.get());
        assertEquals(1729, f3Obj.get$(f3Obj.VOFF$intVar));
        assertEquals(444444L, longPtr.get());
        assertEquals(444444L, f3Obj.get$(f3Obj.VOFF$longVar));
        assertEquals(3.14F, floatPtr.get());
        assertEquals(3.14F, f3Obj.get$(f3Obj.VOFF$floatVar));
        assertEquals(Math.E, doublePtr.get());
        assertEquals(Math.E, f3Obj.get$(f3Obj.VOFF$doubleVar));
        assertEquals('J', charPtr.get());
        assertEquals('J', f3Obj.get$(f3Obj.VOFF$charVar));
        assertEquals(true, booleanPtr.get());
        assertEquals(true, f3Obj.get$(f3Obj.VOFF$booleanVar));
        assertEquals(true, Sequences.isEqual(seq, (Sequence)sequencePtr.get()));
        assertEquals(true, Sequences.isEqual(seq, (Sequence)f3Obj.get$(f3Obj.VOFF$sequenceVar)));
        assertEquals("F3", stringPtr.get());
        assertEquals("F3", f3Obj.get$(f3Obj.VOFF$stringVar));
    } 
}
