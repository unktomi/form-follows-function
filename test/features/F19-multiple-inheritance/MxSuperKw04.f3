/*
 * Testing the 'super' keyword in mixin function bodies.
 *
 * Use 'super' in a body of a function that is not declared 
 * in a parent mixin, but is declared in a parent Java class.
 *
 * @compilefirst MxSuperKw04Java.java
 * @test
 * @run
 */

mixin class Mixin {}

class Mixee extends MxSuperKw04Java, Mixin {
    override public function foo() : String {
        super.foo();
    }
}

function run() {
    var m = Mixee {};
    println(m.foo());
}

