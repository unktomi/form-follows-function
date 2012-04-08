/* Feature test #27 -- default properties
 * Demonstrates use of default properties to simplify object literal syntax
 *
 * @test
 * @run
 */

class SuperDefault {
    public default var contents:String[];
    override function toString() {
        contents.toString()
    }
}

class SubDefault extends SuperDefault {
}

class ShadowDefault extends SuperDefault {
    public default var shadow:String[];
    override function toString() {
        "{contents.toString()} {shadow.toString()}"
    }
}

def superD = SuperDefault {
    ["a", "b", "c"]
}
println(superD);

def subD = SubDefault {
    ["a", "b", "c"]
}
println(subD);

def shadowD = ShadowDefault {
    ["a", "b", "c"]
}
println(shadowD);
