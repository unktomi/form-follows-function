/* Feature test #20 -- bound functions that take booleans
 *
 * @test
 * @run
 */

public class BoundBooleans {

bound function bf1( b: Boolean ):Boolean {    return b;}

bound function bf2( b: Boolean, i:Integer ):Boolean {
    return (b and i==0);
}


function bTest(b:Boolean) {
 if(not b) println("fail")
// else println("success");
}

//*
function testbf1() {
  var b = true;
  var bv1 = bind bf1(b);
  bTest(bv1);
  b=false;
  bTest(bv1==false);
}
//*/
function testbf2() {
 var b = true;
 var j = 0;
 var bv2=bind bf2(b,j);
 bTest(bv2);
 b=false;
 bTest(not bv2);
 b=true; j=1;
 bTest(not bv2);
 j=0;
 bTest(bv2);
}


} //class


public function run(args:String[]) {
  var BB = new BoundBooleans();
  BB.testbf1();
  BB.testbf2();
}


