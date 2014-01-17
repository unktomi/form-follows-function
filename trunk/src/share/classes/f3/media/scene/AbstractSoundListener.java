package f3.media.scene;
import f3.math.Vec3;
import f3.math.Point3;
import f3.math.Tuple3;

public interface AbstractSoundListener {
    public void setLocation(Point3 pos);
    public void setLocation(float x, float y, float z);
    public Point3 getLocation();

    public void setVelocity(Vec3 velocity);
    public void setVelocity(float x, float y, float z);
    public Vec3 getVelocity();

    public void setForwardOrientation(Vec3 vec);
    public void setForwardOrientation(float x, float y, float z);
    public Vec3 getForwardOrientation();
    
    public void setUpwardOrientation(Vec3 vec);
    public void setUpwardOrientation(float x, float y, float z);
    public Vec3 getUpwardOrientation();

    public void setWorldScale(float x, float y, float z);
    public Tuple3 getWorldScale();
}