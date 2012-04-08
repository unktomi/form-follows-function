/*
 * Regression test: An internal error: incompatible type
 *
 * @test
 */

var is:java.io.InputStream;
var buffer:String;
var ch:Integer;
var eos = false;
while(eos == false) {
  ch = is.read();
  buffer += is.read() as Character; //line 81
  if(ch == -1) {
    eos == true;
   }
}
