/*
 * Tests propagation of unreachableType.
 *
 * @test
 */
import java.lang.*;

var cl = ClassLoader {
    public function loadClass(name:String): Class {
        try {
            //return super.loadClass(name);
            return ClassLoader.loadClass(name);
        } catch (e: UnsupportedClassVersionError) {
            System.err.println("failed loading {name}");
            throw e;
        }
    }
}
