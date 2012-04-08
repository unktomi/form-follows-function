/* regression test for the bug 861
 *
 * @test
 * @run
 */
import java.lang.*;
import java.util.*;

var dLocale = Locale.getDefault();
try {
    Locale.setDefault(Locale.US);

    var a1 = ##"Hello";
    System.out.println("a1={a1}");
    var a2 = ##"Hello2";
    System.out.println("a2={a2}");

} finally {
    Locale.setDefault(dLocale); 
}
