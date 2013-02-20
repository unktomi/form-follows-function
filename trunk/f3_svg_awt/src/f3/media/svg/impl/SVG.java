package f3.media.svg.impl;

public class SVG {
    Picture picture;
    RectF bounds = new RectF();

    public SVG(Picture picture, RectF bounds) {
        this.picture = picture;
        this.bounds.set(bounds);
    }

    public void setLimits(RectF rect) {
        this.bounds.set(rect);
    }
}