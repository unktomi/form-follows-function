/*
 * Regression test: usage of the cellFactory var cause a problem in compilation
 *
 * @test
 */

class Bound {
   var width:Number;
}

class Rect {
   var id:String;
   var boundsInParent = bind compute_bounds() on invalidate {println("invalidate bounds");}
   var width:Number on invalidate {invalidate boundsInParent};
   var X = 0;
}

function compute_bounds() {
   println("compute bounds");
   Bound{width:100};
}

var uRect = Rect { id:"UnboundRect"}

var node = uRect;

var bRect: Rect = Rect {
   id: "BoundRect"
   width: bind node.boundsInParent.width;
};

invalidate uRect.boundsInParent;
invalidate bRect.boundsInParent;

var debug = bind uRect.X on replace oldVal {
	println("Selected Item changed from {oldVal} to {debug}");
    node = if (debug == 0) bRect else uRect;
}

bRect.width;

uRect.X++;
uRect.X++;
