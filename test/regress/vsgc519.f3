/*
 * @test
 * @run
 */

import java.lang.System;

class Foo { 
  var x : Integer[] = [ 0 ]
    on replace { 
      System.out.println("x: {x.toString()}");
    };
  var y : Integer
    on replace { 
      System.out.println("y: {y}");
    };
} 

var v = Foo { x : [1, 2, 3] }; 
v.x = [4, 5, 6]; 
var w = Foo { y: 3 };
