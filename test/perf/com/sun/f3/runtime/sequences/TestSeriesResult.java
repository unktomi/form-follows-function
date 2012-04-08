package org.f3.runtime.sequences;

import java.util.*;

public class TestSeriesResult {
    public String timestamp = "";
    public String description = "";
    
    public Map<String, TestResult>data = new LinkedHashMap<String, TestResult>();
    
    public String toString(Iterator<String> tests) {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp);
        sb.append(",");
        sb.append(description);
        sb.append(",");
        TestResult cur;
        while (tests.hasNext()) {
            if ((cur = data.get(tests.next())) != null) {
                sb.append(cur.toString());
            } else {
                sb.append(TestResult.EMPTY.toString());
            }
        }
        return sb.toString();
    }
    
    public static TestSeriesResult parse(String line, Iterator<String> tests, String testResultFormat) {
        String[] parts = line.substring(0, line.length()-1).split(",", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException();
        }
        
        TestSeriesResult result = new TestSeriesResult();
        result.timestamp = parts[0];
        result.description = parts[1];
        
        List<TestResult> resultlist = TestResult.parse(parts[2], testResultFormat);
        for (int i=0; tests.hasNext(); i++) {
            result.data.put(tests.next(), resultlist.get(i));
        }
        
        return result;
    }
}
