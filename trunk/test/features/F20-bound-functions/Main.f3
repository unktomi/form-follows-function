/* Feature test #20 -- bound function
 * Single level calls to bound functions, demonstrating updates.
 *
 * @test
 * @run
 */

import java.lang.*;

class Paia {
  var context : Integer;
  bound function flo(ralph : Integer) : Integer { ralph + context }
  var sum = bind flo(1000);
  var jeff = 1;
  bound function sophie(ray : Integer) : Integer { jeff * ray };
}

var x = 4;
var ba = Paia { context: 10 }
var lydia = bind ba.flo( ba.sophie(x) );
System.out.println(lydia);
ba.context = 90;
System.out.println(lydia);
x = 70;
System.out.println(lydia);
ba.jeff =10;
System.out.println(lydia);

System.out.println(ba.sum);
ba.context = 0;
System.out.println(ba.sum);

class Julie {
  var eris = "cotati";
  var olof;
  bound function donna() : String { "from: {eris} to: {olof}" }
}

var hvf = "happy valley";
var j = Julie { olof: bind hvf }
var john = bind j.donna();

System.out.println(john);
j.eris = "rohnert park";
System.out.println(john);
hvf = "alc";
System.out.println(john);

