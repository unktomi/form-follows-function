/*
 * @test/compile-error
 */

import java.lang.*;
class One extends Foo, Bar {
        function x():java.lang.Integer {
                System.out.println("One");
                23
        }
}
class Bar {
        function x():java.lang.Integer {
                System.out.println("Bar");
                34
        }
}
class Foo extends Bar, One {
        function x():java.lang.Integer {
                System.out.println("Foo");
                12
        }
}
