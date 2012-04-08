import java.lang.System;

class bindif_seq extends cbm {
var loop2size = 100000;
var testseq:Integer[]=[0..loop2size]; //does not seem to matter if these are local or not.

var checksum=417203966;

override public function test() {
  var sum = 0;
  for ( j in [ 1.. 25 ] )   {
    for ( i in [ 0 .. loop2size ] ) {
      var x:Integer[] = bind if ( j<i ) [j..i] else [i..j];
	   sum += sizeof(x);
    }
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
    var t = new bindif_seq();
    cbm.runtest(args,t)
}

