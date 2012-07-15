package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<This, Source, Target> extends Functor<This, Source, Target> {
    public <NewSource extends Target> Monad<This,? super NewSource, Target> 
	flatmap(Function1<? extends Monad<This,NewSource,Target>, ? super Source> f);
}