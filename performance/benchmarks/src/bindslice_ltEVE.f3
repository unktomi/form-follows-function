/**
 * bind to slice determined by less then an end value expression, eg. [i..<10]
 */
import java.lang.System;

class bindslice_ltEVE extends cbm {
var loop2size = 25000;
var testseq:Integer[]=[0..loop2size]; //does not seem to matter if these are local or not.

var checksum=156262500;

override public function test() {
  var sum = 0;
    for ( i in [ 1 .. loop2size ] ) {
       var j:Integer = i+i;
       var x:Integer[] = bind  testseq[i..<j]; //this is very slow in 'old' 1.3
	   sum += sizeof(x);
    }
   debugOutln("sum: {sum} ");
	if(sum != checksum) println("ERROR: checksum did not match! Time may be invalid.");
  return 0;
}

};

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var t = new bindslice_ltEVE();
    cbm.runtest(args,t)
}

