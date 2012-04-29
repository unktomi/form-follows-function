package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<X> extends Functor<X> {
     public <Y> Monad<Y> flatmap(Function1<? extends Monad<Y>, ? super X> f);
}