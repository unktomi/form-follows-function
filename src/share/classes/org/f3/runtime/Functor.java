package org.f3.runtime;
import org.f3.functions.*;

public interface Functor<This extends Functor, A> extends TypeCons1<This, A> {

    public <B> Functor<? extends This, ? extends B> 
	map(Function1<? extends B, ? super A> f);

}
