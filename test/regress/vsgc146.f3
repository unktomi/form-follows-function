/*
 * Regression test: translation within if
 *
 * @test
 */

import javax.swing.*; 
import java.awt.*; 
import java.awt.event.*; 

public class Frame extends ComponentListener { 
    var jlabel: JLabel = new JLabel(); 
    var jframe: JFrame = new JFrame(); 


    public var title: String = "" 
on replace {jframe.setTitle(title);update();}; 

    public var height: Integer = 0 
on replace {jframe.setSize(new Dimension(width, height)); update();}; 

    public var width: Integer = 0 
on replace {jframe.setSize(new Dimension(width, height)); update();}; 

    public var screenX: Integer = 0 
on replace {jframe.setLocation(new Point(screenX, screenY)); update();}; 
    public var screenY: Integer = 0 
on replace {jframe.setLocation(new Point(screenX, screenY)); update();}; 

    public var visible: Boolean = false 
on replace {jframe.setVisible(visible); update();}; 

    function update(): Void { 
jlabel.setText("{screenX} {screenY} {width} {height}"); 
    } 

    public function componentMoved(e:ComponentEvent): Void { 
var location = jframe.getLocation(); 
this.screenX = location.x; 
this.screenY = location.y; 
    } 
    public function componentHidden(e:ComponentEvent): Void { 
    } 

    public function componentShown(e:ComponentEvent): Void { 
    } 

    public function componentResized(e:ComponentEvent): Void { 
var d = jframe.getSize(); 
this.width = d.width; 
this.height = d.height; 
    } 

    init { 
jframe.addComponentListener(this); // doesn't compile anymore 
jframe.getContentPane().add(jlabel); 
jframe.pack(); 
jframe.setSize(new Dimension(width, height)); 
jframe.setLocation(new Point(screenX, screenY)); 
    } 
} 

def f = Frame {height: 500, width: 400, visible: true}; 
