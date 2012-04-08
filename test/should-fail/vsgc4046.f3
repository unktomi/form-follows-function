/*
 * Regression test: Compiler crashes when having a try-catch block within bound function
 *
 * @test/compile-error
 */

bound function func (s: String): Number {
    try {
        return Number.parseFloat (s);
    } catch (e: java.lang.NumberFormatException) {
        return 0.0
    }
}
