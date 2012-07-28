package org.f3.runtime;
import org.f3.functions.*;

//public interface Functor<This, Source, Target> extends TypeCons1<This, Source> {
public interface Functor<This, A> extends TypeCons1<This, A> {
    /*
    public <NewSource extends Target> Functor<This, ? super NewSource, ? extends Target> 
	map(Function1<? extends NewSource, ? super Source> f);
    */
}
