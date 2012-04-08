/*
 * Regression test: Infer parameter type from context.
 *
 * @test
 * @run
 */

import java.lang.System;

class CanvasMouseEvent {
    public var x: Number;
    public var y: Number;
}
class Circle {
  public var onMouseClicked: function(e:CanvasMouseEvent):Void;
};
var c = Circle {
    onMouseClicked: function(mEvt) {
        System.out.println("mouse click: x={mEvt.x} y={mEvt.y}");
    }
} 
c.onMouseClicked(CanvasMouseEvent{x: 2; y: 3} );
