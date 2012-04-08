import java.lang.System;

class C {
  public var arr = [ "G", "F", "E", "D", "C", "B" , "A" ];
  public function getl(x:Integer):String {
   return arr[ x mod 7 ];
  }
}

class bind_member_select01 extends cbm {
var loop2size = 100000;
var y:Integer = 1;
var idx=0;
var j=0;
var i = 1;
var str = "";

override public function test() {
   var c = new C();
    while ( j <= 100 ) {
      while ( i <= loop2size ) {
        var x = bind c.getl(i);
        if( i mod 1000 == 0) { //should be 1000
            str = "{str}{x}";
        }//if
        i++;
    }//while
    j++;
  }//while
  debugOutln(str);
  return 0;
}
};

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var t = new bind_member_select01();
    cbm.runtest(args,t)
}

