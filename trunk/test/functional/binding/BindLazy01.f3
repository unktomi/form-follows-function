import java.lang.System;

/** A simple test of "bind lazy".
  * @test
  * @run
  */ 

var x : String = "a";
function twice(x:String) {
  System.out.println("twice({x}) called.");
  "{x}{x}"
}
var y : String = bind twice(x);  //k
System.out.println("x is {x}.");   //k
System.out.println("y is {y}.");   //k
x = "b";
System.out.println("x is {x}.");   //k
System.out.println("y is {y}.");   //k
var z = bind twice(y);
z;
System.out.println("x is {x}.");
System.out.println("y is {y}.");
System.out.println("z is {z}.");
x = "c";
z;
System.out.println("x is {x}.");
System.out.println("y is {y}.");
System.out.println("z is {z}.");
