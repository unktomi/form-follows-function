package org.f3.runtime;
import org.f3.functions.*;

public interface Comonad<This, X> extends Functor<This, X, Object>, Copointed<X> {
    public <Y> Comonad<This, Y> coflatmap(Function1<? extends Y, ? super Comonad<This, X>> f);
}