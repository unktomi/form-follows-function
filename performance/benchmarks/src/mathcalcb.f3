import java.lang.Exception;


public class  mathcalcb extends cbm {
   var opcount=0;
   var m_count=0;
	var _num1 = "1";
	var _num2 = "1";
	var _functionSeq = [" + "," - "," x "," / "," % "];
	var currFunction = 0;
	var status = "";


	bound function doMathOp0( num1:Integer, num2:Integer ):Number {
		  (num1 + num2)*1.0;
	}

	bound function doMathOp1( num1:Integer, num2:Integer ):Number {
		  (num1 - num2)*1.0
	}
	bound function doMathOp2( num1:Integer, num2:Integer ):Number {
		  (num1 * num2)*1.0
	}
	bound function doMathOp3( num1:Integer, num2:Integer ):Number {
		   (num1 / num2)*1.0
	}
	bound function doMathOp4( num1:Integer, num2:Integer ):Number {
		 (num1 mod num2)*1.0
	}

function doMath(op:Integer, num1:Integer, num2:Integer):Number{
  var ret:Number=0;
 if(op==1) ret = (num1+num2) as Number;
 if(op==2) ret = (num1-num2) as Number;
 if(op==3) ret = (num1*num2) as Number;
 if(op==4) ret = (num1/num2) as Number;
 if(op==5) ret = (num1 mod num2) as Number;
 return ret;
 }
 function check(n1:Number, n2:Number){if(n1!=n2)throw new Exception("ERROR: {n1} != {n2}")}

	/** Bind function to _sum. As args (operation or numbers) change so does the solution */
	var i1=1;
	var i2=1;
    var cnt=0;
	var mo0 = bind doMathOp0( i1, i2) on replace { opcount++ }
	var mo1 = bind doMathOp1( i1, i2) on replace { opcount++ }
	var mo2 = bind doMathOp2( i1, i2) on replace { opcount++ }
	var mo3 = bind doMathOp3( i1, i2) on replace { opcount++ }
	var mo4 = bind doMathOp4( i1, i2) on replace { opcount++ }

	override public function test():Number {
	  var total:Number=0;
	  for ( a in [1..1000] )
		 for ( b in [ 1..600] ) {
            cnt++;
			i1 = a;
			i2 = b;
			total += mo0;  if(DEBUG)check(mo0, doMath(1,i1,i2));
			total += mo1;  if(DEBUG)check(mo1, doMath(2,i1,i2));
            total += mo2;  if(DEBUG)check(mo2, doMath(3,i1,i2));
			total += mo3;  if(DEBUG)check(mo3, doMath(4,i1,i2));
			total += mo4;  if(DEBUG)check(mo4, doMath(5,i1,i2));
		 }
	debugOutln("");
	debugOutln("checksum: {total}");
	debugOutln("opcount: {opcount}");
    if( total != 9.0415194E10 )println("ERROR: checksums did not match! total:{total}");
    debugOutln("count: {cnt}");
	total;
  }


}; //mathcalc

/**
 * define a run method to call runtests passing args and instance of this class
 */
public function run(args:String[]) {
    var m = new mathcalcb();
    cbm.runtest(args,m)
}


