/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.SourceFileScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * F3AntTask
 *
 * @author Brian Goetz
 */
public class F3AntTask extends Javac {
    public Path compilerClassPath;
    public String profile;

    private static final String FAIL_MSG
            = "F3 compile failed; see the compiler error output for details.";

    public static final String F3_ENTRY_POINT = "org.f3.tools.Main";

    public F3AntTask() {
        super();
        super.setCompiler(F3CompilerAdapter.class.getName());
        super.setIncludeantruntime(true);
    }

    @Override
    protected void scanDir(File srcDir, File destDir, String[] files) {
        GlobPatternMapper m = new GlobPatternMapper();
        m.setFrom("*.f3");
        m.setTo("*.class");
        SourceFileScanner sfs = new SourceFileScanner(this);
        File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);

        if (newFiles.length > 0) {
            File[] newCompileList
                    = new File[compileList.length + newFiles.length];
            System.arraycopy(compileList, 0, newCompileList, 0, compileList.length);
            System.arraycopy(newFiles, 0, newCompileList, compileList.length, newFiles.length);
            compileList = newCompileList;
        }
    }

    /**
     * Workaround for classloader bug in CompilerAdapterFactory
     */
    @Override
    protected void compile() {
        if (compileList.length > 0) {
            log("Compiling " + compileList.length + " source file"
                    + (compileList.length == 1 ? "" : "s")
                    + (getDestdir() != null ? " to " + getDestdir() : ""));

            if (listFiles) {
                for (int i = 0; i < compileList.length; i++) {
                    String filename = compileList[i].getAbsolutePath();
                    log(filename);
                }
            }

            CompilerAdapter adapter = new F3CompilerAdapter();

            // now we need to populate the compiler adapter
            adapter.setJavac(this);

            // finally, lets execute the compiler!!
            if (!adapter.execute()) {
                if (failOnError) {
                    throw new BuildException(FAIL_MSG, getLocation());
                } else {
                    log(FAIL_MSG, Project.MSG_ERR);
                }
            }
        }
    }

    public void setCompilerClassPath(Path p) {
        compilerClassPath = p;
    }

    public void setCompilerClassPathRef(Reference r) {
        setCompilerClassPath((Path) r.getReferencedObject());
    }
    
    private URL[] pathAsURLs() throws java.net.MalformedURLException {
        Path p = compilerClassPath != null ? compilerClassPath : new Path(getProject());
        java.util.ArrayList<URL> urls = new java.util.ArrayList<URL>();
        for (String s : p.list()) {
            urls.add(new File(s).toURI().toURL());
        }
        return urls.toArray(new URL[0]);
    }

    public void setProfile(String s) {
        profile = s;
    }

    @Override
    public String getCompiler() {
        return "extJavac";
    }

    @Override
    protected void checkParameters() throws BuildException {
        super.checkParameters();
            if (super.getExecutable() == null && compilerClassPath == null) {
                throw new BuildException("f3c: executable or compilerclasspath must be set", getLocation());
            }
    }
    
    public static class F3CompilerAdapter extends DefaultCompilerAdapter {

        public boolean execute() throws BuildException {
            try {
                if (getJavac().isForkedJavac())
                    return forkeExecute();
                else {
                    Commandline cmd = setupModernJavacCommand();
                    URL[] jars = ((F3AntTask) getJavac()).pathAsURLs();
                    URLClassLoader loader = new URLClassLoader(jars) {
                        @Override
                        protected Class loadClass(String n, boolean r) throws ClassNotFoundException {
                            if (n.indexOf("sun.tools") >= 0 || n.startsWith("com.sun.source")) {
                                Class c = findLoadedClass(n);
                                if (c != null) {
                                    getJavac().log("found loaded class: " + n);
                                    return c;
                                }
                                c = findClass(n);
                                if (c == null) {
                                    getJavac().log("didn't find class:  " + n);
                                    return super.loadClass(n, r);
                                }
                                if (r)
                                    resolveClass(c);
                                return c;
                            }
                            return super.loadClass(n, r);
                        }
                    };
                    Class c = Class.forName(F3_ENTRY_POINT, true, loader);
                    Object compiler = c.newInstance();
                    Method compile = c.getMethod("compile", String[].class);
                    Object[] args = cmd.getArguments();
                    int result = (Integer) compile.invoke(compiler, new Object[]{args});
                    return (result == 0);  // zero errors
                }
            } catch (Exception ex) {
                getJavac().log(ex.toString());
                if (ex instanceof ClassNotFoundException ||
                        ex instanceof java.lang.reflect.InvocationTargetException) {
                    throw new BuildException(ex);
                }
                if (ex instanceof BuildException) {
                    throw (BuildException) ex;
                } else {
                    throw new BuildException("Error starting F3 compiler",
                            ex, location);
                }
            }
        }

        public boolean forkeExecute() throws Exception {
            Commandline cmd = new Commandline();
            String executable = getJavac().getExecutable();
            if (executable != null) {
                cmd.setExecutable(executable);
                if (((F3AntTask) getJavac()).compilerClassPath != null) {
                    getJavac().log("Ignoring attribute compilerclasspath, because executable is set.", Project.MSG_WARN);
                }
            } else {
                cmd.setExecutable(JavaEnvUtils.getJdkExecutable("java"));
                if (memoryInitialSize != null) {
                    cmd.createArgument().setValue("-Xms" + memoryInitialSize);
                    memoryInitialSize = null; // don't include it in setupJavacCommandlineSwitches()
                }
                if (memoryMaximumSize != null) {
                    cmd.createArgument().setValue("-Xmx" + memoryMaximumSize);
                    memoryMaximumSize = null; // don't include it in setupJavacCommandlineSwitches()
                }
                String cp = "-Xbootclasspath/p:" +
                        ((F3AntTask) getJavac()).compilerClassPath.toString();
                cmd.createArgument().setValue(cp);
                cmd.createArgument().setValue(F3_ENTRY_POINT);
            }
            String profile = ((F3AntTask) getJavac()).profile;
            if (profile != null) {
                cmd.createArgument().setLine("-profile " + profile);
            }
            setupJavacCommandlineSwitches(cmd, true);
            int firstFileName = cmd.size();
            logAndAddFilesToCompile(cmd);
            return executeExternalCompile(cmd.getCommandline(), firstFileName, true) == 0;
        }
    }
}
