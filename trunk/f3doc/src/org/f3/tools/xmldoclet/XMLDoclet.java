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

package org.f3.tools.xmldoclet;

import com.sun.javadoc.*;
import org.f3.tools.code.FunctionType;
import org.f3.tools.xslhtml.XHTMLProcessingUtils;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

/**
 * Javadoc doclet which generates XML output.
 * @author tball
 */
public class XMLDoclet {
    private PrintWriter out;
    private Transformer serializer;
    private TransformerHandler hd;
    private AttributesImpl attrs;
    
    // option values
    private static String outFileName = null;
    private static List<String> xmlFiles = new ArrayList<String>();
    // paths from which we would load soure files and "doc-files" to copy
    private static String sourcePath;
    private static File outDocsDir = new File("f3docs");
    private static boolean includeAuthorTags = false;
    private static boolean includeDeprecatedTags = true;
    private static boolean includeSinceTags = true;
    private static boolean includeVersionTags = false;
    private static boolean processXSLT = true;
    
    private static ResourceBundle messageRB = null;
    private static String xsltFileName = null;
    
    private static final boolean debug = false;
    private static final Map<String,String> params = new HashMap<String, String>();
    
    static final Option[] options = {
        new Option("-o", getString("out.file.option"), getString("out.file.description")),
        new Option("-version", getString("version.description")),
        new Option("-author", getString("author.description")),
        new Option("-nosince", getString("nosince.description")),
        new Option("-nodeprecated", getString("nodeprecated.description")),
        new Option("-nohtml", getString("nohtml.description")),
        new Option("-xsltfile", getString("out.file.option"), getString("xsltfile.description")),
        new Option("-mastercss", getString("out.file.option"), getString("xsltfile.description")),
        new Option("-extracss", getString("out.file.option"), getString("xsltfile.description")),
        new Option("-extrajs", getString("out.file.option"), getString("xsltfile.description")),
        new Option("-extrajs2", getString("out.file.option"), getString("xsltfile.description")),
        new Option("-xsl:", getString("xslproperty.description"), "name=value"),
        new Option("-d", getString("out.dir.option"), getString("out.dir.description")),
        new Option("-i", getString("in.file.option"), getString("in.file.description"))
    };

