/* Binding Overhaul test: select
 *
 * @test
 * @run
 */

import java.lang.System;

var enableBindingOverhaul;

class Foo {
  var i = 1234;
  var b = true;
  var d = 3.1415926;
  var s = "Dirt";
  var q = [new java.util.ArrayList, 44, "Gezz"];
}

function xxxxx() {
var oo = new Foo;
var nn = Foo { i: 9 b: false d: 5.5 s: "Mud" q: [5.02]}
var p : Foo;

var bi = bind p.i;
var bb = bind p.b;
var bd = bind p.d;
var bs = bind p.s;
var bq = bind p.q;

System.out.println(bi);
System.out.println(bb);
System.out.println(bd);
System.out.println(bs);
System.out.println(bq);

p = oo;

System.out.println(bi);
System.out.println(bb);
System.out.println(bd);
System.out.println(bs);
System.out.println(bq);

p = nn;

System.out.println(bi);
System.out.println(bb);
System.out.println(bd);
System.out.println(bs);
System.out.println(bq);

p = null;

System.out.println(bi);
System.out.println(bb);
System.out.println(bd);
System.out.println(bs);
System.out.println(bq);

p = oo;
p.i = 77;
p.b = false;
p.d = 0.0001;
p.s = "Rock";
p.q = [false, "Falsehood"];

System.out.println(bi);
System.out.println(bb);
System.out.println(bd);
System.out.println(bs);
System.out.println(bq);
}

xxxxx();
