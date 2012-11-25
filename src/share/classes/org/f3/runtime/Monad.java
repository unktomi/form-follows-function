package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<This extends Monad, A> extends Functor<This, A> {
    /*
    public abstract <b extends java.lang.Object> Monad<This, b> 
	flatmap(Function1<? extends Monad<This, b>, ? super A> f);
    */
}