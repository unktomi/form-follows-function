/* Feature test #10 -- function values
 * @test
 * @run
 */
import java.lang.*;

function plus10 (x : Integer) : Integer { return x+10; }
//System.err.println(plus10);

function call3 (f : function(:Integer):Integer, prefix) {
  System.out.println("{prefix}: 1->{f(1)} 2->{f(2)} 3->{f(3)}");
}

var action1 : function(:String):String
   = function(x:String):String { System.out.println("button pressed"); x };
System.out.println(action1("action1 called"));

class Cl1 {
  var fvar : function(:String):String;
  var xvar : String;
  var concat = function (y : String) { "{xvar}-{y}" };
};
var cl = new Cl1();
cl.xvar = "cl.fvar called";
cl.fvar = action1;
System.out.println(cl.fvar(cl.xvar));
cl.xvar = "reset cl.xvar";
System.out.println(cl.concat("Cl1.concat called"));

var fv2 : function(x:Integer):Integer =
  function (x: Integer):Integer { x + 100 };
System.out.println("fv2(12) = {fv2(12)}");
var fv3 : function(x:Number):Void =
  function (x: Number):Void { System.out.println("void fv3({x})") };
fv3(13.5);

call3(plus10, "call named plus10");

call3(function(x : Integer) :Integer {x+5}, "call anonymous x+5");

var ff : function(x:Integer):Integer = plus10;
call3(ff, "call plus10 assigned to ff");
ff = function (x : Integer) {x+2};
call3(ff, "call anonymous x+2 assigned to ff");

function fun0a (fparg : function():java.lang.Double) : Void {
  var gparg : function():java.lang.Double = fparg;
}
function fun0b (fparg : function():java.lang.Double) : Void {
  var gparg : function():java.lang.Number = fparg;
}
function fun2a (fparg : function(:java.lang.Number, :Integer):java.lang.Double) : Void {
  var gparg : function(:java.lang.Double, :Integer):java.lang.Double = fparg;
}
function fun2b (fparg : function(:java.lang.Number, :Integer):java.lang.Double) : Void {
  var gparg : function(:java.lang.Double, :Integer):java.lang.Double = fparg;
}

/* These should be compile-time errors:, but we don't support
 * error tests yet. FIXME
function fun0c (fparg : function():java.lang.Number) : Void {
  var gparg : function():java.lang.Double = fparg;
}
function fun2c (fparg : function(:java.lang.Double, :Integer):java.lang.Double) : Void {
  var gparg : function(:java.lang.Number, :Integer):java.lang.Double = fparg;
}
*/

function f4 (x : String):String  { "x:{x}" };
var vf4 : function(:String):String = f4;
System.out.println(vf4("test-f4"));

function f5(x:Integer) {
    var y: Integer = x+10;
    y = y + 2;
    function(z:Integer){x+y+z}}
System.out.println("f5(1)(5)={f5(1)(5)}");

// Test of sequence of functions:
function f6 (y : String):String  { "y:{y}" };
var funs = [f4,f6,f4];
for (f in funs)
    System.out.println("funs[{indexof f}]->{f("foo{indexof f}")}"); 
