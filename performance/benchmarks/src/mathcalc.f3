import java.lang.System;

class  mathcalc extends cbm {

	var _num1 = "1";
	var _num2 = "1";
	var _functionSeq = [" + "," - "," x "," / "," % "];
	var currFunction = 0;
	var status = "";


	function doMath(op:Integer, num1:Integer, num2:Integer ):Number {
		var solution = 0.0;
		if(op==0) { solution = num1 + num2; status="addition";}
		if(op==1)  { solution = num1 - num2; status="subtraction";}
		if(op==2)  { solution = num1 * num2; status="multiplication"; }
		if(op==3)  {if(num2==0) { status = "Error: Divide by Zero is not allowed!" }
						else { solution =  num1 / num2 ; status="division"; }}
		if(op==4)  { solution = num1 mod num2; status="remainder"; }

		solution;
	}

	/** Bind function to _sum. As args (operation or numbers) change so does the solution */
	var i1=0;
	var i2=0;
	var isum:Number = bind doMath(currFunction, i1, i2);

	override public function test():Number {
      var cnt=0;
	  debugOut("current operation: ");
	  var total:Number=0;
	 for ( o in [ 0..4] ) {
	  currFunction = o;
	  debugOut(" {_functionSeq[o]} ");
	  for ( a in [1..600] )
		 for ( b in [ 1..600] ) {
            cnt++;
			i1 = a;
			i2 = b;
			total += isum;
//			total += doMath(o, a, b);
		 }
		total;
	}
	debugOutln("");
	debugOutln("checksum: {total}");
    if( total != 3.27243162E10 )println("ERROR: checksums did not match!");
    debugOutln("count: {cnt}");
	total;
	}

}; //mathcalc


/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var m = new mathcalc();
    cbm.runtest(args,m)
}
