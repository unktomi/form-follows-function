LANGREF_HTML_DIR = ../../build/doc/reference
DOCBOOK_XSL_DIR = ../../tools/docbook-xsl-1.73.1

langref:
	xsltproc -ratsXX -xinclude  --stringparam use.id.as.filename 1 --stringparam chunk.section.depth 0 --stringparam chunk.fast 1 -o $(LANGREF_HTML_DIR)/ $(DOCBOOK_XSL_DIR)/xhtml/chunk.xsl VisageReference.xml
