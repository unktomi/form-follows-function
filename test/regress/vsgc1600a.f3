/*
 * @test
 * @run
 */

import java.lang.System;

var x : Integer[] = [ 1, 2, 3 ]
  on replace oldSlice[a..b] = newSlice {
        System.out.println("x: removed {oldSlice[a..b].toString()} and added {newSlice.toString()}"); };
var y : Integer[] = [ 4, 5, 6 ]
  on replace oldSlice[a..b] = newSlice {
        System.out.println("y: removed {oldSlice[a..b].toString()} and added {newSlice.toString()}"); };
var b : Boolean = true;

var z = bind if (b) then x else y
  on replace oldSlice[a..b] = newSlice {
        System.out.println("z: removed {oldSlice[a..b].toString()} and added {newSlice.toString()}"); };

insert 4 into x;
insert 7 into y;
System.out.println("b => false");
b = false;
System.out.println("inserting 5 into x");
insert 5 into x;
System.out.println("inserting 8 into y");
insert 8 into y;
System.out.println("b => true");
b = true;
System.out.println("Done");
