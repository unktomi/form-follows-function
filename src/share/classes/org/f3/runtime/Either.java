package org.f3.runtime;
import org.f3.functions.*;

/**
 * Represents a Choice between values of types 'a' and 'b' respectively
 *
 * For the mathematically inclined:
 *    Either of (a, b) = a + b
 *
 * Either implements Functor and Monad which means you can use it in 
 * a 'for' loop.
 *
 */    

public abstract class Either<a, b> implements Monad<Either, b> 
{
    public abstract <c> c either(Function1<? extends c, ? super a> f,
				 Function1<? extends c, ? super b> g); 
    public static <a, b> Either<a, b> former(a x) {
	return new Former<a, b>(x);
    }
    public static <a, b> Either<a, b> latter(b y) {
	return new Latter<a, b>(y);
    }
}

class Former<a, b> extends Either<a, b> 
{
    final a former;
    public Former(final a x) {
	former = x;
    }
    public <c> Either<a, c> map(Function1<? extends c, ? super b> f) {
	return new Former<a, c>(former);
    }
    public <c> Either<a, c> flatmap(Function1<? extends Either<a, c>, ? super b> f) {
	return new Former<a, c>(former);
    }
    public <c> c either(Function1<? extends c, ? super a> f,
			Function1<? extends c, ? super b> g) {
	return f.invoke(former);
    }
}

class Latter<a, b> extends Either<a, b> 
{
    final b latter;
    public Latter(final b y) {
	latter = y;
    }
    public <c> Either<a, c> map(Function1<? extends c, ? super b> f) {
	return new Latter<a,c>(f.invoke(latter));
    }
    public <c> Either<a, c> flatmap(Function1<? extends Either<a, c>, ? super b> f) {
	return f.invoke(latter);
    }
    public <c> c either(Function1<? extends c, ? super a> f,
			Function1<? extends c, ? super b> g) {
	return g.invoke(latter);
    }
}