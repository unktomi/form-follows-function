/*
 * Regression test: Extra forward-refrerence warning
 * @compilearg -XDfwdRefError=false
 * @test/warning
 */

var aNumber = 0.0;

class Foo {
    var aVar:Boolean;
    function aFunc(x:Number):Boolean {
        return x > 10.0;
    }
    var anotherVar1:Boolean;
    var anotherVar2:Boolean;
}

var f1:Foo = Foo {
    anotherVar1: bind f1.aVar
    anotherVar2: bind f1.aFunc(aNumber)
};

var f2:Foo = Foo {
    anotherVar1: f2.aVar //warning here
    anotherVar2: f2.aFunc(aNumber) //warning here
};
