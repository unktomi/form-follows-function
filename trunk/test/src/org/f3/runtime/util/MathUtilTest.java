package org.f3.runtime.util;

import junit.framework.TestCase;

/**
 * TestMathUtil
 *
 * @author Brian Goetz
 */
public class MathUtilTest extends TestCase {
    public static final int COUNT = 50000000;

    static int random = (int) System.currentTimeMillis();

    public static int xorShift(int x) {
        x ^= (x << 6);
        x ^= (x >>> 21);
        x ^= (x << 7);
        return x;
    }

    public static int nextRandom(int bound) {
        int ret = random % bound;
        if (ret < 0)
            ret += bound;
        random = xorShift(random);
        return ret;
    }

    public void testLog2() {
        int sum = 0;
        for (int i=0; i<COUNT; i++) {
            int n = nextRandom(Integer.MAX_VALUE);
            if (n == 0)
                continue;
            int lg2 = MathUtil.log2(n);
            int log = (int) (Math.log((double) n) / Math.log((double) 2));
            sum += lg2 + log;
            if (lg2 != log)
                throw new AssertionError(String.format("lg2(%d)=%d, Math.log(%d)=%d, ignore=%d", n, lg2, n, log, sum));
        }

    }
}
