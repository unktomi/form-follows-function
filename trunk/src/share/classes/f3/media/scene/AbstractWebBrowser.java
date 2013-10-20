package f3.media.scene;

public interface AbstractWebBrowser {
    public void setURL(String url);
    public String getURL();
    public void setContent(String content);
    public String getContent();
    public void resize(int width, int height);
    public boolean update();
    public AbstractTexture grabTexture();
    public int getTextureId();
    public int getWidth();
    public int getHeight();
    public void injectMouseDown(int button);
    public void injectMouseUp(int button);
    public void injectMouseMove(int x, int y);
    public void injectMouseWheel(int x, int y);
    public void focus();
    public void unfocus();

    public static final int MOD_SHIFT_KEY = 1 << 0;
    public static final int MOD_CONTROL_KEY = 1 << 1;
    public static final int MOD_ALT_KEY = 1 << 2;
    public static final int MOD_META_KEY = 1 << 3;
    public static final int MOD_IS_KEYPAD = 1 << 4;
    public static final int MOD_IS_AUTOREPEAT = 1 << 5;
    
    public void injectKeyDown(int keyCode, int mods);
    public void injectKeyUp(int keyCode, int mods);
    public void injectKeyInput(int keyCode, int mods, char keyChar);

    public CursorType getCursorType();

    public Object executeJavascript(String script);

}