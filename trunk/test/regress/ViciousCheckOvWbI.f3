/**
 * The vicious test superclass, controlled by ViciousEngineI
 *
 * @subtest
 */

public abstract class ViciousCheckOvWbI {

  public var n = 5 on replace { if (nCheck) childrenCheck() }
  var verbose = 0;
  var nCheck = false;
  var onrCheck = false;
  var what : String;

  var mirror : Integer[] = [];
  public var content : Integer[];
  public var children = bind content
     on replace oldValue[a..b] = newValue {
        if (verbose >= 2) println("{this.getClass()} {what}:{n}: [{a}..{b}] = {sizeof newValue}");
        if (oldValue != mirror) {
          println("ERROR {this.getClass()} {what}:{n}: mirror does not oldValue");
          println(mirror);
          println(oldValue);
        }
        mirror[a..b] = newValue;
        if (onrCheck) childrenCheck();
     };

  public abstract function expect() : Integer[];

  function childrenCheck() {
    var exp = expect();
    if (children != exp) {
      println("ERROR {this.getClass()} {what}:{n}: children does not match");
      println(children);
      println(exp);
    }
  }

  function check() {
    if (mirror != children) {
      println("ERROR {this.getClass()} {what}:{n}: mirror does not match children");
      println(mirror);
      println(children);
    }
    childrenCheck()
  }

  function test1() {
    what = "test1";
    for (i in [1 .. 10 ]) {
      n = i;
      check();
    }
  }

  function test2() {
    what = "test2";
    n = 7;
    check();
    n = 3;
    check();
    n = 9;
    check();
  }

  function test3() {
    what = "test3";
    n = 5;
    check();
    n = 4;
    check();
    n = 2;
    check();
  }

  package function test(t : Integer, nCheck : Boolean, onrCheck : Boolean, verbose : Integer) {
    this.nCheck = nCheck;
    this.onrCheck = onrCheck;
    this.verbose = verbose;
    if (t == 1) {
       test1();
       test2();
       test3();
    } else if (t == 2) {
       test1();
       test1();
    } else if (t == 3) {
       test3();
       test1();
       test3();
    } else if (t == 4) {
       test2();
       test1();
       test3();
    } else if (t == 5) {
       test3();
       test3();
       test1();
       test1();
    } else if (t == 6) {
       test2();
       test1();
    }
  }
}
