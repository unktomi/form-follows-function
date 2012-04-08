/*
 * Test for function declarations
 *
 * foo() is declared in a parent Java interface being extended by a mixin.
 * Use the 'override' keyword when declaring this function in a mixin.
 *
 * @compilefirst MxFuncDecl03Java.java
 * @test
 * @run
 */

mixin class Mixin extends MxFuncDecl03Java {
    override public function foo() : String { "Mx" }
}

class Mixee extends Mixin {}

function run() {
    var m = Mixee{};
    println(m.foo());
}

