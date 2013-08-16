package f3.media.scene;

public interface AbstractMovie extends AbstractTexture {
    public void setURL(String url);
    public String getURL();
    public void play();
    public long pause();
    public boolean isPlaying();
    public long stop();
    public long stepForward();
    public long stepBackward();
    public void setVolume(float volume);
    public float getVolume();
    public void setLoopCount(int count);
    public int getLoopCount();
    public void setRate(float rate);
    public float getRate();
    public void setPosition(long pos);
    public long getPosition();
    public long getEndPosition();
    public void setPlaybackRate(float rate);
    public float getPlaybackRate();
    public void update();
}