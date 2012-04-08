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

package org.f3.tools.xslhtml;

import org.f3.tools.script.F3ScriptEngineFactory;
import org.f3.tools.xmldoclet.Util;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static java.util.logging.Level.*;

/**
 *
 * @author joshua.marinacci@sun.com
 */
public class XHTMLProcessingUtils {

    private static PrintWriter pw = null;
    private static ResourceBundle messageRB = null;
    private static Logger logger = Logger.getLogger(XHTMLProcessingUtils.class.getName());;
    private static boolean SDK_THEME = true;
    static {
        // set verbose for initial development
        logger.setLevel(ALL); //TODO: remove or set to INFO when finished
    }
    private static final String PARAMETER_PROFILES_ENABLED = "profiles-enabled";
    private static final String PARAMETER_TARGET_PROFILE = "target-profile";

    /**
     * Transform XMLDoclet output to XHTML using XSLT.
     * 
     * @param xmlInputPath the path of the XMLDoclet output to transform
     * @param xsltStream the XSLT to implement the transformation, as an input stream.
     * @throws java.lang.Exception
     */
     public static void process(List<String> xmlInputs, InputStream xsltStream, 
            String sourcePath, File docsdir, Map<String,String> parameters
            ) throws Exception {
        if (xmlInputs == null || xmlInputs.size() == 0)
            throw new IllegalArgumentException("no XML input file(s)");
        
        System.out.println(getString("transforming.to.html"));
        // TODO code application logic here
        
        //hack to get this to work on the mac
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
            "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory",
            "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        
        if (xsltStream == null) {
            if(SDK_THEME) {
                xsltStream = XHTMLProcessingUtils.class.getResourceAsStream("resources/sdk.xsl");
            } else {
                xsltStream = XHTMLProcessingUtils.class.getResourceAsStream("resources/javadoc.xsl");
            }
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {

            public void warning(SAXParseException exception) throws SAXException {
                pe(WARNING, "warning: ", exception);
            }

            public void error(SAXParseException exception) throws SAXException {
                pe(SEVERE, "error: ", exception);
            }

            public void fatalError(SAXParseException exception) throws SAXException {
                pe(SEVERE, "fatal error", exception);
            }

            private void pe(Level level, String string, SAXParseException exception) {
                p(level, string + " line: " + exception.getLineNumber() + " column: " +
                        exception.getColumnNumber() + " " + exception.getLocalizedMessage());
            }
        });

        //File docsdir = new File("f3docs");
        if (!docsdir.exists()) {
            docsdir.mkdir();
        }

        p(INFO, getString("copying"));

        copyResource(docsdir,"empty.html");
        copyResource(docsdir,"general.css");
        copyResource(docsdir,"sdk.css");
        copyResource(docsdir,"mootools-1.2.1-yui.js");
        copyResource(docsdir,"sdk.js");
        copyResource(docsdir,"sessvars.js");
        File images = new File(docsdir,"images");
        images.mkdir();
        copy(XHTMLProcessingUtils.class.getResource("resources/quote-background-1.gif"), new File(images, "quote-background-1.gif"));
        copyResource(images,"F3_arrow_down.png");
        copyResource(images,"F3_arrow_right.png");
        copyResource(images,"F3_arrow_up.png");
        copyResource(images,"F3_highlight_dot.png");

        p(INFO, getString("transforming"));


        //File xsltFile = new File("javadoc.xsl");
        //p("reading xslt exists in: " + xsltFile.exists());
        Source xslt = new StreamSource(xsltStream);
        TransformerFactory transFact = TransformerFactory.newInstance();
        transFact.setURIResolver(new URIResolver() {
            public Source resolve(String href, String base) throws TransformerException {
                p(INFO, "Trying to resolve: " + href + " " + base);
                URL url = XHTMLProcessingUtils.class.getResource("resources/"+href);
                p(INFO, "Resolved " + href + ":" + base + " to " + url);
                try {
                    return new StreamSource(url.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(XHTMLProcessingUtils.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        });
        Transformer trans = transFact.newTransformer(xslt);
        if(SDK_THEME) {
            trans.setParameter("inline-classlist","true");
            trans.setParameter("inline-descriptions", "true");
        }
        
        for(String key : parameters.keySet()) {
            System.out.println("using key: " + key + " " + parameters.get(key));
            trans.setParameter(key, parameters.get(key));
        }
        trans.setErrorListener(new MainErrorListener());

        //build xml doc for the packages
        Document packages_doc = builder.newDocument();
        Element package_list_elem = packages_doc.createElement("packageList");
        packages_doc.appendChild(package_list_elem);
        
        //merge all xml files into single document
        Document unified = builder.newDocument();
        Element javadocElement = unified.createElement("javadoc");
        unified.appendChild(javadocElement);
        mergeDocuments(xmlInputs, builder, unified, javadocElement);
        
        // print out packages list
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList packages = (NodeList) xpath.evaluate("/javadoc/package", unified, XPathConstants.NODESET); 
        p(INFO, MessageFormat.format(getString("creating.packages"), packages.getLength()));

        // collect all package names in this array 
        String[] pkgNames = new String[packages.getLength()];
        //for each package, generate the package itself and append to package list doc
        for (int i = 0; i < packages.getLength(); i++) {
            Element pkg = ((Element) packages.item(i));
            String name = pkg.getAttribute("name");
            Element package_elem = packages_doc.createElement("package");
            package_elem.setAttribute("name", name);
            package_list_elem.appendChild(package_elem);
            copyDocComment(pkg,package_elem);
            Element first_line = packages_doc.createElement("first-line-comment");
            first_line.appendChild(packages_doc.createTextNode("first line comment"));
            package_elem.appendChild(first_line);
            processPackage(name, pkg, xpath, docsdir, trans, package_elem);
            pkgNames[i] = name;
        }
        //}

        //transform the package list doc
        trans.setParameter("root-path", "./");
        package_list_elem.setAttribute("mode", "overview-summary");
        trans.transform(new DOMSource(packages_doc), new StreamResult(new File(docsdir,"index.html")));

        Transformer indexTrans = transFact.newTransformer(new StreamSource(XHTMLProcessingUtils.class.getResourceAsStream("resources/master-index.xsl")));
        indexTrans.setParameter("root-path", "./");
        indexTrans.transform(new DOMSource(unified), new StreamResult(new File(docsdir,"master-index.html")));
        
        // copy "doc-files" for all packages to output
        Util.copyDocFiles(pkgNames, sourcePath, docsdir);
        p(INFO,getString("finished"));
    }

    private static void mergeDocuments(List<String> xmlInputs, DocumentBuilder builder, Document unified, Element javadocElement) 
            throws DOMException, SAXException, XPathExpressionException, IOException {
        Map<String,Element> packageMap = new HashMap<String,Element>();
        
        for (String xmlInputPath : xmlInputs) {
            File file = new File(xmlInputPath);
            p(INFO, MessageFormat.format(getString("reading.doc"), file.getAbsolutePath()));
            p(FINE, "exists: " + file.exists());
            Document doc = builder.parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList packages = (NodeList) xpath.evaluate("/javadoc/package", doc, XPathConstants.NODESET);
            p(INFO,"found " + packages.getLength()+" packages");
            for (int i = 0; i < packages.getLength(); i++) {
                Element copy = (Element) unified.importNode(packages.item(i), true);
                String pkgName = copy.getAttribute("name");
                if(!packageMap.containsKey(pkgName)) {
                    packageMap.put(pkgName, copy);
                } else {
                    Element pkg = packageMap.get(pkgName);
                    NodeList classes = copy.getChildNodes();
                    //copy to a List for safety before we start moving nodes around
                    List<Node> classesList = new ArrayList<Node>();
                    for(int j=0; j<classes.getLength();j++) {
                        classesList.add(classes.item(j));
                    }
                    for(Node cls : classesList) {
                        pkg.appendChild(cls);
                    }
                }
            }
        }
        
        for(String pkgName : packageMap.keySet()) {
            Element pkg = packageMap.get(pkgName);
            javadocElement.appendChild(pkg);
        }
    }

    
    
/*//keep this around for now.
    private static void p(Level INFO, Element javadocElement, String tab) {
        p(INFO,javadocElement,tab,-1);
    }
    private static void p(Level INFO, Element javadocElement, String tab, int depth) {
        if(depth ==0) return;
        p(INFO,tab+"<element: " + javadocElement.getTagName() + " " + javadocElement.hashCode());
        for(int i=0; i<javadocElement.getAttributes().getLength(); i++) {
            Attr attr = (Attr) javadocElement.getAttributes().item(i);
            p(INFO,tab+"    attr: " + attr.getName() + "=" + attr.getValue());
        }
        for(int i=0; i<javadocElement.getChildNodes().getLength();i++) {
            Node n = javadocElement.getChildNodes().item(i);
            if(n instanceof Element) {
                p(INFO,(Element)n,tab+"  ",depth-1);
            } else {
                p(INFO,tab+"  child = " + n.getNodeName() + " " + n.getNodeValue() + " " + n.hashCode());
            }
        }
        p(INFO,tab+"</element: " + javadocElement.getTagName());
    }
*/    
    

    // Not used
/*    private static void p(Transformer trans, Document packages_doc) throws TransformerException {
        trans.transform(new DOMSource(packages_doc), new StreamResult(System.out));
    }*/
    

    private static void processPackage(String packageName, Element pkg, XPath xpath, File docsdir, Transformer trans, Element package_elem) throws TransformerException, XPathExpressionException, IOException, FileNotFoundException, ParserConfigurationException {
        File packageDir = new File(docsdir, packageName);
        packageDir.mkdir();
        
        //classes
        NodeList classesNodeList = (NodeList) xpath.evaluate(
                    "*[name() = 'class' or name() = 'abstractClass' or name() = 'interface']",
                    pkg, XPathConstants.NODESET);
        List<Element> classes = sort(classesNodeList);
        p(INFO, MessageFormat.format(getString("creating.classes"), classes.size(), packageName));
        
        Document classes_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element class_list = classes_doc.createElement("classList");
        class_list.setAttribute("packageName", packageName);
        classes_doc.appendChild(class_list);
        
        for(Element clazz : classes) {
            processClass(clazz, class_list,  xpath, trans, packageDir);
            Element clazz_elem = (Element) package_elem.getOwnerDocument().importNode(clazz, true);
            package_elem.appendChild(clazz_elem);
        }
        
        class_list.setAttribute("mode", "overview-frame");
        trans.setParameter("root-path", "../");
        trans.transform(new DOMSource(classes_doc), new StreamResult(new File(packageDir,"package-frame.html")));
        class_list.setAttribute("mode", "overview-summary");
        trans.setParameter("root-path", "../");
        trans.transform(new DOMSource(classes_doc), new StreamResult(new File(packageDir,"package-summary.html")));
    }
    
    private static void processClass(Element clazz, Element class_list, XPath xpath, Transformer trans, File packageDir) throws TransformerException, IOException, XPathExpressionException {
        String qualifiedName = clazz.getAttribute("qualifiedName");
        String name = clazz.getAttribute("name");
        
        String profile = (String) xpath.evaluate("docComment/tags/profile/text()", clazz, XPathConstants.STRING);
        if("true".equals(trans.getParameter(PARAMETER_PROFILES_ENABLED))) {
            Object target_profile = trans.getParameter(PARAMETER_TARGET_PROFILE);
            if(profile != null && profile.equals(target_profile)) {
                //p(INFO, "profiles match");
            } else {
                //p(INFO, "Profiles don't match. skipping");
                return;
            }
        }
                
        //add to class list
        Document doc = class_list.getOwnerDocument();
        Element class_elem = doc.createElement("class");
        class_list.appendChild(class_elem);
        class_elem.setAttribute("name", name);
        class_elem.setAttribute("qualifiedName", qualifiedName);
        Element first_line = doc.createElement("first-line-comment");
        first_line.appendChild(doc.createTextNode("first line comment"));
        class_elem.appendChild(first_line);
        
        copyClassDoc(clazz,class_elem);
        
        processInlineExamples(clazz, class_elem, packageDir);

        File xhtmlFile = new File(packageDir, qualifiedName + ".html");
        Result xhtmlResult = new StreamResult(xhtmlFile);
        Source xmlSource = new DOMSource(clazz.getOwnerDocument());
        trans.setParameter("target-class", qualifiedName);
        trans.setParameter("root-path", "../");
        trans.transform(xmlSource, xhtmlResult);
    }

    
    private static void copyClassDoc(Element clazz, Element class_elem) {
        Element docComment = (Element) clazz.getElementsByTagName("docComment").item(0);
        if(docComment == null) return;
        
        NodeList firstSent = docComment.getElementsByTagName("firstSentenceTags");
        if(firstSent.getLength() > 0) {
            class_elem.appendChild(class_elem.getOwnerDocument().importNode(firstSent.item(0),true));
        }
        NodeList tags = docComment.getElementsByTagName("tags");
        if(tags.getLength() > 0) {
            for(int i=0; i<tags.getLength(); i++) {
                Node tag = tags.item(i);
                class_elem.appendChild(class_elem.getOwnerDocument().importNode(tag,true));
            }
        }
    }
    
    private static void copyDocComment(Element pkg, Element package_elem) {
        Element docComment = getFirstChildNamed(pkg, "docComment");
        if (docComment != null) {
            Node copy = package_elem.getOwnerDocument().importNode(docComment, true);
            package_elem.appendChild(copy);
        }
    }
    
    // return the first child element of the given name, if not found return null
    private static Element getFirstChildNamed(Element elem, String childName) {
        NodeList children = elem.getChildNodes();
        final int length = children.getLength();
        for (int index = 0; index < length; index++) {
            Node node =  children.item(index);
            if ((node instanceof Element) && 
                ((Element)node).getTagName().equals(childName)){
                return (Element)node;
            }
        }
        return null;    
    }
            
    private static List<Element> sort(NodeList classesNodeList) {
        List<Element> nodes = new ArrayList<Element>();
        for(int i=0; i<classesNodeList.getLength(); i++) {
            nodes.add((Element)classesNodeList.item(i));
        }
        
        Collections.sort(nodes,new Comparator<Element>() {
            public int compare(Element o1, Element o2) {
                return o1.getAttribute("qualifiedName").compareTo(
                        o2.getAttribute("qualifiedName"));
            }
        }
        );

        return nodes;
    }
    
    
    private static void copy(URL url, File file) throws FileNotFoundException, IOException {
        p(FINE, "copying from: " + url);
        p(FINE, "copying to: " + file.getAbsolutePath());
        InputStream in = url.openStream();
        FileOutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        while (true) {
            int n = in.read(buf);
            if (n < 0) {
                break;
            }
            out.write(buf, 0, n);
        }
    }
    
    private static void copyResource(File docsdir, String string) throws FileNotFoundException, IOException {
        copy(XHTMLProcessingUtils.class.getResource("resources/"+string),new File(docsdir,string));
    }

    /* Not used
    private static void copy(File infile, File outfile) throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(infile);
        FileOutputStream out = new FileOutputStream(outfile);
        byte[] buf = new byte[1024];
        while (true) {
            int n = in.read(buf);
            if (n < 0) {
                break;
            }
            out.write(buf, 0, n);
        }
    }
    */
    
    static String getString(String key) {
        ResourceBundle msgRB = messageRB;
        if (msgRB == null) {
            try {
                messageRB = msgRB =
                    ResourceBundle.getBundle("org.f3.tools.xslhtml.resources.xslhtml");
            } catch (MissingResourceException e) {
                throw new Error("Fatal: Resource for f3doc is missing");
            }
        }
        return msgRB.getString(key);
    }

    private static void p(Level level, String string) {
        if (level.intValue() >= logger.getLevel().intValue())
            System.err.println(string);
    }

    private static void p(Level level, String string, Throwable t) {
        if (level.intValue() >= logger.getLevel().intValue()) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            if (t != null) {
                sb.append(System.getProperty("line.separator"));
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                }
            }
            System.err.println(sb.toString());
        }
    }
    
    /* Not used
    private static void p(String string) {
        System.out.println(string);
    }
    */

    /**
     * Command-line/debugging entry
     */
    public static void main(String[] args) throws Exception {
        List<String> inputs = new ArrayList<String>();
        inputs.add("javadoc.xml");
        process(inputs, null, ".", new File("f3docs_test"), new HashMap<String, String>());
    }

    private static class MainErrorListener implements ErrorListener {

        public MainErrorListener() {
        }

        public void warning(TransformerException exception) throws TransformerException {
            p(WARNING, "warning: " + exception);
        }

        public void error(TransformerException exception) throws TransformerException {
            Throwable thr = exception;
            while (true) {
                p(SEVERE, "error: " + exception.getMessageAndLocation(), thr.getCause());
                if (thr.getCause() != null) {
                    thr = thr.getCause();
                } else {
                    break;
                }
            }
        }

        public void fatalError(TransformerException exception) throws TransformerException {
            p(SEVERE, "fatal error: " + exception.getMessageAndLocation(), exception);
        }
    }
    
    
    private static void processInlineExamples(Element clazz, Element class_elem, File packageDir) {
        NodeList examples = clazz.getElementsByTagName("example");
        if(examples != null & examples.getLength() > 0) {
            for(int i=0; i<examples.getLength(); i++) {
                Element example = (Element) examples.item(i);
                processExampleCode(example, packageDir, clazz, i, true);
            }
        }
        NodeList highlights = clazz.getElementsByTagName("highlight");
        if(highlights != null && highlights.getLength() > 0) {
            for(int i=0; i<highlights.getLength(); i++) {
                Element highlight = (Element) highlights.item(i);
                processExampleCode(highlight, packageDir, clazz, i, false);
            }
        }
    }
    
    private static void processExampleCode(Element example, File packageDir, Element clazz, int i, boolean renderScreenshot) throws DOMException {
        //p(INFO, MessageFormat.format(getString("processing.example"), clazz.getAttribute("name")));
        //p(INFO, example.getTextContent());
        //String script = "import f3.gui.*; CubicCurve { x1: 0  y1: 50  ctrlX1: 25  ctrlY1: 0 ctrlX2: 75  ctrlY2: 100   x2: 100  y2: 50 fill:Color.RED }";
        String script = example.getTextContent();
        StringBuffer out = new StringBuffer();
        out.append("<p>the code:</p>");
        out.append("<pre class='example-code'><code>");
        String styledScript = highlight(script);
        out.append(styledScript);
        out.append("</code></pre>");
        if(renderScreenshot) {
            File imgFile = new File(packageDir, clazz.getAttribute("name") + i + ".png");
            boolean success = renderScriptToImageAsync(imgFile, script, clazz.getAttribute("name"));
            if (success) {
                out.append("<p>produces:</p>");
                out.append("<p>");
                out.append("<img class='example-screenshot' src='" + imgFile.getName() + "'/>");
                out.append("</p>");
            }
        }
        example.setTextContent(out.toString());
    }

    private static String highlight(String text) {
        //String pattern = "(/\\*)";
        //String replace = "<span class='comment'>/*";
        //comments
        text = text.replaceAll("/\\*", "<i class='comment'>/*");
        text = text.replaceAll("\\*/","*/</i>");
        //imports
        text = text.replaceAll("(import|package)","<b>$1</b>");
        //keywords
        text = text.replaceAll("(var)","<b class='keyword'>$1</b>");
        //attribute names
        text = text.replaceAll("(\\w\\w*):","<b>$1</b>:");
        //attribute values
        text = text.replaceAll("(\\d+)","<span class='number-literal'>$1</span>");
        text = text.replaceAll("(\".*\")","<span class='string-literal'>$1</span>");
        //put inside of precode
        //text = "<pre><code>"+text+"</code></pre>";
        //text = "<html><head><link rel='stylesheet' href='test.css'/></head><body>"+
        //        text +"</body></html>";
        return text;
    }
   
    private static boolean renderScriptToImageAsync(final File imgFile, 
                                                 final String script,
                                                 final String name) {
        final boolean[] result = new boolean[1];
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        renderScriptToImage(imgFile, script);
                        result[0] = true;
                    } catch (Throwable ex) {
                        System.out.println("error processing code: " + name);
                        System.out.println("error processing : " + script);
                        ex.printStackTrace();
                        result[0] = false;
                    }
                }
            });
        } catch (Throwable ex) {
            System.out.println("error processing code: " + name);
            System.out.println("error processing : " + script);
            ex.printStackTrace();
            result[0] = false;
        }
        return result[0];
    }

    @SuppressWarnings("unchecked")
    private static void renderScriptToImage(File imgFile, String script) throws ScriptException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        ScriptEngineFactory factory = new F3ScriptEngineFactory();
        ScriptEngine scrEng = factory.getScriptEngine();
        if (pw == null) {
            pw = new PrintWriter(System.err);
        }
        scrEng.getContext().setErrorWriter(pw);
        try {
            Object ret = scrEng.eval(script); 
            // FIXME: should we use f3.reflect here?
            Class<?> f3StageClass = Class.forName("f3.stage.Stage"); 
            Class<?> f3SceneClass = Class.forName("f3.scene.Scene"); 
            Class<?> f3NodeClass = Class.forName("f3.scene.Node"); 
            Object scene = null;
            if (f3SceneClass.isInstance(ret)) { 
                scene = ret; 
            } else if (f3StageClass.isInstance(ret)) {
                try {
                    scene = f3StageClass.getMethod("get$scene").invoke(ret); 
                } catch (Exception ex) {
                    pw.println("f3doc: Exception while processing " + imgFile);
                    ex.printStackTrace(pw);
                    pw.flush();
                    return;
                }
            } else if (f3NodeClass.isInstance(ret)) {
                try {
                    scene = f3NodeClass.getMethod("get$scene").invoke(ret); 
                } catch (Exception ex) {
                    pw.println("f3doc: Exception while processing " + imgFile);
                    ex.printStackTrace(pw);
                }
                if (scene == null) {
                   // Node is not added to a scene. Create a scene with this
                   // node as "content" of it. Need to change file name to
                   // get proper "eval" return value!
                   scrEng.put(ScriptEngine.FILENAME, "___SCENE_WRAPPER___.f3");
                   scrEng.put("node", ret);
                   scene = scrEng.eval(
                       "f3.scene.Scene { " +
                           " content: [ node as f3.scene.Node ] " +
                       "}");
                }
            } else {
                Object f3class = ret.getClass();
                pw.println("ERROR: Unrecongized F3 class: " + f3class); 
                pw.flush();
                return;
            } 
            try {
                Method renderToImage = f3SceneClass.getMethod("renderToImage", Object.class);
                BufferedImage img = (BufferedImage) renderToImage.invoke(scene, (Object)null); 
                ImageIO.write(img, "png", imgFile); 
            } catch (Exception ex) {
                pw.println("f3doc: Exception while processing " + imgFile);
                ex.printStackTrace(pw);
            }
        } catch (javax.script.ScriptException ex) {
            pw.println("f3doc: Exception while processing " + imgFile);
            pw.println(ex.getMessage());
            pw.println(" at: line = " + ex.getLineNumber() + " column = " + ex.getColumnNumber());
            pw.println("file = " + ex.getFileName());
            pw.println("exception = "+ ex.toString());
            pw.println(ex.getMessage());
            ex.printStackTrace(pw);
            pw.println("cause = " + ex.getCause());
        } finally {
            pw.flush();
        }
    }
}
