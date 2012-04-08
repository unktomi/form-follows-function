package org.f3.media.audio;

/**
 * Common interface between Channel/ChannelGroup
 */

public interface ChannelNode {

    public AudioSystem getAudioSystem();

    public void setChannelGroup(ChannelGroup group);
    public ChannelGroup getChannelGroup();

    public void setPaused(boolean value);
    public boolean isPaused();

    // 0.0..1.0
    public void setVolume(float value);
    public float getVolume();

    public void setMute(boolean value);
    public boolean isMute();

    public void addDSP(DSP dsp);

    public void stop();

    public float[] getSpectrum(int channel, int size);
    public float[] getWaveData(int channel, int size);

}
