package f3.lang;
import org.f3.runtime.F3Object;

public interface MemberRef<This extends F3Object> {
    public This getF3Object();
}