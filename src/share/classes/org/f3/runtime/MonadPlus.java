package org.f3.runtime;
import org.f3.functions.*;

public interface MonadPlus<This, X> extends Monad<This, X> {
    public Monad<This, X> mplus(Monad<This, X> m);
}