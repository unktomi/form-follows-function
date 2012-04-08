import java.lang.System;

class bindif extends cbm {
var checksum=500010;
var loop2size = 100000;
var y:Integer = 1;
override public function test() {
  var sum = 0;
  for ( j in [ 1.. 10 ] )   {
    for ( i in [ 0 .. loop2size ] ) {     
      var x:Integer = bind if ( i mod 2 == 1 ) 0 else 1;
	   sum += x;
    }
  }
   debugOutln("sum: {sum} ");
	if(sum != checksum) println("ERROR: checksum did not match! Time may be invalid.");
  return 0;
}//test

};//class

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var t = new bindif();
    cbm.runtest(args,t)
}

