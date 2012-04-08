/*
 *
 * This test verifies the behavior of protected,public,public-read and public-init access modifiers:
 *
 * 1. protected members can be accessed by other classes in the same package, plus subclasses regardless of package.
 * 
 * 2. public members can be accessed(read/initialize/write) from anywhere
 *
 * 3. public-read members can be read from anywhere.
 *
 * 4. public-init can be read/initialized from anywhere, writable from within the script only.
 * 
 * @subtest
 *
 *
*/

package pack1;
import java.lang.System;


public class Base1{
   protected var months=["Jan","Feb","Mar",
   				"Apr","May","Jun","Jul",
   				"Aug","Sept","Oct","Nov",
   				"Dec"];
   
   protected var a:Integer=13;
   public-init var b:Integer=15;
   protected public-read var s:String="Howdy folks";
   public-read var greet:String="Good Morning friends";
   protected var fp:function(:Number,:Integer):java.lang.Double;
   
   protected function fn():function():String{
   	     var x:Integer=10;
   	     function():String{	            
   	             "Hello world";	     	     
   	     };  
   }
   protected var x:function():String=fn();
   

	
   protected bound function g (parameter: Integer): Integer {   
    	return this.a + parameter; 
    };
    
   protected public-read var fv=bind g(20);
   
   protected def max=100;
   
   function fn2(fparg:function(a1:Number,a2:Integer):java.lang.Double):java.lang.Double{
       var gparg:function(:Number,:Integer):java.lang.Double=fparg;       
       return 30.0;
    }

   protected function add(a1:Number,a2:Integer):java.lang.Double{
      a1+a2;  
  }

  public function flip(iseq:Integer[]):Integer[] {
	  var newseq:Integer[];
	  for(i in [sizeof iseq-1 .. 0 step -1]) { insert iseq[i] into newseq; }
	  return newseq;
 }


}

function run(){
	System.out.println((new Base1).months);
        var ref=Base1{
	             b:30;   //public-init can be initialized from anywhere
		     }
       ref.b=45;   // writable from within the script only.
} 


