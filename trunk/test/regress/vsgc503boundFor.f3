/* Binding Overhaul test: for-loop
 *
 * @test
 * @run
 */

import java.lang.System;

function xxxxx() {
  var modvalue = 2;
  var xs = [1..5];
  var bfseq = bind for (x in xs where x mod modvalue == 0) [ "bar", "foo" ];
  var extern = 5;
  var iseq = bind for (x in xs) x*extern;
  var sseq = bind for (x in ["be", "bop", "bong", "bip"]) x;
  var bseq = bind for (x in [true, true, false, true, false, false]) if (x) "X" else "O";

  System.out.println(bfseq);
  System.out.println(iseq);
  System.out.println(sseq);
  System.out.println(bseq);

  insert 88 into xs;

  System.out.println(bfseq);
  System.out.println(iseq);

  delete xs[0];

  System.out.println(bfseq);
  System.out.println(iseq);

  extern = 2;
  modvalue = 3;

  System.out.println(bfseq);
  System.out.println(iseq);
}

xxxxx()
