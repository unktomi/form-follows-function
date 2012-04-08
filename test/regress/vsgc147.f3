/*
 * Regression test: implement an interface with an object literal
 *
 * @test
 * // do not run this testcase. It brings up UI and appers to hang while running regtests. 
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Frame {
    var jlabel: JLabel = new JLabel();
    var jframe: JFrame = new JFrame();

    public var title: String = "" on replace {jframe.setTitle(title); update()}

    public var height: Integer = 0 on replace {jframe.setSize(new Dimension(width, height)); update()}

    public var width: Integer = 0 on replace {jframe.setSize(new Dimension(width, height)); update()}

    public var screenX: Integer = 0 on replace {jframe.setLocation(new Point(screenX, screenY)); update()}

    public var screenY: Integer = 0 on replace {jframe.setLocation(new Point(screenX, screenY)); update()}

    public var visible: Boolean = false on replace {jframe.setVisible(visible); update()}

    function update(): Void { jlabel.setText("{screenX} {screenY} {width} {height}") }

    var componentListener:ComponentListener = ComponentListener {

	public function componentMoved(e:ComponentEvent): Void {
		def location = jframe.getLocation();
		screenX = location.x;
		screenY = location.y
   	}

    	public function componentResized(e:ComponentEvent): Void {
		def d = jframe.getSize();
		width = d.width;
		height = d.height
	}

	public function componentHidden(e:ComponentEvent): Void {
	}

	public function componentShown(e:ComponentEvent): Void {
	}
    }

    init {
	jframe.addComponentListener(componentListener);
	jframe.getContentPane().add(jlabel);
	jframe.pack();
	jframe.setSize(new Dimension(width, height));
	jframe.setLocation(new Point(screenX, screenY))
    }
}

def f = Frame {height: 500, width: 400, visible: true}

