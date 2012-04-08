/*
 * Regression test: flatenning with sequence vars
 *
 * @test
 * @run
 */
import java.lang.System;
class Y {
   public function toString() { "Why" }
};
var ys = [Y {}, [Y {}]]; // this compiles
ys = [Y {}, ys]; // but this didn't 
System.out.println(ys)
