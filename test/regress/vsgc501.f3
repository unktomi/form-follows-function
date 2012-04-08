/**
 * Regression test: bound sequence updates
 *
 * @test
 * @run
 * 
 */

import java.lang.System;

function triple(n : Integer) {
	System.out.println("Tripling {n}");
	return 3 * n
}

var xs = [1, 2, 3];
var ys = bind for (x in xs) triple(x);
insert 4 into xs;
System.out.println(ys);


