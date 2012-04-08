package org.f3.runtime.sequences;

public class ArgumentEvaluator {

    public static Main.Configuration evaluateArgs(String[] args) {
        Main.Configuration result = new Main.Configuration();
        for (int i=0; i<args.length; i++) {
            if (args[i].equalsIgnoreCase("-s"))
                if (++i < args.length) {
                    result.testScript = args[i];
                } else {
                    System.err.println("No parameter for test-script found.");
                    printUsage();
                    return null;
                }
            else if (args[i].equalsIgnoreCase("-o"))
                if (++i < args.length) {
                    result.logFileName = args[i];
                } else {
                    System.err.println("No parameter for log-file found.");
                    printUsage();
                    return null;
                }
            else if (args[i].equalsIgnoreCase("-i"))
                if (++i < args.length) {
                    try {
                        result.iterations = Integer.parseInt(args[i]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Unable to evaluate number of iterations." + args[i]);
                        return null;
                    }
                } else {
                    System.err.println("No parameter for number of iterations found.");
                    printUsage();
                    return null;
                }
            else if (args[i].equalsIgnoreCase("-d"))
                if (++i < args.length) {
                    result.description = args[i];
                } else {
                    System.err.println("No parameter for description found.");
                    printUsage();
                    return null;
                }
            else if (args[i].equalsIgnoreCase("-h")) {
                printUsage();
                return null;
            }
            else {
                System.err.println("Invalid parameter: " + args[i]);
                printUsage();
                return null;
            }
        }
        return result;
    }
    
    private static void printUsage() {
        System.out.println("Usage: java SequenceTest <options>");
        System.out.println("where possible options include:");
        System.out.println("-s <testscript>   Specify test-script");
        System.out.println("-o <logfile>      Specify log-file");
        System.out.println("-i <iterations>   Number of iterations");
        System.out.println("-d <description>  Description");
        System.out.println("-h                Prints this help");
    }
}
