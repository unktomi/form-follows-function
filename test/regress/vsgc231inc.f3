/*
 * Regression test: increment, decrement
 *
 * @test
 * @run
 */

import java.lang.System;

class Foo {
	var x : Integer;
	function inc() { ++x }
	function dec() { --x }
	function postInc() { x++ }
	function postDec() { x-- }
}

var fo = Foo { x: 10 }

System.out.println(fo.inc());
System.out.println(fo.x);
System.out.println(fo.dec());
System.out.println(fo.x);
System.out.println(fo.postInc());
System.out.println(fo.x);
System.out.println(fo.postDec());
System.out.println(fo.x);

var seq = [10..20];
System.out.println(++seq[2]);
System.out.println(--seq[4]);
System.out.println(seq[6]++);
System.out.println(seq[8]--);

System.out.println(seq);

var y = 99;
System.out.println(--y);
System.out.println(y);
System.out.println(++y);
System.out.println(y);
System.out.println(y++);
System.out.println(y);
System.out.println(y--);
System.out.println(y);

var z = 3.14159265358979;
System.out.println("{%7.5f --z}");
System.out.println("{%7.5f z}");
System.out.println("{%7.5f ++z}");
System.out.println("{%7.5f z}");
System.out.println("{%7.5f z++}");
System.out.println("{%7.5f z}");
System.out.println("{%7.5f z--}");
System.out.println("{%7.5f z}");
