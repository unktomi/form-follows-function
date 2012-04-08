/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package launchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.f3.api.F3ScriptEngine;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * This class primarily calls into f3rt, f3c and f3doc to ensure
 * that a conformant version strings are returned for:
 *   a. System properties.
 *   b. tools f3, f3c, and f3doc.
 */
public class Utils {

    private static File distDir = null;
    static File javaExe = null;
    static File f3Exe = null;
    static File f3wExe = null;
    static File f3cExe = null;
    static File f3docExe = null;
    static boolean debug = false;
    static File workingDir = null;
    static JavaCompiler javaCompiler = null;
    private static F3ScriptEngine engine = null;
    static final boolean isWindows =
            System.getProperty("os.name").startsWith("Windows");

    static final FileFilter CLASS_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".class");
        }
    };

    static final FileFilter JAR_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".jar");
        }
    };

    static final FileFilter F3_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".f3");
        }
    };

    static final FileFilter JAVA_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".java");
        }
    };

    static void init() throws IOException {
        if (Utils.javaExe == null) {
            Utils.javaExe = Utils.getJavaExe();
        }
        if (f3Exe == null) {
            f3Exe = getF3DistExe("f3");
        }
         if (f3wExe == null) {
            f3wExe = getF3DistExe("f3w");
        }
        if (f3cExe == null) {
            f3cExe = getF3DistExe("f3c");
        }
        if (f3docExe == null) {
            f3docExe = getF3DistExe("f3doc");
        }
        if (engine == null) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine scrEng = manager.getEngineByName("f3");
            if (scrEng instanceof F3ScriptEngine) {
                engine = (F3ScriptEngine) scrEng;
            } else {
                throw new AssertionError("scrEng not instance of F3SciptEngine");
            }

        }
        if (workingDir == null) {
            workingDir = Utils.getTempDirectory();
        }
        if (!workingDir.exists()) {
            workingDir.mkdirs();
        }
        javaCompiler = ToolProvider.getSystemJavaCompiler();
    }

    static void reset() {
        if (!debug) {
            if (workingDir != null && workingDir.exists()) {
                try {
                    Utils.recursiveDelete(workingDir);
                } catch (IOException ioe) { /*oh well!*/ }
            }
        }
    }

    static void deleteAllFiles() {
        deleteJavaFiles();
        deleteF3Files();
        deleteClassFiles();
        deleteJarFiles();
    }

    static void deleteJavaFiles() {
        deleteFiles(JAVA_FILTER);
    }

    static void deleteF3Files() {
        deleteFiles(F3_FILTER);
    }

    static void deleteClassFiles() {
        deleteFiles(CLASS_FILTER);
    }

    static void deleteJarFiles() {
        deleteFiles(JAR_FILTER);
    }

    static void deleteFiles(FileFilter filter) {
        File[] files = workingDir.listFiles(filter);
        for (File f : files) {
            f.delete();
        }
    }

    /*
     * get the path to where the dist directory lives, we use f3c.jar
     * and f3jdi.jar to get the lib path, the parent of which is the
     * basedir.
     */
    static File getDistDir() throws FileNotFoundException, IOException {
        if (distDir != null) {
            return distDir;
        }
        String javaClasspaths[] =
                System.getProperty("java.class.path", "").split(File.pathSeparator);
        for (String x : javaClasspaths) {
            if (x.endsWith("f3c.jar")) {
                String path = x.substring(0, x.indexOf("lib" +
                        File.separator + "f3c.jar"));
                distDir = new File(path, "dist").getAbsoluteFile();
                return distDir;
            } else if (x.endsWith("f3jdi.jar")) {
                String path = x.substring(0, x.indexOf("lib" +
                        File.separator + "f3jdi.jar"));
                distDir = new File(path, "dist").getAbsoluteFile();
                return distDir;
            }
        }
        throw new IOException("dist dir path could not be determined");
    }

    /**
     * Recursively deletes everything under dir
     */
    static void recursiveDelete(File dir) throws IOException {
        if (dir.isFile()) {
            dir.delete();
        } else if (dir.isDirectory()) {
            File[] entries = dir.listFiles();
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].isDirectory()) {
                    recursiveDelete(entries[i]);
                }
                entries[i].delete();
            }
            dir.delete();
        }
    }

    /*
     * Gets a temporary clean directory for use
     */
    static File getTempDirectory() throws IOException {
        File tmpDir = File.createTempFile("versionTest", ".tmp");
        if (tmpDir.exists()) {
            recursiveDelete(tmpDir);
        }
        tmpDir.mkdirs();
        return tmpDir;
    }

    /*
     * gets the path to the java.exe
     */
    static File getJavaExe() {
        String javaHome = System.getProperty("java.home");

        File jFile = new File(new File(javaHome, "bin"), "java");
        if (isWindows) {
            jFile = new File(jFile.toString() + ".exe");
        }
        return jFile;
    }

    /*
     * returns the path to a f3 executable, do not
     * use .exe extension the method will take care of
     * it.
     */
    static File getF3DistExe(String exename) throws IOException {
        File exe = new File(new File(getDistDir(), "bin"),
                (isWindows) ? exename + ".exe" : exename);
        if (!exe.exists()) {
            return null;
        }
        return exe;
    }

    /*
     *  Check to see if the expected pattern was obtained, if the expectedString
     *  is null, simply check if something was returned.
     */
    static boolean checkExec(List<String> cmds, String expectedString, boolean match) {
        List<String> outputList = doExec(cmds);
        if (expectedString == null) {
            if (outputList != null) {
                return true;
            }
        } else if (match) {
            if (outputList.get(0).matches(expectedString)) {
                return true;
            }
        } else {
            if (outputList.get(0).equals(expectedString)) {
                return true;
            }
        }

        // oh oh, something went wrong, print diagnostics to aid debugging
        System.err.println("Command line:");
        for (String x : cmds) {
            System.err.print(" " + x);
        }
        System.err.println("");
        System.err.println("Process output:");
        for (String x : outputList) {
            System.err.println(" " + x);
        }
        System.err.println("Expected string " + (match ? "match:" : "equals:") +
                ": " + expectedString);
        return false;
    }

    static boolean verifyArguments(List<String> output, String[] expectedArgs) {
        if (expectedArgs.length != output.size()) {
            return false;
        }
        for (int i = 0; i < expectedArgs.length; i++) {
            if (!expectedArgs[i].equals(output.get(i))) {
                return false;
            }
        }
        return true;
    }

    static boolean checkExec(List<String> cmds, String... expectedArgs) 
            throws IOException {
        if (!verifyArguments(getArgumentsFromF3(cmds), expectedArgs)) {
            return false;
        }
        if (!verifyArguments(getArgumentsFromJava(cmds), expectedArgs)) {
            return false;
        }
        if (isWindows) {
            if (!verifyArguments(getArgumentsFromJavawF3(cmds), expectedArgs)) {
                return false;
            }
            if (!verifyArguments(getArgumentsFromJavawJava(cmds), expectedArgs)) {
                return false;
            }
        }
        return true;
    }

    static List<String> doExec(List<String> cmds) {
        return doExec(cmds, null, false);
    }

    /*
     * execs a cmd and returns a List representation of the output,
     * if a testoutput file is provide that file is read for the programs
     * output instead of stdout and stderr.
     */
    static List<String> doExec(List<String> cmds, File testOutput,
            boolean ignoreExitValue) {
        if (debug) {
            System.out.println("CWD: " + workingDir);
            System.out.println("----Execution args----");
            for (String x : cmds) {
                System.out.println(x);
            }
        }
        List<String> outputList = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb = pb.directory(workingDir);
        FileReader      fr = null;
        BufferedReader  rdr = null;
        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // note: its a good idea to read the whole stream, half baked
            // reads can cause undesired side-effects.
            String in = rdr.readLine();
            if (debug) {
                System.out.println("---output---");
            }
            while (in != null) {
                if (debug) {
                    System.out.println(in);
                }
                outputList.add(in);
                in = rdr.readLine();
            }
            p.waitFor();
            p.destroy();
            if (ignoreExitValue == false && p.exitValue() != 0) {
                System.out.println("Error: Unexpected exit value " +
                        p.exitValue());
                return null;
            }
            if (testOutput != null) {
                fr = new FileReader(testOutput);
                rdr = new BufferedReader(fr);
                outputList.clear();
                if (debug) {
                    System.out.println("---file output---");
                }
                in = rdr.readLine();
                while (in != null) {
                    if (debug) {
                        System.out.println(in);
                    }
                    outputList.add(in);
                    in = rdr.readLine();
                }
            }
            return outputList;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ioe) {
                    throw new RuntimeException("Error while closing file " + ioe);
                }
            }
        }
    }

    /*
     * emit our F3 code
     */
    static String emitVersionF3(boolean fullversion) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.print("java.lang.System.out.print(F3.getProperty(\"");
        pw.print((fullversion) ? "f3.runtime.version" : "f3.version");
        pw.println("\"));");
        pw.println("java.lang.System.out.flush();");
        return sw.toString();
    }

    /*
     * Create and compile an F3 file, to get the version string.
     * Note: we could use the ScriptEngine, however we would like to make
     * sure the launchers (f3 and f3c) works!.
     */
    static String getVersionPropFromF3(boolean isFullVersion) throws IOException {
        String filename = "Version";
        FileWriter fw = new FileWriter(new File(workingDir, filename + ".f3"));
        PrintWriter pw = new PrintWriter(fw);
        try {
            pw.println(emitVersionF3(isFullVersion));
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                fw.close();
            }
        }
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(f3cExe.toString());
        cmdsList.add(filename + ".f3");
        doExec(cmdsList);
        cmdsList.clear();
        cmdsList.add(f3Exe.toString());
        cmdsList.add(filename);
        List<String> output = doExec(cmdsList);
        return output.get(0);
    }

    /*
     * emit our F3 code to print out the arguments, if it is
     * javaw launcher then we write to a file.
     */
    private static String emitArgsTestF3(File outFile) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("public function run(args: String[]) : Void {");
        if (outFile != null) {
            pw.println("var ps = new java.io.PrintStream(\"" +
                    outFile.getName() + "\");");
            pw.println("java.lang.System.setOut(ps);");
        }
        pw.println("for (i in args) {");
        pw.println("F3.println(i);");
        pw.println("}");
        if (outFile != null) {
            pw.println("ps.close();");
        }
        pw.println("}");
        return sw.toString();
    }

    /*
     * emit our Java code to print out the arguments, if it is
     * javaw launcher we write to a file.
     */
    private static String emitArgsTestJava(String classname, File outFile) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("public class " + classname + " {");
        pw.println("public static void main(String[] args) throws Exception {");
        if (outFile != null) {
            pw.println("java.io.PrintStream ps = new java.io.PrintStream(\"" +
                    outFile.getName() + "\");");
            pw.println("System.setOut(ps);");
        }
        pw.println("for (String x : args) {");
        pw.println("System.out.println(x);");
        pw.println("}");
        if (outFile != null) {
            pw.println("ps.close();");
        }
        pw.println("}");
        pw.println("}");
        return sw.toString();
    }

    static String emitPropsTestJava(String classname, File outFile) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (outFile != null) {
            pw.println("var ps = new java.io.PrintStream(\"" +
                    outFile.getName() + "\");");
            pw.println("java.lang.System.setOut(ps);");
        }
        pw.println("F3.println(\"SYSTEM-PROPERTIES-START\");");
        pw.println("F3.println(java.lang.System.getProperties());");
        pw.println("F3.println(\"SYSTEM-PROPERTIES-END\");");
        if (outFile != null) {
            pw.println("ps.close();");
        }
        return sw.toString();       
    }

    /* NOTE:
     * usage args = "-cp" | "-classpath", "jar-file", "main-class", "app-args....."
     * OR
     * usage args = "-jar", "jar-file", "app-args....."
     */
    private static List<String> getArgumentsFromLauncher(List<String> cmdsList,
            boolean useF3, boolean usef3w) throws IOException {
        File testOutput = null ;
        if (usef3w) {
            testOutput = new File(workingDir, "output.log");
            if (testOutput.exists()) {
                if (!testOutput.delete()) {
                    throw new RuntimeException("Could not delete file " +
                            testOutput.getAbsolutePath());
                }
            }
        }
        if (useF3) {
            createF3Jar(new File(workingDir, cmdsList.get(1)), testOutput);
        } else {
            createJavaJar(new File(workingDir, cmdsList.get(1)), testOutput);
        }

        ArrayList<String> execList = new ArrayList<String>();
        execList.add(0, (usef3w)
                ? f3wExe.toString()
                : f3Exe.toString()
                );
    

        execList.addAll(cmdsList);
        return doExec(execList, testOutput, false);
    }

    private static List<String> getArgumentsFromF3(List<String> cmdsList)
            throws IOException {
        return getArgumentsFromLauncher(cmdsList, true, false);
    }

    private static List<String> getArgumentsFromJava(List<String> cmdsList)
            throws IOException {
        return getArgumentsFromLauncher(cmdsList, false, false);
    }

    private static List<String> getArgumentsFromJavawF3(List<String> cmdsList)
            throws IOException {
        return getArgumentsFromLauncher(cmdsList, true, true);
    }

    private static List<String> getArgumentsFromJavawJava(List<String> cmdsList)
            throws IOException {
        return getArgumentsFromLauncher(cmdsList, false, true);
    }

    private static void createF3Jar(File jarFilename, File testOutput) throws IOException {
        createJar(true, jarFilename, testOutput);
    }
    
    private static void createJavaJar(File jarFilename, File testOutput) throws IOException {
        createJar(false, jarFilename, testOutput);
    }

    static void createF3Jar(File jarFilename, String testSrc) throws IOException {
        String filename = null;
        String jarfilename = jarFilename.getName();
        if (!jarfilename.endsWith(".jar")) {
            throw new RuntimeException("jarFilename: does not end with .jar");
        } else {
            filename = jarfilename.substring(0, jarfilename.indexOf(".jar"));
        }

        // delete any stray files lying around
        deleteAllFiles();

        PrintStream ps = new PrintStream(new FileOutputStream(
                new File(workingDir, filename + ".f3")));
        ps.println(testSrc);
        ps.close();
        ArrayList<String> cmdsList = new ArrayList<String>();
        cmdsList.add(f3cExe.toString());
        cmdsList.add(filename + ".f3");
        List<String> f3cList = doExec(cmdsList);
        if (f3cList == null) {
            throw new RuntimeException("F3 compilation failed " + filename + ".java");
        }
        String jarArgs[] = {
            (debug) ? "cvfe" : "cfe",
            jarFilename.getAbsolutePath(),
            filename,
            "-C",
            workingDir.getAbsolutePath(),
            "."
        };

        sun.tools.jar.Main jarTool =
                new sun.tools.jar.Main(System.out, System.err, "JarCreator");
        if (!jarTool.run(jarArgs)) {
            throw new RuntimeException("jar creation failed " + jarFilename);
        }
        // delete left over class files
        deleteClassFiles();
    }
    
    private static void createJar(boolean isF3, File jarFilename, File testOutput) throws IOException {
        String filename = null;
        String jarfilename = jarFilename.getName();
        if (!jarfilename.endsWith(".jar")) {
            throw new RuntimeException("jarFilename: does not end with .jar");
        } else {
            filename = jarfilename.substring(0, jarfilename.indexOf(".jar"));
        }
        
        // delete any stray files lying around
        deleteAllFiles();

        if (isF3) {
            PrintStream ps = new PrintStream(new FileOutputStream(
                    new File(workingDir, filename + ".f3")));
            ps.println(emitArgsTestF3(testOutput));
            ps.close();
            ArrayList<String> cmdsList = new ArrayList<String>();
            cmdsList.add(f3cExe.toString());
            cmdsList.add(filename + ".f3");
            List<String> f3cList = doExec(cmdsList);
            if (f3cList == null) {
                throw new RuntimeException("F3 compilation failed " + filename + ".java");
            }
        } else {
            File javaFile = new File(workingDir, filename + ".java");
            PrintStream ps = new PrintStream(new FileOutputStream(javaFile));
            ps.println(emitArgsTestJava(filename, testOutput));
            ps.close();

            String compileArgs[] = {
                "-d",
                workingDir.getAbsolutePath(),
                javaFile.getAbsolutePath()
            };
            if (javaCompiler.run(null, null, null, compileArgs) != 0) {
                throw new RuntimeException("Java compilation failed " + filename + ".java");
            }
        }

        String jarArgs[] = {
            (debug) ? "cvfe" : "cfe",
            jarFilename.getAbsolutePath(),
            filename,
            "-C",
            workingDir.getAbsolutePath(),
            "."
        };
        
        sun.tools.jar.Main jarTool =
                new sun.tools.jar.Main(System.out, System.err, "JarCreator");
        if (!jarTool.run(jarArgs)) {
            throw new RuntimeException("jar creation failed " + jarFilename);
        }
        // delete left over class files
        deleteClassFiles();
    }

    /*
     * creates a file in the test working directory
     */
    static void createFile(String outfileName, String contents) throws IOException {
         PrintStream ps = new PrintStream(new FileOutputStream(
                    new File(workingDir, outfileName)));
            ps.print(contents);
            ps.close();
    }
}
