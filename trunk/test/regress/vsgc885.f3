/*
 * @test
 * @run
 */

import java.lang.System;

class Foo {
    public var attr = false on replace {
        java.lang.System.out.println("REPLACED with {attr}");
    }
}

class Moo {
    public var sattr : Boolean[] on replace {
        java.lang.System.out.println("REPLACED with {sattr}");
    }
}

System.out.print("1:"); Foo {}
System.out.print("2:"); Foo {attr: false}
System.out.print("3:"); Foo {attr: true}
var t = true;
var f = false;
System.out.print("4:"); Foo {attr: bind t}
System.out.print("5:"); Foo {attr: bind f}

// System.out.print("6:"); Moo {}
System.out.print("7:"); Moo {sattr: [ false ]}
System.out.print("8:"); Moo {sattr: [ true ]}
var tt = [true];
var e : Boolean[] = [];
System.out.print("9:"); Moo {sattr: bind tt}
// System.out.print("10:"); Moo {sattr: bind e}

