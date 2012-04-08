/* bound functions
 *
 * Simple sequence argument handling in bound functions taking boolean sequences.
 * @test
 * @run
 */


function checkForXORSequences( b1:Boolean[], b2:Boolean[]):Boolean {
  if(sizeof b1 != sizeof b2) {println("different sizes"); return false; }
  var ret = true;
  for ( i in [ 0..(sizeof b1-1)] )
    if(b1[i]==b2[i])
      ret = false;
  return ret;
}

bound function XOR(B1:Boolean[], B2:Boolean[]):Boolean {
  return checkForXORSequences(B1,B2);
 }

var seq1 = [ true, true,  true ];
var seq5 = [ false, false,false];

var seq2 = [ true, true,  false];
var seq6 = [ false, false,true ];

var seq3 = [ true, false, false];
var seq7 = [ false, true,true ];

var seq4 = [ true, false, true ];
var seq8 = [ false,true,  false];

var A = seq1;
var B = seq5;
var bs = bind XOR(A,B);
println(bs); //true
B=seq6;
println(bs); //false
A=seq2;
println(bs); //true

B=seq7;
println(bs); //false
A=seq3;
println(bs); //true

B=seq4;
println(bs); //false
A=seq8;
println(bs); //true
