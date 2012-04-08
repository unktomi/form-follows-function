/*
 * Test if-then and if-then-else
 * @test
 * @run
 */

import java.lang.System;

var pass = true;
var x = 0;
var bool = false;
if (bool) x = 77;
pass = if (x == 0) pass else false;
System.out.println("{pass} Expect 0: {x}");
pass = if ((if (bool) "yo" else "") == "") pass else false;
System.out.println("{pass} Expect nothing: {if (bool) "yo" else ""}");
bool = true;
if (bool)  x = 77;
pass = if (x == 77) pass else false;
System.out.println("{pass} Expect 77: {x}");
pass = if ((if (bool) "yo" else "").equals("yo")) pass else false;
System.out.println("{pass} Expect yo: {if (bool) "yo" else []}");
var z = if (bool) 888 else 0;
pass = if (z == 888) pass else false;
System.out.println("{pass} Expect 888: {z}");
z = if (not bool) 888 else 0;
pass = if (z == 0) pass else false;
System.out.println("{pass} Expect 0: {z}");

System.out.println(if (pass) "Pass" else "FAIL!");
if (not pass) {
    System.out.println("FAILURE.")
}

