/**
 * Functional test:  Java Language Specification samples
 * @test
 * @run
 */

import java.lang.*;

function run( ) {
	//Evaluate Operands before Operation
	var divisor = 0;
	try {
		var i = 1 / (divisor * loseBig());
	} catch (e:Exception) {
		System.out.println(e);
	}

	//Evaluation Respects Parentheses and Precedence
	var d = 8e+307;
	System.out.println(4.0 * d * 0.5);
	System.out.println(2.0 * d);
}
function loseBig():Integer {
	throw new Exception("Shuffle off to Buffalo!");
}

