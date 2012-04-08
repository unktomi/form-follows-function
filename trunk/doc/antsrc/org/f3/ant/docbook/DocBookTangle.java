/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.ant.docbook;

import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts F3 and Java example source files from 
 * DocBook sources.  This allows a build to verify that they can be compiled 
 * and possibly executed.
 * 
 * @author Tom Ball
 */
public class DocBookTangle {

    public static void main(String[] args) {
        DocBookTangle dbt = new DocBookTangle();
        boolean ok = dbt.run(args);
        if ( !ok ) {
            System.exit(1);
        }
    }

    static interface Log {
        void info(String msg);
        void verbose(String msg);
        void error(String msg, Exception e);
        void error(Exception e);
    }

    private File destDir = tmpDir;
    private Log log;
    private boolean quiet = false;
    private List<String> filenames = new ArrayList<String>();

    public void setLog(Log log) {
        this.log = log;
    }

    public boolean run(String[] args) {
        if (log == null) {
            log = new Log() {
                public void error(String msg, Exception e) {
                    System.err.println("ERROR: DocBookTangle: " + msg);
                    if ( e != null ) {
                        error(e);
                    }
                }
                public void error(Exception e) {
                    System.err.println("EXCEPTION: " + e.toString());
                    e.printStackTrace();
                }
                public void info(String msg) {
                    System.out.println(msg);
                }
                public void verbose(String msg) {
                    if (!quiet)
                        System.out.println(msg);
                }
            };
        }

        boolean ok = parseOptions(args);
            if ( ok && filenames.isEmpty() ) {
                log.error("options parsed but no files to process", null);
                ok = false;
            }
            /* Need at least one file. */
            if ( !ok ) {
                usage(log);
            } else {
                execute();
        }
        return ok;
    }

    void execute() {
        destDir.mkdirs();
        for (String path : filenames) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            if (path.endsWith(".xml")) {
                File srcFile = new File(path);
                String baseName = srcFile.getName();
                baseName = baseName.substring(0, baseName.lastIndexOf(".xml"));
                FileReader srcReader = null;
                try {
                    XMLReader parser = factory.newSAXParser().getXMLReader();
                    DocBookXMLHandler handler = new DocBookXMLHandler(baseName);
                    parser.setContentHandler(handler);
                    parser.setErrorHandler(handler);
                    srcReader = new FileReader(srcFile);
                    InputSource is = new InputSource(srcReader);
                    log.info("parsing " + srcFile.getPath());
                    parser.parse(is);
                } catch (ParserConfigurationException e) {
                    log.error(e);
                } catch (IOException e) {
                    log.error(e);
                } catch (SAXException e) {
                    log.error(e);
                } finally {
                    if (srcReader != null)
                        try {
                            srcReader.close();
                        } catch (IOException e) {
                            log.error(e);
                        }
                }
            }
        }
    }
    
    private boolean parseOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                if (++i == args.length) {
                    log.error("no output directory specified", null);
                    return false;
                }
                destDir = new File(args[i]);
            }
            else
                filenames.add(args[i]);
        }
        return true;
    }

    private static void usage(Log log) {
        log.info("usage:");
        log.info("    java DocBookTangle.java [-o output_directory] file.xml [file2.xml, etc.]");
    }

    private class DocBookXMLHandler extends DefaultHandler {
        private String baseName;
        private StringBuilder text;
        private String language;
        private boolean inProgramListing;
        private int count;
        
        DocBookXMLHandler(String base) {
            super();
            baseName = base;
        }

        @Override
        public void startDocument() throws SAXException {
            text = new StringBuilder();
            inProgramListing = false;
            count = 0;
        }

        @Override
	public void startElement(String namespaceURI, String localName,
				 String qName, Attributes attrs) 
	    throws SAXException {
	    if (qName.equals("programlisting")) {
                String continuationType = attrs.getValue("continuation");
                if ("continues".equals(continuationType)) {
                    inProgramListing = true;
                } 
                else if ("restarts".equals(continuationType)) {
                    saveText();
                    inProgramListing = true;
                }
                else
                    ; // ignore
                language = attrs.getValue("language");
            }
	}

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inProgramListing)
                text.append(ch, start, length);
        }

        @Override
        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            inProgramListing = false;
        }

        @Override
	public void endDocument() {
            saveText();
	}
        
        private void saveText() {
            if (text.length() > 0) {
                String name = baseName + '-' + ++count;
                name += "java".equals(language) ? ".java" : ".f3";
                log.info("writing " + name);
                File destFile = new File(destDir, name);
                try {
                    FileWriter out = new FileWriter(destFile);
                    out.write(text.toString());
                    out.close();
                } catch (IOException e) {
                    log.error(e);
                }
                text = new StringBuilder();
            }
        }

        @Override
	public void warning(SAXParseException e) throws SAXException {
            log.info("parser warning: " + e.getMessage());
	}
        @Override
	public void error(SAXParseException e) throws SAXException {
            log.error("parser error: ", e);
	}
        @Override
	public void fatalError(SAXParseException e) throws SAXException {
            log.error("fatal parser error: ", e);
	}
    }
    
    private static File tmpDir;
    static {
        try {
            File tmpFile = File.createTempFile("xxx", ".tmp");
            tmpDir = tmpFile.getParentFile();
        } catch (IOException e) {}
    }
}
