class boundblocks extends cbm {

def MAX=1000000;
var N = 1;
def bb1 = bind {
		def dbl = N + N;
		def add5 = 5;
		dbl + add5
};

def bb2 = bind {
        def hlf = N/2;
        def add1 = 1;
        hlf+1;
}

override public function test() {
  var ttl = 0;

  for ( i in [ 1 .. MAX ] ) {
   N = i;
   ttl += (bb1 + bb2);
  }
  debugOutln("ttl={ttl}");
  ttl
}
}

public function run(args:String[]) {
    var t = new boundblocks();
    cbm.runtest(args,t)
}
