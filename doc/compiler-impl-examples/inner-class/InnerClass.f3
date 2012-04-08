class Outer {
    var o : Integer;
    var v = Middle {
        a : 1
        function toString() : String { "middle" }
        listener : Listener {
            function onEvent() { println(a + o); }
        }
    }
}

class Middle {
    public var a : Integer;
    public var listener : Listener;
}

abstract class Listener {
    abstract function onEvent() : Void;
}
