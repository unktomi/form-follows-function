/*
 * Testing the 'super' keyword in mixin function bodies.
 *
 * Use 'super' to access a variable declared in the parent class
 *
 * @test/fail
 */

mixin class Mixin {
    public var bar : String;
}

class Mixee extends Mixin {
    function foo() {
        var b = super.bar;
    }
}

