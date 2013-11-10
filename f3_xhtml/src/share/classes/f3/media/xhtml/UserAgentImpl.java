package f3.media.xhtml;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.event.DocumentListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Iterator;


abstract class UserAgentImpl implements UserAgentCallback, DocumentListener {
    private static final int DEFAULT_IMAGE_CACHE_SIZE = 16;

    /**
     * a (simple) LRU cache
     */
    protected LinkedHashMap _imageCache;
    private int _imageCacheCapacity;
    private String _baseURL;


    /**
     * Creates a new instance of UserAgentImpl with a max image cache of 16 images.
     */
    public UserAgentImpl() {
        this(DEFAULT_IMAGE_CACHE_SIZE);
    }

    /**
     * Creates a new UserAgentImpl with a cache of a specific size.
     *
     * @param imgCacheSize Number of images to hold in cache before LRU images are released.
     */
    public UserAgentImpl(final int imgCacheSize) {
        this._imageCacheCapacity = imgCacheSize;

        // note we do *not* override removeEldestEntry() here--users of this class must call shrinkImageCache().
        // that's because we don't know when is a good time to flush the cache
        this._imageCache = new java.util.LinkedHashMap(_imageCacheCapacity, 0.75f, true);
    }

    /**
     * If the image cache has more items than the limit specified for this class, the least-recently used will
     * be dropped from cache until it reaches the desired size.
     */
    public void shrinkImageCache() {
        int ovr = _imageCache.size() - _imageCacheCapacity;
        Iterator it = _imageCache.keySet().iterator();
        while (it.hasNext() && ovr-- > 0) {
            it.next();
            it.remove();
        }
    }

    /**
     * Empties the image cache entirely.
     */
    public void clearImageCache() {
        _imageCache.clear();
    }

    /**
     * Gets a Reader for the resource identified
     *
     * @param uri PARAM
     * @return The stylesheet value
     */
    //TOdO:implement this with nio.
    protected InputStream resolveAndOpenStream(String uri) {
        java.io.InputStream is = null;
        uri = resolveURI(uri);
        try {
            is = new URL(uri).openStream();
        } catch (java.net.MalformedURLException e) {
            XRLog.exception("bad URL given: " + uri, e);
        } catch (java.io.FileNotFoundException e) {
            XRLog.exception("item at URI " + uri + " not found");
        } catch (java.io.IOException e) {
            XRLog.exception("IO problem for " + uri, e);
        }
        return is;
    }

    /**
     * Retrieves the CSS located at the given URI.  It's assumed the URI does point to a CSS file--the URI will
     * be accessed (using java.io or java.net), opened, read and then passed into the CSS parser.
     * The result is packed up into an CSSResource for later consumption.
     *
     * @param uri Location of the CSS source.
     * @return A CSSResource containing the parsed CSS.
     */
    public CSSResource getCSSResource(String uri) {
        return new CSSResource(resolveAndOpenStream(uri));
    }

    /**
     * Retrieves the image located at the given URI. It's assumed the URI does point to an image--the URI will
     * be accessed (using java.io or java.net), opened, read and then passed into the JDK image-parsing routines.
     * The result is packed up into an ImageResource for later consumption.
     *
     * @param uri Location of the image source.
     * @return An ImageResource containing the image.
     */
    public abstract ImageResource getImageResource(String uri);

    /**
     * Retrieves the XML located at the given URI. It's assumed the URI does point to a XML--the URI will
     * be accessed (using java.io or java.net), opened, read and then passed into the XML parser (XMLReader)
     * configured for Flying Saucer. The result is packed up into an XMLResource for later consumption.
     *
     * @param uri Location of the XML source.
     * @return An XMLResource containing the image.
     */
    public XMLResource getXMLResource(String uri) {
        InputStream inputStream = resolveAndOpenStream(uri);
        XMLResource xmlResource;
        try {
            xmlResource = XMLResource.load(inputStream);
        } finally {
            if ( inputStream != null ) try {
                    inputStream.close();
                } catch (IOException e) {
                    // swallow
                }
        }
        return xmlResource;
    }

    public byte[] getBinaryResource(String uri) {
        InputStream is = resolveAndOpenStream(uri);
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[10240];
            int i;
            while ( (i = is.read(buf)) != -1) {
                result.write(buf, 0, i);
            }
            is.close();
            is = null;

            return result.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }


    /**
     * Returns true if the given URI was visited, meaning it was requested at some point since initialization.
     *
     * @param uri A URI which might have been visited.
     * @return Always false; visits are not tracked in the UserAgentImpl.
     */
    public boolean isVisited(String uri) {
        return false;
    }

    /**
     * URL relative to which URIs are resolved.
     *
     * @param url A URI which anchors other, possibly relative URIs.
     */
    public void setBaseURL(String url) {
        _baseURL = url;
    }

    /**
     * Resolves the URI; if absolute, leaves as is, if relative, returns an absolute URI based on the baseUrl for
     * the agent.
     *
     * @param uri A URI, possibly relative.
     *
     * @return A URI as String, resolved, or null if there was an exception (for example if the URI is malformed).
     */
    public String resolveURI(String uri) {
        if (uri == null) return null;
        String ret = null;
        if (_baseURL == null) {//first try to set a base URL
            try {
                URL result = new URL(uri);
                setBaseURL(result.toExternalForm());
            } catch (MalformedURLException e) {
                try {
                    setBaseURL(new File(".").toURI().toURL().toExternalForm());
                } catch (Exception e1) {
                    XRLog.exception("The default UserAgentImpl doesn't know how to resolve the base URL for " + uri);
                    return null;
                }
            }
        }
        // test if the URI is valid; if not, try to assign the base url as its parent
        try {
            return new URL(uri).toString();
        } catch (MalformedURLException e) {
            XRLog.load("Could not read " + uri + " as a URL; may be relative. Testing using parent URL " + _baseURL);
            try {
                URL result = new URL(new URL(_baseURL), uri);
                ret = result.toString();
            } catch (MalformedURLException e1) {
                XRLog.exception("The default UserAgentImpl cannot resolve the URL " + uri + " with base URL " + _baseURL);
            }
        }
        return ret;
    }

    /**
     * Returns the current baseUrl for this class.
     */
    public String getBaseURL() {
        return _baseURL;
    }

    public void documentStarted() {
        shrinkImageCache();
    }

    public void documentLoaded() { /* ignore*/ }

    public void onLayoutException(Throwable t) { /* ignore*/ }

    public void onRenderException(Throwable t) { /* ignore*/ }
}
