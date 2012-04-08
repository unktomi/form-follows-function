/*
 * @test
 * @run/pass
 *
 */

import java.lang.System;

class Node {
  var bounds : Integer;
}

class Foo {
   var focusBounds: Integer = bind focusedNode.bounds;
   var bidirectionalFocusBounds: Integer = bind focusedNode.bounds with inverse;
   var focusedNode : Node;
}

var n1 = Node { bounds: 9 }
var n2 = Node { bounds: 1 }
var f = Foo {focusedNode: n1 }

System.out.println(f.focusBounds);
System.out.println(f.bidirectionalFocusBounds);
n1.bounds = 99;
System.out.println(f.focusBounds);
System.out.println(f.bidirectionalFocusBounds);
n1.bounds = 999;
System.out.println(f.focusBounds);
System.out.println(f.bidirectionalFocusBounds);
f.focusedNode = n2;
System.out.println(f.focusBounds);
System.out.println(f.bidirectionalFocusBounds);
n2.bounds = 100;
System.out.println(f.focusBounds); 
System.out.println(f.bidirectionalFocusBounds);
