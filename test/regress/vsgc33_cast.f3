/*
 * Regression test: cast
 *
 * @test
 * @run
 */

import java.lang.System;

var x : java.lang.Object = null;

x  = System.out;
var po : java.io.PrintStream = x as java.io.PrintStream;
po.println(x.getClass());

x = "Howdy";
var str : String = x as String;
po.println(str);
po.println(str.substring(3));
