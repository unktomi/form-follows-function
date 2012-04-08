/*
 * Regression test: gracefully handle x.f() when x is null
 *
 * @test
 * @run
 */

import java.lang.System;

class Foo {
   function fi() : Integer { 1234 }
   function fs() : String { "bonzo" }
   function fo() : Foo { Foo{} }
   function fas() : String[] { ["peter", "paul", "mary"] }
   var next : Foo;
   function getNext() : Foo { next }
   override function toString() : String { "Foo" }

  var str : java.lang.Object; 
  function toStr() : Void {
              str.toString();
  }
}

var f : Foo = Foo {};
System.out.println(f.fi());
System.out.println(f.next.fi());
System.out.println(f.getNext().fi());
System.out.println(f.fs());
System.out.println(f.next.fs());
System.out.println(f.getNext().fs());
System.out.println(f.fas());
System.out.println(f.next.fas());
System.out.println(f.getNext().fas());
System.out.println(f.fo());
System.out.println(f.next.fo());
System.out.println(f.getNext().fo());

System.out.println("---------------------");
f = null;
System.out.println(f.fi());
System.out.println(f.next.fi());
System.out.println(f.getNext().fi());
System.out.println(f.fs());
System.out.println(f.next.fs());
System.out.println(f.getNext().fs());
System.out.println(f.fas());
System.out.println(f.next.fas());
System.out.println(f.getNext().fas());
System.out.println(f.fo());
System.out.println(f.next.fo());
System.out.println(f.getNext().fo());

System.out.println("---------------------");
f = Foo { next: Foo {} }
System.out.println(f.fi());
System.out.println(f.next.fi());
System.out.println(f.getNext().fi());
System.out.println(f.fs());
System.out.println(f.next.fs());
System.out.println(f.getNext().fs());
System.out.println(f.fas());
System.out.println(f.next.fas());
System.out.println(f.getNext().fas());
System.out.println(f.fo());
System.out.println(f.next.fo());
System.out.println(f.getNext().fo());

System.out.println("---------------------");
var fo = Foo{}
fo.toStr();
System.out.println(fo.str);


