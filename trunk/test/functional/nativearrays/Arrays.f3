/*
 * @test
 * @run
 */

import java.lang.System;
function printIntArr(iarr: nativearray of Integer) {
  print("[");
  for (ia in iarr) {
    def i = indexof ia;
    if (i > 0)
      print(", ");
    print("{i}: {ia}");
  }
  print("]");
}

var ar1 : nativearray of Integer;

// POSSIBLY FUTURE: ar1 = new nativearray of Integer(10);
// ar1[2] = 12;
ar1 = [0, 0, 12, 0, 0, 0, 0, 0, 0, 0] as nativearray of Integer;

ar1[8] = ar1[2];
ar1[2] = 222;
ar1[0] = 10;
ar1[9] = ar1[8]*ar1[0];
System.out.print("ar1: "); printIntArr(ar1); System.out.println();
System.out.println("ar1.length: {ar1.length}");
System.out.println("sizeof ar1: {sizeof ar1}");

var ar2 : nativearray of nativearray of Integer;
ar2 = java.lang.reflect.Array.newInstance(java.lang.Integer.TYPE, 3, 4)
  as nativearray of nativearray of Integer;
for (i in [0..<3]) for (j in [0..<4]) ar2[i][j] = 100*i+10*j;
println("ar2.length: {ar2.length}");
for (i in [0..<3]) for (j in [0..<4]) println("ar2[{i}][{j}] = {ar2[i][j]}");
var ar2_2 = ar2[2];
println("ar2_2.length: {ar2_2.length}");
for (x in ar2_2) println("ar2_2[{indexof x}]={x}");

