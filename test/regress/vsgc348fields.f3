/*
 * Regression test: field access within a bind
 *
 * @test
 * @run
 */

import java.lang.System;

var out = bind System.out;
out.println("hi");
var zero = bind java.lang.Integer.MAX_VALUE - java.lang.Integer.MAX_VALUE;
out.println(zero);
