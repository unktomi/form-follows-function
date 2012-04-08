/*
 * Overriding values of mixin variables
 * without use of "override" keyword
 *
 * @test/compile-error
 */

mixin class A {
   var v: Integer = 5;
}

mixin class E extends A {
   function getE() { return v; }
}

class D extends E {
   var v = 55;
   function getD() { return v; }
}

var x=D{};
println(x.getD());
