/*
 * @test
 * @run/fail
 *
 * the trigger in M0 should fire just once
 */
mixin class M0 {
     var v: Integer = 5 on replace oldvalue {println("update A.v");};
}
mixin class M1 extends M0 {}
mixin class M2 extends M0, M1 {}
class C extends M0, M1, M2 {}

var x = C{};
x.v = 15;
