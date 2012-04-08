/*
 * Test for unresolvable conflicts.
 * Mixee declares an override-incompatible function.
 *
 * @test/compile-error
 */

public mixin class Mixin {
    public abstract function foo() : Void;
}

public class Mixee extends Mixin {
    override public function foo() : String { "x" }
}
