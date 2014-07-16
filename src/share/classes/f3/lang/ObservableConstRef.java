package f3.lang;
import org.f3.functions.*;

public interface ObservableConstRef<a> extends ConstRef<a> {
    public <b> Function0<? extends Void> onReplace(final Function1<? extends b, ? super a> f);
}
