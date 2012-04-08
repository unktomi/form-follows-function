/*
 * Regression test: anonymous inner classes extending a Java interface
 *
 * @test
 * @run
 */

import java.lang.Runnable;
import java.lang.System;

var r = Runnable {
    public function run() {
        System.out.println("running");
    }
};
r.run();
