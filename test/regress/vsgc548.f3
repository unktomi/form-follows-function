/*
 * @test
 * @run
 */

public class One { 
    init { 
        println("init called on One"); 
    } 
    postinit { 
        println("postinit called on One"); 
    } 
} 

public class Two extends One { 
    init { 
        println("init called on Two"); 
    } 
    postinit { 
        println("postinit called on Two"); 
    } 
} 

public class Three extends Two { 
    init { 
        println("init called on Three"); 
    } 
    postinit { 
        println("postinit called on Three"); 
    } 
} 

public class Four extends Three { 
    init { 
        println("init called on Four"); 
    } 
    postinit { 
        println("postinit called on Four"); 
    } 
} 

function run() {
    var x = Four{} 
}

