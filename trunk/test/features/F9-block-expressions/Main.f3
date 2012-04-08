/* Feature test #9 -- block expressions
 * @test
 * @run
 */

import java.lang.*;

var v1 = {var v0 = 15;
 if (v0>3) {v0+10} else v0+20 };

System.out.println("v1: {v1}");
function f1 (x : Integer) : Integer
{
  var ten : Integer = 10;
  return x+ten;
}
System.out.println("f1(1): {f1(1)}");
function f2 (x : Integer) : Integer
{
  var ten : Integer = 10;
  x+ten; // final semi-colon is optional.
}
System.out.println("f2(1): {f2(1)}");
function f3 (x : Integer) : Integer
{
  if (x > 5) {
    var two : Integer = 2;
    x+two
  } else
    x+10
}
System.out.println("f3(1): {f3(1)}");
System.out.println("f3(7): {f3(8)}");

System.out.println(
  {var x : Integer = f3(10);
   { var y = 2 * x;
     "x: {x} y: {y} x+y: {x+y}"}});

System.out.println("x+y: {
  ({var x : Integer = f3(11); 100+x })
  + ({var x : Integer = f3(12); 100+x })}");
function f4() {
  {  
    var x = "f4-nested ";
    System.out.print(x);
  };
  {
    var x  : Integer = 222;
    System.out.println(x);
  };
}
f4();
