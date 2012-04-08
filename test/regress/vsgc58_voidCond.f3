/*
 * Regression test: assertion failure: void conditionals & block expressions
 *
 * @test
 */

import java.lang.System;
    
var BOLD = FontStyle {id: 1, name: "BOLD"};
var PLAIN = FontStyle {id: 2, name: "PLAIN"};
var ITALIC = FontStyle {id: 3, name: "ITALIC"};

public class FontStyle {
    public var id: Integer;
    public var name: String;
}

public class Foo {
    public var style: FontStyle[];
    
    public function Font(faceName:String, style:String[], size:Integer){
        for (i in style) {
            if (i == "PLAIN") then {
                insert PLAIN into this.style
            } else if (i == "ITALIC") then {
                insert ITALIC into this.style
            } else if (i == "BOLD") then{
                insert BOLD into this.style
            } else {
                throw new java.lang.Throwable("Bad font style {i}: expected PLAIN, BOLD, or ITALIC")
            }
        }
    }
}
