/*
 * Test for unresolvable conflicts.
 * Mixee overrides a variable using another type.
 *
 * Note: currently the compilation fails with the wrong message:
 *       "An override variable cannot be given a type
 *        as that comes from the variable you override."
 *       When the message is fixed - the *.EXPECTED file should be corrected.
 *
 * @test/compile-error
 */

public mixin class Mixin {
    public var bar : Integer = 1;
}

public class Mixee extends Mixin {
    override public var bar : String;
}
