/*
 * Regression: VSGC-1184 - Compiler throws internal error while trying to access vararg method of superclass.
 *
 * @subtest
 *
 */

class vsgc1184J {
    public void method(int...i){
            System.out.println("VarArgMethod");
    }
}
