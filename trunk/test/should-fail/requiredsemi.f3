/**
 * Ensure that missing semi-colons are shown as errors when they are absolutely required.
 * @test/compile-error
 */

// Package statements must terminate with a semi colon
// unless it is the last statement, which woudl be pointless\
//
package pack.me.up  // Error

// Import directives must also terminate with a semi colon, unless the last 
// statement (which would be pointless)
//
import something.we.relyon	// Error

class G
{
	// Abstract function defintions do require a terminating
	// semi colon, but non abstract funcitons do not.
	//
	abstract function jim()		// Error
	abstract function idle()	// Good
}

function test1()
{
 	// Has a body
 	//
	var g;	// Good
}

// Override variables are definitions and must end in SEMI unless
// they are the last element of a block or script.
//
class local extends A
{
	override var t = 99		// Error
	override var g = 99		// Good

}

// Statement errors
//
{
  var x  // Error - requires SEMI
  var y  // Good
}

if (a) b else c   // Error Requires SEMI

// Test that if reset the semiIsOptinoal flag
//
g = 99 // Error

if (f)
{
  var x  // Error - requires SEMI
  var y  // Good
}

// The following are all valid, but will throw the test out if
// we accidentally flag them as errors
//
if (a) b else c;

if (a) {

var g;
var y;
if (a) g else c  // Good
}

// Language problem
// Both are valid syntactically, but did you mean that!
//
if (a) b = 8 else return
a = 9;

if (a) b = 8 else return;
a = 9;



