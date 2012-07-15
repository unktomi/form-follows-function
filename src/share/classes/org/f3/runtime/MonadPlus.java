package org.f3.runtime;
import org.f3.functions.*;

public interface MonadPlus<This, X> extends Monad<This, X, Object> {
    public Monad<This, X, Object> mplus(Monad<This, X, Object> m);
}