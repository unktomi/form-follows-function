/* Feature test #12 - bidirectional var binding
 * Demonstrates: bidirectional binding (non-sequences)
 * @test
 * @run
 */

class AttHold {
    var a : Integer = 14;
    var b : Integer = bind a with inverse;
}
var label = "far";
var boundLabel = bind label with inverse;

println("{label} == {boundLabel}");

label = "near";
println("{label} == {boundLabel}");

boundLabel = "there";
println("{label} == {boundLabel}");

var ah = new AttHold;

println("{ah.a} == {ah.b}");

ah.a = 3;
println("{ah.a} == {ah.b}");

ah.b = 99;
println("{ah.a} == {ah.b}");

var tr = 77;

ah = AttHold {
    a: bind tr with inverse
};

println("{ah.a} == {tr}");

ah.a = 6;
println("{ah.a} == {tr}");

tr = 11111;
println("{ah.a} == {tr}");
