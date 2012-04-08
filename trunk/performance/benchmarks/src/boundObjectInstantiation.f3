class Foo {
   var val: Integer;
   var x : Integer;
   init {
     val = x;
   }
   public function getValue():Integer{ var v = val; v }
}

class bOI extends cbm {
  var sum:Integer;
  def checksum = -1522072448;

  override public function test():Number {
    sum = 0;
    def MAX = 4000000;
    for( b in [ 1 .. MAX ] ) {
      var bi = bind     Foo  {   x: b }
      sum += bi.getValue();
    }
    debugOutln("checksum: {sum}");
    if( sum != checksum ) println("ERROR: checksum did not match! {sum} != {checksum}");
    sum * 1.0;
  }
}

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var boi = new bOI();
    cbm.runtest(args,boi)
}

