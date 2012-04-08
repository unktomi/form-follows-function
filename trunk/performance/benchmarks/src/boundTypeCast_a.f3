/**
 * bind to explicit type cast, accessing  bound variable.
 */

class  boundTypeCast_a extends cbm {
  def MAX=900000;
  var checksum = MAX;
  var N = 0;
  function sumseq(ss:Integer[]):Integer { var ttl=0; for(n in ss) ttl += ss[n]; ttl; }

  override public function test():Number {
     var seq:String[];
     var total:Number=0;
     var cnt=0;
     for ( a in [1..MAX ] ) {
            var bs = bind a as Object;
            insert bs.toString() into seq;
            cnt++;
	}
    total = sizeof seq;
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
    var bsr = new boundTypeCast_a();
    cbm.runtest(args,bsr)
}

