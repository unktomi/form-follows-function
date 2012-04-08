package org.f3.runtime.sequences;

import java.util.*;

/**
 *
 * @author Michael Heinrichs
 */
public class TestResult {
    public static final String FORMAT = "time,";
    public static final TestResult EMPTY = new TestResult(0);
    
    private final int time;
    public int getTime() {
        return time;
    }
    
    public TestResult(int time) {
        this.time = time;
    }
    
    public static TestResult average(TestResult... resultlist) {
        int length = resultlist.length;
        int sum_time = 0;
        for (TestResult cur : resultlist) {
            if (!cur.equals(EMPTY))
                sum_time += cur.time;
            else
                length--;
        }
        if (sum_time == 0 || length == 0)
            return EMPTY;
        return new TestResult(sum_time /= resultlist.length);
    }
    
    @Override
    public String toString() {
        if (time == 0)
            return ",";
        else
            return Integer.toString(time) + ",";
    }
    
    public static List<TestResult> parse(String line, String format) {
        List<TestResult> result = new ArrayList<TestResult>();
        if (format.equalsIgnoreCase("time,")) {
            String[] parts = line.split(",", -1);
            for (String s : parts) {
                if (s.length() == 0) {
                    result.add(TestResult.EMPTY);
                } else {
                    result.add(new TestResult(Integer.parseInt(s)));
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof TestResult) {
            return ((TestResult)obj).time == this.time;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return time;
    }
}
