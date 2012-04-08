/*
 * @test
 * @run
 */
 
class Foo {
    var bar1 on replace {
        java.lang.System.out.println("bar1 replace={bar1}");
    }

    var bar2:String on replace {
        java.lang.System.out.println("bar2 replace={bar2}");
    }
}

var foo1 = Foo { bar1:"x" };
var foo2 = Foo { bar2:"y" };
