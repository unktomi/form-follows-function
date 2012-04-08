/*
 * @test
 */
import java.lang.*;
function square(x:Integer) :Integer{
System.out.println("Square of x {x}");
x*x;
}
var double = function(x:Integer):Integer{
System.out.println("Double of x {x}");
x+x;
}
var fnc = [square,double];

for(x:Integer in [1..10]){
        // NOTE: Added return type in function type.
        for(z:function(x:Integer):Integer in null){
                java.lang.System.out.println(" {z(x)} ");
        }
}
