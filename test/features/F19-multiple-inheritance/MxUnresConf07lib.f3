/*
 * Classes with private (script-level) members
 *
 * @subtest MxUnresConf07
 */

public mixin class Mixin01 {
    var bar : Integer = 1;
}

public class Super01 {
    public-read var bar : String = "This is bar from Super01";
}

public class Super02 {
    var bar : Number = 2.2;
}

public mixin class Mixin02 {
    public-init var bar : String = "This is bar from Mixin02";
}

public mixin class Mixin03 {
    function foo() : Integer { 4 }
}

public class Super03 {
    public function foo() : String { "This is foo from Super03" }
}

public class Super04 {
    function foo() : Number { 5.5 }
}

public mixin class Mixin04 {
    public function foo() : String { "This is foo from Mixin04" }
}
