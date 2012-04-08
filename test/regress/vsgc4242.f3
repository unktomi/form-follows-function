/*
 * Regression test: compiler err: this in init in mixin
 *
 * @test
 */

public mixin class SomeMixin {
   init { println("{this.getClass()}"); }
}

class SomeSubClass extends SomeMixin {}
