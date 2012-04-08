/*
 * Regression test:  Internal Compiler error , 'void' type not allowed here
 *
 * @test
 */

var bug: String[] on replace oldVal {
    for (i in [0..10]) {
        var x = bind i; //comment this and this compiles ok
        var seq: Integer[];
        if (false) {
            seq = [1,2,3]
        } else {
            break;
        }
    }
}
