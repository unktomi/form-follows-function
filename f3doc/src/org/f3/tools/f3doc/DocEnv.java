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

package org.f3.tools.f3doc;

import java.util.*;

import com.sun.javadoc.*;

import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.code.Type.MethodType;
import com.sun.tools.mjavac.comp.Check;
import com.sun.tools.mjavac.parser.DocCommentScanner;
import com.sun.tools.mjavac.parser.Token;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.util.Position;
import org.f3.tools.code.FunctionType;
import org.f3.tools.code.F3Types;
import org.f3.tools.comp.F3Attr;
import org.f3.tools.tree.*;
import org.f3.tools.comp.F3ClassReader;


/**
 * Holds the environment for a run of javadoc.
 * Holds only the information needed throughout the
 * run and not the compiler info that could be GC'ed
 * or ported.
 *
 * @since 1.4
 * @author Robert Field
 * @author Neal Gafter (rewrite)
 * @author Scott Seligman (generics)
 */
public class DocEnv {
    protected static final Context.Key<DocEnv> docEnvKey =
        new Context.Key<DocEnv>();

    public static DocEnv instance(Context context) {
        DocEnv instance = context.get(docEnvKey);
        if (instance == null)
            instance = new DocEnv(context);
        return instance;
    }

    private Messager messager;

    DocLocale doclocale;

    /** Predefined symbols known to the compiler. */
    Symtab syms;

    /** Referenced directly in RootDocImpl. */
    F3ClassReader reader;

    /** The compiler's attribution phase (needed to evaluate
     *  constant initializers). */
    F3Attr attr;

    /** Javadoc's own version of the compiler's enter phase. */
    F3docEnter enter;

    /** The name table. */
    Name.Table names;

    /** The encoding name. */
    private String encoding;

    final Symbol externalizableSym;

    /** Access filter (public, protected, ...).  */
    protected ModifierFilter showAccess;

    /** True if we are using a sentence BreakIterator. */
    boolean breakiterator;

    /**
     * True if we do not want to print any notifications at all.
     */
    boolean quiet = false;

    Check chk;
    F3Types types;
    
    /** scanner factory for converting raw doc-comment text */
    com.sun.tools.mjavac.parser.Scanner.Factory scannerFactory;

    /** Allow documenting from class files? */
    boolean docClasses = false;

    /** Does the doclet only expect pre-1.5 doclet API? */
    protected boolean legacyDoclet = true;

    /**
     * Set this to true if you would like to not emit any errors, warnings and
     * notices.
     */
    private boolean silent = false;

