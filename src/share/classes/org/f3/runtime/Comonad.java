package org.f3.runtime;
import org.f3.functions.*;

//public interface Comonad<This, Source, Target> extends Functor<This, Source, Target>, Copointed<Source> {
public interface Comonad<This, A> extends Copointed<A>, TypeCons1<This, A> {
    /*
    public <NewSource extends Target> 
	Comonad<This, ? super NewSource, Target> 
	coflatmap(Function1<? extends NewSource, ? super Comonad<This, Source, Target>> f);
    */
}