package f3.media.svg.impl;
import java.util.*;
import java.awt.Shape;
import java.awt.geom.*;

public class Canvas {

    Stack<Matrix> stack = new Stack();
    
    Matrix current = new Matrix();

    public void save() {
        stack.push(current);
        current = new Matrix(current);
    }

    public void restore() {
        current = stack.pop();
    }

    public void concat(Matrix m) {
        current.concat(m);
    }

    public void drawRect(float x1, float y1, float x2, float y2, Paint paint) {
        addShape(current.at,
                 new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1),
                 paint);
    }

    public void drawCircle(float centerX, float centerY, float radius, Paint paint) {
        addShape(current.at,
                 new Ellipse2D.Float(centerX-radius, centerY-radius, radius *2, radius *2), 
                 paint);
    }

    public void drawOval(RectF rect, Paint paint) {
        Ellipse2D.Float ellipse = 
            new Ellipse2D.Float(rect.left, rect.top, rect.right-rect.left, rect.bottom - rect.top);
        System.out.println("draw oval: "+ellipse.getBounds2D());
        addShape(current.at,
                 ellipse,
                 paint);
    }

    public void drawPath(Path path, Paint paint) {
        addShape(current.at, new GeneralPath(path.gp), paint);
    }


    public void drawLine(float x1, float y1, float x2, float y2, Paint paint) {
        addShape(current.at, new Line2D.Float(x1, y1, x2, y2), paint);
    }
    
    public  void addShape(AffineTransform worldMatrix,
                          Shape shape,
                          Paint paint) {
    }

    public void beginSymbol() {
    }

    public void endSymbol()  {
    }

    public void beginClip() {
    }

    public void endClip() {
    }

    public void use(float x, float y, Matrix mat) {
    }
}