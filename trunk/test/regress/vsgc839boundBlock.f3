/* Binding Overhaul test: block
 *
 * @test
 * @run
 */

import java.lang.System;

var enableBindingOverhaul;

function xxxxx() {
var i = 1;
var d = 1.0;
var ten = bind {var a = 3.7 * d; var b = 6.3; a+b};
var qs = bind {var hi = "hello"; var bye = "goodbye"; var gb = [hi, bye]; [gb, gb]};
var i20 = bind {var i2 = i*i; var i4 = i2*i2; var i10 = {var i6 = i4*i2; i6*i4}; i10*i10};

System.out.println(ten);
d = 0;
System.out.println(ten);
System.out.println(qs);
System.out.println(i20);
i = 2;
System.out.println(i20);
}

xxxxx()
