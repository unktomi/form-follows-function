/*
 * @test
 * @run
 *
 */

import java.lang.System;
import java.lang.Object;
import java.lang.Exception;

/**
 * Port of qsort.java, a javac test.
 * For comparision, compile qsort.java and run (take a second to sort 3000+ items).
 *  The partition() function is very slow.
 * This FAILS for smaller sequences
 */

public class qsort extends cbm
{
    var arraysize = 32000;
    var Int_sequence:Integer[];
    var compare_count = 0;
    var swap_count = 0;

    /** Return length of internal array */
    function length():Integer { return sizeof Int_sequence; }

    /** Print array preceded by message msg  */
    function printArray(msg: String)    {
      verboseOut(msg);
      verboseOutln(Int_sequence);
    }

    /** Swap object in Int_sequence with indexes i and j  */
    function swap( i:Integer, j:Integer)
    {   verboseOut( "swap({i},{j})" );
        ++swap_count;
        var temp = Int_sequence[i];
        Int_sequence[i] = Int_sequence[j];
        Int_sequence[j] = temp;
    }

    function Compare(i1:Integer, i2:Integer):Integer
    {
       ++compare_count;
       return (i1 - i2);
    }

    /** Find pivot point in Int_sequence  */
    function findPivot(i:Integer, j:Integer):Integer
    {
       var ret;
       var firstkey = Int_sequence[i];
       for(k in [i+1..j step 1]) {
          if( Compare(Int_sequence[k],firstkey) > 0) { return k; } //ret =k; return ret; }
          else if( Compare( Int_sequence[k],firstkey) < 0 )  { return i; } //ret = i; return i; }
       }
       //should not get here unless sequence until sequence is sorted.
       return -1;
    }

    /** Partition array around pivot point 'pivot'	  */
    function partition(i:Integer, j:Integer, pivot:Integer): Integer
    {
        verboseOut("start partition({i},{j})......");
        var l = i;
        var r = j;
          while(l<=r)
          {
              swap(l,r);
              while( Compare(Int_sequence[l],pivot)<0 )
              {l = l+1;}
              while( Compare(Int_sequence[r],pivot)>-1 )
              {r = r-1;}
          }
        verboseOut(".....finish partition({i},{j})");
        return l;
    }

    /**
     * Recursive quick sort algorithm
     *   While pivot index is >=0(ie., j>i)
     *   1. Find pivot index.
     *   2. Partition array around index
     *   3. Sort each partition
     */
    function sort(i:Integer,j:Integer):Boolean {
        var k =0;
        var pivotindex = 0;
        var pivot = 0;
        pivotindex = findPivot(i,j);
          if(pivotindex >=0)
          {
              pivot = Int_sequence[pivotindex];
              k = partition(i,j,pivot);
              sort(i,k-1);
              sort(k,j);
          }
        return true;
    }


override public function test():Number {
   if(LOOPCOUNT>0) arraysize=LOOPCOUNT;
   var multiplier = 100;
   var modnum = 151;
     compare_count = 0;
     swap_count = 0;

     for( j in [0..arraysize-1]) {
       insert ((j*multiplier + 1) mod modnum) into Int_sequence;
     }
     printArray("Unsorted");
     sort(0,arraysize-1);
     delete Int_sequence;
     debugOutln("arraysize: {arraysize},  compares: {compare_count}, swap count: {swap_count}");
     return swap_count; 
}

}

public function run(args:String[]) {
    var qs = new qsort();
    cbm.runtest(args,qs)
}
