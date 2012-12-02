package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<This extends Monad, A> extends Functor<This, A> {
    //public abstract <B> Monad<This, B> flatmap(Function1<? extends Monad<This,B>, ? super A> f);
    //public abstract <B> Monad flatmap(Function1 f);
}