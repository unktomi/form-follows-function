package org.f3.media.audio;

public interface SyncPoint {
    public String getName();
    public long getOffset(); // milliseconds
    public Sound getSound();
}
