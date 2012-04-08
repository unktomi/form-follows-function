/*
 * Regression test: assign op, e.g., +=
 *
 * @test
 * @run
 */

import java.lang.System;

class Foo {
	var x : Integer;
	function doubleIt() { x *= 2 }
	function giveMeFive() { x += 5 }
	function loseOne() { x -= 1 }
	function oneInTen() { x /= 10 }
	function itMakesCents() { x = x mod 100 }
}

var fo = Foo { x: 1000 }

System.out.println(fo.giveMeFive());
System.out.println(fo.doubleIt());
System.out.println(fo.oneInTen());
System.out.println(fo.loseOne());
System.out.println(fo.loseOne());
System.out.println(fo.itMakesCents());

var seq = [10..20];
seq[3] += 1000;
seq[5] = seq[5] mod 10;
seq[0] /= 5;
seq[2] -= 10;

System.out.println(seq);

var y = 777;
System.out.println(y -= 3);
System.out.println(y = y mod 10);
System.out.println(y *= 3);
System.out.println(y += 3300);
