/*
 * Testing the 'super' keyword in mixin function bodies.
 *
 * Use 'super' in a body of a function that is not declared in a parent mixin.
 *
 * @test/fail
 */

mixin class Mixin {}

class Mixee extends Mixin {
    public function foo() {
        super.foo();
    }
}

