/* Feature test #13 - script-level members
 * Demonstrates: script-level functions and vars
 * @test
 * @run
 */

import java.lang.System;

var a : Integer = 14;
function doublea() { a + a }

class Main {
    var b : Integer = 55;
}

var ah = new Main;

System.out.println("{ah.a},  {ah.b}");

Main.a = 3;
System.out.println("{ah.a}, twice: {ah.doublea()}, twice: {Main.doublea()}");

ah.b = 99;
System.out.println("{Main.a}, {ah.b}");

var tr = 77;

ah = Main {
    b: 71717
};

System.out.println("{Main.a},  {ah.b}");

