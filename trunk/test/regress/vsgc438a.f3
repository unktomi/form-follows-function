/* test for the string literal translation
 *
 * @test
 * @run
 */

import java.lang.System;
import java.util.*;

// save the default locale for testing
var curLoc = Locale.getDefault();

try {
    // set the default locale to Japan
    Locale.setDefault(Locale.JAPAN);

    System.out.println(##'Hello, World!');

    var bday = new GregorianCalendar(1995, Calendar.MAY, 23);
    System.out.println(##"Duke's birthday: {%1$tm bday} {%2$te bday}, {%3$tY bday}.");

    System.out.println(##'deceiving key1: /*');
    System.out.println(##'deceiving key2: //');

    // explicit key tests
    System.out.println(##[FILE_VERB]'File');
    System.out.println(##[FILE_NOUN]'File');
    System.out.println(##[NON_EXISTENT_KEY]'non-existent');
    System.out.println(##[KEY_WITH_SAME_VALUE]'key with same value default');

    // escape sequence tests in source file
    System.out.println(##'Hello,\u0020World!');
    System.out.println(##'Hello,\tWorld!');
    System.out.println(##'Hello,\'Single Quotes\'');
    System.out.println(##"Hello,\"Double Quotes\"");
    System.out.println(##'Unix style\nnew line');
    System.out.println(##'Windows style\r\nnew line');
    System.out.println(##'Mac style\rnew line');

    // escape sequence tests in properties file
    System.out.println(##'Hello, EscapeOctalSpace!');
    System.out.println(##'Hello, UnicodeEscapeSpace!');
    System.out.println(##'Hello,	Tab!');

    // multiple line tests - no longer supported so same as single line
    System.out.println(##'Unix style\nnew line');
    System.out.println(##'Windows style\r\nnew line');
    System.out.println(##'Mac style\rnew line');

} finally {
    // restore the default locale
    Locale.setDefault(curLoc);
}
