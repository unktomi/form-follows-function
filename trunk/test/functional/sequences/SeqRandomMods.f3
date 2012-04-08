/*
 * Test a medium-large pseudo-random set of sequence modifications.
 * @test
 * @run
 */
import java.lang.System;
var arr : Integer [] = [];
function modulo (i: Integer, j: Integer) : Integer {
  def m = i mod j;
  if (m < 0) m + j else m
}
function modulo (i: Integer) : Integer {
  modulo(i, sizeof arr)
}
function printarr() : Void {
  System.out.print("arr: [");
  for (i in [0 ..< sizeof arr]) {
    if (i mod 10 == 0 and i > 0) {
      System.out.println();
      System.out.print(i); System.out.print(": ");
    }
    else
      System.out.print(" ");
    System.out.print(arr[i]);
  }
  System.out.println("]");
}
function checksum() : Integer {
  var sum : Integer = 0;
  for (i in [0 ..< sizeof(arr)])
    sum += i*arr[i];
  return sum;
}
// def startTime = System.nanoTime(); // DEBUGGING
def size = 1000;
def iterations = 10*size;
for (i in [0 ..< size])
  insert i into arr;
for (i in [0 ..< iterations]) {
  // DEBUGGING: printarr();
  var j : Integer = i * 99907;
  def k : Integer = j * 79943;
  def op = modulo(j, 7);
  var val = 987 * arr[modulo(k)];
  // DEBUGGING: System.out.println("i: {i} arr len={sizeof arr} checksum:{checksum()} val:{val} j:{j}->{modulo(j)} op:{op}");
  j = modulo(j);
  if (op == 0) // replace single
    arr[j] = val
  else if (op == 1) { // insert single before
   insert val before arr[j]
  }
  else if (op == 2) // insert single after
   insert val after arr[j-1]
  else if (op == 3)
   delete arr[j..< modulo(k)]
  else if (op == 4) { // delete single
   //System.out.println("delete {j}->{modulo(j)}");
   delete arr[j];
  }
  else if (op == 5 or op == 6) { // multiple insert or replace
    def n = modulo(k,333);
    var tmp : Integer [] = [];
    for (i2 in [0 ..< n])
       insert i2*k into tmp;
    if (op == 5)
      insert tmp before arr[j]
    else
      arr[j..< modulo(k)] = tmp;
  }
}
var sum = checksum();
// System.out.println("Time used: {(System.nanoTime()-startTime)*0.000001}ms."); // DEBUGGING
System.out.println("Final array length {sizeof arr} check-sum: {sum}.");
