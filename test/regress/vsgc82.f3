/**
 * regression test:  unary operators don't destroy the type of a variable.
 * @test
 */

var KEYBOARD = Keyboard{} 

public class KeyStroke { 
    var description: String; 
    var id: Number 
        on replace { 
            KEYBOARD.keyMap.put(id, this); 
            description = javax.swing.KeyStroke.getKeyStroke(id.intValue(), 0).toString(); 
         
    }; 
    var keyChar: String; 
} 

public class Keyboard { 
     
    protected var keyMap:java.util.Map = new java.util.HashMap(); 

    public function getKeyStroke(id:Number): KeyStroke { 
        return keyMap.get(id) as KeyStroke; 
    } 

} 
