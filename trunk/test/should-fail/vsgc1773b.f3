/*
 * Regression test: Problem with vars being accessed before declared
 * @compilearg -XDfwdRefError=false
 * @test/warning
 */

var u = w.a; //not allowed - w.a is a qualified forward ref where w is not an allowed forward reference
var k = q.a; //not allowed - q.a is a qualified forward ref, where q is an allowed forward reference, but where both usage and decl occur in the same context
var v = bind z.a; //allowed - z.a is a qualified forward ref, where z is an allowed forward reference occuring in a different scope
var l = bind w.a; //allowed - w.a is a qualified forward ref, where w is an allowed forward reference occuring in a different scope
var w = Foo{a : 1}
def q = new Foo();
def z = Foo{a : 2} 

class Foo {
   var a;
}


