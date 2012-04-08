/**
 * Regression test: function values in a bound local variable
 *
 * @test
 * @run
 * 
 */

import java.lang.System;

// use intermediate local variable
function valPlusOneBROKEN(val: Integer): Integer {
    var answer = val + 1;
    return answer;
}

function valPlusOneFIXED(val: Integer): Integer {
    return val + 1;
}

System.out.println("BROKEN");

var x = 0;
var y = bind valPlusOneBROKEN(x);
System.out.println(y); // prints 1 OK
x = 1;
System.out.println(y); // prints 1, should print 2

System.out.println("FIXED");

var a = 0;
var b = bind valPlusOneFIXED(a);
System.out.println(b); // prints 1 OK
a = 1;
System.out.println(b); // prints 1 OK
