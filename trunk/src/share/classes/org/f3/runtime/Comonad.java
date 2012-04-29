package org.f3.runtime;
import org.f3.functions.*;

public interface Comonad<A> extends Functor<A> {
    public <B> Comonad<B> coflatmap(Function1<? extends B, ? super Comonad<A> > f);
    public A extract();
}