/*
 * Regression test: Infer delete trigger oldValue type from context.
 *
 * @test
 * @run
 */

import java.lang.System;

class Base {
    var foo = 1 on replace old {
        System.out.println("Base.foo={foo}, old={old}");
    }
    
    init {
        System.out.println("Base.init");
        foo = 2;
    }
}

var a = new Base; 
