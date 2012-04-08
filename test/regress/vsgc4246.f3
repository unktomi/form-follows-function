/*
 * Regression test: usage of the cellFactory var cause a problem in compilation
 *
 * @test
 */

class ClassA {
    public var show =  false;
}

class ClassB extends ClassA{
    function changeShow(){
       var f = function() { var cell: Object = Object {} };
       show = true;
       this.show = true;
       (this as ClassA).show = true;
       super.show = true;
       (super as ClassA).show = true;
    }
}
