package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<X> {
    public <Y> Monad<Y> map(Function1<? extends Y, ? super X> f);
    public <Y> Monad<Y> flatmap(Function1<? extends Monad<Y>, ? super X> f);
}