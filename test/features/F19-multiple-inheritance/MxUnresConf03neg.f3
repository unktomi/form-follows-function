/*
 * Test for unresolvable conflicts.
 * Mixin1 and Mixin2 declare the same variable using different types.
 *
 * @test/compile-error
 */

public mixin class Mixin1 {
    public var bar : Boolean;
}

public mixin class Mixin2 {
    public var bar : Duration;
}

class Mixee extends Mixin1, Mixin2 {
    var far = bar;
}
