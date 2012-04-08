/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.tools.api.F3cTool;
import org.f3.api.tree.UnitTree;
import java.io.File;
import java.util.List;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for issue VSGC-143:  assertion error in 
 * @author tball
 */
public class VSGC143Test {
    private static final String testSrc = System.getProperty("test.src.dir", "test/src");

    @Test
    public void testVSGC143() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {            
            /* The javac library uses the context classloader to load the 
             * javac implementation.  In a NetBeans module, it needs to
             * be loaded by the module's classloader to make sure that the
             * version of javac this compiler requires takes precedence
             * over the JDK's version.  
             */
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            File file = new File("test/src/org/f3/tools/api/Hello.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file); 
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            List<? extends UnitTree> treeList = (List)f3Task.parse();
            assertTrue(treeList.size() == 1);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
    
    @Test
    public void parseClassSource() throws Exception {
        F3cTool instance = new F3cTool();
        MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
        StandardJavaFileManager fm = instance.getStandardFileManager(dl, null, null);
        File file = new File(testSrc + "/org/f3/tools/api", "UndeclaredClass.f3");
	Iterable<? extends JavaFileObject> fileList = fm.getJavaFileObjects(file);
        F3cTask task = instance.getTask(null, fm, dl, null, fileList);
        assertNotNull("no task returned", task);
        Iterable<? extends UnitTree> result = task.parse();
        assertEquals("parse error(s)", 0, dl.errors());
        assertTrue("no compilation units returned", result.iterator().hasNext());
    }
}
