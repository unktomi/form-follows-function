/**
 * bind to unary_ops usage: +,-; class level variables.
 */

class  unary_ops extends cbm {
  var checksum = -2.49379226E9;
  var N = 0;
  var negateN = bind -N;

  override public function test():Number {
     var cnt=0;
	  var total:Number=0;
	 for ( o in [ 0..9] ) {
	  for ( a in [1..500] )
		 for ( b in [ 1..999] ) {
            N = b;
			total += negateN;
            cnt++;
		 }
		total;
	}
	debugOutln("");
	debugOutln("checksum: {total}");
    if( total != checksum )println("ERROR: total {total} did not match checksums {checksum}");
    debugOutln("count: {cnt}");
	total;
	}

}; //mathcalc


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var u = new unary_ops();
    cbm.runtest(args,u)
}
