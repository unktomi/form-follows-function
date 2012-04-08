/*
 * Copyright 2001-2005 Sun Microsystems, Inc.  All Rights Reserved.
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
package f3jdi;

import org.f3.jdi.test.F3TestBase;
import org.f3.tools.debug.tty.Debugger;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ksrini
 */
public class JdbBase extends Debugger {

    private final ByteArrayOutputStream baout;
    private final PrintStream  psout;
    private boolean isF3 = true;
    private String mainclass = null;
    static final String F3_MAIN = "org.f3.runtime.Main";

    public JdbBase() {
        super("-classpath " + testWorkDirectory() +
                File.pathSeparator + f3rtJar() +
                " -sourcepath " + testWorkDirectory());
        baout = new ByteArrayOutputStream();
        psout = new PrintStream(baout);
        super.setOutput(psout);
    }

    public static JdbBase getInstance() {
        return new JdbBase();
    }

    @Override
    public void resetOutputs() {
        super.resetOutputs();
        baout.reset();
    }

    public void clearOutput() {
        baout.reset();
    }

    public void printOutput() {
        psout.flush();
        System.out.println(baout.toString());
    }

    public boolean lastContains(String expected) {
        List<String> tmp = getOutputAsList();
        return tmp.get(tmp.size()-1).contains(expected);
    }

    public boolean contains(String expected) {
        List<String> tmp = getOutputAsList();
        for (String x : tmp) {
            if (x.contains(expected)) {
                return true;
            }
        }
        return false;
    }
/**
 * This method checks if the given set of strings appear in the output, in the
 * same given order . If the order is broken then it is deemed not contained. So
 * this method is not similar to Collection.containsAll which doesn't care for order.
 * @param expected
 * @return
 */
    public boolean containsInTandem(String[] expected) {
        if (expected == null || expected.length == 0) {
            return false;
        }
        List<String> tmp = getOutputAsList();
        int i = 0;
        for (String x : tmp) {
            if (i == expected.length)
                break;
            if (x.contains(expected[i])) {
                i++;
                continue;
            }
        }
        if (i == expected.length)
            return true;
        return false;
    }

    public void f3run() {
        f3run((String[])null);
    }

    public void f3run(String... args) {
        StringBuffer sb = new StringBuffer(F3_MAIN);
        sb.append(" " + mainclass);
        if (args != null && args.length > 0) {
            for (String x : args) {
                sb.append(" " + x);
            }
        }
        System.out.println("F3Jdb: " + sb.toString());
        super.run(sb.toString());
    }


    public List<String> getOutputAsList() {
        psout.flush();
        String carray[] = baout.toString().split("\\n");
        List<String> tmp = Arrays.asList(carray);
        return tmp;
    }

    public boolean verifyValue(String var, String expectedValue) {
        clearOutput();
        print(var);
        List<String> olist = getOutputAsList();
        if (olist != null && olist.size() > 0) {
            String str = olist.get(0).trim();
            String expected = var + " = \"" +  expectedValue + '\"';
            return str.equals(expected);
        }
        return false;
    }

