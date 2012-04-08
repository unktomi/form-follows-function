/**
 * The vicious test engine, controls subclasses of ViciousCheckI
 *
 * @subtest
 */

public abstract class ViciousEngineI {

  public var verbose = 0;
  
  public abstract function tests() : ViciousCheckI[];

  public function test() : Void {
    for (idx in [1..6]) {
      for (nCheck in [false, true]) {
        for (onrCheck in [false, true]) {
          if (verbose >= 1) println("Pass #{idx} {if (nCheck) 'nCheck ' else ''}{if (onrCheck) 'onrCheck' else ''}");
          for (t in tests()) {
            t.test(idx, nCheck, onrCheck, verbose);
          }
        }
      }
    }
  }
}
