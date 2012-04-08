/**
 * Ensure that def sequences are caught by the compiler and are not allowed to be mutable.
 * @test/compile-error
 */
import java.lang.System;

class Jim {

	def y = [69,69];
}

var g 	= new Jim();
def h 	= [99, 77];
h[2] 	= 88;
g.y[3] 	= 88;
var v 	= h[1];
var j 	= g.y[3];




def s = [1, 2, 3] on replace {
   System.out.println("s={String.valueOf(s)} b={String.valueOf(b)}");
}

var b = bind s;

s[0] = 2;

