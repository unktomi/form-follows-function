/*
 * Regression test: instanceof implemented
 *
 * @test
 * @run
 */
import java.lang.System;

class A {
 var x = 3;
}

class B extends A {
  var y = 9;
}

class C {
  var z =7;
}

def a = new A;
def b = new B;
def c = new C;

System.out.println(if (a instanceof A) then "a instanceof A" else "FAIL: a instanceof A");
System.out.println(if (b instanceof A) then "b instanceof A" else "FAIL: b instanceof A");
System.out.println(if (b instanceof B) then "b instanceof B" else "FAIL: b instanceof B");
System.out.println(if (c instanceof C) then "c instanceof C" else "FAIL: c instanceof C");
System.out.println(if (not (a instanceof B)) then "not (a instanceof B)" else "FAIL: not (a instanceof B)");
