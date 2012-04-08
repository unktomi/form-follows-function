/*
 * @test/fail
 */

/*
 * Cannot be more than one run() function
 */

import java.lang.System;

public function ArgsInfo( args : String[] ) {
 System.out.println("Number of args: {sizeof(args)}");
 System.out.print("Args:");
 for ( msg in args ) System.out.print( " {msg}" );
  System.out.println();
}

function run() : java.lang.Objec {
  var args:String[] = [ "Simple", "form", "of", "run", "can", "be","used","when","no","args","are","used"];
  ArgsInfo( args );
}

function run( runargs : String[]) : java.lang.Objec {
  ArgsInfo( runargs );
}

