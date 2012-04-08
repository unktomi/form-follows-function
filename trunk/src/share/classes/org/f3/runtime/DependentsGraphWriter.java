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

package org.f3.runtime;

import org.f3.runtime.sequence.Sequence;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
 *
 * These GXL files can be viewed graph visualization tools such as "Graphviz"
 * (http://www.graphviz.org). To view a .gxl file with Graphviz, we have to use
 * "gxl2dot" tool to convert it to .dot file.
 *
 * @author A. Sundararajan
 */
public final class DependentsGraphWriter {
    /**
     * Writes given object's dependent network to the file in GXL format.
     *
     * @param obj The F3Object whose dependents graph is serialized
     * @param fileName The name of the file to which the graph is serialized
     */
    public static void write(F3Object obj, String fileName) {
        write(obj, fileName, false);
    }

    /**
     * Writes given object's dependent network to the file in GXL format.
     *
     * @param obj The F3Object whose dependents graph is serialized
     * @param fileName The name of the file to which the graph is serialized
     * @param followFields does the graph includes all transitively reachable F3Objects?
     */
    public static void write(F3Object obj, String fileName, boolean followFields) {
        write(obj, new File(fileName), followFields);
    }

    /**
     * Writes given object's dependent network to the file in GXL format.
     * 
     * @param obj The F3Object whose dependents graph is serialized
     * @param file The file to which the graph is serialized
     */
    public static void write(F3Object obj, File file) {
        write(obj, file, false);
    }

    /**
     * Writes given object's dependent network to the file in GXL format.
     *
     * @param obj The F3Object whose dependents graph is serialized
     * @param file The file to which the graph is serialized
     * @param followFields does the graph includes all transitively reachable F3Objects?
     */
    public static void write(F3Object obj, File file, boolean followFields) {
        DependentsGraphWriter depWriter = new DependentsGraphWriter(file, followFields);
        depWriter.start(file.getName());
        depWriter.writeDependencies(obj);
        depWriter.end();
    }


    // -- Internals only below this point

    private static final String GXL_NS = "http://www.gupro.de/GXL/gxl-1.0.dtd";

    // GXL element names
    private static final String GXL = "gxl";
    private static final String GRAPH = "graph";
    private static final String NODE = "node";
    private static final String EDGE = "edge";
    private static final String ATTR = "attr";
    private static final String STRING = "string";
    private static final String BOOL = "bool";

    // GXL attribute names
    private static final String ID = "id";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String EDGEMODE = "edgemode";
    private static final String NAME = "name";
    private static final String DIRECTED = "directed";

    // attribute types used
    private static final String ATTR_ID = "ID";
    private static final String ATTR_IDREF = "IDREF";
    private static final String ATTR_CDATA = "CDATA";
    private static final String ATTR_NMTOKEN = "NMTOKEN";

    // DOT "attr" names
    private static final String COLOR = "color";

    // Values for edge "attr" s
    private static final String INTEROBJECT_EDGE_COLOR = "red";

    // objects we have seen so far
    private Map<F3Object, F3Object> allObjects;
    // repeatedly cleared and used attributes object
    private AttributesImpl attrs;
    // SAX sink to output SAX events
    private ContentHandler handler;

    /*
     * Chase variables in every F3Object or not - by default, the graph
     * includes only transitive closure of all dependents of the given object.
     * If this flag is true, then graph includes all transitively reachable
     * F3Object type objects as well. (much bigger graph!)
     */
    private boolean followFields;

    private DependentsGraphWriter(File file, boolean followFields) {
        this.allObjects = new IdentityHashMap<F3Object, F3Object>();
        this.attrs = new AttributesImpl();
        this.handler =  makeContentHandler(file);
        this.followFields = followFields;
    }

    private static ContentHandler makeContentHandler(File file) {
        try {
            FileOutputStream fis = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fis);
            StreamResult streamResult = new StreamResult(bos);
            SAXTransformerFactory fac = (SAXTransformerFactory) TransformerFactory.newInstance();
            TransformerHandler thandler = fac.newTransformerHandler();
            Transformer transformer = thandler.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            thandler.setResult(streamResult);
            return thandler;
        } catch (Exception exp) {
            throw wrapException(exp);
        }
    }

    private void start(String graphId) {
        startDocument();
        startElement(GXL);
        attrs.clear();
        attrs.addAttribute("", ID, ID, ATTR_ID, graphId);
        attrs.addAttribute("", EDGEMODE, EDGEMODE, ATTR_CDATA, DIRECTED);
        startElement(GRAPH, attrs);
    }

    private void end() {
        // write node for each symbol seen
        for (F3Object obj : allObjects.keySet()) {
            attrs.clear();
            attrs.addAttribute("", ID, ID, ATTR_ID, id(obj));
            startElement(NODE, attrs);
            endElement(NODE);
        }

        // end the graph
        endElement(GRAPH);
        endElement(GXL);
        endDocument();

        allObjects.clear();
        attrs.clear();

        allObjects = null;
        attrs = null;
        handler = null;
    }

    private void writeDependencies(F3Object bindee) {
        if (allObjects.containsKey(bindee)) {
            return;
        }
        allObjects.put(bindee, bindee);
        List<F3Object> dependents = DependentsManager.getDependents(bindee);
        for (F3Object binder : dependents) {
            // write dependence b/w binder and bindee
            writeDependency(binder, bindee);
            // write the network of the dependee now
            writeDependencies(binder);
        }
        if (followFields) {
            final int count = bindee.count$();
            for (int idx = 0; idx < count; idx++) {
                Object fieldValue = bindee.get$(idx);
                if (fieldValue instanceof F3Object) {
                    writeDependencies((F3Object) fieldValue);
                } else if (fieldValue instanceof Sequence) {
                    Sequence seq = (Sequence)fieldValue;
                    if (seq.getElementType() == TypeInfo.Object) {
                        for (Object elem : seq) {
                            if (elem instanceof F3Object) {
                                writeDependencies((F3Object)elem);
                            }
                        }
                    }
                }
            }
        }
    }

    private void writeDependency(F3Object binder, F3Object bindee) {
        // write an edge from binder to bindee node
        attrs.clear();
        attrs.addAttribute("", FROM, FROM, ATTR_IDREF, id(binder));
        attrs.addAttribute("", TO, TO, ATTR_IDREF, id(bindee));
        startElement(EDGE, attrs);
            attrs.clear();
            attrs.addAttribute("", NAME, NAME, ATTR_NMTOKEN, COLOR);
            startElement(ATTR, attrs);
                startElement(STRING);
                    emitData(INTEROBJECT_EDGE_COLOR);
                endElement(STRING);
            endElement(ATTR);
        endElement(EDGE);
    }

    private String id(F3Object obj) {
        StringBuilder buf = new StringBuilder();
        buf.append(obj.getClass().getName());
        buf.append('@');
        buf.append(Integer.toHexString(System.identityHashCode(obj)));
        return buf.toString();
    }


    // XML helpers below
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

    // generic exception wrapper
    private static RuntimeException wrapException(Exception exp) {
        if (exp instanceof RuntimeException) {
            return (RuntimeException) exp;
        } else {
            return new RuntimeException(exp);
        }
    }
}