    /**
     * Generate documentation here.
     * This method is required for all doclets.
     *
     * @return true on success.
     */
    public static boolean start(RootDoc root) {
        try {
            XMLDoclet doclet = new XMLDoclet();
            doclet.generateXML(root);
            
            if(processXSLT) {
                FileInputStream xsltStream = xsltFileName != null ? 
                    new FileInputStream(xsltFileName) : null;
                XHTMLProcessingUtils.process(xmlFiles, xsltStream, sourcePath, outDocsDir, params);
            } else {
                /*
                 * We are just generating XML files. But, we would still like to copy
                 * "doc-files" subdirectories in location where we generate intermediate
                 * XML files. In a subsequent run where XML files are specified as inputs
                 * we can copy "doc-files" to right place along with output HTMLs.
                 */
                PackageDoc[] pkgs = doclet.packagesToProcess(root);
                String[] pkgNames = new String[pkgs.length];
                for (int index = 0; index < pkgs.length; index++) {
                    pkgNames[index] = pkgs[index].name();
                }
                Util.copyDocFiles(pkgNames, sourcePath, new File(outFileName).getParentFile());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check for doclet-added options.  Returns the number of
     * arguments you must specify on the command line for the
     * given option.  For example, "-d docs" would return 2.
     * <P>
     * This method is required if the doclet contains any options.
     * If this method is missing, Javadoc will print an invalid flag
     * error for every option.
     *
     * @return number of arguments on the command line for an option
     *         including the option name itself.  Zero return means
     *         option not known.  Negative value means error occurred.
     */
    public static int optionLength(String option) {
        if (option.equals("-help")) {
            System.out.println(getString("help.header"));
            for (Option o : options)
                System.out.println(o.help());
            return 1;
        }
        for (Option o : options) {
            if (o.name().equals(option))
                return o.length();
            }
        return 0;  // default is option unknown
    }

    /**
     * Check that options have the correct arguments.
     * Printing option related error messages (using the provided
     * DocErrorReporter) is the responsibility of this method.
     *
     * @return true if the options are valid.
     */
    public static boolean validOptions(String options[][],
                                       DocErrorReporter reporter) {
        if (debug) {
            for(int i=0; i< options.length; i++) {
                for(int j=0; j<options[i].length; j++) {
                    System.out.println("got: " + options[i][j]);
                }
            }
        }
        List<String> otherInputs = new ArrayList<String>();
        for (String[] option : options) {
            if (option[0].equals("-sourcepath")) {
                sourcePath = option[1];
            } else if (option[0].equals("-o"))
                outFileName = option[1];
            else if (option[0].equals("-i"))
                otherInputs.add(option[1]);
            else if (option[0].equals("-version"))
                includeVersionTags = true;
            else if (option[0].equals("-author"))
                includeAuthorTags = true;
            else if (option[0].equals("-nosince"))
                includeSinceTags = false;
            else if (option[0].equals("-nodeprecated"))
                includeDeprecatedTags = false;
            else if (option[0].equals("-nohtml"))
                processXSLT = false;
            else if (option[0].equals("-xsltfile"))
                xsltFileName = option[1];
            else if (option[0].equals("-mastercss"))
                params.put("master-css",option[1]);
            else if (option[0].equals("-extracss"))
                params.put("extra-css",option[1]);
            else if (option[0].equals("-extrajs"))
                params.put("extra-js",option[1]);
            else if (option[0].equals("-extrajs2"))
                params.put("extra-js2",option[1]);
            else if (option[0].equals("-d"))
                outDocsDir = new File(option[1]);
            else if (option[0].startsWith("-xsl:")) {
                String s = option[1];
                int i = s.indexOf('=');
                if (i == -1)
                    return false;
                String name = s.substring(0, i);
                String value = s.substring(i+1);
                System.out.println("using a custom XSL parameter: '" + name + "'='" + value +"'");
                params.put(name, value);
            }
        }
        if (outFileName == null) {
            try {
                File f = File.createTempFile("javadoc", ".xml");
                outFileName = f.getPath();
            } catch (IOException ex) {
                Logger.getLogger(XMLDoclet.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        xmlFiles.add(outFileName);
        xmlFiles.addAll(otherInputs);
        if (sourcePath == null) {
            Set<String> inputDirs = new HashSet<String>();
            for (String in : otherInputs) {
                inputDirs.add(new File(in).getParent());
            }
            StringBuilder buf = new StringBuilder();
            for (String in : inputDirs) {
                buf.append(in);
                buf.append(File.pathSeparatorChar);
            }
            sourcePath = buf.toString();
            if (sourcePath.length() == 0) {
                sourcePath = ".";
            }
        }
        return true;
    }

    private PackageDoc[] packagesToProcess(RootDoc root) {
        Set<PackageDoc> set = new HashSet<PackageDoc>(Arrays.asList(root.specifiedPackages()));
        ClassDoc[] classes = root.specifiedClasses();
        for (int i = 0; i < classes.length; i++) {
            set.add(classes[i].containingPackage());
        }
        ArrayList<PackageDoc> results = new ArrayList<PackageDoc>(set);
        Collections.sort(results);
        return results.toArray(new PackageDoc[] {});
    }

    /**
     * Return the version of the Java Programming Language supported
     * by this doclet.
     *
     * @return  the language version supported by this doclet.
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
    
    void generateXML(RootDoc root) throws IOException, TransformerException, SAXException {
        initTransformer();
        attrs.clear();
        hd.startElement("", "", "javadoc", attrs);
        generateComment(root);
        for (PackageDoc pkg : packagesToProcess(root))
            generatePackage(pkg);
        hd.endElement("","","javadoc");
        hd.endDocument();
    }

    private void generateExecutableMember(ExecutableMemberDoc exec, String kind) throws SAXException {
        if (!exec.isSynthetic()) {
            attrs.clear();
            attrs.addAttribute("", "", "name", "CDATA", exec.name());
            attrs.addAttribute("", "", "qualifiedName", "CDATA", exec.qualifiedName());
            if (exec instanceof MethodDoc)
                attrs.addAttribute("", "", "varargs", "CDATA", Boolean.toString(exec.isVarArgs()));
            hd.startElement("", "", kind, attrs);
            generateComment(exec);
            generateAnnotations(exec.annotations());
            generateModifiers(exec);
            generateTypeParameters(exec.typeParameters());
            generateParameters(exec.parameters(),exec);
            Type[] exceptions = exec.thrownExceptionTypes();
            if (exceptions.length > 0) {
                attrs.clear();
                hd.startElement("", "", "thrownExceptions", attrs);
                for (Type t : exceptions)
                    generateTypeRef(t, "exception", null);
                hd.endElement("", "", "thrownExceptions");
            }
            if (exec instanceof MethodDoc) {
                MethodDoc m = (MethodDoc)exec;
                generateTypeRef(m.returnType(), "returns", rawReturnType(m));
                MethodDoc overridden = m.overriddenMethod();
                if (overridden != null) {
                    String name = overridden.qualifiedName();
                    attrs.clear();
                    attrs.addAttribute("", "", "name", "CDATA", name);
                    hd.startElement("", "", "overrides", attrs);
                    hd.endElement("", "", "overrides");
                }
            }
            hd.endElement("", "", kind);
        }
    }

    private void generateField(FieldDoc field, String kind) throws SAXException {
        if (!field.isSynthetic()) {
            attrs.clear();
            attrs.addAttribute("", "", "name", "CDATA", field.name());
            attrs.addAttribute("", "", "qualifiedName", "CDATA", field.qualifiedName());
            attrs.addAttribute("", "", "enumConstant", "CDATA", Boolean.toString(field.isEnumConstant()));
            hd.startElement("", "", kind, attrs);
            generateComment(field);
            generateAnnotations(field.annotations());
            generateModifiers(field);
            generateTypeRef(field.type(), "type", rawType(field));
            String constantValue = field.constantValueExpression();
            if (constantValue != null) {
                attrs.clear();
                attrs.addAttribute("", "", "value", "CDATA", constantValue);
                hd.startElement("", "", "constant", attrs);
                hd.endElement("", "", "constant");
            }
            hd.endElement("", "", kind);
        }
    }

    private void generateFullHierarchy(ClassDoc cls) throws SAXException {
        attrs.clear();
        hd.startElement("", "", "hierarchy", attrs);
        
        
        //return a list of inherited types, filtering out duplicates
        //and preserving the in-order traversal
        List<Type> types = findInheritedTypes(cls);
        Set<Type> uniqueTypes = new HashSet<Type>();
        ListIterator<Type> it = types.listIterator();
        while(it.hasNext()) {
            Type type = it.next();
            if(uniqueTypes.contains(type)) {
                it.remove();
            } else {
                uniqueTypes.add(type);
            }
        }
        
        //generate xml for the final list of inherited types
        for (Type intf : types) {
            generateTypeRef(intf, "super", null);
        }
        hd.endElement("", "", "hierarchy");
    }
    
    private List<Type> findInheritedTypes(ClassDoc cls) {
        List<Type> types = new ArrayList<Type>();
        for (Type type : cls.interfaces()) {
            if (type != null) {
                types.add(type);
                ClassDoc cd = type.asClassDoc();
                types.addAll(findInheritedTypes(cd));
            }
        }
        return types;
    }

    private void generatePackage(PackageDoc pkg) throws SAXException {
        ClassDoc[] allClasses = pkg.allClasses();
        if (allClasses.length == 0)
            return;

        // TODO: call containingPackage() on a class in this package,
        // before processing this package. This has the side effect of
        // calling PackageDocImpl.setDocPath() on this package, which is
        // necessary in order for this package's doc to be processed.
        // If setDocPath() hasn't been called by the time generateComment()
        // is called, the package doc will be missing.
        allClasses[0].containingPackage();

        attrs.clear();
        attrs.addAttribute("", "", "name", "CDATA", pkg.name());
        hd.startElement("", "", "package", attrs);
        generateComment(pkg);
        generateAnnotations(pkg.annotations());
        for (ClassDoc cls : pkg.allClasses())
            generateClass(cls);
        hd.endElement("", "", "package");
    }

    private void generateClass(ClassDoc cls) throws SAXException {
        /**
         * F3 generates class for modules too. So, a "package-info.f3" will 
         * result in a class. Because we have captured package level doc comment
         * we can ignore this class. Without this "package-info" will appear in
         * classes list!
         */
        if (cls.simpleTypeName().equals("package-info")) {
            return;
        }
        boolean f3Class = isF3Class(cls);
        String classType = 
                cls.isAnnotationType() ? "annotation" :
                cls.isEnum() ? "enum" :
                cls.isInterface() ? "interface" :
                cls.isAbstract() ? "abstractClass" :
                "class";
        attrs.clear();
        attrs.addAttribute("", "", "name", "CDATA", cls.name());
        attrs.addAttribute("", "", "qualifiedName", "CDATA", cls.qualifiedName());
        attrs.addAttribute("", "", "packageName", "CDATA", cls.containingPackage().name());
        ClassDoc containingClass = cls.containingClass();
        if (containingClass != null) {
            attrs.addAttribute("", "", "outerClass", "CDATA", containingClass.qualifiedName());
        }
        attrs.addAttribute("", "", "language", "CDATA", f3Class ? "f3" : "java");
        attrs.addAttribute("", "", "classType","CDATA",classType);
        hd.startElement("", "", "class", attrs);
        generateComment(cls);
        generateModifiers(cls);
        if (!f3Class) {
            generateAnnotations(cls.annotations());
            generateTypeParameters(cls.typeParameters());
        }
        if (cls.superclass() != null && !cls.superclass().qualifiedName().equals("java.lang.Object")) {
            generateTypeRef(cls.superclass(), "superclass", null);
        }
        attrs.clear();
        hd.startElement("", "", "interfaces", attrs);
        for (Type intf : cls.interfaces())
            generateTypeRef(intf, "interface", null);
        hd.endElement("", "", "interfaces");
        generateFullHierarchy(cls);
        if (!f3Class) {
        for (ConstructorDoc cons : cls.constructors())
            generateExecutableMember(cons, "constructor");
        }
        
        for (MethodDoc meth : cls.methods()) {
            if (f3Class) {
                generateExecutableMember(meth, meth.isStatic()? 
                    "script-function" : "function");
            } else {
                generateExecutableMember(meth, "method");
            }
        }

        for (FieldDoc field : cls.fields()) {
            if (f3Class) {
                generateField(field, field.isStatic()? 
                    "script-var" : "var");
            } else {
                generateField(field, "field");
            }
        }
        for (FieldDoc field : cls.enumConstants())
            generateField(field, f3Class ? "script-var" : "field");
        
        hd.endElement("", "", "class");
    }
    
    private void generateModifiers(ProgramElementDoc element) throws SAXException {
        attrs.clear();
        boolean bound = isBoundFunction(element);
        String modifiersText = element.modifiers();
        attrs.addAttribute("", "", "text", "CDATA", modifiersText);
        hd.startElement("", "", "modifiers", attrs);
        attrs.clear();
        if (element.isPublic()) {
            hd.startElement("", "", "public", attrs);
            hd.endElement("", "", "public");
        }
        else if (element.isProtected()) {
            hd.startElement("", "", "protected", attrs);
            hd.endElement("", "", "protected");
        }
        else if (element.isPackagePrivate()) {
            hd.startElement("", "", "package", attrs);
            hd.endElement("", "", "package");
        }
        else if (element.isPrivate()) {
            hd.startElement("", "", "private", attrs);
            hd.endElement("", "", "private");
        }
        else {
            hd.startElement("", "", "script-private", attrs);
            hd.endElement("", "", "script-private");
        }
        if (element.isStatic()) {
            hd.startElement("", "", "static", attrs);
            hd.endElement("", "", "static");
        }
        if (element.isFinal()) {
            hd.startElement("", "", "final", attrs);
            hd.endElement("", "", "final");
        }
        if (isPublicRead(element)) {
            hd.startElement("", "", "public-read", attrs);
            hd.endElement("", "", "public-read");
        }
        if (isPublicInit(element)) {
            hd.startElement("", "", "public-init", attrs);
            hd.endElement("", "", "public-init");
        } 
        if (isAbstract(element)) {
            hd.startElement("", "", "abstract", attrs);
            hd.endElement("", "", "abstract");
        }
        if (isDef(element)) {  // not sure how we want to document this
            hd.startElement("", "", "read-only", attrs);
            hd.endElement("", "", "read-only");
        }
        if (element instanceof ClassDoc &&
            isMixin((ClassDoc)element)) {
            hd.startElement("", "", "mixin", attrs);
            hd.endElement("", "", "mixin");
        }
        /***
        if (element.isNative()) {
            hd.startElement("", "", "native", attrs);
            hd.endElement("", "", "native");
        }
        if (element.isStrict()) {
            hd.startElement("", "", "strictfp", attrs);
            hd.endElement("", "", "strictfp");
        }
        **/
        if (bound) {
            hd.startElement("", "", "bound", attrs);
            hd.endElement("", "", "bound");
        }
        hd.endElement("", "", "modifiers");
    }

    private void generateParameters(Parameter[] parameters, ProgramElementDoc doc) throws SAXException {
        attrs.clear();
        hd.startElement("", "", "parameters", attrs);
        List<Tag> paramDocs = new ArrayList<Tag>();
        for(Tag t : doc.tags()) {
            if("@param".equals(t.kind())) {
                paramDocs.add(t);
            }
        }
        
        for(int i=0; i<parameters.length; i++) {
            Parameter p = parameters[i];
            attrs.clear();
            attrs.addAttribute("", "", "name", "CDATA", p.name());
            hd.startElement("", "", "parameter", attrs);
            generateTypeRef(p.type(), "type", rawType(p));
            generateAnnotations(p.annotations());
            attrs.clear();
            hd.startElement("","", "docComment", attrs);
            if(paramDocs.size() > i) {
                Tag t = paramDocs.get(i);
                Tag[] inlineTags = t.inlineTags();
                if (inlineTags.length <= 1) {
                    String text = t.text();
                    text = text.trim();
                    //trim off the name of the variable, if present
                    int n = text.indexOf(" ");
                    if(n > 0) {
                        text = text.substring(n);
                    }
                    hd.characters(text.toCharArray(), 0, text.length());
                } else {
                    generateTags(inlineTags, "inlineTags");
                }
            }
            hd.endElement("", "", "docComment");
            hd.endElement("", "", "parameter");
        }
        hd.endElement("", "", "parameters");
    }

    private void generateTypeParameters(TypeVariable[] typeParameters) throws SAXException {
        if (typeParameters.length > 0) {
            attrs.clear();
            hd.startElement("", "", "typeParameters", attrs);
            for (TypeVariable tp : typeParameters) {
                attrs.clear();
                attrs.addAttribute("", "", "typeName", "CDATA", tp.typeName());
                attrs.addAttribute("", "", "simpleTypeName", "CDATA", tp.simpleTypeName());
                attrs.addAttribute("", "", "qualifiedTypeName", "CDATA", tp.qualifiedTypeName());
                hd.startElement("", "", "typeParameter", attrs);
                hd.endElement("", "", "typeParameter");
            }
            hd.endElement("", "", "typeParameters");
        }
    }
    
    private void generateTypeRef(Type type, String kind, 
                                 com.sun.tools.mjavac.code.Type rawType) throws SAXException {
        if (type != null) {
            attrs.clear();
            ClassDoc cd = type.asClassDoc();
            boolean isSequence = false;
            if (cd != null) {
                isSequence = isSequence(cd);
                if (isSequence) {
                    if (rawType == null)
                        throw new AssertionError("unknown sequence type");
                    type = sequenceType(cd, rawType);
                    cd = type.asClassDoc();
                }
            }
            boolean isFunctionType = rawType instanceof FunctionType;
            String simpleName = isFunctionType ? 
                simpleFunctionalTypeName(cd, rawType) : type.simpleTypeName();
            attrs.addAttribute("", "", "typeName", "CDATA", type.typeName());
            attrs.addAttribute("", "", "simpleTypeName", "CDATA", simpleName);
            attrs.addAttribute("", "", "qualifiedTypeName", "CDATA", type.qualifiedTypeName());
            if(cd != null && !type.isPrimitive()) {
                attrs.addAttribute("", "", "packageName", "CDATA", cd.containingPackage().name());
            }
            attrs.addAttribute("", "", "dimension", "CDATA", type.dimension());
            attrs.addAttribute("", "", "toString", "CDATA", type.qualifiedTypeName() + type.dimension());
            attrs.addAttribute("", "", "sequence", "CDATA", Boolean.toString(isSequence));
            attrs.addAttribute("", "", "functionType", "CDATA", Boolean.toString(isFunctionType));
            hd.startElement("", "", kind, attrs);                    
            hd.endElement("", "", kind);
        }
    }
    
    private void generateComment(Doc doc) throws SAXException {
        String rawCommentText = doc.getRawCommentText();
        if (rawCommentText.length() > 0) {
            attrs.clear();
            hd.startElement("", "", "docComment", attrs);            
            hd.startElement("", "", "rawCommentText", attrs);
            hd.characters(rawCommentText.toCharArray(), 0, rawCommentText.length());
            hd.endElement("", "", "rawCommentText");

            String commentText = doc.commentText();
            hd.startElement("", "", "commentText", attrs);
            hd.characters(commentText.toCharArray(), 0, commentText.length());
            hd.endElement("", "", "commentText");
            
            generateTags(doc.tags(), "tags");
            Tag[] firstSentenceTags = doc.firstSentenceTags();
            generateTags(firstSentenceTags, "firstSentenceTags");
            generateTags(doc.seeTags(), "seeTags");
            Tag[] inlineTags = getInlineTags(doc);
            generateTags(inlineTags, "inlineTags");
            
            boolean multipleSentences = false;
            if(inlineTags.length != firstSentenceTags.length) {
                multipleSentences = true;
            } else {
                for(int i=0; i<firstSentenceTags.length; i++) {
                    if(!firstSentenceTags[i].text().equals(inlineTags[i].text())) {
                        multipleSentences = true;
                    }
                }
            }
            
            attrs.clear();
            attrs.addAttribute("", "", "multipleSentences", "CDATA", Boolean.toString(multipleSentences));
            hd.startElement("", "", "extraNotes", attrs);
            hd.endElement("", "", "extraNotes");

            
            hd.endElement("", "", "docComment");
        }
    }

    private void generateTags(Tag[] tags, String tagKind) throws SAXException, SAXException {
        if (tags.length == 0)
            return;
        attrs.clear();
        hd.startElement("", "", tagKind, attrs);
        for (Tag t : tags) {
            String kind = t.kind();
            if (kind.startsWith("@"))
                kind = kind.substring(1);
            if (kind.equals("@author") && !includeAuthorTags)
                continue;
            if (kind.equals("@deprecated") && !includeDeprecatedTags)
                continue;
            if (kind.equals("@since") && !includeSinceTags)
                continue;
            if (kind.equals("@version") && !includeVersionTags)
                continue;
            if (!kind.matches("\\w+")) {
                System.out.println("possible invalid tag kind: " + kind);
                kind = "invalidtag";
            }
            attrs.clear();
            attrs.addAttribute("", "", "name", "CDATA", t.name());
            String label = null;
            //process see, link tags specially
            if("@see".equals(t.name()) || "@link".equals(t.name())) {
                String href = t.text();
                if(t instanceof SeeTag) {
                    SeeTag see = (SeeTag) t;
                    if(see.referencedClass() != null) {
                        href = "../"
                                +see.referencedClass().containingPackage().name()
                                +"/"
                                +see.referencedClassName()+".html";
                        MemberDoc referencedMember = see.referencedMember();
                        if (referencedMember != null) {
                            if (referencedMember instanceof ExecutableMemberDoc) {
                                ExecutableMemberDoc execMember =(ExecutableMemberDoc)referencedMember;
                                href += "#" + execMember.name() + execMember.signature();                                
                            } else {
                                href += "#"+ see.referencedMemberName();
                            }
                        }
                    }
                    label = see.label();
                } 
                
                if (label == null || label.length() == 0) {
                    label = t.text();
                }
                //p("final href = " + href);
                attrs.addAttribute("", "", "href", "CDATA", href);
                if(label.startsWith("#")) {
                    label = label.substring(1);
                }
                attrs.addAttribute("", "", "label", "CDATA", label);
            }
            boolean isThrows = false;
            if ("@throws".equals(t.name())) {
                isThrows = true;
                ThrowsTag tt = (ThrowsTag)t;
                attrs.addAttribute("", "", "exceptionName", "CDATA", tt.exceptionName());  
            }
            hd.startElement("", "", kind, attrs);
            if (isThrows) {
                ThrowsTag tt = (ThrowsTag)t;
                generateTypeRef(tt.exceptionType(), "exception", null);
                hd.startElement("", "", "comment", null);
                String comment = tt.exceptionComment();
                hd.characters(comment.toCharArray(), 0, comment.length());
                hd.endElement("", "", "comment");
            }
            Tag[] inlineTags = t.inlineTags();
            if (inlineTags.length <= 1) {
                // for @throws we have already collected everything..
                if (! isThrows) {
                    String text = t.text();
                    hd.characters(text.toCharArray(), 0, text.length());
                }
            } else {
                generateTags(inlineTags, "inlineTags");
            }
            hd.endElement("", "", kind);
        }
        
        hd.endElement("", "", tagKind);
    }
    
    private Tag[] getInlineTags(Doc doc) throws SAXException, SAXException {
        List<Tag> list = new ArrayList<Tag>();
        Tag[] inlineTags = doc.inlineTags();
        if (inlineTags.length > 0 && doc.isMethod()) { // inheritDoc tag only valid for methods
            list.addAll(Arrays.asList(inlineTags));
            boolean changed = false;
            for(int i=0; i<list.size(); i++) {
                Tag t = list.get(i);
                if(t.kind().matches("@inheritDoc")) {
                    Doc inherited = getInheritedDoc(doc);
                    if (inherited != null) {
                        list.remove(i);
                        list.addAll(i,Arrays.asList(getInlineTags(inherited)));
                        changed = true;
                    }
                    break;
                }
            }
            if (changed)
                inlineTags = list.toArray(new Tag[0]);
        }
        return inlineTags;
    }

    
    private void generateAnnotations(AnnotationDesc[] annotations) throws SAXException {
        if (annotations.length > 0) {
            attrs.clear();
            hd.startElement("", "", "annotations", attrs);
            for (AnnotationDesc desc : annotations) {
                attrs.clear();
                hd.startElement("", "", "annotationType", attrs);
                AnnotationTypeDoc type = desc.annotationType();
                attrs.addAttribute("", "", "simpleName", "CDATA", type.simpleTypeName());
                attrs.addAttribute("", "", "qualifiedName", "CDATA", type.qualifiedTypeName());
                attrs.addAttribute("", "", "dimension", "CDATA", type.dimension());
                hd.startElement("", "", type.typeName(), attrs);

                AnnotationTypeElementDoc[] elements = type.elements();
                for (AnnotationTypeElementDoc element : elements) {
                    if (!element.isSynthetic()) {
                        attrs.clear();
                        attrs.addAttribute("", "", "name", "CDATA", element.name());
                        attrs.addAttribute("", "", "qualifiedName", "CDATA", element.qualifiedName());
                        attrs.addAttribute("", "", "commentText", "CDATA", element.commentText());
                        AnnotationValue defValue = element.defaultValue();
                        if (defValue != null)
                            attrs.addAttribute("", "", "defaultValue", "CDATA", defValue.toString());
                        hd.startElement("", "", "element", attrs);
                        hd.endElement("", "", "element");
                    }
                }

                hd.endElement("", "", type.typeName());
                hd.endElement("", "", "annotationType");
            }
            hd.endElement("", "", "annotations");
        }
    }

    private Doc getInheritedDoc(Doc doc) {
        if(doc instanceof MethodDoc) {
            return getOverriddenMethod((MethodDoc)doc);
        }
        return null;
    }


    private MethodDoc getOverriddenMethod(MethodDoc doc) {
        ClassDoc cls = doc.containingClass();
        ClassDoc scls = cls.superclass();
        MethodDoc meth = findDeclaredMethod(scls, doc);
        if (meth == null && isF3Class(cls)) {
            for (ClassDoc intf : cls.interfaces()) {
                meth = findDeclaredMethod(intf, doc);
                if (meth != null)
                    break;
            }
        }
        return meth;
    }

    private MethodDoc findDeclaredMethod(ClassDoc scls, MethodDoc doc) {
        MethodDoc[] meths = scls.methods();
        for (MethodDoc md : meths) {
            if (md.name().equals(doc.name()) && md.signature().equals(doc.signature())) {
                return md;
            }
        }
        return null;
    }

    private void initTransformer() throws IOException, SAXException, TransformerException {
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(3));
        hd = tf.newTransformerHandler();
        serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.setOutputProperty(OutputKeys.VERSION, "1.0");
        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        //TODO: serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"???.dtd");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");

        File f = new File(outFileName);
        f.getParentFile().mkdirs();
        out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
        StreamResult streamResult = new StreamResult(out);
        attrs = new AttributesImpl();        
        hd.setResult(streamResult);
        hd.startDocument();
    }
    
    static String getString(String key) {
        ResourceBundle msgRB = messageRB;
        if (msgRB == null) {
            try {
                messageRB = msgRB =
                    ResourceBundle.getBundle("org.f3.tools.xmldoclet.resources.xmldoclet");
            } catch (MissingResourceException e) {
                throw new Error("Fatal: Resource for f3doc is missing");
            }
        }
        return msgRB.getString(key);
    }
    
    private static boolean isF3Class(ClassDoc clsDoc) {
        return probe(clsDoc, "isF3Class");
    }

    private static boolean isMixin(ClassDoc clsDoc) {
        return probe(clsDoc, "isMixin");
    }
    
    private static boolean isSequence(ClassDoc clsDoc) {
        return probe(clsDoc, "isSequence");
    }
    
    private static boolean probe(ClassDoc clsDoc, String method) {
        try {
            Class<?> cls = clsDoc.getClass();
            Method m = cls.getDeclaredMethod(method);
            Object result = m.invoke(clsDoc);
            return ((Boolean)result).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }
    
    private static com.sun.tools.mjavac.code.Type rawType(FieldDoc field) {
        return rawType(field, "rawType");
    }
    
    private static com.sun.tools.mjavac.code.Type rawType(Parameter param) {
        return rawType(param, "rawType");
    }
    
    private static com.sun.tools.mjavac.code.Type rawReturnType(MethodDoc method) {
        return rawType(method, "rawReturnType");
    }
    
    private static com.sun.tools.mjavac.code.Type rawType(Object o, String method) {
        try {
            Class<?> cls = o.getClass();
            Method m = cls.getDeclaredMethod(method);
            Object result = m.invoke(o);
            return (com.sun.tools.mjavac.code.Type)result;
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean getBooleanFlag(ProgramElementDoc doc, String flagMethod) {
        try {
            Class<?> cls = doc.getClass();
            Method m = cls.getMethod(flagMethod);
            return (Boolean)m.invoke(doc);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isBoundFunction(ProgramElementDoc doc) {
        if (!(doc instanceof ExecutableMemberDoc))
            return false;
        return getBooleanFlag(doc, "isBound");
    }
    
    private boolean isScriptPrivate(ProgramElementDoc doc) {
        return getBooleanFlag(doc, "isScriptPrivate");
    }
    
    private boolean isPublicInit(ProgramElementDoc doc) {
        if (!(doc instanceof FieldDoc))
            return false;
        return getBooleanFlag(doc, "isPublicInit");
    }
    
    private boolean isPublicRead(ProgramElementDoc doc) {
        if (!(doc instanceof FieldDoc))
            return false;
        return getBooleanFlag(doc, "isPublicRead");
    }
    
    private boolean isDef(ProgramElementDoc doc) {
        if (!(doc instanceof FieldDoc))
            return false;
        return getBooleanFlag(doc, "isDef");
    }
    
    private boolean isAbstract(ProgramElementDoc doc) {
        return getBooleanFlag(doc, "isAbstract");
    }
    
    private static Type sequenceType(ClassDoc cd, com.sun.tools.mjavac.code.Type rawType) {
        try {
            Class<?> cls = cd.getClass();
            Method m = cls.getDeclaredMethod("sequenceType", com.sun.tools.mjavac.code.Type.class);
            final Type result = (Type)m.invoke(cd, (Object)rawType);
            return new Type() {
                public String typeName() {
                    return result.typeName();
                }
                public String qualifiedTypeName() {
                    return result.qualifiedTypeName();
                }
                public String simpleTypeName() {
                    return result.simpleTypeName();
                }
                public String dimension() {
                    return "[]";
                }
                public boolean isPrimitive() {
                    return result.isPrimitive();
                }
                public ClassDoc asClassDoc() {
                    return result.asClassDoc();
                }
                public ParameterizedType asParameterizedType() {
                    return null;
                }
                public TypeVariable asTypeVariable() {
                    return null;
                }
                public WildcardType asWildcardType() {
                    return null;
                }
                public AnnotationTypeDoc asAnnotationTypeDoc() {
                    return null;
                }
            };
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String simpleFunctionalTypeName(ClassDoc cd, com.sun.tools.mjavac.code.Type rawType) {
        try {
            Class<?> cls = cd.getClass();
            Method m = cls.getDeclaredMethod("simpleFunctionalTypeName", com.sun.tools.mjavac.code.Type.class);
            Object result = m.invoke(cd, (Object)rawType);
            return (String)result;
        } catch (Exception e) {
            return null;
        }
    }

    static class Option {
        String[] fields;
        String help;
        private static final int DESCRIPTION_COLUMN = 
            Integer.valueOf(getString("help.description.column"));
        
        Option(String field, String help) {
            fields = new String[] { field };
            this.help = help;
        }
        Option(String field, String param, String help) {
            fields = new String[] { field, param };
            this.help = help;
        }
        int length() {
            return fields.length;
        }
        String name() {
            return fields[0];
        }
        String description() {
            return fields.length == 1 ? fields[0] : fields[0] + ' ' + fields[1];
        }
        String help() {
            StringBuffer sb = new StringBuffer(description());
            while (sb.length() < DESCRIPTION_COLUMN)
                sb.append(' ');
            sb.append(help);
            return sb.toString();
        }
    }
}
