package org.f3.runtime;
import org.f3.functions.*;
import java.util.Iterator;
/**
 * Represents a Choice between values of types 'a' and 'b' respectively
 *
 * For the mathematically inclined:
 *    Either of (a, b) = a + b
 *
 * Either implements Functor and Monad which means you can use it in 
 * a 'foreach' loop.
 *
 */    

public abstract class Either<a, b> implements Monad<Either, b> 
{
    public abstract <c> c match(Function1<? extends c, ? super a> f,
                                Function1<? extends c, ? super b> g); 

    public static <a, b> Either<a, b> former(a x) {
	return new Former<a, b>(x);
    }

    public static <a, b> Either<a, b> latter(b y) {
	return new Latter<a, b>(y);
    }

    public boolean isFormer() { return !isLatter(); }
    public boolean isLatter() { return !isFormer(); }

    static class Nothing<c> implements Iterable<c> {
        static class None<c> implements Iterator<c> {
            public boolean hasNext() {
                return false;
            }
            public c next() {
                throw new RuntimeException("nothing");
            }
            public void remove() {
                throw new RuntimeException("nothing");
            }
        };
        static None NONE = new None();
        public Iterator<c> iterator() {
            return (Iterator<c>)NONE;
        }
        static Nothing NOTHING = new Nothing();
        static <c> Iterable<c> instance() {
            return (Iterable<c>)NOTHING;
        }
    }

    public Iterable<a> getFormer() {
        if (isLatter()) {
            return Nothing.<a>instance();
        }
        return (Former)this;
    }

    public Iterable<b> getLatter() {
        if (isFormer()) {
            return Nothing.<b>instance();
        }
        return (Latter)this;
    }

    public Either<b, a> swap() {
	if (isFormer()) {
	    return Either.<b,a>latter(((Former<a,b>)this).former);
	} else {
	    return Either.<b,a>former(((Latter<a,b>)this).latter);
	}
    }

    public <c> Function1<? extends c, Function1<? extends c, ? super b>> 
	sub(final Function1<? extends c, ? super a> f) 
	{
	    return new Function1<c, Function1<? extends c, ? super b>> () 
		{
		    public c invoke(Function1<? extends c, ? super b> g) {
			return match(f, g);
		    }
		};
	}
}

final class Former<a, b> extends Either<a, b> implements Iterable<a>
{
    final a former;
    public Former(final a x) {
	former = x;
    }
    public boolean isFormer() { return true; }
    public <c> Either<a, c> map(Function1<? extends c, ? super b> f) {
	return new Former<a, c>(former);
    }
    public <c> Either<a, c> flatmap(Function1<? extends Either<a, c>, ? super b> f) {
	return new Former<a, c>(former);
    }
    public <c> c match(Function1<? extends c, ? super a> f,
                       Function1<? extends c, ? super b> g) {
	return f.invoke(former);
    }
    public boolean equals(Object obj) {
	if (obj instanceof Former) {
	    Former f = (Former)obj;
	    return former == f.former || (former != null && former.equals(f.former));
	}
	return false;
    }

    public Iterator<a> iterator() {
        return new Iterator<a>() {
            boolean hasNext = true;
            public boolean hasNext() {
                return hasNext;
            }
            public a next() {
                hasNext = false;
                return former;
            }
            public void remove() {
            }
        };
    }
}

final class Latter<a, b> extends Either<a, b> implements Iterable<b>
{
    final b latter;
    public Latter(final b y) {
	latter = y;
    }
    public boolean isLatter() { return true; }
    public <c> Either<a, c> map(Function1<? extends c, ? super b> f) {
	return new Latter<a,c>(f.invoke(latter));
    }
    public <c> Either<a, c> flatmap(Function1<? extends Either<a, c>, ? super b> f) {
	return f.invoke(latter);
    }
    public <c> c match(Function1<? extends c, ? super a> f,
                       Function1<? extends c, ? super b> g) {
	return g.invoke(latter);
    }
    public boolean equals(Object obj) {
	if (obj instanceof Latter) {
	    Latter l = (Latter)obj;
	    return latter == l.latter || (latter != null && latter.equals(l.latter));
	}
	return false;
    }

    public Iterator<b> iterator() {
        return new Iterator<b>() {
            boolean hasNext = true;
            public boolean hasNext() {
                return hasNext;
            }
            public b next() {
                hasNext = false;
                return latter;
            }
            public void remove() {
            }
        };
    }
}