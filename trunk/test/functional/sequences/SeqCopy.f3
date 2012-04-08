/* Test that sequence copying happens when it needs to.
 * (Hopefully will be lazy, but we can't test that unless we check
 * generated code or perform timing measurements.)
 *
 * @test
 * @run
 */

// Note there is a lot of silly-looking code duplication here.
// It would normally be better to use functions more.
// However, the point of this test is to make sure that
// sequences are copied when needed, so we don't want to
// change that (e.g. by calling a function) if we can avoid it.

import java.lang.System;
var arr1 : Integer [] = [];
function checksum1() : Integer {
  var sum : Integer = 0;
  for (i in [0 ..< sizeof arr1])
    sum += i*arr1[i];
  return sum;
}
def size1 = 100;
for (i in [0 ..< size1])
  insert i into arr1;
System.out.println("A.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
var arr2 : Integer [] = arr1;
function checksum2() : Integer {
  var sum : Integer = 0;
  for (i in [0 ..< sizeof arr2])
    sum += i*arr2[i];
  return sum;
}
function checksum(arr:Integer[]) : Integer {
  var sum : Integer = 0;
  for (i in [0 ..< sizeof arr])
    sum += i*arr[i];
  return sum;
}
arr1[5] = 9876;
delete arr1[56..67];
insert [98, 97, 96] before arr1[76];
System.out.println("B.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
System.out.println("B.arr2 length: {sizeof arr2} checksum: {checksum2()}.");
arr2[6] = 98765;
delete arr2[56..69];
insert [98, 97, 96] before arr2[23];
System.out.println("C.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
System.out.println("C.arr2 length: {sizeof arr2} checksum: {checksum2()}.");

function stuff(arr3: Integer[]) {
  System.out.println("D.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
  System.out.println("D.arr3 length: {sizeof arr3} checksum: {checksum(arr3)}.");
  delete arr1[90..99];
  arr1[89] = 876432;
  insert [98, 97, 96] before arr1[17];
  System.out.println("E.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
  System.out.println("E.arr3 length: {sizeof arr3} checksum: {checksum(arr3)}.");
  var arr4 = arr3;
  delete arr4[0..10];
  arr4[55] = 764321;
  insert 97 before arr4[10];
  System.out.println("F.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
  System.out.println("F.arr3 length: {sizeof arr3} checksum: {checksum(arr3)}.");  System.out.println("F.arr4 length: {sizeof arr4} checksum: {checksum(arr4)}.");
}
stuff(arr1);

System.out.println("Check slices.");
arr1 = [100..200];
arr1[60] = 1050;
// Take slice, then modify slice.
// Should be the same as: arr2 = [112..170]; arr2[60-12]=1050;
arr2 = arr1[12..70];
arr2[55] = 1051;
System.out.println("A.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
System.out.println("A.arr2 length: {sizeof arr2} checksum: {checksum2()}.");
// Take slice, then modify original.
arr1 = arr2[8..42];
arr2[30] = 1042;
System.out.println("A.arr1 length: {sizeof arr1} checksum: {checksum1()}.");
System.out.println("A.arr2 length: {sizeof arr2} checksum: {checksum2()}.");
