package f3.media.scene.meshdeform;
import java.nio.*;
import java.util.List;
import java.util.ArrayList;

public class Intersector {

    public final float EPSILON = 0.000001f;

    final Vec3f pointA = new Vec3f();
    final Vec3f pointB = new Vec3f();
    final Vec3f pointC = new Vec3f();

    final Vec3f diff = new Vec3f();
    final Vec3f edge1 = new Vec3f();
    final Vec3f edge2 = new Vec3f();
    final Vec3f norm = new Vec3f();
    final Vec3f tmp1 = new Vec3f();
    final Vec3f rayOrigin = new Vec3f();
    final Vec3f rayDirection = new Vec3f();
    final List<Hit> result = new ArrayList();

    public static class Hit {
        public final float t;
        public final float w1;
        public final float w2;
        public final int vert;
        public Hit(float t, float w1, float w2, int v) {
            this.t = t;
            this.w1 = w1;
            this.w2 = w2;
            this.vert = v;
        }
    }

    public List<Hit> intersect(float x, float y, float z, 
                               float dx, float dy, float dz,
                               IntBuffer ib, 
                               FloatBuffer verts) 
    {
        result.clear();
        rayOrigin.set(x, y, z);
        rayDirection.set(dx, dy, dz);
        rayDirection.normalize();
        final int count = ib.limit();
        for (int i = 0; i < count; i += 3) {
            int v = ib.get(i) * 3;
            pointA.set(verts.get(v+0), verts.get(v+1), verts.get(v+2));
            v = ib.get(i+1) * 3;
            pointB.set(verts.get(v+0), verts.get(v+1), verts.get(v+2));
            v = ib.get(i+2) * 3;
            pointC.set(verts.get(v+0), verts.get(v+1), verts.get(v+2));
            
            diff.set(rayOrigin).sub(pointA);
            edge1.set(pointB).sub(pointA);
            edge2.set(pointC).sub(pointA);
            norm.cross(edge1, edge2);
            float dirDotNorm = rayDirection.dot(norm);
            float sign;
            if (dirDotNorm > EPSILON) {
                sign = 1.0f;
            } else if (dirDotNorm < -EPSILON) {
                sign = -1.0f;
                dirDotNorm = -dirDotNorm;
            } else {
                // ray and triangle are parallel
                continue;
            }
            tmp1.cross(diff, edge2);
            final float dirDotDiffxEdge2 = sign * rayDirection.dot(tmp1);
            if (dirDotDiffxEdge2 >= 0.0f) {
                tmp1.cross(edge1, diff);
                final float dirDotEdge1xDiff = sign * rayDirection.dot(tmp1);
                if (dirDotEdge1xDiff >= 0.0f) {
                    if (dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm) {
                        final float diffDotNorm = -sign * diff.dot(norm);
                        if (diffDotNorm >= 0.0) {
                            final float inv =  1.0f / dirDotNorm;
                            final float t = diffDotNorm * inv;
                            final float w1 = dirDotDiffxEdge2 * inv;
                            final float w2 = dirDotEdge1xDiff * inv;
                            // w0 = 1.0-(w1+w2);
                            result.add(new Hit(t, w1, w2, i / 3));
                        } 
                    }
                }
            }
        }
        return result;
    }
}