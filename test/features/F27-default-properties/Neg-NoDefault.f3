/* Feature test #27 -- duplicate defaults
 * There should only be one default allowed per class declaration
 *
 * @test/compile-error
 */

class NoDefault {
    public var contents:String[];
}

def implicit = NoDefault {
    ["a", "b", "c"]
}
