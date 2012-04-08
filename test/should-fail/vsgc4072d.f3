/*
 * Regression test: Forward reference check overhaul
 * @compilearg -XDfwdRefError=false
 * @test/warning
 */

class A {
  var a1 = 1;
  var a2 = 2;
  var a3 = 3;
  var a4 = 3;
}

class B extends A {
  var b1 = 4;
  var b2 = 5;
  override var a1 = a2; //forward ref
  override var a2 = b1; //forward ref
}

class C extends B {
  var c1 = 5;
  override var a1 = a2; //forward ref
  override var a2 = b1; //forward ref
  override var a3 = c1; //forward ref
  override var b1 = c1; //forward ref
  override var b2 = a4; //ok!
}
