/*
 * @test/fail
 */

import java.lang.System;

function ArgsInfo( args : String[] ) {
 System.out.println("Number of args: {sizeof(args)}");
 System.out.print("Args:");
 for ( msg in args ) System.out.print( " {msg}" );
  System.out.println();
}

function run( )  {
  ArgsInfo(["hello","world"]);            //   ^^^^ no return value specified
}

//run() itself is implicitly public(?)
run()
