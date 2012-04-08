/* Feature test #12 - bidirectional var binding
 * Demonstrates: bidirectional binding (sequences)
 * @test
 * @run
 */


var aseq : Integer[] = [10, 11, 12, 13]
    on replace oldValue[indx  .. lastIndex]=newElements
    { print("on replace aseq bounds: {indx}..{lastIndex} -> "); println(aseq) };

var bseq : Integer[] = bind aseq with inverse
    on replace oldValue[indx  .. lastIndex]=newElements
    { print("on replace bseq bounds: {indx}..{lastIndex} -> "); println(bseq) };

insert 101 into aseq;
insert 102 into bseq;
aseq[2]=22;
bseq[1]=21;
print("aseq "); println(aseq);
print("bseq "); println(bseq);

