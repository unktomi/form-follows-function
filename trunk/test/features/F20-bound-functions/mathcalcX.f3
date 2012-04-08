/* Feature test #20 -- bound function
 *
 * Simple bound math function with two arguments - called in a loop
 * for varying arguments. Check that the bound function responds to
 * changes in both arguments.
 *
 * @author Steve Sides
 * @test
 * @run
 */

import java.lang.System;

var DEBUG=false;
var USETIMER=false;

class  mathcalc {
   var m_count=0;
	function debugOutln(msg:String) { if(DEBUG) println(msg);}
	function debugOut(msg:String) { if(DEBUG) print(msg);}

	var _num1 = "1";
	var _num2 = "1";
	var _functionSeq = [" + "," - "," x "," / "," % "];
	var currFunction = 0;
	var status = "";


	bound function doMathOp2( num1:Integer, num2:Integer ):Number { (num1 * num2) as Number}

	/** Bind function to _sum. As args (operation or numbers) change so does the solution */
	var i1=1;
	var i2=1;
    var cnt=0;
	var mo2 = bind doMathOp2( i1, i2) on replace { m_count++ }

	public function test():Number {
	  var total:Number=0;
	  for ( a in [1..3] )
		 for ( b in [ 1..3] ) {
            cnt++;
			i1 = a;
			i2 = b;
            println("{i1} x {i2} = {mo2}");
          total += mo2;
		 }
	debugOutln("");
	debugOutln("checksum: {total}");
	debugOutln("method call count: {m_count}");
    if( total != 36 )println("ERROR: checksums did not match! total:{total}");
    debugOutln("count: {cnt}");
	total;
  }

}; //mathcalc


/** timer function  */
	function runTest( f:function():Number )  {
	def startTime = System.nanoTime();
	  var t = f();
	println("time: {(System.nanoTime()-startTime)*0.000001}ms.");
}


var mc = new mathcalc();

public function run( args:String[])
{
  //  var mc = new mathcalc();              //mixin compiler will not let me put this here!
  for(arg in args) {
    if(arg.compareTo("-debug")==0) DEBUG=true;
    if(arg.compareTo("-time")==0) USETIMER=true;
  }

  if(USETIMER) {runTest(mc.test);}
  else {  mc.test(); }
}


