package org.f3.media.audio;
import java.nio.FloatBuffer;

public interface Processor {
    public void reset();
    public void setPosition(int pos);
    public void process(FloatBuffer inBuffer,
                        FloatBuffer outBuffer,
                        int length,
                        int inChannels,
                        int outChannels);
}
