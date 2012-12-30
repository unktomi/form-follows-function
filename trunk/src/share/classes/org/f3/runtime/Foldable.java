package org.f3.runtime;
import org.f3.functions.*;

public interface Foldable<a> 
{
    public <b> b foldLeft(b zero, Function2<? extends b, ? super b, ? super a> add);
    public <b> b foldRight(b zero, Function2<? extends b, ? super a, ? super b> add);
}