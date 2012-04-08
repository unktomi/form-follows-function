import java.lang.System;

/* With mixin build, 1000 seems like a trivial amount, but
 * with soma-master, this more easily measureable.
 * Increase to just 2000, mixin is 100 times faster.
 */
class bindloop extends cbm {
/* If y is script level, rather than local, this is slower. */
var loop2size = 2000; //should be 20000
var y:Integer = 1;

/*
 * Simple bind to variable with variable accessing(updating)
 */
override public function test() {
  debugOutln("Bind to and access global variable");
  for (j in [ 1..10]) {
    for ( i in [ 0 .. loop2size ] ) {
      var inc:Integer = i;
      var x:Integer = bind (y + inc);
      if( x mod 10 == 0) { //should be 1000
         y = y+1;
      }
    }
    debugOut("{j} ");
  }
  debugOutln("");
  return 0;
}
};
/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var t = new bindloop();
    cbm.runtest(args,t)
}

