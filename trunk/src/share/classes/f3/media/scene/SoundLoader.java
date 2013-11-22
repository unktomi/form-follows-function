package f3.media.scene;

public interface SoundLoader {
    public void update();
    public AbstractSound loadStream(String url);
    public AbstractSound loadSample(String url);
}
