package org.f3.media.audio;
import java.nio.ByteBuffer;

public interface SignalGenerator {
    public void generateSignal(ByteBuffer output, int length);
}
