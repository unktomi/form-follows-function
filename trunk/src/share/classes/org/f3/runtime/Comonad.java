package org.f3.runtime;
import org.f3.functions.*;

public interface Comonad<This, A> extends Functor<This, A>, Copointed<A> {
    public <B> Comonad<This, B> coflatmap(Function1<? extends B, ? super Comonad<This, A>> f);
}