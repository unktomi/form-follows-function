/**
 * Should-fail test: bind to a value that has been set during initialization.
 *
 * Note: 10/16/2009 RGF -- this now fails, as it should -- but this is a new behavior
 * and runtime has (in the past) depended on it
 *
 * @test
 * @run/fail
 */

class Foo {
    var a : Integer on replace {
        b = a;
    }
     
    var b : Integer;
}

var x = 10;
var f = Foo{b: bind x};
