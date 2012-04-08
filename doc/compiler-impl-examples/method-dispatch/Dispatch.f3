class Base {
  attribute n : Integer;

  function foo(a : Integer) : Integer { a+n+1 }
  function moo(a : Integer) : Integer { a+n+2 }
  function bar(a : Integer) : Integer { a+n+3 }
}

class OtherBase {
  function bork(a : Integer) : Integer { a+4 }
}

class Dispatch extends Base, OtherBase {
  // override foo
  function foo(a : Integer) : Integer { a+n+5 }
}

var f : Dispatch;
var x : Integer;
var v1 = f.foo(3);
var v2 = bind f.foo(3);
var v3 = bind f.foo(x);
var v4 = f.foo(bind x);
var v5 = f.foo(bind x with inverse);

