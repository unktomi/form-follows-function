/**
 * regression test:  bind for
 * @test
 * @run
 */

import java.lang.System;

class Cell {var x: Number; var y: Number;}

var size = 10;
var grid = bind for (row in [1..size], col in [1..size]) Cell {x: col, y: row};
size = 4;
for (cell in grid) {System.out.println("x={cell.x} y={cell.y}");};
