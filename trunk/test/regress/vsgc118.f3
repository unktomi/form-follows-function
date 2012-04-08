/*
 * Regression test: allow local vars declared in an object literal to be visible to the rest of the object literal
 *
 * @test
 * @run
 */

import java.lang.System;

abstract class X {
    public var a: Number = 1;
    public var b: Number = 2;
    public abstract function givemec() : Integer;
    public abstract function f(x : Number) : Number;
    public abstract function increment() : Void;
}

var x = X {
    var q = 100;
    override function givemec() : Integer { q * q }
    override function f(x : Number) { x * q }
    override function increment() : Void { ++q }
    a: q
    b: q + 10
}

System.out.println(x.a);
System.out.println(x.b);
System.out.println(x.givemec());
System.out.println(x.f(5));
x.increment();
System.out.println(x.f(5));


