package org.f3.runtime;
import org.f3.functions.Function1;

public class Id<a> implements Monad<Id, a> {

    public final a self;

    public Id(a x) {
	self = x;
    }

    public <b> Id<b> flatmap(Function1<? extends Id<b>, ? super a> f) {
	return f.invoke(self);
    }

    public <b> Id<b> map(Function1<? extends b, ? super a> f) {
	return new Id<b>(f.invoke(self));
    }
}