package org.f3.media.web.awesomium;
import org.mozilla.javascript.*;

public class AwesomiumRhino {

    static class AweFunction extends FunctionObject {
        AweFunction(AweScriptable target,
                    String name) {
            super(name, null, target);
        }
        public Object call(Context cx, 
                           Scriptable scope, 
                           Scriptable thisObj, 
                           java.lang.Object[] args) 
        {
            return null;
        }
        
        public Scriptable
            construct(Context cx, Scriptable scope, 
                      java.lang.Object[] args) 
        {
            return null;
        }
    }

    public static Object convert (Object input) {
	if (input instanceof JSObject) {
	    return new AweScriptable((JSObject)input);
	} else if (input instanceof JSArray) {
	    return new AweScriptableArray((JSArray)input);
	}
	return input;
    }

    public static Object unconvert (Object input) {
	if (input instanceof AweScriptable) {
	    return ((AweScriptable)input).target;
	} else if (input instanceof AweScriptableArray) {
	    return ((AweScriptableArray)input).target;
	}
	return input;
    }

    public static class AweScriptableArray extends ScriptableObject {

	public String getClassName() {
	    return "AwesomiumArray";
	}

	JSArray target;

	public boolean has(String name, Scriptable start) {
	    if ("length".equals(name)) {
		return true;
	    }
	    return super.has(name, start);
	}

	public Object get(String name, Scriptable start) {
	    if ("length".equals(name)) {
		return getSize();
	    }
	    return super.has(name, start);
	}

	public int getSize() {
	    return target.getSize();
	}

	public AweScriptableArray(JSArray target) {
	    this.target = target;
	}

	public boolean has(int index, Scriptable start) {
	    return index >= 0 && index < getSize();
	}

	public Object get(int index, Scriptable start) {
	    return convert(target.get(index));
	}
    }

    public static class AweScriptable extends ScriptableObject {

	public String getClassName() {
	    return "AwesomiumObject";
	}

	JSObject target;

	public AweScriptable(JSObject target) {
	    this.target = target;
            JSArray methods = target.getMethodIds();
            for (Object obj: methods) {
                super.put(obj.toString(), this, 
                          new AweFunction(this, obj.toString()));
            }
	}

	public Object[] getIds() {
	    AweScriptableArray arr = (AweScriptableArray)
		convert(target.getIds());
	    int size = arr.getSize();
	    Object[] result = new Object[size];
	    for (int i = 0; i < result.length; i++) {
		result[i] = arr.get(i, arr);
	    }
	    return result;
	}

	public boolean has(String name, Scriptable start) {
	    return target.has(name);
	}

	public void put(String name, Scriptable start, Object value) {
	    target.put(name, unconvert(value));
	}

	public Object get(String name, Scriptable start) {
	    return convert(target.get(name));
	}
    }
}