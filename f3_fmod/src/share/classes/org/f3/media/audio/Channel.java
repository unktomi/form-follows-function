package org.f3.media.audio;
import f3.math.Vec3;
import f3.math.Point3;

public interface Channel extends ChannelNode {

    public void play(Sound sound);
    public void play(Sound sound, String path);
    public void play(Sound sound, int index);
    public void play(DSP dsp);
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

    public void setLocation(Point3 vec);
    public void setLocation(float x, float y, float z);
    public Point3 getLocation();

    public void setVelocity(Vec3 vec);
    public void setVelocity(float x, float y, float z);
    public Vec3 getVelocity();

    public DSP getDSPHead();

    public float getAudibility();

    public int getPosition();
    public void setPosition(int millis);

    public void setMinMaxDistance(float min, float max);
    public float getMinDistance();
    public float getMaxDistance();

    public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume);
    public Vec3 getSoundCone();

    public void setConeOrientation(float x, float y, float z);
    public Vec3 getConeOrientation();

}
