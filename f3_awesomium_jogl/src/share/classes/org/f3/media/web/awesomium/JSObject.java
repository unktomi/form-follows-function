package org.f3.media.web.awesomium;

public class JSObject {

    long handle;

    public String toString() {
	StringBuffer result = new StringBuffer();
	String sep = "{";
        for (Object id: getIds()) {
            result.append(sep);
            result.append(id);
            result.append(": ");
            Object value = get((String)id);
            if (value instanceof JSObject) {
                result.append("[Object]");
            } else {
                result.append(value);
            }
            sep = ", ";
        }
        result.append("}");
        return result.toString();
    }

    public static JSObject createFromHandle(long handle) {
        return new JSObject(handle);
    }

    protected void finalize() {
        if (handle != 0) {
            org.f3.runtime.Entry.deferAction(new java.lang.Runnable() {
                    public void run() {
                        Browser.destroy_js_object(handle);
                    }
                });
        }
    }

    public JSArray getIds() {
	return new JSArray(Browser.getPropertyNames(handle));
    }

    public JSArray getMethodIds() {
	return new JSArray(Browser.getMethodNames(handle));
    }

    public JSObject(long handle) {
	this.handle = handle;
    }

    public boolean has(String index) {
	return true; // @TODO
    }

    public Object get(String index) {
	return Browser.get(handle, index);
    }

    public void put(String index, Object value) {
	if (value == null) {
	    Browser.put_null(handle, index);
	} if (value instanceof JSArray) {
	    Browser.put_array(handle, index, ((JSArray)value).handle);
	} else if (value instanceof JSObject) {
	    Browser.put_object(handle, index, ((JSObject)value).handle);
	} else if (value instanceof Integer) {
	    Browser.put_int(handle, index, ((Integer)value).intValue());
	} else if (value instanceof Boolean) {
	    Browser.put_boolean(handle, index, ((Boolean)value).booleanValue());
	} else if (value instanceof Number) {
	    Browser.put_double(handle, index, ((Number)value).doubleValue());
	} else {
	    Browser.put_string(handle, index, value.toString());
	}
    }
}
