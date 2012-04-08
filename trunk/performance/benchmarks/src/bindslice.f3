/**
 * bind to plain old slice
 *
 * All these bind to slice tests are somewhere in the range of 200+ times faster
 * than the older bind implementation.
 *
 * They all run must faster with older implementations when bind is taken out
 * a la the note below in the test() method.
 */
import java.lang.System;

class bindslice extends cbm {
var loop2size = 25000;
var testseq:Integer[]=[0..loop2size]; //does not seem to matter if these are local or not.

var checksum=156275000;

override public function test() {
  var sum = 0;
    for ( i in [ 1 .. loop2size ] ) {
       var j:Integer = i+i;
       var x:Integer[] = bind  testseq[i..j]; //this is very slow in 'old' 1.3
	   sum += sizeof(x);

/* Take out bind and replace with below and
   this is relatively fast in 'old' 1.3 and 1.2.1 */
//       sum += sizeof ( testseq[i..jw] );

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
    var t = new bindslice();
    cbm.runtest(args,t)
}

