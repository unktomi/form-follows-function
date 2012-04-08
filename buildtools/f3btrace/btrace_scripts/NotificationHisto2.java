import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

/**
 * Print histogram of F3Base.notifyDependents$ calls every 10 sec.
 * Class name and variable number are used as key for the histogram.
 *
 * @author A. Sundararajan
 */
@BTrace public class NotificationHisto2 {
    private static Map<String, AtomicInteger> histo = newHashMap();

    @OnMethod(
        clazz="org.f3.runtime.F3Base",
        method="notifyDependents$"
    )
    public static void onNotifyDependents(@Self Object obj, int varNum, int phase) {
        String cn = name(classOf(obj)) +  ":" + varNum;
        AtomicInteger ai = get(histo, cn);
        if (ai == null) {
            ai = newAtomicInteger(1);
            put(histo, cn, ai);
        } else {
            incrementAndGet(ai);
        }     
    }

    @OnMethod(
        clazz="org.f3.runtime.F3Base",
        method="notifyDependents$"
    )
    public static void onNotifyDependents(@Self Object obj, int varNum, int startPos, int endPos, int newLength, int phase) {
        String cn = name(classOf(obj)) +  ":<seq>" + varNum;
        AtomicInteger ai = get(histo, cn);
        if (ai == null) {
            ai = newAtomicInteger(1);
            put(histo, cn, ai);
        } else {
            incrementAndGet(ai);
        }
    }

    @OnTimer(10000)
    public static void print() {
        if (size(histo) != 0) {
            printNumberMap("Notification Histogram", histo);
            println("======================================");
        }
    }
}
