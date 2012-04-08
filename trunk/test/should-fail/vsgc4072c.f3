/*
 * Regression test: Forward reference check overhaul
 * @compilearg -XDfwdRefError=false
 * @test/warning
 */

class A {
    var a1;
    var a2;
    var a3;
    var a4;
}

mixin class M {
    var m1;
}

class B extends A,M {
    var b1;
    override var a1 = m1; //this is a forward ref!
    override var a2 = m1; //this is a forward ref!
    override var a3 = m1; //this is a forward ref!
    override var a4 = m1; //this is a forward ref!
    override var m1 = b1; //this is a forward ref!
}

class C extends B,M {
    override var b1 = m1 //ok!
}
