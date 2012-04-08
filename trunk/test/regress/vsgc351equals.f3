/*
 * Regression test: == should behave as equals()
 *
 * @test
 * @run
 */

import java.lang.System;
import java.lang.Object;

class Bar {
  var x : Integer;
  var y : Integer;
  public function equals(obj : Object) : Boolean { 
	if (obj instanceof Bar) {
		var other : Bar = obj as Bar;
		other.x == x
	} else {
		false
	}
  }
  var  seq = [1..6];
  var  emptySeq : Integer[] = [];
  var  str = "yo";
  var  emptyStr = "";
  var  nullStr : String  = null;
}

var b1 = Bar {x: 44 y: 98 }
var b2 = Bar {x: 44 y: 15 }
var b3 = Bar {x: 99 y: 98 }

var seq = [1..6];
var emptySeq : Integer[] = [];
var str = "yo";
var emptyStr = "";
var nullStr : String  = null;
var zero = 0;
var ten = 10;
var fzero = 0.0;
var nullObject : Object;
nullObject = null;

System.out.println("Its all true");

System.out.println("yaks" == ("YAKS".toLowerCase()));
System.out.println(b1 == b2);
System.out.println(b2 == b1);

System.out.println(seq == b1.seq);
System.out.println(b1.seq == seq);
System.out.println(emptySeq == null);
System.out.println(null == emptySeq);
System.out.println(b1.emptySeq == null);
System.out.println(null == b1.emptySeq);
System.out.println(b1.seq == b2.seq);
System.out.println(b1.seq == seq);

System.out.println(str == b1.str);
System.out.println(b1.str == str);
System.out.println(emptyStr == null);
System.out.println(nullStr == null);
System.out.println(null == emptyStr);
System.out.println(null == nullStr);
System.out.println(b1.emptyStr == null);
System.out.println(b1.nullStr == null);
System.out.println(null == b1.emptyStr);
System.out.println(b1.str == b2.str);
System.out.println(b1.str == str);

System.out.println(zero == nullObject);
System.out.println(ten == 10);
System.out.println(fzero == nullObject);

System.out.println("Its all false");

System.out.println("yaks" == ("MACS".toLowerCase()));
System.out.println(b1 == null);
System.out.println(null == b1);
System.out.println(b1 == b3);
System.out.println(b2 == b3);

System.out.println(seq == [1..2]);
System.out.println(emptySeq == seq);
System.out.println(null == seq);
System.out.println(null == str);
System.out.println(null == b1.str);
System.out.println(str == "bop");
System.out.println("pop" == b1.str);
System.out.println("zipo" == emptyStr);

System.out.println("And this too is false");

System.out.println("yaks" != ("YAKS".toLowerCase()));
System.out.println(b1 != b2);
System.out.println(b2 != b1);

System.out.println(seq != b1.seq);
System.out.println(b1.seq != seq);
System.out.println(emptySeq != null);
System.out.println(null != emptySeq);
System.out.println(b1.emptySeq != null);
System.out.println(null != b1.emptySeq);
System.out.println(b1.seq != b2.seq);
System.out.println(b1.seq != seq);

System.out.println(str != b1.str);
System.out.println(b1.str != str);
System.out.println(emptyStr != null);
System.out.println(nullStr != null);
System.out.println(null != emptyStr);
System.out.println(null != nullStr);
System.out.println(b1.emptyStr != null);
System.out.println(b1.nullStr != null);
System.out.println(null != b1.emptyStr);
System.out.println(b1.str != b2.str);
System.out.println(b1.str != str);

System.out.println(zero != nullObject);
System.out.println(ten != 10);
System.out.println(fzero != nullObject);

System.out.println("But then, this is true");

System.out.println("yaks" != ("MACS".toLowerCase()));
System.out.println(b1 != null);
System.out.println(null != b1);
System.out.println(b1 != b3);
System.out.println(b2 != b3);

System.out.println(seq != [1..2]);
System.out.println(emptySeq != seq);
System.out.println(null != seq);
System.out.println(null != str);
System.out.println(null != b1.str);
System.out.println(str != "bop");
System.out.println("pop" != b1.str);
System.out.println("zipo" != emptyStr);
