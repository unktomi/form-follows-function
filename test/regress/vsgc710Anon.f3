/**
 * Test general init processing in advance of full 710 implementation
 * @test
 * @run
 */

import java.lang.System;
import java.util.BitSet;

class A extends BitSet {
   var ai = 1;
   function afi() : Integer { 3 }
}

class B extends A {
   var bi = 1;
   function bfi() : Integer { 3 }
}

abstract class C extends B {
   var ci = 1;
   function cfi() : Integer { 3 }
   abstract function show() : Void;
}

var x = C {
   function dfi() : Integer { 3 }
   override function show() : Void {
	System.out.println( dfi() );
   }
};
System.out.println( x.ai );
System.out.println( x.afi() );
System.out.println( x.bi );
System.out.println( x.bfi() );
System.out.println( x.ci );
System.out.println( x.cfi() );
x.show();

class E extends C {
   var ei = 1;
   function efi() : Integer { 3 }
   override function show() : Void {System.out.println( cardinality() );} 
}
var y = new E;
System.out.println( y.ai );
System.out.println( y.afi() );
System.out.println( y.bi );
System.out.println( y.bfi() );
System.out.println( y.ci );
System.out.println( y.cfi() );
System.out.println( y.ei );
System.out.println( y.efi() );
y.flip(9);
y.show();
