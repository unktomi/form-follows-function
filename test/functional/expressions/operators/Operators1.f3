/**
 * Functional test:  Java Language Specification samples
 * @test
 * @run
 */

import java.lang.System;
import java.lang.Exception;
//Evaluate Left-Hand Operand First
function run( ) {
	var i = 2;
	var j = (i=3) * i;
	System.out.println(j);	 // prints 9

	var a = 9;
	a += (a = 3);
	System.out.println(a);
	var b = 9;
	b = b + (b = 3);
	System.out.println(b);

	var j1 = 1;
	try {
		var i1 = forgetIt() / (j1 = 2);
	} catch (e:Exception) {
		System.out.println(e);
		System.out.println("Now j1 = {j1}");
	}
}
function forgetIt():Integer {
	throw new Exception("I'm outta here!");
}

