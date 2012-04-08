// To fix: remove the type declaration on the function params when fixes allow

/*
 * @test
 * @run
 */

import java.util.Date;
import java.lang.System;

class Alpha {
  function myop(x : Integer) {
    System.out.println("Alpha: Value={x}");
     if (x > 7) "blither" else "be";
  }
}

class Beta extends Alpha {
  function myop(x : Integer)  {
    System.out.println("Beta: Value={(x)}");
    System.out.println(super.myop(x));
    if (x > 7) "blather" else {var str="bop"; str}
  }
}

var c = Beta {};
System.out.println(c.myop(3));
System.out.println(c.myop(55));
