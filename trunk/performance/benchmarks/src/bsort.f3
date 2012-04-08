/*
 * A sequence benchmark tests.
 * This has no binds and no triggers.
 */
import java.lang.System;

public class bsort extends cbm {
     var swaps = 0;
     var Int_sequence:Integer[];

    /*
     * Bubble Sort with sort and swap routines
     */
     function sort():Integer {
         for (i in [sizeof Int_sequence .. 0 step -1] ) {
            var swapped = false;
            for (j in [0..i-1] ) {
               if (Int_sequence[j] > Int_sequence[j+1]) {
                  swap(j,j+1);
                  swapped=true;
               }
            }
            if (not swapped) return 0;
        }
     	return 1;  //some return enforce
     }

	function swap( ii:Integer, ij:Integer)   {
	  var temp = Int_sequence[ii];
	  Int_sequence[ii] = Int_sequence[ij];
	  Int_sequence[ij] = temp;
	  ++swaps;
	}

   /*
    */
	override public function test():Number {
		var arraysize = 8000;
		var multiplier = 100;
		var modnum = 151;
		swaps = 0;
		for( j in [0..arraysize-1]) {insert ((j*multiplier + 1) mod modnum) into Int_sequence;}
		var ret = sort();
		debugOutln("Done sorting {arraysize} items. Used {swaps} swaps.");
        delete Int_sequence;
		return ret as Number;
	}
}


public function run(args:String[]) {
    var bs = new bsort();
    cbm.runtest(args,bs)
}



