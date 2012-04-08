/*
 * @test
 * @run
 */
class Test{
    var fn1:function(x:Integer):Integer
          on replace = newVa {java.lang.System.out.println("fn1 changed");}
    var getInstance:function():Test
          on replace = newVa {java.lang.System.out.println("getInst fn changed");}
    var fn2 : function():java.lang.Object
          on replace = newVa {java.lang.System.out.println("fn2 changed");}
}
var test = Test{};
test.getInstance = function():Test { test };
