/*
 * @test
 * @run/fail
 * 
 */

public class One {
    function func() {
        java.lang.System.out.println("One.func() called");
    }
    
    public function pub() {
        func();
    }
}

public class Two extends One {
    function func() {
        java.lang.System.out.println("Two.func() called");
    }
}

function run( ) {
    Two{}.pub(); 
}
