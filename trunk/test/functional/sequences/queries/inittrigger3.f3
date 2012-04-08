import java.lang.System;

/**
 * triggers firing in init block.
 * @test
 * @run
 */

class aclass {

public var bnull:Boolean
	on replace { System.out.println("bnull new value: {bnull}"); }
   function set_bnull(newb:Boolean){ bnull = newb; System.out.println("set_b(): bnull:{bnull}"); }
public var snull:String
	on replace oldvalue  { System.out.println("snull old:{oldvalue}  new:{snull}"); }
public var inull:Integer
	on replace oldvalue=newvalue  { System.out.println("inull old:{oldvalue}  newvalue:{newvalue}==inull:{inull}"); }
public var names:testclass[]
	on replace old[idx1..idx2]=newvalues {}


public var b = false
	on replace { System.out.println("b new: {b}"); }
public var s = "Hello"
	on replace oldvalue  { System.out.println("s old:{oldvalue}  new:{s}"); }
public var i = 5
	on replace oldvalue=newvalue  { System.out.println("i old:{oldvalue}  newvalue:{newvalue}==i:{i}"); }

//can capture old sequence; on init {old} is null
public var seqb:Boolean[]=[false,false,false]
	on replace old { System.out.println("seqb old:{old}  new:{seqb}"); }

//Capture old, indexes, new elements and new sequence
public var seqs:String[]=["Mary","had","little","lamb"]
	on replace old[idx1..idx2]=newElements { System.out.println("seqs old:{old}  indexes:{idx1}..{idx2}  new elements:{newElements}  new sequence:{seqs}");}

//simple trigger used for a sequence
public var seqi = [ 100,101,102,103,106,108,110 ]
	on replace { System.out.println("seqi"); System.out.println( {seqi} ); };


init {
	System.out.println("----------values by init ---------");
	System.out.print("bnull:"); 	System.out.println({bnull});
	System.out.print("snull:"); 	System.out.println({snull});
	System.out.print("inull:"); 	System.out.println({inull});

	System.out.print("b:"); 	System.out.println({b});
	System.out.print("s:"); 	System.out.println({s});
	System.out.print("i:"); 	System.out.println({i});

	System.out.print("seqb:"); 	System.out.println({seqb});
	System.out.print("seqs:"); 	System.out.println({seqs});
	System.out.print("seqi:"); 	System.out.println({seqi});
}

function test() {
   System.out.println("-----trigger firing at test()------");
	//simple value replacements
	b=true;
	set_bnull(false);
	set_bnull(true);
	s="Hello, World!";
	i=500;
	//slice replace integer sequence
	seqi[4..5]=[104,105,106,107,108,109];
	//replace explicitly indexed value in sequence
	seqb[0]=true;
	//replace slice (index 1..2) with new sequence
	seqs[1..2]=[" had"," a"," wee", " little "];
   //insert into sequence...goes at the end of sequence
	insert "." into seqs;
   //delete value from sequence
	delete " wee" from seqs;
}
}

class testclass{
	var name:String = "test class";

	function setName(newname:String):String {
		var o = name;
		name=newname;
		return o;
	}
};

System.out.println("-----trigger firing at creation------");
var sc = new aclass;
sc.test()

