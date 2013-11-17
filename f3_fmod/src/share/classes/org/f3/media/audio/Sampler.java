package org.f3.media.audio;
import java.nio.ByteBuffer;

public interface Sampler {
    public void sample(Sound sound,
                       ByteBuffer slice1, int len1,
                       ByteBuffer slice2, int len2);
}
