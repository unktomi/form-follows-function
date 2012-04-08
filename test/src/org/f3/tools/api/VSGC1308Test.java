package org.f3.tools.api;

import org.f3.api.F3cTask;
import com.sun.tools.mjavac.util.JavacFileManager;
import org.f3.tools.api.F3cTool;

import org.f3.tools.util.F3FileManager;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import javax.tools.StandardLocation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Verify that source files can be read from zip files.
 * 
 * @author Tom Ball
 */
public class VSGC1308Test {
    int nerrors;

    @Test
    public void testZipSourceAccess() throws Exception {
        nerrors = 0;

        F3cTool tool = F3cTool.create();
        JavacFileManager manager = 
                (F3FileManager) tool.getStandardFileManager (null, null, null);
        File zip = new File("test/src/org/f3/tools/api/vsgc1308.zip");
        assertTrue(zip.exists());
        manager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(zip));

        ArrayList<JavaFileObject> sources = new ArrayList<JavaFileObject> ();
        JavaFileObject jfo = manager.getJavaFileForInput(StandardLocation.SOURCE_PATH, "Test", JavaFileObject.Kind.SOURCE);
        sources.add(jfo);

        F3cTask task = tool.getTask (null, null, new DiagnosticListener<JavaFileObject>() {
            public void report (Diagnostic<? extends JavaFileObject> diagnostic) {
                System.out.println ("diagnostic = " + diagnostic);
                nerrors++;
            }
        }, Arrays.asList ("-target", "1.5", "-d", getTmpDir().getPath()
        ), sources);

        int n = task.errorCheck();
        assertEquals(0, nerrors);
        assertEquals(0, n);
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
