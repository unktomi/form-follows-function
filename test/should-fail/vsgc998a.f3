/*
 * @test/compile-error
 */
import java.lang.*;
class Bar {
  function x() : Void {
    System.out.println("BAR");
  }
}
class Foo extends Object, Bar {
  function x() : Void { // Warning: Missing override
    System.out.println("Foo");
  }
  override function y() : Void { // Error: doesn't override.
    System.out.println("Foo");
  }
} 
