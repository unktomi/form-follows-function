/*
 * @test
 * @run
 */
import java.lang.System;
class Bug {
    var foo:String;

    var bar = java.lang.Runnable {
        public function run():Void {
            foo = "Hello World";
        }
    };
}
var b : Bug = Bug { foo: "nada" };
System.out.println("foo->{b.foo}");
b.bar.run();
System.out.println("foo->{b.foo}");
