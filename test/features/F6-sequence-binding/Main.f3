/* Feature test #5 -- sequences
 * Demonstrates binding of sequences
 *
 * @test
 * @run
 *
 */

import java.lang.System;

var x = [1, 2, 3];
var y = 1;
var z = bind x[y];

System.out.println(z);        // 2

System.out.println(x[y]);     // 2

x[1] = 5;
System.out.println(x[y]);     // 5

y = 0;
System.out.println(z);        // 1

delete x[0];
System.out.println(z);        // 5

// @NYI insert 99 as first into x;
// System.out.println(z);        // 99

x[y] = 10;
System.out.println(z);        // 10

var n = 3;
var oneToN = bind [ 1..n ];
System.out.println(oneToN);  // 1, 2, 3

n = 5;
System.out.println(oneToN);  // 1, 2, 3, 4, 5

// @FIXME n = 0;
// @FIXME System.out.println(oneToN);  // empty
