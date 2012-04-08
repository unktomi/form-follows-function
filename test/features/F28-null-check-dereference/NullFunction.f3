/* Feature test #27 -- recursive null data structure
 * Demonstrates use of the null check dereference operator (!.)
 *
 * @test
 * @run/ignore-std-error
 */
class NullFunction {
    public var nullVar:NullFunction;
    public function nullFunction():Object {return "function reached"}
}

def nf:NullFunction = NullFunction {};

println(nf.nullVar.nullFunction());
println(nf!.nullVar.nullFunction());
println(nf!.nullVar!.nullFunction()); // this one should fail
