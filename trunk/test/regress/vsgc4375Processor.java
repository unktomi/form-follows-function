/*
 * Regression - VSGC-4375 - Compilation fails when using java classes with generics.
 *
 * @subtest
 *
 */

public class vsgc4375Processor {
    public <T> T process(vsgc4375Getter<T> getter) {
        return getter.get();
    }
}
