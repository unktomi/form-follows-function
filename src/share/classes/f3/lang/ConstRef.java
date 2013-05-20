package f3.lang;
import org.f3.runtime.Monad;
import org.f3.runtime.Comonad;

// Refs/Pointers correspond to the identity monad/comonad

public interface ConstRef<a> extends Monad<ConstRef, a>, Comonad<ConstRef, a> {
    public a get();
}
