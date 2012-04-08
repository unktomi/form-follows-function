import org.f3.runtime.F3Object;
import org.f3.runtime.InitHelper;
import org.f3.runtime.location.*;

/**
 * Dispatch$Impl
 *
 * @author Brian Goetz
 */

interface Base1$Intf extends F3Object {
    public IntLocation get$n();
    public void init$n(IntLocation location);

    public int foo(int a);
    public int moo(int a);
    public int bar(int a);

    public IntLocation foo$bound(IntLocation a);
    public IntLocation moo$bound(IntLocation a);
    public IntLocation bar$bound(IntLocation a);
}

interface Base2$Intf extends F3Object {
    public int bork(int a);
    public IntLocation bork$bound(IntLocation a);
}

interface Dispatch$Intf extends Base1$Intf, Base2$Intf {
}

class Base1$Impl implements Base1$Intf {
    private IntLocation n;

    Base1$Impl() {
        addTriggers$(this);
    }

    public IntLocation get$n() { return n; }
    public void init$n(IntLocation location) { n = location; }
    public void setDefaults$(InitHelper helper) { }
    public void userInit$() { }
    public void initialize$() { }
    public void setInitialized(Location l) { }
    public InitHelper getInitHelper$() { return null; }
    protected static void addTriggers$(final Base1$Intf receiver) { }

    protected static int foo(Base1$Intf receiver, int a) { return a + receiver.get$n().getAsInt() + 1; }
    public static int moo(Base1$Intf receiver, int a) { return a + receiver.get$n().getAsInt() + 2; }
    public static int bar(Base1$Intf receiver, int a) { return a + receiver.get$n().getAsInt() + 3; }

    public static IntLocation foo$bound(final Base1$Intf receiver, final IntLocation a) {
        return IntVariable.make(false, new IntBindingExpression() {
            public int computeValue() {
                return a.getAsInt() + receiver.get$n().getAsInt() + 1;
            }
        }, a);
    }

    public static IntLocation moo$bound(final Base1$Intf receiver, final IntLocation a) {
        return IntVariable.make(false, new IntBindingExpression() {
            public int computeValue() {
                return a.getAsInt() + receiver.get$n().getAsInt() + 2;
            }
        }, a);
    }

    public static IntLocation bar$bound(final Base1$Intf receiver, final IntLocation a) {
        return IntVariable.make(false, new IntBindingExpression() {
            public int computeValue() {
                return a.getAsInt() + receiver.get$n().getAsInt() + 3;
            }
        }, a);
    }

    public int foo(int a) { return foo(this, a); }
    public int moo(int a) { return moo(this, a); }
    public int bar(int a) { return bar(this, a); }


    public IntLocation foo$bound(IntLocation a) { return foo$bound(this, a); }
    public IntLocation moo$bound(IntLocation a) { return moo$bound(this, a); }
    public IntLocation bar$bound(IntLocation a) { return bar$bound(this, a); }
}

class Base2$Impl implements Base2$Intf {
    public void setDefaults$(InitHelper helper) { }
    public void userInit$() { }
    public void initialize$() { }
    public void setInitialized(Location l) { }
    public InitHelper getInitHelper$() { return null; }
    protected static void addTriggers$(final Base2$Intf receiver) { }

    Base2$Impl() {
        addTriggers$(this);
    }

    public static int bork(Base2$Intf receiver, int a) { return a + 4; }
    public static IntLocation bork$bound(Base2$Intf receiver, final IntLocation a) {
        return IntVariable.make(false, new IntBindingExpression() {
            public int computeValue() {
                return a.getAsInt() + 4;
            }
        }, a);
    }

    public int bork(int a) { return bork(this, a); }

    public IntLocation bork$bound(IntLocation a) { return bork$bound(this, a); }
}


public class Dispatch$Impl implements Dispatch$Intf {
    private IntLocation n;
    public IntLocation get$n() { return n; }
    public void init$n(IntLocation location) { n = location; }

    public void setDefaults$(InitHelper helper) { }
    public void userInit$() { }
    public void initialize$() { }
    public void setInitialized(Location l) { }
    public InitHelper getInitHelper$() { return null; }

    public static int foo(Dispatch$Intf receiver, int a) { return a + receiver.get$n().getAsInt() + 5; }

    public static IntLocation foo$bound(final Dispatch$Intf receiver, final IntLocation a) {
        return IntVariable.make(false, new IntBindingExpression() {
            public int computeValue() {
                return a.getAsInt() + receiver.get$n().getAsInt() + 5;
            }
        }, a);
    }

    public int foo(int a) { return foo(this, a); }
    public int moo(int a) { return Base1$Impl.moo(this, a); }
    public int bar(int a) { return Base1$Impl.bar(this, a); }
    public int bork(int a) { return Base2$Impl.bork(this, a); }


    public IntLocation foo$bound(IntLocation a) { return foo$bound(this, a); }
    public IntLocation moo$bound(IntLocation a) { return Base1$Impl.moo$bound(this, a); }
    public IntLocation bar$bound(IntLocation a) { return Base1$Impl.bar$bound(this, a); }
    public IntLocation bork$bound(IntLocation a) { return Base2$Impl.bork$bound(this, a); }
}

class Main {
    public static void main(String[] args) {

        ObjectLocation<Dispatch$Intf> f = null; /* Initialize this */

        // var x : Integer;
        IntLocation x = IntVariable.make();

        // var v1 = foo(3);
        IntLocation v1 = IntVariable.make(f.get().foo(3));

        // var v2 = bind foo(3);
        IntLocation tempA = Locations.unmodifiableLocation(IntVariable.make(3));
        IntLocation v2 = f.get().foo$bound(tempA);

        // var v3 = bind foo(x);
        IntLocation v3 = f.get().foo$bound(Locations.unmodifiableLocation(x));

        // var v4 = foo(bind x);
        IntLocation v4 = IntVariable.make();
        IntLocation functionResult = f.get().foo$bound(Locations.unmodifiableLocation(x));
        v4.setAsInt(functionResult.getAsInt());

        // var v5 = foo(bind x with inverse);
        IntLocation v5 = IntVariable.make();
        functionResult = f.get().foo$bound(x);
        v4.setAsInt(functionResult.getAsInt());
    }
}
