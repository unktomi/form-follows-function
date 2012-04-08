/* Feature test #27 -- default properties
 * Demonstrates use of default properties to simplify object literal syntax
 *
 * @test
 * @run
 */

class WithDefault {
    public default var contents:String[];
    public var notDefault:String[];
    override function toString() {
        "{contents.toString()} {notDefault.toString()}"
    }
}

def explicit = WithDefault {
    contents: ["a", "b", "c"]
}
println(explicit);

def implicit = WithDefault {
    ["a", "b", "c"]
}
println(implicit);

def varScoping = WithDefault {
    def scopeBoth = "both";
    [scopeBoth, "b", "c"]
    notDefault: [scopeBoth, "e", "f"]
}
println(varScoping);
