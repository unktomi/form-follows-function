/*
 * Testing the 'super' keyword in mixin function bodies.
 *
 * Use 'super' keyword to access the parent implementation 
 * of the overridden function that is abstract in a mixin
 *
 * @test/fail
 */

mixin class Mixin {
    public abstract function foo();
}

class Mixee extends Mixin {
    override public function foo() {
        super.foo();
    }
}

