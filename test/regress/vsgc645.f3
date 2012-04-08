/**
 * regression test: Compilation errors with slices as r-values
 * @test
 * @run
 */

import java.lang.System;

var v : Integer[]= [1,2,3];
var w = v[1..2] = [4,5];
System.out.println("slice ->{for (x in w) " {x}"}");
v[1..2] = [6,7];
