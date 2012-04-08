/* Feature test #27 -- recursive null data structure
 * Demonstrates use of the null check dereference operator (!.)
 *
 * @test
 * @run/ignore-std-error
 */
class RecursiveNull {
    public var nullVar:RecursiveNull;
}

def rn:RecursiveNull = RecursiveNull {};

println(rn.nullVar.nullVar);
println(rn!.nullVar.nullVar);
println(rn!.nullVar!.nullVar); // this one should fail
