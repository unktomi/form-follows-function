/*
 * @test
 * @run
 */

import java.lang.System;

var bar = java.lang.Runnable {
    public function run():Void {
        var b = Bug{};
        b.foo = "works!";
        bug = b;
    }
}

var bug : Bug = null;
class Bug {
    var foo:String = "doesn't work.";
}
bar.run();
System.out.println("bug now {bug.foo}");
