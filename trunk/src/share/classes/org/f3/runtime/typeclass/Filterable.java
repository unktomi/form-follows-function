package org.f3.runtime.typeclass;
import org.f3.runtime.TypeCons1;
import org.f3.runtime.F3Base;
import org.f3.runtime.F3Object;
import org.f3.runtime.F3Mixin;
import org.f3.functions.Function1;
import org.f3.runtime.typeclass.Functor;

public interface Filterable<F extends TypeCons1> {
    //public abstract <a> TypeCons1<F, a> filter(TypeCons1<F, a> xs, Function1<? extends Boolean, ? super TypeCons1<F, a>> p);
}