import java.lang.System;

/**
 * Simple bind one var, sum, to one function. Not a bound function.
 */

class  binary_ops extends cbm {
    var checksum = -1256742498;
    var cnt = 0;

	override public function test():Number {
	var N1 = 1;
	var N2 = 1;
	var N3 = 1;
    var  sumtotal = bind N1 + N2 + N3;
    var  difftotal = bind (N1+N3) - N2;
    var  mulTotal = bind N1 * (N2-N3);
    var  divTotal = bind (N1+N3)/N2;
     var total = 0;
     for ( z in [ 1..200] ) {
       N3 = z;
	  for ( a in [1..499] ) {
         N2 = a;
		 for ( b in [ 500..999] ) {
            N1 = b;
			total += sumtotal+difftotal+mulTotal+divTotal;
            cnt++;
		 }
      }
    }
	debugOutln("total: {total}, checksum: {checksum}");
    if( total != checksum )println("ERROR: checksums did not match!");
    debugOutln("count: {cnt}");
	total;
	}

}


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var bbop = new binary_ops();
    cbm.runtest(args,bbop)
}
