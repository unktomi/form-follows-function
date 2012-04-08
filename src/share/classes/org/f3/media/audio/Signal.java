package org.f3.media.audio;

public interface Signal extends Sound {
    public int getLength();
    public int getBufferSize();
    public int getChannels();
    public int getFormat();
    public int getFrequency();
    public SignalGenerator getSignalGenerator();
}
