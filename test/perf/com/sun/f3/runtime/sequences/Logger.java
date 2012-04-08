package org.f3.runtime.sequences;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.util.FileUtils;

/**
 *
 * @author Michael Heinrichs
 */
public class Logger {
    
    private LinkedHashSet<String>tests = new LinkedHashSet<String>();
    private List<TestSeriesResult>oldResults = new ArrayList<TestSeriesResult>();
    private TestSeriesResult newResults;
    private File logFile;

    public static boolean logTestResult(String logFileName, TestSeriesResult newResults) {
        return new Logger(logFileName, newResults).run();
    }
    
    private Logger(String logFileName, TestSeriesResult newResults) {
        this.logFile = new File(logFileName);
        this.newResults = newResults;
    }
    
    private boolean run() {
        if (logFile.exists()) {
            if (! readLogFile()) {
                return false;
            }
            try {
                FileUtils.getFileUtils().copyFile(logFile.getAbsolutePath(), logFile.getAbsolutePath()+".bak");
            } catch (IOException ex) {
                System.err.println("Error creating backup of log-file.");
                System.err.println("Cowardly refusing to overwrite old file.");
                return false;
            }
        }
        
        tests.addAll(newResults.data.keySet());
        
        return writeLogFile();
    }
     
    private boolean writeLogFile() {
        final int length = tests.size();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
            
            // print first line
            writer.write("Timestamp,Description,");
            for (String testname : tests) {
                writer.write(testname);
                writer.write(TestResult.EMPTY.toString());
            }
            writer.newLine();
            
            // print second line
            writer.write(",,");
            for (int i=0; i<length; i++) {
                writer.write(TestResult.FORMAT);
            }
            writer.newLine();
            
            // print old data
            for (TestSeriesResult cur : oldResults) {
                writer.write(cur.toString(tests.iterator()));
                writer.newLine();
            }
            
            // print new data
            writer.write(newResults.toString(tests.iterator()));
            writer.newLine();
            
            writer.close();
        } catch (IOException ex) {
            System.err.println("IOException while writing log-file: " + ex.getLocalizedMessage());
            return false;
        }
                
        return true;
    }
    
    private boolean readLogFile() {
        try {
            BufferedReader reader = new BufferedReader (new FileReader(logFile));
            String line;
            if ((line = reader.readLine()) == null) {
                System.err.println("Error reading old log-file");
                return false;
            }
            String[] parts = line.split(",");
            for (int i=2; i<parts.length; i++)
                tests.add(parts[i]);
            
            if ((line = reader.readLine()) == null) {
                System.err.println("Error reading old log-file");
                return false;
            }
            parts = line.split(",");
            StringBuilder sb = new StringBuilder(parts[2]);
            sb.append(",");
            for (int i=3; i<parts.length && !parts[2].equalsIgnoreCase(parts[i]); i++) {
                sb.append(parts[i]);
                sb.append(",");
            }
            String format = sb.toString();
            
            while ((line = reader.readLine()) != null) {
                oldResults.add(TestSeriesResult.parse(line, tests.iterator(), format));
            }
        } catch (IOException ex) {
            System.err.println("IOException while reading log-file: " + ex.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            System.err.println("Parsing error while reading log-file");
            return false;
        }
                
        return true;
    }
}
