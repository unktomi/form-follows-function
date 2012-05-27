package org.f3.runtime;
import org.f3.functions.*;

public interface Functor<This, X> {
    public <Y> Functor<?, Y> map(Function1<? extends Y, ? super X> f);
}