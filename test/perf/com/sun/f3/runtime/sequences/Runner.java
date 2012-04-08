package org.f3.runtime.sequences;

import java.util.*;

/**
 *
 * @author Michael Heinrichs
 */
public class Runner {

    public static Map<String, TestResult> runTests(Script script, int iterations) {
        Map<String, TestResult> result = new HashMap<String, TestResult>();
        final int length = script.tests.size();
        TestResult[] resultList = new TestResult[iterations];
        for (int test=0; test<length; test++) {
            try {
                Test testClass = (Test)script.tests.get(test).newInstance();
                for (int it=0; it<iterations; it++) 
                    resultList[it] = testClass.start(script.args.get(test));
                result.put(script.commands.get(test) + " " + script.args.get(test), TestResult.average(resultList));
            } catch (Exception ex) {
                // ignore
            }
        }
        return result;
    }
}
