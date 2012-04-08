/*
 * @test
 * @run
 */

var x = ["Sunday", "Monday" ];

var y = bind for (i in x) { i.toUpperCase() };

insert "Tuesday" into x;

println(x);
println(y);

insert "Wednesday" into x;

println(x);
println(y);

// at this point "x" and "y" differ in size!
insert ["Thursday", "Friday", "Saturday" ] into x;

println(x);
println(y);
