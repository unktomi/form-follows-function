/*
 * Regression test: unattributed null value causes NPE in lowering
 *
 * @test
 * @run
 */

function voidFunc():Void {}

function f1() {
  if (true) return null;
     voidFunc();
}

function f2() {
  if (true) return [];
     voidFunc();
}

function f3() {
  if (true) return Object{};
     voidFunc();
}

function f4() {
  if (true) return "";
     voidFunc();
}

function f5() {
  if (true) return 1 as Byte;
     voidFunc();
}

function f6() {
  if (true) return 1 as Character;
     voidFunc();
}

function f7() {
  if (true) return 1 as Short;
     voidFunc();
}

function f8() {
  if (true) return 1;
     voidFunc();
}

function f9() {
  if (true) return 1 as Number;
     voidFunc();
}

function f10() {
  if (true) return 1 as Double;
     voidFunc();
}

function f11() {
  if (true) return true;
     voidFunc();
}
