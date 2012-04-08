/**
 * Ensure that malformed try sequences are caught nicely be the parser/lexer
 * @test/compile-error
 */

// Orphaned catch construct
//
catch (e : Exception) {}
catch (e : Exception) {}

// Orphaned finally caonstruct
//
finally {}
finally {}

// No finally or catch
//
try {

   var v : Integer = 0;
}

// Finally and catch out of order
//
try {

   var v : Integer = 0;
}
finally {
}
catch (e : Exception )
{
}

// Too many finally clauses
//
try { }
catch ( e : Exception) {}
finally {}
finally {}


// Too many finally clauses and out of order
//
try { }
finally {}
catch ( e : Exception) {}
finally {}
catch ( e : Exception) {}







