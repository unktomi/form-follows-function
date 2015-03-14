This project is a work in progress - resurrecting the F3 programming language.

Here's the quickest of overviews:

Syntax - "plain english"-ish

Lazy and reactive self-adjusting computation with mutation.

Higher order functions. Higher order generics.  Implicit values a la Scala.

```
// Hopefully, the syntax is nearly self explanatory, but here are the likely unfamiliar bits:
//
// ..a means all types subtypes of a, i.e. it's an upper bound
// a.. means all supertypes of a, i.e. it's a lower bound
//
// () is the unit type
// null is the bottom type
//
// 'bind' 'bound' 'const' 'var' 'is', 'of', 'function', 'from', 'to', 'with', 'the' are keywords:
//
// 'const'                - introduces constants
// 'var'                   -  introduces variables
// 'is'                     - assigns types
// 'bind'                 - introduces self-adjusting computations
// 'bound'              - introduces self-adjusting function definitions
//
// 'of'                     - introduces type parameters (to both classes and functions)
//
// 'function from (...) to ...'  - constructs function types (and function values)
// Can alternatively be written: to ... from (...). 'from' introduces the parameters, 
// 'to' introduces the return type. The 'from' part can be omitted if there are no parameters, the 'to' part
// can be omitted if the return type is '()'
//
// for example:

    const strings is java.util.ArrayList of String = ...;

    function id of a from (x is a) to a { x }

    const string_identity = id of String;

    println(id("hello")); // type parameter is inferred to a=String

    println(string_identity("hello")); 


```

```

// 'class' and 'interface' introduce classes, the latter being mixins. Higher order generics are supported

interface Functor of (class F of _) 
{
    abstract function map of (a, b)
        from (xs is F of a,
              f is function from a to b)
        to F of b;
}

const listFunctor = the Functor of List;  // performs implicit search (like Scala's implicitly), yes "the" is a keyword.

interface Monoid of a {
     public const zero is a;
     public abstract function add from (x is a, y is a) to a;
}

// implicit parameters are declared/passed with "with" - their order doesn't matter as the types are unique
function sum of a from (xs is a[]) to a with (m is the Monoid of a) 
{
    var r = m.zero;
    for (x in xs) {
        r = m.add(r, x);
    } 
    return r
}

const ConcatStrings = Monoid of String {
     override const zero = "";
     override function add from (x is String, y is String) to String { x.concat(y) }
}

var xs = ["Hello", " ", "World"];
println(sum(xs));  // ConcatStrings is used implicitly
// equivalent to:
println(sum(xs) with the Monoid of String);
// or fully explicit:
println(sum(xs) with ConcatStrings);


```

Definition site variance.
Correct handling of mixed variance.

```
interface List of ..a 
{
    public abstract function prepend of b from (this is List of b, x is b) to List of b;
    public abstract function flatten from (this is List of List of a) to List of a;
}
```


Monad / Comonad Comprehensions (foreach)
```


// Lazy infinite streams                                                              

public class Stream of a is Comonad of (Stream, a), Monad of (Stream, a) 
{

    public readonly var now is a;
    public readonly var later is Stream of a;

    // zip lifts a binary operator to one over Streams                                
    public function zip of (b, c)
        from (binOp is function from (a, b) to c,
                  xs is Stream of b) 
        to Stream of c
    {
        Stream of c
        {
            now: bind binOp(now, xs.now)
            later: bind later.zip(binOp, xs.later)
        }
    }

    // Functor.map lifts a unary operator to one over Streams                                 
    override public function map of b
       from (unaryOp is function from a to b) 
       to Stream of b
    {
        Stream of b
        {
            now: bind unaryOp(this.now)
            later: bind this.later.map(unaryOp)
        }
    }

    // Monad.flatmap lifts a Stream constructor to a unary operator over Streams            
    override public function flatmap of b
        from (ctor is function from a to Stream of b) 
        to Stream of b
    {
        const f = bound function from (x is a) to b { ctor(x).now }
        readonly var h = bind f(now);
        readonly var t = bind later.map(f);
        Stream of b
        {
            now: bind h
            later: bind t
        }
    }

    // Interestingly, there's an alternate implementation in terms of Comonad operations, 
    // which works for any Comonad, not just Streams.
    public function flatmap'' of b
        from (ctor is function from a to Stream of b)
        to Stream of b
    {
        coflatmap(function from (xs is Stream of a) to b
                  {
                      ctor(xs.extract()).extract()
                  });
    }

    // Comonad.coflatmap lifts a Stream deconstructor to a unary operator over Streams           
    override function coflatmap of b
        from (dtor is function from Stream of a to b) 
        to Stream of b
    {
        Stream of b
        {
            now: bind dtor(this)
            later: bind later.coflatmap(dtor)
        }
    }

    // extract - this is the counit of the Stream Comonad
    override public function extract from () to a { now }

    public bound function followedBy
         from (xs is Stream of a) 
         to Stream of a
    {
        Stream of a
        {
            now: bind now
            later: bind xs
        }
    }

    public bound function next
        to Stream of a
    {
        later
    }

    public bound function drop
        from (n is Integer)
        to Stream of a
    {
        if (n == 0) then this else later.drop(n-1)
    }

    public function take
        from (n is Integer)
        to a[]
    {
        var ys = this;
        foreach (i in [0..<n]) {
            const now = ys.now;
            ys = ys.later;
            now
        }
    }

    // Some specializations for Streams of Numbers

    public function + from (this is Stream of Number, ys is Stream of Number) to Stream of Number
    {
        zip(function from (x is Number, y is Number) to Number { x + y }, ys)
    }

    public function - from (this is Stream of Number, ys is Stream of Number) to Stream of Number
    {
        zip(function from (x is Number, y is Number) to Number { x - y }, ys)
    }

    public function * from (this is Stream of Number, ys is Stream of Number) to Stream of Number
    {
        zip(function from (x is Number, y is Number) to Number { x * y }, ys)
    }

    public function / from (this is Stream of Number, ys is Stream of Number) to Stream of Number
    {
        zip(function from (x is Number, y is Number) to Number { x / y }, ys)
    }

    public function sum from (this is Stream of Number) to Stream of Number
    {
        Stream of Number {
            now: 0.0;
            later: bind this + later;
        }
    }

    public bound function prepend
        from (x is a)
        to Stream of a
    {
        Stream of a
        {
            now: bind x;
            later: this;
        }
    }
}


// lifts a value to a Stream by repeating it indefinitely, i.e  x, x, x, ...          
// (this is the unit of the Stream Monad)
public function repeat of a
     from (x is a) 
     to Stream of a
{
    Stream of a
    {
        now: x
        override const later = this
    }
}

// Monad comprehensions (compiler generates calls to map and flatmap - like Scala)

// creates an infinite stream of 3s
readonly var xs = bind foreach (x in repeat(1), y in repeat(2)) { x + y }

// Comonad comprehensions (compiler generates calls to coflatmap)
// sigfpe's simple fir filter
readonly var ys = bind foreach (xs into xs') 2*xs'.now;
readonly var zs = bind foreach (ys into ys') 0.5*ys'.now + 0.5*ys'.later.now;

```