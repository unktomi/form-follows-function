package org.f3.runtime;
import org.f3.functions.*;

public interface Foldable<a> 
{
    public <b> b foldLeft(b z, Function2<? extends b, ? super b, ? super a> f);
    public <b> b foldRight(Function2<? extends b, ? super a, ? super b> f, b z);
}