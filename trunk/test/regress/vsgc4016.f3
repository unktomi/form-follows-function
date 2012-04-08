/*
 * Regression test: Java Store sources fails to compile with 1.3 compiler
 *
 * @test
 * @run
 */

function f() {
   var x = 1;
   try {throw new java.lang.Exception()}
   catch (ex:java.lang.Throwable){
      var message = ex;
      var y = bind x;
      x = 5;
      println(y);
   }
}

f();
