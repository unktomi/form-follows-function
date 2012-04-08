/*
 * Feature test #23 - animation
 *
 * @subtest  // remove the "sub" when ready to run in harness
 */

import java.lang.System;

class Rect { width: Integer, height: Integer }

var x = 2;
x => 100 tween LINEAR;

var rect = Rect {
    width: 500
};
rect => {
    height: 400 tween EASEBOTH, 
    width: 500
};

at (1s) { 
    x => 2 tween LINEAR;
    rect => {width: 400 tween EASEBOTH};
    trigger {
       System.out.println("at 1 second...");
    }
}
