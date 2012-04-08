/*
 * Regression test: returning assignment
 *
 * @test
 */

import java.awt.Dimension;

public class CanvasViewport {
    public var currentHeight: Integer;
    public var currentWidth: Integer;

    public var currentSize: Dimension = new Dimension(0, 0)
        on replace {
            currentWidth = currentSize.width;
            currentHeight = currentSize.height;
    };
    public function setSize(w:Integer, h:Integer){
        currentSize = new Dimension(w, h);
    }
}
