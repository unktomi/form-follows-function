/**
 * regression test: Null-pointer-exception because of null vs [].
 * @test
 * @run
 */

import java.lang.System;

class Main1 {
    var attr: Integer[] on replace oldVal[a..b] = newVal {
        var j = 0;
        for (i in oldVal) {j++}
        System.out.println("loop attr oldVal {j} times.");
        j = 0;
        for (i in newVal) {j++}
        System.out.println("loop attr newVal {j} times.");
    }
}
var x1 = Main1{attr: [1, 2, 3]};

class Main2 {

    function func(val: Integer[]) {
        var j = 0;
        for (i in val) {j++};
        System.out.println("loop over null in x.func {j} times.");
    }
}

var x = Main2{};
x.func(null);

var counter = 0;
for (i in null) { counter++ };
System.out.println("loop over null {counter} times.");

counter = 0;
for (i in []) {}
System.out.println("loop over [] {counter} times.");

counter = 0;
for (i in 3) {counter++}
System.out.println("loop over single Integer {counter} times.");

counter = 0;
for (i in "3") {counter++}
System.out.println("loop over single String {counter} times.");
