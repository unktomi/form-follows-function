package f3.media.svg.impl;

public class Paint {

    public enum Style {
        FILL, STROKE
    }

    public enum Cap {
        BUTT, SQUARE, MITER, ROUND
    }

    public enum Join {
        BEVEL, ROUND, MITER
    }

    Shader shader;
    Style style;
    Cap strokeCap = Cap.BUTT;
    Join strokeJoin = Join.MITER;
    float strokeWidth = 1.0f;
    int color;
    int alpha = 255;

    public Shader getShader() {
        return shader;
    }

    public int getColor() {
        return color;
    }

    public int getAlpha() {
        return alpha;
    }

    public Style getStyle() {
        return style;
    }

    public Cap getStrokeCap() {
        return strokeCap;
    }

    public Join getStrokeJoin() {
        return strokeJoin;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setStrokeCap(Cap cap) {
        this.strokeCap = cap;
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }

    public void setStrokeJoin(Join join) {
        this.strokeJoin = join;
    }

    public void setAntiAlias(boolean value) {
    }

    public void setColor(int color) {
        if (color == 0) {
            Thread.currentThread().dumpStack();
        }
        this.color = color;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
}