/**
 * regression test:  unary operators don't destroy the type of a variable.
 * @test
 */

import java.awt.event.ActionEvent; 
public class Foo { 
    protected var jmenuitem:javax.swing.JMenuItem; 
    public var action: function():Void; 

    public function createComponent():javax.swing.JComponent { 
        jmenuitem = new javax.swing.JMenuItem(); 
        jmenuitem.addActionListener(java.awt.event.ActionListener { 
                                        public function actionPerformed(e:ActionEvent) { 
                                            if (action != null) { 
                                                action(); 
                                            } 
                                        } 
                                    }); 
       jmenuitem; 
    } 
} 
