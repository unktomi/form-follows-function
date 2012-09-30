package org.f3.runtime;
import org.f3.functions.*;

public interface Monad<This, A> extends Functor<This, A> {
    /*
    public <NewSource extends Target> Monad<This,? super NewSource, ? extends Target> 
	flatmap(Function1<? extends Monad<This,NewSource,Target>, ? super Source> f);
    */

    //@org.f3.runtime.annotation.F3Signature("((<A>)this<B>;)this<B>;")
    //public Object flatmap0(Function1<Object, Object> f);

    //public <B> Monad<This, B> coextract(B b);

}