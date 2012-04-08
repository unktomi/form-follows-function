/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.tools.tree.xml;

import org.f3.api.tree.ExpressionTree;
import org.f3.api.tree.UnitTree;
import org.f3.tools.tree.F3Script;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Options;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.ContentHandler;

/**
 * XML tree transformer hooks after specified phase of the compiler and creates
 * an XML document that represents abstract syntax tree. The created XML document
 * is transformed by user specified XSL stylesheet (specified by -XDtreexsl option 
 * from command line). It is possible to specify more than one XSL using a comma
 * separated values.
 *
 * @author A. Sundararajan
 */
public class TreeXMLTransformer {

    public static final Context.Key<TreeXMLTransformer> treeXMLTransformerKey =
            new Context.Key<TreeXMLTransformer>();

    public static void preRegister(Context context) {
        Options options = Options.instance(context);
        // check if "-XDtreexsl" option is specified
        if (options.get(TREE_XSL) != null) {
            try {
                context.put(treeXMLTransformerKey, new TreeXMLTransformer(context));
            } catch (Exception exp) {
                System.err.println(exp.getMessage());
                if (options.get("-doe") != null) {
                    exp.printStackTrace();
                }
            }
        }
    }

    public static void afterParse(Context context, UnitTree cu) {
        TreeXMLTransformer transformer = context.get(treeXMLTransformerKey);
        if (transformer != null && transformer.phase == PARSE && (cu instanceof F3Script)) {
            transformer.transformWithCare(context, (F3Script)cu);
        }
    }

    public static void afterEnter(Context context, UnitTree cu, TypeElement clazz) {
        TreeXMLTransformer transformer = context.get(treeXMLTransformerKey);
        if (transformer != null && transformer.phase == ENTER && (cu instanceof F3Script)) {
            transformer.transformWithCare(context, (F3Script)cu);
        }
    }

    public static void afterAnalyze(Context context, UnitTree cu, TypeElement clazz) {
        TreeXMLTransformer transformer = context.get(treeXMLTransformerKey);
        if (transformer != null && transformer.phase == ANALYZE && (cu instanceof F3Script)) {
            transformer.transformWithCare(context, (F3Script)cu);
        }
    }
    
    // internals only below this point
    
    // print stack trace on exceptions?
    private boolean printStackOnError;
    // XSL templates -- could be zero, one or more
    private Templates[] templatesArray;
    // output directory in which transformed documents are saved
    private String outDir;
    // file extension for output documents
    private String outExt;
    // compilation phase after which XSL transformation is applied
    private int phase;
    // parameters to the XSL sheets
    private Map<String, String> transformParams;

    // various compilation phase values
    private static final int PARSE = 0x1;
    private static final int ENTER = 0x2;
    private static final int ANALYZE = 0x4;

    // Map to tell whether we are seeing the same compilation unit again
    private Map<URI, URI> seenCompUnitAlready;
    
    // command-line options for tree/xsl feature
    // XSL stylesheets that will transform AST/XML documents
    private static final String TREE_XSL = "treexsl";
    // output directory to write the transformed documents
    private static final String TREE_XSL_OUTDIR = "treexsl:d";
    // file extension for transformed documents
    private static final String TREE_XSL_OUTEXT = "treexsl:ext";
    // command line option to specify the compilation phase after
    // which the XSL transformatin is applied
    private static final String TREE_XSL_PHASE = "treexsl:phase";
    // parameters to XSL.
    private static final String TREE_XSL_PARAMS = "treexsl:params";
    
    // the default values of command-line options
    // default output directory where transformed source is saved
    private static final String DEFAULT_OUTDIR = "treexsl";
    // default output document extension is ".xml"
    private static final String DEFAULT_OUTEXT = "xml";
    // by default, XSL transform is applied after "analyze" phase
    // i.e., after parse-enter-analyze => types and symbols are
    // available whereever applicable
    private static final int DEFAULT_PHASE = ANALYZE;
    
    private TreeXMLTransformer(Context context) {
        seenCompUnitAlready = new HashMap<URI, URI>();
        printStackOnError = Options.instance(context).get("-doe") != null;
        initTemplatesArray(context);
        initOutputDirectory(context);
        initOutputExtension(context);
        initTransformPhase(context);
        initTransformParams(context);
    }

