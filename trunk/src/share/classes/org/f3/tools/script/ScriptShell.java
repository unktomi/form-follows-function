/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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

package org.f3.tools.script;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.tools.*;

/**
 * This is the main class for F3 shell.
 */
public class ScriptShell implements DiagnosticListener<JavaFileObject> {
    F3ScriptContext context;

    /**
     * main entry point to the command line tool
     * @param args command line argument array
     */
    public static void main(String[] args) {
        ScriptShell shell = new ScriptShell(Thread.currentThread().getContextClassLoader());
        shell.initScriptEngine();
        /// parse command line options
        String[] scriptArgs = shell.processOptions(args);
        // process each script command
        for (Command cmd : shell.scripts) {
            cmd.run(scriptArgs);
        }
        shell.close();
    }

    public ScriptShell(ClassLoader parentClassLoader) {
        context = new F3ScriptContext(parentClassLoader);
    }

    // Each -e or -f or interactive mode is represented
    // by an instance of Command.
    private static interface Command {
        public void run(String[] arguments);
    }

    /**
     * Parses and processes command line options.
     * @param args command line argument array
     */
    protected String[] processOptions(String[] args) {
        // current script file encoding selected
        String currentEncoding = null;

        // check for -classpath or -cp first
        checkClassPath(args);
        // have we seen -e or -f ?
        boolean seenScript = false;
        // have we seen -f - already?
        boolean seenStdin = false;
        for (int i=0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-classpath") ||
                    arg.equals("-cp")) {
                // handled already, just continue
                i++;
                continue;
            }

            // collect non-option arguments and pass these as script arguments
            if (!arg.startsWith("-")) {
                int numScriptArgs;
                int startScriptArg;
                if (seenScript) {
                    // if we have seen -e or -f already all non-option arguments
                    // are passed as script arguments
                    numScriptArgs = args.length - i;
                    startScriptArg = i;
                } else {
                    // if we have not seen -e or -f, first non-option argument
                    // is treated as script file name and rest of the non-option
                    // arguments are passed to script as script arguments
                    numScriptArgs = args.length - i - 1;
                    startScriptArg = i + 1;
                    addFileSource(args[i], currentEncoding);
                }
                // collect script arguments and return to main
                String[] result = new String[numScriptArgs];
                System.arraycopy(args, startScriptArg, result, 0, numScriptArgs);
                return result;
            }

            if (arg.startsWith("-D")) {
                String value = arg.substring(2);
                int eq = value.indexOf('=');
                if (eq != -1) {
                    System.setProperty(value.substring(0, eq),
                            value.substring(eq + 1));
                } else {
                    if (!value.equals("")) {
                        System.setProperty(value, "");
                    } else {
                        // do not allow empty property name
                        usage(EXIT_CMD_NO_PROPNAME);
                    }
                }
                continue;
            } else if (arg.equals("-?") || arg.equals("-help")) {
                usage(EXIT_SUCCESS);
            } else if (arg.equals("-e")) {
                seenScript = true;
                if (++i == args.length)
                    usage(EXIT_CMD_NO_SCRIPT);

                addStringSource(args[i]);
                continue;
            } else if (arg.equals("-encoding")) {
                if (++i == args.length)
                    usage(EXIT_CMD_NO_ENCODING);
                currentEncoding = args[i];
                continue;
            } else if (arg.equals("-f")) {
                seenScript = true;
                if (++i == args.length)
                    usage(EXIT_CMD_NO_FILE);
                
                if (args[i].equals("-")) {
                    if (seenStdin) {
                        usage(EXIT_MULTIPLE_STDIN);
                    } else {
                        seenStdin = true;
                    }
                    addInteractiveMode();
                } else {
                    addFileSource(args[i], currentEncoding);
                }
                continue;
            }
            // some unknown option...
            usage(EXIT_UNKNOWN_OPTION);
        }

