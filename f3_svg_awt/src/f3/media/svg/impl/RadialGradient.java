package f3.media.svg.impl;
public class RadialGradient extends Shader {
    public float x;
    public float y;
    public float radius;
    public int[] colors;
    public float[] offsets;
    public Shader.TileMode tileMode;
    public Matrix matrix;
    public boolean userSpace = false;
    public RadialGradient(float x, 
                          float y, 
                          float radius, 
                          int[] colors, 
                          float[] positions, 
                          Shader.TileMode tileMode) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.colors = colors;
        this.offsets = positions;
        this.tileMode = tileMode;
    }

    public void setLocalMatrix(Matrix mat) {
        this.matrix = new Matrix(mat);
    }
}