/*
 * @test
 * @run
 */
import java.lang.System;
class Foo {
    var text : String;
    public var func: function(): String = myfunc;
    public function myfunc(): String {text}
}
var foo = Foo { text: "Hello" };
System.out.println("foo.func()->{foo.func()}");
