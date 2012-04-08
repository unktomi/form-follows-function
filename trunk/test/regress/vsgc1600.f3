/*
 * @test
 * @run
 */

import java.lang.System;

class One {
    public var x: Integer[] on replace oldSlice[a..b] = newSlice {
        System.out.println("x: removed {oldSlice[a..b].toString()} and added {newSlice.toString()}");
    }
}

class Two {
    public var one: One;
    public var y: Integer[] = bind one.x on replace oldSlice[a..b] = newSlice {
        System.out.println("y: removed {oldSlice[a..b].toString()} and added {newSlice.toString()}");
    }
}

var two = Two {
    one: One {x: [1, 2, 3]}
}

System.out.println("Inserting 4");
insert 4 into two.one.x;
System.out.println("Inserting 5");
insert 5 into two.one.x;
System.out.println("Inserting 6");
insert 6 into two.one.x;
System.out.println("Done");
