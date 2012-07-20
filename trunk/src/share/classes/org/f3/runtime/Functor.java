package org.f3.runtime;
import org.f3.functions.*;

public interface Functor<This, Source, Target> extends TypeCons1<This, Source>, Functorial<Source> {
    public <NewSource extends Target> Functor<This, ? super NewSource, Target> 
	map(Function1<? extends NewSource, ? super Source> f);
}
