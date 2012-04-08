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

package org.f3.tools.script;

import org.f3.tools.api.F3cTool;
import org.f3.tools.api.F3cTaskImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.*;
import org.f3.tools.code.*;
import org.f3.tools.comp.*;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.util.Name;

/**
 * Simple interface to the F3 compiler using JSR 199 Compiler API.
 * This is "cumulative": Script-level declarations make by one script are
 * visible to future scripts, so this represents a "compiler context".
 * Based on https://scripting.dev.java.net's JavaCompiler by A. Sundararajan.
 */
public class F3ScriptCompiler {
    public F3cTool tool;
    private ClassLoader parentClassLoader;
    // a map in which the key is package name and the value is list of
    // classes in that package.
    public Map<String, List<String>> packageMap;
    
    Scope namedImportScope;

    Map<String,MemoryFileManager.ClassOutputBuffer> clbuffers =
            new HashMap<String,MemoryFileManager.ClassOutputBuffer>();
    MemoryFileManager manager;

    Name.Table names;
    F3Defs defs;
    F3Types types;
    F3Symtab syms;
    F3ClassReader reader;
    Name pseudoSourceFile;
    Name pseudoFile;
    Name pseudoDir;
    Name pseudoProfile;

    public F3ScriptCompiler(ClassLoader parent) {
	parentClassLoader = parent;
        tool = F3cTool.create();
        packageMap = new HashMap<String, List<String>>();
        try {
            // fill package-class-list map from parent class loader
            fillPackageMap(parentClassLoader, packageMap);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        StandardJavaFileManager stdManager = tool.getStandardFileManager(null, null, null);
	manager = new MemoryFileManager(stdManager,
                 parentClassLoader, packageMap, clbuffers);
    }

    void initCompilerContext (Context context, F3cTaskImpl task) {
        if (names ==  null) {
            // This is the first time initCompilerContext is called.
            names = Name.Table.instance(context);
            org.f3.tools.code.F3Symtab.preRegister(context);
            org.f3.tools.code.F3Types.preRegister(context);
            org.f3.tools.comp.F3ClassReader.preRegister(context);
            pseudoFile = names.fromString("__FILE__");
            pseudoSourceFile = names.fromString("__SOURCE_FILE__");
            pseudoDir = names.fromString("__DIR__");
            pseudoProfile = names.fromString("__PROFILE__");
            defs = F3Defs.instance(context);
            syms = (F3Symtab) F3Symtab.instance(context);
            types = F3Types.instance(context);
        }
        else {
            // Re-use names etc from previous calls to initCompilerContext.
            context.put(Name.Table.namesKey, names);
            context.put(F3Defs.f3DefsKey, defs);
            F3Symtab.preRegister(context, syms);
            F3Types.preRegister(context, types);
        }
        reader = F3ClassReader.instance(context);
        F3ScriptClassBuilder classBuilder = F3ScriptClassBuilder.instance(context);
        classBuilder.scriptingMode = true;
        task.compilerMain.registerServices(context, new String[] {});
        if (namedImportScope == null) {
            namedImportScope = new Scope.ImportScope(syms.unnamedPackage);
            F3MemberEnter.importPredefs(syms, namedImportScope);
        }
    }

    /**
     * compile given String source and return bytecodes as a Map.
     *
     * @param fileName source fileName to be used for error messages etc.
     * @param source Java source as String
     * @param err error writer where diagnostic messages are written
     * @param sourcePath location of additional .java source files
     * @param classPath location of additional .class files
     * @param diagnostics error and warning collector
     * @param printDiagnostics true if diagnostics should be displayed
     */
    public F3CompiledScript compile(String fileName, String source,
                    Writer err, String sourcePath, String classPath,
                    DiagnosticListener<JavaFileObject> diagnostics) {
        Context context = new Context();
        context.put(DiagnosticListener.class, diagnostics);
        context.put(JavaFileManager.class, manager);

        // prepare the compilation unit
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
        compUnits.add(manager.makeStringSource(fileName, source));

        // javac options
        List<String> options = new ArrayList<String>();
        options.add("-Xlint:all-unchecked");
        options.add("-g");
        options.add("-deprecation");
        if (sourcePath != null) {
            options.add("-sourcepath");
            options.add(sourcePath);
        }
        if (classPath != null) {
            options.add("-classpath");
            options.add(classPath);
        }
        options.add("-target");
        options.add("1.5");
        
        options.add("-XDdumpf3=" + System.getProperty("java.io.tmpdir"));
        
        // create a compilation task
        F3cTaskImpl task = tool.getTask(context, err, manager, null, options, compUnits);
        initCompilerContext(context, task);
        
        task.setPreserveSymbols(namedImportScope, null, true);

        if (! task.call())
            return null;

        F3Enter env = F3Enter.instance(context);
        Scope scriptScope = env.scriptScopes.first();

        for (Scope.Entry e = scriptScope.elems; e != null; e = e.sibling) {
            if ((e.sym.flags() & Flags.SYNTHETIC) != 0)
                continue;
            Name name = e.sym.name;
            if (name == pseudoSourceFile || name == pseudoFile || name == pseudoDir || name == pseudoProfile)
                continue;
            Symbol old = namedImportScope.lookup(name).sym;
            if (old != null)
                namedImportScope.remove(old);
            e.sym.flags_field |= Flags.PUBLIC;
            namedImportScope.enter(e.sym, scriptScope);
        }
        env.scriptScopes.clear(); // ???
        F3CompiledScript result = new F3CompiledScript();
        result.compiler = this;
        result.scriptScope = scriptScope;
        result.clazzName = ((F3ClassSymbol) scriptScope.owner).flatname.toString();
        return result;
    }

    static String readFully(Reader reader) throws java.io.IOException {
        char[] arr = new char[8*1024]; // 8K at a time
        StringBuilder buf = new StringBuilder();
        int numChars;
        while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
                buf.append(arr, 0, numChars);
        }
        return buf.toString();
    }

