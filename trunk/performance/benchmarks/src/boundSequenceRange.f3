/**
 * bind to sequence range with access to single element of bound var
 */

class  boundSequenceRange extends cbm {
  var checksum = 1.25009126E9;
  var N = 0;
  var negateN = bind N;
  var seq:Integer[] = [1 ..1000000];
  function sumseq(ss:Integer[]):Integer { var ttl=0; for(n in ss) ttl += ss[n]; ttl; }

  override public function test():Number {
     var cnt=0;
     var total:Number=0;
	  for ( a in [0..50000 step 2] ) {
            var bs = bind seq[a..];
			total += bs[a]; //access only 1 element
            cnt++;
            total;
      }
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
    var bsr = new boundSequenceRange();
    cbm.runtest(args,bsr)
}

