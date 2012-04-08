/*
 * Feature test #8 -- replace triggers
 * Demonstrates simple replace triggers
 *
 * @test
 * @run
 */

import java.lang.System;

class Foo {
    var x : Integer
        on replace = newV { System.out.println("x: =>{newV}={x}"); };
    var y : Integer
        on replace { System.out.println("y: {y}"); };
    var z : String = "Ralph"
        on replace { System.out.println("z: {z}"); };
}

var n = 3;
var f = Foo { x: 3, y: bind n+1 };
f.x = 4;
f.x = 4;
n = 10;

class Foo2 {
    var x : Integer
        on replace oldValue { System.out.println("x: {oldValue} => {x}"); };
    var y : Integer
        on replace oldValue { System.out.println("y: {oldValue} => {y}"); };
    var z : String = "Bert"
        on replace oldValue = newValue { System.out.println("z: {oldValue} => {newValue}={z}"); };
}

var n2 = 3;
var f2 = Foo2 { x: 3, y: bind n2+1 };
f2.x = 4;
f2.x = 4;
n2 = 10;
f2.z = "Zoey"

