package org.f3.runtime.sequences;

import java.util.*;
import java.io.*;

public class Script {
    public List<Class<?>> tests = new ArrayList<Class<?>>();
    public List<String> commands = new ArrayList<String>();
    public List<String> args = new ArrayList<String>();

    private static Map<String, Class<?>> testMap = new HashMap<String, Class<?>>();
    static {
        try {
            testMap.put("JE", Class.forName("org.f3.runtime.sequences.JPEGEncoder"));
        } catch (Exception ex) {
            // ignore
        }
        try {
            testMap.put("FM", Class.forName("org.f3.runtime.sequences.FractalMadness"));
        } catch (Exception ex) {
            // ignore
        }
    }
    
    public static Script readFromFile(String script) {
        Script result = new Script();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(script));
            String line;
            String[] parts;
            Class testClass;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;   // ignore comments
                if ((line = line.trim()).equals("")) 
                    continue;  // ignore empty lines

                parts = line.split(" ", 2);
                testClass = testMap.get(parts[0]);
                if (testClass == null) {
                    System.err.println("Error while reading test-script");
                    System.err.println("Command not found: " + line);
                    reader.close();
                    return null;
                }
                result.tests.add(testClass);
                result.commands.add(parts[0]);
                result.args.add(parts[1]);
            }

            reader.close();
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFound: " + script);
            return null;
        } catch (IOException ex) {
            System.err.println("IOException while reading test-script: " + ex.getLocalizedMessage());
            return null;
        }
        return result;
    }
}
