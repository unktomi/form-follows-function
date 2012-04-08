/* Bound functions that take Duration type
 *
 * @test
 * @run
 */

public class boundFunction01 {
 var H= 0h;
 var M= 0m;

bound function bf1( h:Duration, m:Duration ):String {
  return "{h.toHours() as Integer}:{m.toMinutes() as Integer}" }

function bTest(s1:String, s2:String) {
 if( s1.compareTo(s2) != 0 ) println("FAIL: {s1} != {s2}")
}


function testbf1() {
  var h:Duration = 4h;
  var m:Duration = 30m;
  var t = bind bf1(h,m);

  bTest(t,"4:30");
  h=6h;
  m=45m;
  bTest(t,"6:45");
}



function testbf2() {
 var HRS:Duration[] = [ 1h,2h,3h,4h,5h,6h ] ;
 var MNT:Duration[] = [ 0m,15m,30m,45m];
 var t = bind bf1(H,M);
 for ( h in HRS)
   for ( m in MNT) {
     H=h;
     M=m;
     bTest(t,"{h.toHours() as Integer}:{m.toMinutes() as Integer}");
   }
}

} //class

public function run(args:String[]) {
  var B = new boundFunction01();
  B.testbf1();
  B.testbf2();
}
