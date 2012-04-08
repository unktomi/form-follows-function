<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
<xsl:import href="@chunk-xsl@"/>

<!-- Dummy - not actually used, except needs to be non-empty,
     so output.html.stylesheets gets called. -->
<xsl:param name="html.stylesheet">yes</xsl:param>

<xsl:template name="output.html.stylesheets">
<link rel="stylesheet" title="langref"
  href="langref.css" media="screen, print, projection, tv"/>
</xsl:template>

</xsl:stylesheet>
