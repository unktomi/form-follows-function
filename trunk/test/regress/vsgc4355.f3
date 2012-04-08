/*
 * @test
 * @run
 */

class bb {
     var jjpriv1: Boolean;
     var jjpriv2: Boolean;
}


class sub extends bb {
    function fred() {
        jjpriv2;
    }
}

var obj = bb{};
obj.jjpriv2; // this works ok
obj.jjpriv1; // this works ok

var obj1 = sub{};

obj1.jjpriv2; // this works ok
obj1.jjpriv1; // this javadumps

println("Done.");
