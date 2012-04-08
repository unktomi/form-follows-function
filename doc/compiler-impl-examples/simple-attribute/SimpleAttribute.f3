class SimpleAttribute {
    attribute a : Integer = 3
        on change { println("a is now {a}"); }

    init {
        println("a = {a}");
    }
}

var s1 = SimpleAttribute { }
println(s1.a);

var s2 = SimpleAttribute { a: 4 }
println(s1.a);
