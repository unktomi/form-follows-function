package f3.media.svg.impl;
public class LinearGradient extends Shader {
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public int[] colors;
    public float[] offsets;
    public Shader.TileMode tileMode;
    public Matrix matrix;
    public boolean userSpace = false;
    public LinearGradient(float x1,
                          float y1,
                          float x2,
                          float y2,
                          int[] colors,
                          float[] offsets,
                          Shader.TileMode tileMode) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.colors = colors;
        this.offsets = offsets;
        this.tileMode = tileMode;
    }
    public void setLocalMatrix(Matrix mat) {
        this.matrix = new Matrix(mat);
    }
}