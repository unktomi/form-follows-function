/*
 * Simple functional test for sequence operators.
 * Operators : sizeof, indexof, after, before, delete, reverse
 * @test
 * @run
 */


import java.lang.*;

var pass=0;

// Different ways of creating sequence
var a = [1,2,3,4];
var b = { for(k in [1..10] where (k mod 2 == 0)) k };
var c:Integer[] = [];  
var d = [a,b,c];
var e = [a,[b,d],c];
var f = [0..<9];
var g = f[4..];
var h = [0..9];
var i = g[0..<];
var j = f[n|indexof n>2];

function checkAfter(arg:Integer[]) {
		var x = arg;
		var y = [x, x];
		insert x after x[-1];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		insert x after x[-1];
		check(x==y);
		
		x = arg;
		y = [x];
		// insert x after x[-2];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		// insert x after x[-2];
		check(x==y);
		
		x = arg;
		y = [x];
		// insert x after x[Integer.MAX_VALUE-100];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		// insert x after x[Integer.MAX_VALUE-100];
		check(x==y);

		x = arg;
		y = [x,x];
		insert x after x[(sizeof x) - 1];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		insert x after x[(sizeof x) - 1];
		check(x==y);
		
		x = arg;
		y = [x];
		//insert x after x[sizeof x];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		//insert x after x[sizeof x];
		check(x==y);
}
function checkBefore(arg: Integer[]) {
		var x = arg;
		var y = [x, x];
		insert x before x[0];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		insert x before x[0];
		check(x==y);
		
		x = arg;
		y = [x];
		// insert x before x[-1];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		// insert x before x[-1];
		check(x==y);
		
		x = arg;
		y = [x];
		// insert x before x[Integer.MAX_VALUE-100];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		// insert x before x[Integer.MAX_VALUE-100];
		check(x==y);
		
		x = arg;
		y = [x,x];
		insert x before x[sizeof x];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		insert x before x[sizeof x];
		check(x==y);
		
		x = arg;
		y = [x];
		// insert x before x[sizeof x + 1];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		// insert x before x[sizeof x + 1];
		check(x==y);
}

function checkDelete(arg: Integer[]) {
		var x = arg;
		var y:Integer[] = [];
		delete x;
		check(x==y);
		x = reverse (arg);
		delete x;
		check(x==y);
		
		x = arg;
		y = [x];
		delete x[-1];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		delete x[-1];
		check(x==y);
		
		x = arg;
		y = [x];
		delete x[1000];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		delete x[1000];
		check(x==y);
		
		x = arg;
		y = [x];
		delete x[sizeof x];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		delete x[sizeof x];
		check(x==y);
		
		x = arg;
		y = [x];
		delete Integer.MAX_VALUE from x;
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		delete Integer.MAX_VALUE from x;
		check(x==y);
		
		x = arg;
		y = [x,x];
		for(z in x) {
			delete z from y; 
		}
		check([]==y);
		x = reverse (arg);
		y = reverse y;	
		for(z in x) {
			delete z from y; 
		}
		check([]==y);
		
		x = arg;
		y = [x,x];
		for(z in x) {
			delete y[0]; 
		}
		check(x==y);
		x = reverse (arg);
		y = [x,x];	
		for(z in x) {
			delete y[0]; 
		}
		check(x==y);
		
		x = arg;
		y = [x];
		delete x[-100..-200];
		check(x==y);
		delete x[-100..<-200];
		check(x==y);
// Is rhis right?  delete x[-100..<]; 	check(x==y);
		delete x[1000..2000];
		check(x==y);
		delete x[1000..<2000];
		check(x==y);
		delete x[1000..<];
		check(x==y);
		delete x[0..100];
		check(x==[]);
		x = reverse (arg);
		y = reverse y;
		delete x[-100..-200];
		check(x==y);
		delete x[-100..<-200];
		check(x==y);
// This seems wrong:	delete x[-100..<];	check(x==y);
		delete x[1000..2000];
		check(x==y);
		delete x[1000..<2000];
		check(x==y);
		delete x[1000..<];
		check(x==y);
		delete x[0..100];
		check(x==[]);

		
		x = arg;
		y = [x];
//		delete x[-100..100];		check(x==y);
		if(not (sizeof x == 0)) {
			delete x[0..<];
			check(sizeof x == 1);
		}
		x = reverse (arg);
		y = reverse y;
//		delete x[-100..100];		check(x==y);
		if(not (sizeof x == 0)) {
			delete x[0..<];
			check(sizeof x == 1);
		}
}

function checkIndexOf(arg:Integer[]) {
		var x = arg;
		var y:Integer[] = [];
		x = x[n|indexof n>sizeof x];
		check(x==y);
		x = reverse (arg);
		x = x[n|indexof n>sizeof x];
		check(x==y);
		
		x = arg;
		x = x[n|indexof n>sizeof x-1];
		check(x==y);
		x = reverse (arg);
		x = x[n|indexof n>sizeof x-1];
		check(x==y);
		
		x = arg;
		y = x;
		x = x[n|indexof n > Integer.MIN_VALUE];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		x = x[n|indexof n > Integer.MIN_VALUE];
		check(x==y);
		
		x = arg;
		y = x;
		x = x[n|indexof n < Integer.MAX_VALUE];
		check(x==y);
		x = reverse (arg);
		y = reverse y;
		x = x[n|indexof n < Integer.MAX_VALUE];
		check(x==y);

		x = arg;
		if(not (sizeof x ==0)) {
			x = x[n|indexof n>(sizeof x -2)];
			check(sizeof x == 1);
		
			x = reverse (arg);
			x = x[n|indexof n>sizeof x-2];
			check(sizeof x == 1);
		}
}

function check(x:Boolean) {
	if(x) pass++ else throw new Exception("Test failed");
}

checkAfter(a);
checkAfter(b);
checkAfter(c);
checkAfter(d);
checkAfter(e);
checkAfter(f);
checkAfter(h);
checkAfter(i);
checkAfter(j);
checkBefore(a);
checkBefore(b);
checkBefore(c);
checkBefore(d);
checkBefore(e);
checkBefore(f);
checkBefore(h);
checkBefore(i);
checkBefore(j);
checkDelete(a);
checkDelete(b);
checkDelete(c);
checkDelete(d);
checkDelete(e);
checkDelete(f);
checkDelete(h);
checkDelete(i);
checkDelete(j);
checkIndexOf(a);
checkIndexOf(b);
checkIndexOf(c);
checkIndexOf(d);
checkIndexOf(e);
checkIndexOf(f);
checkIndexOf(h);
checkIndexOf(i);
checkIndexOf(j);

System.out.println("Pass count : {pass}");
