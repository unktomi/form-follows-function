package org.f3.tools.api;

import org.f3.api.F3cTask;
import com.sun.tools.mjavac.util.JavacFileManager;
import org.f3.tools.api.F3cTool;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author David Kaspar
 */
public class GenerateTest {

    private static final String SEP = File.pathSeparator;
    private static final String DIR = File.separator;
    private int nerrors;

    @Test
    public void testGeneratePhase() throws Exception {
        String f3Libs = "dist/lib/shared";
        String inputDir = "src/share/classes/f3/lang";
        String outputDir = getTmpDir().getPath();
        nerrors = 0;

        F3cTool tool = F3cTool.create ();
        JavacFileManager manager = tool.getStandardFileManager (null, null, Charset.defaultCharset ());

        ArrayList<JavaFileObject> filesToCompile = new ArrayList<JavaFileObject> ();
        for (String file : new File (inputDir).list ())
            if (file.endsWith (".f3"))
                filesToCompile.add (manager.getFileForInput (inputDir + DIR + file));

        F3cTask task = tool.getTask (null, null, new DiagnosticListener<JavaFileObject>() {
            public void report (Diagnostic<? extends JavaFileObject> diagnostic) {
                System.out.println ("diagnostic = " + diagnostic);
                nerrors++;
            }
        }, Arrays.asList ("-target", "1.5", "-d", outputDir, "-cp",
            f3Libs + DIR + "f3c.jar" + SEP + f3Libs + DIR + "f3rt.jar" + SEP + f3Libs + DIR + "Scenario.jar" + SEP + inputDir
        ), filesToCompile);

        Iterable parseUnits = task.parse();
        assertTrue(parseUnits.iterator().hasNext());
        assertEquals(0, nerrors);
        Iterable analyzeUnits = task.analyze();
        assertTrue(analyzeUnits.iterator().hasNext());
        assertEquals(0, nerrors);
        Iterable generatedFiles = task.generate ();
        assertTrue(generatedFiles.iterator().hasNext());
        assertEquals(0, nerrors);
    }
    
    private static File getTmpDir() {
        try {
            File f = File.createTempFile("dummy", "file");
            f.deleteOnExit();
            File tmpdir = f.getParentFile();
            if (tmpdir != null)
                return tmpdir;
        } catch (IOException ex) {
        }
        File f = new File("test-output");
        f.mkdir();
        return f;
    }

}
