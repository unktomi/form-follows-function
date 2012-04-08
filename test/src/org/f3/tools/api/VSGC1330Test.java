/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.api.tree.ClassDeclarationTree;
import org.f3.api.tree.F3TreePathScanner;
import org.f3.api.tree.SequenceIndexedTree;
import org.f3.api.tree.UnitTree;
import org.f3.api.tree.SourcePositions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import static javax.tools.StandardLocation.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Verifies correct start and end position for indexed sequence expression.
 * 
 * @author tball
 */
public class VSGC1330Test {
    @Test
    public void sequenceExpressionPosTest() throws Exception {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {            
            Thread.currentThread().setContextClassLoader(F3cTool.class.getClassLoader());
            F3cTool tool = F3cTool.create();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            
            StandardJavaFileManager fileManager = tool.getStandardFileManager(dl, null, null);
            List<File> dirs = new ArrayList<File>();
            dirs.add(getTmpDir());
            fileManager.setLocation(CLASS_OUTPUT, dirs);
            
            File file = new File("test/src/org/f3/tools/api/Boids.f3");
            Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(file); 
            F3cTask f3Task = tool.getTask(null, fileManager, dl, null, fileObjects);
            Iterable<? extends UnitTree> treeList = f3Task.parse();
            assertTrue("no parse tree(s) returned", treeList.iterator().hasNext());
            
            final F3cTrees trees = F3cTrees.instance(f3Task);
            final SourcePositions sp = trees.getSourcePositions();
            for (final UnitTree unit : treeList) {
                F3TreePathScanner scanner = new F3TreePathScanner<Object,Void>() {
                    @Override
                    public Object visitSequenceIndexed(SequenceIndexedTree node, Void p) {
                        assertEquals(37, sp.getStartPosition(unit, node));
                        assertEquals(45, sp.getEndPosition(unit, node));
                        return super.visitSequenceIndexed(node, p);
                    }
                };
                scanner.scan(unit, null);
            }
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
