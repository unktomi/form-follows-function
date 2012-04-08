/*
 * Regression test: exceptions across functions, try-catch
 *
 * @test
 * @run
 */
import java.lang.System;
import java.io.*;

class exceptions {
    function getReader() : FileReader { new FileReader("I do not exist") }
    function doit() {
	try {
	    var rdr = getReader();
	    System.out.println("Read char: {rdr.read()}")
	} catch (exc :IOException) {
	    System.out.println("Caught")
	}
    }
}

var t = new exceptions;
t.doit()