    /**
     * Constructor
     *
     * @param context      Context for this javadoc instance.
     */
    protected DocEnv(Context context) {
        context.put(docEnvKey, this);

        messager = Messager.instance0(context);
        syms = Symtab.instance(context);
        reader = F3ClassReader.instance(context);
        enter = F3docEnter.instance0(context);
        attr = F3Attr.instance(context);
        names = Name.Table.instance(context);
        externalizableSym = reader.enterClass(names.fromString("java.io.Externalizable"));
        chk = Check.instance(context);
        types = F3Types.instance(context);
        scannerFactory = DocCommentScanner.Factory.instance(context);

        // Default.  Should normally be reset with setLocale.
        this.doclocale = new DocLocale(this, "", breakiterator);
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * Look up ClassDoc by qualified name.
     */
    public ClassDocImpl lookupClass(String name) {
        ClassSymbol c = getClassSymbol(name);
        if (c != null) {
            return getClassDoc(c);
        } else {
            return null;
        }
    }

    /**
     * Load ClassDoc by qualified name.
     */
    public ClassDocImpl loadClass(String name) {
        try {
            ClassSymbol c = reader.loadClass(names.fromString(name));
            return getClassDoc(c);
        } catch (CompletionFailure ex) {
            chk.completionError(null, ex);
            return null;
        }
    }

    /**
     * Look up PackageDoc by qualified name.
     */
    public PackageDocImpl lookupPackage(String name) {
        //### Jing alleges that class check is needed
        //### to avoid a compiler bug.  Most likely
        //### instead a dummy created for error recovery.
        //### Should investigate this.
        PackageSymbol p = syms.packages.get(names.fromString(name));
        ClassSymbol c = getClassSymbol(name);
        if (p != null && c == null) {
            return getPackageDoc(p);
        } else {
            return null;
        }
    }
        // where
        /** Retrieve class symbol by fully-qualified name.
         */
        ClassSymbol getClassSymbol(String name) {
            // Name may contain nested class qualification.
            // Generate candidate flatnames with successively shorter
            // package qualifiers and longer nested class qualifiers.
            int nameLen = name.length();
            char[] nameChars = name.toCharArray();
            int idx = name.length();
            for (;;) {
                ClassSymbol s = syms.classes.get(names.fromChars(nameChars, 0, nameLen));
                if (s != null)
                    return s; // found it!
                idx = name.substring(0, idx).lastIndexOf('.');
                if (idx < 0) break;
                nameChars[idx] = '$';
            }
            return null;
        }

    /**
     * Set the locale.
     */
    public void setLocale(String localeName) {
        // create locale specifics
        doclocale = new DocLocale(this, localeName, breakiterator);
        // reset Messager if locale has changed.
        messager.reset();
    }

    /** Check whether this member should be documented. */
    public boolean shouldDocument(VarSymbol sym) {
        long mod = sym.flags();

        if ((mod & Flags.SYNTHETIC) != 0) {
            return false;
        }

        return showAccess.checkModifier(mod);
    }

    /** Check whether this member should be documented. */
    public boolean shouldDocument(MethodSymbol sym) {
        long mod = sym.flags();

        if ((mod & Flags.SYNTHETIC) != 0) {
            return false;
        }

        return showAccess.checkModifier(mod);
    }

    /** check whether this class should be documented. */
    public boolean shouldDocument(ClassSymbol sym) {
        return
            (sym.flags_field&Flags.SYNTHETIC) == 0 && // no synthetics
            (docClasses || getClassDoc(sym).tree != null) &&
            isVisible(sym);
    }

    //### Comment below is inaccurate wrt modifier filter testing
    /**
     * Check the visibility if this is an nested class.
     * if this is not a nested class, return true.
     * if this is an static visible nested class,
     *    return true.
     * if this is an visible nested class
     *    if the outer class is visible return true.
     *    else return false.
     * IMPORTANT: This also allows, static nested classes
     * to be defined inside an nested class, which is not
     * allowed by the compiler. So such an test case will
     * not reach upto this method itself, but if compiler
     * allows it, then that will go through.
     */
    protected boolean isVisible(ClassSymbol sym) {
        long mod = sym.flags_field;
        if (!showAccess.checkModifier(mod)) {
            return false;
        }
        ClassSymbol encl = sym.owner.enclClass();
        return (encl == null || (mod & Flags.STATIC) != 0 || isVisible(encl));
    }

    //---------------- print forwarders ----------------//

    /**
     * Print error message, increment error count.
     *
     * @param msg message to print.
     */
    public void printError(String msg) {
        if (silent)
            return;
        messager.printError(msg);
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     */
    public void error(DocImpl doc, String key) {
        if (silent)
            return;
        messager.error(doc==null ? null : doc.position(), key);
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     */
    public void error(SourcePosition pos, String key) {
        if (silent)
            return;
        messager.error(pos, key);
    }

    /**
     * Print error message, increment error count.
     *
     * @param msg message to print.
     */
    public void printError(SourcePosition pos, String msg) {
        if (silent)
            return;
        messager.printError(pos, msg);
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     */
    public void error(DocImpl doc, String key, String a1) {
        if (silent)
            return;
        messager.error(doc==null ? null : doc.position(), key, a1);
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     */
    public void error(DocImpl doc, String key, String a1, String a2) {
        if (silent)
            return;
        messager.error(doc==null ? null : doc.position(), key, a1, a2);
    }

    /**
     * Print error message, increment error count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     */
    public void error(DocImpl doc, String key, String a1, String a2, String a3) {
        if (silent)
            return;
        messager.error(doc==null ? null : doc.position(), key, a1, a2, a3);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param msg message to print.
     */
    public void printWarning(String msg) {
        if (silent)
            return;
        messager.printWarning(msg);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     */
    public void warning(DocImpl doc, String key) {
        if (silent)
            return;
        messager.warning(doc==null ? null : doc.position(), key);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param msg message to print.
     */
    public void printWarning(SourcePosition pos, String msg) {
        if (silent)
            return;
        messager.printWarning(pos, msg);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     */
    public void warning(DocImpl doc, String key, String a1) {
        if (silent)
            return;
        messager.warning(doc==null ? null : doc.position(), key, a1);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     */
    public void warning(DocImpl doc, String key, String a1, String a2) {
        if (silent)
            return;
        messager.warning(doc==null ? null : doc.position(), key, a1, a2);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     */
    public void warning(DocImpl doc, String key, String a1, String a2, String a3) {
        if (silent)
            return;
        messager.warning(doc==null ? null : doc.position(), key, a1, a2, a3);
    }

    /**
     * Print warning message, increment warning count.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     */
    public void warning(DocImpl doc, String key, String a1, String a2, String a3,
                        String a4) {
        if (silent)
            return;
        messager.warning(doc==null ? null : doc.position(), key, a1, a2, a3, a4);
    }

    /**
     * Print a message.
     *
     * @param msg message to print.
     */
    public void printNotice(String msg) {
        if (silent || quiet)
            return;
        messager.printNotice(msg);
    }


    /**
     * Print a message.
     *
     * @param key selects message from resource
     */
    public void notice(String key) {
        if (silent || quiet)
            return;
        messager.notice(key);
    }

    /**
     * Print a message.
     *
     * @param msg message to print.
     */
    public void printNotice(SourcePosition pos, String msg) {
        if (silent || quiet)
            return;
        messager.printNotice(pos, msg);
    }

    /**
     * Print a message.
     *
     * @param key selects message from resource
     * @param a1 first argument
     */
    public void notice(String key, String a1) {
        if (silent || quiet)
            return;
        messager.notice(key, a1);
    }

    /**
     * Print a message.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     */
    public void notice(String key, String a1, String a2) {
        if (silent || quiet)
            return;
        messager.notice(key, a1, a2);
    }

    /**
     * Print a message.
     *
     * @param key selects message from resource
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     */
    public void notice(String key, String a1, String a2, String a3) {
        if (silent || quiet)
            return;
        messager.notice(key, a1, a2, a3);
    }

    /**
     * Exit, reporting errors and warnings.
     */
    public void exit() {
        // Messager should be replaced by a more general
        // compilation environment.  This can probably
        // subsume DocEnv as well.
        messager.exit();
    }

    protected Map<PackageSymbol, PackageDocImpl> packageMap =
            new HashMap<PackageSymbol, PackageDocImpl>();
    /**
     * Return the PackageDoc of this package symbol.
     */
    public PackageDocImpl getPackageDoc(PackageSymbol pack) {
        PackageDocImpl result = packageMap.get(pack);
        if (result != null) return result;
        result = new PackageDocImpl(this, pack);
        packageMap.put(pack, result);
        return result;
    }

    /**
     * Create the PackageDoc (or a subtype) for a package symbol.
     */
    void makePackageDoc(PackageSymbol pack, String docComment, F3Script tree) {
        PackageDocImpl result = packageMap.get(pack);
        docComment = processDocComment(docComment);
        if (result != null) {
            if (docComment != null) result.setRawCommentText(docComment);
            if (tree != null) result.setTree(tree);
        } else {
            result = new PackageDocImpl(this, pack, docComment, tree);
            packageMap.put(pack, result);
        }
    }


    protected Map<ClassSymbol, ClassDocImpl> classMap =
            new HashMap<ClassSymbol, ClassDocImpl>();
    /**
     * Return the ClassDoc (or a subtype) of this class symbol.
     */
    public ClassDocImpl getClassDoc(ClassSymbol clazz) {
        ClassDocImpl result = classMap.get(clazz);
        if (result != null) return result;
        result = new ClassDocImpl(this, clazz);
        classMap.put(clazz, result);
        return result;
    }

    /**
     * Create the ClassDoc (or a subtype) for a class symbol.
     */
    protected void makeClassDoc(ClassSymbol clazz, String docComment, F3ClassDeclaration tree, Position.LineMap lineMap) {
        ClassDocImpl result = classMap.get(clazz);
        docComment = processDocComment(docComment);
        if (result != null) {
            if (docComment != null) result.setRawCommentText(docComment);
            if (tree != null) result.setTree(tree);
            return;
        }
        result = new ClassDocImpl(this, clazz, docComment, tree, lineMap);
        classMap.put(clazz, result);
    }

    protected Map<VarSymbol, FieldDocImpl> fieldMap =
            new HashMap<VarSymbol, FieldDocImpl>();
    /**
     * Return the FieldDoc of this var symbol.
     */
    public FieldDocImpl getFieldDoc(VarSymbol var) {
        FieldDocImpl result = fieldMap.get(var);
        if (result != null) return result;
        result = new FieldDocImpl(this, var);
        fieldMap.put(var, result);
        return result;
    }
    /**
     * Create a FieldDoc for a var symbol.
     */
    protected void makeFieldDoc(VarSymbol var, String docComment, F3Var tree, Position.LineMap lineMap) {
        FieldDocImpl result = fieldMap.get(var);
        docComment = processDocComment(docComment);
        if (result != null) {
            if (docComment != null) result.setRawCommentText(docComment);
            if (tree != null) result.setTree(tree);
        } else {
            result = new FieldDocImpl(this, var, docComment, tree, lineMap);
            fieldMap.put(var, result);
        }
    }

    protected Map<MethodSymbol, ExecutableMemberDocImpl> methodMap =
            new HashMap<MethodSymbol, ExecutableMemberDocImpl>();
    /**
     * Create a MethodDoc for this MethodSymbol.
     * Should be called only on symbols representing methods.
     */
    protected void makeFunctionDoc(MethodSymbol meth, String docComment,
                       F3FunctionDefinition tree, Position.LineMap lineMap) {
        FunctionDocImpl result = (FunctionDocImpl)methodMap.get(meth);
        docComment = processDocComment(docComment);
        if (result != null) {
            if (docComment != null) result.setRawCommentText(docComment);
            if (tree != null) result.setTree(tree);
        } else {
            result = new FunctionDocImpl(this, meth, docComment, tree, lineMap);
            methodMap.put(meth, result);
        }
    }

    /**
     * Return the MethodDoc for a MethodSymbol.
     * Should be called only on symbols representing methods.
     */
    public FunctionDocImpl getFunctionDoc(MethodSymbol meth) {
        FunctionDocImpl result = (FunctionDocImpl)methodMap.get(meth);
        if (result != null) return result;
        result = new FunctionDocImpl(this, meth);
        methodMap.put(meth, result);
        return result;
    }

    /**
     * Create the ConstructorDoc for a MethodSymbol.
     * Should be called only on symbols representing constructors.
     */
    protected void makeConstructorDoc(MethodSymbol meth, String docComment,
                            F3FunctionDefinition tree, Position.LineMap lineMap) {
        ConstructorDocImpl result = (ConstructorDocImpl)methodMap.get(meth);
        docComment = processDocComment(docComment);
        if (result != null) {
            if (docComment != null) 
                result.setRawCommentText(docComment);
            if (tree != null) result.setTree(tree);
        } else {
            result = new ConstructorDocImpl(this, meth, docComment, tree, lineMap);
            methodMap.put(meth, result);
        }
    }

    /**
     * Return the ConstructorDoc for a MethodSymbol.
     * Should be called only on symbols representing constructors.
     */
    public ConstructorDocImpl getConstructorDoc(MethodSymbol meth) {
        ConstructorDocImpl result = (ConstructorDocImpl)methodMap.get(meth);
        if (result != null) return result;
        result = new ConstructorDocImpl(this, meth);
        methodMap.put(meth, result);
        return result;
    }

    /**
     * Set the encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get the encoding.
     */
    public String getEncoding() {
        return encoding;
    }
    
    protected boolean isF3Symbol(Symbol sym) {
        ClassSymbol cls = sym instanceof ClassSymbol ? (ClassSymbol)sym : sym.enclClass();
        return types.isF3Class(cls);
    }

    protected boolean isMixin(ClassSymbol tsym) {
        return types.isMixin(tsym);
    }
    
    protected boolean isSequence(Symbol sym) {
        return sym != null && types.isSequence(sym.type);
    }
    
    protected com.sun.tools.mjavac.code.Type sequenceType(com.sun.tools.mjavac.code.Type type) {
        return types.elementType(type);
    }
    
    protected String simpleFunctionalTypeName(com.sun.tools.mjavac.code.Type type) {
        if (type instanceof FunctionType) {
            FunctionType func = (FunctionType)type;
            MethodType mtype = func.asMethodType();
            StringBuilder s = new StringBuilder();
            s.append("function(");
            if (mtype == null)
                s.append("???");
            else {
                com.sun.tools.mjavac.util.List<com.sun.tools.mjavac.code.Type> args = mtype.argtypes;
                for (com.sun.tools.mjavac.util.List<com.sun.tools.mjavac.code.Type> l = args; 
                        l.nonEmpty(); l = l.tail) {
                    if (l != args)
                        s.append(',');
                    s.append(':');
                    s.append(simpleName(l.head.tsym));
                }
            }
            s.append("):");
            s.append(mtype == null ? "???" : simpleName(mtype.restype.tsym));
            return s.toString();
        } else
            return type.toString();
    }
    
    private String simpleName(TypeSymbol tsym) {
        // print Void correctly as F3 type
        return tsym.type.tag == TypeTags.VOID ? "Void" : tsym.getSimpleName().toString();
    }

    /**
     * Strips a raw docComment of its opening asterisks and whitespace, using
     * the javac DocCommentScanner.
     * @param rawText  the original comment text
     * @return  the comment text, minus blocking characters
     */
    protected String processDocComment(String rawText) {
        if (rawText == null)
            return null;
        com.sun.tools.mjavac.parser.Scanner scanner = scannerFactory.newScanner(rawText);
        do { 
            scanner.nextToken(); 
        } while (scanner.token() != Token.EOF);
        String ret = scanner.docComment().trim();
        return ret != null ? ret : rawText; // true if comment was already processed
    }
}
