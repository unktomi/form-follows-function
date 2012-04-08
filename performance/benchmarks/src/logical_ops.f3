/**
 * bind to logical_ops usage: +,-; class level variables.
 */

class  logical_ops extends cbm {
  var checksum = 1.24461363E9;
  var T1 = true;
  var T2 = true;
  var F1 = false;
  var LO = bind (T1 and T2 and not F1);
  function isOdd(N:Integer) { (N mod 2 == 0) }

  override public function test():Number {
     var cnt=0;
	  var total:Number=0;
	 for ( o in [ 0..9] ) {
	  for ( a in [1..500] )
		 for ( b in [ 1..999] ) {
           T1 = isOdd(b);
           if(LO)
             total += b;
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

};


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var lo = new logical_ops();
    cbm.runtest(args,lo)
}
