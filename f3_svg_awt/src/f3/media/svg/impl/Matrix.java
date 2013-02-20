package f3.media.svg.impl;
import java.awt.geom.AffineTransform;

public class Matrix {

    public AffineTransform at;
    
    public void setValues(float[] values) {
        float m00 = values[0];
        float m01 = values[1];
        float m02 = values[2];
        float m10 = values[3];
        float m11 = values[4];
        float m12 = values[5];
        at = new AffineTransform(m00, m10, m01, m11, m02, m12);
    }

    public void postTranslate(float x, float y) {
        AffineTransform t = AffineTransform.getTranslateInstance(x, y);
        at.concatenate(t);
    }
    
    public void postScale(float x, float y) {
        AffineTransform t = AffineTransform.getScaleInstance(x, y);
        at.concatenate(t);
    }

    public void postSkew(float x, float y) {
        AffineTransform t = AffineTransform.getShearInstance(x, y);
        at.concatenate(t);
    }
    public void postRotate(float angle) {
        AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(angle));
        at.concatenate(t);
    }

    public Matrix() {
        at = new AffineTransform();
    }

    public Matrix(Matrix mat) {
        at = new AffineTransform(mat.at);
    }

    public void preConcat(Matrix mat) {
        at.preConcatenate(mat.at);
    }

    public void concat(Matrix mat) {
        at.concatenate(mat.at);
    }
}
