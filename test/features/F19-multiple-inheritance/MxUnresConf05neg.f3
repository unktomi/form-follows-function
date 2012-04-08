/*
 * Test for unresolvable conflicts.
 * Super and Mixin declare an override-incompatible function.
 *
 * @test/compile-error
 */

public mixin class Mixin {
    public function foo(arg : Integer) : String { "" }
}

class Super {
    public function foo(arg : Integer) : Integer { 0 }
}

class Mixee extends Super, Mixin {}
