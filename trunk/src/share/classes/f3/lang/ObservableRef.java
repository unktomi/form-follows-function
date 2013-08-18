package f3.lang;
import org.f3.functions.*;

public interface ObservableRef<a> extends Ref<a> {
    public <b> Function0<Void> onReplace(final Function1<? extends b, ? super a> f);
}
