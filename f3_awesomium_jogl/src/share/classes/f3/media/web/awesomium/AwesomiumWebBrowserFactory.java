package f3.media.web.awesomium;
import f3.media.scene.*;

public class AwesomiumWebBrowserFactory implements AbstractWebBrowserFactory {
    public AbstractWebBrowser newInstance() {
	return new org.f3.media.web.awesomium.Browser();
    }
}