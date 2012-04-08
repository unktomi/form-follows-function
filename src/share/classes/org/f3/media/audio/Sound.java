package org.f3.media.audio;
import f3.math.Vec3;

public interface Sound {
    public class Util {
        public static int makeSample_PCM24(int b1, int b2, int b3) {
            b1 &= 0xff;
            b2 &= 0xff;
            return (b3 << 16) | (b2 << 8) | b1;
        }
    }

    public String getName();
    public SyncPoint[] getSyncPoints();
    public Subsound[] getSubsounds();
    public void setSubsound(int i, Sound sound);
    public void setPlaylist(Subsound playlist[]);
    public void setPlaylist(int playlist[]);
    public int getPlaylistSize();
    public long getDurationMillis();
    //    public SoundFormat getFormat();
    public int getFormat();
    public void set3d(boolean value);
    public boolean is3d();
    public void setMinMaxDistance(float min, float max);
    public float getMinDistance();
    public float getMaxDistance();

    public Tag[] getTags();
    public void dispose();
    public void sample(int offset, int length, Sampler sample);

    public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume);
    public Vec3 getSoundCone();

    public int getChannels();

}
