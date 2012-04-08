/*
 * A protected member inherited by a subclass from another package is
 * not accessible to any other class in the subclass package, except for the
 * subclass' own subclasses.
 * 
 * package members can't be accessed from classes outside the package
 * Members without modifiers have default (script-only) access
 * public-init member has script-only write access
 * public-read member has script-only write access
 * Local variables can't have access modifiers
 * Functions can't be declared public-init or public-read
 *  
 * @subtest
 * 
 */


package pack1;
import java.lang.System;


public class AccessData{
   protected var months=["Jan","Feb","Mar",
   				"Apr","May","Jun","Jul",
   				"Aug","Sept","Oct","Nov",
   				"Dec"];
   
   package var a:Integer=13;
   public-init var b:Integer=15;
   package public-read var s:String="Howdy folks";
   public-read var greet:String="Good Morning friends";
   package var fp:function(:Number,:Integer):java.lang.Double;
   
   protected function fn():function():String{
   	     var x:Integer=10;
   	     function():String{	            
   	             "Hello world";	     	     
   	     };  
   }
   protected var x:function():String=fn();
   

	
   package bound function g (parameter: Integer): Integer {   
    	return this.a + parameter; 
    };
    
   protected public-read var fv=bind g(20);
   
   def max=100;
   
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
	System.out.println((new AccessData).months);
        var ref=AccessData{
	             b:30;   //public-init can be initialized from anywhere
		     }
       ref.b=45;   // writable from within the script only.
} 


