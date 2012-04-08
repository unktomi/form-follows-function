/**
 * regression test: Null-pointer-exception because of null vs [].
 * @test
 * @run
 */
import java.lang.System;
var foo: Integer[] = null;
var bar = if (foo == null) null else for (i in foo) i;
System.out.println("foo [{foo}]");
class Test {
    public function foo() {
        return [1, 2, 3];
    }
}
var x = Test {};
var z = if (true) null else x.foo();
System.out.println("z [{z}]");
