package org.f3.media.web.awesomium;
import java.lang.Iterable;
import java.util.Iterator;

public class JSArray implements Iterable<Object> {
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            int i = 0;
            public boolean hasNext() {
                return i < getSize();
            }
            public Object next() {
                Object r = get(i);
                i++;
                return r;
            }
            public void remove() {
            }
        };
    }

    long handle;

    protected void finalize() {
        org.f3.runtime.Entry.deferAction(new java.lang.Runnable() {
                public void run() {
                    //System.err.println("deleting js array handle: "+handle);
		    Browser.destroy_js_array(handle);
		}
	    });
    }

    public JSArray(long handle) {
	this.handle = handle;
    }

    public String toString() {
	StringBuffer result = new StringBuffer();
	String sep = "";
        result.append("[");
	for (int i = 0; i < getSize(); i++) {
	    result.append(sep);
	    result.append(get(i));
	    sep = ", ";
	}
	result.append("]");
	return result.toString();
    }

    public int getSize() {
	return Browser.getSize(handle);
    }

    public Object get(int index) {
	return Browser.get_element(handle, index);
    }

    public void put(int index, Object value) {
	if (value == null) {
	    Browser.put_null_element(handle, index);
	} if (value instanceof JSArray) {
	    Browser.put_array_element(handle, index, ((JSArray)value).handle);
	} else if (value instanceof JSObject) {
	    Browser.put_object_element(handle, index, ((JSObject)value).handle);
	} else if (value instanceof Integer) {
	    Browser.put_int_element(handle, index, ((Integer)value).intValue());
	} else if (value instanceof Boolean) {
	    Browser.put_boolean_element(handle, index, ((Boolean)value).booleanValue());
	} else if (value instanceof Number) {
	    Browser.put_double_element(handle, index, ((Number)value).doubleValue());
	} else {
	    Browser.put_string_element(handle, index, value.toString());
	}
    }
}

