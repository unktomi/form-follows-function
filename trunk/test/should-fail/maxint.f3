/**
 * Ensure that large hex integers convert correctly but that integers that 
 * are too big are correctly rejected.
 * @test/compile-error
 */

var j : Integer = 0xFF000000;  // Should pass
var h : Integer = 0x1FF000000;  // Should fail
var g : Integer = 2147483647;	// Should pass
var i : Integer = 2147483648;	// Should fail
var m : Integer = -2147483648;	// Should pass
var n : Integer = -2147483649;	// Should fail
