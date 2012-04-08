/*
 * Regression test: attribution fails to find var
 *
 * @test
 * @run
 */

import java.lang.System;

public mixin class Transform {
    public var af = 1234;
}

public abstract class TCanvasElement {
}

public abstract class TNode extends TCanvasElement, Transform {
}

public class TShape extends TNode {
    function foo() : Integer {
       af
    }
}

function run( ) {
    var tn = TShape{};
    System.out.println(tn.foo())
}

