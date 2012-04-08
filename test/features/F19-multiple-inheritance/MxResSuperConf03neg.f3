/*
 * Test for the 'super' conflicts resolution.
 *
 * Both Super and Mixin1 declare the function foo().
 * Mixee overrides foo() and invokes from its body Mixin1.foo()
 *
 * @test/compile-error
 */

mixin class Mixin1 { function foo() : Integer { 1 } }
mixin class Mixin2 extends Mixin1 {}
class Super { function foo() : Integer { 2 } }

class Mixee extends Super, Mixin2 {
    override function foo() : Integer {
        Mixin1.foo();
    }
}

