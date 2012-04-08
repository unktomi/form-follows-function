package org.f3.runtime.sequences;

import java.util.*;
import java.text.SimpleDateFormat;

public class Main {

    public static class Configuration {
        public String testScript = "test_script.txt";
        public String logFileName = "test_report.csv";
        public int iterations = 5;
        public String description = "";
    }
    
    public static void main(String[] args) {
        Configuration config = ArgumentEvaluator.evaluateArgs(args);
        if (config == null) {
            System.exit(1);
        }
        
        Script script = Script.readFromFile(config.testScript);
        if (script == null) {
            System.exit(2);
        }
        
        TestSeriesResult testSeries = new TestSeriesResult();
        testSeries.timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
        testSeries.description = config.description;
        
        testSeries.data = Runner.runTests(script, config.iterations);

        if (! Logger.logTestResult(config.logFileName, testSeries)) {
            System.exit(3);
        }
    }    
}
