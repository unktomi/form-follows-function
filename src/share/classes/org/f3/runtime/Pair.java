package org.f3.runtime;
import org.f3.functions.*;

/**
 * Represents a pair of values of types 'a' and 'b' respectively
 *
 * For the mathematically inclined:
 *    Pair of (a, b) = a x b
 *
 * Pair implements Functor and Monad which means you can use it in 
 * a 'for' loop.
 */    

public class Pair<a, b> implements Monad<Pair, a>, Comonad<Pair, a>, TypeCons2<Pair, a, b> 
{

    public final a first;
    public final b second;

    public Pair(a x, b y) {
	first = x;
	second = y;
    }

    public int hashCode() {
        int hashCode = 1;
        Object e = first;
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        e = second;
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }

    public boolean equals(Object obj) {
	if (obj instanceof Pair) {
	    Pair p = (Pair)obj;
	    return Checks.equals(first, p.first) && Checks.equals(second, p.second);
	}
	return false;
    }

    public <c> Pair<c, b> map(Function1<? extends c, ? super a> f) {
	return new Pair<c, b>(f.invoke(first), second);
    }

    public <c> Pair<c, b> flatmap(Function1<? extends Pair<c, b>, ? super a> f) {
	return f.invoke(first);
    }

    public <c> Pair<c, b> 
	coflatmap(Function1<? extends c, ? super Pair<a, b> > f) {
	return both(f.invoke(this), second);
    }
    
    public <c> Pair<Pair<a, b>, c> $comma(c x) {
	return new Pair<Pair<a, b>, c>(this, x);
    }

    public static <a, b> Pair<a, b> both(a x, b y) {
	return new Pair<a, b>(x, y);
    }

    public Pair<b, a> swap() {
	return new Pair<b,a>(second, first);
    }

    public String toString() {
	return "("+toString(first, ", ") + toString(second, ")");
    }
    
    private String toString(Object x, String sep) {
	String r = "";
	if (x instanceof Pair) {
	    Pair p = (Pair)x;
	    r += toString(p.first, ", ");
	    r += toString(p.second, sep);
	} else {
	    r += x;
	    r += sep;
	}
	return r;
    }

}