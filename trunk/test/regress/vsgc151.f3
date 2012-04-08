/*
 * @test
 * @run
 */

import java.lang.System; 

public class T { 
  var x: Number = 0 
    on replace {System.out.println("x={x}");} 
  var y: Number = bind x 
    on replace {System.out.println("y={y}");} 
  init { 
    x = 20; 
  } 
} 

function run() {
  var t = T { 
    x: 100 
  }; 
}
