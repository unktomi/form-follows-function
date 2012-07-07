package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<This, X> extends Functor<This, X, Object> {
    public <Y> Monad<This,Y> flatmap(Function1<? extends Monad<This,Y>, ? super X> f);
}