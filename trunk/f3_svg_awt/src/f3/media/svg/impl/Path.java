package f3.media.svg.impl;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class Path {

    GeneralPath gp = new GeneralPath();

    float cx, cy;

    public void rMoveTo(float x, float y) {
        moveTo(cx=cx+x, cy=cy+y);
    }

    public void moveTo(float x, float y) {
        gp.moveTo(cx = x, cy = y);
    }

    public void close() {
        gp.closePath();
    }

    public void rLineTo(float x, float y) {
        lineTo(cx=cx + x, cy=cy + y);
    }

    public void lineTo(float x, float y) {
        gp.lineTo(cx = x, cy = y);
    }

    public void cubicTo(float cx1, float cy1, float cx2, float cy2, float x2, float y2) {
        gp.curveTo(cx1, cy1, cx2, cy2, cx = x2, cy = y2);
    }

    public void quadTo(float cx1, float cy1, float x2, float y2) {
        gp.quadTo(cx1, cy1, cx = x2, cy = y2);
    }

    public void setFillEvenOdd() {
        gp.setWindingRule(GeneralPath.WIND_EVEN_ODD);
    }
    public void setFillNonZero() {
        gp.setWindingRule(GeneralPath.WIND_NON_ZERO);
    }

    public void computeBounds(RectF rect, boolean bool) {
        Rectangle2D bounds = gp.getBounds2D();
        rect.set((float)bounds.getX(), (float)bounds.getY() + (float)bounds.getHeight(), 
                 (float)bounds.getX() + (float)bounds.getWidth(),
                 (float) bounds.getY());
    }
}