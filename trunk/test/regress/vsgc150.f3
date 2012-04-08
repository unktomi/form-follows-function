/*
 * Regression test: translation within if
 *
 * @test
 */

import java.lang.System; 

public class X { 
   var x: Integer = 0 
   on replace oldValue {System.out.println("X.replace {oldValue} with {x}");} 
} 

var x = X { 
x: 20 
}; 
