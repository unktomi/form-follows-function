package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.api.tree.UnitTree;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import static javax.tools.StandardLocation.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for issue VSGC-1517: sizeof operator toString() fails
 * @author tball
 */
public class VSGC1517Test {

    @Test
    public void testVSGC1517() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {            
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            List<File> dirs = new ArrayList<File>();
            dirs.add(getTmpDir());
            fileManager.setLocation(CLASS_OUTPUT, dirs);
            
            File file = new File("test/src/org/f3/tools/api/VSGC1517.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file); 
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            List<? extends UnitTree> treeList = (List)f3Task.parse();
            assertTrue("no (or too many) parse tree(s) returned", treeList.size() == 1);

            UnitTree unit = treeList.get(0);
            unit.toString();  // will throw assertion error if reported error exists
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
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
