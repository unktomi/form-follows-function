package org.f3.runtime;
import org.f3.functions.*;

//public interface MonadPlus<This, X> extends Monad<This, X, Object> {
public interface MonadPlus<This extends MonadPlus, A> extends Monad <This, A> {
    /*
    public Monad<This, X, Object> mplus(Monad<This, X, Object> m);
    */
}