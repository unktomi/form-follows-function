import org.f3.runtime.F3Object;
import org.f3.runtime.InitHelper;
import org.f3.runtime.location.AbstractVariable;
import org.f3.runtime.location.IntVariable;


interface Base$Intf extends F3Object {
    public IntVariable get$a();
}

interface OtherBase$Intf extends F3Object {
    public IntVariable get$b();
}

interface Subclass$Intf extends Base$Intf, OtherBase$Intf {
    public IntVariable get$c();
}


class Base$Impl implements Base$Intf {
    private final IntVariable a = IntVariable.make();

    public Base$Impl() {
        addTriggers$(this);
    }

    public IntVariable get$a() { return a; }
    private AbstractVariable[] attributes = { a };

    protected static void addTriggers$(final Base$Intf receiver) {
        // Call superclass addTriggers$()
        // Add our triggers
    }

    protected static void initAttributes$(final Base$Intf receiver) {
        if (receiver.get$a().needDefault())
            receiver.get$a().set(3);
    }

    public static void userInit$(final Base$Intf receiver) { }

    public void initialize$() {
        initAttributes$(this);
        userInit$(this);
        InitHelper.finish(attributes);
        attributes = null;
    }
}

class OtherBase$Impl implements OtherBase$Intf {
    private final IntVariable b = IntVariable.make();

    public OtherBase$Impl() {
        addTriggers$(this);
    }

    public IntVariable get$b() { return b; }
    private AbstractVariable[] attributes = { b };

    protected static void addTriggers$(final OtherBase$Intf receiver) {
        // Call superclass addTriggers$()
        // Add our triggers
    }

    protected static void initAttributes$(final OtherBase$Intf receiver) {
        if (receiver.get$b().needDefault())
            receiver.get$b().set(4);
    }

    public static void userInit$(final OtherBase$Intf receiver) { }

    public void initialize$() {
        initAttributes$(this);
        userInit$(this);
        InitHelper.finish(attributes);
        attributes = null;
    }
}

public class Subclass$Impl implements Subclass$Intf {
    private final IntVariable a = IntVariable.make();
    private final IntVariable b = IntVariable.make();
    private final IntVariable c = IntVariable.make();
    private AbstractVariable[] attributes = { a, b, c };

    public Subclass$Impl() {
        addTriggers$(this);
    }

    public IntVariable  get$a() { return a; }
    public IntVariable  get$b() { return b; }
    public IntVariable  get$c() { return c; }

    protected static void addTriggers$(final Subclass$Intf receiver) {
        // Call superclass addTriggers$()
        // Add our triggers
    }

    protected static void initAttributes$(final Subclass$Intf receiver) {
        Base$Impl.initAttributes$(receiver);
        OtherBase$Impl.initAttributes$(receiver);
        if (receiver.get$c().needDefault())
            receiver.get$c().set(5);
    }

    public static void userInit$(final Subclass$Intf receiver) {
        Base$Impl.userInit$(receiver);
        OtherBase$Impl.userInit$(receiver);
    }

    public void initialize$() {
        initAttributes$(this);
        userInit$(this);
        InitHelper.finish(attributes);
        attributes = null;
    }

    public static void main(String[] args) {
        Subclass$Impl instance = new Subclass$Impl();
        instance.get$a().set(1);
        instance.get$b().set(2);
        instance.initialize$();
    }
}

