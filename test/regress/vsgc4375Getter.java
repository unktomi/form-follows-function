/*
 * Regression - VSGC-4375 - Compilation fails when using java classes with generics.
 *
 * @subtest
 *
 */

public interface vsgc4375Getter<T> {
    public T get();
}
