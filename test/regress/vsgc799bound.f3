/* Binding Overhaul test: general, including operators, sequences and conditionals
 *
 * @test
 * @run
 */

import java.lang.System;

var enableBindingOverhaul;

function xxxxx() {
var i = 22;
var pi = 3.14159265368979;
var seq = ['a', 'b', 'c', 'd', 'e'];
var low = 1;
var high = 9;
var by = 2;
var cond = false;

var bi = bind i;
var bipi = bind i * pi;
var bii = bind i + i;
var bi5 = bind i - 5;
var bseq = bind seq;
var bexplicit = bind [i, i, i, i];
var explicit =  [i, i, i, i];
var brange1 = bind [low..high];
var brange2 = bind [low..high step by];
var brange3 = bind [0..4 step by];
var brange4 = bind [low..<high];
var brange5 = bind [low..<high step by];
var brange6 = bind [0..<4 step by];
var bnot = bind not cond;
var bif = bind if (cond) "yo" else "beep";
var bifint = bind if (not cond) 1234 else 5678;


System.out.println(bi);
System.out.println(bipi);
System.out.println(bii);
System.out.println(bi5);
System.out.println(bseq);
System.out.println(bexplicit);
System.out.println(brange1);
System.out.println(brange2);
System.out.println(brange3);
System.out.println(brange4);
System.out.println(brange5);
System.out.println(brange6);
System.out.println(bif);
System.out.println(bifint);

i = 7;
delete seq[1];
low = 100;
high = 105;
by = 3;
cond = true;

System.out.println(bi);
System.out.println(bipi);
System.out.println(bii);
System.out.println(bi5);
System.out.println(bseq);
System.out.println(bexplicit);
System.out.println(brange1);
System.out.println(brange2);
System.out.println(brange3);
System.out.println(brange4);
System.out.println(brange5);
System.out.println(brange6);
System.out.println(bif);
System.out.println(bifint);
}

xxxxx()
