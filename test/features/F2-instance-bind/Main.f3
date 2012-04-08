/* Feature test #2 -- simple variable binding
 * Demonstrates: unidirectional binding of local variables to expressions
 *               expressions are completely reevaluated when dependencies change
 *               dependencies are limited to variables named in the expression
 * @test
 * @run
 */

import java.lang.*;
import java.util.Date;

var x = 17;
var y = 2;

var z = bind x + y;
var max = bind Math.max(x, y);

System.out.println("{x} + {y} == {z}");
System.out.println("{x} max {y} == {max}");

x = 4;
System.out.println("{x} + {y} == {z}");
System.out.println("{x} max {y} == {max}");

y = 100;
System.out.println("{x} + {y} == {z}");
System.out.println("{x} max {y} == {max}");

x = 50;
System.out.println("{x} + {y} == {z}");
System.out.println("{x} max {y} == {max}");
