/*
 * Testing cast of mixins with "as" operator.
 * Casting a parent to a child.
 *
 * @test
 * @run/fail
 */

mixin class M1 {}
mixin class M2 extends M1 {}

class Mixee1 extends M1 {}

var m1 = Mixee1 {};
var m2 : M2 = m1 as M2;

