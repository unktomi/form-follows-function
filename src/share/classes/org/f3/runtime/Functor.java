package org.f3.runtime;
import org.f3.functions.*;

public interface Functor<This extends Functor, Source> extends TypeCons1<This, Source> {

    public <NewSource> Functor<This, ? super NewSource> 
	map(Function1<? extends NewSource, ? super Source> f);

}
