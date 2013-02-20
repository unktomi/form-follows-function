package f3.media.svg.impl;

public class RectF {

    public float left, right, top, bottom;

    public RectF() {
    }

    public RectF(float left, float top, float right, float bottom) {
        set(left, bottom, right, top);
    }

    public void set(RectF rect){
        if (rect == null) {
            set(0, 0, 0, 0);
        } else {
            set(rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }
}