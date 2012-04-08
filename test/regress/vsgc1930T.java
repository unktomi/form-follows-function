/*
 * Regression: VSGC-1930 - second-level inheritance of java interfaces and override.
 *
 * @subtest
 *
 */


public interface vsgc1930T extends Runnable { int getId(); }
