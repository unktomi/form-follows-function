/* regression test for the bug 849
 * (Sort-of: bug 849 was a run-time NPE, and we can't "run" tests
 * that create Windows, so it's not clear what we can do, except
 * make sure the code at least compiles.)
 *
 * @test
 */

import java.lang.System; 

class Frame {
	function getWindow(): java.awt.Window { return null; }
}

var frame = new Frame;
var listeners = frame.getWindow().getContainerListeners(); 
for (l in listeners) {
  var cl : java.awt.event.ContainerListener = l;
  println(cl);
};


