/*
 * Regression test: returning assignment
 *
 * @test
 * @run
 */

import java.lang.System;

class Foo {
    var aa = 0;
    var bb = 0;
    function doit() {
       var x = aa = 55;
       System.out.println(x);
       System.out.println(aa);
       bb = 22;
    }
 }
var oo = new Foo;
var doo = oo.doit();
System.out.println(doo);
System.out.println(oo.bb);

