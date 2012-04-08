/*
 * Regression test: Compiled-bind: strange behavior with block expression inside condition of a while containing local bind
 *
 * @test
 * @run
 */

function f1() {
  var i = 0;
  while ({var k = i + 1; i = k; k} < 100) {
     println("i: {i}");
     var l = bind i;
  }
}

function f2() {
  var i = 0;
  while ({i = i + 1; var k = i; k} < 100) {
     println("i: {i}");
     var l = bind i;
  }
}

f1();
f2();
