package org.f3.runtime.typeclass;
import org.f3.runtime.TypeCons1;
import org.f3.runtime.F3Base;
import org.f3.runtime.F3Object;
import org.f3.runtime.F3Mixin;
import org.f3.functions.Function1;
import org.f3.runtime.typeclass.Functor;

public interface Monad<F extends TypeCons1> extends Functor<F> {
    /*
    public abstract <a extends Object, b extends Object>TypeCons1<F, b> 
	flatmap(final TypeCons1<F, a> xs, final Function1<? extends TypeCons1<F, b>, ? super a> f);

    public abstract <a extends Object>TypeCons1<F, a> return(final a x);
    */
}
    
