/*
 * @test
 * @run
 */

import java.lang.System;

public class TestOverride
{
   function method(a:String){
       System.out.println("String param");
   }
   function method(a:Number){
       System.out.println("Number param");
   }
   function method(a:Integer){
       System.out.println("Integer param");
   }
      public function testTestOverride() {
       var int:Integer = 1;
       var num:Number = 1.0;
       method("test");
       method(num);
       method(int);
   }
}

public function run(){
   def test = TestOverride{};
   test.testTestOverride();
}