    private void initTemplatesArray(Context context) {
        Options options = Options.instance(context);
        String xsl = options.get(TREE_XSL);
        try {
            if (xsl != null && xsl.length() != 0 && !xsl.equals(TREE_XSL)) {
                TransformerFactory fac = TransformerFactory.newInstance();
                String[] files = xsl.split(",");
                templatesArray = new Templates[files.length];
                for (int i = 0; i < files.length; i++) {
                    File file = new File(files[i]);
                    if (!file.exists()) {
                        // try URL
                        URL url;
                        try {
                            url = new URL(files[i]);
                            templatesArray[i] = fac.newTemplates(new StreamSource(url.openStream()));
                        } catch (MalformedURLException mue) {
                            throw new IllegalArgumentException("File not found: " + file);
                        }
                    } else {
                        templatesArray[i] = fac.newTemplates(new StreamSource(file));
                    }
                }
            } else {
                this.templatesArray = new Templates[0];
            }
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void initOutputDirectory(Context context) {
        Options options = Options.instance(context);
        outDir = options.get(TREE_XSL_OUTDIR);
        if (outDir == null) {
            outDir = DEFAULT_OUTDIR;
        }
        File file = new File(outDir);
        if (!file.exists() && !file.mkdir()) {
             throw new IllegalArgumentException("Directory not found: " + outDir);
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + outDir);
        }
    }
    
    private void initOutputExtension(Context context) {
        Options options = Options.instance(context);
        String ext = options.get(TREE_XSL_OUTEXT);
        if (ext == null) {
            ext = DEFAULT_OUTEXT;
        }
        outExt = "." + ext;
    }
    
    private void initTransformPhase(Context context) {
        Options options = Options.instance(context);
        String phaseName = options.get(TREE_XSL_PHASE);
        if (phaseName == null) {
            phase = DEFAULT_PHASE;
        } else if (phaseName.equals("parse")) {
            phase = PARSE;   
        } else if (phaseName.equals("enter")) {
            phase = ENTER;
        } else if (phaseName.equals("analyze")) {
            phase = ANALYZE;
        } else {
            throw new IllegalArgumentException("Invalid phase: " + phaseName);
        }
    }
    
    private void initTransformParams(Context context) {
        this.transformParams = new HashMap<String, String>();
        Options options = Options.instance(context);
        String paramsOption = options.get(TREE_XSL_PARAMS);
        try {
            if (paramsOption != null) {
                String[] params = paramsOption.split(",");
                for (String p : params) {
                    int index = p.lastIndexOf('=');
                    String key = (index == -1) ? p : p.substring(0, index);
                    String value = (index == -1) ? "" : p.substring(index + 1);
                    transformParams.put(key, value);
                }
            }
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void transformWithCare(Context context, F3Script script) {
        try {
            transform(context, script);
        } catch (Exception exp) {
            System.err.println(exp.getMessage());
            if (printStackOnError) {
                exp.printStackTrace();
            }
        }
    }

    // transform given compilation unit using XSL
    private void transform(Context context, F3Script script) {
        URI uri = script.getSourceFile().toUri();
        if (seenCompUnitAlready.containsKey(uri)) {
            return;
        }
        seenCompUnitAlready.put(uri, uri);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(bos);
        TransformerHandler handler = createXSLChain(streamResult);

        try {
            // convert AST-to-XML
            convertAST2XML(context, script, handler);

            // collect the transformed XML output into buffer
            bos.flush();
            byte[] buf = bos.toByteArray();
            bos.close();

            // make package directory as needed and get source file name
            String outFile = getOutputFileName(script);
            writeOutput(buf, outFile);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private TransformerHandler createXSLChain(Result finalResult) {
        SAXTransformerFactory fac = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler;
        try {
            Templates[] tempArray = templatesArray;
            if (tempArray.length != 0) {
                TransformerHandler[] handlers = new TransformerHandler[tempArray.length];
                for (int i = 0; i < tempArray.length; i++) {
                    handlers[i] = fac.newTransformerHandler(tempArray[i]);
                }
                // connect the chain elements...
                for (int i = 0; i < handlers.length - 1; i++) {
                    handlers[i].setResult(new SAXResult(handlers[i + 1]));
                }
                // last element of the chain gives output to the final result.
                TransformerHandler lastHandler = handlers[handlers.length - 1];
                lastHandler.setResult(finalResult);
                Transformer transformer = lastHandler.getTransformer();
                setTransformerOptions(transformer);
                handler = handlers[0];
            } else {
                handler = fac.newTransformerHandler();
                Transformer transformer = handler.getTransformer();
                setTransformerOptions(transformer);
                handler.setResult(finalResult);
            }
        } catch (Exception exp) {
            throw wrapException(exp);
        }

        return handler;
    }

    private void setTransformerOptions(Transformer transformer) {
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        for (Map.Entry<String, String> p : transformParams.entrySet()) {
            transformer.setParameter(p.getKey(), p.getValue());
        }
    }

    // converts AST into XML (SAX) events
    private void convertAST2XML(Context context, F3Script script, ContentHandler handler) {
        try {
            TreeXMLSerializer visitor = new TreeXMLSerializer(handler);
            Compiler.enter(context, script, visitor);
            visitor.start(script);
        } finally {
            Compiler.leave();
        }
    }

    // writes transformed document
    private void writeOutput(byte[] buf, String outFile) throws IOException, FileNotFoundException {
        outFile = outFile.replace(".f3", outExt);
        FileOutputStream fos = new FileOutputStream(outFile);
        try {
            fos.write(buf);
        } finally {
            fos.close();
        }
    }

    private String getOutputFileName(UnitTree cu) {
        String pkgName = null;
        ExpressionTree pkg = cu.getPackageName();
        if (pkg != null) {
            pkgName = pkg.toString().replace('.', File.separatorChar);
        }
        StringBuilder sbuf = new StringBuilder(outDir);
        sbuf.append(File.separatorChar);
        if (pkgName != null) {
            sbuf.append(pkgName);
            sbuf.append(File.separatorChar);
            // create package subdirs under output directory
            new File(sbuf.toString()).mkdirs();
        }

        sbuf.append(cu.getSourceFile().getName());
        return sbuf.toString();
    }

    private static RuntimeException wrapException(Exception exp) {
        if (exp instanceof RuntimeException) {
            return (RuntimeException) exp;
        } else {
            return new RuntimeException(exp);
        }
    }
}
