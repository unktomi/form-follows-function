/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.tools.api;

import org.f3.api.F3cTask;
import org.f3.api.tree.SourcePositions;
import org.f3.api.tree.UnitTree;
import org.f3.tools.antlr.v4Lexer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Token;

/**
 * JUnit test suite for the issue VSGC-1117.
 *
 * @author David Strupl
 */
public class VSGC1117Test extends TestSuite {
    private static final String testSrc = System.getProperty("test.src.dir", "test/src");
    private static final String testClasses = System.getProperty("build.test.classes.dir");
    private static File masterFile = new File(testSrc + "/org/f3/tools/api", "AllTrees.f3");

    /**
     * This method is called reflectively by the JUnit test runner.
     */
    public static Test suite() throws Exception {
        return new VSGC1117Test();
    }

    public VSGC1117Test() {
        try {
            addSingleTokenDeletions();
            addSingleCharDeletions();
            addEndOfLineDeletions();
            addSingleCharInsertion();
        } catch (IOException ex) {
            Logger.getLogger(VSGC1117Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addEndOfLineDeletions() throws IOException {
        int start = 0;
        for (int end : tokenStartPositions()) {
            if (start < end) {
                String master = readMasterFile();
                int eol = findEOL(master, start);
                if (eol > start) {
                    String s = master.substring(0, start) + master.substring(eol);
                    String deleted = master.substring(start, eol);
                    addTest(new AnalyzeTest("EndOfLineDeletion " + start + ":" + eol, s, deleted));
                }
            }
            start = end;
        }
    }

    private void addSingleCharInsertion() throws IOException {
        int start = 0;
        for (int end : tokenStartPositions()) {
            if (start < end) {
                String master = readMasterFile();
                String s = master.substring(0, start) + "X" + master.substring(start);
                String inserted = s.substring(start, start + 10);
                addTest(new AnalyzeTest("SingleCharInsertion " + start, s, inserted));
            }
            start = end;
        }
    }

    private int findEOL(String s, int start) {
        Pattern p = Pattern.compile("\n");
        Matcher m = p.matcher(s);
        m.find(start);
        return m.start();
    }

    private void addSingleCharDeletions() throws IOException {
        int start = 0;
        for (int end : tokenStartPositions()) {
            if (start < end) {
                String master = readMasterFile();
                String s = master.substring(0, start) + master.substring(start+1);
                String deleted = master.substring(start, start+1);
                addTest(new AnalyzeTest("SingleCharDeletion " + start, s, deleted));
            }
            start = end;
        }
    }

    private void addSingleTokenDeletions() throws IOException {
        int start = 0;
        for (int end : tokenStartPositions()) {
            if (start < end) {
                String master = readMasterFile();
                String s = master.substring(0, start) + master.substring(end);
                String deleted = master.substring(start, end);
                addTest(new AnalyzeTest("SingleTokenDeletion " + start + ":" + end, s, deleted));
            }
            start = end;
        }
    }

    private List<Integer> tokenStartPositions() throws IOException {
        List<Integer> res = new ArrayList<Integer>();
        ANTLRReaderStream input = new ANTLRInputStream(new FileInputStream(masterFile));
        v4Lexer lexer = new v4Lexer(input);
        Token t = lexer.nextToken();
        while (t.getType() != Token.EOF) {
            if (t.getChannel() != Token.HIDDEN_CHANNEL) {
                res.add(lexer.getCharIndex());
            }
            t = lexer.nextToken();
        }
        return res;
    }

    private String readMasterFile() throws IOException {
        FileInputStream fis = new FileInputStream(masterFile);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb =
            fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());
        Charset cs = Charset.forName("8859_1");
        CharsetDecoder cd = cs.newDecoder();
        CharBuffer cb = cd.decode(bb);
        fc.close();
        return cb.toString();
    }

    public static class AnalyzeTest extends TestCase {
        private static final String FILE_TO_COMPILE = "tmp-to-compile.f3";
        private String script;
        private String reportErrorString;
        private static File file = new File(testSrc + "/org/f3/tools/api", FILE_TO_COMPILE);
        private File tempFile;
        private Exception savedException;

        public AnalyzeTest(String name, String script, String reportErrorString) {
            super(name);
            this.script = script;
            this.reportErrorString = reportErrorString;
        }

        @Override
        protected void setUp() throws Exception {
            // make sure there are no leftovers
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(script);
            osw.close();
            tempFile = File.createTempFile("VSGC1117TestFailure-", ".f3");
            copy(file, tempFile);
System.out.println("Setup for file : " + tempFile.getAbsolutePath());

        }

        @Override
        protected void tearDown() throws Exception {
            if (file.exists()) {
                file.delete();
            }
            if ((tempFile != null) && (tempFile.exists()) &&
                    savedException != null) {
                // ok, there was an exception, let's log it
                FileOutputStream fos = new FileOutputStream(tempFile, true);
                PrintStream ps = new PrintStream(fos);
                ps.print("\n/*");
                savedException.printStackTrace(ps);
                ps.print("\n*/");
                ps.close();
            }
        }

        @Override
        protected void runTest() throws Exception {
            System.out.println("Running " + reportErrorString);
            savedException = null;
            F3cTool instance = new F3cTool();
            MockDiagnosticListener<? super FileObject> dl = new MockDiagnosticListener<FileObject>();
            StandardJavaFileManager fm = instance.getStandardFileManager(dl, null, null);
            Iterable<? extends JavaFileObject> files = fm.getJavaFileObjects(file);
            F3cTask task = instance.getTask(null, fm, dl, null, files);
            assertNotNull("no task returned", task);

            Iterable<? extends UnitTree> result1 = task.parse();
            assertTrue("no compilation units returned", result1.iterator().hasNext());
            Iterable<? extends UnitTree> result2 = null;
            try {
                result2 = task.analyze();
            } catch (Exception x) {
                savedException = x;
                throw x;
            }
            assertTrue("no compilation units returned", result2.iterator().hasNext());
            UnitTree t = result2.iterator().next();
            SourcePositions sourcePositions = F3cTrees.instance(task).getSourcePositions();
            int start = (int) sourcePositions.getStartPosition(t, t);
            int end = (int) sourcePositions.getEndPosition(t, t);
            assertTrue(getName() + " : " + reportErrorString, start != -1 && end != -1 && end != 0);
            if ((tempFile != null) && (tempFile.exists())) {
                // if the test was successfull we can delete the temp copy
                tempFile.delete();
            }
        }

        static class MockDiagnosticListener<T> implements DiagnosticListener<T> {
            public void report(Diagnostic<? extends T> d) {
                diagCodes.add(d.getCode());
            }

            public List<String> diagCodes = new ArrayList<String>();
            public int errors() {
                return diagCodes.size();
            }
        }
    }

    private static void copy(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            long size = in.size();
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
            out.write(buf);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
