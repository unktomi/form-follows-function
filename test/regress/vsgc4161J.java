/*
 * Regression: VSGC-4161 - Compiled bind optimization: new style functions need to be usable from Java
 *
 * @subtest
 *
 */

import org.f3.functions.*;

public class vsgc4161J {
    
    static public  Function2<Integer, Integer, Integer> getF() {
        return new Function2<Integer, Integer, Integer>() {
            public Integer  invoke(Integer x, Integer y) {
                return x + y;
            }
        };
    }
}
