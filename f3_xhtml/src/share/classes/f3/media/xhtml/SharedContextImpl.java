package f3.media.xhtml;
import org.xhtmlrenderer.layout.SharedContext;
import java.awt.Rectangle;

class SharedContextImpl extends SharedContext {

    public SharedContextImpl() {
        super(null);
    }

    Rectangle bounds;

    public void setBounds(int x, int y, int w, int h) {
        bounds = new Rectangle(x, y, w, h);
    }

    public Rectangle getFixedRectangle() {
        return bounds;
    }
}
