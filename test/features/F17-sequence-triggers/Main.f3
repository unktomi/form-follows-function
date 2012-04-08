/*
 * Feature test #16 - sequence "on replace" (slice)
 * 
 * @test
 * @run
 */

import java.lang.System;

class Foo {
	var seq = [100..110] 
	   on replace oldValue[indx  .. lastIndex]=newElements
          { System.out.println("replaced {String.valueOf(oldValue)}[{indx}..{lastIndex}] by {String.valueOf(newElements)}")};
	function doit() {
		seq[3] = 88;
		insert 77 into seq;
		delete 109 from seq;
		delete seq[6];
		System.out.println(seq);
                seq[4..8] = seq[5..7];
		delete seq;
	}
};
var foo = new Foo;
System.out.println("doit - with slice triggers:");
foo.doit();
