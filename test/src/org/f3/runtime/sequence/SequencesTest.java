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
package org.f3.runtime.sequence;

import java.util.Comparator;

import org.f3.runtime.F3TestCase;
import org.f3.runtime.TypeInfo;

/**
 * SequencesTest
 *
 * @author Michael Heinrichs
 */
public class SequencesTest extends F3TestCase {

    public static class DummyElement {
        public int id;
        public DummyElement(int id) { this.id = id; }
        @Override
        public boolean equals(Object o) {
            if (o instanceof DummyElement && id == ((DummyElement)o).id)
                return true;
            return false;
        }
        @Override
        public int hashCode() {
            return id;
        }
    }
    public static class DummyComparator implements Comparator<DummyElement> {
        public int compare(DummyElement o1, DummyElement o2) {
            return o1.id - o2.id;
        }
    }
    
    public Sequence<Integer> emptyInteger, singleInteger, sortedInteger, unsortedInteger;
    public Sequence<DummyElement> emptyElements, singleElements, sortedElements, unsortedElements, longSequence;

    public static DummyElement[] element;
    public static DummyComparator comparator;
    
    @Override
    protected void setUp() {
        // Integer-sequences
        emptyInteger    = TypeInfo.Integer.emptySequence;
        singleInteger   = Sequences.make(TypeInfo.Integer, 0);
        sortedInteger   = Sequences.make(TypeInfo.Integer, 1, 2, 3);
        unsortedInteger = Sequences.make(TypeInfo.Integer, 3, 1, 2);
        
        // 4 Dummyelements
        element = new DummyElement[4];
        for (int i=0; i<element.length; ++i)
            element[i] = new DummyElement(i);
        
        // DummyElement-sequences
        emptyElements    = Sequences.emptySequence(DummyElement.class);
        singleElements   = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[0]);
        sortedElements   = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[1], element[2], element[3]);
        unsortedElements = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2]);
        longSequence     = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[0], element[1], element[2], element[1], element[3]);

        // Comparator
        comparator = new DummyComparator();
    }
    
    /** 
     * <T extends Comparable> int binarySearch (Sequence<? extends T> seq, T key) 
     * This method uses Arrays.binarySearch for sorting, which we can asume to
     * work. Only tests for the mapping are needed.
     */
    public void testBinarySearchComparable() {
        int result;
        // search in empty sequence
        result = Sequences.binarySearch(emptyInteger, 1);
        assertEquals(TypeInfo.Integer.emptySequence, emptyInteger);
        assertEquals(-1, result);
        
        // single element sequence
        // successful search
        result = Sequences.binarySearch(singleInteger, 0);
        assertEquals(singleInteger, 0);
        assertEquals(0, result);
        
        // unsuccessful search
        result = Sequences.binarySearch(singleInteger, 1);
        assertEquals(singleInteger, 0);
        assertEquals(-2, result);
        
        // three elements sequence
        // successful search
        result = Sequences.binarySearch(sortedInteger, 2);
        assertEquals(sortedInteger, 1, 2, 3);
        assertEquals(1, result);
        
        // unsuccessful search
        result = Sequences.binarySearch(sortedInteger, 0);
        assertEquals(sortedInteger, 1, 2, 3);
        assertEquals(-1, result);
        
        // exception when sequence is null
        try {
            Sequences.binarySearch(null, 0);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            System.out.println(ex.getClass());
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    /** 
     * <T> int binarySearch(Sequence<? extends T> seq, T key, Comparator<? super T> c) 
     * This method uses Arrays.binarySearch for sorting, which we can asume to
     * work. Only tests for the mapping are needed.
     */
    public void testBinarySearchComparator() {
        int result;
        // search in empty sequence
        result = Sequences.binarySearch(emptyElements, element[1], comparator);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(-1, result);
        
        // single element sequence
        // successful search
        result = Sequences.binarySearch(singleElements, element[0], comparator);
        assertEquals(singleElements, element[0]);
        assertEquals(0, result);
        
        // unsuccessful search
        result = Sequences.binarySearch(singleElements, element[1], comparator);
        assertEquals(singleElements, element[0]);
        assertEquals(-2, result);
        
        // three elements sequence
        // successful search
        result = Sequences.binarySearch(sortedElements, element[2], comparator);
        assertEquals(sortedElements, element[1], element[2], element[3]);
        assertEquals(1, result);
        
        // unsuccessful search
        result = Sequences.binarySearch(sortedElements, element[0], comparator);
        assertEquals(sortedElements, element[1], element[2], element[3]);
        assertEquals(-1, result);

        // search using natural order
        int resultInt = Sequences.binarySearch(sortedInteger, 2, null);
        assertEquals(sortedInteger, 1, 2, 3);
        assertEquals(1, resultInt);
        
        // exception if using null-operator with non-comparable elements
        try {
            result = Sequences.binarySearch(sortedElements, element[2], null);
            fail("No exception thrown.");
        }
        catch (ClassCastException ex) {
            assertEquals(sortedElements, element[1], element[2], element[3]);
        }
        catch (Exception ex) {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is null
        try {
            Sequences.binarySearch(null, 1, null);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    /** 
     * <T> int indexByIdentity(Sequence<? extends T> seq, T key) 
     */
    public void testIndexByIdentity() {
        int result;
        // search in empty sequence
        result = Sequences.indexByIdentity(emptyElements, element[1]);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(-1, result);
        
        // single element sequence
        // successful search
        result = Sequences.indexByIdentity(singleElements, element[0]);
        assertEquals(singleElements, element[0]);
        assertEquals(0, result);
        
        // unsuccessful search
        result = Sequences.indexByIdentity(singleElements, element[1]);
        assertEquals(singleElements, element[0]);
        assertEquals(-1, result);
        
        // three elements sequence
        // successful search for first element
        result = Sequences.indexByIdentity(unsortedElements, element[3]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(0, result);
        
        // successful search for middle element
        result = Sequences.indexByIdentity(unsortedElements, element[1]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(1, result);
        
        // successful search for last element
        result = Sequences.indexByIdentity(unsortedElements, element[2]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(2, result);
        
        // make sure first element is returned
        result = Sequences.indexByIdentity(longSequence, element[1]);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(1, result);
        
        // unsuccessful search
        result = Sequences.indexByIdentity(unsortedElements, element[0]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(-1, result);
        
        // make sure search is by identity
        DummyElement localElement = new DummyElement(1);
        assertNotSame(element[1], localElement);
        assertEquals(element[1], localElement);
        result = Sequences.indexByIdentity(unsortedElements, localElement);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(-1, result);

        result = Sequences.indexByIdentity(null, 1);
        assertEquals(-1, result);

        // exception when key is null
        try {
            Sequences.indexByIdentity(unsortedElements, null);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    /** 
     * <T> int indexOf(Sequence<? extends T> seq, T key) 
     */
    public void testIndexOf() {
        int result;
        // search in empty sequence
        result = Sequences.indexOf(emptyElements, element[1]);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(-1, result);
        
        // single element sequence
        // successful search
        result = Sequences.indexOf(singleElements, element[0]);
        assertEquals(singleElements, element[0]);
        assertEquals(0, result);
        
        // unsuccessful search
        result = Sequences.indexOf(singleElements, element[1]);
        assertEquals(singleElements, element[0]);
        assertEquals(-1, result);
        
        // three elements sequence
        // successful search for first element
        result = Sequences.indexOf(unsortedElements, element[3]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(0, result);
        
        // successful search for middle element
        result = Sequences.indexOf(unsortedElements, element[1]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(1, result);
        
        // successful search for last element
        result = Sequences.indexOf(unsortedElements, element[2]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(2, result);
        
        // make sure first element is returned
        result = Sequences.indexOf(longSequence, element[1]);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(1, result);
        
        // unsuccessful search
        result = Sequences.indexOf(unsortedElements, element[0]);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(-1, result);

        result = Sequences.indexOf(null, 1);
        assertEquals(-1, result);

        // exception when key is null
        try {
            Sequences.indexOf(unsortedElements, null);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    /**
     * <T extends Comparable> T max (Sequence<T> seq)
     */
    public void testMaxComparable() {
        int result;
        
        // get maximum in single element sequence
        result = Sequences.max(singleInteger);
        assertEquals(singleInteger, 0);
        assertEquals(0, result);
        
        // get first element
        result = Sequences.max(unsortedInteger);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(3, result);
        
        // get middle element
        Sequence<Integer> fixture = Sequences.make(TypeInfo.Integer, 11, 13, 12);
        result = Sequences.max(fixture);
        assertEquals(fixture, 11, 13, 12);
        assertEquals(13, result);
        
        // get last element
        result = Sequences.max(sortedInteger);
        assertEquals(sortedInteger, 1, 2, 3);
        assertEquals(3, result);
        
        // exception when sequence is null
        try {
            Sequences.max(null);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is empty
        try {
            Sequences.max(emptyInteger);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
    }
    
    /**
     * <T> T max (Sequence<T> seq, Comparator<? super T> c)
     */
    public void testMaxComparator() {
        DummyElement result;
        
        // get maximum in single element sequence
        result = Sequences.max(singleElements, comparator);
        assertEquals(singleElements, element[0]);
        assertEquals(element[0], result);
        
        // get first element
        result = Sequences.max(unsortedElements, comparator);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(element[3], result);
        
        // get middle element
        Sequence<DummyElement> fixture = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[0], element[3], element[2]);
        result = Sequences.max(fixture, comparator);
        assertEquals(fixture, element[0], element[3], element[2]);
        assertEquals(element[3], result);
        
        // get last element
        result = Sequences.max(sortedElements, comparator);
        assertEquals(sortedElements, element[1], element[2], element[3]);
        assertEquals(element[3], result);
        
        // max using natural order
        int resultInt = Sequences.max(unsortedInteger, null);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(3, resultInt);
        
        // exception when sequence is null
        try {
            Sequences.<DummyElement>max(null, comparator);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is empty
        try {
            Sequences.max(emptyElements, comparator);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
    }
    
    /**
     * <T extends Comparable> T min (Sequence<T> seq)
     */
    public void testMinComparable() {
        int result;
        
        // get minimum in single element sequence
        result = Sequences.min(singleInteger);
        assertEquals(singleInteger, 0);
        assertEquals(0, result);
        
        // get first element
        result = Sequences.min(sortedInteger);
        assertEquals(sortedInteger, 1, 2, 3);
        assertEquals(1, result);
        
        // get middle element
        result = Sequences.min(unsortedInteger);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(1, result);
        
        // get last element
        Sequence<Integer> fixture = Sequences.make(TypeInfo.Integer, 12, 13, 11);
        result = Sequences.min(fixture);
        assertEquals(fixture, 12, 13, 11);
        assertEquals(11, result);
        
        // exception when sequence is null
        try {
            Sequences.min(null);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is empty
        try {
            Sequences.min(emptyInteger);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
    }
    
     /**
     * <T> T max (Sequence<T> seq, Comparator<? super T> c)
     */
    public void testMinComparator() {
        DummyElement result;
        
        // get maximum in single element sequence
        result = Sequences.min(singleElements, comparator);
        assertEquals(singleElements, element[0]);
        assertEquals(element[0], result);
        
        // get first element
        result = Sequences.min(sortedElements, comparator);
        assertEquals(sortedElements, element[1], element[2], element[3]);
        assertEquals(element[1], result);
        
        // get middle element
        result = Sequences.min(unsortedElements, comparator);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(element[1], result);
        
        // get last element
        Sequence<DummyElement> fixture = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[2], element[3], element[0]);
        result = Sequences.min(fixture, comparator);
        assertEquals(fixture, element[2], element[3], element[0]);
        assertEquals(element[0], result);
        
        // min using natural order
        int resultInt = Sequences.min(unsortedInteger, null);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(1, resultInt);
        
        // exception when sequence is null
        try {
            Sequences.<DummyElement>min(null, comparator);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is empty
        try {
            Sequences.min(emptyElements, comparator);
            fail("No exception thrown.");
        }
        catch (IllegalArgumentException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
        
    }
    
    /**
     * <T> int nextIndexByIdentity(Sequence<? extends T> seq, T key, int pos)
     * The basic functionality is tested by testIndexByIdentity. Only tests for 
     * pos>0 are needed here.
     */
    public void testNextIndexByIdentity() {
        int result;
        // search in empty sequence
        result = Sequences.nextIndexByIdentity(emptyElements, element[1], 1);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(-1, result);
        
        // single element sequence
        result = Sequences.nextIndexByIdentity(singleElements, element[0], 1);
        assertEquals(singleElements, element[0]);
        assertEquals(-1, result);
        
        // search with pos = result
        result = Sequences.nextIndexByIdentity(longSequence, element[1], 1);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(1, result);
        
        // search with pos < result
        result = Sequences.nextIndexByIdentity(longSequence, element[1], 2);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(3, result);
        
        // unsuccessful search
        result = Sequences.nextIndexByIdentity(longSequence, element[1], 4);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(-1, result);
        
        // search with pos > sequence-size
        result = Sequences.nextIndexByIdentity(longSequence, element[1], 5);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(-1, result);
        
        // make sure search is by identity
        DummyElement localElement = new DummyElement(1);
        assertNotSame(element[1], localElement);
        assertEquals(element[1], localElement);
        result = Sequences.nextIndexByIdentity(longSequence, localElement, 1);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(-1, result);
    }
    
    /**
     * <T> int nextIndexOf(Sequence<? extends T> seq, T key, int pos)
     * The basic functionality is tested by testIndexOf. Only tests for 
     * pos>0 are needed here.
     */
    public void testNextIndexOf() {
        int result;
        // search in empty sequence
        result = Sequences.nextIndexOf(emptyElements, element[1], 1);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(-1, result);
        
        // single element sequence
        result = Sequences.nextIndexOf(singleElements, element[0], 1);
        assertEquals(singleElements, element[0]);
        assertEquals(-1, result);
        
        // search with pos = result
        result = Sequences.nextIndexOf(longSequence, element[1], 1);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(1, result);
        
        // search with pos < result
        result = Sequences.nextIndexOf(longSequence, element[1], 2);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(3, result);
        
        // unsuccessful search
        result = Sequences.nextIndexOf(longSequence, element[1], 4);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(-1, result);
        
        // search with pos > sequence-size
        result = Sequences.nextIndexOf(longSequence, element[1], 5);
        assertEquals(longSequence, element[0], element[1], element[2], element[1], element[3]);
        assertEquals(-1, result);
    }
    
    /**
     * <T> Sequence<T> reverse(Sequence<T> seq)
     */
    public void testReverse() {
        Sequence<Integer> result;
        
        // reverse empty sequence
        result = Sequences.reverse(emptyInteger);
        assertEquals(TypeInfo.Integer.emptySequence, emptyInteger);
        assertEquals(TypeInfo.Integer.emptySequence, result);
        
        // reverse single element sequence
        result = Sequences.reverse(singleInteger);
        assertEquals(singleInteger, 0);
        assertEquals(result, 0);
        
        // reverse three element sequence
        result = Sequences.reverse(unsortedInteger);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(result, 2, 1, 3);
        
        // exception when sequence is null
        try {
            Sequences.sort(null);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
   /**
     * <T extends Comparable> Sequence<T> sort (Sequence<T> seq) 
     * This method uses Arrays.sort for sorting, which we can asume to work.
     * Only tests for the mapping are needed.
     */
    public void testSortComparable() {
        Sequence<? extends Integer> result;
        
        // sort empty sequence
        result = Sequences.sort(emptyInteger);
        assertEquals(TypeInfo.Integer.emptySequence, emptyInteger);
        assertEquals(TypeInfo.Integer.emptySequence, result);
        
        // sort single element
        result = Sequences.sort(singleInteger);
        assertEquals(singleInteger, 0);
        assertEquals(result, 0);
        
        // sort unsorted sequence
        result = Sequences.sort(unsortedInteger);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(result, 1, 2, 3);
        
        // exception when sequence is null
        try {
            Sequences.sort(null);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    /**
     * <T> Sequence<T> sort (Sequence<T> seq, Comparator<? super T> c) 
     * This method uses Arrays.sort for sorting, which we can asume to work.
     * Only tests for the mapping are needed.
     */
    public void testSortComparator() {
        Sequence<? extends DummyElement> result;
                
        // sort empty sequence
        result = Sequences.sort(emptyElements, comparator);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), result);
        
        // sort single element
        result = Sequences.sort(singleElements, comparator);
        assertEquals(singleElements, element[0]);
        assertEquals(result, element[0]);
        
        // sort unsorted sequence
        result = Sequences.sort(unsortedElements, comparator);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(result, element[1], element[2], element[3]);
        
        // sort using natural order
        Sequence<? extends Integer> resultInt = Sequences.sort(unsortedInteger, null);
        assertEquals(unsortedInteger, 3, 1, 2);
        assertEquals(resultInt, 1, 2, 3);
        
        // exception if using null-operator with non-comparable elements
        try {
            result = Sequences.sort(unsortedElements, null);
            fail("No exception thrown.");
        }
        catch (ClassCastException ex) {
            assertEquals(unsortedElements, element[3], element[1], element[2]);
        }
        catch (Exception ex) {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
        
        // exception when sequence is null
        try {
            Sequences.sort(null, comparator);
            fail("No exception thrown.");
        }
        catch (NullPointerException ex) {
        }
        catch (Exception ex) {
            fail ("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * 	<T> boolean isEqual(Sequence<T> one, Sequence<T> other) 
     */
    public void testIsEqual() {
        boolean result;
        Sequence<DummyElement> localSeq;

        // compare empty sequences
        localSeq = Sequences.emptySequence(DummyElement.class);
        result = Sequences.isEqual(emptyElements, localSeq);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), localSeq);
        assertEquals(true, result);

        // compare first sequence being null
        result = Sequences.isEqual(null, emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare second sequence being null
        result = Sequences.isEqual(emptyElements, null);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare equal sequence
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2]);
        result = Sequences.isEqual(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1], element[2]);
        assertEquals(true, result);

        // compare sequence unequal by identity but equal by equals()
        DummyElement localElement = new DummyElement(1);
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], localElement, element[2]);
        result = Sequences.isEqual(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], localElement, element[2]);
        assertEquals(true, result);

        // compare first sequence smaller than second
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1]);
        result = Sequences.isEqual(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1]);
        assertEquals(false, result);

        // compare first sequence larger than second
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2], element[3]);
        result = Sequences.isEqual(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1], element[2], element[3]);
        assertEquals(false, result);
    }

    /**
     * 	<T> boolean isEqualByContentIdentity(Sequence<T> one, Sequence<T> other) 
     */
    public void testIsEqualByContentIdentity() {
        boolean result;
        Sequence<DummyElement> localSeq;

        // compare empty sequences
        localSeq = Sequences.emptySequence(DummyElement.class);
        result = Sequences.isEqualByContentIdentity(emptyElements, localSeq);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), localSeq);
        assertEquals(true, result);

        // compare first sequence being null
        result = Sequences.isEqualByContentIdentity(null, emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare second sequence being null
        result = Sequences.isEqualByContentIdentity(emptyElements, null);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare equal sequence
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2]);
        result = Sequences.isEqualByContentIdentity(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1], element[2]);
        assertEquals(true, result);

        // compare sequence unequal by identity but equal by equals()
        DummyElement localElement = new DummyElement(1);
        assertNotSame(element[1], localElement);
        assertEquals(element[1], localElement);
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], localElement, element[2]);
        result = Sequences.isEqualByContentIdentity(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], localElement, element[2]);
        assertEquals(false, result);

        // compare first sequence smaller than second
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1]);
        result = Sequences.isEqualByContentIdentity(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1]);
        assertEquals(false, result);

        // compare first sequence larger than second
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2], element[3]);
        result = Sequences.isEqualByContentIdentity(unsortedElements, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1], element[2], element[3]);
        assertEquals(false, result);
    }
	

    public void testSliceEqual() {
        boolean result;
        Sequence<DummyElement> localSeq;

        // compare empty sequences
        localSeq = Sequences.emptySequence(DummyElement.class);
        result = Sequences.sliceEqual(emptyElements, 0, 0, localSeq);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), localSeq);
        assertEquals(true, result);

        // compare sequence being null
        result = Sequences.sliceEqual(null, 0, 0, emptyElements);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare slice being null
        result = Sequences.sliceEqual(emptyElements, 0, 0, null);
        assertEquals(Sequences.emptySequence(DummyElement.class), emptyElements);
        assertEquals(true, result);

        // compare equal sequence
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1], element[2]);
        result = Sequences.sliceEqual(unsortedElements, 0, 3, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1], element[2]);
        assertEquals(true, result);

        // compare sequence unequal by identity but equal by equals()
        DummyElement localElement = new DummyElement(1);
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], localElement, element[2]);
        result = Sequences.sliceEqual(unsortedElements, 0, 3, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], localElement, element[2]);
        assertEquals(true, result);

        // compare slice at the beginning
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[1]);
        result = Sequences.sliceEqual(unsortedElements, 0, 2, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[1]);
        assertEquals(true, result);
        
        // compare slice at the end
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[1], element[2]);
        result = Sequences.sliceEqual(unsortedElements, 1, 3, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[1], element[2]);
        assertEquals(true, result);
        
        // compare single-element slice
        localSeq = Sequences.singleton(TypeInfo.<DummyElement>getTypeInfo(), element[3]);
        result = Sequences.sliceEqual(unsortedElements, 0, 1, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3]);
        assertEquals(true, result);
        
        // compare unequal slices
        localSeq = Sequences.singleton(TypeInfo.<DummyElement>getTypeInfo(), element[2]);
        result = Sequences.sliceEqual(unsortedElements, 0, 1, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[2]);
        assertEquals(false, result);
        localSeq = Sequences.make(TypeInfo.<DummyElement>getTypeInfo(), element[3], element[2]);
        result = Sequences.sliceEqual(unsortedElements, 0, 2, localSeq);
        assertEquals(unsortedElements, element[3], element[1], element[2]);
        assertEquals(localSeq, element[3], element[2]);
        assertEquals(false, result);
    }     
}