    /*
     * javac needs list of classes of a given package to resolve imports.
     * Refer to JavacFileManager.list() method. We want to get list of classes
     * for each package that is visible to our parent class loader. We do that by
     * looking for resource files that lists all the .class files for each package.
     * The format of the file is same as the output of "jar tf <jar-file>". Each
     * line lists a package directory or a .class file entry with full package 
     * directory.
     */
    private static final String CLASS_LIST_RESOURCE = "META-INF/CLASS.LIST";
    
    // Look for class list resources from the given class loader and fill
    // in class list for each package seen.
    private static void fillPackageMap(ClassLoader loader, 
            Map<String, List<String>> packageMap) throws IOException {
        Enumeration<URL> urls = loader.getResources(CLASS_LIST_RESOURCE);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            InputStream stream = url.openStream();
            fillPackageMap(stream, packageMap);
        }
    }

    // Given an input stream of a resource that lists .class entries, fill
    // in the package to classes list map.
    private static void fillPackageMap(InputStream stream, 
            Map<String, List<String>> packageMap) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;      
        while ((line = br.readLine()) != null) {
            if (line.endsWith(".class")) {
                int lastSlash = line.lastIndexOf('/');
                String dir = (lastSlash == -1)? "" : line.substring(0, lastSlash);
                String file = (lastSlash == -1)? line : line.substring(lastSlash + 1);

                List<String> classList;
                String pkgName = dir.replace('/', '.');
                if (! packageMap.containsKey(pkgName)) {
                    classList = new ArrayList<String>();
                    packageMap.put(pkgName, classList);
                } else {
                    classList = packageMap.get(pkgName);
                }
                classList.add(file.substring(0, file.indexOf(".class")));
            }
        }
    }

    public Symbol lookup (Name name) {
        Scope.Entry entry = namedImportScope.lookup(name);
        return entry == null ? null : entry.sym;
    }

    public Symbol lookup (String name) {
        return lookup(names.fromString(name));
    }
}
