package org.f3.media.audio;

public interface AudioSystemFactory {

    public static final int DEFAULT = -1;

    public int getMaxChannels();
    public void setMaxChannels(int value);

    public int getMixBufferSize();
    public void setMixBufferSize(int size);

    public int getMixBufferCount();
    public void setMixBufferCount(int size);

    public AudioSystem createAudioSystem();

    public AudioDriver[] getAudioDrivers();
}
