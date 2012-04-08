import java.lang.System;

/*
 * @test
 * @run
 */

class Component { 
} 

class Container extends Component { 
    var content : Component[]; 
} 

class Label extends Component { 
} 

var labels : Label[] = [Label {}]; 
var container = Container { 
    content: labels 
} 
var components : Component[] = labels;
insert Component{} into components;