    public boolean verifyNumValue(String var, Number expectedValue) {
        clearOutput();
        print(var);
        List<String> olist = getOutputAsList();
        if (olist != null && olist.size() > 0) {
            String str = olist.get(0).trim();
            String expected = var + " = " +  expectedValue;
            return str.equals(expected);
        }
        return false;
    }
    
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
        File[] files = testWorkDirectory().listFiles(filter);
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {}
        }
    }
    static final String SRC_START_TOKEN = "^//.*@BeginTest.*";
    static final String SRC_END_TOKEN   = "^//.*@EndTest.*";

    String removeComments(String in) {
        return in.replaceFirst("^//", "");
    }

    private void writeToFile(File outFile, String filename) {
        String classname = pkgNameToSystem(this.getClass().getName()) + ".java";
        File testSrcDir = new File(System.getProperty("test.src.dir", "."));
        File srcFile = new File(testSrcDir, classname);

        if (!srcFile.exists()) {
            throw new RuntimeException("could not find source file:" + srcFile);
        }
        FileReader fr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintStream pos = null;
        try {
            pos = new PrintStream(new FileOutputStream(outFile));
            fr = new FileReader(srcFile);
            br = new BufferedReader(fr);
            String line = br.readLine();
            boolean start_found = false;
            boolean end_found = false;
            while (line != null) {
                line = line.trim();
                if (!start_found && line.matches(SRC_START_TOKEN) &&
                        line.contains(filename)) {
                    start_found = true;
                    line = br.readLine();
                    continue;
                }
                if (start_found && line.matches(SRC_END_TOKEN)) {
                    end_found = true;
                    start_found = false;
                    return;
                }
                if (start_found && !end_found) {
                    pos.println(removeComments(line));
                }
                line = br.readLine();
            }
            if (!end_found) { // should not happen
                close(pos);
                close(fos);
                outFile.delete();
                throw new RuntimeException("parsing error, " +
                        "missing @EndTest tag in: " + srcFile.getAbsolutePath());
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            close(br);
            close(fr);
            close(pos);
            close(fos);
        }
    }

    private void writeToFile(File outFile, InputStream is) {
        FileOutputStream fos = null;
        PrintStream f3ps = null;
        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ir);
        try {
            fos = new FileOutputStream(outFile);
            f3ps = new PrintStream(fos);
            String codeline = br.readLine();
            while (codeline != null) {
                f3ps.println(codeline);
                codeline = br.readLine();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            close(f3ps);
            close(fos);
            close(ir);
            close(br);
        }
    }

    private File getFileName(String filename) {
        if (filename.endsWith(".f3")) {
            isF3 = true;
            mainclass = filename.substring(0, filename.indexOf(".f3"));
        } else if (filename.endsWith(".java")) {
            isF3 = false;
            mainclass = filename.substring(0, filename.indexOf(".java"));
        } else {
            throw new RuntimeException("file must end with .f3 or .java");
        }
        deleteAllFiles(); // start with a clean slate
        return new File(testWorkDirectory(), filename);
    }

    /**
     *
     * @param filename
     * This method will read the source file for debuggee sources, see example
     * below, where filename is "Foo.f3" in the first example
     * // @BeginTest Foo.f3
     * // your f3 script or java code
     * // more f3 script or java code
     * // @EndTest
     *
     * // @BeginTest Bar.f3
     * // another debuggee code
     * // @EndTest
     *
     * The script or code so specified will be copied over to the working area
     * on the debuggee's classpath and compiled in that location.
     */
    public void compile(String filename) {
        File outFile = getFileName(filename);
        writeToFile(outFile, filename);
        compile0(outFile);
    }

    /**
     * @param filename the file (.f3, .java)  to compile
     * @param is inputstream of the file
     *
     * The script or code so specified will be copied over to the working area
     * on the debuggee's classpath and compiled in that location.
     *
     */
    public void compile(String filename, InputStream is) {
        File outFile = getFileName(filename);
        writeToFile(outFile, is);
        compile0(outFile);
    }

    /**
     *
     * @param srcFile the source to be compiled
     *
     * The script or code so specified int this file will be copied over to the
     *  working area on the debuggee's classpath and compiled in that location.
     */
    public void compile(File srcFile) {
        File outFile = getFileName(srcFile.getName());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(srcFile);
            writeToFile(outFile, fis);
            compile0(outFile);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            close(fis);
        }

    }
    
    private void compile0(File outFile) {
        clearOutput();  
        String[] compileArgs = {
            "-g",
            outFile.getAbsolutePath()
        };
        int retval = (isF3) 
                ? org.f3.tools.Main.compile(compileArgs)
                : com.sun.tools.javac.Main.compile(compileArgs);

        if (retval != 0) {
            throw new RuntimeException("compilation failed");
        }
    }

    String pkgNameToSystem(String in) {
        return in.replace('.', File.separatorChar);
    }

    static File testBuildDirectory() {
        return new File(F3TestBase.testBuildDirectory(), "f3jdi");
    }

    static File testWorkDirectory() {
        File workDir = new File(testBuildDirectory(), "work");
        workDir.mkdirs();
        return workDir;
    }

    static File f3rtJar() {
        String javaClasspaths[] =
                System.getProperty("java.class.path", "").split(File.pathSeparator);
        for (String x : javaClasspaths) {
            if (x.endsWith("f3rt.jar")) {
                return new File(x);
            }
        }
        throw new RuntimeException("Error: no f3rt.jar in the classpath");
    }
}
