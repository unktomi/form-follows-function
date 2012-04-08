/*
 * Test bind both in module variables and class var.
 * Regression test against them smashing each other.
 * @test
 * @run
 */

import java.lang.System;

class Bar {
	var alpha : Integer = 3;
	var beta : Integer = bind alpha * 10;
	function foo(x : Integer, y : Integer) : Integer { x - y } 
}

var pass : Boolean = true;

var v1 : Integer = 0;
var v2 : Integer = bind v1 * 5;

var bb : Bar = new Bar;
pass = if (bb.alpha == 3) pass else false;
pass = if (bb.beta == 30) pass else false;
System.out.println("{pass} Default-thirty beta={bb.beta}");

v1 = 1;
pass = if (v2 == 5) pass else false;
System.out.println("{pass} v2-five ={v2}");

bb = Bar { alpha: 21 };
pass = if (bb.alpha == 21) pass else false;
pass = if (bb.beta == 210) pass else false;
System.out.println("{pass} Two-Ten beta={bb.beta}");

v1 = 9;
pass = if (v2 == 45) pass else false;
System.out.println("{pass} v2-forty-five ={v2}");

System.out.println(if (pass) "PASS" else "FAIL");
