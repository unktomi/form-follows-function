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

package org.f3.tools.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import com.sun.tools.mjavac.code.Source;
import com.sun.tools.mjavac.code.Scope;
import com.sun.tools.mjavac.jvm.Target;
import com.sun.tools.mjavac.jvm.ClassReader;
import org.f3.tools.main.F3Option.Option;
import com.sun.tools.mjavac.util.*;
import org.f3.tools.main.RecognizedOptions.OptionHelper;
import org.f3.tools.util.F3FileManager;
import org.f3.tools.util.PlatformPlugin;
import org.f3.tools.util.MsgSym;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticListener;
import org.f3.tools.comp.F3TranslationSupport.NotYetImplementedException;

/** This class provides a commandline interface to the GJC compiler.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class Main {

    static {
        ClassLoader loader = Main.class.getClassLoader();
        if (loader != null) {
            loader.setPackageAssertionStatus("org.f3.tools", true);
            loader.setPackageAssertionStatus("com.sun.tools.mjavac", true);
        }
    }

    /** The name of the compiler, for use in diagnostics.
     */
    String ownName;

    /** The writer to use for diagnostic output.
     */
    PrintWriter out;

    /**
     * If true, any command line arg errors will cause an exception.
     */
    boolean fatalErrors;

    /** Result codes.
     */
    static final int
        EXIT_OK = 0,        // Compilation completed with no errors.
        EXIT_ERROR = 1,     // Completed but reported errors.
        EXIT_CMDERR = 2,    // Bad command-line arguments
        EXIT_SYSERR = 3,    // System error or resource exhaustion.
        EXIT_ABNORMAL = 4;  // Compiler terminated abnormally

    private Option[] recognizedOptions = RecognizedOptions.getJavaCompilerOptions(new OptionHelper() {

        public void setOut(PrintWriter out) {
            Main.this.out = out;
        }

        public void error(String key, Object... args) {
            Main.this.error(key, args);
        }

        public void printVersion() {
            Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_VERSION, ownName,  F3Compiler.version()));
        }

        public void printFullVersion() {
            Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_FULLVERSION, ownName,  F3Compiler.fullVersion()));
        }

        public void printHelp() {
            help();
        }

        public void printXhelp() {
            xhelp();
        }

        public void addFile(File f) {
            if (!filenames.contains(f))
                filenames.append(f);
        }

        public void addClassName(String s) {
            classnames.append(s);
        }

    });

    /**
     * Construct a compiler instance.
     */
    public Main(String name) {
        this(name, new PrintWriter(System.err, true));
    }

    /**
     * Construct a compiler instance.
     */
    public Main(String name, PrintWriter out) {
        this.ownName = name;
        this.out = out;
    }
    /** A table of all options that's passed to the JavaCompiler constructor.  */
    private Options options = null;

    /** The list of source files to process
     */
    public ListBuffer<File> filenames = null; // XXX sb protected

    /** List of class files names passed on the command line
     */
    public ListBuffer<String> classnames = null; // XXX sb protected

    /** Print a string that explains usage.
     */
    void help() {
        Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_MSG_USAGE_HEADER, ownName));
        for (int i=0; i<recognizedOptions.length; i++) {
            recognizedOptions[i].help(out);
        }
        out.println();
    }

    /** Print a string that explains usage for X options.
     */
    void xhelp() {
        for (int i=0; i<recognizedOptions.length; i++) {
            recognizedOptions[i].xhelp(out);
        }
        out.println();
        Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_MSG_USAGE_NONSTANDARD_FOOTER));
    }

    /** Report a usage error.
     */
    void error(String key, Object... args) {
        if (fatalErrors) {
            String msg = getLocalizedString(key, args);
            throw new PropagatedException(new IllegalStateException(msg));
        }
        warning(key, args);
        Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_MSG_USAGE, ownName));
    }

    /** Report a warning.
     */
    void warning(String key, Object... args) {
        Log.printLines(out, ownName + ": "
                       + getLocalizedString(key, args));
    }

    public Option getOption(String flag) {
        for (Option option : recognizedOptions) {
            if (option.matches(flag))
                return option;
        }
        return null;
    }

    public void setOptions(Options options) {
        if (options == null)
            throw new NullPointerException();
        this.options = options;
    }

    public void setFatalErrors(boolean fatalErrors) {
        this.fatalErrors = fatalErrors;
    }

    /** Process command line arguments: store all command line options
     *  in `options' table and return all source filenames.
     *  @param flags    The array of command line arguments.
     */
    public List<File> processArgs(String[] flags) { // XXX sb protected
        int ac = 0;
        while (ac < flags.length) {
            String flag = flags[ac];
            ac++;

            int j;
            // quick hack to speed up file processing:
            // if the option does not begin with '-', there is no need to check
            // most of the compiler options.
            int firstOptionToCheck = flag.charAt(0) == '-' ? 0 : recognizedOptions.length-1;
            for (j=firstOptionToCheck; j<recognizedOptions.length; j++)
                if (recognizedOptions[j].matches(flag)) break;

            if (options.get("mjavac") != null && flag.endsWith(".javadump")) {
                File f = new File(flag);
                if (!f.exists()) {
                    error(MsgSym.MESSAGE_ERR_FILE_NOT_FOUND, f);
                    return null;
                }
                if (!f.isFile()) {
                    error(MsgSym.MESSAGE_ERR_FILE_NOT_FILE, f);
                    return null;
                }
                if (!filenames.contains(f))
                    filenames.append(f);
                continue;
            } else {
                if (j == recognizedOptions.length) {
                    error(MsgSym.MESSAGE_ERR_INVALID_FLAG, flag);
                    return null;
                }
            }

            Option option = recognizedOptions[j];
            if (option.hasArg()) {
                if (ac == flags.length) {
                    error(MsgSym.MESSAGE_ERR_REQ_ARG, flag);
                    return null;
                }
                String operand = flags[ac];
                ac++;
                if (option.process(options, flag, operand))
                    return null;
            } else {
                if (option.process(options, flag))
                    return null;
            }
        }

        if (!checkDirectory("-d"))
            return null;
        if (!checkDirectory("-s"))
            return null;

        String sourceString = options.get("-source");
        Source source = (sourceString != null)
            ? Source.lookup(sourceString)
            : Source.DEFAULT;
        String targetString = options.get("-target");
        Target target = (targetString != null)
            ? Target.lookup(targetString)
            : Target.DEFAULT;
        // We don't check source/target consistency for CLDC, as J2ME
        // profiles are not aligned with J2SE targets; moreover, a
        // single CLDC target may have many profiles.  In addition,
        // this is needed for the continued functioning of the JSR14
        // prototype.
        if (Character.isDigit(target.name.charAt(0))) {
            if (target.compareTo(source.requiredTarget()) < 0) {
                if (targetString != null) {
                    if (sourceString == null) {
                        warning(MsgSym.MESSAGE_WARN_TARGET_DEFAULT_SOURCE_CONFLICT,
                                targetString,
                                source.requiredTarget().name);
                    } else {
                        warning(MsgSym.MESSAGE_WARN_SOURCE_TARGET_CONFLICT,
                                sourceString,
                                source.requiredTarget().name);
                    }
                    return null;
                } else {
                    options.put("-target", source.requiredTarget().name);
                }
            } else {
                if (targetString == null && !source.allowGenerics()) {
                    options.put("-target", Target.JDK1_4.name);
                }
            }
        }
        return filenames.toList();
    }
    // where
        private boolean checkDirectory(String optName) {
            String value = options.get(optName);
            if (value == null)
                return true;
            File file = new File(value);
            if (!file.exists()) {
                error(MsgSym.MESSAGE_ERR_DIR_NOT_FOUND, value);
                return false;
            }
            if (!file.isDirectory()) {
                error(MsgSym.MESSAGE_ERR_FILE_NOT_DIRECTORY, value);
                return false;
            }
            return true;
        }

    /** Programmatic interface for main function.
     * @param args    The command line parameters.
     */
    public int compile(String[] args) {
        Context context = new Context();
        int result = compile(args, context, null, null, false, List.<JavaFileObject>nil());
        if (fileManager instanceof JavacFileManager) {
            // A fresh context was created above, so jfm must be a JavacFileManager
            ((JavacFileManager)fileManager).close();
        }
        return result;
    }

    static final Context.Key<Context> backendContextKey =
         new Context.Key<Context>();

    public void registerServices(Context context, String[] args) {
        Context backEndContext = context.get(backendContextKey);
        if (backEndContext != null)
            return;
        backEndContext = new Context();
        context.put(backendContextKey, context);

        // Tranfer the name table -- must be done before any initialization
        backEndContext.put(Name.Table.namesKey, Name.Table.instance(context));

        // Msgs written to the backendContext will get forwarded to the context
        backEndContext.put(DiagnosticListener.class, new DiagnosticForwarder(context));

        // add -target flag to backEndContext, if specified
        options = Options.instance(backEndContext);

        // default target is Java 5
        options.put("-target", Target.JDK1_5.name);

        try {
            String[] allArgs = CommandLine.parse(args);
            for (int i = 0; i < allArgs.length; i++) {
                String opt = allArgs[i];
                if (opt.equals("-g") || opt.startsWith("-g:"))
                    options.put(opt, opt);
                if (opt.equals("-Xjcov"))
                    options.put(opt, opt);
                if (opt.endsWith("-target") && ++i < allArgs.length)
                    options.put("-target", allArgs[i]);
            }
        } catch (IOException e) {
            // ignore: will be caught and reported on second command line parse.
        }
        options = null;
        filenames = null;
        JavaFileManager currentFileManager = context.get(JavaFileManager.class);
        if (currentFileManager == null)
            F3FileManager.preRegister(backEndContext);
        else
            backEndContext.put(JavaFileManager.class, currentFileManager);

        org.f3.tools.util.F3BackendLog.preRegister(backEndContext, context);
        org.f3.tools.comp.F3Flow.preRegister(backEndContext);
        org.f3.tools.code.F3Lint.preRegister(backEndContext);
        org.f3.tools.code.BlockExprSymtab.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprAttr.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprEnter.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprMemberEnter.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprResolve.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprLower.preRegister(backEndContext);
        org.f3.tools.comp.BlockExprGen.preRegister(backEndContext);

        // Sequencing requires that we get the name table from the fully initialized back-end
        // rather than send the completed one.
        F3JavaCompiler f3JavaCompiler = F3JavaCompiler.instance(backEndContext);

        context.put(F3JavaCompiler.f3JavaCompilerKey, f3JavaCompiler);

        // Tranfer the options -- must be done before any initialization
        context.put(Options.optionsKey, (Options)null);  // remove any old value
        context.put(Options.optionsKey, backEndContext.get(Options.optionsKey));

        ClassReader jreader = ClassReader.instance(backEndContext);
        org.f3.tools.comp.F3ClassReader.preRegister(context, jreader);

        if (currentFileManager == null)
            F3FileManager.preRegister(context); // can't create it until Log has been set up
    }

    /** Load a plug-in corresponding to platform option. If platform option had
     *  not been defined, the method returns immediately.
     * @param context The compiler context.
     * @param options The compiler options.
     */
    private void loadPlatformPlugin(Context context, Options options)
    {
        String platform = options.get("-platform");
        if (platform == null)
            return;

        // collect names of jar files located in the compiler lib directory
        String path = this.getClass().getCanonicalName();
        path = path.substring(path.lastIndexOf('.') + 1);
        path = this.getClass().getResource(path + ".class").toString();
        path = path.substring(0, path.lastIndexOf(".jar!"));
        path = path.substring("jar:file:".length(), path.lastIndexOf("/"));
        File   dir  = new File(path);
        File[] jars = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        // search for platform plugins in jar files collected above
        PlatformPlugin plugin = null;
        URL urls[] = new URL[1];
        for (File jar : jars) {
            try {
                plugin  = null;
                urls[0] = jar.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(urls);
                InputStream    stream = loader.getResourceAsStream(
                    "META-INF/services/" + PlatformPlugin.SERVICE);
                if (stream == null)
                    continue;  // there is no platform plugin in this jar
                // read service provider class name
                Reader reader = new InputStreamReader(stream);
                String pname  = Main.readServiceProvider(reader);
                try {
                    reader.close();
                } catch (IOException ioe) {
                }
                if (pname == null) {
                    Log.instance(context).warning(
                        MsgSym.MESSAGE_PLUGIN_CANNOT_LOAD_PLUGIN, urls[0].getPath());
                    continue;
                }
                // load and instantiate plug-in class
                Class pclass = loader.loadClass(pname);
                plugin = (PlatformPlugin)pclass.newInstance();
                if (!plugin.isSupported(platform))
                    continue;  // this plugin does not support required platform
                try {
                    // attempt to load plug-in's messages
                    Class mclass = loader.loadClass(PlatformPlugin.MESSAGE);
                    ResourceBundle msgs = (ResourceBundle)mclass.newInstance();
                    Messages.instance(context).add(msgs);
                } catch (java.lang.ClassNotFoundException cnfe) {
                } catch (java.lang.InstantiationException ie) {
                }
                plugin.initialize(options, Log.instance(context));

                context.put(PlatformPlugin.pluginKey, plugin);
                break;  // the plugin had been loaded; no need to continue

            } catch (java.net.MalformedURLException murle) {
                // cannot resolve URL: ignore this jar
            } catch (java.lang.ClassNotFoundException cnfe) {
                // cannot load service provider
                Log.instance(context).warning(
                    MsgSym.MESSAGE_PLUGIN_CANNOT_LOAD_PLUGIN, urls[0].getPath());
            } catch (java.lang.InstantiationException ie) {
                // cannot create an instance of plugin
                Log.instance(context).warning(
                    MsgSym.MESSAGE_PLUGIN_CANNOT_LOAD_PLUGIN, urls[0].getPath());
            } catch (java.lang.IllegalAccessException iae) {
                // cannot create an instance of plugin
                Log.instance(context).warning(
                    MsgSym.MESSAGE_PLUGIN_CANNOT_LOAD_PLUGIN, urls[0].getPath());
            }
        }
        // handle no plugin found
        if (plugin == null) {
            Log.instance(context).error(
                MsgSym.MESSAGE_PLUGIN_CANNOT_FIND_PLUGIN, platform);
        }
    }

    /** Reads the first class name as defined by Jar &quot;Service provider&quot;
     *  specification.
     * @param reader The reader of service provider configuration file.
     * @return Plugin&apos;s class name on successful read, null otherwise.
     */
    private static String readServiceProvider(Reader reader)
    {
        StringBuffer name = new StringBuffer(128);
        int st = 0;
        try {
            int ch;
            while ((ch = reader.read()) >= 0) {
                switch (st) {
                case 0:  // skip white spaces before class name
                    switch (ch) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        break;

                    case '#':
                        st = 1;  // skip comment before the class name
                        break;

                    default:
                        name.append((char)ch);
                        st = 2;  // accumulate characters of the class name
                        break;
                    }
                    break;

                case 1:  // skip comment before the class name
                    switch (ch) {
                    case '\r':
                    case '\n':
                        st = 0;  // skip white spaces before class name
                        break;

                    default:
                        break;
                    }
                    break;

                case 2:  // accumulate characters of the class name
                    switch (ch) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                    case '#':
                        return name.toString();

                    default:
                        name.append((char)ch);
                        break;
                    }
                    break;

                default:
                    return null;
                }
            }
        } catch (IOException ioe) {
            return null;
        }
        return (st == 2)? name.toString(): null;
    }

    /** Programmatic interface for main function.
     * @param args    The command line parameters.
     */
    public int compile(String[] args,
                       Context context,
                       Scope namedImportScope, Scope starImportScope,
                       boolean preserveSymbols,
                       List<JavaFileObject> fileObjects)
    {
        try {
            String envArgs = System.getenv("F3C_OPTIONS");
            if (envArgs != null && !envArgs.equals("")) {
                String[] moreArgs = envArgs.split(" ");
                String[] modifiedArgs = new String[args.length + moreArgs.length];
                System.arraycopy(args, 0, modifiedArgs, 0, args.length);
                System.arraycopy(moreArgs, 0, modifiedArgs, args.length, moreArgs.length);
                args = modifiedArgs;
            }
        } catch (Exception ignored) {
        }

        registerServices(context, args);
        if (options == null)
            options = Options.instance(context); // creates a new one

        filenames = new ListBuffer<File>();
        classnames = new ListBuffer<String>();
        F3Compiler comp = null;
        F3JavaCompiler mjcomp = null;

        /*
         * TODO: Logic below about what is an acceptable command line
         * should be updated to take annotation processing semantics
         * into account.
         */
        try {
            if (args.length == 0 && fileObjects.isEmpty()) {
                help();
                return EXIT_CMDERR;
            }

            List<File> fnames;
            try {
                fnames = processArgs(CommandLine.parse(args));
                if (fnames == null) {
                    // null signals an error in options, abort
                    return EXIT_CMDERR;
                } else if (fnames.isEmpty() && fileObjects.isEmpty() && classnames.isEmpty()) {
                    // it is allowed to compile nothing if just asking for help or version info
                    if (options.get("-help") != null
                        || options.get("-X") != null
                        || options.get("-version") != null
                        || options.get("-fullversion") != null)
                        return EXIT_OK;
                    error(MsgSym.MESSAGE_ERR_NO_SOURCE_FILES);
                    return EXIT_CMDERR;
                }
            } catch (java.io.FileNotFoundException e) {
                Log.printLines(out, ownName + ": " +
                               getLocalizedString(MsgSym.MESSAGE_ERR_FILE_NOT_FOUND,
                                                  e.getMessage()));
                return EXIT_SYSERR;
            }

            boolean forceStdOut = options.get("stdout") != null;
            if (forceStdOut) {
                out.flush();
                out = new PrintWriter(System.out, true);
            }

            context.put(Log.outKey, out);

            fileManager = context.get(JavaFileManager.class);

            comp = F3Compiler.instance(context);
            if (comp == null) return EXIT_SYSERR;

            loadPlatformPlugin(context, options);

            if (!fnames.isEmpty()) {
                // add filenames to fileObjects
                comp = F3Compiler.instance(context);
                List<JavaFileObject> otherFiles = List.nil();
                JavacFileManager dfm = (JavacFileManager)fileManager;
                for (JavaFileObject fo : dfm.getJavaFileObjectsFromFiles(fnames))
                    otherFiles = otherFiles.prepend(fo);
                for (JavaFileObject fo : otherFiles)
                    fileObjects = fileObjects.prepend(fo);
            }

            boolean useMJavac = options.get("mjavac") != null;
            if (useMJavac) {
                mjcomp = F3JavaCompiler.instance(context);
                mjcomp.compile(fileObjects);

                if (mjcomp.errorCount() != 0 ||
                    options.get("-Werror") != null && mjcomp.warningCount() != 0)
                    return EXIT_ERROR;
            } else {
                comp.compile(fileObjects, classnames.toList(),
                         namedImportScope, starImportScope, preserveSymbols);

                if (comp.errorCount() != 0 ||
                    options.get("-Werror") != null && comp.warningCount() != 0)
                    return EXIT_ERROR;
            }
        } catch (IOException ex) {
            ioMessage(ex);
            return EXIT_SYSERR;
        } catch (OutOfMemoryError ex) {
            resourceMessage(ex);
            return EXIT_SYSERR;
        } catch (StackOverflowError ex) {
            resourceMessage(ex);
            return EXIT_SYSERR;
        } catch (FatalError ex) {
            feMessage(ex);
            return EXIT_SYSERR;
        } catch (NotYetImplementedException ex) {
            Log.printLines(out, "ABORT: " + ex.getMessage());
            return EXIT_ABNORMAL;
        } catch (ClientCodeException ex) {
            // as specified by javax.tools.JavaCompiler#getTask
            // and javax.tools.JavaCompiler.CompilationTask#call
            throw new RuntimeException(ex.getCause());
        } catch (PropagatedException ex) {
            throw ex.getCause();
        } catch (Throwable ex) {
            // Nasty.  If we've already reported an error, compensate
            // for buggy compiler error recovery by swallowing thrown
            // exceptions.
            if (comp == null || comp.errorCount() == 0 ||
                options == null || options.get("dev") != null)
                bugMessage(ex);
            return EXIT_ABNORMAL;
        } finally {
            if (comp != null) comp.close();
            if (mjcomp != null) mjcomp.close();
            filenames = null;
            options = null;
        }
        return EXIT_OK;
    }

    /** Print a message reporting an internal exception.
     */
    void bugMessage(Throwable ex) {
        Log.printLines(out, getF3LocalizedString(MsgSym.MESSAGE_F3_MSG_BUG,
                                               F3Compiler.fullVersion()));
        ex.printStackTrace(out);
    }

    /** Print a message reporting an fatal error.
     */
    void feMessage(Throwable ex) {
        Log.printLines(out, ex.getMessage());
    }

    /** Print a message reporting an input/output error.
     */
    void ioMessage(Throwable ex) {
        Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_MSG_IO));
        ex.printStackTrace(out);
    }

    /** Print a message reporting an out-of-resources error.
     */
    void resourceMessage(Throwable ex) {
        Log.printLines(out, getLocalizedString(MsgSym.MESSAGE_MSG_RESOURCE));
//      System.out.println("(name buffer len = " + Name.names.length + " " + Name.nc);//DEBUG
        ex.printStackTrace(out);
    }

    private JavaFileManager fileManager;

    /* ************************************************************************
     * Internationalization
     *************************************************************************/

    /** Find a localized string in the resource bundle.
     *  @param key     The key for the localized string.
     */
    public static String getLocalizedString(String key, Object... args) { // FIXME sb private
        try {
            if (messages == null)
                messages = new Messages(javacBundleName);
            return messages.getLocalizedString(MsgSym.MESSAGEPREFIX_JAVAC + key, args);
        }
        catch (MissingResourceException e) {
            throw new Error("Fatal Error: Resource for javac is missing", e);
        }
    }

    /** Find a localized string in the resource bundle.
     *  @param key     The key for the localized string.
     */
    public static String getF3LocalizedString(String key, Object... args) { // FIXME sb private
        try {
            Messages f3messages = new Messages(f3BundleName);
            return f3messages.getLocalizedString(key, args);
        }
        catch (MissingResourceException e) {
            throw new Error("Fatal Error: Resource for javac is missing", e);
        }
    }

    public static void useRawMessages(boolean enable) {
        if (enable) {
            messages = new Messages(javacBundleName) {
                @Override
                public String getLocalizedString(String key, Object... args) {
                    return key;
                }
            };
        } else {
            messages = new Messages(javacBundleName);
        }
    }

    private static final String javacBundleName =
        "com.sun.tools.mjavac.resources.javac";

    private static final String f3BundleName =
        "org.f3.tools.resources.f3compiler";

    private static Messages messages;

    private static class DiagnosticForwarder implements DiagnosticListener {
        Context otherContext;

        public DiagnosticForwarder(Context context) {
            otherContext = context;
        }

        // Log.java writeDiagnostic comes here.   Why doesn't Log.report recurse?
        // otherContext is the f3Context. AND f3Context has NO listener so we
        // don't come back here.
        public void report(Diagnostic diag) {
            Log log = Log.instance(otherContext);
            log.report((JCDiagnostic)diag);
        }
    }
}
