/*
 * @test/compile-error
 */

class Box {
  var holders: Holder[] = bind for (i in [0..5]) Holder {
          box: this
          index: i
  }
}

class Holder {
  var box : Box;
  var index : Integer;
  var x = bind
      if (index <= 0) 0.0
      else box.holders[index-1].x + 1.0
}

Box{} 
