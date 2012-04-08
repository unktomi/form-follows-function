/*
 * Regression test: support escape right curly brace in a string literal
 *
 * @test
 * @run
 */


import java.lang.*;

System.out.println("\}");
System.out.println("{ '\}' }");
System.out.println("{ "\}" }");
System.out.println("A block expression starts with a \{ and ends with a \}");
System.out.println("1 level\{{ "2 level\{{ "3 level has value = {100}" }\}" }\}");
System.out.println("1 level\{{ "2 level\{{ "3 level has value = {100}" }\}" }\}"); 

var i = "Hello";
var j = "There";
System.out.println("{ {i; j} }");
System.out.println("\{ {'{i} {j}'} \}");


