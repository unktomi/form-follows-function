/* Feature test #5 -- sequences
 * Demonstrates simple manipulation of sequences -- creation, extraction, insertion, deletion
 *
 * @test
 * @run
 */

import java.lang.System;

function put(test:String, x:Integer[]) {
  print(test); print(": "); println(x);
}

var x = [ 1..4 ];
System.out.println(x);    // 1, 2, 3, 4

x = [1, 2, 3];
System.out.println(x);    // 1, 2, 3

System.out.println(x[1]); // 2

System.out.println(sizeof x);

System.out.println(reverse x);
                          // 3

// @FIXME Currently throws NPE; awaiting clear spec on out-of-bounds access
// System.out.println(x[100]); // 0

insert 4 into x;
System.out.println(x);    // 1, 2, 3, 4

delete 2 from x;
System.out.println(x);    // 1, 3, 4

delete x[2];
System.out.println(x);    // 1, 3

x = [ 2..4 ];

delete x[10];
System.out.println(x);    // 2, 3, 4

// @NYI insert 1 as first into x;
// System.out.println(x);    // 1, 2, 3, 4

x = [ 1..5 ];
System.out.println(x);    // 1, 2, 3, 4, 5

x = [ x, 10 ];
System.out.println(x);    // 1, 2, 3, 4, 5, 10

var z = x[n|n > 3];
System.out.println(z);    // 4, 5, 10

// @NYI z = x[n | indexof n < 3 ] { x };
// System.out.println(z);    // 1, 2, 3

var y = for (n in x) { n*n };
                          // 1, 4, 9, 16, 25, 100
System.out.println(y);

put("reverse y", reverse y);

// Some slice tests:
put("y[0..5]", y[0..5]); // 1, 4, 9, 16, 25, 100
put("y[-1..6]", y[-1..6]); // 1, 4, 9, 16, 25, 100
put("y[2..5]", y[2..5]); // 9, 16, 25, 100
put("y[2..10]", y[2..10]); // 9, 16, 25, 100
put("y[0..2]", y[0..2]); // 1, 4, 9
put("y[-4..2]", y[-4..2]); // 1, 4, 9
put("y[2..2]", y[2..2]); // 9
put("y[2..1]", y[2..1]); // []

// Check some combinations of slices and reverse:
y = y[1..4];
put("y", y);       // 4, 9, 16, 25
put("y[-1..6]", y[-1..6]);// 4, 9, 16, 25
put("y[2..5]", y[2..5]); // 16, 25
put("y[0..1]", y[0..1]); // 4, 9
put("y[2..1]", y[2..1]);     // []
put("y[2..2]", y[2..2]); // 16
put("reverse y", reverse y); // 25, 16, 9, 4
put("(reverse y)[-1..6]", (reverse y)[-1..6]);
put("(reverse y)[2..5]", (reverse y)[2..5]);
put("(reverse y)[0..1]", (reverse y)[0..1]);
put("(reverse y)[2..1]", (reverse y)[2..1]);
put("(reverse y)[2..2]", (reverse y)[2..2]);

y = for (n in x) { n + 1 };
                          // 2, 3, 4, 5, 6
put("comprehension", y);

