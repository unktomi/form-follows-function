/*
 * @test
 * @run
 */

public class Foo {
    public var bar: Boolean = true on replace {
        java.lang.System.out.println("replaced bar with {bar}"); 
    }
}

function run() {
    var x = Foo{bar: false}
    var y = Foo{bar: true}
}

