/*
 * Test for unresolvable conflicts.
 * Super and Mixin declare the same variable using different types.
 *
 * @test/compile-error
 */

public mixin class Mixin {
    public var bar : Number;
}

class Super {
    public var bar : Number[];
}

class Mixee extends Super, Mixin {
    var far = bar;
}
