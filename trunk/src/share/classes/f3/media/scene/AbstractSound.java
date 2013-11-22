package f3.media.scene;
import f3.math.Vec3;
import f3.math.Point3;
import f3.math.Tuple3;

public interface AbstractSound {

    public void setPaused(boolean value);
    public boolean isPaused();

    // 0.0..1.0
    public void setVolume(float value);
    public float getVolume();

    public void setMute(boolean value);
    public boolean isMute();

    public void stop();

    public float[] getSpectrum(int channel, int size);
    public float[] getWaveData(int channel, int size);

    public void play();
    public boolean isPlaying();

    public long getDelay();
    public void setDelay(long value);

    public void setFrequency(float value);
    public float getFrequency();

    // -1.0..1.0
    public void setPan(float value);
    public float getPan();

    // -1: loop forever
    //  0: 1 shot (no loop)
    //  1..N (loop N times)
    //  Default: -1
    public void setLoopCount(int value);
    public int getLoopCount();
    public void setLoopPoints(int start, int end);
    public int getLoopStart();
    public int getLoopEnd();

    public void setLocation(Point3 pt);
    public void setLocation(float x, float y, float z);
    public Point3 getLocation();

    public void setVelocity(Vec3 vec);
    public void setVelocity(float x, float y, float z);
    public Vec3 getVelocity();

    public float getAudibility();

    public int getPosition();
    public void setPosition(int millis);

    public void setMinMaxDistance(float min, float max);
    public float getMinDistance();
    public float getMaxDistance();

    public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume);
    public Tuple3 getSoundCone();

    public void setConeOrientation(float x, float y, float z);
    public Vec3 getConeOrientation();

    public long getDuration();
    public boolean is3D();
    public void set3D(boolean value);

}
