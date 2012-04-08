import java.lang.*;
/*
 * @test
 * @run/fail
 *
 * NOTE:
 * Should be @test and @run
 * When fixed move this to regress and capture correct output to .EXPECTED file.
 */
var add = function(a:Integer, b:Integer):Integer {
a+b
}
var mul = function(a:Integer, b:Integer):Integer {
a*b
}

function call (a) {
for(f in a) {
var fun = f as function(:Integer, :Integer):Integer;
System.out.println(fun(4,2));
}
}
var a = [add,mul];
for(f in a) {
var fun = f as function(:Integer, :Integer):Integer;
System.out.println(fun(4,2));
}
call(a);
