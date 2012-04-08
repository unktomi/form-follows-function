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

package org.f3.tools.comp;

import org.f3.tools.tree.F3Script;
import com.sun.tools.mjavac.code.Symbol;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Options;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This class writes dependency graph in GXL (Graph eXchange Language) format.
 * Please refer to http://www.gupro.de/GXL/ for details about this file format.
 * For each compilation unit, a separate .gxl file is created in the dump dir
 * configured by -XDdumpdepgraph option.
 *
 * These GXL files can be viewed graph visualization tools such as "Graphviz"
 * (http://www.graphviz.org). To view a .gxl file with Graphviz, we have to use
 * "gxl2dot" tool to convert it to .dot file.
 *
 * @author A. Sundararajan
 */
public class DependencyGraphWriter {
    protected static final Context.Key<DependencyGraphWriter> dependencyGraphWriterKey =
        new Context.Key<DependencyGraphWriter>();

    public static final String GXL_NS = "http://www.gupro.de/GXL/gxl-1.0.dtd";

    // GXL element names
    public static final String GXL = "gxl";
    public static final String GRAPH = "graph";
    public static final String NODE = "node";
    public static final String EDGE = "edge";
    public static final String ATTR = "attr";
    public static final String STRING = "string";
    public static final String BOOL = "bool";

    // GXL attribute names
    public static final String ID = "id";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String EDGEMODE = "edgemode";
    public static final String NAME = "name";
    public static final String DIRECTED = "directed";

    // attribute types used
    public static final String ATTR_ID = "ID";
    public static final String ATTR_IDREF = "IDREF";
    public static final String ATTR_CDATA = "CDATA";
    public static final String ATTR_NMTOKEN = "NMTOKEN";

    // DOT "attr" names
    public static final String COMMENT = "comment";
    public static final String COLOR = "color";

    // Values for edge "attr" s
    public static final String INTEROBJECT_EDGE_COLOR = "red";
    public static final String INTRAOBJECT_EDGE_COLOR = "blue";
    public static final String INTEROBJECT = "interobj";
    public static final String INTRAOBJECT = "intraobj";

    // command line option to enable this feature
    public static final String DUMPDEPGRAPH_OPTION = "dumpdepgraph";

    public static DependencyGraphWriter instance(Context context) {
        DependencyGraphWriter instance = context.get(dependencyGraphWriterKey);
        if (instance == null) {
            /*
             * If you want to dump dependency graph into file, specify the
             * directory using -XDdumpdepgraph command line option
             */
            Options options = Options.instance(context);
            String dir = options.get(DUMPDEPGRAPH_OPTION);
            if (dir != null) {
                instance = new DependencyGraphWriter(context, dir);
            }
        }
        return instance;
    }

    public void start(F3Script tree) {
        this.allSyms = new HashSet<Symbol>();
        this.attrs = new AttributesImpl();
        this.fileName = tree.getSourceFile().getName();
        initContentHandler();
        startDocument();
        startElement(GXL);
        attrs.clear();
        attrs.addAttribute("", ID, ID, ATTR_ID, fileName);
        attrs.addAttribute("", EDGEMODE, EDGEMODE, ATTR_CDATA, DIRECTED);
        startElement(GRAPH, attrs);
    }

    public void writeDependency(Symbol binder, Symbol bindee) {
        writeDependency(binder, bindee, true);
    }

    public void writeInterObjectDependency(Symbol binder, Symbol bindee) {
        writeDependency(binder, bindee, false);
    }

    public void end() {
        // write node for each symbol seen
        for (Symbol sym : allSyms) {
            attrs.clear();
            attrs.addAttribute("", ID, ID, ATTR_ID, id(sym));
            startElement(NODE, attrs);
                attrs.clear();
                attrs.addAttribute("", NAME, NAME, ATTR_NMTOKEN, COMMENT);
                // output data type of the symbol as a comment
                startElement(ATTR, attrs);
                    startElement(STRING);
                        emitData("type: " + sym.type.toString());
                    endElement(STRING);
                endElement(ATTR);
            endElement(NODE);
        }
        
        // end the graph
        endElement(GRAPH);
        endElement(GXL);
        endDocument();

        // clear all state
        allSyms.clear();
        allSyms = null;
        attrs.clear();
        attrs = null;
        fileName = null;
        handler = null;
    }

    // -- Internals only below this point

    // directory where GXL documents are dumped
    private String dumpDir;
    // all dependency symbols for current class
    private Set<Symbol> allSyms;
    // repeatedly cleared and used attributes object
    private AttributesImpl attrs;
    // current GXL output file name
    private String fileName;
    // SAX sink to output SAX events
    private ContentHandler handler;

    private DependencyGraphWriter(Context context, String dumpDir) {
        context.put(dependencyGraphWriterKey, this);
        this.dumpDir = dumpDir;
    }

    private void initContentHandler() {
        try {
            new File(dumpDir).mkdirs();
            FileOutputStream fis = new FileOutputStream(new File(dumpDir, fileName + ".gxl"));
            BufferedOutputStream bos = new BufferedOutputStream(fis);
            StreamResult streamResult = new StreamResult(bos);
            SAXTransformerFactory fac = (SAXTransformerFactory) TransformerFactory.newInstance();
            TransformerHandler thandler = fac.newTransformerHandler();
            Transformer transformer = thandler.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            thandler.setResult(streamResult);
            this.handler = thandler;
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void writeDependency(Symbol binder, Symbol bindee, boolean intraObject) {
        // put these symbols in set of all symbols
        allSyms.add(binder);
        allSyms.add(bindee);

        // write an edge from binder to bindee node
        attrs.clear();
        attrs.addAttribute("", FROM, FROM, ATTR_IDREF, id(binder));
        attrs.addAttribute("", TO, TO, ATTR_IDREF, id(bindee));
        startElement(EDGE, attrs);
            attrs.clear();
            attrs.addAttribute("", NAME, NAME, ATTR_NMTOKEN, COMMENT);
            startElement(ATTR, attrs);
                startElement(BOOL);
                    emitData(intraObject? INTRAOBJECT : INTEROBJECT);
                endElement(BOOL);
            endElement(ATTR);
            attrs.clear();
            attrs.addAttribute("", NAME, NAME, ATTR_NMTOKEN, COLOR);
            startElement(ATTR, attrs);
                startElement(STRING);
                    emitData(intraObject? INTRAOBJECT_EDGE_COLOR : INTEROBJECT_EDGE_COLOR);
                endElement(STRING);
            endElement(ATTR);
        endElement(EDGE);
    }

    private String id(Symbol sym) {
        StringBuilder buf = new StringBuilder();
        buf.append(sym.owner.getQualifiedName());
        if (sym.isStatic()) {
            buf.append(F3Defs.scriptClassSuffix);
        }
        buf.append('.');
        buf.append(sym.name);
        return buf.toString();
    }

    private void startDocument() {
        try {
            handler.startDocument();
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void endDocument() {
        try {
            handler.endDocument();
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void startElement(String element) {
        attrs.clear();
        startElement(element, attrs);
    }

    private void startElement(String element, Attributes attrs) {
        try {
            handler.startElement(GXL_NS, element, element, attrs);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void endElement(String element) {
        try {
            handler.endElement(GXL_NS, element, element);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void emitData(String data) {
        if (data == null) {
            return;
        }
        char[] chars = data.toCharArray();
        try {
            handler.characters(chars, 0, chars.length);
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private RuntimeException wrapException(Exception exp) {
        if (exp instanceof RuntimeException) {
            return (RuntimeException) exp;
        } else {
            return new RuntimeException(exp);
        }
    }
}
