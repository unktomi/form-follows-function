/**
 * bind var to sequence range; access bound variable for sum
 */

class  boundSequenceRangeSum extends cbm {
  var checksum = 1.7592186E13;

//didn't matter if 'seq' was local or class for mixin or previous build
  var seq:Integer[] = [1..1000000];

  override public function test():Number {
     var cnt=0;
     var total:Number=0;
	  for ( a in [1..500 step 10] ) {
            var bs = bind seq[a..]; //bind to range in seq
            for(n in bs) total += bs[n]; //sum bound range
            cnt++;
            //debugOut("{total}..");
      }
	debugOutln("");
	debugOutln("total: {total}  checksum: {checksum}");
    if( total != checksum )println("ERROR: total {total} did not match checksums {checksum}");
    debugOutln("count: {cnt}");
	total;
	}

};


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var bsrs = new boundSequenceRangeSum();
    cbm.runtest(args,bsrs)
}