        if (! seenScript) {
            addInteractiveMode();
        }
        return new String[0];
    }

    /**
     * Adds interactive mode Command
     * @param se ScriptEngine to use in interactive mode.
     */
    protected void addInteractiveMode() {
        scripts.add(new Command() {
            public void run(String[] args) {
                setScriptArguments(args);
                processSource("-", null);
            }
        });
    }

    /**
     * Adds script source file Command
     * @param se ScriptEngine used to evaluate the script file
     * @param fileName script file name
     * @param encoding script file encoding
     */
    protected void addFileSource(final String fileName,
            final String encoding) {
        scripts.add(new Command() {
            public void run(String[] args) {
                setScriptArguments(args);
                processSource(fileName, encoding);
            }
        });
    }

    /**
     * Adds script string source Command
     * @param se ScriptEngine to be used to evaluate the script string
     * @param source Script source string
     */
    private void addStringSource(final String source) {
        scripts.add(new Command() {
            public void run(String[] args) {
                setScriptArguments(args);
                String oldFile = setScriptFilename("<string>");
                try {
                    evaluateString(source);
                } finally {
                    setScriptFilename(oldFile);
                }
            }
        });
    }

    /**
     * Processes a given source file or standard input.
     * @param se ScriptEngine to be used to evaluate
     * @param filename file name, can be null
     * @param encoding script file encoding, can be null
     */
    protected void processSource(String filename,
            String encoding) {
        if (filename.equals("-")) {
            BufferedReader in = getReader();
            boolean hitEOF = false;
            
            while (!hitEOF) {
                ++counter;
                String prompt = getPrompt();
                getError().print(prompt);
                String source = "";
                try {
                    source = in.readLine();
                } catch (IOException ioe) {
                    getError().println(ioe.toString());
                }
                if (source == null) {
                    hitEOF = true;
                    break;
                }
                evaluateString(source, false);
            }
        } else {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(filename);
            } catch (FileNotFoundException fnfe) {
                getError().println(getMessage("file.not.found",
                        new Object[] { filename }));
                        System.exit(EXIT_FILE_NOT_FOUND);
            }
            evaluateStream(fis, filename, encoding);
        }
    }

    public int counter;

    DiagnosticListener<JavaFileObject> diagnosticListener;

    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        System.err.println(formatDiagnostic(diagnostic));
    }

    protected String formatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        return "f3"+counter+":"+diagnostic.getLineNumber()+": "+diagnostic.getMessage(null);
    }

    public void setDiagnosticListener(DiagnosticListener<JavaFileObject> diagnosticListener)  {
        this.diagnosticListener = diagnosticListener;
    }

    protected DiagnosticListener<JavaFileObject> getDiagnosticListener() {
        if (diagnosticListener == null)
            diagnosticListener = this;
        return diagnosticListener;
    }

    protected void reportException (Throwable ex) {
        // FIXME we should filter out StackElementElement entries from
        // this clas, and from the scripting framework, but it's non-trivial,
        // since we also have to deal with getCause().
        ex.printStackTrace();
    }

    protected void reportResult (Object value) {
        if (value != null) {
            String str = value.toString();
            if (str == null)
                str = "null";
            getError().println(str);
        }
    }
    /**
     * Evaluates given script source
     * @param se ScriptEngine to evaluate the string
     * @param script Script source string
     * @param exitOnError whether to exit the process on script error
     */
   private void evaluateString(String script, boolean exitOnError) {
        String sourcePath = null; // FIXME
        String classPath = System.getProperty("java.class.path"); // FIXME
        Writer err = null; // FIXME
        String fileName = "f3" + counter;
        F3CompiledScript compiled = context.compiler.compile(fileName, script,
                err, sourcePath, classPath, getDiagnosticListener());
        if (compiled == null)
            return;
        evaluate(compiled);
    }

    /**
     * Evaluate script string source and exit on script error
     * @param se ScriptEngine to evaluate the string
     * @param script Script source string
     */
    public void evaluateString(String script) {
        evaluateString(script, false);
    }

    /**
     * Evaluates script from given reader
     * @param se ScriptEngine to evaluate the string
     * @param reader Reader from which is script is read
     * @param name file name to report in error.
     */
    public void evaluateReader(Reader reader, String fileName) {
        String sourcePath = null; // FIXME
        String classPath = System.getProperty("java.class.path"); // FIXME
        Writer err = null;
        String script;
        try {
            script = F3ScriptCompiler.readFully(reader);
        }
        catch (java.io.IOException ex) {
            reportException(ex);
            return;
        }
        F3CompiledScript compiled = context.compiler.compile(fileName, script,
                err, sourcePath, classPath, getDiagnosticListener());
        if (compiled == null)
            return;
        evaluate(compiled);
    }

    public Object compileAndRun(String script) throws Throwable {
        String sourcePath = null; // FIXME
        String classPath = System.getProperty("java.class.path"); // FIXME
        Writer err = null; // FIXME
        String fileName = "f3" + counter;
        F3CompiledScript compiled = context.compiler.compile(fileName, script,
                err, sourcePath, classPath, getDiagnosticListener());
        if (compiled == null)
            return null;
	return compiled.eval(context);
    }

    public void evaluate (F3CompiledScript compiled) {
         try {
            reportResult(compiled.eval(context));
        }
        catch (Throwable ex) {
            reportException(ex);
        }
    }

    /**
     * Evaluates given input stream
     * @param is InputStream from which script is read
     * @param name file name to report in error
     */
    protected void evaluateStream(InputStream is, String name,
            String encoding) {
        BufferedReader reader = null;
        if (encoding != null) {
            try {
                reader = new BufferedReader(new InputStreamReader(is,
                        encoding));
            } catch (UnsupportedEncodingException uee) {
                getError().println(getMessage("encoding.unsupported",
                        new Object[] { encoding }));
                        System.exit(EXIT_NO_ENCODING_FOUND);
            }
        } else {
            reader = new BufferedReader(new InputStreamReader(is));
        }
        evaluateReader(reader, name);
    }

    /**
     * Prints usage message and exits
     * @param exitCode process exit code
     */
    private void usage(int exitCode) {
        getError().println(getMessage("main.usage",
                new Object[] { PROGRAM_NAME }));
                System.exit(exitCode);
    }

    /**
     * Gets prompt for interactive mode
     * @return prompt string to use
     */
    private String getPrompt() {
        return "/*f3"+counter+"*/ ";
    }

    /**
     * Get formatted, localized error message
     */
    private static String getMessage(String key, Object[] params) {
        return MessageFormat.format(key, params);
    }

    protected BufferedReader shellReader;
    protected BufferedReader getReader() {
        if (shellReader == null)
            shellReader = new BufferedReader (new InputStreamReader(System.in));
        return shellReader;
    }

    public void close() {
        if (shellReader == null) {
            try {
                shellReader.close();
            } catch (Throwable ex) {
                // do nothing
            }
        }
        shellReader = null;
    }

    // stream to print error messages
    protected PrintStream getError() {
        return System.err;
    }

    // initialize a given script engine // FIXME - unused
    private void initScriptEngine() {
        // load init.f3 file from resource
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream sysIn = cl.getResourceAsStream("org/f3/tools/script/shell/init.f3");
        if (sysIn != null) {
            evaluateStream(sysIn, "<system-init>", null);
        }
    }

    /**
     * Checks for -classpath, -cp in command line args. Creates a ClassLoader
     * and sets it as Thread context loader for current thread.
     *
     * @param args command line argument array
     */
    private void checkClassPath(String[] args) {
        String classPath = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-classpath") ||
                    args[i].equals("-cp")) {
                if (++i == args.length) {
                    // just -classpath or -cp with no value
                    usage(EXIT_CMD_NO_CLASSPATH);
                } else {
                    classPath = args[i];
                }
            }
        }

        if (classPath != null) {
            /* We create a class loader, configure it with specified
             * classpath values and set the same as context loader.
             * Note that ScriptEngineManager uses context loader to
             * load script engines. So, this ensures that user defined
             * script engines will be loaded. For classes referred
             * from scripts, Rhino engine uses thread context loader
             * but this is script engine dependent. We don't have
             * script engine independent solution anyway. Unless we
             * know the class loader used by a specific engine, we
             * can't configure correct loader.
             */
            ClassLoader parent = ScriptShell.class.getClassLoader();
            URL[] urls = pathToURLs(classPath);
            URLClassLoader loader = new URLClassLoader(urls, parent);
            Thread.currentThread().setContextClassLoader(loader);
        }
    }

    /**
     * Utility method for converting a search path string to an array
     * of directory and JAR file URLs.
     *
     * @param path the search path string
     * @return the resulting array of directory and JAR file URLs
     */
    private static URL[] pathToURLs(String path) {
        String[] components = path.split(File.pathSeparator);
        URL[] urls = new URL[components.length];
        int count = 0;
        while(count < components.length) {
            URL url = fileToURL(new File(components[count]));
            if (url != null) {
                urls[count++] = url;
            }
        }
        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }

    /**
     * Returns the directory or JAR file URL corresponding to the specified
     * local file name.
     *
     * @param file the File object
     * @return the resulting directory or JAR file URL, or null if unknown
     */
    private static URL fileToURL(File file) {
        String name;
        try {
            name = file.getCanonicalPath();
        } catch (IOException e) {
            name = file.getAbsolutePath();
        }
        name = name.replace(File.separatorChar, '/');
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        // If the file does not exist, then assume that it's a directory
        if (!file.isFile()) {
            name = name + "/";
        }
        try {
            return new URL("file", "", name);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("file");
        }
    }

    protected void setScriptArguments(String[] args) {
        /* FIXME
        put("arguments", args);
        put(ScriptEngine.ARGV, args);
        */
    }

    protected String setScriptFilename(String name) {
        return null;
        /* FIXME
        String oldName = (String) se.get(ScriptEngine.FILENAME);
        se.put(ScriptEngine.FILENAME, name);
        return oldName;
        */
    }

    // exit codes
    private static final int EXIT_SUCCESS            = 0;
    private static final int EXIT_CMD_NO_CLASSPATH   = 1;
    private static final int EXIT_CMD_NO_FILE        = 2;
    private static final int EXIT_CMD_NO_SCRIPT      = 3;
    private static final int EXIT_CMD_NO_LANG        = 4;
    private static final int EXIT_CMD_NO_ENCODING    = 5;
    private static final int EXIT_CMD_NO_PROPNAME    = 6;
    private static final int EXIT_UNKNOWN_OPTION     = 7;
    private static final int EXIT_ENGINE_NOT_FOUND   = 8;
    private static final int EXIT_NO_ENCODING_FOUND  = 9;
    private static final int EXIT_SCRIPT_ERROR       = 10;
    private static final int EXIT_FILE_NOT_FOUND     = 11;
    private static final int EXIT_MULTIPLE_STDIN     = 12;

    // list of scripts to process
    private List<Command> scripts = new ArrayList<Command>();
    // error messages resource
    private static ResourceBundle msgRes;
    private static String BUNDLE_NAME = "org.f3.tools.script.shell.messages";
    private static String PROGRAM_NAME = "f3runscript";

    static {
        //        msgRes = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    }
}
