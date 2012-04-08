import java.lang.System;

/*
 * @test
 * @run 
 */

class Foo {
    public var i : Integer
        on replace ov { System.out.println("i {ov} => {i} init: {isInitialized(i)}"); };
    public var b : Boolean
        on replace ov { System.out.println("b {ov} => {b} init: {isInitialized(b)}"); };
    public var n : Number
        on replace ov { System.out.println("n {ov} => {n} init: {isInitialized(n)}"); };
    public var s : String
        on replace ov { System.out.println("s /{ov}/ => /{s}/ init: {isInitialized(s)}"); };
    public var q : Integer[]
        on replace ov { System.out.println("q /{ov}/ => /{q}/ init: {isInitialized(q)}"); };

    public function printAll() {
        System.out.println("{i} {isInitialized(i)} {b} {isInitialized(b)} {n} {isInitialized(n)} /{s}/ {isInitialized(s)} /{q}/ {isInitialized(q)}");
    }
}

System.out.println("all implicit defaults");
var f = Foo { };
f.printAll();
f.i = 0;
f.b = false;
f.n = 0;
f.s = "";
f.q = [];
f.printAll();

f.i = 10;
f.b = true;
f.n = 10;
f.s = "blah";
f.q = [1];
f.printAll();

System.out.println("all explicit defaults");
f = Foo { i : 0, b: false, n: 0, s: "", q: [] };
f.printAll();

f.i = 0;
f.b = false;
f.n = 0;
f.s = "";
f.q = [];
f.printAll();

f.i = 10;
f.b = true;
f.n = 10;
f.s = "blah";
f.q = [1];
f.printAll();

System.out.println("all nondefaults");
f = Foo { i : 3, b: true, n: 1.0, s : "yada", q : [1] };
f.printAll();

f.i = 3;
f.b = true;
f.n = 1.0;
f.s = "yada";
f.q = [1];
f.printAll();
