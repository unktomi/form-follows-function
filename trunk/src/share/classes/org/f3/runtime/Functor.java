package org.f3.runtime;
import org.f3.functions.*;

public interface Functor<This, X> extends TypeCons1<This, X> {
    public <Y> Functor<This, Y> map(Function1<? extends Y, ? super X> f);
}