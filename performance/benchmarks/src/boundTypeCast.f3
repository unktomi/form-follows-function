/**
 * bind to explicit type cast, no access of bound variable.
 */

class  boundTypeCast extends cbm {
  def MAX=9000000;
  var checksum = MAX;
  var N = 0;
  function sumseq(ss:Integer[]):Integer { var ttl=0; for(n in ss) ttl += ss[n]; ttl; }

  override public function test():Number {
     var total:Number=0;
     var cnt=0;
     for ( a in [1..MAX ] ) {
            var bs = bind a as Object;
            cnt++;
	}
    total = cnt;
	debugOutln("");
	debugOutln("total: {total}  checksum: {checksum}");
    if( total != checksum )println("ERROR: total {total} did not match checksum {checksum}");
    debugOutln("count: {cnt}");
	total;
	}

};


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var bsr = new boundTypeCast();
    cbm.runtest(args,bsr)
}

