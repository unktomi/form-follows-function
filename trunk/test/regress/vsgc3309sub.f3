/*
 * Assertion failure in Gen.java, visitBreak 
 *
 * @subtest
 */

class A {
   var a1;
   var a2;
}

A {
    a1 : A{a1:1, a2 : 2}; //crash 1
}

A {
    a1 : A{a1:1} //crash 2 - reproducible only if backend is latest JDK 7 javac
}
