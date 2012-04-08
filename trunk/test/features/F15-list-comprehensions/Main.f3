/*
 * Feature test #15 - list comprehensions
* 
 * @test
 * @run
 */

var nums = [5, 7, 3, 9];
var numsb = bind nums;
var strs = ["hi", "yo"];
var res = for (a in nums) { a + 1 };
var s = for (a in nums where a < 6) {"({ a }) " };
var db = for (x in strs, y in strs)  {"{indexof x}.{indexof y}:{ x }{ y }-- " };
var wh = for (x in [1..20] where x < 12, y in [100, 200, 400]  where x*100 >= y) { y + x };
for (i in [100..110]) { println("...{ i }"); };
println(nums);
println(strs);
println(res);
println(s);
println(db);
println(wh);

var xs = ["a","b","c"];
var ys = bind for (x in xs) { "<{x}>" };
println("ys:{for (y in ys) " {y}"}");
xs[1]="w";
println("ys:{for (y in ys) " {y}"}");
insert "v" before xs[1];
println("ys:{for (y in ys) " {y}"}");

var xis = [3,4,5,6];
var yis = bind for (x in xis) { "<{x}>" };
println("yis:{for (y in yis) " {y}"}");
xis[2]=9;
println("yis:{for (y in yis) " {y}"}");
insert 7 before xis[1];
println("yis:{for (y in yis) " {y}"}");
